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

package com.lppbpl.android.userapp.model;

import java.util.Timer;

import android.content.Context;
import android.content.SharedPreferences;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.ActivityParams;
import com.lppbpl.Measure;
import com.lppbpl.Measurement;
import com.lppbpl.SFMessaging;
import com.lppbpl.Measure.MeasurementAction;
import com.lppbpl.SFMessaging.MessageType;
import com.lppbpl.android.userapp.SouthFallsUserApp;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.controller.SfApplicationController;
import com.lppbpl.android.userapp.timertask.GetActivityDataTask;

// TODO: Auto-generated Javadoc
/**
 * The Class SfActivityModel.
 */
public class SfActivityModel {

	/** The Constant TAG. */
	public static final String TAG = SfActivityModel.class.getName();

	/** The m controller. */
	private SfApplicationController mController;

	/** The m running. */
	private boolean mRunning;

	/** The m get data timer. */
	private Timer mGetDataTimer;

	/** The m get data timer running. */
	private boolean mGetDataTimerRunning;

	/** The m profile. */
	final private Profile mProfile;

	/** The m act running in bg. */
	private boolean mActRunningInBg = false;

	/**
	 * Constructor for SfActivityModel.
	 *
	 * @param controller
	 *            SfApplicationController
	 */
	public SfActivityModel(SfApplicationController controller) {
		this.mController = controller;
		// mDurationTimer = new Timer();
		mGetDataTimer = new Timer();
		mProfile = new Profile();

		// Load User Profile Data
		SharedPreferences userProfile = SouthFallsUserApp.getInstance()
				.getApplicationContext()
				.getSharedPreferences(Constants.USER_PROFILE,
						Context.MODE_PRIVATE);
		if (userProfile != null) {
			mProfile.setUserName(userProfile.getString("name", null));
			mProfile.setMale(userProfile.getBoolean("gender", true));// true-male,false-female
			mProfile.setUserAge(userProfile.getInt("age", 40));
			mProfile.setUserHeight(userProfile.getInt("height", 172));
			mProfile.setUserWeight(userProfile.getInt("weight", 60));
			mProfile.setPatientId(userProfile.getString("patientId",""));
			mProfile.setClinicName(userProfile.getString("clinicName",""));
		}

	}

	/**
	 * save user profile data in sharedPreferences.
	 */
	public void saveUserProfile() {
		Logger.log(Level.DEBUG, TAG, "saveUserProfile()");
		SharedPreferences userProfile = SouthFallsUserApp
				.getInstance()
				.getApplicationContext()
				.getSharedPreferences(Constants.USER_PROFILE,
						Context.MODE_PRIVATE);
		if (userProfile != null) {
			SharedPreferences.Editor userProfileEditor = userProfile.edit();
			if (userProfileEditor != null) {
				userProfileEditor.putString("name", mProfile.getUserName());
				userProfileEditor.putBoolean("gender", mProfile.isMale());// true-male,false-female
				userProfileEditor.putInt("age", mProfile.getUserAge());
				userProfileEditor.putInt("height", mProfile.getUserHeight());
				userProfileEditor.putInt("weight", mProfile.getUserWeight());
				userProfileEditor.putString("patientId",mProfile.getPatientId());
				userProfileEditor.putString("clinicName",mProfile.getClinicName());


				userProfileEditor.commit();
			}
		}
	}

	/**
	 * Method setRunning.
	 *
	 * @param option
	 *            boolean
	 */
	public void setRunning(boolean option) {
		mRunning = option;
	}

	/**
	 * Method isRunning.
	 *
	 * @return boolean
	 */
	public boolean isRunning() {
		return mRunning;
	}

	// Get Data Timer
	/**
	 * Method startGetDataTimer.
	 *
	 * @param period
	 *            long
	 */
	public void startGetDataTimer(long period) {
		Logger.log(Level.DEBUG, TAG, "GetActivityData timer start invoked.");

		if (!mGetDataTimerRunning) {
			mGetDataTimer = new Timer();
			mGetDataTimer.schedule(new GetActivityDataTask(mController),
					period, period);
			mGetDataTimerRunning = true;
			Logger.log(Level.DEBUG, TAG, "GetActivityData timer started.");
		}
	}

