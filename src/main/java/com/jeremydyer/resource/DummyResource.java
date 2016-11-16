package com.jeremydyer.resource;

import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Dummy resource. For examples only. Should be removed in your actually application.
 *
 * Created by jeremydyer on 11/19/14.
 */
@Path("/dummy")
@Produces(MediaType.APPLICATION_JSON)
public class DummyResource {

    @GET
    @Timed
    public String[] getSearchCache() {
        return new String[]{"dummy", "resource"};
    }
}