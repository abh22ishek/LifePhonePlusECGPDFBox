package com.lpp.xmldata;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by admin on 25-11-2015.
 */

// this class will take a screen shot of activity display and Blood Glucose


public class CaptureScreen {




    public static void captureScreen(Context context,View v,LinearLayout l,String filename) {

        if(v!=null)
        {
            l.setVisibility(View.INVISIBLE);

            v.setDrawingCacheEnabled(true);
            Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
            v.setDrawingCacheEnabled(false);
            try {
                FileOutputStream fos = new FileOutputStream(new File(Environment
                        .getExternalStorageDirectory().toString(),filename+".jpeg"));
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
              //  Toast.makeText(context,filename+"Screen Saved", Toast.LENGTH_SHORT).show();
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
