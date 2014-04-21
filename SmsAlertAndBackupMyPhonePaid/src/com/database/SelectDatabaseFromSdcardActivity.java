package com.database;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.smsalertandbackupmyphonedi.R;

public class SelectDatabaseFromSdcardActivity extends Activity{
	
	ListView backuplist;
	Context context;
	Intent i;
	private static String t="SelectDatabaseFromSdcardActivity";
	
	//public static final String  DATABASE_FILE_PATH =Environment.getExternalStorageDirectory().toString()+"/BackupOnPL/Media/";
	public static String  DATABASE_FILE_PATH=null;
	 public void onCreate(Bundle savedInstanceState) 
	    {
		 
	        super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.choos_database_restore);
	        backuplist=(ListView)findViewById(R.id.listView1);
	        context=this;  
	        //****************
	        
	        DATABASE_FILE_PATH=Environment.getExternalStorageDirectory().toString()+getResourceString(R.string.backup_path);
			
	        i=getIntent();
           backuplist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

			setContent(DATABASE_FILE_PATH);
		  backuplist.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					  String databasename= backuplist.getItemAtPosition(arg2).toString();
						
							Intent i=new Intent(getApplicationContext(),SelectTableFromDatabaseActivity.class);
							i.putExtra("DATABASE_NAME", databasename);
							startActivity(i);
						
						
				}
			});
	       
	    }
	 public void onClick( View v) {
		 switch (v.getId()) {
		
      case R.id.cancel:
	  finish();
	    break;
		default:
			break;
		}
		
	}
		void setContent(String DATABASE_FILE_PATH) {

			 Log.i("SelectDatabaseFromSdcardActivity", "setContent");
			File dir = new File(DATABASE_FILE_PATH);
			if (dir.exists()) {
				File[] files = dir.listFiles();
				if (files.length>0) {
			ArrayList<String>dbname=new ArrayList<String>();
					for (int i = 0; i < files.length; i++) {
						
						if (files[i].getName().endsWith("db")) {
							dbname.add(files[i].getName().substring(0, files[i].getName().length()-3));
						
							Log.i("file ex", " "+files[i].getName().endsWith("db"));
						}
						}
					  backuplist.setAdapter(new ArrayAdapter<String>(this,
				                android.R.layout.simple_list_item_1, dbname));
				}else {
					
		//		lv.setAdapter(new ListAdapter(this, MOBILE_OS, MOBILE_OS1));
			
				}
				
			} else {
			//	lv.setAdapter(new ListAdapter(this, MOBILE_OS, MOBILE_OS1));
				
			}

		}
		public String getResourceString(int id) {
			return context.getResources().getString(id);
			
		}

}
