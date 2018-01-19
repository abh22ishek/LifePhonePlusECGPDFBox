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

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.logging.Level;
import com.logging.Logger;
import com.lpp.pkgdirectory.ReadFileResponse;
import com.lpp.xmldata.ConvertTexttoXml;
import com.lppbpl.DataLoggingResponse;
import com.lppbpl.DataLoggingResponse.RuleAction;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.controller.SfUploadManager;
import com.lppbpl.android.userapp.db.PendingRecordDb;
import com.lppbpl.android.userapp.model.BgMeasurementModel;
import com.lppbpl.android.userapp.model.PendingRecord;
import com.lppbpl.android.userapp.model.Profile;
import com.lppbpl.android.userapp.model.SfSendModel;
import com.lppbpl.android.userapp.util.HttpUtil;
import com.lppbpl.android.userapp.util.Util;
import com.pdfbox.ActivityPdfBox;
import com.pdfbox.BGPdfBox;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

// TODO: Auto-generated Javadoc
/**
 * This is an Activity to select Blood sugar Measurement Symptoms and performs
 * uploading data to cloud.
 *
 */
public class BgSymtemsActivity extends NetworkConnBaseActivity implements
		OnClickListener, OnCheckedChangeListener {

	/** The Constant TAG. */
	private final static String TAG = BgSymtemsActivity.class.getSimpleName();

	/** The m view count. */
	final private int mViewCount = Constants.MAX_NO_OF_BG_SYMPTOMS;// total
	// symptoms
	// plus 1

	/** The m symptom name. */
	final private TextView[] mSymptomName = new TextView[mViewCount];

	/** The m image bg. */
	final private ImageView[] mImageBg = new ImageView[mViewCount];

	/** The m symptom. */
	final private CheckBox[] mSymptom = new CheckBox[mViewCount];

	/** The m show unsaved record. */
	private boolean mShowUnsavedRecord = false;

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
// abhishek
	private String data;
	private List<BgMeasurementModel> BgMeasurementList;

	String symptom_1="";
	String symptom_2="";
	String symptom_3="";
	String symptom_4="";
	String symptom_5="";
	String symptoms_selected="";
	String gender="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bgsymptems);
		setCustomTitle(R.string.title_bloodsugar);
		setCustomHeaderImage(R.drawable.ic_title_bloodsugar);

		mShowUnsavedRecord = getIntent().getBooleanExtra(
				Constants.UNSAVED_RECORD, false);
		BgMeasurementList=new ArrayList<BgMeasurementModel>();


		mProgDialog.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface arg) {
				SfUploadManager.getInstance().cancelUpload();
			}
		});

		int resIdCb = 0;
		int resIdIv = 0;
		int resIdTv;
		for (int i = 0; i < mViewCount; i++) {
			resIdCb = getResources().getIdentifier("symptoms_" + (i + 1), "id",
					getPackageName());
			mSymptom[i] = (CheckBox) this.findViewById(resIdCb);
			mSymptom[i].setOnCheckedChangeListener(this);

			resIdIv = getResources().getIdentifier("imageView" + (i + 1), "id",
			  		getPackageName());
			mImageBg[i] = (ImageView) findViewById(resIdIv);

			resIdTv = getResources().getIdentifier("tvText" + (i + 1), "id",
					getPackageName());
			mSymptomName[i] = (TextView) findViewById(resIdTv);
			mSymptomName[i].setText(Util.getBGSymtoms(i + 1));
		}

		final PendingRecord penRecord = mAppModel.getPendingRecord();

		@SuppressWarnings("unchecked")
		final Vector<Integer> vector = penRecord.getBgSymptoms();
		Logger.log(Level.DEBUG, TAG, "v size=" + vector.size());
		// set the check status for symptoms
		if (null != vector) {
			int val = 0;
			for (int i = 0; i < vector.size(); i++) {
				val = vector.elementAt(i);
				mSymptom[val - 1].setChecked(true);
			}
		}

		// to create a SQL DB for pending record.
		final Button save = (Button) findViewById(R.id.btn_menu_positive);
		save.setText(R.string.save);
		save.setOnClickListener(this);
		final Button delete = (Button) findViewById(R.id.btn_menu_negative);
		delete.setText(R.string.discard);
		delete.setOnClickListener(this);
	}

	/**
	 * Method onCheckedChanged.
	 *
	 * @param v
	 *            CompoundButton
	 * @param isChecked
	 *            boolean
	 * @see android.widget.CompoundButton$OnCheckedChangeListener#onCheckedChanged(CompoundButton,
	 *      boolean)
	 */
	public void onCheckedChanged(CompoundButton v, boolean isChecked) {
		int i = v.getId();
		if (i == R.id.symptoms_1) {
			mImageBg[0].setBackgroundResource(R.xml.single_listview_style);
			if(isChecked==true)
				symptom_1="Excessive Hunger";
			else
				symptom_1="";


		} else if (i == R.id.symptoms_2) {
			mImageBg[1].setBackgroundResource(R.xml.single_listview_style);
			if(isChecked==true)
				symptom_2="Profuse Sweating";
			else
				symptom_2="";


		} else if (i == R.id.symptoms_3) {
			mImageBg[2].setBackgroundResource(R.xml.single_listview_style);
			if(isChecked==true)
				symptom_3="Dizziness";
			else
				symptom_3="";


		} else if (i == R.id.symptoms_4) {
			mImageBg[3].setBackgroundResource(R.xml.single_listview_style);
			if(isChecked==true)
				symptom_4="Fatigue";
			else
				symptom_4="";


		} else if (i == R.id.symptoms_5) {
			mImageBg[4].setBackgroundResource(R.xml.single_listview_style);
			if(isChecked==true)
				symptom_5="Tremors";
			else
				symptom_5="";
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
		if (v.getId() == R.id.btn_menu_positive) {
		/*	if (mPinModel.isLoginForSessionSuccess()) {
				uploadData(true);
			} else {
				final Intent intent = new Intent(this, LoginActivity.class);
				intent.putExtra("show_discard_dialog", !mShowUnsavedRecord);
				startActivityForResult(intent, Constants.REQUEST_CODE_LOGIN);
			}
*/

			Profile mProfile = mActModel.getUserProfile();

			if(mProfile.isMale())
				gender="Male";
			else
				gender="Female";

			List<String> symptoms_list=new ArrayList<String>();

			symptoms_list.add(symptom_1);
			symptoms_list.add(symptom_2);
			symptoms_list.add(symptom_3);
			symptoms_list.add(symptom_4);
			symptoms_list.add(symptom_5);

			if(symptom_1=="")
			{
				symptoms_list.remove(symptom_1);
			}else if(symptom_2=="")
			{
				symptoms_list.remove(symptom_2);
			}
			else if(symptom_3=="")
			{
				symptoms_list.remove(symptom_3);
			}
			else if(symptom_4=="")
			{
				symptoms_list.remove(symptom_4);
			}
			else if(symptom_5=="")
			{
				symptoms_list.remove(symptom_5);
			}



			symptoms_list.removeAll(Arrays.asList(null,""));


			Logger.log(Level.DEBUG,TAG,"**Bg Symptoms list**="+symptoms_list);
			Logger.log(Level.DEBUG,TAG,"**Bg Symptoms list**="+symptoms_list.size());

			for(int i=0;i<symptoms_list.size();i++)
			{

				if(i==symptoms_list.size()-1)
				{
					symptoms_selected=symptoms_selected+symptoms_list.get(i);
				}else{
					symptoms_selected=symptoms_selected+symptoms_list.get(i)+",";
				}

			}

			if(getIntent()!=null)
			{

				data="Blood Glucose "+getIntent().getExtras().getString("BLOODSUGAR")+
						"\n"+ "Date and Time "+getIntent().getExtras().getString("TimeStamp")+"\n"
						+"User Comments "+getIntent().getExtras().getString("Comments");


				BgMeasurementModel bg=new BgMeasurementModel(getIntent().getExtras().getString("BLOODSUGAR"),
						getIntent().getExtras().getString("TimeStamp"),getIntent().getExtras().getString("Comments"),symptoms_selected
						,getIntent().getExtras().getString("FASTING_TYPE"),mProfile.getUserName(),mProfile.getPatientId(),
						mProfile.getClinicName(),String.valueOf(mProfile.getUserHeight()),
						String.valueOf(mProfile.getUserWeight()),String.valueOf(mProfile.getUserAge()),gender);
				BgMeasurementList.add(bg);

			}

			new ExecutePdfOperation().execute();
			//ReadFileResponse f=new ReadFileResponse(BgSymtemsActivity.this);
			//f.execute("BGActivityMeasurement","MeasurementBloodSugar.xml",Constants.BG_GLUCOSE);
		} else if (v.getId() == R.id.btn_menu_negative) {
			mDialogType = Constants.SHOW_DISCARD_DIALOGE;
			showAlertDialog(R.drawable.ic_dialog_delete,
					get(R.string.discard_record),
					get(R.string.discard_confirmation), get(R.string.discard),
					get(R.string.cancel), false);
		}
	};


	class ExecutePdfOperation extends AsyncTask<Void,Void,Void> {

		ProgressDialog progressDialog;

		@Override
		protected Void doInBackground(Void... voids) {
			new BGPdfBox().createBGTable(BgSymtemsActivity.this,false,BgMeasurementList);
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog=new ProgressDialog(BgSymtemsActivity.this);
			progressDialog.setCancelable(true);
			progressDialog.setIndeterminate(true);
			progressDialog.setMessage("Generating Pdf !. Please wait .....");
			progressDialog.show();

		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			if(progressDialog.isShowing()){
				progressDialog.dismiss();
			}
			ConvertTexttoXml.WriteXml(BgMeasurementList,symptoms_selected,BgSymtemsActivity.this);



		}
	}

	/**
	 * Uploads data to the cloud.
	 *
	 * @param uploadMe
	 *            the upload me
	 */
	private void uploadData(final boolean uploadMe) {
		final boolean[] status = new boolean[Constants.MAX_NO_OF_BG_SYMPTOMS];
		for (int i = 0; i < status.length; i++) {
			status[i] = mSymptom[i].isChecked();
		}

		if (!uploadMe || isNetworkConnected()) {
			mAppModel.setSymptomSelectionStatus(status);
			if (uploadMe) {
				showProgressDialog(R.string.saving_to_service);
			}
			mAppCrl.getSendController().uploadUserData(uploadMe);
		} else {
			/*
			 * Save the records even there is no network so that later user can
			 * upload the record from pending records
			 */

			mAppModel.setSymptomSelectionStatus(status);
			mAppCrl.getSendController().uploadUserData(false);

			final String errMsg = HttpUtil
					.getRespnseMsg(HttpUtil.HTTP_CONNECTION_DOWN)
					+ (mShowUnsavedRecord ? ""
					: get(R.string.measurement_saved));

			mDialogType = Constants.SHOW_INFO_DIALOGE;
			showAlertDialog(R.drawable.ic_dialog_error,
					get(R.string.network_connection), errMsg, get(R.string.OK),
					null, false);
		}
	}

	/**
	 * Handler will handle the response and informs the user for consulting the
	 * doctor and process the errors occurred while uploading Blood sugar data
	 * to cloud.
	 */
	final private MyHandler mHandler = new MyHandler(this);

	/**
	 * The Class MyHandler.
	 */
	private static class MyHandler extends Handler {

		/** The m activity. */
		private final WeakReference<BgSymtemsActivity> mActivity;

		/**
		 * Constructor for MyHandler.
		 *
		 * @param activity
		 *            BgSymtemsActivity
		 */
		private MyHandler(BgSymtemsActivity activity) {
			mActivity = new WeakReference<BgSymtemsActivity>(activity);
		}

		/**
		 * Method handleMessage.
		 *
		 * @param msg
		 *            android.os.Message
		 */
		public void handleMessage(android.os.Message msg) {
			final BgSymtemsActivity parent = mActivity.get();
			if (msg.what == Constants.CONTINUE_COMMAND) {
				parent.dismissProgressDislog();
				/*
				 * WHen access token expires, so show the login screen and do
				 * the login then start uploading / retrive the data.
				 */
				if (parent.isAccessTokenExpired()) {
					Logger.log(Level.DEBUG, TAG, "Access token expires.");
					parent.mDialogType = Constants.SHOW_SESSION_EXPIRED_DIALOG;
					parent.showAlertDialog(
							R.drawable.ic_dialog_cloud_upload_state_failed,
							parent.get(R.string.upload_failed),
							parent.get(R.string.session_expired),
							parent.get(R.string.OK), null, false);
					return;
				}
				final int status = SfSendModel.getInstance().getSendStatus();
				if (status < 0) {
					parent.mDialogType = Constants.SHOW_INFO_DIALOGE;
					final String message = parent.mAppModel.isUserNotScribed() ? String
							.format(parent.get(R.string.no_subscriptions_found),
									parent.mPinModel.getServerAddress(),
									parent.mAppModel.getSupportContactNo())
							: parent.mAppModel.isInvalidDeviceId() ? parent
							.get(R.string.invalid_device_id)
							: parent.mAppModel.isDeactivUserAccount() ? String.format(parent.get(R.string.deactive_account), parent.mAppModel.getSupportContactNo())
							: String.format(
							parent.get(R.string.unable_to_save),
							parent.mAppModel
									.getSupportContactNo());
					parent.showAlertDialog(
							R.drawable.ic_dialog_cloud_upload_state_failed,
							parent.get(R.string.upload_failed), message,
							parent.get(R.string.OK), null, false);
					return;
				} else {
					final DataLoggingResponse logResp = SfSendModel
							.getInstance().getDataLoggingResponse();
					if (null != logResp
							&& logResp.getRuleAction() == RuleAction.Inform) {
						if (logResp.getCaseId() <= 0) {
							parent.mDialogType = Constants.SHOW_INFO_DIALOGE;
							parent.showAlertDialog(
									R.drawable.ic_dialog_success,
									parent.get(R.string.save_record),
									parent.get(R.string.record_saved_successfully)
											+ logResp.getMeasurementId() + ".",
									parent.get(R.string.OK), null, false);
						} else {
							parent.mDialogType = Constants.SHOW_INFO_DIALOGE;
							parent.showAlertDialog(
									R.drawable.ic_dialog_success,
									parent.get(R.string.save_record),
									parent.get(R.string.doctor_consultaion_info)
											+ logResp.getCaseId(),
									parent.get(R.string.OK), null, false);
						}
					} else if (null != logResp
							&& logResp.getRuleAction() == RuleAction.Noop) {
						parent.mDialogType = Constants.SHOW_INFO_DIALOGE;
						parent.showAlertDialog(
								R.drawable.ic_dialog_success,
								parent.get(R.string.save_record),
								parent.get(R.string.record_saved_successfully)
										+ logResp.getMeasurementId() + ".",
								parent.get(R.string.OK), null, false);
					} else if (null != logResp
							&& logResp.getRuleAction() == RuleAction.Prompt) {
						/* Always perform a check if TPA agreement is changed before case creation */
						parent.doTPAVersionCheck();
					}
				}
			} else if (msg.what == Constants.DB_ERROR_OCCURRED) {
				parent.dismissProgressDislog();

				parent.mDialogType = Constants.SHOW_SESSION_EXPIRED_DIALOG;
				parent.showAlertDialog(
						R.drawable.ic_dialog_cloud_upload_state_failed,
						parent.get(R.string.upload_failed),
						parent.get(R.string.unexpected_error),
						parent.get(R.string.OK), null, false);

			}
		}
	};

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.lppbpl.android.userapp.AppBaseActivity#onPositiveButtonClick()
	 */
	@Override
	public void onPositiveButtonClick() {
		super.onPositiveButtonClick();
		if (mDialogType == Constants.SHOW_INFO_DIALOGE
				|| mDialogType == Constants.SHOW_DISCARD_DIALOGE
				|| mDialogType == Constants.SHOW_NOT_SUBSCRIBED_DIALOGE) {
			if (mShowUnsavedRecord
					&& mDialogType == Constants.SHOW_DISCARD_DIALOGE) {
				final PendingRecord rec = mAppModel.getPendingRecord();
				if (null != rec && rec.getId() != -1) {
					PendingRecordDb.getInstance().deleteRecord(rec.getId());
				}
			}
			finishActivity();
		} else if (mDialogType == Constants.SHOW_CONSULT_DOC_DIALOGE) {
			showPaymentModeDialog(this);
		} else if (mDialogType == Constants.SHOW_RECORD_SAVED_ONLY_DIALOG) {
			finishActivity();
		}
		else if (mDialogType == Constants.SHOW_SESSION_EXPIRED_DIALOG) {
			mPinModel.setLoginForSessionSuccess(false);
			// disable login class
			final Intent intent = new Intent(BgSymtemsActivity.this,
					MainMenuActivity.class);
			intent.putExtra("show_discard_dialog", !mShowUnsavedRecord);
			startActivityForResult(intent, Constants.REQUEST_CODE_LOGIN);
		}
	};

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.lppbpl.android.userapp.AppBaseActivity#onNegativeButtonClick()
	 */
	public void onNegativeButtonClick() {
		if (mDialogType == Constants.SHOW_CONSULT_DOC_DIALOGE) {
			finishActivity();
		} else if (mDialogType != Constants.SHOW_DISCARD_DIALOGE) {
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Logger.log(Level.DEBUG, TAG, "onActivityResult Request code :"
				+ requestCode);
		if (requestCode == Constants.REQUEST_CODE_LOGIN
				&& resultCode == RESULT_OK) {
			uploadData(true);
		} else if (requestCode == Constants.REQUEST_CODE_LOGIN
				&& resultCode == RESULT_CANCELED) {
			finishActivity();
		} else if (requestCode == Constants.REQUEST_CODE_UPLOAD
				&& resultCode == RESULT_CANCELED) {
			showPaymentModeDialog(this);
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
	 * Method sendEmptyMessage.
	 *
	 * @param what
	 *            int
	 * @see com.lppbpl.android.userapp.listener.SfBTDataListener#sendEmptyMessage(int)
	 */
	@Override
	public void sendEmptyMessage(int what) {
		super.sendEmptyMessage(what);
		mHandler.sendEmptyMessage(what);
	}

	/**
	 * Checks if the TPA agreement is changed or not.
	 */
	private void doTPAVersionCheck(){
		final Intent intent = new Intent(BgSymtemsActivity.this, TPAActivity.class);
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
		final DataLoggingResponse logResp = SfSendModel.getInstance().getDataLoggingResponse();
		if(consultationAllowed){
			if(callCenterSlaTime > 0){
				mDialogType = Constants.SHOW_CONSULT_DOC_DIALOGE;
				showAlertDialog(get(R.string.consult_doctor),
						String.format(get(R.string.consult_prompt_with_cc_sla), logResp.getMeasurementId(), consultationFee, slaTime, callCenterSlaTime),
						get(R.string.yes), get(R.string.no),
						false);
			} else{
				mDialogType = Constants.SHOW_CONSULT_DOC_DIALOGE;
				showAlertDialog(get(R.string.consult_doctor),
						String.format(get(R.string.consult_prompt), logResp.getMeasurementId(), consultationFee, slaTime),
						get(R.string.yes), get(R.string.no),
						false);
			}
		}else{
			mDialogType = Constants.SHOW_RECORD_SAVED_ONLY_DIALOG;
			showAlertDialog(get(R.string.consult_doctor),
					String.format(get(R.string.record_saved_only_prompt), logResp.getMeasurementId()),
					get(R.string.OK), null,
					false);
		}
	}

}
