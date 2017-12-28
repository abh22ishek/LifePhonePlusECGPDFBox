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

package com.lppbpl.android.userapp.constants;

import java.util.HashMap;
import java.util.UUID;

/**
 * The Class Constants.
 */
public final class Constants {

	/** The Constant USER_MEMBER. */
	public static final String USER_MEMBER = "MEMBER";

	/** The Constant USER_PARAMEDIC. */
	public static final String USER_PARAMEDIC = "DEVICE_USER_PARAMEDIC";

	/** The Constant BATTERY_CRITICAL_MIN. */
	public static final int BATTERY_CRITICAL_MIN = 0;

	/** The Constant BATTERY_CRITICAL_MAX. */
	public static final int BATTERY_CRITICAL_MAX = 15;

	/** The Constant BATTERY_LOW_MIN. */
	public static final int BATTERY_LOW_MIN = 16;

	/** The Constant BATTERY_LOW_MAX. */
	public static final int BATTERY_LOW_MAX = 49;

	/** The Constant BATTERY_NORMAL_MIN. */
	public static final int BATTERY_NORMAL_MIN = 50;

	/** The Constant BATTERY_NORMAL_MAX. */
	public static final int BATTERY_NORMAL_MAX = 100;

	/** The Constant TOTAL_ECG_MEASURE_STEPS. */
	public static final int TOTAL_ECG_MEASURE_STEPS = 4;

	/** The Constant MAX_ECG_REMEASURE. */
	public static final int MAX_ECG_REMEASURE = 3;

	// public static final int MAX_NO_OF_SYMPTOMS = 5;
	/** The Constant MAX_NO_OF_BG_SYMPTOMS. */
	public static final int MAX_NO_OF_BG_SYMPTOMS = 5;

	/** The Constant MAX_NO_OF_ECG_SYMPTOMS. */
	public static final int MAX_NO_OF_ECG_SYMPTOMS = 5;

	/** The Constant MAX_NO_OF_ATTEMPT. */
	public static final int MAX_NO_OF_ATTEMPT = 3;

	/** The Constant BG_MIN_VALUE. */
	public final static int BG_MIN_VALUE = 10;

	/** The Constant BG_MAX_VALUE. */
	public final static int BG_MAX_VALUE = 600;

	/** The Constant ACCESSORY_UUID. */
	public static final UUID ACCESSORY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

	// matches 10-digit numbers only
	// public final static String VALID_MOBILE_NO = "^[0-9]{10}$";

	// matches only characters
	/** The Constant VALID_NAME. */
	public final static String VALID_NAME = "[a-zA-Z ]*$";

	/** The Constant UPDATE_PROFILE. */
	public final static String UPDATE_PROFILE = "Update Profile";

	/** The Constant INVALID_DEVICE_ID. */
	public final static String INVALID_DEVICE_ID = "Invalid DeviceId";

	/** The request code login. */
	public static final int REQUEST_CODE_LOGIN = 1;

	/** The request code upload. */
	public static final int REQUEST_CODE_UPLOAD = 2;

	/** The request code TPA Accept. */
	public static final int REQUEST_CODE_TPA = 3;

	/** The Constant HOST. */
	// public static final String HOST = "https://spcas-stg.intel.com";

	/** The Constant BG_OR_ACT_UPLOAD_URL. */
	public static final String BG_OR_ACT_UPLOAD_URL = "/rest/auth/devicedata/data/log";

	/** The Constant GET_BG_LOGDATA_URL. */
	public static final String GET_BG_LOGDATA_URL = "/rest/auth/devicedata/BG/read";

	/** The Constant GET_ACT_LOGDATA_URL. */
	public static final String GET_ACT_LOGDATA_URL = "/rest/auth/devicedata/ACT/read";

	/** The Constant GET_ECG_LOGDATA_URL. */
	public static final String GET_ECG_LOGDATA_URL = "/rest/auth/devicedata/ecg/getmeasurementid";

