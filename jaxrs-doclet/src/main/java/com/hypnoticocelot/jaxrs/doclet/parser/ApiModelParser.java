package com.hypnoticocelot.jaxrs.doclet.parser;

import com.google.common.base.Predicate;
import com.hypnoticocelot.jaxrs.doclet.model.Model;
import com.hypnoticocelot.jaxrs.doclet.model.Property;
import com.sun.javadoc.*;

import java.util.*;

import static com.google.common.collect.Collections2.filter;

public class ApiModelParser {

    private final Translator translator;
    private final Type rootType;
    private final Set<Model> models;

    public ApiModelParser(Translator translator, Type rootType) {
        this.translator = translator;
        this.rootType = rootType;
        this.models = new LinkedHashSet<Model>();
    }

    public Set<Model> parse() {
        parseModel(rootType);
        return models;
    }

    private void parseModel(Type type) {
        boolean isPrimitive = /* type.isPrimitive()? || */ AnnotationHelper.isPrimitive(type);
        boolean isJavaxType = type.qualifiedTypeName().startsWith("javax.");
        ClassDoc classDoc = type.asClassDoc();
        if (isPrimitive || isJavaxType || classDoc == null || alreadyStoredType(type)) {
            return;
        }

        Map<String, Type> types = findReferencedTypes(classDoc);
        Map<String, Property> elements = findReferencedElements(types);
        models.add(new Model(translator.nameFor(type), elements));
        parseNestedModels(types.values());
    }

    private Map<String, Type> findReferencedTypes(ClassDoc classDoc) {
        Map<String, Type> elements = new HashMap<String, Type>();

        FieldDoc[] fieldDocs = classDoc.fields();
        if (fieldDocs != null) {
            for (FieldDoc field : fieldDocs) {
                String name = translator.nameFor(field);
                if (name != null && !elements.containsKey(name)) {
                    elements.put(name, field.type());
                }
            }
        }

        MethodDoc[] methodDocs = classDoc.methods();
        if (methodDocs != null) {
            for (MethodDoc method : methodDocs) {
                String name = translator.nameFor(method);
                if (name != null && !elements.containsKey(name)) {
                    elements.put(name, method.returnType());
                }
            }
        }
        return elements;
    }

    private Map<String, Property> findReferencedElements(Map<String, Type> types) {
        Map<String, Property> elements = new HashMap<String, Property>();
        for (Map.Entry<String, Type> entry : types.entrySet()) {
            Type containerOf = parseParameterisedTypeOf(entry.getValue());
            String containerTypeOf = containerOf == null ? null : translator.nameFor(containerOf);
            String eleTypeName = translator.nameFor(entry.getValue());
            elements.put(entry.getKey(), new Property(eleTypeName, null, containerTypeOf));
        }
        return elements;
    }

    private void parseNestedModels(Collection<Type> types) {
        for (Type type : types) {
            parseModel(type);
            Type pt = parseParameterisedTypeOf(type);
            if (pt != null) {
                parseModel(pt);
            }
        }
    }

    private Type parseParameterisedTypeOf(Type type) {
        Type result = null;
        ParameterizedType pt = type.asParameterizedType();
        if (pt != null) {
            Type[] typeArgs = pt.typeArguments();
            if (typeArgs != null && typeArgs.length > 0) {
                result = typeArgs[0];
            }
        }
        return result;
    }

    private boolean alreadyStoredType(final Type type) {
        return filter(models, new Predicate<Model>() {
            @Override
            public boolean apply(Model model) {
                return model.getId().equals(translator.nameFor(type));
            }
        }).size() > 0;
    }

}
