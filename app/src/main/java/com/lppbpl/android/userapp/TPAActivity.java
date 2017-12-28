package com.lppbpl.android.userapp;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.SortedMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.Response;
import com.lppbpl.ResponseType;
import com.lppbpl.android.userapp.R;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.model.TATCategoryModel;
import com.lppbpl.android.userapp.model.TPAContentModel;
import com.lppbpl.android.userapp.model.TPAInfoModel;
import com.lppbpl.android.userapp.util.HttpUtil;

public class TPAActivity extends AppBaseActivity implements
OnClickListener, OnTouchListener {

	/** The Constant TAG. */
	private static final String TAG = TPAActivity.class.getSimpleName();
	/** The m agree. */
	private Button mAgree;

	/** The m agree check. */
	private CheckBox mAgreeCheck;

	/** The m http util. */
	private HttpUtil mHttpUtil;

	/** The is shut down ack. */
	private boolean isShutDownAck;

	private DoTPARequest mTPARequest;
	private DoTPAInfoRequest mTPAInfoRequest;
	private DoTPAAcceptRequest mTPAAcceptRequest;
	private GetCurrentTATInfoRequest mCurrentTATInfoRequest;

	/** Intent parameters */
	private boolean callInfoApi;
	private int calledFromActivity;

	/** Data model variables */
	private TPAContentModel tpaContentModel;
	private TPAInfoModel tpaInfoModel;
	private ArrayList<TATCategoryModel> tpaTatInfoList;

	public static final String KEY_CALL_INFO_API = "call_info_api";
	public static final String KEY_FROM_ACTIVITY = "from_activity";

	/** Constant for request from default activity i.e. TPAActivity */
	public static final int FROM_DEFAULT_ACTIVITY = 0;

	/** Constant for request from login activity */
	public static final int FROM_LOGIN_ACTIVITY = 1;

	/** Constant for request from bluetooth pairing activity */
	public static final int FROM_BLUETOOTHPAIRING_ACTIVITY = 2;

	/** Constant for request from case creation activity */
	public static final int FROM_CASE_CREATION_ACTIVITY = 3;

	/**
	 * Method onCreate.
	 *
	 * @param savedInstanceState
	 *            Bundle
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		callInfoApi = getIntent().getBooleanExtra(KEY_CALL_INFO_API, false);
		calledFromActivity = getIntent().getIntExtra(KEY_FROM_ACTIVITY, FROM_DEFAULT_ACTIVITY);

		if(callInfoApi){
			Logger.log(Level.ERROR, TAG, "Calling TPA info API");
			setContentView(R.layout.empty_layout);
			setCustomNoIconTitle(R.string.title_app_name);
			doTPAInfoProcess();
		}else{
			setViewToAcceptTPA();
		}

		mProgDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				if (null != mTPARequest) {
					mTPARequest.cancel(true);
				}
				if (null != mTPAInfoRequest) {
					mTPAInfoRequest.cancel(true);
				}
				if (null != mTPAAcceptRequest) {
					mTPAAcceptRequest.cancel(true);
				}
				if (null != mHttpUtil) {
					mHttpUtil.disconnect();
					mHttpUtil = null;
				}
			}
		});

	}

	private void setViewToAcceptTPA(){
		setContentView(R.layout.activity_tpa_acceptance);
		setCustomNoIconTitle(R.string.title_activity_tpa);

		mAgreeCheck = (CheckBox) findViewById(R.id.tpaCheck);
		mAgreeCheck.setEnabled(false);
		mAgreeCheck.setOnClickListener(this);

		mAgree = (Button) findViewById(R.id.btn_menu_positive);
		mAgree.setEnabled(false);
		mAgree.setText(R.string.agree);
		mAgree.setOnClickListener(this);

		final Button mDecline = (Button) findViewById(R.id.btn_menu_negative);
		mDecline.setText(R.string.decline);
		mDecline.setOnClickListener(this);

		final TextView mTPALinkTxtView = (TextView) findViewById(R.id.tpaLink);

		mTPALinkTxtView.setText(Html.fromHtml("<a href=''>"+ getString(R.string.tpa_link_text) +"</a>"));
		mTPALinkTxtView.setOnClickListener(this);
		mTPALinkTxtView.setOnTouchListener(this);
	}


	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		callInfoApi = getIntent().getExtras().getBoolean(KEY_CALL_INFO_API);
	}

	private void doTPAProcess() {
		if (null != tpaContentModel && null != tpaContentModel.getContent()) {
			startPopupActivity(tpaContentModel.getContent());
		}else {
			if (isNetworkConnected()) {
				mTPARequest = new DoTPARequest(TPAActivity.this);
				mTPARequest.execute();
			} else {
				showAlertDialog(R.drawable.ic_dialog_no_signal,
						get(R.string.network_connection),
						HttpUtil.getRespnseMsg(HttpUtil.HTTP_CONNECTION_DOWN),
						get(R.string.OK), null, false);
			}
		}
	}

	private void doTPAInfoProcess() {
		if (isNetworkConnected()) {
			mTPAInfoRequest = new DoTPAInfoRequest(TPAActivity.this);
			mTPAInfoRequest.execute();
		} else {
			mDialogType = Constants.SHOW_NETWORK_ERROR_DIALOG;
			showAlertDialog(R.drawable.ic_dialog_no_signal,
					get(R.string.network_connection),
					HttpUtil.getRespnseMsg(HttpUtil.HTTP_CONNECTION_DOWN),
					get(R.string.OK), null, false);
		}
	}

	private void doTPAAcceptProcess() {
		if (isNetworkConnected()) {
			mTPAAcceptRequest = new DoTPAAcceptRequest(TPAActivity.this);
			mTPAAcceptRequest.execute();
		} else {
			mDialogType = Constants.SHOW_NETWORK_ERROR_DIALOG;
			showAlertDialog(R.drawable.ic_dialog_no_signal,
					get(R.string.network_connection),
					HttpUtil.getRespnseMsg(HttpUtil.HTTP_CONNECTION_DOWN),
					get(R.string.OK), null, false);
		}
	}

	private void getCurrentTATInfoRequest(){
		if (isNetworkConnected()) {
			mCurrentTATInfoRequest = new GetCurrentTATInfoRequest(TPAActivity.this);
			mCurrentTATInfoRequest.execute();
		} else {
			mDialogType = Constants.SHOW_NETWORK_ERROR_DIALOG;
			showAlertDialog(R.drawable.ic_dialog_no_signal,
					get(R.string.network_connection),
					HttpUtil.getRespnseMsg(HttpUtil.HTTP_CONNECTION_DOWN),
					get(R.string.OK), null, false);
		}
	}

	private void startPopupActivity(String result) {
		final Intent intent = new Intent(this, TncnPrivacyPopup.class);
		intent.putExtra("tncnPrivacyResponse", result);
		intent.putExtra("popupTitle", getString(R.string.title_activity_tpa));
		startActivity(intent);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (v.getId() == R.id.tpaLink) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				((TextView) v).setTypeface(Typeface.DEFAULT_BOLD);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				((TextView) v).setTypeface(Typeface.DEFAULT);
			} else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
				((TextView) v).setTypeface(Typeface.DEFAULT);
			}
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.btn_menu_positive) {
			if (calledFromActivity == FROM_LOGIN_ACTIVITY) {
				setResult(RESULT_OK);
				finish();
			} else {
				doTPAAcceptProcess();
			}

		} else if (i == R.id.btn_menu_negative) {
			if (calledFromActivity == FROM_CASE_CREATION_ACTIVITY) {
				setResult(RESULT_CANCELED);
				finish();
			} else {
				showExitDialog();
			}

		} else if (i == R.id.tpaCheck) {
			mAgree.setEnabled(mAgreeCheck.isChecked());

		} else if (i == R.id.tpaLink) {
			doTPAProcess();

		} else {
		}
	}

	/**
	 * Method onKeyDown.
	 *
	 * @param keyCode
	 *            int
	 * @param event
	 *            KeyEvent
	 * @return boolean
	 * @see android.view.KeyEvent$Callback#onKeyDown(int, KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(calledFromActivity == FROM_CASE_CREATION_ACTIVITY){
				setResult(RESULT_CANCELED);
				finish();
			} else{
				showExitDialog();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onPositiveButtonClick() {
		super.onPositiveButtonClick();
		if (mDialogType == Constants.SHOW_EXIT_DIALOGE) {
			exitApp();
		} else if(mDialogType == Constants.SHOW_MEMBER_TPA_ACCEPTENCE_ERROR_DIALOG
				|| mDialogType == Constants.SHOW_NETWORK_ERROR_DIALOG){
			if(calledFromActivity == FROM_CASE_CREATION_ACTIVITY){
				setResult(RESULT_CANCELED);
				finish();
			} else{
				exitApp();
			}
		}
	}

	/**
	 * Stop the timer, send the shutdown message to device and Exits the
	 * application.
	 */
	private void exitApp() {
		mDialogType = Constants.SHOW_DIALOGE_NONE;
		if (isActivityRunning()) {
			mActModel.setRunning(false);
			mActModel.stopGetDataTimer();
		}
		isShutDownAck = true;
		mAppCrl.sendShutdownMsg();
		mHandler.sendEmptyMessageDelayed(Constants.SHUTDOWN_TIMEOUT,
				Constants.SHUTDOWN_TIMEOUT_DELAY);
		if (!mAppCrl.isDeviceConnected()) {
			Logger.log(Level.INFO, TAG, "mAppCrl.isDeviceConnected() = " + mAppCrl.isDeviceConnected());
			closeAllActivities();
		}
	}

	/**
	 * Show exit dialog.
	 */
	private void showExitDialog() {
		mDialogType = Constants.SHOW_EXIT_DIALOGE;
		showAlertDialog(get(R.string.exit_app),
				get(R.string.exit_confirmation), get(R.string.OK),
				get(R.string.cancel), false);
	}

	/**
	 * Method dataReceived.
	 *
	 * @param response
	 *            Response
	 * @see com.lppbpl.android.userapp.listener.SfBTDataListener#dataReceived(Response)
	 */
	@Override
	public void dataReceived(Response response) {
		super.dataReceived(response);
		if (response.getResponseType() == ResponseType.ACK && isShutDownAck) {
			Logger.log(Level.DEBUG, TAG, "shutdown Ack received");
			closeAllActivities();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.lppbpl.android.userapp.AppBaseActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeMessages(Constants.SHUTDOWN_TIMEOUT);
	}

	/** The m handler. */
	private final MyHandler mHandler = new MyHandler(this);

	/**
	 * The Class MyHandler.
	 */
	private static class MyHandler extends Handler {

		/** The m activity. */
		private final WeakReference<TPAActivity> mActivity;

		/**
		 * Constructor for MyHandler.
		 *
		 * @param activity
		 *            TnCnPrivacyActivity
		 */
		private MyHandler(TPAActivity activity) {
			mActivity = new WeakReference<TPAActivity>(activity);
		}

		/**
		 * Method handleMessage.
		 *
		 * @param msg
		 *            android.os.Message
		 */
		@Override
		public void handleMessage(android.os.Message msg) {
			final TPAActivity parent = mActivity.get();
			if (msg.what == Constants.SHUTDOWN_TIMEOUT) {
				Logger.log(Level.DEBUG, TAG, "SHUTDOWN_TIMEOUT. so app is closing");
				parent.closeAllActivities();

			} else {
			}
		}
	};

	/**
	 * AysncTask to get the content of the TPA agreement.
	 *
	 */
	private class DoTPARequest extends AsyncTask<Void, Void, String> {

		private TPAActivity parent;

		public DoTPARequest(TPAActivity parent){
			this.parent = parent;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog(getString(R.string.loading_text));
		}

		@Override
		protected String doInBackground(Void... params) {
			if (!isProgDialogShowing()) {
				return null;
			}
			mHttpUtil = new HttpUtil();
			final SortedMap<String, String> reqHeaders = mHttpUtil.getCommonRequestHeader();
			reqHeaders.put(HttpUtil.ACCEPT_TYPE, HttpUtil.APPLICATION_JSON);
			reqHeaders.put(HttpUtil.CONTENT_TYPE, HttpUtil.FORM_URLENCODED_TYPE);

			final String URL = mPinModel.getServerAddress() + Constants.GET_AGREEMENT;

			final String mStatus = mHttpUtil.getData(HttpUtil.GET, URL, reqHeaders);


			if (Logger.isDebugEnabled()) {
				Logger.log(Level.DEBUG, TAG, "TPA content request status: " + mStatus);
			}
			return mStatus;
		}

		@Override
		protected void onPostExecute(String result) {
			if (!isProgDialogShowing()) {
				return;
			}
			dismissProgressDislog();
			if (mAppModel.getResponseCode() == HttpUtil.HTTP_RESPONSE_SUCCESS && null != result) {
				parseJsonResponse(result);
			} else {
				if (null != result) {
					showAlertDialog(R.drawable.ic_dialog_info,
							parent.get(R.string.information), result,
							parent.get(R.string.OK), null, false);
				} else {
					showAlertDialog(R.drawable.ic_dialog_info,
							parent.get(R.string.network_error),
							parent.get(R.string.http_general_error),
							parent.get(R.string.OK), null, false);
				}
			}
		}

		private void parseJsonResponse(String result){
			try {
				final JSONObject object = new JSONObject(result);
				tpaContentModel = new TPAContentModel();
				if (object != null) {
					if (object.has("version")) {
						tpaContentModel.setVersion(object.getString("version"));
					}
					if (object.has("content")) {
						if(object.getString("content") != null){
							tpaContentModel.setContent(URLDecoder.decode(object.getString("content"), "UTF-8"));
						}
					}
					if (object.has("content_path")) {
						tpaContentModel.setContentPath(object.getString("content_path"));
					}
					if (object.has("tatCategoryList")){
						ArrayList<TATCategoryModel> tatCategoryList = new ArrayList<TATCategoryModel>();
						TATCategoryModel tempObject = new TATCategoryModel();
						JSONArray categoryList = object.getJSONArray("tatCategoryList");
						for(int j=0;j<categoryList.length();j++){
							JSONObject data = categoryList.getJSONObject(j);
							if (data != null) {
								if (data.has("id")) {
									tempObject.setId(data.getInt("id"));
								}
								if (data.has("name")) {
									tempObject.setName(data.getString("name"));
								}
								if (data.has("turnAroundTime")) {
									tempObject.setTurnAroundTime(data.getInt("turnAroundTime"));
								}
								if (data.has("secondTurnAroundTime")) {
									tempObject.setSecondTurnAroundTime(data.getInt("secondTurnAroundTime"));
								}
								if (data.has("thirdTurnAroundTime")) {
									tempObject.setThirdTurnAroundTime(data.getInt("thirdTurnAroundTime"));
								}
								if (data.has("amount")) {
									tempObject.setAmount(data.getInt("amount"));
								}
								if (data.has("data_Subscription_Charge")) {
									tempObject.setDataSubscriptionCharge(data.getInt("data_Subscription_Charge"));
								}
								if (data.has("gp_Subscription_Charge")) {
									tempObject.setGpSubscriptionCharge(data.getInt("gp_Subscription_Charge"));
								}
								tatCategoryList.add(tempObject);
							}
			            }
						tpaContentModel.setCategoryList(tatCategoryList);
					}

					startPopupActivity(tpaContentModel.getContent());
					mAgreeCheck.setEnabled(true);
				}
			} catch (JSONException e) {
				Logger.log(Level.DEBUG, TAG, "" + e);
				showAlertDialog(R.drawable.ic_dialog_info,
						parent.get(R.string.network_error),
						parent.get(R.string.http_general_error),
						parent.get(R.string.OK), null, false);
			} catch (UnsupportedEncodingException uec) {
				Logger.log(Level.DEBUG, TAG, "" + uec);
			}
		}

	}

	/**
	 * AysncTask to get the current and accepted versions of the TPA agreement.
	 *
	 */
	private class DoTPAInfoRequest extends AsyncTask<Void, Void, String> {

		private TPAActivity parent;

		public DoTPAInfoRequest(TPAActivity parent){
			this.parent = parent;
		}
		/*
		 * (non-Javadoc)
		 *
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog(getString(R.string.loading_text));
		}

		@Override
		protected String doInBackground(Void... params) {
			if (!isProgDialogShowing()) {
				return null;
			}
			mHttpUtil = new HttpUtil();
			final SortedMap<String, String> reqHeaders = mHttpUtil.getCommonRequestHeader();
			reqHeaders.put(HttpUtil.ACCEPT_TYPE, HttpUtil.APPLICATION_JSON);
			reqHeaders.put(HttpUtil.CONTENT_TYPE, HttpUtil.FORM_URLENCODED_TYPE);

			final String URL = mPinModel.getServerAddress() + Constants.GET_AGREEMENT_INFO;

			final String mStatus = mHttpUtil.getData(HttpUtil.GET, URL, reqHeaders);

			if (Logger.isDebugEnabled()) {
				Logger.log(Level.DEBUG, TAG, "TPA Info request status: " + mStatus);
			}
			return mStatus;
		}

		@Override
		protected void onPostExecute(String result) {
			if (!isProgDialogShowing()) {
				return;
			}
			dismissProgressDislog();
			if (parent.isAccessTokenExpired()) {
				if(calledFromActivity == FROM_BLUETOOTHPAIRING_ACTIVITY){
					Intent intent = new Intent(TPAActivity.this, MainMenuActivity.class);
					startActivity(intent);
					finish();
				}
			} else {
				if (mAppModel.getResponseCode() == HttpUtil.HTTP_RESPONSE_SUCCESS && null != result) {
					parseJsonResponse(result);
				} else {
					if (null != result) {
						mDialogType = Constants.SHOW_NETWORK_ERROR_DIALOG;
						showAlertDialog(R.drawable.ic_dialog_info,
								parent.get(R.string.information), result,
								parent.get(R.string.OK), null, false);
					} else {
						mDialogType = Constants.SHOW_NETWORK_ERROR_DIALOG;
						showAlertDialog(R.drawable.ic_dialog_info,
								parent.get(R.string.network_error),
								parent.get(R.string.http_general_error),
								parent.get(R.string.OK), null, false);
					}
				}
			}
		}

		private void parseJsonResponse(String result){
			try {
				final JSONObject object = new JSONObject(result);
				tpaInfoModel = new TPAInfoModel();
				if (object != null) {
					if (object.has("current_version")) {
						tpaInfoModel.setCurrentVersion(object.getString("current_version"));
					}
					if (object.has("accepted_version")) {
						tpaInfoModel.setAcceptedVersion(object.getString("accepted_version"));
					}
					if (object.has("provideAdvisory")) {
						tpaInfoModel.setProvideAdvisory(object.getInt("provideAdvisory"));
					}
					moveToNextScreen();
				}
			} catch (JSONException e) {
				Logger.log(Level.DEBUG, TAG, "" + e);
				mDialogType = Constants.SHOW_NETWORK_ERROR_DIALOG;
				showAlertDialog(R.drawable.ic_dialog_info,
						parent.get(R.string.network_error),
						parent.get(R.string.http_general_error),
						parent.get(R.string.OK), null, false);
			}
		}
	}

	/**
	 * Displays the respective screen after TPA check is done.
	 */
	private void moveToNextScreen(){
		if(tpaInfoModel.getCurrentVersion().equals(tpaInfoModel.getAcceptedVersion())){
			if(calledFromActivity == FROM_BLUETOOTHPAIRING_ACTIVITY){
				Logger.log(Level.DEBUG, TAG, "No TPA changes. start Activity for MainMenu");
				Intent intent = new Intent(TPAActivity.this, MainMenuActivity.class);
				startActivity(intent);
				finish();
			}
			if(calledFromActivity == FROM_CASE_CREATION_ACTIVITY){
				Logger.log(Level.DEBUG, TAG, "No TPA changes. Go back to case creation screen");
				getCurrentTATInfoRequest();
			}
		}else{
			if(mAppModel.isPrimaryUser()){
				setViewToAcceptTPA();
			}else{
				mDialogType = Constants.SHOW_MEMBER_TPA_ACCEPTENCE_ERROR_DIALOG;
				showAlertDialog(R.drawable.ic_dialog_info,
						get(R.string.information), get(R.string.tpa_member_acceptance_error),
						get(R.string.OK), null, false);
			}
		}
	}

	/**
	 * AysncTask to get the current and accepted versions of the TPA agreement.
	 *
	 */
	private class GetCurrentTATInfoRequest extends AsyncTask<Void, Void, String> {

		private TPAActivity parent;

		public GetCurrentTATInfoRequest(TPAActivity parent){
			this.parent = parent;
		}
		/*
		 * (non-Javadoc)
		 *
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog(getString(R.string.loading_text));
		}

		@Override
		protected String doInBackground(Void... params) {
			if (!isProgDialogShowing()) {
				return null;
			}
			mHttpUtil = new HttpUtil();
			final SortedMap<String, String> reqHeaders = mHttpUtil.getCommonRequestHeader();
			reqHeaders.put(HttpUtil.ACCEPT_TYPE, HttpUtil.APPLICATION_JSON);
			reqHeaders.put(HttpUtil.CONTENT_TYPE, HttpUtil.FORM_URLENCODED_TYPE);

			final String URL = mPinModel.getServerAddress() + Constants.GET_TPA_TAT_CURRENT_VALUES;

			final String mStatus = mHttpUtil.getData(HttpUtil.GET, URL, reqHeaders);

			if (Logger.isDebugEnabled()) {
				Logger.log(Level.DEBUG, TAG, "Get current TAT request status: " + mStatus);
			}
			return mStatus;
		}

		@Override
		protected void onPostExecute(String result) {
			if (!isProgDialogShowing()) {
				return;
			}
			dismissProgressDislog();
			if (mAppModel.getResponseCode() == HttpUtil.HTTP_RESPONSE_SUCCESS && null != result) {
				parseJsonResponse(result);
			} else {
				if (null != result) {
					mDialogType = Constants.SHOW_NETWORK_ERROR_DIALOG;
					showAlertDialog(R.drawable.ic_dialog_info,
							parent.get(R.string.information), result,
							parent.get(R.string.OK), null, false);
				} else {
					mDialogType = Constants.SHOW_NETWORK_ERROR_DIALOG;
					showAlertDialog(R.drawable.ic_dialog_info,
							parent.get(R.string.network_error),
							parent.get(R.string.http_general_error),
							parent.get(R.string.OK), null, false);
				}
			}
		}

		private void parseJsonResponse(String result){
			try {
				final JSONObject object = new JSONObject(result);

				if (object != null) {
					if (object.has("tatCategoryList")){
						ArrayList<TATCategoryModel> tatCategoryList = new ArrayList<TATCategoryModel>();
						TATCategoryModel tempObject = new TATCategoryModel();
						JSONArray categoryList = object.getJSONArray("tatCategoryList");
						for(int j=0;j<categoryList.length();j++){
							JSONObject data = categoryList.getJSONObject(j);
							if (data != null) {
								if (data.has("id")) {
									tempObject.setId(data.getInt("id"));
								}
								if (data.has("name")) {
									tempObject.setName(data.getString("name"));
								}
								if (data.has("turnAroundTime")) {
									tempObject.setTurnAroundTime(data.getInt("turnAroundTime"));
								}
								if (data.has("secondTurnAroundTime")) {
									tempObject.setSecondTurnAroundTime(data.getInt("secondTurnAroundTime"));
								}
								if (data.has("thirdTurnAroundTime")) {
									tempObject.setThirdTurnAroundTime(data.getInt("thirdTurnAroundTime"));
								}
								if (data.has("amount")) {
									tempObject.setAmount(data.getInt("amount"));
								}
								if (data.has("data_Subscription_Charge")) {
									tempObject.setDataSubscriptionCharge(data.getInt("data_Subscription_Charge"));
								}
								if (data.has("gp_Subscription_Charge")) {
									tempObject.setGpSubscriptionCharge(data.getInt("gp_Subscription_Charge"));
								}
								tatCategoryList.add(tempObject);
							}
			            }
						tpaTatInfoList = tatCategoryList;
					}

					returnDetailsForCaseCreation();
				}
			} catch (JSONException e) {
				Logger.log(Level.DEBUG, TAG, "" + e);
				mDialogType = Constants.SHOW_NETWORK_ERROR_DIALOG;
				showAlertDialog(R.drawable.ic_dialog_info,
						parent.get(R.string.network_error),
						parent.get(R.string.http_general_error),
						parent.get(R.string.OK), null, false);
			}
		}
	}

	/**
	 * AysncTask to update the accepted TPA to server.
	 *
	 */
	private class DoTPAAcceptRequest extends AsyncTask<Void, Void, String> {

		private TPAActivity parent;

		public DoTPAAcceptRequest(TPAActivity parent){
			this.parent = parent;
		}
		/*
		 * (non-Javadoc)
		 *
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog(getString(R.string.please_wait_text));
			mHttpUtil = new HttpUtil();
		}

		@Override
		protected String doInBackground(Void... params) {
			final SortedMap<String, String> reqHeaders = mHttpUtil.getCommonRequestHeader();
			reqHeaders.put(HttpUtil.ACCEPT_TYPE, HttpUtil.APPLICATION_JSON);
			reqHeaders.put(HttpUtil.CONTENT_TYPE, HttpUtil.APPLICATION_JSON);

			final String URL = mPinModel.getServerAddress()	+ Constants.ACCEPT_TPA_AGREEMENT;

			final String status = mHttpUtil.getData(HttpUtil.GET, URL, reqHeaders);

			if (Logger.isDebugEnabled()) {
				Logger.log(Level.DEBUG, TAG, "TPA Accept request status: " + status);
			}
			return status;
		}

		/**
		 * Method onPostExecute.
		 *
		 * @param status
		 *            String
		 */
		@Override
		protected void onPostExecute(String status) {
			super.onPostExecute(status);
			dismissProgressDislog();
			if (mAppModel.getResponseCode() == HttpUtil.HTTP_RESPONSE_SUCCESS && null != status) {
				Logger.log(Level.DEBUG, TAG, "TPA Accepted successfully");
				if(calledFromActivity == FROM_CASE_CREATION_ACTIVITY){
					getCurrentTATInfoRequest();
				} else if(calledFromActivity == FROM_BLUETOOTHPAIRING_ACTIVITY){
					Intent intent = new Intent(TPAActivity.this, MainMenuActivity.class);
					startActivity(intent);
					finish();
				} else {
					setResult(RESULT_OK);
					finish();
				}
			} else{
				if (null != status) {
					mDialogType = Constants.SHOW_NETWORK_ERROR_DIALOG;
					showAlertDialog(R.drawable.ic_dialog_info,
							parent.get(R.string.information), status,
							parent.get(R.string.OK), null, false);
				} else {
					mDialogType = Constants.SHOW_NETWORK_ERROR_DIALOG;
					showAlertDialog(R.drawable.ic_dialog_info,
							parent.get(R.string.network_error),
							parent.get(R.string.http_general_error),
							parent.get(R.string.OK), null, false);
				}
			}
		}
	}

	/**
	 * Method that returns the details back activity requesting case creation.
	 */
	private void returnDetailsForCaseCreation(){
		int fee = tpaTatInfoList.get(0).getAmount();
		int sla = tpaTatInfoList.get(0).getSecondTurnAroundTime();
		int callCenterSLA = tpaTatInfoList.get(0).getThirdTurnAroundTime() - tpaTatInfoList.get(0).getSecondTurnAroundTime();
		Intent intent = new Intent();
		intent.putExtra("provide_advisory", tpaInfoModel.canProvideAdvisory());
		intent.putExtra("consultation_fee", fee);
		intent.putExtra("sla_time", sla);
		if(callCenterSLA > 0){
			intent.putExtra("call_center_sla_time", callCenterSLA);
		} else{
			intent.putExtra("call_center_sla_time", 0);
		}
		setResult(RESULT_OK, intent);
		finish();
	}
}
