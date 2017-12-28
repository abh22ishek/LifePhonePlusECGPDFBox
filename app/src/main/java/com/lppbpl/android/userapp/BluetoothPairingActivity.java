/*
 * INTEL CONFIDENTIAL
 *
 * Copyright 2013 Intel Corporation All Rights Reserved.
 *
 * The source code contained or described herein and all documents
 * related to the source code ("Material") are owned by Intel Corporation
 * or its suppliers or licensors. Title to the Material remains with
 * Intel Corporation or its suppliers and licensors. The Material
 * contains trade secrets and proprietary and confidential information of
 * Intel or its suppliers and licensors. The Material is protected by
 * worldwide copyright and trade secret laws and treaty provisions. No
 * part of the Material may be used, copied, reproduced, modified,
 * published, uploaded, posted, transmitted, distributed, or disclosed in
 * any way without Intel's prior express written permission.
 *
 * No license under any patent, copyright, trade secret or other
 * intellectual property right is granted to or conferred upon you by
 * disclosure or delivery of the Materials, either expressly, by
 * implication, inducement, estoppel or otherwise. Any license under
 * such intellectual property rights must be express and approved by
 * Intel in writing.
 */

package com.lppbpl.android.userapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.Toast;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.DeviceData;
import com.lppbpl.Measurement;
import com.lppbpl.Response;
import com.lppbpl.ResponseType;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.util.Util;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

// TODO: Auto-generated Javadoc
/**
 * This is an activity to discover and find the mHealth Device and make a
 * connection between mobile phone and mHealth Device to trasnfere the data.
 */
public class BluetoothPairingActivity extends AppBaseActivity {

	/** The m bluetooth adapter. */
	private BluetoothAdapter mBluetoothAdapter;

	/** The m txt to show. */
	private String mTxtToShow = null;

	// Intent request codes
	/** The Constant REQUEST_CONNECT_DEVICE. */
	private static final int REQUEST_CONNECT_DEVICE = 1;

	/** The Constant REQUEST_ENABLE_BT. */
	private static final int REQUEST_ENABLE_BT = 2;

	/** The Constant BT_DISCOVERY_FINISHED. */
	private static final int BT_DISCOVERY_FINISHED = 100;

	/** The Constant BT_UNABLE_TO_CONNECT. */
	protected static final int BT_UNABLE_TO_CONNECT = 101;

	// Return Intent extra
	/** The Constant EXTRA_DEVICE_ADDRESS. */
	private static final String EXTRA_DEVICE_ADDRESS = "device_address";

	/** The m remote dev mac. */
	private String mRemoteDevMac = null;

	/** The m southfalls found. */
	private boolean mSouthfallsFound;

	/** The m checked parired devices. */
	private boolean mCheckedPariredDevices;

	/** The m is activity finished. */
	private boolean mIsActivityFinished = false;

	/** The create socket. */
	private CreateSocket createSocket = null;

	/** The m device name. */
	private String mDeviceName = null;

	/** The m back pressed. */
	private boolean mBackPressed;

	/** The m foreground. */
	private boolean mForeground = false;