	/** The Constant TERM_CONDITION_URL. */
	public static final String TERM_CONDITION_URL = "/rest/user/register/termsandconditions";

	/** The Constant PRIVACY_URL. */
	public static final String PRIVACY_URL = "/rest/user/register/privacypolicy";

	/** The Constant REGISTRATION_URL. */
	public static final String REGISTRATION_URL = "/rest/user/register";

	/** The Constant LOGIN_URL. */
	public static final String LOGIN_URL = "/rest/user/login/devicelogin";

	/** The Constant GET_ECGDATA_URL. */
	public static final String GET_ECGDATA_URL = "/rest/auth/devicedata/ecg/getecgdata";

	/** The Constant ECG_UPLOAD_URL. */
	public static final String ECG_UPLOAD_URL = "/rest/auth/devicedata/ecg/upload";

	/** The Constant SERVICE_VALIDATE_URL. */
	public static final String SERVICE_VALIDATE_URL = "/rest/auth/subscription/user";

	/** The Constant CONSULT_DOC_URL. */
	public static final String CONSULT_DOC_URL = "/rest/auth/devicedata/consult";

	/** The Constant ECG_UPLOAD_START_URL. */
	public static final String ECG_UPLOAD_START_URL = "/rest/auth/devicedata/ecg/start";

	/** The Constant ECG_UPLOAD_STOP_URL. */
	public static final String ECG_UPLOAD_STOP_URL = "/rest/auth/devicedata/ecg/stop";

	// new feature
	/** The Constant CHANGE_PASSWORD. */
	public static final String CHANGE_PASSWORD = "/rest/auth/userprofile/changepassword";

	/** The Constant UPDATE_DEVICE_ID. */
	// public static final String UPDATE_DEVICE_ID =
	// "/rest/auth/userprofile/updateDeviceID/";

	/** The Constant CHANGE_MOBILE_NO. */
	public static final String CHANGE_MOBILE_NO = "/rest/auth/userprofile/changePhoneNumber/";

	/** The Constant CONSULTED_CASES. */
	public static final String CONSULTED_CASES = "/rest/auth/consultancy/case/user/cases";

	/** The Constant BG_CONSULTED_CASE_DETAIL. */
	public static final String BG_CONSULTED_CASE_DETAIL = "/rest/auth/devicedata/BG/";

	/** The Constant ECG_CONSULTED_CASE_DETAIL. */
	public static final String ECG_CONSULTED_CASE_DETAIL = "/rest/auth/devicedata/ECG/";

	/** The Constant PASSWORD_RESET_URL. */
	public static final String PASSWORD_RESET_URL = "/rest/user/register/forgotpassword";

	/** The Constant LOGOUT_URL. */
	public static final String LOGOUT_URL = "/rest/user/logout";

	/** The Constant CREATE_MEMBER. */
	public static final String CREATE_MEMBER = "/rest/auth/userprofile/createMember";

	/** The Constant GET_MEMBERS. */
	public static final String GET_MEMBERS = "/rest/auth/userprofile/getMemberList";

	/** The Constant IS_PRIMARY. */
	public static final String IS_PRIMARY = "/rest/auth/session/";

	/** The Constant MEMBER_TERMS_AND_CONDITION. */
	public static final String MEMBER_TERMS_AND_CONDITION = "/rest/auth/userprofile/accepttermsandconditions";

	/** The Constant MY_DATA_URL. */
	public static final String MY_DATA_URL = "/rest/auth/devicedata/searchdata";

	public static final String CUSTOMER_SEGMENT = "/rest/auth/customersegment";

	public static final String CHECK_USER = "/rest/user/validate/checkuser";

	public static final String CHECK_PASSWORD = "/rest/user/validate/checkpassword";

	public static final String GET_USER_PROFILE = "/rest/auth/userprofile/complete/";

	public static final String UPDATE_CONTACT_ADDRESS = "/rest/auth/userprofile/updateAddress";

	public static final String UPDATE_EMERGENCY_CONTACT = "/rest/auth/userprofile/updateEC";

