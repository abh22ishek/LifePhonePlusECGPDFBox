package com.lpp.xmldata;

import android.content.Context;
import android.content.Intent;
import android.util.Xml;
import android.widget.Toast;

import com.logging.Level;
import com.logging.Logger;
import com.lpp.pkgdirectory.FileSaveData;
import com.lppbpl.EcgMultipleLead;
import com.lppbpl.Response;
import com.lppbpl.android.userapp.ECGActivity;
import com.lppbpl.android.userapp.EcgGraphActivity;
import com.lppbpl.android.userapp.MainMenuActivity;
import com.lppbpl.android.userapp.constants.Constants;
import com.lppbpl.android.userapp.model.ActivityMeasurementModel;
import com.lppbpl.android.userapp.model.BgMeasurementModel;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class ConvertTexttoXml {

	private static final String TAG = "ConvertTexttoXml";
	// Xml Pull Parser
	// text to xml for BloodGlucose Data


//	private static String str= "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>" +"\n"
//			+"<EcgMeasurementsRoot EcgMeasurementRootRecordsSize=\"12\">";

//	private static String end_doc="</EcgMeasurementsRoot>";
		public static String WriteXml(List<BgMeasurementModel> measurements,String symptoms,Context context)
		{
			XmlSerializer serializer = Xml.newSerializer();
		    StringWriter writer = new StringWriter();
		    try {
		        serializer.setOutput(writer);
		        serializer.startDocument("UTF-8", true);
				serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
		        serializer.startTag("", "Measurements");
		        serializer.attribute("", "Measurement_Records_Size", String.valueOf(measurements.size()));
		        for (BgMeasurementModel m: measurements){
		           
		        	serializer.startTag("", "Measurement");
		            serializer.attribute("", "code1","code1");
		            
		            serializer.startTag("", "BloodGlucose");
		            serializer.text(m.getBloodglucose()+"mg/dL");
		            serializer.endTag("", "BloodGlucose");
		            
		            serializer.startTag("", "DateAndTime");
		            serializer.text(m.getDateOftest());
		            serializer.endTag("", "DateAndTime");
		            
		            serializer.startTag("", "UserComments");
		            serializer.text(m.getUsercomments());
		            serializer.endTag("", "UserComments");

					serializer.startTag("","Symptoms");
					serializer.text(symptoms);
					serializer.endTag("","Symptoms");

					serializer.startTag("","FastingType");
					serializer.text(m.getFasting_type());
					serializer.endTag("","FastingType");

					serializer.endTag("", "Measurement");
		        }
		        serializer.endTag("", "Measurements");
		        serializer.endDocument();
		       
		        com.logging.Logger.log(Level.INFO, TAG, writer.toString());
		        FileSaveData.writeDatatoFile("LppMeasurement" + " " + getCurrentTimeStamp() + ".xml", Constants.BG_GLUCOSE, writer.toString());
						Toast.makeText(context, "Blood Sugar Measurement is saved into a file", Toast.LENGTH_SHORT).show();
				final Intent intent=new Intent(context,MainMenuActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				context.startActivity(intent);
		        return writer.toString();
		    }

		    catch (Exception e) {
		        throw new RuntimeException(e);
		    }
		}



	// text to xml for Activity Data

	public static String writetoXmlActivityData(List<ActivityMeasurementModel> activityMeasurementModelList,Context context)
	{
		XmlSerializer serializer=Xml.newSerializer();
		StringWriter stringWriter=new StringWriter();
		try {
			serializer.setOutput(stringWriter);
			serializer.startDocument("UTF-8", true);
			serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
			serializer.startTag("", "ActivityMeasurements");
			serializer.attribute("", "ActivityMeasurementRecordsSize", String.valueOf(activityMeasurementModelList.size()));
			for (ActivityMeasurementModel m: activityMeasurementModelList){

				serializer.startTag("", "ActivityMeasurement");
				serializer.attribute("", "code1", "code1");

				serializer.startTag("", "TotalStepsTaken");
				serializer.text(m.getTotalstepstaken());
				serializer.endTag("", "TotalStepsTaken");

				serializer.startTag("", "TotalMetresTravelled");
				serializer.text(m.getTotalmetrestravelled());
				serializer.endTag("", "TotalMetresTravelled");

				serializer.startTag("", "TotalCaloriesBurnt");
				serializer.text(m.getTotalcaloriesburnt());
				serializer.endTag("", "TotalCaloriesBurnt");

				serializer.startTag("", "UserComments");
				serializer.text(m.getUsercomments());
				serializer.endTag("", "UserComments");


				serializer.startTag("", "StartEndTime");
				serializer.text(m.getStartEndtime());
				serializer.endTag("", "StartEndTime");



				serializer.endTag("", "ActivityMeasurement");
			}
			serializer.endTag("", "ActivityMeasurements");
			serializer.endDocument();

			 com.logging.Logger.log(Level.INFO, TAG, stringWriter.toString());
			FileSaveData.writeDatatoFile("LppMeasurement"+" "+getCurrentTimeStamp()+".xml", Constants.ACTIVITY_RUNNING, stringWriter.toString());
					Toast.makeText(context, "Activity Measurement is saved into a file", Toast.LENGTH_SHORT).show();
			final Intent intent=new Intent(context,MainMenuActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			context.startActivity(intent);
			return stringWriter.toString();
		}

		catch (Exception e) {
			throw new RuntimeException(e);
		}



	}


// text to xml for Ecg Data
	public static String writeToxmlEcg(Response response,Vector<EcgMultipleLead> v,Context context,int count)
	{

		XmlSerializer serializer=Xml.newSerializer();

		StringWriter writer=new StringWriter();
		v=response.getEcgData().getMulLead();

		try {
			serializer.setOutput(writer);
			//serializer.startDocument("UTF-8", true);
			serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

			serializer.startTag("", "EcgMeasurements" + count);
			serializer.attribute("", "EcgMeasurementRecordNo.", String.valueOf(count));

			for (int k = 0; k <1; k++) {

				serializer.startTag("", "EcgMeasurementLeads");
				serializer.attribute("", "code1", "code1");

				serializer.startTag("", "MeasurementId");
				serializer.text(response.getEcgData().getMeasurementId() + "");
				serializer.endTag("", "MeasurementId");

			//	serializer.startTag("", "Symptoms");
				//serializer.text(response.getEcgData().getEcgSymptoms() + "");
				//serializer.endTag("", "Symptoms");

				serializer.startTag("", "Duration");
				serializer.text(response.getEcgData().getDuration() + "");
				serializer.endTag("", "Duration");

				serializer.startTag("", "MullLeads");
				serializer.attribute("", "MullLeadRecordSize", String.valueOf(v.size()));
				for(int i=0;i<v.size();i++)
				{
					serializer.startTag("","MullRecords");
					serializer.attribute("", "code1", "code1");

					serializer.startTag("", "Lead");
					serializer.text(changeleadNums(v.get(i).getLead()) + "");
					serializer.endTag("", "Lead");

					serializer.startTag("", "TimeStamp");
					serializer.text(v.get(i).getTimestamp() + "");
					serializer.endTag("", "TimeStamp");

					serializer.startTag("", "StepNum");
					serializer.text(v.get(i).getStepNum() + "");
					serializer.endTag("", "StepNum");

					serializer.startTag("", "EcgStrip");
					serializer.text(String.valueOf(v.get(i).getEcgStrip()));
	                serializer.endTag("", "EcgStrip");

					serializer.endTag("","MullRecords");
				}
				//serializer.text(response.getEcgData().getMulLead());
				serializer.endTag("", "MullLeads");
				serializer.endTag("", "EcgMeasurementLeads");
			}
			serializer.endTag("", "EcgMeasurements" + count);
			serializer.endDocument();
			if(ECGActivity.sb!=null){
				ECGActivity.sb.append(writer);
			}

			com.logging.Logger.log(Level.INFO, TAG, writer.toString());




		} catch (IOException e) {
			e.printStackTrace();
		}
		return writer.toString();
	}


	// chnage the lead numbers to it numerical format
	private static String changeleadNums(int k)
	{
       int l=k;
		String lead_Value="";
		switch (l) {
		/*	case 1:
				lead_Value="MDC_ECG_LEAD_I";
				break;
			case 2:
				lead_Value = "MDC_ECG_LEAD_II";
				break;
			case 3:
				lead_Value = "MDC_ECG_LEAD_III";
				break;
			case 4:
				lead_Value = "MDC_ECG_LEAD_AVR";
				break;
			case 5:
				lead_Value = "MDC_ECG_LEAD_AVL";
				break;

			case 6:
				lead_Value = "MDC_ECG_LEAD_AVF";
				break;
			case 7:
				lead_Value = "MDC_ECG_LEAD_V1";
				break;
			case 8:
				lead_Value = "MDC_ECG_LEAD_V2";
				break;
			case 9:
				lead_Value = "MDC_ECG_LEAD_V3";
				break;
			case 10:
				lead_Value = "MDC_ECG_LEAD_V4";
				break;
			case 11:
				lead_Value = "MDC_ECG_LEAD_V5";
				break;
			case 12:
				lead_Value = "MDC_ECG_LEAD_V6";
				break;

*/
/*
//Lead naming Issue: v 1.03
//Error : Lead I, Lead II, Lead III, MCL4, MCL5, MCL6, aVR, aVL, avF, MCL1,MCL2, MCL3
//Fix : Lead I, Lead II, Lead III, aVR, aVL, avF, MCL1,MCL2, MCL3, MCL4, MCL5, MCL6

			case 1:
				lead_Value="LEAD I";
				break;
			case 2:
				lead_Value = "LEAD II";
				break;
			case 3:
				lead_Value = " LEAD III";
				break;
			case 4:
				lead_Value = "aVR";
				break;
			case 5:
				lead_Value = "avL";
				break;

			case 6:
				lead_Value = "aVF";
				break;
			case 7:
				lead_Value = "MCL1";
				break;
			case 8:
				lead_Value = "MCL2";
				break;
			case 9:
				lead_Value = "MCL3";
				break;
			case 10:
				lead_Value = "MCL4";
				break;
			case 11:
				lead_Value = "MCL5";
				break;
			case 12:
				lead_Value = "MCL6";
				break;
*/
//Lead naming Issue: v 1.03
//Error : Lead I, Lead II, Lead III, MCL4, MCL5, MCL6, aVR, aVL, avF, MCL1,MCL2, MCL3
//Fix : Lead I, Lead II, Lead III, aVR, aVL, avF, MCL1,MCL2, MCL3, MCL4, MCL5, MCL6

			case 1:
				lead_Value="LEAD I";
				break;
			case 2:
				lead_Value = "LEAD II";
				break;
			case 3:
				lead_Value = " LEAD III";
				break;
			case 4:
				lead_Value = "MCL1";
				break;
			case 5:
				lead_Value = "MCL2";
				break;
			case 6:
				lead_Value = "MCL3";
				break;
			case 7:
				lead_Value = "MCL4";
				break;
			case 8:
				lead_Value = "MCL5";
				break;
			case 9:
				lead_Value = "MCL6";
				break;
			case 10:
				lead_Value = "aVR";
				break;
			case 11:
				lead_Value = "avL";
				break;
			case 12:
				lead_Value = "aVF";
				break;
		}

		return lead_Value;
	}
	/**
	 *
	 * @return yyyy-MM-dd HH:mm:ss formate date as string
	 */
	public static String getCurrentTimeStamp(){
		try {

			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			String currentTimeStamp = dateFormat.format(new Date()); // Find todays date

			return currentTimeStamp;
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}
	}
	public static void saveEcgtoFile(boolean mSave,String symptoms_selected,String heart_rate_)
	{

		String str= "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>" +"\n"
				+"<EcgMeasurementsRoot EcgMeasurementRootRecordsSize=\"12\">";

		String end_doc="</EcgMeasurementsRoot>";

		String user_cmt_tag="\n"+"<UserComments>";
		String user_cmt_end_tag="</UserComments>";

		String symptoms_tag="\n"+"<Symptoms>";
		String symptoms_end_tag="</Symptoms>";

		String heart_rate_tag="\n"+"<HeartRate>";
		String heart_rate_end_tag="</HeartRate>";

		String user_comments=EcgGraphActivity.mEditTxt.getText().toString();

		if(mSave)
		{
			StringBuilder strbuilder=new StringBuilder();
			strbuilder.append(str);
			strbuilder.append(user_cmt_tag);
			strbuilder.append(user_comments);
			strbuilder.append(user_cmt_end_tag);
			strbuilder.append(symptoms_tag);
			strbuilder.append(symptoms_selected);
			strbuilder.append(symptoms_end_tag);
			strbuilder.append(heart_rate_tag);
			strbuilder.append(heart_rate_+" "+"bpm");
			strbuilder.append(heart_rate_end_tag);

			strbuilder.append(ECGActivity.sb.toString());
			strbuilder.append(end_doc);


			FileSaveData.writeEcgdatatoFile(strbuilder.toString(),
					"LppMeasurement" + " " +getCurrentTimeStamp() + ".xml",
					"ElectroCardioGraph");
			Logger.log(Level.INFO, TAG, "Ecg Measurement is saved " +
					"into a LppMeasurement folder Successfully");
		}else {
			return;
		}
	}
}
