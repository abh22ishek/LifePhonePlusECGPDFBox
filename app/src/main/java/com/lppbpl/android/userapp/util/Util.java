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

package com.lppbpl.android.userapp.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.TypedValue;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.ConfigMessage;
import com.lppbpl.EcgLead;
import com.lppbpl.EcgMeasurementList;
import com.lppbpl.Measurement;
import com.lppbpl.MeasurementConfig;
import com.lppbpl.MultipleResponse;
import com.lppbpl.SFMessaging;
import com.lppbpl.TimeData;
import com.lppbpl.BgData.BgSymptoms;
import com.lppbpl.ConfigMessage.ConfigType;
import com.lppbpl.SFMessaging.MessageType;
import com.lppbpl.android.userapp.R;
import com.lppbpl.android.userapp.SouthFallsUserApp;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.controller.SfApplicationController;
import com.lppbpl.android.userapp.model.BgMeasurementList;

/**
 * The Class Util.
 */
public class Util {

	/** The s ecg lead array. */
	private static Vector<Integer> sEcgLeadArray = null;

	/** The Constant TAG. */
	private static final String TAG = "userapp.Util";

	/**
	 * Format the given time as long data type to 10 Apr 2012 13:25:30 PM.
	 *
	 * @param timestamp
	 *            long
	 * @return String
	 */
	public static String formatDateTime(long timestamp) {
		return DateFormat.format("dd MMM yyyy hh:mm:ss A", timestamp)
				.toString();
	}

	public static String formatDate(long timestamp) {
		return DateFormat.format("dd MMM yyyy", timestamp)
				.toString();
	}

	public static String formatTime(long timestamp) {
		return DateFormat.format("hh:mm:ss A", timestamp).toString();
	}

	/** The Constant CM_TO_INCHES. */
//	private static final double CM_TO_INCHES = 0.393700787;

	/**
	 * Method convertToFeets.
	 *
	 * @param heightInCms
	 *            int
	 * @return String
	 */
//	public static String convertToFeets(int heightInCms) {
//		double heightInInches = CM_TO_INCHES * heightInCms;
//		int feets = (int) (heightInInches / 12);
//		int inches = (int) (heightInInches % 12);
//
//		return (feets + "\' " + inches + "\" ft");
//	}

	/**
	 * Method readECGConfig.
	 *
	 * @param context
	 *            Context
	 */
	private static void readECGConfig(Context context) {
		sEcgLeadArray = new Vector<Integer>();
		StringBuffer str = new StringBuffer();

		InputStream is;
		if (Logger.isDebugEnabled()) {
			Logger.log(Level.DEBUG, TAG, "Before opening file");
		}
		is = context.getResources().openRawResource(R.raw.ecg_config);
		if (Logger.isDebugEnabled()) {
			Logger.log(Level.DEBUG, TAG, "file opened successfully");
		}

		if (is == null) {
			return;
		}
		try {
			byte b[] = new byte[1];
			int count = 0;
			while (is.read(b) != -1) {
				if (b[0] == ',') {
					String leads = str.toString().trim();
					for (int i = 1; i <= EcgLead.AVF; i++) {
						if (leads.equals(EcgLead.getStringValue(i))) {
							sEcgLeadArray.add(Integer.valueOf(i));
							count++;
							break;
						}
					}
					str.setLength(0);
				} else {
					str.append((char) b[0]);
				}
				final boolean singleStepECG = SfApplicationController
						.getInstance().getAppModel().getPinModel()
						.isSingleStepECG();
				if (singleStepECG && count == 3) {
					Logger.log(Level.DEBUG, TAG, "demoMode is: enabled ");
					break;
				}
			}
		} catch (IOException ioe) {
			Logger.log(Level.ERROR, TAG, "Error => " + ioe.getMessage());
		} finally {
			try {
				is.close();
				is = null;
			} catch (IOException e) {
				Logger.log(Level.ERROR, TAG, "Error 2 => " + e.getMessage());
			}
		}
	}

