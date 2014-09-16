
package com.hypnoticocelot.jaxrs.doclet.parser;

import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.collect.Collections2.filter;
import static com.hypnoticocelot.jaxrs.doclet.model.HttpMethod.DELETE;
import static com.hypnoticocelot.jaxrs.doclet.model.HttpMethod.GET;
import static com.hypnoticocelot.jaxrs.doclet.model.HttpMethod.POST;
import static com.hypnoticocelot.jaxrs.doclet.model.HttpMethod.PUT;
import static com.hypnoticocelot.jaxrs.doclet.parser.AnnotationHelper.parsePath;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hypnoticocelot.jaxrs.doclet.DocletOptions;
import com.hypnoticocelot.jaxrs.doclet.model.ApiParameter;
import com.hypnoticocelot.jaxrs.doclet.model.ApiResponseMessage;
import com.hypnoticocelot.jaxrs.doclet.model.HttpMethod;
import com.hypnoticocelot.jaxrs.doclet.model.Method;
import com.hypnoticocelot.jaxrs.doclet.model.Model;
import com.hypnoticocelot.jaxrs.doclet.translator.Translator;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;

public class ApiMethodParser
{
    private final DocletOptions options;
    private final Translator translator;
    private final String parentPath;
    private final MethodDoc methodDoc;
    private final Set<Model> models;
    private final HttpMethod httpMethod;
    private final Method parentMethod;
    private static final List<HttpMethod> allowedParameterMethodTypes = Arrays.asList(GET, POST, DELETE, PUT);

    public ApiMethodParser(final DocletOptions options, final String parentPath, final MethodDoc methodDoc)
    {
        this.options = options;
        this.translator = options.getTranslator();
        this.parentPath = parentPath;
        this.methodDoc = methodDoc;
        this.models = new LinkedHashSet<Model>();
        this.httpMethod = HttpMethod.fromMethod(methodDoc);
        this.parentMethod = null;
    }

    public ApiMethodParser(final DocletOptions options, final Method parentMethod, final MethodDoc methodDoc)
    {
        this.options = options;
        this.translator = options.getTranslator();
        this.methodDoc = methodDoc;
        this.models = new LinkedHashSet<Model>();
        this.httpMethod = HttpMethod.fromMethod(methodDoc);
        this.parentPath = parentMethod.getPath();
        this.parentMethod = parentMethod;
    }

    public Method parse()
    {
        final String methodPath = firstNonNull(parsePath(methodDoc.annotations()), "");
        if (httpMethod == null && methodPath.isEmpty()) {
            return null;
        }
        final String path = parentPath + methodPath;

        // parameters
        final List<ApiParameter> parameters = new LinkedList<ApiParameter>();
        for (final Parameter parameter : methodDoc.parameters()) {
            if (!shouldIncludeParameter(httpMethod, parameter)) {
                continue;
            }
            if (options.isParseModels()) {
                models.addAll(new ApiModelParser(options, translator, parameter.type()).parse());
            }
            parameters.add(new ApiParameter(AnnotationHelper.paramTypeOf(parameter),
                    AnnotationHelper.paramNameOf(parameter), commentForParameter(methodDoc, parameter),
                    translator.typeName(parameter.type()).value()));
        }

        // parent method parameters are inherited
        if (parentMethod != null) {
            parameters.addAll(parentMethod.getParameters());
        }

        // response messages
        final Pattern pattern = Pattern.compile("(\\d+) (.+)"); // matches "<code><space><text>"
        final List<ApiResponseMessage> responseMessages = new LinkedList<ApiResponseMessage>();
        for (final String tagName : options.getErrorTags()) {
            for (final Tag tagValue : methodDoc.tags(tagName)) {
                final Matcher matcher = pattern.matcher(tagValue.text());
                if (matcher.find()) {
                    responseMessages.add(new ApiResponseMessage(Integer.valueOf(matcher.group(1)), matcher.group(2)));
                }
            }
        }

        // return type
        final Type type = methodDoc.returnType();
        final String returnType = translator.typeName(type).value();
        if (options.isParseModels()) {
            models.addAll(new ApiModelParser(options, translator, type).parse());
        }

        // First Sentence of Javadoc method description
        final Tag[] fst = methodDoc.firstSentenceTags();
        final StringBuilder sentences = new StringBuilder();
        for (final Tag tag : fst) {
            sentences.append(tag.text());
        }
        final String firstSentences = sentences.toString();

        return new Method(httpMethod, methodDoc.name(), path, parameters, responseMessages, firstSentences,
                methodDoc.commentText().replace(firstSentences, ""), returnType);
    }

    public Set<Model> models()
    {
        return models;
    }

    private boolean shouldIncludeParameter(final HttpMethod httpMethod, final Parameter parameter)
    {
        final List<AnnotationDesc> allAnnotations = Arrays.asList(parameter.annotations());
        final Collection<AnnotationDesc> excluded = filter(allAnnotations, new AnnotationHelper.ExcludedAnnotations(
                options));
        if (!excluded.isEmpty()) {
            return false;
        }

        final Collection<AnnotationDesc> jaxRsAnnotations = filter(allAnnotations,
                new AnnotationHelper.JaxRsAnnotations());
        if (!jaxRsAnnotations.isEmpty()) {
            return true;
        }

        return (allAnnotations.isEmpty() || allowedParameterMethodTypes.contains(httpMethod));
    }

    private String commentForParameter(final MethodDoc method, final Parameter parameter)
    {
        for (final ParamTag tag : method.paramTags()) {
            if (tag.parameterName().equals(parameter.name())) {
                return tag.parameterComment();
            }
        }
        return "";
    }
}
