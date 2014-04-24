package com.smsalertandbackupmyphonedi.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.smsalertandbackupmyphonedi.BackupService;
import com.smsalertandbackupmyphonedi.GeoLocationUtil;
import com.smsalertandbackupmyphonedifree.R;

public class SMSReceiver extends BroadcastReceiver {

	
	private Context context;
    public static String t="SMSReceiver";
    SharedPreferences savedsetting;
	// public static String Longitud;
	@Override
	public void onReceive(final Context context, Intent intent) {
		this.context = context;
		Bundle bundle = intent.getExtras();
		 savedsetting = PreferenceManager
				.getDefaultSharedPreferences(context);

		boolean smssetting = savedsetting.getBoolean("SMSSETTING", false);
		boolean backupsetting = savedsetting.getBoolean("BACKUPSETTING", false);
		boolean gpssetting = savedsetting.getBoolean("GPSSETTING", false);

		if (smssetting || backupsetting||gpssetting) {

			Object messages[] = (Object[]) bundle.get("pdus");
			android.telephony.SmsMessage smsMessage[] = new android.telephony.SmsMessage[messages.length];
			String strSavedkeyword = savedsetting.getString("KEYWORDS", "");
			Log.i("MyApp strSavedkeyword", " " + strSavedkeyword);
			for (int n = 0; n < messages.length; n++) {
				smsMessage[n] = android.telephony.SmsMessage
						.createFromPdu((byte[]) messages[n]);
				Log.i("MyApp smsMessage[n]", "" + smsMessage[n]);
			}
			String arr[] = strSavedkeyword.split(",");
			Log.i("MyApp arr.length", "" + arr.length);
			for (int n = 0; n < arr.length; n++) {
				Log.i("MyApp arr[n]", " " + arr[n]);
			}
			if (smssetting && checkKeywords(smsMessage, arr)) {
				String strSaveduri = savedsetting.getString("RINGTONE", "");

				final AudioManager am = (AudioManager) context
						.getSystemService(Context.AUDIO_SERVICE);
				final Ringtone r = RingtoneManager.getRingtone(context,
						Uri.parse(strSaveduri));
                int tonelength=savedsetting.getInt("TONELENGTH",0);
				final Handler handler = new Handler();
				
				
				switch (am.getRingerMode()) {
				case AudioManager.RINGER_MODE_SILENT:
					Log.i("MyApp", "Silent mode");

					am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

					r.play();

					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							r.stop();
							am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
						}
					},tonelength*1000 );
					// am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
					break;
				case AudioManager.RINGER_MODE_VIBRATE:
					Log.i("MyApp", "Vibrate mode");

					am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

					r.play();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							r.stop();
							am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
						}
					},tonelength*1000);
					break;

				}
				
				
				

			}


			if (backupsetting) {
				

				if (smsMessage[0].getMessageBody().equals(getResourceString(R.string.backup))) {
					Intent intent1 = new Intent(context, BackupService.class);
					String newimsi = "";
					TelephonyManager mTelephonyMgr = (TelephonyManager) context
							.getSystemService(Context.TELEPHONY_SERVICE);
					newimsi = mTelephonyMgr.getSubscriberId();
					
					intent1.putExtra("msg", getResourceString(R.string.your_phone_imsi)+" " + newimsi);
					intent1.putExtra("delay", 10000);
					//intent1.putExtra("request", 1);
					context.startService(intent1);

				}
				if (smsMessage[0].getMessageBody().equals(getResourceString(R.string.delete))) {

					try {
						deleteAllDataFromPhone(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						deleteAllDataFromPhone(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
						deleteAllDataFromPhone(MediaStore.Video.Media.EXTERNAL_CONTENT_URI);

					} catch (Exception e) {

					}

				}
				if (smsMessage[0].getMessageBody().equals(getResourceString(R.string.hide))) {

					try {
						ComponentName componentToDisable = new ComponentName(
								"com.smsalertandbackupmyphonedifree",
								"com.smsalertandbackupmyphonedi.CheckPasswordActivity");
						context.getPackageManager()
								.setComponentEnabledSetting(
										componentToDisable,
										PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
										PackageManager.DONT_KILL_APP);

						Intent home_intent = new Intent(
								"android.intent.action.MAIN");
						home_intent.addCategory("android.intent.category.HOME");
						home_intent
								.addCategory("android.intent.category.DEFAULT");
						home_intent
								.removeCategory("android.intent.category.LAUNCHER");
					} catch (Exception e) {
						
						e.printStackTrace();
					}

				}
			}
			if (gpssetting) {
				if (smsMessage[0].getMessageBody().equals(getResourceString(R.string.gps))) {
					Log.i(t, "GPS");
					
					 
					LocationManager manager = (LocationManager) context.getSystemService( Context.LOCATION_SERVICE );

				    if ( manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
				    	Log.i(t, "GPS is anable");
				    	sendMessage();
				    
				    }
					else {
						Log.i(t, "GPS is disableable");
						 try {
								String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
								  if(!provider.contains("gps")){
								    final Intent poke = new Intent();
								    poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
								    poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
								    poke.setData(Uri.parse("3")); 
								    context.sendBroadcast(poke);
								    Log.i(t, "GPS is anable by coading");
								    sendMessage();  
								  
								}
							} catch (Exception e2) {
								// TODO Auto-generated catch block
								e2.printStackTrace();
								Log.i(t, "field to anabling gps");
							}
						 
						 
					}
						 

			      }

				}
			
			}

		}

	

	private boolean checkKeywords(android.telephony.SmsMessage smsMessage[],
			String arr[]) {
		boolean keyword = false;
		for (int i = 0; i < arr.length; i++) {
			// System.out.println(arr[i]+"tnyadav "+
			// smsMessage[0].getMessageBody());

			if (smsMessage[0].getMessageBody().contains(arr[i])) {
				// Log.i("MyApp",""+smsMessage[0].getMessageBody().contains(arr[i])
				// );
				keyword = true;
			}
		}
		return keyword;

	}

	public void deleteAllDataFromPhone(Uri uri) {
		try {
			context.getContentResolver().delete(uri, null, null);
			Log.i("deleteAllDataFromPhone", uri + "deleted");
		} catch (Exception e) {
		
			e.printStackTrace();
			Log.i("deleteAllDataFromPhone", "" + e);
		}

	}
	 public String getResourceString(int id) {
			return context.getResources().getString(id);
			
		}	
	 private void sendMessage() {
			Location loc= GeoLocationUtil.getGeoLocation(context); 
	    	String MNO1 =savedsetting.getString("GPSNO", "");
	    	if(loc != null)
	    	{
	    		Log.i(t, "loc is not null");
	    		String Latitude = String.valueOf(loc.getLatitude());
	    		String Longitud = String.valueOf(loc.getLongitude());
	    			Log.i("MainActivity", String.valueOf(loc.getLatitude() + " - " + String.valueOf(loc.getLongitude())));
	    			if (Latitude!=null&&Longitud!=null) {
	    				try {
	    					TelephonyManager mTelephonyMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
	    			       String msg="http://www.gorissen.info/Pierre/maps/googleMapLocation.php?lat="+Latitude+"&lon="+Longitud;
							android.telephony.SmsManager.getDefault()
									.sendTextMessage(MNO1, null,
											"IMEI-"+mTelephonyMgr.getDeviceId()+"\n"+msg,
											null, null);
							

						} catch (Exception e) {

						}
					}
	    			
	    			
	    	}
	}
}