	public static final String GET_PAYMENT_MODE = "/rest/auth/payment/paymentMode";

	/** Wallet top-up */
	public static final String GET_TOPUP_PRODUCTS = "/rest/auth/payment/products";
	public static final String DO_TOPUP = "/rest/auth/payment/topup";
	public static final String GET_CURRENT_WALLET_BALANCE = "/rest/auth/payment/balance";

	/** Third Party Agreements (TPA) */
	public static final String GET_AGREEMENT = "/rest/user/agreement/current/Y";
	public static final String GET_AGREEMENT_INFO = "/rest/user/agreement/info";
	public static final String ACCEPT_TPA_AGREEMENT = "/rest/user/agreement/accept";
	public static final String GET_TPA_TAT_CURRENT_VALUES = "/rest/auth/provider/conf/tat/current";

	/** The Constant SOUTHFALLS_CORE. */
	public static final String SOUTHFALLS_CORE = "/southfalls-core";

	/** The signed api. */
	public static HashMap<String, Boolean> signedAPI = new HashMap<String, Boolean>();

	static {
		signedAPI.put(SOUTHFALLS_CORE + BG_OR_ACT_UPLOAD_URL, false);
		signedAPI.put(SOUTHFALLS_CORE + GET_BG_LOGDATA_URL, false);
		signedAPI.put(SOUTHFALLS_CORE + GET_ACT_LOGDATA_URL, false);
		signedAPI.put(SOUTHFALLS_CORE + GET_ECG_LOGDATA_URL, false);
		signedAPI.put(SOUTHFALLS_CORE + REGISTRATION_URL, true);
		signedAPI.put(SOUTHFALLS_CORE + LOGIN_URL, false);
		signedAPI.put(SOUTHFALLS_CORE + GET_ECGDATA_URL, false);
		signedAPI.put(SOUTHFALLS_CORE + ECG_UPLOAD_URL, false);
		signedAPI.put(SOUTHFALLS_CORE + SERVICE_VALIDATE_URL, false);
		signedAPI.put(SOUTHFALLS_CORE + CONSULT_DOC_URL, true);
		signedAPI.put(SOUTHFALLS_CORE + ECG_UPLOAD_START_URL, false);
		signedAPI.put(SOUTHFALLS_CORE + ECG_UPLOAD_STOP_URL, false);
		signedAPI.put(SOUTHFALLS_CORE + CHANGE_PASSWORD, true);
		// signedAPI.put(SOUTHFALLS_CORE + UPDATE_DEVICE_ID, true);
		signedAPI.put(SOUTHFALLS_CORE + CHANGE_MOBILE_NO, true);
		signedAPI.put(SOUTHFALLS_CORE + LOGOUT_URL, false);
		signedAPI.put(SOUTHFALLS_CORE + CREATE_MEMBER, false);
		signedAPI.put(SOUTHFALLS_CORE + GET_MEMBERS, false);
		signedAPI.put(SOUTHFALLS_CORE + IS_PRIMARY, false);
		signedAPI.put(SOUTHFALLS_CORE + MEMBER_TERMS_AND_CONDITION, false);
		signedAPI.put(SOUTHFALLS_CORE + MY_DATA_URL, false);
		signedAPI.put(SOUTHFALLS_CORE + GET_TOPUP_PRODUCTS, false);
		signedAPI.put(SOUTHFALLS_CORE + DO_TOPUP, false);
		signedAPI.put(SOUTHFALLS_CORE + GET_CURRENT_WALLET_BALANCE, false);
		signedAPI.put(SOUTHFALLS_CORE + CHECK_USER, true);
		signedAPI.put(SOUTHFALLS_CORE + CHECK_PASSWORD, true);
		signedAPI.put(SOUTHFALLS_CORE + GET_AGREEMENT, false);
		signedAPI.put(SOUTHFALLS_CORE + GET_AGREEMENT_INFO, false);
		signedAPI.put(SOUTHFALLS_CORE + ACCEPT_TPA_AGREEMENT, false);
		signedAPI.put(SOUTHFALLS_CORE + GET_TPA_TAT_CURRENT_VALUES, false);
	}

