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

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.DeviceData;
import com.lppbpl.Measurement;
import com.lppbpl.Response;
import com.lppbpl.android.userapp.R;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.util.DeletePairUtility;
import com.lppbpl.android.userapp.util.HttpUtil;

// TODO: Auto-generated Javadoc
/**
 * This Activity is to display the settings screen for Profile, Device
 * configuration, Blood sugar calibration and changing the mobile number etc.
 *
 */
public class SettingsActivity extends NetworkConnBaseActivity implements
		OnClickListener {

	/** The Constant TAG. */
	public static final String TAG = SettingsActivity.class.getName();

	/** The m txt profile. */
	private TextView mTxtProfile;

	/** The m txt add member. */
	private TextView mTxtAddMember;

	/** The m txt log out. */
	private TextView mTxtLogOut;

	private TextView mTxtBgCalibration;

	/** The do logout. */
	private DoLoginOut doLogout;

	/**
	 * Method onCreate.
	 *
	 * @param savedInstanceState
	 *            Bundle
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		setCustomTitle(R.string.settings);

		mTxtProfile = (TextView) findViewById(R.id.txt_profile);
		mTxtProfile.setOnClickListener(this);

		mTxtAddMember = (TextView) findViewById(R.id.txt_my_members);
		mTxtAddMember.setOnClickListener(this);

		final TextView mTxtDeviceConfig = (TextView) findViewById(R.id.txt_device_configuration);

		if(canBooleanAppProperty("Server_Address_Editable") || canBooleanAppProperty("Demo_Mode") || canBooleanAppProperty("Use_Control_Solution")){
			mTxtDeviceConfig.setVisibility(View.VISIBLE);
			mTxtDeviceConfig.setOnClickListener(this);
		} else{
			mTxtDeviceConfig.setVisibility(View.GONE);
		}


		mTxtBgCalibration = (TextView) findViewById(R.id.txt_bg_calibration);
		mTxtBgCalibration.setOnClickListener(this);

		final TextView mTxtUpdateProfile = (TextView) findViewById(R.id.txt_update_profile);
		mTxtUpdateProfile.setOnClickListener(this);
		mTxtUpdateProfile.setVisibility(View.GONE);


		mTxtLogOut = (TextView) findViewById(R.id.txt_signout);
		mTxtLogOut.setOnClickListener(this);

		final TextView txtAbout = (TextView) findViewById(R.id.txt_about);
		txtAbout.setOnClickListener(this);

		final TextView mTxtDeletePair = (TextView) findViewById(R.id.txt_remove_paried_device);
		mTxtDeletePair.setOnClickListener(this);

		updateRemovePairListItem();

		mProgDialog.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface arg0) {
				if (null != doLogout) {
					doLogout.cancel(true);
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mTxtAddMember.setVisibility(mAppModel.isPrimaryUser() ? View.VISIBLE
				: View.GONE);
		mTxtLogOut
				.setVisibility(mPinModel.isLoginForSessionSuccess() ? View.VISIBLE
						: View.GONE);

		mTxtBgCalibration.setVisibility(isBGModuleEnabled() ? View.VISIBLE
				: View.GONE);
	}

	/**
	 * Updates the unsaved records.
	 */
	private void updateRemovePairListItem() {
		Logger.log(Level.INFO, TAG, "updateRemovePairListItem()");
		final boolean mDeviceState = !(mAppCrl.isDeviceConnected());
		((TextView) findViewById(R.id.txt_remove_paried_device))
				.setVisibility(mDeviceState ? View.VISIBLE : View.GONE);
	}

	/**
	 * Method onClick.
	 *
	 * @param v
	 *            View
	 * @see android.view.View$OnClickListener#onClick(View)
	 */
	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.txt_my_members) {
			final Intent createMember = new Intent(this, MyMembersList.class);
			startActivity(createMember);

		} else if (i == R.id.txt_profile) {
			final Intent profile = new Intent(this, ProfileActivity.class);
			profile.putExtra("Setting", true);
			startActivity(profile);

		} else if (i == R.id.txt_device_configuration) {
			final Intent deviceConfig = new Intent(this,
					DeviceConfiguration.class);
			startActivity(deviceConfig);

		} else if (i == R.id.txt_bg_calibration) {
			if (mAppCrl.isDeviceConnected()) {
				if (isActivityRunning()) {
					mDialogType = Constants.SHOW_ACT_RUNNING_DIALOGE;
					showAlertDialog(get(R.string.information),
							get(R.string.activity_running), get(R.string.OK),
							get(R.string.cancel), false);
				} else if (isDeviceCharging()) {
					showAlertDialog(get(R.string.information),
							get(R.string.device_charging), get(R.string.OK),
							null, true);
				} else {
					mAppModel.createPendingRecord(Measurement.BG);
					final Intent bgCalibration = new Intent(this,
							BloodSugarActivity.class);
					bgCalibration.putExtra("calibration", true);
					startActivity(bgCalibration);
				}
			} else {
				showAlertDialog(get(R.string.information),
						get(R.string.device_off), get(R.string.OK), null, true);
			}

		} else if (i == R.id.txt_update_profile) {
			final Intent updateProfile = new Intent(this,
					UpdateProfileActivity.class);
			startActivity(updateProfile);

		} else if (i == R.id.txt_remove_paried_device) {
			Logger.log(Level.ERROR, TAG, "removed the device from memory.");
			mDialogType = Constants.SHOW_DELETE_PAIR_DIALOGE;
			showAlertDialog(get(R.string.information),
					get(R.string.delete_paired_device), get(R.string.OK),
					get(R.string.cancel), false);

		} else if (i == R.id.txt_signout) {
			Logger.log(Level.ERROR, TAG, "Log out confirmation");
			mDialogType = Constants.SHOW_LOGOUT_DIALOGE;
			showAlertDialog(R.drawable.logout, get(R.string.logout),
					get(R.string.logout_confirmation), get(R.string.OK),
					get(R.string.cancel), false);

		} else if (i == R.id.txt_about) {
			try {
				String firmwareVersion = null;
				final DeviceData deviceData = mAppModel.getDeviceData();
				if (deviceData != null) {
					firmwareVersion = deviceData.getFirmwareVersion();
				} else {
					firmwareVersion = mPinModel.getDeviceVersion();
				}
				final String version = get(R.string.app_name)
						+ " Version : "
						+ getPackageManager().getPackageInfo(getPackageName(),
						0).versionName
					/*	+ "\nDevice Version : " + firmwareVersion  */
						+ "\nDevice Id : " + mPinModel.getDeviceUid();
				showAlertDialog(R.drawable.help, get(R.string.about), version,
						get(R.string.OK), null, true);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}

		} else {
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.lppbpl.android.userapp.NetworkConnBaseActivity#onPositiveButtonClick
	 * ()
	 */
	@Override
	public void onPositiveButtonClick() {
		super.onPositiveButtonClick();
		if (mDialogType == Constants.SHOW_ACT_RUNNING_DIALOGE) {
			stopRunningActivity();

		} else if (mDialogType == Constants.SHOW_DELETE_PAIR_DIALOGE) {
			final String deviceName = getStringAppProperty("DEVICE_NAME");
			if (null != deviceName)
				DeletePairUtility.removePairedDevice(SettingsActivity.this,
						deviceName);
			mDialogType = Constants.SHOW_DIALOGE_NONE;
			clearLoginToken();
			closeAllActivities();

		} else if (mDialogType == Constants.SHOW_LOGOUT_DIALOGE) {
			mDialogType = Constants.SHOW_DIALOGE_NONE;
			// Logout API call
			if (isNetworkConnected()) {
				doLogout = new DoLoginOut(this);
				doLogout.execute();
			} else {
				showAlertDialog(R.drawable.ic_dialog_no_signal,
						get(R.string.network_connection),
						HttpUtil.getRespnseMsg(HttpUtil.HTTP_CONNECTION_DOWN),
						get(R.string.OK), null, true);
			}


		} else {
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.lppbpl.android.userapp.AppBaseActivity#onNegativeButtonClick()
	 */
	@Override
	protected void onNegativeButtonClick() {
		if (mDialogType == Constants.SHOW_DELETE_PAIR_DIALOGE || mDialogType == Constants.SHOW_LOGOUT_DIALOGE) {
			mDialogType = Constants.SHOW_DIALOGE_NONE;

		} else {
		}

	}

	/**
	 * Stop the running activity measurement.
	 */
	private void stopRunningActivity() {
		mDialogType = Constants.SHOW_DIALOGE_NONE;
		mActModel.stopActivity();
		final Intent intent = new Intent(this, ActivityFinalDisplay.class);
		startActivity(intent);
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
		// TODO Auto-generated method stub
		super.dataReceived(response);
		Logger.log(Level.INFO, TAG,
				"response.getResponseType() =" + response.getResponseType());
		Logger.log(Level.INFO, TAG,
				"response.hasDevData() =" + response.hasDevData());
		// to hide the remove paired device option on accessory connect
		if (response.hasDevData()) {
			mHandler.sendEmptyMessage(Constants.CONNECTION_ESTABLISHED);
		}
	}

	/**
	 * Handler is used for handling the bluetooth status: :Connecting :Connected
	 * :Disconnecting :Disconnected.
	 */
	private final Handler mHandler = new MyHandler(this);

	/**
	 * The Class MyHandler.
	 */
	private static class MyHandler extends Handler {

		/** The m activity. */
		private WeakReference<SettingsActivity> mActivity;

		/**
		 * Constructor for MyHandler.
		 *
		 * @param activity
		 *            SettingsActivity
		 */
		private MyHandler(SettingsActivity activity) {
			mActivity = new WeakReference<SettingsActivity>(activity);
		}

		/**
		 * Method handleMessage.
		 *
		 * @param msg
		 *            android.os.Message
		 */
		@Override
		public void handleMessage(android.os.Message msg) {
			Logger.log(Level.INFO, TAG, "msg.what =" + msg.what);
			final SettingsActivity parent = mActivity.get();
			if ((msg.what == Constants.CONNECTION_ESTABLISHED)) {
				parent.updateRemovePairListItem();
			}
		}
	};

	/**
	 * Updates the remove paired device option when device disconnects.
	 */
	@Override
	public void updateTitleOnBtNotify() {
		super.updateTitleOnBtNotify();
		Logger.log(Level.INFO, TAG, "updateTitleOnBtNotify ="
				+ "updateRemovePairListItem");
		updateRemovePairListItem();
	}
}
