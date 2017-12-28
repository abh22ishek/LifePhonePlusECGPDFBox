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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.DeviceData;
import com.lppbpl.android.userapp.R;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.model.Registration;
import com.lppbpl.android.userapp.model.Registration.UserCategory;
import com.lppbpl.android.userapp.util.HttpUtil;
import com.lppbpl.android.userapp.util.Util;

/**
 * This Activity is to display user register screen.
 *
 */
public class RegistrationActivity extends AppBaseActivity implements
		OnTouchListener {

	private static final int STATE_CREATE_ACCOUNT = 1;
	private static final int STATE_PERSONAL_DETAILS = 2;
	private static final int STATE_CONTACT_DETAILS = 3;
	private static final int STATE_ACCOUNT_FINISH = 4;

	private static final int SHOW_DATE_PICKER = 1;

	private DoCreateMember mCreateMember;

	/** The Constant TAG. */
	private static final String TAG = RegistrationActivity.class.getName();

	/** The m http util. */
	protected HttpUtil mHttpUtil;

	/** The m device key. */
	private String mDeviceKey = null;

	private final Registration mRegModel = new Registration();

	/** The m year. */
	private int mYear;

	/** The m month. */
	private int mMonth;

	/** The m day. */
	private int mDay;

	/** The m cal. */
	private Calendar mCal = null;

	/** The m from date. */
	private Date mFromDate;

	/** The m to date. */
	private Date mToDate;

	private LoadUserCatagory loadUserCatagory;
	protected DoRegister register;

	private boolean mIsCreatMember;

	private final int REQUEST_CODE_LOGIN = 1;

	private ValidateUserIdAction validateUserIdAction;
	private ValidatePasswordAction validatePasswordAction;

	/**
	 * Method onCreate.
	 *
	 * @param savedInstanceState
	 *            Bundle
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.registration_page_1);

		mIsCreatMember = getIntent().getBooleanExtra("IS_MEMBER", false);

		setCustomNoIconTitle(mIsCreatMember ? R.string.add_members
				: R.string.title_registration);

		final DeviceData data = mAppModel.getDeviceData();
		if (null != data) {
			mDeviceKey = data.getDeviceUid();
		} else {
			mDeviceKey = mPinModel.getDeviceUid();
		}

		initialize(STATE_CREATE_ACCOUNT);

		if (!mIsCreatMember) {
			if (isNetworkConnected()) {
				loadUserCatagory = new LoadUserCatagory();
				loadUserCatagory.execute();
			} else {
				showAlertDialog(R.drawable.ic_dialog_no_signal,
						get(R.string.network_connection),
						HttpUtil.getRespnseMsg(HttpUtil.HTTP_CONNECTION_DOWN),
						get(R.string.OK), null, true);
			}
		}

		mProgDialog.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface arg0) {
				if (null != mHttpUtil) {
					mHttpUtil.disconnect();
					mHttpUtil = null;
				}
			}
		});

	}

	@SuppressLint("CutPasteId")
	private void initialize(final int state) {
		if (state == STATE_CREATE_ACCOUNT) {
			setContentView(R.layout.registration_page_1);
			final EditText userId = (EditText) findViewById(R.id.edt_userid);
			final EditText psw = (EditText) findViewById(R.id.edt_psw);
			final EditText confPsw = (EditText) findViewById(R.id.edt_confirmpsw);
			final Spinner spinner = (Spinner) findViewById(R.id.spr_usercategory);
			userId.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (!hasFocus && userId != null) {
						if (userId.getText().toString().trim().isEmpty()) {
							userId.setError(getString(R.string.user_id_empty));
						} else if (userId.getText().toString().trim().length() < 6) {
							userId.setError(getString(R.string.user_id_incorrect_length));
						}
					}
				}
			});
			userId.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
					userId.setError(null);
				}
			});
			psw.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (!hasFocus && psw != null) {
						if (psw.getText().toString().trim().isEmpty()) {
							psw.setError(getString(R.string.password_empty));
						} else if (psw.getText().toString().trim().length() < 8) {
							psw.setError(getString(R.string.password_incorrect_length));
						}
					}
				}
			});
			psw.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
					psw.setError(null);
					confPsw.setError(null);
				}
			});
			confPsw.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
					confPsw.setError(null);
				}
			});
			if (mIsCreatMember) {
				spinner.setVisibility(View.GONE);
				((TextView) findViewById(R.id.txt_consumer_category))
						.setVisibility(View.GONE);
				((TextView) findViewById(R.id.txt_spinner_hint))
						.setVisibility(View.GONE);
			}
			if (mRegModel.getUserType() != null) {
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						getApplication(),
						android.R.layout.simple_dropdown_item_1line,
						mRegModel.getUserType());
				adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
				spinner.setAdapter(adapter);
				spinner.setSelection(mRegModel.getSelectionPos());
			}
			if (mRegModel.getUserId() != null) {
				userId.setText(mRegModel.getUserId());
			}
			if (mRegModel.getPassword() != null) {
				psw.setText(mRegModel.getPassword());
			}
			if (mRegModel.getConfirmPassword() != null) {
				confPsw.setText(mRegModel.getConfirmPassword());
			}
			final Button register = (Button) findViewById(R.id.btn_menu_positive);
			register.setText(R.string.next);
			register.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					final String userIdVal = userId.getText().toString().trim();
					final String pswVal = psw.getText().toString().trim();
					final String confPswVal = confPsw.getText().toString().trim();

					if (userIdVal.isEmpty()) {
						userId.setError(getString(R.string.user_id_empty));
					} else if (userIdVal.length() < 6) {
						userId.setError(getString(R.string.user_id_incorrect_length));
					} else if (userId.getError() != null) {
						showRegistrationStatusMsg(userId.getError().toString());
					} else if (pswVal.isEmpty()) {
						psw.setError(getString(R.string.password_empty));
					} else if (pswVal.length() < 8) {
						psw.setError(getString(R.string.password_incorrect_length));
					} else if (psw.getError() != null) {
						showRegistrationStatusMsg(psw.getError().toString());
					} else if (confPswVal.isEmpty()) {
						confPsw.setError(getString(R.string.conf_password_empty));
					} else if (confPsw.getError() != null) {
						showRegistrationStatusMsg(confPsw.getError().toString());
					} else if (!pswVal.equals(confPswVal)) {
						confPsw.setError(getString(R.string.pass_and_conf_same));
					} else if (mRegModel.getUserType() == null && !mIsCreatMember) {
						showRegistrationStatusMsg("Unable to load the consumer category.");
					} else {
						showProgressDialog(getString(R.string.please_wait_text));
						validateUserIDAction();
					}
				}
			});

		} else if (state == STATE_PERSONAL_DETAILS) {
			setContentView(R.layout.registration_page_2);
			final EditText firstName = (EditText) findViewById(R.id.edt_firstname);
			final EditText lastName = (EditText) findViewById(R.id.edt_lastname);
			final RadioGroup gender = (RadioGroup) findViewById(R.id.radioGroup1);
			final TextView dateOfBirth = (TextView) findViewById(R.id.txt_date);
			mCal = Calendar.getInstance();
			mYear = mCal.get(Calendar.YEAR);
			mMonth = mCal.get(Calendar.MONTH);
			mDay = mCal.get(Calendar.DATE);
			mFromDate = mCal.getTime();
			String dob = new StringBuilder().append(mDay).append(' ')
					.append(Util.getMonth(mMonth)).append(' ').append(mYear)
					.append(' ').toString();
			dateOfBirth.setText(dob);
			dateOfBirth.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mHandler.sendEmptyMessage(SHOW_DATE_PICKER);
				}
			});
			if (mRegModel.getFirstName() != null) {
				firstName.setText(mRegModel.getFirstName());
			}
			if (mRegModel.getLastName() != null) {
				lastName.setText(mRegModel.getLastName());
			}
			if (mRegModel.getDateOfBirth() != null) {
				dateOfBirth.setText(mRegModel.getDateOfBirth());
			}
			boolean male = mRegModel.isMale();
			((RadioButton) findViewById(R.id.rbMale)).setChecked(male ? true
					: false);
			((RadioButton) findViewById(R.id.rbFemale)).setChecked(male ? false
					: true);
			final Button next = (Button) findViewById(R.id.btn_menu_positive);
			next.setText(R.string.next);
			next.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					final String firstNameVal = firstName.getText().toString()
							.trim();
					final String lastNameVal = lastName.getText().toString()
							.trim();
					final String dobVal = Util.getDateString(mFromDate);

					if (firstNameVal.isEmpty()) {
						// showRegistrationStatusMsg(get(R.string.first_name_empty));
						firstName.setError(get(R.string.first_name_empty));
					} else if (lastNameVal.isEmpty()) {
						// showRegistrationStatusMsg(get(R.string.last_name_empty));
						lastName.setError(get(R.string.last_name_empty));
					} else {
						mRegModel.setFirstName(firstNameVal);
						mRegModel.setLastName(lastNameVal);
						mRegModel.setDateOfBirth(dobVal);
						final int id = gender.getCheckedRadioButtonId();
						mRegModel.setMale(id == R.id.rbMale);

						initialize(STATE_CONTACT_DETAILS);
					}
				}
			});
			final Button back = (Button) findViewById(R.id.btn_menu_negative);
			back.setText(R.string.back);
			back.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					initialize(STATE_CREATE_ACCOUNT);
				}
			});

		} else if (state == STATE_CONTACT_DETAILS) {
			setContentView(R.layout.registration_page_3);
			final EditText countryCode = (EditText) findViewById(R.id.edt_country_code);
			final EditText phoneNumber = (EditText) findViewById(R.id.edt_phonenumber);
			final EditText email = (EditText) findViewById(R.id.edt_email);
			if (mRegModel.getCountryCode() != null) {
				countryCode.setText(mRegModel.getCountryCode());
			}
			if (mRegModel.getPhoneNumber() != null) {
				phoneNumber.setText(mRegModel.getPhoneNumber());
			}
			if (mRegModel.getEmail() != null) {
				email.setText(mRegModel.getEmail());
			}
			final Button next = (Button) findViewById(R.id.btn_menu_positive);
			next.setText(mIsCreatMember ? R.string.register : R.string.next);
			next.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					final String cCodeVal = countryCode.getText().toString()
							.trim();
					final String phoneNumberVal = phoneNumber.getText()
							.toString().trim();
					final String emailVal = email.getText().toString().trim();

					if (cCodeVal.isEmpty()) {
						countryCode.setError("Country Code can not be empty.");
					} else if (phoneNumberVal.isEmpty()) {
						phoneNumber.setError("Mobile Number can not be empty.");
					} else if (emailVal.isEmpty()) {
						email.setError("Email id can not be empty.");
					} else {
						mRegModel.setCountryCode(cCodeVal);
						mRegModel.setPhoneNumber(phoneNumberVal);
						mRegModel.setEmail(emailVal);
						if (mIsCreatMember) {
							if (isNetworkConnected()) {
								if (mPinModel.isLoginForSessionSuccess()) {
									mCreateMember = new DoCreateMember(
											RegistrationActivity.this);
									mCreateMember.execute();
								} else {
									final Intent intent = new Intent(RegistrationActivity.this,
											LoginActivity.class);
									startActivityForResult(intent,
											REQUEST_CODE_LOGIN);
								}
							} else {
								showAlertDialog(
										R.drawable.ic_dialog_no_signal,
										get(R.string.network_connection),
										HttpUtil.getRespnseMsg(HttpUtil.HTTP_CONNECTION_DOWN),
										get(R.string.OK), null, true);
							}
						} else {
							initialize(STATE_ACCOUNT_FINISH);
						}
					}
				}
			});
			final Button back = (Button) findViewById(R.id.btn_menu_negative);
			back.setText(R.string.back);
			back.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					initialize(STATE_PERSONAL_DETAILS);
				}
			});

		} else if (state == STATE_ACCOUNT_FINISH) {
			setContentView(R.layout.registration_page_4);
			final TextView mTncnLinkTxtView = (TextView) findViewById(R.id.tvtermCondLink);
			mTncnLinkTxtView.setText(Html
					.fromHtml("<u>Terms and conditions</u>"));
			mTncnLinkTxtView.setOnTouchListener(this);
			mTncnLinkTxtView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					final String url = mPinModel.getServerAddress()
							+ Constants.TERM_CONDITION_URL;
					DoTnCnRequest doTnCnRequest = new DoTnCnRequest(
							RegistrationActivity.this, url);
					doTnCnRequest.execute();
				}
			});
			final TextView mPrivacyLinkTxtView = (TextView) findViewById(R.id.privacyLink);
			mPrivacyLinkTxtView
					.setText(Html.fromHtml("<u>Privacy consent</u>"));
			mPrivacyLinkTxtView.setOnTouchListener(this);
			mPrivacyLinkTxtView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					final String url = mPinModel.getServerAddress()
							+ Constants.PRIVACY_URL;
					DoTnCnRequest doTnCnRequest = new DoTnCnRequest(
							RegistrationActivity.this, url);
					doTnCnRequest.execute();
				}
			});
			final CheckBox accept = (CheckBox) findViewById(R.id.tvtermCheck);
			if (mRegModel.getTncVersion() == null) {
				accept.setEnabled(false);
			} else {
				accept.setChecked(true);
			}
			final Button next = (Button) findViewById(R.id.btn_menu_positive);
			next.setText(R.string.register);
			next.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (accept.isChecked()) {
						register = new DoRegister();
						register.execute();
					} else {
						showRegistrationStatusMsg("Kindly read and accept the \"Terms and conditions\" and \"Privacy consent\".");
					}
				}
			});
			final Button back = (Button) findViewById(R.id.btn_menu_negative);
			back.setText(R.string.back);
			back.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					initialize(STATE_CONTACT_DETAILS);
				}
			});

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

	@Override
	public void onBackPressed() {
		mDialogType = Constants.SHOW_DISCARD_DIALOGE;
		showAlertDialog(getString(R.string.registration),
				getString(R.string.cancel_registration), getString(R.string.yes),
				getString(R.string.no), false);
	}

	/**
	 * Method showRegistrationStatusMsg.
	 *
	 * @param status
	 *            String
	 */
	private void showRegistrationStatusMsg(String status) {
		showAlertDialog(
				getResources().getString(R.string.title_authentication),
				status, getResources().getString(R.string.OK), null, true);
	}

	/**
	 * Returns the response status.
	 *
	 * @param response
	 *            String
	 * @return int
	 */
	private int getResponseStatus(String response) {
		int retVal = -1;
		try {
			if (null != response) {
				int index = response.indexOf(',');
				String status = response.substring(0, index);
				index = status.indexOf(':');
				status = status.substring(index + 2);
				retVal = Integer.parseInt(status);
			}
		} catch (StringIndexOutOfBoundsException siof) {
			Logger.log(Level.INFO, TAG, "" + siof.getMessage());
		} catch (Exception e) {
			Logger.log(Level.INFO, TAG, "" + e.getMessage());
		}
		return retVal;
	}

	/**
	 * Process registration.
	 */
	private void processRegistration() {
		if (Logger.isInfoEnabled()) {
			Logger.log(Level.INFO, TAG, "Registration success");
		}
		mDialogType = Constants.SHOW_REGISTRATION_SUCCESS_DIALOG;
		showAlertDialog(
				R.drawable.ic_dialog_success,
				get(R.string.registration),
				String.format(get(R.string.quick_registration),
						mAppModel.getSupportContactNo()), get(R.string.OK),
				null, false);
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
		if (mDialogType == Constants.SHOW_REGISTRATION_SUCCESS_DIALOG) {
			// commit the device id only after registration success
			final SharedPreferences saveUserData = getSharedPreferences(
					Constants.USER_DETAILS_DB, MODE_PRIVATE);
			final SharedPreferences.Editor editor = saveUserData.edit();
			editor.putString(Constants.SAVE_DEVICE_ID, mDeviceKey);
			final DeviceData data = mAppModel.getDeviceData();
			if (null != data) {
				editor.putString(Constants.SAVE_DEVICE_VERSION, data.getFirmwareVersion());
			}
			editor.commit();

			final Intent toMenu = new Intent(this, MainMenuActivity.class);
			startActivity(toMenu);
			finish();
		} else if (mDialogType == Constants.SHOW_USER_CATAGORY_DIALOG) {
			loadUserCatagory = new LoadUserCatagory();
			loadUserCatagory.execute();
		} else if (mDialogType == Constants.SHOW_DISCARD_DIALOGE
				|| mDialogType == Constants.SHOW_INFO_DIALOGE) {
			finish();
		} else if (mDialogType == Constants.SHOW_SESSION_EXPIRED_DIALOG) {
			mPinModel.setLoginForSessionSuccess(false);
			final Intent intent = new Intent(this, LoginActivity.class);
			startActivityForResult(intent, REQUEST_CODE_LOGIN);
		}
	}

	@Override
	protected void onNegativeButtonClick() {
		super.onNegativeButtonClick();
		if (mDialogType == Constants.SHOW_USER_CATAGORY_DIALOG) {
			finish();
		}
	}

	/**
	 * Asyntask will process the login request to cloud.
	 *
	 */
	private class DoCreateMember extends AsyncTask<Void, Void, String> {

		/** The json object. */
		final private JSONObject jsonObject;

		/** The parent. */
		final private RegistrationActivity parent;

		/**
		 * Constructor for DoCreateMember.
		 *
		 * @param parent
		 *            CreateMemberPage2Activity
		 */
		private DoCreateMember(RegistrationActivity parent) {
			this.parent = parent;
			jsonObject = new JSONObject();
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
			try {
				jsonObject.put("userId", mRegModel.getUserId());
				jsonObject.put("password", mRegModel.getPassword());
				jsonObject.put("confirmPassword",
						mRegModel.getConfirmPassword());
				jsonObject.put("firstName", mRegModel.getFirstName());
				jsonObject.put("lastName", mRegModel.getLastName());
				jsonObject.put("countryCode", mRegModel.getCountryCode());
				jsonObject.put("phoneNumber", mRegModel.getPhoneNumber());
				jsonObject.put("email", mRegModel.getEmail());
				jsonObject.put("dateOfBirth", mRegModel.getDateOfBirth());
				jsonObject
						.put("gender", mRegModel.isMale() ? "Male" : "Female");
			} catch (JSONException e) {
				Logger.log(Level.DEBUG, TAG, "JSON Exception" + e);
			}
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
					+ Constants.CREATE_MEMBER;

			final String status = mHttpUtil.postData(HttpUtil.POST, URL,
					jsonObject.toString().getBytes(), reqHeaders);

			if (Logger.isDebugEnabled()) {
				Logger.log(Level.DEBUG, TAG, "Member creation status: "
						+ status);
			}

			return status;
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

			if (parent.isAccessTokenExpired()) {
				Logger.log(Level.DEBUG, TAG, "Access token expires.");
				parent.mDialogType = Constants.SHOW_SESSION_EXPIRED_DIALOG;
				parent.showAlertDialog(
						R.drawable.ic_dialog_cloud_upload_state_failed,
						parent.get(R.string.upload_failed),
						parent.get(R.string.session_expired),
						parent.get(R.string.OK), null, false);
			} else {

				final boolean success = mAppModel.getResponseCode() == HttpUtil.HTTP_RESPONSE_SUCCESS;
				parent.mDialogType = Constants.SHOW_INFO_DIALOGE;
				showAlertDialog(
						success ? R.drawable.ic_dialog_success
								: R.drawable.ic_dialog_error,
						parent.get(R.string.add_members),
						success ? parent.get(R.string.member_registration) : result,
						parent.get(R.string.OK), null, success ? false : true);

			}
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
		if (requestCode == REQUEST_CODE_LOGIN && resultCode == RESULT_OK) {
			mCreateMember = new DoCreateMember(this);
			mCreateMember.execute();
		}
	}

	/**
	 * Asynctask makes the registration request to cloud and handles the
	 * response.
	 *
	 */
	private class DoRegister extends AsyncTask<Void, Void, Void> {

		/** The m register resp. */
		private String mRegisterResp = null;

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
			try {
				mPinModel.setUserID(mRegModel.getUserId());
				if (null != mDeviceKey)
					mPinModel.setDeviceUid(mDeviceKey);

				final SortedMap<String, String> reqHeaders = mHttpUtil
						.getCommonRequestHeader();
				reqHeaders.put(HttpUtil.ACCEPT_TYPE, HttpUtil.PLAIN_TEXT_TYPE);
				reqHeaders
						.put(HttpUtil.CONTENT_TYPE, HttpUtil.APPLICATION_JSON);

				JSONObject object = new JSONObject();
				object.put("userId", mRegModel.getUserId());
				object.put("password", mRegModel.getPassword());
				object.put("confirmPassword", mRegModel.getConfirmPassword());
				object.put("firstName", mRegModel.getFirstName());
				object.put("lastName", mRegModel.getLastName());
				object.put("email", mRegModel.getEmail());
				object.put("gender", mRegModel.isMale() ? "Male" : "Female");
				object.put("dateOfBirth", mRegModel.getDateOfBirth());
				object.put("countryCode", mRegModel.getCountryCode());
				object.put("phoneNumber", mRegModel.getPhoneNumber());
				object.put("deviceId", mPinModel.getDeviceUid());

				final int id = mRegModel.getUserCategoryList()
						.get(mRegModel.getSelectionPos()).getSegmentId();
				object.put("customerSegment", id);
				object.put("termsandConditions", mRegModel.getTncVersion());

				final String URL = mPinModel.getServerAddress()
						+ Constants.REGISTRATION_URL;

				mRegisterResp = mHttpUtil.postData(HttpUtil.POST, URL, object
						.toString().getBytes(), reqHeaders);

				Logger.log(Level.DEBUG, TAG, "mRegisterResp " + mRegisterResp);
			} catch (NumberFormatException nfe) {
				Logger.log(Level.ERROR, TAG, "" + nfe);
			} catch (NullPointerException ee) {
				Logger.log(Level.ERROR, TAG, "" + ee);
			} catch (Exception e) {
				Logger.log(Level.ERROR, TAG, "" + e);
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
			final int resCode = getResponseStatus(mRegisterResp);
			Logger.log(Level.DEBUG, TAG, "resCode " + resCode);
			if ((resCode == Constants.REGISTER_SUCCESS || resCode == Constants.REGISTER_UPDATE)
					&& mAppModel.getResponseCode() == HttpUtil.HTTP_RESPONSE_SUCCESS) {
				// mProvAck = true;
				// sendUserProvMessage();
				processRegistration();
			} else {
				/*
				 * When user entered mobile number, User ID or device id already
				 * registered, cloud gives the response as 400.
				 */
				if (mAppModel.getResponseCode() == HttpUtil.HTTP_BAD_REQUEST) {
					mDialogType = Constants.SHOW_REGISTRATION_FAIL_DIALOG;
					final String registerResp = mRegisterResp.contains("2,") ? (mRegisterResp
							.substring(mRegisterResp.indexOf("2,") + 2))
							: mRegisterResp;
					showAlertDialog(
							R.drawable.ic_dialog_error,
							RegistrationActivity.this
									.get(R.string.registration),
							// RegistrationActivity.this.get(R.string.registration_error),
							registerResp,
							RegistrationActivity.this.get(R.string.OK), null,
							false);
				} else {
					showRegistrationStatusMsg(isNetworkConnected() ? HttpUtil
							.getRespnseMsg(mAppModel.getResponseCode())
							: HttpUtil
									.getRespnseMsg(HttpUtil.HTTP_CONNECTION_DOWN));
				}
			}
		}
	}

	/**
	 * Asynctask makes the registration request to cloud and handles the
	 * response.
	 *
	 */
	private class LoadUserCatagory extends AsyncTask<Void, Void, String> {

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
		 * @return Void
		 */
		@Override
		protected String doInBackground(Void... v) {
			final String requestUri = mPinModel.getServerAddress()
					+ Constants.CUSTOMER_SEGMENT;

			TreeMap<String, String> reqHeaders = mHttpUtil
					.getCommonRequestHeader();
			reqHeaders.put(HttpUtil.ACCEPT_TYPE, HttpUtil.APPLICATION_JSON);
			reqHeaders.put(HttpUtil.CONTENT_TYPE, HttpUtil.APPLICATION_JSON);
			final SortedMap<String, String> reqParams = new TreeMap<String, String>();
			final String responseText = mHttpUtil.postDataThroughParams(HttpUtil.POST, requestUri, reqParams, reqHeaders);
			return responseText;
		}

		/**
		 * Method onPostExecute.
		 *
		 * @param result
		 *            Void
		 */
		@Override
		protected void onPostExecute(final String result) {
			super.onPostExecute(result);
			dismissProgressDislog();
			if (result != null) {
				Logger.log(Level.DEBUG, TAG, result);
				try {
					JSONArray array = new JSONArray(result);
					if (array != null) {
						final ArrayList<UserCategory> userCat = new ArrayList<UserCategory>();
						for (int i = 0; i < array.length(); i++) {
							final UserCategory category = mRegModel.new UserCategory();
							final JSONObject object = array.getJSONObject(i);
							category.setId(object.getInt("id"));
							category.setSegmentId(object.getInt("segment_id"));
							category.setPartnerId(object.getInt("partnerId"));
							category.setName(object.getString("name"));
							userCat.add(category);
						}
						mRegModel.setUserCategoryList(userCat);
						final Spinner spinner = (Spinner) findViewById(R.id.spr_usercategory);
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(
								getApplication(),
								android.R.layout.simple_dropdown_item_1line,
								mRegModel.getUserType());
						adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
						spinner.setAdapter(adapter);

					}
				} catch (JSONException e) {
					e.printStackTrace();
					mDialogType = Constants.SHOW_USER_CATAGORY_DIALOG;
					showAlertDialog(
							getString(R.string.registration),
							getString(R.string.fetch_consumer_category_error),
							"Retry", "Cancel", false);
				}
			} else {
				mDialogType = Constants.SHOW_USER_CATAGORY_DIALOG;
				showAlertDialog(
						getString(R.string.registration),
						getString(R.string.fetch_consumer_category_error),
						"Retry", "Cancel", false);
			}
		}
	}

	/**
	 * Asynctask makes the request to send the terms and condition, privacy
	 * policy data from cloud.
	 *
	 */
	private class DoTnCnRequest extends AsyncTask<Void, Void, String> {
		private RegistrationActivity parent;
		private String url;

		private DoTnCnRequest(RegistrationActivity parent, String url) {
			this.parent = parent;
			this.url = url;
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
			reqHeaders
					.put(HttpUtil.CONTENT_TYPE, HttpUtil.FORM_URLENCODED_TYPE);
			reqParams.put("usertype", "1");
			reqParams.put("customerSegment", mRegModel.getUserCategoryList()
					.get(mRegModel.getSelectionPos()).getSegmentId()
					+ "");

			final String mStatus = mHttpUtil.postDataThroughParams(
					HttpUtil.POST, url, reqParams, reqHeaders);

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
						final String tncVersion = o.getString("version");
						parent.mRegModel.setTncVersion(tncVersion);
						result = URLDecoder
								.decode(o.getString("text"), "UTF-8");
						startPopupActivity(result);

						final CheckBox accept = (CheckBox) findViewById(R.id.tvtermCheck);
						accept.setEnabled(true);

					}
				} catch (JSONException e) {
					Logger.log(Level.DEBUG, TAG, "" + e);
					showAlertDialog(R.drawable.ic_dialog_info,
							parent.get(R.string.network_error),
							parent.get(R.string.http_general_error),
							parent.get(R.string.OK), null, true);
				} catch (UnsupportedEncodingException uec) {
					Logger.log(Level.DEBUG, TAG, "" + uec);
				}

			} else {
				if (null != result) {
					showAlertDialog(R.drawable.ic_dialog_info,
							parent.get(R.string.information), result,
							parent.get(R.string.OK), null, true);
				} else {
					// showResponseStatusMsg(HttpUtil.getRespnseMsg(mAppModel
					// .getResponseCode()));

					showAlertDialog(R.drawable.ic_dialog_info,
							parent.get(R.string.network_error),
							parent.get(R.string.http_general_error),
							parent.get(R.string.OK), null, true);

				}
			}

		}

		private void startPopupActivity(String result) {
			final Intent intent = new Intent(parent, TncnPrivacyPopup.class);
			intent.putExtra("tncnPrivacyResponse", result);
			final String title = url.contains("privacypolicy") ? getString(R.string.title_privacy)
					: getString(R.string.title_tnc);
			intent.putExtra("popupTitle", title);
			startActivity(intent);
		}

	}

	/** The m handler. */
	final private MyHandler mHandler = new MyHandler(this);

	/**
	 * The Class MyHandler.
	 */
	private static class MyHandler extends Handler {

		/** The m activity. */
		private final WeakReference<RegistrationActivity> mActivity;

		/**
		 * Constructor for MyHandler.
		 *
		 * @param activity
		 *            RegistrationActivity
		 */
		private MyHandler(RegistrationActivity activity) {
			mActivity = new WeakReference<RegistrationActivity>(activity);
		}

		/**
		 * Method handleMessage.
		 *
		 * @param msg
		 *            android.os.Message
		 */
		public void handleMessage(android.os.Message msg) {
			final RegistrationActivity parent = mActivity.get();
			if (msg.what == Constants.SHUTDOWN_TIMEOUT) {
				Logger.log(Level.DEBUG, TAG,
						"SHUTDOWN_TIMEOUT. so app is closing");
				parent.closeAllActivities();
			} else if (msg.what == SHOW_DATE_PICKER) {
				final DatePickerDialog dateDlg = new DatePickerDialog(parent,
						parent.mDateSetListener, parent.mYear, parent.mMonth,
						parent.mDay);
				dateDlg.show();
			}
		}
	};

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
			mDay = dayOfMonth;
			mMonth = monthOfYear;
			mYear = year;
			final TextView dateOfBirth = (TextView) findViewById(R.id.txt_date);
			String dob = new StringBuilder().append(dayOfMonth).append(' ')
					.append(Util.getMonth(monthOfYear)).append(' ')
					.append(year).append(' ').toString();
			dateOfBirth.setText(dob);
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
	@SuppressLint("ClickableViewAccessibility")
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

	/**
	 * Asynctask makes the request to validate the userid from cloud.
	 *
	 */
	private class ValidateUserIdAction extends AsyncTask<Void, Void, String> {

		private EditText enteredUserId;

		private ValidateUserIdAction(EditText userId) {
			this.enteredUserId = userId;
		}

		protected void onPreExecute() {
			super.onPreExecute();
			mHttpUtil = new HttpUtil();
		}

		@Override
		protected String doInBackground(Void... params) {
			final String requestUri = mPinModel.getServerAddress() + Constants.CHECK_USER;

			final HashMap<String, String> reqParams = new HashMap<String, String>();
			reqParams.put("username", enteredUserId.getText().toString().trim());

			TreeMap<String, String> reqHeaders = mHttpUtil.getCommonRequestHeader();
			reqHeaders.put(HttpUtil.ACCEPT_TYPE, HttpUtil.APPLICATION_JSON);
			reqHeaders.put(HttpUtil.CONTENT_TYPE, HttpUtil.FORM_URLENCODED_TYPE);
			final String responseText = mHttpUtil.postDataThroughParams(HttpUtil.POST, requestUri, reqParams, reqHeaders);

			return responseText;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (mAppModel.getResponseCode() == HttpUtil.HTTP_RESPONSE_SUCCESS) {
				// UserId is valid and correct. Now validate password
				validatePasswordAction();
			}else{
				if(result != null){
					enteredUserId.setError(result);
				} else{
					enteredUserId.setError("Unable to validate the user id.");
				}
				validatePasswordAction();
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			if (mHttpUtil != null) {
				mHttpUtil.disconnect();
			}
		}

	}

	/**
	 * Asynctask makes the request to validate the password from cloud.
	 *
	 */
	private class ValidatePasswordAction extends AsyncTask<Void, Void, String> {

		private EditText enteredPassword;
		private RegistrationActivity mParent;

		private ValidatePasswordAction(RegistrationActivity parent, EditText enteredPassword) {
			mParent = parent;
			this.enteredPassword = enteredPassword;
		}

		protected void onPreExecute() {
			super.onPreExecute();
			mHttpUtil = new HttpUtil();
		}

		@Override
		protected String doInBackground(Void... params) {
			final String requestUri = mPinModel.getServerAddress() + Constants.CHECK_PASSWORD;
			final HashMap<String, String> reqParams = new HashMap<String, String>();
			reqParams.put("password", enteredPassword.getText().toString().trim());

			TreeMap<String, String> reqHeaders = mHttpUtil.getCommonRequestHeader();
			reqHeaders.put(HttpUtil.ACCEPT_TYPE, HttpUtil.APPLICATION_JSON);
			reqHeaders.put(HttpUtil.CONTENT_TYPE, HttpUtil.FORM_URLENCODED_TYPE);
			final String responseText = mHttpUtil.postDataThroughParams(HttpUtil.POST, requestUri, reqParams, reqHeaders);

			return responseText;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			mParent.dismissProgressDislog();

			if (mAppModel.getResponseCode() == HttpUtil.HTTP_RESPONSE_SUCCESS) {
				final EditText userIdValue = (EditText) findViewById(R.id.edt_userid);
				// Password is correct. Now go to next screen
				if(userIdValue.getError() == null){
					moveToPersonalDetails();
				}
			}else{
				if(result != null){
					enteredPassword.setError(result);
				} else{
					enteredPassword.setError("Unable to validate password.");
				}
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			if (mHttpUtil != null) {
				mHttpUtil.disconnect();
			}
		}

	}

	@SuppressLint("CutPasteId")
	private void moveToPersonalDetails(){
		final EditText userIdValue = (EditText) findViewById(R.id.edt_userid);
		final EditText pwdValue = (EditText) findViewById(R.id.edt_psw);
		final EditText cnfPwdValue = (EditText) findViewById(R.id.edt_confirmpsw);
		final Spinner userCategoryValue = (Spinner) findViewById(R.id.spr_usercategory);

		mRegModel.setUserId(userIdValue.getText().toString().trim());
		mRegModel.setPassword(pwdValue.getText().toString().trim());
		mRegModel.setConfirmPassword(cnfPwdValue.getText().toString().trim());
		mRegModel.setSelectionPos(userCategoryValue.getSelectedItemPosition());
		userIdValue.setOnFocusChangeListener(null);
		userIdValue.addTextChangedListener(null);
		pwdValue.setOnFocusChangeListener(null);
		pwdValue.addTextChangedListener(null);
		cnfPwdValue.addTextChangedListener(null);
		initialize(STATE_PERSONAL_DETAILS);
	}

	private void validateUserIDAction(){
		final EditText userId = (EditText) findViewById(R.id.edt_userid);
		if(userId != null){
			validateUserIdAction = new ValidateUserIdAction(userId);
			validateUserIdAction.execute();
		}
	}

	private void validatePasswordAction(){
		final EditText passwordField = (EditText) findViewById(R.id.edt_psw);
		if(passwordField != null){
			validatePasswordAction = new ValidatePasswordAction(RegistrationActivity.this, passwordField);
			validatePasswordAction.execute();
		}
	}

}
