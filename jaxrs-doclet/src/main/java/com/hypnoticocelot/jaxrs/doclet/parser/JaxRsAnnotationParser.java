package com.hypnoticocelot.jaxrs.doclet.parser;

import com.google.common.base.Predicate;
import com.hypnoticocelot.jaxrs.doclet.DocletOptions;
import com.hypnoticocelot.jaxrs.doclet.ObjectMapperRecorder;
import com.hypnoticocelot.jaxrs.doclet.Recorder;
import com.hypnoticocelot.jaxrs.doclet.ServiceDoclet;
import com.hypnoticocelot.jaxrs.doclet.model.*;
import com.sun.javadoc.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.google.common.collect.Collections2.filter;

public class JaxRsAnnotationParser {

    private static final String JAX_RS_PATH = "javax.ws.rs.Path";
    private static final String JAX_RS_PATH_PARAM = "javax.ws.rs.PathParam";
    private static final String JAX_RS_QUERY_PARAM = "javax.ws.rs.QueryParam";
    private static final String XML_ROOT_ELEMENT = "javax.xml.bind.annotation.XmlRootElement";
    private static final String JAX_RS_ANNOTATION_PACKAGE = "javax.ws.rs";

    @SuppressWarnings("serial")
    private static final List<String> PRIMITIVES = new ArrayList<String>() {{
        add("byte");
        add("boolean");
        add("int");
        add("long");
        add("float");
        add("double");
        add("string");
        add("Date");
    }};

    // ----------------------------------------------------------------------

    private final DocletOptions options;
    private final Recorder recorder;

    private final Map<String, Map<String, List<Method>>> apiMap;
    private final Map<String, Map<String, Model>> modelMap;

    public JaxRsAnnotationParser(DocletOptions options) {
        this.options = options;
        this.recorder = new ObjectMapperRecorder();
        apiMap = new HashMap<String, Map<String, List<Method>>>();
        modelMap = new HashMap<String, Map<String, Model>>();
    }

