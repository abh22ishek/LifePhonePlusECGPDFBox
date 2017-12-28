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
import com.lppbpl.android.userapp.model.Diagnosis;

// TODO: Auto-generated Javadoc
/**
 * Diagnosis Messages Db.
 *
 */
public final class DiagnosisMsgDb extends Database {

	/** The s instance. */
	private static DiagnosisMsgDb sInstance = null;

	/** The Constant TAG. */
	private static final String TAG = "DiagnosisMsgDb";

	/**
	 * Open a record store with the given name.
	 *
	 * @param fileName
	 *            the file name
	 */
	private DiagnosisMsgDb(String fileName) {
		super(fileName);
	}

	/**
	 * Method getInstance.
	 *
	 * @return DiagnosisMsgDb
	 */
	public synchronized static DiagnosisMsgDb getInstance() {
		if (sInstance == null) {
			sInstance = new DiagnosisMsgDb(Constants.DIAGNOSIS_MSGLIST_TABLE);
		}
		return sInstance;
	}

	/**
	 * Add a new record to the record store.
	 *
	 * @param smsMsg
	 *            the sms msg
	 * @param userId
	 *            String
	 * @return int
	 */
	public synchronized int addDiagnosis(final Diagnosis smsMsg,
			final String userId) {
		Logger.log(Level.DEBUG, TAG, "addDiagnosis() function");

		open();
		byte[] b = smsMsg.writeData();
		int id = add(b, userId);
		if (id != -1) {
			smsMsg.setId(id);
		}
		close();
		return id;
	}

	/**
	 * Method updateDiagnosis.
	 *
	 * @param smsMsg
	 *            Diagnosis
	 */
	public void updateDiagnosis(Diagnosis smsMsg) {
		Logger.log(Level.DEBUG, TAG, "updateDiagnosis() function");
		open();
		byte[] data = smsMsg.writeData();
		update(smsMsg.getId(), data);
		close();
	}

	/**
	 * Delete the SMS.
	 *
	 * @param id
	 *            the id
	 */
	public synchronized void deleteDiagnosis(int id) {
		open();
		delete(id);
		close();
	}

	// XXX: Synchronized method
	/**
	 * Method getAllDiagnoses.
	 *
	 * @return ArrayList<Diagnosis>
	 */
	public synchronized ArrayList<Diagnosis> getAllDiagnoses(String userId) {

		Logger.log(Level.DEBUG, TAG, "getAllDiagnoses() function");
		open();
		ArrayList<Diagnosis> recordList = new ArrayList<Diagnosis>();
		Cursor getCursor = qwerySort(userId);

		int noRecords = getCursor.getCount();
		Logger.log(Level.DEBUG, TAG, "getAllDiagnoses() noRecords = "
				+ noRecords);

		if (getCursor.moveToFirst()) {
			do {
				String stringId = getCursor.getString(0);
				Integer rowID = Integer.parseInt(stringId);
				int id = -1;
				if (rowID != null) {
					id = rowID.intValue();
				}
				byte[] recData = getCursor.getBlob(1);
				Diagnosis smsMsg = new Diagnosis();
				smsMsg.readData(recData);
				smsMsg.setId(id);

				recordList.add(smsMsg);

			} while (getCursor.moveToNext());
		}
		getCursor.close();
		close();
		return recordList;
	}

}
