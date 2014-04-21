package com.database;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.smsalertandbackupmyphonedifree.R;

public class DatabaseNameActivity extends Activity{

	Context context;
	EditText databasename;
	Button ok, cancel;
    DatabaseHelper dh;
    Intent intent;
	public static SQLiteDatabase database ;
	private static String t="DatabaseNameActivity";
	
	 public void onCreate(Bundle savedInstanceState) 
	    {
	        super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.database_name);
	      
	        context=this;
	       intent=getIntent();
	        databasename=(EditText)findViewById(R.id.databasename);
	        ok=(Button)findViewById(R.id.ok);
	        cancel=(Button)findViewById(R.id.cancel);
	        databasename.setText(android.text.format.DateFormat.format(
						"ddMMyyyy_hhmmss", new java.util.Date()).toString());
	   

	      
	        ok.setOnClickListener(new OnClickListener() {
				
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String dbName;
						dbName=databasename.getText().toString();
                    	 try {
							dh=new DatabaseHelper(context,dbName,true);
						} catch (Exception e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
							 Log.i("sdcard", " isDatabaseExit in name "+e2);
						}
     					
                    	
						boolean isDatabaseExit=dh.createDataBase();
						 Log.i(t, "isDatabaseExit  "+isDatabaseExit);
						 dh.close();
				
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						  
						  if (isDatabaseExit) {
							Toast.makeText(context, ""+getResourceString(R.string.Database_Exist_Choose_Another_Name), Toast.LENGTH_LONG).show();
						}
						  else {
							
							  String[] itemlist=intent.getStringArrayExtra("ITEMLIST");
							
							  intent=new Intent(getApplicationContext(),PhoneDataBackupActivity.class);
							  intent.putExtra("dbName", dbName);
							  intent.putExtra("itemList", itemlist);
							  startActivity(intent); 
							  finish();
							
							   
						}
				
				}
			});
	      cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		}) ;
	    }
	 public String getResourceString(int id) {
			return context.getResources().getString(id);
			
		}
}
