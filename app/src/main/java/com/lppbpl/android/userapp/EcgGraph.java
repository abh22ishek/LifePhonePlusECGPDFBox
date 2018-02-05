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
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.android.userapp.constants.Constants;

import java.util.Vector;

// TODO: Auto-generated Javadoc
/**
 * This class will draw the Ecg graph view.
 */
public class EcgGraph extends View {

	/** The m paint. */
	private Paint mPaint;

	/** The m pixels per cm. */
	private int mPixelsPerCm;

	/** The m leads. */
	@SuppressWarnings("rawtypes")
	private Vector mLeads;

	/** The m lead labels. */
	private Vector<String> mLeadLabels;

	/** The m graph img. */
	private Bitmap mGraphImg;

	/** The m canvas. */
	private Canvas mCanvas;

	/** The m do paint. */
	private boolean mDoPaint;

	/** The r1. */
	private Rect r1;

	/** The r2. */
	private Rect r2;

	/** The r3. */
	private Rect r3;

	/**
	 * Constructor for EcgGraph.
	 *
	 * @param context
	 *            Context
	 */
	public EcgGraph(Context context) {
		super(context);
		init();
	}

	/**
	 * Constructor for EcgGraph.
	 *
	 * @param context
	 *            Context
	 * @param attributeSet
	 *            AttributeSet
	 */
	public EcgGraph(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		init();
	}

	/**
	 * Will set the initialization for View Height, Width and density.
	 */
	private void init() {
		mPaint = new Paint();
		final float density = getResources().getDisplayMetrics().densityDpi;
		mPixelsPerCm = (int) (density / Constants.CMS_PER_INCH + 0.5f);
		setMinimumWidth(25 * mPixelsPerCm);
	}

	// Rect mRect = new Rect(0, 0, getWidth(), getHeight());

