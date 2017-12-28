package com.lppbpl.android.userapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Resources.NotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.android.userapp.R;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.util.HttpUtil;

public class WalletTopupActivity extends AppBaseActivity {
	/** The Constant TAG. */
	private static final String TAG = WalletTopupActivity.class.getName();

	private int mCaseId = -1;
	private int mOrderId;
	private String mRedirectLocation;
	private boolean mRedirect = false;

	protected Spinner spinnerTopUpProducts;
	protected Spinner spinnerPaymentModes;
	protected TextView currentBalance;
	protected EditText quatityValue;
	protected TextView unitPriceValue;
	protected TextView totalAmountValue;

	/** The selected payment mode index. */
	protected int mSelectedPaymentModeIndex = 1;

	/** The selected topup product index. */
	protected int mSelectedTopupIndex = 0;

	private String currentBalanceResponse = "0";
	protected String paymentMethod = null;
	protected String productCode = null;
	protected int quantity = 0;

	private ArrayList<TopupProduct> topupProducts;
	private ArrayList<PaymentMode> paymentModes;

	/** The http util. */
	private HttpUtil mHttpUtil;

	/** Identifier to initiate request for wallet balance refresh when coming back from payment gateway after payment.
	 * Value true means to initiate request. */
	private boolean backFromPG = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Initially setting this to false to restrict refresh balance request
		backFromPG = false;
		if (isNetworkConnected()) {
			if (mPinModel.isLoginForSessionSuccess()) {
				// If login session is active fetch top-up products request
				new FetchTopUpProducts(WalletTopupActivity.this).execute();
			} else {
				final Intent intent = new Intent(this, LoginActivity.class);
				startActivityForResult(intent, 1);
			}
		} else {
			mDialogType = Constants.SHOW_WALLET_TOPUP_INITIALISE_FAIL_DIALOG;
			showAlertDialog(R.drawable.ic_dialog_no_signal,
					get(R.string.network_connection),
					HttpUtil.getRespnseMsg(HttpUtil.HTTP_CONNECTION_DOWN),
					get(R.string.OK), null, false);
		}

