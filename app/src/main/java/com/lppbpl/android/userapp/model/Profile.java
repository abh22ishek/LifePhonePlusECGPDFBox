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
 * The Class Profile.
 */
public class Profile {

    /** The m user name. */
    private String mUserName;

    /** The m gender. */
    private boolean mGender;

    /** The m user age. */
    private int mUserAge;

    /** The m user height. */
    private int mUserHeight;

    /** The m user weight. */
    private int mUserWeight;


    private  String mPatientId;

    private String mClinicName;





    public String getPatientId() {
        return mPatientId;
    }

    public void setPatientId(String mPatientId) {
        this.mPatientId = mPatientId;
    }

    public String getClinicName() {
        return mClinicName;
    }

    public void setClinicName(String mClinicName) {
        this.mClinicName = mClinicName;
    }

    /**
     * Method getUserName.
     * @return String
     */
    public String getUserName() {
        return mUserName;
    }

    /**
     * Method setUserName.
     * @param userName String
     */
    public void setUserName(String userName) {
        this.mUserName = userName;
    }

    /**
     * Method isMale.
     * @return boolean
     */
    public boolean isMale() {
        return mGender;
    }

    /**
     * Method setMale.
     * @param gender boolean
     */
    public void setMale(boolean gender) {
        this.mGender = gender;
    }

    /**
     * Method getUserAge.
     * @return int
     */
    public int getUserAge() {
        return mUserAge;
    }

    /**
     * Method setUserAge.
     * @param userAge int
     */
    public void setUserAge(int userAge) {
        this.mUserAge = userAge;
    }

    /**
     * Method getUserHeight.
     * @return int
     */
    public int getUserHeight() {
        return mUserHeight;
    }

    /**
     * Method setUserHeight.
     * @param userHeight int
     */
    public void setUserHeight(int userHeight) {
        this.mUserHeight = userHeight;
    }

    /**
     * Method getUserWeight.
     * @return int
     */
    public int getUserWeight() {
        return mUserWeight;
    }

    /**
     * Method setUserWeight.
     * @param userWeight int
     */
    public void setUserWeight(int userWeight) {
        this.mUserWeight = userWeight;
    }
}
