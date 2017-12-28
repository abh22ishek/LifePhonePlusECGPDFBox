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

import java.util.TreeMap;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.android.userapp.R;
import com.lppbpl.android.userapp.util.HttpUtil;

/**
 * The Class BillPaymentActivity.
 */
@SuppressLint("SetJavaScriptEnabled")
public class BillPaymentActivity extends AppBaseActivity {
	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.payment_webview);
		final int pageTitle = getIntent().getIntExtra("activityTitle", R.string.consultation);
		setCustomNoIconTitle(pageTitle);

		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

		webView = (WebView) findViewById(R.id.webview);
		webView.clearHistory();

		final WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setAllowFileAccess(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setLoadsImagesAutomatically(true);
		settings.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		final String urlString = getIntent().getStringExtra("redirectLocation");

		new LoadURLTask(webView).execute(urlString);
	}

	/**
	 * ASync Task that checks if the site is reachable and then loads the URL.
	 */
	private class LoadURLTask extends AsyncTask<String, Void, Boolean> {
		private final WebView mWebView;
		private String mURLString;
		private String mRedirectUrl;

		private LoadURLTask(final WebView webView) {
			mWebView = webView;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Boolean doInBackground(final String... urls) {
			boolean ret = true;
			mURLString = urls[0];

			try {
				final CookieSyncManager cookieSyncManager = CookieSyncManager
						.createInstance(webView.getContext());
				cookieSyncManager.startSync();
				final CookieManager cookieManager = CookieManager.getInstance();
				cookieManager.setAcceptCookie(true);
				cookieManager.removeSessionCookie();
				cookieManager.setCookie(mPinModel.getServerAddress(),
						HttpUtil.SF_USER_TOKEN + '=' + mAppModel.getTokenKey());
				cookieSyncManager.startSync();

				Logger.log(Level.DEBUG, TAG,
						"Server Address:" + mPinModel.getServerAddress());

				final MyWebViewClient client = new MyWebViewClient();
				mWebView.setWebViewClient(client);

				mWebView.setWebChromeClient(new WebChromeClient() {
					@Override
					public boolean onJsAlert(WebView view, String url,
							String message, JsResult result) {
						return super.onJsAlert(view, url, message, result);
					}
				});

				final TreeMap<String, String> reqHeaders = new TreeMap<String, String>();
				reqHeaders.put("Cookie", HttpUtil.SF_USER_TOKEN + '='
						+ mAppModel.getTokenKey());
				reqHeaders.put(HttpUtil.SF_USER_TOKEN, mAppModel.getTokenKey());
				mWebView.loadUrl(mURLString, reqHeaders);
			} catch (Exception e) {
				ret = false;
				Logger.log(Level.ERROR, TAG, "" + e);
			}

			return ret;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(final Boolean isReachable) {
			if (!isReachable) {
				createAndShowNetworkDialog(mRedirectUrl);
			}
		}
	}

	/**
	 * Shows the network dialog alerting the user that the net is down.
	 */
	private void createAndShowNetworkDialog(String message) {
		new AlertDialog.Builder(this)
				.setTitle("Browser Error")
				.setMessage(message)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(final DialogInterface dialog,
									final int which) {
								finish();
							}
						}).show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Check if the key event was the Back button and if there's history
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			webView.goBack();
			return true;
		}
		// If it wasn't the Back key or there's no web page history, bubble up
		// to the default
		// system behavior (probably exit the activity)
		return super.onKeyDown(keyCode, event);
	}

	private class MyWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// Logger.log(Level.DEBUG, TAG, "shouldOverrideUrlLoading url=" +
			// url);

			CookieSyncManager.createInstance(webView.getContext());
			final CookieManager cookieManager = CookieManager.getInstance();
			cookieManager.setCookie(mPinModel.getServerAddress(),
					HttpUtil.SF_USER_TOKEN + '=' + mAppModel.getTokenKey());

			Logger.log(Level.DEBUG, TAG,
					"Server Address:" + mPinModel.getServerAddress());

			CookieSyncManager.getInstance().startSync();

			view.loadUrl(url);
			return false;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// Logger.log(Level.DEBUG, TAG, "onPageStarted url=" + url);
			super.onPageStarted(view, url, favicon);

			showProgressDialog(getString(R.string.please_wait_text));
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			// Logger.log(Level.DEBUG, TAG, "onPageFinished url=" + url);
			webView.clearHistory();
			dismissProgressDislog();
		}
	}

}
