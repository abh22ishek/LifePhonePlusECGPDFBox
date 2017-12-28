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

import java.lang.ref.WeakReference;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import net.jarlehansen.protobuf.javame.UninitializedMessageException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.EcgData;
import com.lppbpl.EcgLead;
import com.lppbpl.EcgMeasurementList;
import com.lppbpl.EcgMultipleLead;
import com.lppbpl.HrData;
import com.lppbpl.Measurement;
import com.lppbpl.MultipleResponse;
import com.lppbpl.Response;
import com.lppbpl.ResponseType;
import com.lppbpl.BgData.BgSymptoms;
import com.lppbpl.EcgData.EcgSymptoms;
import com.lppbpl.android.userapp.R;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.model.BgMeasurementList;
import com.lppbpl.android.userapp.model.MyCaseInfo;
import com.lppbpl.android.userapp.model.SfSendModel;
import com.lppbpl.android.userapp.util.HttpUtil;
import com.lppbpl.android.userapp.util.Util;

// TODO: Auto-generated Javadoc
/**
 * This Activity is to display The list of latest records fetched from cloud.
 *
 */
public class GetRecordsDataList extends AppBaseActivity {

	/** The Constant TAG. */
	private static final String TAG = GetRecordsDataList.class.getName();

	/** The ecg getdata response received. */
	private final int ECG_GETDATA_RESPONSE_RECEIVED = 100;

	/** The bg getdata response received. */
	private final int BG_GETDATA_RESPONSE_RECEIVED = 101;

	/** The m saved ecg data. */
	private String mSavedEcgData;

	/** The m http util. */
	public HttpUtil mHttpUtil;

	/** The m my cases. */
	private boolean mMyCases;

	/** The m saved bg data. */
	protected String mSavedBGData;

	/** The m response error. */
	private boolean mResponseError = false;

