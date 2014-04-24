package com.smsalertandbackupmyphonedi;

import android.content.Context;
import android.telephony.TelephonyManager;

public class Util {
	
	public static final String FILE_UPLOAD_URL="http://dicorporation.com/PlanB/Services/backuppl_paid_uploadfiles.php";
	public static final String GOOGLE_SHARE_URL="https://play.google.com/store/apps/details?id=com.smsalertandbackupmyphonedifree";
	public static final String AMEZON_RATE_URL="http://www.amazon.com/gp/mas/dl/android?p=com.smsalertandbackupmyphonedifree";
	public static final String PLANB_URL="https://play.google.com/store/apps/details?id=com.planbdi";
	
	public static final boolean isGoogle = true;
	public static final boolean testMode = false;
	
	public static String getIMEI(Context context)
	{
		TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();	
	}
	
	public static String DateTime(Context context)
	{
		return android.text.format.DateFormat
				.format("dd-MM-yyyy",
						new java.util.Date())
				.toString();
	}
	public static String sustemDateTime()
	{
		return ""+System.currentTimeMillis();
	}
}
