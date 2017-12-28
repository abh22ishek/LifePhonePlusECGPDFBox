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

package com.lppbpl.android.userapp.controller;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.DataLoggingResponse;
import com.lppbpl.EcgData;
import com.lppbpl.EcgMultipleLead;
import com.lppbpl.Measurement;
import com.lppbpl.Response;
import com.lppbpl.Response.ServiceRequest;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.db.PendingRecordDb;
import com.lppbpl.android.userapp.model.PendingRecord;
import com.lppbpl.android.userapp.model.SfApplicationModel;
import com.lppbpl.android.userapp.model.SfPinModel;
import com.lppbpl.android.userapp.model.SfSendModel;
import com.lppbpl.android.userapp.util.HttpUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class SfUploadManager.
 */
public final class SfUploadManager {

	/** The Constant TAG. */
	public static final String TAG = SfUploadManager.class.getName();

	/** The m app model. */
	private final SfApplicationModel mAppModel;

	// private Thread thread;
	/** The m queue. */
	private Vector<Integer> mQueue = null;

	/** The s upload controller. */
	private static SfUploadManager sUploadController = null;

	/** The m send status. */
	private int mSendStatus = Constants.SEND_STATUS_NW_ERROR;

	// private boolean quit = false;
	/** The m record db. */
	private final PendingRecordDb mRecordDb;

	/** The m http util. */
	private HttpUtil mHttpUtil = null;

	/**
	 * Instantiates a new sf upload manager.
	 */
	private SfUploadManager() {
		mAppModel = getRootController().getAppModel();
		mRecordDb = PendingRecordDb.getInstance();
		mQueue = new Vector<Integer>();
		// thread = new Thread(this);
		// thread.start();
	}

	/**
	 * Gets the single instance of SfUploadManager.
	 *
	 * @return SfUploadManager object
	 */
	public final static synchronized SfUploadManager getInstance() {
		if (sUploadController == null) {
			sUploadController = new SfUploadManager();
		}
		return sUploadController;
	}

	/**
	 * Method getRootController.
	 *
	 * @return SfApplicationController
	 */
	public final SfApplicationController getRootController() {
		return SfApplicationController.getInstance();
	}

	/**
	 * add unsaved record to db.
	 *
	 * @param pendingRecord
	 *            the pending record
	 * @param uploadMe
	 *            the upload me
	 */
	public synchronized void addPendingRecord(PendingRecord pendingRecord,
			final boolean uploadMe) {
		Logger.log(Level.DEBUG, TAG,
				"new pendingRecord id :" + pendingRecord.getId());
		pendingRecord.setUploadStatus(PendingRecord.UPLOAD_NOT_STARTED);
		// add pending record to db
		if (pendingRecord.getId() == -1) {
			final int id = mRecordDb.addRecord(pendingRecord);
			pendingRecord.setId(id);
			if (Logger.isDebugEnabled()) {
				Logger.log(Level.DEBUG, TAG, "new pendingRecord added with id "
						+ pendingRecord.getId());
			}
			// Logger.log(Level.DEBUG, TAG,
			// Measurement.getStringValue(mRecordDb.getPendingRecord(id).getMeasurementType()));
		} else {
			// updates the record
			mRecordDb.updateRecord(pendingRecord);
		}

		if (uploadMe) {
			uploadPendingRecord(pendingRecord.getId());
		}
	}

	/**
	 * Method uploadPendingRecord.
	 *
	 * @param id
	 *            int
	 */
	public void uploadPendingRecord(int id) {
		notifyPendingRecords(id);
	}

	/**
	 * Method notifyPendingRecords.
	 *
	 * @param id
	 *            int
	 */
	private void notifyPendingRecords(int id) {
		mQueue.addElement(Integer.valueOf(id));
		if (Logger.isDebugEnabled()) {
			Logger.log(Level.DEBUG, TAG, "pending record queue " + mQueue);
		}
		// queue.notify();
		mHttpUtil = new HttpUtil();
		dataUpload();
	}

