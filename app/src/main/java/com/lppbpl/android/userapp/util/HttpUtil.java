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

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import net.jarlehansen.protobuf.javame.UninitializedMessageException;

import org.apache.http.protocol.HTTP;

import android.content.res.Resources;
import android.util.Base64;

import com.logging.Level;
import com.logging.Logger;
import com.lppbpl.DataLoggingResponse;
import com.lppbpl.MultipleResponse;
import com.lppbpl.android.userapp.R;
import com.lppbpl.android.userapp.SouthFallsUserApp;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.controller.SfApplicationController;
import com.lppbpl.android.userapp.model.SfApplicationModel;

// TODO: Auto-generated Javadoc
/**
 * The Class HttpUtil.
 */
public class HttpUtil {

	/** The Constant TAG. */
	public static final String TAG = "HttpUtil";

	/** The Constant USER_AGENT. */
	private static final String USER_AGENT = "Android Mobile";

	/** The Constant SF_API_ACCESS_KEY. */
	public static final String SF_API_ACCESS_KEY = "sf_api_acess_key";

	/** The Constant SF_USER_TOKEN. */
	public static final String SF_USER_TOKEN = "sf_access_token";

	/** The Constant SF_NONCE_TOKEN. */
	public static final String SF_NONCE_TOKEN = "sf_nonce_token";

	/** The Constant SF_API_SIGNATURE. */
	private static final String SF_API_SIGNATURE = "sf_api_signature";

	/** The Constant SF_API_KEY. */
	public static final String SF_API_KEY = "sf_api_key";

	/** The Constant PLAIN_TEXT_TYPE. */
	public static final String PLAIN_TEXT_TYPE = HTTP.PLAIN_TEXT_TYPE;

	/** The Constant PROTOBUF_ENCODING_TYPE. */
	public static final String PROTOBUF_ENCODING_TYPE = "application/x-protobuf";

	/** The Constant FORM_URLENCODED_TYPE. */
	public static final String FORM_URLENCODED_TYPE = "application/x-www-form-urlencoded";

	/** The Constant APPLICATION_JSON. */
	public static final String APPLICATION_JSON = "application/json";

	/** The Constant UTF8_CHARSET. */
	private final static String UTF8_CHARSET = "UTF-8";

	/** The Constant ACCEPT_TYPE. */
	public final static String ACCEPT_TYPE = "accept";

	/** The Constant CONTENT_TYPE. */
	public final static String CONTENT_TYPE = "content-type";

	/** The Constant POST. */
	public final static String POST = "POST";

	/** The Constant GET. */
	public final static String GET = "GET";

	/** The Constant HTTP_RESPONSE_SUCCESS. */
	public static final int HTTP_RESPONSE_SUCCESS = 200;// OK. On successful
														// invocation of the API

	// Login Success Along with
	// Account LOCK || PASSWORD_EXPIRY || PASSWORD_CHANGE || YET TO ACCEPT THE
	// TERMS and Condition
	// || Incomplete Profile || USER NOT APPROVED BY OEM || DEVICE MISMATCH
	/** The Constant HTTP_RESPONSE_SUCCESS_WITH_CONDITION. */
	public static final int HTTP_RESPONSE_SUCCESS_WITH_CONDITION = 202;

	// public static final int HTTP_CHANGE_PASSWORD = 201;// OK. But force user
	// to
	// change password

	// public static final int HTTP_CHANGE_IN_DEVICEID = 202;// OK. But There is
	// a change in device
	// id

	/** The Constant HTTP_BAD_REQUEST. */
	public static final int HTTP_BAD_REQUEST = 400; // Bad Request. Request from
													// client is not valid.
													// Response will contain
													// appropriate error message

	/** The Constant HTTP_AUTH_FAILED. */
	public static final int HTTP_AUTH_FAILED = 401;// Authentication failed. API
	// key used / Signature of
	// the message is invalid.
	// Response will contain
	// appropriate error message

	/** The Constant HTTP_METHOD_NOT_SUPPORTED. */
	public static final int HTTP_METHOD_NOT_SUPPORTED = 405;// Method not
	// supported. HTTP
	// method supported
	// is only POST.
	// Client invoking
	// the API is using
	// a different HTTP
	// method

	/** The Constant HTTP_SERVER_ERROR. */
	public static final int HTTP_SERVER_ERROR = 500;// Server error occurred and
	// the processing could not
	// be completed at this
	// point in time. Need to
	// report this error to the
	// server admin

	/** The Constant HTTP_CONNECTION_DOWN. */
	public static final int HTTP_CONNECTION_DOWN = 600;

	/** The Constant HTTP_DEACTIVE_ACCOUNT. */
	public static final int HTTP_DEACTIVE_ACCOUNT = 402;