	/**
	 * Method onCreate.
	 *
	 * @param savedInstanceState
	 *            Bundle
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.multirow_list);

		SfSendModel.getInstance().setSendStatus(-1);

		mMyCases = getIntent().getBooleanExtra("my_cases", false);
		setCustomNoIconTitle(mMyCases ? R.string.title_consulted_cases
				: R.string.title_recrods);
		// mMyCases is true then will display the list of consulted cases
		final Vector<MyCaseInfo> myCaseInfo = mAppModel.getMyCasesData();
		@SuppressWarnings("rawtypes")
		final Vector measurementDetails = mMyCases ? myCaseInfo
				: getMeasurementDetails();
		@SuppressWarnings("rawtypes")
		final Vector timestamps = getMeasurementTimestamps();

		final ListView lv = (ListView) findViewById(R.id.lMultiListView);
		lv.setAdapter(new LatestRecCustAdapter(this, measurementDetails,
				timestamps));

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				mAppModel.clearPendingRecord();
				mAppModel.createPendingRecord(getMeasurementType());

				if (getMeasurementType() == Measurement.ECG
						|| getMeasurementType() == Measurement.BG || mMyCases) {
					Logger.log(Level.INFO, TAG, "on item click");
					final Object object = lv.getItemAtPosition(position);
					if (object instanceof EcgMeasurementList) {
						final EcgMeasurementList ecgItem = (EcgMeasurementList) object;
						getECGDataRequest(ecgItem.getMeasurementId());
					} else if (object instanceof BgMeasurementList) {
						final BgMeasurementList bgItem = (BgMeasurementList) object;
						mAppModel.setBgCaseDetail("Measurement Id: "
								+ bgItem.getBgMeasurementId());
						mAppModel.setBgStartTime(bgItem.getBgMeasurementTime());
						doBGDataRequest(bgItem.getBgMeasurementId());
					} else if (object instanceof MyCaseInfo) {
						final MyCaseInfo info = (MyCaseInfo) object;
						final int measureType = 1 == info.getSpecializationId() ? Measurement.ECG
								: Measurement.BG;
						mAppModel.clearPendingRecord();
						mAppModel.createPendingRecord(measureType);
						if (measureType == Measurement.ECG) {
							mAppModel.setEcgCaseDetail("Measurement Id: "
									+ info.getMeasurementId() + ","
									+ System.getProperty("line.separator")
									+ "TAT Type: " + info.getTatType() + ","
									+ System.getProperty("line.separator")
									+ "Status: " + info.getCaseStatus());
							getECGDataRequest(info.getMeasurementId());
						} else if (measureType == Measurement.BG) {
							mAppModel.setBgCaseDetail("Measurement Id: "
									+ info.getMeasurementId() + ","
									+ System.getProperty("line.separator")
									+ "TAT Type: " + info.getTatType() + ","
									+ System.getProperty("line.separator")
									+ "Status: " + info.getCaseStatus());
							mAppModel.setBgStartTime(info.getStartTime());
							doBGDataRequest(info.getMeasurementId());
						}
					}
				}
			}
		});

		mProgDialog.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface arg0) {
				if (null != mHttpUtil) {
					Logger.log(Level.DEBUG, TAG, "ProgressDialog Dismissed");
					mHttpUtil.disconnect();
					mHttpUtil = null;
				}
			}
		});

	}

	/**
	 * Method getMeasurementDetails.
	 *
	 * @return Vector
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Vector getMeasurementDetails() {
		final Vector results = new Vector();

		final MultipleResponse mulRes = mAppModel.getGetDataResponse();

		if (null != mulRes) {
			if (mulRes.getMultiMeasurementType() == Measurement.ACT) {
				results.addAll(mulRes.getActData());
			} else if (mulRes.getMultiMeasurementType() == Measurement.BG) {
				results.addAll(mulRes.getBgData());
				results.addAll(mulRes.getEcgList());
			} else if (mulRes.getMultiMeasurementType() == Measurement.ECG) {
				results.addAll(mulRes.getEcgList());
			}
		}
		return results;
	}

	/**
	 * Method getMeasurementTimestamps.
	 *
	 * @return Vector
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Vector getMeasurementTimestamps() {
		final Vector results = new Vector();

		final MultipleResponse mulRes = mAppModel.getGetDataResponse();

		if (null != mulRes) {
			if (mulRes.getMultiMeasurementType() == Measurement.BG
					|| mulRes.getMultiMeasurementType() == Measurement.ACT) {
				results.addAll(mulRes.getTimestamp());
			}
		}
		return results;
	}

	/**
	 * Adapter for the latest records list.
	 *
	 */
	public class LatestRecCustAdapter extends BaseAdapter {

		/** The m inflater. */
		private final LayoutInflater mInflater;

		/** The search array list. */
		@SuppressWarnings("rawtypes")
		public final Vector searchArrayList;

		/** The timestamps. */
		@SuppressWarnings("rawtypes")
		public final Vector timestamps;

