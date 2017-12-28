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

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lppbpl.BgReadingType;
import com.lppbpl.Measurement;
import com.lppbpl.android.userapp.R;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.db.PendingRecordDb;
import com.lppbpl.android.userapp.model.PendingRecord;
import com.lppbpl.android.userapp.util.Util;

// TODO: Auto-generated Javadoc
/**
 * This Activity is to display list of unsaved records.
 *
 */
public class UnsavedRecordsList extends AppBaseActivity {

	/** The Constant TAG. */
	public static final String TAG = "UnsavedRecordsList";

	/** The m list view. */
	private ListView mListView = null;

	/**
	 * Method onCreate.
	 *
	 * @param savedInstanceState
	 *            Bundle
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.multirow_list);

		setCustomNoIconTitle(R.string.title_unsaved_records);

		mListView = (ListView) findViewById(R.id.lMultiListView);
		updateList();
		mListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				PendingRecord pendRecord = (PendingRecord) mListView
						.getItemAtPosition(position);
				if (pendRecord.getUploadStatus() == PendingRecord.UPLOAD_FAILED) {
					if (pendRecord.getNumOfAttempt() == Constants.MAX_NO_OF_ATTEMPT) {
						pendRecord.setNumOfAttempt(0);
					}
				}

				if (pendRecord.getUploadStatus() == PendingRecord.UPLOAD_STARTED) {
					showAlertDialog(get(R.string.information),
							get(R.string.pending_record_upload),
							getResources().getString(R.string.OK), null, true);
				} else {// if (pendRecord.getUploadStatus() ==
						// PendingRecord.UPLOAD_FAILED) {
					// We have to load response for ECG.
					if (pendRecord.getMeasurementType() == Measurement.ECG) {
						pendRecord = PendingRecordDb.getInstance()
								.getPendingRecord(pendRecord.getId());
					}

					mAppModel.setPendingRecord(pendRecord);

					if (pendRecord.getMeasurementType() == Measurement.ECG) {
						final Intent intent = new Intent(
								UnsavedRecordsList.this, EcgGraphActivity.class);
						intent.putExtra(Constants.UNSAVED_RECORD, true);
						startActivity(intent);
						// finish();
					} else if (pendRecord.getMeasurementType() == Measurement.BG) {
						final Intent intent = new Intent(
								UnsavedRecordsList.this,
								BgReadingTypeListActivity.class);
						intent.putExtra(Constants.UNSAVED_RECORD, true);
						startActivity(intent);
						// finish();
					} else if (pendRecord.getMeasurementType() == Measurement.ACT) {
						final Intent intent = new Intent(
								UnsavedRecordsList.this,
								ActivityFinalDisplay.class);
						intent.putExtra(Constants.UNSAVED_RECORD, true);
						startActivity(intent);
						// finish();
					}

				}
			}
		});

		/*
		 * registerForContextMenu(lv); lv.setOnItemLongClickListener(new
		 * OnItemLongClickListener() { public boolean
		 * onItemLongClick(AdapterView<?> parent, View view, int position, long
		 * id) { listPos = position; return false; } });
		 */
		mListView.setTextFilterEnabled(true);
	}

	/**
	 * Updates the list from pending records.
	 */
	private void updateList() {
		final ArrayList<PendingRecord> mPendRecs = PendingRecordDb
				.getInstance().getAllRecords();
		if (0 == mPendRecs.size()) {
			final TextView textView = new TextView(this);
			textView.setText(R.string.no_recrods);
			textView.setGravity(Gravity.CENTER);
			final RelativeLayout layout = (RelativeLayout) findViewById(R.id.lMultiListLayout);
			layout.addView(textView, new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT));
		}
		final PendingRecCustAdapter mPendRecCustAdapter = new PendingRecCustAdapter(
				this, mPendRecs);
		mListView.setAdapter(mPendRecCustAdapter);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		updateList();
	}

	/**
	 * The Class PendingRecCustAdapter.
	 */
	public class PendingRecCustAdapter extends BaseAdapter {

		/** The m inflater. */
		final private LayoutInflater mInflater;

		/** The search array list. */
		public ArrayList<PendingRecord> searchArrayList;

		/**
		 * Constructor for PendingRecCustAdapter.
		 *
		 * @param context
		 *            Context
		 * @param results
		 *            ArrayList<PendingRecord>
		 */
		public PendingRecCustAdapter(Context context,
				ArrayList<PendingRecord> results) {
			searchArrayList = results;
			mInflater = LayoutInflater.from(context);
		}

		/**
		 * Method getCount.
		 *
		 * @return int
		 * @see android.widget.Adapter#getCount()
		 */
		public int getCount() {
			return searchArrayList.size();
		}

		/**
		 * Method getItem.
		 *
		 * @param position
		 *            int
		 * @return Object
		 * @see android.widget.Adapter#getItem(int)
		 */
		public Object getItem(int position) {
			return searchArrayList.get(position);
		}

		/**
		 * Method getItemId.
		 *
		 * @param position
		 *            int
		 * @return long
		 * @see android.widget.Adapter#getItemId(int)
		 */
		public long getItemId(int position) {
			return position;
		}

		/**
		 * Method getView.
		 *
		 * @param position
		 *            int
		 * @param convertView
		 *            View
		 * @param parent
		 *            ViewGroup
		 * @return View
		 * @see android.widget.Adapter#getView(int, View, ViewGroup)
		 */
		public View getView(final int position, final View convertView,
				final ViewGroup parent) {
			ViewHolder holder;
			View view = convertView;
			if (null == view) {
				view = mInflater.inflate(R.layout.custom_row_view, null);
				holder = new ViewHolder();
				holder.measurementVal = (TextView) view
						.findViewById(R.id.measurementVal);
				holder.measurementInfo = (TextView) view
						.findViewById(R.id.measurementInfo);
				holder.statusIcon = (ImageView) view
						.findViewById(R.id.statusImage);
				view.setTag(holder);
				view.setBackgroundResource(R.xml.multiline_listview_style);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			final String timeStamp = Util.formatDateTime(searchArrayList.get(
					position).getMeasurementTime());

			holder.measurementVal.setText(searchArrayList.get(position)
					.getMsg());
			holder.measurementInfo.setText(timeStamp);
			holder.statusIcon.setImageResource(getImageForState(searchArrayList
					.get(position).getUploadStatus()));

			if (searchArrayList.get(position).getMeasurementType() == Measurement.BG) {
				final int bgReadingType = searchArrayList.get(position)
						.getResponse().getBgData().getBgReadingType();
				holder.measurementInfo.setText(BgReadingType
						.getStringValue(bgReadingType) + " " + timeStamp);
			}
			return view;
		}

		/**
		 * Method getImageForState.
		 *
		 * @param uploadStatus
		 *            int
		 * @return int
		 */
		private int getImageForState(int uploadStatus) {
			int resId = R.drawable.ic_cloud_upload_state_failed_black;
			// if (uploadStatus == PendingRecord.UPLOAD_NOT_STARTED) {
			// resId = Image.createImage(20, 20);
			// } else
			if (uploadStatus == PendingRecord.UPLOAD_FAILED) {
				resId = R.drawable.ic_cloud_upload_state_failed_black;
			} else if (uploadStatus == PendingRecord.UPLOAD_STARTED) {
				resId = R.drawable.ic_cloud_upload_state_progressing;
			}
			return resId;
		}

		/**
		 * The Class ViewHolder.
		 */
		protected class ViewHolder {

			/** The measurement val. */
			protected TextView measurementVal;

			/** The measurement info. */
			protected TextView measurementInfo;

			/** The status icon. */
			protected ImageView statusIcon;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.lppbpl.android.userapp.AppBaseActivity#onPositiveButtonClick()
	 */
	@Override
	public void onPositiveButtonClick() {
		super.onPositiveButtonClick();
		finish();
	}

}
