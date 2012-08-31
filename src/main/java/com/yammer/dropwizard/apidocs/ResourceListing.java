package com.yammer.dropwizard.apidocs;

import java.util.List;

public class ResourceListing {
    private String apiVersion;
    private String basePath;
    private List<ResourceListingAPI> apis;

    @SuppressWarnings("unused")
	private ResourceListing() { }

    public ResourceListing(String apiVersion, String basePath, List<ResourceListingAPI> apis) {
        this.apiVersion = apiVersion;
        this.basePath = basePath;
        this.apis = apis;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public String getSwaggerVersion() {
        return "1.1";
    }

    public String getBasePath() {
        return basePath;
    }

    public List<ResourceListingAPI> getApis() {
        return apis;
    }
}
