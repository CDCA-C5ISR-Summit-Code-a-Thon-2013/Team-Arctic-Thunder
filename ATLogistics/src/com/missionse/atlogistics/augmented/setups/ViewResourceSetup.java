package com.missionse.atlogistics.augmented.setups;

import gl.GLFactory;
import gl.animations.AnimationFaceToCamera;
import gl.scenegraph.MeshComponent;
import gl.scenegraph.Shape;
import gui.GuiSetup;

import java.util.List;
import java.util.Random;

import markerDetection.MarkerObjectMap;
import util.Vec;
import v2.simpleUi.util.IO;
import worldData.Obj;
import worldData.World;
import android.app.Activity;
import android.graphics.Typeface;
import android.widget.TextView;

import com.missionse.atlogistics.resources.Resource;
import com.missionse.atlogistics.resources.ResourceManager;

public class ViewResourceSetup extends DefaultMultiSetup {

	public ViewResourceSetup(final Activity parent) {
		super(parent, false);

	}

	@Override
	public void _a4_addDefaultMarkers(final MarkerObjectMap markerObjectMap) {
		// TODO Auto-generated method stub

	}

	@Override
	public void _b2_addDefaultWorldObjects(final World world) {
		List<Resource> resources = ResourceManager.getInstance().getResources();

		for (Resource r : resources) {
			MeshComponent m = GLFactory.getInstance().newTexturedSquare(r.getResourceName(),
					IO.loadBitmapFromId(getActivity(), r.getType().getResourceId()));

			m.addChild(new AnimationFaceToCamera(getCamera(), 0.5f));
			m.setScale(new Vec(3, 3, 3));

			TextView v = new TextView(getActivity());
			v.setTypeface(null, Typeface.BOLD);
			StringBuilder sb = new StringBuilder();
			sb.append(r.getResourceName() + ": ");
			//sb.append("(" + r.getCount() + "/" + r.getFillCount() + ")");
			v.setText(sb.toString());

			Random ran = new Random();

			//MeshComponent mesh = GLFactory.getInstance().newTexturedSquare(
			//		"txtView" + r.getResourceName() + r.getCount() + r.getFillCount() + ran.nextInt(100) , 
			//		util.IO.loadBitmapFromView(v), 
			//		2);
			//mesh.addAnimation(new AnimationFaceToCamera(getCamera()));

			final MeshComponent itemMesh = new Shape();
			itemMesh.addChild(m);
			//itemMesh.addChild(mesh);
			itemMesh.addAnimation(new AnimationFaceToCamera(getCamera()));

			Obj o = new Obj();
			o.setComp(itemMesh);
			o.setPosition(Vec.getNewRandomPosInXYPlane(getCamera().getPosition(), 10, 40));
			//o.setMyLatitude(Vec.);
			//o.setMyLongitude(r.getLon());
			//o.setMyAltitude(getCamera().getGPSPositionAsGeoObj().getAltitude());
			world.add(o);
		}

	}

	@Override
	public void _e3_addElementsToUi(final GuiSetup guiSetup, final Activity activity) {
		// TODO Auto-generated method stub

	}

}
