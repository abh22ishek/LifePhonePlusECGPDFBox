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
import java.util.Vector;

import net.jarlehansen.protobuf.javame.UninitializedMessageException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.Measurement;
import com.lppbpl.MultipleResponse;
import com.lppbpl.android.userapp.R;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.listener.SecondaryUserNotification;
import com.lppbpl.android.userapp.util.HttpUtil;
import com.lppbpl.android.userapp.util.Util;

// TODO: Auto-generated Javadoc
/**
 * This Activity is to display screen to fetch the latest records from cloud.
 *
 */
public class LatestRecordsList extends MemberBaseActivity implements
		OnClickListener, OnSeekBarChangeListener, SecondaryUserNotification {

	/** The Constant TAG. */
	private static final String TAG = "LatestRecordsList";

	/** The m view count. */
	final private int mViewCount = 3;

	/** The m old value. */
	private int mOldValue = 15;

	/** The m no of recs. */
	protected int mNoOfRecs = 15;

	/** The m measurement type selected. */
	protected int mMeasurementTypeSelected = -1;

	/** The m response error. */
	private boolean mResponseError = false;

	/** The m has data response. */
	private boolean mHasDataResponse = false;

	/** The m num of rec. */
	private TextView mNumOfRec;

	/** The m measurement types. */
	final private TextView[] mMeasurementTypes = new TextView[mViewCount];

	/** The m image bgs. */
	final private ImageView[] mImageBgs = new ImageView[mViewCount];

	/** The m radio bg images. */
	final private ImageView[] mRadioBgImages = new ImageView[mViewCount];

	/** The maximum. */
	private final int MAXIMUM = 300;

	/** The interval. */
	private final int INTERVAL = 5;

	/**
	 * Method onCreate.
	 *
	 * @param savedInstanceState
	 *            Bundle
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		notify = this;
		setContentView(R.layout.latestrecords);
		setCustomTitle(R.string.title_latest_records);
		setCustomHeaderImage(R.drawable.ic_title_records);
		final Button getRecords = (Button) findViewById(R.id.btn_menu_positive);
		getRecords.setText(R.string.get);
		getRecords.setOnClickListener(this);

		final TextView txtMemberList = (TextView) findViewById(R.id.txt_member_selector);
		final TextView txtMembers = (TextView) findViewById(R.id.txt_members);
		txtMemberList.setOnClickListener(this);

		if (!mAppModel.isParamedicUser()) {
			txtMemberList.setVisibility(View.GONE);
			txtMembers.setVisibility(View.GONE);
		}

		mMeasurementTypeSelected = Measurement.ECG;
		final SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
		seekBar.setMax(MAXIMUM);
		seekBar.setProgress(mOldValue);
		seekBar.setOnSeekBarChangeListener(this);

		mNumOfRec = (TextView) findViewById(R.id.tvNumOfRec);
		mNumOfRec.setText(Integer.toString(seekBar.getProgress()));

		int resIdRadio = 0;
		int resIdImage = 0;
		int resIdSymp = 0;
		for (int i = 0; i < mViewCount; i++) {
			resIdRadio = getResources().getIdentifier("ivBg_Radio_" + (i + 1),
					"id", getPackageName());
			mRadioBgImages[i] = (ImageView) this.findViewById(resIdRadio);

			resIdImage = getResources().getIdentifier("ivBg_" + (i + 1), "id",
					getPackageName());
			mImageBgs[i] = (ImageView) findViewById(resIdImage);

			resIdSymp = getResources().getIdentifier(
					"tvMeasurementType_" + (i + 1), "id", getPackageName());
			mMeasurementTypes[i] = (TextView) findViewById(resIdSymp);
			mMeasurementTypes[i].setOnClickListener(this);
		}

		final OnCancelListener listener = new OnCancelListener() {
			public void onCancel(DialogInterface arg0) {
				if (null != mHttpUtil) {
					Logger.log(Level.DEBUG, TAG, "ProgressDialog Dismissed");
					mHttpUtil.disconnect();
					mHttpUtil = null;
				}
			}
		};
		mProgDialog.setOnCancelListener(listener);

	}

	/**
	 * Method onClick.
	 *
	 * @param v
	 *            View
	 * @see android.view.View$OnClickListener#onClick(View)
	 */
	public void onClick(View v) {
		Logger.log(Level.DEBUG, TAG, "onClick");
		int i = v.getId();
		if (i == R.id.tvMeasurementType_1) {
			mMeasurementTypeSelected = Measurement.ECG;
			mMeasurementTypes[0]
					.setBackgroundResource(R.xml.single_listview_style);
			mRadioBgImages[0]
					.setImageResource(R.drawable.ic_radiobutton_selected);
			mRadioBgImages[1]
					.setImageResource(R.drawable.ic_radiobutton_notselected);
			mRadioBgImages[2]
					.setImageResource(R.drawable.ic_radiobutton_notselected);

		} else if (i == R.id.tvMeasurementType_2) {
			mMeasurementTypeSelected = Measurement.BG;
			mMeasurementTypes[1]
					.setBackgroundResource(R.xml.single_listview_style);
			mRadioBgImages[1]
					.setImageResource(R.drawable.ic_radiobutton_selected);
			mRadioBgImages[0]
					.setImageResource(R.drawable.ic_radiobutton_notselected);
			mRadioBgImages[2]
					.setImageResource(R.drawable.ic_radiobutton_notselected);

		} else if (i == R.id.tvMeasurementType_3) {
			mMeasurementTypeSelected = Measurement.ACT;
			mMeasurementTypes[2]
					.setBackgroundResource(R.xml.single_listview_style);
			mRadioBgImages[2]
					.setImageResource(R.drawable.ic_radiobutton_selected);
			mRadioBgImages[0]
					.setImageResource(R.drawable.ic_radiobutton_notselected);
			mRadioBgImages[1]
					.setImageResource(R.drawable.ic_radiobutton_notselected);

		} else if (i == R.id.btn_menu_positive) {
			if (mPinModel.isLoginForSessionSuccess()) {
				sendReqToServer();
			} else {
				final Intent intent = new Intent(this, LoginActivity.class);
				startActivityForResult(intent, 1);
			}

		} else if (i == R.id.txt_member_selector) {
			if (null != mAppModel.getMemberList()) {
				showMemberListDialog();
			} else {
				if (mPinModel.isLoginForSessionSuccess()) {
					myMembers = new FetchMyMembers();
					myMembers.execute();
				} else {
					final Intent intent = new Intent(this, LoginActivity.class);
					startActivityForResult(intent, 2);
				}
			}

		} else {
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
			sendReqToServer();
		} else if (2 == requestCode && resultCode == RESULT_OK) {
			myMembers = new FetchMyMembers();
			myMembers.execute();
		}
	}

	/**
	 * Process the request to cloud to fetch the latest record of particular
	 * type.
	 */
	private void sendReqToServer() {
		if (mMeasurementTypeSelected == Measurement.BG) {
			showBgRadioDialog();
		} else {
			showProgressDialog(getString(R.string.loading_text));
			final GetLatestRecordsThread getRec = new GetLatestRecordsThread();
			getRec.start();
		}

	}

	/**
	 * Thread Makes the request to cloud to fetch the latest record.
	 *
	 */
	public class GetLatestRecordsThread extends Thread {

		/** The m multi response. */
		private MultipleResponse mMultiResponse = null;

		/**
		 * Instantiates a new gets the latest records thread.
		 */
		public GetLatestRecordsThread() {
			if (null != mHttpUtil) {
				mHttpUtil.disconnect();
				mHttpUtil = null;
			}
			mHttpUtil = new HttpUtil();
			mHasDataResponse = false;
			mResponseError = false;
		}

		/**
		 * Method run.
		 *
		 * @see Runnable#run()
		 */
		@Override
		public void run() {
			super.run();

			try {
				final JSONObject jsonObject = new JSONObject();
				try {
					final JSONArray ids = new JSONArray();
					if (0 != mSelectedUserId) {
						ids.put(mSelectedUserId);
					}
					final int dataType = mMeasurementTypeSelected == Measurement.BG ? 2
							: mMeasurementTypeSelected;
					jsonObject.put("userIds", ids);
					jsonObject.put("dataType", dataType);
					jsonObject.put("measurementType", Measurement
							.getStringValue(mMeasurementTypeSelected));
					jsonObject.put("status", "-1");
					jsonObject.put("providerId", 0);
					jsonObject.put("pageSize", mNoOfRecs);
					jsonObject.put("noOfRecords", mNoOfRecs);
					jsonObject.put("currentPageNum", 1);
					jsonObject.put("noOfPages", 0);
					jsonObject.put("forOptOut", false);
					jsonObject.put("paymentMethod", "-1");
					jsonObject.put("startDate", null);
					jsonObject.put("endDate", null);
					// object.toString();
					Logger.log(Level.DEBUG, "sendReqToServer",
							jsonObject.toString());
				} catch (JSONException ex) {
					Logger.log(Level.ERROR, TAG, "" + ex);
				}

				// Logger.log(Level.DEBUG, TAG, "message " + message);

				Logger.log(Level.DEBUG, TAG, "postGetDataRequest++");
				if (isNetworkConnected()) {
					final SortedMap<String, String> reqHeaders = mHttpUtil
							.getCommonRequestHeader();

					if (mMeasurementTypeSelected == Measurement.ECG
							|| (0 == mBgSelectedIndex && mMeasurementTypeSelected == Measurement.BG)) {
						reqHeaders.put(HttpUtil.ACCEPT_TYPE,
								HttpUtil.APPLICATION_JSON);
						reqHeaders.put(HttpUtil.CONTENT_TYPE,
								HttpUtil.APPLICATION_JSON);

						final String URL = mPinModel.getServerAddress()
								+ Constants.MY_DATA_URL;

						final String response = mHttpUtil.postData(
								HttpUtil.POST, URL, jsonObject.toString()
										.getBytes(), reqHeaders);

						Logger.log(Level.DEBUG, TAG, "response = " + response);

						if (0 == mBgSelectedIndex
								&& mMeasurementTypeSelected == Measurement.BG) {
							mMultiResponse = Util
									.createMultiResponseBG(response);
						} else {
							mMultiResponse = Util.createMultiResponse(response);
						}
					} else {
						reqHeaders.put(HttpUtil.ACCEPT_TYPE,
								HttpUtil.PROTOBUF_ENCODING_TYPE);
						reqHeaders.put(HttpUtil.CONTENT_TYPE,
								HttpUtil.APPLICATION_JSON);

						final String URL = mPinModel.getServerAddress()
								+ getDataLogURL();

						mMultiResponse = mHttpUtil.postGetDataRequest(
								HttpUtil.POST, URL, jsonObject.toString()
										.getBytes(), reqHeaders);
					}
				}

				Logger.log(Level.DEBUG, TAG, "multiResponse = "
						+ mMultiResponse);
				if (null != mMultiResponse) {
					if (mMultiResponse.getMultiMeasurementType() == Measurement.ECG) {
						@SuppressWarnings("rawtypes")
						final Vector ecgList = mMultiResponse.getEcgList();
						mHasDataResponse = (null != ecgList && ecgList.size() > 0);
					} else if (mMultiResponse.getMultiMeasurementType() == Measurement.BG) {
						@SuppressWarnings("rawtypes")
						final Vector bgList = mMultiResponse.getBgData();
						mHasDataResponse = (null != bgList && bgList.size() > 0);
					} else if (mMultiResponse.getMultiMeasurementType() == Measurement.ACT) {
						@SuppressWarnings("rawtypes")
						final Vector actList = mMultiResponse.getActData();
						mHasDataResponse = (null != actList && actList.size() > 0);
					}
					mAppModel.setGetDataResponse(mMultiResponse);
				} else {
					mAppModel.setGetDataResponse(null);
				}
				Logger.log(Level.DEBUG, TAG, "postGetDataRequest--");
				// } catch (IOException e) {
				// mResponseError = true;
				// Logger.log(Level.DEBUG, TAG, "exception++");
				// e.printStackTrace();
				// mAppModel.setGetDataResponse(null);
			} catch (UninitializedMessageException e) {
				mResponseError = true;
				Logger.log(Level.DEBUG, TAG, "exception++=" + e);
				mAppModel.setGetDataResponse(null);
			} catch (NullPointerException npe) {
				Logger.log(Level.DEBUG, TAG, "exception++=" + npe);
			} finally {
				mHandler.sendEmptyMessage(Constants.GETLATESTREC_RESPONSE_RECEIVED);
			}

		}

		/**
		 * Method getDataLogURL.
		 *
		 * @return String
		 */
		private String getDataLogURL() {
			if (mMeasurementTypeSelected == Measurement.ECG) {
				return Constants.GET_ECG_LOGDATA_URL;
			} else if (mMeasurementTypeSelected == Measurement.ACT) {
				return Constants.GET_ACT_LOGDATA_URL;
			} else if (mMeasurementTypeSelected == Measurement.BG) {
				return Constants.GET_BG_LOGDATA_URL;
			}
			return null;
		}
	}

	/** Handler handle the message back from the cloud. */
	final private MyHandler mHandler = new MyHandler(this);

	/**
	 * The Class MyHandler.
	 */
	private static class MyHandler extends Handler {

		/** The m activity. */
		private final WeakReference<LatestRecordsList> mActivity;

		/**
		 * Constructor for MyHandler.
		 *
		 * @param activity
		 *            LatestRecordsList
		 */
		private MyHandler(LatestRecordsList activity) {
			mActivity = new WeakReference<LatestRecordsList>(activity);
		}

		/**
		 * Method handleMessage.
		 *
		 * @param msg
		 *            android.os.Message
		 */
		public void handleMessage(android.os.Message msg) {
			final LatestRecordsList parent = mActivity.get();
			if (msg.what == Constants.GETLATESTREC_RESPONSE_RECEIVED) {
				parent.dismissProgressDislog();
				/*
				 * WHen access token expires, so show the login screen and do
				 * the login then start uploading / retrive the data.
				 */
				if (parent.isAccessTokenExpired()) {
					Logger.log(Level.DEBUG, TAG, "Access token expires.");
					parent.mDialogType = Constants.SHOW_SESSION_EXPIRED_DIALOG;
					parent.showAlertDialog(R.drawable.ic_dialog_info,
							parent.get(R.string.information),
							parent.get(R.string.session_expired),
							parent.get(R.string.OK), null, false);
					return;
				}

				if (null != parent.mAppModel.getGetDataResponse()) {
					if (parent.mHasDataResponse) {
						Logger.log(Level.DEBUG, TAG, "toLaunchRecordsList");
						if (parent.mMeasurementTypeSelected == Measurement.ECG
								|| (0 == parent.mBgSelectedIndex && parent.mMeasurementTypeSelected == Measurement.BG)) {
							final Intent toSpecificRec = new Intent(parent,
									GetRecordsDataList.class);
							parent.startActivity(toSpecificRec);
						} else if (parent.mMeasurementTypeSelected == Measurement.ACT) {
							final Intent toSpecificRec = new Intent(parent,
									ActGraphActivity.class);
							parent.startActivity(toSpecificRec);
						} else if (parent.mMeasurementTypeSelected == Measurement.BG) {
							final Intent toSpecificRec = new Intent(parent,
									BgGraphActivity.class);
							parent.startActivity(toSpecificRec);
						}
					} else {
						parent.showAlertDialog(
								R.drawable.ic_dialog_error,
								parent.get(R.string.no_recrods),
								parent.get(R.string.no_records_found),
								parent.get(R.string.OK), null, true);
					}
				} else {
					if (parent.isNetworkConnected()) {
						parent.showAlertDialog(
								R.drawable.ic_dialog_error,
								parent.get(R.string.network_error),
								parent.mResponseError ? parent.get(R.string.response_failed)
										: parent.get(R.string.http_general_error),
								parent.get(R.string.OK), null, true);
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
	protected void onPositiveButtonClick() {
		if (mDialogType == Constants.SHOW_SESSION_EXPIRED_DIALOG) {
			mPinModel.setLoginForSessionSuccess(false);
			final Intent intent = new Intent(this, LoginActivity.class);
			startActivityForResult(intent, 1);
		} else if (mDialogType == Constants.SHOW_SESSION_EXPIRED_DIALOG_1) {
			mPinModel.setLoginForSessionSuccess(false);
			final Intent intent = new Intent(this, LoginActivity.class);
			startActivityForResult(intent, 2);
		}
	};

	/**
	 * Method onProgressChanged.
	 *
	 * @param seekBar
	 *            SeekBar
	 * @param progress
	 *            int
	 * @param fromUser
	 *            boolean
	 * @see android.widget.SeekBar$OnSeekBarChangeListener#onProgressChanged(SeekBar,
	 *      int, boolean)
	 */
	public void onProgressChanged(final SeekBar seekBar, final int progress,
			final boolean fromUser) {
		int prog = progress;
		if (0 == prog) {
			seekBar.setProgress(1);
			return;
		}
		prog = Math.round(((float) prog) / INTERVAL) * INTERVAL;
		if (0 == prog) {
			prog = 1;
		}
		mNoOfRecs = prog;
		mOldValue = prog;
		mNumOfRec.setText(Integer.toString(prog));
	}

	/**
	 * Method onStartTrackingTouch.
	 *
	 * @param seekBar
	 *            SeekBar
	 * @see android.widget.SeekBar$OnSeekBarChangeListener#onStartTrackingTouch(SeekBar)
	 */
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	/**
	 * Method onStopTrackingTouch.
	 *
	 * @param seekBar
	 *            SeekBar
	 * @see android.widget.SeekBar$OnSeekBarChangeListener#onStopTrackingTouch(SeekBar)
	 */
	public void onStopTrackingTouch(SeekBar seekBar) {
	}

	/**
	 * Method parseMyMembersResponse.
	 *
	 * @param response
	 *            String
	 */
	@Override
	public void parseMyMembersResponse(String response) {
		parseMemberResponse(response);
	}

	/**
	 * Method updateSelectedMemberUI.
	 *
	 * @param selectedMember
	 *            String
	 */
	@Override
	public void updateSelectedMemberUI(String selectedMember) {
		final TextView txtMemberList = (TextView) findViewById(R.id.txt_member_selector);
		txtMemberList.setText(selectedMember);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.lppbpl.android.userapp.MemberBaseActivity#notifyBgRecordType()
	 */
	@Override
	public void notifyBgRecordType() {
		showProgressDialog(getString(R.string.loading_text));
		final GetLatestRecordsThread getRec = new GetLatestRecordsThread();
		getRec.start();
	}

}
