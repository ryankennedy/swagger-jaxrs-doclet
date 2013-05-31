package com.hypnoticocelot.jaxrs.doclet.model;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ApiDeclaration {
    private String apiVersion;
    private String basePath;
    private Collection<Api> apis;
    private Map<String, Model> models;

    @SuppressWarnings("unused")
    private ApiDeclaration() {
    }

    public ApiDeclaration(String apiVersion, String basePath, Collection<Api> apis, Collection<Model> models) {
        this.apiVersion = apiVersion;
        this.basePath = basePath;
        this.apis = apis;
        this.models = new HashMap<String, Model>(Maps.uniqueIndex(models, new Function<Model, String>() {
            @Override
            public String apply(Model model) {
                return model.getId();
            }
        }));
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
