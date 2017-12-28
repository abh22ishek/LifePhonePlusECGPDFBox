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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.android.userapp.R;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.util.Util;

/**
 * This Activity will provides options to change the configuration for the
 * device. Server address, Blood glucose calibration and ECG step settings.
 */
public class DeviceConfiguration extends AppBaseActivity implements
		OnClickListener {

	/** The m edit url help. */
	private TextView mEditUrlHelp;

	/** The m edit url. */
	private EditText mEditUrl;

	/** The m ctl sol. */
	private CheckBox mCtlSol = null;

	/** The m demo mode. */
	private CheckBox mDemoMode = null;

	/** The m is ctl sol enabled. */
	private boolean mIsCtlSolEnabled;

	/** The m is demo mode. */
	private boolean mIsDemoMode;

	/** The save. */
	private boolean save = false;

	/**
	 * Method onCreate.
	 *
	 * @param savedInstanceState
	 *            Bundle
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.devconfig);

		setCustomNoIconTitle(R.string.title_device_configuration);

		mEditUrlHelp = (TextView) findViewById(R.id.tvDevConfigTitle);
		mEditUrl = (EditText) findViewById(R.id.etDevConfigURL);

		final SharedPreferences deviceConfigDb = this.getSharedPreferences(
				Constants.DEVICE_CONFIG, MODE_PRIVATE);

		final String storedPreference = deviceConfigDb.getString(
				"dev_config_url", null);
		if (null != storedPreference) {
			mPinModel.setServerAddress(storedPreference);
		}
		// server address check
		if (canBooleanAppProperty("Server_Address_Editable")) {
			mEditUrl.setText((null != storedPreference) ? storedPreference
					: mPinModel.getServerAddress());
		}else{
			mEditUrl.setVisibility(View.GONE);
			mEditUrlHelp.setVisibility(View.GONE);
		}


		// blood sugar configuration check
		mCtlSol = (CheckBox) findViewById(R.id.use_ctl_solu);
		if (canBooleanAppProperty("Use_Control_Solution")) {
			final SharedPreferences pinPref = this.getSharedPreferences(
					Constants.CONTROL_SOLUTION, MODE_PRIVATE);
			mIsCtlSolEnabled = pinPref.getBoolean(Constants.CONTROL_SOLUTION,
					false);
			mCtlSol.setChecked(mIsCtlSolEnabled);
		} else {
			mCtlSol.setVisibility(View.GONE);
		}
		// single step ecg
		mDemoMode = (CheckBox) findViewById(R.id.single_step_ecg);
		if (canBooleanAppProperty("Demo_Mode")) {
			final SharedPreferences pinPref = this.getSharedPreferences(
					Constants.DEMO_MODE, MODE_PRIVATE);
			mIsDemoMode = pinPref.getBoolean(Constants.DEMO_MODE, false);
			mDemoMode.setChecked(mIsDemoMode);
		} else {
			mDemoMode.setVisibility(View.GONE);
		}

		final Button save = (Button) findViewById(R.id.btn_menu_positive);
		save.setText(R.string.save);
		save.setOnClickListener(this);
	}

	/**
	 * Method onClick.
	 *
	 * @param arg0
	 *            View
	 * @see android.view.View$OnClickListener#onClick(View)
	 */
	@Override
	public void onClick(View arg0) {
		save = false;
		// To handle the control solution bg measurement
		if (mIsCtlSolEnabled != mCtlSol.isChecked() && mCtlSol.isEnabled()) {
			final SharedPreferences pinPref = this.getSharedPreferences(
					Constants.CONTROL_SOLUTION, MODE_PRIVATE);
			final SharedPreferences.Editor prefsEditor = pinPref.edit();
			prefsEditor.putBoolean(Constants.CONTROL_SOLUTION,
					mCtlSol.isChecked());
			prefsEditor.commit();
		}

		// To handle the Demo mode
		if (mIsDemoMode != mDemoMode.isChecked() && mDemoMode.isEnabled()) {
			final SharedPreferences pinPref = this.getSharedPreferences(
					Constants.DEMO_MODE, MODE_PRIVATE);
			final SharedPreferences.Editor prefsEditor = pinPref.edit();
			prefsEditor.putBoolean(Constants.DEMO_MODE, mDemoMode.isChecked());
			prefsEditor.commit();

			final boolean singleStepECG = canBooleanAppProperty("Demo_Mode")
					&& getSharedPreferences(Constants.DEMO_MODE, MODE_PRIVATE)
							.getBoolean(Constants.DEMO_MODE, false);
			Logger.log(Level.DEBUG, TAG, "singleStepECG = " + singleStepECG);
			// set single step ECG.
			mPinModel.setSingleStepECG(singleStepECG);
			sendCommand(Util.getConfigMessage());
		}

		// To handle the server address
		if (canBooleanAppProperty("Server_Address_Editable")) {
			final String oldUrl = mPinModel.getServerAddress();
			final String newUrl = mEditUrl.getText().toString();

			if (0 != oldUrl.compareTo(newUrl) && isValidURL(newUrl)) {
				// Save the new URL
				final SharedPreferences.Editor prefsEditor = getSharedPreferences(
						Constants.DEVICE_CONFIG, MODE_PRIVATE).edit();
				prefsEditor.putString("dev_config_url", newUrl);
				prefsEditor.commit();

				// Clear the existing login token
				clearLoginToken();

				// set server address to model
				mPinModel.setServerAddress(newUrl);

				mDialogType = Constants.SHOW_SERVER_ADDRESS_CHANGED_DIALOG;
				showAlertDialog(get(R.string.serveraddress),
						get(R.string.server_address_save), get(R.string.OK), null,
						false);
				save = true;
			}
		}

		if (!save) {
			finish();
		}
	}

	/**
	 * Method validateURL.
	 *
	 * @param url
	 *            String
	 * @return boolean
	 */
	private boolean isValidURL(String url) {
		boolean valid = true;
		save = true;
		if (null == url || 0 == url.trim().length()) {
			showAlertDialog(get(R.string.title_profile),
					get(R.string.url_empty), get(R.string.OK), null, true);
			valid = false;
		} else if (!(url.startsWith("http://") || url.startsWith("https://"))) {
			showAlertDialog(get(R.string.title_profile),
					get(R.string.valid_url), get(R.string.OK), null, true);
			valid = false;
		}
		return valid;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.lppbpl.android.userapp.AppBaseActivity#onPositiveButtonClick()
	 */
	@Override
	protected void onPositiveButtonClick() {
		super.onPositiveButtonClick();
		if (mDialogType == Constants.SHOW_SERVER_ADDRESS_CHANGED_DIALOG) {
			startActivityForResult(new Intent(this, LoginActivity.class), 1000);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (1000 == requestCode && resultCode == RESULT_OK) {
			finish();
		}
	}
}
