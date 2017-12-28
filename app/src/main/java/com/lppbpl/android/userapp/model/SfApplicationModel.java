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

import java.util.Set;
import java.util.Vector;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.DeviceData;
import com.lppbpl.Measurement;
import com.lppbpl.MultipleResponse;
import com.lppbpl.Response;
import com.lppbpl.android.userapp.bluetooth.BluetoothClient;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.controller.SfApplicationController;
import com.lppbpl.android.userapp.db.DiagnosisMsgDb;
import com.lppbpl.android.userapp.db.PendingRecordDb;

// TODO: Auto-generated Javadoc
/**
 * The Class SfApplicationModel.
 */
public class SfApplicationModel {

	/** The Constant TAG. */
	private static final String TAG = SfApplicationModel.class.getSimpleName();

	/** The m controller. */
	private final SfApplicationController mController;

	/** The m bluetooth impl. */
	private final BluetoothClient mBluetoothImpl;

	/** The m status. */
	private boolean[] mStatus = null;

	// Device Data
	/** The m device data. */
	private DeviceData mDeviceData;

	// PIN information stored from the form
	/** The m pin model. */
	private final SfPinModel mPinModel;

	// GetData Response
	/** The m get data response. */
	private MultipleResponse mGetDataResponse;

	// Activity model
	/** The m act model. */
	private SfActivityModel mActModel;

	// SendModel
	/** The m send model. */
	private final SfSendModel mSendModel;

	/** The m pending record. */
	private PendingRecord mPendingRecord = null;

	/** The m response code. */
	private int mResponseCode = 0;

	/** The m sf api key. */
	private String mSfApiKey = null;

	/** The m sf api access key. */
	private String mSfApiAccessKey = null;

	/** The m sf api secret key. */
	private String mSfApiSecretKey = null;

	/** The m token key. */
	private String mTokenKey = null;

	/** The m user role. */
	private String mUserRole = null;

	/** The m user unique Id. */
	private String mUserUniqueId = null;

	/** The m ecg lead count. */
	private int mEcgLeadCount;

	/** The m user not sub scribed. */
	private boolean mUserNotSubScribed;

	/** The m deactivated user account. */
	private boolean mDeactivatedUserAccount;

	/** The m valid device id. */
	private boolean mValidDeviceId;

	/** The m ecg case detail. */
	private String mEcgCaseDetail;

	/** The m bg case symptoms. */
	private String mBGCaseSymptoms;

	/** The m bg reading value. */
	private double mBGReadingValue;

	/** The m bg comment. */
	private String mBGComment;

	/** The m bg start time. */
	private long mBGStartTime;

	/** The m bg reading type. */
	private String mBGReadingType;

	/** The m case id. */
	private int mCaseId;

	/** The m bg case detail. */
	private String mBgCaseDetail;

	/** The my cases data. */
	private Vector<MyCaseInfo> myCasesData;

	/** The m support contact no. */
	private String mSupportContactNo;

	/** The m reg model. */
	private Registration mRegModel;

	/** The m member list. */
	private Set<String> mMemberList;

	private String defaultServerAddress;

	/**
	 * Constructor for SfApplicationModel.
	 *
	 * @param controller
	 *            SfApplicationController
	 */
	public SfApplicationModel(SfApplicationController controller) {
		this.mController = controller;

		// Create Model
		mBluetoothImpl = new BluetoothClient(controller);
		mBluetoothImpl.startHelperThread();

		mPinModel = new SfPinModel();
		// mPinModel.getRegistrationInfo();
		mSendModel = SfSendModel.getInstance();
		// Activity Model
		mActModel = new SfActivityModel(mController);
	}

	/**
	 * Method getBluetoothClient.
	 *
	 * @return BluetoothClient
	 */
	public BluetoothClient getBluetoothClient() {
		return mBluetoothImpl;
	}

	/**
	 * Creates the registration model.
	 */
	public void createRegistrationModel() {
		mRegModel = new Registration();
	}

	/**
	 * Method getRegistrationModel.
	 *
	 * @return Registration
	 */
	public Registration getRegistrationModel() {
		return mRegModel;
	}

	/**
	 * Method setSymptomSelectionStatus.
	 *
	 * @param status
	 *            boolean[]
	 */
	public void setSymptomSelectionStatus(boolean[] status) {
		if (this.mStatus == null && status != null) {
			this.mStatus = new boolean[status.length];
			for (int i = 0; i < status.length; i++) {
				this.mStatus[i] = status[i];
			}
		}
	}

