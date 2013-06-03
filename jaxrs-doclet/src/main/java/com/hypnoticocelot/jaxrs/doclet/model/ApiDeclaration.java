package com.hypnoticocelot.jaxrs.doclet.model;

import com.google.common.base.Objects;

import java.util.Collection;
import java.util.Map;

public class ApiDeclaration {
    private String apiVersion;
    private String basePath;
    private Collection<Api> apis;
    private Map<String, Model> models;

    @SuppressWarnings("unused")
    private ApiDeclaration() {
    }

    public ApiDeclaration(String apiVersion, String basePath, Collection<Api> apis, Map<String, Model> models) {
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

    public Collection<Api> getApis() {
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
        return Objects.equal(apiVersion, that.apiVersion)
                && Objects.equal(apis, that.apis)
                && Objects.equal(basePath, that.basePath)
                && Objects.equal(models, that.models);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(apiVersion, basePath, apis, models);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("apiVersion", apiVersion)
                .add("basePath", basePath)
                .add("apis", apis)
                .add("models", models)
                .toString();
    }
}
