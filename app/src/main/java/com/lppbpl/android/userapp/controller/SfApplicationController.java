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
import com.lppbpl.GetData;
import com.lppbpl.Measure;
import com.lppbpl.Response;
import com.lppbpl.ResponseType;
import com.lppbpl.SFMessaging;
import com.lppbpl.GetData.DataType;
import com.lppbpl.Measure.MeasurementAction;
import com.lppbpl.Response.ResStatusCode;
import com.lppbpl.SFMessaging.MessageType;
import com.lppbpl.android.userapp.bluetooth.BluetoothClient;
import com.lppbpl.android.userapp.listener.SfBTDataListener;
import com.lppbpl.android.userapp.listener.SfBluetoothStatusListener;
import com.lppbpl.android.userapp.model.SfApplicationModel;

// TODO: Auto-generated Javadoc
/**
 * The Class SfApplicationController.
 */
public final class SfApplicationController implements SfBluetoothStatusListener {

	/** The Constant TAG. */
	private final static String TAG = SfApplicationController.class
			.getSimpleName();

	/** The s instance. */
	private static SfApplicationController sInstance;

	/** The m model. */
	private SfApplicationModel mModel = null;

	/** The m send controller. */
	private SfSendController mSendController = null;

	/** The m is bt connected. */
	private boolean mIsBTConnected = false;

	/** The m is config ack received. */
	private boolean mIsConfigAckReceived = false;

	/** The data listener. */
	private SfBTDataListener dataListener;

	/**
	 * Instantiates a new sf application controller.
	 */
	private SfApplicationController() {
		mModel = new SfApplicationModel(this);
		Logger.log(Level.DEBUG, TAG, "App model created");
		mSendController = new SfSendController(this);
	}

	/**
	 * Method getSendController.
	 *
	 * @return SfSendController
	 */
	public SfSendController getSendController() {
		return mSendController;
	}

	/**
	 * Gets the single instance of SfApplicationController.
	 *
	 * @return the SfApplicationController singleton instance object.
	 */
	public final static SfApplicationController getInstance() {
		if (sInstance == null) {
			sInstance = new SfApplicationController();
		}
		return sInstance;
	}

	/**
	 * SfApplicationController singleton instance object made null every time
	 * when application paired with device.
	 */
	public static void releaseResources() {
		sInstance = null;
	}

	/**
	 * Release the resources close the running threads.
	 */
	public void close() {
		if (mModel.getBluetoothClient() != null) {
			mModel.getBluetoothClient().releaseResources();
		}
		mModel.close();
		sInstance = null;
	}

	/**
	 * Gets the app model.
	 *
	 * @return Application Model object
	 */
	public final SfApplicationModel getAppModel() {
		return mModel;
	}

	/**
	 * Gets the bluetooth client.
	 *
	 * @return BluetoothClient object.
	 */
	public final BluetoothClient getBluetoothClient() {
		return mModel.getBluetoothClient();
	}

	/**
	 * Checks if is device connected.
	 *
	 * @return true if Device connected
	 */
	public boolean isDeviceConnected() {
		return mIsBTConnected;
	}

	/**
	 * Set the Configuration Ack status.
	 *
	 * @param configAct
	 *            the new config ack recived
	 */
	public void setConfigAckRecived(boolean configAct) {
		mIsConfigAckReceived = configAct;
	}

	/**
	 * Checks if is config ack recived.
	 *
	 * @return true if Configuration Ack received after pairing the device.
	 */
	public boolean isConfigAckRecived() {
		return mIsConfigAckReceived;
	}

	/**
	 * Send Device data(like ID, BT status, Battery status etc)request after
	 * pairing happens.
	 */
	public void sendDeviceDataRequest() {
		Logger.log(Level.DEBUG, TAG, "send Device Data Request");
		// send the request and get the data back from Accessory
		final GetData getData = GetData.newBuilder().setDataType(DataType.DeviceData).build();

		Logger.log(Level.INFO,TAG,"***SFApplicationContyrolleer--getData***"+"="+getData);

		final SFMessaging message = SFMessaging.newBuilder()
				.setSfMsgType(MessageType.GetDataMsg).setSfGetdataMsg(getData)
				.build();

		Logger.log(Level.INFO,"***SFApplicationControlleer--message***",""+message);

		getBluetoothClient().sendCommand(message);
	}

