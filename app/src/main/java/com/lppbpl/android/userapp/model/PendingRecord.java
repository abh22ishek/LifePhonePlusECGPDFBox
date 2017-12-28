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

import android.util.SparseIntArray;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.ActivityData;
import com.lppbpl.BgData;
import com.lppbpl.EcgData;
import com.lppbpl.EcgLead;
import com.lppbpl.EcgMultipleLead;
import com.lppbpl.HrData;
import com.lppbpl.Measurement;
import com.lppbpl.Response;
import com.lppbpl.android.userapp.controller.SfApplicationController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

// TODO: Auto-generated Javadoc
/**
 * The Class PendingRecord.
 */
public class PendingRecord {

	/** The Constant UPLOAD_FAILED. */
	public static final int UPLOAD_FAILED = -1;

	/** The Constant UPLOAD_NOT_STARTED. */
	public static final int UPLOAD_NOT_STARTED = 0;

	/** The Constant UPLOAD_STARTED. */
	public static final int UPLOAD_STARTED = 1;

	/** The Constant UPLOAD_SUCCESS. */
	public static final int UPLOAD_SUCCESS = 2;

	/** The m id. */
	private int mId = -1;

	/** The m measurement type. */
	private int mMeasurementType = Measurement.ECG;

	/** The m upload status. */
	private int mUploadStatus = UPLOAD_NOT_STARTED;

	// public static final int TOTAL_ECG_LEAD = 12;

	/** The m measure size. */
	private int mMeasureSize = 0;

	/** The m response. */
	private Response[] mResponse = null;

	/** The m rread. */
	private boolean mRread = false;

	/** The m measurement time. */
	private long mMeasurementTime = -1;

	/** The m num of attempt. */
	private int mNumOfAttempt = 0;

	/** The model. */
	private final SfApplicationModel model = SfApplicationController
			.getInstance().getAppModel();

	/**
	 * Constructor for PendingRecord.
	 *
	 * @param measurementType
	 *            int
	 */
	public PendingRecord(int measurementType) {
		this.mMeasurementType = measurementType;
		createResponse(measurementType == Measurement.ECG ? model
				.getEcgLeadCount() : 1);
		// mMeasurementTime = new Date().getTime();
	}

	/**
	 * Instantiates a new pending record.
	 */
	public PendingRecord() {
		// mMeasurementTime = new Date().getTime();
	}

	/**
	 * Constructor for PendingRecord.
	 *
	 * @param uploadStatus
	 *            int
	 * @param response
	 *            Response
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PendingRecord(int uploadStatus, Response response) {
		super();
		this.mUploadStatus = uploadStatus;
		mMeasurementType = response.getMeasurementType();
		if (mMeasurementType == Measurement.ECG) {
			// this.response[0] = response;
			EcgData ecgData = response.getEcgData();
			HrData hrData = response.getHrData();
			Vector mulLeads = ecgData.getMulLead();
			this.mResponse = new Response[mulLeads.size()];
			// mMeasurementTime = ((EcgMultipleLead)
			// mulLeads.get(0)).getTimestamp();
			for (int i = 0; i < mulLeads.size(); i++) {
				EcgMultipleLead ecgStripLead = (EcgMultipleLead) mulLeads
						.elementAt(i);
				Vector leadVec = new Vector(1);
				leadVec.addElement(ecgStripLead);
				EcgData ecgNewData = EcgData.newBuilder()
						.setDuration(ecgData.getDuration()).setMulLead(leadVec)
						.build();
				Response.Builder newResponseBuilder = Response.newBuilder()
						.setResponseType(response.getResponseType())
						.setMeasurementType(response.getMeasurementType())
						.setEcgData(ecgNewData);
				if (hrData != null) {
					newResponseBuilder.setHrData(hrData);
				}
				final Response newResponse = newResponseBuilder.build();
				this.mResponse[i] = newResponse;
				if (i + 1 == mResponse.length) {
					break;
				}
			}
		} else {
			this.mResponse = new Response[1];
			this.mResponse[0] = response;
			// mMeasurementTime = response.getTimestamp();
		}
		// mMeasurementTime = new Date().getTime();
	}

	/**
	 * Method isECGRecord.
	 *
	 * @return boolean
	 */
	public boolean isECGRecord() {
		return (this.mMeasurementType == Measurement.ECG);
	}

	/**
	 * Method isBGRecord.
	 *
	 * @return boolean
	 */
	public boolean isBGRecord() {
		return (this.mMeasurementType == Measurement.BG);
	}

	/**
	 * Method isActivityRecord.
	 *
	 * @return boolean
	 */
	public boolean isActivityRecord() {
		return (this.mMeasurementType == Measurement.ACT);
	}

