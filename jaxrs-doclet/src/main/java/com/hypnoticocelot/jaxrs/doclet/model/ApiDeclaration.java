package com.hypnoticocelot.jaxrs.doclet.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.base.Objects;

import java.util.Collection;
import java.util.Map;

@JsonPropertyOrder({"apiVersion", "swaggerVersion", "basePath", "resourcePath", "apis", "models"})
public class ApiDeclaration {
    private String apiVersion;
    private String swaggerVersion;
    private String basePath;
    private String resourcePath;
    private Collection<Api> apis;
    private Map<String, Model> models;

    @SuppressWarnings("unused")
    private ApiDeclaration() {
    }

    public ApiDeclaration(String apiVersion, String basePath, String resourcePath, Collection<Api> apis, Map<String, Model> models) {
        this.apiVersion = apiVersion;
        this.swaggerVersion = "1.1";
        this.basePath = basePath;
        this.resourcePath = resourcePath;
        this.apis = apis;
        this.models = models;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public String getSwaggerVersion() {
        return swaggerVersion;
    }

    public String getBasePath() {
        return basePath;
    }

    public String getResourcePath() {
        return resourcePath;
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
                && Objects.equal(swaggerVersion, that.swaggerVersion)
                && Objects.equal(basePath, that.basePath)
                && Objects.equal(resourcePath, that.resourcePath)
                && Objects.equal(apis, that.apis)
                && Objects.equal(models, that.models);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(apiVersion, swaggerVersion, basePath, resourcePath, apis, models);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("apiVersion", apiVersion)
                .add("swaggerVersion", swaggerVersion)
                .add("basePath", basePath)
                .add("resourcePath", resourcePath)
                .add("apis", apis)
                .add("models", models)
                .toString();
    }
}
