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

import java.lang.ref.WeakReference;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.BluetoothConfig;
import com.lppbpl.ConfigMessage;
import com.lppbpl.Response;
import com.lppbpl.ResponseType;
import com.lppbpl.SFMessaging;
import com.lppbpl.ConfigMessage.ConfigType;
import com.lppbpl.SFMessaging.MessageType;
import com.lppbpl.android.userapp.R;
import com.lppbpl.android.userapp.constants.Constants;

// TODO: Auto-generated Javadoc
/**
 * THis is an Activity to change the default PIN to new PIN.
 */
public class BluetoothPinChangeActivity extends AppBaseActivity {

	/** The Constant TAG. */
	private final static String TAG = BluetoothPinChangeActivity.class
			.getName();

	/** The m pin edit txt. */
	private EditText mPinEditTxt = null;

	/** The m save button. */
	private Button mSaveButton = null;

	/** The m default pin. */
	private String mDefaultPin = null;

	/** The m dialage msg. */
	private String mDialageMsg = null;

	/** The is shutdown ack. */
	private boolean isShutdownACK = false;

	/** The is config ack. */
	private boolean isConfigAck = false;

	/**
	 * Method onCreate.
	 *
	 * @param savedInstanceState
	 *            Bundle
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.log(Level.DEBUG, TAG, "onCreate()");
		setContentView(R.layout.bluetooth_pin_change);
		setCustomTitle(R.string.title_pin_change);

		// Assign value for default value
		mDefaultPin = getIntent().getStringExtra("mDefaultPin");

		mPinEditTxt = (EditText) findViewById(R.id.newPinEditView);

		mSaveButton = (Button) findViewById(R.id.btn_menu_positive);
		mSaveButton.setText(R.string.save);
		mSaveButton.setOnClickListener(mClickListener);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		isShutdownACK = true;
		// send shutdown message to device
		mAppCrl.sendShutdownMsg();
		closeAllActivities();
	}

	/** click listener for the save button- resets the new device pin. */
	final private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			Logger.log(Level.DEBUG, TAG, "button clicked");
			if (isValidNewPin()) {
				// Send the changed Pin to accessory to update.
				final String newPin = mPinEditTxt.getText().toString().trim();
				long btPin = 0;
				try {
					btPin = Long.parseLong(newPin);
				} catch (NumberFormatException e) {
					Logger.log(Level.ERROR, TAG, "" + e);
				}

				isConfigAck = true;
				final BluetoothConfig bluetoothConfig = BluetoothConfig
						.newBuilder().setConfigType(ConfigType.Bluetooth)
						.setBtPin(btPin).build();

				final ConfigMessage confMsg = ConfigMessage.newBuilder()
						.setConfigType(ConfigType.Bluetooth)
						.setBluetoothConfig(bluetoothConfig).build();

				final SFMessaging messaging = SFMessaging.newBuilder()
						.setSfMsgType(MessageType.ConfigMsg)
						.setSfConfigMsg(confMsg).build();

				sendCommand(messaging);

				mSaveButton.setEnabled(false);
			} else {
				showAlertDialog(get(R.string.title_pin_change), mDialageMsg,
						get(R.string.OK), null, true);
			}
		}
	};

	/**
	 * validates the entered pin is proper or not.
	 *
	 * @return boolean
	 */
	protected boolean isValidNewPin() {
		boolean valid = true;
		final String newPin = mPinEditTxt.getText().toString().trim();
		Logger.log(Level.DEBUG, TAG, "new Pin=" + newPin + ",default pin="
				+ mDefaultPin);
		mDialageMsg = get(R.string.please_enter_pin);
		if (null != newPin && 6 != newPin.length()) {
			mDialageMsg = get(R.string.please_enter_pin);
			valid = false;
		} else if (null != newPin && 0 == mDefaultPin.compareTo(newPin)) {
			mDialageMsg = get(R.string.please_cant_default);
			valid = false;
		}
		return valid;
	}

	/**
	 * Handler to handle the information back from device while changing the bt
	 * pin.
	 */
	final private MyHandler mHandler = new MyHandler(this);

	/**
	 * The Class MyHandler.
	 */
	private static class MyHandler extends Handler {

		/** The m activity. */
		private final WeakReference<BluetoothPinChangeActivity> mActivity;

		/**
		 * Constructor for MyHandler.
		 *
		 * @param activity
		 *            BluetoothPinChangeActivity
		 */
		private MyHandler(BluetoothPinChangeActivity activity) {
			mActivity = new WeakReference<BluetoothPinChangeActivity>(activity);
		}

		/**
		 * Method handleMessage.
		 *
		 * @param msg
		 *            android.os.Message
		 */
		public void handleMessage(android.os.Message msg) {
			final BluetoothPinChangeActivity parent = mActivity.get();
			if (msg.what == Constants.CONFIG_ACK_RECEIVED && parent.isConfigAck) {
				Logger.log(Level.DEBUG, TAG,
						"Constants.BT_PIN_CHANGE_ACK_RECEIVED");
				parent.mDialogType = Constants.CONFIG_ACK_RECEIVED;
				parent.mAppCrl.setConfigAckRecived(true);
				parent.showAlertDialog(parent.get(R.string.title_pin_change),
						parent.get(R.string.pin_successfully_changed),
						parent.get(R.string.OK), null, false);
			} else if (msg.what == Constants.SHUTDOWN_ACK_RECEIVED
					|| msg.what == Constants.SHUTDOWN_TIMEOUT) {
				Logger.log(Level.DEBUG, TAG, "Constants.SHUTDOWN_ACK_RECEIVED");
				parent.closeAllActivities();
			}
		};
	};

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.lppbpl.android.userapp.AppBaseActivity#onPositiveButtonClick()
	 */
	@Override
	protected void onPositiveButtonClick() {
		// sending shutdown msg to device
		if (mDialogType == Constants.CONFIG_ACK_RECEIVED) {
			isShutdownACK = true;
			mAppCrl.sendShutdownMsg();
			mHandler.sendEmptyMessageDelayed(Constants.SHUTDOWN_TIMEOUT,
					Constants.SHUTDOWN_TIMEOUT_DELAY);
		}
	};

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
		Logger.log(Level.ERROR, TAG, response.toString());
		if (response.getResponseType() == ResponseType.ACK) {
			Logger.log(Level.ERROR, TAG, "ack received");
			mHandler.sendEmptyMessage(isShutdownACK ? Constants.SHUTDOWN_ACK_RECEIVED
					: Constants.CONFIG_ACK_RECEIVED);
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
		mHandler.removeMessages(Constants.SHUTDOWN_TIMEOUT);
	}

}
