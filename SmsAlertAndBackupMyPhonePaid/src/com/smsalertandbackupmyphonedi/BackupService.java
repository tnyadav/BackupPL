package com.smsalertandbackupmyphonedi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.sql.Date;
import java.util.Timer;
import java.util.TimerTask;

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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;

@SuppressWarnings("unused")
public class BackupService extends Service {

	 String BACKUP_FILE_PATH;
	 String BACKUP_FILE_PATH_CONTACT ;
	
	
	Context context;
	String Text, emailmessage="";
	FileInputStream fis;
	FileOutputStream mFileOutputStream;
	OutputStreamWriter osw;
	boolean flag=false;
	SharedPreferences savedsetting;
    static String t="BackupService";
	@Override
	public void onCreate() {
		context=this;
	}

	@Override
	public void onDestroy() {
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {

		

		super.onStart(intent, startId);
		
	}

	@Override
	public boolean onUnbind(Intent intent) {

		
	
		return super.onUnbind(intent);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		SharedPreferences mailing=context.getSharedPreferences("mailing", 0);
	/*	 int sender=intent.getIntExtra("request", 0);
		 if (sender==1) {
		mailing.edit().putBoolean("smsmail", true).commit();
		
		} 
		 else if (sender==2) {
	   mailing.edit().putBoolean("simchangemail", true).commit();	
		}*/
		 BACKUP_FILE_PATH = Environment.getExternalStorageDirectory()
					.toString() +getResourceString(R.string.backuppl);

	    BACKUP_FILE_PATH_CONTACT = Environment.getExternalStorageDirectory()
					.toString() +getResourceString(R.string.backuppl_contact)+Util.sustemDateTime()+"backup.txt";

		File f = new File(BACKUP_FILE_PATH);
		if (!f.exists()) {
			f.mkdirs();
		}
		savedsetting = PreferenceManager
				.getDefaultSharedPreferences(context);
		File c=new File(BACKUP_FILE_PATH_CONTACT);
		
		if (c.exists()) {
			c.delete();
		}
		
		//message 
	
		TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = mTelephonyMgr.getSubscriberId();

		Intent intent1 = new Intent(context, BackupService.class);
		 emailmessage = "Hello,\n\nThanks for downloading Track Lost Phone & Get Backup\n\nYour phone IMEI is "
				+ mTelephonyMgr.getDeviceId();
		Location location = GeoLocationUtil.getGeoLocation(context);
		String currentlocation = null;
		if (!(location == null)) {
			double m_dLng = location.getLongitude();
			double m_dLat = location.getLatitude();
			currentlocation = "\n\nTrack Lost Phone has located your phone within 25m of  http://maps.google.com/?q="
					+ m_dLat + "," + m_dLng + "&z=" + m_dLat + "," + m_dLng;
			Log.i(t, m_dLat + "  " + m_dLng);
		} else {
			currentlocation = "\nLocation not found";

		}
		emailmessage = emailmessage
				+ currentlocation
				+ "\n\nAlready Lost your phone ? Try our Plan B for Backup -https://play.google.com/store/apps/details?id=com.planbdi" +
				"\n\nAttached the Contacts, SMS and Call Log for your reference." +
				"\n\nKeep the attached zip file. You can restore Contacts, Call Log, SMS to your New Phone by downloading our app - https://play.google.com/store/apps/details?id=com.smsalertandbackupmyphonedi" +
				"\n\nHappy to see the priceless backup, please rate - https://play.google.com/store/apps/details?id=com.smsalertandbackupmyphonedi" +
				"\n\nDo not reply on this email.For any query, please email at apps@diwebtech.com\n\nGood Luck!";

		//
		
		 int delay=intent.getIntExtra("delay", 60000);
		final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
            	
            	contactBackup();
        	    smsBackup();	
        		callLog();
        		if (checkConnection()) {
        			Log.i(t, "connection is on");
        			sendMail(emailmessage);
				} else {
					Log.i(t, "connection is off");	

					try {
						Log.i(t, "enabling dataconnection");
						ConnectivityManager dataManager;
						dataManager  = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
						Method dataMtd;
						dataMtd = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", boolean.class);
						dataMtd.setAccessible(true);
						dataMtd.invoke(dataManager, true);
						Log.i(t, "data connection enable");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					try {
						Log.e(t, "enabling wifi connection");
						WifiManager wifiManager ;
						wifiManager  = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
						wifiManager.setWifiEnabled(true);        //True - to enable WIFI connectivity .
						Log.e(t, "wifi enable");
					} catch (Exception e1) {
						
						e1.printStackTrace();
					}

					timer.schedule(new TimerTask() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							sendMail(emailmessage);
						}
					}, 100);	  
				}
        		
        	     
            }
        }, delay);
		
		
		return START_STICKY;

	}

	@Override
	public IBinder onBind(Intent intent) {
	
		return null;
	}

	public void contactBackup() {
		writeText("*************Contact************");
		Cursor cursor1 = context.getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

		while (cursor1.moveToNext()) {

			String id = cursor1.getString(cursor1
					.getColumnIndex(ContactsContract.Contacts._ID));

			String DisplayName = cursor1.getString(cursor1
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			if (Integer
					.parseInt(cursor1.getString(cursor1
							.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
				getPhoneNumbers(id, DisplayName);
			}

		}
		cursor1.close();
		
flag=true;
	}

	public void getPhoneNumbers(String id, String displayName) {

		Cursor pCur = context.getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
				new String[] { id }, null);
		while (pCur.moveToNext()) {

			Text = "\n"
					+ displayName
					+ " -"
					+ pCur.getString(pCur
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			writeText(Text);

		}
		pCur.close();

	}

	public void smsBackup() {
		writeText("\n\n*************Sms************\n\n");
		Cursor cursor1 = context.getContentResolver().query(
				Uri.parse("content://sms"),
				new String[] { "_id", "thread_id", "address", "person", "date",
						"body" }, null, null, null);
		String[] columns = new String[] { "_id", "thread_id", "address",
				"person", "date", "body" };
		while (cursor1.moveToNext()) {

			Text = "\nAddress-"
					+ cursor1.getString(cursor1.getColumnIndex(columns[2]))
					+ " Date-"
					+ getDate(cursor1.getString(cursor1.getColumnIndex(columns[4])))
					+ " Body-"
					+ cursor1.getString(cursor1.getColumnIndex(columns[5]));
			writeText(Text);
		}
		cursor1.close();
	}

	public void callLog() {

		writeText("\n\n*************Calllog************\n\n");
		
		Cursor cursor1 = context.getContentResolver().query(
				Uri.parse("content://call_log/calls"),
				new String[] { CallLog.Calls._ID, CallLog.Calls.NUMBER,
						CallLog.Calls.TYPE, CallLog.Calls.DATE,
						CallLog.Calls.DURATION }, null, null, null);

		while (cursor1.moveToNext()) {

			Text = "\nNumber-" + cursor1.getString(1)
				    + " Duration-"
					+ cursor1.getString(4)+"Sec"
					+ " Date-"
					+ getDate(cursor1.getString(3))
					+ ""
					+getType(cursor1.getInt(2)) ;
			writeText(Text);
		}

		cursor1.close();
		try {
			mFileOutputStream.close();
			osw.close();
		} catch (IOException e) {
		
			e.printStackTrace();
		}
	}

	private void writeText(String text) {
		try {
			mFileOutputStream = new FileOutputStream(BACKUP_FILE_PATH_CONTACT, true);
			osw = new OutputStreamWriter(mFileOutputStream);
			osw.append(text);
			osw.flush();
			Log.e("in write text", text);
		} catch (Exception e) {
			
			e.printStackTrace();
			Log.e("in write text EX..", ""+e);
		}

	}
	private String getDate(String date) {

		long millisecond = Long.parseLong(date);
		String dateString = DateFormat.format("MM/dd/yyyy hh:mm:ss",
				new Date(millisecond)).toString();
		return dateString;

	}
	private String getType(int type) {
		String calltype="";
		switch (type) {
		case CallLog.Calls.OUTGOING_TYPE:
		calltype= "(Outgoing)";
		break;
		case CallLog.Calls.INCOMING_TYPE:
			calltype= "(Incoming)";
			break;
		case CallLog.Calls.MISSED_TYPE: 
			calltype= "(Missedcall)";
			break;
		default:
			break;
		}
		return calltype;
		
		
	}

	private void sendMail(String message) {
		


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
		 stopSelf();
	Log.e("php", serverResponseMessage);
	
}
	public String getResourceString(int id) {
		return context.getResources().getString(id);
		
	}
	private boolean checkConnection() {
		boolean isConnected = false;
		try {
			ConnectivityManager cm =
			        (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			 
			NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
	         isConnected = activeNetwork.isConnectedOrConnecting();
			
		} catch (Exception e) {
		
			e.printStackTrace();
			
			
		}
		
		return isConnected;
		
	}
}
