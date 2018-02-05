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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.android.userapp.R;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.model.Profile;

// TODO: Auto-generated Javadoc
/**
 * This activity screen is to display user profile.
 *
 */
public class ProfileActivity extends AppBaseActivity implements OnClickListener {

	/** The m gender. */
	private RadioGroup mGender;

	/** The m edt name. */
	private EditText mEdtName;

	// private EditText mEdtAge;

	/** The m edt height. */
	private EditText mEdtHeight;

	/** The m edt weight. */
	private EditText mEdtWeight;

	private EditText etAge;

	/** The m save profile. */
	private Button mSaveProfile;

	/** The m profile. */
	private Profile mProfile;

	/** The Constant HEIGHT_MIN. */
	public static final int HEIGHT_MIN = 150;

	/** The Constant HEIGHT_MAX. */
	public static final int HEIGHT_MAX = 190;

	/** The Constant HEIGHT_VAL. */
	public static final int HEIGHT_VAL = 158;

	/** The Constant WEIGHT_MIN. */
	public static final int WEIGHT_MIN = 40;

	/** The Constant WEIGHT_MAX. */
	public static final int WEIGHT_MAX = 128;

	/** The Constant WEIGHT_VAL. */
	public static final int WEIGHT_VAL = 65;

	/** The Constant AGE_MIN. */
	public static final int AGE_MIN = 1;

	/** The Constant AGE_MAX. */
	public static final int AGE_MAX = 100;

	/** The Constant AGE_VAL. */
	public static final int AGE_VAL = 40;


	/**
	 * Method onCreate.
	 *
	 * @param savedInstanceState
	 *            Bundle
	 */


	private EditText mPatientId;

	private EditText mClinicName;

	private CheckBox tvtermCheck;