	// handle https not supported
	/** The Constant HTTP_VERSION_NOT_SUPORTED. */
	public static final int HTTP_VERSION_NOT_SUPORTED = 505;

	/** The Constant HTTP_PAGE_NOT_FOUND. */
	public static final int HTTP_PAGE_NOT_FOUND = 404;

	/** The Constant HTTP_SERVER_UNAVAILABLLE. */
	public static final int HTTP_SERVER_UNAVAILABLLE = 503;

	/** The m con http. */
	private URLConnection mURLCon = null;

	/** The m os. */
	private DataOutputStream mOs = null;

	/** The m is. */
	private DataInputStream mIs = null;

	// private boolean disconnected = false;
	/** The m response code. */
	private int mResponseCode = 0;

	/** The m app model. */
	final SfApplicationModel mAppModel;

	/** The hostname verifier. */
	final HostnameVerifier hostnameVerifier;

	/**
	 * Instantiates a new http util.
	 */
	public HttpUtil() {
		mAppModel = SfApplicationController.getInstance().getAppModel();
		mAppModel.setResponseCode(0);
		hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
		Logger.log(Level.INFO, TAG, "Access Token=" + mAppModel.getTokenKey());
	}

	/**
	 * Method getCommonRequestHeader.
	 *
	 * @return TreeMap<String,String>
	 */
	public TreeMap<String, String> getCommonRequestHeader() {
		final String tokenKey = mAppModel.getTokenKey();
		final String apiKey = mAppModel.getSfApiKey();
		final String apiAccessKey = mAppModel.getSfApiAccessKey();

		TreeMap<String, String> reqHeaders = new TreeMap<String, String>();
		if (tokenKey != null) {
			reqHeaders.put(SF_USER_TOKEN, tokenKey);
		}

		if (apiKey != null) {
			reqHeaders.put(SF_API_KEY, apiKey);
		}

		if (apiAccessKey != null) {
			reqHeaders.put(SF_API_ACCESS_KEY, apiAccessKey);
		}
		return reqHeaders;
	}

	/**
	 * this method is used only for Blood Sugar & Activity upload.
	 *
	 * @param requestMethod
	 *            the request method
	 * @param requestUri
	 *            the request uri
	 * @param postData
	 *            byte[]
	 * @param reqHeaders
	 *            the req headers
	 * @return DataLoggingResponse
	 * @throws UninitializedMessageException
	 *             the uninitialized message exception
	 */
	public DataLoggingResponse uploadMeasurementToCloud(String requestMethod,
			String requestUri, byte[] postData, Map<String, String> reqHeaders)
			throws UninitializedMessageException {
		byte[] responseBytes = postRequestDataWithSecure(requestMethod,
				requestUri, postData, reqHeaders);
		DataLoggingResponse resp = null;
		if (responseBytes != null) {
			try {
				resp = DataLoggingResponse.parseFrom(responseBytes);
			} catch (IOException e) {
				checkAuthChallenge(e);
				e.printStackTrace();
				try {
					if (mResponseCode == HTTP_BAD_REQUEST) {
						String message = new String(responseBytes).trim();
						Logger.log(Level.INFO, TAG, "message" + message);
						if (message.equals(Constants.UPDATE_PROFILE)) {
							mAppModel.setUserNotScribtion(true);
						} else if (message.equals(Constants.INVALID_DEVICE_ID)) {
							mAppModel.setInvalidDeviceId(true);
						}

					} else if (mResponseCode == HTTP_DEACTIVE_ACCOUNT) {
						mAppModel.setDeactivUserAccount(true);
					} else if (mResponseCode == HTTP_RESPONSE_SUCCESS) {
						mAppModel.setUserNotScribtion(false);
						mAppModel.setInvalidDeviceId(false);
					}
				} catch (Exception ee) {
					ee.printStackTrace();
					Logger.log(Level.DEBUG, TAG,
							"Error occured : " + ee.getMessage());
				}
			}
		}

		if (Logger.isInfoEnabled()) {
			Logger.log(Level.INFO, TAG,
					"In HTTP before returning upload(BG/Activity) response is =>"
							+ resp);
		}

		return resp;
	}

	/**
	 * Method postData.
	 *
	 * @param requestMethod
	 *            String
	 * @param requestUri
	 *            String
	 * @param postData
	 *            byte[]
	 * @param reqHeaders
	 *            Map<String,String>
	 * @return String
	 */
	public String postData(String requestMethod, String requestUri,
			byte[] postData, Map<String, String> reqHeaders) {
		byte[] responseBytes = postRequestDataWithSecure(requestMethod,
				requestUri, postData, reqHeaders);
		String status = (responseBytes != null ? new String(responseBytes)
				: null);
		if (status == null) {
			status = "-1000";
			if (Logger.isErrorEnabled()) {
				Logger.log(Level.ERROR, TAG,
						"No response received. setting the status as failed ("
								+ status + ")");
			}
		}

		return status;
	}

