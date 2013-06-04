package com.hypnoticocelot.jaxrs.doclet.translator;

import com.hypnoticocelot.jaxrs.doclet.parser.AnnotationHelper;
import com.hypnoticocelot.jaxrs.doclet.parser.AnnotationParser;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Type;

import java.util.HashMap;
import java.util.Map;

public class JacksonAwareTranslator implements Translator {

    private static final String JSON_ROOT = "com.fasterxml.jackson.annotation.JsonRootName";
    private static final String JSON_ELEMENT = "com.fasterxml.jackson.annotation.JsonProperty";
    private static final String JSON_IGNORE = "com.fasterxml.jackson.annotation.JsonIgnore";

    private final Map<String, Type> reverseIndex;
    private final Map<Type, String> namedTypes;

    public JacksonAwareTranslator() {
        reverseIndex = new HashMap<String, Type>();
        namedTypes = new HashMap<Type, String>();
    }

    @Override
    public String nameFor(Type type) {
        if (AnnotationHelper.isPrimitive(type)) {
            return AnnotationHelper.typeOf(type.qualifiedTypeName());
        }
        if (namedTypes.containsKey(type)) {
            return namedTypes.get(type);
        }

        String name = jacksonNameFor(JSON_ROOT, type.asClassDoc());
        if (name != null) {
            StringBuilder nameBuilder = new StringBuilder(name);
            while (reverseIndex.containsKey(nameBuilder.toString())) {
                nameBuilder.append('_');
            }
            name = nameBuilder.toString();
            namedTypes.put(type, name);
            reverseIndex.put(name, type);
        }
        return name;
    }

    @Override
    public String nameFor(FieldDoc field) {
        return jacksonNameFor(JSON_ELEMENT, field);
    }

    @Override
    public String nameFor(MethodDoc method) {
        return jacksonNameFor(JSON_ELEMENT, method);
    }

    private String jacksonNameFor(String annotation, ProgramElementDoc doc) {
        AnnotationParser element = new AnnotationParser(doc);
        if (element.isAnnotatedBy(JSON_IGNORE)) {
            return null;
        }
        return element.getAnnotationValue(annotation, "name");
    }

}
