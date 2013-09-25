package com.hypnoticocelot.jaxrs.doclet.parser;

import com.google.common.base.Function;
import com.hypnoticocelot.jaxrs.doclet.DocletOptions;
import com.hypnoticocelot.jaxrs.doclet.model.Api;
import com.hypnoticocelot.jaxrs.doclet.model.Method;
import com.hypnoticocelot.jaxrs.doclet.model.Model;
import com.hypnoticocelot.jaxrs.doclet.model.Operation;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Type;

import java.util.*;

import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.collect.Collections2.transform;
import static com.hypnoticocelot.jaxrs.doclet.parser.AnnotationHelper.parsePath;

public class ApiClassParser {

    private final DocletOptions options;
    private final ClassDoc classDoc;
    private final String rootPath;
    private final Set<Model> models;
    private final Collection<ClassDoc> classes;

    public ApiClassParser(DocletOptions options, ClassDoc classDoc, Collection<ClassDoc> classes) {
        this.options = options;
        this.classDoc = classDoc;
        this.rootPath = firstNonNull(parsePath(classDoc.annotations()), "");
        this.models = new LinkedHashSet<Model>();
        this.classes = classes;
    }

    public String getRootPath() {
        return rootPath;
    }

    public Collection<Api> parse() {
        List<Api> apis = new ArrayList<Api>();
        Map<String, Collection<Method>> apiMethods = new HashMap<String, Collection<Method>>();

        for (MethodDoc method : classDoc.methods()) {
            ApiMethodParser methodParser = new ApiMethodParser(options, rootPath, method);
            Method parsedMethod = methodParser.parse();
            if (parsedMethod == null) {
                if (methodParser.isSubResource()) {
                    ClassDoc subResourceClassDoc = lookUpClassDoc(method.returnType());
                    if (subResourceClassDoc != null) {
                        String subResourcePath = firstNonNull(parsePath(subResourceClassDoc.annotations()), "");

                        // delete class from the dictionary to handle recursive sub-resources
                        Collection<ClassDoc> shrunkClasses = new ArrayList<ClassDoc>(classes);
                        shrunkClasses.remove(classDoc);

                        // recursively parser the sub-resource class
                        ApiClassParser subResourceParser = new ApiClassParser(options, subResourceClassDoc, shrunkClasses);
                        for (Api api : subResourceParser.parse()) {
                            String subApiPath = methodParser.getPath() + api.getPath().substring(subResourcePath.length());
                            apis.add(new Api(subApiPath, api.getDescription(), api.getOperations()));
                        }
                        models.addAll(subResourceParser.models());
                    }
                }
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

    private ClassDoc lookUpClassDoc(Type type) {
        for (ClassDoc subResourceClassDoc : classes) {
            if (subResourceClassDoc.qualifiedTypeName().equals(type.qualifiedTypeName())) {
                return subResourceClassDoc;
            }
        }
        return null;
    }

    public Collection<Model> models() {
        return models;
    }

}
