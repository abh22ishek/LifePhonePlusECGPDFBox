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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.android.userapp.SouthFallsUserApp;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.controller.SfApplicationController;

// TODO: Auto-generated Javadoc
/**
 * The Class Database.
 */
public abstract class Database {

	/** The Constant TAG. */
	private final static String TAG = "Database";

	/** The Constant SF_USERAPP_DB. */
	private static final String SF_USERAPP_DB = "SF_USERAPP_DB";

	// public static final String SFDATABASE_TABLE = "SF_PR_TABLE";
	/** The Constant SFDATABASE_VERSION. */
	private static final int SFDATABASE_VERSION = 2;

	/** The Constant KEY_ROWID. */
	private static final String KEY_ROWID = "_id";

	/** The Constant KEY_RECORD. */
	private static final String KEY_RECORD = "userapp_data";

	/** The Constant KEY_USERID. */
	private static final String KEY_USERID = "user_id";

	// Pending records Table creation
	/** The feedback table. */
	private final String FEEDBACK_TABLE = "create table "
			+ Constants.DIAGNOSIS_MSGLIST_TABLE + " (" + KEY_ROWID
			+ " integer primary key autoincrement, " + KEY_RECORD
			+ " text not null, " + KEY_USERID + "  text not null );";

	/** The measurement record table. */
	private final String MEASUREMENT_RECORD_TABLE = "create table "
			+ Constants.PENDING_RECORDS_TABLE + " (" + KEY_ROWID
			+ " integer primary key autoincrement, " + KEY_RECORD
			+ " text not null, " + KEY_USERID + "  text not null );";

	/** The m sq lite helper. */
	private SQLiteHelper mSqLiteHelper;

	/** The m sq lite database. */
	private SQLiteDatabase mSqLiteDatabase;

	/** The m context. */
	private Context mContext;

	/** The m table name. */
	private final String mTableName;

	/**
	 * Constructor for Database.
	 *
	 * @param fileName
	 *            String
	 */
	Database(String fileName) {
		this.mTableName = fileName;
		this.mContext = SouthFallsUserApp.getInstance().getApplicationContext();
	}

	/**
	 * Method getUserId.
	 *
	 * @return String
	 */
	private String getUserId() {
		String userId = null;
		if (SfApplicationController.getInstance().getAppModel().getPinModel()
				.isLoginForSessionSuccess()) {
			userId = SfApplicationController.getInstance().getAppModel()
					.getUserUniqueId();

		}
		return userId;
	}

	/**
	 * Open SQLiteDatabase Connection.
	 *
	 * @return Database
	 * @throws SQLException
	 *             the SQL exception
	 */
	protected Database open() throws android.database.SQLException {
		Logger.log(Level.DEBUG, TAG, "Open the DB ++");
		mSqLiteHelper = new SQLiteHelper(mContext, SF_USERAPP_DB, null,
				SFDATABASE_VERSION);
		mSqLiteDatabase = mSqLiteHelper.getWritableDatabase();
		Logger.log(Level.DEBUG, TAG, "Open the DB --");
		return this;
	}

	/**
	 * Close SQLiteDatabase Connection.
	 */
	public void close() {
		Logger.log(Level.DEBUG, TAG, "close ++");
		if (mSqLiteHelper != null) {
			mSqLiteHelper.close();
		}
		Logger.log(Level.DEBUG, TAG, "close --");
	}

	/**
	 * INSERTING/ADDING/WRITING record.
	 *
	 * @param data
	 *            byte[]
	 * @return int
	 */
	protected int add(final byte[] data) {
		return add(data, getUserId());
	}

	/**
	 * Method add.
	 *
	 * @param data
	 *            byte[]
	 * @param userId
	 *            String
	 * @return int
	 */
	protected int add(final byte[] data, final String userId) {
		Logger.log(Level.DEBUG, TAG, "add++");
		ContentValues values = createContentValues(data, userId);

		Logger.log(Level.DEBUG, TAG, "add:: insert record into database");
		long retVal = mSqLiteDatabase.insert(mTableName, null, values);
		if (retVal == -1) {
			// TODO: suppress toast
			Toast.makeText(mContext, "RECORD ADDTION FAILED",
					Toast.LENGTH_SHORT).show();
		}

		return (int) retVal;

	}

	/**
	 * Updating the record.
	 *
	 * @param recId
	 *            the rec id
	 * @param data
	 *            the data
	 * @return int
	 */
	protected int update(final int recId, final byte[] data) {
		Logger.log(Level.DEBUG, TAG, "update++");
		ContentValues values = createContentValues(data, getUserId());
		String where = KEY_ROWID + "=" + recId;
		Logger.log(Level.DEBUG, TAG, "add:: insert record into database");
		int retVal = mSqLiteDatabase.update(mTableName, values, where, null);
		if (retVal == -1) {
			Toast.makeText(mContext, "RECORD UPDATION FAILED",
					Toast.LENGTH_SHORT).show();
		}

		return retVal;
	}

