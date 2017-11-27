package com.easygaadi.trucksmobileapp;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.JSONParser;
import com.easygaadi.trucksmobileapp.TruckApp;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class PostLoadActivity extends AppCompatActivity implements OnClickListener {

	Context context;
	SharedPreferences sharedPreferences;
	private ConnectionDetector detectConnection;
	JSONParser parser;
	ProgressDialog pDialog;

	private static final String TAG_RESULT = "predictions";

	JSONObject json;
	ArrayList<String> names;
	ArrayAdapter<String> namesAdapter;
	String browserKey = "AIzaSyAqh4el3Bd1lafjwQMQ7w6hJ_OVZWEst-0";
	String url;

	ArrayList<String> truckTypeKeys,truckTypesValues,goodsTypesKeys,goodsTypesValues;


	Dialog dateDialog,timeDialog;
	AutoCompleteTextView src_et,des_et;
	Spinner truckType_spn,goodsType_spn;
	EditText expectedPrice_et,comment_et,loadingchrgs_et,unloadingchrgs_et,nooftrucks_et;
	AutoCompleteTextView pickuppoint_actv;
	protected Button date_btn,submit_btn;

	String selectedTruckType=null,selectedGoodsType=null;
	private boolean srcset=false, destset=false, pickuppointset=false;

	private InterstitialAd mInterstitialAd;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_postload);

		context           = this;
		detectConnection  = new ConnectionDetector(context);
		parser            = JSONParser.getInstance();
		pDialog           = new ProgressDialog(this);
		pDialog.setCancelable(false);
		sharedPreferences = getApplicationContext().getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);


		src_et        = (AutoCompleteTextView)findViewById(R.id.srcactv);
		des_et        = (AutoCompleteTextView)findViewById(R.id.desactv);
		truckType_spn = (Spinner)findViewById(R.id.trucktype_spn);
		goodsType_spn = (Spinner)findViewById(R.id.goodstype_spn);
		expectedPrice_et  = (EditText)findViewById(R.id.expprc_et);
		pickuppoint_actv  = (AutoCompleteTextView)findViewById(R.id.pickuppoint_actv);
		comment_et        = (EditText)findViewById(R.id.comment_et);
		loadingchrgs_et   = (EditText)findViewById(R.id.loadingcharges_et);
		unloadingchrgs_et = (EditText)findViewById(R.id.unloadingcharges_et);
		nooftrucks_et     = (EditText)findViewById(R.id.nooftrucks_et);
		date_btn          = (Button)findViewById(R.id.date_btn);
		submit_btn        = (Button)findViewById(R.id.submit_btn);
		src_et.setAdapter(new GooglePlaceAutoCompleteAdapter(context, android.R.layout.simple_list_item_1, "India", false));
		src_et.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				srcset=true;
			}
			
		});
		des_et.setAdapter(new GooglePlaceAutoCompleteAdapter(context, android.R.layout.simple_list_item_1, "India", false));
		des_et.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				destset=true;
			}
			
		});
		pickuppoint_actv.setAdapter(new GooglePlaceAutoCompleteAdapter(context, android.R.layout.simple_list_item_1, "India", false));
		pickuppoint_actv.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				pickuppointset=true;
			}
			
		});
		pickuppoint_actv.addTextChangedListener(new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(pickuppointset){
					pickuppointset=false;
				}
				
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
			
		});
		date_btn.setOnClickListener(this);

		fetchTruckTypes();
		//intializeGoogleAd();

		src_et.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				/*if (detectConnection.isConnectingToInternet()) {
					names = new ArrayList<String>();
					updateList(s.toString(),src_et);
				}else{
					System.out.println("NO NET");
				}*/
				if(srcset) srcset=false;
			}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) { }

			@Override
			public void afterTextChanged(Editable arg0) { }


		});

		des_et.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				/*if ( detectConnection.isConnectingToInternet()) {
					names = new ArrayList<String>();
					updateList(s.toString(), des_et);
				} else {
					System.out.println("NO NET");
				}*/
				if(destset) destset=false;
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});

		submit_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				submit_act();

			}
		});

	}

	private void intializeGoogleAd() {
		mInterstitialAd = new InterstitialAd(this);

		// set the ad unit ID
		mInterstitialAd.setAdUnitId(getString(R.string.eg_interstital));

		AdRequest adRequest = new AdRequest.Builder()
				.build();

		// Load ads into Interstitial Ads
		mInterstitialAd.loadAd(adRequest);

		mInterstitialAd.setAdListener(new AdListener() {
			public void onAdLoaded() {
				showInterstitial();
			}
		});

	}

	private void showInterstitial() {
		if (mInterstitialAd.isLoaded()) {
			mInterstitialAd.show();
		}
	}


	protected void submit_act() {
		String source_str       = src_et.getText().toString().trim();
		String destination_str  = des_et.getText().toString().trim();
		String expectedpri_str  = expectedPrice_et.getText().toString().trim();
		String pickup_str       = pickuppoint_actv.getText().toString().trim();
		String lodcharges_str   = loadingchrgs_et.getText().toString().trim();
		String unlodcharges_str = unloadingchrgs_et.getText().toString().trim();
		String date_str         = date_btn.getText().toString().trim();
		String comment_str      = comment_et.getText().toString().trim();
		String nooftrucks_str   = nooftrucks_et.getText().toString().trim();

		if(source_str.length()!=0 && destination_str.length()!=0 && nooftrucks_str.length()!=0 && 
				!date_str.equalsIgnoreCase("Date Required")  && 
				selectedGoodsType!=null && selectedTruckType!=null &&srcset && destset && pickuppointset){

			new PostLoad(sharedPreferences.getString("uid", ""), source_str, destination_str, pickup_str, expectedpri_str, 
					comment_str, selectedTruckType, selectedGoodsType, date_str,lodcharges_str,unlodcharges_str,nooftrucks_str).execute();

		}else{
			TruckApp.editTextValidation(src_et, source_str, getResources().getString(R.string.source_error));
			TruckApp.editTextValidation(des_et, destination_str, getResources().getString(R.string.destination_error));
			TruckApp.editTextValidation(nooftrucks_et, nooftrucks_str, getResources().getString(R.string.noooftrucks_error));
			TruckApp.autocompleteValidation(src_et, srcset, "select source from dropdown");
			TruckApp.autocompleteValidation(des_et, destset, "select destination from dropdown");
			TruckApp.autocompleteValidation(pickuppoint_actv, pickuppointset, "select pickup point from dropdown");
			//TruckApp.editTextValidation(pickuppoint_actv, pickup_str, getResources().getString(R.string.destination_error));
			String error="";
			if(date_str.equalsIgnoreCase("Date Required")){
				error = error+"\n"+getResources().getString(R.string.selectdate_error);
			}

			if(selectedGoodsType== null){
				error = error+"\n Please select goodstype";
			}

			if(selectedTruckType == null){
				error = error+"\n Please select truck type";
			}

			if(error.trim().length()!=0){
				Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
			}
		}


	}


	public void updateList(String place,final AutoCompleteTextView auto_tv) {
		String input = "";

		try {
			input = "input=" + URLEncoder.encode(place, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		String output = "json";
		String parameter = input + "&types=geocode&sensor=true&key="
				+ browserKey;

		url = "https://maps.googleapis.com/maps/api/place/autocomplete/"
				+ output + "?" + parameter;

		JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url,
				null, new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {

				try {

					JSONArray ja = response.getJSONArray(TAG_RESULT);

					for (int i = 0; i < ja.length(); i++) {
						String description;
						JSONObject c = ja.getJSONObject(i);
						String temp = c.getString("description");
						int indexOfIndia = temp.lastIndexOf(", India");
						if(indexOfIndia==(temp.length()-7)) description =  temp.substring(0, indexOfIndia);
						else description = temp;
						Log.d("description", description);
						names.add(description);
					}

					namesAdapter = new ArrayAdapter<String>(
							getApplicationContext(),
							android.R.layout.simple_list_item_1, names) {
						@Override
						public View getView(int position,
								View convertView, ViewGroup parent) {
							View view = super.getView(position,
									convertView, parent);
							TextView text = (TextView) view
									.findViewById(android.R.id.text1);
							text.setTextColor(Color.BLACK);
							return view;
						}
					};
					auto_tv.setAdapter(namesAdapter);
					namesAdapter.notifyDataSetChanged();
				} catch (Exception e) {
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
			}
		});
		TruckApp.getInstance().addToRequestQueue(jsonObjReq, "jreq");
	}


	// Register  DatePickerDialog listener
	private DatePickerDialog.OnDateSetListener mDateSetListener =
			new DatePickerDialog.OnDateSetListener() {              
		// the callback received when the user "sets" the Date in the DatePickerDialog
		@Override
		public void onDateSet(DatePicker view, int yearSelected, 
				int monthOfYear, int dayOfMonth) {
			String date = ((new StringBuilder().append(yearSelected).append("-").append(set2Digit(monthOfYear+1)).append("-")
					.append(set2Digit(dayOfMonth))).toString());
			System.out.println("Date:"+date);
			date_btn.setText(date);
		}
	};




	private static String set2Digit(int val){
		String no= String.valueOf(val);
		if(1 == no.length())
		{ no="0"+no; }
		return no;
	}


	@Override
	public void onClick(View view) {
		Calendar c = Calendar.getInstance();
		switch (view.getId()) {
		case R.id.date_btn:
			dateDialog = new DatePickerDialog(this,mDateSetListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
			dateDialog.show();
			break;

		}
	}

	public void fetchTruckTypes(){
		if(detectConnection.isConnectingToInternet()){
			try{
				System.out.println("AccId:"+sharedPreferences.getString("accountID", "no accountid "));
				new GetTruckTypes().execute();
			}catch(Exception e){
				System.out.println("Ex inn on resume:"+e.toString());
			}
		}else {
			Toast.makeText(context, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
		}
	}


	private class PostLoad extends AsyncTask<String, String, JSONObject>{
		String uid,source_address,destination_address,pickup_point,expected_price,
		comment,id_truck_type,id_goods_type,date_required,loading_charge,unloading_charge,nooftrucks;

		public PostLoad(String uid, String source_address,
				String destination_address, String pickup_point,
				String expected_price, String comment,
				String id_truck_type, String id_goods_type,
				String date_required,String loading_charge,String unloading_charge,String nooftrucks) {

			this.uid = uid;
			this.source_address = source_address;
			this.destination_address = destination_address;
			this.pickup_point = pickup_point;
			this.expected_price = expected_price;
			this.comment = comment;
			this.id_truck_type = id_truck_type;
			this.id_goods_type = id_goods_type;
			this.date_required = date_required;
			this.loading_charge = loading_charge;
			this.unloading_charge = unloading_charge;
			this.nooftrucks = nooftrucks;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog.setMessage("Posting a load ...");
			pDialog.show();
		}

		@Override
		protected JSONObject doInBackground(String... params) {
			JSONObject json = null;
			try {	
				String urlParameters = setUrlParameters(uid, source_address, destination_address, pickup_point, expected_price,
						comment, id_truck_type, id_goods_type, date_required,loading_charge,unloading_charge,nooftrucks);
				System.out.println("URL_PARAMS "+TruckApp.postLoadURL+" "+urlParameters);
				String res = parser.excutePost(TruckApp.postLoadURL,urlParameters);
				System.out.println("Params:"+urlParameters+"post load o/p"+res);
				json = new JSONObject(res);
			}catch(Exception e){
				Log.e("DoIN EX in post load",e.toString());
			}
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			TruckApp.checkPDialog(pDialog);
			if(result!=null){
				try {
					if (0 == result.getInt("status")) {
						Toast.makeText(context,"Failed to post the load",Toast.LENGTH_LONG).show();
					}else if (1 == result.getInt("status")){	
						Toast.makeText(context,"Successfully posted the load",Toast.LENGTH_LONG).show();
						finish();
					}
				} catch (JSONException e) {
					Log.e(" Postload EX",e.toString());
					e.printStackTrace();
				}
			}else{			
				Toast.makeText(getApplicationContext(),"Some problem please try again" , Toast.LENGTH_LONG).show();				
			}
		}


		protected String setUrlParameters(String uid, String source_address,
				String destination_address, String pickup_point,
				String expected_price, String comment,
				String id_truck_type, String id_goods_type,
				String date_required,String loading_charge,String unloading_charge,String nooftrucks){

			StringBuilder builder= new StringBuilder();
			try {
				builder.append("uid=").append(URLEncoder.encode(uid, "UTF-8"));
				builder.append("&source_address=").append(URLEncoder.encode(source_address, "UTF-8"));
				builder.append("&destination_address=").append(URLEncoder.encode(destination_address, "UTF-8"));
				builder.append("&pickup_point=").append(URLEncoder.encode(pickup_point, "UTF-8"));
				builder.append("&expected_price=").append(URLEncoder.encode(expected_price, "UTF-8"));
				builder.append("&comment=").append(URLEncoder.encode(comment, "UTF-8"));
				builder.append("&id_truck_type=").append(URLEncoder.encode(id_truck_type, "UTF-8"));
				builder.append("&id_goods_type=").append(URLEncoder.encode(id_goods_type, "UTF-8"));
				builder.append("&date_required=").append(URLEncoder.encode(date_required, "UTF-8"));
				builder.append("&loading_charge=").append(URLEncoder.encode(loading_charge, "UTF-8"));
				builder.append("&unloading_charge=").append(URLEncoder.encode(unloading_charge, "UTF-8"));
				builder.append("&no_of_trucks=").append(URLEncoder.encode(nooftrucks, "UTF-8"));

				return builder.toString();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

	}


	private class GetTruckTypes extends AsyncTask<String, String, String>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog.setMessage("Fetching trucktypes ...");
			pDialog.show(); 
		}

		@Override
		protected String doInBackground(String... params) {
			String json = null;
			try {	
				json  = parser.excutePost(TruckApp.getTruckTypesURL,"");
			}catch(Exception e){
				Log.e(" GetTruckTypes DoIN EX",e.toString());
			}
			return json;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result!=null){
				try {
					JSONArray truckTypesArray = new JSONArray(result);

					truckTypeKeys    = new ArrayList<String>();
					truckTypesValues = new ArrayList<String>();
					if(truckTypesArray.length()>0){
						for (int i = 0; i < truckTypesArray.length(); i++) {
							JSONObject obj = truckTypesArray.getJSONObject(i); 
							truckTypeKeys.add(obj.getString("Key"));
							truckTypesValues.add(obj.getString("Value"));
						}
						truckType_spn.setAdapter(new ArrayAdapter<String>(context, R.layout.simplelistitem,R.id.text1, truckTypesValues));
						truckType_spn.setOnTouchListener(new OnTouchListener(){

							@Override
							public boolean onTouch(View v, MotionEvent event) {
								TruckApp.hide_keyboard_from(context, v);
								return false;
							}});
						truckType_spn.setOnTouchListener(new View.OnTouchListener(){

							@Override
							public boolean onTouch(View v, MotionEvent event) {
								TruckApp.hide_keyboard_from(context, v);
								return false;
							}
							
						});
						truckType_spn.setOnItemSelectedListener(new OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								selectedTruckType = truckTypeKeys.get(truckType_spn.getSelectedItemPosition());
							}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) {
								// TODO Auto-generated method stub

							}
						});
					}
					try{
						System.out.println("AccId:"+sharedPreferences.getString("accountID", "no accountid "));
						TruckApp.checkPDialog(pDialog);
						new GetGoodsTypes().execute();
					}catch(Exception e){
						TruckApp.checkPDialog(pDialog);
						System.out.println("Ex inn on resume:"+e.toString());
					}
				} catch (JSONException e) {
					Log.e(" GetTruckTypes post EX",e.toString());
					e.printStackTrace();
					TruckApp.checkPDialog(pDialog);
				}
			}else{
				TruckApp.checkPDialog(pDialog);
				Toast.makeText(getApplicationContext(),"Some problem please try again" , Toast.LENGTH_LONG).show();
				finish();
			}
		}

	}

	private class GetGoodsTypes extends AsyncTask<String, String, String>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog.setMessage("Fetching goodstypes ...");
			pDialog.show(); 
		}

		@Override
		protected String doInBackground(String... params) {
			String json = null;
			try {	
				json  = parser.excutePost(TruckApp.getGoodsTypesURL,"");
			}catch(Exception e){
				Log.e(" GetTruckTypes DoIN EX",e.toString());
			}
			return json;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result!=null){
				try {
					JSONArray goodsTypesArray = new JSONArray(result);

					goodsTypesKeys    = new ArrayList<String>();
					goodsTypesValues = new ArrayList<String>();
					if(goodsTypesArray.length()>0){
						for (int i = 0; i < goodsTypesArray.length(); i++) {
							JSONObject obj = goodsTypesArray.getJSONObject(i); 
							goodsTypesKeys.add(obj.getString("Key"));
							goodsTypesValues.add(obj.getString("Value"));
						}
						goodsType_spn.setAdapter(new ArrayAdapter<String>(context, R.layout.simplelistitem,R.id.text1, goodsTypesValues));
						goodsType_spn.setOnTouchListener(new OnTouchListener(){

							@Override
							public boolean onTouch(View v, MotionEvent event) {
								TruckApp.hide_keyboard_from(context, v);
								return false;
							}});
						goodsType_spn.setOnItemSelectedListener(new OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								selectedGoodsType = goodsTypesKeys.get(goodsType_spn.getSelectedItemPosition());
								System.out.println("selected goods type:"+selectedGoodsType);
							}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) {
								// TODO Auto-generated method stub

							}
						});
					}
					TruckApp.checkPDialog(pDialog);
				} catch (JSONException e) {
					Log.e(" GetTruckTypes post EX",e.toString());
					e.printStackTrace();
					TruckApp.checkPDialog(pDialog);
				}
			}else{
				TruckApp.checkPDialog(pDialog);
				Toast.makeText(getApplicationContext(),"Some problem please try again" , Toast.LENGTH_LONG).show();
				finish();
			}
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		TruckApp.getInstance().trackScreenView("PostLoad Screen");
	}


	@Override
	protected void onDestroy() {
		timeDialog = null;
		dateDialog = null;
		super.onDestroy();
	}

}
