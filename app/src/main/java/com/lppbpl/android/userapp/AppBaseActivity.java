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

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.DeviceData;
import com.lppbpl.GetData;
import com.lppbpl.GetData.DataType;
import com.lppbpl.Measurement;
import com.lppbpl.Response;
import com.lppbpl.Response.ResErrorCode;
import com.lppbpl.Response.ResStatusCode;
import com.lppbpl.ResponseType;
import com.lppbpl.SFMessaging;
import com.lppbpl.SFMessaging.MessageType;
import com.lppbpl.android.userapp.bluetooth.BluetoothClient;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.controller.SfApplicationController;
import com.lppbpl.android.userapp.db.PendingRecordDb;
import com.lppbpl.android.userapp.listener.SfBTDataListener;
import com.lppbpl.android.userapp.model.PendingRecord;
import com.lppbpl.android.userapp.model.SfActivityModel;
import com.lppbpl.android.userapp.model.SfApplicationModel;
import com.lppbpl.android.userapp.model.SfPinModel;
import com.lppbpl.android.userapp.util.HttpUtil;
import com.lppbpl.android.userapp.util.Util;
import com.pdfbox.EcgPdfBox;

import java.lang.ref.WeakReference;

// TODO: Auto-generated Javadoc
/**
 * AppBaseActivity is a BaseClass for all the Activity used in the application,
 * This class has all the common functions and functionality and interface
 * implementation etc...
 *
 */
public class AppBaseActivity extends Activity implements SfBTDataListener {

	/** The m base activity receiver. */
	private final BaseActivityReceiver mBaseActivityReceiver = new BaseActivityReceiver();

	/** The m act title. */
	private String mActTitle = null;

	/** The m dialog. */
	protected Dialog mDialog = null;

	/** The m title txt. */
	private TextView mTitleTxt;

	/** The m header image. */
	private ImageView mHeaderImage;

	/** The m bt state image. */
	private ImageView mBtStateImage;

	/** The m app crl. */
	protected final SfApplicationController mAppCrl = SfApplicationController
			.getInstance();

	/** The m bt client. */
	protected final BluetoothClient mBtClient = SfApplicationController
			.getInstance().getAppModel().getBluetoothClient();

	/** The m app model. */
	protected final SfApplicationModel mAppModel = SfApplicationController
			.getInstance().getAppModel();

	/** The m act model. */
	protected final SfActivityModel mActModel = SfApplicationController
			.getInstance().getAppModel().getActivityModel();

	/** The m pin model. */
	protected final SfPinModel mPinModel = SfApplicationController
			.getInstance().getAppModel().getPinModel();

	/** The m prog dialog. */
	protected ProgressDialog mProgDialog;

	// private boolean mAppInForeGround = false;

	/** The m dialog type. */
	protected int mDialogType = Constants.SHOW_DIALOGE_NONE;

	protected final static String TAG = AppBaseActivity.class.getSimpleName();

	private boolean isSplash;

	/**
	 * Method onCreate.
	 *
	 * @param bundle
	 *            Bundle
	 */
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		isSplash = this instanceof SplashScreenActivity;
		requestWindowFeature(isSplash ? Window.FEATURE_NO_TITLE : Window.FEATURE_CUSTOM_TITLE);

		// if (isSplash) {
		// SfApplicationController.releaseResources();
		// }

		registerReceiver(mBaseActivityReceiver,((SouthFallsUserApp) getApplication()).getIntentFilter());

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


