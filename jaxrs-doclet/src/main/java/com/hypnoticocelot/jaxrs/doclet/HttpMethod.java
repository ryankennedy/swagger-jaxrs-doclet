package com.hypnoticocelot.jaxrs.doclet;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.MethodDoc;

import java.util.ArrayList;
import java.util.List;

public enum HttpMethod {
    GET("javax.ws.rs.GET"),
    PUT("javax.ws.rs.PUT"),
    POST("javax.ws.rs.POST"),
    DELETE("javax.ws.rs.DELETE");

    private final String canonicalClassname;

    private HttpMethod(String canonicalClassname) {
        this.canonicalClassname = canonicalClassname;
    }

    public static HttpMethod fromMethod(MethodDoc method) {
        List<String> typeNames = new ArrayList<String>();
        for (AnnotationDesc annotation : method.annotations()) {
            typeNames.add(annotation.annotationType().qualifiedTypeName());
        }
        HttpMethod found = null;
        for (HttpMethod value : HttpMethod.values()) {
            if (typeNames.contains(value.canonicalClassname)) {
                found = value;
                break;
            }
        }
        return found;
    }
}
