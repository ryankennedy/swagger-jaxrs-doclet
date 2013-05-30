package com.hypnoticocelot.jaxrs.doclet.model;

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

        Model model = (Model) o;

        if (id != null ? !id.equals(model.id) : model.id != null) return false;
        if (properties != null ? !properties.equals(model.properties) : model.properties != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Model{" +
                "id='" + id + '\'' +
                ", properties=" + properties +
                '}';
    }
}
