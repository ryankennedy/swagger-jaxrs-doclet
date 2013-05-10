package com.hypnoticocelot.jaxrs.doclet;

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

        if (dataType != null ? !dataType.equals(that.dataType) : that.dataType != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (paramType != null ? !paramType.equals(that.paramType) : that.paramType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = paramType != null ? paramType.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (dataType != null ? dataType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ApiParameter{" +
                "paramType='" + paramType + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", dataType='" + dataType + '\'' +
                '}';
    }
}