	/**
	 * Stop get data timer.
	 */
	public void stopGetDataTimer() {
		Logger.log(Level.DEBUG, TAG, "GetActivityData timer stop invoked.");

		if (mGetDataTimerRunning) {
			mGetDataTimer.cancel();
			mGetDataTimerRunning = false;
			Logger.log(Level.DEBUG, TAG, "GetActivityData timer stopped.");
		}
	}

	/**
	 * Send start and stop activity.
	 *
	 * @param activityParams
	 *            the activity params
	 */
	public void startActivity(ActivityParams activityParams) {
		final Measure measureCmd;
		final SFMessaging message;

		measureCmd = Measure.newBuilder().setMeasurementType(Measurement.ACT)
				.setMeasurementAction(MeasurementAction.START)
				.setActParams(activityParams).build();
		message = SFMessaging.newBuilder().setSfMsgType(MessageType.MeasureMsg)
				.setSfMeasureMsg(measureCmd).build();
		mController.getBluetoothClient().sendCommand(message);

		// Start Duration timer
		setRunning(true);
		// startDurationTimer(convertHoursToMilliseconds(activityParams.getActDuration()));
		Logger.log(Level.DEBUG, TAG, "Activity started..");
	}

	/**
	 * Stop activity.
	 */
	public void stopActivity() {
		// Stop the Duration and GetData timers
		setRunning(false);
		// stopDurationTimer();
		stopGetDataTimer();
		setActivityRunningInBackground(false);

		final Measure measureCmd;
		final SFMessaging message;

		measureCmd = Measure.newBuilder().setMeasurementType(Measurement.ACT)
				.setMeasurementAction(MeasurementAction.STOP).build();
		message = SFMessaging.newBuilder().setSfMsgType(MessageType.MeasureMsg)
				.setSfMeasureMsg(measureCmd).build();
		mController.getBluetoothClient().sendCommand(message);
		Logger.log(Level.DEBUG, TAG, "Activity stopped..");
	}

	/**
	 * Pause activity.
	 */
	// public void pauseActivity() {
	// // Stop the Duration and GetData timers
	// stopGetDataTimer();
	//
	// final Measure measureCmd;
	// final SFMessaging message;
	//
	// measureCmd = Measure.newBuilder().setMeasurementType(Measurement.ACT)
	// .setMeasurementAction(MeasurementAction.PAUSE).build();
	// message = SFMessaging.newBuilder().setSfMsgType(MessageType.MeasureMsg)
	// .setSfMeasureMsg(measureCmd).build();
	// mController.getBluetoothClient().sendCommand(message);
	// Logger.log(Level.DEBUG, TAG, "Activity paused..");
	// }

	/**
	 * Resume activity.
	 */
	// public void resumeActivity() {
	// final Measure measureCmd;
	// final SFMessaging message;
	//
	// measureCmd = Measure.newBuilder().setMeasurementType(Measurement.ACT)
	// .setMeasurementAction(MeasurementAction.RESUME).build();
	// message = SFMessaging.newBuilder().setSfMsgType(MessageType.MeasureMsg)
	// .setSfMeasureMsg(measureCmd).build();
	// mController.getBluetoothClient().sendCommand(message);
	//
	// // Start Duration timer
	// setRunning(true);
	// startGetDataTimer(Constants.GET_DATA_ACTIVITY_IN_MILLISEC);
	// Logger.log(Level.DEBUG, TAG, "Activity resumed..");
	// }

	/**
	 * Method getUserProfile.
	 *
	 * @return Profile
	 */
	public Profile getUserProfile() {
		return this.mProfile;
	}

	/**
	 * Method isActivityRunningInBackground.
	 *
	 * @return boolean
	 */
	public boolean isActivityRunningInBackground() {
		return mActRunningInBg;
	}

	/**
	 * Method setActivityRunningInBackground.
	 *
	 * @param running
	 *            boolean
	 */
	public void setActivityRunningInBackground(boolean running) {
		mActRunningInBg = running;
	}

}
