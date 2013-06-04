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

public class JaxbAwareTranslator implements Translator {

    private static final String JAXB_XML_ROOT_ELEMENT = "javax.xml.bind.annotation.XmlRootElement";
    private static final String JAXB_XML_ELEMENT = "javax.xml.bind.annotation.XmlElement";
    private static final String JAXB_XML_TRANSIENT = "javax.xml.bind.annotation.XmlTransient";

    private final Map<OptionalName, Type> reverseIndex;
    private final Map<Type, OptionalName> namedTypes;

    public JaxbAwareTranslator() {
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

        OptionalName name = nameFor(JAXB_XML_ROOT_ELEMENT, type.asClassDoc());
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
        return nameFor(JAXB_XML_ELEMENT, field);
    }

    @Override
    public OptionalName methodName(MethodDoc method) {
        return nameFor(JAXB_XML_ELEMENT, method);
    }

    private OptionalName nameFor(String annotation, ProgramElementDoc doc) {
        AnnotationParser element = new AnnotationParser(doc);
        if (element.isAnnotatedBy(JAXB_XML_TRANSIENT)) {
            return ignored();
        }
        return presentOrMissing(element.getAnnotationValue(annotation, "name"));
    }

}
