package com.hypnoticocelot.jaxrs.doclet.parser;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ProgramElementDoc;

public class AnnotationParser {

    private final AnnotationDesc[] annotations;

    public AnnotationParser(ProgramElementDoc element) {
        annotations = element.annotations();
    }

    public AnnotationParser(Parameter parameter) {
        annotations = parameter.annotations();
    }

    public String getAnnotationValue(String qualifiedAnnotationType, String key) {
        AnnotationDesc annotation = getAnnotation(qualifiedAnnotationType);
        if (annotation == null) {
            return null;
        }
        for (AnnotationDesc.ElementValuePair evp : annotation.elementValues()) {
            if (evp.element().name().equals(key)) {
                return evp.value().value().toString();
            }
        }
        return null;
    }

    public boolean isAnnotatedBy(String qualifiedAnnotationType) {
        return getAnnotation(qualifiedAnnotationType) != null;
    }

    private AnnotationDesc getAnnotation(String qualifiedAnnotationType) {
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

}
