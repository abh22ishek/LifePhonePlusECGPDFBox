package com.lpp.pkgdirectory;

import android.os.Environment;
import android.util.Log;

import com.lppbpl.android.userapp.constants.Constants;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileSaveData {

  public static final String TAG=FileSaveData.class.getSimpleName();
	
	public static void  writeDatatoFile(String filename,String measurementype,String data)
	{
		String filenamedir="";
		if(measurementype.equalsIgnoreCase(Constants.BG_GLUCOSE))
	      filenamedir="LppMeasurement";
		else
		filenamedir="LppMeasurement";
	
	File file =new File(Environment.getExternalStorageDirectory(),filenamedir);
	if(!file.exists())
	{
		file.mkdir();
	}else{
		if(file.listFiles().length>=1)
		{
			// copy all existing files into directory
			String ExistingFiles[];
			ExistingFiles=file.list();
			for(int i=0;i<ExistingFiles.length;i++)
			{
				File existingfile = new File(file, ExistingFiles[i]);
				existingfile.delete();
			}
		}
	}

			File ecgfile=new File(file,filename);


		try {
			FileWriter filewriter=new FileWriter(ecgfile,false);
			filewriter.append(data);
			filewriter.flush();
			filewriter.close();
			Log.i("FilePath", "saving data into file");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
	}  
		



   public static void writeEcgdatatoFile(String sb,String filename,String datatype)
   {
	   String filenamedir="LppMeasurement";

	   File file =new File(Environment.getExternalStorageDirectory(),filenamedir);

	   if(!file.exists())
	   {
		   file.mkdir();

	   }else{
		   if(file.listFiles().length>=1)
		   {
			   // copy all existing files into directory
			   String ExistingFiles[];
			   ExistingFiles=file.list();
			   for(String s:ExistingFiles)
			  //for(int i=0;i<ExistingFiles.length;i++)
			   {
				   File existingfile = new File(file,s);
				   existingfile.delete();
			   }
		   }
	   }


	   File ecgfile=new File(file,filename);
	   try {
		   FileWriter filewriter=new FileWriter(ecgfile,false);
		   filewriter.append(sb);
		   filewriter.flush();
		   Log.i("FilePath", "saving data into file");
		   //appendFile(filewriter, string);
	   } catch (IOException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
	   }

   }





}
