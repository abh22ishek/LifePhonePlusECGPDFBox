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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;
import android.os.Process;

import com.logging.Level;
import com.logging.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class ForceCloseHandler.
 */
public class ForceCloseHandler implements UncaughtExceptionHandler {

	// private Context appContext = null;

	/**
	 * Constructor for ForceCloseHandler.
	 *
	 * @param context
	 *            Context
	 */
	public ForceCloseHandler(Context context) {
		// appContext = context;
	}

	/**
	 * Method uncaughtException.
	 *
	 * @param thread
	 *            Thread
	 * @param exception
	 *            Throwable
	 * @see java.lang.Thread$UncaughtExceptionHandler#uncaughtException(Thread,
	 *      Throwable)
	 */
	public void uncaughtException(Thread thread, Throwable exception) {

		final StringWriter exceptionStacktrace = new StringWriter();
		exception.printStackTrace(new PrintWriter(exceptionStacktrace));

		Logger.log(Level.ERROR, "ForceCloseHandler",
				exceptionStacktrace.toString());

		/*
		 * AlertDialog.Builder builder = new AlertDialog.Builder(appContext);
		 * builder.setMessage(exceptionStacktrace.toString())
		 * .setCancelable(false) .setPositiveButton("OK", new
		 * DialogInterface.OnClickListener() { public void
		 * onClick(DialogInterface dialog, int id) {
		 * SfApplicationController.getInstance().close();
		 * Process.killProcess(Process.myPid()); System.exit(10); } });
		 * AlertDialog alert = builder.create(); alert.show();
		 */

		SfApplicationController.getInstance().close();
		Process.killProcess(Process.myPid());
		System.exit(2);
	}
}
