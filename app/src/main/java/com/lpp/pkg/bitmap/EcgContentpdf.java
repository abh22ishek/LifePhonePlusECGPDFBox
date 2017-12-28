/*
package com.lpp.pkg.bitmap;

import android.os.Environment;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.logging.Level;
import com.logging.Logger;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

*/
/**
 * Created by admin on 01-12-2015.
 *//*

public class EcgContentpdf {
    public static final String TAG=EcgContentpdf.class.getSimpleName();
    public static void addTexttoPdf(String content)
    {


        Document document=new Document();

        try {
            PdfWriter pdfwriter= PdfWriter.getInstance(document, new FileOutputStream(Environment.getExternalStorageDirectory() + "/ECGResult.pdf"));



        } catch (DocumentException e1) {
            e1.printStackTrace();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        document.open();

            try {
                //Image image= Image.getInstance(Environment.getExternalStorageDirectory()+"/Screen Activity Display1448432573148.jpeg");
                PdfPTable t=new PdfPTable(1);
                t.setWidthPercentage(100);
                t.addCell(headerTextCell("Hello World"));

                PdfPTable table=new PdfPTable(1);
                table.setSpacingBefore(20f);
                table.setWidthPercentage(100);

                table.addCell(createTextCell(content));

                document.add(t);
                document.add(table);
                document.close();

                Logger.log(Level.INFO, TAG, "Successfully Added contents to the file");

            } catch (BadElementException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (DocumentException e1) {
                e1.printStackTrace();
            }


    }



    public static PdfPCell createTextCell(String text) throws DocumentException, IOException {
        PdfPCell cell = new PdfPCell();
        Paragraph p = new Paragraph(text);
        p.setAlignment(Element.ALIGN_LEFT);
        cell.addElement(p);
        cell.setPaddingLeft(5f);
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
