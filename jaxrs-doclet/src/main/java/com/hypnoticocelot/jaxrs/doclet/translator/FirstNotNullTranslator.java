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
    public String nameFor(final Type type) {
        return firstNotNullOf(new Function<Translator, String>() {
            @Override
            public String apply(Translator translator) {
                return translator.nameFor(type);
            }
        });
    }

    @Override
    public String nameFor(final FieldDoc field) {
        return firstNotNullOf(new Function<Translator, String>() {
            @Override
            public String apply(Translator translator) {
                return translator.nameFor(field);
            }
        });
    }

    @Override
    public String nameFor(final MethodDoc method) {
        return firstNotNullOf(new Function<Translator, String>() {
            @Override
            public String apply(Translator translator) {
                return translator.nameFor(method);
            }
        });
    }

    private String firstNotNullOf(Function<Translator, String> function) {
        String name = null;
        Iterator<Translator> iterator = chain.iterator();
        while (name == null && iterator.hasNext()) {
            name = function.apply(iterator.next());
        }
        return name;
    }

}
