package com.yammer.dropwizard.apidocs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationDesc.ElementValuePair;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;

public class ServiceDoclet {
	public static final String JAX_RS_ANNOTATION_PACKAGE = "javax.ws.rs";
	public static final String JAX_RS_PATH = "javax.ws.rs.Path";
	public static final String JAX_RS_PATH_PARAM = "javax.ws.rs.PathParam";
	public static final String JAX_RS_QUERY_PARAM = "javax.ws.rs.QueryParam";
	public static final String XML_ROOT_ELEMENT = "javax.xml.bind.annotation.XmlRootElement";
	@SuppressWarnings("serial")
	private static final List<String> JAX_RS_EXCLUDE_ANNOTATION_CLASSES = new ArrayList<String>() {{
		add("javax.ws.rs.HeaderParam");
		add("javax.ws.rs.core.Context");
	}};

	private static String docBasePath = "http://localhost:8080";
	private static String apiBasePath = "http://localhost:8080";
	private static String apiVersion = "0";
	/**
	 * If a method parameter is annotated with one of these classes, exclude
	 * from swagger documentation
	 */
	private static List<String> excludeAnnotationClasses = new ArrayList<String>(JAX_RS_EXCLUDE_ANNOTATION_CLASSES);

	@SuppressWarnings("serial")
	public static final List<String> METHODS = new ArrayList<String>() {{
		add("javax.ws.rs.GET");
		add("javax.ws.rs.PUT");
		add("javax.ws.rs.POST");
		add("javax.ws.rs.DELETE");
	}};

