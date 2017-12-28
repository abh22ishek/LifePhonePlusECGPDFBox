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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.BloodGlucoseParams;
import com.lppbpl.Measure;
import com.lppbpl.Measurement;
import com.lppbpl.Response;
import com.lppbpl.Response.ResErrorCode;
import com.lppbpl.Response.ResStatusCode;
import com.lppbpl.ResponseType;
import com.lppbpl.SFMessaging;
import com.lppbpl.SFMessaging.MessageType;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.model.PendingRecord;

import java.lang.ref.WeakReference;

// TODO: Auto-generated Javadoc
/**
 * This is an Activity to measure blood sugar.
 */
public class BloodSugarActivity extends AppBaseActivity {

	/** The Constant TAG. */
	private static final String TAG = BloodSugarActivity.class.getSimpleName();

	/** The m instr txt view. */
	private TextView mInstrTxtView = null;

	/** The m waiting for bg strip insert. */
	private boolean mWaitingForBGStripInsert = true;

	/** The m bg strip img. */
	private ImageView mBgStripImg = null;

	/** The m telephony mgr. */
	private TelephonyManager mTelephonyMgr;

	/**
	 * Method onCreate.
	 *
	 * @param savedInstanceState
	 *            Bundle
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.bloodsugaractivity);

		setCustomTitle(R.string.title_bloodsugar);
		setCustomHeaderImage(R.drawable.ic_title_bloodsugar);

		mInstrTxtView = (TextView) findViewById(R.id.tvBGInfo);
		mBgStripImg = (ImageView) findViewById(R.id.ibBgInstruction);

		mTelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		if (null != mTelephonyMgr) {
			mTelephonyMgr.listen(phoneStateListener,
					PhoneStateListener.LISTEN_CALL_STATE);
		}

		boolean mIsCtrSolution = getIntent().getBooleanExtra("calibration",
				false);
		final SharedPreferences pinPref = this.getSharedPreferences(
				Constants.CONTROL_SOLUTION, MODE_PRIVATE);
		mIsCtrSolution = !mIsCtrSolution ? pinPref.getBoolean(
				Constants.CONTROL_SOLUTION, false) : mIsCtrSolution;

		/*
		 * The below code is required when activity recreated and to avoid
		 * sending BG message to accessory
		 */
		if (null == savedInstanceState) {
			Logger.log(Level.INFO, TAG, "savedInstanceState is null");
			final Measure.Builder measureBuilder = Measure.newBuilder();
			measureBuilder.setMeasurementType(Measurement.BG);

			final BloodGlucoseParams bgParams = BloodGlucoseParams.newBuilder()
					.setBgControlSolution(mIsCtrSolution ? 1 : 0).build();
			measureBuilder.setBgParams(bgParams);

			final SFMessaging message = SFMessaging.newBuilder()

			.setSfMsgType(MessageType.MeasureMsg)
					.setSfMeasureMsg(measureBuilder.build()).build();

			sendCommand(message);
		} else {
			Logger.log(Level.INFO, TAG, "savedInstanceState is not null");
			mIsCtrSolution = savedInstanceState.getBoolean("calibration",
					mIsCtrSolution);
			mWaitingForBGStripInsert = savedInstanceState.getBoolean(
					"mWaitingForBGStripInsert", true);

			Logger.log(Level.INFO, TAG, "mIsCtrSolution=" + mIsCtrSolution);
			Logger.log(Level.INFO, TAG, "mWaitingForBGStripInsert="
					+ mWaitingForBGStripInsert);

			if (!mWaitingForBGStripInsert) {
				mInstrTxtView
						.setText(getResources()
								.getText(
										mIsCtrSolution ? R.string.instruction_ctl_sol_drop_blood
												: R.string.instruction_drop_blood));
				mBgStripImg
						.setImageResource(mIsCtrSolution ? R.drawable.ic_blooddrop_with_solution
								: R.drawable.ic_blooddrop);
			}
		}

