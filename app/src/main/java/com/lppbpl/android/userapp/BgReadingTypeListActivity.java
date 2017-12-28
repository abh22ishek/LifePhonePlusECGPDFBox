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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.logging.Level;
import com.logging.Logger;
import com.lpp.xmldata.CaptureScreen;
import com.lppbpl.BgData;
import com.lppbpl.BgReadingType;
import com.lppbpl.Measurement;
import com.lppbpl.Response;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.db.PendingRecordDb;
import com.lppbpl.android.userapp.model.PendingRecord;
import com.lppbpl.android.userapp.model.SfSendModel;
import com.lppbpl.android.userapp.util.Util;

import java.util.Date;

/**
 * This is an Activity to display Blood sugar Measurement value and ask to
 * provide blood sugar reading type taken when fasting, post and random.
 *
 */
public class BgReadingTypeListActivity extends AppBaseActivity implements
		OnClickListener {

	/** The Constant TAG. */
	private static final String TAG = "BgReadingTypeListActivity";

	/** The m view count. */
	final private int mViewCount = 3;

	/** The m sample type. */
	final private TextView[] mSampleType = new TextView[mViewCount];

	/** The m image bg. */
	final private ImageView[] mImageBg = new ImageView[mViewCount];

	/** The m radio bg. */
	final private ImageView[] mRadioBg = new ImageView[mViewCount];

	/** The m show unsaved record. */
	private boolean mShowUnsavedRecord = false;

	/** The m selected id. */
	private int mSelectedId = -1;

	/** The Constant REQUEST_CODE. */
	private static final int REQUEST_CODE = 1;

	/**
	 * Method onCreate.
	 *
	 * @param savedInstanceState
	 *            Bundle
	 */
// To store the data into a file
	private EditText editBSAnotate;
	private TextView timestampTxtView;
	private TextView bgRecordValue;
	LinearLayout menubars;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.bgreadingtype);

		setCustomTitle(R.string.title_bloodsugar);
		setCustomHeaderImage(R.drawable.ic_title_bloodsugar);

		final Button next = (Button) findViewById(R.id.btn_menu_positive);
		next.setText(R.string.next);
		next.setOnClickListener(this);
		final Button delete = (Button) findViewById(R.id.btn_menu_negative);
		delete.setText(R.string.discard);
		delete.setOnClickListener(this);

		timestampTxtView = (TextView) findViewById(R.id.bg_timestamp_txtview);
		editBSAnotate = (EditText) findViewById(R.id.etBSAnotate);

		mShowUnsavedRecord = getIntent().getBooleanExtra(
				Constants.UNSAVED_RECORD, false);

		int resIdRadio = 0;
		int resIdImage = 0;
		int resIDSymp = 0;
		for (int i = 0; i < mViewCount; i++) {
			resIdRadio = getResources().getIdentifier("ivBg_Radio_" + (i + 1),
					"id", getPackageName());
			mRadioBg[i] = (ImageView) this.findViewById(resIdRadio);

			resIdImage = getResources().getIdentifier("ivBg_" + (i + 1), "id",
					getPackageName());
			mImageBg[i] = (ImageView) findViewById(resIdImage);

			resIDSymp = getResources().getIdentifier(
					"tvReadingType_" + (i + 1), "id", getPackageName());
			mSampleType[i] = (TextView) findViewById(resIDSymp);
			mSampleType[i].setOnClickListener(this);
		}
		// set the bg reading type
		final PendingRecord penRecord = mAppModel.getPendingRecord();

