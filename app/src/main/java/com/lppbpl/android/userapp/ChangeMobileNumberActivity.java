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

import java.util.SortedMap;
import java.util.TreeMap;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.android.userapp.R;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.util.HttpUtil;

/**
 * This is an Activity to change the mobile number.
 */
public class ChangeMobileNumberActivity extends AppBaseActivity implements
		OnClickListener {

	/** The m change mobile num. */
	private EditText mChangeMobileNum = null;

	/** The m country code. */
	private EditText mCountryCode = null;

	/** The Constant TAG. */
	private static final String TAG = ChangePasswordActivity.class
			.getSimpleName();

	/** The m http util. */
	private HttpUtil mHttpUtil;

	/**
	 * Method onCreate.
	 *
	 * @param savedInstanceState
	 *            Bundle
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_mobile_no);

		setCustomNoIconTitle(R.string.change_mobile_no);

		mChangeMobileNum = (EditText) findViewById(R.id.edt_update_mobile_no);

		mCountryCode = (EditText) findViewById(R.id.country_code);

		final Button changePassword = (Button) findViewById(R.id.btn_menu_positive);
		changePassword.setText(R.string.save);
		changePassword.setOnClickListener(this);

		mProgDialog.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface arg0) {
				if (null != mHttpUtil) {
					mHttpUtil.disconnect();
					mHttpUtil = null;
				}
			}
		});
		Logger.log(Level.DEBUG, TAG, "onCreate");
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
		Logger.log(Level.DEBUG, TAG, "onClick()");
		if (v.getId() == R.id.btn_menu_positive && isValidMobileNo()) {
			// process the change password request
			if (isNetworkConnected()) {
				if (mPinModel.isLoginForSessionSuccess()) {
					new DoChangeMobileNum(this).execute();
				} else {
					final Intent intent = new Intent(this, LoginActivity.class);
					startActivityForResult(intent, 1);
				}
			} else {
				showAlertDialog(R.drawable.ic_dialog_no_signal,
						get(R.string.network_connection),
						HttpUtil.getRespnseMsg(HttpUtil.HTTP_CONNECTION_DOWN),
						get(R.string.OK), null, true);
			}
		}
	}

	/**
	 * Process the request to cloud for changing the mobile number.
	 */
	private class DoChangeMobileNum extends AsyncTask<Void, Void, Void> {

		/** The activity. */
		final private ChangeMobileNumberActivity activity;

		/** The m status. */
		private String mStatus;

		/**
		 * Constructor for DoChangeMobileNum.
		 *
		 * @param activity
		 *            ChangeMobileNumberActivity
		 */
		private DoChangeMobileNum(ChangeMobileNumberActivity activity) {
			this.activity = activity;
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
		 * @return Void
		 */
		@Override
		protected Void doInBackground(Void... arg0) {

			final SortedMap<String, String> reqHeaders = mHttpUtil
					.getCommonRequestHeader();
			final SortedMap<String, String> reqParams = new TreeMap<String, String>();
			reqHeaders.put(HttpUtil.ACCEPT_TYPE, HttpUtil.APPLICATION_JSON);
			reqHeaders
					.put(HttpUtil.CONTENT_TYPE, HttpUtil.FORM_URLENCODED_TYPE);

			final StringBuffer requestURIBuilder = new StringBuffer();
			final String countryCode = mCountryCode.getText().toString();
			final String changedMobileNo = mChangeMobileNum.getText()
					.toString();

			reqParams.put("Userid", mPinModel.getUserID());
			reqParams.put("phonenumber", changedMobileNo);

			requestURIBuilder.append(mPinModel.getServerAddress());
			requestURIBuilder.append(Constants.CHANGE_MOBILE_NO);
			requestURIBuilder.append("phonecountrycode/");
			requestURIBuilder.append(countryCode);
			requestURIBuilder.append("/phonenumber/");
			requestURIBuilder.append(changedMobileNo);

			mStatus = mHttpUtil.postDataThroughParams(HttpUtil.POST,
					requestURIBuilder.toString(), reqParams, reqHeaders);

			if (Logger.isDebugEnabled()) {
				Logger.log(Level.DEBUG, TAG, "Change Mobile no status: "
						+ mStatus);
			}
			if (mAppModel.getResponseCode() == HttpUtil.HTTP_RESPONSE_SUCCESS) {
				mStatus = Constants.LOGIN_SUCCESS;
			}

			return null;
		}

		/**
		 * Method onPostExecute.
		 *
		 * @param result
		 *            Void
		 */
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			dismissProgressDislog();
			if (isAccessTokenExpired()) {
				Logger.log(Level.DEBUG, TAG, "Access token expires.");
				mDialogType = Constants.SHOW_SESSION_EXPIRED_DIALOG;
				showAlertDialog(R.drawable.ic_dialog_error,
						activity.get(R.string.information),
						activity.get(R.string.session_expired),
						activity.get(R.string.OK), null, false);
				return;
			} else if (null == mStatus) {
				showAlertDialog(activity.get(R.string.information),
						HttpUtil.getRespnseMsg(mAppModel.getResponseCode()),
						activity.get(R.string.OK), null, true);
			} else if (mStatus.equals(Constants.LOGIN_SUCCESS)) {
				mDialogType = Constants.SHOW_CHANGE_MOBILE_NUMBER_DIALOG;
				showAlertDialog(R.drawable.ic_dialog_success,
						activity.get(R.string.information),
						activity.get(R.string.successfully_changed_mobile_no),
						activity.get(R.string.OK), null, false);
			} else {
				showAlertDialog(activity.get(R.string.information), mStatus,
						activity.get(R.string.OK), null, true);
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
	protected void onPositiveButtonClick() {
		if (mDialogType == Constants.SHOW_CHANGE_MOBILE_NUMBER_DIALOG) {
			finish();
		} else if (mDialogType == Constants.SHOW_SESSION_EXPIRED_DIALOG) {
			mPinModel.setLoginForSessionSuccess(false);
			final Intent intent = new Intent(this, LoginActivity.class);
			startActivityForResult(intent, 1);
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Logger.log(Level.DEBUG, TAG, "onActivityResult Request code :"
				+ requestCode);
		if (1 == requestCode && resultCode == RESULT_OK) {
			new DoChangeMobileNum(this).execute();
		}
	}

	/**
	 * validates the entered mobile number is proper or not.
	 *
	 * @return boolean
	 */
	private boolean isValidMobileNo() {
		boolean valid = true;
		Logger.log(Level.DEBUG, TAG, "validateUserEnteredData()");
		if (null == mChangeMobileNum || null == mChangeMobileNum.getText()
				|| 0 == mChangeMobileNum.getText().toString().length()) {
			showAlertDialog(get(R.string.registration),
					get(R.string.enter_currect_mobile_no), get(R.string.OK),
					null, true);
			valid = false;
		} else if (null == mCountryCode || null == mCountryCode.getText()
				|| 0 == mCountryCode.getText().toString().trim().length()) {
			showAlertDialog(get(R.string.registration),
					get(R.string.enter_country_code), get(R.string.OK), null,
					true);
			valid = false;
		}
		return valid;
	}
}
