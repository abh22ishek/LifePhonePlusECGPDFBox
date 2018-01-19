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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.EcgLead;
import com.lppbpl.EcgData.EcgSymptoms;
import com.lppbpl.android.userapp.R;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.db.PendingRecordDb;
import com.lppbpl.android.userapp.model.PendingRecord;
import com.lppbpl.android.userapp.model.SfSendModel;
import com.lppbpl.android.userapp.util.Util;

/**
 * This Activity is to display the Ecg Details- Ecg value, Ecg graph and provide
 * textbox for user comments.
 *
 */
public class EcgGraphActivity extends AppBaseActivity implements
		OnClickListener {

	/** The Constant TAG. */
	private final static String TAG = EcgGraphActivity.class.getName();

	/** The m edit txt. */
	private   EditText mEditTxt;

	/** The m comment. */
	private TextView mComment;

	/** The m savefrom edt. */
	private TextView mSavefromEdt;

	/** The m image view. */
	private ImageView mImageView;

	/** The m delete. */
	private Button mDelete;

	/** The m edit. */
	private Button mEdit;

	/** The m show unsaved record. */
	private boolean mShowUnsavedRecord = false;

	/** The Constant REQUEST_CODE. */
	private static final int REQUEST_CODE = 1;

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

		Logger.log(Level.DEBUG, TAG, "OnCreate started");
		setContentView(R.layout.ecggraph);

		setCustomTitle(R.string.title_ecg);
		setCustomHeaderImage(R.drawable.ic_title_ecg);

		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

		final EcgGraph ecgComp = (EcgGraph) findViewById(R.id.graph);

		final PendingRecord penRecord = mAppModel.getPendingRecord();

		mEditTxt = (EditText) findViewById(R.id.comments_txtview);

		// set user comment
		final String userComment = penRecord.getComment();
		if (null != userComment && userComment.length() > 0) {
			mEditTxt.setText(userComment);
		}

		mComment = (TextView) findViewById(R.id.tvECGComment);
		mEdit = (Button) findViewById(R.id.bECGGraphEdit);
		mSavefromEdt = (TextView) findViewById(R.id.tvECGGraphSavefrmET);
		mImageView = (ImageView) findViewById(R.id.ivECGGraphComment);

		mShowUnsavedRecord = getIntent().getBooleanExtra(
				Constants.UNSAVED_RECORD, false);

		mDelete = (Button) findViewById(R.id.btn_menu_negative);
		mDelete.setText(R.string.discard);
		final Button mNext = (Button) findViewById(R.id.btn_menu_positive);
		mNext.setText(R.string.next);

		mDelete.setOnClickListener(this);
		mNext.setOnClickListener(this);
		mEdit.setOnClickListener(this);

		final TextView hrdataView = (TextView) findViewById(R.id.tvHrRate);
		final TextView ecgTimestamp = (TextView) findViewById(R.id.tvecgTimestamp);

		ecgTimestamp.setText(Util.formatDateTime(penRecord.getMeasurementTime()));

		final RelativeLayout layout = (RelativeLayout) findViewById(R.id.tvHrdataLayout);
		// layout.setVisibility(penRecord.getHeartRate() > 0 ? View.VISIBLE :
		// View.GONE);
		try {
			hrdataView.setText(Integer.toString(penRecord.getHeartRate()));
		} catch (NumberFormatException e) {
			Logger.log(Level.DEBUG, TAG, e.getMessage());
			layout.setVisibility(View.GONE);
		}

		final Vector ecgLeads = penRecord.getEcgLeads();
		Logger.log(Level.INFO,TAG,"***ecgleads size***="+ecgLeads.size()+ecgLeads);
		ecgComp.setLeads(ecgLeads);
		final Vector<String> ecgLeadsLabel = penRecord.getEcgLeadLabels();

		Logger.log(Level.INFO,TAG,"*****ecgleads label size*****="+ecgLeadsLabel.size()+ecgLeadsLabel);
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
		ecgComp.setLeadLabels(ecgLeadsLabel);

		Logger.log(Level.DEBUG, TAG, "OnCreate ended");
		final boolean hideSoftKey = getIntent().getBooleanExtra(
				"disable_menubar", false);
		if (hideSoftKey) {
			mNext.setVisibility(View.INVISIBLE);
			mDelete.setVisibility(View.INVISIBLE);
			final ImageView imgView = (ImageView) findViewById(R.id.imageView5);
			imgView.setVisibility(View.INVISIBLE);

			final String comment = penRecord.getComment();
			if (null != comment && comment.length() > 0) {
				SfSendModel.getInstance().setUserComment(comment);
				mEditTxt.setText(comment);
			}
		}
		@SuppressWarnings("unchecked")
		// set Ecg sysmptoms
		final Vector<Integer> v = penRecord.getEcgSymptoms();
		Logger.log(Level.DEBUG, TAG, "v size=" + v.size());

		final TextView symptoms = (TextView) findViewById(R.id.symptoms_1);

		if (0 == v.size()) {
			symptoms.setVisibility(View.GONE);
		} else {
			final StringBuilder temp = new StringBuilder();
			temp.append("Feeling ");
			int val = 0;
			String updatedSymptom = null;
			for (int i = 0; i < v.size(); i++) {
				val = v.elementAt(i);
				updatedSymptom = EcgSymptoms.getStringValue(val);
				Logger.log(Level.DEBUG, TAG, "val =" + val);
				if (null != updatedSymptom) {
					temp.append(updatedSymptom);
				}

				if (i + 1 < v.size()) {
					temp.append(", ");
				}
			}
			symptoms.setText(temp);
		}
		/*
		 * ScrollView scView = (ScrollView)findViewById(R.id.scroll_v); int
		 * height = scView.getHeight(); Logger.log(Level.DEBUG, TAG,
		 * "scroll view height before="+height); height += Comment.getHeight();
		 * if(hrdataView.isShown()){ height += hrdataView.getHeight(); }
		 * if(symptoms.isShown()){ height += symptoms.getHeight(); }
		 * Logger.log(Level.DEBUG, TAG, "scroll view height after="+height);
		 * scView.setMinimumHeight(height);
		 */
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.lppbpl.android.userapp.AppBaseActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		removeDrawables(findViewById(R.layout.ecggraph));
		// System.gc();
	}

	/**
	 * Remove the views from view group.
	 *
	 * @param view
	 *            the view
	 */
	private void removeDrawables(View view) {
		try {
			if (null != view.getBackground()) {
				view.getBackground().setCallback(null);
			}
			if (view instanceof ViewGroup) {
				for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
					removeDrawables(((ViewGroup) view).getChildAt(i));
				}
				try {
					((ViewGroup) view).removeAllViews();
				} catch (Exception e) {
					Logger.log(Level.ERROR, TAG, "Error => " + e.getMessage());
				}
			}
		} catch (Exception e) {
			Logger.log(Level.ERROR, TAG, "Error 2 => " + e.getMessage());
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
		int i = v.getId();
		if (i == R.id.btn_menu_positive) {
			final String editTextStr = mEditTxt.getText().toString();

			if (null != editTextStr) {
				SfSendModel.getInstance().setUserComment(editTextStr);
			}
			final Intent intentSymtem = new Intent(this,
					EcgSymptomsListActivity.class);
			intentSymtem.putExtra(Constants.UNSAVED_RECORD, mShowUnsavedRecord);
			startActivityForResult(intentSymtem, REQUEST_CODE);

		} else if (i == R.id.btn_menu_negative) {
			showAlertDialog(R.drawable.ic_dialog_delete,
					get(R.string.discard_record),
					get(R.string.discard_confirmation), get(R.string.discard),
					get(R.string.cancel), false);

		} else if (i == R.id.bECGGraphEdit) {
			mEditTxt.setVisibility(View.VISIBLE);
			mSavefromEdt.setVisibility(View.INVISIBLE);
			mImageView.setVisibility(View.INVISIBLE);
			mEdit.setVisibility(View.INVISIBLE);
			mComment.setVisibility(View.INVISIBLE);

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
		if (mShowUnsavedRecord) {
			final PendingRecord rec = mAppModel.getPendingRecord();
			if (null != rec && rec.getId() != -1) {
				PendingRecordDb.getInstance().deleteRecord(rec.getId());
			}
			// finish();
			// } else {
			// Intent main = new Intent(this, MainMenuActivity.class);
			// startActivity(main);
			// finish();
		}
		finish();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		if (null != mDelete && mDelete.isEnabled() && !mShowUnsavedRecord) {
			showAlertDialog(R.drawable.ic_dialog_delete,
					get(R.string.discard_record),
					get(R.string.discard_confirmation), get(R.string.discard),
					get(R.string.cancel), false);
		} else {
			super.onBackPressed();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
			finish();
		}
	}

}
