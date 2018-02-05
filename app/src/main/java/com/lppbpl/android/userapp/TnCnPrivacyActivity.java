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

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.util.SortedMap;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.Response;
import com.lppbpl.ResponseType;
import com.lppbpl.android.userapp.R;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.util.HttpUtil;

// TODO: Auto-generated Javadoc
/**
 * This activity is to display the terms and condition to accept the user.
 *
 */
public class TnCnPrivacyActivity extends AppBaseActivity implements
		OnClickListener, OnTouchListener {

	/** The state tandc. */
	private static final int STATE_TANDC = 0;

	/** The state privacy. */
	private static final int STATE_PRIVACY = 1;

	/** The m state. */
	private int mState = STATE_TANDC;

	/** The m agree. */
	private Button mAgree;

	/** The m agree check. */
	private CheckBox mAgreeCheck;

	/** The m title. */
	private String mTitle;

	/** The m tncn data. */
	private String mTncnData = null;

	/** The m privacy data. */
	private String mPrivacyData = null;

	/** The m tn cn request. */
	private DoTnCnRequest mTnCnRequest;

	/** The m http util. */
	private HttpUtil mHttpUtil;

	/** The is shut down ack. */
	private boolean isShutDownAck;

	private String tncVersion;

	/**
	 * Method onCreate.
	 *
	 * @param savedInstanceState
	 *            Bundle
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.tncnprivacy);

		setCustomNoIconTitle(R.string.title_tnc);

		mAgreeCheck = (CheckBox) findViewById(R.id.tvtermCheck);
		mAgreeCheck.setEnabled(false);
		mAgreeCheck.setOnClickListener(this);

		mAgree = (Button) findViewById(R.id.btn_menu_positive);
		mAgree.setEnabled(false);
		mAgree.setText(R.string.agree);
		mAgree.setOnClickListener(this);

		final Button mDecline = (Button) findViewById(R.id.btn_menu_negative);
		mDecline.setText(R.string.decline);
		mDecline.setOnClickListener(this);

		final TextView mTxtView = (TextView) findViewById(R.id.tvtermCond);
		mTxtView.setText(R.string.tncn_text);

		final TextView mTncnLinkTxtView = (TextView) findViewById(R.id.tvtermCondLink);
		mTncnLinkTxtView.setText(Html
				.fromHtml("<a href=''>Terms and conditions</a>"));
		mTncnLinkTxtView.setOnClickListener(this);
		mTncnLinkTxtView.setOnTouchListener(this);

		final TextView mPrivacyLinkTxtView = (TextView) findViewById(R.id.privacyLink);
		mPrivacyLinkTxtView.setText(Html
				.fromHtml("<a href=''>Privacy consent</a>"));
		mPrivacyLinkTxtView.setOnClickListener(this);
		mPrivacyLinkTxtView.setOnTouchListener(this);

		mProgDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				if (null != mTnCnRequest) {
					mTnCnRequest.cancel(true);
				}
				if (null != mHttpUtil) {
					mHttpUtil.disconnect();
					mHttpUtil = null;
				}
			}
		});

		setResult(RESULT_CANCELED);
	}

	/**
	 * Do tn cn privacy process.
	 */
	private void doTnCnPrivacyProcess() {
		if (null != mTncnData && mState == STATE_TANDC) {
			startPopupActivity(mTncnData);
		} else if (null != mPrivacyData && mState == STATE_PRIVACY) {
			startPopupActivity(mPrivacyData);
		} else {
			if (isNetworkConnected()) {
				mTnCnRequest = new DoTnCnRequest(this);
				mTnCnRequest.execute();
			} else {
				showAlertDialog(
						R.drawable.ic_dialog_no_signal,
						get(R.string.network_connection),
						"Unable to load the "
								+ mTitle
								+ ". Please visit this link to know more details."
								+ System.getProperty("line.separator")
								+ mPinModel.getServerAddress(),
						get(R.string.OK), null, true);
			}
		}
	}

	/**
	 * Asynctask makes the request to send the terms and condition, privacy
	 * policy data from cloud.
	 *
	 */
	private class DoTnCnRequest extends AsyncTask<Void, Void, String> {
		private TnCnPrivacyActivity parent;

		private DoTnCnRequest(TnCnPrivacyActivity parent) {
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
			showProgressDialog(getString(R.string.loading_text));
		}

		/**
		 * Method doInBackground.
		 *
		 * @param params
		 *            Void[]
		 * @return String
		 */
		@Override
		public String doInBackground(Void... params) {
			if (!isProgDialogShowing()) {
				return null;
			}
			mHttpUtil = new HttpUtil();

			final SortedMap<String, String> reqHeaders = mHttpUtil
					.getCommonRequestHeader();
			final SortedMap<String, String> reqParams = new TreeMap<String, String>();
			reqHeaders.put(HttpUtil.ACCEPT_TYPE, HttpUtil.APPLICATION_JSON);
			reqHeaders.put(HttpUtil.CONTENT_TYPE, HttpUtil.FORM_URLENCODED_TYPE);

			// reqParams.put("usertype", "deviceUser");

			final String URL = mPinModel.getServerAddress()
					+ ((mState == STATE_TANDC) ? Constants.TERM_CONDITION_URL
							: Constants.PRIVACY_URL);

			final String mStatus = mHttpUtil.postDataThroughParams(
					HttpUtil.POST, URL, reqParams, reqHeaders);

			if (Logger.isDebugEnabled()) {
				Logger.log(Level.DEBUG, TAG, "Terms and Condition status: "
						+ mStatus);
			}
			return mStatus;
		}

		/**
		 * Method onPostExecute.
		 *
		 * @param result
		 *            String
		 */
		@Override
		protected void onPostExecute(String result) {
			if (!isProgDialogShowing()) {
				return;
			}
			dismissProgressDislog();
			if (mAppModel.getResponseCode() == HttpUtil.HTTP_RESPONSE_SUCCESS
					&& null != result) {
				try {
					final JSONObject o = new JSONObject(result);
					if (o != null) {
						tncVersion = o.getString("version");
						result = URLDecoder
								.decode(o.getString("text"), "UTF-8");
						callPopActivity(result);
						mAgreeCheck.setEnabled(true);
					}
				} catch (JSONException e) {
					Logger.log(Level.DEBUG, TAG, "" + e);
					showAlertDialog(R.drawable.ic_dialog_info,
							parent.get(R.string.network_error),
							parent.get(R.string.http_general_error),
							parent.get(R.string.OK), null, false);
				} catch (UnsupportedEncodingException uec) {
					Logger.log(Level.DEBUG, TAG, "" + uec);
				}

			} else {
				if (null != result) {
					showAlertDialog(R.drawable.ic_dialog_info,
							parent.get(R.string.information), result,
							parent.get(R.string.OK), null, false);
				} else {
					showResponseStatusMsg(HttpUtil.getRespnseMsg(mAppModel
							.getResponseCode()));

					showAlertDialog(R.drawable.ic_dialog_info,
							parent.get(R.string.network_error),
							parent.get(R.string.http_general_error),
							parent.get(R.string.OK), null, false);

				}
			}

		}
	}

	/**
	 * Method callPopActivity.
	 *
	 * @param result
	 *            String
	 */
	private void callPopActivity(String result) {
		if (mState == STATE_TANDC) {
			mTncnData = result;
		} else if (mState == STATE_PRIVACY) {
			mPrivacyData = result;
		}
		startPopupActivity(result);
	}

	/**
	 * Calls the Activity to display the Terms and condition, Privacy consent to
	 * user.
	 *
	 * @param result
	 *            the result
	 */
	private void startPopupActivity(String result) {
		final Intent intent = new Intent(this, TncnPrivacyPopup.class);
		intent.putExtra("tncnPrivacyResponse", result);
		intent.putExtra("popupTitle", mTitle);
		startActivity(intent);
	}

	/**
	 * Method showResponseStatusMsg.
	 *
	 * @param status
	 *            String
	 */
	private void showResponseStatusMsg(String status) {
		showAlertDialog(R.drawable.ic_dialog_error, mTitle, status,
				get(R.string.OK), null, true);
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
		if (i == R.id.btn_menu_positive) {
			final SharedPreferences saveTnCVersion = getSharedPreferences(
					Constants.TNC_VERSION, MODE_PRIVATE);
			final SharedPreferences.Editor editor = saveTnCVersion.edit();
			editor.putString(Constants.TNC_VERSION, tncVersion);
			editor.commit();

			if (getIntent().getBooleanExtra("from_splash_screen", false)) {
				Intent intent = new Intent(this, BluetoothPairingActivity.class);
				startActivity(intent);
				finish();
			} else {
				setResult(RESULT_OK);
				finish();
			}


		} else if (i == R.id.btn_menu_negative) {
			showExitDialog();

		} else if (i == R.id.tvtermCheck) {
			final boolean enable = (mTncnData != null || mPrivacyData != null);
			mAgree.setEnabled(mAgreeCheck.isChecked() && enable);
			mAgree.setEnabled(mAgreeCheck.isChecked());

		} else if (i == R.id.tvtermCondLink) {
			mState = STATE_TANDC;
			mTitle = get(R.string.title_tnc);
			doTnCnPrivacyProcess();

		} else if (i == R.id.privacyLink) {
			mState = STATE_PRIVACY;
			mTitle = get(R.string.title_privacy);
			doTnCnPrivacyProcess();

		} else {
		}
	}

	/** The m handler. */
	private final MyHandler mHandler = new MyHandler(this);

	/**
	 * The Class MyHandler.
	 */
	private static class MyHandler extends Handler {

		/** The m activity. */
		private final WeakReference<TnCnPrivacyActivity> mActivity;

		/**
		 * Constructor for MyHandler.
		 *
		 * @param activity
		 *            TnCnPrivacyActivity
		 */
		private MyHandler(TnCnPrivacyActivity activity) {
			mActivity = new WeakReference<TnCnPrivacyActivity>(activity);
		}

		/**
		 * Method handleMessage.
		 *
		 * @param msg
		 *            android.os.Message
		 */
		@Override
		public void handleMessage(android.os.Message msg) {
			final TnCnPrivacyActivity parent = mActivity.get();
			if (msg.what == Constants.SHUTDOWN_TIMEOUT) {
				Logger.log(Level.DEBUG, TAG,
						"SHUTDOWN_TIMEOUT. so app is closing");
				parent.closeAllActivities();

			} else {
			}
		}
	};

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
	}

	/**
	 * Stop the timer, send the shutdown message to device and Exits the
	 * application.
	 */
	private void exitApp() {
		mDialogType = Constants.SHOW_DIALOGE_NONE;
		if (isActivityRunning()) {
			mActModel.setRunning(false);
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
		if (v.getId() == R.id.privacyLink || v.getId() == R.id.tvtermCondLink) {
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

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.lppbpl.android.userapp.AppBaseActivity#onPositiveButtonClick()
	 */
	@Override
	protected void onPositiveButtonClick() {
		super.onPositiveButtonClick();
		if (mDialogType == Constants.SHOW_EXIT_DIALOGE) {
			exitApp();
		}
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
			showExitDialog();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Show exit dialog.
	 */
	private void showExitDialog() {
		mDialogType = Constants.SHOW_EXIT_DIALOGE;
		showAlertDialog(get(R.string.exit_app),
				get(R.string.exit_confirmation), get(R.string.OK),
				get(R.string.cancel), false);
	}
}