		/**
		 * Constructor for LatestRecCustAdapter.
		 *
		 * @param context
		 *            Context
		 * @param results
		 *            Vector
		 * @param timestamps
		 *            Vector
		 */
		@SuppressWarnings("rawtypes")
		public LatestRecCustAdapter(Context context, Vector results,
				Vector timestamps) {
			searchArrayList = results;
			this.timestamps = timestamps;
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
				view = mInflater.inflate(R.layout.custom_list_title, null);
				holder = new ViewHolder();
				holder.measurementVal = (TextView) view
						.findViewById(R.id.txt_measurement_info);
				holder.measurementDate = (TextView) view
						.findViewById(R.id.txt_measurement_date);
				holder.measurementTime = (TextView) view
						.findViewById(R.id.txt_measurement_time);
				view.setTag(holder);
				view.setBackgroundResource(R.xml.multiline_listview_style);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			if (mMyCases) {
				// Set the text to Consulted cases list item
				final MyCaseInfo object = (MyCaseInfo) searchArrayList
						.get(position);
				final String measureType = (1 == object.getSpecializationId()) ? "ECG"
						: "BG";
				holder.measurementVal.setText(measureType + ", Case Id: "
						+ object.getCaseId());
				holder.measurementDate.setText(Util.formatDate(object
						.getStartTime()));
				holder.measurementTime.setText(Util.formatTime(object
						.getStartTime()));
			} else {
				final MultipleResponse mulRes = mAppModel.getGetDataResponse();
				int measurementId = 0;
				long measurementTime = 0;
				if (mulRes.getMultiMeasurementType() == Measurement.ECG) {
					final EcgMeasurementList measureList = (EcgMeasurementList) searchArrayList
							.elementAt(position);
					measurementId = measureList.getMeasurementId();
					measurementTime = measureList.getEcgTimestamp();
				} else if (mulRes.getMultiMeasurementType() == Measurement.BG) {
					final BgMeasurementList bgList = (BgMeasurementList) searchArrayList
							.elementAt(position);
					measurementId = bgList.getBgMeasurementId();
					measurementTime = bgList.getBgMeasurementTime();
				}

				holder.measurementVal.setText(Measurement.getStringValue(mulRes
						.getMultiMeasurementType())
						+ ", Measurement Id: "
						+ measurementId);

				holder.measurementDate.setText(Util.formatDate(measurementTime));
				holder.measurementTime.setText(Util.formatTime(measurementTime));
			}

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
	 * Returns the type of measurement.
	 *
	 * @return int
	 */
	private int getMeasurementType() {
		int measurementType = 0;
		final MultipleResponse mulRes = mAppModel.getGetDataResponse();
		if (null != mulRes) {
			if (mulRes.getMultiMeasurementType() == Measurement.ECG) {
				measurementType = Measurement.ECG;

			} else if (mulRes.getMultiMeasurementType() == Measurement.BG) {
				measurementType = Measurement.BG;

			} else if (mulRes.getMultiMeasurementType() == Measurement.ACT) {
				measurementType = Measurement.ACT;

			} else {
			}
		}
		return measurementType;
	}

	/**
	 * Process the Ecg response from the cloud.
	 *
	 *
	 * @param ecgResponse
	 *            String
	 */
	private void buildECGResponse(final String ecgResponse) {
		try {
			final JSONObject object = new JSONObject(ecgResponse);
			final String annotation = object.getString("annotation");
			final int heartRate = object.getInt("heartRate");
			final int caseId = object.getInt("caseId");
			mAppModel.setCaseId(caseId);

			final JSONArray objSym = object.getJSONArray("symptoms");
			final Vector<Integer> symtems = new Vector<Integer>();
			for (int i = 0; i < objSym.length(); i++) {
				for (int j = EcgSymptoms.ChestPain; j <= EcgSymptoms.Anxiety; j++) {
					if (objSym.getString(i).equalsIgnoreCase(
							EcgSymptoms.getStringValue(j))) {
						symtems.add(j);
					}
				}
			}

			final JSONArray objArr = object.getJSONArray("leads");
			final int totalLeads = objArr.length();
			mAppModel.getPendingRecord().createResponse(totalLeads);
			JSONObject o = null;
			int stepNum = 0;
			long measurementTime = 0;
			String leadName = null;
			int duration = 0;
			String leads = null;
			String[] leadsValue = null;
			Vector<Integer> v = null;
			EcgMultipleLead.Builder multLeadBuilder = null;
			EcgData.Builder ecgDataBuilder = null;
			HrData tempHrData = null;
			Response response = null;
			for (int i = 0; i < totalLeads; i++) {
				o = objArr.getJSONObject(i);
				stepNum = o.getInt("stepNum");
				measurementTime = o.getLong("timestamp");
				leadName = o.getString("name");
				duration = o.getInt("duration");
				leads = o.getString("stripValue");
				leadsValue = leads.split(",");
				v = new Vector<Integer>();
				for (int j = 0; j < leadsValue.length; j++) {
					v.add(Integer.parseInt(leadsValue[j]));
				}

				multLeadBuilder = EcgMultipleLead.newBuilder();
				multLeadBuilder.setLead(getLeadNum(leadName));
				multLeadBuilder.setEcgStrip(v);
				multLeadBuilder.setStepNum(stepNum);
				multLeadBuilder.setTimestamp(measurementTime);
				ecgDataBuilder = EcgData.newBuilder().addElementMulLead(
						multLeadBuilder.build());
				ecgDataBuilder.setDuration(duration);
				// its required to be updated in
				if (i == (totalLeads - 1)) {
					ecgDataBuilder.setAnnotationTxt(annotation);
					ecgDataBuilder.setEcgSymptoms(symtems);
					tempHrData = HrData.newBuilder().setHeartRate(heartRate)
							.build();
				}

				response = Response.newBuilder()
						.setResponseType(ResponseType.SRD)
						.setEcgData(ecgDataBuilder.build())
						.setMeasurementType(Measurement.ECG)
						// .setTimestamp(ecgDataResp.getTimestamp())
						.setHrData(tempHrData).build();
				mAppModel.getPendingRecord().setResponse(response, i);
			}

		} catch (JSONException e) {
			Logger.log(Level.ERROR, TAG, "JSONException:" + e);
			mSavedEcgData = null;
		}

	}

	/**
	 * Method getLeadNum.
	 *
	 * @param leadName
	 *            String
	 * @return int
	 */
	private int getLeadNum(String leadName) {
		int lead = EcgLead.LEAD1;
		for (int i = EcgLead.LEAD1; i <= EcgLead.AVF; i++) {
			if (leadName.equalsIgnoreCase(EcgLead.getStringValue(i))) {
				lead = i;
				break;
			}
		}
		return lead;
	}

	/**
	 * Creates a thread to make request to get the details of clicked Consulted
	 * blood sugar record from the list.
	 *
	 * @param measurementId
	 *            the measurement id
	 */
	public void doBGDataRequest(int measurementId) {
		showProgressDialog(getString(R.string.loading_text));
		final GetBGDataRecordsThread thread = new GetBGDataRecordsThread(
				measurementId);
		thread.start();
	}

	/**
	 * Thread Makes the request to get the details of clicked Consulted blood
	 * sugar record from the list.
	 *
	 */
	public class GetBGDataRecordsThread extends Thread {

		/** The measurement id. */
		final private int measurementId;

		/**
		 * Constructor for GetBGDataRecordsThread.
		 *
		 * @param id
		 *            int
		 */
		public GetBGDataRecordsThread(int id) {
			measurementId = id;
			if (null != mHttpUtil) {
				mHttpUtil.disconnect();
				mHttpUtil = null;
			}
			mHttpUtil = new HttpUtil();
			mSavedBGData = null;
		}

		/**
		 * Method run.
		 *
		 * @see Runnable#run()
		 */
		public void run() {
			super.run();
			try {
				mResponseError = false;
				Logger.log(Level.DEBUG, TAG, "postGetDataRequest++");
				if (isNetworkConnected()) {
					final SortedMap<String, String> reqHeaders = mHttpUtil
							.getCommonRequestHeader();
					reqHeaders.put(HttpUtil.ACCEPT_TYPE,
							HttpUtil.APPLICATION_JSON);
					reqHeaders.put(HttpUtil.CONTENT_TYPE,
							HttpUtil.FORM_URLENCODED_TYPE);

					final SortedMap<String, String> reqParam = new TreeMap<String, String>();

					final String URL = mPinModel.getServerAddress()
							+ Constants.BG_CONSULTED_CASE_DETAIL
							+ measurementId;
					mSavedBGData = mHttpUtil.postDataThroughParams(
							HttpUtil.POST, URL, reqParam, reqHeaders);
					parseResponse(mSavedBGData);
				}

				Logger.log(Level.DEBUG, TAG, "postGetDataRequest--");
			} catch (UninitializedMessageException e) {
				// mResponseError = true;
				Logger.log(Level.DEBUG, TAG, "exception++=" + e);
				mAppModel.setGetDataResponse(null);
				mSavedBGData = null;
			} catch (NullPointerException npe) {
				Logger.log(Level.DEBUG, TAG, "exception++=" + npe);
				mSavedBGData = null;
			} finally {
				final Message msg = new Message();
				msg.what = BG_GETDATA_RESPONSE_RECEIVED;
				msg.obj = Integer.valueOf(measurementId);
				mHandler.sendMessage(msg);
			}
		}
	}

	/**
	 * Parse the json response back from the cloud.
	 *
	 * @param response
	 *            the response
	 */
	public void parseResponse(String response) {
		JSONArray jsonArray = null;
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(response);
		} catch (JSONException e) {
			Logger.log(Level.ERROR, TAG, "" + e);
		}
		if (null == jsonObject) {
			mResponseError = true;
			mSavedBGData = null;
		} else {
			try {
				mAppModel.setBgReadingValue(jsonObject.getDouble("bgReading"));
				mAppModel.setComment(jsonObject.getString("annotation_txt"));
				mAppModel.setBGReadingType(jsonObject
						.getString("bgReadingType"));
				mAppModel.setCaseId(jsonObject.getInt("caseId"));
				jsonArray = jsonObject.getJSONArray("symptoms");
				final StringBuffer oldSymptoms = new StringBuffer();
				if (null != jsonArray) {
					final int len = jsonArray.length();
					String string;
					String symptoms = null;
					for (int i = 0; i < len; i++) {
						string = jsonArray.getString(i);
						if (string.equals(BgSymptoms
								.getStringValue(BgSymptoms.ExcessiveHunger))) {
							symptoms = "Excessive Hunger";
							oldSymptoms.append(symptoms);
							if (i != (len - 1)) {
								oldSymptoms.append(',');
							}
						} else if (string.equals(BgSymptoms
								.getStringValue(BgSymptoms.ProfuseSweating))) {
							symptoms = "Profuse Sweating";
							oldSymptoms.append(symptoms);
							if (i != (len - 1)) {
								oldSymptoms.append(',');
							}
						} else {
							oldSymptoms.append(string);
							if (i != (len - 1)) {
								oldSymptoms.append(',');
							}
						}
						oldSymptoms.append(' ');
					}
					symptoms = null;
					mAppModel.setBGCaseSymptoms(oldSymptoms.toString());
				}
			} catch (JSONException e) {
				mResponseError = true;
				mSavedBGData = null;
				Logger.log(Level.ERROR, TAG, "" + e);
			}

		}

	}

