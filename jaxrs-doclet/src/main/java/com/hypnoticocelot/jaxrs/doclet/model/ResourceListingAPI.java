package com.hypnoticocelot.jaxrs.doclet.model;

import com.google.common.base.Objects;

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
        return Objects.equal(path, that.path)
                && Objects.equal(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(path, description);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("path", path)
                .add("description", description)
                .toString();
    }
}
