package com.lppbpl.android.userapp.model;

public class TATCategoryModel {

	private int id;
	private String name;
	private int turnAroundTime;
	private int secondTurnAroundTime;
	private int thirdTurnAroundTime;
	private int amount;
	private int data_Subscription_Charge;
	private int gp_Subscription_Charge;

	public void setId(int value){
		id = value;
	}

	public int getId(){
		return id;
	}

	public void setName(String value){
		name = value;
	}

	public String getName(){
		return name;
	}

	public void setTurnAroundTime(int value){
		turnAroundTime = value;
	}

	public int getTurnAroundTime(){
		return turnAroundTime;
	}

	public void setSecondTurnAroundTime(int value){
		secondTurnAroundTime = value;
	}

	public int getSecondTurnAroundTime(){
		return secondTurnAroundTime;
	}

	public void setThirdTurnAroundTime(int value){
		thirdTurnAroundTime = value;
	}

	public int getThirdTurnAroundTime(){
		return thirdTurnAroundTime;
	}

	public void setAmount(int value){
		amount = value;
	}

	public int getAmount(){
		return amount;
	}

	public void setDataSubscriptionCharge(int value){
		data_Subscription_Charge = value;
	}

	public int getDataSubscriptionCharge(){
		return data_Subscription_Charge;
	}

	public void setGpSubscriptionCharge(int value){
		gp_Subscription_Charge = value;
	}

	public int getGpSubscriptionCharge(){
		return gp_Subscription_Charge;
	}
}