	/**
	 * Returns the Blood sugar symptoms.
	 *
	 * @param i
	 *            the i
	 * @return String
	 */
	public static String getBGSymtoms(int i) {
		String symtem = null;
		switch (i) {
		case BgSymptoms.ExcessiveHunger:
			symtem = "Excessive Hunger";
			break;
		case BgSymptoms.ProfuseSweating:
			symtem = "Profuse Sweating";
			break;
		default:
			symtem = BgSymptoms.getStringValue(i);
			break;
		}
		return symtem;
	}

	/**
	 * process request to get the details of clicked Consulted Ecg record from
	 * the list.
	 *
	 * @param measurementId
	 *            int
	 * @return the ECG data request
	 */
	public void getECGDataRequest(int measurementId) {
		showProgressDialog(getString(R.string.loading_text));

		final GetECGDataRecordsThread thread = new GetECGDataRecordsThread(
				measurementId);
		thread.start();
	}

	/**
	 * Thread Makes the request to get the details of clicked Consulted Ecg
	 * record from the list.
	 *
	 */
	public class GetECGDataRecordsThread extends Thread {

		/** The measurement id. */
		private final int measurementId;

		/**
		 * Constructor for GetECGDataRecordsThread.
		 *
		 * @param measurementId
		 *            int
		 */
		public GetECGDataRecordsThread(int measurementId) {
			this.measurementId = measurementId;
			mSavedEcgData = null;
		}

