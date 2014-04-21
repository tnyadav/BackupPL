package com.smsalertandbackupmyphonedi;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.webkit.WebView;

import com.smsalertandbackupmyphonedifree.R;

public class HelpActivity extends Activity {

	Context context;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.help);
		 WebView localWebView = (WebView)findViewById(R.id.wv);
		    localWebView.getSettings().setBuiltInZoomControls(true);
		    String str = Locale.getDefault().getLanguage();
		    if (!str.equalsIgnoreCase("en"))
		    {
		      if (!str.equalsIgnoreCase("es"))
		      {
		        if (!str.equalsIgnoreCase("ja"))
		        {
		          if (!str.equalsIgnoreCase("ko")) {
		            localWebView.loadUrl("file:///android_asset/en.html");
		          } else {
		            localWebView.loadUrl("file:///android_asset/kr.html");
		          }
		        }
		        else {
		          localWebView.loadUrl("file:///android_asset/jp.html");
		        }
		      }
		      else {
		        localWebView.loadUrl("file:///android_asset/sp.html");
		      }
		    }
		    else {
		      localWebView.loadUrl("file:///android_asset/en.html");
		    }
		
	}

}

