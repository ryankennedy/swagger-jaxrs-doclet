package com.hypnoticocelot.jaxrs.doclet.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

public class ApiResponseMessage {

    private int code;
    
    @JsonProperty("reason") // swagger 1.1 name
    private String message; // swagger 1.2 name
    
    @SuppressWarnings("unused")
    private ApiResponseMessage() {
    }

    public ApiResponseMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiResponseMessage that = (ApiResponseMessage) o;
        return Objects.equal(code, that.code)
                && Objects.equal(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(code, message);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("code", code)
                .add("message", message)
                .toString();
    }}
