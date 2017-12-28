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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.BgData;
import com.lppbpl.BgReadingType;

import java.util.Vector;

// TODO: Auto-generated Javadoc
/**
 * This is a view component to display Blood Sugar Graph.
 */
public class BgGraphComponent extends View {

	/** The m vertical gap. */
	final private int mVerticalGap = 50;

	/** The m width. */
	final private int mWidth;

	/** The m height. */
	private int mHeight;

	/** The m paint. */
	private Paint mPaint = null;

	/** The m received bg data. */
	private BGUploadedData mReceivedBGData = null;

	/** The m canvas. */
	private Canvas mCanvas;

	/** The m graph img. */
	private Bitmap mGraphImg;

	/** The m do paint. */
	private boolean mDoPaint;

	/** The Constant MAX_DIFFERENT_BETWEEN_POINTS. */
	private final static int MAX_DIFFERENT_BETWEEN_POINTS = 75;

	/**
	 * Constructor for BgGraphComponent.
	 *
	 * @param context
	 *            Context
	 * @param attrs
	 *            AttributeSet
	 */
	public BgGraphComponent(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
		mWidth = getResources().getDisplayMetrics().widthPixels;
		mHeight = getResources().getDisplayMetrics().heightPixels;
		setMinimumHeight(mHeight);
		setMinimumWidth(mWidth);
	}

	/**
	 * Constructor for BgGraphComponent.
	 *
	 * @param context
	 *            Context
	 */
	public BgGraphComponent(Context context) {
		super(context);
		mPaint = new Paint();
		mWidth = getResources().getDisplayMetrics().widthPixels;
		mHeight = getResources().getDisplayMetrics().heightPixels;
		setMinimumHeight(mHeight);
		setMinimumWidth(mWidth);
	}

	/**
	 * Set data to graph.
	 *
	 * @param retrivedData
	 *            the retrived data
	 * @param timeStampData
	 *            the time stamp data
	 */
	public void setData(Vector<BgData> retrivedData, Vector<Long> timeStampData) {
		mReceivedBGData = new BGUploadedData();
		mReceivedBGData.setAreaWidth(mWidth / 3);
		mReceivedBGData.filterBasedMeasurementType(retrivedData, timeStampData);
		final int tHeight = mHeight;
		// if ((verticalGap * receivedBGData.getBgDrawData().length - 1) >
		// height) {
		mHeight = (mVerticalGap * mReceivedBGData.getBgDrawDataLength() - 1)
				+ 2 * mVerticalGap;
		// }
		Logger.log(Level.DEBUG, "BGGraph", "retrivedData length="
				+ retrivedData.size());
		int lastXPostCord = -1;
		int lastXFastCord = -1;
		int lastXRandomCord = -1;

		int pX1 = 0;
		int pX2 = 0;
		int temp = 0;
		for (int i = 0; i < mReceivedBGData.getBgDrawDataLength(); i++) {
			pX1 = 0;
			pX2 = mReceivedBGData.getBgDrawData_xCord(i);
			if (mReceivedBGData.getBgDrawData_ReadingType(i) == BgReadingType.RANDOM
					&& lastXRandomCord != -1) {
				pX1 = lastXRandomCord;
				lastXRandomCord = pX2;
			} else if (mReceivedBGData.getBgDrawData_ReadingType(i) == BgReadingType.POST
					&& lastXPostCord != -1) {
				pX1 = lastXPostCord;
				lastXPostCord = pX2;
			} else if (mReceivedBGData.getBgDrawData_ReadingType(i) == BgReadingType.FASTING
					&& lastXFastCord != -1) {
				pX1 = lastXFastCord;
				lastXFastCord = pX2;
			} else if (mReceivedBGData.getBgDrawData_ReadingType(i) == BgReadingType.RANDOM
					&& lastXRandomCord == -1) {
				lastXRandomCord = mReceivedBGData.getBgDrawData_xCord(i);
			} else if (mReceivedBGData.getBgDrawData_ReadingType(i) == BgReadingType.POST
					&& lastXPostCord == -1) {
				lastXPostCord = mReceivedBGData.getBgDrawData_xCord(i);
			} else if (mReceivedBGData.getBgDrawData_ReadingType(i) == BgReadingType.FASTING
					&& lastXFastCord == -1) {
				lastXFastCord = mReceivedBGData.getBgDrawData_xCord(i);
			}
			if (Math.abs(pX1 - pX2) > MAX_DIFFERENT_BETWEEN_POINTS) {
				temp = Math.abs(pX1 - pX2) / MAX_DIFFERENT_BETWEEN_POINTS;
				temp = temp > 3 ? 3 : temp;
				mHeight += temp * mVerticalGap - mVerticalGap;
			}
		}

		if (tHeight > mHeight) {
			mHeight = tHeight;
		}
		setMinimumHeight(mHeight);
		setMinimumWidth(mWidth);
	}

