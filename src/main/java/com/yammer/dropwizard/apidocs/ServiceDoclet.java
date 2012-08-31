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

	private static String docBasePath = "http://localhost:8080";
	private static String apiBasePath = "http://localhost:8080";
	private static String apiVersion = "0";

	@SuppressWarnings("serial")
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

		if(parameters.getDocBasePath()!=null)
			docBasePath=parameters.getDocBasePath();
		if(parameters.getApiBasePath()!=null)
			apiBasePath=parameters.getApiBasePath();
		if(parameters.getApiVersion()!=null)
			apiVersion=parameters.getApiVersion();

		Map<String, Map<String,List<Method>>> apiMap = new HashMap<String, Map<String,List<Method>>>();

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);

		try {
			List<ResourceListingAPI> builder = new LinkedList<ResourceListingAPI>();

			for (ClassDoc classDoc : doc.classes()) {
				String apiPath = path(classDoc.annotations());
				if (apiPath != null) {

					Map<String,List<Method>> methodMap = apiMap.get(apiPath);
					if(methodMap==null){
						methodMap = new HashMap<String,List<Method>>();
					}

					for (MethodDoc method : classDoc.methods()) {
						Method me = parseMethod(method);
						if (me != null) {
							List<Method> methods = methodMap.get(me.getPath());
							if (methods==null){
								methods = new ArrayList<Method>();
							}
							methods.add(me);
							methodMap.put(me.getPath(), methods);

						}
					}
					apiMap.put(apiPath, methodMap);
				}
			}
			List<String> apiList = new ArrayList<String>(apiMap.keySet());
			Collections.sort(apiList);
			for(String apiPath: apiList){
				List<Api> apiBuilder = new LinkedList<Api>();

				Map<String,List<Method>> methodMap = apiMap.get(apiPath);
				List<String> keyList = new ArrayList<String>(methodMap.keySet());
				Collections.sort(keyList);
				for(String path:keyList){
					List<Operation> methodBuilder = new LinkedList<Operation>();

					for(Method me:methodMap.get(path)){
						methodBuilder.add(new Operation(me.getMethod(), me.getMethodName(), typeOf(me.getReturnType()),
								me.getParameters(), me.getFirstSentence(), me.getComment()));
					}
					apiBuilder.add(new Api(apiPath+path, "", methodBuilder));
				}

				builder.add(new ResourceListingAPI("/" + apiPath.replace("/", "") + ".{format}",""));

				File apiFile = new File(parameters.getOutput(), apiPath.replace("/", "") + ".json");
				ApiDeclaration declaration = new ApiDeclaration(apiVersion, apiBasePath, apiBuilder);
				mapper.writeValue(apiFile, declaration);
			}

			ResourceListing listing = new ResourceListing(apiVersion, docBasePath, builder);
			File docFile = new File(parameters.getOutput(), "service.json");
			mapper.writeValue(docFile, listing);

			return true;
		} catch (IOException e) {
			return false;
		}
	}

	private static String path(AnnotationDesc[] annotations) {
		for (AnnotationDesc annotationDesc : annotations) {
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

				String path = path(method.annotations());
				if (path==null) path = "";

				List<ApiParameter> parameterBuilder = new LinkedList<ApiParameter>();

				for (Parameter parameter : method.parameters()) {
					String parameterComment = commentForParameter(method, parameter);
					parameterBuilder.add(new ApiParameter(paramTypeOf(parameter), parameter.name(), parameterComment,
							typeOf(parameter.typeName())));
				}
				
				Tag[] fst = method.firstSentenceTags();
				String fss = "";
				for(Tag t:fst){
					fss += t.text();
				}

				return new Method(desc.annotationType().name(),
						method.name(),
						path,
						parameterBuilder,
						fss,
						method.commentText().replace(fss, ""),
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
		} else if(javaType.equals("java.util.Date")) {
			return "Date";
		} else {
			// TODO: have to make sure we add this type to the models section
			int i = javaType.lastIndexOf(".");
			if(i>=0){
				return javaType.substring(i+1);
			} else {
				return javaType;	
			}
			
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
		options.put("-docBasePath", 2);
		options.put("-apiBasePath", 2);
		options.put("-apiVersion", 2);

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
