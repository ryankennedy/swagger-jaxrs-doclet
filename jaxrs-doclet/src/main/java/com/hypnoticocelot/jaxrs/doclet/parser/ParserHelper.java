package com.hypnoticocelot.jaxrs.doclet.parser;

import com.hypnoticocelot.jaxrs.doclet.model.Api;
import com.hypnoticocelot.jaxrs.doclet.model.ApiDeclaration;
import com.hypnoticocelot.jaxrs.doclet.model.Model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ParserHelper {

    public static Api findApiWithSamePath(Collection<Api> apis, Api api) {
        for (Api a : apis) {
            if (a.getPath().equals(api.getPath())) {
                return a;
            }
        }
        return null;
    }

    public static void mergeApis(Collection<Api> apis, Collection<Api> apis1) {
        for (Api api : apis1) {
            Api samePathApi = findApiWithSamePath(apis, api);
            if (samePathApi == null) {
                apis.add(api);
            } else {
                samePathApi.getOperations().addAll(api.getOperations());
            }
        }
    }

    public static ApiDeclaration findDeclarationWithSamePath(Collection<ApiDeclaration> declarations,
                                                             ApiDeclaration declaration) {
        for (ApiDeclaration d : declarations) {
            if (d.getResourcePath().equals(declaration.getResourcePath())) {
                return d;
            }
        }
        return null;
    }

    public static void mergeDeclarations(Collection<ApiDeclaration> declarations, ApiDeclaration declaration) {
        ApiDeclaration samePathDeclaration = findDeclarationWithSamePath(declarations, declaration);
        if (samePathDeclaration == null) {
            declarations.add(declaration);
            return;
        }

        declarations.remove(samePathDeclaration);
        mergeApis(samePathDeclaration.getApis(), declaration.getApis());
        Map<String, Model> models = new HashMap<>(samePathDeclaration.getModels());
        models.putAll(declaration.getModels());
        declarations.add(new ApiDeclaration(declaration.getApiVersion(),
                declaration.getBasePath(), declaration.getResourcePath(), samePathDeclaration.getApis(), models));
    }
}
