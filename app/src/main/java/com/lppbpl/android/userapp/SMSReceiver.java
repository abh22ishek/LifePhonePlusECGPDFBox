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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.android.userapp.controller.SfApplicationController;
import com.lppbpl.android.userapp.db.DiagnosisMsgDb;
import com.lppbpl.android.userapp.listener.SfBTDataListener;
import com.lppbpl.android.userapp.model.Diagnosis;

import java.util.Date;

// TODO: Auto-generated Javadoc
/**
 * BroadcastReceiver receives the feedback messages from the doctor.
 *
 */
public class SMSReceiver extends BroadcastReceiver {

	/** The m dlg txt in fg. */
	final private String mDlgTxtInFG = "Feedback";

	/**ht
	 * Method onReceive.
	 *
	 * @param context
	 *            Context
	 * @param smsData
	 *            Intent
	 */
	@Override
	public void onReceive(final Context context, final Intent smsData) {

		Logger.log(Level.DEBUG, "SMSReceiver", "onReceive");

		// gets the sms data
		final Bundle smsBundle = smsData.getExtras();

		if (null != smsBundle) {
			final Object[] pdus = (Object[]) smsBundle.get("pdus");
			final SmsMessage[] msgs = new SmsMessage[pdus.length];
			String senderAddr = null;
			String msgBody = null;
			String[] split = null;
			String userId = null;
			String message = null;
			Date date = new Date();
			long timestamp = 0;
			Diagnosis diagnosisData = null;
			DiagnosisMsgDb diagnosisDB = null;
			SfBTDataListener obj = null;
			for (int i = 0; i < msgs.length; i++) {
				msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);

				senderAddr = msgs[i].getOriginatingAddress();
				abortBroadcast();

				msgBody = msgs[i].getMessageBody();
				Logger.log(Level.DEBUG, "SMSReceiver", "msgBody=" + msgBody);
				if (i == 0) {
					split = msgBody.split("\\|", 2);
					if (split.length > 0) {
						userId = split[0];
					}

					if (split.length > 1) {
						message = split[1];
					}
				} else {
					message += msgBody;
				}

				Logger.log(Level.DEBUG, "SMSReceiver", "userId=" + userId
						+ ", message=" + message);

				timestamp = msgs[i].getTimestampMillis();
				date.setTime(timestamp);

			}

			// Put it in Diagnosis format
			diagnosisData = new Diagnosis();
			diagnosisData.setSenderAddress(senderAddr);
			diagnosisData.setMsg(message);
			diagnosisData.setDate(date);
			diagnosisData.setReadStatus(false);
			// Adds feedback message to db
			diagnosisDB = DiagnosisMsgDb.getInstance();
			diagnosisDB.addDiagnosis(diagnosisData, userId);
			Logger.log(Level.DEBUG, "SMSReceiver",
					"diagnosis msg is added to DB");
			// to handle feedback messages while ecg measurement running
			obj = SfApplicationController.getInstance().getBluetoothDataListener();
			if (!(obj instanceof ECGActivity)) {
				launchAlertActivity(context, mDlgTxtInFG, true, userId);
			}

		}

	}

	/**
	 * Launch the AlertPopupActivity screen to alert the user received the
	 * feedback message from doctor.
	 *
	 * @param context
	 *            the context
	 * @param dlgHeader
	 *            the dlg header
	 * @param isFG
	 *            the is fg
	 */
	public static void launchAlertActivity(final Context context,
			final String dlgHeader, final boolean isFG, final String userId) {
		Logger.log(Level.DEBUG, "SMSReceiver", "Starting Alertpopup activity++");
		final Intent diaActivity = new Intent(context, AlertPopupActivity.class);
		diaActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		final Bundle bundle = new Bundle();
		bundle.putBoolean("isFG", isFG);
		bundle.putString("dlgHdr", dlgHeader);
		bundle.putString("userId", userId);
		diaActivity.putExtras(bundle);
		Logger.log(Level.DEBUG, "SMSReceiver", "Starting Alertpopup activity--");
		context.startActivity(diaActivity);
	}
}
