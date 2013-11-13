package com.missionse.atlogistics.network;

import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class ConnectThread extends Thread {

	private BluetoothAdapter adapter;
	private BluetoothService bluetoothService;

	private BluetoothSocket socket;
	private final BluetoothDevice remoteDevice;

	public ConnectThread(final BluetoothService service, final BluetoothDevice device, final boolean secure) {
		bluetoothService = service;
		adapter = BluetoothAdapter.getDefaultAdapter();
		remoteDevice = device;

		// Get a BluetoothSocket for a connection with the given BluetoothDevice
		try {
			if (secure) {
				socket = device.createRfcommSocketToServiceRecord(BluetoothService.MY_UUID_SECURE);
			} else {
				socket = device.createInsecureRfcommSocketToServiceRecord(BluetoothService.MY_UUID_INSECURE);
			}
		} catch (IOException e) {
		}
	}

	@Override
	public void run() {
		// Always cancel discovery because it will slow down a connection
		adapter.cancelDiscovery();

		// Make a connection to the BluetoothSocket
		try {
			// This is a blocking call and will only return on a
			// successful connection or an exception
			socket.connect();
		} catch (IOException e) {
			// Close the socket
			try {
				socket.close();
			} catch (IOException e2) {
			}
			bluetoothService.onConnectionFailed();
			return;
		}

		// Start the connected thread
		bluetoothService.onConnectionSuccessful(socket, remoteDevice);
	}

	public void cancel() {
		try {
			socket.close();
		} catch (IOException e) {
		}
	}
}
