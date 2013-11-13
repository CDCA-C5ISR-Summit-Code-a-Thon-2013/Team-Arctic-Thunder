package com.missionse.atlogistics;

import java.util.Random;

import system.ArActivity;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.missionse.atlogistics.augmented.setups.DefaultMultiSetup;
import com.missionse.atlogistics.augmented.setups.ViewResourceSetup;
import com.missionse.atlogistics.maps.DualMapContainer;
import com.missionse.atlogistics.maps.LeftMapsFragment;
import com.missionse.atlogistics.maps.RightMapsFragment;
import com.missionse.atlogistics.modelviewer.ModelViewerFragment;
import com.missionse.atlogistics.modelviewer.ObjectLoadedListener;
import com.missionse.atlogistics.network.BluetoothService;
import com.missionse.atlogistics.network.DiscoveryFragment;
import com.missionse.atlogistics.resources.Resource;
import com.missionse.atlogistics.resources.ResourceManager;
import com.missionse.atlogistics.resources.ResourceType;

public class ATLogistics extends Activity implements ObjectLoadedListener {

	private static final int REQUEST_ENABLE_BT = 1;

	private SlidingMenu navigationMenu;
	private SlidingMenu filterMenu;

	private RightMapsFragment rightMapsFragment;
	private LeftMapsFragment leftMapsFragment;
	private DualMapContainer mapContainer;

	private ModelViewerFragment modelViewerFragment;

	private int lastLoadedModel = 0;
	private BluetoothService bluetoothService;
	private BroadcastReceiver broadcastReceiver;
	private String connectedDeviceName = "";

	// The Handler that gets information back from the BluetoothNetworkService
	private final Handler messageHandler = new Handler() {
		@Override
		public void handleMessage(final Message msg) {
			switch (msg.what) {
				case BluetoothService.MESSAGE_STATE_CHANGE:
					switch (msg.arg1) {
						case BluetoothService.STATE_CONNECTED:
							getActionBar().setSubtitle("connected to " + connectedDeviceName);
							break;
						case BluetoothService.STATE_CONNECTING:
							getActionBar().setSubtitle("connecting...");
							break;
						case BluetoothService.STATE_LISTEN:
						case BluetoothService.STATE_NONE:
							getActionBar().setSubtitle("not connected");
							break;
					}
					break;
				case BluetoothService.MESSAGE_WRITE:
					byte[] writeBuf = (byte[]) msg.obj;
					// construct a string from the buffer
					String writeMessage = new String(writeBuf);
					break;
				case BluetoothService.MESSAGE_READ:
					byte[] readBuf = (byte[]) msg.obj;
					// construct a string from the valid bytes in the buffer
					String readMessage = new String(readBuf, 0, msg.arg1);
					break;
				case BluetoothService.MESSAGE_DEVICE_NAME:
					// save the connected device's name
					connectedDeviceName = msg.getData().getString(BluetoothService.DEVICE_NAME);
					Toast.makeText(ATLogistics.this, "Connected to " + connectedDeviceName, Toast.LENGTH_SHORT).show();
					break;
				case BluetoothService.MESSAGE_TOAST:
					Toast.makeText(ATLogistics.this, msg.getData().getString(BluetoothService.TOAST).toString(),
							Toast.LENGTH_SHORT).show();
					break;
			}
		}
	};

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

		modelViewerFragment = new ModelViewerFragment();
		modelViewerFragment.registerObjectLoadedListener(this);

		showRightMap();
		showLeftMap();

		addDummyData();

