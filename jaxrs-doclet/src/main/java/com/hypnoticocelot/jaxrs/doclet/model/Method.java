package com.hypnoticocelot.jaxrs.doclet.model;

import java.util.List;

public class Method {
    private HttpMethod method;
    private String methodName;
    private List<ApiParameter> apiParameters;
    private String firstSentence;
    private String comment;
    private String returnType;
    private String path;

    @SuppressWarnings("unused")
    private Method() {
    }

    public Method(HttpMethod method, String methodName, String path, List<ApiParameter> apiParameters, String firstSentence, String comment, String returnType) {
        this.method = method;
        this.methodName = methodName;
        this.path = path;
        this.apiParameters = apiParameters;
        this.firstSentence = firstSentence;
        this.comment = comment;
        this.returnType = returnType;
    }

    public HttpMethod getMethod() {
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

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Method method1 = (Method) o;

        if (apiParameters != null ? !apiParameters.equals(method1.apiParameters) : method1.apiParameters != null)
            return false;
        if (comment != null ? !comment.equals(method1.comment) : method1.comment != null) return false;
        if (firstSentence != null ? !firstSentence.equals(method1.firstSentence) : method1.firstSentence != null)
            return false;
        if (method != null ? !method.equals(method1.method) : method1.method != null) return false;
        if (methodName != null ? !methodName.equals(method1.methodName) : method1.methodName != null) return false;
        if (path != null ? !path.equals(method1.path) : method1.path != null) return false;
        if (returnType != null ? !returnType.equals(method1.returnType) : method1.returnType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = method != null ? method.hashCode() : 0;
        result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
        result = 31 * result + (apiParameters != null ? apiParameters.hashCode() : 0);
        result = 31 * result + (firstSentence != null ? firstSentence.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (returnType != null ? returnType.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Method{" +
                "method='" + method + '\'' +
                ", methodName='" + methodName + '\'' +
                ", apiParameters=" + apiParameters +
                ", firstSentence='" + firstSentence + '\'' +
                ", comment='" + comment + '\'' +
                ", returnType='" + returnType + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
