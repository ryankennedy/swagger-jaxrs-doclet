package com.hypnoticocelot.jaxrs.doclet.parser;

import com.google.common.base.Predicate;

import com.hypnoticocelot.jaxrs.doclet.DocletOptions;
import com.hypnoticocelot.jaxrs.doclet.model.Model;
import com.hypnoticocelot.jaxrs.doclet.model.Property;
import com.hypnoticocelot.jaxrs.doclet.translator.Translator;
import com.sun.javadoc.*;

import java.util.*;

import static com.google.common.collect.Collections2.filter;

public class ApiModelParser {

    private final DocletOptions options;
    private final Translator translator;
    private final Type rootType;
    private final Set<Model> models;

    public ApiModelParser(DocletOptions options, Translator translator, Type rootType) {
        this.options = options;
        this.translator = translator;
        this.rootType = rootType;
        this.models = new LinkedHashSet<Model>();
    }

    public Set<Model> parse() {
        parseModel(rootType);
        return models;
    }

    private void parseModel(Type type) {
    	if("com.sun.jersey.api.JResponse".equals(type.qualifiedTypeName())) {
    		type = type.asParameterizedType().typeArguments()[0];
    	}

        boolean isPrimitive = /* type.isPrimitive()? || */ AnnotationHelper.isPrimitive(type);
        boolean isJavaxType = type.qualifiedTypeName().startsWith("javax.");
        boolean isBaseObject = type.qualifiedTypeName().equals("java.lang.Object");
        boolean isTypeToTreatAsOpaque = options.getTypesToTreatAsOpaque().contains(type.qualifiedTypeName());
        ClassDoc classDoc = type.asClassDoc();
        if (isPrimitive || isJavaxType || isBaseObject || isTypeToTreatAsOpaque || classDoc == null || alreadyStoredType(type)) {
            return;
        }
        
        Map<String, Type> types = findReferencedTypes(classDoc);
        Map<String, Property> elements = findReferencedElements(types);
        if (!elements.isEmpty()) {
            models.add(new Model(translator.typeName(type).value(), elements));
            parseNestedModels(types.values());
        }
    }

    private Map<String, Type> findReferencedTypes(ClassDoc classDoc) {
    	AnnotationParser p = new AnnotationParser(classDoc);
    	String xmlAccessorType = p.getAnnotationValue("javax.xml.bind.annotation.XmlAccessorType", "value");
        Map<String, Type> elements = new HashMap<String, Type>();

        if (!"javax.xml.bind.annotation.XmlAccessType.PROPERTY".equals(xmlAccessorType)) {
	        FieldDoc[] fieldDocs = classDoc.fields();
	        if (fieldDocs != null) {
	            for (FieldDoc field : fieldDocs) {
	            	if (field.isStatic() || field.name().charAt(0) == '_') {
	            		continue;
	            	}
	                String name = translator.fieldName(field).value();
	                if (name != null && !elements.containsKey(name)) {
	                    elements.put(name, field.type());
	                }
	            }
	        }
        }

        if(!"javax.xml.bind.annotation.XmlAccessType.FIELD".equals(xmlAccessorType)) {
	        MethodDoc[] methodDocs = classDoc.methods();
	        if (methodDocs != null) {
	            for (MethodDoc method : methodDocs) {
	            	if (method.isStatic() || method.name().charAt(0) == '_') {
	            		continue;
	            	}
	                String name = translator.methodName(method).value();
	                if (name != null && !elements.containsKey(name)) {
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
            String typeName = entry.getKey();
            Type type = entry.getValue();
            ClassDoc typeClassDoc = type.asClassDoc();

            Type containerOf = parseParameterisedTypeOf(type);
            String containerTypeOf = containerOf == null ? null : translator.typeName(containerOf).value();

            String propertyName = translator.typeName(type).value();
            Property property;
            if (typeClassDoc != null && typeClassDoc.isEnum()) {
                property = new Property(typeClassDoc.enumConstants(), null);
            } else {
                property = new Property(propertyName, null, containerTypeOf);
            }
            elements.put(typeName, property);
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
                result = typeArgs[typeArgs.length > 1 ? 1 : 0];
            }
        }
        return result;
    }

    private boolean alreadyStoredType(final Type type) {
        return filter(models, new Predicate<Model>() {
            @Override
            public boolean apply(Model model) {
                return model.getId().equals(translator.typeName(type).value());
            }
        }).size() > 0;
    }

}
