package com.hypnoticocelot.jaxrs.doclet.translator;

import com.hypnoticocelot.jaxrs.doclet.parser.AnnotationHelper;
import com.hypnoticocelot.jaxrs.doclet.parser.AnnotationParser;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Type;

import java.util.HashMap;
import java.util.Map;

public class JaxbAwareTranslator implements Translator {

    private static final String JAXB_XML_ROOT_ELEMENT = "javax.xml.bind.annotation.XmlRootElement";
    private static final String JAXB_XML_ELEMENT = "javax.xml.bind.annotation.XmlElement";
    private static final String JAXB_XML_TRANSIENT = "javax.xml.bind.annotation.XmlTransient";

    private final Map<String, Type> reverseIndex;
    private final Map<Type, String> namedTypes;

    public JaxbAwareTranslator() {
        reverseIndex = new HashMap<String, Type>();
        namedTypes = new HashMap<Type, String>();
    }

    @Override
    public String nameFor(Type type) {
        if (namedTypes.containsKey(type)) {
            return namedTypes.get(type);
        }
        if (AnnotationHelper.isPrimitive(type) || type.asClassDoc() == null) {
            return null;
        }

        String name = jaxbNameFor(JAXB_XML_ROOT_ELEMENT, type.asClassDoc());
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
        return jaxbNameFor(JAXB_XML_ELEMENT, field);
    }

    @Override
    public String nameFor(MethodDoc method) {
        return jaxbNameFor(JAXB_XML_ELEMENT, method);
    }

    private String jaxbNameFor(String annotation, ProgramElementDoc doc) {
        AnnotationParser element = new AnnotationParser(doc);
        if (element.isAnnotatedBy(JAXB_XML_TRANSIENT)) {
            return null;
        }
        return element.getAnnotationValue(annotation, "name");
    }

}
