package com.hypnoticocelot.jaxrs.doclet.apidocs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteStreams;
import com.hypnoticocelot.jaxrs.doclet.DocletOptions;
import com.hypnoticocelot.jaxrs.doclet.Recorder;
import com.hypnoticocelot.jaxrs.doclet.model.ApiDeclaration;
import com.hypnoticocelot.jaxrs.doclet.model.ResourceListing;
import com.hypnoticocelot.jaxrs.doclet.parser.JaxRsAnnotationParser;
import com.hypnoticocelot.jaxrs.doclet.translator.NameBasedTranslator;
import com.sun.javadoc.RootDoc;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Options;
import com.sun.tools.javadoc.JavadocTool;
import com.sun.tools.javadoc.Messager;
import com.sun.tools.javadoc.ModifierFilter;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ServiceDocletTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testStart() throws IOException {
        // Load the fixture as a RootDoc
        final Context context = new Context();
        final Options compOpts = Options.instance(context);
        compOpts.put("-sourcepath", "src/test/resources");

        Messager.preRegister(context, "Messager!");
        final ListBuffer<String> subPackages = new ListBuffer<String>();
        subPackages.append("fixtures.sample");
        final JavadocTool javaDoc = JavadocTool.make0(context);
        final RootDoc rootDoc = javaDoc.getRootDocImpl("", null, new ModifierFilter(ModifierFilter.ALL_ACCESS),
                new ListBuffer<String>().toList(),
                new ListBuffer<String[]>().toList(),
                false,
                subPackages.toList(),
                new ListBuffer<String>().toList(),
                false, false, false);

        // Parse the RootDoc with ServiceDoclet to a JSON structure
        final TestRecorder recorder = new TestRecorder();
        DocletOptions options = DocletOptions.parse(rootDoc.options());
        options.setRecorder(recorder);
        options.setTranslator(new NameBasedTranslator());
        assertTrue("JavaDoc generation failed", new JaxRsAnnotationParser(options, rootDoc).run());

        final ResourceListing expectedListing = mapper.readValue(getClass().getResourceAsStream("/fixtures/sample/service.json"), ResourceListing.class);
        assertThat(recorder.getListing(new File("service.json")), equalTo(expectedListing));

        final ApiDeclaration expectedDeclaration = mapper.readValue(getClass().getResourceAsStream("/fixtures/sample/foo.json"), ApiDeclaration.class);
        assertThat(recorder.getDeclaration(new File("foo.json")), equalTo(expectedDeclaration));
    }

    private class TestRecorder implements Recorder {
        private final Map<File, ResourceListing> listings = new HashMap<File, ResourceListing>();
        private final Map<File, ApiDeclaration> declarations = new HashMap<File, ApiDeclaration>();
        private final Map<File, byte[]> rawFiles = new HashMap<File, byte[]>();

        public ResourceListing getListing(File file) {
            return listings.get(file);
        }

        public ApiDeclaration getDeclaration(File file) {
            return declarations.get(file);
        }

        @Override
        public void record(File file, ResourceListing listing) throws IOException {
            listings.put(file, listing);
        }

        @Override
        public void record(File file, ApiDeclaration declaration) throws IOException {
            declarations.put(file, declaration);
        }

        @Override
        public void record(File file, InputStream stream) throws IOException {
            rawFiles.put(file, ByteStreams.toByteArray(stream));
        }
    }
}
