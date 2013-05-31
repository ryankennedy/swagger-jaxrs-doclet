package com.hypnoticocelot.jaxrs.doclet.parser;

import com.google.common.base.Function;
import com.hypnoticocelot.jaxrs.doclet.DocletOptions;
import com.hypnoticocelot.jaxrs.doclet.model.Api;
import com.hypnoticocelot.jaxrs.doclet.model.Method;
import com.hypnoticocelot.jaxrs.doclet.model.Model;
import com.hypnoticocelot.jaxrs.doclet.model.Operation;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;

import java.util.*;

import static com.google.common.collect.Collections2.transform;
import static com.hypnoticocelot.jaxrs.doclet.parser.AnnotationHelper.parsePath;

public class ApiClassParser {

    private final DocletOptions options;
    private final ClassDoc classDoc;
    private final String apiPath;
    private final Set<Model> models;

    public ApiClassParser(DocletOptions options, ClassDoc classDoc) {
        this.options = options;
        this.classDoc = classDoc;
        this.apiPath = parsePath(classDoc.annotations());
        this.models = new LinkedHashSet<Model>();
    }

    public Collection<Api> parse() {
        Map<String, Collection<Method>> apiMethods = new HashMap<String, Collection<Method>>();

        for (MethodDoc method : classDoc.methods()) {
            ApiMethodParser methodParser = new ApiMethodParser(options, apiPath, method);
            Method parsedMethod = methodParser.parse();
            if (parsedMethod == null) {
                continue;
            }
            models.addAll(methodParser.models());

            String realPath = parsedMethod.getPath();
            Collection<Method> matchingMethods = apiMethods.get(realPath);
            if (matchingMethods == null) {
                matchingMethods = new ArrayList<Method>();
                apiMethods.put(realPath, matchingMethods);
            }
            matchingMethods.add(parsedMethod);
        }

        List<Api> apis = new ArrayList<Api>();
        for (Map.Entry<String, Collection<Method>> apiEntries : apiMethods.entrySet()) {
            Collection<Operation> operations = new ArrayList<Operation>(
                    transform(apiEntries.getValue(), new Function<Method, Operation>() {
                        @Override
                        public Operation apply(Method method) {
                            return new Operation(method);
                        }
                    })
            );
            apis.add(new Api(apiEntries.getKey(), "", operations));
        }
        Collections.sort(apis, new Comparator<Api>() {
            @Override
            public int compare(Api o1, Api o2) {
                return o1.getPath().compareTo(o2.getPath());
            }
        });
        return apis;
    }

    public Collection<Model> models() {
        return models;
    }

}