    public boolean parse(RootDoc rootDoc) {
        try {
            List<ResourceListingAPI> builder = new LinkedList<ResourceListingAPI>();

            for (ClassDoc classDoc : rootDoc.classes()) {
                //go through each class
                String apiPath = this.path(classDoc.annotations());
                if (apiPath != null) {
                    Map<String, List<Method>> methodMap = apiMap.get(apiPath);
                    if (methodMap == null) {
                        methodMap = new HashMap<String, List<Method>>();
                    }
                    Map<String, Model> classModelMap = modelMap.get(apiPath);
                    if (classModelMap == null) {
                        classModelMap = new HashMap<String, Model>();
                    }

                    //add all jax-rs annotated methods to the methodmap
                    for (MethodDoc method : classDoc.methods()) {
                        Method me = this.parseMethod(method);
                        if (me != null) {
                            List<Method> methods = methodMap.get(me.getPath());
                            if (methods == null) {
                                methods = new ArrayList<Method>();
                            }
                            methods.add(me);
                            methodMap.put(me.getPath(), methods);

                            if (options.isParseModels()) {
                                //build models for method parameters
                                for (Parameter parameter : method.parameters()) {
                                    if (this.shouldIncludeParameter(me.getMethod(), parameter)) {
                                        classModelMap = this.parseModels(parameter.type(), classModelMap);
                                    }
                                }

                                //build model for method return type
                                Type type = method.returnType();
                                if (!type.simpleTypeName().equalsIgnoreCase("void")) {
                                    String name = this.typeOf(type);
                                    if (me.getReturnType() == null || !me.getReturnType().equals(name)) {
                                        me.setReturnType(name);
                                    }
                                    classModelMap = this.parseModels(type, classModelMap);
                                }
                            }
                        }
                    }
                    apiMap.put(apiPath, methodMap);
                    modelMap.put(apiPath, classModelMap);
                }
            }

            //Sort the classes based upon class path annotation
            List<String> apiList = new ArrayList<String>(apiMap.keySet());
            Collections.sort(apiList);

            for (String apiPath : apiList) {
                List<Api> apiBuilder = new LinkedList<Api>();

                Map<String, List<Method>> methodMap = apiMap.get(apiPath);
                List<String> keyList = new ArrayList<String>(methodMap.keySet());
                Collections.sort(keyList);
                for (String path : keyList) {
                    //turn list of methods into list of api objects
                    List<Operation> methodBuilder = new LinkedList<Operation>();

                    for (Method me : methodMap.get(path)) {
                        methodBuilder.add(new Operation(me.getMethod(), me.getMethodName(), this.typeOf(me.getReturnType()),
                                me.getParameters(), me.getFirstSentence(), me.getComment()));
                    }
                    apiBuilder.add(new Api(apiPath + path, "", methodBuilder));
                }

                //write out json for methods
                String rootPath = (apiPath.startsWith("/") ? apiPath.replaceFirst("/", "") : apiPath).replaceAll("/", "_").replaceAll("(\\{|\\})", "");
                builder.add(new ResourceListingAPI("/" + rootPath + ".{format}", ""));

                File apiFile = new File(options.getOutput(), rootPath + ".json");
                ApiDeclaration declaration = new ApiDeclaration(options.getApiVersion(), options.getApiBasePath(), apiBuilder, modelMap.get(apiPath));

                recorder.record(apiFile, declaration);
            }

            //write out json for api
            ResourceListing listing = new ResourceListing(options.getApiVersion(), options.getDocBasePath(), builder);
            File docFile = new File(options.getOutput(), "service.json");
            recorder.record(docFile, listing);

            // Copy swagger-ui into the output directory.
            final ZipInputStream swaggerZip = new ZipInputStream(ServiceDoclet.class.getResourceAsStream("/swagger-ui.zip"));
            ZipEntry entry = swaggerZip.getNextEntry();
            while (entry != null) {
                final File swaggerFile = new File(options.getOutput(), entry.getName());
                if (entry.isDirectory()) {
                    if (!swaggerFile.isDirectory() && !swaggerFile.mkdirs()) {
                        throw new RuntimeException("Unable to create directory: " + swaggerFile);
                    }
                } else {
                    recorder.record(swaggerFile, swaggerZip);
                }

                entry = swaggerZip.getNextEntry();
            }
            swaggerZip.close();

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Gets the string representation of the jax-rs path from an array of annotations.
     */
    private String path(AnnotationDesc[] annotations) {
        for (AnnotationDesc annotationDesc : annotations) {
            if (annotationDesc.annotationType().qualifiedTypeName().equals(JAX_RS_PATH)) {
                for (AnnotationDesc.ElementValuePair pair : annotationDesc.elementValues()) {
                    if (pair.element().name().equals("value")) {
                        String path = pair.value().value().toString();
                        if (path.endsWith("/")) {
                            path = path.substring(0, path.length() - 1);
                        }
                        return path.startsWith("/") ? path : "/" + path;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Turns a MethodDoc(Javadoc) into a swagger serialize-able method object.
     */
    private Method parseMethod(MethodDoc method) {
        HttpMethod httpMethod = HttpMethod.fromMethod(method);
        if (httpMethod == null) {
            return null;
        }

        //Path
        String path = path(method.annotations());
        if (path == null) path = "";

        //Parameters
        List<ApiParameter> parameterBuilder = new LinkedList<ApiParameter>();

        for (Parameter parameter : method.parameters()) {
            if (shouldIncludeParameter(httpMethod, parameter)) {
                String parameterComment = commentForParameter(method, parameter);
                parameterBuilder.add(new ApiParameter(paramTypeOf(parameter), paramNameOf(parameter),
                        parameterComment,
                        typeOf(parameter.type())));
            }
        }

        //First Sentence of Javadoc method description
        Tag[] fst = method.firstSentenceTags();
        StringBuilder fssBuffer = new StringBuilder();
        for (Tag t : fst) {
            fssBuffer.append(t.text());
        }
        String fss = fssBuffer.toString();

        return new Method(httpMethod,
                method.name(),
                path,
                parameterBuilder,
                fss,
                method.commentText().replace(fss, ""),
                method.returnType().qualifiedTypeName());
    }

    /**
     * Recursively adds models to the model map for a type
     */
    private Map<String, Model> parseModels(Type type, Map<String, Model> modelMap) {
        String typeName = typeOf(type);
        if (!PRIMITIVES.contains(typeName) && !type.qualifiedTypeName().startsWith("javax.")) {
            ClassDoc cd = type.asClassDoc();
            if (cd != null) {
                Model model = modelMap.get(typeName);
                if (model == null) {
                    Map<String, Type> eleMap = new HashMap<String, Type>();

                    //Get fields
                    FieldDoc[] fdArr = cd.fields();
                    if (fdArr != null && fdArr.length > 0) {
                        for (FieldDoc fd : fdArr) {
                            if (eleMap.get(fd.name()) == null) {
                                eleMap.put(fd.name(), fd.type());
                            }
                        }
                    }

                    //Get methods
                    MethodDoc[] mdArr = cd.methods();
                    if (mdArr != null && mdArr.length > 0) {
                        for (MethodDoc md : mdArr) {
                            if (md.name().startsWith("get") && md.name().length() > 3) {
                                String name = md.name().substring(3);
                                name = name.substring(0, 1).toLowerCase() + (name.length() > 1 ? name.substring(1) : "");
                                if (eleMap.get(name) == null) {
                                    eleMap.put(name, md.returnType());
                                }
                            }
                        }
                    }

                    //Process all fields & methods
                    if (eleMap.keySet().size() > 0) {

                        //Add to the model
                        Map<String, Property> fieldMap = new HashMap<String, Property>();
                        for (Map.Entry<String, Type> entry : eleMap.entrySet()) {
                            //Check if it is a collection and get collection type
                            String containerOf = null;
                            ParameterizedType pt = entry.getValue().asParameterizedType();
                            if (pt != null) {
                                Type[] typeArgs = pt.typeArguments();
                                if (typeArgs != null && typeArgs.length > 0) {
                                    containerOf = typeOf(typeArgs[0]);
                                }
                            }

                            //Add to map
                            String eleTypeName = typeOf(entry.getValue());
                            fieldMap.put(entry.getKey(), new Property(eleTypeName, null, containerOf));

                        }

                        modelMap.put(typeName, new Model(typeName, fieldMap));

                        //Build contained models
                        for (Map.Entry<String, Type> entry : eleMap.entrySet()) {
                            //Check if it is a collection and get collection type
                            ParameterizedType pt = entry.getValue().asParameterizedType();
                            if (pt != null) {
                                Type[] typeArgs = pt.typeArguments();
                                if (typeArgs != null && typeArgs.length > 0) {
                                    parseModels(typeArgs[0], modelMap);
                                }
                            }

                            parseModels(entry.getValue(), modelMap);
                        }
                    }
                }
            }
        }
        return modelMap;
    }

    /**
     * Determines if a parameter should be included, based upon annotation package.
     */
    private boolean shouldIncludeParameter(HttpMethod httpMethod, Parameter parameter) {
        List<AnnotationDesc> allAnnotations = Arrays.asList(parameter.annotations());
        Collection<AnnotationDesc> excluded = filter(allAnnotations, new ExcludedAnnotations());
        if (!excluded.isEmpty()) {
            return false;
        }

        Collection<AnnotationDesc> jaxRsAnnotations = filter(allAnnotations, new JaxRsAnnotations());
        if (!jaxRsAnnotations.isEmpty()) {
            return true;
        }

        return (allAnnotations.isEmpty() || httpMethod == HttpMethod.POST);
    }

    /**
     * Determines the string representation of the parameter type.
     */
    private static String paramTypeOf(Parameter parameter) {
        AnnotationDesc[] annotations = parameter.annotations();
        for (AnnotationDesc annotation : annotations) {
            String annotationTypeName = annotation.annotationType().qualifiedTypeName();
            if (annotationTypeName.equals(JAX_RS_PATH_PARAM)) {
                return "path";
            } else if (annotationTypeName.equals(JAX_RS_QUERY_PARAM)) {
                return "query";
            }
        }
        return "body";
    }

    /**
     * Determines the XmlRootElement name. Returns null if no name found.
     */
    private static String getRootElementNameOf(ClassDoc classDoc) {
        AnnotationDesc[] annotations = classDoc.annotations();
        for (AnnotationDesc annotation : annotations) {
            String annotationTypeName = annotation.annotationType().qualifiedTypeName();
            if (annotationTypeName.equals(XML_ROOT_ELEMENT)) {
                AnnotationDesc.ElementValuePair[] evpArr = annotation.elementValues();
                if (evpArr.length > 0) {
                    for (AnnotationDesc.ElementValuePair evp : evpArr) {
                        if (evp.element().name().equals("name")) {
                            return evp.value().value().toString();
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Determines the string representation of the parameter name.
     */
    private static String paramNameOf(Parameter parameter) {
        AnnotationDesc[] annotations = parameter.annotations();
        for (AnnotationDesc annotation : annotations) {
            String annotationTypeName = annotation.annotationType().qualifiedTypeName();
            if (annotationTypeName.equals(JAX_RS_PATH_PARAM) || annotationTypeName.equals(JAX_RS_QUERY_PARAM)) {
                AnnotationDesc.ElementValuePair[] evpArr = annotation.elementValues();
                if (evpArr.length > 0) {
                    for (AnnotationDesc.ElementValuePair evp : evpArr) {
                        if (evp.element().name().equals("value")) {
                            return evp.value().value().toString();
                        }
                    }
                }
            }
        }
        return parameter.name();
    }

    /**
     * Determines the String representation of the object Type.
     */
    private String typeOf(String javaType) {
        String type;
        if (javaType.startsWith("java.lang.")) {
            int i = javaType.lastIndexOf(".");
            type = javaType.substring(i + 1).toLowerCase();
        } else if (PRIMITIVES.contains(javaType.toLowerCase())) {
            type = javaType.toLowerCase();
        } else if (javaType.equals("java.util.Date")) {
            type = "Date";
        } else {
            int i = javaType.lastIndexOf(".");
            if (i >= 0) {
                type = javaType.substring(i + 1);
            } else {
                type = javaType;
            }
        }
        if (type.equalsIgnoreCase("integer")) {
            type = "int";
        } else if (type.equalsIgnoreCase("arraylist") || type.equalsIgnoreCase("linkedlist")) {
            type = "List";
        }
        return type;
    }

    private String typeOf(Type type) {
        String name;
        ClassDoc cd = type.asClassDoc();
        if (cd != null) {
            name = getRootElementNameOf(cd);
            if (name == null) {
                name = typeOf(type.qualifiedTypeName());
            }
        } else {
            name = typeOf(type.qualifiedTypeName());
        }
        return name;
    }

    /**
     * Gets the string representation of the parameter comment from the Javadoc.
     */
    private static String commentForParameter(MethodDoc method, Parameter parameter) {
        for (ParamTag tag : method.paramTags()) {
            if (tag.parameterName().equals(parameter.name())) {
                return tag.parameterComment();
            }
        }
        return null;
    }

    private class ExcludedAnnotations implements Predicate<AnnotationDesc> {
        @Override
        public boolean apply(AnnotationDesc annotationDesc) {
            String annotationClass = annotationDesc.annotationType().qualifiedTypeName();
            return options.getExcludeAnnotationClasses().contains(annotationClass);
        }
    }

    private class JaxRsAnnotations implements Predicate<AnnotationDesc> {
        @Override
        public boolean apply(AnnotationDesc annotationDesc) {
            String annotationClass = annotationDesc.annotationType().qualifiedTypeName();
            return annotationClass.startsWith(JAX_RS_ANNOTATION_PACKAGE);
        }
    }

}
