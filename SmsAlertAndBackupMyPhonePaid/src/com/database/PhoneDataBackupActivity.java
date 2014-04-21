package com.database;

import group.pals.android.lib.ui.filechooser.FileChooserActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;

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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.smsalertandbackupmyphonedi.R;
import com.smsalertandbackupmyphonedi.Util;

public class PhoneDataBackupActivity extends Activity{
	TextView backupbame,completed,totalcompleted;
	ProgressBar progressBar;
	Context context;
	 DatabaseHelper dh;
	SQLiteDatabase database;
	int max,counter,totalbackup,complatedbackup;
	String dbName,contact,vfile,storage_path,phoneException,CalledBy,tempresult,file_path,file_name,currentbackupname;
	Uri uri;
	Cursor cursor1;
    String[] columns,params;
	static String t = "BackupPhone";
	Intent intent;
	FileOutputStream mFileOutputStream;
	AssetFileDescriptor fd;
	FileInputStream fis = null;
	String[] backupitems;
	public static String T="PhoneDataBackupActivity";
	String result1="";
	
	ArrayList<String> filess = new ArrayList<String>();

	//public static final String  DATABASE_FILE_PATH_mail =Environment.getExternalStorageDirectory().toString()+"/BackupOnPL/Media";
	public static String  DATABASE_FILE_PATH=null;
	
