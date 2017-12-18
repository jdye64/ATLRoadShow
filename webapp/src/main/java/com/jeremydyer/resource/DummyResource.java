package com.jeremydyer.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;

/**
 * Dummy resource. For examples only. Should be removed in your actually application.
 *
 * Created by jeremydyer on 11/19/14.
 */
@Path("/dummy")
@Produces(MediaType.APPLICATION_JSON)
public class DummyResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(DummyResource.class);

    @GET
    @Timed
    @Path("normal")
    public Response normalOperation() {
        LOGGER.info("Normal API operation with no errors .....");
        return Response.ok("OK").build();
    }

    @GET
    @Timed
    @Path("npe")
    public Response npeOperation() {
        //NPE will be thrown here ...
        String empty = null;
        empty.length();
        return Response.ok("OK").build();
    }

    @GET
    @Timed
    @Path("kill")
    public Response killJVM() {
        LOGGER.warn("killing jvm");
        System.exit(-1);
        return Response.ok("OK").build();
    }
}