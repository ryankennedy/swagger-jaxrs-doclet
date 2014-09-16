
package com.hypnoticocelot.jaxrs.doclet.parser;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Predicate;
import com.hypnoticocelot.jaxrs.doclet.DocletOptions;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.Type;

public class AnnotationHelper
{

    private static final String JAX_RS_ANNOTATION_PACKAGE = "javax.ws.rs";
    private static final String JAX_RS_PATH = "javax.ws.rs.Path";
    private static final String JAX_RS_PATH_PARAM = "javax.ws.rs.PathParam";
    private static final String JAX_RS_HEADER_PARAM = "javax.ws.rs.HeaderParam";
    private static final String JAX_RS_QUERY_PARAM = "javax.ws.rs.QueryParam";
    private static final String JERSEY_MULTIPART_FORM_PARAM = "com.sun.jersey.multipart.FormDataParam";
    private static final String DROPWIZARD_AUTH = "io.dropwizard.auth.Auth";

    @SuppressWarnings("serial")
    static final List<String> PRIMITIVES = new ArrayList<String>()
    {
        {
            add("byte");
            add("boolean");
            add("int");
            add("long");
            add("float");
            add("double");
            add("string");
            add("Date");
        }
    };

    public static String parsePath(final AnnotationDesc[] annotations)
    {
        for (final AnnotationDesc annotationDesc : annotations) {
            if (annotationDesc.annotationType().qualifiedTypeName().equals(JAX_RS_PATH)) {
                for (final AnnotationDesc.ElementValuePair pair : annotationDesc.elementValues()) {
                    if (pair.element().name().equals("value")) {
                        String path = pair.value().value().toString();
                        if (path.endsWith("/")) {
                            path = path.substring(0, path.length() - 1);
                        }
                        return path.isEmpty() || path.startsWith("/") ? path : "/" + path;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Determines the String representation of the object Type.
     */
    public static String typeOf(final String javaType)
    {
        String type;
        if (javaType.startsWith("java.lang.")) {
            final int i = javaType.lastIndexOf(".");
            type = javaType.substring(i + 1).toLowerCase();
        } else if (PRIMITIVES.contains(javaType.toLowerCase())) {
            type = javaType.toLowerCase();
        } else if (javaType.equals("java.util.Date")) {
            type = "Date";
        } else {
            final int i = javaType.lastIndexOf(".");
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

    /**
     * Determines the string representation of the parameter type.
     */
    public static String paramTypeOf(final Parameter parameter)
    {
        final AnnotationParser p = new AnnotationParser(parameter);
        if (p.isAnnotatedBy(JAX_RS_PATH_PARAM)) {
            return "path";
        } else if (p.isAnnotatedBy(JAX_RS_HEADER_PARAM)) {
            return "header";
        } else if (p.isAnnotatedBy(JAX_RS_QUERY_PARAM)) {
            return "query";
        } else if (p.isAnnotatedBy(JERSEY_MULTIPART_FORM_PARAM)) {
            return "form";
        } else if (p.isAnnotatedBy(DROPWIZARD_AUTH)) {
            return "auth";
        }

        return "body";
    }

    /**
     * Determines the string representation of the parameter name.
     */
    public static String paramNameOf(final Parameter parameter)
    {
        // TODO (DL): make this part of Translator?
        final AnnotationParser p = new AnnotationParser(parameter);
        String name = p.getAnnotationValue(JAX_RS_PATH_PARAM, "value");
        if (name == null) {
            name = p.getAnnotationValue(JAX_RS_QUERY_PARAM, "value");
        }
        if (name == null) {
            name = p.getAnnotationValue(JAX_RS_HEADER_PARAM, "value");
        }
        if (name == null) {
            name = parameter.name();
        }
        return name;
    }

    public static boolean isPrimitive(final Type type)
    {
        return PRIMITIVES.contains(typeOf(type.qualifiedTypeName()));
    }

    public static class ExcludedAnnotations implements Predicate<AnnotationDesc>
    {
        private final DocletOptions options;

        public ExcludedAnnotations(final DocletOptions options)
        {
            this.options = options;
        }

        @Override
        public boolean apply(final AnnotationDesc annotationDesc)
        {
            final String annotationClass = annotationDesc.annotationType().qualifiedTypeName();
            return options.getExcludeAnnotationClasses().contains(annotationClass);
        }
    }

    public static class JaxRsAnnotations implements Predicate<AnnotationDesc>
    {
        @Override
        public boolean apply(final AnnotationDesc annotationDesc)
        {
            final String annotationClass = annotationDesc.annotationType().qualifiedTypeName();
            return annotationClass.startsWith(JAX_RS_ANNOTATION_PACKAGE);
        }
    }

}
