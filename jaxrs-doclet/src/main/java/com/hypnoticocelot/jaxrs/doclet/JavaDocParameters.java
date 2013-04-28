package com.hypnoticocelot.jaxrs.doclet;

import java.util.Arrays;

import java.io.File;
import java.util.List;

public class JavaDocParameters {
    private File output;
    private String docBasePath;
    private String apiBasePath;
    private String apiVersion;
    private List<String> excludeAnnotationClasses;

    private JavaDocParameters() {
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

    /**
     * Parse javadoc doclet options
     *
	 * @param options
	 * <p>
     *                Example:
     *                <pre>
     *                  -apiVersion 1
     *                  -docBasePath http://localhost:8080
     *                  -apiBasePath http://localhost:8080
     *                  -excludeAnnotationClasses com.example.Context com.example.Auth
     *                  </pre>
     *                </p>
     * @return parsed javadoc parameters
	 *
     * @see com.sun.javadoc.RootDoc#options
     */
    public static JavaDocParameters parse(String[][] options) {
        JavaDocParameters parameters = new JavaDocParameters();

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
                parameters.excludeAnnotationClasses = Arrays.asList(Arrays.copyOfRange(option, 1, option.length));
            }
        }

        return parameters;
    }
}
