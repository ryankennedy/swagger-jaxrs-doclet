package com.yammer.dropwizard.apidocs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Property {
	private String type;
	private String description;
	private String containerOf;
	
	public Property(){}
	
	public Property(String type, String description, String containerOf){
		this.type = type;
		this.description = description;
		this.containerOf = containerOf;
	}
	
	public String getType(){
		return type;
	}
	
	public String getDescription(){
		return description;
	}
	
    public AllowableValues getAllowableValues() {
    	if(type.equals("boolean")){
    		List<String> values = new ArrayList<>();
    		values.add("false");
    		values.add("true");
    		return new AllowableValues(values);
    	} else {
    		return null;
    	}
    }
    
    public Map<String,String> getItems(){
    	Map<String,String> result = null;
    	if(containerOf!=null){
    		result = new HashMap<>();
    		result.put("$ref", containerOf);
    	}
    	return result;
    }
}