		/**
		 * Method run.
		 *
		 * @see Runnable#run()
		 */
		@Override
		public void run() {
			super.run();

			try {

				// GetData Request

				// uploadStatus = "0";
				if (Logger.isDebugEnabled()) {
					Logger.log(Level.DEBUG, TAG,
							"GetECG Data request sent to URL "
									+ Constants.GET_ECGDATA_URL);
				}
				if (isNetworkConnected()) {
					mHttpUtil = new HttpUtil();
					final SortedMap<String, String> reqHeaders = mHttpUtil
							.getCommonRequestHeader();
					reqHeaders.put(HttpUtil.ACCEPT_TYPE,
							HttpUtil.APPLICATION_JSON);
					reqHeaders.put(HttpUtil.CONTENT_TYPE,
							HttpUtil.FORM_URLENCODED_TYPE);

					final SortedMap<String, String> reqParam = new TreeMap<String, String>();

					final String URL = mPinModel.getServerAddress()
							+ Constants.ECG_CONSULTED_CASE_DETAIL
							+ measurementId;
					mSavedEcgData = mHttpUtil.postDataThroughParams(
							HttpUtil.POST, URL, reqParam, reqHeaders);

					buildECGResponse(mSavedEcgData);

				}

			} catch (NullPointerException npe) {
				mSavedEcgData = null;
				Logger.log(Level.DEBUG, TAG, "null Error " + npe);
			} finally {
				final Message msg = new Message();
				msg.what = ECG_GETDATA_RESPONSE_RECEIVED;
				msg.obj = Integer.valueOf(measurementId);
				mHandler.sendMessage(msg);
			}
		}
	}