	/**
	 * Method getSymptomSelectionStatus.
	 *
	 * @return boolean[]
	 */
	public boolean[] getSymptomSelectionStatus() {
		return mStatus;
	}

	/**
	 * Clear all data.
	 */
	public void clearAllData() {
		mStatus = null;
		mSendModel.setUserComment(null);
	}

	/**
	 * Method getGetDataResponse.
	 *
	 * @return MultipleResponse
	 */
	public MultipleResponse getGetDataResponse() {
		return mGetDataResponse;
	}

	/**
	 * Method setGetDataResponse.
	 *
	 * @param getDataResponse
	 *            MultipleResponse
	 */
	public void setGetDataResponse(MultipleResponse getDataResponse) {
		this.mGetDataResponse = getDataResponse;
	}

	// Store ECG Data
	/*
	 * public Response getGetDataEcg() { return savedEcgData; } public void
	 * setGetDataEcg(Response ecgData) { this.savedEcgData = ecgData; }
	 */

	/**
	 * Method getActivityModel.
	 *
	 * @return SfActivityModel
	 */
	public SfActivityModel getActivityModel() {
		return mActModel;
	}

	// Device data
	/**
	 * Method getDeviceData.
	 *
	 * @return DeviceData
	 */
	public DeviceData getDeviceData() {
		return mDeviceData;
	}

	/**
	 * Method setDeviceData.
	 *
	 * @param deviceData
	 *            DeviceData
	 */
	public void setDeviceData(DeviceData deviceData) {
		this.mDeviceData = deviceData;
	}

	// PIN information
	/**
	 * Method getPinModel.
	 *
	 * @return SfPinModel
	 */
	public SfPinModel getPinModel() {
		return mPinModel;
	}

	/** The step response. */
	private Response stepResponse;

	// private HttpUtil httpUtil;

	// public boolean isTermsConditionsAccepted() {
	// return mPinModel.isTermsConditionsAccepted();
	// }

	// Free all the resources
	/**
	 * Close.
	 */
	public void close() {
		DiagnosisMsgDb.getInstance().close();
		PendingRecordDb.getInstance().close();
	}

	/**
	 * Method setStepResponse.
	 *
	 * @param response
	 *            Response
	 */
	public void setStepResponse(Response response) {
		this.stepResponse = response;
	}

	/**
	 * Method getStepResponse.
	 *
	 * @return Response
	 */
	public Response getStepResponse() {
		return this.stepResponse;
	}

	/**
	 * Method getPendingRecord.
	 *
	 * @return PendingRecord
	 */
	public PendingRecord getPendingRecord() {
		return this.mPendingRecord;
	}

	/**
	 * Method setPendingRecord.
	 *
	 * @param pendingRecord
	 *            PendingRecord
	 */
	public void setPendingRecord(PendingRecord pendingRecord) {
		this.mPendingRecord = pendingRecord;
	}

	/**
	 * Clear pending record.
	 */
	public void clearPendingRecord() {
		this.mPendingRecord = null;
	}

	/**
	 * Method createPendingRecord.
	 *
	 * @param measurementType
	 *            int
	 */
	public void createPendingRecord(int measurementType) {
		this.mPendingRecord = new PendingRecord(measurementType);
		Logger.log(Level.DEBUG, TAG, "pending record created for "
				+ Measurement.getStringValue(measurementType));
	}

	/**
	 * Method getResponseCode.
	 *
	 * @return int
	 */
	public int getResponseCode() {
		return mResponseCode;
	}

	/**
	 * Method setResponseCode.
	 *
	 * @param mResponseCode
	 *            int
	 */
	public void setResponseCode(int mResponseCode) {
		this.mResponseCode = mResponseCode;
	}

	/**
	 * Method getSfApiAccessKey.
	 *
	 * @return String
	 */
	public String getSfApiAccessKey() {
		return mSfApiAccessKey;
	}

	/**
	 * Method setSfApiAccessKey.
	 *
	 * @param mSfApiAccessKey
	 *            String
	 */
	public void setSfApiAccessKey(String mSfApiAccessKey) {
		this.mSfApiAccessKey = mSfApiAccessKey;
	}

