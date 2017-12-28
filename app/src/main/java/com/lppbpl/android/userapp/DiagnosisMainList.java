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
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lppbpl.android.userapp.R;
import com.lppbpl.android.userapp.db.DiagnosisMsgDb;
import com.lppbpl.android.userapp.model.Diagnosis;
import com.lppbpl.android.userapp.util.Util;

// TODO: Auto-generated Javadoc
/**
 * This activity display the list of feedback.
 */
public class DiagnosisMainList extends AppBaseActivity {

	/** The m adapter. */
	private DiagnosisCustAdapter mAdapter;

	/** The m record id. */
	private int mRecordID = -1;

	/** The m list pos. */
	private int mListPos;

	/** The m lv. */
	private ListView mLv = null;

	/** The m dg msgs. */
	private ArrayList<Diagnosis> mDgMsgs = null;

	/**
	 * Method onCreate.
	 *
	 * @param savedInstanceState
	 *            Bundle
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.multirow_list);
		setCustomNoIconTitle(R.string.title_diagnosis);

		final String userId = getIntent().getStringExtra("userId");
		mDgMsgs = DiagnosisMsgDb.getInstance().getAllDiagnoses(userId);

		mAdapter = new DiagnosisCustAdapter(this, mDgMsgs);
		mLv = (ListView) findViewById(R.id.lMultiListView);
		// mLv.setAdapter(mAdapter);

		registerForContextMenu(mLv);

		mLv.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				mListPos = position;
				return false;
			}

		});

		mLv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				final Diagnosis diagnosisData = (Diagnosis) mLv
						.getItemAtPosition(position);
				final String diaMsg = diagnosisData.getMsg();
				// Sets the read status for the feedback message.
				diagnosisData.setReadStatus(true);
				DiagnosisMsgDb.getInstance().updateDiagnosis(diagnosisData);
				final Intent diaActivity = new Intent(getApplicationContext(),
						DiagnosisActivity.class);
				final Bundle bundle = new Bundle();
				final String secondRow = diagnosisData.getDate().toString();
				if (null != diaMsg) {
					bundle.putString("diaMsg", diaMsg);
					bundle.putString("diaData", secondRow);
					diaActivity.putExtras(bundle);
				}
				startActivity(diaActivity);
			}

		});

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mLv.setAdapter(mAdapter);
	}

	/**
	 * Set record id.
	 *
	 * @param recID
	 *            the new rec id
	 */
	public void setRecID(int recID) {
		mRecordID = recID;
	}

	/**
	 * Gets the rec id.
	 *
	 * @return record id
	 */
	public int getRecID() {
		return mRecordID;
	}

	/**
	 * Adapter for the feedback messages.
	 *
	 */
	public class DiagnosisCustAdapter extends BaseAdapter {

		/** The m inflater. */
		final private LayoutInflater mInflater;

		/** The dia array list. */
		final private ArrayList<Diagnosis> diaArrayList;

		/**
		 * Constructor for DiagnosisCustAdapter.
		 *
		 * @param context
		 *            Context
		 * @param results
		 *            ArrayList<Diagnosis>
		 */
		public DiagnosisCustAdapter(Context context,
				ArrayList<Diagnosis> results) {
			diaArrayList = results;
			mInflater = LayoutInflater.from(context);
		}

		/**
		 * Method getCount.
		 *
		 * @return int
		 * @see android.widget.Adapter#getCount()
		 */
		public int getCount() {
			return diaArrayList.size();
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

			return diaArrayList.get(position);
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
				view = mInflater.inflate(R.layout.custom_list_title, null);
				holder = new ViewHolder();
				holder.measurementVal = (TextView) view
						.findViewById(R.id.txt_measurement_info);
				holder.measurementDate = (TextView) view
						.findViewById(R.id.txt_measurement_date);
				holder.measurementTime = (TextView) view
						.findViewById(R.id.txt_measurement_time);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			final Diagnosis disMsg = diaArrayList.get(position);
			try {
				final String msg = disMsg.getMsg();
				holder.measurementVal.setText(msg.substring(0,
						msg.indexOf(System.getProperty("line.separator"))));
			} catch (StringIndexOutOfBoundsException e) {
				holder.measurementVal.setText(disMsg.getMsg());
			}
			// set text style as bold for read and normal for unread status
			holder.measurementVal.setTypeface(null,
					disMsg.isRead() ? Typeface.NORMAL : Typeface.BOLD);

			holder.measurementDate.setText(Util.formatDate(disMsg.getDate().getTime()));
			holder.measurementDate.setTypeface(null,
					disMsg.isRead() ? Typeface.NORMAL : Typeface.BOLD);

			holder.measurementTime.setText(Util.formatTime(disMsg.getDate().getTime()));
			holder.measurementTime.setTypeface(null,
					disMsg.isRead() ? Typeface.NORMAL : Typeface.BOLD);
			return view;

		}

		/**
		 * The Class ViewHolder.
		 */
		protected class ViewHolder {

			/** The measurement val. */
			protected TextView measurementVal;

			/** The measurement info. */
			protected TextView measurementDate;

			/** The status icon. */
			protected TextView measurementTime;
		}
	}

	/**
	 * Method onCreateContextMenu.
	 *
	 * @param menu
	 *            ContextMenu
	 * @param v
	 *            View
	 * @param menuInfo
	 *            ContextMenuInfo
	 * @see android.view.View$OnCreateContextMenuListener#onCreateContextMenu(ContextMenu,
	 *      View, ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.lMultiListView) {
			menu.add(R.string.delete);
		}
	}

	/**
	 * Method onContextItemSelected.
	 *
	 * @param item
	 *            MenuItem
	 * @return boolean
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		showAlertDialog(R.drawable.ic_dialog_delete, get(R.string.delete),
				get(R.string.delete_consfirmation), get(R.string.yes),
				get(R.string.no), false);
		return true;
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
		if (null != mLv) {
			if (mListPos >= 0) {
				// Deletes the message from the DB
				final Diagnosis diag = mDgMsgs.get(mListPos);
				DiagnosisMsgDb.getInstance().deleteDiagnosis(diag.getId());
				mDgMsgs.remove(diag);
				mAdapter.notifyDataSetChanged();
			}
		}
	}

}
