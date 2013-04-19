package com.yammer.dropwizard.apidocs;

public class ResourceListingAPI {
    private String path;
    private String description;

    @SuppressWarnings("unused")
    private ResourceListingAPI() {
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResourceListingAPI that = (ResourceListingAPI) o;

        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ResourceListingAPI{" +
                "path='" + path + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
