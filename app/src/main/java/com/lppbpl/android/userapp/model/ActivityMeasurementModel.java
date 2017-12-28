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
