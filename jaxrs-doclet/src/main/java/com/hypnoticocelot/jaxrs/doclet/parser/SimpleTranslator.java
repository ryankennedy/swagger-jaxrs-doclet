package com.hypnoticocelot.jaxrs.doclet.parser;

import com.sun.javadoc.*;

import java.util.HashMap;
import java.util.Map;

public class SimpleTranslator implements Translator {

    private static final String XML_ROOT_ELEMENT = "javax.xml.bind.annotation.XmlRootElement";
    private Map<String, Type> reverseIndex;
    private Map<Type, String> namedTypes;

    public SimpleTranslator() {
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

        String name = null;
        ClassDoc cd = type.asClassDoc();
        if (cd != null) {
            name = getRootElementNameOf(cd);
            if (name != null) {
                while (reverseIndex.containsKey(name)) {
                    name += '_';
                }
            }
        }
        if (name == null) {
            name = AnnotationHelper.typeOf(type.qualifiedTypeName());
        }
        namedTypes.put(type, name);
        reverseIndex.put(name, type);
        return name;
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

    private static String getRootElementNameOf(ClassDoc classDoc) {
        AnnotationDesc[] annotations = classDoc.annotations();
        for (AnnotationDesc annotation : annotations) {
            String annotationTypeName = annotation.annotationType().qualifiedTypeName();
            if (annotationTypeName.equals(XML_ROOT_ELEMENT)) {
                AnnotationDesc.ElementValuePair[] evpArr = annotation.elementValues();
                if (evpArr.length > 0) {
                    for (AnnotationDesc.ElementValuePair evp : evpArr) {
                        if (evp.element().name().equals("name")) {
                            return evp.value().value().toString();
                        }
                    }
                }
            }
        }
        return null;
    }

}
