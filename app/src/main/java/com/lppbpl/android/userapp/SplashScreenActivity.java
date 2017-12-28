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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.android.userapp.R;
import com.lppbpl.android.userapp.constants.Constants;

// TODO: Auto-generated Javadoc
/**
 * This Activity is to display the Splash screen when application launched.
 *
 */
public class SplashScreenActivity extends AppBaseActivity implements
		OnTouchListener {

	/** The m display timer. */
	private Thread mDisplayTimer = null;

	/**
	 * Method onCreate.
	 *
	 * @param bundle
	 *            Bundle
	 */
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.splash);

		final RelativeLayout splashLayout = (RelativeLayout) findViewById(R.id.splashLayout);

		splashLayout.setOnTouchListener(this);

		loadAppInfo();

		loadUserData();

		/*
		 * Load the saved login token to avoid user to login again
		 */
		final String loginToken = getLoginToken();
		mAppModel.setTokenKey(loginToken);
		mPinModel
				.setLoginForSessionSuccess((null != loginToken) ? true : false);

		final String userRole = getUserRole();
		mAppModel.setUserRole(userRole);

		final String userUniqueId = getUserUniqueId();
		mAppModel.setUserUniqueId(userUniqueId);

		mDisplayTimer = new Thread() {
			@Override
			public void run() {
				try {
					sleep(3000);
				} catch (InterruptedException e) {
					Logger.log(Level.ERROR, TAG, "" + e);
				}
				moveToNextScreen();
			}
		};
		mDisplayTimer.start();

	}

	/**
	 * Loads the app information from the application model.
	 */
	private void loadAppInfo() {
		Logger.log(Level.DEBUG, TAG, "Load App Info ++");
		mAppModel
				.setDefaultServerAddress(getStringAppProperty("SERVER_ADDRESS"));

		mPinModel.setServerAddress(getSharedPreferences(
				Constants.DEVICE_CONFIG, MODE_PRIVATE).getString(
				"dev_config_url", mAppModel.getDefaultServerAddress()));

		Logger.log(Level.DEBUG, TAG,
				"Server address:" + mPinModel.getServerAddress());

		mAppModel.setSfApiKey(getStringAppProperty("API_KEY"));

		Logger.log(Level.DEBUG, TAG, "SfApikey = " + mAppModel.getSfApiKey());

		mAppModel.setSfApiSecretKey(getStringAppProperty("SF_API_SECRAT"));

		Logger.log(Level.DEBUG, TAG,
				"SfApiSecratkey = " + mAppModel.getSfApiSecretKey());

		mAppModel.setSfApiAccessKey(getStringAppProperty("SF_API_ACCESS_KEY"));

		Logger.log(Level.DEBUG, TAG,
				"Sfaccesskey = " + mAppModel.getSfApiAccessKey());

		mAppModel.setEcgLeadCount(getIntAppProperty("TOTAL_ECG_LEAD"));

		Logger.log(Level.DEBUG, TAG,
				"TOTAL_ECG_LEAD = " + mAppModel.getEcgLeadCount());

		final boolean singleStepECG = canBooleanAppProperty("Demo_Mode")
				&& getSharedPreferences(Constants.DEMO_MODE, MODE_PRIVATE)
						.getBoolean(Constants.DEMO_MODE, false);

		Logger.log(Level.DEBUG, TAG, "singleStepECG = " + singleStepECG);
		mPinModel.setSingleStepECG(singleStepECG);

		mAppModel
				.setSupportContactNo(getStringAppProperty("SUPPORT_CONTACT_NUMBER"));

		Logger.log(Level.DEBUG, TAG,
				"getSupportContactNo = " + mAppModel.getSupportContactNo());

		final boolean serverAddressEditable = canBooleanAppProperty("Server_Address_Editable");
		Logger.log(Level.DEBUG, TAG,
				"isServerAddressEditable = " + serverAddressEditable);

		Logger.log(Level.DEBUG, TAG, "Load App Info --");

		final String deviceId = loadDeviceId();
		if (null != deviceId) {
			mPinModel.setDeviceUid(deviceId);
		}

		final String deviceVersion = loadDeviceVersion();
		if (null != deviceVersion) {
			mPinModel.setDeviceVersion(deviceVersion);
		}
	}

	/**
	 * Load user data.
	 */
	private void loadUserData() {
		Logger.log(Level.DEBUG, TAG, "loadUserData()");
		final SharedPreferences saveUserData = getSharedPreferences(
				Constants.USER_DETAILS_DB, MODE_PRIVATE);
		final String userId = saveUserData
				.getString(Constants.SAVE_USER_ID, "");
		mPinModel.setUserID(userId);
		Logger.log(Level.DEBUG, TAG, "userId =" + userId);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

	/**
	 * Gets the saved login token.
	 *
	 * @return String
	 */
	private String getLoginToken() {
		Logger.log(Level.DEBUG, TAG, "getLoginToken()");
		final SharedPreferences getLoginToken = getSharedPreferences(
				Constants.USER_LOGIN_TOKEN_DB, MODE_PRIVATE);
		final String loginToken = getLoginToken.getString(
				Constants.SAVE_LOGIN_TOKEN, mAppModel.getTokenKey());
		Logger.log(Level.DEBUG, TAG, "loginToken = " + loginToken);
		return loginToken;
	}

	/**
	 * Gets the saved user role.
	 *
	 * @return String
	 */
	private String getUserRole() {
		Logger.log(Level.DEBUG, TAG, "getUserRole()");
		final SharedPreferences getLoginToken = getSharedPreferences(
				Constants.USER_LOGIN_TOKEN_DB, MODE_PRIVATE);
		final String userRole = getLoginToken.getString(
				Constants.SAVE_USER_ROLE, mAppModel.getUserRole());
		Logger.log(Level.DEBUG, TAG, "userRole = " + userRole);
		return userRole;
	}

	/**
	 * Gets the saved Unique Id.
	 *
	 * @return String
	 */
	private String getUserUniqueId() {
		Logger.log(Level.DEBUG, TAG, "getUserUniqueId()");
		final SharedPreferences getLoginToken = getSharedPreferences(
				Constants.USER_LOGIN_TOKEN_DB, MODE_PRIVATE);
		final String userUniqueId = getLoginToken.getString(
				Constants.SAVE_USER_UNIQUE_ID, mAppModel.getUserUniqueId());
		Logger.log(Level.DEBUG, TAG, "userUniqueId = " + userUniqueId);
		return userUniqueId;
	}

	/**
	 * Move to Main menu screen if user already accepted the Terms and
	 * conditions.
	 */
	private void moveToNextScreen() {
		if (!isFinishing()) {
			// final String tncVersion = getSharedPreferences(
			// Constants.TNC_VERSION, MODE_PRIVATE).getString(
			// Constants.TNC_VERSION, null);
			// Intent intent = new Intent(this,
			// (tncVersion != null) ? BluetoothPairingActivity.class
			// : TnCnPrivacyActivity.class);
			Intent intent = new Intent(this, BluetoothPairingActivity.class);
			intent.putExtra("from_splash_screen", true);
			startActivity(intent);
			finish();
		}
	}

	/**
	 * Method onTouch.
	 *
	 * @param v
	 *            View
	 * @param event
	 *            MotionEvent
	 * @return boolean
	 * @see android.view.View$OnTouchListener#onTouch(View, MotionEvent)
	 */
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (null != mDisplayTimer && mDisplayTimer.isAlive()) {
			mDisplayTimer.interrupt();
		}
		return false;
	}

}
