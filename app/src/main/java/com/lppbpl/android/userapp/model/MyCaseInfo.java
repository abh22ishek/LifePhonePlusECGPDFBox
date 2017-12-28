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

// TODO: Auto-generated Javadoc
/**
 * The Class MyCaseInfo.
 */
public class MyCaseInfo {

    /** The m case id. */
    private int mCaseId;

    /** The m specialization id. */
    private int mSpecializationId;

    /** The m start time. */
    private long mStartTime;

    /** The m case status. */
    private String mCaseStatus;

    /** The m measurement id. */
    private int mMeasurementId;

    /** The m tat type. */
    private String mTatType;


    /**
     * Method getTatType.
     * @return String
     */
    public String getTatType() {
        return mTatType;
    }

    /**
     * Method setTatType.
     * @param tatType String
     */
    public void setTatType(String tatType) {
        this.mTatType = tatType;
    }

    /**
     * Method getMeasurementId.
     * @return int
     */
    public int getMeasurementId() {
        return mMeasurementId;
    }

    /**
     * Method setMeasurementId.
     * @param measurementId int
     */
    public void setMeasurementId(int measurementId) {
        this.mMeasurementId = measurementId;
    }

    /**
     * Method setCaseId.
     * @param caseId int
     */
    public void setCaseId(int caseId) {
        this.mCaseId = caseId;
    }

    /**
     * Method getCaseId.
     * @return int
     */
    public int getCaseId() {
        return mCaseId;
    }

    /**
     * Method setSpecializationId.
     * @param specializationId int
     */
    public void setSpecializationId(int specializationId) {
        this.mSpecializationId = specializationId;
    }

    /**
     * Method getSpecializationId.
     * @return int
     */
    public int getSpecializationId() {
        return mSpecializationId;
    }

    /**
     * Method setStartTime.
     * @param startTime long
     */
    public void setStartTime(long startTime) {
        this.mStartTime = startTime;
    }

    /**
     * Method getStartTime.
     * @return long
     */
    public long getStartTime() {
        return mStartTime;
    }

    /**
     * Method setCaseStatus.
     * @param caseStatus String
     */
    public void setCaseStatus(String caseStatus) {
        this.mCaseStatus = caseStatus;
    }

    /**
     * Method getCaseStatus.
     * @return String
     */
    public String getCaseStatus() {
        return mCaseStatus;
    }


}
