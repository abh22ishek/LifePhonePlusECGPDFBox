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

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.ActivityData;
import com.lppbpl.BgData;
import com.lppbpl.BgReadingType;
import com.lppbpl.EcgData;
import com.lppbpl.EcgMultipleLead;
import com.lppbpl.HrData;
import com.lppbpl.Measurement;
import com.lppbpl.Response;
import com.lppbpl.Response.ServiceRequest;
import com.lppbpl.ResponseType;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.model.PendingRecord;
import com.lppbpl.android.userapp.model.SfApplicationModel;
import com.lppbpl.android.userapp.model.SfSendModel;

import java.io.IOException;

// TODO: Auto-generated Javadoc
// XXX: Populate the sendResponse member in the ApplicationModel before calling this controller
/**
 * The Class SfSendController.
 */
public class SfSendController {

	/** The Constant TAG. */
	private static final String TAG = "SfSendController";

	/** The m root controller. */
	private final SfApplicationController mRootController;

	/**
	 * Constructor for SfSendController.
	 *
	 * @param root
	 *            SfApplicationController
	 */
	public SfSendController(SfApplicationController root) {
		this.mRootController = root;
	}

	/**
	 * Starts thread to upload measured data to cloud.
	 *
	 * @param uploadMe
	 *            the upload me
	 */
	public void uploadUserData(final boolean uploadMe) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				postResponseToCloud(uploadMe);
			}
		}, "UploadData").start();
	}

	/**
	 * Upload the if pending record exists. Build the ECG, BG and Activity
	 * Records and upload to Cloud.
	 *
	 * @param uploadMe
	 *            the upload me
	 */
	private void postResponseToCloud(final boolean uploadMe) {

		if (Logger.isDebugEnabled()) {
			Logger.log(Level.DEBUG, TAG,
					"postResponseToCloud befor getting response -> "
							+ Runtime.getRuntime().freeMemory());
		}

		final SfApplicationModel appModel = this.mRootController.getAppModel();
		final PendingRecord pendingRec = appModel.getPendingRecord();
		Response response = pendingRec.getResponse();
		// server address
		final String serverAddress = appModel.getPinModel().getServerAddress();

		if (response == null
				&& (serverAddress == null || serverAddress.length() == 0)) {
			SfSendModel.getInstance().setSendStatus(
					Constants.SEND_STATUS_NO_DATA_OR_URL);
		} else if (pendingRec.getId() != -1 && uploadMe) {
			if (Logger.isDebugEnabled()) {
				Logger.log(Level.DEBUG, TAG,
						"this record already saved in pending record, "
								+ "so directly upload to cloud");
			}
			// fix for pending record not saving the states when symptoms get
			// changed
			if (response != null) {
				final int measurementType = response.getMeasurementType();
				if (measurementType == Measurement.ECG) {
					if (Logger.isDebugEnabled()) {
						Logger.log(Level.DEBUG, TAG,
								"Building the final response.....");
					}
					response = buildLastEcgResponse();
					if (Logger.isDebugEnabled()) {
						Logger.log(Level.DEBUG, TAG,
								"updating the final response to pending record");
					}
					pendingRec.updateLastEcgResponse(response);
				} /*
				 * else if (measurementType == Measurement.HR) { response =
				 * buildHrResponse(response); pendingRec.setResponse(response);
				 * SfSendModel.getInstance().setState(SfSendModel.SEND_HR); }
				 */else if (measurementType == Measurement.BG) {
					Logger.log(Level.DEBUG, TAG,
							"Building Blood Sugar response");
					response = buildBgResponse(response);
					pendingRec.setResponse(response);
				} else if (measurementType == Measurement.ACT) {
					Logger.log(Level.DEBUG, TAG, "Building Activity response");
					response = buildActResponse(response);
					pendingRec.setResponse(response);
				}
			}
			// uploads pending record
			SfUploadManager.getInstance().uploadPendingRecord(
					pendingRec.getId());
		} else if (response != null) {
			final int measurementType = response.getMeasurementType();

			try {
				if (measurementType == Measurement.ECG) {
					if (Logger.isDebugEnabled()) {
						Logger.log(Level.DEBUG, TAG,
								"Building the final response.....");
					}
					response = buildLastEcgResponse();
					if (Logger.isDebugEnabled()) {
						Logger.log(Level.DEBUG, TAG,
								"updating the final response to pending record");
					}
					pendingRec.updateLastEcgResponse(response);
				} /*
				 * else if (measurementType == Measurement.HR) { response =
				 * buildHrResponse(response); pendingRec.setResponse(response);
				 * SfSendModel.getInstance().setState(SfSendModel.SEND_HR); }
				 */else if (measurementType == Measurement.BG) {
					Logger.log(Level.DEBUG, TAG,
							"Building Blood Sugar response");
					response = buildBgResponse(response);
					pendingRec.setResponse(response);
				} else if (measurementType == Measurement.ACT) {
					Logger.log(Level.DEBUG, TAG, "Building Activity response");
					response = buildActResponse(response);
					pendingRec.setResponse(response);
				}
				try {
					SfUploadManager.getInstance().addPendingRecord(pendingRec,
							uploadMe);
				} catch (Exception e) {
					Logger.log(Level.DEBUG, TAG,
							"Unable to Save the record the DB:" + e);
					if (mRootController.getBluetoothDataListener() != null) {
						mRootController.getBluetoothDataListener()
								.sendEmptyMessage(Constants.DB_ERROR_OCCURRED);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				if (Logger.isDebugEnabled()) {
					Logger.log(Level.DEBUG, TAG, "upload initiation failed ..."
							+ e.toString());
				}
				SfSendModel.getInstance().setSendStatus(
						Constants.SEND_STATUS_NW_ERROR);
			}
		}

		if (Logger.isDebugEnabled()) {
			Logger.log(Level.DEBUG, TAG,
					"postResponseToCloud after post response -> "
							+ Runtime.getRuntime().freeMemory());
		}
	}

	/**
	 * Builds Ecg Data to upload for cloud.
	 *
	 * @return Response object
	 */
	private final Response buildLastEcgResponse() {

		final SfApplicationModel model = this.mRootController.getAppModel();

		// UserContext userContext = model.buildUserContext();

		// if (Logger.isDebugEnabled()) {
		// Logger.log(Level.DEBUG, TAG, "The User context Build is " +
		// userContext);
		// }

		final Response lastEcgResp = model.getPendingRecord()
				.getLastEcgLeadResponse();

		if (lastEcgResp == null) {
			return null;
		}

		final EcgData ecgData = lastEcgResp.getEcgData();

		if (Logger.isDebugEnabled()) {
			Logger.log(Level.DEBUG, TAG, "Retrieved last ecg response ecgdata "
					+ ecgData);
		}

		final EcgData.Builder ecgDataBuilder = EcgData.newBuilder();
		final boolean[] status = model.getSymptomSelectionStatus();
		if (status != null) {
			for (int i = 0; i < Constants.MAX_NO_OF_ECG_SYMPTOMS; i++) {
				if (status[i]) {
					ecgDataBuilder.addElementEcgSymptoms(i + 1);
					if (Logger.isDebugEnabled()) {
						Logger.log(Level.DEBUG, TAG, "symtem getting added");
					}

				}
			}
			model.setSymptomSelectionStatus(null);
		}

		if (Logger.isDebugEnabled()) {
			Logger.log(Level.DEBUG, TAG, "Retrieving heartrate... ");
		}

		final int heartRate = model.getPendingRecord().getHeartRate();

		if (Logger.isDebugEnabled()) {
			Logger.log(Level.DEBUG, TAG, "Retrieved heartrate is " + heartRate);
		}

		final HrData hrData = HrData.newBuilder().setHeartRate(heartRate)
				.build();

		if (Logger.isDebugEnabled()) {
			Logger.log(Level.DEBUG, TAG, "adding multilead to new response.. ");
		}

		ecgDataBuilder.addElementMulLead((EcgMultipleLead) ecgData.getMulLead()
				.elementAt(0));

		final String userComment = SfSendModel.getInstance().getUserComment();

		if (userComment != null && userComment.trim().length() > 0) {
			ecgDataBuilder.setAnnotationTxt(userComment);
		}
		if (Logger.isDebugEnabled()) {
			Logger.log(Level.DEBUG, TAG, "entered ecg comment=" + userComment);
		}

		final int duration = ecgData.getDuration();

		if (Logger.isDebugEnabled()) {
			Logger.log(Level.DEBUG, TAG, "building the final ecgdata... ");
		}

		final EcgData ecgTemp = ecgDataBuilder.setDuration(duration).build();

		final Response.Builder retResponseBuilder = Response.newBuilder()
				.setResponseType(ResponseType.DVD)
				.setServiceReq(ServiceRequest.DataLogging)
				.setMeasurementType(Measurement.ECG).setEcgData(ecgTemp)
				.setTimestamp(lastEcgResp.getTimestamp());
		// .setUserContext(userContext);
		// if (heartRate > 0) {
		retResponseBuilder.setHrData(hrData);
		// }
		final Response retResponse = retResponseBuilder.build();

		if (Logger.isDebugEnabled()) {
			try {
				Logger.log(Level.DEBUG, TAG,
						"Response length returns from the buildEcg "
								+ retResponse.toByteArray().length);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Logger.log(Level.DEBUG, TAG, "Response returns from the buildEcg "
					+ retResponse.toString());
		}

		return retResponse;
	}

	/**
	 * Build Blood sugar response to upload for cloud.
	 *
	 * @param response
	 *            the response
	 * @return Response
	 */
	private final Response buildBgResponse(Response response) {
		if (response == null) {
			return null;
		}

		// UserContext userContext =
		// this.mRootController.getAppModel().buildUserContext();

		final BgData origBgData = response.getBgData();
		int bgReadingType = origBgData.getBgReadingType();
		if (bgReadingType == 0) {
			bgReadingType = BgReadingType.RANDOM;
		}

		final BgData.Builder bgDataBuilder = BgData.newBuilder();
		bgDataBuilder.setBgReadingType(bgReadingType).setBgReading(
				origBgData.getBgReading());

		final String userComment = SfSendModel.getInstance().getUserComment();
		if (userComment != null && userComment.trim().length() > 0) {
			// set Annotation texts
			bgDataBuilder.setAnnotationTxt(userComment);
		}

		final boolean[] status = this.mRootController.getAppModel()
				.getSymptomSelectionStatus();
		if (status != null) {
			for (int i = 0; i < Constants.MAX_NO_OF_BG_SYMPTOMS; i++) {
				if (status[i]) {
					bgDataBuilder.addElementBgSymptoms(i + 1);
					if (Logger.isDebugEnabled()) {
						Logger.log(Level.DEBUG, TAG, "symtem getting added");
					}

				}
			}
			this.mRootController.getAppModel().setSymptomSelectionStatus(null);
		}

		// Build BG Data
		// Nothing to build... use the data in the response
		final Response retResponse = Response.newBuilder()
				.setResponseType(response.getResponseType())
				.setMeasurementType(response.getMeasurementType())
				.setBgData(bgDataBuilder.build())
				// .setUserContext(userContext)
				.setTimestamp(response.getTimestamp())
				.setServiceReq(ServiceRequest.DataLogging).build();

		return retResponse;
	}

	/**
	 * Build Activity measurement response to upload for cloud.
	 *
	 * @param response
	 *            the response
	 * @return Response
	 */
	private final Response buildActResponse(Response response) {
		if (response == null) {
			return response;
		}

		// UserContext userContext =
		// this.mRootController.getAppModel().buildUserContext();

		// Build Activity Data
		final ActivityData.Builder actBuilder = ActivityData.newBuilder();
		actBuilder
				.setTimeCoveredSofar(
						response.getActData().getTimeCoveredSofar())
				.setTotalDistance(response.getActData().getTotalDistance())
				.setTotalEnergy(response.getActData().getTotalEnergy())
				.setStartingTime(response.getActData().getStartingTime())
				.setTotalSteps(response.getActData().getTotalSteps());

		final String userComment = SfSendModel.getInstance().getUserComment();
		if (userComment != null && userComment.trim().length() > 0) {
			// set Annotation texts
			actBuilder.setAnnotationTxt(userComment);
		}

		// Nothing to build... use the data in the response
		final Response retResponse = Response.newBuilder()
				.setResponseType(response.getResponseType())
				.setMeasurementType(response.getMeasurementType())
				.setActData(actBuilder.build())
				// .setUserContext(userContext)
				.setTimestamp(response.getTimestamp())
				.setServiceReq(ServiceRequest.DataLogging).build();
		return retResponse;
	}
}
