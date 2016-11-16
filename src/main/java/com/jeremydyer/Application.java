package com.jeremydyer;

import com.jeremydyer.resource.DummyResource;

import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * Created by jeremydyer on 11/19/14.
 */
public class Application
        extends io.dropwizard.Application<ApplicationConfiguration> {

    @Override
    public void initialize(Bootstrap<ApplicationConfiguration> bootstrap) {

        //Creates an Asset bundle to serve up static content. Served from http://localhost:8080/assets/
        bootstrap.addBundle(new AssetsBundle());
    }

    @Override
    public void run(ApplicationConfiguration configuration, Environment environment) throws Exception {

        //Register your Web Resources like below.
        final DummyResource dummyResource = new DummyResource();
        environment.jersey().register(dummyResource);
    }

    public static void main(String[] args) throws Exception {
        new Application().run(args);
    }
}
