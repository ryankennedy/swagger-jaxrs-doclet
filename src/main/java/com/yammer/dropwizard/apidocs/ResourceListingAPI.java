package com.yammer.dropwizard.apidocs;

public class ResourceListingAPI {
    private String path;
    private String description;

    private ResourceListingAPI() { }

    public ResourceListingAPI(String path, String description) {
        this.path = path;
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public String getDescription() {
        return description;
    }
}
