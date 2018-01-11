package com.pdfbox;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfDocument;

import com.lpp.xmldata.ConvertTexttoXml;
import com.lppbpl.android.userapp.EcgGraphActivity;
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

/**
 * Created by abhishek.raj on 28-12-2017.
 */

public class EcgPdfBox {


    public  String leadArr[]={"MCL6","MCL5","MCL4","MCL3","MCL2","MCL1","aVF","aVL","aVR","III","II","I"};

    String appVersion;



    public  void createEcgTable(Context context,String clinicName,String patientName,String gender,String patientId,String symptoms)
    {


        PDDocument document = new PDDocument();
        PDPage page = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));


        float page_height=page.getMediaBox().getHeight();
        float page_width=page.getMediaBox().getWidth();


        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            appVersion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String path = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/Download/EcgPdfBox.pdf";
        document.addPage(page);

        float cursorX =60f;
        float cursorY = 20f;
        PDPageContentStream contentStream = null;


        float unit_per_cm=28.34f;
        float rect_width=unit_per_cm*25f;
        float rect_height=unit_per_cm*18f;

        float marginLowerLine=rect_height+cursorY+5;
        float marginUpperLine=rect_height+2*unit_per_cm;
        float rectangle_X=rect_width+60;
        float rectangle_Y=marginUpperLine;

        try {
            contentStream = new PDPageContentStream(document,page);
            PDFont font = PDType1Font.HELVETICA_BOLD;
            //contentStream.setNonStrokingColor(200, 200, 200); //gray background

            contentStream.addRect(40f,16f,rectangle_X,marginUpperLine);

            contentStream.setStrokingColor(AWTColor.BLACK);
            contentStream.setLineWidth(1.4f);

            contentStream.moveTo(40f,marginLowerLine);
            contentStream.lineTo(rectangle_X+40,marginLowerLine);
            contentStream.stroke();

            //draw text
            contentStream.setNonStrokingColor(0, 0, 0); //black text
            contentStream.beginText();
            contentStream.setFont(font,11);
            contentStream.newLineAtOffset(120f,marginUpperLine+2);
            contentStream.showText("Patient Id : "+patientId +"  "+"Patient Name : "+patientName+"   "+"Gender : "+
                    gender+"  "+"Clinic Name : "+clinicName);

            contentStream.endText();

            contentStream.setNonStrokingColor(0, 0, 0); //black text
            contentStream.beginText();
            contentStream.setFont(font,11);
            contentStream.newLineAtOffset(120f,marginUpperLine-12);
            contentStream.showText("Symptoms :"+symptoms);

            contentStream.endText();

            contentStream.setNonStrokingColor(0, 0, 0); //black text
            contentStream.beginText();
            contentStream.setFont(font,10);
            contentStream.newLineAtOffset(120f,marginUpperLine-24);
            contentStream.showText("Comments : "+ EcgGraphActivity.mEditTxt.getText().toString());
            contentStream.endText();

            contentStream.setNonStrokingColor(0, 0, 0); //black text
            contentStream.beginText();
            contentStream.setFont(font, 10);
            contentStream.newLineAtOffset(2*cursorX,cursorY+5);
            contentStream.showText("10mm/mV    25mm/s");
            contentStream.endText();

            // Add app version
            contentStream.setNonStrokingColor(0, 0, 0); //black text
            contentStream.beginText();
            contentStream.setFont(font, 8);
            contentStream.newLineAtOffset(page_width-200,marginUpperLine-12);
            contentStream.showText("App Version : "+appVersion);
            contentStream.endText();


            contentStream.setNonStrokingColor(0, 0, 0); //black text
            contentStream.beginText();
            contentStream.setFont(font, 10);
            contentStream.newLineAtOffset(5*cursorX-10,cursorY+5);
            contentStream.showText("0.3Hz  to  25Hz");
            contentStream.endText();







            // add image

            AssetManager assetManager = context.getAssets();
            InputStream is = null;
            InputStream alpha;
            is =  assetManager.open("bpl.png");
            //alpha =  assetManager.open("bpl.png");

           // Bitmap b= BitmapFactory.decodeStream(alpha);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            PDImageXObject ximage = LosslessFactory.createFromImage(document,bitmap);
          //  PDImageXObject yimage=LosslessFactory.createFromImage(document,b);



            contentStream.drawImage(ximage,40+4,marginLowerLine+2);
            //contentStream.drawImage(yimage,rect_width+cursorX-20,marginLowerLine);



            contentStream.moveTo(100f,marginLowerLine+2);
            contentStream.lineTo(100f,marginUpperLine+15);
            contentStream.stroke();


            contentStream.moveTo(rect_width+cursorX-20,marginLowerLine);
            contentStream.lineTo(rect_width+cursorX-20,marginUpperLine+15);
            contentStream.stroke();






            contentStream.setLineWidth(1.5f);
            // vertical grids
            contentStream.setStrokingColor(AWTColor.PINK);

            for(int i=0;i<26;i++)
            {
                contentStream.moveTo(cursorX,cursorY);
                contentStream.lineTo(cursorX,rect_height+cursorY);
                contentStream.stroke();
                cursorX=cursorX+unit_per_cm;

            }



            // Horizontal grids

            cursorX=60;
            cursorY=20;

            for(int i=0;i<19;i++)
            {
                contentStream.moveTo(cursorX,cursorY);
                contentStream.lineTo(cursorX+rect_width,cursorY);
                contentStream.stroke();
                cursorY=cursorY+unit_per_cm;
            }


            cursorX=60;
            cursorY=20;
            contentStream.setLineWidth(0.2f);

            contentStream.setStrokingColor(AWTColor.pink);
            // draw minor vertical grids
            for(int i=0;i<250;i++)
            {
                contentStream.moveTo(cursorX,cursorY);
                contentStream.lineTo(cursorX,rect_height+cursorY);

                contentStream.stroke();
                cursorX=cursorX+unit_per_cm/10f;

            }


            // draw minor horizontal grids

            cursorX=60;
            cursorY=20;


            for(int i=0;i<180;i++)
            {
                contentStream.moveTo(cursorX,cursorY);
                contentStream.lineTo(cursorX+rect_width,cursorY);
                contentStream.stroke();
                cursorY=cursorY+unit_per_cm/10f;
            }





            // Find out the pixel density
            final float widthScale =unit_per_cm / (float)100; // 100 ... 0.28
            // samples per cm
            final float heightScale = (float)328 /unit_per_cm; // pixels  328/63=5.2

            //float mX=56.69f;
            //float mP1=0f;


            //-------------


            float mP1 = 10f, mPp1 = 10f;

            float mP2 = 10f, mPp2 = 10f;

            float mP3 = 10f, mPp3 = 10f;

            float mP4 = 10f, mPp4 = 10f;

            float mP5 = 10f, mPp5 = 10f;

            float mP6 = 10f, mPp6 = 10f;

            float mP7 = 10f, mPp7 = 10f;

            float mP8 = 10f, mPp8 = 10f;

            float mP9 = 10f, mPp9 = 10f;

            float mP10 = 10f, mPp10 = 10f;

            float mP11 = 10f, mPp11 = 10f;

            float mP12 = 10f, mPp12 = 10f;

            float mX=60f;
            mP1 = mP2 = mP3 = 0;
            mP4 = mP5 = mP6 = mP7 = mP8 = mP9 = mP10 = mP11 = mP12 = 0;


            float graphHeight=42.51f;

            float mPx1 = mX;
            mPp1 = 1 * graphHeight;   // 126
            float mPx2 = mX;
            mPp2 = 2 * graphHeight; // graphHeight * 2;   315
            float mPx3 = mX;
            mPp3 = 3 * graphHeight; // graphHeight * 3;   504
            float mPx4 = mX;
            mPp4 = 4 * graphHeight; // graphHeight * 3;  693
            float mPx5 =mX;
            mPp5 = 5 * graphHeight; // graphHeight * 3;
            float mPx6 =mX;
            mPp6 = 6 * graphHeight; // graphHeight * 3;
            float mPx7 = mX;
            mPp7 = 7* graphHeight; // graphHeight * 3;
            float mPx8 = mX;
            mPp8 = 8 * graphHeight; // graphHeight * 3;
            float mPx9 = mX;
            mPp9 = 9 * graphHeight; // graphHeight * 3;
            float mPx10 = mX;
            mPp10 = 10 * graphHeight; // graphHeight * 3;
            float mPx11 = mX;
            mPp11 = 11* graphHeight; // graphHeight * 3;



            float mPx12 = mX;
            mPp12 = 12 * graphHeight; // graphHeight * 3;

            int noOfLeads =12;

            final int leadSampleCount = 2500;
            contentStream.setLineWidth(1.0f);


            int count=0;

            for(int i=0;i<2500;i++)
            {
                if (noOfLeads >= 12) {
                    mP1 = (1 *graphHeight)+( ConvertTexttoXml.Ecglead12.get(i)/ heightScale);
                    contentStream.setStrokingColor(AWTColor.black);
                    contentStream.moveTo(mPx1, mPp1);
                    contentStream.lineTo(mX,mP1);
                    contentStream.stroke();

                    mPx1=mX;
                    mPp1=mP1;

                }


                if (noOfLeads >= 11) {
                    mP2 = (2 *graphHeight)+( ConvertTexttoXml.Ecglead11.get(i)/ heightScale);
                    contentStream.setStrokingColor(AWTColor.black);
                    contentStream.moveTo(mPx2, mPp2);
                    contentStream.lineTo(mX,mP2);
                    contentStream.stroke();

                    mPx2=mX;
                    mPp2=mP2;

                }



                if (noOfLeads >= 10) {
                    mP3 = (3 *graphHeight)+( ConvertTexttoXml.Ecglead10.get(i) / heightScale);
                    contentStream.setStrokingColor(AWTColor.black);
                    contentStream.moveTo(mPx3, mPp3);
                    contentStream.lineTo(mX,mP3);
                    contentStream.stroke();

                    mPx3=mX;
                    mPp3=mP3;

                }


                if (noOfLeads >= 9) {
                    mP4 = (4 *graphHeight)+ ( ConvertTexttoXml.Ecglead9.get(i) / heightScale);
                    contentStream.setStrokingColor(AWTColor.black);
                    contentStream.moveTo(mPx4, mPp4);
                    contentStream.lineTo(mX,mP4);
                    contentStream.stroke();

                    mPx4=mX;
                    mPp4=mP4;

                }


                if (noOfLeads >= 8) {
                    mP5 = (5 *graphHeight)+( ConvertTexttoXml.Ecglead8.get(i) / heightScale);
                    contentStream.setStrokingColor(AWTColor.black);
                    contentStream.moveTo(mPx5, mPp5);
                    contentStream.lineTo(mX,mP5);
                    contentStream.stroke();

                    mPx5=mX;
                    mPp5=mP5;

                }

                if (noOfLeads >= 7) {
                    mP6 = (6 *graphHeight)+( ConvertTexttoXml.Ecglead7.get(i) / heightScale);
                    contentStream.setStrokingColor(AWTColor.black);
                    contentStream.moveTo(mPx6, mPp6);
                    contentStream.lineTo(mX,mP6);
                    contentStream.stroke();

                    mPx6=mX;
                    mPp6=mP6;

                }
                if (noOfLeads >= 6) {
                    mP7 = (7 *graphHeight)+( ConvertTexttoXml.Ecglead6.get(i)/ heightScale);
                    contentStream.setStrokingColor(AWTColor.black);
                    contentStream.moveTo(mPx7, mPp7);
                    contentStream.lineTo(mX,mP7);
                    contentStream.stroke();

                    mPx7=mX;
                    mPp7=mP7;

                }
                if (noOfLeads >= 5) {
                    mP8 = (8 *graphHeight)+( ConvertTexttoXml.Ecglead5.get(i)/ heightScale);
                    contentStream.setStrokingColor(AWTColor.black);
                    contentStream.moveTo(mPx8, mPp8);
                    contentStream.lineTo(mX,mP8);
                    contentStream.stroke();

                    mPx8=mX;
                    mPp8=mP8;

                }
                if (noOfLeads >= 4) {
                    mP9 = (9 *graphHeight)+ ( ConvertTexttoXml.Ecglead4.get(i) / heightScale);
                    contentStream.setStrokingColor(AWTColor.black);
                    contentStream.moveTo(mPx9, mPp9);
                    contentStream.lineTo(mX,mP9);
                    contentStream.stroke();

                    mPx9=mX;
                    mPp9=mP9;

                }
                if (noOfLeads >= 3) {
                    mP10 = (10 *graphHeight)+(ConvertTexttoXml.Ecglead3.get(i) / heightScale);
                    contentStream.setStrokingColor(AWTColor.black);
                    contentStream.moveTo(mPx10, mPp10);
                    contentStream.lineTo(mX,mP10);
                    contentStream.stroke();

                    mPx10=mX;
                    mPp10=mP10;

                }

                if (noOfLeads >= 2) {
                    mP11 = (11 *graphHeight)+( ConvertTexttoXml.Ecglead2.get(i) / heightScale);
                    contentStream.setStrokingColor(AWTColor.black);
                    contentStream.moveTo(mPx11, mPp11);
                    contentStream.lineTo(mX,mP11);
                    contentStream.stroke();

                    mPx11=mX;
                    mPp11=mP11;

                }

                if (noOfLeads >= 1) {
                    mP12 = (12 *graphHeight)+( ConvertTexttoXml.Ecglead1.get(i) / heightScale);
                    contentStream.setStrokingColor(AWTColor.black);
                    contentStream.moveTo(mPx12, mPp12);
                    contentStream.lineTo(mX,mP12);
                    contentStream.stroke();

                    mPx12=mX;
                    mPp12=mP12;

                }

                mX=mX+widthScale;
                System.out.println("count mx="+count++  +"mX value="+mX);




            }


            cursorX=60;
            for(int x=1;x<=leadArr.length;x++)
            {
                contentStream.setNonStrokingColor(0, 0, 0); //black text
                contentStream.beginText();
                contentStream.setFont(font,10);
                contentStream.moveTextPositionByAmount(cursorX,(x*graphHeight)+4f);
                contentStream.drawString(leadArr[x-1]);
                contentStream.endText();
            }

            contentStream.close();

            // Add Second Page


            createSecondPage(context,document,page_height);


            document.save(path);
            document.close();




        } catch (IOException e) {
            e.printStackTrace();

        }


    }


    public void createSecondPage(Context context, PDDocument document,float page_height)
    {
        PDPage page = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));// landscape mode

        float cursorX =10f;
        float cursorY = 10f;
        PDPageContentStream pdPageContentStream2 = null;

        float offsetX=60f;

        float unit_per_cm=28.34f;
        float rect_width=unit_per_cm*25f;
        float rect_height=unit_per_cm*20f;


        try {
            pdPageContentStream2 = new PDPageContentStream(document,page);
            document.addPage(page);
            PDFont font = PDType1Font.HELVETICA_BOLD;
            pdPageContentStream2.setNonStrokingColor(200, 200, 200); //gray background
            pdPageContentStream2.addRect(cursorX, cursorY,rect_width, rect_height);



            // draw image

            AssetManager assetManager = context.getAssets();
            InputStream inputStream1 ;
            inputStream1 =  assetManager.open("bpl.png");

            Bitmap bitmap= BitmapFactory.decodeStream(inputStream1);
            PDImageXObject ximage = LosslessFactory.createFromImage(document,bitmap);
            pdPageContentStream2.drawImage(ximage,offsetX,page_height-140);

            //draw text
            pdPageContentStream2.setNonStrokingColor(200, 200, 200); //black text
            pdPageContentStream2.beginText();
            pdPageContentStream2.setFont(font,40);
            pdPageContentStream2.newLineAtOffset(150f,page_height-130);
            pdPageContentStream2.showText("Notes to the Consulting Physician");
            pdPageContentStream2.endText();

            font = PDType1Font.HELVETICA_BOLD;

            pdPageContentStream2.setNonStrokingColor(0, 0, 0); //black text
            pdPageContentStream2.beginText();
            pdPageContentStream2.setFont(font,20f);
            pdPageContentStream2.newLineAtOffset(offsetX,page_height-170);
            pdPageContentStream2.showText("* The 12 ECG report was generated from ECG obtained in 4 sequential steps");
            pdPageContentStream2.endText();


            AssetManager assetManager2 = context.getAssets();
            InputStream inputStream ;
            inputStream =  assetManager2.open("ecg_steps.png");

            Bitmap bitmap2= BitmapFactory.decodeStream(inputStream);
            PDImageXObject ximage2 = LosslessFactory.createFromImage(document,bitmap2);


           pdPageContentStream2.drawImage(ximage2,offsetX,page_height-340);

            font = PDType1Font.HELVETICA_BOLD;
            //draw text
            pdPageContentStream2.setNonStrokingColor(0, 0, 0); //black text
            pdPageContentStream2.beginText();
            pdPageContentStream2.setFont(font,22f);
            pdPageContentStream2.newLineAtOffset(offsetX,190f);
            pdPageContentStream2.showText("*Lead I,II,III,aVR,aVL,aVF were simultaneously acquired in Step 1");
            pdPageContentStream2.endText();

            pdPageContentStream2.setNonStrokingColor(0, 0, 0); //black text
            pdPageContentStream2.beginText();
            pdPageContentStream2.setFont(font,22f);
            pdPageContentStream2.newLineAtOffset(offsetX,160f);
            pdPageContentStream2.showText("*Leads MCL1,MCL2 were simultaneously acquired in Step 2");

            pdPageContentStream2.endText();

            pdPageContentStream2.setNonStrokingColor(0, 0, 0); //black text
            pdPageContentStream2.beginText();
            pdPageContentStream2.setFont(font,22f);
            pdPageContentStream2.newLineAtOffset(offsetX,130f);
            pdPageContentStream2.showText("*Leads MCL3,MCL4 were simultaneously acquired in Step 3");
            pdPageContentStream2.endText();


            pdPageContentStream2.setNonStrokingColor(0, 0, 0); //black text
            pdPageContentStream2.beginText();
            pdPageContentStream2.setFont(font,22f);
            pdPageContentStream2.newLineAtOffset(offsetX,100f);
            pdPageContentStream2.showText("*Leads MCL5,MCL6 were simultaneously acquired in Step 4 ");
            pdPageContentStream2.endText();



            font = PDType1Font.HELVETICA_BOLD;
            pdPageContentStream2.setNonStrokingColor(0, 0, 0); //black text
            pdPageContentStream2.beginText();
            pdPageContentStream2.setFont(font,17f);
            pdPageContentStream2.newLineAtOffset(offsetX,40f);
            pdPageContentStream2.showText("* This sequential ECG report does not replace the 12-lead resting ECG disclosure from a" );
            //"resting ECG disclosure from a diagnostic electrocardiograph." +"\n"+"The pacemaker spike positions are not indicated");
            pdPageContentStream2.endText();
            pdPageContentStream2.setNonStrokingColor(0, 0, 0); //black text
            pdPageContentStream2.beginText();
            pdPageContentStream2.setFont(font,17f);
            pdPageContentStream2.newLineAtOffset(offsetX,15f);
            pdPageContentStream2.showText("diagnostic electrocardiograph.The pacemaker spike positions are not indicated" );
            //"resting ECG disclosure from a diagnostic electrocardiograph." +"\n"+"The pacemaker spike positions are not indicated");
            pdPageContentStream2.endText();
            pdPageContentStream2.close();


        } catch (IOException e) {
            e.printStackTrace();

        }


    }

}