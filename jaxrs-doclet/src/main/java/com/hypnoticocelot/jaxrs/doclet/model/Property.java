package com.hypnoticocelot.jaxrs.doclet.model;

import com.google.common.base.Objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Property {
    private String type;
    private String description;
    private String containerOf;

    private Property() {
    }

    public Property(String type, String description, String containerOf) {
        this.type = type;
        this.description = description;
        this.containerOf = containerOf;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public AllowableValues getAllowableValues() {
        if (type.equals("boolean")) {
            List<String> values = new ArrayList<String>();
            values.add("false");
            values.add("true");
            return new AllowableValues(values);
        } else {
            return null;
        }
    }

    public Map<String, String> getItems() {
        Map<String, String> result = null;
        if (containerOf != null) {
            result = new HashMap<String, String>();
            result.put("$ref", containerOf);
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Property that = (Property) o;
        return Objects.equal(type, that.type)
                && Objects.equal(description, that.description)
                && Objects.equal(containerOf, that.containerOf);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(type, description, containerOf);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("type", type)
                .add("description", description)
                .add("containerOf", containerOf)
                .toString();
    }
}
