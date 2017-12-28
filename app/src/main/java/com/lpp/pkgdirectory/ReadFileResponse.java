package com.lpp.pkgdirectory;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.lppbpl.android.userapp.MainMenuActivity;


/**
 * Created by admin on 01-12-2015.
 */
 public class ReadFileResponse extends AsyncTask<String,Integer,String>
{
    ProgressDialog p;
    String result;
    int count;

    Context context;

    public ReadFileResponse(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        count=0;
        p=new ProgressDialog(context);
        p.setIndeterminate(false);
        p.setCancelable(true);
        p.setTitle("Reading a file and creating a pdf");
        p.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        p.show();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(p!=null)
        {
            p.dismiss();
            final Intent intent=new Intent(context,MainMenuActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
    }



    @Override
    protected String doInBackground(String [] params) {



        try {


            FileReadData.readfile(params[0],params[1],params[2]);
            result = "done";
        } catch (Exception e) {
            e.printStackTrace();

        }

        return result;
    }

}
