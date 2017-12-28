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

import java.util.Vector;

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
import com.lppbpl.ActivityData;
import com.lppbpl.android.userapp.R;

// TODO: Auto-generated Javadoc
/**
 * This Class is the view component to draw the Activity measurements graph.
 *
 */
public class ActGraphComponent extends View {

	/** The Constant TAG. */
	private static final String TAG = ActGraphComponent.class.getName();

	/** The m paint. */
	private final Paint mPaint;

	/** The m width. */
	private int mHeight, mWidth;

	/** The m y cord gap. */
	private int mYCordGap = 40;

	/** The Constant MAX_DIFFERENT_BETWEEN_POINTS. */
	private static final int MAX_DIFFERENT_BETWEEN_POINTS = 75;

	/** The m energy burnt. */
	private int[] mEnergyBurnt;

	/** The m time data. */
	private Vector<Long> mTimeData;

	/** The m graph img. */
	private Bitmap mGraphImg;

	/** The m canvas. */
	private Canvas mCanvas;

	/** The m do paint. */
	private boolean mDoPaint;

	/** The curr date. */
	private String currDate;

	/** The rect1. */
	private final Rect rect1 = new Rect(0, 0, getWidth(), getHeight());

	/** The rect2. */
	private final Rect rect2 = new Rect(0, 0, getWidth(), getHeight());

	/** The rect3. */
	private final Rect rect3 = new Rect(0, 0, getWidth(), getHeight());

	/** The rect4. */
	private final Rect rect4 = new Rect(0, 0, getWidth(), getHeight());

	/**
	 * Constructor for ActGraphComponent.
	 *
	 * @param context
	 *            Context
	 * @param attrs
	 *            AttributeSet
	 */
	public ActGraphComponent(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
		mWidth = getResources().getDisplayMetrics().widthPixels;
		mHeight = getResources().getDisplayMetrics().heightPixels;
		setMinimumHeight(mHeight);
		setMinimumWidth(mWidth);
	}