	/**
	 * Draw the graph lines.
	 *
	 * @param g
	 *            the g
	 * @param moderatePortionStart
	 *            the moderate portion start
	 * @param startX
	 *            the start x
	 * @param height
	 *            the height
	 * @param color
	 *            the color
	 * @param graphVLineStartAt
	 *            the graph v line start at
	 * @param linesGapPx
	 *            the lines gap px
	 * @param graphHLineStartAt
	 *            the graph h line start at
	 */
	private void drawRegions(Canvas g, int moderatePortionStart, int startX,
			int height, int color, final int graphVLineStartAt, int linesGapPx,
			final int graphHLineStartAt) {

		int verLine = graphVLineStartAt;
		int horLine = graphHLineStartAt;
		mPaint.setColor(color);
		while (verLine <= height) {
			g.drawLine(startX, verLine, moderatePortionStart, verLine, mPaint);
			verLine += linesGapPx;
		}
		while (horLine <= moderatePortionStart) {
			g.drawLine(horLine, 0, horLine, getHeight() + 0, mPaint);
			horLine += linesGapPx;
		}
	}

	/** The rect1. */
	private Rect rect1 = new Rect(0, 0, getWidth(), getHeight());

	/** The rect2. */
	private Rect rect2 = new Rect(0, 0, getWidth(), getHeight());

	/**
	 * Method onDraw.
	 *
	 * @param c
	 *            Canvas
	 */
	@Override
	protected void onDraw(Canvas c) {
		super.onDraw(c);
		if (null == mGraphImg) {
			mGraphImg = Bitmap.createBitmap(this.getWidth(), this.getHeight(),
					Bitmap.Config.ARGB_8888);

			rect1 = new Rect(0, 0, getWidth(), getHeight());
			rect2 = new Rect(0, 0, getWidth(), getHeight());
		}

		if (null == mCanvas) {
			mCanvas = new Canvas(mGraphImg);
		}

		if (null != mGraphImg && mDoPaint) {
			c.drawBitmap(mGraphImg, rect1, rect2, mPaint);
			return;
		}
		paint(mCanvas);

		c.drawBitmap(mGraphImg, rect1, rect2, mPaint);
		mDoPaint = true;
	}

