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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.android.userapp.R;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.util.HttpUtil;

/**
 * THis class also containing some common methods which is used in many classes.
 */
public class NetworkConnBaseActivity extends AppBaseActivity {

	/** The m selected category index. */
	protected int mSelectedCategoryIndex = 0;

	private String mChosenPaymentMode;
	private boolean mShowUnsavedRecord;

	/**
	 * The Class DoLoginOut.
	 */
	protected class DoLoginOut extends AsyncTask<Void, Void, String> {

		/** The parent. */
		private AppBaseActivity parent;

		/** The http util. */
		private HttpUtil httpUtil;

		/**
		 * Constructor for DoLoginOut.
		 *
		 * @param parent
		 *            AppBaseActivity
		 */
		protected DoLoginOut(AppBaseActivity parent) {
			this.parent = parent;
			httpUtil = new HttpUtil();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see android.os.AsyncTask#onCancelled()
		 */
		@Override
		protected void onCancelled() {
			super.onCancelled();
			if (null != httpUtil) {
				httpUtil.disconnect();
			}
		}

		/**
		 * Method onCancelled.
		 *
		 * @param result
		 *            String
		 */
//		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
//		@Override
//		protected void onCancelled(String result) {
//			if(Build.VERSION_CODES.HONEYCOMB <= Build.VERSION.SDK_INT) {
//			super.onCancelled(result);
//			}
//			if (null != httpUtil) {
//				httpUtil.disconnect();
//			}
//		}

		/*
		 * (non-Javadoc)
		 *
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog(R.string.logging_out);
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

			final SortedMap<String, String> reqHeaders = httpUtil
					.getCommonRequestHeader();
			reqHeaders.put(HttpUtil.CONTENT_TYPE, HttpUtil.APPLICATION_JSON);
			reqHeaders.put(HttpUtil.ACCEPT_TYPE, HttpUtil.APPLICATION_JSON);

			final String URL = mPinModel.getServerAddress()
					+ Constants.LOGOUT_URL;

			final String ret = httpUtil.getData(HttpUtil.GET, URL, reqHeaders);

			if (Logger.isDebugEnabled()) {
				Logger.log(Level.DEBUG, TAG, "Login out status: " + ret);
			}

			return ret;
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
//			if (mAppModel.getResponseCode() == HttpUtil.HTTP_RESPONSE_SUCCESS
//					|| mAppModel.getResponseCode() == HttpUtil.HTTP_AUTH_FAILED) {
				clearLoginToken();
				startActivity(new Intent(parent, LoginActivity.class));
				if (parent instanceof SettingsActivity) {
					finish();
				}
//			} else {
//				parent.showAlertDialog(parent.get(R.string.logout),
//						"Unable to Sign out. Kindly try again.", "OK", null,
//						true);
//			}
		}
	}

	/**
	 * The Class FetchPaymentMode.
	 */
	private class FetchPaymentMode extends AsyncTask<Void, Void, String> {

		/** The parent. */
		private Activity parent;

		/** The http util. */
		private HttpUtil httpUtil;

		/**
		 * Constructor for DoLoginOut.
		 *
		 * @param parent
		 *            AppBaseActivity
		 */
		protected FetchPaymentMode(Activity parent) {
			this.parent = parent;
			httpUtil = new HttpUtil();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see android.os.AsyncTask#onCancelled()
		 */
		@Override
		protected void onCancelled() {
			super.onCancelled();
			if (null != httpUtil) {
				httpUtil.disconnect();
			}
		}

		/**
		 * Method onCancelled.
		 *
		 * @param result
		 *            String
		 */
//		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
//		@Override
//		protected void onCancelled(String result) {
//			if(Build.VERSION_CODES.HONEYCOMB <= Build.VERSION.SDK_INT) {
//				super.onCancelled(result);
//			}
//			if (null != httpUtil) {
//				httpUtil.disconnect();
//			}
//		}

		/*
		 * (non-Javadoc)
		 *
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog(getString(R.string.please_wait_text));
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

			final SortedMap<String, String> reqHeaders = httpUtil
					.getCommonRequestHeader();
			reqHeaders
					.put(HttpUtil.CONTENT_TYPE, HttpUtil.FORM_URLENCODED_TYPE);
			reqHeaders.put(HttpUtil.ACCEPT_TYPE, HttpUtil.APPLICATION_JSON);

			final HashMap<String, String> reqParams = new HashMap<String, String>();
			reqParams.put("productType", "MCONSULT");

			final String URL = mPinModel.getServerAddress()
					+ Constants.GET_PAYMENT_MODE;

			final String ret = httpUtil.postDataThroughParams(HttpUtil.POST,
					URL, reqParams, reqHeaders);

			if (Logger.isDebugEnabled()) {
				Logger.log(Level.DEBUG, TAG, "Login out status: " + ret);
			}

			return ret;
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
			if (mAppModel.getResponseCode() == HttpUtil.HTTP_RESPONSE_SUCCESS
					&& result != null) {
				parseJsonArray(result);
			} else {
				if(mAppModel.getResponseCode() == HttpUtil.HTTP_SERVER_ERROR){
					mDialogType = Constants.SHOW_GET_USER_PAYMENT_MODE_FALIURE;
					showAlertDialog(
					NetworkConnBaseActivity.this.get(R.string.consultation),
					result == null ? NetworkConnBaseActivity.this.get(R.string.fetch_payment_modes_error)
							: result, NetworkConnBaseActivity.this.get(R.string.retry), NetworkConnBaseActivity.this.get(R.string.cancel), false);
				} else{
					mDialogType = Constants.SHOW_GET_USER_PAYMENT_MODE_FALIURE_NO_RETRY;
					showAlertDialog(
							NetworkConnBaseActivity.this.get(R.string.consultation),
							result == null ? NetworkConnBaseActivity.this.get(R.string.fetch_payment_modes_error)
									: result, NetworkConnBaseActivity.this.get(R.string.OK), null, false);
				}
			}
		}

		private void parseJsonArray(final String message) {
			try {
				JSONArray array = new JSONArray(message);
				ArrayList<PaymentMode> paymentModes = new ArrayList<PaymentMode>();
				if (array != null) {
					for (int i = 0; i < array.length(); i++) {
						JSONObject object = array.getJSONObject(i);
						PaymentMode mode = new PaymentMode();
						if (object.has("id")) {
							mode.setId(object.getInt("id"));
						}
						if (object.has("desc")) {
							mode.setDesc(object.getString("desc"));
						}
						if (object.has("name")) {
							mode.setName(object.getString("name"));
						}
						if (object.has("partnerId")) {
							mode.setPartnerId(object.getInt("partnerId"));
						}
						paymentModes.add(mode);
					}
					showPaymentListDialog(parent, paymentModes);
				}
			} catch (JSONException e) {
				Logger.log(Level.DEBUG, TAG, "JSON error" + e);
			}
		}
	}

	/**
	 * Will Display the Severity option dialog to select.
	 */
	protected void showPaymentModeDialog(final Activity parent) {
		new FetchPaymentMode(parent).execute();
	}

	private void showPaymentListDialog(final Activity parent,
			final ArrayList<PaymentMode> list) {
		mDialogType = Constants.SHOW_DIALOGE_NONE;
		mShowUnsavedRecord = getIntent().getBooleanExtra(
				Constants.UNSAVED_RECORD, false);
		final String[] menu = new String[list.size()];
		Iterator<PaymentMode> iterator = list.iterator();
		int i = 0;
		String temp;
		while (iterator.hasNext()) {
			temp = iterator.next().getDesc();
			if (!temp.isEmpty()) {
				menu[i++] = temp;
			}
		}
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(get(R.string.payment_mode));
		builder.setSingleChoiceItems(menu, mSelectedCategoryIndex,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						Logger.log(Level.DEBUG, TAG, "item:" + item);
						mSelectedCategoryIndex = item;
					}
				});

		builder.setPositiveButton(get(R.string.OK),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mChosenPaymentMode = list.get(mSelectedCategoryIndex).getName();
						if(mChosenPaymentMode.equalsIgnoreCase("WALLET") && mAppModel.isPrimaryUser()){
							new FetchCurrentBalance(NetworkConnBaseActivity.this).execute();
						} else{
							final Intent main = new Intent(parent, ChargeInfoAuthActivity.class);
							main.putExtra(Constants.UNSAVED_RECORD,	mShowUnsavedRecord);
							main.putExtra("payment_mode", mChosenPaymentMode);
							startActivityForResult(main, Constants.REQUEST_CODE_UPLOAD);
						}
					}
				});