	// List of commands
	/** The Constant ERROR_COMMAND. */
	public static final int ERROR_COMMAND = -1;

	/** The Constant DISPLAY_COMMAND. */
	public static final int DISPLAY_COMMAND = 9;

	// public static final int BG_TIMEOUT = 18;
	/** The Constant CONTINUE_COMMAND. */
	public static final int CONTINUE_COMMAND = 29;

	/** The Constant CONSULT_COMMAND. */
	public static final int CONSULT_COMMAND = 30;

	/** The Constant SHUTDOWN_TIMEOUT. */
	public static final int SHUTDOWN_TIMEOUT = 35;

	// public static final int BATTERY_UPDATE = 36;
	/** The Constant CANCEL_TIMEOUT. */
	public static final int CANCEL_TIMEOUT = 37;

	// Bluetooth connection code
	/** The Constant BT_CONNECTION_LOST. */
	public static final int BT_CONNECTION_LOST = -100;

	// Received Events
	/** The Constant RECEIVED_ECG_DATA. */
	public static final int RECEIVED_ECG_DATA = 100;

	// Device Data received
	/** The Constant RECEIVED_DEVICE_DATA. */
	public static final int RECEIVED_DEVICE_DATA = 101;

	/** The Constant RECEIVED_DEVICE_DATA_ERR. */
	public static final int RECEIVED_DEVICE_DATA_ERR = 102;

	/** The Constant RECEIVED_ECG_STEP_INFO. */
	public static final int RECEIVED_ECG_STEP_INFO = 103;

	/** The Constant RECEIVED_BG_STEP_INFO. */
	public static final int RECEIVED_BG_STEP_INFO = 104;

	/** The Constant RECEIVED_ECG_QUALITY_POOR. */
	public static final int RECEIVED_ECG_QUALITY_POOR = 105;

	/** The Constant CACHED_MEASUREMENTS. */
	public static final int CACHED_MEASUREMENTS = 106;

	/** The Constant LOGIN_RESPONSE_RECEIVED. */
	public static final int LOGIN_RESPONSE_RECEIVED = 107;

	/** The Constant REGISTERATION_RESPONSE_RECEIVED. */
	public static final int REGISTERATION_RESPONSE_RECEIVED = 108;

	/** The Constant GETLATESTREC_RESPONSE_RECEIVED. */
	public static final int GETLATESTREC_RESPONSE_RECEIVED = 109;

	/** The Constant GETDATA_RESPONSE_RECEIVED. */
	public static final int GETDATA_RESPONSE_RECEIVED = 110;

	/** The Constant UPDATE_ECG_UPLOAD_PROGRESS. */
	public static final int UPDATE_ECG_UPLOAD_PROGRESS = 111;

	public static final int UPDATE_CENCELED = 112;

	public static final int UPDATE_CENCELED_CLEAR = 113;

	public static final int DB_ERROR_OCCURRED = 114;

	// Status of handleCommand
	/** The Constant STATUS_OK. */
	public static final int STATUS_OK = 0;

	/** The Constant STATUS_IGNORE. */
	public static final int STATUS_IGNORE = 1000;

	/** The Constant STATUS_PROCESS. */
	public static final int STATUS_PROCESS = 1001;

	/** The Constant STATUS_CONSUMED. */
	public static final int STATUS_CONSUMED = 1002;

	// Registration statuses received on SMS
	/** The Constant REGISTER_SUCCESS. */
	public static final int REGISTER_SUCCESS = 1;

	/** The Constant REGISTER_UPDATE. */
	public static final int REGISTER_UPDATE = 2;

	/** The Constant REGISTER_FAILED. */
	public static final int REGISTER_FAILED = -1;

	/** The Constant INVALID_UID. */
	public static final int INVALID_UID = -3;

	/** The Constant MAX_BG_WAIT_TIME. */
	public static final int MAX_BG_WAIT_TIME = 60;

