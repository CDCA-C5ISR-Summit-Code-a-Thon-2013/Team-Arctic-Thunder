package com.missionse.atlogistics.resources;

import java.util.LinkedList;
import java.util.List;

public class ResourceManager {
	
	public static ResourceManager instance;
	
	private List<Resource> resources;
	
	public ResourceManager getInstance(){
		if(instance == null){
			instance = new ResourceManager();
		}
		return instance;
	}
	
	private ResourceManager(){
		resources = new LinkedList<Resource>();
		
	}
	

	public List<Resource> getResources(){
		return this.resources;
	}
	
	public void addResource(Resource r){
		resources.add(r);
	}
	
	public void removeResource(Resource r){
		resources.remove(r);
	}
	
	


}
