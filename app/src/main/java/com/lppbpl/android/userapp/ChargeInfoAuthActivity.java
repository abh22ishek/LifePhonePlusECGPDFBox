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

import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.Measurement;
import com.lppbpl.android.userapp.R;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.model.PendingRecord;
import com.lppbpl.android.userapp.model.SfSendModel;
import com.lppbpl.android.userapp.util.HttpUtil;

// TODO: Auto-generated Javadoc
/**
 * This is an Activity to display the charge information details.
 */
public class ChargeInfoAuthActivity extends AppBaseActivity implements
		OnClickListener {

	/** The tag. */
	private final String TAG = ChargeInfoAuthActivity.class.getSimpleName();

	/** The m case id. */
	private int mCaseId = -1;

	private int mOrderId;
	private String mRedirectLocation;
	private boolean mRedirect = false;

	/** The m http util. */
	private HttpUtil mHttpUtil = null;

	/**
	 * Method onCreate.
	 *
	 * @param savedInstanceState
	 *            Bundle
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.charge_info_auth);
		setCustomNoIconTitle(R.string.title_consultdoctor);

		final Button ok = (Button) findViewById(R.id.btn_menu_positive);
		ok.setText(R.string.OK);
		ok.setOnClickListener(this);

		final Button cancel = (Button) findViewById(R.id.btn_menu_negative);
		cancel.setText(R.string.back);
		cancel.setOnClickListener(this);

		// mPinEditTxt = (EditText)findViewById(R.id.enter_pin_edit_view);
		final String walletBalance = getIntent().getStringExtra("wallet_balance");
		final String paymentMode = getIntent().getStringExtra("payment_mode");
		final TextView chargeInfoTxt = (TextView) findViewById(R.id.charge_info_text_view);
		if(paymentMode.equalsIgnoreCase("WALLET") && mAppModel.isPrimaryUser()){
			chargeInfoTxt.setText(String.format(get(R.string.payment_mode_with_wallet_info), paymentMode, walletBalance));
		} else{
			chargeInfoTxt.setText(String.format(get(R.string.service_charge_info), paymentMode));
		}


		mProgDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				if (null != mHttpUtil) {
					mHttpUtil.disconnect();
					mHttpUtil = null;
				}
			}
		});
	}

	/**
	 * show the dialog to confirm the user for consulting the doctor.
	 */
	public void sendDoctorConsultationRequest() {
		showProgressDialog(getString(R.string.please_wait_text));
		new ConsultDocThread().start();
	}

	/**
	 * Thread to process the request to consult the doctor.
	 */
	private class ConsultDocThread extends Thread {

		/**
		 * Instantiates a new consult doc thread.
		 */
		private ConsultDocThread() {
		}

		/**
		 * Method run.
		 *
		 * @see Runnable#run()
		 */
		@Override
		public void run() {
			doConsultDoctor();
		}
	}

	/**
	 * Process the request to cloud for consult the doctor.
	 */
	private void doConsultDoctor() {

		final PendingRecord pendingRecord = mAppModel.getPendingRecord();

		mHttpUtil = new HttpUtil();

		final String consultDocUrl = mPinModel.getServerAddress()
				+ Constants.CONSULT_DOC_URL;

		final SortedMap<String, String> reqHeaders = mHttpUtil
				.getCommonRequestHeader();
		final SortedMap<String, String> reqParams = new TreeMap<String, String>();

		final String paymentMode = getIntent().getStringExtra("payment_mode");

		reqHeaders.put(HttpUtil.ACCEPT_TYPE, HttpUtil.APPLICATION_JSON);
		reqHeaders.put(HttpUtil.CONTENT_TYPE, HttpUtil.FORM_URLENCODED_TYPE);

		reqParams.put("userId", mPinModel.getUserID());
		reqParams.put("deviceId", mPinModel.getDeviceUid());
		reqParams.put("measurementId", SfSendModel.getInstance()
				.getSendStatus() + "");
		reqParams.put("measurementType",
				Measurement.getStringValue(pendingRecord.getMeasurementType()));
		final String tatCategory = getIntent().getStringExtra(
				Constants.TAT_CATEGORY);
		if (null != tatCategory) {
			reqParams.put("tatCategory", tatCategory);
		}
		reqParams.put("paymentMethod", paymentMode);
		String status = null;
		try {
			status = mHttpUtil.postDataThroughParams(HttpUtil.POST,
					consultDocUrl, reqParams, reqHeaders);

			if (Logger.isDebugEnabled()) {
				Logger.log(Level.DEBUG, TAG, "consult doctor request status: "
						+ status);
			}

			try {
				final JSONObject object = new JSONObject(status);
				mCaseId = object.getInt("caseId");
				mOrderId = object.getInt("orderId");
				mRedirectLocation = object.getString("redirectLocation");
				mRedirect = object.getBoolean("redirect");
			} catch (JSONException e) {
				Logger.log(Level.ERROR, TAG, "" + e);
			}
			// currentState = STATE_CONFIRMATION;
		} catch (NumberFormatException e) {
			mCaseId = -1;
			Logger.log(Level.ERROR, TAG, "" + e);
		} catch (NullPointerException npe) {
			Logger.log(Level.ERROR, TAG, "" + npe);
		} finally {
			final Message message = new Message();
			message.what = Constants.CONSULT_COMMAND;
			message.obj = status;
			mHandler.sendMessage(message);
		}
	}

	/**
	 * Handler handles the information and errors back from the cloud.
	 */
	private final MyHandler mHandler = new MyHandler(this);

	/**
	 * The Class MyHandler.
	 */
	private static class MyHandler extends Handler {

		/** The m activity. */
		private final WeakReference<ChargeInfoAuthActivity> mActivity;

		/**
		 * Constructor for MyHandler.
		 *
		 * @param activity
		 *            ChargeInfoAuthActivity
		 */
		private MyHandler(ChargeInfoAuthActivity activity) {
			mActivity = new WeakReference<ChargeInfoAuthActivity>(activity);
		}

		/**
		 * Method handleMessage.
		 *
		 * @param msg
		 *            android.os.Message
		 */
		@Override
		public void handleMessage(Message msg) {
			final ChargeInfoAuthActivity parent = mActivity.get();
			if (msg.what == Constants.CONSULT_COMMAND) {
				parent.dismissProgressDislog();
				if (parent.mCaseId >= 0) {
					final long charges = parent.getIntent().getLongExtra(
							Constants.TAT_CHARGES, 0);
					final long balanceAmount = parent.getIntent().getLongExtra(
							Constants.TAT_BALANCE_AMOUNT, 0);

					final boolean mShowUnsavedRecord = parent.getIntent()
							.getBooleanExtra(Constants.UNSAVED_RECORD, false);

					if (parent.mRedirect) {
						final Intent intent = new Intent(parent,
								BillPaymentActivity.class);
						intent.putExtra("caseid", parent.mCaseId);
						intent.putExtra("orderid", parent.mOrderId);
						intent.putExtra("redirectLocation",
								parent.mRedirectLocation);
						intent.putExtra(Constants.UNSAVED_RECORD,
								mShowUnsavedRecord);
						intent.putExtra("activityTitle", R.string.consultation);
						parent.startActivity(intent);
						parent.finishActivity();
					} else {
						final Intent intent = new Intent(parent,
								ChargeInfoConfirmation.class);
						intent.putExtra("caseid", parent.mCaseId);
						intent.putExtra("revised_amount", balanceAmount
								- charges);
						intent.putExtra(Constants.UNSAVED_RECORD,
								mShowUnsavedRecord);
						parent.startActivity(intent);
						parent.finishActivity();
					}
				} else {
					if (parent.isNetworkConnected()) {
						String message = (String) msg.obj;
						if (message != null
								&& (message.contains("ERR009-ADDRESS_MISSING") || message
										.contains("ERR014-EMAIL_ID_MISSING"))) {
							final String[] split = message.split(":");
							if (split != null && split.length > 1) {
								message = split[1];
							}
							parent.mDialogType = Constants.SHOW_UPDATE_ADDRESS_DIALOG;
						}
						parent.showAlertDialog(
								parent.get(R.string.consultation),
								null == message || message.trim().length() < 1 ? parent.get(R.string.request_placing_failed)
										: message,
								parent.mDialogType == Constants.SHOW_UPDATE_ADDRESS_DIALOG ? parent.get(R.string.update)
										: parent.get(R.string.retry), parent.get(R.string.cancel), false);
					} else {
						parent.showAlertDialog(
								R.drawable.ic_dialog_no_signal,
								parent.get(R.string.network_connection),
								HttpUtil.getRespnseMsg(HttpUtil.HTTP_CONNECTION_DOWN),
								parent.get(R.string.OK), null, true);
					}
				}
			}
		}
	};

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.lppbpl.android.userapp.AppBaseActivity#onPositiveButtonClick()
	 */
	@Override
	protected void onPositiveButtonClick() {
		if (mDialogType == Constants.SHOW_UPDATE_ADDRESS_DIALOG) {
			Intent intent = new Intent(this, UpdateAddressActivity.class);
			startActivityForResult(intent, Constants.SHOW_UPDATE_ADDRESS_DIALOG);
		} else {
			sendDoctorConsultationRequest();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Constants.SHOW_UPDATE_ADDRESS_DIALOG
				&& resultCode == RESULT_OK) {
			sendDoctorConsultationRequest();
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
		setResult(RESULT_CANCELED);
		finish();
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
		if (v.getId() == R.id.btn_menu_positive) {
			sendDoctorConsultationRequest();
		} else if (v.getId() == R.id.btn_menu_negative) {
			setResult(RESULT_CANCELED);
			finish();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		setResult(RESULT_CANCELED);
	}

	public void finishActivity() {
		setResult(RESULT_OK);
		finish();
	}

}
