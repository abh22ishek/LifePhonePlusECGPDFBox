package com.lppbpl.android.userapp.listener;

import com.lppbpl.EcgMultipleLead;

import java.util.Vector;

/**
 * Created by abhishek.raj on 29-12-2017.
 */

public interface EcgLLeadsListner {

    public  void ecgDataListner(Vector<EcgMultipleLead> vector);
}
