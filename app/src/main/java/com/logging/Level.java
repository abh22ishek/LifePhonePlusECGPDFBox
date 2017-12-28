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

/**
 * The Interface Level.
 */
public interface Level {
	/**
	 * Enable all logging.
	 */
	public static final int DEBUG = 1;

	/** The Constant INFO. */
	public static final int INFO = 2;

	/** The Constant WARNING. */
	public static final int WARNING = 3;

	/** The Constant ERROR. */
	public static final int ERROR = 4;

	/** The Constant OFF. */
	public static final int OFF = 5;

	/** The Constant DEBUG_STR. */
	public static final String DEBUG_STR = "DEBUG";

	/** The Constant INFO_STR. */
	public static final String INFO_STR = "INFO";

	/** The Constant WARNING_STR. */
	public static final String WARNING_STR = "WARNING";

	/** The Constant ERROR_STR. */
	public static final String ERROR_STR = "ERROR";

	/** The Constant LOG_TO_FILE. */
	public static final boolean LOG_TO_FILE = true;

	/** The Constant LOG_TO_CONSOLE. */
	public static final boolean LOG_TO_CONSOLE = true;
}
