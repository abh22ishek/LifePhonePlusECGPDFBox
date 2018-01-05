package com.pdfbox;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.lppbpl.android.userapp.model.ActivityMeasurementModel;
import com.lppbpl.android.userapp.model.BgMeasurementModel;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.font.PDFont;
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font;
import com.tom_roush.pdfbox.pdmodel.graphics.image.LosslessFactory;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by abhishek.raj on 04-01-2018.
 */

public class BGPdfBox {

    public void createBGTable(Context context, boolean isCalibaration, List<BgMeasurementModel> BGList)
    {

        String bloodGlucose="",dateOfTest="",fastingType="",mSymptoms = "",userComments = "";


        for( BgMeasurementModel m: BGList)
        {
            bloodGlucose=m.getBloodglucose();
            dateOfTest=m.getDateOftest();
            fastingType=m.getFasting_type();
            mSymptoms=m.getSymptoms();
            userComments=m.getUsercomments();
        }



        PDDocument document = new PDDocument();
        PDPage page = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));


        String path = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Download/BloodGlucoseTable.pdf";
        document.addPage(page);
        float unit_per_cm = 28.34f;

        float cursorX = 60f;
        float cursorY = unit_per_cm * 8f;
        PDPageContentStream pdPageContentStream ;



        float rect_width = unit_per_cm * 25f;
        float rect_height = unit_per_cm * 12f;


        String bG="Blood Glucose Calibration Report";
        if(!isCalibaration)
            bG="Blood Glucose Value Report";


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
            Bitmap bitmap= BitmapFactory.decodeStream(inputStream);

            PDImageXObject imageXObject= LosslessFactory.createFromImage(document,bitmap);

            pdPageContentStream.drawImage(imageXObject,cursorX+5f,cursorY+5f);// cursor y value updated

            PDFont font = PDType1Font.TIMES_BOLD_ITALIC;
            pdPageContentStream.setNonStrokingColor(0, 0, 0); //black text
            pdPageContentStream.beginText();
            pdPageContentStream.setFont(font, 20);
            pdPageContentStream.newLineAtOffset(5*cursorX,cursorY+10f);
            pdPageContentStream.showText(bG);
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
                    text="Comments : "+userComments;
                }else if(i==1)
                {
                    text="Symptoms : "+mSymptoms;
                }else if(i==2){
                    text="Measurement Time : "+dateOfTest;

                }

                else if(i==3){
                    text="Fasting Type : "+fastingType;
                }else if(i==4)
                {
                    text="Blood Glucose Value : "+bloodGlucose;
                }else if(i==5)
                {
                    text="Patient Height : ";
                }else if(i==6){
                    text="Gender";
                }else if(i==7){
                    text="Patient Weight : ";
                }
                else if(i==8){
                    text="Patient Age : ";
                }
                else if(i==9){
                    text="Patient Name : ";
                }


                pdPageContentStream.beginText();
                pdPageContentStream.setFont(font, 11);
                pdPageContentStream.newLineAtOffset(cursorX+5f,cursorY+10f);
                pdPageContentStream.showText(text);
                pdPageContentStream.endText();
                cursorY=cursorY+unit_per_cm;


            }


            pdPageContentStream.close();
            document.save(path);
            document.close();




        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
