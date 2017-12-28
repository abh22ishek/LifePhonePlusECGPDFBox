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
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.logging.Level;
import com.logging.Logger;
import com.lpp.xmldata.ConvertTexttoXml;
import com.lppbpl.EcgMultipleLead;
import com.lppbpl.Measure;
import com.lppbpl.Measurement;
import com.lppbpl.Response;
import com.lppbpl.Response.ResErrorCode;
import com.lppbpl.Response.ResStatusCode;
import com.lppbpl.ResponseType;
import com.lppbpl.SFMessaging;
import com.lppbpl.SFMessaging.MessageType;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.db.DiagnosisMsgDb;
import com.lppbpl.android.userapp.model.PendingRecord;

import java.lang.ref.WeakReference;
import java.util.Vector;

// TODO: Auto-generated Javadoc
/**
 * This activity measures the ECG.
 *
 */
public class ECGActivity extends AppBaseActivity {
	/* Max ECG Steps */
	/** The Constant ECG_MEASUREMENT_NOT_STARTED. */
	private static final int ECG_MEASUREMENT_NOT_STARTED = 0;

	/** The Constant ECG_STEP_1. */
	private static final int ECG_STEP_ONE = 1;

	/** The Constant ECG_STEP_2. */
	private static final int ECG_STEP_TWO = 2;

	/** The Constant ECG_STEP_3. */
	private static final int ECG_STEP_THREE = 3;

	/** The Constant ECG_STEP_4. */
	private static final int ECG_STEP_FOUR = 4;

	/* Different states of each ECG Lead. */
	/** The Constant STEP_NOT_STARTED. */
	private static final int STEP_NOT_STARTED = 1;

	/** The Constant STEP_STARTED. */
	private static final int STEP_STARTED = 2;

	/** The Constant STEP_COMPLETED. */
	private static final int STEP_COMPLETED = 3;

	/** The m current step state. */
	private int mCurrentStepState = STEP_NOT_STARTED;

	/** The m current step. */
	private int mCurrentStep = ECG_MEASUREMENT_NOT_STARTED;

	/** The Constant TAG. */
	private final static String TAG = ECGActivity.class.getSimpleName();

	/** The m steps. */
	private Button[] mSteps;

	/** The m image. */
	private ImageView mImage;

	/** The m step text. */
	private TextView mStepText;

	/** The m is cancel ack. */
	private boolean mIsCancelAck = false;

	/** The m telephony mgr. */
	private TelephonyManager mTelephonyMgr;

	/** The message count. */
	private int messageCount;

	/**
	 * Method onCreate.
	 *
	 * @param savedInstanceState
	 *            Bundle
	 */
	int count=0;

	// stringbuilder to store all ecg lead in a buffer then later store it into a file
	public static StringBuilder sb=null;
	Vector<EcgMultipleLead> v;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.log(Level.DEBUG, TAG, "OnCreate");
		setRemeasureView();

		setCustomTitle(R.string.title_ecg);
		setCustomHeaderImage(R.drawable.ic_title_ecg);
		mIsCancelAck = false;
		count=0;
		/*
		 * Create TelephonyManager to handle the incoming calls while measuring
		 * the Ecg.
		 */
		mTelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		if (null != mTelephonyMgr) {
			mTelephonyMgr.listen(phoneStateListener,
					PhoneStateListener.LISTEN_CALL_STATE);
		}

		if (null != savedInstanceState) {
			mCurrentStep = savedInstanceState.getInt("current_step", ECG_MEASUREMENT_NOT_STARTED);
			mCurrentStepState = savedInstanceState.getInt("current_step_state", STEP_NOT_STARTED);

			Logger.log(Level.DEBUG, TAG, "mCurrentStep=" + mCurrentStep + ",mCurrentStepState=" + mCurrentStepState);
		}

		// to handle feedback messages while ecg measurement running
		messageCount = DiagnosisMsgDb.getInstance().numOfRecords();

