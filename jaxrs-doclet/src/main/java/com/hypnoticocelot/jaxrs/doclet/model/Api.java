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

        Api api = (Api) o;

        if (path != null ? !path.equals(api.path) : api.path != null) return false;
        if (description != null ? !description.equals(api.description) : api.description != null) return false;
        if (operations != null ? !operations.equals(api.operations) : api.operations != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (operations != null ? operations.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "\n--->" + Objects.toStringHelper(this)
                .add("path", path)
                .add("description", description)
                .add("operations", operations)
                .toString();
    }
}
