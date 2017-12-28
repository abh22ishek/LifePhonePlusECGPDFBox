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
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.android.userapp.R;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.util.HttpUtil;
import com.lppbpl.android.userapp.util.Util;

public class UpdateAddressActivity extends AppBaseActivity implements
		OnClickListener {

	HttpUtil mHttpUtil;

	final ArrayList<Address> countryList = new ArrayList<Address>();
	final ArrayList<Address> stateList = new ArrayList<Address>();
	final ArrayList<Address> cityList = new ArrayList<Address>();

	private final static int COUNRTY = 1;
	private final static int STATE = 2;
	private final static int CITY = 3;

	private int countryIndex;
	private int stateIndex;
	private int cityIndex;

	EditText address1View;
	EditText address2View;
	EditText address3View;
	EditText emailView;
	EditText zipCodeView;

	TextView countryView;
	TextView stateView;
	TextView cityView;

	ImageButton country;
	ImageButton state;
	ImageButton city;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.update_address);
		setCustomNoIconTitle(R.string.update_address);

		setResult(RESULT_CANCELED);

		final Button changeAddress = (Button) findViewById(R.id.btn_menu_positive);
		changeAddress.setText(R.string.save);
		changeAddress.setOnClickListener(this);

		address1View = (EditText) findViewById(R.id.edt_address_1);
		address2View = (EditText) findViewById(R.id.edt_address_2);
		address3View = (EditText) findViewById(R.id.edt_address_3);

		countryView = (TextView) findViewById(R.id.edt_country);
		country = (ImageButton) findViewById(R.id.btn_country);

		stateView = (TextView) findViewById(R.id.edt_state);
		state = (ImageButton) findViewById(R.id.btn_state);

		cityView = (TextView) findViewById(R.id.edt_city);
		city = (ImageButton) findViewById(R.id.btn_city);

		zipCodeView = (EditText) findViewById(R.id.edt_zipcode);

		emailView = (EditText) findViewById(R.id.edt_email);

		country.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isNetworkConnected()) {
					if (mPinModel.isLoginForSessionSuccess()) {
						new LoadAddress(COUNRTY, countryView).execute();
					} else {
						final Intent intent = new Intent(getApplication(),
								LoginActivity.class);
						startActivityForResult(intent, 1);
					}
				} else {
					showAlertDialog(
							R.drawable.ic_dialog_no_signal,
							get(R.string.network_connection),
							HttpUtil.getRespnseMsg(HttpUtil.HTTP_CONNECTION_DOWN),
							get(R.string.OK), null, true);
				}
			}
		});

		state.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new LoadAddress(STATE, stateView).execute();
			}
		});

		city.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new LoadAddress(CITY, cityView).execute();
			}
		});

		if (isNetworkConnected()) {
			if (mPinModel.isLoginForSessionSuccess()) {
				new LoadUserProfile().execute();
			} else {
				final Intent intent = new Intent(this, LoginActivity.class);
				startActivityForResult(intent, 2);
			}
		} else {
			showAlertDialog(R.drawable.ic_dialog_no_signal,
					get(R.string.network_connection),
					HttpUtil.getRespnseMsg(HttpUtil.HTTP_CONNECTION_DOWN),
					get(R.string.OK), null, true);
		}

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_menu_positive) {
			if (address1View.getText().toString().isEmpty()) {
				showAlertDialog(get(R.string.update_address),
						get(R.string.address1_field_empty), get(R.string.OK), null,
						true);
			} else if (countryView.getText().toString().isEmpty()) {
				showAlertDialog(get(R.string.update_address),
						get(R.string.country_field_empty), get(R.string.OK), null,
						true);
			} else if (stateView.getText().toString().isEmpty()) {
				showAlertDialog(get(R.string.update_address),
						get(R.string.state_field_empty), get(R.string.OK), null, true);
			} else if (cityView.getText().toString().isEmpty()) {
				showAlertDialog(get(R.string.update_address),
						get(R.string.city_field_empty), get(R.string.OK), null, true);
			} else if (zipCodeView.getText().toString().isEmpty()) {
				showAlertDialog(get(R.string.update_address),
						get(R.string.zipcode_field_empty), get(R.string.OK), null,
						true);
			} else if (!Util.isValidEmail(emailView.getText())) {
				showAlertDialog(get(R.string.update_address),
						get(R.string.emailid_invalid), get(R.string.OK), null, true);
			} else {
				new SaveUserProfile().execute();
			}
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
					+ Constants.UPDATE_CONTACT_ADDRESS;

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
			final UpdateAddressActivity activity = UpdateAddressActivity.this;
			if (mAppModel.getResponseCode() == HttpUtil.HTTP_RESPONSE_SUCCESS) {
				mDialogType = Constants.SHOW_INFO_DIALOGE;
				showAlertDialog(R.drawable.ic_dialog_success,
						activity.get(R.string.information),
						activity.get(R.string.update_address_success),
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
			final String address1 = address1View.getText().toString();
			final String address2 = address2View.getText().toString();
			final String address3 = address3View.getText().toString();
			final String zipCode = zipCodeView.getText().toString();
			final String email = emailView.getText().toString();
			try {
				object.put("address1", address1);
				object.put("address2", address2);
				object.put("address3", address3);
				object.put("country", "" + countryIndex);
				object.put("state", "" + stateIndex);
				object.put("city", "" + cityIndex);
				object.put("zipCode", zipCode);
				object.put("email", email);
			} catch (JSONException e) {
				Logger.log(Level.DEBUG, TAG, "" + e);
			}

			return object;
		}

	}

	public class LoadUserProfile extends AsyncTask<Void, Void, String> {
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
			UpdateAddressActivity activity = UpdateAddressActivity.this;
			Logger.log(Level.DEBUG, TAG, "Response:" + result);
			if (isAccessTokenExpired()) {
				Logger.log(Level.DEBUG, TAG, "Access token expires.");
				mDialogType = Constants.SHOW_SESSION_EXPIRED_DIALOG_1;
				showAlertDialog(R.drawable.ic_dialog_error,
						activity.get(R.string.information),
						activity.get(R.string.session_expired),
						activity.get(R.string.OK), null, false);
				return;
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
					if (object.has("address1")) {
						final String address1 = object.getString("address1");
						if (address1 != null && !address1.isEmpty()) {
							address1View.setText(address1);
						}
					}
					if (object.has("address2")) {
						final String address2 = object.getString("address2");
						if (address2 != null && !address2.isEmpty()) {
							address2View.setText(address2);
						}
					}
					if (object.has("address3")) {
						final String address3 = object.getString("address3");
						if (address3 != null && !address3.isEmpty()) {
							address3View.setText(address3);
						}
					}
					if (object.has("countryName")) {
						final String country = object.getString("countryName");
						if (country != null && !country.isEmpty()) {
							countryView.setText(country);
						}
					}
					if (object.has("stateName")) {
						final String state = object.getString("stateName");
						if (state != null && !state.isEmpty()) {
							stateView.setText(state);
						}
					}
					if (object.has("cityName")) {
						final String city = object.getString("cityName");
						if (city != null && !city.isEmpty()) {
							cityView.setText(city);
						}
					}
					if (object.has("zipCode")) {
						final String zipCode = object.getString("zipCode");
						if (zipCode != null && !zipCode.isEmpty()) {
							zipCodeView.setText(zipCode);
						}
					}
					if (object.has("email")) {
						final String email = object.getString("email");
						if (email != null && !email.isEmpty()) {
							emailView.setText(email);
						}
					}
					if (object.has("country")) {
						final String country = object.getString("country");
						if (country != null && !country.isEmpty()) {
							try {
								countryIndex = Integer.parseInt(country);
							} catch (NumberFormatException e) {
								Logger.log(Level.DEBUG, TAG,
										"" + e.getMessage());
							}
							UpdateAddressActivity.this.state
									.setVisibility(View.VISIBLE);

						}
					}
					if (object.has("state")) {
						final String state = object.getString("state");
						if (state != null && !state.isEmpty()) {
							try {
								stateIndex = Integer.parseInt(state);
							} catch (NumberFormatException e) {
								Logger.log(Level.DEBUG, TAG,
										"" + e.getMessage());
							}
							UpdateAddressActivity.this.city
									.setVisibility(View.VISIBLE);
						}
					}
					if (object.has("city")) {
						final String city = object.getString("state");
						if (city != null && !city.isEmpty()) {
							try {
								cityIndex = Integer.parseInt(city);
							} catch (NumberFormatException e) {
								Logger.log(Level.DEBUG, TAG,
										"" + e.getMessage());
							}
						}
					}

				}
			} catch (JSONException e) {
				Logger.log(Level.DEBUG, TAG, "JSON error" + e);
			}
		}
	}

	public class LoadAddress extends AsyncTask<String, Void, String> {
		private int type;
		private TextView view;
		private final String URL;

		public LoadAddress(int type, TextView view) {
			this.type = type;
			this.view = view;
			if (type == COUNRTY) {
				URL = "/rest/ajax/country";
			} else if (type == STATE) {
				URL = "/rest/ajax/country/" + countryIndex + "/state";
			} else if (type == CITY) {
				URL = "/rest/ajax/country/" + countryIndex + "/state/"
						+ stateIndex + "/city";
			} else {
				URL = "";
			}
		}

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
		protected String doInBackground(String... v) {
			final String requestUri = mPinModel.getServerAddress() + URL;

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
			final UpdateAddressActivity activity = UpdateAddressActivity.this;
			Logger.log(Level.DEBUG, TAG, "Response:" + result);
			if (isAccessTokenExpired()) {
				Logger.log(Level.DEBUG, TAG, "Access token expires.");
				mDialogType = Constants.SHOW_SESSION_EXPIRED_DIALOG;
				showAlertDialog(R.drawable.ic_dialog_error,
						activity.get(R.string.information),
						activity.get(R.string.session_expired),
						activity.get(R.string.OK), null, false);
				return;
			} else if (result == null) {
				showAlertDialog(activity.get(R.string.information),
						HttpUtil.getRespnseMsg(mAppModel.getResponseCode()),
						activity.get(R.string.OK), null, true);
			} else if (result != null
					&& mAppModel.getResponseCode() == HttpUtil.HTTP_RESPONSE_SUCCESS) {
				try {
					if (type == COUNRTY) {
						countryList.clear();
					} else if (type == STATE) {
						stateList.clear();
					} else if (type == CITY) {
						cityList.clear();
					}
					JSONArray array = new JSONArray(result);
					if (array != null && array.length() > 0) {
						for (int i = 0; i < array.length(); i++) {
							JSONObject object = array.getJSONObject(i);
							Address address = new Address(type);
							if (object.has("id")) {
								address.setId(object.getInt("id"));
							}
							if (object.has("name")) {
								address.setName(object.getString("name"));
							}
							if (type == COUNRTY) {
								countryList.add(address);
							} else if (type == STATE) {
								stateList.add(address);
							} else if (type == CITY) {
								cityList.add(address);
							}

						}
						if (type == COUNRTY) {
							showListDialog("Choose Country", countryList);
						} else if (type == STATE) {
							showListDialog("Choose State", stateList);
						} else if (type == CITY) {
							showListDialog("Choose City", cityList);
						}
					}

				} catch (JSONException e) {
					Logger.log(Level.ERROR, TAG, "" + e);
				}
			} else {
				showAlertDialog(activity.get(R.string.information), result,
						activity.get(R.string.OK), null, true);
			}
		}

		private void showListDialog(final String title,
				final ArrayList<Address> menuList) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(
					UpdateAddressActivity.this);
			if (title != null) {
				builder.setTitle(title);
			}

			final CharSequence[] menu = new CharSequence[menuList.size()];
			final Iterator<Address> itr = menuList.iterator();
			int i = 0;
			String temp;
			while (itr.hasNext()) {
				temp = itr.next().getName();
				if (temp != null) {
					menu[i++] = temp;
				}
			}

			if (type == COUNRTY) {
				countryIndex = menuList.get(0).getId();
			} else if (type == STATE) {
				stateIndex = menuList.get(0).getId();
			} else if (type == CITY) {
				cityIndex = menuList.get(0).getId();
			}

			view.setText(menuList.get(0).getName());

			builder.setSingleChoiceItems(menu, 0,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							Logger.log(Level.DEBUG, TAG, "item:" + item);
							if (type == COUNRTY) {
								countryIndex = menuList.get(item).getId();
							} else if (type == STATE) {
								stateIndex = menuList.get(item).getId();
							} else if (type == CITY) {
								cityIndex = menuList.get(item).getId();
							}
							view.setText(menu[item]);
						}
					});

			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if (view.getId() == R.id.edt_country) {
								stateView.setText("");
								cityView.setText("");
								state.setVisibility(View.VISIBLE);
							} else if (view.getId() == R.id.edt_state) {
								cityView.setText("");
								city.setVisibility(View.VISIBLE);
							}

						}
					});

			builder.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface arg0) {
				}
			});
			final AlertDialog alertDialog = builder.create();
			alertDialog.setCanceledOnTouchOutside(false);
			alertDialog.show();
		}

	}

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
		} else if (mDialogType == Constants.SHOW_INFO_DIALOGE) {
			setResult(RESULT_OK);
			finish();
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
			new LoadAddress(COUNRTY, countryView).execute();
		} else if (2 == requestCode && resultCode == RESULT_OK) {
			new LoadUserProfile().execute();
		}
	}

	public class Address {
		private int type;
		private int id;
		private String name;

		public Address(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

}