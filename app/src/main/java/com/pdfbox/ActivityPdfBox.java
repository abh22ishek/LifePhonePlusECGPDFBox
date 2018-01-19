package com.pdfbox;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.lpp.xmldata.ConvertTexttoXml;
import com.lppbpl.android.userapp.ActivityFinalDisplay;
import com.lppbpl.android.userapp.model.ActivityMeasurementModel;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.font.PDFont;
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font;
import com.tom_roush.pdfbox.pdmodel.graphics.image.LosslessFactory;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject;
import com.tom_roush.pdfbox.util.awt.AWTColor;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by abhishek.raj on 29-12-2017.
 */

public class ActivityPdfBox {

    String totalStepsTaken;
    String totalMetresTravelled;
    String totalCaloriesBurnt;
    String userCmments;
    String startEndTime;

    String userName;
    String mHeight;
    String mWeight;
    String appVersion;

    String patientId;
    String clinicName;
    String age;

    String gender;


    public  void createActivityPdfTable(Context context,List<ActivityMeasurementModel>
            activityMeasurementModelList) {

        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            appVersion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }



        for( ActivityMeasurementModel m: activityMeasurementModelList)
       {
          totalStepsTaken= m.getTotalstepstaken();
          totalCaloriesBurnt= m.getTotalcaloriesburnt();
          startEndTime= m.getStartEndtime();
          totalMetresTravelled= m.getTotalmetrestravelled();
           userCmments=m.getUsercomments();

           userName=m.getUserName();
           mHeight=m.getHeight();
           mWeight=m.getWeight();
           clinicName=m.getClinicName();
           patientId=m.getPatientId();
           age=m.getAge();
           gender=m.getGender();
       }

        PDDocument document = new PDDocument();
        PDPage page = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));

        float f = 0;
        System.out.println("float increment=" + f++);

        String path = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Download/ActivityTable.pdf";
        document.addPage(page);
        float unit_per_cm = 28.34f;

        float cursorX = 60f;
        float cursorY = unit_per_cm * 8f;
        PDPageContentStream pdPageContentStream = null;



        float rect_width = unit_per_cm * 25f;
        float rect_height = unit_per_cm * 12f;

        float marginTop=40f;


        try {
            pdPageContentStream = new PDPageContentStream(document,page);

            pdPageContentStream.setNonStrokingColor(0, 0, 0); //gray background
            pdPageContentStream.addRect(cursorX, cursorY,rect_width, rect_height);
            pdPageContentStream.stroke();



            // horizontal grids

            for(int i=0;i<10;i++)
            {
                pdPageContentStream.moveTo(cursorX,cursorY+unit_per_cm);
                pdPageContentStream.lineTo(rect_width+cursorX,cursorY+unit_per_cm);// it is taking coordinate from page start
                pdPageContentStream.stroke();
                cursorY=cursorY+unit_per_cm;


            }


            // Add Bpl logo
            AssetManager assetManager = context.getAssets();
            InputStream inputStream;
            inputStream =  assetManager.open("bpl.png");
            Bitmap bitmap=BitmapFactory.decodeStream(inputStream);
            PDImageXObject imageXObject=LosslessFactory.createFromImage(document,bitmap);
            pdPageContentStream.drawImage(imageXObject,cursorX+5f,cursorY+5f);// cursor y value updated

            PDFont font = PDType1Font.TIMES_BOLD_ITALIC;
            pdPageContentStream.setNonStrokingColor(0, 0, 0); //black text
            pdPageContentStream.beginText();
            pdPageContentStream.setFont(font, 20);
            pdPageContentStream.newLineAtOffset(5*cursorX,cursorY+10f);
            pdPageContentStream.showText("Activity Profile Report");
            pdPageContentStream.endText();


            // add vertical line for logo
            pdPageContentStream.moveTo(2*cursorX,cursorY);
            pdPageContentStream.lineTo(2*cursorX,cursorY+(2*unit_per_cm));// it is taking coordinate from page start
            pdPageContentStream.stroke();


            // Add Patient Name ,Gender, Height, Weight

            font = PDType1Font.TIMES_ROMAN;

            cursorX=60f;
            cursorY=unit_per_cm*8f;

            String text="";
            for(int i=0;i<10;i++)
            {
                if(i==0){
                    text="Comments : "+userCmments;
                }else if(i==1)
                {
                    text="No. of meters Travelled : "+totalMetresTravelled;
                }else if(i==2){
                    text="No. of calories burnt : "+totalCaloriesBurnt;

                }

                else if(i==3){
                    text="No. of steps walked : "+totalStepsTaken;
                }else if(i==4)
                {
                    text="Duration";
                }else if(i==5)
                {
                    text="Activity Measurement Time : "+startEndTime;
                }else if(i==6){
                    text="Gender : " +gender;
                }else if(i==7){
                    text="Patient Weight : "+mWeight+"Kg";

                    pdPageContentStream.beginText();
                    pdPageContentStream.setFont(font, 11);
                    pdPageContentStream.newLineAtOffset(cursorX+200f,cursorY+10f);
                    pdPageContentStream.showText("Height : "+mHeight+"CM");
                    pdPageContentStream.endText();

                }
                else if(i==8){
                    text="Patient Id : "+patientId;

                    pdPageContentStream.beginText();
                    pdPageContentStream.setFont(font, 11);
                    pdPageContentStream.newLineAtOffset(cursorX+200f,cursorY+10f);
                    pdPageContentStream.showText("Age : "+age);
                    pdPageContentStream.endText();
                }
                else if(i==9){
                    text="Patient Name : "+userName;
                }


                pdPageContentStream.beginText();
                pdPageContentStream.setFont(font, 11);
                pdPageContentStream.newLineAtOffset(cursorX+5f,cursorY+10f);
                pdPageContentStream.showText(text);
                pdPageContentStream.endText();
                cursorY=cursorY+unit_per_cm;


                // Add App Version


              /*  pdPageContentStream.beginText();
                pdPageContentStream.setFont(font, 11);
                pdPageContentStream.newLineAtOffset(10f,600f);
                pdPageContentStream.showText("App Version : "+appVersion);
                pdPageContentStream.endText();*/

            }


            pdPageContentStream.close();
            document.save(path);
            document.close();




        } catch (IOException e) {
            e.printStackTrace();
        }









    }

}
