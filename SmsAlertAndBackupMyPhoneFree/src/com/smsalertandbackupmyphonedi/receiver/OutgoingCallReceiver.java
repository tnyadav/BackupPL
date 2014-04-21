package com.smsalertandbackupmyphonedi.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;


public class OutgoingCallReceiver extends BroadcastReceiver {

	
	 @Override
	 public void onReceive(Context context,Intent intent) {
		
	            Bundle bundle = intent.getExtras();
	            if (null == bundle)
	                return;
	            String phoneNubmer = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
	            System.out.println(phoneNubmer);
	            
	            if (phoneNubmer.equals("#12345")) {
	                setResultData(null);
	                PackageManager pm  = context.getPackageManager();

	                ComponentName componentToDisable =
	  				  new ComponentName("com.smsalertandbackupmyphonedifree",
	  				  "com.smsalertandbackupmyphonedi.CheckPasswordActivity");
	                pm.setComponentEnabledSetting(
	  				  componentToDisable,
	  				  PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
	  				  PackageManager.DONT_KILL_APP);
	  				
	  				  Intent home_intent = new Intent("android.intent.action.MAIN");
	  				  home_intent.addCategory("android.intent.category.LAUNCHER");
	              
	                Intent appIntent = new Intent(context, com.smsalertandbackupmyphonedi.CheckPasswordActivity.class);
	                appIntent.addCategory("android.intent.category.LAUNCHER");
	                appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	                context.startActivity(appIntent);
	                this.abortBroadcast();
	            }

	        }
}