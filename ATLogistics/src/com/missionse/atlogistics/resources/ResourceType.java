package com.missionse.atlogistics.resources;
public enum ResourceType {
	
	FOOD(0,"Food"),
	WATER(0,"Water"),
	;
	
	private int resource;
	private String text;
	
	ResourceType(final int resourceId, String description){
		resource = resourceId;
		text = description;
	}
	
	public int getResourceId(){
		return resource;
	}
	
	public String getDescription(){
		return text;
	}

}
