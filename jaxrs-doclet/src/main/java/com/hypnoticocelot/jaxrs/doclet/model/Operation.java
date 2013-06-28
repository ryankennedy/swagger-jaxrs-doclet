package com.hypnoticocelot.jaxrs.doclet.model;

import com.google.common.base.Objects;
import com.hypnoticocelot.jaxrs.doclet.parser.AnnotationHelper;

import java.util.List;

import static com.google.common.base.Strings.emptyToNull;

public class Operation {

    private HttpMethod httpMethod;
    private String nickname;
    private String responseClass; // void, primitive, complex or a container
    private List<ApiParameter> parameters;
    private String summary; // cap at 60 characters for readability in the UI
    private String notes;

    @SuppressWarnings("unused")
    private Operation() {
    }

    public Operation(Method method) {
        this.httpMethod = method.getMethod();
        this.nickname = emptyToNull(method.getMethodName());
        this.responseClass = emptyToNull(AnnotationHelper.typeOf(method.getReturnType()));
        this.parameters = method.getParameters().isEmpty() ? null : method.getParameters();
        this.summary = emptyToNull(method.getFirstSentence());
        this.notes = emptyToNull(method.getComment());
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getNickname() {
        return nickname;
    }

    public String getResponseClass() {
        return responseClass;
    }

    public List<ApiParameter> getParameters() {
        return parameters;
    }

    public String getSummary() {
        return summary;
    }

    public String getNotes() {
        return notes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Operation that = (Operation) o;
        return Objects.equal(httpMethod, that.httpMethod)
                && Objects.equal(nickname, that.nickname)
                && Objects.equal(responseClass, that.responseClass)
                && Objects.equal(parameters, that.parameters)
                && Objects.equal(summary, that.summary)
                && Objects.equal(notes, that.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(httpMethod, nickname, responseClass, parameters, summary, notes);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("httpMethod", httpMethod)
                .add("nickname", nickname)
                .add("responseClass", responseClass)
                .add("parameters", parameters)
                .add("summary", summary)
                .add("notes", notes)
                .toString();
    }
}
