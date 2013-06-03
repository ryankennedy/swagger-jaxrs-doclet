package com.hypnoticocelot.jaxrs.doclet.parser;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ProgramElementDoc;

public class AnnotationParser {

    private final ProgramElementDoc element;

    public AnnotationParser(ProgramElementDoc element) {
        this.element = element;
    }

    public String getAnnotationValue(String qualifiedAnnotationType, String key) {
        AnnotationDesc annotation = AnnotationHelper.getAnnotation(element, qualifiedAnnotationType);
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
        return AnnotationHelper.getAnnotation(element, qualifiedAnnotationType) != null;
    }

}