	private TextView txtTermsConditions;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.user_profile);
		mGender = (RadioGroup) findViewById(R.id.radioGroup1);
		mEdtName = (EditText) findViewById(R.id.etName);

		mEdtHeight = (EditText) findViewById(R.id.etHight);
		mEdtWeight = (EditText) findViewById(R.id.etWeight);
		mSaveProfile = (Button) findViewById(R.id.btn_menu_positive);
		mSaveProfile.setText(R.string.save);

		mPatientId= (EditText) findViewById(R.id.etPatientId);
		mClinicName= (EditText) findViewById(R.id.etClinicName);

		etAge= (EditText) findViewById(R.id.etAge);

		tvtermCheck= (CheckBox) findViewById(R.id.tvtermCheck);

		txtTermsConditions= (TextView) findViewById(R.id.txtTermsConditions);

		txtTermsConditions.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {

				if(motionEvent.getAction()==MotionEvent.ACTION_DOWN)
				{
					showAlertDialog(getString(R.string.title_tnc),getString(R.string.t_and_c_text),"OK",
							null,true);
				}

				return true;
			}
		});

		mProfile = mActModel.getUserProfile();
		mEdtName.setText(mProfile.getUserName());

		if (mProfile.isMale()) {
			mGender.check(R.id.rbMale);
		} else {
			mGender.check(R.id.rbFemale);
		}



			tvtermCheck.setVisibility(View.VISIBLE);
			txtTermsConditions.setVisibility(View.VISIBLE);


		mPatientId.setText(mProfile.getPatientId());
		mClinicName.setText(mProfile.getClinicName());

		 try {
			 etAge.setText(Integer.toString(mProfile.getUserAge()));
		 } catch (NumberFormatException e) {
			 etAge.setText("0");
		 }
		try {
			mEdtHeight.setText(Integer.toString(mProfile.getUserHeight()));
		} catch (NumberFormatException e) {
			mEdtHeight.setText("0");
			Logger.log(Level.DEBUG, TAG, "Error occured : " + e.getMessage());
		}
		try {
			mEdtWeight.setText(Integer.toString(mProfile.getUserWeight()));
		} catch (NumberFormatException e) {
			mEdtWeight.setText("0");
			Logger.log(Level.DEBUG, TAG, "Error occured : " + e.getMessage());
		}

		boolean isSettingAct = getIntent().getBooleanExtra("Setting", false);


		if (!isSettingAct) {
			setCustomTitle(R.string.title_activity);
			setCustomHeaderImage(R.drawable.ic_title_activity);
			mSaveProfile.setText(R.string.start);
		} else {
			setCustomNoIconTitle(R.string.title_profile);
		}
		mSaveProfile.setOnClickListener(this);
	}


	public void onClick(View v) {
		if (isValidUserData()) {

			if(!tvtermCheck.isChecked() && tvtermCheck.getVisibility()==View.VISIBLE){
				Toast.makeText(this,"Please Accept the terms and conditions",Toast.LENGTH_SHORT).show();
				return;
			}


			mProfile.setUserName(getName());
			mProfile.setUserAge(getAge());
			mProfile.setUserHeight(getHeight());
			mProfile.setUserWeight(getWeight());
			mProfile.setPatientId(mPatientId.getText().toString());
			mProfile.setClinicName(mClinicName.getText().toString());
			mProfile.setTermsAndConditionCheckBox(true);



			final int rbId = mGender.getCheckedRadioButtonId();
			mProfile.setMale(rbId == R.id.rbMale ? true : false);

			mActModel.saveUserProfile();

			if (mSaveProfile.getText().equals("Start")) {
				final Intent activityIntent = new Intent(this, ActivityON.class);
				activityIntent.putExtra("name", mEdtName.getText().toString());
				activityIntent.putExtra(Constants.SHOW_MAINMENU, true);
				startActivity(activityIntent);
				finish();
			} else {
				finish();
			}
		}
	}

	/**
	 * Validates user entered data.
	 *
	 * @return boolean
	 */
	private boolean isValidUserData() {
		boolean valid = true;


		if (null == getName() || 0 == getName().trim().length()) {
			showAlertDialog(get(R.string.title_profile),
					get(R.string.name_empty), get(R.string.OK), null, true);
			valid = false;
		} else if (!getName().matches(Constants.VALID_NAME)) {
			showAlertDialog(get(R.string.title_profile),
					get(R.string.valid_name), get(R.string.OK), null, true);
			valid = false;
		} else if ((getHeight() < HEIGHT_MIN) || (getHeight() > HEIGHT_MAX)) {
			showAlertDialog(get(R.string.title_profile),
					get(R.string.valid_height), get(R.string.OK), null, true);
			valid = false;
		} else if ((getWeight() < WEIGHT_MIN) || (getWeight() > WEIGHT_MAX)) {
			showAlertDialog(get(R.string.title_profile),
					get(R.string.valid_weight), get(R.string.OK), null, true);
			valid = false;
		}else if((getAge()<AGE_MIN) || (getAge()>AGE_MAX)){
		    showAlertDialog(get(R.string.title_profile),get(R.string.valid_age),get(R.string.OK),null,true);
        	valid=false;
		}
		return valid;
	}

	/**
	 * Method getHeight.
	 *
	 * @return int
	 */
	private int getHeight() {
		int height = 0;
		if (mEdtHeight.getText().toString().length() > 0) {
			try {
				height = (Integer.valueOf(mEdtHeight.getText().toString())
						.intValue());
			} catch (NumberFormatException e) {
				Logger.log(Level.DEBUG, TAG,
						"Error occured : " + e.getMessage());
			}
		}

		return height;
	}

	/**
	 * Method getName.
	 *
	 * @return String
	 */
	private String getName() {
		return mEdtName.getText().toString();
	}

	/**
	 * Method getWeight.
	 *
	 * @return int
	 */
	private int getWeight() {
		int weight = 0;
		if (mEdtWeight.getText().toString().length() > 0) {
			try {
				weight = (Integer.valueOf(mEdtWeight.getText().toString())
						.intValue());
			} catch (NumberFormatException e) {
				Logger.log(Level.DEBUG, TAG,
						"Error occured : " + e.getMessage());
			}
		}
		return weight;
	}



	private int getAge()
    {
        int age=0;

        if(etAge.getText().toString().length()>0){
            try {
                age = (Integer.valueOf(etAge.getText().toString()).intValue());
            } catch (NumberFormatException e) {
                Logger.log(Level.DEBUG, TAG,
                        "Error occured : " + e.getMessage());
            }
        }


        return age;
    }
}
