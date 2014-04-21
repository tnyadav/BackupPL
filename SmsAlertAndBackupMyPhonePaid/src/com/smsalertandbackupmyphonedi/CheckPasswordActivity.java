package com.smsalertandbackupmyphonedi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CheckPasswordActivity extends Activity {
	

	private SharedPreferences savedsetting;

	private String checkPassWord;
	private EditText passWord;
	private EditText conformPassWord;
	private Button btnSave;
	private Button changePass;
	
	private static int dialogOpen = 0;

	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.check_password);

		context = this;
		passWord = (EditText) findViewById(R.id.et_checkPass_password);
		conformPassWord = (EditText) findViewById(R.id.et_checkPass_Con_password);
		btnSave = (Button) findViewById(R.id.btn_save);
		changePass=(Button)findViewById(R.id.changpass);
		
		/*try {
			checkExpireApp();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
	
		savedsetting = getSharedPreferences("savedsetting", MODE_PRIVATE);
		checkPassWord = savedsetting.getString("PASSWORD", "");

		if (checkPassWord.equals("")) {
			dialogOpen = 0;
			changePass.setVisibility(View.GONE);
		} else {
			btnSave.setText("");
			btnSave.setText(getResourceString(R.string.ok));
			conformPassWord.setVisibility(View.GONE);
			dialogOpen = 1;
		}

	}

	public void onClick(View view) {
		switch (view.getId()) {

		case R.id.btn_save:
			String pass = null;
			String conpass = null;

			pass = passWord.getText().toString();

			if (dialogOpen == 1) {
				if (pass.length()>0) {
					if (pass.equals(checkPassWord)) {
						Intent intent = new Intent(context,
								SmsAlertMainActivity.class);
						startActivity(intent);

					} else {
						Toast.makeText(context,getResourceString(R.string.password_notmatch),
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(context,getResourceString(R.string.pleas_enter_your_password),
							Toast.LENGTH_SHORT).show();
                         blink(passWord);
				}
				

			}
			if (dialogOpen == 0) {
				conpass = conformPassWord.getText().toString();
				if (pass.length()>0&&conpass.length()>0) {
					if (pass.equals(conpass)) {
						savedsetting.edit().putString("PASSWORD", pass).commit();
						Intent intent = new Intent(context,
								SmsAlertMainActivity.class);
						startActivity(intent);
					} else {
						Toast.makeText(context,getResourceString(R.string.password_notmatch),
								Toast.LENGTH_SHORT).show();
					}
				} else {
					if (pass.length()==0) {
						
						
						Toast.makeText(context,getResourceString(R.string.enter_all_fields),
								Toast.LENGTH_SHORT).show();	
							blink(passWord);
					
							
							
						
					}
                     if (conpass.length()==0) {
                    	 Toast.makeText(context,getResourceString(R.string.enter_all_fields),
     							Toast.LENGTH_SHORT).show();
                    	 blink(conformPassWord);
					}
					
				}
				

			}
			if (dialogOpen == 2) {
				conpass = conformPassWord.getText().toString();
				if (pass.length()>0&&conpass.length()>0) {
					if (pass.equals(checkPassWord)) {
						savedsetting.edit().putString("PASSWORD", conpass).commit();
						Intent intent = new Intent(context,
								SmsAlertMainActivity.class);
						startActivity(intent);
					} else {
						Toast.makeText(context,getResourceString(R.string.password_notmatch),
								Toast.LENGTH_SHORT).show();
					}
				} else {
					if (pass.length()==0) {
					
							
						Toast.makeText(context,getResourceString(R.string.enter_all_fields),
								Toast.LENGTH_SHORT).show();	
							blink(passWord);
					
							
							
						
					}
                     if (conpass.length()==0) {
                    	 Toast.makeText(context,getResourceString(R.string.enter_all_fields),
     							Toast.LENGTH_SHORT).show();
                    	 blink(conformPassWord);
					}
					
				}
				

			}
			break;

		case R.id.btn_cancel:

			finish();

			break;
		case R.id.changpass:
		       passWord.setHint(getResourceString(R.string.enter_current_pass));
		       conformPassWord.setHint(R.string.enter_new_pass);
			   conformPassWord.setVisibility(View.VISIBLE);	
			   changePass.setVisibility(View.GONE);
			   dialogOpen =2;
					break;
		}
		
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		finish();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		SharedPreferences sharedPreferences = getSharedPreferences("imsi", 0);
		if (sharedPreferences.getString("IMSI", "").length() > 1) {
			TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			String imsi = mTelephonyMgr.getSubscriberId();
			//Toast.makeText(context, imsi, 3).show();
		} else {
			TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			String imsi = mTelephonyMgr.getSubscriberId();
			sharedPreferences.edit().putString("IMSI", imsi).commit();
			//Toast.makeText(context, imsi, 3).show();
		}

	}

	@SuppressWarnings("deprecation")
	private void checkExpireApp() throws ParseException {
		// TODO Auto-generated method stub
		final SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy-MM-dd");
		final long ONE_DAY = 24 * 60 * 60 * 1000;
		savedsetting = getSharedPreferences("savedsetting", MODE_PRIVATE);
		String installDate = savedsetting.getString("InstallDate", null);
		if (installDate == null) {
			// First run, so save the current date

			Date now = new Date();
			String dateString = formatter.format(now);
			savedsetting.edit().putString("InstallDate", dateString).commit();
		} else {
			boolean exp = savedsetting.getBoolean("EXP", false);
			if (!exp) { 
				Date before = (Date) formatter.parse(installDate);
				Date now = new Date();

				System.out.println(now.getTime() + " now.getTime()"
						+ before.getTime() + " before.getTime()");
				long diff = now.getTime() - before.getTime();
				long days = diff / ONE_DAY;
				if (days > 45) {

					savedsetting.edit().putBoolean("EXP", true).commit();
					try {
						checkExpireApp();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}else {
				AlertDialog ad = new AlertDialog.Builder(context).create();
				ad.setCancelable(false); // This blocks the 'BACK' button
				ad.setMessage(getResourceString(R.string.expery_message));
				
				ad.setButton(getResourceString(R.string.ok), new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        dialog.dismiss(); 
				        
				        finish();
				    }
				});
				ad.show();
			}

		}
	}
	public String getResourceString(int id) {
		return context.getResources().getString(id);
		
	}
	private void blink(TextView tv){
	    
		Animation anim = new AlphaAnimation(0.0f, 1.0f);
		anim.setDuration(5); //You can manage the time of the blink with this parameter
		anim.setStartOffset(20);
		anim.setRepeatMode(Animation.REVERSE);
		anim.setRepeatCount(5);
		tv.startAnimation(anim);
	    }
}
