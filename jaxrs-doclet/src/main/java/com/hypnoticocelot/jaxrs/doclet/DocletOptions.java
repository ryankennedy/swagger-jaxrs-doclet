package com.hypnoticocelot.jaxrs.doclet;

import com.hypnoticocelot.jaxrs.doclet.translator.AnnotationAwareTranslator;
import com.hypnoticocelot.jaxrs.doclet.translator.FirstNotNullTranslator;
import com.hypnoticocelot.jaxrs.doclet.translator.NameBasedTranslator;
import com.hypnoticocelot.jaxrs.doclet.translator.Translator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Arrays.asList;
import static java.util.Arrays.copyOfRange;

public class DocletOptions {

    private File outputDirectory;
    private String docBasePath = "http://localhost:8080";
    private String apiBasePath = "http://localhost:8080";
    private String apiVersion = "0";
    private List<String> excludeAnnotationClasses;
    private List<String> errorTags;
    private boolean parseModels = true;
    private Recorder recorder = new ObjectMapperRecorder();
    private Translator translator;

    public DocletOptions() {
        excludeAnnotationClasses = new ArrayList<String>();
        excludeAnnotationClasses.add("javax.ws.rs.HeaderParam");
        excludeAnnotationClasses.add("javax.ws.rs.core.Context");
        errorTags = new ArrayList<String>();
        errorTags.add("errorResponse");   // swagger 1.1
        errorTags.add("responseMessage"); // swagger 1.2
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
            } else if (option[0].equals("-excludeAnnotationClasses")) {
                parsedOptions.excludeAnnotationClasses.addAll(asList(copyOfRange(option, 1, option.length)));
            } else if (option[0].equals("-disableModels")) {
                parsedOptions.parseModels = false;
            } else if (option[0].equals("-errorTags")) {
                parsedOptions.errorTags.addAll(asList(copyOfRange(option, 1, option.length)));;
            }
        }
        return parsedOptions;
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

    public List<String> getExcludeAnnotationClasses() {
        return excludeAnnotationClasses;
    }
    
    public List<String> getErrorTags() {
        return errorTags;
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

}
