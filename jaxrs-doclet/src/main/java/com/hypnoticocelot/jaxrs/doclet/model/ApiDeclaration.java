package com.hypnoticocelot.jaxrs.doclet.model;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApiDeclaration that = (ApiDeclaration) o;

        if (apiVersion != null ? !apiVersion.equals(that.apiVersion) : that.apiVersion != null) return false;
        if (apis != null ? !apis.equals(that.apis) : that.apis != null) return false;
        if (basePath != null ? !basePath.equals(that.basePath) : that.basePath != null) return false;
        if (models != null ? !models.equals(that.models) : that.models != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = apiVersion != null ? apiVersion.hashCode() : 0;
        result = 31 * result + (basePath != null ? basePath.hashCode() : 0);
        result = 31 * result + (apis != null ? apis.hashCode() : 0);
        result = 31 * result + (models != null ? models.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ApiDeclaration{" +
                "apiVersion='" + apiVersion + '\'' +
                ", basePath='" + basePath + '\'' +
                ", apis=" + apis +
                ", models=" + models +
                '}';
    }
}