	/**
	 * Draw the graph on Canvas.
	 *
	 * @param g
	 *            the g
	 */
	public void paint(Canvas g) {
		try {
			final float fontSize = getResources().getDimension(
					R.dimen.graph_font_size);// *
												// (getResources().getDisplayMetrics().densityDpi
												// /
												// (float)160)
												// ;
			mPaint.setTextSize(fontSize);
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			mPaint.setStrokeWidth(1);
		} catch (Exception e) { // resource not found exception
			Logger.log(Level.ERROR, "Paint", "" + e);
		}
		mPaint.setColor(0xFFE5E5E5);
		g.drawRect(new RectF(0, 0, mWidth, mHeight), mPaint);
		final int measurementCategoryAreaWidth = getWidth() / 3;
		final int linesGapPx = 10;
		final int graphVLineStartAt = 0;

		drawRegions(g, measurementCategoryAreaWidth, 0, mHeight, 0xFFD3D9DF,
				graphVLineStartAt, linesGapPx, 0);

		g.drawRect(new RectF(measurementCategoryAreaWidth, 0,
				measurementCategoryAreaWidth + measurementCategoryAreaWidth,
				mHeight), mPaint);

		drawRegions(g, 2 * measurementCategoryAreaWidth,
				measurementCategoryAreaWidth, mHeight, 0xFFE5E5E5,
				graphVLineStartAt, linesGapPx, measurementCategoryAreaWidth);

		drawRegions(g, getWidth(), 2 * measurementCategoryAreaWidth, mHeight,
				0xFFD3D9DF, graphVLineStartAt, linesGapPx,
				2 * measurementCategoryAreaWidth);

		if (null == mReceivedBGData) {
			return;
		}
		int xStart = 2;
		int yStart = 5 + (int) mPaint.getTextSize();
		final int rectW = 20;
		final int rectH = 20;
		final int padding = 10;

		mPaint.setColor(mReceivedBGData.getDrawColor(BgReadingType.FASTING));
		g.drawRect(new RectF(xStart, yStart, xStart + rectW, rectH), mPaint);
		xStart += rectW;
		xStart += padding;
		g.drawText("FAST", xStart, yStart, mPaint);

		xStart = measurementCategoryAreaWidth;
		mPaint.setColor(mReceivedBGData.getDrawColor(BgReadingType.RANDOM));
		g.drawRect(new RectF(xStart, yStart, xStart + rectW, rectH), mPaint);
		xStart += rectW;
		xStart += padding;
		g.drawText("RANDOM", xStart, yStart, mPaint);

		xStart = 2 * measurementCategoryAreaWidth;
		mPaint.setColor(mReceivedBGData.getDrawColor(BgReadingType.POST));
		g.drawRect(new RectF(xStart, yStart, xStart + rectW, rectH), mPaint);
		xStart += rectW;
		xStart += padding;
		g.drawText("POST", xStart, yStart, mPaint);
		yStart += mPaint.getTextSize();

		/* Draw three lines one is */
		mPaint.setColor(0xFF000000);// Balck color
		final String mLowBGReadingStr = "Low";
		int mStrWidth = (int) mPaint.measureText(mLowBGReadingStr);
		int mMarginLeft = (measurementCategoryAreaWidth - mStrWidth) >> 1;
		g.drawText(mLowBGReadingStr, mMarginLeft, yStart, mPaint);

		final String mMediumBGReadingStr = "Medium";
		mStrWidth = (int) mPaint.measureText(mMediumBGReadingStr);
		mMarginLeft = (measurementCategoryAreaWidth - mStrWidth) >> 1;
		g.drawText(mMediumBGReadingStr, mMarginLeft
				+ measurementCategoryAreaWidth, yStart, mPaint);

		final String mHighBGReadingStr = "High";
		mStrWidth = (int) mPaint.measureText(mHighBGReadingStr);
		mMarginLeft = (measurementCategoryAreaWidth - mStrWidth) >> 1;
		g.drawText(mHighBGReadingStr, mMarginLeft
				+ (measurementCategoryAreaWidth << 1), yStart, mPaint);

		int yCordStart = mVerticalGap + yStart;
		String displayDate = null;

		int lastXPostCord = -1;
		int lastXFastCord = -1;
		int lastXRandomCord = -1;

		int lastYPostCord = -1;
		int lastYFastCord = -1;
		int lastYRandomCord = -1;

		String currDate = null;
		int tX = 0;
		String tempStr = null;
		int tempX = 0;
		int pX1 = 0;
		int pX2 = 0;
		int temp = 0;
		for (int i = 0; i < mReceivedBGData.getBgDrawDataLength(); i++) {
			currDate = mReceivedBGData.getBgDrawData_timeStamp(i);
			if (null == displayDate || !currDate.equals(displayDate)) {
				mPaint.setColor(0xFF626257);
				tX = (getWidth() - (int) mPaint.measureText(mReceivedBGData
						.getBgDrawData_timeStamp(i))) >> 1;
				g.drawText(mReceivedBGData.getBgDrawData_timeStamp(i), tX,
						yCordStart - mPaint.getTextSize(), mPaint);
				// yCordStart+= verticalGap;
				displayDate = mReceivedBGData.getBgDrawData_timeStamp(i);
			}

			mPaint.setColor(mReceivedBGData.getDrawColor(mReceivedBGData
					.getBgDrawData_ReadingType(i)));
			tempStr = Integer
					.toString(mReceivedBGData.getBgDrawData_Reading(i));
			tempX = mReceivedBGData.getBgDrawData_xCord(i)
					+ (int) mPaint.measureText(tempStr);
			if (tempX > getWidth()) {
				tempX = getWidth() - (int) mPaint.measureText(tempStr) - 10;// -10
																			// for
																			// scroll
																			// width
			} else {
				tempX = mReceivedBGData.getBgDrawData_xCord(i);
			}
			pX1 = 0;
			pX2 = mReceivedBGData.getBgDrawData_xCord(i);
			if (mReceivedBGData.getBgDrawData_ReadingType(i) == BgReadingType.RANDOM
					&& lastXRandomCord != -1) {
				pX1 = lastXRandomCord;
			} else if (mReceivedBGData.getBgDrawData_ReadingType(i) == BgReadingType.POST
					&& lastXPostCord != -1) {
				pX1 = lastXPostCord;
			} else if (mReceivedBGData.getBgDrawData_ReadingType(i) == BgReadingType.FASTING
					&& lastXFastCord != -1) {
				pX1 = lastXFastCord;
			}

			if (pX1 > 0 && Math.abs(pX1 - pX2) > MAX_DIFFERENT_BETWEEN_POINTS) {
				temp = Math.abs(pX1 - pX2) / MAX_DIFFERENT_BETWEEN_POINTS;
				temp = temp > 3 ? 3 : temp;
				yCordStart += temp * mVerticalGap - mVerticalGap;
			}

			if (mReceivedBGData.getBgDrawData_ReadingType(i) == BgReadingType.RANDOM
					&& lastXRandomCord != -1
					|| mReceivedBGData.getBgDrawData_ReadingType(i) == BgReadingType.POST
					&& lastXPostCord != -1
					|| mReceivedBGData.getBgDrawData_ReadingType(i) == BgReadingType.FASTING
					&& lastXFastCord != -1) {
				g.drawText(tempStr, tempX, yCordStart + mVerticalGap, mPaint);
			} else {
				g.drawText(tempStr, tempX, yCordStart, mPaint);
			}

			if (mReceivedBGData.getBgDrawData_ReadingType(i) == BgReadingType.FASTING) {
				if (lastXFastCord == -1) {
					lastXFastCord = mReceivedBGData.getBgDrawData_xCord(i);
					lastYFastCord = yCordStart;
				} else {
					g.drawLine(lastXFastCord, lastYFastCord,
							mReceivedBGData.getBgDrawData_xCord(i), yCordStart
									+ mVerticalGap, mPaint);
					lastXFastCord = mReceivedBGData.getBgDrawData_xCord(i);
					lastYFastCord = yCordStart + mVerticalGap;
				}
			} else if (mReceivedBGData.getBgDrawData_ReadingType(i) == BgReadingType.POST) {
				if (lastXPostCord == -1) {
					lastXPostCord = mReceivedBGData.getBgDrawData_xCord(i);
					lastYPostCord = yCordStart;
				} else {
					g.drawLine(lastXPostCord, lastYPostCord,
							mReceivedBGData.getBgDrawData_xCord(i), yCordStart
									+ mVerticalGap, mPaint);
					lastXPostCord = mReceivedBGData.getBgDrawData_xCord(i);
					lastYPostCord = yCordStart + mVerticalGap;
				}
			} else if (mReceivedBGData.getBgDrawData_ReadingType(i) == BgReadingType.RANDOM) {
				if (lastXRandomCord == -1) {
					lastXRandomCord = mReceivedBGData.getBgDrawData_xCord(i);
					lastYRandomCord = yCordStart;
				} else {
					g.drawLine(lastXRandomCord, lastYRandomCord,
							mReceivedBGData.getBgDrawData_xCord(i), yCordStart
									+ mVerticalGap, mPaint);
					lastXRandomCord = mReceivedBGData.getBgDrawData_xCord(i);
					lastYRandomCord = yCordStart + mVerticalGap;
				}
			}
			// g.setFont(g.getFont().createSystemFont(Font.FACE_MONOSPACE,
			// Font.STYLE_ITALIC,Font.SIZE_SMALL));
			yCordStart += mVerticalGap;
		}
	}

}

