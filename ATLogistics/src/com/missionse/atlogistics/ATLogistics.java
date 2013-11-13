package com.missionse.atlogistics;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class ATLogistics extends Activity {

	private SlidingMenu navigationMenu;
	private SlidingMenu filterMenu;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atlogistics);

		createNavigationMenu();
		createFilterMenu();
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

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.atlogistics, menu);
		return true;
	}

}
