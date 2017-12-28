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
 * THis is an Activity to change the default password.
 */
public class ChangePasswordActivity extends AppBaseActivity implements
		OnClickListener {

	/** The m current password. */
	private EditText mCurrentPassword = null;

	/** The m new password. */
	private EditText mNewPassword = null;

	/** The m confirm new password. */
	private EditText mConfirmNewPassword = null;

	/** The Constant TAG. */
	private static final String TAG = ChangePasswordActivity.class
			.getSimpleName();

	/** The m status. */
	private String mStatus;

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
		setContentView(R.layout.change_password);

		setCustomNoIconTitle(R.string.change_password);

		mCurrentPassword = (EditText) findViewById(R.id.edt_current_password);
		mNewPassword = (EditText) findViewById(R.id.edt_new_password);
		mConfirmNewPassword = (EditText) findViewById(R.id.edt_confirm_new_password);

		final Button changePassword = (Button) findViewById(R.id.btn_menu_positive);
		changePassword.setText(R.string.change_password);
		changePassword.setOnClickListener(this);

		setResult(RESULT_CANCELED);

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
		if (v.getId() == R.id.btn_menu_positive) {
			if (isValidPassword()) {
				// process the change password request
				if (isNetworkConnected()) {
					new DoChangePassword().execute();
				} else {
					showAlertDialog(
							R.drawable.ic_dialog_no_signal,
							get(R.string.network_connection),
							HttpUtil.getRespnseMsg(HttpUtil.HTTP_CONNECTION_DOWN),
							get(R.string.OK), null, true);
				}
			}
		}
	}

	/**
	 * Process the request to cloud for changing the password.
	 *
	 */
	private class DoChangePassword extends AsyncTask<Void, Void, Void> {

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

			reqParams.put("pin", mCurrentPassword.getText().toString());
			reqParams.put("newpin", mNewPassword.getText().toString());
			reqParams.put("confirmpin", mConfirmNewPassword.getText()
					.toString());

			final String requestURI = mPinModel.getServerAddress()
					+ Constants.CHANGE_PASSWORD;

			mStatus = mHttpUtil.postDataThroughParams(HttpUtil.POST,
					requestURI, reqParams, reqHeaders);

			if (Logger.isDebugEnabled()) {
				Logger.log(Level.DEBUG, TAG, "Change Password status: "
						+ mStatus);
			}
			if (mAppModel.getResponseCode() == HttpUtil.HTTP_RESPONSE_SUCCESS) {
				mStatus = Constants.LOGIN_SUCCESS;
				setResult(RESULT_OK);
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
			if (null == mStatus) {
				showAlertDialog(
						ChangePasswordActivity.this
								.get(R.string.change_password),
						HttpUtil.getRespnseMsg(mAppModel.getResponseCode()),
						ChangePasswordActivity.this.get(R.string.OK), null,
						true);
			} else if (!mStatus.equals(Constants.LOGIN_SUCCESS)) {
				showAlertDialog(
						ChangePasswordActivity.this
								.get(R.string.change_password),
						mStatus, ChangePasswordActivity.this.get(R.string.OK),
						null, true);
			} else {
				finish();
			}
		}

	}

	/**
	 * validate the entered password according to password policy.
	 *
	 * @return boolean
	 */
	private boolean isValidPassword() {
		boolean valid = true;
		Logger.log(Level.DEBUG, TAG, "validateUserEnteredData()");
		if (null == mCurrentPassword || null == mCurrentPassword.getText()
				|| 0 == mCurrentPassword.getText().toString().trim().length()) {
			showAlertDialog(get(R.string.change_password),
					get(R.string.current_password_empty), get(R.string.OK),
					null, true);
			valid = false;
		} else if (null == mNewPassword || null == mNewPassword.getText()
				|| 0 == mNewPassword.getText().toString().trim().length()) {
			showAlertDialog(get(R.string.change_password),
					get(R.string.new_password_empty), get(R.string.OK), null,
					true);
			valid = false;
		} else if (null == mConfirmNewPassword
				|| null == mConfirmNewPassword.getText()
				|| 0 == mConfirmNewPassword.getText().toString().trim()
						.length()) {
			showAlertDialog(get(R.string.change_password),
					get(R.string.confirm_new_password_empty), get(R.string.OK),
					null, true);
			valid = false;
		} else if (!(mNewPassword.getText().toString()
				.equals(mConfirmNewPassword.getText().toString()))) {
			showAlertDialog(get(R.string.change_password),
					get(R.string.newpass_and_conf_newpass_same),
					get(R.string.OK), null, true);
			valid = false;
		} else if (mNewPassword.getText().toString()
				.equals(mCurrentPassword.getText().toString())) {
			showAlertDialog(get(R.string.change_password),
					get(R.string.currpass_and_newpass_same), get(R.string.OK),
					null, true);
			valid = false;
		}
		return valid;
	}
}
