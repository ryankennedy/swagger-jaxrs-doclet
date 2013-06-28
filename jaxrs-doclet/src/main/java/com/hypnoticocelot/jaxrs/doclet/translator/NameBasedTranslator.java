package com.hypnoticocelot.jaxrs.doclet.translator;

import com.hypnoticocelot.jaxrs.doclet.parser.AnnotationHelper;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Type;

import static com.hypnoticocelot.jaxrs.doclet.translator.Translator.OptionalName.presentOrMissing;

public class NameBasedTranslator implements Translator {

    @Override
    public OptionalName typeName(Type type) {
        return presentOrMissing(AnnotationHelper.typeOf(type.qualifiedTypeName()));
    }

    @Override
    public OptionalName fieldName(FieldDoc field) {
        return presentOrMissing(field.name());
    }

    @Override
    public OptionalName methodName(MethodDoc method) {
        String name = null;
        if (method.name().startsWith("get") && method.name().length() > 3) {
            name = method.name().substring(3);
            name = name.substring(0, 1).toLowerCase() + (name.length() > 1 ? name.substring(1) : "");
        }
        return presentOrMissing(name);
    }

}
