package com.lppbpl.android.userapp.model;

public class BgMeasurementModel {

	String Bloodglucose;
	String dateOftest;
	String Usercomments;

	String symptoms;
	String fasting_type;

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



	public BgMeasurementModel(String bloodglucose, String dateOftest, String usercomments,String fasting_type) {
		super();
		Bloodglucose = bloodglucose;
		this.dateOftest = dateOftest;
		Usercomments = usercomments;
		this.fasting_type=fasting_type;
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
