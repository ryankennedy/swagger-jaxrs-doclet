package com.yammer.dropwizard.apidocs;

import java.util.List;

public class Method {
    private String method;
    private String methodName;
    private List<ApiParameter> apiParameters;
    private String comment;
    private String returnType;

    private Method() { }

    public Method(String method, String methodName, List<ApiParameter> apiParameters, String comment, String returnType) {
        this.method = method;
        this.methodName = methodName;
        this.apiParameters = apiParameters;
        this.comment = comment;
        this.returnType = returnType;
    }

    public String getMethod() {
        return method;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<ApiParameter> getParameters() {
        return apiParameters;
    }

    public String getComment() {
        return comment;
    }

    public String getReturnType() {
        return returnType;
    }
}
