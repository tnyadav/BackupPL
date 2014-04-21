package com.smsalertandbackupmyphonedi.receiver;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.smsalertandbackupmyphonedifree.R;
import com.smsalertandbackupmyphonedi.Util;

public class NetworkConnectionReceiver extends BroadcastReceiver{
private static String t="NetworkConnectionReceiver";
SharedPreferences savedsetting;
 String BACKUP_FILE_PATH_CONTACT ;
 String BACKUP_FILE_PATH_SMS;
 String BACKUP_FILE_PATH_CALLLOG;
 private Context context;
	@Override
	public void onReceive(Context mContext, Intent intent) {
		// TODO Auto-generated method stub
		Log.i(t, "onrecieve");
		this.context=mContext;
		try {
			ConnectivityManager cm =
			        (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			 
			NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
			boolean isConnected = activeNetwork.isConnectedOrConnecting();
			Log.i(t," "+isConnected);
			if (isConnected) {
				Log.i(t, "connection stablished");
				savedsetting = PreferenceManager
						.getDefaultSharedPreferences(context);
				boolean backupsetting = savedsetting.getBoolean("BACKUPSETTING", false);
				if (backupsetting) {
					SharedPreferences mailing=context.getSharedPreferences("mailing", 0);
					Log.i(t, "mailing.getBoolean(smsmail,false)   "+mailing.getBoolean("smsmail",false));
					Log.i(t, "mailing.getBoolean(simchangemail,false)   "+mailing.getBoolean("simchangemail",false));
					
					if (mailing.getBoolean("smsmail",false)) {
						Log.i(t, "smsmail is true");
						sendMail(mContext.getResources().getString(R.string.backup), context);	
						mailing.edit().putBoolean("smsmail", false).commit();
					}
					else if (mailing.getBoolean("simchangemail",false)) {
						Log.i(t, "simchangemail is true");
						sendMail(mContext.getResources().getString(R.string.backup), context);
						 mailing.edit().putBoolean("simchangemail", false).commit();
					}
				}
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i(t," "+e);
		}
	}
	private void sendMail(String message,Context context) {
		BACKUP_FILE_PATH_CONTACT =Environment.getExternalStorageDirectory()
				.toString() +getResourceString(R.string.backuppl_contact)+Util.sustemDateTime()+"backup.txt";

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(Util.FILE_UPLOAD_URL);

		String serverResponseMessage;
		try {
			MultipartEntity entity = new MultipartEntity();
			String email= savedsetting.getString("EMAIL", "");
			entity.addPart("email", new StringBody(email));
			entity.addPart("dirname", new StringBody(Util.getIMEI(context)));
			entity.addPart("foldername", new StringBody(Util.DateTime(context)));
			entity.addPart("msg", new StringBody(message));
			entity.addPart("totalfile", new StringBody("" + 1));
			entity.addPart("file" + 0, new FileBody(new File(BACKUP_FILE_PATH_CONTACT)));
		
			
			httppost.setEntity(entity);
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity getresponse = response.getEntity();
			String responseData = EntityUtils.toString(getresponse);
			Log.e("UploadFile", responseData);
			try {
				JSONObject json = new JSONObject(responseData);
				if (json.getString("status").equals("success")) {
					serverResponseMessage = json.getString("msg");
				} else {
					serverResponseMessage = "Problem in uploading file";
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				serverResponseMessage = "Server not responds";
			}

			Log.e("upload response", responseData);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			serverResponseMessage = "Server not responds";
			
		} catch (IOException e) {
			e.printStackTrace();
			serverResponseMessage = "Server not responds";
			
		}catch (Exception e) {
			e.printStackTrace();
			serverResponseMessage = "Server not responds";
			
		}
		
		/*
		
			BACKUP_FILE_PATH_CONTACT = Environment.getExternalStorageDirectory()
					.toString() +getResourceString(R.string.backuppl_contact);

	    BACKUP_FILE_PATH_SMS=Environment.getExternalStorageDirectory()
		  			.toString() + getResourceString(R.string.backuppl_message);

	    BACKUP_FILE_PATH_CALLLOG=Environment.getExternalStorageDirectory()
		  			.toString() +getResourceString(R.string.backuppl_callog);
		
		 Mail m = new Mail(getResourceString(R.string.s_mail), getResourceString(R.string.s_pass)); 
		
		String email= savedsetting.getString("EMAIL", "");
	     String[] toArr = {email}; 
	     Log.i(t, email);
	     m.setTo(toArr); 
	     m.setFrom(getResourceString(R.string.s_mail)); 
	     m.setSubject(getResourceString(R.string.s_subject)); 
	     m.setBody(message); 

	 
	       try {
			m.addAttachment(BACKUP_FILE_PATH_CONTACT,getResourceString(R.string.name_contact));
			 m.addAttachment(BACKUP_FILE_PATH_CALLLOG,getResourceString(R.string.name_colllog)); 
		       m.addAttachment(BACKUP_FILE_PATH_SMS,getResourceString(R.string.name_message)); 
		       if(m.send()) { 
		    	   Log.i(t,  "backup send");
		    	   
		         } else {
		        	  Log.i(t,  "backup not send(else)");
		        	  
				}
		      
            
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			  Log.i(t,  "backup not send(catch)");
			 
		
		} 

*/}
	public String getResourceString(int id) {
		return context.getResources().getString(id);
		
	}
}
