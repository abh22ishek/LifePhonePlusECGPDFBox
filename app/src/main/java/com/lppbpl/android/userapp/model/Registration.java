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

package com.lppbpl.android.userapp.model;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class Registration.
 */
public class Registration {

	/** The user id. */
	private String userId;

	/** The password. */
	private String password;

	/** The confirm password. */
	private String confirmPassword;

	/** The first name. */
	private String firstName;

	/** The last name. */
	private String lastName;

	/** The country code. */
	private String countryCode;

	/** The phone number. */
	private String phoneNumber;

	/** The email. */
	private String email;

	/** The date of birth. */
	private String dateOfBirth;

	private boolean male;

	private List<UserCategory> userCategory;

	private String[] userType;

	private int selectionPos;

	private String tncVersion;

	/**
	 * Method getUserId.
	 *
	 * @return String
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * Method setUserId.
	 *
	 * @param userId
	 *            String
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * Method getPassword.
	 *
	 * @return String
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Method setPassword.
	 *
	 * @param password
	 *            String
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Method getConfirmPassword.
	 *
	 * @return String
	 */
	public String getConfirmPassword() {
		return confirmPassword;
	}

	/**
	 * Method setConfirmPassword.
	 *
	 * @param confirmPassword
	 *            String
	 */
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	/**
	 * Method getFirstName.
	 *
	 * @return String
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Method setFirstName.
	 *
	 * @param firstName
	 *            String
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Method getLastName.
	 *
	 * @return String
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Method setLastName.
	 *
	 * @param lastName
	 *            String
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Method getPhoneNumber.
	 *
	 * @return String
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * Method setPhoneNumber.
	 *
	 * @param phoneNumber
	 *            String
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * Method getEmail.
	 *
	 * @return String
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Method setEmail.
	 *
	 * @param email
	 *            String
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Method getDateOfBirth.
	 *
	 * @return String
	 */
	public String getDateOfBirth() {
		return dateOfBirth;
	}

	/**
	 * Method setDateOfBirth.
	 *
	 * @param dateOfBirth
	 *            String
	 */
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	/**
	 * Method getCountryCode.
	 *
	 * @return String
	 */
	public String getCountryCode() {
		return countryCode;
	}

	/**
	 * Method setCountryCode.
	 *
	 * @param countryCode
	 *            String
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public boolean isMale() {
		return male;
	}

	public void setMale(boolean male) {
		this.male = male;
	}

	public List<UserCategory> getUserCategoryList() {
		return userCategory;
	}

	public void setUserCategoryList(List<UserCategory> userCategory) {
		this.userCategory = userCategory;
		if (userCategory != null) {
			final String[] string = new String[userCategory.size()];
			for (int i = 0; i < userCategory.size(); i++) {
				string[i] = userCategory.get(i).getName();
			}
			setUserType(string);
		}
	}

	public String[] getUserType() {
		return userType;
	}

	public void setUserType(String[] userType) {
		this.userType = userType;
	}

	public int getSelectionPos() {
		return selectionPos;
	}

	public void setSelectionPos(int selectionPos) {
		this.selectionPos = selectionPos;
	}

	public String getTncVersion() {
		return tncVersion;
	}

	public void setTncVersion(String tncVersion) {
		this.tncVersion = tncVersion;
	}

	public class UserCategory {
		private int id;
		private int segmentId;
		private int partnerId;
		private String name;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getPartnerId() {
			return partnerId;
		}

		public void setPartnerId(int partnerId) {
			this.partnerId = partnerId;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getSegmentId() {
			return segmentId;
		}

		public void setSegmentId(int segmentId) {
			this.segmentId = segmentId;
		}

	}

}