//		if (!mShowUnsavedRecord) {
//			mSelectedId = BgReadingType.FASTING;
//		} else {
		if(mShowUnsavedRecord) {
			setSelectedBgReadingType(penRecord.getBgReadingType());
		}

		bgRecordValue = (TextView) findViewById(R.id.tvBSAnotateRecordValue);
		final Response response = mAppModel.getPendingRecord().getResponse();
		final double value = response.getBgData().getBgReading();


		bgRecordValue.setText(Double.toString(value));
		timestampTxtView.setText(Util.formatDateTime((new Date()).getTime()));
		final String userComment = SfSendModel.getInstance()
				.getUserComment();
		if (null != userComment && userComment.length() > 0) {
			editBSAnotate.setText(userComment);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		menubars= (LinearLayout) findViewById(R.id.id_menubars);
		menubars.setVisibility(View.VISIBLE);
	}
	/**
	 * Will set the blood sugar reading type of unsaved records.
	 *
	 * @param bgReadingType
	 *            the new selected bg reading type
	 */
	private void setSelectedBgReadingType(int bgReadingType) {
		if (bgReadingType == BgReadingType.FASTING) {
			mSelectedId = BgReadingType.FASTING;
			mSampleType[0].setBackgroundResource(R.xml.single_listview_style);
			mRadioBg[0].setImageResource(R.drawable.ic_radiobutton_selected);
			mRadioBg[1].setImageResource(R.drawable.ic_radiobutton_notselected);
			mRadioBg[2].setImageResource(R.drawable.ic_radiobutton_notselected);
		} else if (bgReadingType == BgReadingType.POST) {
			mSelectedId = BgReadingType.POST;
			mSampleType[1].setBackgroundResource(R.xml.single_listview_style);
			mRadioBg[1].setImageResource(R.drawable.ic_radiobutton_selected);
			mRadioBg[0].setImageResource(R.drawable.ic_radiobutton_notselected);
			mRadioBg[2].setImageResource(R.drawable.ic_radiobutton_notselected);
		} else if (bgReadingType == BgReadingType.RANDOM) {
			mSelectedId = BgReadingType.RANDOM;
			mSampleType[2].setBackgroundResource(R.xml.single_listview_style);
			mRadioBg[2].setImageResource(R.drawable.ic_radiobutton_selected);
			mRadioBg[0].setImageResource(R.drawable.ic_radiobutton_notselected);
			mRadioBg[1].setImageResource(R.drawable.ic_radiobutton_notselected);
		}
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
		Logger.log(Level.DEBUG, TAG, "onClick" + v.getId());
		int i = v.getId();
		if (i == R.id.tvReadingType_1) {
			setSelectedBgReadingType(BgReadingType.FASTING);

		} else if (i == R.id.tvReadingType_2) {
			setSelectedBgReadingType(BgReadingType.POST);

		} else if (i == R.id.tvReadingType_3) {
			setSelectedBgReadingType(BgReadingType.RANDOM);

		} else if (i == R.id.btn_menu_positive) {
			if (mSelectedId == -1) {
				showAlertDialog(R.drawable.ic_dialog_info,
						get(R.string.title_bloodsugar),
						get(R.string.select_bg_reading_type), get(R.string.OK),
						null, true);
			} else {
				updateBGResponse(mSelectedId);
				// capture the screen for converting to pdf
				View view= findViewById(R.id.iv_scroll).getRootView();
				CaptureScreen.captureScreen(BgReadingTypeListActivity.this, view, menubars, "ScreenBloodGlucose");
				// Add Blood Sugar Glucose data into file
				final Response response = mAppModel.getPendingRecord().getResponse();
				final Intent main = new Intent(this, BgSymtemsActivity.class);
				main.putExtra("BLOODSUGAR",String.valueOf(response.getBgData().getBgReading()));
				main.putExtra("Comments",editBSAnotate.getText().toString().trim());
				main.putExtra("TimeStamp",timestampTxtView.getText().toString().trim());
				if(response.getBgData().getBgReadingType()==1)
				{
					main.putExtra("FASTING_TYPE","Fasting");
				}else if(response.getBgData().getBgReadingType()==2)
				{
					main.putExtra("FASTING_TYPE","Post");
				}else if(response.getBgData().getBgReadingType()==3){
					main.putExtra("FASTING_TYPE","Random");
				}else{
					main.putExtra("FASTING_TYPE","");
				}


				main.putExtra(Constants.UNSAVED_RECORD, mShowUnsavedRecord);
				startActivityForResult(main, REQUEST_CODE);
			}

		} else if (i == R.id.btn_menu_negative) {
			mDialogType = Constants.SHOW_DISCARD_DIALOGE;
			showAlertDialog(R.drawable.ic_dialog_delete,
					get(R.string.discard_record),
					get(R.string.discard_confirmation), get(R.string.discard),
					get(R.string.cancel), false);

		} else {
		}
	}

	/**
	 * Update bg response.
	 *
	 * @param bgReadingType
	 *            the bg reading type
	 */
	private void updateBGResponse(int bgReadingType) {
		final PendingRecord pendingRec = mAppModel.getPendingRecord();
		final Response response = pendingRec.getResponse();
		if (null != response && response.getMeasurementType() == Measurement.BG
				&& response.hasBgData()) {

			final BgData orgBgData = response.getBgData();
			final BgData bgData = BgData.newBuilder()
					.setBgReading(orgBgData.getBgReading())
					.setBgSymptoms(pendingRec.getBgSymptoms())
					.setBgReadingType(bgReadingType).build();

			final Response retResponse = Response.newBuilder()
					.setResponseType(response.getResponseType())
					.setMeasurementType(response.getMeasurementType())
					.setBgData(bgData).build();
			pendingRec.setResponse(retResponse);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.lppbpl.android.userapp.AppBaseActivity#onPositiveButtonClick()
	 */
	@Override
	public void onPositiveButtonClick() {
		super.onPositiveButtonClick();
		if (mShowUnsavedRecord && mDialogType == Constants.SHOW_DISCARD_DIALOGE) {
			final PendingRecord rec = mAppModel.getPendingRecord();
			if (null != rec && rec.getId() != -1) {
				PendingRecordDb.getInstance().deleteRecord(rec.getId());
			}
		}
		finishActivity();
	};

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.lppbpl.android.userapp.AppBaseActivity#onNegativeButtonClick()
	 */
	@Override
	public void onNegativeButtonClick() {
		if (mDialogType != Constants.SHOW_DISCARD_DIALOGE) {
			finishActivity();
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
			finishActivity();
		}
	}

	/**
	 * Close the activity.
	 */
	public void finishActivity() {
		setResult(RESULT_OK);
		finish();
	}

	@Override
	public void onBackPressed() {
		final Button delete = (Button) findViewById(R.id.btn_menu_negative);
		if (null != delete && delete.isShown() && !mShowUnsavedRecord) {
			mDialogType = Constants.SHOW_DISCARD_DIALOGE;
			showAlertDialog(R.drawable.ic_dialog_delete,
					get(R.string.discard_record),
					get(R.string.discard_confirmation), get(R.string.discard),
					get(R.string.cancel), false);
		} else {
			super.onBackPressed();
		}
	}

}
