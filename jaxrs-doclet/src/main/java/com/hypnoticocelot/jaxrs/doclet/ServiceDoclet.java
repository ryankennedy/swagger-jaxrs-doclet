package com.hypnoticocelot.jaxrs.doclet;

import com.hypnoticocelot.jaxrs.doclet.parser.JaxRsAnnotationParser;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;

import java.util.HashMap;
import java.util.Map;

public class ServiceDoclet {

    /**
     * Generate documentation here.
     * This method is required for all doclets.
     *
     * @return true on success.
     */
    public static boolean start(RootDoc doc) {
        DocletOptions options = DocletOptions.parse(doc.options());
        return new JaxRsAnnotationParser(options, doc).run();
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
        options.put("-disableModels", 1);
        options.put("-errorTags", 2);

        Integer value = options.get(option);
        if (value != null) {
            return value;
        } else {
            return 0;
        }
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