	/**
	 * Handler to handle the information and errors back from cloud.
	 */
	final private MyHandler mHandler = new MyHandler(this);

	/**
	 * The Class MyHandler.
	 */
	private static class MyHandler extends Handler {

		/** The m activity. */
		private final WeakReference<GetRecordsDataList> mActivity;

		/**
		 * Constructor for MyHandler.
		 *
		 * @param activity
		 *            GetRecordsDataList
		 */
		private MyHandler(GetRecordsDataList activity) {
			mActivity = new WeakReference<GetRecordsDataList>(activity);
		}

		/**
		 * Method handleMessage.
		 *
		 * @param msg
		 *            android.os.Message
		 */
		public void handleMessage(Message msg) {
			final GetRecordsDataList parent = mActivity.get();
			if (msg.what == parent.ECG_GETDATA_RESPONSE_RECEIVED) {
				parent.dismissProgressDislog();
				Logger.log(Level.DEBUG, TAG, "toLaunchMycasesList");
				if (null != parent.mSavedEcgData) {
					// parent.buildECGResponse(parent.mSavedEcgData);
					final Intent intent = new Intent(parent,
							SavedECGGraph.class);
					intent.putExtra("disable_menubar", true);
					intent.putExtra("my_case", parent.mMyCases);
					intent.putExtra("measurementId", (Integer) msg.obj);
					parent.startActivity(intent);
				} else {
					if (parent.isNetworkConnected()) {
						parent.showAlertDialog(R.drawable.ic_dialog_error,
								parent.get(R.string.error),
								parent.get(R.string.ecg_record_fetch_error),
								parent.get(R.string.OK), null, true);
					} else {
						parent.showAlertDialog(
								R.drawable.ic_dialog_no_signal,
								parent.get(R.string.network_connection),
								HttpUtil.getRespnseMsg(HttpUtil.HTTP_CONNECTION_DOWN),
								parent.get(R.string.OK), null, true);
					}
				}
			} else if (msg.what == parent.BG_GETDATA_RESPONSE_RECEIVED) {
				parent.dismissProgressDislog();
				Logger.log(Level.DEBUG, TAG, "bg case detail");
				if (null != parent.mSavedBGData) {
					final Intent intent = new Intent(parent,
							BGMycaseDetailActivity.class);
					intent.putExtra("measurementId", (Integer) msg.obj);
					parent.startActivity(intent);
				} else {
					if (parent.isNetworkConnected()) {
						parent.showAlertDialog(
								R.drawable.ic_dialog_error,
								parent.get(R.string.error),
								parent.mResponseError ? parent.get(R.string.bg_record_fetch_error)
										: parent.get(R.string.response_failed),
								parent.get(R.string.OK), null, true);
					} else {
						parent.showAlertDialog(
								R.drawable.ic_dialog_error,
								parent.get(R.string.network_connection),
								HttpUtil.getRespnseMsg(HttpUtil.HTTP_CONNECTION_DOWN),
								parent.get(R.string.OK), null, true);
					}
				}
			}

		}
	};
}
