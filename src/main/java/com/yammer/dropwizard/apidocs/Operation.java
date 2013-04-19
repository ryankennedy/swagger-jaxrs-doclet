package com.yammer.dropwizard.apidocs;

import java.util.List;

public class Operation {
    private String httpMethod;
    private String nickname;
    private String responseClass; // void, primitive, complex or a container
    private List<ApiParameter> parameters;
    private String summary; // cap at 60 characters for readability in the UI
    private String notes;

    @SuppressWarnings("unused")
    private Operation() {
    }

    public Operation(String httpMethod, String nickname, String responseClass, List<ApiParameter> parameters, String summary, String notes) {
        this.httpMethod = httpMethod;
        this.nickname = nickname;
        this.responseClass = responseClass;
        this.parameters = parameters;
        this.summary = summary;
        this.notes = notes;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getNickname() {
        return nickname;
    }

    public String getResponseClass() {
        return responseClass;
    }

    public List<ApiParameter> getParameters() {
        return parameters;
    }

    public String getSummary() {
        return summary;
    }

    public String getNotes() {
        return notes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Operation operation = (Operation) o;

        if (httpMethod != null ? !httpMethod.equals(operation.httpMethod) : operation.httpMethod != null) return false;
        if (nickname != null ? !nickname.equals(operation.nickname) : operation.nickname != null) return false;
        if (notes != null ? !notes.equals(operation.notes) : operation.notes != null) return false;
        if (parameters != null ? !parameters.equals(operation.parameters) : operation.parameters != null) return false;
        if (responseClass != null ? !responseClass.equals(operation.responseClass) : operation.responseClass != null)
            return false;
        if (summary != null ? !summary.equals(operation.summary) : operation.summary != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = httpMethod != null ? httpMethod.hashCode() : 0;
        result = 31 * result + (nickname != null ? nickname.hashCode() : 0);
        result = 31 * result + (responseClass != null ? responseClass.hashCode() : 0);
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        result = 31 * result + (summary != null ? summary.hashCode() : 0);
        result = 31 * result + (notes != null ? notes.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Operation{" +
                "httpMethod='" + httpMethod + '\'' +
                ", nickname='" + nickname + '\'' +
                ", responseClass='" + responseClass + '\'' +
                ", parameters=" + parameters +
                ", summary='" + summary + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}
