package com.missionse.atlogistics;

import java.util.Random;

import system.ArActivity;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.missionse.atlogistics.augmented.setups.DefaultMultiSetup;
import com.missionse.atlogistics.augmented.setups.ViewResourceSetup;
import com.missionse.atlogistics.maps.DualMapContainer;
import com.missionse.atlogistics.maps.LeftMapsFragment;
import com.missionse.atlogistics.maps.RightMapsFragment;
import com.missionse.atlogistics.resources.Resource;
import com.missionse.atlogistics.resources.ResourceManager;
import com.missionse.atlogistics.resources.ResourceType;

public class ATLogistics extends Activity {

	private SlidingMenu navigationMenu;
	private SlidingMenu filterMenu;

	private RightMapsFragment rightMapsFragment;
	private LeftMapsFragment leftMapsFragment;
	private DualMapContainer mapContainer;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atlogistics);

		createNavigationMenu();
		createFilterMenu();

		mapContainer = new DualMapContainer();
		rightMapsFragment = new RightMapsFragment();
		rightMapsFragment.setMapContainer(mapContainer);
		leftMapsFragment = new LeftMapsFragment();
		leftMapsFragment.setMapContainer(mapContainer);

		showRightMap();
		showLeftMap();

		addDummyData();
	}

	private void createNavigationMenu() {
		navigationMenu = new SlidingMenu(this);
		navigationMenu.setMode(SlidingMenu.LEFT);
		navigationMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		navigationMenu.setShadowWidthRes(R.dimen.menu_shadow_width);
		navigationMenu.setShadowDrawable(R.drawable.shadow_left);
		navigationMenu.setBehindWidthRes(R.dimen.navigation_menu_width);
		navigationMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		navigationMenu.setMenu(R.layout.navigation_menu);

		Fragment leftMenuFragment;
		FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
		leftMenuFragment = new NavigationMenuFragment();
		transaction.replace(R.id.navigation_menu, leftMenuFragment);
		transaction.commit();
	}

	private void createFilterMenu() {
		filterMenu = new SlidingMenu(this);
		filterMenu.setMode(SlidingMenu.RIGHT);
		filterMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		filterMenu.setShadowWidthRes(R.dimen.menu_shadow_width);
		filterMenu.setShadowDrawable(R.drawable.shadow_right);
		filterMenu.setBehindWidthRes(R.dimen.filter_menu_width);
		filterMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		filterMenu.setMenu(R.layout.filter_menu);

		Fragment rightMenuFragment;
		FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
		rightMenuFragment = new FilterMenuFragment();
		transaction.replace(R.id.filter_menu, rightMenuFragment);
		transaction.commit();
	}

	private void addDummyData() {
		Random random = new Random();
		for (int count = 0; count < 20; ++count) {
			Resource tempResource = new Resource(ResourceType.values()[random.nextInt(ResourceType.values().length)]);
			tempResource.setLat(11.05 + random.nextDouble() * 0.1);
			tempResource.setLon(124.367 + random.nextDouble() * 0.1);
			ResourceManager.getInstance().addResource(tempResource);
		}
		ResourceManager.getInstance().notifyResourcesChanged();
	}

	@Override
	public void onNewIntent(final Intent intent) {
		setIntent(intent);
		String action = intent.getAction();
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action) || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
				|| NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			Log.e("something", "got nfc");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.atlogistics, menu);
		return true;
	}

	public void showResourceFinder() {
		navigationMenu.showContent();
		DefaultMultiSetup s = new ViewResourceSetup(this);
		startAR(s);
	}

	private void startAR(final DefaultMultiSetup s) {
		ArActivity.startWithSetup(this, s);
	}

	public void showRightMap() {
		navigationMenu.showContent();
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.right_content, rightMapsFragment).commit();
	}

	public void showLeftMap() {
		navigationMenu.showContent();
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.left_content, leftMapsFragment).commit();
	}
}
