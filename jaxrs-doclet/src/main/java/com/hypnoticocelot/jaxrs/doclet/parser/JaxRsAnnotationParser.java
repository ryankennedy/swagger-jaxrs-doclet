package com.hypnoticocelot.jaxrs.doclet.parser;

import com.google.common.base.Function;
import com.hypnoticocelot.jaxrs.doclet.DocletOptions;
import com.hypnoticocelot.jaxrs.doclet.Recorder;
import com.hypnoticocelot.jaxrs.doclet.ServiceDoclet;
import com.hypnoticocelot.jaxrs.doclet.model.*;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.google.common.collect.Maps.uniqueIndex;

public class JaxRsAnnotationParser {

    private final DocletOptions options;
    private final RootDoc rootDoc;

    public JaxRsAnnotationParser(DocletOptions options, RootDoc rootDoc) {
        this.options = options;
        this.rootDoc = rootDoc;
    }

    public boolean run() {
        try {
            Collection<ApiDeclaration> declarations = new ArrayList<ApiDeclaration>();
            for (ClassDoc classDoc : rootDoc.classes()) {
                ApiClassParser classParser = new ApiClassParser(options, classDoc);
                Collection<Api> apis = classParser.parse();
                if (apis.isEmpty()) {
                    continue;
                }

                // The idea behind this declaration is that "/foo" and "/foo/annotated" are stored in separate "Api" classes but are essentially the same APIs.
                // ... "Api" class should actually include all API methods, but with paths.
                Map<String, Model> models = uniqueIndex(classParser.models(), new Function<Model, String>() {
                    @Override
                    public String apply(Model model) {
                        return model.getId();
                    }
                });
                String apiPath = apis.iterator().next().getPath();
                String rootPath = "/" + (apiPath.startsWith("/") ? apiPath.replaceFirst("/", "") : apiPath).replaceAll("/", "_").replaceAll("(\\{|\\})", "");
                declarations.add(new ApiDeclaration(options.getApiVersion(), options.getApiBasePath(), rootPath, apis, models));
            }
            writeApis(declarations);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void writeApis(Collection<ApiDeclaration> apis) throws IOException {
        List<ResourceListingAPI> resources = new LinkedList<ResourceListingAPI>();
        File outputDirectory = options.getOutputDirectory();
        Recorder recorder = options.getRecorder();

        for (ApiDeclaration api : apis) {
            resources.add(new ResourceListingAPI(api.getResourcePath() + ".{format}", ""));
            File apiFile = new File(outputDirectory, api.getResourcePath().replaceFirst("/", "") + ".json");
            recorder.record(apiFile, api);
        }

        //write out json for api
        ResourceListing listing = new ResourceListing(options.getApiVersion(), options.getDocBasePath(), resources);
        File docFile = new File(outputDirectory, "service.json");
        recorder.record(docFile, listing);

        // Copy swagger-ui into the output directory.
        final ZipInputStream swaggerZip = new ZipInputStream(ServiceDoclet.class.getResourceAsStream("/swagger-ui.zip"));
        ZipEntry entry = swaggerZip.getNextEntry();
        while (entry != null) {
            final File swaggerFile = new File(outputDirectory, entry.getName());
            if (entry.isDirectory()) {
                if (!swaggerFile.isDirectory() && !swaggerFile.mkdirs()) {
                    throw new RuntimeException("Unable to create directory: " + swaggerFile);
                }
            } else {
                recorder.record(swaggerFile, swaggerZip);
            }

            entry = swaggerZip.getNextEntry();
        }
        swaggerZip.close();
    }

}
