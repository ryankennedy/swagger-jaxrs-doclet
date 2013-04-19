package com.yammer.dropwizard.apidocs;

import java.util.List;

public class ResourceListing {
    private String apiVersion;
    private String basePath;
    private List<ResourceListingAPI> apis;

    @SuppressWarnings("unused")
    private ResourceListing() {
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResourceListing that = (ResourceListing) o;

        if (apiVersion != null ? !apiVersion.equals(that.apiVersion) : that.apiVersion != null) return false;
        if (apis != null ? !apis.equals(that.apis) : that.apis != null) return false;
        if (basePath != null ? !basePath.equals(that.basePath) : that.basePath != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = apiVersion != null ? apiVersion.hashCode() : 0;
        result = 31 * result + (basePath != null ? basePath.hashCode() : 0);
        result = 31 * result + (apis != null ? apis.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ResourceListing{" +
                "apiVersion='" + apiVersion + '\'' +
                ", basePath='" + basePath + '\'' +
                ", apis=" + apis +
                '}';
    }
}