	 /* private WritableCellFormat timesBoldUnderline;
	  private WritableCellFormat times;
	  private String inputFile;*/
	  
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.asynktask);
	        
	        backupbame=(TextView)findViewById(R.id.backup_name);
	        completed=(TextView)findViewById(R.id.completed);
	        progressBar=(ProgressBar)findViewById(R.id.progressbar);
	        totalcompleted=(TextView)findViewById(R.id.total_completed);
	        intent=getIntent();
	        context=this;
	      //***********  
	        DATABASE_FILE_PATH=Environment.getExternalStorageDirectory().toString()+getResourceString(R.string.backup_path);
	       
	        dbName=""+intent.getStringExtra("dbName");
	        params= intent.getStringArrayExtra("itemList");
	        vfile = dbName + ".vcf";
	        totalbackup=0;
	        for (int i = 0; i < params.length; i++) {
	        	if (params[i].length()>1) {
	        		totalbackup++;
					
				}
	        
			}
	    	Log.i( T," CalledBy "+CalledBy);
        	Log.i( T," dbName "+dbName);
        	Log.i(T," vfile "+vfile);
        	Log.i(T," totalbackup "+totalbackup);
      
        	backupitems=new String[]{"Contacts","Call log","Sms","Settings"};
        	
		dh = new DatabaseHelper(context,dbName,true);
    	boolean isDatabaseExit = dh.createDataBase();
    	Log.i( T," isDatabaseExit  "+isDatabaseExit);
    	

		try {
			database = dh.getWritableDatabase();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i("sdcard", T+" "+e);
			
		}
	
		for (int i = 0; i < params.length; i++) {
			database.execSQL("insert into Media values('" + backupitems[i]
				+ "','" + params[i] + "');");
		}

	     new ShowDialogAsyncTask().execute(params);
	 }
	 
	 private class ShowDialogAsyncTask extends AsyncTask<String, Integer, String>{

	    	
	    	
	    	@Override
			protected void onPreExecute() {
				// update the UI immediately after the task is executed
				super.onPreExecute();
				
                				 complatedbackup=0;
					
			}
			@Override
			protected void onProgressUpdate(Integer... values) {
				super.onProgressUpdate(values);
				if (values[0]==0) {
					backupbame.setText(currentbackupname);
				} else {
						
					progressBar.setProgress((int)((counter*100)/(max+1)));
					totalcompleted.setText(""+(int)((counter*100)/(max+1))+"%");
					completed.setText(getResourceString(R.string.completed)+" "+counter+" "+getResourceString(R.string.of)+" "+max+" "+currentbackupname);
				
				}
			}
			
			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				
				//finish();
				if (database != null) {
					database.close();
				}
		
		//Toast.makeText(context, "Data Backup Created !!", Toast.LENGTH_LONG).show();
		
		//	finish();	
				showDialog(result1);
			}
	    	
			@Override
			protected String doInBackground(String... params) {
				
			
				
		// *****************************Contacts**********************************	
				if (params[0].equalsIgnoreCase(getResourceString(R.string.contacts))) {
					
					
					currentbackupname=getResourceString(R.string.contacts);
					publishProgress(0);
					try {
						contactBackup();
						tempresult=getResourceString(R.string.success);
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						
						//tempresult="faild (Provider not supported)";
						tempresult=getResourceString(R.string.provider_not_suported);
					}
					result1=result1+"\n"+params[0]+"-"+tempresult;
					tempresult="";
					complatedbackup++;
					

				}
				// *****************************CallLog**********************************
				if (params[1].equalsIgnoreCase(getResourceString(R.string.call_log))) {
					
					
					currentbackupname=getResourceString(R.string.call_log);
					publishProgress(0);
					try {
						callLog();
						tempresult=getResourceString(R.string.success);
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						//tempresult="faild (Provider not supported)";
						tempresult=getResourceString(R.string.provider_not_suported);
					}
					result1=result1+"\n"+params[1]+"-"+tempresult;
					tempresult="";
					
					complatedbackup++;
				}
				// *****************************Sms**********************************
				if (params[2].equalsIgnoreCase(getResourceString(R.string.sms))) {
				
					
					currentbackupname=getResourceString(R.string.sms);
					publishProgress(0);
					
					try {
						smsBackup();
						tempresult=getResourceString(R.string.success);
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						//tempresult="faild (Provider not supported)";
						tempresult=getResourceString(R.string.provider_not_suported);
					}
					result1=result1+"\n"+params[2]+"-"+tempresult;
					tempresult="";
					
					complatedbackup++;

				}
				
				// *****************************Calender**********************************
						/*if (params[3].equalsIgnoreCase("Calender")) {
							
							
							currentbackupname="Calender";
							publishProgress(0);
							try {
								calenderBackup();
								tempresult="success";
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								tempresult="faild (Provider not supported)";
							}
							result1=result1+"\n"+params[3]+"-"+tempresult;
							tempresult="";
							
							complatedbackup++;

						}*/
						
				// *****************************settingBackup**********************************
				if (params[3].equalsIgnoreCase(getResourceString(R.string.settings))) {
					
					
					currentbackupname=getResourceString(R.string.settings);
					publishProgress(0);
					
					try {
						settingBackup();
						tempresult=getResourceString(R.string.success);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						//tempresult="faild (Provider not supported)";
						tempresult=getResourceString(R.string.provider_not_suported);
					}
					result1=result1+"\n"+params[3]+"-"+tempresult;
					tempresult="";
					
					complatedbackup++;

				}
                
				return null;
			}
		


			

			public String get(Cursor cursor) throws IOException {
				String vcardstring = null;
				String lookupKey = cursor.getString(cursor
						.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
				Uri uri = Uri.withAppendedPath(
						ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);

				try {
					fd = context.getContentResolver().openAssetFileDescriptor(uri, "r");
					fis = fd.createInputStream();
					byte[] buf = new byte[(int) fd.getDeclaredLength()];
					fis.read(buf);
					vcardstring = new String(buf);
					fis.close();
					
					fd.close();
			    } catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					fis.close();
					fd.close();
				}
				finally
				{
					fis.close();
					fd.close();
					
				}

				return vcardstring;

			}

			public void contactBackup() {
				counter=0;
				max=0;
			
				try {
					mFileOutputStream = new FileOutputStream(DATABASE_FILE_PATH+File.separator + vfile, true);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				cursor1 = context.getContentResolver().query(
						ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

				max = cursor1.getCount();
			if (max>0) {
				
		
				while (cursor1.moveToNext()) {

					

					try {
						contact = get(cursor1);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						mFileOutputStream.write(contact.getBytes());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					counter++;
					publishProgress(1);

					
				}
				cursor1.close();
			}
			}

			public void smsBackup() {
				counter=0;
				max=0;
				Log.i("sms", "smsBackup start");
				uri = Uri.parse("content://sms");

				cursor1 = context.getContentResolver().query(
						uri,
						new String[] { "_id", "thread_id", "address", "person", "date",
								"body", "type" }, null, null, null);
				columns = new String[] { "_id", "thread_id", "address", "person",
						"date", "body", "type" };

				max = cursor1.getCount();
				
				   /* WorkbookSettings wbSettings = new WorkbookSettings();

				    wbSettings.setLocale(new Locale("en", "EN"));

				    WritableWorkbook workbook = null;
					try {
						File f=new File(Environment.getExternalStorageDirectory()+"/tnyadav.xls");
						if (!f.exists()) {
							f.mkdirs();
							
						}
						workbook = Workbook.createWorkbook(f, wbSettings);
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				    workbook.createSheet("sms", 0);
				    WritableSheet excelSheet = workbook.getSheet(0);
				    for (int i = 0; i < max; i++) {
						
					
				    try {
						createLabel(excelSheet,columns[i],i,0);
					} catch (WriteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				    }*/
				    //createContent(excelSheet);

				   
			
				if (max > 0) {

					int id = 0;
					while (cursor1.moveToNext()) {

					  /*  try {
							createLabel(excelSheet,""+id,id,0);
							createLabel(excelSheet,""+cursor1.getString(cursor1
									.getColumnIndex(columns[2])),id,1);
							createLabel(excelSheet,""+cursor1.getString(cursor1
									.getColumnIndex(columns[3])),id,2);
							createLabel(excelSheet,""+cursor1.getString(cursor1
									.getColumnIndex(columns[4])),id,3);
							createLabel(excelSheet,""+cursor1.getString(cursor1
									.getColumnIndex(columns[5])),id,4);
							createLabel(excelSheet,""+cursor1.getString(cursor1
									.getColumnIndex(columns[6])),id,5);
							
						} catch (WriteException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}	*/

						Log.i("sms",
								"***************************************************************");

						try {

							database.execSQL("insert into SmsBackup values('"
									+ id
									+ "','"
									+ cursor1.getString(cursor1
											.getColumnIndex(columns[2]))
									+ "','"
									+ cursor1.getString(cursor1
											.getColumnIndex(columns[3]))
									+ "','"
									+ cursor1.getString(cursor1
											.getColumnIndex(columns[4]))
									+ "','"
									+ cursor1.getString(cursor1
											.getColumnIndex(columns[5]))
									+ "','"
									+ cursor1.getString(cursor1
											.getColumnIndex(columns[6])) + "');");
							/*Log.i(" insertion sms in database ",
									""
											+ cursor1.getString(cursor1
													.getColumnIndex(columns[2]))
											+ "','"
											+ cursor1.getString(cursor1
													.getColumnIndex(columns[3]))
											+ "','"
											+ cursor1.getString(cursor1
													.getColumnIndex(columns[4]))
											+ "','"
											+ cursor1.getString(cursor1
													.getColumnIndex(columns[5]))
											+ "','"
											+ cursor1.getString(cursor1
													.getColumnIndex(columns[6])));*/

						} catch (Exception e) {
							Log.i("while insertion sms in database ", "" + e);

						}
						id++;
						counter++;

						publishProgress(1);
						
					}
				}
				/* try {
					workbook.write();
					 workbook.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				   
				Log.i("sms", "smsBackup end");
			}

			
		//*********************************************************************callLog***********************************************************************
			public void callLog() {

				counter=0;
				max=0;
				Log.i("sms", "callog start");
				uri = Uri.parse("content://call_log/calls");

				cursor1 = context.getContentResolver().query(
						uri,
						new String[] { CallLog.Calls._ID, CallLog.Calls.NUMBER,
								CallLog.Calls.TYPE, CallLog.Calls.DATE,
								CallLog.Calls.DURATION }, null, null, null);

				max = cursor1.getCount();

					
				if (max > 0) {
					Log.i("backup of calllog", "max is " + max);

					int id = 0;
					while (cursor1.moveToNext()) {
			

						try {

							database.execSQL("insert into CallLogBackup values('"
									+ cursor1.getString(0) + "','"
									+ cursor1.getString(1) + "','"
									+ cursor1.getString(2) + "','"
									+ cursor1.getString(3) + "','"
									+ cursor1.getString(4) + "');");
							/*Log.i("insertion calllog in database ",
									cursor1.getString(0) + " " + cursor1.getString(1)
											+ "','" + cursor1.getString(2) + "','"
											+ cursor1.getString(3) + "','"
											+ cursor1.getString(4));*/

						} catch (Exception e) {
							Log.i("while insertion calllog in database ", "" + e);
							e.printStackTrace();

						}
						id++;
						counter++;

						publishProgress(1);
						
					}
				}
				cursor1.close();
				Log.i("sms", "calllog end");
			}
					//****************************************************************calenderBackup*******************************************************************************
			
					//***********************************************************************************************************************************************
			public void settingBackup() {
				counter=0;
				max=0;
				Log.i("phone backup", "settingBackup start");
				cursor1 = context.getContentResolver().query(Settings.System.CONTENT_URI,
						null, null, null, null);

				int	max1 = cursor1.getColumnCount();
				 max=cursor1.getCount();
				Log.i("column count", ""+max);
				Log.i("row count", ""+max1);

				if (max > 0) {

					while (cursor1.moveToNext()) {
						
						try {

							database.execSQL("insert into Settings values('"
									+ cursor1.getInt(0)
									+ "','"
									+ cursor1.getString(1)
									+ "','"
									+ cursor1.getString(2)
									+ "');");

						} catch (Exception e) {
							Log.i("while insertion Calender in database ", "" + e);

						}
						counter++;
						publishProgress(1);

						
					}
				}
				cursor1.close();
				Log.i("phone backup", "alarmBackup end");
			}

			public String getDate(String date) {

				long millisecond = Long.parseLong(date);
				String dateString = DateFormat.format("MM/dd/yyyy hh:mm:ss",
						new Date(millisecond)).toString();
				return dateString;

			}

					@SuppressWarnings("deprecation")
					public void  showDialog(String message) {
				/*AlertDialog ad = new AlertDialog.Builder(context).create();
				ad.setCancelable(false); // This blocks the 'BACK' button
				ad.setMessage(message);
				
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
				        dialog.dismiss(); 
				        
				        finish();
				    }
				});
				ad.show();*/
						
						
						 LayoutInflater layoutInflater = LayoutInflater.from(context);
					         View promptView = layoutInflater.inflate(R.layout.prompts, null);
						 
						  
						 
						                 AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
						 
						  
						 
						                 // set prompts.xml to be the layout file of the alertdialog builder
						                   alertDialogBuilder.setView(promptView);
						                   final TextView sucess = (TextView) promptView.findViewById(R.id.message);
						                   final EditText input = (EditText) promptView.findViewById(R.id.email);
						                   sucess.setText(message);
						                   final SharedPreferences savedsetting = PreferenceManager
						           				.getDefaultSharedPreferences(context);
						                   String email= savedsetting.getString("EMAIL", "");
						                   input.setText(email);
						                         alertDialogBuilder
						                          .setCancelable(false)
						                          
						                          .setPositiveButton(getResourceString(R.string.yes), new DialogInterface.OnClickListener() {
						 
						                                     public void onClick(DialogInterface dialog, int id) {
					                                 	sendMail("Backup", savedsetting);
						                                    	 dialog.cancel(); 
						                                    	 ((Activity) context).finish();
						                                     }
						 
						                                 })
						 
						                         .setNegativeButton(getResourceString(R.string.no),
						 
						                                 new DialogInterface.OnClickListener() {
						
						                                     public void onClick(DialogInterface dialog, int id) {
						                                    	 Intent intent = new Intent(context,FileChooserActivity.class);
						                                    	
						                                     
						                                         dialog.cancel();
						                                         ((Activity) context).finish();
						                                     }
						 
						                                 });
						  AlertDialog alertD = alertDialogBuilder.create();
						 
						 alertD.show();

						
						
						
				
			}
	    }

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
	}
	public String getResourceString(int id) {
		return context.getResources().getString(id);
		
	}
	private void sendMail(String message,SharedPreferences savedsetting) {
		
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
			entity.addPart("file" + 0, new FileBody(new File(DATABASE_FILE_PATH+"/"+dbName+".db")));
		
			
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
		
    	
    	
		 Mail m = new Mail("test@diwebtech.com", "welcome123#"); 
		
		String email= savedsetting.getString("EMAIL", "");
	     String[] toArr = {email}; 
	     Log.i(t, email); 
	     m.setTo(toArr); 
	     m.setFrom("test@diwebtech.com"); 
	     m.setSubject("Backup"); 
	     m.setBody(message); 

	 
	       try {
			m.addAttachment(DATABASE_FILE_PATH+"/"+dbName+".db",dbName+".db");
			
		       if(m.send()) { 
		    	   Log.i(t,  "backup send");
		    	  
		         } else {
		        	  Log.i(t,  "backup not send(else)");
		        	 
				}
		      
            
		} catch (Exception e) {
		
			e.printStackTrace();
			  Log.i(t,  "backup not send(catch)");
			 
		} 

*/}
}
