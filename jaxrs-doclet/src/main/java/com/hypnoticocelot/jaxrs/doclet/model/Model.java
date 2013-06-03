package com.hypnoticocelot.jaxrs.doclet.model;

import com.google.common.base.Objects;

import java.util.Map;

public class Model {

    private String id;
    private Map<String, Property> properties;

    public Model() {
    }

    public Model(String id, Map<String, Property> properties) {
        this.id = id;
        this.properties = properties;
    }

    public String getId() {
        return id;
    }

    public Map<String, Property> getProperties() {
        return properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Model other = (Model) o;
        return Objects.equal(id, other.id)
                && Objects.equal(properties, other.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, properties);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("properties", properties)
                .toString();
    }
}