	/**
	 * Method createContentValues.
	 *
	 * @param data
	 *            byte[]
	 * @param userId
	 *            String
	 * @return ContentValues
	 */
	private ContentValues createContentValues(final byte[] data,
			final String userId) {
		ContentValues values = new ContentValues();
		Logger.log(Level.DEBUG, TAG, "createContentValues++");
		values.put(KEY_RECORD, data);
		if (userId != null) {
			values.put(KEY_USERID, userId);
		}
		Logger.log(Level.DEBUG, TAG, "createContentValues--");
		return values;
	}

	/**
	 * Method readData.
	 *
	 * @param recID
	 *            int
	 * @return byte[]
	 */
	public byte[] readData(final int recID) {
		Logger.log(Level.DEBUG, TAG, "readData++ recID" + recID);
		byte[] readData = null;
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
					Logger.log(Level.DEBUG, TAG, "readDaata++ id" + id);
					if (id == recID) {
						readData = getCursor.getBlob(1);
						break;
					}
				} while (getCursor.moveToNext());
			}
			getCursor.close();
		} else {
		}

		return readData;
	}

	/**
	 * Query the records in ascending order.
	 *
	 * @return Cursor
	 */
	protected Cursor queueAll() {
		final String where = KEY_USERID + "='" + getUserId() + "'";
		Logger.log(Level.DEBUG, TAG, "Query ++");
		Cursor cursor = mSqLiteDatabase.query(mTableName, new String[] {
				KEY_ROWID, KEY_RECORD, KEY_USERID }, where, null, null, null,
				null);
		Logger.log(Level.DEBUG, TAG, "Query --");

		return cursor;
	}

	/**
	 * Query the records in descending order.
	 *
	 * @return Cursor
	 */
	protected Cursor qwerySort(final String userId) {
		Logger.log(Level.DEBUG, TAG, "Query ++");
		String where = null;
		if (userId != null) {
			where = KEY_USERID + "='" + userId + "'";
		} else {
			where = KEY_USERID + "='" + getUserId() + "'";
		}
		Cursor cursor = mSqLiteDatabase.query(mTableName, new String[] {
				KEY_ROWID, KEY_RECORD, KEY_USERID }, where, null, null, null,
				KEY_ROWID + " DESC");
		Logger.log(Level.DEBUG, TAG, "Query --");

		return cursor;
	}

	/**
	 * delete record.
	 *
	 * @param recID
	 *            the rec id
	 * @return int
	 */
	protected int delete(int recID) {
		Logger.log(Level.DEBUG, TAG, "delete recId=++ " + recID);
		Logger.log(Level.DEBUG, TAG, "delete --");
		return mSqLiteDatabase
				.delete(mTableName, KEY_ROWID + "=" + recID, null);

	}

	/**
	 * Delete all records.
	 */
	// protected void deleteAll() {
	// Logger.log(Level.DEBUG, TAG, "delete ++");
	// mSqLiteDatabase.delete(mTableName, null, null);
	// Logger.log(Level.DEBUG, TAG, "delete --");
	// }

	/**
	 * Returns number of records.
	 *
	 * @return int
	 */
	public int numOfRecords() {
		int numOfRecords = 0;
		open();
		final String where = KEY_USERID + "='" + getUserId() + "'";
		Logger.log(Level.DEBUG, TAG, "Query ++");
		Cursor cursor = mSqLiteDatabase.query(mTableName, new String[] {
				KEY_ROWID, KEY_USERID }, where, null, null, null, null);
		numOfRecords = cursor.getCount();
		cursor.close();
		close();
		Logger.log(Level.DEBUG, TAG, "Query --");
		return numOfRecords;
	}

	/**
	 * The Class SQLiteHelper.
	 */
	class SQLiteHelper extends SQLiteOpenHelper {

		/**
		 * Constructor for SQLiteHelper.
		 *
		 * @param context
		 *            Context
		 * @param name
		 *            String
		 * @param factory
		 *            CursorFactory
		 * @param version
		 *            int
		 */
		public SQLiteHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
			Logger.log(Level.DEBUG, TAG, "HelperClass Constructor");
		}

		// To create database for the first time
		/**
		 * Method onCreate.
		 *
		 * @param db
		 *            SQLiteDatabase
		 */
		public void onCreate(SQLiteDatabase db) {
			Logger.log(Level.DEBUG, TAG, "HelperClass Create DATABASE++");
			db.execSQL(FEEDBACK_TABLE);

			db.execSQL(MEASUREMENT_RECORD_TABLE);
			Logger.log(Level.DEBUG, TAG, "HelperClass Create DATABASE--");
		}

		// To upgrade database every time
		/**
		 * Method onUpgrade.
		 *
		 * @param db
		 *            SQLiteDatabase
		 * @param oldVersion
		 *            int
		 * @param newVersion
		 *            int
		 */
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Logger.log(Level.DEBUG, TAG, "HelperClass onUpgrade DATABASE++");
			onCreate(db);
			Logger.log(Level.DEBUG, TAG, "HelperClass onUpgrade DATABASE--");
		}
	}

}
