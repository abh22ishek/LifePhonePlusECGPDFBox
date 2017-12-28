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

import android.annotation.TargetApi;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.lppbpl.android.userapp.R;

// TODO: Auto-generated Javadoc
/**
 * This class will create own custom dialog to display the user.
 */
public class CustomDialogBuilder extends Builder implements
		View.OnClickListener {

	/** The m title. */
	private TextView mTitle = null;

	/** The m message. */
	private TextView mMessage = null;

	/** The m pos btn. */
	private Button mPosBtn = null;

	/** The m neg btn. */
	private Button mNegBtn = null;

	/** The m pos btn listener. */
	private OnClickListener mPosBtnListener = null;

	/** The m neg btn listener. */
	private OnClickListener mNegBtnListener = null;

	/** The m dialog interface. */
	private DialogInterface mDialogInterface;

	/**
	 * Construct the custom dialog.
	 *
	 * @param context
	 *            the context
	 * @param btnPresent
	 *            the btn present
	 */
	public CustomDialogBuilder(final Context context, final boolean btnPresent) {
		super(context);

		final View customDialog = View.inflate(context,
				btnPresent ? R.layout.custom_dialog_layout
						: R.layout.custom_dialog_layoutnobtn, null);
		if (btnPresent) {
			mNegBtn = (Button) customDialog.findViewById(R.id.btnNegative);
			mNegBtn.setOnClickListener(this);
		}
		mTitle = (TextView) customDialog.findViewById(R.id.alertTitle);
		mMessage = (TextView) customDialog.findViewById(R.id.message);
		mPosBtn = (Button) customDialog.findViewById(R.id.btnPositive);
		mPosBtn.setOnClickListener(this);
		setView(customDialog);
	}

	/**
	 * Construct the custom dialog for android version from honeycomb.
	 *
	 * @param context
	 *            the context
	 * @param btnPresent
	 *            the btn present
	 * @param val
	 *            the val
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public CustomDialogBuilder(final Context context, final boolean btnPresent,
			final boolean val) {
		super(context, R.style.AboutDialog);

		final View customDialog = View.inflate(context,
				btnPresent ? R.layout.custom_dialog_layout
						: R.layout.custom_dialog_layoutnobtn, null);
		if (btnPresent) {
			mNegBtn = (Button) customDialog.findViewById(R.id.btnNegative);
			mNegBtn.setOnClickListener(this);
		}
		mTitle = (TextView) customDialog.findViewById(R.id.alertTitle);
		mMessage = (TextView) customDialog.findViewById(R.id.message);
		mPosBtn = (Button) customDialog.findViewById(R.id.btnPositive);
		mPosBtn.setOnClickListener(this);
		setView(customDialog);
	}

	/**
	 * Method setTitle.
	 *
	 * @param textResId
	 *            int
	 * @return CustomDialogBuilder
	 */
	@Override
	public CustomDialogBuilder setTitle(int textResId) {
		mTitle.setText(textResId);
		return this;
	}

	/**
	 * Method setTitle.
	 *
	 * @param text
	 *            CharSequence
	 * @return CustomDialogBuilder
	 */
	@Override
	public CustomDialogBuilder setTitle(CharSequence text) {
		mTitle.setText(text);
		return this;
	}

	/**
	 * Method setMessage.
	 *
	 * @param textResId
	 *            int
	 * @return CustomDialogBuilder
	 */
	@Override
	public CustomDialogBuilder setMessage(int textResId) {
		mMessage.setText(textResId);
		return this;
	}

	/**
	 * Method setMessage.
	 *
	 * @param text
	 *            CharSequence
	 * @return CustomDialogBuilder
	 */
	@Override
	public CustomDialogBuilder setMessage(CharSequence text) {
		mMessage.setText(text);
		return this;
	}

	/**
	 * Method setIcon.
	 *
	 * @param drawableResId
	 *            int
	 * @return CustomDialogBuilder
	 */
	@Override
	public CustomDialogBuilder setIcon(int drawableResId) {
		mTitle.setCompoundDrawablesWithIntrinsicBounds(drawableResId, 0, 0, 0);
		return this;
	}

	/**
	 * Method setIcon.
	 *
	 * @param icon
	 *            Drawable
	 * @return CustomDialogBuilder
	 */
	@Override
	public CustomDialogBuilder setIcon(Drawable icon) {
		mTitle.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
		return this;
	}

	/**
	 * Method setPositiveButton.
	 *
	 * @param text
	 *            CharSequence
	 * @param listener
	 *            OnClickListener
	 * @return Builder
	 */
	@Override
	public Builder setPositiveButton(CharSequence text, OnClickListener listener) {
		mPosBtn.setVisibility(View.VISIBLE);
		if (null != listener) {
			mPosBtnListener = listener;
		}
		mPosBtn.setText(text);
		return this;
	}

	/**
	 * Method setNegativeButton.
	 *
	 * @param text
	 *            CharSequence
	 * @param listener
	 *            OnClickListener
	 * @return Builder
	 */
	@Override
	public Builder setNegativeButton(CharSequence text, OnClickListener listener) {

		mNegBtn.setVisibility(View.VISIBLE);
		if (null != listener) {
			mNegBtnListener = listener;
		}
		mNegBtn.setText(text);
		return this;
	}

	/**
	 * Method onClick.
	 *
	 * @param v
	 *            View
	 * @see android.view.View$OnClickListener#onClick(View)
	 */
	public void onClick(View v) {
		final int id = v.getId();
		// clicked on positive button
		if (id == R.id.btnPositive) {
			if (null != mPosBtnListener) {
				mPosBtnListener.onClick(mDialogInterface, R.id.btnPositive);
			}
			// Clicked on negative button
		} else if (id == R.id.btnNegative) {
			if (null != mNegBtnListener) {
				mNegBtnListener.onClick(mDialogInterface, R.id.btnNegative);
			}
		}
	}

	/**
	 * Set the dlgInterface.
	 *
	 * @param dlgInterface
	 *            the new dialog interface
	 */
	public void setDialogInterface(DialogInterface dlgInterface) {
		mDialogInterface = dlgInterface;
	}
}
