package com.hypnoticocelot.jaxrs.doclet.apidocs;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class FixtureLoader {

    private FixtureLoader() {
    }

    public static <T> T loadFixture(String path, Class<T> resourceClass) throws IOException {
        return new ObjectMapper().readValue(
                FixtureLoader.class.getResourceAsStream(path),
                resourceClass
        );
    }

}
