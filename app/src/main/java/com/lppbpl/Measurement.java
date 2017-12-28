/*
 *
 */
package com.lppbpl;
// TODO: Auto-generated Javadoc
// Generated by proto2javame, Mon Jul 23 20:06:21 IST 2012.

/**
 * The Class Measurement.
 */
public final class Measurement {

	/** The Constant ECG. */
	public static final int ECG = 1;

	/** The Constant HR. */
	public static final int HR = 2;

	/** The Constant ACT. */
	public static final int ACT = 3;

	/** The Constant TEMP. */
	public static final int TEMP = 4;

	/** The Constant BG. */
	public static final int BG = 5;

	/** The Constant CACHED. */
	public static final int CACHED = 6;

	/**
	 * Method getStringValue.
	 * @param value int
	 * @return String
	 */
	public static String getStringValue(int value) {
		String retValue;

		switch(value) {
			case 1:
				retValue = "ECG";
				break;
			case 2:
				retValue = "HR";
				break;
			case 3:
				retValue = "ACT";
				break;
			case 4:
				retValue = "TEMP";
				break;
			case 5:
				retValue = "BG";
				break;
			case 6:
				retValue = "CACHED";
				break;
			default:
				retValue = "";
				break;
		}

		return retValue;
	}
}