		// Register for broadcasts when a device is discovered and discovery has finished.
		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(final Context context, final Intent intent) {
				String action = intent.getAction();
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					// Get the BluetoothDevice object from the Intent
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					// If it's already paired, skip it, because it's been listed already

					if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
						Fragment fragment = getFragmentManager().findFragmentByTag("dialog");
						if (fragment != null) {
							//Dialog is showing
							DiscoveryFragment discoveryFragment = (DiscoveryFragment) fragment;
							discoveryFragment.addDevice(device.getName() + "\n" + device.getAddress());
						}
					}
				} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
					Fragment fragment = getFragmentManager().findFragmentByTag("dialog");
					if (fragment != null) {
						//Dialog is showing
						DiscoveryFragment deviceList = (DiscoveryFragment) fragment;
						deviceList.onDiscoveryFinished();
					}
				}
			}
		};

		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(broadcastReceiver, filter);
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
		for (int count = 0; count < 6; ++count) {
			Resource tempResource = new Resource(ResourceType.values()[random.nextInt(ResourceType.values().length)]);
			tempResource.setLat(11.05 + random.nextDouble() * 0.1);
			tempResource.setLon(124.367 + random.nextDouble() * 0.1);
			ResourceManager.getInstance().addResource(tempResource);
		}
		for (int count = 0; count < 3; count++) {
			Resource tempResource = new Resource(ResourceType.values()[random.nextInt(ResourceType.values().length)]);
			tempResource.setLat(14.195 + random.nextDouble() * 0.1);
			tempResource.setLon(121.260 + random.nextDouble() * 0.1);
			ResourceManager.getInstance().addResource(tempResource);
		}
		ResourceManager.getInstance().notifyResourcesChanged();
	}

	@Override
	public void onStart() {
		super.onStart();

		// Request that Bluetooth be enabled if not enabled already.
		// setupChat() will be called during onActivityResult()
		if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		} else {
			if (bluetoothService == null) {
				startBluetoothService();
			}
		}
	}

	@Override
	public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if (requestCode == REQUEST_ENABLE_BT) {
			if (resultCode == Activity.RESULT_OK) {
				startBluetoothService();
			} else {
				Toast.makeText(this, "Bluetooth not enabled. Exiting.", Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	private void startBluetoothService() {
		bluetoothService = new BluetoothService(this, messageHandler);
	}

	@Override
	public void onNewIntent(final Intent intent) {
		setIntent(intent);
		String action = intent.getAction();
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action) || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
				|| NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			showLeftMap();
			getFragmentManager().executePendingTransactions();
			showModel(R.raw.helicopter_obj);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.atlogistics, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case R.id.secure_connect_scan:
				showDeviceList(true);
				return true;
			case R.id.discoverable:
				enableDiscovery();
				return true;
		}
		return true;
	}

	private void showDeviceList(final boolean secure) {
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		Fragment previousFragment = getFragmentManager().findFragmentByTag("dialog");
		if (previousFragment != null) {
			transaction.remove(previousFragment);
		}
		transaction.addToBackStack(null).commit();

		// Launch the DeviceListActivity to see devices and do scan
		DiscoveryFragment deviceListFragment = DiscoveryFragment.newInstance(secure);
		deviceListFragment.show(getFragmentManager(), "dialog");
	}

	private void enableDiscovery() {
		if (BluetoothAdapter.getDefaultAdapter().getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 30);
			startActivity(discoverableIntent);
		}
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

	public void setResourceShown(final ResourceType resourceType, final boolean isChecked) {
		rightMapsFragment.setResourceVisibility(resourceType, isChecked);
		leftMapsFragment.setResourceVisibility(resourceType, isChecked);
	}

	public void showModel(final int model) {
		navigationMenu.showContent();

		modelViewerFragment.setModel(model);
		switch (model) {
			case R.raw.helicopter_obj:
				modelViewerFragment.setModelText(R.string.helicopter_text);
				break;
			case R.raw.wooden_crate_ammo_obj:
				modelViewerFragment.setModelText(R.string.ammunition_text);
		}

		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.left_content, modelViewerFragment).commit();
		lastLoadedModel = model;
	}

	@Override
	public void onObjectLoaded() {
		switch (lastLoadedModel) {
			case R.raw.helicopter_obj:
				modelViewerFragment.getAnimator().scaleTo(0.25f, 2000);
				modelViewerFragment.getAnimator().rotateTo(0f, 45f, 0f, 2000);
				break;
			case R.raw.wooden_crate_ammo_obj:
				modelViewerFragment.getAnimator().rotateTo(0f, 45f, 0f, 2000);
				break;
		}
	}

	public void connectDevice(final String address, final boolean secure) {
		// Get the BluetoothDevice object
		BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
		// Attempt to connect to the device
		bluetoothService.connect(device, secure);
	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		if (bluetoothService != null) {
			bluetoothService.start();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(broadcastReceiver);

		if (bluetoothService != null) {
			bluetoothService.stop();
		}
	}

	public boolean sendMessage(final String message) {
		// Check that there's actually something to send
		byte[] data = message.getBytes();
		if (!bluetoothService.write(data)) {
			Toast.makeText(this, "Error: Not connected.", Toast.LENGTH_SHORT).show();
			return false;
		};
		return true;
	}
}
