package com.missionse.atlogistics.resources;

public class Resource {

	private ResourceType type;
	private String resourceName = "";
	private String flavorText = "";
	private double lat = 0;
	private double lon = 0;

	public Resource(final String name, final ResourceType resourceType, final double latitude, final double longitude, final String text) {
		resourceName = name;
		type = resourceType;
		lat = latitude;
		lon = longitude;
		flavorText = text;
	}

	public ResourceType getType() {
		return type;
	}

	public void setType(final ResourceType type) {
		this.type = type;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(final String name) {
		resourceName = name;
	}

	public String getFlavorText() {
		return flavorText;
	}

	public void setFlavorText(final String text) {
		flavorText = text;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(final double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(final double lon) {
		this.lon = lon;
	}
}
