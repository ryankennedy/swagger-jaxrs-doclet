package com.hypnoticocelot.jaxrs.doclet.model;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.sun.javadoc.FieldDoc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.transform;
import static java.util.Arrays.asList;

public class Property {
    private String type;
    private String description;
    private String containerOf;
    private AllowableValues allowableValues;

    private Property() {
    }

    public Property(String type, String description, String containerOf) {
        this.type = type;
        this.description = description;
        this.containerOf = containerOf;
        if (type.equals("boolean")) {
            List<String> values = new ArrayList<String>();
            values.add("false");
            values.add("true");
            allowableValues = new AllowableValues(values);
        }
    }

    public Property(FieldDoc[] enumConstants, String description) {
        this.type = "string";
        this.description = description;
        this.allowableValues = new AllowableValues(transform(asList(enumConstants), new Function<FieldDoc, String>() {
            @Override
            public String apply(FieldDoc input) {
                return input.name();
            }
        }));
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public AllowableValues getAllowableValues() {
        return allowableValues;
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
                && Objects.equal(containerOf, that.containerOf)
                && Objects.equal(allowableValues, that.allowableValues);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(type, description, containerOf, allowableValues);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("type", type)
                .add("description", description)
                .add("containerOf", containerOf)
                .add("allowableValues", allowableValues)
                .toString();
    }
}