	// Activity values
	/** The Constant MIN_IN_MILLISEC. */
	public static final long MIN_IN_MILLISEC = 60 * 1000;

	// Hour in milliseconds
	/** The Constant HOUR_IN_MILLISEC. */
	public static final int HOUR_IN_MILLISEC = 60 * 60 * 1000;

	// DAY in milliseconds
	/** The Constant DAY_IN_MILLISEC. */
	public static final int DAY_IN_MILLISEC = 1000 * 60 * 60 * 24;

	// 30 seconds
	/** The Constant GET_DATA_ACTIVITY_IN_MILLISEC. */
	public static final int GET_DATA_ACTIVITY_IN_MILLISEC = 5 * 1000;

	// 30 seconds
	/** The Constant GET_ECG_DATA_IN_MILLLISEC. */
	public static final int GET_ECG_DATA_IN_MILLLISEC = 5 * 1000;

	// measure time out
	/** The Constant GET_MEASURE_TIMEOUT. */
	public static final int GET_MEASURE_TIMEOUT = 25 * 1000;

	/** The Constant MAX_PHONE_NUM_LEN. */
	public static final int MAX_PHONE_NUM_LEN = 10;

	/** The Constant BT_RECONNECT_DELAY. */
	public static final long BT_RECONNECT_DELAY = 30 * 1000;// 30 secs delay

	/** The Constant BT_TIMEOUT_DELAY. */
	public static final long BT_TIMEOUT_DELAY = 30 * 1000;

	/** The Constant SHUTDOWN_TIMEOUT_DELAY. */
	public static final long SHUTDOWN_TIMEOUT_DELAY = 4 * 1000;

	/** The Constant CANCEL_TIMEOUT_DELAY. */
	public static final long CANCEL_TIMEOUT_DELAY = 2 * 1000;

	/** The Constant RESET_ACT_GETDATA_DELAY. */
	public static final long RESET_ACT_GETDATA_DELAY = 5 * 1000L;

	/** The Constant LOGIN_SUCCESS. */
	public static final String LOGIN_SUCCESS = "Success";

	/** The Constant LOGIN_FAIL. */
	public static final String LOGIN_FAIL = "Failure";

	/** The Constant CONTROL_SOLUTION. */
	public static final String CONTROL_SOLUTION = "control_solution";

	/** The Constant DEMO_MODE. */
	public static final String DEMO_MODE = "demo_mode";

	// ecg config file
	/** The Constant ECG_CONFIG_RES. */
	public static final String ECG_CONFIG_RES = "ecg_config.txt";

	// Send Statuses
	// Server returned status code
	/** The Constant SEND_STATUS_SUCCESS. */
	public static final int SEND_STATUS_SUCCESS = 0;

	/** The Constant SEND_STATUS_AUTH_FAIL. */
	public static final int SEND_STATUS_AUTH_FAIL = -1;

	/** The Constant SEND_STATUS_SERVER_DB_ERR. */
	public static final int SEND_STATUS_SERVER_DB_ERR = -2;

	// In case of exception to reach Server
	/** The Constant SEND_STATUS_NW_ERROR. */
	public static final int SEND_STATUS_NW_ERROR = -1000;

	// Server request timeout at the application
	/** The Constant SEND_STATUS_REQ_TIMEOUT. */
	public static final int SEND_STATUS_REQ_TIMEOUT = -1001;

	// No valid data or URL set
	/** The Constant SEND_STATUS_NO_DATA_OR_URL. */
	public static final int SEND_STATUS_NO_DATA_OR_URL = -1002;

	// Diagnosis Message List
	/** The Constant DIAGNOSIS_MSGLIST_TABLE. */
	public static final String DIAGNOSIS_MSGLIST_TABLE = "diagnosis_msglist_table";

	/** The Constant USER_LOGIN_TOKEN_DB. */
	public static final String USER_LOGIN_TOKEN_DB = "user_login_db";

