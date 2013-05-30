package com.hypnoticocelot.jaxrs.doclet;

import com.hypnoticocelot.jaxrs.doclet.model.ApiDeclaration;
import com.hypnoticocelot.jaxrs.doclet.model.ResourceListing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface Recorder {
    void record(File file, ResourceListing listing) throws IOException;

    void record(File file, ApiDeclaration declaration) throws IOException;

    void record(File file, InputStream stream) throws IOException;
}