	/**
	 * Method getCurrentTimeData.
	 *
	 * @return TimeData
	 */
	private static TimeData getCurrentTimeData() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("UTC"));
		cal.setTime(new Date());
		final TimeData timeData = TimeData.newBuilder()
				.setYear(cal.get(Calendar.YEAR))
				.setMonth(cal.get(Calendar.MONTH))
				.setDay(cal.get(Calendar.DAY_OF_MONTH))
				.setHour(cal.get(Calendar.HOUR_OF_DAY))
				.setMinutes(cal.get(Calendar.MINUTE))
				.setSeconds(cal.get(Calendar.SECOND)).build();
		return timeData;
	}

	/**
	 * Method getConfigMessage.
	 *
	 * @return SFMessaging
	 */
	public static SFMessaging getConfigMessage() {
		readECGConfig(SouthFallsUserApp.getInstance());
		Vector<Integer> ecgLeads = getECGConfig();
		if (Logger.isDebugEnabled()) {
			Logger.log(Level.DEBUG, TAG, "ecgLeads length" + ecgLeads.size());
		}

		MeasurementConfig measurementConfig = MeasurementConfig.newBuilder()
				.setBgWaitTime(Constants.MAX_BG_WAIT_TIME)
				.setCurrentTime(getCurrentTimeData()).setEcgLeads(ecgLeads)
				.build();

		ConfigMessage confMsg = ConfigMessage.newBuilder()
				.setConfigType(ConfigType.Measurement)
				.setMeasurementConfig(measurementConfig).build();

		final SFMessaging messaging = SFMessaging.newBuilder()
				.setSfMsgType(MessageType.ConfigMsg).setSfConfigMsg(confMsg)
				.build();

		return messaging;
	}

	/**
	 * Method getECGConfig.
	 *
	 * @return Vector<Integer>
	 */
	public static Vector<Integer> getECGConfig() {
		return sEcgLeadArray;
	}

	/**
	 * Method getMonth.
	 *
	 * @param month
	 *            int
	 * @return String
	 */
	public static String getMonth(int month) {
		switch (month) {
		case 0:
			return "January";
		case 1:
			return "February";
		case 2:
			return "March";
		case 3:
			return "April";
		case 4:
			return "May";
		case 5:
			return "June";
		case 6:
			return "July";
		case 7:
			return "August";
		case 8:
			return "September";
		case 9:
			return "October";
		case 10:
			return "November";
		case 11:
			return "December";
		default:
			return "invalid";
		}
	}

	/**
	 * Returns Blood sugar symptom string.
	 *
	 * @param i
	 *            the i
	 * @return String
	 */
	public static String getBGSymtoms(int i) {
		switch (i) {
		case BgSymptoms.ExcessiveHunger:
			return "Excessive Hunger";
		case BgSymptoms.ProfuseSweating:
			return "Profuse Sweating";
		default:
			return BgSymptoms.getStringValue(i);
		}
	}

	/**
	 * Returns proper date format to send to cloud.
	 *
	 * @param date
	 *            the date
	 * @return String
	 */
	public static String getDateString(final Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		final StringBuffer dateStr = new StringBuffer();
		final int year = cal.get(Calendar.YEAR);
		final int month = cal.get(Calendar.MONTH) + 1; // Calendar.MONTH starts
														// from 0,
														// Calendar.JANUARY == 0
		final int day = cal.get(Calendar.DAY_OF_MONTH);
		dateStr.append(year);
		dateStr.append('-');
		dateStr.append(month < 10 ? "0" + month : month);
		dateStr.append('-');
		dateStr.append(day < 10 ? "0" + day : day);
		return dateStr.toString();
	}

	/**
	 * Method createMultiResponseBG.
	 *
	 * @param jsonResponse
	 *            String
	 * @return MultipleResponse
	 */
	public static MultipleResponse createMultiResponseBG(
			final String jsonResponse) {
		MultipleResponse.Builder builder = MultipleResponse.newBuilder();
		builder.setMultiMeasurementType(Measurement.BG);
		builder.setMultiRespType(Measurement.BG);

		Vector<BgMeasurementList> v = new Vector<BgMeasurementList>();
		if (jsonResponse != null) {
			try {
				JSONArray jsonArr = new JSONArray(jsonResponse);
				for (int i = 0; i < jsonArr.length(); i++) {
					JSONObject object = jsonArr.getJSONObject(i);
					if (object != null) {
						v.add(new BgMeasurementList(object.getInt("id"), object
								.getLong("created")));
					}
				}

			} catch (JSONException e) {
				Logger.log(Level.DEBUG, TAG, "JSONException e" + e);
			}
		}
		builder.setBgData(v);
		return builder.build();
	}

	/**
	 * Method createMultiResponse.
	 *
	 * @param jsonResponse
	 *            String
	 * @return MultipleResponse
	 */
	public static MultipleResponse createMultiResponse(String jsonResponse) {
		MultipleResponse.Builder builder = MultipleResponse.newBuilder();
		builder.setMultiMeasurementType(Measurement.ECG);
		builder.setMultiRespType(Measurement.ECG);

		if (jsonResponse != null) {
			try {
				JSONArray jsonArr = new JSONArray(jsonResponse);
				for (int i = 0; i < jsonArr.length(); i++) {
					EcgMeasurementList.Builder leadBuilder = EcgMeasurementList
							.newBuilder();
					JSONObject object = jsonArr.getJSONObject(i);
					if (object != null) {
						leadBuilder.setMeasurementId(object.getInt("id"));
						leadBuilder.setEcgTimestamp(object
								.getLong("recordedOn"));
						builder.addElementEcgList(leadBuilder.build());
					}
				}

			} catch (JSONException e) {
				Logger.log(Level.DEBUG, TAG, "JSONException e" + e);
			}
		}
		return builder.build();
	}

	public final static boolean isValidEmail(CharSequence target) {
		return ((!TextUtils.isEmpty(target)) && (android.util.Patterns.EMAIL_ADDRESS
				.matcher(target).matches()));
	}

	public static String getReadableDateStr(Long timestamp) {
		if (timestamp == null) {
			return "";
		}
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp);
		return getReadableDateStr(cal);
	}

	public static String getReadableDateStr(Calendar cal) {
		if (cal == null) {
			return "1-Jan";
		}
		String date = cal.get(Calendar.DAY_OF_MONTH) + " "
				+ getMonth(cal.get(Calendar.MONTH));
		return date;
	}

	public static float getPixelsForDip(Context context, int dipUnit) {
		Resources r = context.getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dipUnit, r.getDisplayMetrics());
		return px;
	}
}
