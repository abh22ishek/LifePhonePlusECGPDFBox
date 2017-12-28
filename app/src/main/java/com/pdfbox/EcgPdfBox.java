package com.pdfbox;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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



    public  void createtable(Context context)
    {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));


        float page_height=page.getMediaBox().getHeight();
        float page_width=page.getMediaBox().getWidth();


        System.out.println(page.getMediaBox().getHeight());
        System.out.println(page.getMediaBox().getWidth());

        float f=0;
        System.out.println("float icrement="+f++);

        String path = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/EcgPdfBox.pdf";
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
            contentStream.showText("UHID:2134325421" +" "+"Patient Name:Abhishek"+"  "+"Age:27"+
                    " Gender:Male"+" Clinid Id:Apollo");
            contentStream.endText();

            contentStream.setNonStrokingColor(0, 0, 0); //black text
            contentStream.beginText();
            contentStream.setFont(font,11);
            contentStream.newLineAtOffset(120f,marginUpperLine-12);
            contentStream.showText("Symptoms:Sweating,Anxiety,Dizziness,Chest Pain"
                    +" "+"Refereed Doctor:Dr Aryan Patil"+" "+"App Version:version_v_1.0");

            contentStream.endText();

            contentStream.setNonStrokingColor(0, 0, 0); //black text
            contentStream.beginText();
            contentStream.setFont(font,10);
            contentStream.newLineAtOffset(120f,marginUpperLine-24);
            contentStream.showText("Comments= Heart beat is 72 bpm");
            contentStream.endText();

            contentStream.setNonStrokingColor(0, 0, 0); //black text
            contentStream.beginText();
            contentStream.setFont(font, 10);
            contentStream.newLineAtOffset(2*cursorX,cursorY+5);
            contentStream.showText("10mm/mV    25mm/s");
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
            is =  assetManager.open("apna_csc.png");
            alpha =  assetManager.open("apollo.png");

            Bitmap b= BitmapFactory.decodeStream(alpha);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            PDImageXObject ximage = LosslessFactory.createFromImage(document,bitmap);
            PDImageXObject yimage=LosslessFactory.createFromImage(document,b);



            contentStream.drawImage(ximage,40+4,marginLowerLine+2);
            contentStream.drawImage(yimage,rect_width+cursorX-20,marginLowerLine);



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
            // draw minor verticl grids
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
                    mP1 = (1 *graphHeight)+(int) ( PlotXYarray3.ecgdata12[i] / heightScale);
                    contentStream.setStrokingColor(AWTColor.black);
                    contentStream.moveTo(mPx1, mPp1);
                    contentStream.lineTo(mX,mP1);
                    contentStream.stroke();

                    mPx1=mX;
                    mPp1=mP1;

                }


                if (noOfLeads >= 11) {
                    mP2 = (2 *graphHeight)+(int) ( PlotXYarray3.ecgdata11[i] / heightScale);
                    contentStream.setStrokingColor(AWTColor.black);
                    contentStream.moveTo(mPx2, mPp2);
                    contentStream.lineTo(mX,mP2);
                    contentStream.stroke();

                    mPx2=mX;
                    mPp2=mP2;

                }



                if (noOfLeads >= 10) {
                    mP3 = (3 *graphHeight)+(int) ( PlotXYarray3.ecgdata10[i] / heightScale);
                    contentStream.setStrokingColor(AWTColor.black);
                    contentStream.moveTo(mPx3, mPp3);
                    contentStream.lineTo(mX,mP3);
                    contentStream.stroke();

                    mPx3=mX;
                    mPp3=mP3;

                }


                if (noOfLeads >= 9) {
                    mP4 = (4 *graphHeight)+(int) ( PlotXYarray2.ecgdata9[i] / heightScale);
                    contentStream.setStrokingColor(AWTColor.black);
                    contentStream.moveTo(mPx4, mPp4);
                    contentStream.lineTo(mX,mP4);
                    contentStream.stroke();

                    mPx4=mX;
                    mPp4=mP4;

                }


                if (noOfLeads >= 8) {
                    mP5 = (5 *graphHeight)+(int) ( PlotXYarray2.ecgdata8[i] / heightScale);
                    contentStream.setStrokingColor(AWTColor.black);
                    contentStream.moveTo(mPx5, mPp5);
                    contentStream.lineTo(mX,mP5);
                    contentStream.stroke();

                    mPx5=mX;
                    mPp5=mP5;

                }

                if (noOfLeads >= 7) {
                    mP6 = (6 *graphHeight)+(int) ( PlotXYarray2.ecgdata7[i] / heightScale);
                    contentStream.setStrokingColor(AWTColor.black);
                    contentStream.moveTo(mPx6, mPp6);
                    contentStream.lineTo(mX,mP6);
                    contentStream.stroke();

                    mPx6=mX;
                    mPp6=mP6;

                }
                if (noOfLeads >= 6) {
                    mP7 = (7 *graphHeight)+(int) ( PlotXYarray1.ecgdata6[i] / heightScale);
                    contentStream.setStrokingColor(AWTColor.black);
                    contentStream.moveTo(mPx7, mPp7);
                    contentStream.lineTo(mX,mP7);
                    contentStream.stroke();

                    mPx7=mX;
                    mPp7=mP7;

                }
                if (noOfLeads >= 5) {
                    mP8 = (8 *graphHeight)+(int) ( PlotXYarray1.ecgdata5[i] / heightScale);
                    contentStream.setStrokingColor(AWTColor.black);
                    contentStream.moveTo(mPx8, mPp8);
                    contentStream.lineTo(mX,mP8);
                    contentStream.stroke();

                    mPx8=mX;
                    mPp8=mP8;

                }
                if (noOfLeads >= 4) {
                    mP9 = (9 *graphHeight)+(int) ( PlotXYarray1.ecgdata4[i] / heightScale);
                    contentStream.setStrokingColor(AWTColor.black);
                    contentStream.moveTo(mPx9, mPp9);
                    contentStream.lineTo(mX,mP9);
                    contentStream.stroke();

                    mPx9=mX;
                    mPp9=mP9;

                }
                if (noOfLeads >= 3) {
                    mP10 = (10 *graphHeight)+(int) ( PlotXYarray.ecgdata3[i] / heightScale);
                    contentStream.setStrokingColor(AWTColor.black);
                    contentStream.moveTo(mPx10, mPp10);
                    contentStream.lineTo(mX,mP10);
                    contentStream.stroke();

                    mPx10=mX;
                    mPp10=mP10;

                }

                if (noOfLeads >= 2) {
                    mP11 = (11 *graphHeight)+(int) ( PlotXYarray.ecgdata2[i] / heightScale);
                    contentStream.setStrokingColor(AWTColor.black);
                    contentStream.moveTo(mPx11, mPp11);
                    contentStream.lineTo(mX,mP11);
                    contentStream.stroke();

                    mPx11=mX;
                    mPp11=mP11;

                }

                if (noOfLeads >= 1) {
                    mP12 = (12 *graphHeight)+(int) ( PlotXYarray.ecgdata1[i] / heightScale);
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



            //---
            // PDRectangle pageSize = page.getMediaBox();
            // PDPageContentStream p=new PDPageContentStream()
            // contentStream.addRect(cursorX,unit_per_cm*18f,rect_width,unit_per_cm*19f);
            //contentStream.setNonStrokingColor(26, 255,255);
            // contentStream.setStrokingColor(AWTColor.BLACK);
            //contentStream.fill();


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
            document.save(path);
            document.close();




        } catch (IOException e) {
            e.printStackTrace();

        }


    }


    private void get_assets_image(Context context,PDDocument document,PDPageContentStream contentStream)
    {

        //imageView.setImageBitmap(bitmap);


    }

}