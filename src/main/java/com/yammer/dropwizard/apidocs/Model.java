package com.yammer.dropwizard.apidocs;

import java.util.Map;

public class Model {
	
	private String id;
	private Map<String,Property> properties;
	
	public Model(){}
	
	public Model(String id, Map<String,Property> properties){
		this.id = id;
		this.properties = properties;
	}
	
	public String getId(){
		return id;
	}
	
	public Map<String,Property> getProperties(){
		return properties;
	}
}