		mAppCrl.setBluetoothDataListener(this); // SFDATA listner is initialized
		mProgDialog = new ProgressDialog(this);
	}

	/**
	 * Methods returns the mobile network status : connected : true ; else :
	 * false.
	 *
	 * @return boolean
	 */

	// used to check whether wifi or mobile connected or not
	protected boolean isNetworkConnected() {
		boolean connected = false;
		final ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo info = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (null != info) {
			connected = info.isConnected();
		}

		info = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (null != info && !connected) {
			connected = info.isConnected();
		}
		return connected;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		Logger.log(Level.DEBUG, TAG, "onStart()");
		mAppCrl.setBluetoothDataListener(this);
		updateTitleOnBtNotify();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		Logger.log(Level.DEBUG, TAG, "onStop");
	}

	/**
	 * To set the custom header of the Title bar.
	 *
	 * @param titleIcon
	 *            boolean
	 */
	private void setCustomHeader(boolean titleIcon) {
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.window_title);
		mTitleTxt = (TextView) findViewById(R.id.title);
		if (null != mActTitle) {
			if (!titleIcon) {
				final RelativeLayout.LayoutParams lastTxtParams = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
				// llp.setMargins(left, top, right, bottom);
				lastTxtParams.setMargins(
						getResources().getDimensionPixelSize(
								R.dimen.default_margin_left),
						getResources().getDimensionPixelSize(
								R.dimen.default_margin) - 1,
						0,
						getResources().getDimensionPixelSize(
								R.dimen.default_margin) - 1);
				mTitleTxt.setLayoutParams(lastTxtParams);
				mTitleTxt.invalidate();
			}
			mTitleTxt.setText(mActTitle);
		}
		mHeaderImage = (ImageView) findViewById(R.id.header);
		if (!titleIcon) {
			mHeaderImage.setImageBitmap(null);
			mHeaderImage.setVisibility(View.INVISIBLE);
		}
		mBtStateImage = (ImageView) findViewById(R.id.iBTstate);
		setBTStatus(mAppCrl.isDeviceConnected());

	}

	/**
	 * To set the title with any Icon.
	 *
	 * @param strId
	 *            the new custom no icon title
	 */
	protected void setCustomNoIconTitle(int strId) {
		mActTitle = get(strId);
		if (null != mTitleTxt) {
			mTitleTxt.setText(mActTitle);
			setBTStatus(mAppCrl.isDeviceConnected());
		} else {
			setCustomHeader(false);
		}
	}

	/**
	 * set the custom title.
	 *
	 * @param strId
	 *            the new custom title
	 */
	protected void setCustomTitle(int strId) {
		this.setCustomTitle(get(strId));
	}

	/**
	 * set the custom title.
	 *
	 * @param title
	 *            the new custom title
	 */
	private void setCustomTitle(String title) {
		mActTitle = title;
		if (null != mTitleTxt) {
			mTitleTxt.setText(mActTitle);
			setBTStatus(mAppCrl.isDeviceConnected());
		} else {
			setCustomHeader(true);
		}
	}

	/**
	 * set the custom title with an icon.
	 *
	 * @param resId
	 *            the new custom header image
	 */
	protected void setCustomHeaderImage(int resId) {
		if (null != mHeaderImage) {
			mHeaderImage.setImageDrawable(getResources().getDrawable(resId));
			mHeaderImage.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * set the bluetooth status icon in custom title bar.
	 *
	 * @param btOn
	 *            the new BT status
	 */
	private void setBTStatus(boolean btOn) {
		if (null != mBtStateImage) {
			mBtStateImage.setImageDrawable(getResources().getDrawable(
					btOn ? R.drawable.ic_status_bt_pairing_on
							: R.drawable.ic_status_bt_pairing_off));
			mBtStateImage.setVisibility(View.VISIBLE);
			mBtStateImage.invalidate();
			Logger.log(Level.DEBUG, TAG, "BTStatus is " + (btOn ? "On" : "Off"));
		} else {
			Logger.log(Level.DEBUG, TAG, "BTStatus is null");
		}
	}

	/**
	 * To show the custom dialog screen when @param negButTxt set as false and @param
	 * isDismiss set as true; onPositiveButtonClick() will not get call. when @param
	 * negButTxt set as false and @param isDismiss set as false;
	 * onPositiveButtonClick() will get call.
	 *
	 * @param icontTitle
	 *            int
	 * @param title
	 *            String
	 * @param body
	 *            String
	 * @param posBtnTxt
	 *            String
	 * @param negButTxt
	 *            String
	 * @param isDismiss
	 *            boolean
	 */
	protected void showAlertDialog(final int icontTitle, String title,
			String body, String posBtnTxt, String negButTxt,
			final boolean isDismiss) {
		if (isFinishing()) {
			Logger.log(Level.DEBUG, TAG, "showAlertDialog () isFinishing()");
			return;
		}
		// dialog = new Dialog(getApplicationContext());
		final boolean btnsPresent = ((null != posBtnTxt) && (null != negButTxt)) ? true
				: false;
		CustomDialogBuilder builder;
		// To handle black line space on top and bottom of the dialog
		if (Build.VERSION.SDK_INT <= 10) {
			builder = new CustomDialogBuilder(this, btnsPresent);
		} else {
			builder = new CustomDialogBuilder(this, btnsPresent, false);
		}
		if (null != title) {
			builder.setTitle(title);
		}
		if (null != body) {
			builder.setMessage(body);
		}
		builder.setIcon((icontTitle == -1) ? R.drawable.ic_dialog_info
				: icontTitle);

		if (null != posBtnTxt) {
			builder.setPositiveButton(posBtnTxt,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialogInterface,
								int which) {
							mDialog.dismiss();
							if (!isDismiss) {
								onPositiveButtonClick();
							}
						}
					});
		}

		if (null != negButTxt) {
			builder.setNegativeButton(negButTxt,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface,
								int which) {
							mDialog.dismiss();
							onNegativeButtonClick();
						}
					});
		}

		if (null != mDialog) {
			if (mDialog.isShowing()) {
				mDialog.dismiss();
				mDialog = null;
			}
		}
		mDialog = builder.create();
		mDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				mDialog.dismiss();
				if (btnsPresent) {
					onNegativeButtonClick();
				} else {
					onPositiveButtonClick();
				}
			}
		});
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setCancelable(true);
		builder.setDialogInterface(mDialog);
		mDialog.show();
	}

	/**
	 * On negative button click.
	 */
	protected void onNegativeButtonClick() {
	}

	/**
	 * To show the custom dialog screen when @param negButTxt set as false and @param
	 * isDismiss set as true; onPositiveButtonClick() will not get call. when @param
	 * negButTxt set as false and @param isDismiss set as false;
	 * onPositiveButtonClick() will get call.
	 *
	 * @param title
	 *            String
	 * @param body
	 *            String
	 * @param posBtnTxt
	 *            String
	 * @param negButTxt
	 *            String
	 * @param isDismiss
	 *            boolean
	 */
	protected void showAlertDialog(String title, String body, String posBtnTxt,
			String negButTxt, final boolean isDismiss) {

		this.showAlertDialog(-1, title, body, posBtnTxt, negButTxt, isDismiss);
	}

	/**
	 * Update bluetooth status in custom title bar.
	 */
	protected void updateTitleOnBtNotify() {
		setBTStatus(mAppCrl.isDeviceConnected());
	}

	/**
	 * method getting called when Dialog is invoked.
	 */

	protected void onPositiveButtonClick() {
		if (mDialogType == Constants.SHOW_CATCHED_REC_DIALOGE) {
			mDialogType = Constants.SHOW_DIALOGE_NONE;
			final Intent intent = new Intent(this, UnsavedRecordsList.class);
			startActivity(intent);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Logger.log(Level.DEBUG, TAG, "**onDestroy()**");
		unregisterReceiver(mBaseActivityReceiver);
	}

	/**
	 * Handler is used for handling the bluetooth status: :Connecting :Connected
	 * :Disconnecting :Disconnected.
	 */
	private final MyHandler mBTHandler = new MyHandler(this);

	/**
	 * The Class MyHandler.
	 */
	private static class MyHandler extends Handler {

		/** The m activity. */
		private final WeakReference<AppBaseActivity> mActivity;

		/**
		 * Constructor for MyHandler.
		 *
		 * @param mActivity
		 *            AppBaseActivity
		 */
		private MyHandler(AppBaseActivity mActivity) {
			this.mActivity = new WeakReference<AppBaseActivity>(mActivity);
		}

		/**
		 * Method handleMessage.
		 *
		 * @param msg
		 *            android.os.Message
		 */
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			final AppBaseActivity parent = mActivity.get();

			Logger.log(Level.INFO,TAG,"getts message="+msg.what);

			if ((msg.what == Constants.CONNECTION_LOST_REMOTE)
					|| (msg.what == Constants.CONNECTION_ERROR)
					|| (msg.what == Constants.REQUEST_RECONNECTION)) {
				Logger.log(Level.DEBUG, TAG, "msg.what=" + msg.what);
				if (parent instanceof BluetoothPairingActivity) {
					Logger.log(Level.DEBUG, TAG, "AppBaseActivity.this "
							+ "instanceof BluetoothPairingActivity");
					sendEmptyMessage(BluetoothPairingActivity.BT_UNABLE_TO_CONNECT);
					return;
				}

				if (parent.getIntent().getBooleanExtra(Constants.SHOW_MAINMENU,false)) {
					parent.finish();
				}

				parent.updateTitleOnBtNotify();
				if (msg.what == Constants.REQUEST_RECONNECTION) {
					Logger.log(Level.DEBUG, TAG, "Constants.REQUEST_RECONNECTION");
					parent.mAppCrl.setConfigAckRecived(false);
					parent.mBtClient.reportConnectionError(Constants.REQUEST_RECONNECTION);
				}
			} else if (msg.what == Constants.CONNECTION_ESTABLISHED) {
				Logger.log(Level.INFO,TAG,"Handle message="+"Connection Established");
				parent.updateTitleOnBtNotify();
				if (parent instanceof BluetoothPairingActivity) {
					return;
				}
				Logger.log(Level.INFO,TAG,"Handle message="+"Send Device data request");
			// this code is important one
				parent.mAppCrl.sendDeviceDataRequest();
			} else if (msg.what == Constants.CACHED_MEASUREMENTS) {
				parent.notifyCachedRecords(msg);
			}
		}
	}

	/**
	 * BaseActivityReceiver class will receive BluetoothDevice actions like
	 * disconnected states. finishing the all the activities. If BluetoothDevice
	 * disconnected will send message to handler for reconnection.
	 *
	 */
	private class BaseActivityReceiver extends BroadcastReceiver {
		/**
		 * Method onReceive.
		 *
		 * @param context
		 *            Context
		 * @param intent
		 *            Intent
		 */
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (action.equals(Constants.FINISH_ALL_ACTIVITIES)) {
				finish();
			} else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)
					|| action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {

				Logger.log(Level.DEBUG, TAG,"***"+BluetoothAdapter.ACTION_STATE_CHANGED);
				final boolean remoteDevStatus = BluetoothDevice.ACTION_ACL_DISCONNECTED
						.equals(action);
				final boolean devStatus = (intent.getIntExtra(
						BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF)
						|| (intent
								.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_TURNING_OFF);

				Logger.log(Level.DEBUG, TAG, "remote device ACTION_ACL_DISCONNECTED="
								+ remoteDevStatus);
				Logger.log(
						Level.DEBUG,
						TAG,
						"devStatus=state_off:"
								+ (intent.getIntExtra(
										BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF));
				Logger.log(
						Level.DEBUG, TAG, "devStatus=state_turning_off:" + (intent.getIntExtra(
										BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_TURNING_OFF));
				if (remoteDevStatus || devStatus) {
					/*
					 * Dont report any bluetooth state info when user is the
					 * following screen activity: SplashScreenActivity
					 * TnCnPrivacyActivity
					 */
					// Not Required since we removed the Terms and Condition
					// screen
					final boolean isTnC = AppBaseActivity.this instanceof TnCnPrivacyActivity;
					if (!isSplash && !isTnC) {
						mBTHandler.sendEmptyMessage(Constants.REQUEST_RECONNECTION);
					}
				}
			}
		}
	}

	/**
	 * Load the the device id.
	 *
	 * @return String
	 */
	protected String loadDeviceId() {
		Logger.log(Level.DEBUG, TAG, "loadDeviceId()");
		final SharedPreferences saveUserData = getSharedPreferences(Constants.USER_DETAILS_DB, MODE_PRIVATE);
		final String deviceId = saveUserData.getString(Constants.SAVE_DEVICE_ID, "");
		Logger.log(Level.DEBUG, TAG, "deviceId =" + deviceId);
		return deviceId;
	}

	/**
	 * Load the the device version.
	 *
	 * @return String
	 */
	protected String loadDeviceVersion() {
		Logger.log(Level.DEBUG, TAG, "loadDeviceId()");
		final SharedPreferences saveUserData = getSharedPreferences(
				Constants.USER_DETAILS_DB, MODE_PRIVATE);
		final String deviceVersion = saveUserData.getString(
				Constants.SAVE_DEVICE_VERSION, "");
		Logger.log(Level.DEBUG, TAG, "deviceVersion =" + deviceVersion);
		return deviceVersion;
	}

	/**
	 * To finish all the activities in app.
	 */
	protected void closeAllActivities() {
		Logger.log(Level.INFO, TAG, "closeAllActivities()");
		sendBroadcast(new Intent(Constants.FINISH_ALL_ACTIVITIES));
		// if (mAppCrl.isDeviceConnected() || Build.VERSION.SDK_INT <= 10) {
		SouthFallsUserApp.getInstance().onTerminate();
		// }
		if (Build.VERSION.SDK_INT >= 18 /* && Build.MODEL.equals("Nexus 7") */) {
			throw new BTCloseException("BT Connection closed");
		}
	}

	/**
	 * Returns true when app is InForeGround else returns false.
	 *
	 * @param msg
	 *            android.os.Message
	 */
	// protected boolean isAppInForeGround() {
	// return mAppInForeGround;
	// }

	/**
	 * show the cache records dialog
	 *
	 * @param msg
	 */
	private void notifyCachedRecords(Message msg) {
		final String alertMsg = (String) msg.obj;
		if (null != alertMsg) {
			mDialogType = Constants.SHOW_CATCHED_REC_DIALOGE;
			showAlertDialog(get(R.string.catched_records), alertMsg,
					get(R.string.view_now),
					getResources().getString(R.string.later), false);
		}
	}

	/**
	 * Method used to send the SFMessaging data to device(Hardware tool kit)
	 * using bluetooth.
	 *
	 * @param message
	 *            the message
	 */
	protected void sendCommand(SFMessaging message) {
		mBtClient.sendCommand(message);
	}

	/**
	 * Dismiss the dialog.
	 */
	protected void dismissProgressDislog() {
		if (mProgDialog.isShowing()) {
			mProgDialog.dismiss();
		}
	}

	/**
	 * Method isProgDialogShowing.
	 *
	 * @return boolean
	 */
	protected boolean isProgDialogShowing() {
		return mProgDialog.isShowing();
	}

	/**
	 * Show the progressDialog.
	 *
	 * @param stringID
	 *            the string id
	 */
	protected void showProgressDialog(int stringID) {
		showProgressDialog(get(stringID));
	}

	/**
	 * Show the progressDialog.
	 *
	 * @param progressMsg
	 *            the progress msg
	 */
	protected void showProgressDialog(final String progressMsg) {
		if (isFinishing()) {
			Logger.log(Level.DEBUG, TAG, "showProgressDialog () isFinishing()");
			return;
		}

		if (mProgDialog.isShowing()) {
			mProgDialog.cancel();
		}
		mProgDialog.setCanceledOnTouchOutside(false);
		mProgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgDialog.setMessage(progressMsg);
		mProgDialog.setIndeterminate(true);
		mProgDialog.show();
	}

	/**
	 * Change the message in existing Dialog.
	 *
	 * @param stringID
	 *            the string id
	 */
	protected void updateProgressDialog(int stringID) {
		mProgDialog.setMessage(get(stringID));
	}

	/**
	 * Change the message in existing Dialog.
	 *
	 * @param progressMsg
	 *            the progress msg
	 */
	protected void updateProgressDialog(String progressMsg) {
		mProgDialog.setMessage(progressMsg);
	}

	/**
	 * Get the String in the resource by passing resourceId.
	 *
	 * @param stringId
	 *            the string id
	 * @return String
	 */
	protected String get(int stringId) {
		try {
			return getResources().getString(stringId);
		} catch (NotFoundException nfe) {
			Logger.log(Level.DEBUG, TAG, "Error occured : " + nfe.getMessage());
			return "String Not Loaded";
		}
	}

	/**
	 * Read the meta-data which is defined in Androidmenifest.xml file
	 *
	 * @param name
	 *            the name
	 * @return boolean
	 */
	protected boolean canBooleanAppProperty(String name) {
		boolean value = false;
		final Object object = getMetaData(name);
		if (null != object && object instanceof Boolean) {
			value = ((Boolean) object).booleanValue();
		}
		return value;
	}

	/**
	 * Read the meta-data which is defined in Androidmenifest.xml file
	 *
	 * @param name
	 *            the name
	 * @return String
	 */
	protected String getStringAppProperty(String name) {
		String value = null;
		final Object object = getMetaData(name);
		if (null != object && object instanceof String) {
			value = ((String) object);
		}
		return value;
	}

	/**
	 * Method getIntAppProperty.
	 *
	 * @param name
	 *            String
	 * @return int
	 */
	protected int getIntAppProperty(String name) {
		int value = 0;
		final Object object = getMetaData(name);
		if (null != object && object instanceof Integer) {
			value = ((Integer) object).intValue();
		}
		return value;
	}

	/**
	 * Reads customization entry from the Androidmenifest.xml file
	 *
	 * @param name
	 *            String
	 * @return Object
	 */
	private Object getMetaData(String name) {
		try {
			final ApplicationInfo appInfo = getPackageManager()
					.getApplicationInfo(getPackageName(),
							PackageManager.GET_META_DATA);
			final Bundle metaData = appInfo.metaData;
			if (null == metaData) {
				Logger.log(Level.DEBUG, TAG,
						"metaData is null. Unable to get meta data for " + name);
			} else {
				return metaData.get(name);
			}
		} catch (NameNotFoundException e) {
			Logger.log(Level.ERROR, TAG, "" + e);
		}

		return null;
	}

	/**
	 * Data received from Davice using bluetooth.
	 *
	 * @param response
	 *            Response
	 * @see SfBTDataListener#dataReceived(Response)
	 */
	@Override
	public void dataReceived(Response response) {

		final boolean skipPair = getIntent().getBooleanExtra("skip_pair", false);

		if (response.getResponseType() == ResponseType.ERR
				&& response.getErrCode() == ResErrorCode.MES_ECG_NO_LEAD_CONFIG) {
			sendCommand(Util.getConfigMessage());
		} else if (response.getResponseType() == ResponseType.SRD
				&& response.getMeasurementType() == Measurement.CACHED) {

			handleCachedMeasurements(response);

			final SfActivityModel actModel = mAppModel.getActivityModel();
			if (actModel.isActivityRunningInBackground() && !actModel.isRunning()) {

				final GetData getActivityData = GetData.newBuilder()
						.setDataType(DataType.SensorData)
						.setMeasurementType(Measurement.ACT).build();

				final SFMessaging message = SFMessaging.newBuilder()
						.setSfMsgType(MessageType.GetDataMsg)
						.setSfGetdataMsg(getActivityData).build();
				sendCommand(message);
				Logger.log(Level.DEBUG, TAG, "Sent request for Activity data");
				actModel.setRunning(true);
				actModel.startGetDataTimer(Constants.GET_DATA_ACTIVITY_IN_MILLISEC);
			}
		} else if ((null != mAppModel.getDeviceData() || skipPair)
				&& response.hasDevData()) {
			Logger.log(Level.DEBUG, TAG, "Devicedata response:" + response.toString());
			/*
			 * While activity running, when accessory re started or power
			 * shutdown then user app should cancel the activity
			 */
			if (!response.getDevData().hasActivityRunning()) {
				if (mActModel.isRunning()) {
					mActModel.stopActivity();
				}
			}

			if (!mAppCrl.isConfigAckRecived()) {
				mBtClient.sendCommand(Util.getConfigMessage());
			}
		} else if (response.hasActData()) {
			final PendingRecord pendRec = mAppModel.getPendingRecord();
			if (null != pendRec && pendRec.isActivityRecord()) {
				pendRec.setResponse(response);
			}
			// Below code update the device status in app when shutdown idle
			// timeout occurred
		} else if (response.getResponseType() == ResponseType.STT
				&& response.getSttCode() == ResStatusCode.CLIENT_SHUTDOWN) {
			Logger.log(Level.DEBUG, TAG, "shutdown idle timeout occurred");
			mBtClient.close();
			if (Build.VERSION.SDK_INT >= 18 /* && Build.MODEL.equals("Nexus 7") */) {
				throw new BTCloseException("BT Connection closed idle timeout");
			}
		} else if (response.getResponseType() == ResponseType.ACK
				&& 0 == response.getMeasurementType() && !mAppCrl.isConfigAckRecived()) {
			Logger.log(Level.DEBUG, TAG, "Config message ACK received.");
			if (!mAppCrl.isConfigAckRecived()) {
				if (this instanceof BluetoothPairingActivity) {
					return;
				}
				mAppCrl.setConfigAckRecived(true);
			}
		}
	}

	/**
	 * Used to send the message to activity.
	 *
	 * @param what
	 *            int
	 * @see SfBTDataListener#sendEmptyMessage(int)
	 */
	@Override
	public void sendEmptyMessage(int what) {
		if (null != mBTHandler) {
			Logger.log(Level.INFO, TAG,"**(Get Empty mesage)**"+what);
			mBTHandler.sendEmptyMessage(what);
		}
	}

	/**
	 * To know whether the device is charging : //true : charging : false : not
	 * charging.
	 *
	 * @return boolean
	 */
	public boolean isDeviceCharging() {
		boolean charging = false;
		final DeviceData data = mAppModel.getDeviceData();
		if (null != data) {
			charging = (1 == data.getIsCharging());
		}
		return charging;
	}

	/**
	 * To know whether Activity is running : true : running ; false : not
	 * running.
	 *
	 * @return boolean
	 */
	public boolean isActivityRunning() {
		if (null != mActModel && mActModel.isActivityRunningInBackground()) {
			return true;
		}

		if (null != mActModel && mActModel.isRunning()) {
			return true;
		}
		return false;
	}

	/**
	 * Checks for the Access token expiry.
	 *
	 *
	 * @return boolean
	 */
	protected boolean isAccessTokenExpired() {
		Logger.log(Level.DEBUG, TAG, "isAccessTokenExpired()");
		Logger.log(Level.DEBUG, TAG, "Token key=" + mAppModel.getTokenKey());
		Logger.log(Level.DEBUG, TAG, "Response Code=" + mAppModel.getResponseCode());

		final boolean isAccessTokenExpired = null != mAppModel.getTokenKey()
				&& mAppModel.getResponseCode() == HttpUtil.HTTP_AUTH_FAILED;
		if (isAccessTokenExpired) {
			clearLoginToken();
		}
		return isAccessTokenExpired;
	}

	/**
	 * remove the existing token when it expires.
	 */
	protected void clearLoginToken() {
		Logger.log(Level.DEBUG, TAG, "clearLoginToken()");
		final SharedPreferences saveLoginToken = getSharedPreferences(
				Constants.USER_LOGIN_TOKEN_DB, MODE_PRIVATE);
		final SharedPreferences.Editor editor = saveLoginToken.edit();
		editor.clear();
		editor.commit();
		mAppModel.setTokenKey(null);
		mPinModel.setLoginForSessionSuccess(false);
		mAppModel.setMemberList(null);
		Logger.log(Level.DEBUG, TAG, "saved access token is=" + mAppModel.getTokenKey());
	}

	/**
	 * Handles Cache data records from bluetooth -while measuring the data if
	 * device got off due to critically low battery.
	 *
	 * @param response
	 *            Response
	 */
	private void handleCachedMeasurements(final Response response) {
		final StringBuilder alertMsg = new StringBuilder(256);
		boolean measurementFound = false;
		int count = 0;
		Logger.log(Level.DEBUG, TAG, "handleCachedMeasurements ++");

		if (response.hasEcgData()) {
			Logger.log(Level.DEBUG, TAG, "response.hasEcgData()");
			final Response ecgResponse = Response.newBuilder()
					.setMeasurementType(Measurement.ECG)
					.setResponseType(ResponseType.SRD)
					.setEcgData(response.getEcgData())
					.setTimestamp(response.getTimestamp()).build();
			final PendingRecord ecgRecord = new PendingRecord(
					PendingRecord.UPLOAD_NOT_STARTED, ecgResponse);
			PendingRecordDb.getInstance().addRecord(ecgRecord);
			alertMsg.append("1 new ECG");
			measurementFound = true;
			count++;
		}

		if (response.hasBgData()) {
			Logger.log(Level.DEBUG, TAG, "response.hasBgData()");
			alertMsg.append(measurementFound ? ", " : "");
			final Response bgResponse = Response.newBuilder()
					.setMeasurementType(Measurement.BG)
					.setResponseType(ResponseType.SRD)
					.setBgData(response.getBgData())
					.setTimestamp(response.getTimestamp()).build();
			final PendingRecord ecgRecord = new PendingRecord(
					PendingRecord.UPLOAD_NOT_STARTED, bgResponse);
			PendingRecordDb.getInstance().addRecord(ecgRecord);
			alertMsg.append("1 new Blood Sugar");
			measurementFound = true;
			count++;
		}

		if (response.hasActData()) {
			Logger.log(Level.DEBUG, TAG, "response.hasActData()");
			alertMsg.append(measurementFound ? ", " : "");
			final Response actResponse = Response.newBuilder()
					.setMeasurementType(Measurement.ACT)
					.setResponseType(ResponseType.SRD)
					.setActData(response.getActData())
					.setTimestamp(response.getTimestamp()).build();
			final PendingRecord ecgRecord = new PendingRecord(
					PendingRecord.UPLOAD_NOT_STARTED, actResponse);
			PendingRecordDb.getInstance().addRecord(ecgRecord);
			alertMsg.append("1 new Activity");
			measurementFound = true;
			count++;
		}
		alertMsg.append((count > 1) ? " "
				+ getString(R.string.plural_measurement) : " "
				+ getString(R.string.single_measurement));
		if (measurementFound) {
			final Message message = new Message();
			message.what = Constants.CACHED_MEASUREMENTS;
			message.obj = alertMsg.toString();
			mBTHandler.sendMessage(message);
			Logger.log(Level.DEBUG, TAG, "sent Message");
		}
		Logger.log(Level.DEBUG, TAG, "handleCachedMeasurements --");
	}


/*	protected boolean isBGModuleEnabled() {
		final String deviceId = mPinModel.getDeviceUid();
		if (deviceId != null) {
			try {
				final String major = deviceId.substring(7, 8);
				final String minor = deviceId.substring(8, 10);

				Logger.log(Level.DEBUG, TAG, "major =" + major + ",minor="
						+ minor);
				Logger.log(Level.DEBUG, TAG,
						get(R.string.device_sw_major_version) + ","
								+ get(R.string.device_sw_minor_version));

				if (major != null
						&& major.equals(get(R.string.device_sw_major_version))
						&& minor != null
						&& minor.equals(get(R.string.device_sw_minor_version))) {
					return false;
				}
			} catch (Exception e) {
				Logger.log(Level.DEBUG, TAG, "e" + e);
			}
		}
		return true;
	}*/

	/*
     * isBGModuleEnabled() is used to test the BG module is enabled or not based
     * on the device id major and minor version
     */
     protected boolean isBGModuleEnabled() {
            final String deviceId = mPinModel.getDeviceUid();
            if (deviceId != null) {
                   try {
                         //Check the position number 5 in device id to check bg module enabled or not
                         final char bgChar = deviceId.charAt(4);
                        if (bgChar != 'B' && bgChar != 'b') {
                                return false; //To SAY  BG MODULE IS DESABLED.
                         }
                   } catch (Exception e) {
                         Logger.log(Level.DEBUG, TAG, "e" + e);
                   }
            }
            return true;
     }


	@Override
	public String toString() {
		return super.toString();
	}


	/*public boolean isStoragePermissionGranted() {
		if (Build.VERSION.SDK_INT >= 23) {
			if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
				Logger.log(Level.DEBUG,TAG,"--Permission is granted--");
				return true;
			} else {

				Logger.log(Level.ERROR,TAG,"--Permission is revoked--");
				ActivityCompat.requestPermissions(this, new String[]
						{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
				return false;
			}
		} else {
			System.out.println("------///Run Time Permission  not required----//");
			return true;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			//Logger.log(Level.DEBUG, TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
			Log.v("TAG","Permission: "+permissions[0]+ "was "+grantResults[0]);
			//(new EcgPdfBox()).createtable(EcgSymptomsListActivity.this);
		}
	}*/

}
