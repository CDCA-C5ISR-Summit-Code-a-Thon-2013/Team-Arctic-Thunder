package com.missionse.atlogistics.resources;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import android.content.res.Resources;

public class ResourceManager {

	private static ResourceManager instance;

	private List<Resource> resources;

	public static ResourceManager getInstance(){
		if(instance == null){
			instance = new ResourceManager();
		}
		return instance;
	}

	private ResourceManager(){
		resources = new LinkedList<Resource>();
		resources.add(new Resource(ResourceType.MEDICAL));
		Random m = new Random();
		for(int x = 0; x < 20; x++){
			resources.add(new Resource(ResourceType.values()[m.nextInt(ResourceType.values().length)]));
		}
		
	}


	public List<Resource> getResources(){
		return this.resources;
	}

	public void addResource(final Resource r){
		resources.add(r);
	}

	public void removeResource(final Resource r){
		resources.remove(r);
	}
}
