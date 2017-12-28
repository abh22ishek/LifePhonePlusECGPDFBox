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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.android.userapp.R;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.db.PendingRecordDb;

// TODO: Auto-generated Javadoc
/**
 * This is a activity to display the records List.
 */
public class RecordsListActivity extends AppBaseActivity implements
		OnClickListener {

	/** The m rec no. */
	private int mRecNo = 0;

	/** The m my data txt. */
	private TextView mMyDataTxt = null;

	/** The m un saved rec txt. */
	private TextView mUnSavedRecTxt = null;

	/** The Constant TAG. */
	public static final String TAG = RecordsListActivity.class.getName();

	/**
	 * Method onCreate.
	 *
	 * @param savedInstanceState
	 *            Bundle
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.singlerow_list);

		mUnSavedRecTxt = (TextView) findViewById(R.id.tvRecView_3);

		mMyDataTxt = (TextView) findViewById(R.id.tvRecView_1);
		mMyDataTxt.setOnClickListener(this);

		final TextView myCasesTxt = (TextView) findViewById(R.id.tvRecView_2);
		myCasesTxt.setOnClickListener(this);

		setCustomTitle(R.string.title_recrods);
		setCustomHeaderImage(R.drawable.ic_title_records);

		updateUnsavedRecordItem();
	}

	/**
	 * Updates the unsaved records.
	 */
	private void updateUnsavedRecordItem() {
		mRecNo = PendingRecordDb.getInstance().numOfRecords();
		final boolean recAvailable = mRecNo > 0;
		mUnSavedRecTxt.setOnClickListener(recAvailable ? this : null);
		final ImageView mUnSavedRec = (ImageView) findViewById(R.id.ivRec_3);
		mUnSavedRec.setClickable(recAvailable);
		mUnSavedRec.setEnabled(recAvailable);
		mUnSavedRec
				.setBackgroundResource(recAvailable ? R.drawable.ic_listitem_not_selected
						: R.drawable.ic_listitem_disabled);
		mUnSavedRecTxt.setTextColor(recAvailable ? mMyDataTxt
				.getCurrentTextColor() : getResources().getColor(
				android.R.color.darker_gray));
		mUnSavedRecTxt
				.setText(recAvailable ? get(R.string.title_unsaved_records)
						+ ((mRecNo > 0) ? " (" + mRecNo + ")" : "")
						: get(R.string.title_unsaved_records));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		Logger.log(Level.DEBUG, TAG, "onResume()");
		updateUnsavedRecordItem();
	}

	/**
	 * Method onClick.
	 *
	 * @param v
	 *            View
	 * @see android.view.View$OnClickListener#onClick(View)
	 */
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.ivRec_1 || i == R.id.tvRecView_1) {
			callRecordsGroup(Constants.MY_DATA);

		} else if (i == R.id.ivRec_2 || i == R.id.tvRecView_2) {
			callRecordsGroup(Constants.MY_CASE);

		} else if (i == R.id.ivRec_3 || i == R.id.tvRecView_3) {
			mUnSavedRecTxt.setBackgroundResource(R.xml.single_listview_style);
			if (mRecNo > 0) {
				final Intent intent = new Intent(this, UnsavedRecordsList.class);
				startActivity(intent);
			}

		} else {
		}
	}

	/**
	 * Method callRecordsGroup.
	 *
	 * @param record
	 *            int
	 */
	private void callRecordsGroup(int record) {
		final Intent intent = new Intent(this, RecordsGroupActivty.class);
		intent.putExtra("records", record);
		startActivity(intent);
	}
}
