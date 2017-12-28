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

package com.logging;

import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * The Class Logger.
 */
public class Logger {

	/** The Constant EFFECTIVE_LOGLEVEL. */
	public static final int EFFECTIVE_LOGLEVEL = Level.DEBUG;

	/** The Constant ENABLE_LOGS. */
	public static final boolean ENABLE_LOGS = true;

	/** The s log file. */
	private static File sLogFile;

	/** The s f out writer. */
	private static OutputStreamWriter sFOutWriter;

	/** The s file exists. */
	private static boolean sFileExists = true;

	/**
	 * Method log.
	 *
	 * @param level
	 *            int
	 * @param className
	 *            String
	 * @param message
	 *            String
	 */
	public static void log(int level, String className, String message) {
		if (!ENABLE_LOGS)
			return;

		if (!(EFFECTIVE_LOGLEVEL <= level))
			return;

		if (message == null || className == null) {
			return;
		}

		if (level == Level.DEBUG) {
			Log.d(className, message);
		} else if (level == Level.INFO) {
			Log.i(className, message);
		} else if (level == Level.ERROR) {
			Log.e(className, message);
		} else if (level == Level.WARNING) {
			Log.w(className, message);
		}

		if (!Level.LOG_TO_FILE || !sFileExists)
			return;

		StringBuffer logSb = new StringBuffer();

		logSb.append(DateFormat.format("dd-MM-yyyy::hh:mm:ss",
				System.currentTimeMillis()));
		logSb.append('[').append(getLevelStr(level)).append(']')
				.append(className).append(">>").append(message);
		logSb.append("\r\n");

		try {

			if (sFOutWriter != null) {
				sFOutWriter.append(logSb.toString());
				sFOutWriter.flush();
				// fOutWriter.close();
				// fOut.close();
			}

		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}/*
		 * finally{ fOutWriter = null; }
		 */

	}

	/**
	 * Method init.
	 *
	 * @return boolean
	 */
	public static boolean init() {
		sLogFile = new File(Environment.getExternalStorageDirectory().getPath()
				+ "/LPPWL_userlog.txt");

		if (!sLogFile.exists()) {
			try {
				boolean ok = sLogFile.createNewFile();
				if (ok) {
					Log.i("Log", "file is created");
				} else {
					Log.i("Log", "file creation failed");
					return false;
				}
				// sLogFile.setWritable(true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			// Creting in append mode
			FileOutputStream fOut = new FileOutputStream(sLogFile, true);
			sFOutWriter = new OutputStreamWriter(fOut);
			sFileExists = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Method getLevelStr.
	 *
	 * @param level
	 *            int
	 * @return String
	 */
	public static String getLevelStr(int level) {
		String levelStr = Level.DEBUG_STR;
		/*switch (level) {
		case Level.DEBUG:
			levelStr = Level.DEBUG_STR;
			break;
		case Level.INFO:
			levelStr = Level.INFO_STR;
			break;
		case Level.WARNING:
			levelStr = Level.WARNING_STR;
			break;
		case Level.ERROR:
			levelStr = Level.ERROR_STR;
			break;
		}*/
		return levelStr;
	}

	/**
	 * Is this <code>Logger</code> enabled for DEBUG level?.
	 *
	 * @return true if logging is enabled.
	 */
	public static boolean isDebugEnabled() {
		int effectiveLevel = getEffectiveLevel();
		return effectiveLevel <= Level.DEBUG;
	}

	/**
	 * Is this <code>Logger</code> enabled for INFO level?.
	 *
	 * @return true if the <code>Level.INFO</code> level is enabled.
	 */
	public static boolean isInfoEnabled() {
		int effectiveLevel = getEffectiveLevel();
		return effectiveLevel <= Level.INFO;
	}

	/**
	 * Is this <code>Logger</code> enabled for <code>Level.WARNING</code> level?
	 *
	 *
	 * @return true if WARN level is enabled.
	 */
	public static boolean isWarnEnabled() {
		int effectiveLevel = getEffectiveLevel();
		return effectiveLevel <= Level.WARNING;
	}

	/**
	 * Is this LOGGER enabled for ERROR level?.
	 *
	 * @return true if the ERROR level is enabled.
	 */
	public static boolean isErrorEnabled() {
		int effectiveLevel = getEffectiveLevel();
		return effectiveLevel <= Level.ERROR;
	}

	/**
	 * Method getEffectiveLevel.
	 *
	 * @return int
	 */
	public static int getEffectiveLevel() {
		return EFFECTIVE_LOGLEVEL;
	}

	/**
	 * Close.
	 */
	public static void close() {
		try {

			if (sFOutWriter != null) {
				sFOutWriter.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