	/**
	 * uploads data.
	 */
	private void dataUpload() {
		final Thread uploadThread = new Thread() {
			public void run() {
				if (mQueue != null && mQueue.size() > 0) {
					final Integer integer = mQueue.elementAt(0);
					final int recordId = integer.intValue();

					if (Logger.isDebugEnabled()) {
						Logger.log(Level.DEBUG, TAG,
								"pending record found in queue => " + recordId);
					}

					PendingRecord pendingRecord = mAppModel.getPendingRecord();
					if (pendingRecord == null
							|| recordId != pendingRecord.getId()) {
						pendingRecord = mRecordDb.getPendingRecord(recordId);
						if (pendingRecord == null) {
							Logger.log(Level.ERROR, TAG,
									"pending record retrived from RMS, pendingRecord is null");
							return;
						}

						if (Logger.isDebugEnabled()) {
							Logger.log(Level.DEBUG, TAG,
									"pending record retrived from RMS, id is="
											+ recordId);
						}
					}

					// pendingRecord.setUploadStatus(PendingRecord.UPLOAD_STARTED);
					try {
						mRecordDb.updateRecord(pendingRecord);
					} catch (IllegalStateException e) {
						e.printStackTrace();
					}
					int noOfTry = pendingRecord.getNumOfAttempt();
					int sendStatus = -1;
					do {
						noOfTry++;
						if (Logger.isDebugEnabled()) {
							Logger.log(Level.DEBUG, TAG,
									"no. of retries till now =>" + noOfTry);
						}
						pendingRecord.setNumOfAttempt(noOfTry);
						sendStatus = uploadRecord(pendingRecord);
						if (sendStatus < 0) {
							if (Logger.isDebugEnabled()) {
								Logger.log(Level.DEBUG, TAG,
										"uploaded failed pendingRecord is="
												+ pendingRecord.getId());
							}

							if (Constants.MAX_NO_OF_ATTEMPT <= noOfTry) {
								if (Logger.isDebugEnabled()) {
									Logger.log(Level.DEBUG, TAG,
											"no. of retries exceeded the limit so removing from the queue");
								}
								mQueue.removeElementAt(0);
								pendingRecord
										.setUploadStatus(PendingRecord.UPLOAD_FAILED);
								try {
									mRecordDb.updateRecord(pendingRecord);
								} catch (IllegalStateException e) {
									e.printStackTrace();
								}
								if (getRootController() != null
										&& getRootController()
												.getBluetoothDataListener() != null) {
									getRootController()
											.getBluetoothDataListener()
											.sendEmptyMessage(
													Constants.CONTINUE_COMMAND);
								}
							}
						} else {
							mRecordDb.deleteRecord(pendingRecord.getId());

							if (Logger.isDebugEnabled()) {
								Logger.log(Level.DEBUG, TAG,
										"deleted pendingRecord is="
												+ pendingRecord.getId());
							}
							if (getRootController() != null
									&& getRootController()
											.getBluetoothDataListener() != null) {
								getRootController().getBluetoothDataListener()
										.sendEmptyMessage(
												Constants.CONTINUE_COMMAND);
							}
							mQueue.removeElementAt(0);
							break;
						}
					} while (Constants.MAX_NO_OF_ATTEMPT > noOfTry);
					pendingRecord = null;
				}
			}
		};
		uploadThread.start();
	}

