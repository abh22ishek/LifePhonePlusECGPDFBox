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

import java.util.Enumeration;
import java.util.Vector;

import com.lppbpl.DataLoggingResponse;
import com.lppbpl.TATCategory;

// TODO: Auto-generated Javadoc
/**
 * The Class SfSendModel.
 */
public final class SfSendModel {

	// Constants for the state
	/** The Constant SEND_ECG. */
	public static final int SEND_ECG = 1;

	/** The Constant SEND_HR. */
	public static final int SEND_HR = 2;

	/** The Constant SEND_BG. */
	public static final int SEND_BG = 3;

	/** The Constant SEND_ACT. */
	public static final int SEND_ACT = 4;

	/** The s instance. */
	private static SfSendModel sInstance = null;

	/** The m send status. */
	private int mSendStatus;

	/** The m user comment. */
	private String mUserComment = "";

	/** The m data log resp. */
	private DataLoggingResponse mDataLogResp = null;

	// private ArrayList<PaymentMode> paymentMode;

	/**
	 * Instantiates a new sf send model.
	 */
	private SfSendModel() {
	}

	/**
	 * Method getInstance.
	 *
	 * @return SfSendModel
	 */
	public static SfSendModel getInstance() {
		if (sInstance == null) {
			sInstance = new SfSendModel();
		}
		return sInstance;
	}

	/**
	 * Method getSendStatus.
	 *
	 * @return int
	 */
	public int getSendStatus() {
		return mSendStatus;
	}

	/**
	 * Method setSendStatus.
	 *
	 * @param status
	 *            int
	 */
	public void setSendStatus(int status) {
		this.mSendStatus = status;
	}

	/**
	 * Method setUserComment.
	 *
	 * @param userComment
	 *            String
	 */
	public void setUserComment(String userComment) {
		this.mUserComment = userComment;
	}

	/**
	 * Method getUserComment.
	 *
	 * @return String
	 */
	public String getUserComment() {
		return mUserComment;
	}

	/**
	 * Method setDataLoggingResponse.
	 *
	 * @param dataLogResp
	 *            DataLoggingResponse
	 */
	public void setDataLoggingResponse(DataLoggingResponse dataLogResp) {
		this.mDataLogResp = dataLogResp;
		setTATCategoryDetails(dataLogResp);
	}

	/**
	 * Method getDataLoggingResponse.
	 *
	 * @return DataLoggingResponse
	 */
	public DataLoggingResponse getDataLoggingResponse() {
		return this.mDataLogResp;
	}

	/** The charges menu. */
	private String[] chargesMenu = null;

	/** The charges list. */
	private long[] chargesList = null;

	/** The charges id. */
	private long[] chargesId = null;

	/** The charges tat. */
	private long[] chargesTat = null;

	/** The balance amount. */
	private long balanceAmount = 0;

	/**
	 * set TAT category and balance amount details.
	 *
	 * @param logResp
	 *            the new TAT category details
	 */
	private void setTATCategoryDetails(DataLoggingResponse logResp) {
		if (logResp != null && logResp.hasTatCategories()) {
			@SuppressWarnings("rawtypes")
			Vector tatCategory = logResp.getTatCategories().getCategories();

			balanceAmount = logResp.getTatCategories().getBalanceAmount();

			int size = tatCategory.size();
			chargesMenu = new String[size];
			chargesList = new long[size];
			chargesId = new long[size];
			chargesTat = new long[size];

			int index = 0;
			@SuppressWarnings("rawtypes")
			Enumeration enumeration = tatCategory.elements();
			while (enumeration.hasMoreElements()) {
				final TATCategory category = (TATCategory) enumeration
						.nextElement();
				chargesMenu[index] = category.getName();
				chargesList[index] = category.getAmount();
				chargesId[index] = category.getId();
				chargesTat[index] = category.getTat();
				index++;
			}
		} else {
			clearTATData();
		}
	}

	/**
	 * Method getChargesMenu.
	 *
	 * @return String[]
	 */
	public String[] getChargesMenu() {
		return chargesMenu;
	}

	/**
	 * Method getChargesList.
	 *
	 * @return long[]
	 */
	public long[] getChargesList() {
		return chargesList;
	}

	/**
	 * Method getBalanceAmount.
	 *
	 * @return long
	 */
	public long getBalanceAmount() {
		return balanceAmount;
	}

	/**
	 * clear the Tat details.
	 */
	public void clearTATData() {
		chargesMenu = null;
		chargesList = null;
		chargesId = null;
		chargesTat = null;

		balanceAmount = 0;
	}

	// public ArrayList<PaymentMode> getPaymentMode() {
	// return paymentMode;
	// }
	//
	// public void setPaymentMode(ArrayList<PaymentMode> paymentMode) {
	// this.paymentMode = paymentMode;
	// }

}
