package com.yammer.dropwizard.apidocs;

import java.util.List;

public class Operation {
    private String httpMethod;
    private String nickname;
    private String responseClass; // void, primitive, complex or a container
    private List<ApiParameter> parameters;
    private String summary; // cap at 60 characters for readability in the UI

    private Operation() { }

    public Operation(String httpMethod, String nickname, String responseClass, List<ApiParameter> parameters, String summary) {
        this.httpMethod = httpMethod;
        this.nickname = nickname;
        this.responseClass = responseClass;
        this.parameters = parameters;
        this.summary = summary;
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
}
