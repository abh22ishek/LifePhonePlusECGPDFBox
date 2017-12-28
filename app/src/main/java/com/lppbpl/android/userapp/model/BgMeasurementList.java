/*
 *
 */
package com.lppbpl.android.userapp.model;

// TODO: Auto-generated Javadoc
/**
 * The Class BgMeasurementList.
 */
public class BgMeasurementList {

	/** The measurement id. */
	private final int measurementId;

	/** The measurement time. */
	private final long measurementTime;

	/**
	 * Constructor for BgMeasurementList.
	 * @param measurementId int
	 * @param measurementTime long
	 */
	public BgMeasurementList(final int measurementId, final long measurementTime) {
		this.measurementId = measurementId;
		this.measurementTime = measurementTime;
	}

	/**
	 * Method getBgMeasurementId.
	 * @return int
	 */
	public int getBgMeasurementId() {
		return measurementId;
	}

	/**
	 * Method getBgMeasurementTime.
	 * @return long
	 */
	public long getBgMeasurementTime() {
		return measurementTime;
	}

}
