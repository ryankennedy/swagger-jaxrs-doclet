package com.hypnoticocelot.jaxrs.doclet.model;

import com.google.common.base.Objects;

import java.util.Collection;

public class Api {
    private String path;
    private String description;
    private Collection<Operation> operations;

    @SuppressWarnings("unused")
    private Api() {
    }

    public Api(String path, String description, Collection<Operation> operations) {
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

    public Collection<Operation> getOperations() {
        return operations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Api that = (Api) o;
        return Objects.equal(path, that.path)
                && Objects.equal(description, that.description)
                && Objects.equal(operations, that.operations);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(path, description, operations);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("path", path)
                .add("description", description)
                .add("operations", operations)
                .toString();
    }
}
