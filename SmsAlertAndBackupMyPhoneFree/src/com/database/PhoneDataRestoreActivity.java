package com.database;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.smsalertandbackupmyphonedifree.R;

public class PhoneDataRestoreActivity extends Activity {
	TextView backupbame, completed, totalcompleted;
	ProgressBar progressBar;
	Context context;
	SQLiteDatabase database;
	int max, counter, totalbackup, complatedbackup;
	String databasename, contact, vfile, storage_path, phoneException,
			tempresult, file_path, file_name, currentbackupname;
	Uri uri;
	Cursor cursor1;
	String[] columns, params;
	static String t = "BackupPhone";
	boolean smstask = true;
	Intent intent;
	FileOutputStream mFileOutputStream;

	AssetFileDescriptor fd;
	FileInputStream fis = null;
	String[] backupitems;
	private String result1 = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.asynktask);

		backupbame = (TextView) findViewById(R.id.backup_name);
		completed = (TextView) findViewById(R.id.completed);
		progressBar = (ProgressBar) findViewById(R.id.progressbar);
		totalcompleted = (TextView) findViewById(R.id.total_completed);

		intent = getIntent();
		context = this;
		databasename = "" + intent.getStringExtra("dbName");
		params = intent.getStringArrayExtra("itemList");
		vfile = databasename + ".vcf";

		totalbackup = 0;
		for (int i = 0; i < params.length; i++) {
			if (params[i].length() > 1) {
				totalbackup++;

			}

		}

		Log.i("databasename", "" + databasename);

		Log.i("totalbackup", "" + totalbackup);
		backupitems = context.getResources().getStringArray(R.array.backupitems);

		new ShowDialogAsyncTask().execute(params);
	}

	private class ShowDialogAsyncTask extends
			AsyncTask<String, Integer, String> {

		//int progress_status;

		@Override
		protected void onPreExecute() {
			// update the UI immediately after the task is executed
			super.onPreExecute();

			database = SelectTableFromDatabaseActivity.database;

			complatedbackup = 0;

		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);

			if (values[0] == 0) {
				backupbame.setText(currentbackupname);
			} else {
				
				progressBar.setProgress((int) ((counter * 100) / (max+1)));
				totalcompleted
						.setText("" + (int) ((counter * 100) / (max+1)) + "%");
				completed.setText((R.string.completed)+" " + counter +(R.string.of) +" " + max + " "
						+ currentbackupname);
			}

		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (database != null) {
				database.close();
			}

			for (int i = 0; i < params.length; i++) {
				if (params[i].equalsIgnoreCase(getResourceString(R.string.contacts))) {
					Intent i1 = new Intent();
					i1.setAction(android.content.Intent.ACTION_VIEW);
					i1.setDataAndType(Uri
							.parse("file:///mnt/sdcard//BackupOnPL/Media/"
									+ databasename + ".vcf"), "text/x-vcard");
					context.startActivity(i1);

				}
			}
			finish();
			// showDialog(result1);

		}

		@Override
		protected String doInBackground(String... paramss) {

			// *****************************Contacts**********************************

			counter = 0;

			// *****************************CallLog**********************************
			for (int i = 0; i < paramss.length; i++) {
				if (paramss[i].equalsIgnoreCase(getResourceString(R.string.call_log))) {

					currentbackupname = getResourceString(R.string.call_log);
					publishProgress(0);
					try {
						callLog();
						tempresult =getResourceString(R.string.success);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						tempresult =getResourceString(R.string.provider_not_suported);
					}
					result1 = result1 + "\n" + paramss[i] + "-" + tempresult;
					tempresult = "";
					smstask = true;
					counter = 0;
					complatedbackup++;

				}
			}
			// *****************************Sms**********************************

			for (int i = 0; i < paramss.length; i++) {
				if (paramss[i].equalsIgnoreCase(getResourceString(R.string.sms))) {
					currentbackupname =getResourceString(R.string.sms);
					publishProgress(0);
					try {
						smsRestore();
						tempresult =getResourceString(R.string.success);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						tempresult =getResourceString(R.string.provider_not_suported);
					}
					result1 = result1 + "\n" + paramss[i] + "-" + tempresult;
					tempresult = "";
					smstask = true;
					counter = 0;
					complatedbackup++;

				}
			}

			for (int i = 0; i < paramss.length; i++) {
				if (paramss[i].equalsIgnoreCase(getResourceString(R.string.settings))) {
					currentbackupname =getResourceString(R.string.settings);
					publishProgress(0);
					try {
						Setting();
						;
						tempresult =getResourceString(R.string.success);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						tempresult =getResourceString(R.string.provider_not_suported);
					}
					result1 = result1 + "\n" + paramss[i] + "-" + tempresult;
					tempresult = "";

					smstask = true;
					counter = 0;
					complatedbackup++;

				}
			}

			return null;
		}

		public void smsRestore() {
			counter = 0;
			max = 0;
			Log.i("phone restore", "smsRestore start");
			uri = Uri.parse("content://sms");
			cursor1=null;
			cursor1 = database.rawQuery("select *from SmsBackup;", null);

			max = cursor1.getCount();
			Log.i("phone restore", "max is" + max);

			deleteAllDataFromPhone(uri);
			if (max > 0) {

				while (cursor1.moveToNext()) {

					try {

						ContentValues initialValues = new ContentValues();
						initialValues.put("address", cursor1.getString(1));
						initialValues.put("person", cursor1.getString(2));
						initialValues.put("date", cursor1.getString(3));
						initialValues.put("body", cursor1.getString(4));
						initialValues.put("type", cursor1.getString(5));
						context.getContentResolver().insert(uri, initialValues);

					} catch (Exception e) {
						Log.i("while insertion sms in database ", "" + e);

					}

					counter++;

					publishProgress(1);

				}
			}
			Log.i("phone restore", "smsRestore end");
		}

		public void callLog() {
			counter = 0;
			max = 0;
			Log.i("phone restore", "callog start");
			uri = CallLog.Calls.CONTENT_URI;
			cursor1=null;
			cursor1 = database.rawQuery("select *from CallLogBackup;", null);

			max = cursor1.getCount();

			deleteAllDataFromPhone(uri);
			if (max > 0) {

				while (cursor1.moveToNext()) {
					try {

						ContentValues initialValues = new ContentValues();
						initialValues.put(CallLog.Calls.NUMBER,
								cursor1.getString(1));
						initialValues.put(CallLog.Calls.TYPE,
								cursor1.getString(2));
						initialValues.put(CallLog.Calls.DATE,
								cursor1.getString(3));
						initialValues.put(CallLog.Calls.DURATION,
								cursor1.getString(4));

						context.getContentResolver().insert(uri, initialValues);

					} catch (Exception e) {
						Log.i("while insertion sms in database ", "" + e);

					}

					counter++;

					publishProgress(1);

				}
			}
			Log.i("phone restore", "callog end");
		}

		public void Setting() {
			counter = 0;
			max = 0;
			Log.i("phone Setting", "callog start");
			cursor1=null;
			cursor1 = database.rawQuery("select *from Settings;", null);

			max = cursor1.getCount();

			deleteAllDataFromPhone(Settings.System.CONTENT_URI);
			if (max > 0) {

				while (cursor1.moveToNext()) {
					try {

						ContentValues initialValues = new ContentValues();
						initialValues.put("_id", cursor1.getInt(0));
						initialValues.put("name", cursor1.getString(1));
						initialValues.put("value", cursor1.getString(2));

						context.getContentResolver().insert(
								Settings.System.CONTENT_URI, initialValues);
						Log.i("while insertion Setting in database ",
								"id-" + cursor1.getInt(0) + " name-"
										+ cursor1.getString(1) + " value-"
										+ cursor1.getString(2));

					} catch (Exception e) {
						Log.i("while insertion Setting in database ", "" + e);

					}

					counter++;

					publishProgress(1);

				}
			}
			Log.i("phone restore", "Setting end");
		}

		public int deleteAllDataFromPhone(Uri uri) {
			int count = 0;

			try {
				context.getContentResolver().delete(uri, null, null);
				Log.i("deleteAllDataFromPhone", uri + "deleted");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.i("deleteAllDataFromPhone", "" + e);
			}

			return count;

		}

	}
	public String getResourceString(int id) {
		return context.getResources().getString(id);
		
	}
}
