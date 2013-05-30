package com.hypnoticocelot.jaxrs.doclet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Arrays.copyOfRange;

public class DocletOptions {

    private File output;
    private String docBasePath = "http://localhost:8080";
    private String apiBasePath = "http://localhost:8080";
    private String apiVersion = "0";
    private List<String> excludeAnnotationClasses;
    private boolean parseModels = true;
    private Recorder recorder = new ObjectMapperRecorder();

    private DocletOptions() {
        excludeAnnotationClasses = new ArrayList<String>();
        excludeAnnotationClasses.add("javax.ws.rs.HeaderParam");
        excludeAnnotationClasses.add("javax.ws.rs.core.Context");
    }

    public File getOutput() {
        return output;
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

    public void setRecorder(Recorder recorder) {
        this.recorder = recorder;
    }

    public static DocletOptions parse(String[][] options) {
        DocletOptions parameters = new DocletOptions();
        for (String[] option : options) {
            if (option[0].equals("-d")) {
                parameters.output = new File(option[1]);
            } else if (option[0].equals("-docBasePath")) {
                parameters.docBasePath = option[1];
            } else if (option[0].equals("-apiBasePath")) {
                parameters.apiBasePath = option[1];
            } else if (option[0].equals("-apiVersion")) {
                parameters.apiVersion = option[1];
            } else if (option[0].equals("-excludeAnnotationClasses")) {
                parameters.excludeAnnotationClasses.addAll(asList(copyOfRange(option, 1, option.length)));
            } else if (option[0].equals("-disableModels")) {
                parameters.parseModels = false;
            }
        }
        return parameters;
    }

}