		sb=new StringBuilder();


	}

	/**
	 * Listener to detect incoming calls.
	 */
	private PhoneStateListener phoneStateListener = new PhoneStateListener() {
		public void onCallStateChanged(int state, String incomingNumber) {
			if (state == TelephonyManager.CALL_STATE_RINGING) {
				Logger.log(Level.DEBUG, TAG, "CALL_STATE_RINGING()");
				mIsCancelAck = true;
				mAppCrl.sendCancelMsg(Measurement.ECG);
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	};

	/*
	 * (non-Javadoc)
	 *
	 * @see com.lppbpl.android.userapp.AppBaseActivity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		Logger.log(Level.DEBUG, TAG, "OnStart");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		Logger.log(Level.DEBUG, TAG, "OnResume");
		updateView();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		super.onRestart();
		Logger.log(Level.DEBUG, TAG, "onRestart()");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.lppbpl.android.userapp.AppBaseActivity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		Logger.log(Level.DEBUG, TAG, "onStop()");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.lppbpl.android.userapp.AppBaseActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Logger.log(Level.DEBUG, TAG, "OnDestroy");
		mHandler.removeMessages(Constants.CANCEL_TIMEOUT);
		// Remove the phone listener
		if (null != mTelephonyMgr) {
			mTelephonyMgr.listen(phoneStateListener,
					PhoneStateListener.LISTEN_NONE);
		}
		// to handle feedback messages while ecg measurement running
		if (messageCount < DiagnosisMsgDb.getInstance().numOfRecords()) {
			SMSReceiver.launchAlertActivity(this, "Feedback", true, null);
		}
	}

	/**
	 * Update the Ecg step view and if measurement completed then calls draws
	 * Ecg Graph.
	 */
	private void updateView() {
		Logger.log(Level.DEBUG, TAG, "updateView() currentStep=" + mCurrentStep);
		if (mCurrentStep > Constants.TOTAL_ECG_MEASURE_STEPS) {
			// After the measurement completed, Reset the step info
			mCurrentStepState = STEP_NOT_STARTED;
			mCurrentStep = ECG_MEASUREMENT_NOT_STARTED;
			final Intent showGraph = new Intent(this, EcgGraphActivity.class);
			startActivity(showGraph);
			finish();
			return;

		} else if (mCurrentStep == ECG_STEP_ONE || mCurrentStep == ECG_STEP_TWO
				|| mCurrentStep == ECG_STEP_THREE
				|| mCurrentStep == ECG_STEP_FOUR) {
			updateForm(mCurrentStep, mCurrentStepState);
		} else if (mCurrentStep == ECG_MEASUREMENT_NOT_STARTED) {
			sendMeasureRequest();
			mCurrentStep = ECG_STEP_ONE;
			updateForm(mCurrentStep, mCurrentStepState);
		}
	}

	/**
	 * Method onSaveInstanceState.
	 *
	 * @param outState
	 *            Bundle
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Logger.log(Level.DEBUG, TAG, "onSaveInstanceState");
		Logger.log(Level.DEBUG, TAG, "mCurrentStep=" + mCurrentStep
				+ ",mCurrentStepState=" + mCurrentStepState);
		outState.putInt("current_step", mCurrentStep);
		outState.putInt("current_step_state", mCurrentStepState);
	}

	/**
	 * Method onRestoreInstanceState.
	 *
	 * @param savedInstanceState
	 *            Bundle
	 */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		Logger.log(Level.DEBUG, TAG, "onRestoreInstanceState");
	}

	/**
	 * Send Measure request to Device.
	 */
	private void sendMeasureRequest() {
		Logger.log(Level.DEBUG, TAG, "sendMeasureRequest()");
		final Measure measure = Measure.newBuilder().setMeasurementType(Measurement.ECG).setDuration(10).build();

		final SFMessaging ecgMeasureMessaging = SFMessaging.newBuilder().setSfMsgType(MessageType.MeasureMsg).setSfMeasureMsg(measure).build();
		Logger.log(Level.DEBUG, TAG, ecgMeasureMessaging.toString());
		sendCommand(ecgMeasureMessaging);

		// simulation code start

		// Thread t = new Thread(new Runnable() {
		// public void run() {
		// int[][] leadNums = {
		// {
		// 1, 2, 3
		// }, {
		// 4
		// }, {
		// 6
		// }, {
		// 8
		// }
		// };
		// Vector<Integer> ecgStrip = new Vector<Integer>();
		// java.util.Random randomInt = new java.util.Random();
		// for (int j = 0; j < 2500; j++) {
		// ecgStrip.addElement(new Integer(500 - randomInt.nextInt(1000)));
		// }
		// for (int i = 0; i < 4; i++) {
		// try {
		// Thread.sleep(3000);
		// } catch (Exception e) {
		// }
		// Response respEcgMeasureStart = Response.newBuilder()
		// .setResponseType(ResponseType.STT).setMeasurementType(Measurement.ECG)
		// .setSttCode(ResStatusCode.STT_MES_STEP_STARTED).build();
		// dataReceived(respEcgMeasureStart); // leads for step sending
		// // started
		// for (int k = 0; k < leadNums[i].length; k++) {
		// try {
		// Thread.sleep(10000);
		// } catch (Exception e) {
		// }
		// EcgMultipleLead.Builder multLeadBuilder =
		// EcgMultipleLead.newBuilder();
		// multLeadBuilder.setLead(leadNums[i][k]);
		// multLeadBuilder.setEcgStrip(ecgStrip);
		// EcgData.Builder ecgDataBuilder =
		// EcgData.newBuilder().addElementMulLead(
		// multLeadBuilder.build());
		// ecgDataBuilder.setDuration(10);
		// HrData hrData = HrData.newBuilder().setHeartRate(70).build();
		// Response response =
		// Response.newBuilder().setResponseType(ResponseType.SRD)
		// .setEcgData(ecgDataBuilder.build())
		// .setMeasurementType(Measurement.ECG).setHrData(hrData).build();
		// dataReceived(response);
		// }
		// // leads for step sending done
		// Response respEcgMeasureStop = Response.newBuilder()
		// .setResponseType(ResponseType.STT).setMeasurementType(Measurement.ECG)
		// .setSttCode(ResStatusCode.STT_MES_STEP_COMPLETED).build();
		// dataReceived(respEcgMeasureStop);
		// }
		// }
		// });
		// t.start();

		// simulation code end

	}

	/**
	 * Updates the Ecg step images while measuring the ecg.
	 *
	 * @param nextState
	 *            the next state
	 * @param currentStepState
	 *            the current step state
	 */
	public void updateForm(int nextState, int currentStepState) {
		if (nextState == ECG_STEP_ONE || nextState == ECG_STEP_TWO
				|| nextState == ECG_STEP_THREE || nextState == ECG_STEP_FOUR) {
			updateImageComponent(nextState, currentStepState);
		}
	}

	/**
	 * Updates the Ecg step images while measuring the ecg.
	 *
	 * @param nextState
	 *            the next state
	 * @param currentStepState
	 *            the current step state
	 */
	private void updateImageComponent(int nextState, int currentStepState) {

		String startStepMsg = null;
		String measuringStepMsg = null;
		for (int i = 1; i <= Constants.TOTAL_ECG_MEASURE_STEPS; i++) {

			if (nextState == i) {// current state //started
				if (currentStepState == STEP_NOT_STARTED) {
					startStepMsg = get(R.string.ecg_start_step_msg);
					if (1 == i) {
						mSteps[i - 1]
								.setBackgroundResource(R.drawable.ic_ecg_tab_left_active);
						mImage.setImageResource(R.drawable.ic_ecg_step1);
					} else if (2 == i) {
						mSteps[i - 1]
								.setBackgroundResource(R.drawable.ic_ecg_tab_center_active);
						mImage.setImageResource(R.drawable.ic_ecg_step2);
					} else if (3 == i) {
						mSteps[i - 1]
								.setBackgroundResource(R.drawable.ic_ecg_tab_center_active);
						mImage.setImageResource(R.drawable.ic_ecg_step3);
					} else if (4 == i) {
						mSteps[i - 1]
								.setBackgroundResource(R.drawable.ic_ecg_tab_right_active);
						mImage.setImageResource(R.drawable.ic_ecg_step4);
					}
					mSteps[i - 1].setTextColor(getResources().getColor(
							R.color.reading_color));
					mStepText.setText(String.format(startStepMsg, i));
				} else if (currentStepState == STEP_STARTED) {
					measuringStepMsg = get(R.string.ecg_measure_step_msg);
					mStepText.setText(String.format(measuringStepMsg, i));
					mSteps[i - 1].setTextColor(getResources().getColor(
							R.color.reading_color));
				}
			} else if (i < nextState) {// (currentStepState == STEP_COMPLETED) {
				// //completed
				if (1 == i) {
					mSteps[i - 1]
							.setBackgroundResource(R.drawable.ic_ecg_tab_left_completed);
				} else if (2 == i) {
					mSteps[i - 1]
							.setBackgroundResource(R.drawable.ic_ecg_tab_center_complete);
				} else if (3 == i) {
					mSteps[i - 1]
							.setBackgroundResource(R.drawable.ic_ecg_tab_center_complete);
				} else if (4 == i) {
					mSteps[i - 1]
							.setBackgroundResource(R.drawable.ic_ecg_tab_right_complete);
				}
				mSteps[i - 1].setTextColor(getResources().getColor(
						R.color.heading_color));
			}
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
		if (mDialogType == Constants.SHOW_RE_MEASURE_DIALOGE) {
			for (int i = 0; i < mAppModel.getEcgLeadCount(); i++) {
				mAppModel.getPendingRecord().clearECGResponse(i);
				if (Logger.isInfoEnabled()) {
					Logger.log(Level.INFO, TAG, "ECG Response (" + i
							+ ")cleared");
				}
			}
			// reameasure from step 1
			mCurrentStep = ECG_MEASUREMENT_NOT_STARTED;
			mCurrentStepState = STEP_NOT_STARTED;
			setRemeasureView();
			updateView();
		} else if (mDialogType == Constants.SHOW_CANCEL_DIALOGE) {
			mIsCancelAck = true;
			mAppCrl.sendCancelMsg(Measurement.ECG);
			mHandler.sendEmptyMessageDelayed(Constants.CANCEL_TIMEOUT,
					Constants.CANCEL_TIMEOUT_DELAY);
		} else {
			updateView();
		}
	}

	/**
	 * To set the view on oncreate and when get artifact.
	 */
	private void setRemeasureView() {
		setContentView(R.layout.ecgactivity);
		// Ecg steps
		mSteps = new Button[4];
		mSteps[0] = (Button) findViewById(R.id.btn_tab_1);
		mSteps[1] = (Button) findViewById(R.id.btn_tab_2);
		mSteps[2] = (Button) findViewById(R.id.btn_tab_3);
		mSteps[3] = (Button) findViewById(R.id.btn_tab_4);

		mImage = (ImageView) findViewById(R.id.img_ecg_step_info);
		mStepText = (TextView) findViewById(R.id.txt_ecg_step_info);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.lppbpl.android.userapp.AppBaseActivity#onNegativeButtonClick()
	 */
	protected void onNegativeButtonClick() {

		if (mDialogType == Constants.SHOW_RE_MEASURE_DIALOGE) {
			mCurrentStepState = STEP_NOT_STARTED;
			mCurrentStep = ECG_MEASUREMENT_NOT_STARTED;
			final Intent showGraph = new Intent(this, EcgGraphActivity.class);
			startActivity(showGraph);
			finish();
		}
	}

	/**
	 * Handler handles the ecg data and errors from the device.
	 */
	final private MyHandler mHandler = new MyHandler(this);

	/**
	 * The Class MyHandler.
	 */
	private static class MyHandler extends Handler {

		/** The m activity. */
		private final WeakReference<ECGActivity> mActivity;

		/**
		 * Constructor for MyHandler.
		 *
		 * @param activity
		 *            ECGActivity
		 */
		private MyHandler(ECGActivity activity) {
			mActivity = new WeakReference<ECGActivity>(activity);
		}

		/**
		 * Method handleMessage.
		 *
		 * @param msg
		 *            android.os.Message
		 */
		public void handleMessage(android.os.Message msg) {

			final ECGActivity parent = mActivity.get();
			Logger.log(Level.DEBUG, TAG, "msg.what=" + msg.what);
			if (msg.what == Constants.RECEIVED_ECG_STEP_INFO) {
				Logger.log(Level.DEBUG, TAG, "RECEIVED_ECG_STEP_INFO");
				final Response response = parent.mAppModel.getStepResponse();
				if (response.getResponseType() == ResponseType.STT) {
					Logger.log(Level.DEBUG, TAG, "ResponseType.STT");
					if (response.getSttCode() == ResStatusCode.STT_MES_STEP_STARTED) {
						Logger.log(Level.DEBUG, TAG,
								"ResStatusCode.STT_MES_STEP_STARTED");
						parent.mCurrentStepState = STEP_STARTED;
						parent.updateView();// move to the next ECG Lead.
					} else if (response.getSttCode() == ResStatusCode.STT_MES_STEP_COMPLETED) {
						Logger.log(Level.DEBUG, TAG,
								"ResStatusCode.STT_MES_STEP_COMPLETED");
						parent.mCurrentStepState = STEP_COMPLETED;

						if (parent.mPinModel.isSingleStepECG()) {
							parent.mCurrentStep = ECG_STEP_FOUR + 1; // that
							// means
							// step 4
							// completed
						} else {
							// do any logging here for the completed step
							parent.mCurrentStep++;
						}
						parent.mCurrentStepState = STEP_NOT_STARTED;
						parent.updateView();// move to the next ECG Lead.

					}
				} else {
					Logger.log(Level.DEBUG, TAG,
							"else block RECEIVED_ECG_STEP_INFO");
				}

			} else if (msg.what == Constants.RECEIVED_DEVICE_DATA_ERR) {
				Logger.log(Level.DEBUG, TAG,
						"Constants.RECEIVED_DEVICE_DATA_ERR");
				parent.mDialogType = Constants.SHOW_REPEAT_STEP_DIALOGE;
				parent.showAlertDialog(R.drawable.ic_dialog_error,
						parent.get(R.string.error), parent.get(R.string.repeat_step)
								+ parent.mCurrentStep, parent.get(R.string.OK),
						null, false);

			} else if (msg.what == Constants.RECEIVED_ECG_DATA) {
				Logger.log(Level.DEBUG, TAG, "Constants.RECEIVED_ECG_DATA");
				final Response ackResponse = Response.newBuilder()
						.setResponseType(ResponseType.ACK)
						.setMeasurementType(Measurement.ECG).build();
				final SFMessaging ackMessaging = SFMessaging.newBuilder()
						.setSfMsgType(MessageType.ResponseMsg)
						.setSfResponseMsg(ackResponse).build();
				parent.sendCommand(ackMessaging);

			} else if (msg.what == Constants.RECEIVED_ECG_QUALITY_POOR) {
				Logger.log(Level.DEBUG, TAG,
						"Constants.RECEIVED_ECG_QUALITY_POOR");
				parent.mDialogType = Constants.SHOW_RE_MEASURE_DIALOGE;
				parent.showAlertDialog(R.drawable.ic_dialog_error,
						parent.get(R.string.error),
						parent.get(R.string.remeasure_ecg),
						parent.get(R.string.re_measure), parent.get(R.string.continue_label), false);

			} else if (msg.what == Constants.BT_CONNECTION_LOST) {
				Logger.log(Level.DEBUG, TAG, "Constants.BT_CONNECTION_LOST");

			} else if (msg.what == Constants.CANCEL_TIMEOUT) {
				parent.finish();

			} else {
			}
		};
	};

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		mDialogType = Constants.SHOW_CANCEL_DIALOGE;
		showAlertDialog(R.drawable.ic_dialog_error, get(R.string.information),
				get(R.string.cancel_measurement), get(R.string.yes),
				get(R.string.no), false);
		// super.onBackPressed();
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
			Logger.log(Level.INFO, TAG, "ECG Response received");
		}
		// Received the ECG canceled response
		if (response.getResponseType() == ResponseType.ACK) {
			if (Logger.isInfoEnabled()) {
				Logger.log(Level.INFO, TAG, "ResponseType.ACK received");
			}
			if (mIsCancelAck) {
				finish();
			}
		} else if (response.getResponseType() == ResponseType.STT
				&& response.getSttCode() == ResStatusCode.STT_MES_STEP_STARTED) {
			if (Logger.isInfoEnabled()) {
				Logger.log(Level.INFO, TAG, "STT_MES_STEP_STARTED");
			}
			mAppModel.setStepResponse(response);
			mHandler.sendEmptyMessage(Constants.RECEIVED_ECG_STEP_INFO);
		} else if (response.getResponseType() == ResponseType.ERR) {
			if (response.getErrCode() == ResErrorCode.MES_ECG_QUALITY_POOR) {
				mHandler.sendEmptyMessage(Constants.RECEIVED_ECG_QUALITY_POOR);
			} else if (response.hasErrCode()) {
				mHandler.sendEmptyMessage(Constants.RECEIVED_DEVICE_DATA_ERR);
			}
		} else if (response.getResponseType() == ResponseType.SRD) {
			if (Logger.isInfoEnabled()) {
				Logger.log(Level.INFO, TAG, "Received the leads from accessory");
			}
			final PendingRecord pendingRec = mAppModel.getPendingRecord();
			if (null != pendingRec) {
				pendingRec.addEcgResponse(response);
				if (Logger.isInfoEnabled()) {
					count++;
					Logger.log(Level.INFO, TAG, "Ecg response ---> " + response);
					ConvertTexttoXml.writeToxmlEcg(response, v, ECGActivity.this, count);
					Logger.log(Level.INFO,TAG,"Count Value-->"+count);
					if (response.hasEcgData()) {
						@SuppressWarnings("unchecked")
						final Vector<EcgMultipleLead> v = response.getEcgData()
								.getMulLead();
						for (int i = 0; i < v.size(); i++) {
							Logger.log(Level.INFO, TAG, "Ecg Lead :"
									+ v.get(i).getLead());
							Logger.log(Level.INFO, TAG,
									"Ecg step no:" + v.get(i).getStepNum());
						}
					}
				}

				// count ++;
				// if (count <= 2) {
				mHandler.sendEmptyMessage(Constants.RECEIVED_ECG_DATA);
				// }
				// mHandler.sendEmptyMessageDelayed(Constants.RECEIVED_ECG_DATA,
				// 3000);
			}
		} else if (response.getResponseType() == ResponseType.STT
				&& response.getSttCode() == ResStatusCode.STT_MES_STEP_COMPLETED) {
			if (Logger.isInfoEnabled()) {
				Logger.log(Level.INFO, TAG, "STT_MES_STEP_COMPLETED");
			}
			mAppModel.setStepResponse(response);
			mHandler.sendEmptyMessage(Constants.RECEIVED_ECG_STEP_INFO);
			Logger.log(Level.INFO, TAG, "STT_MES_STEP_COMPLETED returning");
		}

	}

}
