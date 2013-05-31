package com.hypnoticocelot.jaxrs.doclet.parser;

import com.google.common.base.Function;
import com.hypnoticocelot.jaxrs.doclet.model.Model;
import com.hypnoticocelot.jaxrs.doclet.model.Property;
import com.sun.javadoc.*;

import java.util.*;

import static com.google.common.collect.Collections2.transform;

public class ApiModelParser {

    private final Type rootType;
    private final Set<Model> models;

    public ApiModelParser(Type rootType) {
        this.rootType = rootType;
        this.models = new LinkedHashSet<Model>();
    }

    public Set<Model> parse() {
        parseModel(rootType);
        return models;
    }

    private void parseModel(Type type) {
        String typeName = AnnotationHelper.typeIdOf(type);
        boolean isPrimitive = /* type.isPrimitive()? || */ AnnotationHelper.PRIMITIVES.contains(typeName);
        boolean isJavaxType = type.qualifiedTypeName().startsWith("javax.");
        ClassDoc classDoc = type.asClassDoc();
        if (isPrimitive || isJavaxType || classDoc == null || alreadyStoredType(typeName)) {
            return;
        }

        Map<String, Type> types = findReferencedTypes(classDoc);
        Map<String, Property> elements = findReferencedElements(types);
        models.add(new Model(typeName, elements));
        parseNestedModels(types.values());
    }

    private Map<String, Type> findReferencedTypes(ClassDoc classDoc) {
        Map<String, Type> elements = new HashMap<String, Type>();

        //Get fields
        FieldDoc[] fieldDocs = classDoc.fields();
        if (fieldDocs != null && fieldDocs.length > 0) {
            for (FieldDoc field : fieldDocs) {
                if (elements.get(field.name()) == null) {
                    elements.put(field.name(), field.type());
                }
            }
        }

        //Get methods
        MethodDoc[] methodDocs = classDoc.methods();
        if (methodDocs != null && methodDocs.length > 0) {
            for (MethodDoc method : methodDocs) {
                if (method.name().startsWith("get") && method.name().length() > 3) {
                    String name = method.name().substring(3);
                    name = name.substring(0, 1).toLowerCase() + (name.length() > 1 ? name.substring(1) : "");
                    if (elements.get(name) == null) {
                        elements.put(name, method.returnType());
                    }
                }
            }
        }
        return elements;
    }

    private Map<String, Property> findReferencedElements(Map<String, Type> types) {
        Map<String, Property> elements = new HashMap<String, Property>();
        for (Map.Entry<String, Type> entry : types.entrySet()) {
            Type containerOf = parseParameterisedTypeOf(entry.getValue());
            String containerTypeOf = containerOf == null ? null : AnnotationHelper.typeIdOf(containerOf);
            String eleTypeName = AnnotationHelper.typeIdOf(entry.getValue());
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

    private boolean alreadyStoredType(String typeName) {
        return transform(models, new Function<Model, String>() {
            @Override
            public String apply(Model model) {
                return model.getId();
            }
        }).contains(typeName);
    }

}
