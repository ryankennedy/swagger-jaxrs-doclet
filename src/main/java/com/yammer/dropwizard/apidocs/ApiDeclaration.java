package com.yammer.dropwizard.apidocs;

import java.util.List;

public class ApiDeclaration {
    private String apiVersion;
    private String basePath;
    private List<Api> apis;

    private ApiDeclaration() { }

    public ApiDeclaration(String apiVersion, String basePath, List<Api> apis) {
        this.apiVersion = apiVersion;
        this.basePath = basePath;
        this.apis = apis;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public String getSwaggerVersion() {
        return "1.1";
    }

    public String getBasePath() {
        return basePath;
    }

    public List<Api> getApis() {
        return apis;
    }
}