	/**
	 * Method setResponse.
	 *
	 * @param response
	 *            Response
	 */
	public void setResponse(Response response) {
		if (response != null
				&& response.getMeasurementType() != Measurement.ECG) {
			this.mResponse[0] = response;
		}
	}

	/**
	 * Method createResponse.
	 *
	 * @param size
	 *            int
	 */
	public void createResponse(int size) {
		this.mResponse = new Response[size];
	}

	/**
	 * Method setResponse.
	 *
	 * @param response
	 *            Response
	 * @param index
	 *            int
	 */
	public void setResponse(Response response, int index) {
		if (response != null
				&& response.getMeasurementType() == Measurement.ECG) {
			this.mResponse[index] = response;
		}
	}

	/**
	 * Method clearECGResponse.
	 *
	 * @param index
	 *            int
	 */
	public void clearECGResponse(int index) {
		if (this.mResponse.length > index) {
			this.mResponse[index] = null;
		}
	}

	/**
	 * Method getResponse.
	 *
	 * @return Response
	 */
	public Response getResponse() {
		return mResponse[0];
	}

	/**
	 * Method getResponse.
	 *
	 * @param index
	 *            int
	 * @return Response
	 */
	public Response getResponse(int index) {
		if (isECGRecord()) {
			return mResponse[index];
		}
		return null;
	}

	/**
	 * Method getLastEcgLeadResponse.
	 *
	 * @return Response
	 */
	public Response getLastEcgLeadResponse() {
		if (isECGRecord()) {
			for (int i = mResponse.length - 1; i >= 0; i--) {
				Response resp = mResponse[i];
				if (resp != null) {
					return resp;
				}
			}
		}
		return null;
	}

	/**
	 * Method updateLastEcgResponse.
	 *
	 * @param lastResp
	 *            Response
	 */
	public void updateLastEcgResponse(Response lastResp) {
		if (isECGRecord()) {
			for (int i = mResponse.length - 1; i >= 0; i--) {
				Response resp = mResponse[i];
				if (resp != null) {
					mResponse[i] = lastResp;
					break;
				}
			}
		}
	}

	/**
	 * Method addEcgResponse.
	 *
	 * @param ecgLeadResp
	 *            Response
	 */
	public void addEcgResponse(Response ecgLeadResp) {
		if (isECGRecord()) {
			for (int i = 0; i < mResponse.length; i++) {
				Response ecgResp = mResponse[i];
				if (ecgResp == null) {
					Logger.log(Level.DEBUG, "PendingRecord", "Added " + (i + 1)
							+ " Lead Response");
					mResponse[i] = ecgLeadResp;
					break;
				}
			}
		}
	}

	/**
	 * Method getId.
	 *
	 * @return int
	 */
	public int getId() {
		return mId;
	}

	/**
	 * Method setId.
	 *
	 * @param id
	 *            int
	 */
	public void setId(int id) {
		this.mId = id;
	}

	/**
	 * Method getMeasurementType.
	 *
	 * @return int
	 */
	public int getMeasurementType() {
		return this.mMeasurementType;
	}

	/**
	 * Method setMeasurementType.
	 *
	 * @param measurementType
	 *            int
	 */
	public void setMeasurementType(int measurementType) {
		this.mMeasurementType = measurementType;
	}

	/**
	 * Method getUploadStatus.
	 *
	 * @return int
	 */
	public int getUploadStatus() {
		return mUploadStatus;
	}

	/**
	 * Method setUploadStatus.
	 *
	 * @param uploadStatus
	 *            int
	 */
	public void setUploadStatus(int uploadStatus) {
		this.mUploadStatus = uploadStatus;
	}

	/**
	 * Method isRead.
	 *
	 * @return boolean
	 */
	public boolean isRead() {
		return mRread;
	}

	/**
	 * Method setRead.
	 *
	 * @param readStatus
	 *            boolean
	 */
	public void setRead(boolean readStatus) {
		this.mRread = readStatus;
	}

	/**
	 * Method getMeasurementTime.
	 *
	 * @return long
	 */
	public long getMeasurementTime() {
		long mtime = 0;
		if (isECGRecord()) {
			mtime = getECGMeasurementTime();
			if (mtime == 0) {
				mtime = mMeasurementTime;
			}
		} else {
			Response r = getResponse();
			if (r != null) {
				mtime = r.getTimestamp();
			}
		}
		return mtime;
	}

	/**
	 * Method getNumOfAttempt.
	 *
	 * @return int
	 */
	public int getNumOfAttempt() {
		return this.mNumOfAttempt;
	}

