package com.yammer.dropwizard.apidocs;

public class ApiParameter {
    private String paramType;
    private String name;
    private String description;
    private String dataType;

    @SuppressWarnings("unused")
	private ApiParameter() { }

    public ApiParameter(String paramType, String name, String description, String dataType) {
        this.paramType = paramType;
        this.name = name;
        this.description = description;
        this.dataType = dataType;
    }

    public String getParamType() {
        return paramType;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDataType() {
        return dataType;
    }

    public boolean getRequired() {
        return (paramType.equals("query")?false:true);
    }
}
