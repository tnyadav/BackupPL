package com.smsalertandbackupmyphonedi;

import group.pals.android.lib.ui.filechooser.FileChooserActivity;
import group.pals.android.lib.ui.filechooser.io.localfile.LocalFile;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.database.SelectDataBackupActivity;
import com.database.SelectDatabaseFromSdcardActivity;

public class SmsAlertMainActivity extends Activity {

	public static  String  DATABASE_FILE_PATH=null;
	Context context;
	private static final int RESULT_SETTINGS = 1;
	 String shareUrl=null;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		
		context = this;
		
		Intent intent1=new Intent(context,BackupService.class);
       
        intent1.putExtra("delay", 600);
       // intent1.putExtra("request", 2);
        context.startService(intent1);
		
		
	    DATABASE_FILE_PATH =Environment.getExternalStorageDirectory().toString()+getResourceString(R.string.backup_path);
	   
		if (Util.isGoogle) {
			shareUrl = Util.GOOGLE_SHARE_URL;
		} else {
			shareUrl = Util.AMEZON_RATE_URL;
		}
	  
		File f=new File(DATABASE_FILE_PATH);
		if (!f.exists()) {
			f.mkdirs();
		}

	}

	public void onClick(View view) {
		Intent intent;
		switch (view.getId()) {
		case R.id.btn_backup:
			intent = new Intent(context, SelectDataBackupActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_restore:
            AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(context);
		
                    alertDialogBuilder1
                     .setMessage(R.string.restore_title)
                    .setPositiveButton(R.string.application, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int id) {

                               	 Intent intent = new Intent(context, SelectDatabaseFromSdcardActivity.class);
                        			startActivity(intent); 

                                }

                            })

                    .setNegativeButton(R.string.zip_file,

                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int id) {
                               	 Intent intent = new Intent(context,FileChooserActivity.class);
                               	 intent.putExtra(FileChooserActivity._RegexFilenameFilter, "(?si).*\\.(zip|7z)$");

                               	 //intent.putExtra(FileChooserActivity._RegexFilenameFilter, "zip");
                                        startActivityForResult(
                                                intent,
                                                0001);
                                
                                    dialog.cancel();

                                }

                            });
AlertDialog alertD = alertDialogBuilder1.create();

alertD.show();
			break;

		case R.id.btn_sms_setting:

			Intent i = new Intent(context, UserSettingActivity.class);
			startActivityForResult(i, RESULT_SETTINGS);
			break;

		case R.id.btn_sms_trace:

			intent = new Intent(context, AddContactActivity.class);
			startActivity(intent);

			break;

		case R.id.btn_app_hide:
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					context);
			alertDialogBuilder.setTitle(getResourceString(R.string.confermation));
			alertDialogBuilder
					.setMessage(
							getResourceString(R.string.hideapp_message))
					
					.setPositiveButton(getResourceString(R.string.conferm),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									ComponentName componentToDisable = new ComponentName(
											"com.smsalertandbackupmyphonedi",
											"com.smsalertandbackupmyphonedi.CheckPasswordActivity");
									getPackageManager()
											.setComponentEnabledSetting(
													componentToDisable,
													PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
													PackageManager.DONT_KILL_APP);

									Intent home_intent = new Intent(
											"android.intent.action.MAIN");
									home_intent
											.addCategory("android.intent.category.HOME");
									home_intent
											.addCategory("android.intent.category.DEFAULT");
									home_intent
											.removeCategory("android.intent.category.LAUNCHER");

								}
							})
					.setNegativeButton(getResourceString(R.string.cancel),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();

								}
							});
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();

			break;
		case R.id.btn_exit:
	
		showDialog(getResourceString(R.string.douwant_toexit));
			break;
			
		case R.id.share:
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.setType("text/plain");
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					getResourceString(R.string.download_backup_on_phone_lost));
			emailIntent
					.putExtra(android.content.Intent.EXTRA_TEXT,
							shareUrl);

			startActivity(Intent.createChooser(emailIntent,
					getResourceString(R.string.share_this_application_via)));

			break;
			case R.id.rate:
			   	Uri uri = Uri.parse(shareUrl);
				 Intent intent1 = new Intent(Intent.ACTION_VIEW, uri);
				 startActivity(intent1);
				 break;
				 case R.id.btn_help:
					startActivity(new Intent(context, HelpActivity.class));
					 break;

		}

	}
	@SuppressWarnings("deprecation")
	public void  showDialog(String message) {
		AlertDialog ad = new AlertDialog.Builder(context).create();
		ad.setCancelable(false); // This blocks the 'BACK' button
		ad.setMessage(message);
		
		ad.setButton(getResourceString(R.string.later), new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.dismiss(); 
		    	 finish();
		    }
		});
		ad.setButton2(getResourceString(R.string.download_now), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				 dialog.dismiss(); 	
				 Uri uri = Uri.parse(Util.PLANB_URL);
				 Intent intent1 = new Intent(Intent.ACTION_VIEW, uri);
				 startActivity(intent1);
			}
		});
		ad.show();
		
	}
	@SuppressWarnings("deprecation")
	public void  showDialog() {
		AlertDialog ad = new AlertDialog.Builder(context).create();
		ad.setCancelable(false); // This blocks the 'BACK' button
		ad.setMessage("");
		
		ad.setButton(getResourceString(R.string.yes), new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.dismiss(); 
		        
		        finish();
		    }
		});
		ad.setButton2(getResourceString(R.string.no), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				 dialog.dismiss(); 	
			}
		});
		ad.show();
		
	}
	public String getResourceString(int id) {
		return context.getResources().getString(id);
		
	}
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0001:      
            if (resultCode == RESULT_OK) {  
            	  @SuppressWarnings("unchecked")
				List<LocalFile> files = (List<LocalFile>)
                          data.getSerializableExtra(FileChooserActivity._Results);
                File path = files.get(0);
			
					new UnZipFileAsyncTask(context).execute(path.getAbsolutePath(),path.getName());
					
					
                Log.d("TAG", "File Path: " + path);
                // Get the file instance
                // File file = new File(path);
                // Initiate the upload
            }           
            break;
        }
    super.onActivityResult(requestCode, resultCode, data);
    }
}