	/**
	 * Called when the activity is first created. * @param savedInstanceState
	 * Bundle
	 *
	 * @param savedInstanceState
	 *            the saved instance state
	 */
	private final static String TAG="BluetoothPairingActivity";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.empty_layout);
		setCustomNoIconTitle(R.string.title_app_name);

		mIsActivityFinished = false;
		Logger.log(Level.DEBUG, TAG, "onCreate()++");
		// Get the local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (null == mBluetoothAdapter) {
			Logger.log(Level.DEBUG, TAG, "Bluethooth does not support. ");
			closeAllActivities();
			return;
		}

		Logger.log(Level.DEBUG, TAG, "Bluethooth enabled ? "
				+ mBluetoothAdapter.isEnabled());

		mTxtToShow = "Pairing...";

		mDeviceName = getStringAppProperty("DEVICE_NAME");// ISHPRD1 name is saved

		final IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(mReceiver, filter);

		// If bluetooth is not enabled already,let's enable it.
		if (null != mBluetoothAdapter && !mBluetoothAdapter.isEnabled()) {
			final Intent enabler = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enabler, REQUEST_ENABLE_BT);
		} else {
			Logger.log(Level.DEBUG, TAG, "Already BT is ON");
			checkForConnection();
		}
		Logger.log(Level.DEBUG, TAG, "onCreate()++");
	}

	/** The num of attempt. */
	private int numOfAttempt = 0;

	/** The connecting with mac address count. */
	protected int connectingWithMacAddressCount = 0;

	/** The m conn paused. */
	protected boolean mConnPaused;

	/**
	 * Trying to pair the device.
	 */
	private void checkForConnection() {

		if (!mProgDialog.isShowing()) {
			showProgressDialog(mTxtToShow);
		}

		Logger.log(Level.DEBUG, TAG, "checkForConnection()++");

		// to avoid infinite search for BT connection
		if (numOfAttempt > 5) {
			Logger.log(Level.DEBUG, TAG, "Num of attemps crossed...");
			numOfAttempt = 0;
			mDeviceFound = false;
			mBtClient.releaseResources();
			mHandler.sendEmptyMessage(BT_UNABLE_TO_CONNECT);
			return;
		}
		Logger.log(Level.DEBUG, TAG, "************Num of attempts*************** "+numOfAttempt);
	//	mBtClient.mIsAppExiting=false;
		numOfAttempt++;
		// Get a set of currently paired devices
		final Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

		mRemoteDevMac = getMacAddress();

		Logger.log(Level.DEBUG, TAG, "****connectDevice*****" + mRemoteDevMac);

		if (null != mRemoteDevMac) {
			// remote_dev_name = mBluetoothAdapter.getName();
			connectDevice(mRemoteDevMac);
			return;
		}
		if (!mCheckedPariredDevices && null != pairedDevices
				&& pairedDevices.size() > 0) {
			Logger.log(Level.DEBUG, TAG, "check in paried devices");
			for (BluetoothDevice device : pairedDevices) {
				Logger.log(Level.DEBUG, TAG,
						"oncreate device name" + device.getName());
				mSouthfallsFound = equalSouthfalls(device);
				if (mSouthfallsFound) {
					mCheckedPariredDevices = true;
					return;
				}
			}
		}

		Logger.log(Level.DEBUG, TAG, "Do discovery for finding south falls");
		// If "" device is not paired already, discover the device.
		doDiscovery();

		Logger.log(Level.DEBUG, TAG, "checkForConnection()--");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		// fix for not pairing when make delay to enter pin in default bt pin
		// screen
		mForeground = false;
		Logger.log(Level.DEBUG, TAG, "onPause Called");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		Logger.log(Level.DEBUG, TAG, "onResume Called");
		// fix for not pairing when make delay to enter pin in default bt pin
		// screen
		mForeground = true;
		if (mConnPaused) {
			checkForConnection();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.lppbpl.android.userapp.AppBaseActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();

		mIsActivityFinished = true;
		dismissProgressDislog();
		// Make sure we're not doing discovery anymore
		Logger.log(Level.DEBUG, TAG, "onDestroy called");
		if (null != mBluetoothAdapter) {
			if (mBluetoothAdapter.isDiscovering()) {
				mBluetoothAdapter.cancelDiscovery();
			}
		} else {
			return;
		}
		// Unregister broadcast listeners
		if (null != mReceiver) {
			this.unregisterReceiver(mReceiver);
		}
	}

	/**
	 * Method onActivityResult.
	 *
	 * @param requestCode
	 *            int
	 * @param resultCode
	 *            int
	 * @param data
	 *            Intent
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Logger.log(Level.DEBUG, TAG, "onActivityResult Request code :" + requestCode);
		if (requestCode == REQUEST_CONNECT_DEVICE) {
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				Logger.log(Level.DEBUG, TAG, "Calling Connect Device");
				connectDevice(data);
			}
		} else if (requestCode == REQUEST_ENABLE_BT) {
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				checkForConnection();
			} else {
				Logger.log(Level.DEBUG, TAG, get(R.string.enable_bluetooth));
				Toast.makeText(this, get(R.string.enable_bluetooth),
						Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	/**
	 * Connects the device.
	 *
	 * @param data
	 *            the data
	 */
	public void connectDevice(Intent data) {
		Logger.log(Level.DEBUG, TAG, "connectDevice called");
		mRemoteDevMac = data.getExtras().getString(EXTRA_DEVICE_ADDRESS);
		connectDevice(mRemoteDevMac);
	}

	/**
	 * Connect the device.
	 *
	 * @param address
	 *            the address
	 */
	public void connectDevice(String address) {
		Logger.log(Level.DEBUG, TAG, "connectDevice called");
		// Get the BLuetoothDevice object
		if (null != mBluetoothAdapter) {

			if (mBluetoothAdapter.isDiscovering()) {
				mBluetoothAdapter.cancelDiscovery();
			}
			mBtClient.setMacAdd(mRemoteDevMac);
			final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
			Logger.log(Level.DEBUG, TAG, "Calling Connect Device ----");
			// Attempt to connect to the device
			if (null != createSocket) {
				createSocket.close();
				createSocket = null;
			}
			createSocket = new CreateSocket(device);
			createSocket.start();
		} else {
			Logger.log(Level.ERROR, TAG, "Adapter is NULL");
		}
	}

	/**
	 * Create a Bluetooth socket.
	 *
	 */
	private class CreateSocket extends Thread {

		/** The socket. */
		private BluetoothSocket socket = null;

		/**
		 * Constructor for CreateSocket.
		 *
		 * @param device
		 *            BluetoothDevice
		 */
		private CreateSocket(BluetoothDevice device) {
			mConnPaused = false;
			try {
				if (null != device) {
					mDeviceFound = true;
					BluetoothSocket temp = null;
					if (Build.VERSION.SDK_INT <= 10) {
						Logger.log(Level.DEBUG, TAG,
								"Socket created using reflection way");
						final Method method = device.getClass()
								.getMethod("createRfcommSocket",
										new Class[] { int.class });
						if (null != method) {
							temp = (BluetoothSocket) method.invoke(device, 3);
						}
					} else {
						Logger.log(Level.DEBUG, TAG,
								"Socket created using standard way");
						temp = device.createRfcommSocketToServiceRecord(Constants.ACCESSORY_UUID);
					}
					socket = temp;
				}
			} catch (IOException e) {
				Logger.log(Level.ERROR, TAG, "" + e);
			} catch (IllegalAccessException e) {
				Logger.log(Level.ERROR, TAG, "" + e);
			} catch (IllegalArgumentException e) {
				Logger.log(Level.ERROR, TAG, "" + e);
			} catch (InvocationTargetException e) {
				Logger.log(Level.ERROR, TAG, "" + e);
			} catch (NoSuchMethodException e) {
				Logger.log(Level.ERROR, TAG, "" + e);
			}

		}

		/**
		 * Method run.
		 *
		 * @see Runnable#run()
		 */
		@Override
		public void run() {
			mBtClient.setBluetoothSocket(socket);
		}

		/**
		 * Close.
		 */
		public void close() {
			if (null != socket) {
				try {
					socket.close();
				} catch (IOException e) {
					Logger.log(Level.ERROR, TAG, "Error => " + e.getMessage());
				} finally {
					socket = null;
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.lppbpl.android.userapp.AppBaseActivity#onPositiveButtonClick()
	 */
	@Override
	public void onPositiveButtonClick() {
		if (mDialogType == Constants.SHOW_BT_PAIR_SKIP_DIALOGE
				&& null != mRemoteDevMac) {
			if (!mBackPressed) {
				if(mAppModel.getTokenKey() != null){
					/* Check for TPA version change */
					Intent intent = new Intent(BluetoothPairingActivity.this, TPAActivity.class);
					intent.putExtra(TPAActivity.KEY_CALL_INFO_API, true);
					intent.putExtra(TPAActivity.KEY_FROM_ACTIVITY, TPAActivity.FROM_BLUETOOTHPAIRING_ACTIVITY);
					startActivity(intent);
					finish();
				} else{
					Logger.log(Level.DEBUG, TAG, "start Activity for MainMenu");
					final Intent intent = new Intent(this, MainMenuActivity.class);
					intent.putExtra("skip_pair", true);
					startActivity(intent);
					finish();
				}
			} else {
				onBackPressed();
			}
		} else {
			mBtClient.releaseResources();
			finish();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		mBtClient.releaseResources();
		super.onBackPressed();
	}

	/**
	 * Handler will handle the data received while pairing device and informs
	 * the user for the errors.
	 */

	private final Handler mHandler = new MyHandler(this);

	/**
	 * The Class MyHandler.
	 */
	private static class MyHandler extends Handler {

		/** The m activity. */
		private final WeakReference<BluetoothPairingActivity> mActivity;

		/**
		 * Constructor for MyHandler.
		 *
		 * @param activity
		 *            BluetoothPairingActivity
		 */
		private MyHandler(BluetoothPairingActivity activity) {
			mActivity = new WeakReference<BluetoothPairingActivity>(activity);
		}

		/**
		 * Method handleMessage.
		 *
		 * @param msg
		 *            android.os.Message
		 */
		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			final BluetoothPairingActivity parent = mActivity.get();
			if (parent.mIsActivityFinished) {
				return;
			}

			if (msg.what == Constants.CONNECTION_ESTABLISHED) {
				Logger.log(Level.DEBUG, TAG, "connection established...");
				if (null != parent.mRemoteDevMac) {
					parent.saveMacAddress(parent.mRemoteDevMac);
				}
				if (parent.mCheckedPariredDevices) {
					parent.mCheckedPariredDevices = false;
				}
				Logger.log(Level.DEBUG, TAG, "sending device data request...");

				// send the device data request to lifephone plus device
				// 1st condition is to send request
				parent.mAppCrl.sendDeviceDataRequest();

				parent.mTxtToShow = parent.get(R.string.waiting_for_device_resp);
				parent.mProgDialog.setMessage(parent.mTxtToShow);

				Logger.log(Level.DEBUG, TAG, "sent the device data request...");

			} else if (msg.what == Constants.DEVICE_DATA_RECEIVED) {
				Logger.log(Level.DEBUG, TAG, "Device data received.");
				final DeviceData deviceData = parent.mAppModel.getDeviceData();
				Logger.log(Level.DEBUG, TAG, "Default PIN STATUS="
						+ (null != deviceData && deviceData.hasBtDefaultPin()));
				if (null != deviceData && deviceData.hasBtDefaultPin()) {
					final long defaultPin = deviceData.getBtDefaultPin();
					final Intent btPinChangeAct = new Intent(parent,
							BluetoothPinChangeActivity.class);
					btPinChangeAct.putExtra("mDefaultPin",
							Long.toString(defaultPin));
					parent.startActivity(btPinChangeAct);
					parent.finish();
				} else {
					Logger.log(Level.DEBUG, TAG, "Sending Config message");
					parent.mBtClient.sendCommand(Util.getConfigMessage());
				}
			} else if (msg.what == Constants.CONFIG_ACK_RECEIVED) {
				Logger.log(Level.DEBUG, TAG, "Constants.CONFIG_ACK_RECEIVED");
				parent.dismissProgressDislog();
				parent.mAppCrl.setConfigAckRecived(true);
				final DeviceData deviceData = parent.mAppModel.getDeviceData();
				if (null != deviceData && deviceData.hasActivityRunning()) {
					parent.mActModel.setActivityRunningInBackground(true);
					parent.mAppModel.createPendingRecord(Measurement.ACT);
					Logger.log(Level.DEBUG, TAG,
							"deviceData.hasActivityRunning() "
									+ "so pending record created for activity");
				}

				if (null != deviceData) {
					// SET THE DEVICEID TO PIN MODEL CLASS Its required, when we
					// start uploading ecg
					// Load saved data to pin model
					parent.mPinModel.setDeviceUid(deviceData.getDeviceUid());
					Logger.log(Level.DEBUG, TAG, "set the deviceID to pin model:" + deviceData.getDeviceUid());

					final String storedDeviceId = parent.loadDeviceId();
					final boolean isDifferentDevice = !storedDeviceId
							.equals(parent.mPinModel.getDeviceUid());
					/*
					 * If user changed the device; take the user to login screen
					 * to update the new deviceId to cloud.
					 */
					Intent intent = null;
					if (isDifferentDevice || parent.isFreshApp()) {
						parent.clearLoginToken();
						parent.saveFreshAppState(false);
						intent = new Intent(parent, MainMenuActivity.class);
						intent.putExtra("deviceid_mismatch", true);
						intent.putExtra("enable_skip", true);
						parent.startActivity(intent);
						parent.finish();
					} else if(parent.mAppModel.getTokenKey() != null){
						/* Check for TPA version change */
						intent = new Intent(parent, TPAActivity.class);
						intent.putExtra(TPAActivity.KEY_CALL_INFO_API, true);
						intent.putExtra(TPAActivity.KEY_FROM_ACTIVITY, TPAActivity.FROM_BLUETOOTHPAIRING_ACTIVITY);
						parent.startActivity(intent);
						parent.finish();
					} else {
						Logger.log(Level.DEBUG, TAG,
								"start Activity for MainMenu");
						intent = new Intent(parent, MainMenuActivity.class);
						parent.startActivity(intent);
						parent.finish();
					}
				}

			} else if (msg.what == Constants.CONNECTION_ERROR) {
				Logger.log(Level.DEBUG, TAG, "--CONNECTION_ERROR--");
				parent.mDeviceFound = false;
				// fix for not pairing when make delay to enter pin in default
				// bt pin screen
				parent.mConnPaused = !parent.mForeground;
				if (parent.mForeground) {
					if (parent.connectingWithMacAddressCount > 1
							&& null != parent.getMacAddress()) {
						parent.connectingWithMacAddressCount = 0;
						parent.mHandler.sendEmptyMessage(BT_DISCOVERY_FINISHED);
					} else {
						parent.connectingWithMacAddressCount++;
						parent.checkForConnection();
					}
				}
			} else if (msg.what == BT_DISCOVERY_FINISHED) {
				parent.mDialogType = Constants.SHOW_INFO_DIALOGE;
				if (!parent.mDeviceFound) {
					parent.dismissProgressDislog();
					if (null != parent.mRemoteDevMac) {
						parent.mDialogType = Constants.SHOW_BT_PAIR_SKIP_DIALOGE;
						parent.showAlertDialog(R.drawable.ic_dialog_info,
								parent.get(R.string.information),
								parent.get(R.string.skip_pair_southfalls),
								parent.get(R.string.OK), null, false);
						parent.mBackPressed = false;
						parent.mDialog.setOnKeyListener(new OnKeyListener() {

							@Override
							public boolean onKey(DialogInterface arg0,
									int arg1, KeyEvent arg2) {
								parent.mBackPressed = arg2.getKeyCode() == KeyEvent.KEYCODE_BACK;
								return false;
							}
						});
					} else {
						parent.showAlertDialog(R.drawable.ic_dialog_error,
								parent.get(R.string.bluetooth),
								parent.get(R.string.unable_to_find_southfalls),
								parent.get(R.string.OK), null, false);
					}
				}
			} else if (msg.what == BT_UNABLE_TO_CONNECT
					|| msg.what == Constants.CONNECTION_TIMEOUT) {
				parent.mDialogType = Constants.SHOW_INFO_DIALOGE;
				parent.dismissProgressDislog();
				parent.showAlertDialog(R.drawable.ic_dialog_error,
						parent.get(R.string.bluetooth),
						parent.get(R.string.unable_to_connect_southfalls),
						parent.get(R.string.OK), null, false);
			}
		}

	};

	/** The m device found. */
	private boolean mDeviceFound = false;

	/**
	 * after discovering the device try to connect the device.
	 *
	 * @param device
	 *            the device
	 * @return boolean
	 */
	private boolean equalSouthfalls(BluetoothDevice device) {
		boolean found = false;
		if (null != device && null != device.getName()) {
			if (device.getName().equalsIgnoreCase(mDeviceName)) {
				if (mBluetoothAdapter.isDiscovering()) {
					mBluetoothAdapter.cancelDiscovery();
				}

				final String info = device.getAddress();
				final String address = info.substring(info.length() - 17);

				// Create the result Intent and include the MAC address
				final Intent intent = new Intent();
				intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

				connectDevice(intent);
				found = true;
			}
		} else {
			Logger.log(Level.INFO, TAG, "Southfalls is not paired yet");
		}
		return found;
	}

	/**
	 * Start device discover with the BluetoothAdapter.
	 */
	private void doDiscovery() {
		Logger.log(Level.DEBUG, TAG, "doDiscovery()");

		// If we're already discovering, stop it
		if (mBluetoothAdapter.isDiscovering()) {
			mBluetoothAdapter.cancelDiscovery();
		}

		mDeviceFound = false;
		// Request discover from BluetoothAdapter
		mBluetoothAdapter.startDiscovery();
	}

	/** BroadcastReceiver to find the Paired device. */
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			Logger.log(Level.DEBUG, TAG, "BroadcastReceiver action== " + action);
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				final BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// If it's already paired, skip it, because it's been listed
				// already
				// commented to handle bug- not pairing when make delay to enter
				// pin in default bt pin screen
				// if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
				Logger.log(
						Level.DEBUG,
						TAG,
						"BroadcastReceiver ACTION_FOUND device name"
								+ device.getName());
				equalSouthfalls(device);
				// }
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				Logger.log(Level.DEBUG, TAG,
						"BroadcastReceiver ACTION_DISCOVERY_FINISHED ");
				Logger.log(Level.DEBUG, TAG, "BroadcastReceiver numOfAttempt "
						+ numOfAttempt);
				// fix for not pairing when make delay to enter pin in default
				// bt pin screen
				if (mForeground) {
					BluetoothPairingActivity.this.mHandler.sendEmptyMessage(BT_DISCOVERY_FINISHED);
				}
			}
		}
	};

	/**
	 * saves the mac address of the device in sharedpreference.
	 *
	 * @param macAddress
	 *            the mac address
	 */
	public void saveMacAddress(String macAddress) {
		Logger.log(Level.INFO, TAG, "saveMacAddress() " + macAddress);
		final SharedPreferences btPref = getSharedPreferences("device_mac",
				MODE_PRIVATE);
		final SharedPreferences.Editor prefsEditor = btPref.edit();
		prefsEditor.putString("device_mac_add", macAddress);
		prefsEditor.commit();
	}

	/**
	 * clear the mac address of the device.
	 */
	public void clearMacAddress() {
		Logger.log(Level.INFO, TAG, "clearMacAddress()");
		saveMacAddress(null);
	}

	/**
	 * get the mac address of the device.
	 *
	 * @return String
	 */
	public String getMacAddress() {
		final SharedPreferences btPref = getSharedPreferences("device_mac",
				MODE_PRIVATE);
		return btPref.getString("device_mac_add", null);
	}

	/**
	 * Method dataReceived.
	 *
	 * @param response
	 *            Response
	 * @see com.lppbpl.android.userapp.listener.SfBTDataListener#dataReceived(Response)
	 */
	@Override
	public void dataReceived(Response response) {
		super.dataReceived(response);

		if (response.hasDevData()) {
			try {
				if (Logger.isInfoEnabled()) {
					Logger.log(Level.INFO, TAG, "Device Data response received");
				}
				mAppModel.setDeviceData(response.getDevData());
				final String oldDeviceId = loadDeviceId();
				if (0 == oldDeviceId.compareTo("")) {
					Logger.log(Level.INFO, TAG, "Old Device id="+oldDeviceId);
					saveDeviceId(response.getDevData().getDeviceUid());
					saveFreshAppState(true);
				}
				mHandler.sendEmptyMessage(Constants.DEVICE_DATA_RECEIVED);
			} catch (Exception e) {
				Logger.log(Level.ERROR, TAG, "Device Data received, Error => "
						+ e.getMessage());
			}
		} else if (response.getResponseType() == ResponseType.ACK && 0 == response.getMeasurementType()) {
			Logger.log(Level.DEBUG, TAG, "Config message ACK received.");
			if (!mAppCrl.isConfigAckRecived()) {
				mHandler.sendEmptyMessage(Constants.CONFIG_ACK_RECEIVED);
			}
		}
	}

	/**
	 * Method saveDeviceId.
	 *
	 * @param newDeviceId
	 *            String
	 */
	private void saveDeviceId(String newDeviceId) {
		Logger.log(Level.INFO, TAG, "saveDeviceId()" + newDeviceId);
		final SharedPreferences saveUserData = getSharedPreferences(Constants.USER_DETAILS_DB, MODE_PRIVATE);
		final SharedPreferences.Editor editor = saveUserData.edit();
		editor.putString(Constants.SAVE_DEVICE_ID, newDeviceId);
		final DeviceData data = mAppModel.getDeviceData();
		if (null != data) {
			editor.putString(Constants.SAVE_DEVICE_VERSION, data.getFirmwareVersion());
		}
		editor.commit();
	}

	/**
	 * Method sendEmptyMessage.
	 *
	 * @param what
	 *            int
	 * @see com.lppbpl.android.userapp.listener.SfBTDataListener#sendEmptyMessage(int)
	 */
	@Override
	public void sendEmptyMessage(int what) {
		super.sendEmptyMessage(what);
		if (null != mHandler) {
			mHandler.sendEmptyMessage(what);
		}
	}

	/**
	 * Load the the device state to check for fresh app or not.
	 *
	 * @return boolean
	 */
	protected boolean isFreshApp() {
		final SharedPreferences saveUserData = getSharedPreferences(Constants.USER_DETAILS_DB, MODE_PRIVATE);
		final boolean isFreshApp = saveUserData.getBoolean(Constants.SAVE_FRESH_APP_STATE, false);
		Logger.log(Level.DEBUG, TAG, "isFreshApp =" + isFreshApp);
		return isFreshApp;
	}

	/**
	 * save the the device state to check for fresh app or not.
	 *
	 * @param isFreshDevice
	 *            boolean
	 */
	protected void saveFreshAppState(boolean isFreshDevice) {
		Logger.log(Level.INFO, TAG, "saveDeviceId()" + isFreshDevice);
		final SharedPreferences saveUserData = getSharedPreferences(
				Constants.USER_DETAILS_DB, MODE_PRIVATE);
		final SharedPreferences.Editor editor = saveUserData.edit();
		editor.putBoolean(Constants.SAVE_FRESH_APP_STATE, isFreshDevice);
		editor.commit();
	}
}
