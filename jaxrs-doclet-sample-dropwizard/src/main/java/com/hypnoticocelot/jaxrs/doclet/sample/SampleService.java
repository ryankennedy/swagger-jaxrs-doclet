package com.hypnoticocelot.jaxrs.doclet.sample;

import com.google.common.base.Optional;
import com.hypnoticocelot.jaxrs.doclet.sample.resources.*;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.auth.Authenticator;
import com.yammer.dropwizard.auth.basic.BasicAuthProvider;
import com.yammer.dropwizard.auth.basic.BasicCredentials;
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
        environment.addProvider(new BasicAuthProvider<String>(new Authenticator<BasicCredentials, String>() {
            @Override
            public Optional<String> authenticate(BasicCredentials basicCredentials) throws AuthenticationException {
                return Optional.of("USERNAME");
            }
        }, "AuthResource Realm"));

        environment.addResource(new AuthResource());
        environment.addResource(new GreetingsResource());
        environment.addResource(new HttpServletRequestResource());
        environment.addResource(new RecursiveResource());
        environment.addResource(new ResponseResource());
        environment.addResource(new ModelResource());
    }
}
