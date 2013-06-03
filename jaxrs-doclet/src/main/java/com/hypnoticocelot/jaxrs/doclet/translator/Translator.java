package com.hypnoticocelot.jaxrs.doclet.translator;

import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Type;

public interface Translator {
    String nameFor(Type type);

    String nameFor(FieldDoc field);

    String nameFor(MethodDoc method);
}