/**
 */
class BgDataWithTimeStamp {
	protected int bgReading;

	protected int bgReadingType;

	protected int xCord;

	protected String timeStamp;
}

/**
 */
class BGUploadedData {

	private BgDataWithTimeStamp[] bgReadingWithTime;

	final private int postPrandialThresholdLower = 100;

	final private int postPrandialThresholdMedium = 160;

	final private int fastThresholdLower = 60;

	final private int fastThresholdMedium = 110;

	final private int randomThresholdLower = 60;

	final private int randomThresholdMedium = 200;

	private int areaWidth = 0;

	/**
	 * Method setAreaWidth.
	 *
	 * @param area
	 *            int
	 */
	public void setAreaWidth(int area) {
		areaWidth = area;
	}

	/**
	 * Returns the color code
	 *
	 * @param measureType
	 *
	 * @return int
	 */
	public int getDrawColor(int measureType) {
		int color = 0;
		if (measureType == BgReadingType.POST) {
			color = 0xFFED5314;
		} else if (measureType == BgReadingType.FASTING) {
			color = 0xFF3ABBC9;
		} else if (measureType == BgReadingType.RANDOM) {
			color = 0xFF828277;
		}
		return color;
	}

	/**
	 * Method getReadingValue.
	 *
	 * @param readingVal
	 *            int
	 * @param readingType
	 *            int
	 * @return int
	 */
	public int getReadingValue(int readingVal, int readingType) {
		int retVal = 0;
		final int mediumRegionWidth = 2 * areaWidth;
		final int highRegionWidth = 3 * areaWidth;

		if (readingType == BgReadingType.FASTING) {
			if (readingVal <= fastThresholdLower) {
				final float rationBetweenBelowLimit = (float) areaWidth
						/ (float) fastThresholdLower;
				retVal = ((int) ((float) readingVal * rationBetweenBelowLimit));
			} else if (fastThresholdMedium >= readingVal
					&& (readingVal >= fastThresholdLower)) {
				final float rationBetweenMediumLimit = ((float) mediumRegionWidth - (float) areaWidth)
						/ ((float) fastThresholdMedium - (float) fastThresholdLower);
				retVal = ((int) (areaWidth + (rationBetweenMediumLimit * (float) (readingVal - fastThresholdLower))));
			} else {
				final float rationBetweenHigherLimit = ((float) highRegionWidth - (float) mediumRegionWidth)
						/ ((float) highRegionWidth - (float) fastThresholdMedium);
				final int returnVal = (int) (mediumRegionWidth + rationBetweenHigherLimit
						* (float) (readingVal - fastThresholdLower));
				retVal = (returnVal > highRegionWidth) ? highRegionWidth
						: returnVal;
			}
		} else if (readingType == BgReadingType.RANDOM) {
			if (readingVal <= randomThresholdLower) {
				final float rationBetweenBelowLimit = (float) areaWidth
						/ (float) randomThresholdLower;
				retVal = ((int) ((float) readingVal * rationBetweenBelowLimit));
			} else if (randomThresholdMedium >= readingVal
					&& (readingVal >= randomThresholdLower)) {
				final float rationBetweenMediumLimit = ((float) mediumRegionWidth - (float) areaWidth)
						/ ((float) randomThresholdMedium - (float) randomThresholdLower);
				retVal = ((int) (areaWidth + rationBetweenMediumLimit
						* (float) (readingVal - randomThresholdLower)));
			} else {
				final float rationBetweenHigherLimit = ((float) highRegionWidth - (float) mediumRegionWidth)
						/ ((float) highRegionWidth - (float) randomThresholdMedium);
				final int returnVal = (int) (mediumRegionWidth + rationBetweenHigherLimit
						* (float) (readingVal - randomThresholdMedium));
				retVal = (returnVal > highRegionWidth) ? highRegionWidth
						: returnVal;
			}
		} else if (readingType == BgReadingType.POST) {
			if (readingVal <= postPrandialThresholdLower) {
				final float rationBetweenBelowLimit = (float) areaWidth
						/ (float) postPrandialThresholdLower;
				retVal = ((int) ((float) readingVal * rationBetweenBelowLimit));
			} else if (postPrandialThresholdMedium >= readingVal
					&& (readingVal >= postPrandialThresholdLower)) {

				final float rationBetweenMediumLimit = ((float) mediumRegionWidth - (float) areaWidth)
						/ ((float) postPrandialThresholdMedium - (float) postPrandialThresholdLower);
				retVal = ((int) (areaWidth + (rationBetweenMediumLimit * (float) (readingVal - postPrandialThresholdLower))));
			} else {

				final float rationBetweenHigherLimit = ((float) highRegionWidth - (float) mediumRegionWidth)
						/ ((float) highRegionWidth - (float) postPrandialThresholdMedium);
				final int returnVal = (int) (mediumRegionWidth + rationBetweenHigherLimit
						* (float) (readingVal - postPrandialThresholdLower));
				retVal = (returnVal > highRegionWidth) ? highRegionWidth
						: returnVal;
			}
		}
		return retVal;
	}

