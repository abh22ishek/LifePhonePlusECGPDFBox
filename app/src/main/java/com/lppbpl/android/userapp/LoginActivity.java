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
import java.util.SortedMap;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.DeviceData;
import com.lppbpl.Response;
import com.lppbpl.ResponseType;
import com.lppbpl.android.userapp.R;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.model.SfPinModel;
import com.lppbpl.android.userapp.util.HttpUtil;

// TODO: Auto-generated Javadoc
/**
 * This Activity is to display the login screen.
 *
 */
@SuppressLint("ClickableViewAccessibility")
public class LoginActivity extends AppBaseActivity implements OnClickListener,
		OnTouchListener {

	/** The m user id. */
	private EditText mUserID;

	/** The m set pin. */
	private EditText mSetPin;

	/** The m device id. */
	private TextView mDeviceId;

	/** The m remember user id. */
	private CheckBox mRememberUserID;

	/** The m shutdown ack. */
	private boolean mShutdownAck = false;

	/** The skip login. */
	private boolean skipLogin;

	/** The m http util. */
	private HttpUtil mHttpUtil;

	/** The do login task. */
	private DoLogin doLoginTask;

	/**
	 * Method onCreate.
	 *
	 * @param savedInstanceState
	 *            Bundle
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.login);

		setCustomNoIconTitle(R.string.title_login);

		skipLogin = getIntent().getBooleanExtra("enable_skip", false);
		mDeviceId = (TextView) findViewById(R.id.tvdevid);
		mUserID = (EditText) findViewById(R.id.etUserID);
		mSetPin = (EditText) findViewById(R.id.etSetPin);
		final TextView signup = (TextView) findViewById(R.id.btn_signup);
		mRememberUserID = (CheckBox) findViewById(R.id.remember_user_id);
		final TextView forgotPassView = (TextView) findViewById(R.id.tvForgotPassLink);
		final Button login = (Button) findViewById(R.id.btn_menu_positive);
		login.setText(R.string.login);
		final Button skip = (Button) findViewById(R.id.btn_menu_negative);
		skip.setText(R.string.skip);

		skip.setEnabled(skipLogin ? true : false);
		skip.setTextColor(skipLogin ? Color.BLACK : getResources().getColor(
				R.color.grayed_disable));

		loadUserData();

		mProgDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				if (null != mHttpUtil) {
					mHttpUtil.disconnect();
					mHttpUtil = null;
				}
			}
		});

		final DeviceData data = mAppModel.getDeviceData();
		if (null != data) {
			final String devicekey = data.getDeviceUid();
			mDeviceId.setText(String.format(get(R.string.devicekeyLogin),
					devicekey));
		} else {
			mDeviceId.setText(String.format(get(R.string.devicekeyLogin),
					mPinModel.getDeviceUid()));
		}

		signup.setOnClickListener(this);
		login.setOnClickListener(this);
		skip.setOnClickListener(this);
		forgotPassView.setOnClickListener(this);
		forgotPassView.setOnTouchListener(this);
		signup.setOnTouchListener(this);

		setResult(RESULT_CANCELED);
	}

	/**
	 * Do login process.
	 */
	private void doLoginProcess() {
		final SfPinModel pinModel = mAppModel.getPinModel();
		final DeviceData data = mAppModel.getDeviceData();
		if (null != data) {
			pinModel.setDeviceUid(data.getDeviceUid());
		}
		pinModel.setUserID(mUserID.getText().toString());
		// set login data to pin model
		if (isNetworkConnected()) {
			doLoginTask = new DoLogin(this);
			doLoginTask.execute();
		} else {
			showAlertDialog(R.drawable.ic_dialog_no_signal,
					get(R.string.network_connection),
					HttpUtil.getRespnseMsg(HttpUtil.HTTP_CONNECTION_DOWN),
					get(R.string.OK), null, true);
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
		//
		Logger.log(Level.DEBUG, TAG, "onDestroy");
	}

	/**
	 * load the user id details.
	 */
	private void loadUserData() {
		Logger.log(Level.DEBUG, TAG, "loadUserData()");
		final SharedPreferences saveUserData = getSharedPreferences(
				Constants.USER_DETAILS_DB, MODE_PRIVATE);
		final boolean hasUserNo = saveUserData.getBoolean(
				Constants.REMEMBER_USER_ID, false);
		Logger.log(Level.DEBUG, TAG, "hasUserID =" + hasUserNo);
		mRememberUserID.setChecked(hasUserNo);
		final String userId = saveUserData
				.getString(Constants.SAVE_USER_ID, "");
		mPinModel.setUserID(userId);
		mUserID.setText(hasUserNo ? userId : "");
		Logger.log(Level.DEBUG, TAG, "userId =" + userId);
	}

	/**
	 * Save the user id in shared preference.
	 */
	private void saveUserID() {
		Logger.log(Level.DEBUG, TAG, "saveUserID()");
		final SharedPreferences saveUserData = getSharedPreferences(
				Constants.USER_DETAILS_DB, MODE_PRIVATE);
		final SharedPreferences.Editor editor = saveUserData.edit();
		Logger.log(Level.DEBUG, TAG, "mRememberUserID.isChecked()"
				+ mRememberUserID.isChecked());
		editor.putBoolean(Constants.REMEMBER_USER_ID,
				mRememberUserID.isChecked());
		editor.putString(Constants.SAVE_USER_ID, mUserID.getText().toString());
		editor.commit();
	}

	/**
	 * Save the device id in shared preference.
	 */
	private void saveDeviceId() {
		Logger.log(Level.DEBUG, TAG, "saveDeviceId()");
		final SharedPreferences saveUserData = getSharedPreferences(
				Constants.USER_DETAILS_DB, MODE_PRIVATE);
		final String deviceId = saveUserData.getString(
				Constants.SAVE_DEVICE_ID, "");
		if (!deviceId.equals(mPinModel.getDeviceUid())) {
			final SharedPreferences.Editor editor = saveUserData.edit();
			editor.putString(Constants.SAVE_DEVICE_ID, mPinModel.getDeviceUid());
			final DeviceData data = mAppModel.getDeviceData();
			if (null != data) {
				editor.putString(Constants.SAVE_DEVICE_VERSION, data.getFirmwareVersion());
			}
			editor.commit();
		}
	}

	/**
	 * validates the user entered data while login.
	 *
	 *
	 * @return boolean
	 */
	private boolean isValidUserData() {
		final String userId = mUserID.getText().toString();
		final String pin = mSetPin.getText().toString();
		final String deviceId = mPinModel.getDeviceUid();
		if (null == userId || 0 == userId.trim().length()) {
			showAlertDialog(get(R.string.login), get(R.string.user_id_empty),
					get(R.string.OK), null, true);
			return false;
		} else if (null == pin || 0 == pin.trim().length()) {
			showAlertDialog(get(R.string.login), get(R.string.password_empty),
					get(R.string.OK), null, true);
			return false;
		} else if (null == deviceId || 0 == deviceId.trim().length()) {
			final DeviceData data = mAppModel.getDeviceData();
			Logger.log(Level.INFO, TAG, "data =" + data);
			if (null != data) {
				mPinModel.setDeviceUid(data.getDeviceUid());
				return true;
			} else {
				showAlertDialog(get(R.string.login),
						get(R.string.deviceid_empty), get(R.string.OK), null,
						true);
			}
			return false;
		}
		return true;
	}

	/**
	 * Method onClick.
	 *
	 * @param view
	 *            View
	 * @see android.view.View$OnClickListener#onClick(View)
	 */
	@Override
	public void onClick(final View view) {
		int i = view.getId();
		if (i == R.id.btn_signup) {
			Logger.log(Level.DEBUG, TAG, "start Activity for Registration");
			final Intent startApp = new Intent(this, RegistrationActivity.class);
			startActivity(startApp);

		} else if (i == R.id.btn_menu_positive) {
			if (isValidUserData()) {
				saveUserID();
				doLoginProcess();
				return;
			}

		} else if (i == R.id.btn_menu_negative) {
			mDialogType = Constants.SHOW_LOGIN_SKIP_DIALOG;
			showAlertDialog(get(R.string.skip),
					get(R.string.skip_confirmation), get(R.string.OK),
					get(R.string.cancel), false);

		} else if (i == R.id.tvForgotPassLink) {
			final Intent intent = new Intent(this, ForgotPasswordActivity.class);
			startActivity(intent);

		} else {
		}
	}

	/**
	 * AysncTask to update the Device id after login if device id got mismatch
	 * to the login user id.
	 *
	 */
	// private class DoUpdateDeviceId extends AsyncTask<Void, Void, String> {

	/*
	 * (non-Javadoc)
	 *
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	// @Override
	// protected void onPreExecute() {
	// super.onPreExecute();
	// showProgressDialog(R.string.updating_device);
	// mHttpUtil = new HttpUtil();
	// }

	/**
	 * Method doInBackground.
	 *
	 * @param arg0
	 *            Void[]
	 * @return String
	 */
	// @Override
	// protected String doInBackground(Void... arg0) {
	//
	// final SortedMap<String, String> reqHeaders = mHttpUtil
	// .getCommonRequestHeader();
	// final SortedMap<String, String> reqParams = new TreeMap<String,
	// String>();
	// reqHeaders.put(HttpUtil.ACCEPT_TYPE, HttpUtil.APPLICATION_JSON);
	// reqHeaders
	// .put(HttpUtil.CONTENT_TYPE, HttpUtil.FORM_URLENCODED_TYPE);
	//
	// reqParams.put("Username", mPinModel.getUserID());
	// reqParams.put("deviceID", mPinModel.getDeviceUid());
	//
	// final String URL = mPinModel.getServerAddress()
	// + Constants.UPDATE_DEVICE_ID + mPinModel.getDeviceUid();
	//
	// final String status = mHttpUtil.postDataThroughParams(
	// HttpUtil.POST, URL, reqParams, reqHeaders);
	//
	// if (Logger.isDebugEnabled()) {
	// Logger.log(Level.DEBUG, TAG, "Login status: " + status);
	// }
	//
	// return status;
	// }

	/**
	 * Method onPostExecute.
	 *
	 * @param status
	 *            String
	 */
	// @Override
	// protected void onPostExecute(String status) {
	// super.onPostExecute(status);
	// dismissProgressDislog();
	// if (mAppModel.getResponseCode() == HttpUtil.HTTP_RESPONSE_SUCCESS) {
	// if (skipLogin) {
	// callMainMenuAactivity();
	// } else {
	// saveLoginToken();
	// saveDeviceId();
	// mPinModel.setLoginForSessionSuccess(true);
	// setResult(RESULT_OK);
	// finish();
	// }
	// } else {
	// if (null != status) {
	// mDialogType = Constants.SHOW_UPDATE_DEVICE_ID_ERROR_DIALOG;
	// showAlertDialog(R.drawable.ic_dialog_info,
	// LoginActivity.this.get(R.string.information),
	// status, LoginActivity.this.get(R.string.OK), null,
	// false);
	// } else {
	// showLoginStatusMsg((null != status) ? status : HttpUtil
	// .getRespnseMsg(mAppModel.getResponseCode()));
	// }
	// }
	// }
	// }

	/**
	 * First time After login success calls the Main menu screen.
	 */
	private void callMainMenuAactivity() {
		Logger.log(Level.DEBUG, TAG, "callMainMenuAactivity()");
		mPinModel.setLoginForSessionSuccess(true);
		saveLoginToken();
		saveDeviceId();
		Logger.log(Level.DEBUG, TAG, "start Activity for MainMenu");
		final Intent intent = new Intent(LoginActivity.this,
				MainMenuActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * Show the error dialog of login status.
	 *
	 * @param status
	 *            the status
	 */
	private void showLoginStatusMsg(String status) {
		showAlertDialog(R.drawable.ic_dialog_error,
				get(R.string.title_authentication), status, get(R.string.OK),
				null, true);
	}

	/**
	 * Asyntask will process the login request to cloud.
	 *
	 */
	private class DoLogin extends AsyncTask<Void, Void, String> {

		/** The error code. */
		private String errorCode = null;

		/** The error msg. */
		private String errorMsg = null;

		/** The token. */
		private String token = null;

		/** The parent. */
		private final LoginActivity parent;

		/**
		 * Constructor for DoLogin.
		 *
		 * @param parent
		 *            LoginActivity
		 */
		private DoLogin(LoginActivity parent) {
			this.parent = parent;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog(R.string.auth_progress);
			mHttpUtil = new HttpUtil();
		}

		/**
		 * Method doInBackground.
		 *
		 * @param arg0
		 *            Void[]
		 * @return String
		 */
		@Override
		protected String doInBackground(Void... arg0) {

			final SortedMap<String, String> reqHeaders = mHttpUtil
					.getCommonRequestHeader();
			final SortedMap<String, String> reqParams = new TreeMap<String, String>();
			reqHeaders.put(HttpUtil.ACCEPT_TYPE, HttpUtil.PLAIN_TEXT_TYPE);
			reqHeaders
					.put(HttpUtil.CONTENT_TYPE, HttpUtil.FORM_URLENCODED_TYPE);

			reqParams.put("userid", mPinModel.getUserID());
			reqParams.put("pin", mSetPin.getText().toString());
			reqParams.put("deviceid", mPinModel.getDeviceUid());

			final String URL = mPinModel.getServerAddress()
					+ Constants.LOGIN_URL;

			mAppModel.setTokenKey(null);
			String status = mHttpUtil.postDataThroughParams(HttpUtil.POST, URL,
					reqParams, reqHeaders);

			if (Logger.isDebugEnabled()) {
				Logger.log(Level.DEBUG, TAG, "Login status: " + status);
			}

			if (mAppModel.getResponseCode() == HttpUtil.HTTP_RESPONSE_SUCCESS) {
				mAppModel.setTokenKey(status);
				status = Constants.LOGIN_SUCCESS;
				// After the login success, Make a API call to fetch the User
				// Role to know, primary user or secondary user
				final String[] userRole = getUserRole();
				if (null != userRole[0] && null != userRole[1]) {
					mAppModel.setUserRole(userRole[0]);
					mAppModel.setUserUniqueId(userRole[1]);
				} else {
					status = null;
				}
				Logger.log(Level.DEBUG, TAG, "response =" + userRole);
			} else if (mAppModel.getResponseCode() == HttpUtil.HTTP_RESPONSE_SUCCESS_WITH_CONDITION) {
				parseLoginResponse(status);
				// After the login success, Make a API call to fetch the User
				// Role to know, primary user or secondary user
				final String[] userRole = getUserRole();
				if (null != userRole[0] && null != userRole[1]) {
					mAppModel.setUserRole(userRole[0]);
					mAppModel.setUserUniqueId(userRole[1]);
					status = Constants.LOGIN_FAIL;
				} else {
					errorCode = null;
					errorMsg = null;
					token = null;
					status = parent.get(R.string.unexpected_error);
					mAppModel.setTokenKey(null);
				}
			} else if (mAppModel.getResponseCode() == HttpUtil.HTTP_AUTH_FAILED
					|| mAppModel.getResponseCode() == HttpUtil.HTTP_BAD_REQUEST) {
				parseLoginResponse(status);
			}

			return status;
		}

		/**
		 * Method getUserRole.
		 *
		 * @return String
		 */
		private String[] getUserRole() {
			final String[] string = new String[2];
			String userRole = null;
			final String requestUri = mPinModel.getServerAddress()
					+ Constants.IS_PRIMARY + mAppModel.getTokenKey();

			TreeMap<String, String> reqHeaders = mHttpUtil
					.getCommonRequestHeader();
			reqHeaders.put(HttpUtil.ACCEPT_TYPE, HttpUtil.APPLICATION_JSON);
			reqHeaders.put(HttpUtil.CONTENT_TYPE, HttpUtil.APPLICATION_JSON);
			final String responseText = mHttpUtil.getData(HttpUtil.GET,
					requestUri, reqHeaders);

			Logger.log(Level.DEBUG, TAG, "requestUri =" + requestUri);
			if (null != responseText) {
				try {
					Logger.log(Level.DEBUG, TAG, "responseText ="
							+ responseText);
					final JSONObject object = new JSONObject(responseText);
					final JSONArray jsonArr = object.getJSONArray("roles");
					if (jsonArr.length() > 0) {
						userRole = jsonArr.getString(0);
					}

					final JSONObject userObj = object.getJSONObject("user");
					final int userId = userObj.getInt("id");

					string[0] = userRole;
					string[1] = Integer.toString(userId);
				} catch (JSONException e) {
					Logger.log(Level.ERROR, TAG, "" + e);
				}

			}

			Logger.log(Level.DEBUG, TAG, "userRole =" + userRole);
			Logger.log(Level.DEBUG, TAG, "userId =" + string[1]);

			return string;
		}

		/**
		 * Method onPostExecute.
		 *
		 * @param result
		 *            String
		 */
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dismissProgressDislog();
			processLoginResponse(result);
		}

		/**
		 * Method parseLoginResponse.
		 *
		 * @param response
		 *            String
		 */
		private void parseLoginResponse(String response) {
			if (null != response) {
				String[] msgs = null;
				if (response.contains("ERR005-INCOMP-PROFILE")) {
					msgs = response.split(":", 2);
				} else {
					msgs = response.split(":");
				}
				if (null != msgs && msgs.length > 0) {
					errorCode = msgs[0];
				}

				Logger.log(Level.DEBUG, TAG, "errorCode =" + errorCode);
				if (null != msgs && msgs.length > 1) {
					errorMsg = msgs[1];
				}

				Logger.log(Level.DEBUG, TAG, "errorMsg =" + errorMsg);
				if (null != msgs && msgs.length > 2) {
					token = msgs[2];
				}

				Logger.log(Level.DEBUG, TAG, "token =" + token);
				mAppModel.setTokenKey(token);
			}
		}

		/**
		 * Method processLoginResponse.
		 *
		 * @param mStatus
		 *            String
		 */
		private void processLoginResponse(final String mStatus) {
			Logger.log(Level.INFO, TAG,
					"login Response code:" + mAppModel.getResponseCode());
			if (null == mStatus || 0 == mStatus.trim().length()) {
				Logger.log(Level.INFO, TAG, "Post login data status is null");
				showLoginStatusMsg(HttpUtil
						.getRespnseMsg(isNetworkConnected() ? mAppModel
								.getResponseCode()
								: HttpUtil.HTTP_CONNECTION_DOWN));

			} else {
				if (mStatus.equalsIgnoreCase(Constants.LOGIN_SUCCESS)) {
					if (Logger.isInfoEnabled()) {
						Logger.log(Level.INFO, TAG,
								"Post login data status = success");
					}
					final boolean updateDeviceId = getIntent().getBooleanExtra(
							"deviceid_mismatch", false);
					Logger.log(Level.DEBUG, TAG, "deviceid_mismatch:"
							+ updateDeviceId);
					if (updateDeviceId && skipLogin) {
						callMainMenuAactivity();
					} else {
						/*
						 * If Login success : save access token else save null
						 * value
						 */
						saveLoginToken();
						saveDeviceId();
						mPinModel.setLoginForSessionSuccess(true);
						setResult(RESULT_OK);
						finish();
					}
				} else {
					processLoginFaliure(mStatus);
				}
			}
		}

		private void processLoginFaliure(final String mStatus) {

			if (null != errorCode) {
				// Show error message when Account is locked || PROFILE
				// INCOMPLETE || NOT APPORVED
				if ("ERR001-ACCTLOCK".equals(errorCode)
						|| "ERR005-INCOMP-PROFILE".equals(errorCode)
						|| "ERR006-NOT-APPROVED".equals(errorCode)) {
					showLoginStatusMsg(errorMsg);
				}
				// Show the Terms and Condition screen and ask the user
				// to accept it.
				else if ("ERR004-ACCEPT-TNC".equals(errorCode)) {
					final Intent intent = new Intent(parent,
							TnCnPrivacyActivity.class);
					startActivityForResult(intent, 2);
				}
				// Show the TPA screen and ask the user
				// to accept it.
				else if ("ERR016-ACCEPT-TPA".equals(errorCode)) {
					final Intent intent = new Intent(parent, TPAActivity.class);
					intent.putExtra(TPAActivity.KEY_CALL_INFO_API, false);
					intent.putExtra(TPAActivity.KEY_FROM_ACTIVITY, TPAActivity.FROM_LOGIN_ACTIVITY);
					startActivityForResult(intent, 3);
				}
				else if ("ERR008-DEVICE_DEACTIVE".equals(errorCode)) {
					showLoginStatusMsg(errorMsg);
				}
				// SHow error msg when Device is mismatch and ask the
				// user for update it.
				else if ("ERR010-DEVICE_IN_USE".equals(errorCode)
						|| "ERR011-INVALID_DEVICE_ID".equals(errorCode)
						|| "ERR013-NOT_DEVICE_USER".equals(errorCode)) {
					mDialogType = Constants.SHOW_UPDATE_DEVICE_ID_ERROR_DIALOG;
					showAlertDialog(R.drawable.ic_dialog_info,
							parent.get(R.string.title_authentication),
							errorMsg, parent.get(R.string.OK), null, false);
				}/*
				 * else if ("ERR007-DEVICE_MISMATCH".equals(errorCode)) {
				 * mDialogType = Constants.SHOW_UPDATE_DEVICE_ID_DIALOG;
				 * showAlertDialog( R.drawable.ic_dialog_info,
				 * parent.get(R.string.title_authentication), (null == errorMsg)
				 * ? parent .get(R.string.update_device_id) : errorMsg,
				 * parent.get(R.string.save), parent.get(R.string.exit_app),
				 * false); }
				 */// Ask the user to change the password when password
					// expired or To change the default password
				else if ("ERR003-PASSWDCHG".equals(errorCode)) {
					mDialogType = Constants.SHOW_CHANGE_PASSWORD_DIALOG;
					showAlertDialog(
							R.drawable.ic_dialog_info,
							parent.get(R.string.title_authentication),
							(null == errorMsg) ? parent
									.get(R.string.change_password_info)
									: errorMsg, parent.get(R.string.OK), null,
							false);
				} else if ("ERR002-PASSWDEXP".equals(errorCode)) {
					mDialogType = Constants.SHOW_CHANGE_PASSWORD_DIALOG;
					showAlertDialog(
							R.drawable.ic_dialog_info,
							parent.get(R.string.title_authentication),
							(null == errorMsg) ? parent
									.get(R.string.password_expired_info)
									: errorMsg, parent.get(R.string.OK), null,
							false);
				} else {
					showLoginStatusMsg(isNetworkConnected() ? mStatus
							: HttpUtil
									.getRespnseMsg(HttpUtil.HTTP_CONNECTION_DOWN));
				}
				if (Logger.isInfoEnabled()) {
					Logger.log(Level.INFO, TAG,
							"Post login data status = Failed");
				}
			} else {
				if (mAppModel.getResponseCode() == HttpUtil.HTTP_PAGE_NOT_FOUND) {
					showLoginStatusMsg(parent
							.get(R.string.http_pagenotfound_error));
				} else if (mAppModel.getResponseCode() == HttpUtil.HTTP_SERVER_UNAVAILABLLE) {
					showLoginStatusMsg(parent
							.get(R.string.http_server_unavailable));
				} else {
					showLoginStatusMsg(isNetworkConnected() ? mStatus
							: HttpUtil
									.getRespnseMsg(HttpUtil.HTTP_CONNECTION_DOWN));
				}
			}

		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		Logger.log(Level.DEBUG, TAG, "onBackPressed()");
		final boolean updateDeviceId = getIntent().getBooleanExtra(
				"deviceid_mismatch", false);
		Logger.log(Level.DEBUG, TAG, "deviceid_mismatch:" + updateDeviceId);
		if (updateDeviceId) {
			mShutdownAck = true;
			mAppCrl.sendShutdownMsg();
			mHandler.sendEmptyMessageDelayed(Constants.SHUTDOWN_TIMEOUT,
					Constants.SHUTDOWN_TIMEOUT_DELAY);
			// closeAllActivities();
		} else if (getIntent().getBooleanExtra("show_discard_dialog", false)) {
			mDialogType = Constants.SHOW_DISCARD_DIALOGE;
			showAlertDialog(R.drawable.ic_dialog_delete,
					get(R.string.discard_record),
					get(R.string.discard_confirmation), get(R.string.discard),
					get(R.string.cancel), false);
		} else {
			super.onBackPressed();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.lppbpl.android.userapp.AppBaseActivity#onPositiveButtonClick()
	 */
	@Override
	protected void onPositiveButtonClick() {
		// if (mDialogType == Constants.SHOW_UPDATE_DEVICE_ID_DIALOG) {
		// new DoUpdateDeviceId().execute();
		// } else
		if (mDialogType == Constants.SHOW_CHANGE_PASSWORD_DIALOG) {
			final Intent changePsw = new Intent(this,
					ChangePasswordActivity.class);
			startActivityForResult(changePsw, 1);
		} else if (mDialogType == Constants.SHOW_UPDATE_DEVICE_ID_ERROR_DIALOG) {
			mShutdownAck = true;
			mAppCrl.sendShutdownMsg();
			mHandler.sendEmptyMessageDelayed(Constants.SHUTDOWN_TIMEOUT,
					Constants.SHUTDOWN_TIMEOUT_DELAY);
			// closeAllActivities();
		} else if (mDialogType == Constants.SHOW_LOGIN_SKIP_DIALOG) {
			Logger.log(Level.DEBUG, TAG, "start Activity for MainMenu");
			final Intent intent = new Intent(LoginActivity.this,
					MainMenuActivity.class);
			startActivity(intent);
			finish();
		} else if (mDialogType == Constants.SHOW_DISCARD_DIALOGE) {
			finish();
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
		super.onNegativeButtonClick();
		/*
		 * When user select exit in Do u want to update device id to cloud
		 * dialog?
		 */
		if (mDialogType == Constants.SHOW_UPDATE_DEVICE_ID_DIALOG) {
			mShutdownAck = true;
			mAppCrl.sendShutdownMsg();
			mHandler.sendEmptyMessageDelayed(Constants.SHUTDOWN_TIMEOUT,
					Constants.SHUTDOWN_TIMEOUT_DELAY);
			// closeAllActivities();
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
		Logger.log(Level.INFO, TAG, "onActivityResult is true");
		if (resultCode == RESULT_OK) {
			if (1 == requestCode) {
				final boolean updateDeviceId = getIntent().getBooleanExtra(
						"deviceid_mismatch", false);
				Logger.log(Level.DEBUG, TAG, "deviceid_mismatch:"
						+ updateDeviceId);
				if (updateDeviceId && skipLogin) {
					callMainMenuAactivity();
				} else {
					saveLoginToken();
					saveDeviceId();
					mPinModel.setLoginForSessionSuccess(true);
					setResult(RESULT_OK);
					finish();
				}
			} else if (2 == requestCode) {
				new AcceptTermsAndCondition().execute();
			} else if (3 == requestCode) {
				new AcceptTPA().execute();
			}
		}

	}

	/**
	 * AysncTask to update the Terms and condition to server.
	 *
	 */
	private class AcceptTermsAndCondition extends AsyncTask<Void, Void, String> {

		/*
		 * (non-Javadoc)
		 *
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog(getString(R.string.please_wait_text));
			mHttpUtil = new HttpUtil();
		}

		/**
		 * Method doInBackground.
		 *
		 * @param arg0
		 *            Void[]
		 * @return String
		 */
		@Override
		protected String doInBackground(Void... arg0) {
			final SortedMap<String, String> reqHeaders = mHttpUtil
					.getCommonRequestHeader();
			reqHeaders.put(HttpUtil.ACCEPT_TYPE, HttpUtil.APPLICATION_JSON);
			reqHeaders.put(HttpUtil.CONTENT_TYPE, HttpUtil.APPLICATION_JSON);

			final String URL = mPinModel.getServerAddress()
					+ Constants.MEMBER_TERMS_AND_CONDITION;

			final String status = mHttpUtil.getData(HttpUtil.GET, URL,
					reqHeaders);

			if (Logger.isDebugEnabled()) {
				Logger.log(Level.DEBUG, TAG,
						"Accept Terms and Condition status: " + status);
			}
			return status;
		}

		/**
		 * Method onPostExecute.
		 *
		 * @param status
		 *            String
		 */
		@Override
		protected void onPostExecute(String status) {
			super.onPostExecute(status);
			dismissProgressDislog();
			if (null != doLoginTask) {
				doLoginTask.parseLoginResponse(status);
				final boolean success = (mAppModel.getResponseCode() == HttpUtil.HTTP_RESPONSE_SUCCESS);
				final String result = success ? Constants.LOGIN_SUCCESS
						: Constants.LOGIN_FAIL;
				doLoginTask.processLoginResponse(result);
			}
		}
	}

	/**
	 * AysncTask to update the accepted TPA to server.
	 *
	 */
	private class AcceptTPA extends AsyncTask<Void, Void, String> {

		/*
		 * (non-Javadoc)
		 *
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog(getString(R.string.please_wait_text));
			mHttpUtil = new HttpUtil();
		}

		@Override
		protected String doInBackground(Void... params) {
			final SortedMap<String, String> reqHeaders = mHttpUtil.getCommonRequestHeader();
			reqHeaders.put(HttpUtil.ACCEPT_TYPE, HttpUtil.APPLICATION_JSON);
			reqHeaders.put(HttpUtil.CONTENT_TYPE, HttpUtil.APPLICATION_JSON);

			final String URL = mPinModel.getServerAddress()	+ Constants.ACCEPT_TPA_AGREEMENT;

			final String status = mHttpUtil.getData(HttpUtil.GET, URL, reqHeaders);

			if (Logger.isDebugEnabled()) {
				Logger.log(Level.DEBUG, TAG, "TPA Accept request status: " + status);
			}
			return status;
		}

		/**
		 * Method onPostExecute.
		 *
		 * @param status
		 *            String
		 */
		@Override
		protected void onPostExecute(String status) {
			super.onPostExecute(status);
			dismissProgressDislog();
			if (null != doLoginTask) {
				if(status != null){
					if(status.contains("ERR003-PASSWDCHG")
							|| status.contains("ERR002-PASSWDEXP")){
						doLoginTask.parseLoginResponse(status);
					}
				}

				final boolean success = (mAppModel.getResponseCode() == HttpUtil.HTTP_RESPONSE_SUCCESS);
				final String result = success ? Constants.LOGIN_SUCCESS
						: Constants.LOGIN_FAIL;
				doLoginTask.processLoginResponse(result);
			}
		}
	}

	/**
	 * Save the login token.
	 */
	private void saveLoginToken() {
		Logger.log(Level.DEBUG, TAG, "saveLoginToken()");
		final SharedPreferences saveLoginToken = getSharedPreferences(
				Constants.USER_LOGIN_TOKEN_DB, MODE_PRIVATE);
		final SharedPreferences.Editor editor = saveLoginToken.edit();
		editor.putString(Constants.SAVE_LOGIN_TOKEN, mAppModel.getTokenKey());
		editor.putString(Constants.SAVE_USER_ROLE, mAppModel.getUserRole());
		editor.putString(Constants.SAVE_USER_UNIQUE_ID,
				mAppModel.getUserUniqueId());
		editor.commit();
		Logger.log(Level.DEBUG, TAG,
				"saved access token is=" + mAppModel.getTokenKey());
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
		if (response.getResponseType() == ResponseType.ACK && mShutdownAck) {
			closeAllActivities();
		}
	}

	/** The m handler. */
	private final Handler mHandler = new MyHandler(this);

	/**
	 * The Class MyHandler.
	 */
	private static class MyHandler extends Handler {

		/** The m activity. */
		private final WeakReference<LoginActivity> mActivity;

		/**
		 * Constructor for MyHandler.
		 *
		 * @param activity
		 *            LoginActivity
		 */
		private MyHandler(LoginActivity activity) {
			mActivity = new WeakReference<LoginActivity>(activity);
		}

		/**
		 * Method handleMessage.
		 *
		 * @param msg
		 *            android.os.Message
		 */
		@Override
		public void handleMessage(android.os.Message msg) {
			final LoginActivity parent = mActivity.get();
			if (msg.what == Constants.SHUTDOWN_TIMEOUT) {
				Logger.log(Level.DEBUG, TAG,
						"SHUTDOWN_TIMEOUT. so app is closing");
				parent.closeAllActivities();
			}
		}
	};

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
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (v.getId() == R.id.btn_signup || v.getId() == R.id.tvForgotPassLink) {
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
}
