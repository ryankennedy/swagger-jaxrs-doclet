package com.hypnoticocelot.jaxrs.doclet.sample.api;

import org.joda.time.DateTime;

public class ModelResourceModel {
    private final long modelId;
    private final String title;
    private final String description;
    private final DateTime modified;

    public ModelResourceModel(long modelId, String title, String description, DateTime modified) {
        //To change body of created methods use File | Settings | File Templates.
        this.modelId = modelId;
        this.title = title;
        this.description = description;
        this.modified = modified;
    }

    public long getModelId() {
        return modelId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public DateTime getModified() {
        return modified;
    }
}
