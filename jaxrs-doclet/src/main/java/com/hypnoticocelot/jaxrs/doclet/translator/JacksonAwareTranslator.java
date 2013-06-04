package com.hypnoticocelot.jaxrs.doclet.translator;

import com.hypnoticocelot.jaxrs.doclet.parser.AnnotationHelper;
import com.hypnoticocelot.jaxrs.doclet.parser.AnnotationParser;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Type;

import java.util.HashMap;
import java.util.Map;

import static com.hypnoticocelot.jaxrs.doclet.translator.Translator.OptionalName.ignored;
import static com.hypnoticocelot.jaxrs.doclet.translator.Translator.OptionalName.presentOrMissing;

public class JacksonAwareTranslator implements Translator {

    private static final String JSON_ROOT = "com.fasterxml.jackson.annotation.JsonRootName";
    private static final String JSON_ELEMENT = "com.fasterxml.jackson.annotation.JsonProperty";
    private static final String JSON_IGNORE = "com.fasterxml.jackson.annotation.JsonIgnore";

    private final Map<OptionalName, Type> reverseIndex;
    private final Map<Type, OptionalName> namedTypes;

    public JacksonAwareTranslator() {
        reverseIndex = new HashMap<OptionalName, Type>();
        namedTypes = new HashMap<Type, OptionalName>();
    }

    @Override
    public OptionalName typeName(Type type) {
        if (namedTypes.containsKey(type)) {
            return namedTypes.get(type);
        }
        if (AnnotationHelper.isPrimitive(type) || type.asClassDoc() == null) {
            return null;
        }

        OptionalName name = nameFor(JSON_ROOT, type.asClassDoc());
        if (name.isPresent()) {
            StringBuilder nameBuilder = new StringBuilder(name.value());
            while (reverseIndex.containsKey(name)) {
                nameBuilder.append('_');
                name = presentOrMissing(nameBuilder.toString());
            }
            namedTypes.put(type, name);
            reverseIndex.put(name, type);
        }
        return name;
    }

    @Override
    public OptionalName fieldName(FieldDoc field) {
        return nameFor(JSON_ELEMENT, field);
    }

    @Override
    public OptionalName methodName(MethodDoc method) {
        return nameFor(JSON_ELEMENT, method);
    }

    private OptionalName nameFor(String annotation, ProgramElementDoc doc) {
        AnnotationParser element = new AnnotationParser(doc);
        if (element.isAnnotatedBy(JSON_IGNORE)) {
            return ignored();
        }
        return presentOrMissing(element.getAnnotationValue(annotation, "value"));
    }

}
