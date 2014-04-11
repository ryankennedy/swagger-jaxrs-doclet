
package com.hypnoticocelot.jaxrs.doclet.parser;

import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.collect.Collections2.transform;
import static com.hypnoticocelot.jaxrs.doclet.parser.AnnotationHelper.parsePath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.hypnoticocelot.jaxrs.doclet.DocletOptions;
import com.hypnoticocelot.jaxrs.doclet.model.Api;
import com.hypnoticocelot.jaxrs.doclet.model.Method;
import com.hypnoticocelot.jaxrs.doclet.model.Model;
import com.hypnoticocelot.jaxrs.doclet.model.Operation;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Type;

public class ApiClassParser
{

    private final DocletOptions options;
    private final ClassDoc classDoc;
    private final String rootPath;
    private final Set<Model> models;
    private final Collection<ClassDoc> classes;
    private final Method parentMethod;

    public ApiClassParser(final DocletOptions options, final ClassDoc classDoc, final Collection<ClassDoc> classes)
    {
        this.options = options;
        this.classDoc = classDoc;
        this.rootPath = firstNonNull(parsePath(classDoc.annotations()), "");
        this.models = new LinkedHashSet<Model>();
        this.classes = classes;
        this.parentMethod = null;
    }

    /**
     * Creates sub-resource class parser.
     * 
     * @param parentMethod method that creates the sub-resource.
     */
    public ApiClassParser(final DocletOptions options, final ClassDoc classDoc, final Collection<ClassDoc> classes,
            final Method parentMethod)
    {
        this.options = options;
        this.classDoc = classDoc;
        this.rootPath = firstNonNull(parsePath(classDoc.annotations()), "");
        this.models = new LinkedHashSet<Model>();
        this.classes = classes;
        this.parentMethod = parentMethod;
    }

    public String getRootPath()
    {
        return rootPath;
    }

    public Collection<Api> parse()
    {
        final List<Api> apis = new ArrayList<Api>();
        final Map<String, Collection<Method>> apiMethods = new HashMap<String, Collection<Method>>();

        ClassDoc currentClassDoc = classDoc;
        while (currentClassDoc != null) {
            for (final MethodDoc method : currentClassDoc.methods()) {
                final ApiMethodParser methodParser = parentMethod == null ? new ApiMethodParser(options, rootPath,
                        method) : new ApiMethodParser(options, parentMethod, method);
                final Method parsedMethod = methodParser.parse();
                if (parsedMethod == null) {
                    continue;
                }
                if (parsedMethod.isSubResource()) {
                    final ClassDoc subResourceClassDoc = lookUpClassDoc(method.returnType());
                    if (subResourceClassDoc != null) {
                        // delete class from the dictionary to handle recursive sub-resources
                        final Collection<ClassDoc> shrunkClasses = new ArrayList<ClassDoc>(classes);
                        shrunkClasses.remove(currentClassDoc);
                        // recursively parse the sub-resource class
                        final ApiClassParser subResourceParser = new ApiClassParser(options, subResourceClassDoc,
                                shrunkClasses, parsedMethod);
                        apis.addAll(subResourceParser.parse());
                        models.addAll(subResourceParser.models());
                    }
                    continue;
                }
                models.addAll(methodParser.models());

                final String realPath = parsedMethod.getPath();
                Collection<Method> matchingMethods = apiMethods.get(realPath);
                if (matchingMethods == null) {
                    matchingMethods = new ArrayList<Method>();
                    apiMethods.put(realPath, matchingMethods);
                }
                matchingMethods.add(parsedMethod);
            }
            currentClassDoc = currentClassDoc.superclass();
        }

        for (final Map.Entry<String, Collection<Method>> apiEntries : apiMethods.entrySet()) {
            final Collection<Operation> operations = new ArrayList<Operation>(transform(apiEntries.getValue(),
                    new Function<Method, Operation>()
                    {
                        @Override
                        public Operation apply(final Method method)
                        {
                            return new Operation(method);
                        }
                    }));
            apis.add(new Api(apiEntries.getKey(), "", operations));
        }
        Collections.sort(apis, new Comparator<Api>()
        {
            @Override
            public int compare(final Api o1, final Api o2)
            {
                return o1.getPath().compareTo(o2.getPath());
            }
        });
        return apis;
    }

    private ClassDoc lookUpClassDoc(final Type type)
    {
        for (final ClassDoc subResourceClassDoc : classes) {
            if (subResourceClassDoc.qualifiedTypeName().equals(type.qualifiedTypeName())) {
                return subResourceClassDoc;
            }
        }
        return null;
    }

    public Collection<Model> models()
    {
        return models;
    }

}