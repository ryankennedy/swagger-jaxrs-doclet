package com.hypnoticocelot.jaxrs.doclet.parser;

import com.google.common.base.Function;
import com.hypnoticocelot.jaxrs.doclet.model.Model;
import com.hypnoticocelot.jaxrs.doclet.model.Property;
import com.sun.javadoc.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import static com.google.common.collect.Collections2.transform;

public class ApiModelParser {

    private final Type rootType;
    private final Collection<Model> models;

    public ApiModelParser(Type rootType) {
        this.rootType = rootType;
        this.models = new LinkedHashSet<Model>();
    }

    public Collection<Model> parse() {
        findModels(rootType);
        return models;
    }

    private void findModels(Type type) {
        String typeName = AnnotationHelper.typeOf(type);
        if (AnnotationHelper.PRIMITIVES.contains(typeName) || type.qualifiedTypeName().startsWith("javax.")) {
            return;
        }

        ClassDoc classDoc = type.asClassDoc();
        if (classDoc == null) {
            return;
        }

        if (alreadyStoredType(typeName)) {
            return;
        }

        Map<String, Type> fields = new HashMap<String, Type>();

        //Get fields
        FieldDoc[] fieldDocs = classDoc.fields();
        if (fieldDocs != null && fieldDocs.length > 0) {
            for (FieldDoc field : fieldDocs) {
                if (fields.get(field.name()) == null) {
                    fields.put(field.name(), field.type());
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
                    if (fields.get(name) == null) {
                        fields.put(name, method.returnType());
                    }
                }
            }
        }

        //Process all fields & methods
        if (fields.keySet().size() > 0) {
            //Add to the model
            Map<String, Property> fieldMap = new HashMap<String, Property>();
            for (Map.Entry<String, Type> entry : fields.entrySet()) {
                //Check if it is a collection and get collection type
                String containerOf = null;
                ParameterizedType pt = entry.getValue().asParameterizedType();
                if (pt != null) {
                    Type[] typeArgs = pt.typeArguments();
                    if (typeArgs != null && typeArgs.length > 0) {
                        containerOf = AnnotationHelper.typeOf(typeArgs[0]);
                    }
                }

                //Add to map
                String eleTypeName = AnnotationHelper.typeOf(entry.getValue());
                fieldMap.put(entry.getKey(), new Property(eleTypeName, null, containerOf));
            }

            models.add(new Model(typeName, fieldMap));

            //Build contained models
            for (Map.Entry<String, Type> entry : fields.entrySet()) {
                //Check if it is a collection and get collection type
                ParameterizedType pt = entry.getValue().asParameterizedType();
                if (pt != null) {
                    Type[] typeArgs = pt.typeArguments();
                    if (typeArgs != null && typeArgs.length > 0) {
                        findModels(typeArgs[0]);
                    }
                }
                findModels(entry.getValue());
            }
        }
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
