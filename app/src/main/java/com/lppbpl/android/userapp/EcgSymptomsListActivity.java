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

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.logging.Level;
import com.logging.Logger;
import com.lpp.xmldata.ConvertTexttoXml;
import com.lppbpl.DataLoggingResponse;
import com.lppbpl.DataLoggingResponse.RuleAction;
import com.lppbpl.EcgData.EcgSymptoms;
import com.lppbpl.android.userapp.R;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.controller.SfUploadManager;
import com.lppbpl.android.userapp.db.PendingRecordDb;
import com.lppbpl.android.userapp.model.PendingRecord;
import com.lppbpl.android.userapp.model.Profile;
import com.lppbpl.android.userapp.model.SfSendModel;
import com.lppbpl.android.userapp.util.HttpUtil;
import com.pdfbox.EcgPdfBox;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import static com.lppbpl.android.userapp.R.id.symptoms_1;
import static com.lppbpl.android.userapp.R.id.symptoms_2;
import static com.lppbpl.android.userapp.R.id.symptoms_3;
import static com.lppbpl.android.userapp.R.id.symptoms_4;
import static com.lppbpl.android.userapp.R.id.symptoms_5;

/**
 * This Activity is to provide list of Ecg symptoms for selection.
 *
 */
public class EcgSymptomsListActivity extends NetworkConnBaseActivity implements
		OnClickListener, OnCheckedChangeListener {

	/** The tag. */
	private final String TAG = EcgSymptomsListActivity.class.getSimpleName();

	/** The m view count. */
	private final int mViewCount = Constants.MAX_NO_OF_ECG_SYMPTOMS;

	/** The m symptom name. */
	private final TextView[] mSymptomName = new TextView[mViewCount];

	/** The m image bg. */
	private final ImageView[] mImageBg = new ImageView[mViewCount];

	/** The m symptom. */
	private final CheckBox[] mSymptom = new CheckBox[mViewCount];

	/** The m show unsaved record. */
	private boolean mShowUnsavedRecord = false;

	/** The m saving step. */
	private int mSavingStep = 0;

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
	 * @param icicle
	 *            Bundle
	 */

	String symptom_1="";
	String symptom_2="";
	String symptom_3="";
	String symptom_4="";
	String symptom_5="";

	String clinicName;
	String patientName;
	String patientId;
	String Gender;
	String age;

	String symptoms_selected="";

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.ecgsymptoms);

		setCustomTitle(R.string.title_ecg);
		setCustomHeaderImage(R.drawable.ic_title_ecg);

		mShowUnsavedRecord = getIntent().getBooleanExtra(Constants.UNSAVED_RECORD, false);

		final PendingRecord penRecord = mAppModel.getPendingRecord();

		int resIdcb = 0;
		int resIdIv = 0;
		int resIdTv = 0;
		for (int i = 0; i < mViewCount; i++) {
			resIdcb = getResources().getIdentifier("symptoms_" + (i + 1), "id",
					getPackageName());
			mSymptom[i] = (CheckBox) this.findViewById(resIdcb);
			mSymptom[i].setOnCheckedChangeListener(this);

			resIdIv = getResources().getIdentifier("imageView" + (i + 1), "id",
					getPackageName());
			mImageBg[i] = (ImageView) findViewById(resIdIv);

			resIdTv = getResources().getIdentifier("tvText" + (i + 1), "id",
					getPackageName());
			mSymptomName[i] = (TextView) findViewById(resIdTv);
			mSymptomName[i].setText(EcgSymptoms.getStringValue(i + 1));
		}

		@SuppressWarnings("unchecked")
		final Vector<Integer> vector = penRecord.getEcgSymptoms();
		Logger.log(Level.DEBUG, TAG, "v size=" + vector.size());
		// set the check status for symptoms
		if (null != vector) {
			int val = 0;
			for (int i = 0; i < vector.size(); i++) {
				val = vector.elementAt(i);
				mSymptom[val - 1].setChecked(true);
			}
		}

		mProgDialog.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface arg0) {
				SfUploadManager.getInstance().cancelUpload();
			}
		});

		// to create a SQL DB for pending record.
		final Button mSave = (Button) findViewById(R.id.btn_menu_positive);
		mSave.setText(R.string.save);
		mSave.setOnClickListener(this);
		final Button mDelete = (Button) findViewById(R.id.btn_menu_negative);
		mDelete.setText(R.string.discard);
		mDelete.setOnClickListener(this);

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
	@Override
	public void onCheckedChanged(CompoundButton v, boolean isChecked) {
		int i = v.getId();
		if (i == R.id.symptoms_1) {
			mImageBg[0].setBackgroundResource(R.xml.single_listview_style);
			if(isChecked==true)
			symptom_1="Chest Pain";
			else
				symptom_1="";

		} else if (i == R.id.symptoms_2) {
			mImageBg[1].setBackgroundResource(R.xml.single_listview_style);
			if(isChecked==true)
				symptom_2="Palpitation";
				else
				symptom_2="";

		} else if (i == R.id.symptoms_3) {
			mImageBg[2].setBackgroundResource(R.xml.single_listview_style);
			if(isChecked==true)
				symptom_3="Sweating";
				else
				symptom_3="";

		} else if (i == R.id.symptoms_4) {
			mImageBg[3].setBackgroundResource(R.xml.single_listview_style);
			if(isChecked==true)
				symptom_4="Dizziness";
				else
				symptom_4="";

		} else if (i == R.id.symptoms_5) {
			mImageBg[4].setBackgroundResource(R.xml.single_listview_style);
			if(isChecked==true)
				symptom_5="Anxiety";
				else
				symptom_5="";


		} else {
		}
	}

	/** Handler handles the information and errors back from cloud. */
	final private MyHandler mHandler = new MyHandler(this);

	/**
	 * The Class MyHandler.
	 */
	private static class MyHandler extends Handler {

		/** The m activity. */
		private final WeakReference<EcgSymptomsListActivity> mActivity;

		/**
		 * Constructor for MyHandler.
		 *
		 * @param activity
		 *            EcgSymptomsListActivity
		 */
		private MyHandler(EcgSymptomsListActivity activity) {
			mActivity = new WeakReference<EcgSymptomsListActivity>(activity);
		}

		/**
		 * Method handleMessage.
		 *
		 * @param msg
		 *            android.os.Message
		 */
		public void handleMessage(android.os.Message msg) {
			final EcgSymptomsListActivity parent = mActivity.get();
			if (msg.what == Constants.CONTINUE_COMMAND) {
				parent.dismissProgressDislog();
				/*
				 * WHen access token expires, so show the login screen and do
				 * the login then start uploading / retrive the data.
				 */
				if (parent.isAccessTokenExpired()) {
					Logger.log(Level.DEBUG, parent.TAG, "Access token expires.");
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
					Logger.log(Level.DEBUG, "ECG SYMPTOMS",
							"DIALOG showAlertDialog++");
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
					Logger.log(Level.DEBUG, "ECG SYMPTOMS",
							"DIALOG showAlertDialog--");
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
			} else if (msg.what == Constants.UPDATE_ECG_UPLOAD_PROGRESS) {
				parent.mSavingStep += 100 / (2 + parent.mAppModel
						.getEcgLeadCount());
				parent.updateProgressDialog("( " + parent.mSavingStep + "% ) "
						+ parent.get(R.string.saving_to_service));
			} else if (msg.what == Constants.UPDATE_CENCELED) {
				sendEmptyMessageDelayed(Constants.UPDATE_CENCELED_CLEAR, 1000);
			} else if (msg.what == Constants.UPDATE_CENCELED_CLEAR) {
				SfUploadManager.getInstance().cancelUpload();
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
	 * com.lppbpl.android.userapp.AppBaseActivity#onNegativeButtonClick()
	 */
	public void onNegativeButtonClick() {
		if (mDialogType == Constants.SHOW_CONSULT_DOC_DIALOGE) {
			finishActivity();
		} else if (mDialogType != Constants.SHOW_DISCARD_DIALOGE) {
			finishActivity();
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
			final Intent intent = new Intent(this,MainMenuActivity.class);
			intent.putExtra("show_discard_dialog", !mShowUnsavedRecord);
			startActivityForResult(intent, Constants.REQUEST_CODE_LOGIN);
		}
	};

	// private void showSeverityDialog() {
	// mDialogType = Constants.SHOW_DIALOGE_NONE;
	// final SfSendModel sendModel = SfSendModel.getInstance();
	// final String[] chargesMenu = sendModel.getChargesMenu();
	// final long[] chargesList = sendModel.getChargesList();
	// final long balanceAmount = sendModel.getBalanceAmount();
	//
	// if (chargesList == null) {
	// Logger.log(Level.ERROR, TAG, "chargesList " + null
	// + "so finishing the activity");
	// finishActivity();
	// }
	//
	// // mSelectedCategoryIndex = 0;
	// // Will display the severity option dialog
	// final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	// builder.setTitle(get(R.string.choose_urgency));
	// builder.setSingleChoiceItems(chargesMenu, mSelectedCategoryIndex,
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int item) {
	// Logger.log(Level.DEBUG, TAG, "item:" + item);
	// mSelectedCategoryIndex = item;
	// }
	// });
	//
	// builder.setPositiveButton(get(R.string.OK),
	// new DialogInterface.OnClickListener() {
	//
	// public void onClick(DialogInterface dialog, int which) {
	//
	// final Intent main = new Intent(
	// EcgSymptomsListActivity.this,
	// ChargeInfoAuthActivity.class);
	// main.putExtra(Constants.UNSAVED_RECORD,
	// mShowUnsavedRecord);
	// main.putExtra(Constants.TAT_CATEGORY,
	// chargesMenu[mSelectedCategoryIndex]);
	// main.putExtra(Constants.TAT_CHARGES,
	// chargesList[mSelectedCategoryIndex]);
	// main.putExtra(Constants.TAT_BALANCE_AMOUNT,
	// balanceAmount);
	// startActivityForResult(main, REQUEST_CODE_UPLOAD);
	// // finishActivity();
	// }
	// });
	//
	// builder.setNegativeButton(get(R.string.cancel),
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int which) {
	// finishActivity();
	// }
	// });
	//
	// builder.setOnCancelListener(new OnCancelListener() {
	//
	// @Override
	// public void onCancel(DialogInterface arg0) {
	// finishActivity();
	// }
	// });
	//
	// final AlertDialog alert = builder.create();
	// alert.setCanceledOnTouchOutside(false);
	// alert.show();
	// }

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

			final Profile mProfile = mActModel.getUserProfile();

			patientId=mProfile.getPatientId();
			clinicName=mProfile.getClinicName();
			age= String.valueOf(mProfile.getUserAge());
			patientName=mProfile.getUserName();

			if(mProfile.isMale())
				Gender="Male";
			else
				Gender="Female";



			//symptoms_selected=symptom_1+" "+symptom_2+" "+symptom_3+" "+symptom_4+" "+symptom_5;
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


			Logger.log(Level.DEBUG,TAG,"**Symptoms list**="+symptoms_list);
			Logger.log(Level.DEBUG,TAG,"**Symptoms list**="+symptoms_list.size());

			for(int i=0;i<symptoms_list.size();i++)
			{

				if(i==symptoms_list.size()-1)
				{
					symptoms_selected=symptoms_selected+symptoms_list.get(i);
				}else{
					symptoms_selected=symptoms_selected+symptoms_list.get(i)+",";
				}

			}

			final PendingRecord penRecord = mAppModel.getPendingRecord();
			int heart_rate_=0;
			if(penRecord!=null)
			{
				heart_rate_=penRecord.getHeartRate();
			}
			Logger.log(Level.DEBUG,TAG,"**Symptoms selected**="+symptoms_selected);
			Logger.log(Level.DEBUG,TAG,"**Heart Rate**="+heart_rate_);
			// saving Ecg to xml file
			ConvertTexttoXml.saveEcgtoFile(true,symptoms_selected,String.valueOf(heart_rate_));



			if (mPinModel.isLoginForSessionSuccess()) {
				uploadData(true);
			} else {
				new ExecutePdfOperation().execute();

				//intent.putExtra("show_discard_dialog", !mShowUnsavedRecord);
				//startActivityForResult(intent, Constants.REQUEST_CODE_LOGIN);
			}
		} else if (v.getId() == R.id.btn_menu_negative) {

			mDialogType = Constants.SHOW_DISCARD_DIALOGE;
			showAlertDialog(R.drawable.ic_dialog_delete,
					get(R.string.discard_record),
					get(R.string.discard_confirmation), get(R.string.discard),
					get(R.string.cancel), false);
		}
	}


	class ExecutePdfOperation extends AsyncTask<Void,Void,Void>{

		ProgressDialog progressDialog;



		@Override
		protected Void doInBackground(Void... voids) {
			new EcgPdfBox().createEcgTable(EcgSymptomsListActivity.this,clinicName,patientName,Gender,
					patientId,symptoms_selected);
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog=new ProgressDialog(EcgSymptomsListActivity.this);
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
			Toast.makeText(EcgSymptomsListActivity.this, "Ecg Measurement is saved into a File", Toast.LENGTH_SHORT).show();
			final Intent intent = new Intent(EcgSymptomsListActivity.this,MainMenuActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);


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
	 * Upload Ecg data to cloud.
	 *
	 * @param uploadMe
	 *            the upload me
	 */
	private void uploadData(final boolean uploadMe) {
		final boolean[] status = new boolean[Constants.MAX_NO_OF_ECG_SYMPTOMS];
		for (int i = 0; i < status.length; i++) {
			status[i] = mSymptom[i].isChecked();
		}

		if (!uploadMe || isNetworkConnected()) {
			mAppModel.setSymptomSelectionStatus(status);
			mAppCrl.getSendController().uploadUserData(uploadMe);
			Logger.log(Level.DEBUG, "ECG SYMPTOMS", "DIALOG++");
			if (uploadMe) {
				mSavingStep = 0;
				showProgressDialog(R.string.saving_to_service);
			}
			Logger.log(Level.DEBUG, "ECG SYMPTOMS", "DIALOG--");
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
	 * Method sendEmptyMessage.
	 *
	 * @param what
	 *            int
	 * @see com.lppbpl.android.userapp.listener.SfBTDataListener#sendEmptyMessage(int)
	 */
	@Override
	public void sendEmptyMessage(int what) {
		super.sendEmptyMessage(what);
		if (null != mHandler) {
			mHandler.sendEmptyMessage(what);
		}
	}

	/**
	 * Checks if the TPA agreement is changed or not.
	 */
	private void doTPAVersionCheck(){
		final Intent intent = new Intent(EcgSymptomsListActivity.this, TPAActivity.class);
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