	/** The Constant SAVE_LOGIN_TOKEN. */
	public static final String SAVE_LOGIN_TOKEN = "save_login_token";

	/** The Constant SAVE_USER_ROLE. */
	public static final String SAVE_USER_ROLE = "save_user_role";

	/** The Constant SAVE_USER_UNIQUE_ID. */
	public static final String SAVE_USER_UNIQUE_ID = "save_user_unique_id";

	// User registration and profile Db
	/** The Constant USER_DETAILS_DB. */
	public static final String USER_DETAILS_DB = "user_details_db";

	/** The Constant REMEMBER_USER_ID. */
	public static final String REMEMBER_USER_ID = "remember_user_id";

	/** The Constant SAVE_USER_ID. */
	public static final String SAVE_USER_ID = "save_user_id";

	/** The Constant SAVE_DEVICE_ID. */
	public static final String SAVE_DEVICE_ID = "save_device_id";

	public static final String SAVE_DEVICE_VERSION = "save_device_version";

	/** The Constant MEMBER_LIST. */
	public static final String MEMBER_LIST = "member_user_db";

	/** The Constant MEMBER_NAME. */
	public static final String MEMBER_NAME = "member_name";

	/** The Constant MEMBER_ID. */
	public static final String MEMBER_ID = "member_id";

	public static final String TNC_VERSION = "TNC_VERSION";

	// Pending Records Db
	/** The Constant PENDING_RECORDS_TABLE. */
	public static final String PENDING_RECORDS_TABLE = "pending_records_table";

	/** The Constant DEVICE_CONFIG. */
	public static final String DEVICE_CONFIG = "device_config";

	/** The Constant USER_PROFILE. */
	public static final String USER_PROFILE = "user_profile";

	/** The Constant UNSAVED_RECORD. */
	public static final String UNSAVED_RECORD = "UnsavedRecord";

	/** The Constant SHOW_MAINMENU. */
	public static final String SHOW_MAINMENU = "IF_BT_DISCONNECTED";

	// public static final String ACT_START_TIME = "act_start_time";

	/** The Constant TAT_CATEGORY. */
	public static final String TAT_CATEGORY = "tat_category";

	/** The Constant TAT_CHARGES. */
	public static final String TAT_CHARGES = "tat_charges";

	/** The Constant TAT_BALANCE_AMOUNT. */
	public static final String TAT_BALANCE_AMOUNT = "tat_balance_amt";

	// request for result:
	/** The Constant REQUEST_USER_COMMENT. */
	public static final int REQUEST_USER_COMMENT = 1;

	// ecg graph
	// Measurement units
	/** The Constant CMS_PER_INCH. */
	public static final float CMS_PER_INCH = 2.54f;// 5.08f;//2.54f;

	/** The Constant AMPLITUDE_PER_CM. */
	public static final float AMPLITUDE_PER_CM = 328;

	/** The Constant SAMPLES_PER_CM. */
	public static final float SAMPLES_PER_CM = 100; // 100 samples per cm

	// Handler constants
	/** The Constant CONNECTION_ESTABLISHED. */
	public static final int CONNECTION_ESTABLISHED = 1;

	/** The Constant DEVICE_DATA_RECEIVED. */
	public static final int DEVICE_DATA_RECEIVED = 2;

	/** The Constant CONNECTION_ERROR. */
	public static final int CONNECTION_ERROR = 3;

	/** The Constant CONNECTION_LOST_REMOTE. */
	public static final int CONNECTION_LOST_REMOTE = 4;

	/** The Constant REQUEST_RECONNECTION. */
	public static final int REQUEST_RECONNECTION = 5;

	/** The Constant RECONNECTION_FAILED. */
	public static final int RECONNECTION_FAILED = 6;

	/** The Constant RECONNECTION_SUCCESS. */
	public static final int RECONNECTION_SUCCESS = 7;

	/** The Constant CONFIG_ACK_RECEIVED. */
	public static final int CONFIG_ACK_RECEIVED = 8;

