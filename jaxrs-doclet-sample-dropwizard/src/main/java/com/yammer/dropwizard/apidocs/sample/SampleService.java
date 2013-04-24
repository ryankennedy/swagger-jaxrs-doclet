package com.yammer.dropwizard.apidocs.sample;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.apidocs.sample.resources.GreetingsResource;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.config.Environment;

public class SampleService extends Service<Configuration> {
    public static void main(String[] args) throws Exception {
        new SampleService().run(args);
    }

    @Override
    public void initialize(Bootstrap<Configuration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/apidocs", "/apidocs", "index.html"));
    }

    @Override
    public void run(Configuration configuration, Environment environment) throws Exception {
        environment.addResource(new GreetingsResource());
    }
}