		mProgDialog.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface arg0) {
				if (null != mHttpUtil) {
					mHttpUtil.disconnect();
					mHttpUtil = null;
				}
				setResult(RESULT_CANCELED);
				finish();
			}
		});
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		// If this cnodition is true it means we are coming back after doing a payment. So refresh the wallet balance.
		if(backFromPG){
			// Set it back to false as we are now back from payment gateway.
			backFromPG = false;
			new FetchCurrentBalance(WalletTopupActivity.this, true).execute();
		}
	}

	/**
	 * Method onActivityResult.
	 *
	 * @param requestCode
	 *            int
	 * @param resultCode
	 *            int
	 * @param data
	 *            Intent
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (1 == requestCode && resultCode == RESULT_OK) {
			new FetchTopUpProducts(WalletTopupActivity.this).execute();
		} else if (requestCode == Constants.SHOW_UPDATE_ADDRESS_DIALOG && resultCode == RESULT_OK) {
			new DoWalletTopUp(WalletTopupActivity.this).execute();
		} else if (requestCode == Constants.SHOW_UPDATE_ADDRESS_DIALOG && resultCode == RESULT_CANCELED) {
			// Don't remove this empty block.
		} else{
			finishActivity();
		}
	}

	/**
	 * close the Activity.
	 */
	public void finishActivity() {
		setResult(RESULT_OK);
		finish();
	}

	/**
	 * Get the String in the resource by passing resourceId.
	 *
	 * @param stringId
	 *            the string id
	 * @return String
	 */
	protected String get(int stringId) {
		try {
			return getResources().getString(stringId);
		} catch (NotFoundException nfe) {
			Logger.log(Level.ERROR, TAG, "Error occured : " + nfe.getMessage());
			return "String Not Loaded";
		}
	}

	@Override
	protected void onPositiveButtonClick() {
		super.onPositiveButtonClick();
		if (mDialogType == Constants.SHOW_UPDATE_ADDRESS_DIALOG) {
			Intent intent = new Intent(this, UpdateAddressActivity.class);
			startActivityForResult(intent, Constants.SHOW_UPDATE_ADDRESS_DIALOG);
		} else if(mDialogType == Constants.SHOW_WALLET_TOPUP_INITIALISE_FAIL_DIALOG){
			finishActivity();
		}
	}

	@Override
	protected void onNegativeButtonClick() {
		super.onNegativeButtonClick();

	}

	/**
	 * Initializing and populating this activity view.
	 */
	private void initializeActivityView(){
		setContentView(R.layout.activity_wallet_topup);
		setCustomNoIconTitle(R.string.title_activity_wallet_topup);

		currentBalance = (TextView) findViewById(R.id.balance_value);
		unitPriceValue = (TextView) findViewById(R.id.unit_price_value);
		totalAmountValue = (TextView) findViewById(R.id.total_amount_value);
		spinnerTopUpProducts = (Spinner) findViewById(R.id.topup_products);
		spinnerPaymentModes = (Spinner) findViewById(R.id.payment_mode_spinner);
		quatityValue = (EditText) findViewById(R.id.quantity_value);

		quantity = Integer.parseInt(quatityValue.getText().toString());
		quatityValue.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.length() > 0){
					// If quantity > 0, calculate the total amount w.r.t the unit price value of top-up product selected.
					quantity = Integer.parseInt(quatityValue.getText().toString());
					int amt = Integer.parseInt(s.toString()) * Integer.parseInt(unitPriceValue.getText().toString());
					//Set the new total amount value
					totalAmountValue.setText(String.valueOf(amt));
				} else{
					// If quantity < 0 or empty then set total amount to zero
					totalAmountValue.setText("0");
					quantity = 0;
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		currentBalance.setText(currentBalanceResponse);
		initTopupProductSpinner();
		initPaymentModeSpinner();

		final Button buyNow = (Button) findViewById(R.id.btn_menu_positive);
		buyNow.setText(R.string.buy_now_button);
		buyNow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (quantity > 0) {
					// Initiate request for wallet top-up
					new DoWalletTopUp(WalletTopupActivity.this).execute();
				}else{
					quatityValue.setError(getString(R.string.invalid_quantity));
				}
			}
		});

		final Button back = (Button) findViewById(R.id.btn_menu_negative);
		back.setText(R.string.back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finishActivity();
			}
		});

	}

	/**
	 * Initializing top-up products option list
	 */
	private void initTopupProductSpinner(){
		final String[] menu = new String[topupProducts.size()];
		Iterator<TopupProduct> iterator = topupProducts.iterator();
		int i = 0;
		String temp;
		while (iterator.hasNext()) {
			temp = iterator.next().getName();
			if (!temp.isEmpty()) {
				menu[i++] = temp;
			}
		}
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_item, menu);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerTopUpProducts.setAdapter(dataAdapter);
		spinnerTopUpProducts.setOnItemSelectedListener(new TopupProductOnItemSelectedListener());
	}

	/**
	 * Initializing payment modes option list
	 */
	private void initPaymentModeSpinner(){
		final String[] menu = new String[paymentModes.size()];
		Iterator<PaymentMode> iterator = paymentModes.iterator();
		int i = 0;
		String temp;
		while (iterator.hasNext()) {
			temp = iterator.next().getDesc();
			if (!temp.isEmpty()) {
				menu[i++] = temp;
			}
		}
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_item, menu);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerPaymentModes.setAdapter(dataAdapter);
		spinnerPaymentModes.setOnItemSelectedListener(new PaymentModeOnItemSelectedListener());
	}

	/**
	 * Redirect to payment gateway by calling BillPaymentActivity
	 */
	private void redirectToPaymentGateway(){
		if (mRedirect) {
			final Intent intent = new Intent(WalletTopupActivity.this,BillPaymentActivity.class);
			intent.putExtra("caseid", mCaseId);
			intent.putExtra("orderid", mOrderId);
			intent.putExtra("redirectLocation",	mRedirectLocation);
			intent.putExtra("activityTitle", R.string.title_activity_wallet_topup);
			startActivity(intent);
			backFromPG = true;
		}
	}

	/**
	 * Class to fetch the list of payment modes available for wallet top-up
	 *
	 */
	private class FetchTopUpPaymentMode extends AsyncTask<Void, Void, String>{

		/** The parent. */
		private WalletTopupActivity parent;

		/**
		 * Constructor for FetchTopUpPaymentMode.
		 *
		 * @param parent
		 *            WalletTopupActivity
		 */
		protected FetchTopUpPaymentMode(WalletTopupActivity parent) {
			this.parent = parent;
			mHttpUtil = new HttpUtil();
		}

		@Override
		protected String doInBackground(Void... params) {
			final SortedMap<String, String> reqHeaders = mHttpUtil.getCommonRequestHeader();
			reqHeaders.put(HttpUtil.CONTENT_TYPE, HttpUtil.FORM_URLENCODED_TYPE);
			reqHeaders.put(HttpUtil.ACCEPT_TYPE, HttpUtil.APPLICATION_JSON);

			final HashMap<String, String> reqParams = new HashMap<String, String>();
			reqParams.put("productType", "WALLET_TOPUP");

			final String URL = mPinModel.getServerAddress() + Constants.GET_PAYMENT_MODE;

			final String ret = mHttpUtil.postDataThroughParams(HttpUtil.POST, URL, reqParams, reqHeaders);

			if (Logger.isDebugEnabled()) {
				Logger.log(Level.DEBUG, TAG, "status: " + ret);
			}

			return ret;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (mAppModel.getResponseCode() == HttpUtil.HTTP_RESPONSE_SUCCESS && result != null) {
				parseJsonArray(result);
			} else {
				dismissProgressDislog();
				parent.mDialogType = Constants.SHOW_WALLET_TOPUP_INITIALISE_FAIL_DIALOG;
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

		private void parseJsonArray(final String message) {
			try {
				JSONArray array = new JSONArray(message);
				paymentModes = new ArrayList<PaymentMode>();
				if (array != null) {
					for (int i = 0; i < array.length(); i++) {
						JSONObject object = array.getJSONObject(i);
						PaymentMode mode = new PaymentMode();
						if (object.has("id")) {
							mode.setId(object.getInt("id"));
						}
						if (object.has("desc")) {
							mode.setDesc(object.getString("desc"));
						}
						if (object.has("name")) {
							mode.setName(object.getString("name"));
						}
						if (object.has("partnerId")) {
							mode.setPartnerId(object.getInt("partnerId"));
						}
						paymentModes.add(mode);
					}
					new FetchCurrentBalance(WalletTopupActivity.this, false).execute();

				}
			} catch (JSONException e) {
				Logger.log(Level.ERROR, TAG, "JSON error" + e);
				dismissProgressDislog();
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			if (null != mHttpUtil) {
				mHttpUtil.disconnect();
			}
		}

		@SuppressLint("NewApi") @Override
		protected void onCancelled(String result) {
			super.onCancelled(result);
			if (null != mHttpUtil) {
				mHttpUtil.disconnect();
			}
		}
	}

	/**
	 * Class to fetch the available top-up products
	 *
	 */
	private class FetchTopUpProducts extends AsyncTask<Void, Void, String>{

		/** The parent. */
		private WalletTopupActivity parent;

		/**
		 * Constructor for FetchTopUpProducts.
		 *
		 * @param parent
		 *            WalletTopupActivity
		 */
		protected FetchTopUpProducts(WalletTopupActivity parent) {
			this.parent = parent;
			mHttpUtil = new HttpUtil();
		}

		@Override
		protected String doInBackground(Void... params) {
			final SortedMap<String, String> reqHeaders = mHttpUtil.getCommonRequestHeader();
			reqHeaders.put(HttpUtil.ACCEPT_TYPE, HttpUtil.APPLICATION_JSON);
			final String URL = mPinModel.getServerAddress()	+ Constants.GET_TOPUP_PRODUCTS;
			final String ret = mHttpUtil.getData(HttpUtil.GET, URL, reqHeaders);

			if (Logger.isDebugEnabled()) {
				Logger.log(Level.DEBUG, TAG, "status: " + ret);
			}

			return ret;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (mAppModel.getResponseCode() == HttpUtil.HTTP_RESPONSE_SUCCESS
					&& result != null) {
				parseJsonArray(result);
			} else {
				dismissProgressDislog();
				parent.mDialogType = Constants.SHOW_WALLET_TOPUP_INITIALISE_FAIL_DIALOG;
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

		private void parseJsonArray(final String message) {
			try {
				JSONArray array = new JSONArray(message);
				topupProducts = new ArrayList<TopupProduct>();
				if (array != null) {
					for (int i = 0; i < array.length(); i++) {
						JSONObject object = array.getJSONObject(i);
						TopupProduct product = new TopupProduct();
						if (object.has("id")) {
							product.setId(object.getInt("id"));
						}
						if (object.has("desc")) {
							product.setDesc(object.getString("desc"));
						}
						if (object.has("name")) {
							product.setName(object.getString("name"));
						}
						if (object.has("cost")) {
							product.setCost(object.getInt("cost"));
						}
						if (object.has("partnerId")) {
							product.setPartnerId(object.getInt("partnerId"));
						}
						if (object.has("productCode")) {
							product.setProductCode(object.getString("productCode"));
						}
						if (object.has("productType")) {
							product.setProductType(object.getString("productType"));
						}
						if (object.has("inUse")) {
							product.setInUse(object.getInt("inUse"));
						}
						topupProducts.add(product);
					}
					new FetchTopUpPaymentMode(WalletTopupActivity.this).execute();
				}
			} catch (JSONException e) {
				Logger.log(Level.ERROR, TAG, "JSON error" + e);
				dismissProgressDislog();
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			if (null != mHttpUtil) {
				mHttpUtil.disconnect();
			}
		}

		@SuppressLint("NewApi") @Override
		protected void onCancelled(String result) {
			super.onCancelled(result);
			if (null != mHttpUtil) {
				mHttpUtil.disconnect();
			}
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
		}

	}

	/**
	 * Class to request for wallet top-up.
	 *
	 */
	private class DoWalletTopUp extends AsyncTask<Void, Void, String>{

		/** The parent. */
		private WalletTopupActivity parent;

		protected DoWalletTopUp (WalletTopupActivity parent) {
			this.parent = parent;
			mHttpUtil = new HttpUtil();
		}

		@Override
		protected String doInBackground(Void... params) {
			final SortedMap<String, String> reqHeaders = mHttpUtil.getCommonRequestHeader();
			reqHeaders.put(HttpUtil.CONTENT_TYPE, HttpUtil.FORM_URLENCODED_TYPE);
			reqHeaders.put(HttpUtil.ACCEPT_TYPE, HttpUtil.APPLICATION_JSON);

			final HashMap<String, String> reqParams = new HashMap<String, String>();
			reqParams.put("paymentMethod", paymentMethod);
			reqParams.put("productCode", productCode);
			reqParams.put("quantity", ""+quantity);

			final String URL = mPinModel.getServerAddress() + Constants.DO_TOPUP;

			final String ret = mHttpUtil.postDataThroughParams(HttpUtil.POST, URL, reqParams, reqHeaders);

			if (Logger.isDebugEnabled()) {
				Logger.log(Level.DEBUG, TAG, "status: " + ret);
			}

			return ret;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dismissProgressDislog();
			if (mAppModel.getResponseCode() == HttpUtil.HTTP_RESPONSE_SUCCESS
					&& result != null) {
				try {
					final JSONObject object = new JSONObject(result);
					mCaseId = object.getInt("caseId");
					mOrderId = object.getInt("orderId");
					mRedirectLocation = object.getString("redirectLocation");
					mRedirect = object.getBoolean("redirect");
				} catch (JSONException e) {
					Logger.log(Level.ERROR, TAG, "" + e);
				}
				redirectToPaymentGateway();
			} else {
				if (null != result) {
					// If the user address and email-id is not registered, ask user to update it in order to do wallet top-up
					if((result.contains("ERR009-ADDRESS_MISSING") || result.contains("ERR014-EMAIL_ID_MISSING"))){
						parent.mDialogType = Constants.SHOW_UPDATE_ADDRESS_DIALOG;
						final String[] split = result.split(":");
						String message = null;
						if (split != null && split.length > 1) {
							message = split[1];
						}
						parent.showAlertDialog(
								parent.get(R.string.title_activity_wallet_topup),
								null == message || message.trim().length() < 1 ? parent.get(R.string.request_placing_failed)
										: message,
								parent.mDialogType == Constants.SHOW_UPDATE_ADDRESS_DIALOG ? parent.get(R.string.update)
										: parent.get(R.string.retry), parent.get(R.string.cancel), false);

					}else{
						parent.mDialogType = Constants.SHOW_WALLET_TOPUP_FAIL_DIALOG;
						showAlertDialog(R.drawable.ic_dialog_info,
								parent.get(R.string.information), result,
								parent.get(R.string.OK), null, false);
					}
				} else {
					parent.mDialogType = Constants.SHOW_WALLET_TOPUP_FAIL_DIALOG;
					showAlertDialog(R.drawable.ic_dialog_info,
							parent.get(R.string.network_error),
							parent.get(R.string.http_general_error),
							parent.get(R.string.OK), null, false);
				}
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog(getString(R.string.please_wait_text));
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			if (null != mHttpUtil) {
				mHttpUtil.disconnect();
			}
		}

		@SuppressLint("NewApi") @Override
		protected void onCancelled(String result) {
			super.onCancelled(result);
			if (null != mHttpUtil) {
				mHttpUtil.disconnect();
			}
		}

	}

	/**
	 * Class to fetch the current balance in wallet.
	 *
	 */
	private class FetchCurrentBalance extends AsyncTask<Void, Void, String>{

		/** The parent. */
		private WalletTopupActivity parent;
		private boolean refreshBalanceOnly = false;

		/**
		 * Constructor for FetchCurrentBalance
		 * @param parent WalletTopupActivity
		 * @param refreshBalanceOnly for only updating wallet balance on activity screen. If true only current balance will be updated on screen.
		 */
		protected FetchCurrentBalance (WalletTopupActivity parent, boolean refreshBalanceOnly) {
			this.parent = parent;
			this.refreshBalanceOnly = refreshBalanceOnly;
			mHttpUtil = new HttpUtil();
		}

		@Override
		protected String doInBackground(Void... params) {
			final SortedMap<String, String> reqHeaders = mHttpUtil.getCommonRequestHeader();
			reqHeaders.put(HttpUtil.ACCEPT_TYPE, HttpUtil.APPLICATION_JSON);

			final String URL = mPinModel.getServerAddress() + Constants.GET_CURRENT_WALLET_BALANCE;
			final String ret = mHttpUtil.getData(HttpUtil.GET, URL, reqHeaders);

			if (Logger.isDebugEnabled()) {
				Logger.log(Level.DEBUG, TAG, "status: " + ret);
			}

			return ret;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dismissProgressDislog();
			if (mAppModel.getResponseCode() == HttpUtil.HTTP_RESPONSE_SUCCESS
					&& result != null) {
				currentBalanceResponse = result;
				if(refreshBalanceOnly){
					if(currentBalance != null){
						currentBalance.setText(currentBalanceResponse);
					}
				}else{
					initializeActivityView();
				}
			} else {
				parent.mDialogType = Constants.SHOW_WALLET_TOPUP_INITIALISE_FAIL_DIALOG;
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

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(refreshBalanceOnly){
				showProgressDialog(getString(R.string.please_wait_text));
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			if (null != mHttpUtil) {
				mHttpUtil.disconnect();
			}
		}

		@SuppressLint("NewApi") @Override
		protected void onCancelled(String result) {
			super.onCancelled(result);
			if (null != mHttpUtil) {
				mHttpUtil.disconnect();
			}
		}

	}

	/**
	 * Model class for storing the available payment modes
	 *
	 */
	public class PaymentMode {
		private int id;
		private String desc;
		private String name;
		private int partnerId;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getPartnerId() {
			return partnerId;
		}

		public void setPartnerId(int partnerId) {
			this.partnerId = partnerId;
		}
	}

	/**
	 * Model class for storing the available wallet top-up products
	 *
	 */
	public class TopupProduct{
		private int id;
		private String desc;
		private String name;
		private int cost;
		private int partnerId;
		private String productCode;
		private String productType;
		private int inUse;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getDesc(){
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public String getName(){
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getCost() {
			return cost;
		}

		public void setCost(int cost) {
			this.cost = cost;
		}

		public int getPartnerId() {
			return partnerId;
		}

		public void setPartnerId(int partnerId) {
			this.partnerId = partnerId;
		}

		public String getProductCode(){
			return productCode;
		}

		public void setProductCode(String productCode) {
			this.productCode = productCode;
		}

		public String getProductType(){
			return productType;
		}

		public void setProductType(String productType) {
			this.productType = productType;
		}

		public int getInUse() {
			return inUse;
		}

		public void setInUse(int inUse) {
			this.inUse = inUse;
		}
	}

	/**
	 * Listener for item selected from top-up products list
	 */
	public class TopupProductOnItemSelectedListener implements OnItemSelectedListener {

	    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	    	// Based upon the unit price of the selected product, update the total amount w.r.t quantity value
	    	Logger.log(Level.DEBUG, TAG, "Selected topup product: " + parent.getItemAtPosition(pos).toString());
	    	mSelectedTopupIndex = pos;
	    	productCode = topupProducts.get(mSelectedTopupIndex).getProductCode();
	    	unitPriceValue.setText(String.valueOf(topupProducts.get(mSelectedTopupIndex).getCost()));
	    	int amt = Integer.parseInt(quatityValue.getText().toString()) * Integer.parseInt(unitPriceValue.getText().toString());
			totalAmountValue.setText(String.valueOf(amt));
	    }

	    @Override
	    public void onNothingSelected(AdapterView<?> arg0) {
	    }

	}

	/**
	 * Listener for selected payment mode.
	 */
	public class PaymentModeOnItemSelectedListener implements OnItemSelectedListener {

	    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	    	Logger.log(Level.DEBUG, TAG, "Selected payment mode: " + parent.getItemAtPosition(pos).toString());
	    	mSelectedPaymentModeIndex = pos;
	    	paymentMethod = paymentModes.get(mSelectedPaymentModeIndex).getName();
	    }

	    @Override
	    public void onNothingSelected(AdapterView<?> arg0) {
	    }

	}
}