	/**
	 * Method parseSendStatus.
	 *
	 * @param postResponse
	 *            String
	 * @return int
	 */
	private int parseSendStatus(String postResponse) {
		int status = Constants.SEND_STATUS_NW_ERROR;

		try {
			status = Integer.parseInt(postResponse);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return status;
	}

	/**
	 * upload unsaved record to cloud.
	 *
	 * @param pendingRecord
	 *            the pending record
	 * @return int
	 */
	private synchronized int uploadRecord(PendingRecord pendingRecord) {

		Logger.log(Level.DEBUG, TAG, "uploadRecord()");

		mSendStatus = Constants.SEND_STATUS_NW_ERROR;
		SfSendModel.getInstance().setSendStatus(mSendStatus);
		final Response response = pendingRecord.getResponse();
		if (pendingRecord.isECGRecord()) {
			mSendStatus = ecgUpload(pendingRecord);
		} else {

			if (Logger.isDebugEnabled()) {
				Logger.log(Level.DEBUG, TAG, "Response being uploaded is "
						+ response);
			}

			String status = "-1000";
			final SfPinModel pinModel = getRootController().getAppModel()
					.getPinModel();
			try {
				DataLoggingResponse logResp = null;
				if (mHttpUtil != null && response != null) {
					final SortedMap<String, String> reqHeaders = mHttpUtil
							.getCommonRequestHeader();
					reqHeaders.put(HttpUtil.ACCEPT_TYPE,
							HttpUtil.PROTOBUF_ENCODING_TYPE);
					reqHeaders.put(HttpUtil.CONTENT_TYPE,
							HttpUtil.PROTOBUF_ENCODING_TYPE);

					final String URL = pinModel.getServerAddress()
							+ Constants.BG_OR_ACT_UPLOAD_URL;
					logResp = mHttpUtil.uploadMeasurementToCloud(HttpUtil.POST,
							URL, response.toByteArray(), reqHeaders);
				}
				SfSendModel.getInstance().setDataLoggingResponse(logResp);
				if (logResp != null) {
					try {
						final int logRespStatus = Integer.valueOf(
								logResp.getStatus()).intValue();
						if (logRespStatus >= 0) {
							status = String.valueOf(logResp.getMeasurementId());
						}
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
				Logger.log(Level.DEBUG, TAG, "Upload status: " + status);
				mSendStatus = parseSendStatus(status);
				SfSendModel.getInstance().setSendStatus(mSendStatus);
			} catch (Exception e) {
				e.printStackTrace();
				SfSendModel.getInstance().setSendStatus(
						Constants.SEND_STATUS_NW_ERROR);
			}
		}

		return mSendStatus;
	}

	/**
	 * upload ecg record to cloud.
	 *
	 * @param pendingRecord
	 *            the pending record
	 * @return int
	 */
	public int ecgUpload(PendingRecord pendingRecord) {
		final SfPinModel pinModel = getRootController().getAppModel()
				.getPinModel();

		if (mHttpUtil == null) {
			return Constants.SEND_STATUS_NW_ERROR;
		}

		SortedMap<String, String> reqHeaders = mHttpUtil
				.getCommonRequestHeader();
		final SortedMap<String, String> reqParams = new TreeMap<String, String>();
		reqHeaders.put(HttpUtil.ACCEPT_TYPE, HttpUtil.PLAIN_TEXT_TYPE);
		reqHeaders.put(HttpUtil.CONTENT_TYPE, HttpUtil.FORM_URLENCODED_TYPE);

		reqParams.put("userId", pinModel.getUserID());
		reqParams.put("deviceId", pinModel.getDeviceUid());

		final String requestURI = pinModel.getServerAddress()
				+ Constants.ECG_UPLOAD_START_URL;

		String measurementIdStr = null;
		int measurementId = -1;

		try {
			measurementIdStr = mHttpUtil.postDataThroughParams(HttpUtil.POST,
					requestURI, reqParams, reqHeaders);
			measurementId = Integer.parseInt(measurementIdStr);
			if (Logger.isDebugEnabled()) {
				Logger.log(Level.DEBUG, TAG, "measurementId =" + measurementId);
			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
			measurementId = -1;
			if (Logger.isDebugEnabled()) {
				Logger.log(Level.DEBUG, TAG, "-NumberFormatException-");
			}
		} catch (NullPointerException npe) {
			measurementId = -1;
			if (Logger.isDebugEnabled()) {
				Logger.log(Level.DEBUG, TAG,
						"-NullPointerException-" + npe.getMessage());
			}
		}

		// lead by lead upload through a different URL
		// send the hrData, symptoms, comment in last lead
		// lead1
		String statusStr = "-1";
		if (measurementId > -1) {
			if (getRootController() != null
					&& getRootController().getBluetoothDataListener() != null) {
				getRootController().getBluetoothDataListener()
						.sendEmptyMessage(Constants.UPDATE_ECG_UPLOAD_PROGRESS);
			}
			final int ecgLeadLength = pendingRecord.getEcgLeadCount();
			Response resp = null;
			int success = 0;
			for (int i = 0; i < ecgLeadLength; i++) {
				resp = pendingRecord.getResponse(i);
				if (resp != null) {
					success = ecgLeadUpload(measurementId, resp);
					writeLog(i + 1, success);
					if (success < 0) {
						Logger.log(Level.DEBUG, TAG,
								"Once again unable to upload. so stop the upload");
						SfSendModel.getInstance().setSendStatus(
								Constants.SEND_STATUS_NW_ERROR);
						return Constants.SEND_STATUS_NW_ERROR;
					} else {
						if (getRootController() != null
								&& getRootController()
										.getBluetoothDataListener() != null) {
							getRootController()
									.getBluetoothDataListener()
									.sendEmptyMessage(
											Constants.UPDATE_ECG_UPLOAD_PROGRESS);
						}
					}
				}
			}

			reqHeaders = mHttpUtil.getCommonRequestHeader();
			reqHeaders.put(HttpUtil.ACCEPT_TYPE,
					HttpUtil.PROTOBUF_ENCODING_TYPE);
			reqHeaders
					.put(HttpUtil.CONTENT_TYPE, HttpUtil.FORM_URLENCODED_TYPE);

			reqParams.put("measurementId", measurementIdStr);

			try {
				final String URL = pinModel.getServerAddress()
						+ Constants.ECG_UPLOAD_STOP_URL;
				final DataLoggingResponse logResp = mHttpUtil
						.postStopEcgRequestToCloud(HttpUtil.POST, URL,
								reqParams, reqHeaders);

				SfSendModel.getInstance().setDataLoggingResponse(logResp);
				if (logResp != null) {
					int logRespStatus = -1;
					try {
						logRespStatus = Integer.valueOf(logResp.getStatus())
								.intValue();
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
					if (logRespStatus >= 0) {
						statusStr = String.valueOf(logResp.getMeasurementId());
					}
					if (getRootController() != null
							&& getRootController().getBluetoothDataListener() != null) {
						getRootController().getBluetoothDataListener()
								.sendEmptyMessage(
										Constants.UPDATE_ECG_UPLOAD_PROGRESS);
					}
				}
				mSendStatus = parseSendStatus(statusStr);
				SfSendModel.getInstance().setSendStatus(mSendStatus);

			} catch (NumberFormatException e) {
				e.printStackTrace();
				if (Logger.isDebugEnabled()) {
					Logger.log(Level.DEBUG, TAG,
							"NumberFormatException :sendStatus " + mSendStatus);
				}
			} catch (NullPointerException npe) {
				if (Logger.isDebugEnabled()) {
					Logger.log(Level.DEBUG, TAG,
							"-NullPointerException-" + npe.getMessage());
				}
			}

			if (Logger.isDebugEnabled()) {
				Logger.log(Level.DEBUG, TAG,
						"Measurement Id after stop ecg upload is "
								+ mSendStatus);
			}
		}

		return mSendStatus;
	}

	/**
	 * Method ecgLeadUpload.
	 *
	 * @param measurementId
	 *            int
	 * @param response
	 *            Response
	 * @return int
	 */
	@SuppressWarnings("rawtypes")
	private int ecgLeadUpload(int measurementId, final Response response) {
		final SfPinModel pinModel = getRootController().getAppModel()
				.getPinModel();
		final EcgData.Builder ecgDataBuilder = EcgData.newBuilder();

		// UserContext userContext =
		// getRootController().getAppModel().buildUserContext();
		ecgDataBuilder.setMeasurementId(measurementId).addElementMulLead(
				(EcgMultipleLead) response.getEcgData().getMulLead()
						.elementAt(0));

		ecgDataBuilder.setDuration(response.getEcgData().getDuration());

		if (response.getEcgData().hasAnnotationTxt()) {
			ecgDataBuilder.setAnnotationTxt(response.getEcgData()
					.getAnnotationTxt());
		}

		final Vector ecgSymptoms = response.getEcgData().getEcgSymptoms();
		if (ecgSymptoms != null && ecgSymptoms.size() > 0) {
			ecgDataBuilder.setEcgSymptoms(ecgSymptoms);
		}

		final Response.Builder ecgRespBuilder = Response.newBuilder();

		if (response.hasHrData()) {
			ecgRespBuilder.setHrData(response.getHrData());
			Logger.log(Level.DEBUG, TAG, "Hr Data : " + response.getHrData());
		}

		final Response ecgResponse = ecgRespBuilder
				.setResponseType(response.getResponseType())
				.setServiceReq(ServiceRequest.DataLogging)
				.setMeasurementType(Measurement.ECG)
				.setEcgData(ecgDataBuilder.build())
				// .setUserContext(userContext)
				.build();

		if (Logger.isDebugEnabled()) {
			Logger.log(Level.DEBUG, TAG,
					"ECG lead upload size: " + ecgResponse.computeSize());
		}

		mSendStatus = -1;

		try {
			String status = "-1000";

			if (mHttpUtil != null) {
				final SortedMap<String, String> reqHeaders = mHttpUtil
						.getCommonRequestHeader();
				reqHeaders.put(HttpUtil.ACCEPT_TYPE, HttpUtil.PLAIN_TEXT_TYPE);
				reqHeaders.put(HttpUtil.CONTENT_TYPE,
						HttpUtil.PROTOBUF_ENCODING_TYPE);

				final String URL = pinModel.getServerAddress()
						+ Constants.ECG_UPLOAD_URL;

				status = mHttpUtil.postData(HttpUtil.POST, URL,
						ecgResponse.toByteArray(), reqHeaders);
			}

			Logger.log(Level.DEBUG, TAG, "Upload status: " + status);
			mSendStatus = parseSendStatus(status);
			SfSendModel.getInstance().setSendStatus(mSendStatus);
		} catch (Exception e) {
			e.printStackTrace();
			if (Logger.isErrorEnabled()) {
				Logger.log(Level.ERROR, TAG, "ECG upload failed ");
			}
			SfSendModel.getInstance().setSendStatus(
					Constants.SEND_STATUS_NW_ERROR);
		}

		return mSendStatus;
	}

	/**
	 * Cancel upload.
	 */
	public void cancelUpload() {
		if (mHttpUtil != null) {
			mHttpUtil.disconnect();
			mHttpUtil = null;
			Logger.log(Level.DEBUG, TAG, "cancelUpload() ");
		} else {
			if (getRootController() != null
					&& getRootController().getBluetoothDataListener() != null) {
				getRootController().getBluetoothDataListener()
						.sendEmptyMessage(Constants.UPDATE_CENCELED);
			}
		}
	}

	/**
	 * Method writeLog.
	 *
	 * @param leadIndex
	 *            int
	 * @param sucess
	 *            int
	 */
	private void writeLog(int leadIndex, int sucess) {
		if (Logger.isDebugEnabled()) {
			final String sucessStr = (sucess >= 0) ? "lead " + leadIndex
					+ " upload sucess" : "lead " + leadIndex + " upload fail";
			Logger.log(Level.DEBUG, TAG, sucessStr);
		}
	}

}
