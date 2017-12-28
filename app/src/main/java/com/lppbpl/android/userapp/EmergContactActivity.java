package com.lppbpl.android.userapp;

import java.util.SortedMap;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

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

public class EmergContactActivity extends AppBaseActivity implements
		OnClickListener {

	HttpUtil mHttpUtil;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.emergency_contact);
		setCustomNoIconTitle(R.string.emergency_contact);

		if (isNetworkConnected()) {
			if (mPinModel.isLoginForSessionSuccess()) {
				new LoadUserProfile().execute();
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

		final Button changeAddress = (Button) findViewById(R.id.btn_menu_positive);
		changeAddress.setText(R.string.save);
		changeAddress.setOnClickListener(this);

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
			new LoadUserProfile().execute();
		} else if (2 == requestCode && resultCode == RESULT_OK) {
			new SaveUserProfile().execute();
		}
	}

	@Override
	protected void onPositiveButtonClick() {
		super.onPositiveButtonClick();
		if (mDialogType == Constants.SHOW_SESSION_EXPIRED_DIALOG) {
			mPinModel.setLoginForSessionSuccess(false);
			final Intent intent = new Intent(this, LoginActivity.class);
			startActivityForResult(intent, 1);
		} else if (mDialogType == Constants.SHOW_SESSION_EXPIRED_DIALOG_1) {
			mPinModel.setLoginForSessionSuccess(false);
			final Intent intent = new Intent(this, LoginActivity.class);
			startActivityForResult(intent, 2);
		} else if (mDialogType == Constants.SHOW_CHANGE_MOBILE_NUMBER_DIALOG) {
			finish();
		}

	}

	public class SaveUserProfile extends AsyncTask<Void, Void, String> {
		private JSONObject object;

		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog(getString(R.string.please_wait_text));
			mHttpUtil = new HttpUtil();

			object = generateJsonMessage();

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

		@Override
		protected String doInBackground(Void... arg0) {

			final SortedMap<String, String> reqHeaders = mHttpUtil
					.getCommonRequestHeader();
			reqHeaders.put(HttpUtil.ACCEPT_TYPE, HttpUtil.APPLICATION_JSON);
			reqHeaders.put(HttpUtil.CONTENT_TYPE, HttpUtil.APPLICATION_JSON);

			final String URL = mPinModel.getServerAddress()
					+ Constants.UPDATE_EMERGENCY_CONTACT;

			final String mStatus = mHttpUtil.postData(HttpUtil.POST, URL,
					object.toString().getBytes(), reqHeaders);

			if (Logger.isDebugEnabled()) {
				Logger.log(Level.DEBUG, TAG, "Updating the Address status: "
						+ mStatus);
			}

			return mStatus;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dismissProgressDislog();
			final EmergContactActivity activity = EmergContactActivity.this;
			if (isAccessTokenExpired()) {
				Logger.log(Level.DEBUG, TAG, "Access token expires.");
				mDialogType = Constants.SHOW_SESSION_EXPIRED_DIALOG_1;
				showAlertDialog(R.drawable.ic_dialog_error,
						activity.get(R.string.information),
						activity.get(R.string.session_expired),
						activity.get(R.string.OK), null, false);
			} else if (mAppModel.getResponseCode() == HttpUtil.HTTP_RESPONSE_SUCCESS) {
				mDialogType = Constants.SHOW_CHANGE_MOBILE_NUMBER_DIALOG;
				showAlertDialog(R.drawable.ic_dialog_success,
						activity.get(R.string.information),
						activity.get(R.string.update_emergency_details_success),
						activity.get(R.string.OK), null, false);

			} else if (result == null || result.trim().isEmpty()
					|| result.equals("-1000")) {
				showAlertDialog(activity.get(R.string.information),
						HttpUtil.getRespnseMsg(mAppModel.getResponseCode()),
						activity.get(R.string.OK), null, true);
			} else if (result != null) {
				showAlertDialog(activity.get(R.string.information), result,
						activity.get(R.string.OK), null, true);
			}
		}

		private JSONObject generateJsonMessage() {
			JSONObject object = new JSONObject();
			final EditText edtName = (EditText) findViewById(R.id.edt_firstname);
			final EditText edtLName = (EditText) findViewById(R.id.edt_lastname);
			final EditText edtRelation = (EditText) findViewById(R.id.edt_relationship);
			final EditText edtCode = (EditText) findViewById(R.id.edt_country_code);
			final EditText edtmobileNo = (EditText) findViewById(R.id.edt_phonenumber);

			final String fName = edtName.getText().toString();
			final String lName = edtLName.getText().toString();
			final String relation = edtRelation.getText().toString();
			final String ccode = edtCode.getText().toString();
			final String mobileNo = edtmobileNo.getText().toString();
			try {
				object.put("ecFirstName", fName);
				object.put("ecLastName", "" + lName);
				object.put("ecRelationship", relation);
				object.put("ecCountryCode", ccode);
				object.put("ecMobileNumber", mobileNo);
			} catch (JSONException e) {
				Logger.log(Level.DEBUG, TAG, "" + e);
			}

			return object;
		}

	}

	private class LoadUserProfile extends AsyncTask<Void, Void, String> {

		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog(getString(R.string.please_wait_text));
			mHttpUtil = new HttpUtil();

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
		 * Method doInBackground.
		 *
		 * @param arg0
		 *            Void[]
		 * @return Void
		 */
		@Override
		protected String doInBackground(Void... v) {
			final String requestUri = mPinModel.getServerAddress()
					+ Constants.GET_USER_PROFILE + mPinModel.getUserID();

			TreeMap<String, String> reqHeaders = mHttpUtil
					.getCommonRequestHeader();
			reqHeaders.put(HttpUtil.ACCEPT_TYPE, HttpUtil.APPLICATION_JSON);
			reqHeaders.put(HttpUtil.CONTENT_TYPE, HttpUtil.APPLICATION_JSON);
			final String responseText = mHttpUtil.getData(HttpUtil.GET,
					requestUri, reqHeaders);
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
			Logger.log(Level.DEBUG, TAG, "Response:" + result);
			EmergContactActivity activity = EmergContactActivity.this;
			Logger.log(Level.DEBUG, TAG, "Response:" + result);
			if (isAccessTokenExpired()) {
				Logger.log(Level.DEBUG, TAG, "Access token expires.");
				mDialogType = Constants.SHOW_SESSION_EXPIRED_DIALOG;
				showAlertDialog(R.drawable.ic_dialog_error,
						activity.get(R.string.information),
						activity.get(R.string.session_expired),
						activity.get(R.string.OK), null, false);
			} else if (null == result) {
				showAlertDialog(activity.get(R.string.information),
						HttpUtil.getRespnseMsg(mAppModel.getResponseCode()),
						activity.get(R.string.OK), null, true);
			} else if (result != null
					&& mAppModel.getResponseCode() == HttpUtil.HTTP_RESPONSE_SUCCESS) {
				parseJsonMessage(result);
			} else {
				showAlertDialog(activity.get(R.string.information), result,
						activity.get(R.string.OK), null, true);
			}

		}

		private void parseJsonMessage(final String message) {
			try {
				final JSONObject object = new JSONObject(message);
				if (object != null) {
					if (object.has("ecFirstName")) {
						final String fname = object.getString("ecFirstName");
						if (fname != null && !fname.isEmpty()) {
							final EditText edtName = (EditText) findViewById(R.id.edt_firstname);
							edtName.setText(fname);
						}
					}
					if (object.has("ecLastName")) {
						final String lname = object.getString("ecLastName");
						if (lname != null && !lname.isEmpty()) {
							final EditText edtLName = (EditText) findViewById(R.id.edt_lastname);
							edtLName.setText(lname);
						}
					}
					if (object.has("ecRelationship")) {
						final String relation = object
								.getString("ecRelationship");
						if (relation != null && !relation.isEmpty()) {
							final EditText edtRelation = (EditText) findViewById(R.id.edt_relationship);
							edtRelation.setText(relation);
						}
					}
					if (object.has("ecCountryCode")) {
						final String ccode = object.getString("ecCountryCode");
						if (ccode != null && !ccode.isEmpty()) {
							final EditText edtCode = (EditText) findViewById(R.id.edt_country_code);
							edtCode.setText(ccode);
						}
					}

					if (object.has("ecMobileNumber")) {
						final String mobileNo = object
								.getString("ecMobileNumber");
						if (mobileNo != null && !mobileNo.isEmpty()) {
							final EditText edtmobileNo = (EditText) findViewById(R.id.edt_phonenumber);
							edtmobileNo.setText(mobileNo);
						}
					}
				}
			} catch (JSONException e) {
				Logger.log(Level.DEBUG, TAG, "JSON error" + e);
			}
		}

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_menu_positive) {
			final EditText edtName = (EditText) findViewById(R.id.edt_firstname);
			final EditText edtLName = (EditText) findViewById(R.id.edt_lastname);
			final EditText edtRelation = (EditText) findViewById(R.id.edt_relationship);
			final EditText edtCode = (EditText) findViewById(R.id.edt_country_code);
			final EditText edtmobileNo = (EditText) findViewById(R.id.edt_phonenumber);

			final String fName = edtName.getText().toString();
			final String lName = edtLName.getText().toString();
			final String relation = edtRelation.getText().toString();
			final String ccode = edtCode.getText().toString();
			final String mobileNo = edtmobileNo.getText().toString();

			if (fName.isEmpty()) {
				showAlertDialog(get(R.string.update_emergency_contact),
						get(R.string.first_name_empty), get(R.string.OK), null,
						true);
			} else if (lName.isEmpty()) {
				showAlertDialog(get(R.string.update_emergency_contact),
						get(R.string.last_name_empty), get(R.string.OK), null,
						true);
			} else if (relation.isEmpty()) {
				showAlertDialog(get(R.string.update_emergency_contact),
						get(R.string.relation_field_empty), get(R.string.OK),
						null, true);
			} else if (ccode.isEmpty()) {
				showAlertDialog(get(R.string.update_emergency_contact),
						get(R.string.enter_country_code), get(R.string.OK),
						null, true);
			} else if (mobileNo.isEmpty()) {
				showAlertDialog(get(R.string.update_emergency_contact),
						get(R.string.enter_new_mobile_no), get(R.string.OK),
						null, true);
			} else {
				if (isNetworkConnected()) {
					if (mPinModel.isLoginForSessionSuccess()) {
						new SaveUserProfile().execute();
					} else {
						final Intent intent = new Intent(this,
								LoginActivity.class);
						startActivityForResult(intent, 2);
					}
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

}
