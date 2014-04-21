package com.smsalertandbackupmyphonedi;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.database.SelectDatabaseFromSdcardActivity;

class UnZipFileAsyncTask extends AsyncTask<String, Integer, String> {
	ProgressDialog mProgressDialog;
	Context context;
	String CalledBy;
	String databasename;
	
	
	
	
	
	UnZipFileAsyncTask(Context context)
	{
		this.context=context;
		
		Log.i("sdcard", "UnZipFileAsyncTask start");
		
	}
	
	  @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        mProgressDialog = new ProgressDialog(context);
	        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	        mProgressDialog.setMessage("UnZiping File");
	        mProgressDialog.show();
	        
	        
	    }

	
	
    @Override
    protected String doInBackground(String... values) {
    	   InputStream is;
    	     ZipInputStream zis;
    	     databasename=values[1];

    	     try 
    	     {
    	         
    	         is = new FileInputStream(values[0]);
    	         zis = new ZipInputStream(new BufferedInputStream(is));          
    	         ZipEntry ze;
    	         byte[] buffer = new byte[1024];
    	         int count;

    	         while ((ze = zis.getNextEntry()) != null) 
    	         {
    	             
    	             FileOutputStream fout = new FileOutputStream(Environment.getExternalStorageDirectory().toString()+"/BackupOnPL/Media/"+fileName(values[1])+".db");

    	             
    	             while ((count = zis.read(buffer)) != -1) 
    	             {
    	                 fout.write(buffer, 0, count);             
    	             }

    	             fout.close();               
    	             zis.closeEntry();
    	         }

    	         zis.close();
    	     } 
    	     catch(IOException e)
    	     {
    	         e.printStackTrace();
    	         Log.i("sdcard", "UnZipFileAsyncTask background ex"+e);
    	        
    	     }

    	   
    
        return null;
    }
  
   
    @Override
	protected void onPostExecute(String result) {

		mProgressDialog.dismiss();
		Log.i("sdcard", "UnZipFileAsyncTask post");
		 Intent intent = new Intent(context, SelectDatabaseFromSdcardActivity.class);
			context.startActivity(intent);
		
	}
    private String fileName(String f) {
		String name = f;
		int idx = name.lastIndexOf('.');
		if (idx == -1)
			return name;
		if (idx == 0)
			return new String("");
		return name.substring(0, idx);
	}
}