	/**
	 * Method onDraw.
	 *
	 * @param c
	 *            Canvas
	 */
	@SuppressWarnings("rawtypes")
	protected void onDraw(Canvas c) {
		super.onDraw(c);
		if (null == mGraphImg) {
			/*
			 * Used Bitmap.Config.ALPHA_8 instead Bitmap.Config.ARGB_8888 to
			 * avoid OutOfMemory exception in the application on various phones
			 * for example (Samsung Note 2,Micromax Bolt, LG Optimal)
			 */
			mGraphImg = Bitmap.createBitmap(this.getWidth(), this.getHeight(),
					Bitmap.Config.ALPHA_8);
		}

		if (null == mCanvas) {
			mCanvas = new Canvas(mGraphImg);
		}

		if (null == r1) {
			r1 = new Rect(0, 0, getWidth(), getHeight());
		}

		if (null == r2) {
			r2 = new Rect(0, 0, getWidth(), getHeight());
		}

		if (null == r3) {
			r3 = new Rect(0, 0, getWidth(), getHeight());
		}

		if (null != mGraphImg && mDoPaint) {
			c.drawBitmap(mGraphImg, r1, r2, mPaint);
			return;
		}

		// mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(1);

		// Find out the pixel density
		final float widthScale = mPixelsPerCm / Constants.SAMPLES_PER_CM; // 100
		// samples per cm
		final float heightScale = Constants.AMPLITUDE_PER_CM / mPixelsPerCm; // pixels

		mPaint.setColor(0X0082CB00);
		mCanvas.drawRect(r3, mPaint);

		// paint.setColor(Color.LTGRAY);
		mPaint.setColor(0x2798D800);// 3FAFFF00);
		for (float i = mPixelsPerCm / (float) 10; i < getWidth(); i += mPixelsPerCm
				/ (float) 10) {
			mCanvas.drawLine(i, 0, i, getHeight(), mPaint);
		}

		for (float i = mPixelsPerCm / (float) 10; i < getHeight(); i += mPixelsPerCm
				/ (float) 10) {
			mCanvas.drawLine(0, i, getWidth(), i, mPaint);
		}

		mPaint.setColor(0x43AFEB00);// 4FBFFF00);
		mPaint.setTextSize(12.0f);
		int count = 1;
		int y = 0;
		Logger.log(Level.INFO,"GRAPHVIEW","*****"+getWidth());
		for (float i = mPixelsPerCm; i < getWidth(); i += mPixelsPerCm) {
			mPaint.setColor(0x43AFEB00);// 4FBFFF00);
			mCanvas.drawLine(i, 0, i, getHeight(), mPaint);
			mPaint.setColor(0xB9E6FF00);
			for (int k = 0; k < noOfLeads(); k++) {
				y = ((k * 3) * mPixelsPerCm) + 15;
				mCanvas.drawText(count + "", i + 2, y, mPaint);
			}
			count++;
		}

		for (float i = mPixelsPerCm; i < getHeight(); i += mPixelsPerCm) {
			mCanvas.drawLine(0, i, getWidth(), i, mPaint);
		}

		mPaint.setColor(0X88D3FC00);// Color.WHITE);
		// paint.setStyle(Paint.Style.FILL);
		// mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaint.setStrokeWidth(1);
		mPaint.setTextSize(16.0f);

		if (null != mLeadLabels) {
			int mul = 0;
			for (int i = 0; i < mLeadLabels.size(); i++) {
				mul = (3 * i) * mPixelsPerCm;
				mCanvas.drawText(mLeadLabels.elementAt(i), 2, 20 + mul, mPaint);
			}
		}

		int mP1 = 0, mPp1 = 0;

		int mP2 = 0, mPp2 = 0;

		int mP3 = 0, mPp3 = 0;

		int mP4 = 0, mPp4 = 0;

		int mP5 = 0, mPp5 = 0;

		int mP6 = 0, mPp6 = 0;

		int mP7 = 0, mPp7 = 0;

		int mP8 = 0, mPp8 = 0;

		int mP9 = 0, mPp9 = 0;

		int mP10 = 0, mPp10 = 0;

		int mP11 = 0, mPp11 = 0;

		int mP12 = 0, mPp12 = 0;

		float mX = mP1 = mP2 = mP3 = 0;
		mP4 = mP5 = mP6 = mP7 = mP8 = mP9 = mP10 = mP11 = mP12 = 0;

		final int graphHeight = mPixelsPerCm;

		float mPx1 = 0;
		mPp1 = 2 * graphHeight;
		float mPx2 = 0;
		mPp2 = 5 * graphHeight; // graphHeight * 2;
		float mPx3 = 0;
		mPp3 = 8 * graphHeight; // graphHeight * 3;
		float mPx4 = 0;
		mPp4 = 11 * graphHeight; // graphHeight * 3;
		float mPx5 = 0;
		mPp5 = 14 * graphHeight; // graphHeight * 3;
		float mPx6 = 0;
		mPp6 = 17 * graphHeight; // graphHeight * 3;
		float mPx7 = 0;
		mPp7 = 20 * graphHeight; // graphHeight * 3;
		float mPx8 = 0;
		mPp8 = 23 * graphHeight; // graphHeight * 3;
		float mPx9 = 0;
		mPp9 = 26 * graphHeight; // graphHeight * 3;
		float mPx10 = 0;
		mPp10 = 29 * graphHeight; // graphHeight * 3;
		float mPx11 = 0;
		mPp11 = 32 * graphHeight; // graphHeight * 3;
		float mPx12 = 0;
		mPp12 = 35 * graphHeight; // graphHeight * 3;

		final int noOfLeads = noOfLeads();

		final int leadSampleCount = getLeadsLength();
		// traverse through the points one by one to get moving graphs
		for (int i = 0; i < leadSampleCount; i++) {
			if (mX > getWidth()) {
				break;
			}
			mX += widthScale;

			mPaint.setColor(Color.WHITE);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeWidth(2);

			// draw ECG

			if (noOfLeads >= 1) {
				mP1 = (2 * graphHeight)
						- (int) (((Integer) ((Vector) mLeads.elementAt(0))
								.elementAt(i)).intValue() / heightScale);
				mCanvas.drawLine(mPx1, mPp1, mX, mP1, mPaint);
			}

			if (noOfLeads >= 2) {
				mP2 = (5 * graphHeight)
						- (int) (((Integer) ((Vector) mLeads.elementAt(1))
								.elementAt(i)).intValue() / heightScale);
				mCanvas.drawLine(mPx2, mPp2, mX, mP2, mPaint);
			}

			if (noOfLeads >= 3) {
				mP3 = (8 * graphHeight)
						- (int) (((Integer) ((Vector) mLeads.elementAt(2))
								.elementAt(i)).intValue() / heightScale);
				mCanvas.drawLine(mPx3, mPp3, mX, mP3, mPaint);
			}

			if (noOfLeads >= 4) {
				mP4 = (11 * graphHeight)
						- (int) (((Integer) ((Vector) mLeads.elementAt(3))
								.elementAt(i)).intValue() / heightScale);
				mCanvas.drawLine(mPx4, mPp4, mX, mP4, mPaint);
			}

			if (noOfLeads >= 5) {
				mP5 = (14 * graphHeight)
						- (int) (((Integer) ((Vector) mLeads.elementAt(4))
								.elementAt(i)).intValue() / heightScale);
				mCanvas.drawLine(mPx5, mPp5, mX, mP5, mPaint);
			}

			if (noOfLeads >= 6) {
				mP6 = (17 * graphHeight)
						- (int) (((Integer) ((Vector) mLeads.elementAt(5))
								.elementAt(i)).intValue() / heightScale);
				mCanvas.drawLine(mPx6, mPp6, mX, mP6, mPaint);
			}

			if (noOfLeads >= 7) {
				mP7 = (20 * graphHeight)
						- (int) (((Integer) ((Vector) mLeads.elementAt(6))
								.elementAt(i)).intValue() / heightScale);
				mCanvas.drawLine(mPx7, mPp7, mX, mP7, mPaint);
			}

			if (noOfLeads >= 8) {
				mP8 = (23 * graphHeight)
						- (int) (((Integer) ((Vector) mLeads.elementAt(7))
								.elementAt(i)).intValue() / heightScale);
				mCanvas.drawLine(mPx8, mPp8, mX, mP8, mPaint);
			}

			if (noOfLeads >= 9) {
				mP9 = (26 * graphHeight)
						- (int) (((Integer) ((Vector) mLeads.elementAt(8))
								.elementAt(i)).intValue() / heightScale);
				mCanvas.drawLine(mPx9, mPp9, mX, mP9, mPaint);
			}

			if (noOfLeads >= 10) {
				mP10 = (29 * graphHeight)
						- (int) (((Integer) ((Vector) mLeads.elementAt(9))
								.elementAt(i)).intValue() / heightScale);
				mCanvas.drawLine(mPx10, mPp10, mX, mP10, mPaint);
			}

			if (noOfLeads >= 11) {
				mP11 = (32 * graphHeight)
						- (int) (((Integer) ((Vector) mLeads.elementAt(10))
								.elementAt(i)).intValue() / heightScale);
				mCanvas.drawLine(mPx11, mPp11, mX, mP11, mPaint);
			}

			if (noOfLeads >= 12) {
				mP12 = (35 * graphHeight)
						- (int) (((Integer) ((Vector) mLeads.elementAt(11))
								.elementAt(i)).intValue() / heightScale);
				mCanvas.drawLine(mPx12, mPp12, mX, mP12, mPaint);
			}
			// store current point as previous point
			mPp1 = mP1;
			mPp2 = mP2;
			mPp3 = mP3;
			mPp4 = mP4;
			mPp5 = mP5;
			mPp6 = mP6;
			mPp7 = mP7;
			mPp8 = mP8;
			mPp9 = mP9;
			mPp10 = mP10;
			mPp11 = mP11;
			mPp12 = mP12;
			mPx1 = mPx2 = mPx3 = mPx4 = mPx5 = mPx6 = mPx7 = mPx8 = mPx9 = mPx10 = mPx11 = mPx12 = mX;
		}
		c.drawBitmap(mGraphImg, r1, r2, mPaint);
		mDoPaint = true;
	}

	/**
	 * Gets the leads length.
	 *
	 * @return leads length
	 */
	@SuppressWarnings("rawtypes")
	private int getLeadsLength() {
		int leadLength = 0;
		if (null != mLeads && mLeads.size() > 0 && null != mLeads.elementAt(0)) {
			leadLength = ((Vector) mLeads.elementAt(0)).size();
		}
		return leadLength;
	}

	/**
	 * set Leads.
	 *
	 * @param ecgLeads
	 *            the new leads
	 */
	public void setLeads( Vector ecgLeads) {
		mLeads = ecgLeads;
		if (null != mLeads) {
			setMinimumHeight((3 * mLeads.size()) * mPixelsPerCm);
		}
	}

	/**
	 * set lead ecg step labels.
	 *
	 * @param ecgLeadLabels
	 *            the new lead labels
	 */
	public void setLeadLabels(Vector<String> ecgLeadLabels) {
		mLeadLabels = ecgLeadLabels;
	}

	/**
	 * return number of leads.
	 *
	 * @return int
	 */
	public int noOfLeads() {
		return null != mLeadLabels ? mLeadLabels.size() : 0;
	}
}
