package com.easygaadi.trucksmobileapp;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.ExceptionHandler;
import com.easygaadi.gpsapp.utilities.JSONParser;
import com.easygaadi.trucksmobileapp.SetTruckInfo.DataDownloadListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class TrucksActivity extends AppCompatActivity {


	SharedPreferences sharedPreferences;
	Context context;
	ConnectionDetector detectCnnection;
	JSONParser parser;
	ProgressDialog pDialog;
	public static ListView trucks_lv;
	ArrayList<String> truckTypeKeys,truckTypesValues;
	JSONArray trucksArray;
	TrucksAdapter adapter;
	public View dialogView;
	public AlertDialog alertDialog;
	Dialog createTruckDialog;
	Button createTruck_btn;
	EditText truckRegNo_et;
	Spinner truckTypes_spn;
	ImageView cancel_btn;
	String selectedTruckType=null;
	
	long minDate ,maxDate;
	DatePicker datePicker;
	Dialog deletedialog;
	ScrollView dialogRoot;
	TextView insurance_expiry_tv,fitness_expiry_tv,national_permit_tv,updia_tit_tv;;
	EditText  rc_no_et,insurance_amount_et;
	CheckBox  national_permit_cb;
	LinearLayout natpermit_layout;
	DatePickerDialog dateDialog;
	AutoCompleteTextView city_actv;
	private InterstitialAd mInterstitialAd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trucks);

		context           = this;
		detectCnnection   = new ConnectionDetector(context);
		parser            = JSONParser.getInstance();
		dialogView        = View.inflate(context, R.layout.update_dialog, null);
		dialogRoot		  = (ScrollView) dialogView.findViewById(R.id.trucks_update_dlg_rootview);
		alertDialog       = new AlertDialog.Builder(context).create();
		sharedPreferences = getApplicationContext().getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);
		trucks_lv         = (ListView)findViewById(R.id.trucksList);
		
		pDialog           = new ProgressDialog(this);
		pDialog.setCancelable(false);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(TrucksActivity.this));

		long currentTimeMills = System.currentTimeMillis();
		minDate = currentTimeMills - 10000;
		maxDate = currentTimeMills + (7*24*60*60*1000);

		System.out.println("min0:"+minDate);
		System.out.println("max0:"+maxDate);

		System.out.println("min1:"+TruckApp.getDate(minDate, "dd/MM/yyyy"));
		System.out.println("max1:"+TruckApp.getDate(maxDate,"dd/MM/yyyy"));

		minDate = (TruckApp.convertStringToDate(TruckApp.getDate(minDate, "dd/MM/yyyy"))).getTime();
		maxDate = (TruckApp.convertStringToDate(TruckApp.getDate(maxDate, "dd/MM/yyyy"))).getTime();

		System.out.println("min2:"+minDate);
		System.out.println("max2:"+maxDate);

		datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
		
		datePicker.setMinDate(minDate);
		datePicker.setMaxDate(maxDate);
		
		city_actv = (AutoCompleteTextView) dialogView.findViewById(R.id.city_actv);
		//city_actv.setText(sharedPreferences.getString("city", ""));
		city_actv.setAdapter(new GooglePlaceAutoCompleteAdapter(context, android.R.layout.simple_list_item_1, "India", true));
		city_actv.setThreshold(1);

		createTruckDialog = new Dialog(TrucksActivity.this, android.R.style.Theme_Dialog);
		createTruckDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		createTruckDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
		createTruckDialog.setContentView(R.layout.createtruckdialog);
		truckRegNo_et        = (EditText) createTruckDialog.findViewById(R.id.truckReg_et);
		createTruck_btn      = (Button) createTruckDialog.findViewById(R.id.update_btn);
		cancel_btn           = (ImageView) createTruckDialog.findViewById(R.id.close_iv);
		truckTypes_spn       = (Spinner) createTruckDialog.findViewById(R.id.trucktypes);
		insurance_amount_et  = (EditText) createTruckDialog.findViewById(R.id.ins_amount_et);
		rc_no_et             = (EditText) createTruckDialog.findViewById(R.id.rc_no_et);
		insurance_expiry_tv  = (TextView) createTruckDialog.findViewById(R.id.insurance_expiry_et);
		national_permit_tv   = (TextView) createTruckDialog.findViewById(R.id.national_permit_et);
		fitness_expiry_tv    = (TextView)createTruckDialog.findViewById(R.id.fitness_expiry_et);
		updia_tit_tv         = (TextView)createTruckDialog.findViewById(R.id.dialogtitle_tv);

		national_permit_cb   = (CheckBox)createTruckDialog.findViewById(R.id.np_cb);
		natpermit_layout     = (LinearLayout)createTruckDialog.findViewById(R.id.np_layout);
		
		dialogRoot.setOnTouchListener(new View.OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.requestFocus();
				return false;
			}
			
		});
		cancel_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				TruckApp.checkDialog(createTruckDialog);				
			}
		});
		
		TruckApp.hide_keyboard_from(context, city_actv);
		city_actv.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TruckApp.hide_keyboard_from(context, view);
				
			}
			
		});
		city_actv.setOnFocusChangeListener(new View.OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus) {
					System.out.println("actv loosing focus");
					TruckApp.hide_keyboard_from(context, v);
				} else System.out.println("actv has focus");
				
			}
			
		});
		fetchTruckTypes();
		intializeGoogleAd();
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


	@Override
	protected void onResume() {
		super.onResume();
		TruckApp.getInstance().trackScreenView("Trucks Screen");
	}

	public void fetchTrucks(){
		if(detectCnnection.isConnectingToInternet()){
			trucksArray = null;
			adapter     = null;
			trucksArray       = new JSONArray();
			adapter           = new TrucksAdapter(trucksArray, TrucksActivity.this);
			trucks_lv.setAdapter(adapter);
			try{
				new TrackAllVehicles(sharedPreferences.getString("accountID", ""),sharedPreferences.getString("uid", "")).execute();
			}catch(Exception e){
				System.out.println("Ex inn on resume:"+e.toString());
			}
		}else {
			Toast.makeText(context, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
		}
	}

	public void fetchTruckTypes(){
		if(detectCnnection.isConnectingToInternet()){
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
						truckTypes_spn.setAdapter(new ArrayAdapter<String>(context, R.layout.simplelistitem,R.id.text1, truckTypesValues));
						truckTypes_spn.setOnItemSelectedListener(new OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								selectedTruckType = truckTypeKeys.get(truckTypes_spn.getSelectedItemPosition());
							}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) {
								// TODO Auto-generated method stub

							}
						});
					}
					TruckApp.checkPDialog(pDialog);
					fetchTrucks();
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

	private class TrackAllVehicles extends AsyncTask<String, String, JSONObject>{

		String account_id,uid;
		public TrackAllVehicles(String account_id,String uid) {
			this.account_id = account_id;
			this.uid        = uid;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog.setMessage("Fetching trucks data...");
			pDialog.show();  
		}

		@Override
		protected JSONObject doInBackground(String... params) {

			JSONObject json = null;
			try {
				String url=TruckApp.getTrucksURL+"?uid="+uid+"&accountid="+account_id;
				String res = parser.executeGet(url);
				System.out.println("URL: "+url+"\nTRUCKS MKL:"+res);
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
			TruckApp.checkPDialog(pDialog);
			if (result != null){
				try {
					if (0 == result.getInt("status")) {
						Toast.makeText(context,"No trucks found",Toast.LENGTH_LONG).show();
					}else if (1 == result.getInt("status")){
						JSONArray truckArray1 = result.getJSONArray("data");
						for (int i = 0; i < truckArray1.length(); i++) {
							trucksArray.put(truckArray1.getJSONObject(i));					
						}
						if(trucksArray.length()==0){
							Toast.makeText(context,"No records found",Toast.LENGTH_LONG).show();
						}
						adapter.notifyDataSetChanged();
						trucks_lv.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0,
									View arg1, int position, long arg3) {
								try {
									String key = trucksArray.getJSONObject(position).getString("truck_reg_no");
									showUpdateDialog(key);
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
					}
				}catch (Exception e){
					System.out.println("ex in get leads"+e.toString());
				}
			}else{
				Toast.makeText(context,getResources().getString(R.string.exceptionmsg),Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.trucks_menu , menu);
		return true;
	}




	public class TrucksAdapter extends BaseAdapter {

		JSONArray  trucksArray;
		Activity       activity;
		LayoutInflater inflater ;
		HashMap<String, String> retrivedMap;
		List<String>   trucksList;

		private SharedPreferences preferences;
		public class ViewHolder{
			TextView truck_regno_tv,date_tv,city_tv;
			ImageView delete_iv,edit_iv;
		}

		@SuppressWarnings("unchecked")
		public TrucksAdapter(JSONArray array,Activity activity) {
			this.trucksArray   = array;
			this.activity      = activity;
			this.inflater      = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			preferences        = activity.getSharedPreferences("trucks", Context.MODE_PRIVATE);
			retrivedMap        = (HashMap<String,String>) preferences.getAll();
			trucksList         = new ArrayList<String>(retrivedMap.keySet());
			System.out.println(preferences.getAll());
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return trucksArray.length();
		}

		@Override
		public Object getItem(int pos) {
			// TODO Auto-generated method stub

			try {
				return trucksArray.get(pos);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;

		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewholder = null;
			if(convertView == null){
				viewholder                = new ViewHolder();
				convertView               = inflater.inflate(R.layout.update_truck_item, parent, false);
				viewholder.truck_regno_tv = (TextView)convertView.findViewById(R.id.truck_regno_tv);
				viewholder.city_tv        = (TextView)convertView.findViewById(R.id.city_tv);
				viewholder.date_tv        = (TextView)convertView.findViewById(R.id.date_tv);
				viewholder.delete_iv      = (ImageView)convertView.findViewById(R.id.deletebtn);
				viewholder.edit_iv        = (ImageView)convertView.findViewById(R.id.editbtn);
				convertView.setTag(viewholder);			
			}else{
				viewholder = (ViewHolder)convertView.getTag();
			}
			setData(viewholder, position);
			return convertView;
		}

		@SuppressWarnings("unchecked")
		private void setData(ViewHolder viewholder, int position) {
			try {
				retrivedMap       = (HashMap<String,String>) preferences.getAll();
				String truckRegNo = trucksArray.getJSONObject(position).getString("truck_reg_no");
				viewholder.truck_regno_tv.setText(truckRegNo);
				viewholder.delete_iv.setTag(trucksArray.getJSONObject(position));
				viewholder.edit_iv.setTag(position+";"+trucksArray.getJSONObject(position));

				if(trucksList.contains(truckRegNo)){
					JSONObject obj = new JSONObject(retrivedMap.get(truckRegNo));
					System.out.println("position "+position+"retrivedMap"+retrivedMap.get(truckRegNo));
					viewholder.date_tv.setText(obj.getString("date"));
					viewholder.city_tv.setText(obj.getString("city"));
				}else{
					viewholder.date_tv.setText(Html.fromHtml("Date"));
					viewholder.city_tv.setText(Html.fromHtml("City"));
				}


				viewholder.delete_iv.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						deleteTruck((JSONObject)arg0.getTag());
					}
				});

				viewholder.edit_iv.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {
						System.out.println("clicked the edit button");
						String[] array = (view.getTag().toString()).split(";");
						updateTruckDialog(Integer.parseInt(array[0]), array[1]);					}
				});

				boolean ins_time = TruckApp.checkTime(trucksArray.getJSONObject(position).getString("vehicle_insurance_expiry_date")+" 00:00:00");
				boolean fit_time = TruckApp.checkTime(trucksArray.getJSONObject(position).getString("fitness_certificate_expiry_date")+" 00:00:00");
				boolean np_time  = TruckApp.checkTime(trucksArray.getJSONObject(position).getString("national_permit_expiry_date")+" 00:00:00");

				if(ins_time && fit_time && np_time){
					viewholder.edit_iv.setImageResource(R.drawable.ic_edit_blue);
				}else{
					viewholder.edit_iv.setImageResource(R.drawable.ic_edit_r);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		protected void updateTruckDialog(final int parseInt, String string) {
			createTruckDialog.show();
			try {
				truckRegNo_et.setVisibility(View.GONE);
				truckTypes_spn.setVisibility(View.GONE);
				createTruck_btn.setText("Update");
				final JSONObject jsonObj = new JSONObject(string);
				updia_tit_tv.setText(jsonObj.getString("truck_reg_no")+" Truck Info");
				insurance_expiry_tv.setText(jsonObj.getString("vehicle_insurance_expiry_date"));
				fitness_expiry_tv.setText(jsonObj.getString("fitness_certificate_expiry_date"));
				national_permit_tv.setText(jsonObj.getString("national_permit_expiry_date"));
				rc_no_et.setText(jsonObj.getString("rc_no"));
				boolean ins_time = TruckApp.checkTime(jsonObj.getString("vehicle_insurance_expiry_date")+" 00:00:00");
				boolean fit_time = TruckApp.checkTime(jsonObj.getString("fitness_certificate_expiry_date")+" 00:00:00");
				boolean np_time  = TruckApp.checkTime(jsonObj.getString("national_permit_expiry_date")+" 00:00:00");


				if(!ins_time){
					insurance_expiry_tv.setError("");
				}else{
					insurance_expiry_tv.setError(null);
				}
				if(!fit_time){
					fitness_expiry_tv.setError("");
				}else{
					fitness_expiry_tv.setError(null);
				}
				if(!np_time){
					national_permit_tv.setError("");
				}else{
					national_permit_tv.setError(null);
				}

				rc_no_et.setText(jsonObj.getString("rc_no"));
				insurance_amount_et.setText(jsonObj.getString("insurance_amount"));
				insurance_expiry_tv.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Calendar c = Calendar.getInstance();
						dateDialog = new DatePickerDialog(context,mInsDateLst, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
						dateDialog.show();
					}
				});
				fitness_expiry_tv.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Calendar c = Calendar.getInstance();
						dateDialog = new DatePickerDialog(context,mFitDateLst, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
						dateDialog.show();
					}
				});

				national_permit_tv.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Calendar c = Calendar.getInstance();

						dateDialog = new DatePickerDialog(context,mNPDateLst, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
						dateDialog.show();
					}
				});

				national_permit_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0, boolean arg1) {

						if(arg1){
							natpermit_layout.setVisibility(View.VISIBLE);
						}else {
							natpermit_layout.setVisibility(View.GONE);
						}

					}
				});

				if(jsonObj.getString("national_permit_available") != null && jsonObj.getString("national_permit_available").equalsIgnoreCase("0"))
				{ national_permit_cb.setChecked(false);}
				else
				{ national_permit_cb.setChecked(true); }


				createTruck_btn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if(detectCnnection.isConnectingToInternet()){
							String ins_exp = insurance_expiry_tv.getText().toString();
							String fit_exp = fitness_expiry_tv.getText().toString();
							String np_exp  = national_permit_tv.getText().toString();
							String ins_amt = insurance_amount_et.getText().toString();
							String rc_no   = rc_no_et.getText().toString().trim();
							if(rc_no.length()!=0){
								try {
									new CreateTruck(sharedPreferences.getString("accountID", ""),sharedPreferences.getString("uid", ""),
											jsonObj.getString("truck_reg_no"),null,
											String.valueOf(sharedPreferences.getInt("gps", -1)),
											ins_exp,fit_exp,np_exp,ins_amt,rc_no,national_permit_cb.isChecked(),jsonObj.getString("id_truck")).execute();

								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}else{
								TruckApp.editTextValidation(
										rc_no_et,
										rc_no,
										getResources().getString(
												R.string.rc_no_error));
							}						
						}else {
							Toast.makeText(context,
									getResources().getString(R.string.internet_str),
									Toast.LENGTH_LONG).show();
						}
					}
				});

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}






	@SuppressWarnings("unchecked")
	public void showUpdateDialog(final String truckRegNo){
		final SharedPreferences preferences       = context.getSharedPreferences("trucks", Context.MODE_PRIVATE);
		final HashMap<String, String> retrivedMap = (HashMap<String,String>) preferences.getAll(); 
		JSONObject obj1 =null;
		String ct="";
		try {
			if(retrivedMap.containsKey(truckRegNo)){
			 obj1 = new JSONObject(retrivedMap.get(truckRegNo));
			 if(obj1!=null){
				 if(!obj1.getString("city").equals(null))
				 ct = obj1.getString("city");
			 }
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		city_actv.setText(ct);
		


		dialogView.findViewById(R.id.update_data_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//final Spinner  city_spn       = (Spinner)dialogView.findViewById(R.id.city_spn);
				
				final StringBuilder builder1 = new StringBuilder();
				(builder1.append(datePicker.getYear()).append("-").append(TrucksActivity.set2Digit(datePicker.getMonth()+1)).
						append("-").append(TrucksActivity.set2Digit(datePicker.getDayOfMonth()))).toString();

				System.out.println("date:"+builder1.toString());

				if(detectCnnection.isConnectingToInternet()){
					try{
						/*SetTruckInfo truckInfoObj =	new SetTruckInfo(sharedPreferences.getString("accountID", ""), 
								sharedPreferences.getString("uid", ""),truckRegNo,
								builder1.toString(), city_spn.getSelectedItem().toString(), sharedPreferences.getString("phone", ""),
								"Updating truck info",TrucksActivity.this,pDialog,parser);*/
						
						SetTruckInfo truckInfoObj =	new SetTruckInfo(sharedPreferences.getString("accountID", ""), 
								sharedPreferences.getString("uid", ""),truckRegNo,
								builder1.toString(), city_actv.getText().toString(), sharedPreferences.getString("phone", ""),
								"Updating truck info",TrucksActivity.this,pDialog,parser);
						truckInfoObj.setDataDownloadListener(new DataDownloadListener() {

							@Override
							public void dataDownloadedSuccessfully(Object data) {
								try{
									if(((JSONObject)data).getString("status").equals("1")){
										try{
											JSONObject obj = new JSONObject();
											obj.put("date", builder1.toString().replace(" ", "\n" ));
											//obj.put("city", city_spn.getSelectedItem());
											obj.put("city",  city_actv.getText().toString());
											retrivedMap.put(truckRegNo, obj.toString());
											SharedPreferences.Editor editor = preferences.edit();    
											for (String s : retrivedMap.keySet()) {
												editor.putString(s, retrivedMap.get(s));
											}
											editor.commit();
											adapter = new TrucksAdapter(trucksArray, TrucksActivity.this);
											trucks_lv.setAdapter(adapter);
											adapter.notifyDataSetChanged();
										}
										catch (Exception e) {
											System.out.println("while saving data");
										}}
								}catch(Exception e){

								}
							}

							@Override
							public void dataDownloadFailed() {
								// TODO Auto-generated method stub

							}
						});
						truckInfoObj.execute();
					}catch(Exception e){
						System.out.println("Ex inn on resume:"+e.toString());
					}
				}else {
					Toast.makeText(context, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
				}
				alertDialog.dismiss();
			}});
		alertDialog.setView(dialogView);
		alertDialog.show();
	}

	@Override
	protected void onDestroy() {
		adapter    = null;
		trucks_lv  = null;
		trucksArray= null;
		detectCnnection  = null;
		parser           = null;
		dialogView       = null;
		alertDialog      = null;
		sharedPreferences = null;
		trucks_lv        = null;
		trucksArray      = null;
		adapter          = null;
		pDialog          = null;
		deletedialog     = null;
		super.onDestroy();
	}

	private static String set2Digit(int val){
		String no= String.valueOf(val);
		if(1 == no.length())
		{ no="0"+no; }
		return no;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_rewards:
			startActivity(new Intent(context, LoyaltyPointsActivity.class));
			break;
		case R.id.action_refresh:
			fetchTruckTypes();
			break;
		case R.id.action_add:
			createTruck();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void createTruck(){
		updia_tit_tv.setText("Create Truck");
		truckRegNo_et.setText("");
		insurance_amount_et.setText("");
		truckRegNo_et.setVisibility(View.VISIBLE);
		truckTypes_spn.setVisibility(View.VISIBLE);
		insurance_expiry_tv.setText("0000-00-00");
		fitness_expiry_tv.setText("0000-00-00");
		national_permit_tv.setText("0000-00-00");
		rc_no_et.setText("");
		national_permit_cb.setChecked(false);
		createTruck_btn.setText("Create");

		createTruckDialog.show();


		insurance_expiry_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Calendar c = Calendar.getInstance();
				dateDialog = new DatePickerDialog(context,mInsDateLst, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
				dateDialog.show();
			}
		});
		fitness_expiry_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Calendar c = Calendar.getInstance();
				dateDialog = new DatePickerDialog(context,mFitDateLst, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
				dateDialog.show();
			}
		});

		national_permit_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Calendar c = Calendar.getInstance();

				dateDialog = new DatePickerDialog(context,mNPDateLst, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
				dateDialog.show();
			}
		});

		national_permit_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				System.out.println("checkbox change listener");

				if(isChecked){
					natpermit_layout.setVisibility(View.VISIBLE);
				}else {
					natpermit_layout.setVisibility(View.GONE);
				}



			}
		});

		createTruck_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if(detectCnnection.isConnectingToInternet()){
					String truckRegnoString = truckRegNo_et.getText().toString().trim();
					String ins_exp = insurance_expiry_tv.getText().toString();
					String fit_exp = fitness_expiry_tv.getText().toString();
					String np_exp  = national_permit_tv.getText().toString();
					String ins_amt = insurance_amount_et.getText().toString();
					String rc_no   = rc_no_et.getText().toString().trim();

					if(truckRegnoString.length()!=0 && selectedTruckType!=null && rc_no.length()!=0){
						new CreateTruck(sharedPreferences.getString("accountID", ""),sharedPreferences.getString("uid", ""),
								truckRegnoString, selectedTruckType,
								String.valueOf(sharedPreferences.getInt("gps", -1)),
								ins_exp,fit_exp,np_exp,ins_amt,rc_no,national_permit_cb.isChecked()).execute();
					}else{
						TruckApp.editTextValidation(truckRegNo_et, truckRegnoString, getResources().getString(R.string.truck_reg_no));
						TruckApp.editTextValidation(
								rc_no_et,
								rc_no,
								getResources().getString(
										R.string.rc_no_error));
						if(selectedTruckType == null){
							Toast.makeText(context, "Please select truck type", Toast.LENGTH_SHORT).show();
						}
					}
				}else {
					Toast.makeText(context,
							getResources().getString(R.string.internet_str),
							Toast.LENGTH_LONG).show();
				}


			}
		});
	}

	private class CreateTruck extends AsyncTask<String, String, JSONObject>{

		String account_id,truckRegNo,truckType,gps,uid;
		String ins_exp, fit_exp,np_exp, ins_amt, rc_no,id_truck;
		boolean npavaliable;

		public CreateTruck(String account_id,String uid, String truckRegno ,
				String trucktype ,String gps,String ins_exp,String fit_exp,String np_exp,String ins_amt,String rc_no,boolean npavaliable) {
			this.account_id  = account_id;
			this.truckRegNo  = truckRegno;
			this.gps         = gps;
			this.uid         = uid;
			this.truckType   = trucktype;
			this.ins_exp=ins_exp;
			this.fit_exp=fit_exp;
			this.np_exp=np_exp;
			this.ins_amt=ins_amt;
			this.rc_no=rc_no;
			this.npavaliable=npavaliable;
			this.id_truck = null;
		}

		public CreateTruck(String account_id,String uid, String truckRegno ,
				String trucktype ,String gps,String ins_exp,String fit_exp,String np_exp,String ins_amt,String rc_no,boolean npavaliable,String id_truck) {
			this.account_id  = account_id;
			this.truckRegNo  = truckRegno;
			this.gps         = gps;
			this.uid         = uid;
			this.truckType   = null;
			this.ins_exp=ins_exp;
			this.fit_exp=fit_exp;
			this.np_exp=np_exp;
			this.ins_amt=ins_amt;
			this.rc_no=rc_no;
			this.npavaliable=npavaliable;
			this.id_truck = id_truck;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if(id_truck==null){
				pDialog.setMessage("Creating the truck...");
			}else{
				pDialog.setMessage("Updating truck info...");
			}
			pDialog.show();  
		}

		@Override
		protected JSONObject doInBackground(String... params) {

			JSONObject json = null;
			try {	
				StringBuilder builder = new StringBuilder();
				/*String urlParameters =
						"username=" + URLEncoder.encode(account_id, "UTF-8") +
						"&uid=" + URLEncoder.encode(uid, "UTF-8")+
						"&truck_reg_no=" + URLEncoder.encode(truckRegNo, "UTF-8")+
						"&gps=" + URLEncoder.encode(gps, "UTF-8")+
						"&id_truck_type=" + URLEncoder.encode(truckType, "UTF-8");*/

				builder.append("username=").append(
						URLEncoder.encode(account_id, "UTF-8"));
				builder.append("&uid=").append(
						URLEncoder.encode(uid, "UTF-8"));
				builder.append("&truck_reg_no=").append(
						URLEncoder.encode(truckRegNo, "UTF-8"));
				builder.append("&gps=").append(
						URLEncoder.encode(gps, "UTF-8"));
				if (truckType != null) 
				{builder.append("&id_truck_type=").append(
						URLEncoder.encode(truckType, "UTF-8")); }

				builder.append("&fitness_expiry_date=").append(
						URLEncoder.encode(fit_exp, "UTF-8"));
				builder.append("&insurance_expiry_date=").append(
						URLEncoder.encode(ins_exp, "UTF-8"));
				builder.append("&rc_no=").append(
						URLEncoder.encode(rc_no, "UTF-8"));
				builder.append("&np_available=").append(
						URLEncoder.encode((npavaliable?"1":"0"), "UTF-8"));
				builder.append("&np_expiry_date=").append(
						URLEncoder.encode(np_exp, "UTF-8"));
				builder.append("&insurance_amount=").append(
						URLEncoder.encode(ins_amt, "UTF-8"));

				if (id_truck != null) 
				{ builder.append("&id_truck=").append(
						URLEncoder.encode(id_truck, "UTF-8")); }

				String res = parser.excutePost(TruckApp.setTruckURL,builder.toString());
				System.out.println("URLPARAMS"+builder.toString()+"TRUCKS MKL:"+res);
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
			TruckApp.checkPDialog(pDialog);
			if (result != null){
				try {
					if (0 == result.getInt("status")) {
						Toast.makeText(context,"Failed to create the truck",Toast.LENGTH_LONG).show();
					}else if (1 == result.getInt("status")){
						if(id_truck == null){
							Toast.makeText(context,"Successfully created truck info",Toast.LENGTH_LONG).show();
						}else{
							Toast.makeText(context,"Successfully updated truck info",Toast.LENGTH_LONG).show();
						}
						fetchTrucks();
					}
					TruckApp.checkDialog(createTruckDialog);
				}catch (Exception e){
					System.out.println("ex in get leads"+e.toString());
				}
			}else{
				Toast.makeText(context,getResources().getString(R.string.exceptionmsg),Toast.LENGTH_LONG).show();
			}
		}
	}

	public void deleteTruck(JSONObject tid){
		if(detectCnnection.isConnectingToInternet()){

			deletedialog = null;
			try{
				deletedialog = new Dialog(TrucksActivity.this, android.R.style.Theme_Dialog);
				deletedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				deletedialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
				deletedialog.setContentView(R.layout.deletetruckdialog);
				((TextView) deletedialog.findViewById(R.id.deletetv)).
				setText("Do you want to delete truck with reg no "+tid.getString("truck_reg_no")+" ?");
				Button send_btn      = (Button) deletedialog.findViewById(R.id.send_btn);
				Button cancel_btn    = (Button) deletedialog.findViewById(R.id.cancel_btn);
				send_btn.setTag(tid.getString("id_truck"));
				send_btn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						new DeleteTruck(sharedPreferences.getString("uid", ""),arg0.getTag().toString()).execute();
					}
				});
				cancel_btn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						deletedialog.dismiss();
					}
				});
				deletedialog.show();

			}catch(Exception e){
				System.out.println("Ex inn on resume:"+e.toString());
			}
		}else {
			Toast.makeText(context, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
		}
	}

	private class DeleteTruck extends AsyncTask<String, String, JSONObject>{

		String tid;
		public DeleteTruck(String uid,String tid) {
			this.tid    = tid;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog.setMessage("Deleting a truck...");
			pDialog.show();  
		}

		@Override
		protected JSONObject doInBackground(String... params) {

			JSONObject json = null;
			try {	
				StringBuilder builder= new StringBuilder();
				builder.append("&tid=").append(URLEncoder.encode(tid, "UTF-8"));				
				String res = parser.excutePost(TruckApp.deleteTruckURL,builder.toString());
				System.out.println("Params:"+builder.toString()+"delete truck o/p"+res);
				json = new JSONObject(res);
			}catch(Exception e){
				Log.e("delete doin ex",e.toString());
			}
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			TruckApp.checkPDialog(pDialog);
			if (result != null){
				try {
					if (0 == result.getInt("status")) {
						Toast.makeText(context,"Failed to delete truck",Toast.LENGTH_LONG).show();
					}else if (1 == result.getInt("status")){
						TruckApp.checkDialog(deletedialog);
						Toast.makeText(context,"Successfully deleted truck",Toast.LENGTH_LONG).show();
						fetchTrucks();
					}
				}catch (Exception e){
					System.out.println("ex in get leads"+e.toString());
				}
			}else{
				Toast.makeText(context,getResources().getString(R.string.exceptionmsg),Toast.LENGTH_LONG).show();
			}
		}
	}


	// Register  DatePickerDialog listener
	public DatePickerDialog.OnDateSetListener mInsDateLst =
			new DatePickerDialog.OnDateSetListener() {              
		// the callback received when the user "sets" the Date in the DatePickerDialog
		@Override
		public void onDateSet(DatePicker view, int yearSelected, 
				int monthOfYear, int dayOfMonth) {
			String date = ((new StringBuilder().append(yearSelected).append("-").append(set2Digit(monthOfYear+1)).append("-")
					.append(set2Digit(dayOfMonth))).toString());
			System.out.println("Date:"+date);
			insurance_expiry_tv.setText(date);
		}
	};

	// Register  DatePickerDialog listener
	DatePickerDialog.OnDateSetListener mFitDateLst =
			new DatePickerDialog.OnDateSetListener() {              
		// the callback received when the user "sets" the Date in the DatePickerDialog
		@Override
		public void onDateSet(DatePicker view, int yearSelected, 
				int monthOfYear, int dayOfMonth) {
			String date = ((new StringBuilder().append(yearSelected).append("-").append(set2Digit(monthOfYear+1)).append("-")
					.append(set2Digit(dayOfMonth))).toString());
			System.out.println("Date:"+date);
			fitness_expiry_tv.setText(date);
		}
	};

	// Register  DatePickerDialog listener
	DatePickerDialog.OnDateSetListener mNPDateLst =
			new DatePickerDialog.OnDateSetListener() {              
		// the callback received when the user "sets" the Date in the DatePickerDialog
		@Override
		public void onDateSet(DatePicker view, int yearSelected, 
				int monthOfYear, int dayOfMonth) {
			String date = ((new StringBuilder().append(yearSelected).append("-").append(set2Digit(monthOfYear+1)).append("-")
					.append(set2Digit(dayOfMonth))).toString());
			System.out.println("Date:"+date);
			national_permit_tv.setText(date);
		}
	};




	/*public void reportproblem(){
	String trace = null,line;
	try{
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(getApplicationContext().openFileInput("stack.trace")));
		while((line = reader.readLine()) != null) {
			trace += line+"\n";
		}
	} catch(FileNotFoundException fnfe) {
		// ...
	} catch(IOException ioe) {
		// ...
	}catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 

	Intent sendIntent = new Intent(Intent.ACTION_SEND);
	String subject = "Error report";
	String body =
			"Mail this to gdinesh029@gmail.com: "+
					"\n\n"+
					trace+
					"\n\n";

	sendIntent.putExtra(Intent.EXTRA_EMAIL,
			new String[] {"gdinesh029@gmail.com"});
	sendIntent.putExtra(Intent.EXTRA_TEXT, body);
	sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
	sendIntent.setType("message/rfc822");

	startActivity(
			Intent.createChooser(sendIntent, "Title:"));
	deleteFile("stack.trace");
}
	 */


}