	/** The Constant CONNECTED_DEVICE_NOT_FOUND. */
	public static final int CONNECTED_DEVICE_NOT_FOUND = 9;

	/** The Constant CONNECTING_DEVICE. */
	public static final int CONNECTING_DEVICE = 10;

	/** The Constant CONNECTION_TIMEOUT. */
	public static final int CONNECTION_TIMEOUT = 11;

	/** The Constant SHUTDOWN_ACK_RECEIVED. */
	public static final int SHUTDOWN_ACK_RECEIVED = 12;

	/** The Constant ALERT_RESPONSE_OK. */
	public static final int ALERT_RESPONSE_OK = 1;

	/** The Constant ALERT_RESPONSE_CANCEL. */
	public static final int ALERT_RESPONSE_CANCEL = 2;

	/** The Constant FINISH_ALL_ACTIVITIES. */
	public static final String FINISH_ALL_ACTIVITIES = "com.lppbpl.android.userapp.FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION";

	/** The Constant ACTION_MAIN_MENU_. */
	public static final String ACTION_MAIN_MENU_ = "";

	/** The Constant SHOW_DIALOGE_NONE. */
	public final static int SHOW_DIALOGE_NONE = 0;

	/** The Constant SHOW_EXIT_DIALOGE. */
	public final static int SHOW_EXIT_DIALOGE = 100;

	/** The Constant SHOW_ACT_RUNNING_DIALOGE. */
	public final static int SHOW_ACT_RUNNING_DIALOGE = 101;

	/** The Constant SHOW_INFO_DIALOGE. */
	public final static int SHOW_INFO_DIALOGE = 102;

	/** The Constant SHOW_DISCARD_DIALOGE. */
	public final static int SHOW_DISCARD_DIALOGE = 103;

	/** The Constant SHOW_REPEAT_STEP_DIALOGE. */
	public final static int SHOW_REPEAT_STEP_DIALOGE = 104;

	/** The Constant SHOW_RE_MEASURE_DIALOGE. */
	public final static int SHOW_RE_MEASURE_DIALOGE = 105;

	/** The Constant SHOW_CANCEL_DIALOGE. */
	public final static int SHOW_CANCEL_DIALOGE = 106;

	/** The Constant SHOW_CONSULT_DOC_DIALOGE. */
	public final static int SHOW_CONSULT_DOC_DIALOGE = 107;

	/** The Constant SHOW_NOT_SUBSCRIBED_DIALOGE. */
	public final static int SHOW_NOT_SUBSCRIBED_DIALOGE = 108;

	/** The Constant SHOW_PREV_PAIRED_DIALOGE. */
	public final static int SHOW_PREV_PAIRED_DIALOGE = 109;

	/** The Constant SHOW_CATCHED_REC_DIALOGE. */
	public final static int SHOW_CATCHED_REC_DIALOGE = 110;

	/** The Constant SHOW_REGISTRATION_SUCCESS_DIALOG. */
	public final static int SHOW_REGISTRATION_SUCCESS_DIALOG = 111;

	/** The Constant SHOW_REGISTRATION_FAIL_DIALOG. */
	public final static int SHOW_REGISTRATION_FAIL_DIALOG = 112;

	/** The Constant SHOW_UPDATE_DEVICE_ID_DIALOG. */
	public final static int SHOW_UPDATE_DEVICE_ID_DIALOG = 113;

	/** The Constant SHOW_CHANGE_PASSWORD_DIALOG. */
	public final static int SHOW_CHANGE_PASSWORD_DIALOG = 114;

	/** The Constant SHOW_UPDATE_DEVICE_ID_ERROR_DIALOG. */
	public final static int SHOW_UPDATE_DEVICE_ID_ERROR_DIALOG = 115;

	/** The Constant SHOW_SESSION_EXPIRED_DIALOG. */
	public final static int SHOW_SESSION_EXPIRED_DIALOG = 116;

	/** The Constant SHOW_CHANGE_MOBILE_NUMBER_DIALOG. */
	public final static int SHOW_CHANGE_MOBILE_NUMBER_DIALOG = 117;

