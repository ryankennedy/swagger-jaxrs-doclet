package com.yammer.dropwizard.apidocs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Property {
    private String type;
    private String description;
    private String containerOf;

    private Property() { }

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
            List<String> values = new ArrayList<>();
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
            result = new HashMap<>();
            result.put("$ref", containerOf);
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Property property = (Property) o;

        if (containerOf != null ? !containerOf.equals(property.containerOf) : property.containerOf != null)
            return false;
        if (description != null ? !description.equals(property.description) : property.description != null)
            return false;
        if (type != null ? !type.equals(property.type) : property.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (containerOf != null ? containerOf.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Property{" +
                "type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", containerOf='" + containerOf + '\'' +
                '}';
    }
}