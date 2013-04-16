package com.yammer.dropwizard.apidocs;

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

}
