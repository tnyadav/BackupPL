package com.database;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.smsalertandbackupmyphonedi.R;

public class SelectTableFromDatabaseActivity extends Activity {

	ListView backuplist;
	Button save, selectall, cancel;
	boolean allchecked = false;
	Context context;
	/*public static final String DATABASE_FILE_PATH = Environment
			.getExternalStorageDirectory().toString() + "/BackupOnPL/Media/";*/

	DatabaseHelper dh;
	Intent i;
	public static SQLiteDatabase database;
	Cursor cursor1;
	String databasename, query;
	int cursorlayout;
	String[] itemlist, from;
	int[] to;
	public static int request = 001;
	private static String t="ChoosTableFromDatabaseActivity";
	ArrayList<String> backuptables;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		   requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.backup_restore1);
		i=getIntent();
	
		setContent();
		
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_multiple_choice,
					backuptables);
			backuplist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			backuplist.setAdapter(adapter);
	

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.save:

			int size = backuplist.getCount();
			itemlist = new String[size];
			for (int i = 0; i < size; i++) {
				if (backuplist.isItemChecked(i)) {
					itemlist[i] = backuplist.getItemAtPosition(i).toString();
				} else {
					itemlist[i] = "";
				}

			}

			for (int i = 0; i < size; i++) {
				Log.i("list", " value at " + i + " " + itemlist[i]);
			}
//***********************************************************************************************************
			  i=new Intent(getApplicationContext(),PhoneDataRestoreActivity.class);
			
			  i.putExtra("dbName", databasename);
			  i.putExtra("itemList", itemlist);
			 startActivity(i); 
			 finish();

			break;

		case R.id.selectall:
			int size1 = backuplist.getCount();
			if (!allchecked) {

				for (int i1 = 0; i1 <= size1; i1++)
					backuplist.setItemChecked(i1, true);
				allchecked = true;

			} else {
				for (int i1 = 0; i1 <= size1; i1++)
					backuplist.setItemChecked(i1, false);
				allchecked = false;

			}

			break;

		case R.id.cancel:
			finish();
			break;
		default:
			break;
		}

	}

	
	//@SuppressLint("ResourceAsColor")
	public void setContent() {
		backuplist = (ListView) findViewById(R.id.listView1);
		save = (Button) findViewById(R.id.save);
		selectall = (Button) findViewById(R.id.selectall);
		cancel = (Button) findViewById(R.id.cancel);
		context = this;
		i = getIntent();
		databasename = i.getStringExtra("DATABASE_NAME");
		dh = new DatabaseHelper(context, databasename,true);
		database = dh.getWritableDatabase();
		cursor1 = database.rawQuery("select *from Media;", null);
		backuptables = new ArrayList<String>();
		if (cursor1.getCount() > 0) {

			while (cursor1.moveToNext()) {
				Log.i("tables", cursor1.getString(1));
				if (cursor1.getString(1).length() > 2) {

					backuptables.add(cursor1.getString(0));
					Log.i(t,""+cursor1.getString(0));

				}

			}

		}

	}
	
}
