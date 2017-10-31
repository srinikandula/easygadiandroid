package com.easygaadi.trucksmobileapp;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import com.easygaadi.gpsapp.utilities.ConnectionDetector;

public class ExpectedPriceDialogActivity extends Activity {

	Context context;
	JSONObject truckObj;
	private ConnectionDetector detectCnnection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.booktruckdialog);
		
	}
}
