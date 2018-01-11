package com.lppbpl.android.userapp.model;

/**
 * Created by admin on 13-11-2015.
 */
public class ActivityMeasurementModel {

    String totalstepstaken;
    String totalmetrestravelled;
    String totalcaloriesburnt;
    String usercomments;
    String startEndtime;

    String userName;
    String height;

    String patientId;
    String clinicName;

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

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

    String weight;



    public String getTotalmetrestravelled() {
        return totalmetrestravelled;
    }

    public void setTotalmetrestravelled(String totalmetrestravelled) {
        this.totalmetrestravelled = totalmetrestravelled;
    }

    public String getTotalstepstaken() {
        return totalstepstaken;
    }

    public void setTotalstepstaken(String totalstepstaken) {
        this.totalstepstaken = totalstepstaken;
    }

    public String getTotalcaloriesburnt() {
        return totalcaloriesburnt;
    }

    public void setTotalcaloriesburnt(String totalcaloriesburnt) {
        this.totalcaloriesburnt = totalcaloriesburnt;
    }

    public String getUsercomments() {
        return usercomments;
    }

    public void setUsercomments(String usercomments) {
        this.usercomments = usercomments;
    }

    public String getStartEndtime() {
        return startEndtime;
    }

    public void setStartEndtime(String startEndtime) {
        this.startEndtime = startEndtime;
    }
}
