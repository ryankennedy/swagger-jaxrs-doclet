package com.hypnoticocelot.jaxrs.doclet;

import com.hypnoticocelot.jaxrs.doclet.translator.*;

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
    private boolean parseModels = true;
    private Recorder recorder = new ObjectMapperRecorder();
    private Translator translator;

    public DocletOptions() {
        excludeAnnotationClasses = new ArrayList<String>();
        excludeAnnotationClasses.add("javax.ws.rs.HeaderParam");
        excludeAnnotationClasses.add("javax.ws.rs.core.Context");
        translator = new FirstNotNullTranslator()
                .addNext(new JacksonAwareTranslator())
                .addNext(new JaxbAwareTranslator())
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
