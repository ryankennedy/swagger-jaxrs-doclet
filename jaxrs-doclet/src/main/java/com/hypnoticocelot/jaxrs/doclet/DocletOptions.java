package com.hypnoticocelot.jaxrs.doclet;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Arrays.asList;
import static java.util.Arrays.copyOfRange;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.hypnoticocelot.jaxrs.doclet.translator.AnnotationAwareTranslator;
import com.hypnoticocelot.jaxrs.doclet.translator.FirstNotNullTranslator;
import com.hypnoticocelot.jaxrs.doclet.translator.NameBasedTranslator;
import com.hypnoticocelot.jaxrs.doclet.translator.Translator;

public class DocletOptions {
    public static final String DEFAULT_SWAGGER_UI_ZIP_PATH = "n/a";
    private File outputDirectory;
    private String docBasePath = "http://localhost:8080";
    private String apiBasePath = "http://localhost:8080";
    private String swaggerUiZipPath = DEFAULT_SWAGGER_UI_ZIP_PATH;
    private String apiVersion = "0";
    private List<String> typesToTreatAsOpaque;
    private List<String> errorTags;
    private List<String> excludeAnnotationClasses;
    private boolean parseModels = true;
    private Recorder recorder = new ObjectMapperRecorder();
    private Translator translator;
    /**
     * mapping between a fully qualified method name and a type that should be used for documenting the model
     * e.g.: 
     * <pre>
     * fixtures.sample.Service.getSubResourceWrappedInResponse(java.lang.String,java.lang.String)=fixtures.sample.SubResource
     * </pre>
     */
    private Properties returnTypesOverrideMapping = new Properties();


    public DocletOptions() {
        excludeAnnotationClasses = new ArrayList<String>();
        excludeAnnotationClasses.add("javax.ws.rs.HeaderParam");
        excludeAnnotationClasses.add("javax.ws.rs.core.Context");
        errorTags = new ArrayList<String>();
        errorTags.add("errorResponse");   // swagger 1.1
        errorTags.add("responseMessage"); // swagger 1.2
        typesToTreatAsOpaque = new ArrayList<String>();
        typesToTreatAsOpaque.add("org.joda.time.DateTime");
        typesToTreatAsOpaque.add("java.util.UUID");
        translator = new FirstNotNullTranslator()
                .addNext(new AnnotationAwareTranslator()
                        .ignore("javax.xml.bind.annotation.XmlTransient")
                        .element("javax.xml.bind.annotation.XmlElement", "name")
                        .rootElement("javax.xml.bind.annotation.XmlRootElement", "name")
                )
                .addNext(new AnnotationAwareTranslator()
                        .ignore("com.fasterxml.jackson.annotation.JsonIgnore")
                        .element("com.fasterxml.jackson.annotation.JsonProperty", "value")
                        .rootElement("com.fasterxml.jackson.annotation.JsonRootName", "value")
                )
                .addNext(new NameBasedTranslator());
    }

    public static DocletOptions parse(String[][] options) {
        DocletOptions parsedOptions = new DocletOptions();
        for (String[] option : options) {
            if (option[0].equals("-d")) {
                parsedOptions.outputDirectory = new File(option[1]);
                checkArgument(parsedOptions.outputDirectory.isDirectory(), "Path after -d is expected to be a directory!");
            } else if (option[0].equals("-docBasePath")) {
                parsedOptions.docBasePath = option[1];
            } else if (option[0].equals("-apiBasePath")) {
                parsedOptions.apiBasePath = option[1];
            } else if (option[0].equals("-apiVersion")) {
                parsedOptions.apiVersion = option[1];
            } else if (option[0].equals("-swaggerUiZipPath")) {
                parsedOptions.swaggerUiZipPath = option[1];
            } else if (option[0].equals("-excludeAnnotationClasses")) {
                parsedOptions.excludeAnnotationClasses.addAll(asList(copyOfRange(option, 1, option.length)));
            } else if (option[0].equals("-disableModels")) {
                parsedOptions.parseModels = false;
            } else if (option[0].equals("-errorTags")) {
                parsedOptions.errorTags.addAll(asList(copyOfRange(option, 1, option.length)));;
            } else if (option[0].equals("-typesToTreatAsOpaque")) {
                parsedOptions.typesToTreatAsOpaque.addAll(asList(copyOfRange(option, 1, option.length)));;
            } else if (option[0].equals("-returnTypesOverrideMapping")) {
                checkArgument(option.length > 1, "Path to properties file with return types override mapping not provided!");
                parsedOptions.returnTypesOverrideMapping = loadTypesOverrideMapping(option[1]);
            }
        }
        return parsedOptions;
    }
    
    /**
     * Loads a property file pointed by <code>filePath</code> that contains a mapping between a fully qualified method 
     * and the return type that should be used for documenting the model
     * 
     * @param filePath
     * @return
     */
    private static Properties loadTypesOverrideMapping(String filePath) {
        Properties result = new Properties();
        FileInputStream in = null;
        try {
            in = new FileInputStream(filePath); 
            result.load(in);
        } catch (FileNotFoundException e) {
            checkArgument(false, "Properties file with return types override mapping not found!");
        } catch (IOException e) {
            checkArgument(false, "Error reading properties file with return types override mapping!");
        } catch(IllegalArgumentException e) {
            checkArgument(false, "Illegal characters in properties file with return types override mapping!");
        }
        finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException ignore) {
                    {}
                }
            }
        }
        
        return result;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public String getDocBasePath() {
        return docBasePath;
    }

    public String getApiBasePath() {
        return apiBasePath;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public String getSwaggerUiZipPath() {
        return swaggerUiZipPath;
    }

    public List<String> getExcludeAnnotationClasses() {
        return excludeAnnotationClasses;
    }
    
    public List<String> getErrorTags() {
        return errorTags;
    }

    public List<String> getTypesToTreatAsOpaque() {
        return typesToTreatAsOpaque;
    }

    public boolean isParseModels() {
        return parseModels;
    }

    public Recorder getRecorder() {
        return recorder;
    }

    public DocletOptions setRecorder(Recorder recorder) {
        this.recorder = recorder;
        return this;
    }

    public Translator getTranslator() {
        return translator;
    }

    public DocletOptions setTranslator(Translator translator) {
        this.translator = translator;
        return this;
    }
    
    public Properties getReturnTypesOverrideMapping() {
        return returnTypesOverrideMapping;
    }

}
