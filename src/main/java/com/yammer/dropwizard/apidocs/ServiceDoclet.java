package com.yammer.dropwizard.apidocs;

import com.sun.javadoc.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ServiceDoclet {
    public static final String JAX_RS_PATH = "javax.ws.rs.Path";
    public static final String JAX_RS_PATH_PARAM = "javax.ws.rs.PathParam";
    public static final String JAX_RS_QUERY_PARAM = "javax.ws.rs.QueryParam";

    public static final List<String> METHODS = new ArrayList<String>() {{
        add("javax.ws.rs.GET");
        add("javax.ws.rs.PUT");
        add("javax.ws.rs.POST");
        add("javax.ws.rs.DELETE");
    }};

    /**
     * Generate documentation here.
     * This method is required for all doclets.
     *
     * @return true on success.
     */
    public static boolean start(RootDoc doc) {
        JavaDocParameters parameters = JavaDocParameters.parse(doc.options());

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);

        try {
            List<ResourceListingAPI> builder = new LinkedList<ResourceListingAPI>();

            String basePath = "http://localhost:8080";
            String apiVersion = "API_VERSION_HERE";

            for (ClassDoc classDoc : doc.classes()) {
                List<Api> apiBuilder = new LinkedList<Api>();
                String apiPath = path(classDoc);
                if (apiPath != null) {
                    List<Operation> methodBuilder = new LinkedList<Operation>();

                    for (MethodDoc method : classDoc.methods()) {
                        Method me = parseMethod(method);
                        if (me != null) {
                            methodBuilder.add(new Operation(me.getMethod(), me.getMethodName(), "Greeting",
                                                            me.getParameters(), me.getComment()));
                        }
                    }

                    apiBuilder.add(new Api(apiPath, classDoc.getRawCommentText(), methodBuilder));
                    builder.add(new ResourceListingAPI("/apidocs/" + classDoc.name() + ".{format}",
                                                       classDoc.getRawCommentText()));

                    File classFile = new File(parameters.getOutput(), classDoc.name() + ".json");
                    ApiDeclaration declaration = new ApiDeclaration(apiVersion, basePath, apiBuilder);
                    mapper.writeValue(classFile, declaration);
                }
            }

            ResourceListing listing = new ResourceListing(apiVersion, basePath, builder);
            File docFile = new File(parameters.getOutput(), "service.json");
            mapper.writeValue(docFile, listing);

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static String path(ClassDoc doc) {
        for (AnnotationDesc annotationDesc : doc.annotations()) {
            if (annotationDesc.annotationType().qualifiedTypeName().equals(JAX_RS_PATH)) {
                for (AnnotationDesc.ElementValuePair pair : annotationDesc.elementValues()) {
                    if (pair.element().name().equals("value")) {
                        return pair.value().value().toString();
                    }
                }
            }
        }

        return null;
    }

    private static Method parseMethod(MethodDoc method) {
        for (AnnotationDesc desc : method.annotations()) {
            if (METHODS.contains(desc.annotationType().qualifiedTypeName())) {
                List<ApiParameter> parameterBuilder = new LinkedList<ApiParameter>();

                for (Parameter parameter : method.parameters()) {
                    String parameterComment = commentForParameter(method, parameter);
                    parameterBuilder.add(new ApiParameter(paramTypeOf(parameter), parameter.name(), parameterComment,
                                                          typeOf(parameter.typeName())));
                }

                return new Method(desc.annotationType().name(),
                                  method.name(),
                                  parameterBuilder,
                                  method.commentText(),
                                  method.returnType().qualifiedTypeName());
            }
        }

        return null;
    }

    private static String paramTypeOf(Parameter parameter) {
        AnnotationDesc[] annotations = parameter.annotations();
        for (AnnotationDesc annotation : annotations) {
            String annotationTypeName = annotation.annotationType().qualifiedTypeName();
            if (annotationTypeName.equals(JAX_RS_PATH_PARAM)) {
                return "path";
            } else if (annotationTypeName.equals(JAX_RS_QUERY_PARAM)) {
                return "query";
            }
        }
        return "body";
    }

    private static String typeOf(String javaType) {
        if (javaType.equals("String") || javaType.equals("java.lang.String")) {
            return "string";
        } else {
            // todo: have to make sure we add this type to the models section
            return javaType;
        }
    }

    private static String commentForParameter(MethodDoc method, Parameter parameter) {
        for (ParamTag tag : method.paramTags()) {
            if (tag.parameterName().equals(parameter.name())) {
                return tag.parameterComment();
            }
        }

        return null;
    }

    /**
     * Check for doclet-added options.  Returns the number of
     * arguments you must specify on the command line for the
     * given option.  For example, "-d docs" would return 2.
     * <p/>
     * This method is required if the doclet contains any options.
     * If this method is missing, Javadoc will print an invalid flag
     * error for every option.
     *
     * @return number of arguments on the command line for an option
     *         including the option name itself.  Zero return means
     *         option not known.  Negative value means error occurred.
     */
    public static int optionLength(String option) {
        Map<String, Integer> options = new HashMap<String, Integer>();
        options.put("-d", 2);

        Integer value = options.get(option);
        if (value != null) {
            return value;
        } else {
            return 0;
        }
    }

    /**
     * Check that options have the correct arguments.
     * <p/>
     * This method is not required, but is recommended,
     * as every option will be considered valid if this method
     * is not present.  It will default gracefully (to true)
     * if absent.
     * <p/>
     * Printing option related error messages (using the provided
     * DocErrorReporter) is the responsibility of this method.
     *
     * @return true if the options are valid.
     */
    public static boolean validOptions(String options[][],
                                       DocErrorReporter reporter) {
        return true;
    }

    /**
     * Return the version of the Java Programming Language supported
     * by this doclet.
     * <p/>
     * This method is required by any doclet supporting a language version
     * newer than 1.1.
     *
     * @return the language version supported by this doclet.
     * @since 1.5
     */
    public static LanguageVersion languageVersion() {
        return LanguageVersion.JAVA_1_5;
    }
}
