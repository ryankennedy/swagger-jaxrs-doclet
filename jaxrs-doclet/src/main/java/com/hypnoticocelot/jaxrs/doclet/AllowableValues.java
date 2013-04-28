package com.hypnoticocelot.jaxrs.doclet;

import java.util.List;

public class AllowableValues {
    private List<String> values;

    @SuppressWarnings("unused")
    private AllowableValues() {

    }

    public AllowableValues(List<String> values) {
        this.values = values;
    }

    public String getValueType() {
        return "List";
    }

    public List<String> getValues() {
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AllowableValues that = (AllowableValues) o;

        if (values != null ? !values.equals(that.values) : that.values != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return values != null ? values.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "AllowableValues{" +
                "values=" + values +
                '}';
    }
}