	/**
	 * Constructor for ActGraphComponent.
	 *
	 * @param context
	 *            Context
	 */
	public ActGraphComponent(Context context) {
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
	 * @param dataVector
	 *            the data vector
	 * @param timeData
	 *            the time data
	 */
	public void setData(final Vector<ActivityData> dataVector,
			final Vector<Long> timeData) {
		mTimeData = timeData;
		Logger.log(Level.DEBUG, TAG, "this.dataVector=" + dataVector.size());
		Logger.log(Level.DEBUG, TAG, "this.timeData size=" + mTimeData.size());

		mWidth = getResources().getDisplayMetrics().widthPixels;
		mHeight = getResources().getDisplayMetrics().heightPixels;
		mEnergyBurnt = new int[dataVector.size()];// {12,0,15,7,800,250,600,100,2,900,710};
		ActivityData actData = null;
		for (int i = 0; i < mEnergyBurnt.length; i++) {
			actData = dataVector.elementAt(i);
			mEnergyBurnt[i] = actData.getTotalEnergy();
		}
		final float fontSize = getResources().getDimension(
				R.dimen.graph_font_size)
				* (getResources().getDisplayMetrics().densityDpi / (float) 160);
		mYCordGap = 2 * (int) fontSize;

		if (mEnergyBurnt.length * mYCordGap > mHeight) {
			mHeight = mEnergyBurnt.length * mYCordGap + mYCordGap;
		}

		int pX1 = 0;
		int pX2 = 0;
		int maxLimit = 0;
		for (int i = 0; i < mEnergyBurnt.length - 1; i++) {
			pX1 = getValue(i);
			pX2 = getValue(i + 1);
			if (Math.abs(pX1 - pX2) > MAX_DIFFERENT_BETWEEN_POINTS) {
				maxLimit = Math.abs(pX1 - pX2) / MAX_DIFFERENT_BETWEEN_POINTS;
				maxLimit = maxLimit > 3 ? 3 : maxLimit;
				mHeight += maxLimit * mYCordGap - mYCordGap;
			}
		}
		Logger.log(Level.DEBUG, TAG, "height=" + mHeight);
		Logger.log(Level.DEBUG, TAG, "width=" + mWidth);

		setMinimumHeight(mHeight);
		setMinimumWidth(mWidth);
	}

	/**
	 * Draw the graph lines.
	 *
	 * @param graphics
	 *            the graphics
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
	private void drawRegions(final Canvas graphics,
			final int moderatePortionStart, final int startX, final int height,
			final int color, final int graphVLineStartAt, final int linesGapPx,
			final int graphHLineStartAt) {

		int verLine = graphVLineStartAt;
		int horLine = graphHLineStartAt;
		mPaint.setColor(color);
		while (verLine <= height) {
			graphics.drawLine(startX, verLine, moderatePortionStart, verLine,
					mPaint);
			verLine += linesGapPx;
		}
		while (horLine <= moderatePortionStart) {
			graphics.drawLine(horLine, 0, horLine, getHeight() + 0, mPaint);
			horLine += linesGapPx;
		}
	}

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

			rect1.set(0, 0, mWidth, mHeight);
			rect2.set(0, 0, mWidth, mHeight);
			rect3.set(0, 0, mWidth, mHeight);
			rect4.set(0, 0, mWidth, mHeight);
		}

		if (null == mCanvas) {
			mCanvas = new Canvas(mGraphImg);
		}

		if (null != mGraphImg && mDoPaint) {
			c.drawBitmap(mGraphImg, rect1, rect2, mPaint);
			return;
		}
		if (null != mCanvas) {
			paint(mCanvas);
		}
		c.drawBitmap(mGraphImg, rect3, rect4, mPaint);
		mDoPaint = true;
	}

	/**
	 * Paint.
	 *
	 * @param g
	 *            Draw the graph on Canvas
	 */
	public void paint(Canvas g) {
		try {
			final float fontSize = getResources().getDimension(
					R.dimen.graph_font_size);
			mPaint.setTextSize(fontSize);
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			mPaint.setStrokeWidth(1);
		} catch (Exception e) { // res not found exception
			Logger.log(Level.DEBUG, TAG, "Error occured : " + e.getMessage());
		}
		mPaint.setColor(0xFFE5E5E5);// Ash color
		g.drawRect(new RectF(0, 0, mWidth, mHeight), mPaint);

		int yCordStart = (int) mPaint.getTextSize() << 1;
		final int graphVLineStartAt = 0;
		final int linesGapPx = 10;
		final int sNumberOfDataCategories = 3;

		final int categoryWidth = mWidth / sNumberOfDataCategories;

		drawRegions(g, categoryWidth, 0, mHeight, 0xffD3D9DF,
				graphVLineStartAt, linesGapPx, 0);

		mPaint.setColor(0xFFD3D9DF);
		g.drawRect(new RectF(categoryWidth, 0, categoryWidth + categoryWidth,
				mHeight), mPaint);

		drawRegions(g, 2 * categoryWidth, categoryWidth, mHeight, 0xFFE5E5E5,
				graphVLineStartAt, linesGapPx, categoryWidth);

		drawRegions(g, getWidth(), 2 * categoryWidth, mHeight, 0xFFD3D9DF,
				graphVLineStartAt, linesGapPx, 2 * categoryWidth);

		mPaint.setColor(0xFF000000);

		final String mLowCaloriStr = "Low";
		int strWidth = (int) mPaint.measureText(mLowCaloriStr);
		int marginLeft = (categoryWidth - strWidth) >> 1;
		g.drawText(mLowCaloriStr, marginLeft, mPaint.getTextSize(), mPaint);

		final String mMediumCaloriStr = "Moderate";
		strWidth = (int) mPaint.measureText(mMediumCaloriStr);
		marginLeft = (categoryWidth - strWidth) >> 1;
		g.drawText(mMediumCaloriStr, marginLeft + categoryWidth,
				mPaint.getTextSize(), mPaint);

		final String mHighCaloriStr = "High";
		strWidth = (int) mPaint.measureText(mHighCaloriStr);
		marginLeft = (categoryWidth - strWidth) >> 1;
		g.drawText(mHighCaloriStr, marginLeft + (categoryWidth << 1),
				mPaint.getTextSize(), mPaint);

		yCordStart += mYCordGap;
		mPaint.setColor(0xFF2C0402);
		final String mLowCaloriRange = "0-250 cal";
		strWidth = (int) mPaint.measureText(mLowCaloriRange);
		marginLeft = (categoryWidth - strWidth) >> 1;
		g.drawText(mLowCaloriRange, marginLeft,
				((int) mPaint.getTextSize() << 1) + 5, mPaint);

		final String mMediumCaloriRange = "251-500 cal";
		strWidth = (int) mPaint.measureText(mMediumCaloriRange);
		marginLeft = (categoryWidth - strWidth) >> 1;
		g.drawText(mMediumCaloriRange, marginLeft + categoryWidth,
				((int) mPaint.getTextSize() << 1) + 5, mPaint);

		final String mHighCaloriRange = ">500 cal";
		strWidth = (int) mPaint.measureText(mHighCaloriRange);
		marginLeft = (categoryWidth - strWidth) >> 1;
		g.drawText(mHighCaloriRange, marginLeft + (categoryWidth << 1),
				((int) mPaint.getTextSize() << 1) + 5, mPaint);

		mPaint.setColor(0xFF2C0402);// Brown color
		yCordStart += mYCordGap;

		if (null == mEnergyBurnt) {
			return;
		}

		int pX1 = 0;
		int pX2 = 0;
		int minLimit = 0;
		for (int i = 0; i < mEnergyBurnt.length - 1; i++) {
			if (yCordStart > getHeight()) {
				break;
			}
			drawCal(g, i, yCordStart);
			drawDate(g, i, yCordStart);
			pX1 = getValue(i);
			pX2 = getValue(i + 1);
			minLimit = 1;
			if (Math.abs(pX1 - pX2) > MAX_DIFFERENT_BETWEEN_POINTS) {
				minLimit = Math.abs(pX1 - pX2) / MAX_DIFFERENT_BETWEEN_POINTS;
				minLimit = minLimit > 3 ? 3 : minLimit;
			}
			g.drawLine(pX1, yCordStart, pX2, yCordStart + minLimit * mYCordGap,
					mPaint);
			yCordStart += minLimit * mYCordGap;
		}
		drawCal(g, mEnergyBurnt.length - 1, yCordStart);
		drawDate(g, mEnergyBurnt.length - 1, yCordStart);
		// Junk value fix
		// String calvalue = Integer.toString(getValue(true,
		// (mEnergyBurnt.length - 1)));
		// g.drawText(calvalue, 0 + getValue(false, (mEnergyBurnt.length - 1)),
		// yCordStart, mPaint);
	}

	/**
	 * Draw the value on canvas.
	 *
	 * @param g
	 *            the g
	 * @param i
	 *            the i
	 * @param yCordStart
	 *            the y cord start
	 */
	private void drawCal(Canvas g, int i, int yCordStart) {
		final String calStr = Integer.toString(mEnergyBurnt[i]);
		mPaint.setColor(0xFF000000);
		float startPos = getValue(i);
		startPos = startPos < mPaint.measureText(calStr) ? startPos : startPos
				- mPaint.measureText(calStr);
		g.drawText(calStr, startPos, yCordStart, mPaint);
	}

	/**
	 * Draw the date.
	 *
	 * @param g
	 *            the g
	 * @param index
	 *            the index
	 * @param yCordStart
	 *            the y cord start
	 */
	private void drawDate(final Canvas g, final int index, final int yCordStart) {
		final long inTimeInMillis = mTimeData.elementAt(index).longValue();
		final String dateStr = DateFormat.format("dd-MMM-yyyy", inTimeInMillis)
				.toString();

		if (null == currDate || !currDate.equals(dateStr)) {
			currDate = dateStr;
			final int startPos = (getWidth() - (int) mPaint
					.measureText(dateStr)) >> 1;
			mPaint.setColor(0xFF474747);
			g.drawText(dateStr, startPos, yCordStart - mPaint.getTextSize(),
					mPaint);
		}
	}

	/**
	 * Returns measurements value.
	 *
	 * @param index
	 *            the index
	 * @return int
	 */
	private int getValue(int index) {
		final int widthDecided = getResources().getDisplayMetrics().widthPixels;
		final int sHighCaloriBurntIndex = 750;
		final float widthFactor = ((float) widthDecided / (float) sHighCaloriBurntIndex);

		int value = (int) (mEnergyBurnt[index] * widthFactor);
		if ((value - 10) > widthDecided) {
			value = widthDecided - 10;// (Scroll bar width + margin to draw the
										// val) )
		}
		return value;
	}
}
