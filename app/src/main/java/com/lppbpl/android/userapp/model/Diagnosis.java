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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

// TODO: Auto-generated Javadoc
/**
 * The Class Diagnosis.
 */
public class Diagnosis {

    /** The m id. */
    private int mId;

    /** The m msg. */
    private String mMsg;

    /** The m time stamp. */
    private long mTimeStamp = -1;

    /** The m read status. */
    private boolean mReadStatus;

    /** The m sender addr. */
    private String mSenderAddr;

    /**
     * Instantiates a new diagnosis.
     */
    public Diagnosis() {
        mTimeStamp = System.currentTimeMillis();
    }

    /**
     * Method getId.
     * @return int
     */
    public int getId() {
        return mId;
    }

    /**
     * Method setId.
     * @param id int
     */
    public void setId(int id) {
        this.mId = id;
    }

    /**
     * Method getMsg.
     * @return String
     */
    public String getMsg() {
        return mMsg;
    }

    /**
     * Method setMsg.
     * @param msg String
     */
    public void setMsg(String msg) {
        this.mMsg = msg;
    }

    /**
     * Method getDate.
     * @return Date
     */
    public Date getDate() {
        return new Date(mTimeStamp);
    }

    /**
     * Method setDate.
     * @param date Date
     */
    public void setDate(Date date) {
        this.mTimeStamp = date.getTime();
    }

    /**
     * Method isRead.
     * @return boolean
     */
    public boolean isRead() {
        return mReadStatus;
    }

    /**
     * Method setReadStatus.
     * @param status boolean
     */
    public void setReadStatus(boolean status) {
        this.mReadStatus = status;
    }

    /**
     * Method setSenderAddress.
     * @param senderAddr String
     */
    public void setSenderAddress(String senderAddr) {
        this.mSenderAddr = senderAddr;
    }

    /**
     * Method getSenderAddress.
     * @return String
     */
    public String getSenderAddress() {
        return mSenderAddr;
    }

    /**
     * Method readData.
     * @param data byte[]
     */
    public void readData(byte[] data) {
        try {
            ByteArrayInputStream bin = new ByteArrayInputStream(data);
            DataInputStream din = new DataInputStream(bin);
            mReadStatus = din.readBoolean();
            mMsg = din.readUTF();
            mSenderAddr = din.readUTF();
            mTimeStamp = din.readLong();
            din.close();
            bin.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Method writeData.
     * @return byte[]
     */
    public byte[] writeData() {
        byte[] data = null;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeBoolean(mReadStatus);
            dos.writeUTF(mMsg);
            dos.writeUTF(mSenderAddr);
            dos.writeLong(mTimeStamp);
            data = baos.toByteArray();
            dos.close();
            baos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return data;
    }
}