	/**
	 * Send Shutdown message to device when application exits to close the
	 * Bluetooth connection.
	 */
	public void sendShutdownMsg() {
		final Response response = Response.newBuilder()
				.setResponseType(ResponseType.STT)
				.setSttCode(ResStatusCode.CLIENT_SHUTDOWN).build();
		final SFMessaging messaging = SFMessaging.newBuilder()
				.setSfMsgType(MessageType.ResponseMsg)
				.setSfResponseMsg(response).build();

		getBluetoothClient().sendCommand(messaging);
	}

	/**
	 * Cancle the measurement.
	 *
	 * @param measurementType
	 *            will be ECG or BG.
	 */
	public void sendCancelMsg(int measurementType) {
		final Measure measure = Measure.newBuilder()
				.setMeasurementType(measurementType).setDuration(10)
				.setMeasurementAction(MeasurementAction.CANCEL).build();

		final SFMessaging measureCancelMsg = SFMessaging.newBuilder()
				.setSfMsgType(MessageType.MeasureMsg).setSfMeasureMsg(measure)
				.build();

		getBluetoothClient().sendCommand(measureCancelMsg);
	}

	/**
	 * Method setConnectionStatus.
	 *
	 * @param connStatus
	 *            boolean
	 * @see SfBluetoothStatusListener#setConnectionStatus(boolean)
	 */
	@Override
	public void setConnectionStatus(boolean connStatus) {
		mIsBTConnected = connStatus;
	}

	/**
	 * Method dataReceived.
	 *
	 * @param response
	 *            Response
	 * @see SfBluetoothStatusListener#dataReceived(Response)
	 */
	@Override
	public void dataReceived(Response response) {
		if (dataListener != null) {
			dataListener.dataReceived(response);// sending to datarecieve in SFDATALISTNER
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.lppbpl.android.userapp.listener.SfBluetoothStatusListener#
	 * sendEmptyMessage(int)
	 */
	/**
	 * Method sendEmptyMessage.
	 *
	 * @param what
	 *            int
	 * @see SfBluetoothStatusListener#sendEmptyMessage(int)
	 */
	@Override
	public void sendEmptyMessage(int what) {
		if (dataListener != null) {
			Logger.log(Level.INFO, TAG,"** (Received Message)**"+what);
			dataListener.sendEmptyMessage(what);
		}
	}

	/**
	 * To set the Bluetooth listener.
	 *
	 * @param dataListener
	 *            the new bluetooth data listener
	 */
	public void setBluetoothDataListener(SfBTDataListener dataListener) {
		this.dataListener = dataListener;
	}

	/**
	 * Returns the Bluetooth listener object.
	 *
	 * @return SfBTDataListener
	 */
	public final SfBTDataListener getBluetoothDataListener() {
		return this.dataListener;
	}

	/**
	 * Below all methods used to avoid sending multiple message to device while
	 * activity is running.
	 */
	private boolean canSendMsg = true;

	/**
	 * Pause getting activity data.
	 */
	public void pauseGettingActivityData() {
		Logger.log(Level.DEBUG, TAG, "pauseGettingActivityData()");
		canSendMsg = false;
	}

	/**
	 * Resume getting activity data.
	 */
	public void resumeGettingActivityData() {
		Logger.log(Level.DEBUG, TAG, "resumeGettingActivityData()");
		canSendMsg = true;
	}

	/**
	 * Method canSendGetActivityDataRequest.
	 *
	 * @return boolean
	 */
	public boolean canSendGetActivityDataRequest() {
		return canSendMsg;
	}
}