	@SuppressWarnings("serial")
	public static final List<String> PRIMITIVES = new ArrayList<String>() {{
		add("byte");
		add("boolean");
		add("int");
		add("long");
		add("float");
		add("double");
		add("string");
		add("Date");
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
		if(parameters.getExcludeAnnotationClasses()!=null)
			excludeAnnotationClasses.addAll(parameters.getExcludeAnnotationClasses());

		Map<String, Map<String,List<Method>>> apiMap = new HashMap<String, Map<String,List<Method>>>();
		Map<String, Map<String,Model>> modelMap = new HashMap<String, Map<String,Model>>();

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

		try {
			List<ResourceListingAPI> builder = new LinkedList<ResourceListingAPI>();

			for (ClassDoc classDoc : doc.classes()) {
				//go through each class
				String apiPath = path(classDoc.annotations());
				if (apiPath != null) {
					Map<String,List<Method>> methodMap = apiMap.get(apiPath);
					if(methodMap==null){
						methodMap = new HashMap<String,List<Method>>();
					}
					Map<String,Model> classModelMap = modelMap.get(apiPath);
					if(classModelMap==null){
						classModelMap = new HashMap<String,Model>();
					}

					//add all jax-rs annotated methods to the methodmap
					for (MethodDoc method : classDoc.methods()) {
						Method me = parseMethod(method);
						if (me != null) {
							List<Method> methods = methodMap.get(me.getPath());
							if (methods==null){
								methods = new ArrayList<Method>();
							}
							methods.add(me);
							methodMap.put(me.getPath(), methods);

							//build model for body parameter
							for (Parameter parameter : method.parameters()) {
								if (shouldIncludeParameter(parameter)) {
									if(paramTypeOf(parameter).equalsIgnoreCase("body") && !PRIMITIVES.contains(typeOf(parameter.type())))
										classModelMap = parseModels(parameter.type(), classModelMap);
								}
							}

							//build model for return type
							Type type = method.returnType();
							if(!type.simpleTypeName().equalsIgnoreCase("void")){
								String name = typeOf(type);
								if (me.getReturnType()==null || !me.getReturnType().equals(name)){
									me.setReturnType(name);
								}
								if(!PRIMITIVES.contains(name)){
									classModelMap = parseModels(type, classModelMap);
								}
							}

						}
					}
					apiMap.put(apiPath, methodMap);
					modelMap.put(apiPath, classModelMap);
				}
			}

			//Sort the classes based upon class path annotation
			List<String> apiList = new ArrayList<String>(apiMap.keySet());
			Collections.sort(apiList);

			for(String apiPath: apiList){
				List<Api> apiBuilder = new LinkedList<Api>();

				Map<String,List<Method>> methodMap = apiMap.get(apiPath);
				List<String> keyList = new ArrayList<String>(methodMap.keySet());
				Collections.sort(keyList);
				for(String path:keyList){
					//turn list of methods into list of api objects
					List<Operation> methodBuilder = new LinkedList<Operation>();

					for(Method me:methodMap.get(path)){
						methodBuilder.add(new Operation(me.getMethod(), me.getMethodName(), typeOf(me.getReturnType()),
								me.getParameters(), me.getFirstSentence(), me.getComment()));
					}
					apiBuilder.add(new Api(apiPath+path, "", methodBuilder));
				}

				//write out json for methods
				String rootPath = (apiPath.startsWith("/") ? apiPath.replaceFirst("/", "") : apiPath).replaceAll("/", "_").replaceAll("(\\{|\\})", "");
				builder.add(new ResourceListingAPI("/" + rootPath + ".{format}",""));

				File apiFile = new File(parameters.getOutput(), rootPath + ".json");
				ApiDeclaration declaration = new ApiDeclaration(apiVersion, apiBasePath, apiBuilder, modelMap.get(apiPath));

				mapper.writeValue(apiFile, declaration);
			}

			//write out json for api
			ResourceListing listing = new ResourceListing(apiVersion, docBasePath, builder);
			File docFile = new File(parameters.getOutput(), "service.json");
			mapper.writeValue(docFile, listing);

			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Gets the string representation of the jax-rs path from an array of annotations.
	 *
	 * @param annotations
	 * @return
	 */
	private static String path(AnnotationDesc[] annotations) {
		for (AnnotationDesc annotationDesc : annotations) {
			if (annotationDesc.annotationType().qualifiedTypeName().equals(JAX_RS_PATH)) {
				for (AnnotationDesc.ElementValuePair pair : annotationDesc.elementValues()) {
					if (pair.element().name().equals("value")) {
						String path = pair.value().value().toString();
						if(path.endsWith("/")){
							path = path.substring(0,path.length()-1);
						}
						return path.startsWith("/") ? path : "/" + path;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Turns a MethodDoc(Javadoc) into a swagger serialize-able method object.
	 *
	 * @param method
	 * @return
	 */
	private static Method parseMethod(MethodDoc method) {
		for (AnnotationDesc desc : method.annotations()) {
			if (METHODS.contains(desc.annotationType().qualifiedTypeName())) {

				//Path
				String path = path(method.annotations());
				if (path==null) path = "";

				//Parameters
				List<ApiParameter> parameterBuilder = new LinkedList<ApiParameter>();

				for (Parameter parameter : method.parameters()) {
					if (shouldIncludeParameter(parameter)) {
						String parameterComment = commentForParameter(method, parameter);
						parameterBuilder.add(new ApiParameter(paramTypeOf(parameter), paramNameOf(parameter),
								parameterComment,
								typeOf(parameter.type())));
					}
				}

				//First Sentence of Javadoc method description
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

	/**
	 * Recursively adds models to the model map for a type
	 *
	 * @param modelMap
	 * @return
	 */
	private static Map<String,Model> parseModels(Type type, Map<String,Model> modelMap){
		String typeName = typeOf(type);
		ClassDoc cd = type.asClassDoc();
		if(cd!=null){
			Model model = modelMap.get(typeName);
			if(model == null){
				Map<String, Type> eleMap = new HashMap<String, Type>();

				//Get fields
				FieldDoc[] fdArr = cd.fields();
				if(fdArr!=null && fdArr.length>0){
					for(FieldDoc fd: fdArr){
						if(eleMap.get(fd.name())==null){
							eleMap.put(fd.name(), fd.type());
						}
					}
				}

				//Get methods
				MethodDoc[] mdArr = cd.methods();
				if(mdArr!=null && mdArr.length>0){
					for(MethodDoc md:mdArr){
						if(md.name().startsWith("get") && md.name().length()>3){
							String name = md.name().substring(3);
							name = name.substring(0,1).toLowerCase() + (name.length()>1?name.substring(1):"");
							if(eleMap.get(name)==null){
								eleMap.put(name, md.returnType());
							}
						}
					}
				}

				//Process all fields & methods
				if(eleMap.keySet().size()>0){
					Map<String,Property> fieldMap = new HashMap<String, Property>();
					for(String eleName: eleMap.keySet()){

						Type eleType = eleMap.get(eleName);

						//Check if it is a collection and get collection type
						String containerOf = null;
						ParameterizedType pt = eleType.asParameterizedType();
						if(pt!=null){
							Type[] typeArgs = pt.typeArguments();
							if(typeArgs!=null && typeArgs.length>0){
								containerOf = typeOf(typeArgs[0]);
								if(!PRIMITIVES.contains(containerOf)){
									parseModels(typeArgs[0],modelMap);
								}
							}
						}

						//Add to map
						String eleTypeName = typeOf(eleType);
						fieldMap.put(eleName, new Property(eleTypeName,null,containerOf));

						//If not primitive, build the model for it too
						if(!PRIMITIVES.contains(eleTypeName)){
							parseModels(eleType,modelMap);
						}
					}
					modelMap.put(typeName, new Model(typeName,fieldMap));
				}
			}
		}
		return modelMap;
	}

	/**
	 * Determines if a parameter should be included, based upon annotation package.
	 *
	 * @param parameter
	 * @return
	 */
	private static boolean shouldIncludeParameter(Parameter parameter) {
		AnnotationDesc[] annotations = parameter.annotations();

		//Include if it has no annotations
		if(annotations==null || annotations.length==0)
			return true;

		//Include if it has a jax-rs annotation
		for (AnnotationDesc annotation : annotations) {
			String annotationClass = annotation.annotationType().qualifiedTypeName();
			if(annotationClass.startsWith(JAX_RS_ANNOTATION_PACKAGE)
					&& !excludeAnnotationClasses.contains(annotationClass)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines the string representation of the parameter type.
	 *
	 * @param parameter
	 * @return
	 */
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

	/**
	 * Determines the XmlRootElement name. Returns null if no name found.
	 *
	 * @param classDoc
	 * @return
	 */
	private static String getRootElementNameOf(ClassDoc classDoc) {
		AnnotationDesc[] annotations = classDoc.annotations();
		for (AnnotationDesc annotation : annotations) {
			String annotationTypeName = annotation.annotationType().qualifiedTypeName();
			if (annotationTypeName.equals(XML_ROOT_ELEMENT)) {
				ElementValuePair[] evpArr = annotation.elementValues();
				if(evpArr.length>0){
					for(ElementValuePair evp:evpArr){
						if (evp.element().name().equals("name")) {
							return evp.value().value().toString();
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Determines the string representation of the parameter name.
	 *
	 * @param parameter
	 * @return
	 */
	private static String paramNameOf(Parameter parameter) {
		AnnotationDesc[] annotations = parameter.annotations();
		for (AnnotationDesc annotation : annotations) {
			String annotationTypeName = annotation.annotationType().qualifiedTypeName();
			if (annotationTypeName.equals(JAX_RS_PATH_PARAM)||annotationTypeName.equals(JAX_RS_QUERY_PARAM)) {
				ElementValuePair[] evpArr = annotation.elementValues();
				if(evpArr.length>0){
					for(ElementValuePair evp:evpArr){
						if (evp.element().name().equals("value")) {
							return evp.value().value().toString();
						}
					}
				}
			}
		}
		return parameter.name();
	}

	/**
	 * Determines the String representation of the object Type.
	 *
	 * @param javaType
	 * @return
	 */
	private static String typeOf(String javaType) {
		String type = null;
		if (javaType.startsWith("java.lang.")) {
			int i = javaType.lastIndexOf(".");
			type = javaType.substring(i+1).toLowerCase();
		} else 	if (PRIMITIVES.contains(javaType.toLowerCase())) {
			type = javaType.toLowerCase();
		} else if(javaType.equals("java.util.Date")) {
			type = "Date";
		} else {
			int i = javaType.lastIndexOf(".");
			if(i>=0){
				type = javaType.substring(i+1);
			} else {
				type = javaType;
			}
		}
		if(type.equalsIgnoreCase("integer")){
			type = "int";
		} else if(type.equalsIgnoreCase("arraylist") || type.equalsIgnoreCase("linkedlist")){
			type = "List";
		}
		return type;
	}

	private static String typeOf(Type type){
		String name = null;
		ClassDoc cd = type.asClassDoc();
		if(cd!=null){
			name = getRootElementNameOf(cd);
			if(name==null){
				name = typeOf(type.qualifiedTypeName());
			}
		} else {
			name = typeOf(type.qualifiedTypeName());
		}
		return name;
	}

	/**
	 * Gets the string representation of the parameter comment from the Javadoc.
	 *
	 * @param method
	 * @param parameter
	 * @return
	 */
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
		options.put("-excludeAnnotationClasses", 2);

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
