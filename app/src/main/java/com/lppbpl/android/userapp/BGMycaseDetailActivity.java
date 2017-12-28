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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.android.userapp.R;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.model.SfSendModel;
import com.lppbpl.android.userapp.util.Util;

/**
 * This is an Activity to display Consulted Blood Sugar case details.
 *
 */
public class BGMycaseDetailActivity extends NetworkConnBaseActivity {

	/** The Constant TAG. */
	private static final String TAG = BGMycaseDetailActivity.class
			.getSimpleName();

	/** Consultation fees */
	private int consultationFee = 0;

	/** SLA time */
	private int slaTime = 0;

	/** Call center SLA time */
	private int callCenterSlaTime = 0;

	/** Provide advisory is available or not */
	private boolean provideAdvisory = true;

	/**
	 * Method onCreate.
	 *
	 * @param savedInstanceState
	 *            Bundle
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bg_mycase_detail);
		Logger.log(Level.DEBUG, TAG, "onCreate()");

		setCustomTitle(R.string.title_bloodsugar);
		setCustomHeaderImage(R.drawable.ic_title_bloodsugar);

		final TextView textComment = (TextView) findViewById(R.id.comments_txtview);
		final TextView bgReadingValue = (TextView) findViewById(R.id.tvyourbgvalue);
		final TextView bgReadingTimeStamp = (TextView) findViewById(R.id.tvtimestamp);
		final TextView bgSymptoms = (TextView) findViewById(R.id.symptoms_1);
		final TextView bgReadingType = (TextView) findViewById(R.id.bgReading_type);
		final TextView bgCaseDetail = (TextView) findViewById(R.id.bg_case_details);

		final double mValue = mAppModel.getBgReadingValue();
		bgReadingValue.setText(Double.toString(mValue));
		bgReadingTimeStamp.setText(Util.formatDateTime(mAppModel
				.getBgStartTime()));
		bgReadingType.setText("BG Reading Type : "
				+ mAppModel.getBGReadingType());
		bgCaseDetail.setText(mAppModel.getBgCaseDetail());

		final String symptoms = mAppModel.getBGCaseSymptoms();
		if (null != symptoms && !symptoms.isEmpty()) {
			bgSymptoms.setVisibility(View.VISIBLE);
			bgSymptoms.setText("Feeling: " + symptoms);
		}
		final String comment = mAppModel.getComment();
		if (null != comment && comment.length() > 0) {
			textComment.setVisibility(View.VISIBLE);
			textComment.setText("Comment:"
					+ System.getProperty("line.separator") + comment);
		}

		final RelativeLayout menuBar = (RelativeLayout) findViewById(R.id.layout_single_menubar);
		menuBar.setVisibility((0 == mAppModel.getCaseId()) ? View.VISIBLE
				: View.GONE);
		final Button btnCreateCase = (Button) findViewById(R.id.btn_menu_positive);
		btnCreateCase.setText("Create Case");
		btnCreateCase.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				/* Always perform a check if TPA agreement is changed before case creation */
				doTPAVersionCheck();
			}
		});
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
		if (requestCode == Constants.REQUEST_CODE_UPLOAD
				&& resultCode == RESULT_CANCELED) {
			SfSendModel.getInstance().setSendStatus(
					getIntent().getIntExtra("measurementId", 0));
			showPaymentModeDialog(BGMycaseDetailActivity.this);
		} else if (requestCode == Constants.REQUEST_CODE_UPLOAD
				&& resultCode == RESULT_OK) {
			finishActivity();
		} else if (requestCode == Constants.REQUEST_CODE_TPA
				&& resultCode == RESULT_OK) {
			continueCaseConsultation(data);
		} else if (requestCode == Constants.REQUEST_CODE_TPA
				&& resultCode == RESULT_CANCELED) {
			finishActivity();
		}
	}

	/**
	 * Continue with the process of case creation after receiving SLA details
	 * @param data the intent holding the details
	 */
	private void continueCaseConsultation(Intent data){
		consultationFee = data.getIntExtra("consultation_fee", 0);
		slaTime = data.getIntExtra("sla_time", 0);
		provideAdvisory = data.getBooleanExtra("provide_advisory", true);
		callCenterSlaTime = data.getIntExtra("call_center_sla_time", 0);
		showConsultDialogPrompt(provideAdvisory);
	}

	/**
	 * Checks if the TPA agreement is changed or not.
	 */
	private void doTPAVersionCheck(){
		final Intent intent = new Intent(BGMycaseDetailActivity.this, TPAActivity.class);
		intent.putExtra(TPAActivity.KEY_CALL_INFO_API, true);
		intent.putExtra(TPAActivity.KEY_FROM_ACTIVITY, TPAActivity.FROM_CASE_CREATION_ACTIVITY);
		startActivityForResult(intent, Constants.REQUEST_CODE_TPA);
	}

	/**
	 * Shows the doctor consultation dialog.
	 * @param consultationAllowed if true means record saved and user can proceed with doctor consultation,
	 * otherwise false means record only saved to cloud without doctor consultation.
	 */
	private void showConsultDialogPrompt(boolean consultationAllowed){
		if(consultationAllowed){
			mDialogType = Constants.SHOW_CONSULT_DOC_DIALOGE;
			if(callCenterSlaTime > 0){
				showAlertDialog(get(R.string.consult_doctor),
						String.format(get(R.string.bg_consult_prompt_with_cc_sla), consultationFee, slaTime, callCenterSlaTime),
						get(R.string.yes), get(R.string.no),
						false);
			} else{
				showAlertDialog(get(R.string.consult_doctor),
						String.format(get(R.string.bg_consult_prompt), consultationFee, slaTime),
						get(R.string.yes), get(R.string.no),
						false);
			}
		}else{
			mDialogType = Constants.SHOW_RECORD_SAVED_ONLY_DIALOG;
			showAlertDialog(get(R.string.consult_doctor),
					String.format(get(R.string.tpa_consult_feature_disabled), mAppModel.getSupportContactNo()),
					get(R.string.OK), null,
					false);
		}
	}

	@Override
	protected void onPositiveButtonClick() {
		super.onPositiveButtonClick();
		if (mDialogType == Constants.SHOW_CONSULT_DOC_DIALOGE) {
			SfSendModel.getInstance().setSendStatus(
					getIntent().getIntExtra("measurementId", 0));
			showPaymentModeDialog(BGMycaseDetailActivity.this);
		} else if (mDialogType == Constants.SHOW_RECORD_SAVED_ONLY_DIALOG) {
			finishActivity();
		}
	}

	@Override
	protected void onNegativeButtonClick() {
		if (mDialogType == Constants.SHOW_CONSULT_DOC_DIALOGE) {
			finishActivity();
		}
	}
}