		builder.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				finishActivity();
			}
		});

		final AlertDialog alert = builder.create();
		alert.setCanceledOnTouchOutside(false);
		alert.show();
	}

	/**
	 * close the Activity.
	 */
	public void finishActivity() {
		setResult(RESULT_OK);
		finish();
	}

	@Override
	protected void onPositiveButtonClick() {
		super.onPositiveButtonClick();
		if (mDialogType == Constants.SHOW_GET_USER_PAYMENT_MODE_FALIURE || mDialogType == Constants.SHOW_WALLET_BALANCE_FAIL_DIALOG) {
			showPaymentModeDialog(NetworkConnBaseActivity.this);
		}

		if (mDialogType == Constants.SHOW_GET_USER_PAYMENT_MODE_FALIURE_NO_RETRY) {
			finishActivity();
		}
	}

	public class PaymentMode {
		private int id;
		private String desc;
		private String name;
		private int partnerId;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getPartnerId() {
			return partnerId;
		}

		public void setPartnerId(int partnerId) {
			this.partnerId = partnerId;
		}

	}

	/**
	 * Class to fetch the current balance in wallet.
	 *
	 */
	private class FetchCurrentBalance extends AsyncTask<Void, Void, String>{

		/** The parent. */
		private NetworkConnBaseActivity parent;
		/** The http util. */
		private HttpUtil mHttpUtil;

		/**
		 * Constructor for FetchCurrentBalance
		 * @param parent NetworkConnBaseActivity
		 */
		protected FetchCurrentBalance (NetworkConnBaseActivity parent) {
			this.parent = parent;
			mHttpUtil = new HttpUtil();
		}

		@Override
		protected String doInBackground(Void... params) {
			final SortedMap<String, String> reqHeaders = mHttpUtil.getCommonRequestHeader();
			reqHeaders.put(HttpUtil.ACCEPT_TYPE, HttpUtil.APPLICATION_JSON);

			final String URL = mPinModel.getServerAddress() + Constants.GET_CURRENT_WALLET_BALANCE;
			final String ret = mHttpUtil.getData(HttpUtil.GET, URL, reqHeaders);

			if (Logger.isDebugEnabled()) {
				Logger.log(Level.DEBUG, TAG, "status: " + ret);
			}

			return ret;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dismissProgressDislog();
			if (mAppModel.getResponseCode() == HttpUtil.HTTP_RESPONSE_SUCCESS
					&& result != null) {
				final Intent main = new Intent(parent, ChargeInfoAuthActivity.class);
				main.putExtra(Constants.UNSAVED_RECORD,	mShowUnsavedRecord);
				main.putExtra("payment_mode", mChosenPaymentMode);
				main.putExtra("wallet_balance", result);
				startActivityForResult(main, Constants.REQUEST_CODE_UPLOAD);
			} else {
				parent.mDialogType = Constants.SHOW_WALLET_BALANCE_FAIL_DIALOG;
				if (null != result) {
					showAlertDialog(R.drawable.ic_dialog_info,
							parent.get(R.string.information), result,
							parent.get(R.string.OK), null, false);
				} else {
					showAlertDialog(R.drawable.ic_dialog_info,
							parent.get(R.string.network_error),
							parent.get(R.string.http_general_error),
							parent.get(R.string.OK), null, false);
				}
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog(getString(R.string.please_wait_text));
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			if (null != mHttpUtil) {
				mHttpUtil.disconnect();
			}
		}

		@SuppressLint("NewApi") @Override
		protected void onCancelled(String result) {
			super.onCancelled(result);
			if (null != mHttpUtil) {
				mHttpUtil.disconnect();
			}
		}

	}
}
