package com.database;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.util.Log;

import com.smsalertandbackupmyphonedi.R;

public class DatabaseHelper

{
	private static final String T  = "DatabaseHelper";
//	String mystring = getResources().getString(R.string.mystring);
	//public static final String  DATABASE_FILE_PATH_STRING =Environment.getExternalStorageDirectory().toString()+"/BackupOnPL/Media";
	public static String  DATABASE_FILE_PATH=null;
	public   String  DATABASE_NAME;
	Context context;
	private SQLiteDatabase database;
	
	public DatabaseHelper(Context context,String databasename,boolean which)
	{  
	
		Log.i("DatabaseHelper", "database helper cons.. called" );

		this.context=context;
		this.DATABASE_NAME=databasename+".db";
		if (which) {
			DATABASE_FILE_PATH=Environment.getExternalStorageDirectory().toString()+getResourceString(R.string.backup_path);	
		}
		else {
			DATABASE_FILE_PATH=Environment.getExternalStorageDirectory().toString()+getResourceString(R.string.backuppl);		
		}
		
	}



	public void close()
	{
		Log.i("DatabaseHelper", "database helper close.. called");
		if (database != null)
		{
			database.close();
		}
	}

	public SQLiteDatabase getReadableDatabase()
	{
		database = SQLiteDatabase.openDatabase(DATABASE_FILE_PATH

				+ File.separator + DATABASE_NAME, null,

				SQLiteDatabase.OPEN_READONLY);
		return database;
	}

	public SQLiteDatabase getWritableDatabase()
	{
		Log.i("sdcard", "DATABASE_FILE_PATH "+DATABASE_FILE_PATH);
		Log.i("sdcard", "DATABASE_NAME "+DATABASE_NAME);
		
		database = SQLiteDatabase.openDatabase(DATABASE_FILE_PATH

				+ File.separator + DATABASE_NAME, null,

				SQLiteDatabase.OPEN_READWRITE);

		return database;
	}
	public boolean createDataBase() {
    Log.i("sdcard", " create data base called");
		try {
			boolean dbExist = checkDataBase();
			 Log.i("sdcard", "boolean dbExist = checkDataBase(); "+dbExist);
			if (!dbExist) {
				copyAssets();
				return false;
				
			}else {
				return true;	
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//Toast.makeText(context, " "+e, 3).show();
			Log.i("sdcard","public boolean createDataBase()"+e );
			return true;
			
		}
		
	

	}
	private boolean checkDataBase() {
		  Log.i(T, " checkDataBase called");
		SQLiteDatabase checkDB = null;

		try {
			
			checkDB = SQLiteDatabase.openDatabase(DATABASE_FILE_PATH + File.separator + DATABASE_NAME, null,SQLiteDatabase.OPEN_READONLY);
			  Log.i("sdcard", " value of check "+checkDB);
		} catch (SQLiteException e) {

			e.printStackTrace();
			  Log.i("sdcard","boolean checkDataBase "+e);

		}

		if (checkDB != null) {

			checkDB.close();

		}

		return checkDB != null ? true : false;
	}
	
	 private void copyAssets() {
		  Log.i(T, " copyAssets called");
	   
	            InputStream in = null;
	            OutputStream out = null;
	            try {
	            
	            	in=context.getAssets().open("PhoneBackup.db");
	              out = new FileOutputStream(DATABASE_FILE_PATH + File.separator + DATABASE_NAME);
	              copyFile(in, out);
	              in.close();
	              in = null;
	              out.flush();
	              out.close();
	              out = null;
	            } catch(Exception e) {
	                Log.e("tag", e.getMessage());
	            }       
	      
	    }
	    private void copyFile(InputStream in, OutputStream out) throws IOException {
	        byte[] buffer = new byte[1024];
	        int read;
	        while((read = in.read(buffer)) != -1){
	          out.write(buffer, 0, read);
	        }
	    }	
	    public String getResourceString(int id) {
			return context.getResources().getString(id);
			
		}
}

