package com.hypnoticocelot.jaxrs.doclet.translator;

import com.hypnoticocelot.jaxrs.doclet.parser.AnnotationHelper;
import com.hypnoticocelot.jaxrs.doclet.parser.AnnotationParser;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Type;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Objects.firstNonNull;

public class JaxbAwareTranslator implements Translator {

    private static final String JAXB_XML_ROOT_ELEMENT = "javax.xml.bind.annotation.XmlRootElement";
    private static final String JAXB_XML_ELEMENT = "javax.xml.bind.annotation.XmlElement";
    private static final String JAXB_XML_TRANSIENT = "javax.xml.bind.annotation.XmlTransient";

    private final Translator simpleTranslator;
    private final Map<String, Type> reverseIndex;
    private final Map<Type, String> namedTypes;

    public JaxbAwareTranslator() {
        simpleTranslator = new SimpleTranslator();
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

        String name = jaxbNameFor(JAXB_XML_ROOT_ELEMENT, type.asClassDoc());
        if (name != null) {
            while (reverseIndex.containsKey(name)) {
                name += '_';
            }
        }
        name = firstNonNull(name, simpleTranslator.nameFor(type));
        namedTypes.put(type, name);
        reverseIndex.put(name, type);
        return name;
    }

    @Override
    public String nameFor(FieldDoc field) {
        String name = jaxbNameFor(JAXB_XML_ELEMENT, field);
        if (name == null) {
            name = simpleTranslator.nameFor(field);
        }
        return name;
    }

    @Override
    public String nameFor(MethodDoc method) {
        String name = jaxbNameFor(JAXB_XML_ELEMENT, method);
        if (name == null) {
            name = simpleTranslator.nameFor(method);
        }
        return name;
    }

    private String jaxbNameFor(String annotation, ProgramElementDoc doc) {
        AnnotationParser element = new AnnotationParser(doc);
        if (element.isAnnotatedBy(JAXB_XML_TRANSIENT)) {
            return null;
        }
        return element.getAnnotationValue(annotation, "name");
    }

}
