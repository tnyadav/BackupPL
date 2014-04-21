package com.smsalertandbackupmyphonedi;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.smsalertandbackupmyphonedifree.R;

public class UserSettingActivity extends PreferenceActivity {

	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.settings);

	}
}
