package com.lppbpl.android.userapp.model;

import java.util.ArrayList;

public class TPAInfoModel {

	/*
	 * {
			"current_version":"0.0.1",
			"accepted_version":"0.0.1",
			"provideAdvisory":1,
			"tatCategoryList":[
				{"id":0,
				"name":"mConsult",
				"turnAroundTime":5,
				"secondTurnAroundTime":13,
				"thirdTurnAroundTime":21,
				"amount":250,
				"dataSubscriptionCharge":25,
				"gpSubscriptionCharge":25,
				"tpaUrl":null,
				"checked":false
				}
			]
		}
	 */

	private String current_version;
	private String accepted_version;
	private boolean provideAdvisory;
//	private ArrayList<TATCategoryModel> tatCategoryList;

	public void setCurrentVersion(String value){
		current_version = value;
	}

	public String getCurrentVersion(){
		return current_version;
	}

	public void setAcceptedVersion(String value){
		accepted_version = value;
	}

	public String getAcceptedVersion(){
		return accepted_version;
	}

	public void setProvideAdvisory(int value){
		if(value == 0){
			provideAdvisory = false;
		} else{
			provideAdvisory = true;
		}
	}

	public boolean canProvideAdvisory(){
		return provideAdvisory;
	}

//	public void setCategoryList(ArrayList<TATCategoryModel> value){
//		tatCategoryList = value;
//	}
//
//	public ArrayList<TATCategoryModel> getCategoryList(){
//		return tatCategoryList;
//	}
}
