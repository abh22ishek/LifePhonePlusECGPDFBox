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

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.MultipleResponse;
import com.lppbpl.android.userapp.R;

// TODO: Auto-generated Javadoc
/**
 * This is an Activity to display Blood Sugar Graph.
 *
 */
public class BgGraphActivity extends AppBaseActivity {

	/** The Constant TAG. */
	private final static String TAG = ActGraphActivity.class.getName();

	/**
	 * Method onCreate.
	 *
	 * @param savedInstanceState
	 *            Bundle
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.log(Level.DEBUG, TAG, "onCreate()");
		setContentView(R.layout.bg_graph);
		setCustomTitle(R.string.title_bloodsugar);

		final BgGraphComponent component = (BgGraphComponent) findViewById(R.id.bg_graph);

		final MultipleResponse mulRes = mAppModel.getGetDataResponse();
		if (null != mulRes) {
			component.setData(mulRes.getBgData(), mulRes.getTimestamp());
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.lppbpl.android.userapp.AppBaseActivity#onDestroy()
	 */
	protected void onDestroy() {
		super.onDestroy();
		removeDrawables(findViewById(R.id.bg_graph));
		// System.gc();
	}

	/**
	 * Remove the view from view group.
	 *
	 * @param view
	 *            the view
	 */
	private void removeDrawables(View view) {
		if (null != view.getBackground()) {
			view.getBackground().setCallback(null);
		}
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				removeDrawables(((ViewGroup) view).getChildAt(i));
			}
			((ViewGroup) view).removeAllViews();
		}
	}
}
