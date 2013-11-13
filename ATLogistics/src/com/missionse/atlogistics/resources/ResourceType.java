package com.missionse.atlogistics.resources;

import com.missionse.atlogistics.R;

public enum ResourceType {

	FOODWATER(R.drawable.food, "Food/Water"), 
	AMMO(R.drawable.ammo, "Ammo"), 
	CLOTHING(R.drawable.clothing, "Clothing"), 
	FUEL(R.drawable.fuel, "Fuel"), 
	MEDICAL(R.drawable.medical,"Medical"), 
	VEHICLES(R.drawable.ic_launcher, "Vehicles"), 
	PHOTO(R.drawable.ic_launcher, "Photo"), 
	VIDEO(R.drawable.ic_launcher, "Video"), 
	WAYPOINTS(R.drawable.ic_launcher, "Waypoints"), ;

	private int resource;
	private String text;

	ResourceType(final int resourceId, final String description) {
		resource = resourceId;
		text = description;
	}

	public int getResourceId() {
		return resource;
	}

	public String getDescription() {
		return text;
	}
	
	public static CharSequence[] valuesAsCharSequence() {
		CharSequence[] value = new CharSequence[values().length];
		ResourceType[] types = values();
		for (int i = 0; i < types.length; i++) {
			value[i] = types[i].toString();
		}
		return value;
	}


}