		// simulation code start

		// try { Thread.sleep(4000); } catch (InterruptedException e) {
		// e.printStackTrace(); } BgData bgData =
		// BgData.newBuilder().setBgReading(200)
		// .setBgReadingType(BgReadingType.RANDOM).build(); Response response =
		// Response.newBuilder().setResponseType( ResponseType.SRD).
		// setMeasurementType(Measurement.BG) .setBgData(bgData).build();
		// dataReceived(response);

		// simulator code end

	}

	/**
	 * Listener to detect incoming calls.
	 */
	private PhoneStateListener phoneStateListener = new PhoneStateListener() {
		public void onCallStateChanged(int state, String incomingNumber) {
			if (state == TelephonyManager.CALL_STATE_RINGING) {
				Logger.log(Level.DEBUG, TAG, "CALL_STATE_RINGING()");
				mAppCrl.sendCancelMsg(Measurement.BG);
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	};

	/**
	 * get the error text message for Blood sugar measurements.
	 *
	 * @param bgErrorCode
	 *            the bg error code
	 * @return String
	 */
	private String getBgErrorText(final int bgErrorCode) {
		int bgErrStr = R.string.bg_normal_error;

		if (bgErrorCode == ResErrorCode.MES_TIMEOUT) {
			bgErrStr = mWaitingForBGStripInsert ? R.string.bg_timeout
					: R.string.bg_timeout_tryagain;
		} else if (bgErrorCode == ResErrorCode.MES_BG_SELF_CHECK_ERROR) {
			bgErrStr = R.string.bg_self_check_err;
		} else if (bgErrorCode == ResErrorCode.MES_BG_STRIP_REMOVED_TOO_SOON) {
			bgErrStr = R.string.bg_strip_removed_too_soon;
		} else if (bgErrorCode == ResErrorCode.MES_BG_BLOOD_APPLIED_TOO_SOON) {
			bgErrStr = R.string.bg_blood_applied_too_soon;
		} else if (bgErrorCode == ResErrorCode.MES_BG_STRIP_CONTAMINATED) {
			bgErrStr = R.string.bg_strip_contaminated;
		} else if (bgErrorCode == ResErrorCode.MES_BG_NOT_ENOUGH_BLOOD) {
			bgErrStr = R.string.bg_not_enough_blood;
		} else if (bgErrorCode == ResErrorCode.MES_BG_BATTERY_LOW) {
			bgErrStr = R.string.bg_battery_low;
		} else if (bgErrorCode == ResErrorCode.MES_BG_WRONG_STRIP_TYPE) {
			bgErrStr = R.string.bg_wrong_strip_type;
		} else if (bgErrorCode == ResErrorCode.MES_BG_HIGH_AMBIENT_TEMPERATURE) {
			bgErrStr = R.string.bg_high_ambient_temparature;
		} else if (bgErrorCode == ResErrorCode.MES_BG_LOW_AMBIENT_TEMPERATURE) {
			bgErrStr = R.string.bg_low_ambient_temparature;
		} else if (bgErrorCode == ResErrorCode.MES_BG_GLUCOSE_HIGH) {
			bgErrStr = R.string.bg_glucose_high;
			return String
					.format(get(bgErrStr), mAppModel.getSupportContactNo());
		} else if (bgErrorCode == ResErrorCode.MES_BG_GLUCOSE_LOW) {
			bgErrStr = R.string.bg_glucose_low;
			return String
					.format(get(bgErrStr), mAppModel.getSupportContactNo());
		}
		return get(bgErrStr);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.lppbpl.android.userapp.AppBaseActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeMessages(Constants.CANCEL_TIMEOUT);
		if (null != mTelephonyMgr) {
			mTelephonyMgr.listen(phoneStateListener,
					PhoneStateListener.LISTEN_NONE);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Logger.log(Level.INFO, TAG, "onSaveInstanceState");
		boolean mIsCtrSolution = getIntent().getBooleanExtra("calibration",
				false);
		final SharedPreferences pinPref = getSharedPreferences(
				Constants.CONTROL_SOLUTION, MODE_PRIVATE);
		mIsCtrSolution = !mIsCtrSolution ? pinPref.getBoolean(
				Constants.CONTROL_SOLUTION, false) : mIsCtrSolution;
		outState.putBoolean("calibration", mIsCtrSolution);
		outState.putBoolean("mWaitingForBGStripInsert",
				mWaitingForBGStripInsert);

		Logger.log(Level.INFO, TAG, "mIsCtrSolution=" + mIsCtrSolution);
		Logger.log(Level.INFO, TAG, "mWaitingForBGStripInsert="
				+ mWaitingForBGStripInsert);
	}

	/**
	 * Handler will handle the data received from device and informs the user
	 * for the errors occurred while blood sugar measurements.
	 */
	final private MyHandler mHandler = new MyHandler(this);

	/**
	 * The Class MyHandler.
	 */
	private static class MyHandler extends Handler {

		/** The m activity. */
		private final WeakReference<BloodSugarActivity> mActivity;

		/**
		 * Constructor for MyHandler.
		 *
		 * @param activity
		 *            BloodSugarActivity
		 */
		private MyHandler(BloodSugarActivity activity) {
			mActivity = new WeakReference<BloodSugarActivity>(activity);
		}

		/**
		 * Method handleMessage.
		 *
		 * @param msg
		 *            android.os.Message
		 */
		public void handleMessage(android.os.Message msg) {
			final BloodSugarActivity parent = mActivity.get();
			if (msg.what == Constants.DISPLAY_COMMAND) {
				final Response bgValueResponse = parent.mAppModel
						.getPendingRecord().getResponse();
				final double bgValue = bgValueResponse.getBgData()
						.getBgReading();
				// Below code is to handle negative value.
				if (bgValue >= Constants.BG_MIN_VALUE
						&& bgValue <= Constants.BG_MAX_VALUE) {
					final Intent bgReading = new Intent(
							parent.getApplicationContext(),
							BgReadingTypeListActivity.class);
					parent.startActivity(bgReading);
					parent.finish();
				} else {
					boolean bgLow = bgValue < Constants.BG_MIN_VALUE;
					Logger.log(Level.INFO, TAG, "Invalid bg value =" + bgValue);
					parent.mDialogType = Constants.SHOW_BG_INVALID_DATA_DIALOG;
					parent.showAlertDialog(R.drawable.ic_dialog_error,
							parent.get(R.string.error),
							parent.get(bgLow ? R.string.bg_reading_low_error : R.string.bg_reading_high_error),
							parent.get(R.string.OK), null, false);
				}

			} else if (msg.what == Constants.RECEIVED_BG_STEP_INFO) {
				if (Logger.isDebugEnabled()) {
					Logger.log(Level.DEBUG, TAG, "RECEIVED_BG_STEP_INFO");
				}
				final Response response = parent.mAppModel.getStepResponse();
				if (response.getResponseType() == ResponseType.STT
						&& response.getSttCode() == ResStatusCode.STT_MES_BG_STRIP_INSERTED) {
					parent.mWaitingForBGStripInsert = false;

					boolean mIsCtrSolution = parent.getIntent()
							.getBooleanExtra("calibration", false);
					final SharedPreferences pinPref = parent
							.getSharedPreferences(Constants.CONTROL_SOLUTION,
									MODE_PRIVATE);
					mIsCtrSolution = !mIsCtrSolution ? pinPref.getBoolean(
							Constants.CONTROL_SOLUTION, false) : mIsCtrSolution;

					parent.mInstrTxtView
							.setText(parent
									.getResources()
									.getText(
											mIsCtrSolution ? R.string.instruction_ctl_sol_drop_blood
													: R.string.instruction_drop_blood));
					parent.mBgStripImg
							.setImageResource(mIsCtrSolution ? R.drawable.ic_blooddrop_with_solution
									: R.drawable.ic_blooddrop);
				}
			} else if (msg.what == Constants.RECEIVED_DEVICE_DATA_ERR) {
				final Response response = parent.mAppModel.getStepResponse();
				int errCode = -1;
				if (response.getResponseType() == ResponseType.ERR) {
					errCode = response.getErrCode();
				}
				final String bgErrStr = parent.getBgErrorText(errCode);
				parent.mDialogType = Constants.SHOW_INFO_DIALOGE;
				parent.showAlertDialog(R.drawable.ic_dialog_error,
						parent.get(R.string.error), bgErrStr,
						parent.get(R.string.OK), null, false);
			} else if (msg.what == Constants.CANCEL_TIMEOUT) {
				parent.finish();

			}
		}
	};

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onBackPressed()
	 */
	public void onBackPressed() {
		mDialogType = Constants.SHOW_CANCEL_DIALOGE;
		showAlertDialog(R.drawable.ic_dialog_error, get(R.string.information),
				get(R.string.cancel_measurement), get(R.string.yes),
				get(R.string.no), false);
	};

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.lppbpl.android.userapp.AppBaseActivity#onPositiveButtonClick()
	 */
	public void onPositiveButtonClick() {
		super.onPositiveButtonClick();
		if (mDialogType == Constants.SHOW_INFO_DIALOGE) {
			final Response response = Response.newBuilder()
					.setResponseType(ResponseType.STT)
					.setSttCode(ResStatusCode.STT_ERROR_CLEARED).build();

			final SFMessaging messaging = SFMessaging.newBuilder()
					.setSfMsgType(MessageType.ResponseMsg)
					.setSfResponseMsg(response).build();

			sendCommand(messaging);
			mHandler.sendEmptyMessageDelayed(Constants.CANCEL_TIMEOUT,
					Constants.CANCEL_TIMEOUT_DELAY);
		} else if (mDialogType == Constants.SHOW_CANCEL_DIALOGE) {
			mAppCrl.sendCancelMsg(Measurement.BG);
			mHandler.sendEmptyMessageDelayed(Constants.CANCEL_TIMEOUT,
					Constants.CANCEL_TIMEOUT_DELAY);
		} else if (mDialogType == Constants.SHOW_BG_INVALID_DATA_DIALOG) {
			finish();
		} else {
			mDialogType = Constants.SHOW_DIALOGE_NONE;
		}
	};

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.lppbpl.android.userapp.AppBaseActivity#onNegativeButtonClick()
	 */
	public void onNegativeButtonClick() {
		if (mDialogType != Constants.SHOW_CANCEL_DIALOGE) {
			finish();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.lppbpl.android.userapp.AppBaseActivity#dataReceived(com.lppbpl
	 * .Response)
	 */
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
			Logger.log(Level.INFO, TAG, "Bloog Sugar Response received"
					+ response.toString());
		}

		if (response.getResponseType() == ResponseType.STT
				&& response.getSttCode() == ResStatusCode.STT_MES_BG_STRIP_INSERTED) {
			mAppModel.setStepResponse(response);
			mHandler.sendEmptyMessage(Constants.RECEIVED_BG_STEP_INFO);
		} else if (response.getResponseType() == ResponseType.SRD) {
			final PendingRecord pendingRec = mAppModel.getPendingRecord();
			if (null != pendingRec) {
				pendingRec.setResponse(response);
				mHandler.sendEmptyMessage(Constants.DISPLAY_COMMAND);
			}
		} else if (response.getResponseType() == ResponseType.ERR) {
			if (response.hasErrCode()) {
				mAppModel.setStepResponse(response);
				mHandler.sendEmptyMessage(Constants.RECEIVED_DEVICE_DATA_ERR);
			}
		} else if (response.getResponseType() == ResponseType.ACK) {
			Logger.log(Level.INFO, TAG, "ResponseType.ACK received");
			finish();
		}
	}
}
