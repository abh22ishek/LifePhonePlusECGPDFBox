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
import java.util.Calendar;
import java.util.Date;
import java.util.SortedMap;
import java.util.Vector;

import net.jarlehansen.protobuf.javame.UninitializedMessageException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
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
 * This Activity fetch the Ecg, BG and Activity records form the cloud.
 *
 */
public class GetRecords extends MemberBaseActivity implements OnClickListener,
		OnSeekBarChangeListener, SecondaryUserNotification {

	/** The m no of recs. */
	private int mNoOfRecs = 15;

	/** The m measurement type selected. */
	private int mMeasurementTypeSelected = -1;

	/** The m from date. */
	private Date mFromDate;

	/** The m to date. */
	private Date mToDate;

	/** The m multi response. */
	private MultipleResponse mMultiResponse = null;

	/** The m old value. */
	private int mOldValue = 15;

	/** The m num of rec. */
	private TextView mNumOfRec;

	/** The m date. */
	private TextView mDate;

	/** The m year. */
	private int mYear;

	/** The m month. */
	private int mMonth;

	/** The m day. */
	private int mDay;

	/** The m cal. */
	private Calendar mCal = null;

	/** The m view count. */
	final private int mViewCount = 3;

	/** The m measurement type field. */
	final private TextView[] mMeasurementTypeField = new TextView[mViewCount];

	/** The m image bg. */
	final private ImageView[] mImageBg = new ImageView[mViewCount];

	/** The m radio bg. */
	final private ImageView[] mRadioBg = new ImageView[mViewCount];

	/** The m response error. */
	private boolean mResponseError = false;

	/** The m has data response. */
	private boolean mHasDataResponse = false;

	/** The Constant MAXIMUM. */
	private static final int MAXIMUM = 300;

	/** The Constant INTERVAL. */
	private static final int INTERVAL = 5;

	/** The Constant DATE_DIALOG_ID. */
	private static final int DATE_DIALOG_ID = 0;

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
		setContentView(R.layout.getrecords);
		setCustomTitle(R.string.title_get_specific_records);
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

		// Seekbar to select number of records to fetch from the cloud
		final SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar1);
		seekBar.setMax(MAXIMUM);
		seekBar.setProgress(mOldValue);
		seekBar.setOnSeekBarChangeListener(this);

		mNumOfRec = (TextView) findViewById(R.id.textView1);
		mNumOfRec.setText(seekBar.getProgress() + "");

		int resIdRadio = 0;
		int resIdImage = 0;
		int resIdSymp = 0;
		for (int i = 0; i < mViewCount; i++) {
			resIdRadio = getResources().getIdentifier("ivBg_Radio_" + (i + 1),
					"id", getPackageName());
			mRadioBg[i] = (ImageView) this.findViewById(resIdRadio);

			resIdImage = getResources().getIdentifier("ivBg_" + (i + 1), "id",
					getPackageName());
			mImageBg[i] = (ImageView) findViewById(resIdImage);

			resIdSymp = getResources().getIdentifier(
					"tvMeasurementType_" + (i + 1), "id", getPackageName());
			mMeasurementTypeField[i] = (TextView) findViewById(resIdSymp);
			mMeasurementTypeField[i].setOnClickListener(this);
		}

		mDate = (TextView) findViewById(R.id.tvDate);
		mDate.setOnClickListener(this);

		final ImageView mDateBg = (ImageView) findViewById(R.id.ivDate_Bg);
		mDateBg.setOnClickListener(this);

		mCal = Calendar.getInstance();
		mYear = mCal.get(Calendar.YEAR);
		mMonth = mCal.get(Calendar.MONTH);
		mDay = mCal.get(Calendar.DATE);

		mProgDialog.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface arg0) {
				if (null != mHttpUtil) {
					Logger.log(Level.DEBUG, TAG, "ProgressDialog Dismissed");
					mHttpUtil.disconnect();
					mHttpUtil = null;
				}
			}
		});
	}

	/**
	 * Makes the request to cloud to fetch the records between specified dates.
	 */
	private void sendReqToServer() {
		if (null == mFromDate) {
			final long date = mCal.getTimeInMillis();
			mToDate = new Date(date);
			mFromDate = new Date(mToDate.getTime()
					- (7 * Constants.DAY_IN_MILLISEC));
		}

		if (mMeasurementTypeSelected == Measurement.BG) {
			showBgRadioDialog();
		} else {
			showProgressDialog(getString(R.string.loading_text));
			final GetDataRecordsThread getRec = new GetDataRecordsThread();
			getRec.start();
		}

	}

	/**
	 * Thread will send the http request to cloud to fetch the records.
	 *
	 */
	public class GetDataRecordsThread extends Thread {

		/**
		 * Instantiates a new gets the data records thread.
		 */
		public GetDataRecordsThread() {
			if (null != mHttpUtil) {
				mHttpUtil.disconnect();
				mHttpUtil = null;
			}
			mHttpUtil = new HttpUtil();
			mHasDataResponse = false;
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
					jsonObject.put("startDate", getDateString(mFromDate));
					jsonObject.put("endDate", getDateString(mToDate));
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
				mResponseError = false;
				// } catch (IOException e) {
				// mResponseError = true;
				// Logger.log(Level.DEBUG, TAG, "exception++" + e);
				// e.printStackTrace();
				// mAppModel.setGetDataResponse(null);
			} catch (UninitializedMessageException e) {
				mResponseError = true;
				Logger.log(Level.DEBUG, TAG, "exception++=" + e);
				mAppModel.setGetDataResponse(null);
			} catch (NullPointerException npe) {
				Logger.log(Level.DEBUG, TAG, "exception++=" + npe);
			} finally {
				mHandler.sendEmptyMessage(Constants.GETDATA_RESPONSE_RECEIVED);
			}

		}
	}

	/**
	 * Returns the selected option URL like ECG, BG or Activity records.
	 *
	 * @return String
	 */
	private String getDataLogURL() {
		String retStr = null;
		if (mMeasurementTypeSelected == Measurement.ECG) {
			retStr = Constants.GET_ECG_LOGDATA_URL;
		} else if (mMeasurementTypeSelected == Measurement.ACT) {
			retStr = Constants.GET_ACT_LOGDATA_URL;
		} else if (mMeasurementTypeSelected == Measurement.BG) {
			retStr = Constants.GET_BG_LOGDATA_URL;
		}
		return retStr;
	}

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
		// Seekbar progress set to 1 instead of zero as minimum value.
		if (0 == prog) {
			seekBar.setProgress(1);
			return;
		}
		prog = Math.round(((float) prog) / INTERVAL) * INTERVAL;
		// if(progress>0) {
		// seekBar.setProgress(progress);
		// }
		if (0 == prog) {
			prog = 1;
		}
		mNoOfRecs = prog;
		mOldValue = prog;
		mNumOfRec.setText(prog + "");

	}

	/**
	 * show the Date picker dialog to set the date.
	 */
	final private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {

			boolean dateOk = canUpdateDisplay(year, monthOfYear, dayOfMonth);
			if (!dateOk) {
				view.updateDate(mYear, mMonth, mDay);
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
	 * Handler handle the information and errors back from the cloud while
	 * fetching the records from cloud.
	 */
	final private MyHandler mHandler = new MyHandler(this);

	/**
	 * The Class MyHandler.
	 */
	private static class MyHandler extends Handler {

		/** The m activity. */
		private final WeakReference<GetRecords> mActivity;

		/**
		 * Constructor for MyHandler.
		 *
		 * @param activity
		 *            GetRecords
		 */
		private MyHandler(GetRecords activity) {
			mActivity = new WeakReference<GetRecords>(activity);
		}

		/**
		 * Method handleMessage.
		 *
		 * @param msg
		 *            android.os.Message
		 */
		public void handleMessage(android.os.Message msg) {
			final GetRecords parent = mActivity.get();
			if (msg.what == Constants.GETDATA_RESPONSE_RECEIVED) {
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

	/**
	 * Update the display after selecting the date.
	 *
	 * @param year
	 *            the year
	 * @param monthOfYear
	 *            the month of year
	 * @param dayOfMonth
	 *            the day of month
	 * @return boolean
	 */
	private boolean canUpdateDisplay(int year, int monthOfYear, int dayOfMonth) {
		boolean valid = false;
		Logger.log(Level.DEBUG, TAG, "updateDisplay() for set the date");

		// get the current date
		final long timeInLong = mCal.getTimeInMillis();
		mToDate = new Date(timeInLong);

		final Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, monthOfYear);
		c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

		mFromDate = new Date(c.getTimeInMillis());

		final boolean isToday = year == mYear && monthOfYear == mMonth
				&& dayOfMonth == mDay;
		if (mToDate.after(mFromDate) || isToday) {
			mDate.setText(new StringBuilder(256).append(dayOfMonth).append(' ')
					.append(Util.getMonth(monthOfYear)).append(' ')
					.append(year).append(' '));
			valid = true;
		} else {
			mFromDate = null;
			mDate.setText(R.string.date);
			showAlertDialog(get(R.string.information),
					get(R.string.enter_correct_date),
					get(R.string.OK), null, true);
			valid = false;
		}
		return valid;
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
	 * Method onCreateDialog.
	 *
	 * @param id
	 *            int
	 * @return Dialog
	 */
	protected Dialog onCreateDialog(int id) {
		DatePickerDialog dateDlg = null;
		if (id == DATE_DIALOG_ID) {
			dateDlg = new DatePickerDialog(this, mDateSetListener, mYear,
					mMonth, mDay);
		}
		return dateDlg;
	}

	/**
	 * Method getDateString.
	 *
	 * @param date
	 *            Date
	 * @return String
	 */
	private String getDateString(final Date date) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		final StringBuffer dateStr = new StringBuffer(256);
		final int year = cal.get(Calendar.YEAR);
		final int month = cal.get(Calendar.MONTH) + 1; // Calendar.MONTH starts
														// from 0,
														// Calendar.JANUARY == 0
		final int day = cal.get(Calendar.DAY_OF_MONTH);
		dateStr.append(year);
		dateStr.append('-');
		dateStr.append((month < 10) ? "0" + month : month);
		dateStr.append('-');
		dateStr.append((day < 10) ? "0" + day : day);
		return dateStr.toString();
	}

	/**
	 * Method onClick.
	 *
	 * @param v
	 *            View
	 * @see android.view.View$OnClickListener#onClick(View)
	 */
	@SuppressWarnings("deprecation")
	public void onClick(View v) {

		int i = v.getId();
		if (i == R.id.tvMeasurementType_1) {
			mMeasurementTypeSelected = Measurement.ECG;
			mMeasurementTypeField[0]
					.setBackgroundResource(R.xml.single_listview_style);
			mRadioBg[0].setImageResource(R.drawable.ic_radiobutton_selected);
			mRadioBg[1].setImageResource(R.drawable.ic_radiobutton_notselected);
			mRadioBg[2].setImageResource(R.drawable.ic_radiobutton_notselected);

		} else if (i == R.id.tvMeasurementType_2) {
			mMeasurementTypeSelected = Measurement.BG;
			mMeasurementTypeField[1]
					.setBackgroundResource(R.xml.single_listview_style);
			mRadioBg[1].setImageResource(R.drawable.ic_radiobutton_selected);
			mRadioBg[0].setImageResource(R.drawable.ic_radiobutton_notselected);
			mRadioBg[2].setImageResource(R.drawable.ic_radiobutton_notselected);

		} else if (i == R.id.tvMeasurementType_3) {
			mMeasurementTypeSelected = Measurement.ACT;
			mMeasurementTypeField[2]
					.setBackgroundResource(R.xml.single_listview_style);
			mRadioBg[2].setImageResource(R.drawable.ic_radiobutton_selected);
			mRadioBg[0].setImageResource(R.drawable.ic_radiobutton_notselected);
			mRadioBg[1].setImageResource(R.drawable.ic_radiobutton_notselected);

		} else if (i == R.id.btn_menu_positive) {
			if (mPinModel.isLoginForSessionSuccess()) {
				sendReqToServer();
			} else {
				final Intent intent = new Intent(this, LoginActivity.class);
				startActivityForResult(intent, 1);
			}

		} else if (i == R.id.tvDate || i == R.id.ivDate_Bg) {
			showDialog(DATE_DIALOG_ID);

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
	 * Method onStartTrackingTouch.
	 *
	 * @param arg0
	 *            SeekBar
	 * @see android.widget.SeekBar$OnSeekBarChangeListener#onStartTrackingTouch(SeekBar)
	 */
	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
	}

	/**
	 * Method onStopTrackingTouch.
	 *
	 * @param arg0
	 *            SeekBar
	 * @see android.widget.SeekBar$OnSeekBarChangeListener#onStopTrackingTouch(SeekBar)
	 */
	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
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
		final GetDataRecordsThread getRec = new GetDataRecordsThread();
		getRec.start();
	}
}
