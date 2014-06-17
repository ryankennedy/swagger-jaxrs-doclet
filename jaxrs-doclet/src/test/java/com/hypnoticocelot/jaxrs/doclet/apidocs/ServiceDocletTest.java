package com.hypnoticocelot.jaxrs.doclet.apidocs;

import com.hypnoticocelot.jaxrs.doclet.DocletOptions;
import com.hypnoticocelot.jaxrs.doclet.Recorder;
import com.hypnoticocelot.jaxrs.doclet.model.ApiDeclaration;
import com.hypnoticocelot.jaxrs.doclet.model.ResourceListing;
import com.hypnoticocelot.jaxrs.doclet.parser.JaxRsAnnotationParser;
import com.sun.javadoc.RootDoc;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static com.hypnoticocelot.jaxrs.doclet.apidocs.FixtureLoader.loadFixture;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ServiceDocletTest {

    private Recorder recorderMock;
    private DocletOptions options;

    @Before
    public void setup() {
        recorderMock = mock(Recorder.class);
        options = new DocletOptions().setRecorder(recorderMock);
    }

    @Test
    public void testStart() throws IOException {
        final RootDoc rootDoc = RootDocLoader.fromPath("src/test/resources", "fixtures.sample");

        boolean parsingResult = new JaxRsAnnotationParser(options, rootDoc).run();
        assertThat("JavaDoc generation failed", parsingResult, equalTo(true));

        final ResourceListing expectedListing = loadFixture("/fixtures/sample/service.json", ResourceListing.class);
        verify(recorderMock).record(any(File.class), eq(expectedListing));

        final ApiDeclaration expectedDeclaration = loadFixture("/fixtures/sample/foo.json", ApiDeclaration.class);
        verify(recorderMock).record(any(File.class), eq(expectedDeclaration));
    }
    
    @Test
    public void testStartWithReturnTypeOverriden() throws IOException {
        // given
        String[][] opts = {{"-returnTypesOverrideMapping", "src/test/resources/fixtures/sample/returnTypeMapping.properties"}};
        options = DocletOptions.parse(opts);
        options.setRecorder(recorderMock);
        
        final RootDoc rootDoc = RootDocLoader.fromPath("src/test/resources", "fixtures.sample");
        
        //when
        boolean parsingResult = new JaxRsAnnotationParser(options, rootDoc).run();
        
        //then
        assertThat("JavaDoc generation failed", parsingResult, equalTo(true));

        final ResourceListing expectedListing = loadFixture("/fixtures/sample/service.json", ResourceListing.class);
        verify(recorderMock).record(any(File.class), eq(expectedListing));

        final ApiDeclaration expectedDeclaration = loadFixture("/fixtures/sample/foo_returnTypeOverriden.json", ApiDeclaration.class);
        verify(recorderMock).record(any(File.class), eq(expectedDeclaration));
    }

}
