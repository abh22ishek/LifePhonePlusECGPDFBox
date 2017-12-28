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

package com.lppbpl.android.userapp.db;

import java.util.ArrayList;

import android.database.Cursor;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.model.PendingRecord;

// TODO: Auto-generated Javadoc
/**
 * Pending records db.
 *
 */
public final class PendingRecordDb extends Database {

	/** The s instance. */
	private static PendingRecordDb sInstance = null;

	/** The Constant TAG. */
	private static final String TAG = "PendingRecordDb";

	/** The m file name. */
	protected String mFileName = null;

	/**
	 * Open a record store with the given name.
	 *
	 * @param fileName
	 *            the file name
	 */
	private PendingRecordDb(String fileName) {
		super(fileName);
	}

	/**
	 * Method getInstance.
	 *
	 * @return PendingRecordDb
	 */
	public synchronized static PendingRecordDb getInstance() {
		if (sInstance == null) {
			sInstance = new PendingRecordDb(Constants.PENDING_RECORDS_TABLE);
		}
		return sInstance;
	}

	/**
	 * Add a new record to the record store.
	 *
	 * @param record
	 *            the record
	 * @return int
	 */

	public synchronized int addRecord(final PendingRecord record) {
		Logger.log(Level.DEBUG, TAG, "addRecord() function");
		open();
		byte[] b = record.writeData();
		int id = add(b);
		if (id != -1) {
			record.setId(id);
		}
		close();

		return id;
	}

	/**
	 * Method updateRecord.
	 *
	 * @param record
	 *            PendingRecord
	 */
	public void updateRecord(final PendingRecord record) {
		Logger.log(Level.DEBUG, TAG, "updateRecord() function");
		open();
		byte[] data = record.writeData();
		update(record.getId(), data);
		close();
	}

	/**
	 * Delete the Pending Record.
	 *
	 * @param id
	 *            the id
	 */
	public synchronized void deleteRecord(int id) {
		Logger.log(Level.DEBUG, TAG, "deleteRecord ++=" + id);
		open();
		int success = delete(id);
		close();
		Logger.log(Level.DEBUG, TAG, "deleteRecord ---success=" + success);
	}

	/**
	 * Method getAllRecords.
	 *
	 * @return Vector<PendingRecord>
	 */
	public synchronized ArrayList<PendingRecord> getAllRecords() {
		Logger.log(Level.DEBUG, TAG, "getAllRecords() function++");
		open();
		ArrayList<PendingRecord> recordList = new ArrayList<PendingRecord>();
		Cursor getCursor = queueAll();
		if (getCursor != null) {
			if (getCursor.moveToFirst()) {
				do {
					String stringId = getCursor.getString(0);
					Integer rowID = Integer.parseInt(stringId);
					int id = -1;
					if (rowID != null) {
						id = rowID.intValue();
					}
					byte[] readData = getCursor.getBlob(1);
					Logger.log(Level.DEBUG, TAG, "data size:" + readData.length);
					PendingRecord record = new PendingRecord();
					record.readData(readData, true);
					record.setId(id);
					recordList.add(record);
				} while (getCursor.moveToNext());
			}
			getCursor.close();
		} else {
		}
		close();
		Logger.log(Level.DEBUG, TAG, "getAllRecords() function--");
		return recordList;
	}

	/**
	 * Method getPendingRecord.
	 *
	 * @param recID
	 *            int
	 * @return PendingRecord
	 */
	public synchronized PendingRecord getPendingRecord(final int recID) {
		Logger.log(Level.DEBUG, TAG, "getRecord (id)=" + recID);

		try {
			open();
			// Get next record
			byte[] recData = readData(recID);
			PendingRecord record = new PendingRecord();
			record.readData(recData, false);
			record.setId(recID);
			close();
			return record;
		} catch (Exception e) {
			Logger.log(Level.ERROR, TAG, e.toString());
		}
		return null;
	}

}
