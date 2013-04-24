package com.yammer.dropwizard.apidocs;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
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
        final ListBuffer<String> subPackages = new ListBuffer<>();
        subPackages.add("fixtures.sample");
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
        assertTrue("ServiceDoclet failed", ServiceDoclet.startInternal(rootDoc, recorder));

        // Validate the JSON structure against a fixture
        final ResourceListing expectedListing = mapper.readValue(getClass().getResourceAsStream("/fixtures/sample/service.json"), ResourceListing.class);
        assertEquals(expectedListing, recorder.getListing(new File("service.json")));

        final ApiDeclaration expectedDeclaration = mapper.readValue(getClass().getResourceAsStream("/fixtures/sample/foo.json"), ApiDeclaration.class);
        assertEquals(expectedDeclaration, recorder.getDeclaration(new File("foo.json")));
    }

    private class TestRecorder implements ServiceDoclet.Recorder {
        private final Map<File, ResourceListing> listings = new HashMap<>();
        private final Map<File, ApiDeclaration> declarations = new HashMap<>();

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
    }
}
