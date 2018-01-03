package com.pdfbox;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.lpp.xmldata.ConvertTexttoXml;
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
 * Created by abhishek.raj on 29-12-2017.
 */

public class ActivityPdfBox {

    public void createActivityPdfTable(Context context)
    {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));


        float page_height=page.getMediaBox().getHeight();
        float page_width=page.getMediaBox().getWidth();


        System.out.println(page.getMediaBox().getHeight());
        System.out.println(page.getMediaBox().getWidth());


        String path = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/Download/ActivityPdfBox.pdf";


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
            contentStream.showText("Activity Final Report");
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


            document.save(path);
            document.close();


        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}
