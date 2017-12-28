/*
 *
 */
package com.lppbpl;
// TODO: Auto-generated Javadoc
// Generated by proto2javame, Thu May 02 15:16:35 IST 2013.

/**
 * The Class ResponseType.
 */
public final class ResponseType {

	/** The Constant ERR. */
	public static final int ERR = 1;

	/** The Constant VER. */
	public static final int VER = 2;

	/** The Constant ACK. */
	public static final int ACK = 3;

	/** The Constant EOD. */
	public static final int EOD = 4;

	/** The Constant DVD. */
	public static final int DVD = 5;

	/** The Constant SRD. sensor data*/
	public static final int SRD = 6;

	/** The Constant STT. */
	public static final int STT = 7;

	/** The Constant TST. */
	public static final int TST = 8;

	/**
	 * Method getStringValue.
	 * @param value int
	 * @return String
	 */
	public static String getStringValue(int value) {
		String retValue;

		switch(value) {
			case 1:
				retValue = "ERR";
				break;
			case 2:
				retValue = "VER";
				break;
			case 3:
				retValue = "ACK";
				break;
			case 4:
				retValue = "EOD";
				break;
			case 5:
				retValue = "DVD";
				break;
			case 6:
				retValue = "SRD";
				break;
			case 7:
				retValue = "STT";
				break;
			case 8:
				retValue = "TST";
				break;
			default:
				retValue = "";
				break;
		}

		return retValue;
	}
}