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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.android.userapp.R;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.listener.SecondaryUserNotification;
import com.lppbpl.android.userapp.util.HttpUtil;

// TODO: Auto-generated Javadoc
/**
 * This Base Activity is containing common methods which is common across many
 * classes.
 *
 */
public class MemberBaseActivity extends AppBaseActivity {

	/** The m http util. */
	protected HttpUtil mHttpUtil;

	/** The m bg selected index. */
	protected int mBgSelectedIndex;

	/** The m selected index. */
	protected int mSelectedIndex;

	/** The m selected user id. */
	protected int mSelectedUserId;

	/** The my members. */
	protected FetchMyMembers myMembers;

	protected SecondaryUserNotification notify;

	/**
	 * Create and Display the List of secondary user(s) dialog to select.
	 */
	protected void showMemberListDialog() {
		mDialogType = Constants.SHOW_DIALOGE_NONE;

		final Set<String> memberList = mAppModel.getMemberList();
		final String[] names = new String[memberList.size() + 1];
		final int[] userId = new int[memberList.size() + 1];
		names[0] = "Self Data";
		final Iterator<String> ite = memberList.iterator();
		int i = 1;
		String temp = null;
		String[] split = null;
		while (ite.hasNext()) {
			temp = ite.next();
			split = temp.split(":");
			if (null != split && split.length > 1) {
				try {
					userId[i] = Integer.parseInt(split[0]);
				} catch (NumberFormatException e) {
					userId[i] = 0;
					Logger.log(Level.DEBUG, TAG, "NumberFormatException:" + e);
				}
				names[i] = split[1];
			}
			i++;
		}

		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(get(R.string.my_members));
		builder.setSingleChoiceItems(names, mSelectedIndex,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						Logger.log(Level.DEBUG, TAG, "item:" + item);
						mSelectedIndex = item;
					}
				});

		builder.setPositiveButton(get(R.string.OK),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						notify.updateSelectedMemberUI(names[mSelectedIndex]);
						mSelectedUserId = userId[mSelectedIndex];
					}
				});

		final AlertDialog alert = builder.create();
		alert.setCanceledOnTouchOutside(false);
		alert.show();
	}

	/**
	 * Show the Dialog to choose BG List for create case or BG Graph.
	 */
	protected void showBgRadioDialog() {
		final String[] names = new String[] { "BG List (Create Case)",
				"BG Graph" };

		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(get(R.string.title_bloodsugar));
		builder.setSingleChoiceItems(names, mBgSelectedIndex,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						mBgSelectedIndex = item;
					}
				});

		builder.setPositiveButton(get(R.string.OK),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						notify.notifyBgRecordType();
					}
				});
		builder.setNegativeButton(get(R.string.cancel), null);

		final AlertDialog alert = builder.create();
		alert.setCanceledOnTouchOutside(false);
		alert.show();
	}

	/**
	 * FetchMyMembers is a class class which extends AsyncTask to fetch the list
	 * of secondary users associated to the primary user.
	 *
	 */
	protected class FetchMyMembers extends AsyncTask<Void, Void, String> {

		/*
		 * (non-Javadoc)
		 *
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog(R.string.loading_mymembers);
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

			final SortedMap<String, String> reqHeaders = mHttpUtil
					.getCommonRequestHeader();
			final SortedMap<String, String> reqParams = new TreeMap<String, String>();
			reqHeaders.put(HttpUtil.ACCEPT_TYPE, HttpUtil.APPLICATION_JSON);
			reqHeaders.put(HttpUtil.CONTENT_TYPE, HttpUtil.APPLICATION_JSON);

			reqParams.put("searchparameter", "0");

			final String URL = mPinModel.getServerAddress()
					+ Constants.GET_MEMBERS;

			Logger.log(Level.DEBUG, TAG, "My Member URL: " + URL);

			final String status = mHttpUtil.postDataThroughParams(
					HttpUtil.POST, URL, reqParams, reqHeaders);

			if (Logger.isDebugEnabled()) {
				Logger.log(Level.DEBUG, TAG, "My Member status: " + status);
			}

			return status;
		}

		/**
		 * Method onPostExecute.
		 *
		 * @param status
		 *            String
		 */
		@Override
		protected void onPostExecute(final String status) {
			super.onPostExecute(status);
			dismissProgressDislog();
			notify.parseMyMembersResponse(status);
		}
	}

	/**
	 * Method parseMemberResponse.
	 *
	 * @param response
	 *            String
	 */
	protected void parseMemberResponse(String response) {
		/*
		 * WHen access token expires, so show the login screen and do the login
		 * then start uploading / retrive the data.
		 */
		if (isAccessTokenExpired()) {
			Logger.log(Level.DEBUG, TAG, "Access token expires.");
			mDialogType = Constants.SHOW_SESSION_EXPIRED_DIALOG_1;
			showAlertDialog(R.drawable.ic_dialog_info,
					get(R.string.information), get(R.string.session_expired),
					get(R.string.OK), null, false);
		} else {

			if (null != response
					&& HttpUtil.HTTP_RESPONSE_SUCCESS == mAppModel
							.getResponseCode()) {
				try {
					final Set<String> mList = new HashSet<String>(128);
					final JSONArray jsonArray = new JSONArray(response);
					JSONObject obj = null;
					String firstName = null;
					String lastName = null;
					Integer userId = 0;
					for (int i = 0; i < jsonArray.length(); i++) {
						obj = jsonArray.getJSONObject(i);
						if (!obj.isNull("firstName")) {
							firstName = (String) obj.get("firstName");
						}
						if (!obj.isNull("lastName")) {
							lastName = (String) obj.get("lastName");
						}

						if (!obj.isNull("id")) {
							userId = (Integer) obj.get("id");
						}
						mList.add(userId + ":" + firstName + " " + lastName);
					}

					mAppModel.setMemberList(mList);
					showMemberListDialog();
				} catch (JSONException e) {
					Logger.log(Level.ERROR, TAG, "" + e);
				}
			} else {

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
}
