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
import android.widget.TextView;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.android.userapp.R;
import com.lppbpl.android.userapp.constants.Constants;

// TODO: Auto-generated Javadoc
/**
 * This is a activity to display the records group.
 */
public class RecordsGroupActivty extends AppBaseActivity implements
		OnClickListener {

	/** The records to get. */
	private int recordsToGet;

	/** The Constant TAG. */
	public static final String TAG = RecordsGroupActivty.class.getName();

	/**
	 * Method onCreate.
	 *
	 * @param savedInstanceState
	 *            Bundle
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.records_group);

		recordsToGet = getIntent().getIntExtra("records", 0);

		final TextView latRecRecTxt = (TextView) findViewById(R.id.tvRecView_1);
		latRecRecTxt.setOnClickListener(this);

		final TextView getSpecRecTxt = (TextView) findViewById(R.id.tvRecView_2);
		getSpecRecTxt.setOnClickListener(this);

		setCustomTitle(R.string.title_recrods);
		setCustomHeaderImage(R.drawable.ic_title_records);
		// set the title and text to buttons
		if (recordsToGet == Constants.MY_CASE) {
			setCustomTitle(R.string.title_consulted_cases);
			latRecRecTxt.setText(R.string.title_latest_cases);
			getSpecRecTxt.setText(R.string.title_specific_cases);
		}
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
	}

	/**
	 * Method onClick.
	 *
	 * @param v
	 *            View
	 * @see android.view.View$OnClickListener#onClick(View)
	 */
	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.ivRec_1 || i == R.id.tvRecView_1) {
			if (recordsToGet == Constants.MY_DATA) {
				final Intent intent = new Intent(this, LatestRecordsList.class);
				startActivity(intent);
			} else if (recordsToGet == Constants.MY_CASE) {
				final Intent intent = new Intent(this, MyCasesRecordsList.class);
				intent.putExtra("fetchByDate", false);
				startActivity(intent);
			}

		} else if (i == R.id.ivRec_2 || i == R.id.tvRecView_2) {
			if (recordsToGet == Constants.MY_DATA) {
				final Intent intent = new Intent(this, GetRecords.class);
				startActivity(intent);
			} else if (recordsToGet == Constants.MY_CASE) {
				final Intent intent = new Intent(this, MyCasesRecordsList.class);
				intent.putExtra("fetchByDate", true);
				startActivity(intent);
			}

		} else {
		}
	}

}
