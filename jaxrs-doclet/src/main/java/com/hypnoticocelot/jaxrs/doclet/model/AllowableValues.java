package com.hypnoticocelot.jaxrs.doclet.model;

import com.google.common.base.Objects;

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
        return Objects.equal(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(values);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("values", values)
                .toString();
    }
}
