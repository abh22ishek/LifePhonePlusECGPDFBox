/*

package com.lpp.pkg.bitmap;

import android.os.Environment;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfPage;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapEcg {

	public static void covertImagetoPdf()
    {
        Document document=new Document(PageSize.A4.rotate());
        try {
           PdfWriter pdfwriter= PdfWriter.getInstance(document, new FileOutputStream(Environment.getExternalStorageDirectory() + "/MyImage3.pdf"));
            document.open();
            try {
                Image image= Image.getInstance(Environment.getExternalStorageDirectory()+"/Screen Activity Display1448432573148.jpeg");
               // document.add(new Paragraph("My Header of Image"));
                int identation=0;
               //Rectangle rectangle=document.getPageSize();


                float scalerX = ((document.getPageSize().getWidth() - document.leftMargin()
                        - document.rightMargin() - identation) / image.getWidth()) * 100;


                float scalerY=(( document.getPageSize().getHeight()-document.topMargin()
                        -document.bottomMargin()-identation)/image.getHeight())*100;




                 //image.scaleToFit(PageSize.A4.getWidth() - document.leftMargin() - document.rightMargin(), PageSize.A4.getHeight() - document.topMargin() - document.bottomMargin());
                   image.scalePercent(scalerX,scalerY);
                
                document.add(image);
                document.close();



            } catch (BadElementException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }


        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }



    public static class Rotation extends PdfPageEventHelper
    {




        @Override
        public void onStartPage(PdfWriter writer, Document document) {
            writer.addPageDictEntry(PdfName.ROTATE,PdfPage.LANDSCAPE);
        }


    }
	
}

*/
