package com.hypnoticocelot.jaxrs.doclet.translator;

import com.google.common.base.Function;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Type;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FirstNotNullTranslator implements Translator {

    private final List<Translator> chain;

    public FirstNotNullTranslator() {
        chain = new ArrayList<Translator>();
    }

    public FirstNotNullTranslator addNext(Translator link) {
        chain.add(link);
        return this;
    }

    @Override
    public OptionalName typeName(final Type type) {
        return firstNotNullOf(new Function<Translator, OptionalName>() {
            @Override
            public OptionalName apply(Translator translator) {
                return translator.typeName(type);
            }
        });
    }

    @Override
    public OptionalName fieldName(final FieldDoc field) {
        return firstNotNullOf(new Function<Translator, OptionalName>() {
            @Override
            public OptionalName apply(Translator translator) {
                return translator.fieldName(field);
            }
        });
    }

    @Override
    public OptionalName methodName(final MethodDoc method) {
        return firstNotNullOf(new Function<Translator, OptionalName>() {
            @Override
            public OptionalName apply(Translator translator) {
                return translator.methodName(method);
            }
        });
    }

    private OptionalName firstNotNullOf(Function<Translator, OptionalName> function) {
        OptionalName name = null;
        Iterator<Translator> iterator = chain.iterator();
        while ((name == null || name.isMissing()) && iterator.hasNext()) {
            name = function.apply(iterator.next());
        }
        return name;
    }

}
