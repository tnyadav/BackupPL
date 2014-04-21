package com.smsalertandbackupmyphonedi.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.smsalertandbackupmyphonedi.BackupService;
import com.smsalertandbackupmyphonedi.R;


public class SIMChangeReceiver extends BroadcastReceiver {
	private final String BOOT_COMPLETED_ACTION = "android.intent.action.BOOT_COMPLETED";
	private static String t="SIMChangeReceiver";
 
	 @Override
	 public void onReceive( Context mContext, Intent intent) {
	  
	  Log.i(t,"onReceive");
	//  Toast.makeText(mContext, t, 3).show();
	  if(intent.getAction().equals(BOOT_COMPLETED_ACTION))
	 {
		 Log.i(t,"boot completed");
		 SharedPreferences savedsetting = PreferenceManager
					.getDefaultSharedPreferences(mContext);
		    boolean smssetting = savedsetting.getBoolean("SMSSETTING", false);
			boolean backupsetting = savedsetting.getBoolean("BACKUPSETTING", false);
			 Log.i(t,"smssetting-"+smssetting);
			 Log.i(t,"backupsetting-"+backupsetting);
			String newimsi="";
			TelephonyManager mTelephonyMgr = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
	        newimsi = mTelephonyMgr.getSubscriberId();
	        SharedPreferences sharedPreferences = mContext.getSharedPreferences("imsi",0);
	        String strSavedIMSI= sharedPreferences.getString("IMSI", "");
	        
	       // Toast.makeText(mContext, ""+strSavedIMSI.equals(newimsi), 3).show();
	        
	        if (strSavedIMSI.equals(newimsi) == false) {
	        	final String message =  mContext.getResources().getString(R.string.your_phone_with_imsi)+" " + strSavedIMSI
						+  mContext.getResources().getString(R.string.is_having_new_sim_card_with_imsi)+" "
						+ newimsi;
	       	 Log.i(t,"sim chenged");
            if (smssetting) {
            	
    	        String MNO1= sharedPreferences.getString("MNO1", "");
    	        String MNO2= sharedPreferences.getString("MNO2", "");
    	        String MNO3= sharedPreferences.getString("MNO3", "");
    	        String MNO4= sharedPreferences.getString("MNO4", "");
    	        
    	       
					
					String numbers[] = { MNO1, MNO2, MNO3, MNO4 };

					for (int i = 0; i < numbers.length; i++) {
						
						if (numbers[i].length() >= 10) {
							try {
					android.telephony.SmsManager.getDefault().sendTextMessage(numbers[i], null, message,
										null, null);

							} catch (Exception e) {
								
							}
						

						}

					}
					
				}
			
            if (backupsetting) {
            	Intent intent1=new Intent(mContext,BackupService.class);
                intent1.putExtra("msg",message);
                intent1.putExtra("delay", 60000);
               // intent1.putExtra("request", 2);
                mContext.startService(intent1);
            	
            	
        			
			 }
	        }
	     }
	 
	}
	
}
