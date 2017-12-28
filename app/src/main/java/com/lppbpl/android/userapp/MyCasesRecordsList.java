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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.android.userapp.R;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.listener.SecondaryUserNotification;
import com.lppbpl.android.userapp.model.MyCaseInfo;
import com.lppbpl.android.userapp.util.HttpUtil;
import com.lppbpl.android.userapp.util.Util;

// TODO: Auto-generated Javadoc
/**
 * This Activity is to display the list of consulted cases.
 */
public class MyCasesRecordsList extends MemberBaseActivity implements
		OnClickListener, OnSeekBarChangeListener, OnCheckedChangeListener,
		SecondaryUserNotification {

	/** The Constant TAG. */
	private static final String TAG = MyCasesRecordsList.class.getSimpleName();

	/** The m no of recs. */
	private int mNoOfRecs = 15;

	/** The m measurement type selected. */
	private int mMeasurementTypeSelected = Constants.ALL_TYPE;

	/** The m record status type selected. */
	private String mRecordStatusTypeSelected = Constants.ALL_CASES;

	/** The m from date. */
	private Date mFromDate;

	/** The m to date. */
	private Date mToDate;

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
	private final int mViewCount = 2;

	/** The m radio view count. */
	private final int mRadioViewCount = 4;

	/** The m measurement types. */
	private final TextView[] mMeasurementTypes = new TextView[mViewCount];

	/** The m image bgs. */
	private final ImageView[] mImageBgs = new ImageView[mViewCount];

	/** The m check bg images. */
	private final CheckBox[] mCheckBgImages = new CheckBox[mViewCount];

	/** The m response error. */
	private boolean mResponseError = false;

	/** The m has empty response. */
	private boolean mHasEmptyResponse = false;

	/** The Constant MAXIMUM. */
	private static final int MAXIMUM = 300;

	/** The Constant INTERVAL. */
	private static final int INTERVAL = 5;

	/** The Constant DATE_DIALOG_ID. */
	private static final int DATE_DIALOG_ID = 0;

	/** The my cases. */
	private Vector<MyCaseInfo> myCases;

	/** The response. */
	protected String response;

	/** The m date layout. */
	private RelativeLayout mDateLayout;

	/** The m get by date. */
	private boolean mGetByDate = false;

	/** The m radio bg. */
	final private ImageView[] mRadioBg = new ImageView[mRadioViewCount];

	/** The m radio image bg. */
	final private ImageView[] mRadioImageBg = new ImageView[mRadioViewCount];

	/** The m status type field. */
	final private TextView[] mStatusTypeField = new TextView[mRadioViewCount];

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
		setContentView(R.layout.my_cases_records);

		mGetByDate = getIntent().getBooleanExtra("fetchByDate", false);
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

		// Seekbar to select number of records to fetch from the cloud
		final SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
		seekBar.setMax(MAXIMUM);
		seekBar.setProgress(mOldValue);
		seekBar.setOnSeekBarChangeListener(this);

		mNumOfRec = (TextView) findViewById(R.id.tvNumOfRec);
		mNumOfRec.setText(seekBar.getProgress() + "");
		// find the checkbox view and set listener
		int resIdCheck = 0;
		int resIdImage = 0;
		int resIsSymp = 0;
		for (int i = 0; i < mViewCount; i++) {
			resIdCheck = getResources().getIdentifier("ivBg_Check_" + (i + 1),
					"id", getPackageName());
			mCheckBgImages[i] = (CheckBox) this.findViewById(resIdCheck);
			mCheckBgImages[i].setOnCheckedChangeListener(this);

			resIdImage = getResources().getIdentifier("ivBg_" + (i + 1), "id",
					getPackageName());
			mImageBgs[i] = (ImageView) findViewById(resIdImage);

			resIsSymp = getResources().getIdentifier(
					"tvMeasurementType_" + (i + 1), "id", getPackageName());
			mMeasurementTypes[i] = (TextView) findViewById(resIsSymp);
			mMeasurementTypes[i].setOnClickListener(this);
		}

		// find the radio buttons view and set the listener
		int resIdRadio = 0;
		int resIdSymp = 0;
		for (int i = 0; i < mRadioViewCount; i++) {
			resIdRadio = getResources().getIdentifier(
					"ivCaseStatusBg_Radio_" + (i + 1), "id", getPackageName());
			mRadioBg[i] = (ImageView) this.findViewById(resIdRadio);

			resIdImage = getResources().getIdentifier(
					"ivCaseStatusBg_" + (i + 1), "id", getPackageName());
			mRadioImageBg[i] = (ImageView) findViewById(resIdImage);

			resIdSymp = getResources().getIdentifier(
					"tvCaseStatusType_" + (i + 1), "id", getPackageName());
			mStatusTypeField[i] = (TextView) findViewById(resIdSymp);
			mStatusTypeField[i].setOnClickListener(this);
		}
		mDateLayout = (RelativeLayout) findViewById(R.id.date_layout);
		mDate = (TextView) findViewById(R.id.tvDate);
		mDate.setOnClickListener(this);

		final ImageView mDateBg = (ImageView) findViewById(R.id.ivDate_Bg);
		mDateBg.setOnClickListener(this);

		// setting title for the screen and visibity for date view
		if (mGetByDate) {
			setCustomTitle(R.string.title_specific_cases);
		} else {
			mDateLayout.setVisibility(View.INVISIBLE);
			setCustomTitle(R.string.title_latest_cases);
		}

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
	 * Method onCheckedChanged.
	 *
	 * @param v
	 *            CompoundButton
	 * @param isChecked
	 *            boolean
	 * @see android.widget.CompoundButton$OnCheckedChangeListener#onCheckedChanged(CompoundButton,
	 *      boolean)
	 */
	@Override
	public void onCheckedChanged(CompoundButton v, boolean isChecked) {
		if (v.getId() == R.id.ivBg_Check_1) {
			mMeasurementTypes[0]
					.setBackgroundResource(R.xml.single_listview_style);
		} else if (v.getId() == R.id.ivBg_Check_2) {
			mMeasurementTypes[1]
					.setBackgroundResource(R.xml.single_listview_style);
		}
	}

	/**
	 * selected record type to get.
	 *
	 * @return int
	 */
	private int getSelectedRecordType() {
		if (mCheckBgImages[0].isChecked() && mCheckBgImages[1].isChecked()) {
			mMeasurementTypeSelected = Constants.ALL_TYPE;
		} else if (mCheckBgImages[0].isChecked()) {
			mMeasurementTypeSelected = Constants.ECG;
		} else if (mCheckBgImages[1].isChecked()) {
			mMeasurementTypeSelected = Constants.BG;
		}
		return mMeasurementTypeSelected;
	}

	/**
	 * Makes the request to cloud to fetch the records between specified dates.
	 */
	private void sendReqToServer() {
		showProgressDialog(getString(R.string.loading_text));
		if (null == mFromDate) {
			final long date = mCal.getTimeInMillis();
			mToDate = new Date(date);
			mFromDate = new Date(mToDate.getTime()
					- (7 * Constants.DAY_IN_MILLISEC));
		}
		// GetData Request
		new FetchMyCasesProcess().execute();
	}

	/**
	 * Asynctask which makes request to fetch Consulted cases from cloud.
	 *
	 */
	private class FetchMyCasesProcess extends AsyncTask<Void, Void, String> {

		/*
		 * (non-Javadoc)
		 *
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (null != mHttpUtil) {
				mHttpUtil.disconnect();
				mHttpUtil = null;
			}
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
		public String doInBackground(Void... arg0) {
			final JSONObject jsonObject = new JSONObject();
			try {
				final JSONArray ids = new JSONArray();
				if (0 != mSelectedUserId) {
					ids.put(mSelectedUserId);
				}
				jsonObject.put("userIds", ids);
				jsonObject.put("dataType", getSelectedRecordType());
				jsonObject.put("status", mRecordStatusTypeSelected);
				jsonObject.put("providerId", 0);
				jsonObject.put("pageSize", mNoOfRecs);
				jsonObject.put("currentPageNum", 1);
				jsonObject.put("noOfPages", 0);
				jsonObject.put("forOptOut", false);
				jsonObject.put("paymentMethod", "-1");
				jsonObject.put("startDate",
						mGetByDate ? getDateString(mFromDate) : null);
				jsonObject.put("endDate", mGetByDate ? getDateString(mToDate)
						: null);
				// object.toString();
				Logger.log(Level.DEBUG, "sendReqToServer",
						jsonObject.toString());
			} catch (JSONException ex) {
				Logger.log(Level.ERROR, TAG, "" + ex);
			}
			mResponseError = false;
			mHasEmptyResponse = false;
			try {
				Logger.log(Level.DEBUG, TAG, "postGetDataRequest++");
				if (isNetworkConnected()) {
					final SortedMap<String, String> reqHeaders = mHttpUtil
							.getCommonRequestHeader();
					reqHeaders.put(HttpUtil.ACCEPT_TYPE,
							HttpUtil.APPLICATION_JSON);
					reqHeaders.put(HttpUtil.CONTENT_TYPE,
							HttpUtil.APPLICATION_JSON);

					final String URL = mPinModel.getServerAddress()
							+ Constants.CONSULTED_CASES;

					response = mHttpUtil.postData(HttpUtil.POST, URL,
							jsonObject.toString().getBytes(), reqHeaders);
					Logger.log(Level.DEBUG, TAG, "response " + response);
				}

				Logger.log(Level.DEBUG, TAG, "postGetDataRequest--");
			} catch (UninitializedMessageException e) {
				Logger.log(Level.DEBUG, TAG, "log mHasEmptyResponse 1 == "
						+ mHasEmptyResponse);
				mResponseError = true;
				Logger.log(Level.DEBUG, TAG, "exception++=" + e);
				mAppModel.setGetDataResponse(null);
			} catch (NullPointerException npe) {
				Logger.log(Level.DEBUG, TAG, "exception++=" + npe);
			}
			return response;
		}

		/**
		 * Method onPostExecute.
		 *
		 * @param response
		 *            String
		 */
		protected void onPostExecute(String response) {
			parseResponse(response);
			mHandler.sendEmptyMessage(Constants.GETLATESTREC_RESPONSE_RECEIVED);
		}
	}

	/**
	 * Parse the response from the cloud.
	 *
	 * @param response
	 *            the response
	 */
	public void parseResponse(String response) {
		JSONArray jsonArray = null;
		JSONObject jsonObject = null;
		try {
			jsonArray = new JSONArray(response);
		} catch (Exception e) {
			mResponseError = true;
			jsonArray = null;
			Logger.log(Level.ERROR, TAG, "" + e);
		}
		if (null == jsonArray) {
			mResponseError = true;
		} else {
			final int length = jsonArray.length();
			if (0 != length) {
				myCases = new Vector<MyCaseInfo>(length);
				try {
					MyCaseInfo myCaseInfo = null;
					for (int i = 0; i < length; i++) {
						jsonObject = jsonArray.getJSONObject(i);
						// storing the information in MyCaseInfo object.
						myCaseInfo = new MyCaseInfo();
						myCaseInfo.setCaseId(jsonObject.getInt("id"));
						myCaseInfo
								.setMeasurementId(jsonObject.getInt("dataId"));
						myCaseInfo.setSpecializationId(jsonObject
								.getInt("specializationId"));
						myCaseInfo.setStartTime(jsonObject
								.getLong("slaStartTime"));
						myCaseInfo.setCaseStatus(jsonObject
								.getString("slaStatus"));
						myCaseInfo.setTatType(jsonObject.getString("tatType"));
						myCases.add(myCaseInfo);
					}
					mAppModel.setMyCasesData(myCases);

				} catch (Exception e) {
					mResponseError = true;
					Logger.log(Level.ERROR, TAG, "" + e);
				}
			} else {
				mHasEmptyResponse = true;
			}
		}

	}

	/**
	 * Handles the information and errors back the cloud while fetching
	 * consulted cases.
	 */
	final private MyHandler mHandler = new MyHandler(this);

	/**
	 * The Class MyHandler.
	 */
	private static class MyHandler extends Handler {

		/** The m activity. */
		private final WeakReference<MyCasesRecordsList> mActivity;

		/**
		 * Constructor for MyHandler.
		 *
		 * @param activity
		 *            MyCasesRecordsList
		 */
		private MyHandler(MyCasesRecordsList activity) {
			mActivity = new WeakReference<MyCasesRecordsList>(activity);
		}

		/**
		 * Method handleMessage.
		 *
		 * @param msg
		 *            android.os.Message
		 */
		public void handleMessage(android.os.Message msg) {
			final MyCasesRecordsList parent = mActivity.get();
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
				if (parent.mAppModel.getResponseCode() == HttpUtil.HTTP_RESPONSE_SUCCESS) {
					if (null != parent.response) {
						if (!parent.mHasEmptyResponse) {
							Logger.log(Level.DEBUG, TAG, "toGetRecordsDataList");
							final Intent toMyCasesRec = new Intent(parent,
									GetRecordsDataList.class);
							toMyCasesRec.putExtra("my_cases", true);
							parent.startActivity(toMyCasesRec);
						} else if (parent.response.equals(Integer
								.toString(Constants.SEND_STATUS_NW_ERROR))) {
							parent.showAlertDialog(R.drawable.ic_dialog_error,
									parent.get(R.string.network_error),
									parent.get(R.string.http_general_error),
									parent.get(R.string.OK), null, true);
						} else {
							parent.showAlertDialog(R.drawable.ic_dialog_error,
									parent.get(R.string.title_norecords),
									parent.get(R.string.norecords),
									parent.get(R.string.OK), null, true);
						}
					}
				} else {
					if (parent.isNetworkConnected()) {
						parent.showAlertDialog(
								R.drawable.ic_dialog_error,
								parent.get(R.string.network_error),
								parent.mResponseError ? parent
										.get(R.string.http_general_error)
										: parent.get(R.string.response_failed),
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
	@Override
	public void onProgressChanged(final SeekBar seekBar, final int progress,
			final boolean fromUser) {
		int prog = progress;
		// Seekbar progress set to 1 instead of zero as minimum value.
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
	@Override
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
			mDate.setText(new StringBuilder().append(dayOfMonth).append(' ')
					.append(Util.getMonth(monthOfYear)).append(' ')
					.append(year).append(' '));
			return true;
		} else {
			mFromDate = null;
			mDate.setText(R.string.date);
			showAlertDialog(get(R.string.information),
					get(R.string.enter_correct_date),
					get(R.string.OK), null, true);
			return false;
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
	@Override
	protected Dialog onCreateDialog(int id) {
		DatePickerDialog dateDlg = null;
		if (id == DATE_DIALOG_ID) {
			dateDlg = new DatePickerDialog(this, mDateSetListener, mYear,
					mMonth, mDay);
		}
		return dateDlg;
	}

	/**
	 * Returns string format of Date.
	 *
	 * @param date
	 *            the date
	 * @return String
	 */
	private String getDateString(final Date date) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		final StringBuffer dateStr = new StringBuffer();
		final int year = cal.get(Calendar.YEAR);
		final int month = cal.get(Calendar.MONTH) + 1; // Calendar.MONTH starts
														// from 0,
														// Calendar.JANUARY == 0
		final int day = cal.get(Calendar.DAY_OF_MONTH);
		dateStr.append(year);
		dateStr.append('-');
		dateStr.append(month < 10 ? "0" + month : month);
		dateStr.append('-');
		dateStr.append(day < 10 ? "0" + day : day);
		return dateStr.toString();
	}

	/**
	 * Method onClick.
	 *
	 * @param v
	 *            View
	 * @see android.view.View$OnClickListener#onClick(View)
	 */
	@Override
	@SuppressWarnings("deprecation")
	public void onClick(View v) {
		Logger.log(Level.DEBUG, TAG, "onClick");
		int i = v.getId();
		if (i == R.id.tvCaseStatusType_1) {
			mRecordStatusTypeSelected = Constants.ALL_CASES;
			mStatusTypeField[0]
					.setBackgroundResource(R.xml.single_listview_style);
			mRadioBg[0].setImageResource(R.drawable.ic_radiobutton_selected);
			mRadioBg[1].setImageResource(R.drawable.ic_radiobutton_notselected);
			mRadioBg[2].setImageResource(R.drawable.ic_radiobutton_notselected);
			mRadioBg[3].setImageResource(R.drawable.ic_radiobutton_notselected);

		} else if (i == R.id.tvCaseStatusType_2) {
			mRecordStatusTypeSelected = Constants.ASSIGNED_CASES;
			mStatusTypeField[1]
					.setBackgroundResource(R.xml.single_listview_style);
			mRadioBg[1].setImageResource(R.drawable.ic_radiobutton_selected);
			mRadioBg[0].setImageResource(R.drawable.ic_radiobutton_notselected);
			mRadioBg[2].setImageResource(R.drawable.ic_radiobutton_notselected);
			mRadioBg[3].setImageResource(R.drawable.ic_radiobutton_notselected);

		} else if (i == R.id.tvCaseStatusType_3) {
			mRecordStatusTypeSelected = Constants.PENDING_CASES;
			mStatusTypeField[2]
					.setBackgroundResource(R.xml.single_listview_style);
			mRadioBg[2].setImageResource(R.drawable.ic_radiobutton_selected);
			mRadioBg[0].setImageResource(R.drawable.ic_radiobutton_notselected);
			mRadioBg[1].setImageResource(R.drawable.ic_radiobutton_notselected);
			mRadioBg[3].setImageResource(R.drawable.ic_radiobutton_notselected);

		} else if (i == R.id.tvCaseStatusType_4) {
			mRecordStatusTypeSelected = Constants.CLOSED_CASES;
			mStatusTypeField[3]
					.setBackgroundResource(R.xml.single_listview_style);
			mRadioBg[3].setImageResource(R.drawable.ic_radiobutton_selected);
			mRadioBg[0].setImageResource(R.drawable.ic_radiobutton_notselected);
			mRadioBg[1].setImageResource(R.drawable.ic_radiobutton_notselected);
			mRadioBg[2].setImageResource(R.drawable.ic_radiobutton_notselected);

		} else if (i == R.id.tvMeasurementType_1) {
			mMeasurementTypes[0]
					.setBackgroundResource(R.xml.single_listview_style);
			if (mCheckBgImages[0].isChecked()) {
				mCheckBgImages[0].setChecked(false);
			} else {
				mCheckBgImages[0].setChecked(true);
				mMeasurementTypeSelected = Constants.ECG;
			}

		} else if (i == R.id.tvMeasurementType_2) {
			mMeasurementTypes[1]
					.setBackgroundResource(R.xml.single_listview_style);
			if (mCheckBgImages[1].isChecked()) {
				mCheckBgImages[1].setChecked(false);
			} else {
				mMeasurementTypeSelected = Constants.BG;
				mCheckBgImages[1].setChecked(true);
			}

		} else if (i == R.id.btn_menu_positive) {
			if (mCheckBgImages[0].isChecked() || mCheckBgImages[1].isChecked()) {
				if (mPinModel.isLoginForSessionSuccess()) {
					sendReqToServer();
				} else {
					final Intent intent = new Intent(this, LoginActivity.class);
					startActivityForResult(intent, 1);
				}
			} else {
				showAlertDialog(get(R.string.information),
						getString(R.string.select_record_type),
						get(R.string.OK), null, true);
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

	}
}