	/** The Constant SHOW_BG_INVALID_DATA_DIALOG. */
	public final static int SHOW_BG_INVALID_DATA_DIALOG = 118;

	/** The Constant SHOW_LOGIN_SKIP_DIALOG. */
	public final static int SHOW_LOGIN_SKIP_DIALOG = 119;

	/** The Constant SHOW_ACT_START_DIALOGE. */
	public final static int SHOW_ACT_START_DIALOGE = 120;

	/** The Constant SHOW_PASSWORD_RESET_SUCSESS_DIALOG. */
	public final static int SHOW_PASSWORD_RESET_SUCSESS_DIALOG = 121;

	/** The Constant SHOW_PASSWORD_RESET_FAIL_DIALOG. */
	public final static int SHOW_PASSWORD_RESET_FAIL_DIALOG = 122;

	/** The Constant SHOW_SERVER_ADDRESS_CHANGED_DIALOG. */
	public final static int SHOW_SERVER_ADDRESS_CHANGED_DIALOG = 123;

	/** The Constant SHOW_SERVER_ADDRESS_ERROR_DIALOG. */
	public final static int SHOW_SERVER_ADDRESS_ERROR_DIALOG = 124;

	/** The Constant SHOW_BT_PAIR_SKIP_DIALOGE. */
	public final static int SHOW_BT_PAIR_SKIP_DIALOGE = 125;

	/** The Constant SHOW_DELETE_PAIR_DIALOGE. */
	public final static int SHOW_DELETE_PAIR_DIALOGE = 126;

	/** The Constant SHOW_LOGOUT_DIALOGE. */
	public final static int SHOW_LOGOUT_DIALOGE = 127;

	/** The Constant SHOW_SESSION_EXPIRED_DIALOG_1. */
	public final static int SHOW_SESSION_EXPIRED_DIALOG_1 = 128;

	public final static int SHOW_USER_CATAGORY_DIALOG = 129;

	public final static int SHOW_GET_USER_PAYMENT_MODE_FALIURE = 130;

	public final static int SHOW_UPDATE_ADDRESS_DIALOG = 131;

	public final static int SHOW_WALLET_TOPUP_FAIL_DIALOG = 132;

	public final static int SHOW_WALLET_TOPUP_INITIALISE_FAIL_DIALOG = 133;

	public final static int SHOW_MEMBER_TPA_ACCEPTENCE_ERROR_DIALOG = 134;

	public final static int SHOW_RECORD_SAVED_ONLY_DIALOG = 135;

	public final static int SHOW_NETWORK_ERROR_DIALOG = 136;

	public final static int SHOW_WALLET_BALANCE_FAIL_DIALOG = 137;

	public final static int SHOW_GET_USER_PAYMENT_MODE_FALIURE_NO_RETRY = 138;

	/** The Constant MY_DATA. */
	public static final int MY_DATA = 1;

	/** The Constant MY_CASE. */
	public static final int MY_CASE = 2;

	/** The Constant ASSIGNED_CASES. */
	public static final String ASSIGNED_CASES = "ASSIGNED";

	/** The Constant PENDING_CASES. */
	public static final String PENDING_CASES = "PENDING";

	/** The Constant CLOSED_CASES. */
	public static final String CLOSED_CASES = "CLOSED";

	/** The Constant ALL_CASES. */
	public static final String ALL_CASES = "-1";

	/** The Constant ECG. */
	public static final int ECG = 1;

	/** The Constant BG. */
	public static final int BG = 2;

	/** The Constant ALL_TYPE. */
	public static final int ALL_TYPE = -1;

	/** The Constant SAVE_FRESH_APP_STATE. */
	public static final String SAVE_FRESH_APP_STATE = "save_fresh_app_state";

	// constant for  bg and ctivity
	public static final String BG_GLUCOSE="bg_glucose";

	public static final String ACTIVITY_RUNNING="activity_running";

}
