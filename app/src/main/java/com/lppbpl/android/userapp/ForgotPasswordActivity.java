/*
 *
 */
package com.lppbpl.android.userapp;

import java.util.Calendar;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.android.userapp.R;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.util.HttpUtil;
import com.lppbpl.android.userapp.util.Util;

// TODO: Auto-generated Javadoc
/**
 * THis activity is to get the forgot possword.
 */
public class ForgotPasswordActivity extends AppBaseActivity implements
		OnClickListener {

	/** The Constant TAG. */
	private static final String TAG = ForgotPasswordActivity.class.getName();

	/** The m user id. */
	private EditText mUserID;

	/** The m mobile no. */
	private EditText mMobileNo;

	/** The m email id. */
	private EditText mEmailId;

	/** The m con code. */
	private EditText mConCode;

	/** The m http util. */
	protected HttpUtil mHttpUtil;

	/** The m year. */
	private int mYear;

	/** The m month. */
	private int mMonth;

	/** The m day. */
	private int mDay;

	/** The m date. */
	private TextView mDate;

	/** The m cal. */
	private Calendar mCal = null;

	/** The m from date. */
	private Date mFromDate;

	/** The m to date. */
	private Date mToDate;

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

		setContentView(R.layout.forgot_password);
		setCustomNoIconTitle(R.string.forgot_password);

		mUserID = (EditText) findViewById(R.id.etUserID);

		mMobileNo = (EditText) findViewById(R.id.etPhoneNumber);

		mEmailId = (EditText) findViewById(R.id.etEmailId);

		mConCode = (EditText) findViewById(R.id.country_code);

		mDate = (TextView) findViewById(R.id.tvDate);
		mDate.setOnClickListener(this);
		setCurrentDateOnView();

		final Button mReset = (Button) findViewById(R.id.btn_menu_positive);
		mReset.setText(R.string.reset);
		mReset.setOnClickListener(this);

		mProgDialog.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface arg0) {
				if (null != mHttpUtil) {
					mHttpUtil.disconnect();
					mHttpUtil = null;
				}
			}
		});
	}

	/**
	 * display current date.
	 */
	public void setCurrentDateOnView() {
		mCal = Calendar.getInstance();
		mYear = mCal.get(Calendar.YEAR);
		mMonth = mCal.get(Calendar.MONTH);
		mDay = mCal.get(Calendar.DATE);
		mDate.setText(new StringBuilder().append(mDay).append(' ')
				.append(Util.getMonth(mMonth)).append(' ').append(mYear)
				.append(' '));
	}

	/**
	 * Receive event on reset button clicked.
	 *
	 * @param v
	 *            the v
	 * @see android.view.View$OnClickListener#onClick(View)
	 */
	@SuppressWarnings("deprecation")
	public void onClick(View v) {
		Logger.log(Level.DEBUG, TAG, "OnClick");
		if (v.getId() == R.id.btn_menu_positive) {
			if (isValidUserData()) {
				if (isNetworkConnected()) {
					new DoResetPassword().execute();
				} else {
					showAlertDialog(
							R.drawable.ic_dialog_no_signal,
							get(R.string.network_connection),
							HttpUtil.getRespnseMsg(HttpUtil.HTTP_CONNECTION_DOWN),
							getResources().getString(R.string.OK), null, true);
				}
			}
		} else if (v.getId() == R.id.tvDate) {
			showDialog(DATE_DIALOG_ID);
		}
	}

	/**
	 * Validates the user entered data while reseting the password.
	 *
	 * @return boolean
	 */
	private boolean isValidUserData() {
		boolean valid = true;
		if (null == mUserID || null == mUserID.getText()
				|| 0 == mUserID.getText().toString().length()) {
			showAlertDialog(get(R.string.forgot_password),
					get(R.string.user_id_empty), get(R.string.OK), null, true);
			valid = false;
		} else if (null == mMobileNo || null == mMobileNo.getText()
				|| 0 == mMobileNo.getText().toString().length()) {
			showAlertDialog(get(R.string.forgot_password),
					get(R.string.enter_currect_mobile_no), get(R.string.OK),
					null, true);
			valid = false;
		} else if (null == mEmailId || null == mEmailId.getText()
				|| 0 == mEmailId.getText().toString().length()) {
			showAlertDialog(get(R.string.forgot_password),
					get(R.string.email_empty), get(R.string.OK), null, true);
			valid = false;
		} else if (null == mConCode || null == mConCode.getText()
				|| 0 == mConCode.getText().toString().trim().length()) {
			showAlertDialog(get(R.string.forgot_password),
					get(R.string.enter_country_code), get(R.string.OK), null,
					true);
			valid = false;
		}
		return valid;
	}

	/**
	 * Asynctask makes the password reset request to cloud and handles the
	 * response.
	 *
	 */
	private class DoResetPassword extends AsyncTask<Void, Void, String> {

		/*
		 * (non-Javadoc)
		 *
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog(R.string.auth_progress);
			if (null == mFromDate) {
				final long date = mCal.getTimeInMillis();
				mFromDate = new Date(date);
			}
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
		protected String doInBackground(Void... arg0) {
			String mPassResetResp = null;
			try {
				if (isNetworkConnected()) {
					final SortedMap<String, String> reqHeaders = mHttpUtil
							.getCommonRequestHeader();
					reqHeaders.put(HttpUtil.ACCEPT_TYPE,
							HttpUtil.APPLICATION_JSON);
					reqHeaders.put(HttpUtil.CONTENT_TYPE,
							HttpUtil.FORM_URLENCODED_TYPE);

					final SortedMap<String, String> reqParam = new TreeMap<String, String>();
					reqParam.put("username", mUserID.getText().toString());
					reqParam.put("phonenumber", mMobileNo.getText().toString());
					reqParam.put("email", mEmailId.getText().toString());
					reqParam.put("dob", Util.getDateString(mFromDate));
					reqParam.put("countryCode", mConCode.getText().toString());

					final String URL = mPinModel.getServerAddress()
							+ Constants.PASSWORD_RESET_URL;

					mPassResetResp = mHttpUtil.postDataThroughParams(
							HttpUtil.POST, URL, reqParam, reqHeaders);
				}

				Logger.log(Level.DEBUG, TAG, "mRegisterResp " + mPassResetResp);
			} catch (NumberFormatException nfe) {
				Logger.log(Level.ERROR, TAG, "" + nfe);
			} catch (NullPointerException ee) {
				Logger.log(Level.ERROR, TAG, "" + ee);
			} catch (Exception e) {
				Logger.log(Level.ERROR, TAG, "" + e);
			}
			return mPassResetResp;
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
			if (mAppModel.getResponseCode() == HttpUtil.HTTP_RESPONSE_SUCCESS) {
				mDialogType = Constants.SHOW_PASSWORD_RESET_SUCSESS_DIALOG;
				showAlertDialog(R.drawable.ic_dialog_success,
						ForgotPasswordActivity.this
								.get(R.string.forgot_password),
						getString(R.string.pass_reset_sucsess),
						ForgotPasswordActivity.this.get(R.string.OK), null,
						false);
			} else {
				/*
				 * When user entered mobile number, User ID or device id already
				 * registered, cloud gives the response as 400.
				 */
				if (mAppModel.getResponseCode() == HttpUtil.HTTP_BAD_REQUEST) {
					mDialogType = Constants.SHOW_PASSWORD_RESET_FAIL_DIALOG;
					showAlertDialog(R.drawable.ic_dialog_error,
							ForgotPasswordActivity.this
									.get(R.string.forgot_password), result,
							ForgotPasswordActivity.this.get(R.string.OK), null,
							false);
				} else {
					showResetPassStatusMsg(isNetworkConnected() ? HttpUtil
							.getRespnseMsg(mAppModel.getResponseCode())
							: HttpUtil
									.getRespnseMsg(HttpUtil.HTTP_CONNECTION_DOWN));
				}
			}
		}
	}

	/**
	 * Show the authentication dialog error for connecting cloud.
	 *
	 * @param status
	 *            the status
	 */
	private void showResetPassStatusMsg(String status) {
		showAlertDialog(R.drawable.ic_dialog_error,
				get(R.string.network_connection), status, getResources()
						.getString(R.string.OK), null, true);
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
		if (mDialogType == Constants.SHOW_PASSWORD_RESET_SUCSESS_DIALOG) {
			finish();
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
			mDate.setText(new StringBuilder().append(dayOfMonth).append(' ')
					.append(Util.getMonth(monthOfYear)).append(' ')
					.append(year).append(' '));
			valid = true;
		} else {
			mFromDate = new Date(timeInLong);
			showAlertDialog(get(R.string.information),
					get(R.string.enter_correct_date),
					get(R.string.OK), null, true);
			valid = false;
		}

		return valid;
	}
}
