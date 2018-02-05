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
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.logging.Level;
import com.logging.Logger;
import com.lpp.pkgdirectory.ReadFileResponse;
import com.lpp.xmldata.CaptureScreen;
import com.lpp.xmldata.ConvertTexttoXml;
import com.lppbpl.ActivityData;
import com.lppbpl.DataLoggingResponse;
import com.lppbpl.Response;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.controller.SfUploadManager;
import com.lppbpl.android.userapp.db.PendingRecordDb;
import com.lppbpl.android.userapp.model.ActivityMeasurementModel;
import com.lppbpl.android.userapp.model.PendingRecord;
import com.lppbpl.android.userapp.model.Profile;
import com.lppbpl.android.userapp.model.SfSendModel;
import com.lppbpl.android.userapp.util.HttpUtil;
import com.pdfbox.ActivityPdfBox;
import com.pdfbox.EcgPdfBox;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class ActivityFinalDisplay.
 */
public class ActivityFinalDisplay extends AppBaseActivity implements
		OnClickListener {

	/** The Constant TAG. */
	private static final String TAG = ActivityFinalDisplay.class
			.getSimpleName();

	/** The m delete. */
	private Button mDelete;

	/** The m edit. */
	private Button mEdit;

	/** The m edit txt. */
	private EditText mEditTxt;

	/** The m act data. */
	private ActivityData mActData = null;
	// Activity data display data to save
	private TextView mCalsBurntTotTimeTxtView;
	private TextView mActTimeStampTxtView;
	private long stopTime;
	private long startTime;
	private String time;
	private List<ActivityMeasurementModel> activityMeasurementModelList;
	private LinearLayout menubar;

	/**
	 * Method onCreate.
	 *
	 * @param bundle
	 *            Bundle
	 */
	public  Button mSave;

	@Override
	protected void onResume() {
		super.onResume();
		activityMeasurementModelList=new ArrayList<ActivityMeasurementModel>();
		menubar= (LinearLayout) findViewById(R.id.id_menubars);
		menubar.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onCreate(final Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activityfinaldisplay);

		setCustomTitle(R.string.title_activity);
		setCustomHeaderImage(R.drawable.ic_title_activity);

		mSave = (Button) findViewById(R.id.btn_menu_positive);
		mSave.setText(R.string.save);
		mDelete = (Button) findViewById(R.id.btn_menu_negative);
		mDelete.setText(R.string.discard);
		mEdit = (Button) findViewById(R.id.bActivityFinalEdit);
		mEditTxt = (EditText) findViewById(R.id.comments_txtview);
		mSave.setOnClickListener(this);
		mDelete.setOnClickListener(this);

		final PendingRecord record = mAppModel.getPendingRecord();

		if (null != record) {
			final Response resp = record.getResponse();
			if (null != resp) {
				mActData = resp.getActData();
			}

			final String comment = record.getComment();
			if (null != comment && comment.length() > 0) {
				mEditTxt.setText(comment);
			}
		}

		mProgDialog.setOnCancelListener(cancelListener);

		final TextView mCalsBurntValueTxtView = (TextView) findViewById(R.id.calories_burnt_value_textview);
		mCalsBurntTotTimeTxtView = (TextView) findViewById(R.id.activity_timediff_txtview);
		mActTimeStampTxtView = (TextView) findViewById(R.id.activity_timestamp_txtview);
		final TextView mDistanceDetailsTxtView = (TextView) findViewById(R.id.distance_details_txtview);
		final TextView mStepsDetailsTxtView = (TextView) findViewById(R.id.steps_details_txtview);

		// Below code is to set the time, When activity viewed from pending
		// records.
		long tempStopTime = new Date().getTime();
		long tempActStartTime = tempStopTime;
		if (null != record && null != mActData) {
			tempActStartTime = mActData.getStartingTime();

			tempStopTime = tempActStartTime
					+ (mActData.getTimeCoveredSofar() * 60 * 1000L);
		}

		stopTime = getIntent().getLongExtra("stop_time",
				tempStopTime);
		startTime = getIntent().getLongExtra("start_time",
				tempActStartTime);



		if (null != mActData) {
			mCalsBurntValueTxtView.setText(Integer.toString(mActData
					.getTotalEnergy()));
			mDistanceDetailsTxtView.setText(Integer.toString(mActData
					.getTotalDistance()));
			mStepsDetailsTxtView.setText(Integer.toString(mActData
					.getTotalSteps()));
		}
		// activityTimestampTxtview.setText(Util.formatDateTime(stopTime));
		mActTimeStampTxtView.setText(DateFormat.format("hh:mm", startTime)
				.toString()
				+ DateFormat.format(" - hh:mm A,dd MMM yy", stopTime)
				.toString());

		final int delta = (int) (stopTime - startTime);
		Logger.log(Level.DEBUG, TAG, "delta = " + delta);

		final int hours = delta / Constants.HOUR_IN_MILLISEC;
		final int mins = (delta - hours * Constants.HOUR_IN_MILLISEC)
				/ (60 * 1000);
		final int secs = (delta - hours * Constants.HOUR_IN_MILLISEC - mins
				* (60 * 1000)) / (1000);

		final boolean isTimeAvailable = (hours > 0) || (mins > 0) || (secs > 0) ? true
				: false;
		Logger.log(Level.DEBUG, TAG, "isTimeAvailable = " + isTimeAvailable);

		mCalsBurntTotTimeTxtView.setVisibility(View.VISIBLE);
//		mCalsBurntTotTimeTxtView.setText(isTimeAvailable ? hours + ":" + mins
//				+ " mins" : 0 + ":" + 0 + " min");

		time = hours > 9 ? ""+ hours : "0" + hours;
		time +=" hr ";
		time += mins > 9 ? mins : "0" + mins;
		time +=" min";
		mCalsBurntTotTimeTxtView.setText(time);

	}

	final private OnCancelListener cancelListener = new OnCancelListener() {
		@Override
		public void onCancel(DialogInterface arg0) {
			SfUploadManager.getInstance().cancelUpload();
		}
	};

	/**
	 * Handler will handle the response and informs the user for the errors
	 * occurred while uploading activity data to cloud.
	 */
	private final MyHandler mHandler = new MyHandler(this);

	/**
	 * The Class MyHandler.
	 */
	private static class MyHandler extends Handler {

		/** The m activity. */
		private final WeakReference<ActivityFinalDisplay> mActivity;

		/**
		 * Constructor for MyHandler.
		 *
		 * @param activity
		 *            ActivityFinalDisplay
		 */
		private MyHandler(final ActivityFinalDisplay activity) {
			mActivity = new WeakReference<ActivityFinalDisplay>(activity);
		}

		/**
		 * Method handleMessage.
		 *
		 * @param msg
		 *            android.os.Message
		 */
		public void handleMessage(android.os.Message msg) {
			final ActivityFinalDisplay parent = mActivity.get();
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
							: parent.mAppModel.isDeactivUserAccount() ? String.format(parent.get(R.string.deactive_account), parent.mAppModel.getSupportContactNo())
							: String.format(parent
									.get(R.string.unable_to_save),
							parent.mAppModel
									.getSupportContactNo());
					parent.showAlertDialog(
							R.drawable.ic_dialog_cloud_upload_state_failed,
							parent.get(R.string.upload_failed), message,
							parent.get(R.string.OK), null, false);
				} else {
					final DataLoggingResponse logResp = SfSendModel
							.getInstance().getDataLoggingResponse();

					if (null != logResp) {
						Logger.log(Level.DEBUG, TAG,
								"Activity upload response " + logResp);
					}
					parent.mDialogType = Constants.SHOW_DIALOGE_NONE;
					parent.showAlertDialog(R.drawable.ic_dialog_success,
							parent.get(R.string.success),
							parent.get(R.string.successfully_saved),
							parent.get(R.string.OK), null, false);
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
		if (1 == requestCode && resultCode == RESULT_OK) {
			uploadData(true);
		} else if (1 == requestCode && resultCode == RESULT_CANCELED) {
			finish();
		}
	}

	/**
	 * Method onClick.
	 *
	 * @param
	 *
	 *
	 */


	@Override
	public void onClick(View v) {

		if (v == mSave) {
			String gender;
			Profile mProfile = mActModel.getUserProfile();
			if(mProfile.isMale())
					gender="Male";
			else
				gender="Female";
		/*	if (mPinModel.isLoginForSessionSuccess()) {
				uploadData(true); // save and upload
			} else {
				final Intent intent = new Intent(this, LoginActivity.class);
				final boolean showUnsavedRecord = getIntent().getBooleanExtra(
						Constants.UNSAVED_RECORD, false);
				intent.putExtra("show_discard_dialog", !showUnsavedRecord);
				startActivityForResult(intent, 1);
			}
        */
			String data = "Total steps taken=" + mActData.getTotalEnergy() + "\n" + "Total meters travelled=" + mActData.getTotalDistance() + "\n"
					+ "Total calories burnt=" + mActData.getTotalEnergy() + "\n" + "User Comments=" + mEditTxt.getText().toString() + "\n" + "Start Time to End Time="
					+ mActTimeStampTxtView.getText().toString() + "\n" + "Duration=" + mCalsBurntTotTimeTxtView.getText().toString();

		//	Toast.makeText(ActivityFinalDisplay.this, "Activity Measurement is saved into a File", Toast.LENGTH_SHORT).show();

			ActivityMeasurementModel act = new ActivityMeasurementModel();

			act.setStartEndtime(DateFormat.format("hh:mm", startTime).toString() + DateFormat.format(" - hh:mm A,dd MMM yy", stopTime)
					.toString());
			act.setTotalcaloriesburnt(mActData.getTotalEnergy() + "");
			act.setTotalstepstaken(String.valueOf(mActData.getTotalSteps()));
			act.setTotalmetrestravelled(String.valueOf(mActData.getTotalDistance()));
			act.setUsercomments(mEditTxt.getText().toString());



			act.setUserName(getIntent().getExtras().get("name").toString());
			act.setHeight(getIntent().getExtras().get("height").toString());
			act.setWeight(	getIntent().getExtras().get("weight").toString());
			act.setPatientId(getIntent().getExtras().getString("patientId"));
			act.setClinicName(getIntent().getExtras().getString("clinicName"));
			act.setGender(gender);
			act.setAge(String.valueOf(mProfile.getUserAge()));

			activityMeasurementModelList.add(act);
			View view = findViewById(R.id.scroll_v).getRootView();
		//	CaptureScreen.captureScreen(ActivityFinalDisplay.this, view, menubar, "ScreenActivity");
			//captureScreen(R.id.scroll_v);

			new ExecutePdfOperation().execute("");
			//ReadFileResponse f = new ReadFileResponse(ActivityFinalDisplay.this);
			//f.execute("ActivityMeasurement", "ActivityMeasurement.xml", Constants.ACTIVITY_RUNNING);



		} else if (v == mDelete) {
			mDialogType = Constants.SHOW_DISCARD_DIALOGE;
			showAlertDialog(R.drawable.ic_dialog_delete,
					get(R.string.discard_record),
					get(R.string.discard_confirmation), get(R.string.discard),
					get(R.string.cancel), false);

		} else if (v == mEdit) {
			mEditTxt.setVisibility(View.VISIBLE);

			final TextView saveFromET = (TextView) findViewById(R.id.tvSavefrmET);
			saveFromET.setVisibility(View.INVISIBLE);

			final ImageView myIV = (ImageView) findViewById(R.id.myiv);
			myIV.setVisibility(View.INVISIBLE);

			mEdit.setVisibility(View.INVISIBLE);

			final TextView actComment = (TextView) findViewById(R.id.tvActivityComment);
			actComment.setVisibility(View.INVISIBLE);
		}
	}

	class ExecutePdfOperation extends AsyncTask<String,Void,String> {

		ProgressDialog progressDialog;

		@Override
		protected String doInBackground(String... Strings) {
			new ActivityPdfBox().createActivityPdfTable(ActivityFinalDisplay.this,activityMeasurementModelList);
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog=new ProgressDialog(ActivityFinalDisplay.this);
			progressDialog.setCancelable(true);
			progressDialog.setIndeterminate(true);
			progressDialog.setMessage("Generating Report !. Please wait .....");
			progressDialog.show();

		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(progressDialog.isShowing()){
				progressDialog.dismiss();
			}
			ConvertTexttoXml.writetoXmlActivityData(activityMeasurementModelList,
					ActivityFinalDisplay.this);



		}
	}

	/**
	 * Uploads measured activity data to cloud.
	 *
	 * @param uploadMe
	 *            the upload me
	 */
	private void uploadData(final boolean uploadMe) {

		if (!uploadMe || isNetworkConnected()) {
			final String userComment = mEditTxt.getText().toString();
			Logger.log(Level.DEBUG, TAG, "set userComment" + userComment);
			if (null != userComment && userComment.length() > 0) {
				SfSendModel.getInstance().setUserComment(userComment);
			}

			Logger.log(Level.DEBUG, TAG, "Upload Activity Called");
			if (uploadMe) {
				showProgressDialog(R.string.saving_to_service);
			}
			mAppCrl.getSendController().uploadUserData(uploadMe);
		} else {
			/*
			 * Save the records even there is no network so that later user can
			 * upload the record from pending records
			 */
			final String userComment = mEditTxt.getText().toString();
			Logger.log(Level.DEBUG, TAG, "set userComment" + userComment);
			if (null != userComment && userComment.length() > 0) {
				SfSendModel.getInstance().setUserComment(userComment);
			}
			mAppCrl.getSendController().uploadUserData(false);
			final boolean showUnsavedRecord = getIntent().getBooleanExtra(
					Constants.UNSAVED_RECORD, false);
			final String errMsg = HttpUtil
					.getRespnseMsg(HttpUtil.HTTP_CONNECTION_DOWN)
					+ (showUnsavedRecord ? "" : get(R.string.measurement_saved));

			mDialogType = Constants.SHOW_INFO_DIALOGE;
			showAlertDialog(R.drawable.ic_dialog_error,
					get(R.string.network_connection), errMsg, get(R.string.OK),
					null, true);
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
		final boolean showUnsavedRecord = getIntent().getBooleanExtra(
				Constants.UNSAVED_RECORD, false);
		if (showUnsavedRecord && mDialogType == Constants.SHOW_DISCARD_DIALOGE) {
			final PendingRecord rec = mAppModel.getPendingRecord();
			if (null != rec && rec.getId() != -1) {
				PendingRecordDb.getInstance().deleteRecord(rec.getId());
			}
			finish();
		} else if (mDialogType == Constants.SHOW_SESSION_EXPIRED_DIALOG) {
			mPinModel.setLoginForSessionSuccess(false);
			final Intent intent = new Intent(ActivityFinalDisplay.this, MainMenuActivity.class);
			intent.putExtra("show_discard_dialog", !showUnsavedRecord);
			startActivityForResult(intent, 1);
		} else {
			finish();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		final boolean showUnsavedRecord = getIntent().getBooleanExtra(
				Constants.UNSAVED_RECORD, false);
		if (null != mDelete && mDelete.isShown() && !showUnsavedRecord) {
			mDialogType = Constants.SHOW_DISCARD_DIALOGE;
			showAlertDialog(R.drawable.ic_dialog_delete,
					get(R.string.discard_record),
					get(R.string.discard_confirmation), get(R.string.discard),
					get(R.string.cancel), false);
		} else {
			super.onBackPressed();
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
	public void sendEmptyMessage(final int what) {
		super.sendEmptyMessage(what);
		if (null != mHandler) {
			mHandler.sendEmptyMessage(what);
		}
	}

}
