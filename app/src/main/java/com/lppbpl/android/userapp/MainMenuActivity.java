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
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.DeviceData;
import com.lppbpl.Measure;
import com.lppbpl.Measurement;
import com.lppbpl.Response;
import com.lppbpl.ResponseType;
import com.lppbpl.SFMessaging;
import com.lppbpl.SFMessaging.MessageType;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.db.DiagnosisMsgDb;
import com.lppbpl.android.userapp.db.PendingRecordDb;
import com.lppbpl.android.userapp.model.PendingRecord;
import com.lppbpl.android.userapp.model.Profile;
import com.lppbpl.android.userapp.util.DeletePairUtility;
import com.lppbpl.android.userapp.util.HttpUtil;

import java.lang.ref.WeakReference;

// TODO: Auto-generated Javadoc
/**
 * THis activity is to display the main menu.
 */
@SuppressWarnings("ALL")
public class MainMenuActivity extends NetworkConnBaseActivity implements
		OnClickListener, OnTouchListener {

	/** The m diag layout. */
	private RelativeLayout mDiagLayout;

	/** The user name. */
	private String userName;

	/** The m back pressed. */
	private boolean mBackPressed;

	/** The do logout. */
	private DoLoginOut doLogout;

	/**
	 * Method onCreate.
	 *
	 * @param savedInstanceState
	 *            Bundle
	 */

	final static String TAG=MainMenuActivity.class.getSimpleName();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.log(Level.DEBUG, TAG, "onCreate");

		setContentView(R.layout.mainmenu);
		setCustomNoIconTitle(R.string.title_app_name);

		/*if(!checkRunningService())
		{
			this.finishActivity();
			exitApp();
			return;
		}*/

		updateDeviceStatus();

		final RelativeLayout mEcgLayout = (RelativeLayout) findViewById(R.id.layout_ecg);
		mEcgLayout.setOnClickListener(this);
		final RelativeLayout mBgLayout = (RelativeLayout) findViewById(R.id.layout_bg);
		mBgLayout.setOnClickListener(this);
		final RelativeLayout mActLayout = (RelativeLayout) findViewById(R.id.layout_act);
		mActLayout.setOnClickListener(this);
		final RelativeLayout mRecLayout = (RelativeLayout) findViewById(R.id.layout_rec);
		mRecLayout.setOnClickListener(this);
		mDiagLayout = (RelativeLayout) findViewById(R.id.layout_diag);
		mDiagLayout.setOnClickListener(this);

		updateWalletTopupFeature();

//		final RelativeLayout mTrendLayout = (RelativeLayout) findViewById(R.id.layout_trend);
//		mTrendLayout.setOnClickListener(this);
//		((ImageView) findViewById(R.id.menu_image_trend))
//				.setImageResource(R.drawable.ic_menuitem_dashboard_disabled);
//		mTrendLayout.setClickable(false);

		final ImageView mMenuSetting = (ImageView) findViewById(R.id.menu_settings);
		mMenuSetting.setOnClickListener(this);

		// thread will handle the cache records from device
		new Thread() {
			@Override
			public void run() {
				final Measure measure = Measure.newBuilder().setMeasurementType(Measurement.CACHED).build();
				Logger.log(Level.DEBUG, TAG,"*** MEASURE***"+measure);

				final SFMessaging requestForCached = SFMessaging.newBuilder()
						.setSfMsgType(MessageType.MeasureMsg)
						.setSfMeasureMsg(measure).build();
				     sendCommand(requestForCached);
			}
		}.start();

		final boolean skipPair = getIntent()
				.getBooleanExtra("skip_pair", false);
		if (skipPair) {
			sendEmptyMessage(Constants.REQUEST_RECONNECTION);
			Logger.log(Level.DEBUG, TAG, "REQUEST_RECONNECTION message");
		}

		mProgDialog.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface arg0) {
				if (null != doLogout) {
					doLogout.cancel(true);
				}
			}
		});
	}

	/**
	 * Enables/disables the wallet topup feature based upon the user role. If it is primary user wallet feature will be enabled otherwise disabled.
	 */
	private void updateWalletTopupFeature(){
		final RelativeLayout mWalletTopupLayout = (RelativeLayout) findViewById(R.id.layout_wallet_top_up);
		mWalletTopupLayout.setOnClickListener(this);
		final ImageView mWalletTopUpImage = (ImageView) findViewById(R.id.menu_image_wallet_top_up);
		final TextView walletTopupLayoutLabel = (TextView) findViewById(R.id.menu_lable_wallet_top_up);
		if(mAppModel.isPrimaryUser()){
			mWalletTopupLayout.setClickable(true);
			mWalletTopUpImage.setEnabled(true);
			walletTopupLayoutLabel.setTextColor(Color.WHITE);
		}else{
			mWalletTopupLayout.setClickable(false);
			mWalletTopUpImage.setEnabled(false);
			walletTopupLayoutLabel.setTextColor(0x47474700);
		}
	}

	/**
	 * Update diagnosis.
	 */
	private void updateDiagnosis() {
		final ImageView diagImage = (ImageView) findViewById(R.id.menu_image_diag);
		final TextView diagLabel = (TextView) findViewById(R.id.menu_lable_diag);
		final boolean diagnosesFound = DiagnosisMsgDb.getInstance()
				.numOfRecords() > 0;
		if (null != diagImage) {
			diagImage
					.setImageResource(diagnosesFound ? R.drawable.ic_menuitem_diagnosis
							: R.drawable.ic_menuitem_diagnosis_disabled);
			diagLabel.setTextColor(diagnosesFound ? Color.WHITE : 0x47474700);
		}
		mDiagLayout.setClickable(diagnosesFound);
	}

	/*
	 * updateBGModule() method used to disable/enable the BG option in main menu based on device id (Date and month);
	 */
	private void updateBGModule() {
		final TextView bgLabel = (TextView) findViewById(R.id.menu_lable_bg);
		if (null != bgLabel) {
			bgLabel.setTextColor(isBGModuleEnabled() ? Color.WHITE : 0x47474700);
		}
		((RelativeLayout)findViewById(R.id.layout_bg)).setClickable(isBGModuleEnabled());
	}

	/** Handler updates the the device status and resumes the activity. */
	private final MyHandler mHandler = new MyHandler(this);

	/**
	 * The Class MyHandler.
	 */
	private static class MyHandler extends Handler {

		/** The m activity. */
		private final WeakReference<MainMenuActivity> mActivity;

		/**
		 * Constructor for MyHandler.
		 *
		 * @param activity
		 *            MainMenuActivity
		 */
		private MyHandler(MainMenuActivity activity) {
			mActivity = new WeakReference<MainMenuActivity>(activity);
		}

		/**
		 * Method handleMessage.
		 *
		 * @param msg
		 *            android.os.Message
		 */
		@Override
		public void handleMessage(android.os.Message msg) {
			final MainMenuActivity parent = mActivity.get();
			if (msg.what == Constants.DISPLAY_COMMAND) {
				parent.updateDeviceStatus();

			} else if (msg.what == Constants.SHUTDOWN_TIMEOUT) {
				Logger.log(Level.DEBUG, TAG, "SHUTDOWN_TIMEOUT. so app is closing");
				parent.closeAllActivities();

			} else if (msg.what == Constants.CANCEL_TIMEOUT) {
				parent.mAppCrl.resumeGettingActivityData();

				// case Constants.BATTERY_UPDATE:
				// mAppCrl.sendDeviceDataRequest();
				// break;
			} else {
			}
		}
	};

	/** The is shut down ack. */
	private boolean isShutDownAck;

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		//updateWalletTopupFeature();
		updateDiagnosis();

		updatePendingRecords();

		updateBGModule();

		final String userId = mPinModel.getUserID();
		final TextView mUserId = (TextView) findViewById(R.id.txt_loginas);
		mUserId.setVisibility(View.INVISIBLE);
		if (null != userId && userId.length() > 0
				&& null != mAppModel.getTokenKey()) {
			mUserId.setText(userId);
			mUserId.setOnTouchListener(null);
			mUserId.setOnClickListener(null);
		} else {
			mUserId.setText(R.string.login_underline);
			mUserId.setOnTouchListener(this);
			mUserId.setOnClickListener(this);
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		super.onRestart();
		Logger.log(Level.DEBUG, TAG, "onRestart()");
		mAppCrl.pauseGettingActivityData();
		mAppCrl.sendDeviceDataRequest();
		mHandler.sendEmptyMessageDelayed(Constants.CANCEL_TIMEOUT,
				Constants.RESET_ACT_GETDATA_DELAY);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.lppbpl.android.userapp.AppBaseActivity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		Logger.log(Level.DEBUG, TAG, "onStart()");
		// updateDiagnosis();
		updateWalletTopupFeature();
		updatePendingRecords();

		if (!isActivityRunning()) {
			mAppModel.clearPendingRecord();
		}

		mAppModel.clearAllData();

		// mHandler.sendEmptyMessageDelayed(Constants.BATTERY_UPDATE,
		// Constants.BATTERY_UPDATE_DELAY);
	}

	/**
	 * Updates the pending records image and count.
	 */
	private void updatePendingRecords() {
		final int record = PendingRecordDb.getInstance().numOfRecords();
		final ImageView mCoungBg = (ImageView) findViewById(R.id.menu_image_rec_count);
		final TextView mCountTxt = (TextView) findViewById(R.id.menu_lable_rec_count);

		mCoungBg.setVisibility((record > 0) ? View.VISIBLE : View.INVISIBLE);
		mCountTxt.setVisibility((record > 0) ? View.VISIBLE : View.INVISIBLE);
		mCountTxt.setText((record > 9) ? "9+" : Integer.toString(record));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.lppbpl.android.userapp.AppBaseActivity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		Logger.log(Level.DEBUG, TAG, "onStop()");
		// mHandler.removeMessages(Constants.BATTERY_UPDATE);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.lppbpl.android.userapp.AppBaseActivity#onNegativeButtonClick()
	 */
	@Override
	protected void onNegativeButtonClick() {
		if (mDialogType == Constants.SHOW_CATCHED_REC_DIALOGE) {
			updatePendingRecords();

		} else if (mDialogType == Constants.SHOW_ACT_START_DIALOGE) {
			if (!mBackPressed) {
				startProfileActivity();
			}

		} else {
			mDialogType = Constants.SHOW_DIALOGE_NONE;
			mAppCrl.resumeGettingActivityData();

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
		Logger.log(Level.INFO,TAG,"mDialogType is="+mDialogType);
		if (mDialogType == Constants.SHOW_EXIT_DIALOGE) {
			exitApp();

		} else if (mDialogType == Constants.SHOW_ACT_RUNNING_DIALOGE) {
			stopRunningActivity();

		} else if (mDialogType == Constants.SHOW_ACT_START_DIALOGE) {
			startTheActivity();

		} else if (mDialogType == Constants.SHOW_DELETE_PAIR_DIALOGE) {
			final String deviceName = getStringAppProperty("DEVICE_NAME");
			if (null != deviceName) {
				DeletePairUtility.removePairedDevice(this, deviceName);
			}
			mDialogType = Constants.SHOW_DIALOGE_NONE;
			clearLoginToken();
			closeAllActivities();

		} else if (mDialogType == Constants.SHOW_LOGOUT_DIALOGE) {// Logout API call
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

	/**
	 * starts the profile activity.
	 */
	private void startProfileActivity() {
		final PendingRecord record = mAppModel.getPendingRecord();
		if (null == record || !record.isActivityRecord()) {
			mAppModel.createPendingRecord(Measurement.ACT);
		}
		final Intent activityIntent = new Intent(this, ProfileActivity.class);
		startActivity(activityIntent);
	}

	/**
	 * Starts the activity measurement screen.
	 */
	private void startTheActivity() {
		final PendingRecord record = mAppModel.getPendingRecord();
		if (null == record || !record.isActivityRecord()) {
			mAppModel.createPendingRecord(Measurement.ACT);
		}
		/*
		 * if userName is null, that means user profile is not updated, so take
		 * the user to profile screen else take the user to Activity Running
		 * screen.
		 */

		final Intent activityIntent = new Intent(this,
				(null != userName || isActivityRunning()) ? ActivityON.class
						: ProfileActivity.class);
		activityIntent.putExtra("name", userName);
		activityIntent.putExtra(Constants.SHOW_MAINMENU, true);
		startActivity(activityIntent);
	}

	/**
	 * Stop the timer, send the shutdown message to device and Exits the
	 * application.
	 */
	private void exitApp() {
		mDialogType = Constants.SHOW_DIALOGE_NONE;
		if (isActivityRunning()) {
			mActModel.setRunning(false);
			// mActModel.stopDurationTimer();
			mActModel.stopGetDataTimer();
		}
		isShutDownAck = true;
		mAppCrl.sendShutdownMsg();
		mHandler.sendEmptyMessageDelayed(Constants.SHUTDOWN_TIMEOUT,
				Constants.SHUTDOWN_TIMEOUT_DELAY);
		if (!mAppCrl.isDeviceConnected()) {
			Logger.log(Level.INFO, TAG, "mAppCrl.isDeviceConnected() = "
					+ mAppCrl.isDeviceConnected());
			closeAllActivities();
		}
	}

	/**
	 * Stops the running activity.
	 */
	private void stopRunningActivity() {
		mDialogType = Constants.SHOW_DIALOGE_NONE;
		mActModel.stopActivity();
		final Intent intent = new Intent(this, ActivityFinalDisplay.class);
		startActivity(intent);
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
		mHandler.removeMessages(Constants.CANCEL_TIMEOUT);
	}

	/**
	 * Method onKeyDown.
	 *
	 * @param keyCode
	 *            int
	 * @param event
	 *            KeyEvent
	 * @return boolean
	 * @see android.view.KeyEvent$Callback#onKeyDown(int, KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mAppCrl.pauseGettingActivityData();
			mDialogType = Constants.SHOW_EXIT_DIALOGE;
			showAlertDialog(get(R.string.exit_app),
					get(R.string.exit_confirmation), get(R.string.OK),
					get(R.string.cancel), false);
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Method onCreateOptionsMenu.
	 *
	 * @param menu
	 *            Menu
	 * @return boolean
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.settings_menu, menu);
		return true;
	}

	/**
	 * Method onPrepareOptionsMenu.
	 *
	 * @param menu
	 *            Menu
	 * @return boolean
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem item = menu.findItem(R.id.delete_pair_item);
		item.setVisible(mAppCrl.isDeviceConnected() ? false : true);

		item = menu.findItem(R.id.my_members);
		item.setVisible(mAppModel.isPrimaryUser());

		item = menu.findItem(R.id.logout);
		item.setVisible(mPinModel.isLoginForSessionSuccess());

		item = menu.findItem(R.id.bg_calibration);
		item.setVisible(isBGModuleEnabled());

		item = menu.findItem(R.id.deviceConfig);
		if(canBooleanAppProperty("Server_Address_Editable") || canBooleanAppProperty("Demo_Mode") || canBooleanAppProperty("Use_Control_Solution")){
			item.setVisible(true);
		}else{
			item.setVisible(false);
		}

		return true;
	}

	/**
	 * Method onOptionsItemSelected.
	 *
	 * @param item
	 *            MenuItem
	 * @return boolean
	 */
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		int i = item.getItemId();
		if (i == R.id.profile) {
			final Intent profile = new Intent(this, ProfileActivity.class);
			profile.putExtra("Setting", true);
			startActivity(profile);

		} else if (i == R.id.deviceConfig) {
			final Intent deviceConfig = new Intent(this,
					DeviceConfiguration.class);
			startActivity(deviceConfig);

		} else if (i == R.id.bg_calibration) {
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

		}
		/*else if (i == R.id.update_profile) {
			final Intent updateProfile = new Intent(this,
					UpdateProfileActivity.class);
			startActivity(updateProfile);

		}*/
		else if (i == R.id.delete_pair_item) {
			Logger.log(Level.ERROR, TAG, "removed the device from memory.");
			mDialogType = Constants.SHOW_DELETE_PAIR_DIALOGE;
			showAlertDialog(get(R.string.information),
					get(R.string.delete_paired_device), get(R.string.OK),
					get(R.string.cancel), false);

		} else if (i == R.id.logout) {
			Logger.log(Level.ERROR, TAG, "Logout dialog shown.");
			mDialogType = Constants.SHOW_LOGOUT_DIALOGE;
			showAlertDialog(R.drawable.logout, get(R.string.logout),
					get(R.string.logout_confirmation), get(R.string.OK),
					get(R.string.cancel), false);

		} else if (i == R.id.my_members) {
			final Intent createMember = new Intent(this, MyMembersList.class);
			startActivity(createMember);

		} else if (i == R.id.about) {
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

		return true;
	}

	/**
	 * Updates device connected status.
	 */
	private void updateDeviceStatus() {
		final TextView mPowerStatusView = (TextView) findViewById(R.id.device_power_status_view);
		final TextView mBatteryStatuView = (TextView) findViewById(R.id.device_battery_status_view);
		mPowerStatusView.setText(mAppCrl.isDeviceConnected() ? get(R.string.on) : get(R.string.off));
		mBatteryStatuView.setText(getBatteryLevel());
		if(mAppModel.getDeviceData()!=null)
		{
			//mBatteryStatuView.setText(getBatteryLevel()+"("+ mAppModel.getDeviceData().getBatteryLevel()+")");
			mBatteryStatuView.setText(getBatteryLevel());
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.lppbpl.android.userapp.AppBaseActivity#updateTitleOnBtNotify()
	 */
	@Override
	public void updateTitleOnBtNotify() {
		super.updateTitleOnBtNotify();
		updateDeviceStatus();
		// close the option menu
		closeOptionsMenu();
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
		if (i == R.id.layout_ecg || i == R.id.layout_bg) {
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
					mAppModel.createPendingRecord((v.getId() == R.id.layout_ecg) ? Measurement.ECG : Measurement.BG);
					final Intent intent = new Intent(this, (v.getId() == R.id.layout_ecg) ? ECGActivity.class : BloodSugarActivity.class);
					intent.putExtra(Constants.SHOW_MAINMENU, true);
					startActivity(intent);
				}
			} else {
				showAlertDialog(get(R.string.information),
						get(R.string.device_off), get(R.string.OK), null, true);
			}

		} else if (i == R.id.layout_act) {
			if (mAppCrl.isDeviceConnected()) {
				if (isDeviceCharging()) {
					showAlertDialog(get(R.string.information),
							get(R.string.device_charging), get(R.string.OK),
							null, true);
				} else {
					mDialogType = Constants.SHOW_ACT_START_DIALOGE;
					final Profile mProfile = mActModel.getUserProfile();
					userName = mActModel.getUserProfile().getUserName();
					if (!isActivityRunning()) {
						final String msg = getString(R.string.ht_wt_info,
								mProfile.getUserHeight() + " cm",
								Integer.toString(mProfile.getUserWeight()));
						showAlertDialog(get(R.string.title_activity), msg,
								get(R.string.start), get(R.string.Edit), false);
						mBackPressed = false;
						mDialog.setOnKeyListener(new OnKeyListener() {

							@Override
							public boolean onKey(DialogInterface arg0,
												 int arg1, KeyEvent arg2) {
								mBackPressed = arg2.getKeyCode() == KeyEvent.KEYCODE_BACK;
								return false;
							}
						});
					} else {
						startTheActivity();
					}
				}
			} else {
				showAlertDialog(get(R.string.information),
						get(R.string.device_off), get(R.string.OK), null, true);
			}

		} else if (i == R.id.layout_rec) {
			final Intent recActivity = new Intent(this,
					RecordsListActivity.class);
			startActivity(recActivity);

		} else if (i == R.id.layout_diag) {
			final int noOfDiagnoses = DiagnosisMsgDb.getInstance()
					.numOfRecords();
			if (noOfDiagnoses > 0) {
				final Intent diaActivity = new Intent(this,
						DiagnosisMainList.class);
				startActivity(diaActivity);
			}

		} else if (i == R.id.layout_wallet_top_up) {
			final Intent walletTopUpDummyActivity = new Intent(this, WalletTopupActivity.class);
			startActivity(walletTopUpDummyActivity);

		} else if (i == R.id.menu_settings) {
			final Intent settingActivity = new Intent(this,
					SettingsActivity.class);
			startActivity(settingActivity);

		} else if (i == R.id.txt_loginas) {
			final Intent loginActivity = new Intent(this, LoginActivity.class);
			startActivity(loginActivity);

		} else {
		}
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
		if (response.getResponseType() == ResponseType.ACK && isShutDownAck) {
			Logger.log(Level.DEBUG, TAG, "shutdown Ack received");
			closeAllActivities();
		} else if (response.hasDevData()) {
			mAppModel.setDeviceData(response.getDevData());
			mHandler.sendEmptyMessage(Constants.DISPLAY_COMMAND);
			mAppCrl.resumeGettingActivityData();
		}
	}

	/**
	 * Returns battery level information.
	 *
	 * @return String
	 */
	public String getBatteryLevel() {
		final DeviceData data = mAppModel.getDeviceData();
		String retStr = "NA";
		if (null != data && mAppCrl.isDeviceConnected()) {
			final int level = data.getBatteryLevel();
			if (isDeviceCharging()) {// 1 IF CHARGING, 0 IF NOT BEING CHARGED
				retStr = get(R.string.charging);
			} else if (level >= Constants.BATTERY_CRITICAL_MIN
					&& level <= Constants.BATTERY_CRITICAL_MAX) {
				retStr = get(R.string.device_blevel_critical);
			} else if (level >= Constants.BATTERY_LOW_MIN
					&& level <= Constants.BATTERY_LOW_MAX) {
				retStr = get(R.string.device_blevel_low);
			} else if (level >= Constants.BATTERY_NORMAL_MIN
					&& level <= Constants.BATTERY_NORMAL_MAX) {
				retStr = get(R.string.device_blevel_normal);
			}
		}
		return retStr;
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
		if (v.getId() == R.id.txt_loginas) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				((TextView) v).setTypeface(Typeface.DEFAULT_BOLD);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				((TextView) v).setTypeface(Typeface.DEFAULT);
			} else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
				((TextView) v).setTypeface(Typeface.DEFAULT);
			}
		}
		return false;
	}
	public boolean checkRunningService()
	{



		ActivityManager activityManager=(ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE))
		{
			Log.i("Running Services", service.service.getClassName());

			if ("com.pkg.externalapp.MyService".equals(service.service.getClassName()))
			{
				//Toast.makeText(MainActivity.this,"My srevice running" ,Toast.LENGTH_SHORT).show();
				Intent i = new Intent();
				i.setComponent(new ComponentName("com.pkg.externalapp", "com.pkg.externalapp.MyService"));
				if(i!=null)
				{
					stopService(i);
					//Toast.makeText(MainActivity.this," Service is stopped  app",Toast.LENGTH_SHORT).show();
				}
				return true;
			}
		}
		return false;

	}
}
