package com.smsalertandbackupmyphonedi;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddContactActivity extends Activity{
	EditText firstEt,secondEt,thirdEt,fourthEt;
	Context context;
	String imsi="";
	SharedPreferences sharedPreferences;

	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.choose_no);
	        context=this;
	        firstEt=(EditText)findViewById(R.id.et_enterno_first);
	        secondEt=(EditText)findViewById(R.id.et_enterno_second);
	        thirdEt=(EditText)findViewById(R.id.et_enterno_third);
	        fourthEt=(EditText)findViewById(R.id.et_enterno_fourth);
	       
	         sharedPreferences = context.getSharedPreferences("imsi",0);
	        //String strSavedIMSI= sharedPreferences.getString("IMSI", "");
	        String MNO1= sharedPreferences.getString("MNO1", "");
	        String MNO2= sharedPreferences.getString("MNO2", "");
	        String MNO3= sharedPreferences.getString("MNO3", "");
	        String MNO4= sharedPreferences.getString("MNO4", "");
	        
	        firstEt.setText(MNO1);
	        secondEt.setText(MNO2);
	        thirdEt.setText(MNO3);
	        fourthEt.setText(MNO4);
	    }

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_enterno_first:
			 
			callPhoneBook(1);
			
			break;
		case R.id.btn_enterno_second:
			callPhoneBook(2);            
			break;
		case R.id.btn_enterno_third:
			callPhoneBook(3);           
			break;
		case R.id.btn_enterno_fourth:
			callPhoneBook(4);
			break;
		
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_save:
			System.out.println("hello");
			if(firstEt.getText().length()==0&& secondEt.getText().length()==0&& thirdEt.getText().length()==0&& fourthEt.getText().length()==0)
			{
			
				
		        sharedPreferences.edit().putString("MNO1","").commit();
		        sharedPreferences.edit().putString("MNO2","").commit();
		        sharedPreferences.edit().putString("MNO3","").commit();
		        sharedPreferences.edit().putString("MNO4","").commit();
		        Toast.makeText(getApplicationContext(),getResourceString(R.string.traker_deactiveted),Toast.LENGTH_LONG).show();
			}
			else
			{
			
            
	        sharedPreferences.edit().putString("MNO1", firstEt.getText().toString()).commit();
	        sharedPreferences.edit().putString("MNO2", secondEt.getText().toString()).commit();
	        sharedPreferences.edit().putString("MNO3", thirdEt.getText().toString()).commit();
	        sharedPreferences.edit().putString("MNO4", fourthEt.getText().toString()).commit();
	        
	        
	        Toast.makeText(getApplicationContext(),getResourceString(R.string.traker_activeted),Toast.LENGTH_LONG).show();
			}

			break;
		default:
			break;
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode==RESULT_OK) {
			
		
		if (requestCode==1) {
			
			addContactToList(firstEt, data);
		}
		if (requestCode==2) {
			
			addContactToList(secondEt, data);
		}
		if (requestCode==3) {
			
			addContactToList(thirdEt, data);
		}
		if (requestCode==4) {
		
			addContactToList(fourthEt, data);
		}
		
		
		}	
	   
	}
	public String getName( Intent data) {
		String number = null;
		 if (data != null) {
		        Uri uri = data.getData();

		        if (uri != null) {
		            Cursor c = null;
		            try {
		                c = getContentResolver().query(uri, new String[]{ 
		                            ContactsContract.CommonDataKinds.Phone.NUMBER,  
		                            },
		                        null, null, null);

		                if (c != null && c.moveToFirst()) {
		                 number = c.getString(0);
		                   
		                   // showSelectedNumber(type, number);
		                }
		            } finally {
		                if (c != null) {
		                    c.close();
		                }
		            }
		        }
		    }
		return number;	
	}
	

	
	public void callPhoneBook(int i) {
		 Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
         intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
         startActivityForResult(intent, i); 
	}
	
	public boolean checkContact(String contact) {
		List<String> AllContacts1=new ArrayList<String>();
		AllContacts1.add(firstEt.getText().toString());
		AllContacts1.add(secondEt.getText().toString());
		AllContacts1.add(thirdEt.getText().toString());
		AllContacts1.add(fourthEt.getText().toString());
		boolean isExists = false;
		if (contact.length()>1) {
			for (int i = 0; i < AllContacts1.size(); i++) {
				
				if (contact.equals(AllContacts1.get(i))) {
					isExists=true;
				}
				
		}
		
			
		}
		return isExists;
		
		
	}
	public void addContactToList(View v,Intent data) {
		if (!checkContact(getName(data))) {
			((EditText) v).setText(getName(data));
		} else {
          Toast.makeText(context, getResourceString(R.string.already_added), Toast.LENGTH_LONG).show();
		}
	}
	public String getResourceString(int id) {
		return context.getResources().getString(id);
		
	}
}
