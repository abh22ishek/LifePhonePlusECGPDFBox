/*
package com.lpp.pkg.bitmap;

import android.os.Environment;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPage;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.lppbpl.android.userapp.constants.Constants;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

*/
/**
 * Created by admin on 25-11-2015.
 *//*

public class ConvertImagetoPdf {



    public static void covertImagetoPdf(String content,String type)
    {
        Document document=new Document(PageSize.A4.rotate());
        try {
            if(type.equalsIgnoreCase(Constants.BG_GLUCOSE))
            {
                PdfWriter pdfwriter= PdfWriter.getInstance(document, new FileOutputStream
                        (Environment.getExternalStorageDirectory() + "/BGResult.pdf"));
            }

            else{
                PdfWriter pdfwriter= PdfWriter.getInstance(document, new FileOutputStream
                        (Environment.getExternalStorageDirectory() + "/ActivityResult.pdf"));
            }

            document.open();
            try {
                //Image image= Image.getInstance(Environment.getExternalStorageDirectory()+"/Screen Activity Display1448432573148.jpeg");
                PdfPTable t=new PdfPTable(1);
                t.setWidthPercentage(100);
                 t.addCell(headerTextCell("Name=Abhishek" + "\n" + "Age =27" + "\n" + "Sex =Male"));

                PdfPTable table=new PdfPTable(2);
                table.setSpacingBefore(20f);
                table.setWidthPercentage(100);
                table.setWidths(new int[]{1, 2});
                if(type.equalsIgnoreCase(Constants.BG_GLUCOSE))
                    table.addCell(createImageCell(Environment.getExternalStorageDirectory() + "/ScreenBloodGlucose.jpeg"));
                else
                  table.addCell(createImageCell(Environment.getExternalStorageDirectory() + "/ScreenActivity.jpeg"));

                table.addCell(createTextCell(content));

                document.add(t);
                document.add(table);
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
            writer.addPageDictEntry(PdfName.ROTATE, PdfPage.LANDSCAPE);
        }


    }
    public static PdfPCell createTextCell(String text) throws DocumentException, IOException {
        PdfPCell cell = new PdfPCell();
        Paragraph p = new Paragraph(text);
        p.setAlignment(Element.ALIGN_LEFT);
        cell.addElement(p);
        cell.setPaddingLeft(20f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
       // cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    public static PdfPCell createImageCell(String path) throws DocumentException, IOException {
        Image img = Image.getInstance(path);
        PdfPCell cell = new PdfPCell(img, true);
        return cell;
    }


    public static PdfPCell headerTextCell(String text) throws DocumentException, IOException {
        PdfPCell cell = new PdfPCell();
        Paragraph p = new Paragraph(text);
        p.setAlignment(Element.ALIGN_LEFT);
        cell.addElement(p);
        cell.setPaddingTop(3f);
        cell.setPaddingBottom(3f);
        cell.setPaddingLeft(5f);

        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        // cell.setVerticalAlignment(Element.ALIGN_CENTER);
        return cell;
    }
}
*/
