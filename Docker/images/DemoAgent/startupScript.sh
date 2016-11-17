#!/bin/bash

echo "Starting Sample ATLRoadShow Web Application"
nohup java -jar /atlroadshow-webapp-1.0.0.jar server /application.yml &
echo "Starting NiFi"
$NIFI_HOME/bin/nifi.sh run