	/**
	 * Post the data using REST Service with secure connection(https).
	 *
	 * @param requestMethod
	 *            the request method
	 * @param requestUri
	 *            the request uri
	 * @param postData
	 *            byte[]
	 * @param reqHeaders
	 *            the req headers
	 * @return byte[]
	 */
	private byte[] postRequestDataWithSecure(String requestMethod,
			String requestUri, byte[] postData, Map<String, String> reqHeaders) {
		byte[] responseBytes = null;
		try {
			mURLCon = getSecureHttpsConnection(requestUri, requestMethod);

			mURLCon.setRequestProperty(HTTP.CONTENT_LEN, "" + postData.length);

			final String nonce = getUniqueNonce();
			if (nonce != null) {
				mURLCon.addRequestProperty(SF_NONCE_TOKEN, nonce);
			}
			addRequestHeaderProperty(reqHeaders);

			Logger.log(Level.DEBUG, TAG, "Sending Content Length: "
					+ postData.length);

			OutputStream outstream = mURLCon.getOutputStream();
			if (outstream != null) {
				mOs = new DataOutputStream(outstream);
			}
			// sending query string to web server
			if (mOs != null) {
				mOs.write(postData);
				mOs.flush();
			}

			mResponseCode = getResponseCode(mURLCon);
			mAppModel.setResponseCode(mResponseCode);
			Logger.log(Level.DEBUG, TAG, "Connection ResponseCode: "
					+ mResponseCode);
			try {
				// Read the bytes
				InputStream inputStream = mURLCon.getInputStream();
				if (inputStream != null) {
					mIs = new DataInputStream(inputStream);
				}
			} catch (IOException e) {
				checkAuthChallenge(e);
				/*
				 * When we do registration with already existing user, cloud
				 * gives bad request and giving the error message in error
				 * stream.
				 */
				mResponseCode = getResponseCode(mURLCon);
				mAppModel.setResponseCode(mResponseCode);
				InputStream errorStream = getErrorStream(mURLCon);
				if (errorStream != null) {
					mIs = new DataInputStream(errorStream);
				}
				Logger.log(Level.DEBUG, TAG, "mCon.getErrorStream(): "
						+ errorStream);
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int len = mURLCon.getContentLength();

			Logger.log(Level.DEBUG, TAG, "Content Length: " + len);

			// / below code changed
			int chr;
			while (mIs != null && (chr = mIs.read()) != -1) {
				baos.write(chr);
			}
			responseBytes = baos.toByteArray();
		} catch (IOException e) {
			checkAuthChallenge(e);
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} finally {
			closeAll();
		}

		return responseBytes;
	}

	private int getResponseCode(final URLConnection urlConnection) {
		int resCode = 0;
		if (urlConnection instanceof HttpURLConnection) {
			try {
				resCode = ((HttpURLConnection) urlConnection).getResponseCode();
			} catch (IOException e) {
				checkAuthChallenge(e);
				resCode = mResponseCode;
			}
		} else if (urlConnection instanceof HttpsURLConnection) {
			try {
				resCode = ((HttpsURLConnection) urlConnection)
						.getResponseCode();
			} catch (IOException e) {
				checkAuthChallenge(e);
				resCode = mResponseCode;
			}
		}

		return resCode;
	}

	private InputStream getErrorStream(final URLConnection urlConnection) {
		InputStream is = null;
		if (urlConnection instanceof HttpURLConnection) {
			is = ((HttpURLConnection) urlConnection).getErrorStream();
		} else if (urlConnection instanceof HttpsURLConnection) {
			is = ((HttpsURLConnection) urlConnection).getErrorStream();
		}
		return is;
	}

	/**
	 * Method postGetDataRequestWithSecure.
	 *
	 * @param requestMethod
	 *            String
	 * @param requestUri
	 *            String
	 * @param postData
	 *            byte[]
	 * @param reqHeaders
	 *            Map<String,String>
	 * @return MultipleResponse
	 * @throws UninitializedMessageException
	 *             the uninitialized message exception
	 */
	private MultipleResponse postGetDataRequestWithSecure(String requestMethod,
			String requestUri, byte[] postData, Map<String, String> reqHeaders)
			throws UninitializedMessageException {
		MultipleResponse multiResponse = null;
		try {
			mURLCon = getSecureHttpsConnection(requestUri, requestMethod);

			mURLCon.setRequestProperty(HTTP.CONTENT_LEN, "" + postData.length);
			final String nonce = getUniqueNonce();
			if (nonce != null) {
				mURLCon.addRequestProperty(SF_NONCE_TOKEN, nonce);
			}

			addRequestHeaderProperty(reqHeaders);

			Logger.log(Level.DEBUG, TAG, "Sending Content Length: "
					+ postData.length);
			OutputStream outStream = mURLCon.getOutputStream();
			if (outStream != null) {
				mOs = new DataOutputStream(outStream);
			}
			// sending query string to web server
			if (mOs != null) {
				mOs.write(postData);
				mOs.flush();
			}

			mResponseCode = getResponseCode(mURLCon);
			mAppModel.setResponseCode(mResponseCode);
			Logger.log(Level.DEBUG, TAG, "mResponseCode: " + mResponseCode);

			// Read the byte
			InputStream inputStream = mURLCon.getInputStream();
			if (inputStream != null) {
				mIs = new DataInputStream(inputStream);
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int len = mURLCon.getContentLength();
			Logger.log(Level.DEBUG, TAG, "Content Length: " + len);

			// / below code changed
			if (len > 0 && mIs != null) {
				byte[] responseData = new byte[len];
				mIs.readFully(responseData);
				baos.write(responseData);

				if (Logger.isDebugEnabled()) {
					Logger.log(Level.DEBUG, TAG,
							"received bytes: " + baos.toByteArray().length);
					// + " " + new String(baos.toByteArray()));
				}
				multiResponse = MultipleResponse.parseFrom(baos.toByteArray());
			}

			Logger.log(Level.DEBUG, TAG,
					"**postGetDataRequest MultipleResponse**");
		} catch (IOException e) {
			checkAuthChallenge(e);
			try {
				mResponseCode = getResponseCode(mURLCon);
				mAppModel.setResponseCode(mResponseCode);
				Logger.log(Level.DEBUG, TAG, "Connection ResponseCode: "
						+ mResponseCode);
			} catch (NullPointerException ee) {
				ee.printStackTrace();
			}
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} finally {
			closeAll();
		}

		return multiResponse;
	}

	/**
	 * Post the data using REST Service.
	 *
	 * @param requestMethod
	 *            the request method
	 * @param requestUri
	 *            the request uri
	 * @param postData
	 *            the post data
	 * @param reqHeaders
	 *            the req headers
	 * @return MultipleResponse
	 * @throws UninitializedMessageException
	 *             the uninitialized message exception
	 */
	public MultipleResponse postGetDataRequest(String requestMethod,
			String requestUri, byte[] postData, Map<String, String> reqHeaders)
			throws UninitializedMessageException {
		MultipleResponse multiResponse = postGetDataRequestWithSecure(
				requestMethod, requestUri, postData, reqHeaders);
		return multiResponse;
	}

	/**
	 * close the http connection.
	 *
	 * @return boolean
	 */
	private boolean closeAll() {
		try {
			if (mIs != null) {
				mIs.close();
			}
			if (mOs != null) {
				mOs.close();
			}
			if (mURLCon != null) {
				if (mURLCon instanceof HttpURLConnection) {
					((HttpURLConnection) mURLCon).disconnect();
				} else if (mURLCon instanceof HttpsURLConnection) {
					((HttpsURLConnection) mURLCon).disconnect();
				}
				mURLCon = null;
			}
			return true;
		} catch (IOException e) {
			checkAuthChallenge(e);
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Method disconnect.
	 *
	 * @return boolean
	 */
	public boolean disconnect() {
		try {
			closeAll();
			// disconnected = true;
			Logger.log(Level.DEBUG, TAG, "Http Connection disconnected.");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Method postStopEcgRequestToCloud.
	 *
	 * @param requestMethod
	 *            String
	 * @param requestUri
	 *            String
	 * @param reqParams
	 *            Map<String,String>
	 * @param reqHeaders
	 *            Map<String,String>
	 * @return DataLoggingResponse
	 */
	public DataLoggingResponse postStopEcgRequestToCloud(String requestMethod,
			String requestUri, final Map<String, String> reqParams,
			final Map<String, String> reqHeaders) {
		try {
			byte[] responseBytes = postDataViaParamsWithSecure(requestMethod,
					requestUri, reqParams, reqHeaders);
			DataLoggingResponse resp = null;

			if (responseBytes != null) {
				try {
					resp = DataLoggingResponse.parseFrom(responseBytes);
				} catch (IOException e) {
					e.printStackTrace();
					checkAuthChallenge(e);
				}
			}
			if (Logger.isInfoEnabled()) {
				Logger.log(Level.INFO, TAG,
						"In HTTP before returning stopECG response is =>"
								+ resp);
			}
			return resp;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Method postDataThroughParams.
	 *
	 * @param requestMethod
	 *            String
	 * @param requestUri
	 *            String
	 * @param reqParams
	 *            Map<String,String>
	 * @param reqHeaders
	 *            Map<String,String>
	 * @return String
	 */
	public String postDataThroughParams(String requestMethod,
			String requestUri, Map<String, String> reqParams,
			Map<String, String> reqHeaders) {

		try {
			byte[] responseBytes = postDataViaParamsWithSecure(requestMethod,
					requestUri, reqParams, reqHeaders);
			String status = responseBytes != null ? new String(responseBytes)
					: null;

			if (Logger.isInfoEnabled()) {
				Logger.log(Level.INFO, TAG,
						"In HTTP before returning status valus is =>" + status);
			}

			if (mResponseCode == HTTP_BAD_REQUEST) {
				Logger.log(Level.INFO, TAG, "message" + status);
				if (status != null
						&& status.trim().equals(Constants.UPDATE_PROFILE)) {
					mAppModel.setUserNotScribtion(true);
				} else if (status != null
						&& status.trim().equals(Constants.INVALID_DEVICE_ID)) {
					mAppModel.setInvalidDeviceId(true);
				}
			} else if (mResponseCode == HTTP_DEACTIVE_ACCOUNT) {
				mAppModel.setDeactivUserAccount(true);
			} else if (mResponseCode == HTTP_RESPONSE_SUCCESS) {
				mAppModel.setUserNotScribtion(false);
				mAppModel.setInvalidDeviceId(false);
			}

			return status;
		} catch (Exception e) {
			Logger.log(
					Level.DEBUG,
					TAG,
					"exception when receiving response from server"
							+ e.getMessage());
		}

		return null;
	}

	/**
	 * Method addRequestHeaderProperty.
	 *
	 * @param reqHeaders
	 *            Map<String,String>
	 */
	private void addRequestHeaderProperty(Map<String, String> reqHeaders) {
		Logger.log(Level.DEBUG, TAG, "setRequestProperty()");
		Iterator<Map.Entry<String, String>> iter = reqHeaders.entrySet()
				.iterator();
		while (iter.hasNext()) {
			Map.Entry<String, String> kvpair = iter.next();
			if (mURLCon != null) {
				mURLCon.setRequestProperty(kvpair.getKey(), kvpair.getValue());
			}
			Logger.log(Level.DEBUG, TAG,
					kvpair.getKey() + "=" + kvpair.getValue());
		}
	}

	/**
	 * Method postDataViaParamsWithSecure.
	 *
	 * @param requestMethod
	 *            String
	 * @param requestUri
	 *            String
	 * @param reqParams
	 *            Map<String,String>
	 * @param reqHeaders
	 *            Map<String,String>
	 * @return byte[]
	 * @throws Exception
	 *             the exception
	 */
	private byte[] postDataViaParamsWithSecure(String requestMethod,
			String requestUri, Map<String, String> reqParams,
			Map<String, String> reqHeaders) throws Exception {
		byte[] responseBytes = null;

		Logger.log(Level.DEBUG, TAG, "postDataViaParams Value in http: URL: "
				+ requestUri);
		// + " data length: " + postData.length);

		try {
			byte[] postData = generateRequestParam(reqParams).getBytes();
			final String signingURL = requestUri.replace(mAppModel
					.getPinModel().getServerAddress(),
					Constants.SOUTHFALLS_CORE);
			/*
			 * Check the API to be signed or not.
			 */
			Boolean isSignedAPI = Constants.signedAPI.get(signingURL);
			if (isSignedAPI == null || !isSignedAPI.booleanValue()) {
				isSignedAPI = requestUri.contains(Constants.CHANGE_MOBILE_NO)
				/* || requestUri.contains(Constants.UPDATE_DEVICE_ID) */;
			}
			Logger.log(Level.DEBUG, TAG, "is Signed API?" + isSignedAPI);

			if (isSignedAPI != null && isSignedAPI.booleanValue()) {
				final String signature = sign(requestMethod, signingURL,
						reqParams, reqHeaders, mAppModel.getSfApiSecretKey());
				reqHeaders.put(SF_API_SIGNATURE, signature);
				Logger.log(Level.DEBUG, TAG, "signature:" + signature);
			}

			mURLCon = getSecureHttpsConnection(requestUri, requestMethod);

			Logger.log(Level.DEBUG, TAG, "The value of ( c ) after open => "
					+ mURLCon);
			mURLCon.setRequestProperty(HTTP.CONTENT_LEN, "" + postData.length);

			final String nonce = getUniqueNonce();
			if (nonce != null) {
				mURLCon.addRequestProperty(SF_NONCE_TOKEN, nonce);
			}

			addRequestHeaderProperty(reqHeaders);

			Logger.log(Level.DEBUG, TAG, "Sending Content Length: "
					+ postData.length);

			OutputStream outStream = mURLCon.getOutputStream();
			if (outStream != null) {
				mOs = new DataOutputStream(outStream);
			}

			Logger.log(Level.DEBUG, TAG, "openDataOutputStream => " + mOs);

			// sending query string to web server
			if (mOs != null) {
				mOs.write(postData);
				mOs.flush();
			}

			// Read the bytes
			try {
				final InputStream inputStream = mURLCon.getInputStream();
				if (inputStream != null) {
					mIs = new DataInputStream(inputStream);
				}
			} catch (IOException e) {
				checkAuthChallenge(e);
				mResponseCode = getResponseCode(mURLCon);
				mAppModel.setResponseCode(mResponseCode);
				InputStream inputStream = getErrorStream(mURLCon);
				if (inputStream != null) {
					mIs = new DataInputStream(inputStream);
				}
				Logger.log(Level.DEBUG, TAG, "mCon.getErrorStream() "
						+ inputStream);
			} catch (NullPointerException ee) {
				Logger.log(Level.ERROR, TAG, "Error => " + ee.getMessage());
			}

			Logger.log(Level.DEBUG, TAG, "c.openDataInputStream =>" + mIs);

			int len = mURLCon.getContentLength();

			Logger.log(Level.DEBUG, TAG, "Content Length: " + len);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int chr;
			while (mIs != null && (chr = mIs.read()) != -1) {
				baos.write(chr);
			}
			responseBytes = baos.toByteArray();

			mResponseCode = getResponseCode(mURLCon);
			mAppModel.setResponseCode(mResponseCode);
			Logger.log(Level.INFO, TAG, "" + mResponseCode);
		} catch (IOException e) {
			checkAuthChallenge(e);
			if (e.getMessage().contains(
					"Trust anchor for certification path not found")) {
				mResponseCode = HTTP_VERSION_NOT_SUPORTED;
				mAppModel.setResponseCode(mResponseCode);
			}

			try {
				Logger.log(Level.DEBUG, TAG, "Msg :" + e.getMessage());
				mResponseCode = getResponseCode(mURLCon);
				mAppModel.setResponseCode(mResponseCode);
				Logger.log(Level.DEBUG, TAG, "Connection ResponseCode: "
						+ mResponseCode);
			} catch (NullPointerException ee) {
				ee.printStackTrace();
			}
			// e.getStackTrace();
		} finally {
			Logger.log(Level.DEBUG, TAG, "Inside finally");
			closeAll();
		}

		return responseBytes;
	}

	/**
	 * Method getData.
	 *
	 * @param requestMethod
	 *            String
	 * @param requestUri
	 *            String
	 * @param reqHeaders
	 *            Map<String,String>
	 * @return String
	 */
	public String getData(String requestMethod, String requestUri,
			Map<String, String> reqHeaders) {
		String ret = getDataSecure(requestMethod, requestUri, reqHeaders);
		return ret;
	}

	/**
	 * Method getDataSecure.
	 *
	 * @param requestMethod
	 *            String
	 * @param requestUri
	 *            String
	 * @param reqHeaders
	 *            Map<String,String>
	 * @return String
	 */
	private String getDataSecure(String requestMethod, String requestUri,
			Map<String, String> reqHeaders) {
		String retString = null;
		try {
			mURLCon = getSecureHttpsConnection(requestUri, requestMethod);

			mURLCon.setDoOutput(false);

			final String nonce = getUniqueNonce();
			if (nonce != null) {
				mURLCon.addRequestProperty(SF_NONCE_TOKEN, nonce);
			}

			addRequestHeaderProperty(reqHeaders);

			mURLCon.setRequestProperty("Content-length", "0");
			mURLCon.setUseCaches(false);
			mURLCon.setAllowUserInteraction(false);
			mURLCon.connect();

			mResponseCode = getResponseCode(mURLCon);
			mAppModel.setResponseCode(mResponseCode);

			Logger.log(Level.DEBUG, TAG, "mResponseCode: " + mResponseCode);

			// Read the byte
			InputStream inputStream = mURLCon.getInputStream();
			if (inputStream != null) {
				mIs = new DataInputStream(inputStream);
			}

			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			int data = mIs.read();
			while (data != -1) {
				baos.write(data);
				data = mIs.read();
			}

			retString = new String(baos.toByteArray());

		} catch (IOException e) {
			e.printStackTrace();
			checkAuthChallenge(e);
			try {
				mResponseCode = getResponseCode(mURLCon);
				mAppModel.setResponseCode(mResponseCode);
				Logger.log(Level.DEBUG, TAG, "Connection ResponseCode: "
						+ mResponseCode);
			} catch (NullPointerException ee) {
				ee.printStackTrace();
			}

		} catch (NullPointerException e) {
			e.printStackTrace();
		} finally {
			closeAll();
		}

		if (Logger.isDebugEnabled()) {
			Logger.log(Level.DEBUG, TAG, "returning the response");
		}

		return retString;
	}

	/**
	 * Method getSecureHttpsConnection.
	 *
	 * @param url
	 *            String
	 * @return HttpsURLConnection
	 */
	private URLConnection getSecureHttpsConnection(final String url,
			final String requestMethod) {
		try {
			Logger.log(Level.INFO, TAG, "getSecureConnection()");
			URL nUrl = new URL(url);
			Logger.log(Level.INFO, TAG, "Before connections");
			mURLCon = nUrl.openConnection();
			Logger.log(Level.INFO, TAG, "After connections");
			if (mURLCon instanceof HttpsURLConnection) {
				((HttpsURLConnection) mURLCon)
						.setHostnameVerifier(hostnameVerifier);
				((HttpsURLConnection) mURLCon).setRequestMethod(requestMethod);
			} else if (mURLCon instanceof HttpURLConnection) {
				((HttpURLConnection) mURLCon).setRequestMethod(requestMethod);
			}
			mURLCon.setDoInput(true);
			mURLCon.setDoOutput(true);
			mURLCon.addRequestProperty(HTTP.USER_AGENT, USER_AGENT);
			mURLCon.setConnectTimeout(30000);

		} catch (MalformedURLException e) {
			e.printStackTrace();
			Logger.log(Level.ERROR, TAG, e.getMessage());
		} catch (IOException e) {
			checkAuthChallenge(e);
			e.printStackTrace();
			Logger.log(Level.ERROR, TAG, e.getMessage());
		}
		return mURLCon;
	}

	/**
	 * Method getRespnseMsg.
	 *
	 * @param resCode
	 *            int
	 * @return String
	 */
	public static String getRespnseMsg(int resCode) {
		final Resources res = SouthFallsUserApp.getInstance().getResources();
		if (res == null) {
			return "";
		}

		switch (resCode) {
		case HttpUtil.HTTP_AUTH_FAILED:
			return res.getString(R.string.http_auth_fail);
		case HttpUtil.HTTP_BAD_REQUEST:
			return res.getString(R.string.http_bad_request);
		case HttpUtil.HTTP_METHOD_NOT_SUPPORTED:
			return res.getString(R.string.http_method_not_supported);
		case HttpUtil.HTTP_SERVER_ERROR:
			return res.getString(R.string.http_server_down);
		case HttpUtil.HTTP_CONNECTION_DOWN:
			return res.getString(R.string.http_connection_down);
		case HttpUtil.HTTP_VERSION_NOT_SUPORTED:
			return res.getString(R.string.https_not_suppoted);
		default:
			return res.getString(R.string.http_general_error);
		}
	}

	/**
	 * Sign.
	 *
	 * @param requestMethod
	 *            the request method
	 * @param requestUri
	 *            the request uri
	 * @param reqParams
	 *            the req params
	 * @param reqHeaders
	 *            the req headers
	 * @param secretKey
	 *            the secret key
	 * @return String
	 */
	private static String sign(String requestMethod, String requestUri,
			Map<String, String> reqParams, Map<String, String> reqHeaders,
			String secretKey) {
		Logger.log(Level.DEBUG, TAG,
				"....... Entered HMACSignature:sign ...........");

		String qsToSign = requestMethod + "\n" + requestUri;

		Logger.log(Level.DEBUG, TAG, "reqParams.size: " + reqParams.size());
		Logger.log(Level.DEBUG, TAG, "reqHeaders.size: " + reqHeaders.size());

		if (reqHeaders.size() > 0) {
			Logger.log(Level.DEBUG, TAG,
					"Creating canonicalized string for request headers....");
			SortedMap<String, String> sortedHeaderMap = new TreeMap<String, String>(
					reqHeaders);
			String canonicalRequestHeaders = canonicalize(sortedHeaderMap);
			qsToSign = qsToSign + "\n" + canonicalRequestHeaders;
		}
		if (reqParams.size() > 0) {
			Logger.log(Level.DEBUG, TAG,
					"Creating canonicalized string for request params....");
			SortedMap<String, String> sortedParamMap = new TreeMap<String, String>(
					reqParams);
			String canonicalRequestParams = canonicalize(sortedParamMap);
			qsToSign = qsToSign + "\n" + canonicalRequestParams;
		}

		Logger.log(Level.DEBUG, TAG, "Query string to sign: " + qsToSign);
		String hmac = hmac(qsToSign, secretKey);
		String sig = percentEncodeRfc3986(hmac);

		return sig;
	}

	/**
	 * Method hmac.
	 *
	 * @param stringToSign
	 *            String
	 * @param secretkey
	 *            String
	 * @return String
	 */
	private static String hmac(String stringToSign, String secretkey) {
		byte[] regData = null;
		String signature = null;
		try {
			javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
			SecretKeySpec secrat = new SecretKeySpec(
					secretkey.getBytes(UTF8_CHARSET), mac.getAlgorithm());
			mac.init(secrat);
			regData = mac.doFinal(stringToSign.getBytes(UTF8_CHARSET));

			// Base 64 Encode the results
			signature = Base64.encodeToString(regData, Base64.NO_WRAP);
		} catch (NoSuchAlgorithmException e) {
			Logger.log(Level.DEBUG, TAG,
					"NoSuchAlgorithmException:" + e.getMessage());
			e.printStackTrace();
		} catch (UnsupportedEncodingException ee) {
			Logger.log(Level.DEBUG, TAG,
					"UnsupportedEncodingException:" + ee.getMessage());
			ee.printStackTrace();
		} catch (InvalidKeyException ivke) {
			Logger.log(Level.DEBUG, TAG,
					"InvalidKeyException:" + ivke.getMessage());
		}
		return signature;
	}

	/**
	 * Canonicalizes the input String.
	 *
	 * @param reqParam
	 *            the req param
	 * @return String
	 */
	private static String generateRequestParam(Map<String, String> reqParam) {
		Logger.log(Level.DEBUG, TAG, "generateRequestParam()");

		if (reqParam.isEmpty()) {
			return "";
		}

		StringBuffer buffer = new StringBuffer();
		Iterator<Map.Entry<String, String>> iter = reqParam.entrySet()
				.iterator();

		try {
			while (iter.hasNext()) {
				Map.Entry<String, String> kvpair = iter.next();
				buffer.append(URLEncoder.encode(kvpair.getKey(), HTTP.UTF_8));
				buffer.append('=');
				buffer.append(URLEncoder.encode(kvpair.getValue(), HTTP.UTF_8));
				if (iter.hasNext()) {
					buffer.append('&');
				}
			}
		} catch (UnsupportedEncodingException e) {
			Logger.log(Level.DEBUG, TAG, "UnsupportedEncodingException:" + e);
		}
		String canonical = buffer.toString();
		Logger.log(Level.DEBUG, TAG, "reqParam:" + buffer.toString());
		return canonical;
	}

	/**
	 * Canonicalizes the input String.
	 *
	 * @param sortedMap
	 *            the sorted map
	 * @return String
	 */
	private static String canonicalize(SortedMap<String, String> sortedMap) {
		if (sortedMap.isEmpty()) {
			return "";
		}

		StringBuffer buffer = new StringBuffer();
		Iterator<Map.Entry<String, String>> iter = sortedMap.entrySet()
				.iterator();

		while (iter.hasNext()) {
			Map.Entry<String, String> kvpair = iter.next();
			buffer.append(percentEncodeRfc3986(kvpair.getKey()));
			buffer.append('=');
			buffer.append(percentEncodeRfc3986(kvpair.getValue()));
			if (iter.hasNext()) {
				buffer.append('&');
			}
		}
		String canonical = buffer.toString();
		return canonical;
	}

	/**
	 * Percent encodes the input string based on rfc 3986.
	 *
	 * @param s
	 *            the s
	 * @return String
	 */
	private static String percentEncodeRfc3986(String s) {
		String out;
		try {
			out = URLEncoder.encode(s, UTF8_CHARSET).replace("+", "%20")
					.replace("*", "%2A").replace("%7E", "~");
		} catch (UnsupportedEncodingException e) {
			out = s;
			Logger.log(Level.DEBUG, TAG, "Error occured : " + e.getMessage());
		}
		return out;
	}

	/**
	 * returns nonce token.
	 *
	 * @return String
	 */
	private String getUniqueNonce() {
		String nonceToken = null;
		try {
			// Create a secure random number generator
			SecureRandom sr = new SecureRandom();
			nonceToken = Long.toString(sr.nextLong());
		} catch (Exception e) {
			Logger.log(Level.DEBUG, TAG, e.getMessage());
		}

		Logger.log(Level.DEBUG, TAG, "nonceToken : " + nonceToken);
		return nonceToken;
	}

	/**
	 * Method checkAuthChallenge.
	 *
	 * @param e
	 *            IOException
	 */
	private void checkAuthChallenge(IOException e) {
		Logger.log(Level.DEBUG, TAG, "checkAuthChallenge : " + e.getMessage());
		if (e.getMessage().contains("authentication challenge")) {
			mResponseCode = 401;
			mAppModel.setResponseCode(mResponseCode);
		}
	}
}
