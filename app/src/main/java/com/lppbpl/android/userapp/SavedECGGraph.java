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

import java.util.Vector;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.EcgLead;
import com.lppbpl.HrData;
import com.lppbpl.Response;
import com.lppbpl.EcgData.EcgSymptoms;
import com.lppbpl.android.userapp.R;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.model.PendingRecord;
import com.lppbpl.android.userapp.model.SfSendModel;
import com.lppbpl.android.userapp.util.Util;

// TODO: Auto-generated Javadoc
/**
 * This Activity is to display the Ecg detail information and Graph.
 *
 */
public class SavedECGGraph extends NetworkConnBaseActivity {

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
	@SuppressWarnings("rawtypes")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.saved_ecg_graph);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		final boolean myCases = getIntent().getBooleanExtra("my_case", false);
		setCustomTitle(R.string.title_ecg);
		setCustomHeaderImage(R.drawable.ic_title_ecg);

		final PendingRecord penRecord = mAppModel.getPendingRecord();

		Logger.log(Level.DEBUG, TAG, "penRecord = " + penRecord);
		if (penRecord == null) {
			Logger.log(Level.ERROR, TAG, "penRecord is null");
			return;
		}

		final TextView mHrDataView = (TextView) findViewById(R.id.tvHrRate);

		final RelativeLayout layout = (RelativeLayout) findViewById(R.id.tvHrdataLayout);
		// layout.setVisibility(penRecord.getHeartRate() > 0 ? View.VISIBLE :
		// View.GONE);
		try {
			final Response lastResponse = penRecord.getLastEcgLeadResponse();
			if (null != lastResponse) {
				final HrData hrData = lastResponse.getHrData();
				if (null != hrData) {
					mHrDataView
							.setText(Integer.toString(hrData.getHeartRate()));
				}
			}
		} catch (NumberFormatException e) {
			layout.setVisibility(View.GONE);
			Logger.log(Level.DEBUG, TAG, "Error occured : " + e.getMessage());
		}

		final TextView mEcgTimeStamp = (TextView) findViewById(R.id.tvecgTimestamp);
		mEcgTimeStamp.setText(Util.formatDateTime(penRecord
				.getMeasurementTime()));

		if (myCases) {
			final TextView caseDetail = (TextView) findViewById(R.id.ecg_detail_info);
			caseDetail.setVisibility(View.VISIBLE);
			caseDetail.setText(mAppModel.getEcgCaseDetail());
		}
		final TextView mCommentTxtView = (TextView) findViewById(R.id.comments_txtview);

		final Response lastEcgRes = penRecord.getLastEcgLeadResponse();
		if (null != lastEcgRes) {
			final String comment = lastEcgRes.getEcgData().getAnnotationTxt();
			if (null != comment && comment.length() > 0) {
				mCommentTxtView.setVisibility(View.VISIBLE);
				SfSendModel.getInstance().setUserComment(comment);
				mCommentTxtView.setText("Comment:"
						+ System.getProperty("line.separator") + comment);
			} else {
				mCommentTxtView.setVisibility(View.GONE);
			}
		}

		final EcgGraph mEcgComp = (EcgGraph) findViewById(R.id.graph);

		final Vector ecgLeads = penRecord.getEcgLeads();
		mEcgComp.setLeads(ecgLeads);
		final Vector<String> ecgLeadsLabel = penRecord.getEcgLeadLabels();

		// To Display lead1, lead2, lead3 as ROMAN letter I, II III
		for (int i = 0; i < ecgLeadsLabel.size(); i++) {
			if (ecgLeadsLabel.elementAt(i).equals(
					EcgLead.getStringValue(EcgLead.LEAD1))) {
				ecgLeadsLabel.setElementAt("I", i);
			} else if (ecgLeadsLabel.elementAt(i).equals(
					EcgLead.getStringValue(EcgLead.LEAD2))) {
				ecgLeadsLabel.setElementAt("II", i);
			} else if (ecgLeadsLabel.elementAt(i).equals(
					EcgLead.getStringValue(EcgLead.LEAD3))) {
				ecgLeadsLabel.setElementAt("III", i);
			} else if (ecgLeadsLabel.elementAt(i).equals(
					EcgLead.getStringValue(EcgLead.AVF))) {
				ecgLeadsLabel.setElementAt("aVF", i);
			} else if (ecgLeadsLabel.elementAt(i).equals(
					EcgLead.getStringValue(EcgLead.AVL))) {
				ecgLeadsLabel.setElementAt("aVL", i);
			} else if (ecgLeadsLabel.elementAt(i).equals(
					EcgLead.getStringValue(EcgLead.AVR))) {
				ecgLeadsLabel.setElementAt("aVR", i);
			}
		}
		mEcgComp.setLeadLabels(ecgLeadsLabel);

		@SuppressWarnings("unchecked")
		final Vector<Integer> vector = penRecord.getEcgSymptoms();
		Logger.log(Level.DEBUG, "SavedECGGraph", "v size=" + vector.size());

		final TextView mSymptoms = (TextView) findViewById(R.id.symptoms_1);

		if (0 == vector.size()) {
			mSymptoms.setVisibility(View.GONE);
		} else {
			final StringBuilder temp = new StringBuilder();
			temp.append("Feeling ");
			int val = 0;
			String updatedSymptom = null;
			for (int i = 0; i < vector.size(); i++) {
				val = vector.elementAt(i);
				updatedSymptom = EcgSymptoms.getStringValue(val);
				Logger.log(Level.DEBUG, "SavedECGGraph", "val =" + val);
				if (null != updatedSymptom) {
					temp.append(updatedSymptom);
				}

				if (i + 1 < vector.size()) {
					temp.append(", ");
				}
			}
			mSymptoms.setVisibility(View.VISIBLE);
			mSymptoms.setText(temp);
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

	/*
	 * (non-Javadoc)
	 *
	 * @see com.lppbpl.android.userapp.AppBaseActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();

		unbindDrawables(findViewById(R.id.graph));
		// System.gc();
	}

	/**
	 * Method unbindDrawables.
	 *
	 * @param view
	 *            View
	 */
	private void unbindDrawables(View view) {
		if (null != view.getBackground()) {
			view.getBackground().setCallback(null);
		}
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}
			((ViewGroup) view).removeAllViews();
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
		if (requestCode == Constants.REQUEST_CODE_UPLOAD
				&& resultCode == RESULT_CANCELED) {
			SfSendModel.getInstance().setSendStatus(
					getIntent().getIntExtra("measurementId", 0));
			showPaymentModeDialog(SavedECGGraph.this);
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
		final Intent intent = new Intent(SavedECGGraph.this, TPAActivity.class);
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
			showPaymentModeDialog(SavedECGGraph.this);
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
