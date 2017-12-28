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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.android.userapp.R;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.listener.SecondaryUserNotification;
import com.lppbpl.android.userapp.util.HttpUtil;

/**
 * THis activity is show the list of secondary users list.
 */
public class MyMembersList extends MemberBaseActivity implements
		SecondaryUserNotification {

	/** The m list view. */
	private ListView mListView;

	/** The request code login. */
	private final int REQUEST_CODE_LOGIN = 1;

	/**
	 * Method onCreate.
	 *
	 * @param bundle
	 *            Bundle
	 */
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		notify = this;
		setContentView(R.layout.multirow_list);
		setCustomNoIconTitle(R.string.my_members);

		final RelativeLayout parent = (RelativeLayout) findViewById(R.id.layout_single_menubar);
		parent.setVisibility(View.VISIBLE);

		final Button addMember = (Button) findViewById(R.id.btn_menu_positive);
		addMember.setText(R.string.add_members);
		addMember.setVisibility(View.VISIBLE);

		addMember.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// mAppModel.createRegistrationModel();
				// startActivity(new Intent(getApplication(),
				// CreateMemberPage1Activity.class));
				Intent intent = new Intent(getApplication(),
						RegistrationActivity.class);
				intent.putExtra("IS_MEMBER", true);
				startActivity(intent);
			}
		});

		mProgDialog
				.setOnCancelListener(new android.content.DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(android.content.DialogInterface v) {
						if (null != mHttpUtil) {
							mHttpUtil.disconnect();
							mHttpUtil = null;
						}
					}
				});

		mListView = (ListView) findViewById(R.id.lMultiListView);

		if (isNetworkConnected()) {
			if (mPinModel.isLoginForSessionSuccess()) {
				myMembers = new FetchMyMembers();
				myMembers.execute();
			} else {
				final Intent intent = new Intent(this, LoginActivity.class);
				startActivityForResult(intent, REQUEST_CODE_LOGIN);
			}
		} else {
			showAlertDialog(R.drawable.ic_dialog_no_signal,
					get(R.string.network_connection),
					HttpUtil.getRespnseMsg(HttpUtil.HTTP_CONNECTION_DOWN),
					get(R.string.OK), null, true);
		}

	}

	/**
	 * Method parseMyMembersResponse.
	 *
	 * @param response
	 *            String
	 */
	@Override
	public void parseMyMembersResponse(final String response) {
		if (isAccessTokenExpired()) {
			Logger.log(Level.DEBUG, TAG, "Access token expires.");
			mDialogType = Constants.SHOW_SESSION_EXPIRED_DIALOG;
			showAlertDialog(R.drawable.ic_dialog_cloud_upload_state_failed,
					get(R.string.upload_failed), get(R.string.session_expired),
					get(R.string.OK), null, false);
		} else {
			if (null != response
					&& HttpUtil.HTTP_RESPONSE_SUCCESS == mAppModel
							.getResponseCode()) {
				try {
					final List<MyMemBerData> data = new ArrayList<MyMemBerData>();
					final Set<String> mList = new HashSet<String>(128);
					final JSONArray jsonArray = new JSONArray(response);
					MyMemBerData memberData = null;
					JSONObject obj = null;
					String firstName = null;
					String lastName = null;
					String countryCode = null;
					String mobileNo = null;
					String emailId = null;
					Integer profileId = 0;
					for (int i = 0; i < jsonArray.length(); i++) {
						memberData = new MyMemBerData();
						obj = jsonArray.getJSONObject(i);

						if (!obj.isNull("firstName")) {
							firstName = (String) obj.get("firstName");
						}
						if (!obj.isNull("lastName")) {
							lastName = (String) obj.get("lastName");
						}

						if (!obj.isNull("phoneNum_countrycode")) {
							countryCode = (String) obj
									.get("phoneNum_countrycode");
						}
						if (!obj.isNull("phoneNum")) {
							mobileNo = (String) obj.get("phoneNum");
						}
						if (!obj.isNull("emailId")) {
							emailId = (String) obj.get("emailId");
						}
						if (!obj.isNull("id")) {
							profileId = (Integer) obj.get("id");
						}
						memberData.name = firstName + " " + lastName;
						memberData.mobileNo = countryCode + "-" + mobileNo;
						memberData.emailId = emailId;
						memberData.userID = profileId;

						mList.add(memberData.userID + ":" + memberData.name);
						data.add(memberData);
					}

					if (0 == data.size()) {
						final TextView textView = new TextView(this);
						textView.setText("No Members Found");
						textView.setGravity(Gravity.CENTER);
						final RelativeLayout layout = (RelativeLayout) findViewById(R.id.lMultiListLayout);
						layout.addView(textView, new ViewGroup.LayoutParams(
								ViewGroup.LayoutParams.MATCH_PARENT,
								ViewGroup.LayoutParams.MATCH_PARENT));
					} else {
						mAppModel.setMemberList(mList);
						mListView.setAdapter(new MyMemberAdapter(
								getApplication(), data));
					}
				} catch (JSONException e) {
					Logger.log(Level.ERROR, TAG, "" + e);
				}
			} else {
				final TextView textView = new TextView(this);
				textView.setText("Error occured.");
				textView.setGravity(Gravity.CENTER);
				final RelativeLayout layout = (RelativeLayout) findViewById(R.id.lMultiListLayout);
				layout.addView(textView, new ViewGroup.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT));

				final String message = HttpUtil
						.getRespnseMsg(isNetworkConnected() ? mAppModel
								.getResponseCode()
								: HttpUtil.HTTP_CONNECTION_DOWN);
				mDialogType = Constants.SHOW_INFO_DIALOGE;
				showAlertDialog(R.drawable.ic_dialog_error,
						get(R.string.title_authentication), message,
						get(R.string.OK), null, false);
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
			myMembers = new FetchMyMembers();
			myMembers.execute();
		} else {
			setResult(RESULT_OK);
			finish();
		}
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
		if (mDialogType == Constants.SHOW_SESSION_EXPIRED_DIALOG) {
			mPinModel.setLoginForSessionSuccess(false);
			final Intent intent = new Intent(this, LoginActivity.class);
			startActivityForResult(intent, REQUEST_CODE_LOGIN);
		} else if (mDialogType == Constants.SHOW_INFO_DIALOGE) {
			finish();
		}
	}

	/**
	 * The Class MyMemBerData.
	 */
	public class MyMemBerData {

		/** The name. */
		public String name;

		/** The mobile no. */
		public String mobileNo;

		/** The email id. */
		public String emailId;

		/** The user id. */
		public Integer userID;
	}

	/**
	 * The Class MyMemberAdapter.
	 */
	public class MyMemberAdapter extends BaseAdapter {

		/** The m inflater. */
		private final LayoutInflater mInflater;

		/** The data. */
		private final List<MyMemBerData> data;

		/**
		 * Constructor for MyMemberAdapter.
		 *
		 * @param context
		 *            Context
		 * @param name
		 *            List<MyMemBerData>
		 */
		public MyMemberAdapter(Context context, List<MyMemBerData> name) {
			data = name;
			mInflater = LayoutInflater.from(context);
		}

		/**
		 * Method getCount.
		 *
		 * @return int
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			return data.size();
		}

		/**
		 * Method getItem.
		 *
		 * @param position
		 *            int
		 * @return MyMemBerData
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public MyMemBerData getItem(int position) {
			return data.get(position);
		}

		/**
		 * Method getItemId.
		 *
		 * @param position
		 *            int
		 * @return long
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int position) {
			return position;
		}

		/**
		 * Method getView.
		 *
		 * @param position
		 *            int
		 * @param convertView
		 *            View
		 * @param parent
		 *            ViewGroup
		 * @return View
		 * @see android.widget.Adapter#getView(int, View, ViewGroup)
		 */
		@Override
		public View getView(final int position, final View convertView,
				final ViewGroup parent) {
			ViewHolder holder;
			View view = convertView;
			if (null == view) {
				view = mInflater.inflate(R.layout.my_members, null);
				holder = new ViewHolder();
				holder.txt_myName = (TextView) view
						.findViewById(R.id.txt_my_name);
				holder.txt_myMobileNo = (TextView) view
						.findViewById(R.id.txt_my_contact_details);
				holder.txt_myEmailID = (TextView) view
						.findViewById(R.id.txt_my_contact_emailid);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			final MyMemBerData data = getItem(position);
			if (null != data) {
				if (null != data.name) {
					holder.txt_myName.setText(data.name);
				}
				if (null != data.mobileNo) {
					final String text = get(R.string.contact_details) + " : "
							+ data.mobileNo;
					holder.txt_myMobileNo.setText(text);
				}
				if (null != data.emailId) {
					holder.txt_myEmailID.setText(data.emailId);
				}
			}

			return view;
		}

		/**
		 * The Class ViewHolder.
		 */
		protected class ViewHolder {

			/** The txt_my name. */
			protected TextView txt_myName;

			/** The txt_my mobile no. */
			protected TextView txt_myMobileNo;

			/** The txt_my email id. */
			protected TextView txt_myEmailID;
		}
	}

	/**
	 * Method updateSelectedMemberUI.
	 *
	 * @param selectedMember
	 *            String
	 */
	@Override
	public void updateSelectedMemberUI(String selectedMember) {
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
