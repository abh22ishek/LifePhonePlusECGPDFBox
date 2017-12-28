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
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.android.userapp.R;

// TODO: Auto-generated Javadoc
/**
 * This is the Activity that displays Alert message to user when Feedback
 * message received from the Specialist.
 */
public class AlertPopupActivity extends AppBaseActivity implements
		OnCompletionListener {

	/** The m is fore ground. */
	private boolean mIsForeGround = false;

	/** The m media player. */
	private MediaPlayer mMediaPlayer = null;

	/** The m is beep on. */
	private boolean mIsBeepOn = true;

	/**
	 * Method onCreate.
	 *
	 * @param savedInstanceState
	 *            Bundle
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Bundle data = getIntent().getExtras();
		final String txt = data.getString("dlgHdr");
		mIsForeGround = data.getBoolean("isFG");
		Logger.log(Level.DEBUG, "AlertPopup", txt + "  " + mIsForeGround);
		if (mIsBeepOn) {
			// creating beep sound to alert the feedback received
			mMediaPlayer = MediaPlayer.create(this, R.raw.beep);
			Logger.log(Level.DEBUG, "IntelScreenActivity",
					"MediaPlayer start 1++");
			mMediaPlayer.start();
			Logger.log(Level.DEBUG, "IntelScreenActivity",
					"MediaPlayer start 1--");
			mMediaPlayer.setOnCompletionListener(this);
		}

		if (null != txt) {
			showAlertDialog(txt, get(R.string.diagnosis_received),
					get(R.string.view_now), get(R.string.later), false);
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
		mMediaPlayer.release();
		// opens the feedback list
		final Intent diaActivity = new Intent(this, DiagnosisMainList.class);
		diaActivity.putExtra("userId", getIntent().getStringExtra("userId"));
		startActivity(diaActivity);
		finish();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.lppbpl.android.userapp.AppBaseActivity#onNegativeButtonClick()
	 */
	@Override
	public void onNegativeButtonClick() {
		mMediaPlayer.release();
		if (!mIsForeGround) {
			closeAllActivities();
		}
		finish();
	}

	/**
	 * Method onCompletion.
	 *
	 * @param mp
	 *            MediaPlayer
	 * @see android.media.MediaPlayer$OnCompletionListener#onCompletion(MediaPlayer)
	 */
	@Override
	public void onCompletion(MediaPlayer mp) {
		if (mIsBeepOn) {
			mMediaPlayer.start();
			mIsBeepOn = false;
		}
	}
}
