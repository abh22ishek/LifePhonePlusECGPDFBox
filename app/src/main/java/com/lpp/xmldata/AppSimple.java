package com.lpp.xmldata;

/**
 * Created by admin on 18-11-2015.
 */
public class AppSimple {
    private static AppSimple ourInstance = new AppSimple();

    public static AppSimple getInstance() {
        return ourInstance;
    }

    private AppSimple() {
    }
}