	/**
	 * Method setNumOfAttempt.
	 *
	 * @param numOfAttempt
	 *            int
	 */
	public void setNumOfAttempt(int numOfAttempt) {
		this.mNumOfAttempt = numOfAttempt;
	}

	/**
	 * Method getEcgLeadCount.
	 *
	 * @return int
	 */
	public int getEcgLeadCount() {
		return mResponse != null ? mResponse.length : 0;
	}

	/**
	 * Method getEcgLeadLabels.
	 *
	 * @return Vector<String>
	 */
	public Vector<String> getEcgLeadLabels() {
		Vector<String> leadLabels = new Vector<String>();
		if (isECGRecord()) {
			for (int i = 0; i < mResponse.length; i++) {
				Response ecgResp = mResponse[i];
				if (ecgResp != null) {
					EcgMultipleLead multiLead = (EcgMultipleLead) ecgResp
							.getEcgData().getMulLead().elementAt(0);
					String leadLabel = EcgLead.getStringValue(multiLead
							.getLead());
					leadLabels.addElement(leadLabel);
				}
			}
		}
		return leadLabels;
	}

	/**
	 * Method getEcgLeads.
	 *
	 * @return Vector
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getEcgLeads() {
		Vector leads = new Vector();
		if (isECGRecord()) {
			for (int i = 0; i < mResponse.length; i++) {
				Response ecgResp = mResponse[i];
				if (ecgResp != null) {
					leads.addElement(((EcgMultipleLead) ecgResp.getEcgData()
							.getMulLead().elementAt(0)).getEcgStrip());
				}
			}
		}
		return leads;
	}

	/**
	 * Method getHeartRate.
	 *
	 * @return int
	 */
	public int getHeartRate() {
		int heartRate = 0;
		SparseIntArray map = new SparseIntArray();
		if (isECGRecord()) {
			for (int i = 0; i < mResponse.length; i++) {
				Response ecgResp = mResponse[i];
				if (ecgResp != null) {
					int lead = ((EcgMultipleLead) ecgResp.getEcgData()
							.getMulLead().get(0)).getLead();
					if (lead == EcgLead.LEAD2 || lead == EcgLead.MCL2
							|| lead == EcgLead.MCL5) {
						HrData leadHeartRate = ecgResp.getHrData();
						if (leadHeartRate != null) {
							map.put(lead, leadHeartRate.getHeartRate());
						}
					}
				}
			}
		}

		Logger.log(Level.DEBUG, "PendingRecord", "Hart Rate Calculation:" + map);

		final int x = Math.abs(map.get(EcgLead.LEAD2) - map.get(EcgLead.MCL2));
		final int y = Math.abs(map.get(EcgLead.LEAD2) - map.get(EcgLead.MCL5));
		final int z = Math.abs(map.get(EcgLead.MCL2) - map.get(EcgLead.MCL5));

		int flag = 0;
		int min = Integer.MAX_VALUE;

		if (min >= x) {
			min = x;
			flag = 1;
		}

		if (min >= y) {
			min = y;
			flag = 2;
		}

		if (min >= z) {
			min = z;
			flag = 3;
		}

		if (flag == 1) {
			heartRate = (map.get(EcgLead.LEAD2) + map.get(EcgLead.MCL2)) / 2;
		} else if (flag == 2) {
			heartRate = (map.get(EcgLead.LEAD2) + map.get(EcgLead.MCL5)) / 2;
		} else if (flag == 3) {
			heartRate = (map.get(EcgLead.MCL2) + map.get(EcgLead.MCL5)) / 2;
		}

		Logger.log(Level.DEBUG, "PendingRecord", "Hart Rate value:" + heartRate);

		return heartRate;
	}

	/**
	 * Method getECGMeasurementTime.
	 *
	 * @return long
	 */
	private long getECGMeasurementTime() {
		long ecgTime = 0;
		if (isECGRecord() && mResponse != null) {
			for (int i = 0; i < mResponse.length; i++) {
				Response ecgResp = mResponse[i];
				if (ecgResp != null && ecgResp.hasEcgData()) {
					ecgTime = ((EcgMultipleLead) ecgResp.getEcgData()
							.getMulLead().get(0)).getTimestamp();
					break;
				}
			}
		}
		return ecgTime;
	}

	/**
	 * Method getEcgSymptoms.
	 *
	 * @return Vector
	 */
	@SuppressWarnings("rawtypes")
	public Vector getEcgSymptoms() {
		Vector symptoms = new Vector();
		if (isECGRecord()) {
			for (int i = mResponse.length - 1; i >= 0; i--) {
				Response resp = mResponse[i];
				if (resp != null) {
					symptoms = resp.getEcgData().getEcgSymptoms();
					break;
				}
			}
		}
		return symptoms;
	}

