package com.yammer.dropwizard.apidocs;

import java.io.File;

public class JavaDocParameters {
    private File output;

    private JavaDocParameters() {
    }

    public File getOutput() {
        return output;
    }

    public static JavaDocParameters parse(String[][] options) {
        JavaDocParameters parameters = new JavaDocParameters();

        for (String[] option : options) {
            if (option[0].equals("-d")) {
                parameters.output = new File(option[1]);
            }
        }

        return parameters;
    }
}
