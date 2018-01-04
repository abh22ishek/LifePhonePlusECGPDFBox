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

import java.lang.ref.WeakReference;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.ActivityData;
import com.lppbpl.ActivityParams;
import com.lppbpl.Response;
import com.lppbpl.android.userapp.R;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.model.PendingRecord;
import com.lppbpl.android.userapp.model.Profile;

/**
 * The Class ActivityON.
 */
public class ActivityON extends AppBaseActivity implements OnClickListener {

	/** The Constant TAG. */
	private static final String TAG = "ActivityON";

	/** The m stop btn. */
	private Button mStopBtn;

	/** The m start time. */
	private long mStartTime;

	/**
	 * Method onCreate.
	 *
	 * @param savedInstanceState
	 *            Bundle
	 */

	 String userName;
	 Profile mProfile;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activityon);
		setCustomTitle(R.string.title_activity);
		setCustomHeaderImage(R.drawable.ic_title_activity);

		final TextView mName = (TextView) findViewById(R.id.tvName);
		 userName = this.getIntent().getStringExtra("name");
		if (null != userName) {
			mName.setText(userName);
		}

		// mUserAge = (TextView)findViewById(R.id.user_age);
		final TextView mUserHeight = (TextView) findViewById(R.id.user_height);
		final TextView mUserWeight = (TextView) findViewById(R.id.user_weight);

		mStopBtn = (Button) findViewById(R.id.btn_menu_positive);
		mStopBtn.setText(R.string.stop);
		mStopBtn.setEnabled(false);

		mStopBtn.setOnClickListener(this);
		mStopBtn.setBackgroundResource(R.drawable.button_grey);
		 mProfile = mActModel.getUserProfile();
		if (null == userName) {
			mName.setText(mProfile.getUserName());
		}
		// mUserAge.setText(Integer.toString(mProfile.getUserAge()) + " yrs");
		mUserHeight.setText("Ht: " + mProfile.getUserHeight() + " cm");
		mUserWeight.setText("Wt: " + Integer.toString(mProfile.getUserWeight()) + " kg");

		startAct();

	}

	/**
	 * Stop the running activity.
	 */
	private void stopAct() {
		if (mActModel.isRunning()) {
			mActModel.stopActivity();
		}
	}

	/**
	 * Starts the activity.
	 */
	private void startAct() {
		Logger.log(Level.DEBUG, TAG, "startAct().. started");

		if (!mActModel.isActivityRunningInBackground()
				&& !mActModel.isRunning()) {
			final int height = mActModel.getUserProfile().getUserHeight();
			final int weight = mActModel.getUserProfile().getUserWeight();

			final ActivityParams activityParams = ActivityParams.newBuilder()
					.setActUsrHeight(height).setActUsrWeight(weight).build();

			mActModel.startActivity(activityParams);
			mStartTime = new Date().getTime();
		} else {
			updateActDisplay();
		}

		Logger.log(Level.DEBUG, TAG, "startAct().. ended");
		mActModel.startGetDataTimer(Constants.GET_DATA_ACTIVITY_IN_MILLISEC);
		Logger.log(Level.DEBUG, TAG, "Activity reading started..");

		// simulation code start
		/*
		 * Thread t = new Thread(new Runnable() { public void run() { for (int i
		 * = 0; i < 10; i ++) { try { Thread.sleep(1000); } catch
		 * (InterruptedException e) { e.printStackTrace(); } ActivityData
		 * actData = ActivityData.newBuilder().setTotalDistance(123 + i*10)
		 * .setTotalEnergy(50 * (i+1) + i*3).setTotalSteps(200 + i*20).build();
		 * Response response = Response.newBuilder().setResponseType(
		 * ResponseType.SRD). setMeasurementType(Measurement.ACT)
		 * .setActData(actData).build(); dataReceived(response); } } });
		 * t.start();
		 */
		// simulation code end
	}

	/**
	 * Handler initiates call updateActDisplay() on data received from the
	 * devices to update the display.
	 */
	private final MyHandler mHandler = new MyHandler(this);

	/**
	 * The Class MyHandler.
	 */
	private static class MyHandler extends Handler {

		/** The m activity. */
		private final WeakReference<ActivityON> mActivity;

		/**
		 * Constructor for MyHandler.
		 *
		 * @param activity
		 *            ActivityON
		 */
		private MyHandler(ActivityON activity) {
			mActivity = new WeakReference<ActivityON>(activity);
		}

		/**
		 * Method handleMessage.
		 *
		 * @param msg
		 *            android.os.Message
		 */
		public void handleMessage(android.os.Message msg) {
			final ActivityON parent = mActivity.get();
			if (msg.what == Constants.DISPLAY_COMMAND) {
				parent.updateActDisplay();
			}
		}
	};

	/**
	 * Updates activity display screen- Calories, Distance covered and time of
	 * activity started etc.
	 */
	private void updateActDisplay() {

		final PendingRecord record = mAppModel.getPendingRecord();
		if (null != record && null != record.getResponse()) {
			final ActivityData activityData = record.getResponse().getActData();
			if (null == activityData) {
				return;
			}
			mStopBtn.setBackgroundResource(R.drawable.button_green);
			mStopBtn.setEnabled(true);
			mStartTime = activityData.getStartingTime();

			final TextView mActStartedTimeStampTxtView = (TextView) findViewById(R.id.activity_timestamp_txtview);
			final TextView mDistanceDetailsTxtView = (TextView) findViewById(R.id.distance_details_txtview);
			final TextView mStepsDetailsTxtView = (TextView) findViewById(R.id.steps_details_txtview);
			final TextView mCaloriesBurntValueTxtView = (TextView) findViewById(R.id.calories_burnt_value_textview);
			final TextView mActTillTimeStampTxtview = (TextView) findViewById(R.id.activity_timediff_txtview);

			mActStartedTimeStampTxtView.setText(DateFormat.format("dd MMM, hh:mm A", mStartTime)
					.toString());
			mCaloriesBurntValueTxtView.setVisibility(View.VISIBLE);
			mCaloriesBurntValueTxtView.setText(Integer.toString(activityData
					.getTotalEnergy()));
			mDistanceDetailsTxtView.setText(Integer.toString(activityData
					.getTotalDistance()));
			mStepsDetailsTxtView.setText(Integer.toString(activityData
					.getTotalSteps()));
			mActTillTimeStampTxtview.setVisibility(View.VISIBLE);

			final int delta = (int) (new Date().getTime() - mStartTime);

			Logger.log(Level.DEBUG, TAG, "delta = " + delta);

			final int hours = delta / Constants.HOUR_IN_MILLISEC;
			final int mins = (delta - hours * Constants.HOUR_IN_MILLISEC)
					/ (60 * 1000);
			final int secs = (delta - hours * Constants.HOUR_IN_MILLISEC - mins
					* (60 * 1000)) / (1000);

			final boolean isTimeAvailable = ((hours > 0) || (mins > 0) || (secs > 0)) ? true
					: false;
			Logger.log(Level.DEBUG, TAG, "isTimeAvailable = " + isTimeAvailable);

			mActTillTimeStampTxtview
					.setVisibility(isTimeAvailable ? View.VISIBLE
							: View.INVISIBLE);

			String time = hours > 9 ? ""+ hours : "0" + hours;
			time +=" hr ";
			time += mins > 9 ? mins : "0" + mins;
			time +=" min";
			mActTillTimeStampTxtview.setText(time);

		}
	}

	/**
	 * Method onClick.
	 *
	 * @param view
	 *            View
	 * @see android.view.View$OnClickListener#onClick(View)
	 */
	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.btn_menu_positive) {
			showNextScreen();
		}
	}

	/**
	 * Moves to ActivityFinalDisplay Activity screen.
	 */
	public void showNextScreen() {
		Logger.log(Level.DEBUG, TAG, "showNextScreen()");
		stopAct();
		final long mStopTime = new Date().getTime();
		final Intent intent = new Intent(this, ActivityFinalDisplay.class);
		intent.putExtra("start_time", mStartTime);
		intent.putExtra("stop_time", mStopTime);
		intent.putExtra("name",userName);
		intent.putExtra("height",String.valueOf(mProfile.getUserHeight()));
		intent.putExtra("weight",String.valueOf(mProfile.getUserWeight()));
		startActivity(intent);
		finish();
	}

	/**
	 * Method dataReceived.
	 *
	 * @param response
	 *            Response
	 * @see com.lppbpl.android.userapp.listener.SfBTDataListener#dataReceived(Response)
	 */
	@Override
	public void dataReceived(Response response) {
		super.dataReceived(response);

		if (Logger.isInfoEnabled()) {
			Logger.log(Level.INFO, TAG, "Activity Response received:"
					+ response.toString());
		}

		final PendingRecord pendingRec = mAppModel.getPendingRecord();
		if (null != pendingRec) {
			pendingRec.setResponse(response);
			mHandler.sendEmptyMessage(Constants.DISPLAY_COMMAND);
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

}
