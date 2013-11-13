package com.missionse.atlogistics.maps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.missionse.atlogistics.resources.Resource;

public class ResourceMarker {

	private GoogleMap googleMap;
	private Context context;

	private Marker marker;
	private Circle circle;

	ResourceMarker(final GoogleMap map, final Resource resource, final Context context) {
		googleMap = map;
		this.context = context;
		//marker = googleMap.addMarker(new MarkerOptions()
		//		.icon(BitmapDescriptorFactory.fromResource(resource.getType().getResourceId()))
		//		.title(resource.getType().toString()).position(new LatLng(resource.getLat(), resource.getLon())));
		marker = googleMap.addMarker(new MarkerOptions()
						.icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap(resource.getType().getResourceId())))
						.title(resource.getType().toString()).position(new LatLng(resource.getLat(), resource.getLon())));
		circle = googleMap.addCircle(new CircleOptions().center(new LatLng(resource.getLat(), resource.getLon()))
				.radius(30).fillColor(Color.RED).strokeWidth(0));
	}
	
	private Bitmap resizeBitmap(int resourceID){
		Bitmap b= BitmapFactory.decodeResource(context.getResources(), resourceID);
		Bitmap bhalfsize=Bitmap.createScaledBitmap(b, (int)(b.getWidth()/2),(int)(b.getHeight()/2), false);
		
		return bhalfsize;
	}

	public void setVisible(final boolean visible) {
		marker.setVisible(visible);
		circle.setVisible(visible);
	}

	public Marker getMarker() {
		return marker;
	}

}
