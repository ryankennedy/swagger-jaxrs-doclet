package com.yammer.dropwizard.apidocs;

import java.util.List;

public class Api {
    private String path;
    private String description;
    private List<Operation> operations;

    @SuppressWarnings("unused")
	private Api() { }

    public Api(String path, String description, List<Operation> operations) {
        this.path = path;
        this.description = description;
        this.operations = operations;
    }

    public String getPath() {
        return path;
    }

    public String getDescription() {
        return description;
    }

    public List<Operation> getOperations() {
        return operations;
    }
}
