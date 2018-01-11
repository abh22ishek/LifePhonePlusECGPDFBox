package com.lppbpl.android.userapp.model;

public class BgMeasurementModel {

	String Bloodglucose;
	String dateOftest;
	String Usercomments;

	String symptoms;
	String fasting_type;

	String userName;
	String patientId;
	String clinicName;

	String height;
	String weight;

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getClinicName() {
		return clinicName;
	}

	public BgMeasurementModel(String bloodglucose, String dateOftest, String usercomments, String symptoms, String fasting_type,
							  String userName, String patientId, String clinicName, String height, String weight) {
		Bloodglucose = bloodglucose;
		this.dateOftest = dateOftest;
		Usercomments = usercomments;
		this.symptoms = symptoms;
		this.fasting_type = fasting_type;
		this.userName = userName;
		this.patientId = patientId;
		this.clinicName = clinicName;
		this.height = height;
		this.weight = weight;
	}

	public void setClinicName(String clinicName) {
		this.clinicName = clinicName;

	}

	public String getFasting_type() {
		return fasting_type;
	}

	public void setFasting_type(String fasting_type) {
		this.fasting_type = fasting_type;
	}

	public String getSymptoms() {
		return symptoms;
	}

	public void setSymptoms(String symptoms) {
		this.symptoms = symptoms;
	}




	public String getBloodglucose() {
		return Bloodglucose;
	}
	public void setBloodglucose(String bloodglucose) {
		Bloodglucose = bloodglucose;
	}
	public String getDateOftest() {
		return dateOftest;
	}
	public void setDateOftest(String dateOftest) {
		this.dateOftest = dateOftest;
	}
	public String getUsercomments() {
		return Usercomments;
	}
	public void setUsercomments(String usercomments) {
		Usercomments = usercomments;
	}
	
	
}
