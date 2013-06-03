package com.hypnoticocelot.jaxrs.doclet.translator;

import com.hypnoticocelot.jaxrs.doclet.parser.AnnotationHelper;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Type;

public class SimpleTranslator implements Translator {

    @Override
    public String nameFor(Type type) {
        return AnnotationHelper.typeOf(type.qualifiedTypeName());
    }

    @Override
    public String nameFor(FieldDoc field) {
        return field.name();
    }

    @Override
    public String nameFor(MethodDoc method) {
        String name = null;
        if (method.name().startsWith("get") && method.name().length() > 3) {
            name = method.name().substring(3);
            name = name.substring(0, 1).toLowerCase() + (name.length() > 1 ? name.substring(1) : "");
        }
        return name;
    }

}
