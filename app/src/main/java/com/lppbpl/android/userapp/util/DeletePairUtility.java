/*
 *
 */
package com.lppbpl.android.userapp.util;

import java.lang.reflect.Method;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.android.userapp.constants.Constants;

// TODO: Auto-generated Javadoc
/**
 * The Class DeletePairUtility.
 */
public class DeletePairUtility {

	/** The Constant TAG. */
	private static final String TAG = DeletePairUtility.class.getName();

	/**
	 * Method removePairedDevice.
	 * @param context Context
	 * @param deviceName String
	 */
	public static void removePairedDevice(Context context, String deviceName) {
		Logger.log(Level.INFO, TAG, "removePairedDevice()");
		saveMacAddress(context, null);
		clearDeviceId(context);
		// remove southfalls from paired list
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		if (bluetoothAdapter != null) {
			Set<BluetoothDevice> pairedDevices = bluetoothAdapter
					.getBondedDevices();

			if (pairedDevices != null && pairedDevices.size() > 0) {
				for (BluetoothDevice device : pairedDevices) {
					if (device != null && device.getName() != null) {
						if (device.getName().equalsIgnoreCase(deviceName)) {
							unpairDevice(device);
							Logger.log(Level.ERROR, TAG,
									"unpaired south fall device...");
						}
					}
				}
			}
		}
	}

	/**
	 * saves the mac address of the device in sharedpreference.
	 *
	 * @param context Context
	 * @param macAddress the mac address
	 */
	public static void saveMacAddress(Context context, String macAddress) {
		Logger.log(Level.INFO, TAG, "saveMacAddress()");
		SharedPreferences btPref = context.getSharedPreferences("device_mac",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor prefsEditor = btPref.edit();
		prefsEditor.putString("device_mac_add", macAddress);
		prefsEditor.commit();
	}

	/**
	 * Unpair the paired device.
	 *
	 * @param device the device
	 */
	private static void unpairDevice(BluetoothDevice device) {
		Logger.log(Level.INFO, TAG, "unpairDevice()");
		try {
			Method m = device.getClass()
					.getMethod("removeBond", (Class[]) null);
			m.invoke(device, (Object[]) null);
		} catch (Exception e) {
			Logger.log(Level.DEBUG, TAG, e.getMessage());
		}
	}

	/**
	 * clear the paired device.
	 *
	 * @param context Context
	 * @param newDeviceId String
	 */
	private static void clearDeviceId(Context context) {
		SharedPreferences saveUserData = context.getSharedPreferences(
				Constants.USER_DETAILS_DB, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = saveUserData.edit();
		editor.remove(Constants.SAVE_DEVICE_ID);
		editor.remove(Constants.SAVE_DEVICE_VERSION);
		editor.commit();
	}
}