	/**
	 * Method getSfApiSecretKey.
	 *
	 * @return String
	 */
	public String getSfApiSecretKey() {
		return mSfApiSecretKey;
	}

	/**
	 * Method setSfApiSecretKey.
	 *
	 * @param mSfApiSecretKey
	 *            String
	 */
	public void setSfApiSecretKey(String mSfApiSecretKey) {
		this.mSfApiSecretKey = mSfApiSecretKey;
	}

	/**
	 * Method getSfApiKey.
	 *
	 * @return String
	 */
	public String getSfApiKey() {
		return mSfApiKey;
	}

	/**
	 * Method setSfApiKey.
	 *
	 * @param mSfApiKey
	 *            String
	 */
	public void setSfApiKey(String mSfApiKey) {
		this.mSfApiKey = mSfApiKey;
	}

	/**
	 * Method getTokenKey.
	 *
	 * @return String
	 */
	public String getTokenKey() {
		return mTokenKey;
	}

	/**
	 * Method setTokenKey.
	 *
	 * @param mSessionKey
	 *            String
	 */
	public void setTokenKey(String mSessionKey) {
		this.mTokenKey = mSessionKey;
	}

	/**
	 * Method isPrimaryUser.
	 *
	 * @return boolean
	 */
	public boolean isPrimaryUser() {
		boolean flag = false;
		if (mPinModel.isLoginForSessionSuccess() && mUserRole != null
				&& !mUserRole.equals(Constants.USER_MEMBER)) {
			flag = true;
		}
		return flag;
	}

	/**
	 * Method isParamedicUser.
	 *
	 * @return boolean
	 */
	public boolean isParamedicUser() {
		boolean flag = false;
		if (mPinModel.isLoginForSessionSuccess() && mUserRole != null
				&& mUserRole.equals(Constants.USER_PARAMEDIC)) {
			flag = true;
		}
		return flag;
	}

	/**
	 * Method getUserRole.
	 *
	 * @return String
	 */
	public String getUserRole() {
		return mUserRole;
	}

	/**
	 * Method setUserRole.
	 *
	 * @param mUserRole
	 *            String
	 */
	public void setUserRole(String mUserRole) {
		this.mUserRole = mUserRole;
	}

	public String getUserUniqueId() {
		return mUserUniqueId;
	}

	public void setUserUniqueId(String userUniqueId) {
		this.mUserUniqueId = userUniqueId;
	}

	/**
	 * Method setUserNotScribtion.
	 *
	 * @param subscribed
	 *            boolean
	 */
	public void setUserNotScribtion(boolean subscribed) {
		this.mUserNotSubScribed = subscribed;
	}

	/**
	 * Method isUserNotScribed.
	 *
	 * @return boolean
	 */
	public boolean isUserNotScribed() {
		return this.mUserNotSubScribed;
	}

	/**
	 * Method setInvalidDeviceId.
	 *
	 * @param validDeviceId
	 *            boolean
	 */
	public void setInvalidDeviceId(boolean validDeviceId) {
		this.mValidDeviceId = validDeviceId;
	}

	/**
	 * Method isInvalidDeviceId.
	 *
	 * @return boolean
	 */
	public boolean isInvalidDeviceId() {
		return this.mValidDeviceId;
	}

	/**
	 * Method setDeactivUserAccount.
	 *
	 * @param deactivated
	 *            boolean
	 * @return boolean
	 */
	public boolean setDeactivUserAccount(boolean deactivated) {
		return this.mDeactivatedUserAccount = deactivated;
	}

	/**
	 * Method isDeactivUserAccount.
	 *
	 * @return boolean
	 */
	public boolean isDeactivUserAccount() {
		return this.mDeactivatedUserAccount;
	}

	/**
	 * Method setEcgCaseDetail.
	 *
	 * @param mEcgCaseDetail
	 *            String
	 */
	public void setEcgCaseDetail(String mEcgCaseDetail) {
		this.mEcgCaseDetail = mEcgCaseDetail;
	}

	/**
	 * Method getEcgCaseDetail.
	 *
	 * @return String
	 */
	public String getEcgCaseDetail() {
		return mEcgCaseDetail;
	}

	/**
	 * Method setBGCaseSymptoms.
	 *
	 * @param mBGCaseSymptoms
	 *            String
	 */
	public void setBGCaseSymptoms(String mBGCaseSymptoms) {
		this.mBGCaseSymptoms = mBGCaseSymptoms;
	}

