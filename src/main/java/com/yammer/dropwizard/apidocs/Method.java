package com.yammer.dropwizard.apidocs;

import java.util.List;

public class Method {
    private String method;
    private String methodName;
    private List<ApiParameter> apiParameters;
    private String firstSentence;
    private String comment;
    private String returnType;
    private String path;

    private Method() { }

    public Method(String method, String methodName, String path, List<ApiParameter> apiParameters, String firstSentence, String comment, String returnType) {
        this.method = method;
        this.methodName = methodName;
        this.path = path;
        this.apiParameters = apiParameters;
        this.firstSentence = firstSentence;
        this.comment = comment;
        this.returnType = returnType;
    }

    public String getMethod() {
        return method;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getPath() {
    	return path;
    }
    
    public List<ApiParameter> getParameters() {
        return apiParameters;
    }

    public String getFirstSentence() {
    	return firstSentence;
    }
    
    public String getComment() {
        return comment;
    }

    public String getReturnType() {
        return returnType;
    }
}
