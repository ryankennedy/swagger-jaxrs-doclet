package com.yammer.dropwizard.apidocs;

import java.io.File;

public class JavaDocParameters {
    private File output;
    private String docBasePath;
    private String apiBasePath;
    private String apiVersion;

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

	public static JavaDocParameters parse(String[][] options) {
        JavaDocParameters parameters = new JavaDocParameters();

        for (String[] option : options) {
            if (option[0].equals("-d")) {
                parameters.output = new File(option[1]);
            } else if(option[0].equals("-docBasePath")) {
            	parameters.docBasePath = option[1];
            } else if(option[0].equals("-apiBasePath")) {
            	parameters.apiBasePath = option[1];
            } else if(option[0].equals("-apiVersion")) {
            	parameters.apiVersion = option[1];
            }
        }

        return parameters;
    }
}
