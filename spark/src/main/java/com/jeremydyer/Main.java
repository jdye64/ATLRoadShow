package com.jeremydyer;


import java.io.IOException;
import java.util.Map;

import org.apache.nifi.remote.Transaction;
import org.apache.nifi.remote.TransferDirection;
import org.apache.nifi.remote.client.SiteToSiteClient;
import org.apache.nifi.remote.client.SiteToSiteClientConfig;
import org.apache.nifi.spark.NiFiDataPacket;
import org.apache.nifi.spark.NiFiReceiver;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

/**
 * Created by jdyer on 11/15/16.
 */
public class Main {

    private static SiteToSiteClient dataToSparkClient = null;

    public static void main(String[] args) throws IOException {

        SiteToSiteClientConfig dataToSparkConfig = new SiteToSiteClient.Builder()
                .url("http://localhost:8080/nifi")
                .portName("Data From Spark")
                .buildConfig();

        dataToSparkClient = new SiteToSiteClient.Builder().fromConfig(dataToSparkConfig).build();


        SiteToSiteClientConfig dataFromSparkConfig = new SiteToSiteClient.Builder()
                .url("http://localhost:8080/nifi")
                .portName("Data For Spark")
                .buildConfig();

        SparkConf sparkConf = new SparkConf().setAppName("NiFi-Spark Streaming example");
        JavaStreamingContext ssc = new JavaStreamingContext(sparkConf, new Duration(1000L));
        JavaReceiverInputDStream packetStream = ssc.receiverStream(new NiFiReceiver(dataFromSparkConfig, StorageLevel.MEMORY_ONLY()));

        JavaDStream text = packetStream.map(new Function<NiFiDataPacket, String>() {
            public String call(final NiFiDataPacket dataPacket) throws Exception {

                //Examine the incoming message body and parse the log message
                String logMessage = new String(dataPacket.getContent());

                if (logMessage != null && logMessage.contains("ERROR")) {

                    String[] lines = logMessage.split("\\n");
                    System.out.println(lines.length + " log lines");
                    String fileLine = "";

                    // Loop through the lines to find the first line in the stack trace that begins with \tat
                    for (int i = 0; i < lines.length; i++) {
                        if (lines[i].contains("at")) {
                            String line = lines[i];
                            fileLine = line.split("\\(")[1].split("\\)")[0];
                            break;
                        }
                    }

                    //Create the Github issue object
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("title", "Error @ " + fileLine);
                    jsonObject.put("body", logMessage);
                    jsonObject.put("assignee", "jdye64");
                    JSONArray labels = new JSONArray();
                    labels.put("bug");
                    jsonObject.put("labels", labels);

                    Map<String, String> atts = dataPacket.getAttributes();
                    atts.put("LOG.STATUS", "ERROR");

                    final Transaction transaction = dataToSparkClient.createTransaction(TransferDirection.SEND);
                    transaction.send(jsonObject.toString().getBytes(), atts);
                    transaction.confirm();
                    transaction.complete();

                } else {
                    // Everything seems fine with this log message ....
                    Map<String, String> atts = dataPacket.getAttributes();
                    atts.put("LOG.STATUS", "OK");

                    final Transaction transaction = dataToSparkClient.createTransaction(TransferDirection.SEND);
                    transaction.send(dataPacket.getContent(), atts);
                    transaction.confirm();
                    transaction.complete();
                }

                return new String(dataPacket.getContent());
            }
        });

        text.print();

        ssc.start();
        System.out.println("Spark Streaming successfully initialized. Awaiting manual termination ....");
        ssc.awaitTermination();
    }
}