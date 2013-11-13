package com.missionse.atlogistics.resources;
public enum ResourceType {
	
	FOOD(0,"Food"),
	WATER(0,"Water"),
	AMMO(0,"Ammo"),
	CLOTHING(0,"Clothing"),
	FUEL(0,"Fuel"),
	MEDICAL(0,"Medical"),
	VEHICLES(0,"Vehicles"),
	PHOTO(0,"Photo"),
	VIDEO(0,"Video"),
	WAYPOINTS(0,"Waypoints"),
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
