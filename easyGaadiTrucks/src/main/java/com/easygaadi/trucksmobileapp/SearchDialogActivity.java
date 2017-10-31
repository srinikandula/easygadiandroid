package com.easygaadi.trucksmobileapp;

import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.CustomDateTimePicker;

public class SearchDialogActivity extends Activity implements OnClickListener {

	CustomDateTimePicker customDialogFrom,customDialogTo;
	TextView fromdate_tv,todate_tv;
	Context context;
	JSONObject truckObj;
	private ConnectionDetector detectCnnection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dialoglocationsearch);
		getWindow().setBackgroundDrawable(new ColorDrawable(0));

		context     = this;
		detectCnnection = new ConnectionDetector(context);
		fromdate_tv = (TextView)findViewById(R.id.fromdate_et);
		todate_tv   = (TextView)findViewById(R.id.todate_et);

		if(getIntent().hasExtra("json")){
			try {
				truckObj = new JSONObject(getIntent().getStringExtra("json"));
			} catch (JSONException e) {
				e.printStackTrace();
				finish();
			}
		}

		fromdate_tv.setText(TruckApp.getDateString(false));
		todate_tv.setText(TruckApp.getDateString(true));

		((Button)findViewById(R.id.cancel_btn)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		((Button)findViewById(R.id.tracktruck_btn)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(detectCnnection.isConnectingToInternet()){

					String fromStr = fromdate_tv.getText().toString().trim();
					String toStr   = todate_tv.getText().toString().trim();
					if(!fromStr.equalsIgnoreCase("From Date") && !toStr.equalsIgnoreCase("To Date")){
						String params = "from="+fromStr.replaceAll(" ", "%20")+"&to="+toStr.replace(" ", "%20");
						try {
							Intent nextIntent=new Intent(context, TrackTruckActivity.class);
							nextIntent.putExtra("regno", truckObj.getString("truck_no"));
							nextIntent.putExtra("urlParams", params);
							nextIntent.putExtra("vehicleType",truckObj.getString("vehicleType"));
							startActivity(nextIntent);
							finish();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}else{

						String error = "Please select ";
						if(fromStr.equalsIgnoreCase("From Date")){
							error = error + "\nFrom Date";
						}
						if(toStr.equalsIgnoreCase("To Date")){
							error = error + "\nTo Date";
						}
						Toast.makeText(context, error, Toast.LENGTH_LONG).show();

					}
				}else {
					Toast.makeText(context, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
				}
			}
		});


		customDialogFrom = new CustomDateTimePicker(this,
				new CustomDateTimePicker.ICustomDateTimeListener() {

			@Override
			public void onSet(Dialog dialog, Calendar calendarSelected,
					Date dateSelected, int year, String monthFullName,
					String monthShortName, int monthNumber, int date,
					String weekDayFullName, String weekDayShortName,
					int hour24, int hour12, int min, int sec,
					String AM_PM) {

				fromdate_tv.setText(year+"-"+TruckApp.set2Digit((monthNumber+1))+"-"+TruckApp.set2Digit(calendarSelected.get(Calendar.DAY_OF_MONTH))
						+ " " + TruckApp.set2Digit(hour24) + ":" + TruckApp.set2Digit(min)+ ":00");
			}

			@Override
			public void onCancel() {

			}
		});



		customDialogTo = new CustomDateTimePicker(this,
				new CustomDateTimePicker.ICustomDateTimeListener() {

			@Override
			public void onSet(Dialog dialog, Calendar calendarSelected,
					Date dateSelected, int year, String monthFullName,
					String monthShortName, int monthNumber, int date,
					String weekDayFullName, String weekDayShortName,
					int hour24, int hour12, int min, int sec,
					String AM_PM) {
				todate_tv.setText(year+"-"+TruckApp.set2Digit((monthNumber+1))+"-"+TruckApp.set2Digit(calendarSelected.get(Calendar.DAY_OF_MONTH))
						+ " " + TruckApp.set2Digit(hour24) + ":" + TruckApp.set2Digit(min)+ ":00");
			}

			@Override
			public void onCancel() {

			}
		});

		/**
		 * Pass Directly current time format it will return AM and PM if you set
		 * false
		 */
		customDialogFrom.set24HourFormat(true);
		customDialogTo.set24HourFormat(true);
		/**
		 * Pass Directly current data and time to show when it pop up
		 */
		customDialogFrom.setDate(Calendar.getInstance());
		customDialogTo.setDate(Calendar.getInstance());

		fromdate_tv.setOnClickListener(this);
		todate_tv.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		customDialogFrom = null; 
		customDialogTo= null;
		truckObj= null;;
		detectCnnection= null;
		super.onDestroy();
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fromdate_et:
			customDialogFrom.showDialog();
			break;
		case R.id.todate_et:
			customDialogTo.showDialog();
			break;

		default:
			break;
		}

	}

}
