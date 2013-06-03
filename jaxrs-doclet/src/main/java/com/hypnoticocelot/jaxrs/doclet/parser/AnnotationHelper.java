package com.hypnoticocelot.jaxrs.doclet.parser;

import com.google.common.base.Predicate;
import com.hypnoticocelot.jaxrs.doclet.DocletOptions;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Type;

import java.util.ArrayList;
import java.util.List;

public class AnnotationHelper {

    private static final String JAX_RS_ANNOTATION_PACKAGE = "javax.ws.rs";
    private static final String JAX_RS_PATH = "javax.ws.rs.Path";
    private static final String JAX_RS_PATH_PARAM = "javax.ws.rs.PathParam";
    private static final String JAX_RS_QUERY_PARAM = "javax.ws.rs.QueryParam";

    @SuppressWarnings("serial")
    static final List<String> PRIMITIVES = new ArrayList<String>() {{
        add("byte");
        add("boolean");
        add("int");
        add("long");
        add("float");
        add("double");
        add("string");
        add("Date");
    }};

    public static String parsePath(AnnotationDesc[] annotations) {
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
     * Determines the String representation of the object Type.
     */
    public static String typeOf(String javaType) {
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

    public static AnnotationDesc getAnnotation(ProgramElementDoc doc, String qualifiedAnnotationType) {
        if (doc == null) {
            return null;
        }
        return getAnnotation(doc.annotations(), qualifiedAnnotationType);
    }

    private static AnnotationDesc getAnnotation(AnnotationDesc[] annotations, String qualifiedAnnotationType) {
        AnnotationDesc found = null;
        for (AnnotationDesc annotation : annotations) {
            try {
                if (annotation.annotationType().qualifiedTypeName().equals(qualifiedAnnotationType)) {
                    found = annotation;
                    break;
                }
            } catch (RuntimeException e) {
                System.err.println(annotation + " has invalid javadoc: " + e.getClass() + ": " + e.getMessage());
            }
        }
        return found;
    }

    /**
     * Determines the string representation of the parameter type.
     */
    public static String paramTypeOf(Parameter parameter) {
        AnnotationDesc[] annotations = parameter.annotations();
        if (getAnnotation(annotations, JAX_RS_PATH_PARAM) != null) {
            return "path";
        } else if (getAnnotation(annotations, JAX_RS_QUERY_PARAM) != null) {
            return "query";
        }
        return "body";
    }

    /**
     * Determines the string representation of the parameter name.
     */
    public static String paramNameOf(Parameter parameter) {
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

    public static boolean isPrimitive(Type type) {
        return PRIMITIVES.contains(typeOf(type.qualifiedTypeName()));
    }

    public static class ExcludedAnnotations implements Predicate<AnnotationDesc> {
        private final DocletOptions options;

        public ExcludedAnnotations(DocletOptions options) {
            this.options = options;
        }

        @Override
        public boolean apply(AnnotationDesc annotationDesc) {
            String annotationClass = annotationDesc.annotationType().qualifiedTypeName();
            return options.getExcludeAnnotationClasses().contains(annotationClass);
        }
    }

    public static class JaxRsAnnotations implements Predicate<AnnotationDesc> {
        @Override
        public boolean apply(AnnotationDesc annotationDesc) {
            String annotationClass = annotationDesc.annotationType().qualifiedTypeName();
            return annotationClass.startsWith(JAX_RS_ANNOTATION_PACKAGE);
        }
    }

}
