package com.hypnoticocelot.jaxrs.doclet.model;

import com.google.common.base.Objects;

import java.util.ArrayList;
import java.util.List;

public class ApiParameter {
    private String paramType;
    private String name;
    private String description;
    private String dataType;

    @SuppressWarnings("unused")
    private ApiParameter() {
    }

    public ApiParameter(String paramType, String name, String description, String dataType) {
        this.paramType = paramType;
        this.name = name;
        this.description = description;
        this.dataType = dataType;
    }

    public String getParamType() {
        return paramType;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDataType() {
        return dataType;
    }

    public boolean getRequired() {
        return !paramType.equals("query");
    }

    public AllowableValues getAllowableValues() {
        if (dataType.equals("boolean")) {
            List<String> values = new ArrayList<String>();
            values.add("false");
            values.add("true");
            return new AllowableValues(values);
        } else {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiParameter that = (ApiParameter) o;
        return Objects.equal(paramType, that.paramType)
                && Objects.equal(name, that.name)
                && Objects.equal(description, that.description)
                && Objects.equal(dataType, that.dataType);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(paramType, name, description, dataType);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("paramType", paramType)
                .add("name", name)
                .add("description", description)
                .add("dataType", dataType)
                .toString();
    }
}
