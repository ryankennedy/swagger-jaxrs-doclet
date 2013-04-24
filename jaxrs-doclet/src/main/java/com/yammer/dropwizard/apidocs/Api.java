package com.yammer.dropwizard.apidocs;

import java.util.List;

public class Api {
    private String path;
    private String description;
    private List<Operation> operations;

    @SuppressWarnings("unused")
    private Api() {
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Api api = (Api) o;

        if (description != null ? !description.equals(api.description) : api.description != null) return false;
        if (operations != null ? !operations.equals(api.operations) : api.operations != null) return false;
        if (path != null ? !path.equals(api.path) : api.path != null) return false;

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
        return "Api{" +
                "path='" + path + '\'' +
                ", description='" + description + '\'' +
                ", operations=" + operations +
                '}';
    }
}
