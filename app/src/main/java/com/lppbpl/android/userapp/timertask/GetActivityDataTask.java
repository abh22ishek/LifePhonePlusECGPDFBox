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

package com.lppbpl.android.userapp.timertask;

import java.util.TimerTask;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.GetData;
import com.lppbpl.Measurement;
import com.lppbpl.SFMessaging;
import com.lppbpl.GetData.DataType;
import com.lppbpl.SFMessaging.MessageType;
import com.lppbpl.android.userapp.controller.SfApplicationController;

// TODO: Auto-generated Javadoc
/**
 * The Class GetActivityDataTask.
 */
public class GetActivityDataTask extends TimerTask {

    /** The m controller. */
    private SfApplicationController mController;

    /** The m counter. */
    private int mCounter = 0;

    /**
     * Constructor for GetActivityDataTask.
     * @param controller SfApplicationController
     */
    public GetActivityDataTask(SfApplicationController controller) {
        this.mController = controller;
        mCounter = 0;
    }

    /**
     * Method run.
     * @see Runnable#run()
     */
    public void run() {
        if (mController.canSendGetActivityDataRequest()) {
        	// Timer to send the request to get activity data
            final GetData getActivityData = GetData.newBuilder().setDataType(DataType.SensorData)
                    .setMeasurementType(Measurement.ACT).build();
            final SFMessaging message = SFMessaging.newBuilder()
                    .setSfMsgType(MessageType.GetDataMsg).setSfGetdataMsg(getActivityData).build();
            mController.getBluetoothClient().sendCommand(message);
            Logger.log(Level.DEBUG, "GetActivityDataTask",
                    "GetActivityData request sent to accessory, request num:" + mCounter++);
        }
    }
}
