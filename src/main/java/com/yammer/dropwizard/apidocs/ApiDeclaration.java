package com.yammer.dropwizard.apidocs;

import java.util.List;
import java.util.Map;

public class ApiDeclaration {
    private String apiVersion;
    private String basePath;
    private List<Api> apis;
    private Map<String, Model> models;

    @SuppressWarnings("unused")
    private ApiDeclaration() {
    }

    public ApiDeclaration(String apiVersion, String basePath, List<Api> apis, Map<String, Model> models) {
        this.apiVersion = apiVersion;
        this.basePath = basePath;
        this.apis = apis;
        this.models = models;
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

    public Map<String, Model> getModels() {
        return models;
    }
}
