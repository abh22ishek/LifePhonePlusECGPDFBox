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

package com.lppbpl.android.userapp;

import android.app.Application;
import android.content.IntentFilter;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.controller.ForceCloseHandler;
import com.lppbpl.android.userapp.controller.SfApplicationController;

// TODO: Auto-generated Javadoc
/**
 * THis is main class of the application. It is a entry point to the application
 */
public class SouthFallsUserApp extends Application {

	/** The s instance. */
	private static SouthFallsUserApp INSTANCE = null;

	// private BTStatusReceiver statusReceiver = null;
	/** The intent filter. */
	private IntentFilter intentFilter = null;

	public SouthFallsUserApp() {

	}

	/**
	 * Method getInstance.
	 *
	 * @return SouthFallsUserApp
	 */
	public static SouthFallsUserApp getInstance() {
		return INSTANCE;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		INSTANCE = this;
		Logger.init();
		intentFilter = createIntentFilter();
		Thread.setDefaultUncaughtExceptionHandler(new ForceCloseHandler(
				getApplicationContext()));
	}

	/**
	 * Method createIntentFilter.
	 *
	 * @return IntentFilter
	 */
	private static IntentFilter createIntentFilter() {
		final IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.FINISH_ALL_ACTIVITIES);
		filter.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
		filter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
		return filter;
	}

	/**
	 * Method getIntentFilter.
	 *
	 * @return IntentFilter
	 */
	public IntentFilter getIntentFilter() {
		return intentFilter;
	}

	/**
	 * Method onLowMemory.
	 *
	 * @see android.content.ComponentCallbacks#onLowMemory()
	 */
	@Override
	public void onLowMemory() {
		Logger.log(Level.ERROR, "SouthFallsUserApp", "onLowMemory()");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Application#onTerminate()
	 */
	@Override
	public void onTerminate() {
		Logger.log(Level.DEBUG, "SouthFallsUserApp", "onTerminate()");
		// this.unregisterReceiver(statusReceiver);
		SfApplicationController.getInstance().close();
		// Logger.close();
	}
}