	/**
	 * Method getBGCaseSymptoms.
	 *
	 * @return String
	 */
	public String getBGCaseSymptoms() {
		return mBGCaseSymptoms;
	}

	/**
	 * Method setBgReadingValue.
	 *
	 * @param bgReadingVal
	 *            double
	 */
	public void setBgReadingValue(double bgReadingVal) {
		mBGReadingValue = bgReadingVal;
	}

	/**
	 * Method getBgReadingValue.
	 *
	 * @return double
	 */
	public double getBgReadingValue() {
		return mBGReadingValue;
	}

	/**
	 * Method setComment.
	 *
	 * @param comment
	 *            String
	 */
	public void setComment(String comment) {
		mBGComment = comment;
	}

	/**
	 * Method getComment.
	 *
	 * @return String
	 */
	public String getComment() {
		return mBGComment;
	}

	/**
	 * Method setBgStartTime.
	 *
	 * @param bgstarttime
	 *            long
	 */
	public void setBgStartTime(long bgstarttime) {
		mBGStartTime = bgstarttime;
	}

	/**
	 * Method getBgStartTime.
	 *
	 * @return long
	 */
	public long getBgStartTime() {
		return mBGStartTime;
	}

	/**
	 * Method setBGReadingType.
	 *
	 * @param bgreadingType
	 *            String
	 */
	public void setBGReadingType(String bgreadingType) {
		mBGReadingType = bgreadingType;
	}

	/**
	 * Method getBGReadingType.
	 *
	 * @return String
	 */
	public String getBGReadingType() {
		return mBGReadingType;
	}

	/**
	 * Method setMyCasesData.
	 *
	 * @param myCasesData
	 *            Vector<MyCaseInfo>
	 */
	public void setMyCasesData(Vector<MyCaseInfo> myCasesData) {
		this.myCasesData = myCasesData;
	}

	/**
	 * Method getMyCasesData.
	 *
	 * @return Vector<MyCaseInfo>
	 */
	public Vector<MyCaseInfo> getMyCasesData() {
		return myCasesData;
	}

	/**
	 * Method setBgCaseDetail.
	 *
	 * @param mBgCaseDetail
	 *            String
	 */
	public void setBgCaseDetail(String mBgCaseDetail) {
		this.mBgCaseDetail = mBgCaseDetail;
	}

	/**
	 * Method getBgCaseDetail.
	 *
	 * @return String
	 */
	public String getBgCaseDetail() {
		return mBgCaseDetail;
	}

	/**
	 * Method getEcgLeadCount.
	 *
	 * @return int
	 */
	public int getEcgLeadCount() {
		return mEcgLeadCount;
	}

	/**
	 * Method setEcgLeadCount.
	 *
	 * @param mEcgLeadCount
	 *            int
	 */
	public void setEcgLeadCount(int mEcgLeadCount) {
		this.mEcgLeadCount = mEcgLeadCount;
	}

	/**
	 * Method setSupportContactNo.
	 *
	 * @param mSupportContactNo
	 *            String
	 */
	public void setSupportContactNo(String mSupportContactNo) {
		this.mSupportContactNo = mSupportContactNo;
	}

	/**
	 * Method getSupportContactNo.
	 *
	 * @return String
	 */
	public String getSupportContactNo() {
		return mSupportContactNo;
	}

	/**
	 * Method getMemberList.
	 *
	 * @return Set<String>
	 */
	public Set<String> getMemberList() {
		return mMemberList;
	}

	/**
	 * Method setMemberList.
	 *
	 * @param mMemberList
	 *            Set<String>
	 */
	public void setMemberList(Set<String> mMemberList) {
		this.mMemberList = mMemberList;
	}

	/**
	 * Method getCaseId.
	 *
	 * @return int
	 */
	public int getCaseId() {
		return mCaseId;
	}

	/**
	 * Method setCaseId.
	 *
	 * @param caseId
	 *            int
	 */
	public void setCaseId(int caseId) {
		this.mCaseId = caseId;
	}

	public String getDefaultServerAddress() {
		return defaultServerAddress;
	}

	public void setDefaultServerAddress(String defaultServerAddress) {
		this.defaultServerAddress = defaultServerAddress;
	}
}
