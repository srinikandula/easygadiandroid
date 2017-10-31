package com.easygaadi.trucksmobileapp;

import java.net.URLEncoder;
import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.easygaadi.gpsapp.utilities.JSONParser;

public class SetTruckInfo extends AsyncTask<String, String, JSONObject>{

	DataDownloadListener dataDownloadListener;

	String accountid,address,truck_reg_no,date_available,mobile,message,uid;
	HashMap<String, String> retrivedMap;
	SharedPreferences preferences;
	ProgressDialog pDialog;
	Activity context;
	JSONParser parser;
	@SuppressWarnings("unchecked")
	public SetTruckInfo(String accountid,String uid,String truckRegNo,String date_available,String address,String mobile,String message,Activity act,ProgressDialog pDialog,JSONParser parser) {
		this.accountid      = accountid;
		this.address        = address;
		this.truck_reg_no   = truckRegNo;
		this.date_available = date_available;
		this.mobile         = mobile;
		this.message        = message;
		context             = act;
		this.pDialog        = pDialog;
		this.uid            = uid;
		this.parser         = parser;
		preferences         = context.getSharedPreferences("trucks", Context.MODE_PRIVATE);
		retrivedMap         = (HashMap<String,String>) preferences.getAll();		
	}


	public void setDataDownloadListener(DataDownloadListener dataDownloadListener) {
		this.dataDownloadListener = dataDownloadListener;
	}


	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		pDialog.setMessage(message+" data...");
		pDialog.show();  
	}

	@Override
	protected JSONObject doInBackground(String... params) {
		JSONObject json = null;
		try {	
			String urlParameters =
					"accountid=" + URLEncoder.encode(accountid, "UTF-8") +
					"&uid=" + URLEncoder.encode(uid, "UTF-8")+
					"&address=" + URLEncoder.encode(address, "UTF-8")+
					"&truck_reg_no=" + URLEncoder.encode(truck_reg_no, "UTF-8")
					+"&mobile=" + URLEncoder.encode(mobile, "UTF-8")+
					"&date_available="+ URLEncoder.encode(date_available, "UTF-8");
			String res = parser.excutePost(TruckApp.setTruckInfoURL, urlParameters);
			//System.out.println("urlParameters:"+urlParameters+"settruck info o/p"+res);
			json = new JSONObject(res);
		}catch(Exception e){
			Log.e("Login DoIN EX",e.toString());
		}
		return json;
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		pDialog.dismiss();
		if (result != null){
			try {
				if (0 == result.getInt("status")) {
					Toast.makeText(context,context.getResources().getString(R.string.failedUpdate),Toast.LENGTH_LONG).show();
				}else if (1 == result.getInt("status")){
					Toast.makeText(context,result.getString("msg"),Toast.LENGTH_LONG).show();
				}
				dataDownloadListener.dataDownloadedSuccessfully(result);
			}catch (Exception e)
			{ System.out.println("ex in get leads"+e.toString()); }
		}else{
			dataDownloadListener.dataDownloadFailed();
			Toast.makeText(context,context.getResources().getString(R.string.exceptionmsg),Toast.LENGTH_LONG).show();
		}
	}

	public static interface DataDownloadListener {
		void dataDownloadedSuccessfully(Object data);
		void dataDownloadFailed();
	}

}



