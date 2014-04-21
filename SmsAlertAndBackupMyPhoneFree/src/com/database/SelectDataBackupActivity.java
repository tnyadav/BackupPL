package com.database;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.smsalertandbackupmyphonedifree.R;


public class SelectDataBackupActivity extends Activity{
	
	String[] backupitems;
	ListView backuplist;
	boolean allchecked=false;
	Context context;
	Intent intent;
	String[] itemlist;
	 public void onCreate(Bundle savedInstanceState) 
	    {
	        super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.backup_restore1);
	        backuplist=(ListView)findViewById(R.id.listView1);
	      context=this;  
	     backupitems=context.getResources().getStringArray(R.array.backupitems);
	      intent= getIntent();
	         
	     ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
	                android.R.layout.simple_list_item_multiple_choice, backupitems);
	       backuplist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	       backuplist.setAdapter(adapter);
	    
           backuplist.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					
				}
			});
	       
	    }
	 public void onClick( View v) {
		 switch (v.getId()) {
		case R.id.save:
		
		int size=backuplist.getCount();
		itemlist=new String[size];
		for (int i = 0; i < size; i++) {
			if(backuplist.isItemChecked(i))
			{
				itemlist[i]=backuplist.getItemAtPosition(i).toString();
			}
			else {
				itemlist[i]="";
			}
			
		}
		
		for (int i = 0; i < size; i++) {
		Log.i("list", " value at "+i+" "+itemlist[i]);	
		}
		
			Intent i=new Intent(getApplicationContext(),DatabaseNameActivity.class);
			i.putExtra("ITEMLIST", itemlist);
			startActivity(i);
			  
	break;
			
      case R.id.selectall:
    	  int size1=backuplist.getCount();
    	if (!allchecked) {
		
    	    for(int i1 = 0; i1 <= size1; i1++)
    	    	backuplist.setItemChecked(i1, true);
    	    allchecked=true;
    		
		} else {
			 for(int i1 = 0; i1 <= size1; i1++)
	    	    	backuplist.setItemChecked(i1, false);
	    	    allchecked=false;

		}
    	
    	
			
			break;

      case R.id.cancel:
        finish();
	    break;
		default:
			break;
		}
		
	}

}