	/**
	 * Method getBgSymptoms.
	 *
	 * @return Vector
	 */
	@SuppressWarnings("rawtypes")
	public Vector getBgSymptoms() {
		Vector symptoms = new Vector();
		if (isBGRecord()) {
			Response resp = mResponse[0];
			if (resp != null) {
				symptoms = resp.getBgData().getBgSymptoms();
			}
		}
		return symptoms;
	}

	/**
	 * Method getBgReadingType.
	 *
	 * @return int
	 */
	public int getBgReadingType() {
		Response response = this.mResponse[0];
		if (response != null && isBGRecord()) {
			return response.getBgData().getBgReadingType();
		}
		return -1;
	}

	/**
	 * Method getComment.
	 *
	 * @return String
	 */
	public String getComment() {
		String userComment = null;
		for (int i = mResponse.length - 1; i >= 0; i--) {
			Response resp = mResponse[i];
			if (resp != null) {
				if (isECGRecord() && resp.hasEcgData()) {
					userComment = resp.getEcgData().getAnnotationTxt();
					break;
				} else if (isBGRecord() && resp.hasBgData()) {
					userComment = resp.getBgData().getAnnotationTxt();
					break;
				} else if (isActivityRecord() && resp.hasActData()) {
					userComment = resp.getActData().getAnnotationTxt();
					break;
				}
			}
		}
		return userComment;
	}

	/**
	 * Method getMsg.
	 *
	 * @return String
	 */
	public String getMsg() {
		StringBuffer msg = new StringBuffer();
		if (isECGRecord()) {
			msg.append("ECG");
		} else if (isBGRecord()) {
			BgData bgData = mResponse[0].getBgData();
			msg.append("Blood Sugar ").append(bgData.getBgReading())
					.append(" mg/dL");
		} else if (isActivityRecord()) {
			ActivityData act = mResponse[0].getActData();
			msg.append("Activity ").append(act.getTotalEnergy()).append(" cal");
		}
		return msg.toString();
	}

	/**
	 * Read data from outputstream.
	 *
	 * @param data
	 *            the data
	 * @param skipECGResponses
	 *            the skip ecg responses
	 */
	public void readData(byte[] data, boolean skipECGResponses) {
		try {
			ByteArrayInputStream bin = new ByteArrayInputStream(data);
			DataInputStream din = new DataInputStream(bin);
			mMeasurementType = din.readInt();
			mRread = din.readBoolean();
			mUploadStatus = din.readShort();
			mNumOfAttempt = din.readInt();
			int resLength = din.readInt();

			if (skipECGResponses && mMeasurementType == Measurement.ECG) {
				mResponse = null;
				for (int i = 0; i < resLength; i++) {
					mMeasureSize = din.readInt();
					din.skipBytes(mMeasureSize);
				}
			} else {
				mResponse = new Response[resLength];

				for (int i = 0; i < resLength; i++) {
					mMeasureSize = din.readInt();
					if (mMeasureSize > 0) {
						byte[] b = new byte[mMeasureSize];
						int dlen = din.read(b);
						if (dlen > 0) {
							mResponse[i] = Response.parseFrom(b);
						}
					}
				}
			}
			mMeasurementTime = din.readLong();
			din.close();
			bin.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * Writes data to outputstream.
	 *
	 * @return byte[]
	 */
	public byte[] writeData() {
		byte[] data = null;

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			dos.writeInt(mMeasurementType);
			dos.writeBoolean(mRread);
			dos.writeShort(mUploadStatus);
			dos.writeInt(mNumOfAttempt);
			dos.writeInt(mResponse.length);
			for (int i = 0; i < mResponse.length; i++) {
				mMeasureSize = (mResponse[i] == null) ? 0 : mResponse[i]
						.computeSize();
				dos.writeInt(mMeasureSize);
				// System.out.println("Measure size wrote found " +
				// mMeasureSize);
				if (mMeasureSize != 0) {
					final byte[] responseBytes = mResponse[i].toByteArray();
					dos.write(responseBytes);
				}
			}
			dos.writeLong(mResponse[0].getTimestamp());
			data = baos.toByteArray();
			dos.close();
			baos.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return data;
	}

	/**
	 * Method toString.
	 *
	 * @return String
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("PendingRecord [id=");
		sb.append(mId);
		sb.append(", measurementTime=");
		// sb.append(mMeasurementTime);
		sb.append(", readStatus=");
		sb.append(mRread);
		if (mResponse != null) {
			sb.append(", response=");
			for (int i = 0; i < mResponse.length; i++) {
				sb.append(mResponse[i].toString());
			}
		}
		sb.append(", uploadStatus=");
		sb.append(mUploadStatus).append(']');
		return sb.toString();
	}
}