	public int getBgDrawData_Reading(int index) {
		return bgReadingWithTime[index].bgReading;
	}

	public int getBgDrawData_ReadingType(int index) {
		return bgReadingWithTime[index].bgReadingType;
	}

	public int getBgDrawData_xCord(int index) {
		return bgReadingWithTime[index].xCord;
	}

	public String getBgDrawData_timeStamp(int index) {
		return bgReadingWithTime[index].timeStamp;
	}

	public int getBgDrawDataLength() {
		return bgReadingWithTime.length;
	}

	/**
	 * Method filterBasedMeasurementType.
	 *
	 * @param retrievedData
	 *            Vector<BgData>
	 * @param timeStampData
	 *            Vector<Long>
	 */
	public void filterBasedMeasurementType(final Vector<BgData> retrievedData,
			final Vector<Long> timeStampData) {

		bgReadingWithTime = new BgDataWithTimeStamp[retrievedData.size()];// Contains
																			// all
																			// the
																			// Data.
		BgData samples = null;
		String dateStr = null;
		for (int i = 0; i < retrievedData.size(); i++) {
			samples = retrievedData.elementAt(i);
			bgReadingWithTime[i] = new BgDataWithTimeStamp();
			bgReadingWithTime[i].bgReading = (int) samples.getBgReading();
			bgReadingWithTime[i].bgReadingType = samples.getBgReadingType();
			dateStr = DateFormat.format("dd MMM yyyy",
					timeStampData.elementAt(i).longValue()).toString();
			bgReadingWithTime[i].timeStamp = dateStr;
			bgReadingWithTime[i].xCord = getReadingValue(
					bgReadingWithTime[i].bgReading,
					bgReadingWithTime[i].bgReadingType);
		}
	}
}
