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
import android.widget.TextView;
import com.lppbpl.android.userapp.R;

// TODO: Auto-generated Javadoc
/**
 * This Activity Display the detail of feedback message got from the doctor.
 *
 */
public class DiagnosisActivity extends AppBaseActivity {

	/**
	 * Method onCreate.
	 *
	 * @param savedInstanceState
	 *            Bundle
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.diagnosislayout);
		setCustomNoIconTitle(R.string.title_diagnosis);

		final Bundle bundle = getIntent().getExtras();
		if (null != bundle) {
			final TextView mUserText = (TextView) findViewById(R.id.userText);
			final String tempStr = bundle.getString("diaMsg");
			mUserText.setText(tempStr);
		}
	}
}
