/*
 *
 */
package com.lppbpl.android.userapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import com.lppbpl.android.userapp.R;

// TODO: Auto-generated Javadoc
/**
 * This Activity is to display the Terms and condition, Privacy consent to user.
 *
 */
public class TncnPrivacyPopup extends Activity implements OnClickListener {
	TextView mTncnTextview;
	Button mOkButton;
	WebView mTncnWebview;

	/**
	 * Method onCreate.
	 *
	 * @param savedInstanceState
	 *            Bundle
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tncn_privacy_popup);
		final String mResult = getIntent().getExtras().getString("tncnPrivacyResponse");
		final String mTitle = getIntent().getExtras().getString("popupTitle");
		setTitle(mTitle);
		mTncnTextview = (TextView) findViewById(R.id.tvterm_privacy_textview);
		mTncnTextview.setText(R.string.tnc_loading_wait_text);

		mTncnWebview = (WebView) findViewById(R.id.tnc_webview);
		mTncnWebview.setVisibility(View.GONE);
		mTncnWebview.setWebViewClient(new MyWebViewClient());

		mTncnWebview.loadDataWithBaseURL(null, mResult, "text/html", "UTF-8", null);

		mOkButton = (Button) findViewById(R.id.ok);
		mOkButton.setText(R.string.OK);
		mOkButton.setVisibility(View.GONE);
		mOkButton.setOnClickListener(this);
	}

	/**
	 * Method onClick.
	 *
	 * @param v
	 *            View
	 * @see android.view.View$OnClickListener#onClick(View)
	 */
	public void onClick(View v) {
		if (v.getId() == R.id.ok) {
			finish();
		}
	}

	private class MyWebViewClient extends WebViewClient{
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			mTncnTextview.setVisibility(View.VISIBLE);
			view.setVisibility(View.GONE);
        	mOkButton.setVisibility(View.GONE);
		}

        @Override
        public void onPageFinished(WebView view, String url) {
        	mTncnTextview.setVisibility(View.GONE);
        	view.setVisibility(View.VISIBLE);
        	mOkButton.setVisibility(View.VISIBLE);
        }
	}
}
