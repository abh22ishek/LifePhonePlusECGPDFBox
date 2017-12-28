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

import com.lppbpl.android.userapp.controller.SfApplicationController;

// TODO: Auto-generated Javadoc
/**
 * The Class SfPinModel.
 */
public class SfPinModel {

	/** The m user id. */
	private String mUserID;

	/** The m device uid. */
	private String mDeviceUid;

	/** The m login for session. */
	private boolean mLoginForSession = false;

	/** The m single step ecg. */
	private boolean mSingleStepECG;

	// Service URL
	/** The m server address. */
	private String mServerAddress;

	private String deviceVersion;

	/** The Constant USER_REG_SMS_SEND. */
	public static final int USER_REG_SMS_SEND = 0;

	/** The Constant USER_REG_SMS_RESPONSE_RECEIVED. */
	public static final int USER_REG_SMS_RESPONSE_RECEIVED = 1;

	/** The Constant USER_AUTH_STATUS. */
	public static final int USER_AUTH_STATUS = 2;

	/**
	 * Instantiates a new sf pin model.
	 */
	public SfPinModel() {
		mUserID = mDeviceUid = mServerAddress = "";
	}

	/**
	 * Method getUserID.
	 *
	 * @return String
	 */
	public String getUserID() {
		return mUserID;
	}

	/**
	 * Method setUserID.
	 *
	 * @param userId
	 *            String
	 */
	public void setUserID(String userId) {
		this.mUserID = userId;
	}

	/**
	 * Method getDeviceUid.
	 *
	 * @return String
	 */
	public String getDeviceUid() {
		return mDeviceUid;
	}

	/**
	 * Method setDeviceUid.
	 *
	 * @param uid
	 *            String
	 */
	public void setDeviceUid(String uid) {
		mDeviceUid = uid;
	}

	// Server Address
	/**
	 * Method getServerAddress.
	 *
	 * @return String
	 */
	public String getServerAddress() {
		if (mServerAddress != null && mServerAddress.trim().length() > 0) {
			return mServerAddress;
		} else {
			return SfApplicationController.getInstance().getAppModel()
					.getDefaultServerAddress();
		}
	}

	/**
	 * Method setServerAddress.
	 *
	 * @param serverAddress
	 *            String
	 */
	public void setServerAddress(String serverAddress) {
		this.mServerAddress = serverAddress;
	}

	/**
	 * Method isLoginForSessionSuccess.
	 *
	 * @return boolean
	 */
	public boolean isLoginForSessionSuccess() {
		return mLoginForSession;
	}

	/**
	 * set login success.
	 *
	 * @param mLoginForSession
	 *            the new login for session success
	 */
	public void setLoginForSessionSuccess(boolean mLoginForSession) {
		this.mLoginForSession = mLoginForSession;
	}

	/**
	 * Method isSingleStepECG.
	 *
	 * @return boolean
	 */
	public boolean isSingleStepECG() {
		return mSingleStepECG;
	}

	/**
	 * Method setSingleStepECG.
	 *
	 * @param singleStepECG
	 *            boolean
	 */
	public void setSingleStepECG(boolean singleStepECG) {
		this.mSingleStepECG = singleStepECG;
	}

	public void setDeviceVersion(String deviceVersion) {
		this.deviceVersion = deviceVersion;
	}

	public String getDeviceVersion() {
		return this.deviceVersion;
	}
}
