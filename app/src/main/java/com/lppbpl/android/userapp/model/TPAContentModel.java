package com.lppbpl.android.userapp.model;

import java.util.ArrayList;

public class TPAContentModel {
	/*
	 * {
"version":”1.0.0”,
“content”: <TPA_TEXT>,
“content_path”: <TPA_FILE_LOCATION>,
"tatCategoryList":[
               {"id":0,
               "name":"mConsult",
               "turnAroundTime":5,
               "secondTurnAroundTime":13,
               "thirdTurnAroundTime":21,
               "amount":400,
               "data_Subscription_Charge":100,
               "gp_Subscription_Charge":200,
               "tpaUrl": null,
               "checked":false}
],
"created":null
}

	 */
	private String version;
	private String content;
	private String content_path;
	private ArrayList<TATCategoryModel> tatCategoryList;

	public void setVersion(String value){
		version = value;
	}

	public String getVersion(){
		return version;
	}

	public void setContent(String value){
		content = value;
	}

	public String getContent(){
		return content;
	}

	public void setContentPath(String value){
		content_path = value;
	}

	public String getContentPath(){
		return content_path;
	}

	public void setCategoryList(ArrayList<TATCategoryModel> value){
		tatCategoryList = value;
	}

	public ArrayList<TATCategoryModel> getCategoryList(){
		return tatCategoryList;
	}
}
