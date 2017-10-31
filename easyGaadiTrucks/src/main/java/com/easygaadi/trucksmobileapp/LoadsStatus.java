package com.easygaadi.trucksmobileapp;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;

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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.JSONParser;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class LoadsStatus extends AppCompatActivity implements OnClickListener {

	Context context;
	SharedPreferences sharedPreferences;
	private ConnectionDetector detectConnection;
	JSONParser parser;
	ProgressDialog pDialog;
	private InterstitialAd mInterstitialAd;


	private static final String TAG_RESULT = "predictions";

	
	ListView loadsLV;
	JSONArray loadsArray;
	LoadStatusAdapter adapter;
	boolean  loadMore_st, srcset=true, destset=true, pickuppointset=true;
	int      totalLoads,offset;
	Dialog repostDialog;

	Dialog dateDialog,timeDialog;
	AutoCompleteTextView src_et,des_et, pickuppoint_actv;
	Spinner truckType_spn,goodsType_spn;
	EditText expectedPrice_et,comment_et,loadingchrgs_et,unloadingchrgs_et,nooftrucks_et;
	protected Button date_btn,submit_btn;
	ImageView close_iv;

	ArrayList<String> names;
	ArrayAdapter<String> namesAdapter;
	String browserKey = "AIzaSyAqh4el3Bd1lafjwQMQ7w6hJ_OVZWEst-0";
	String url;
	
	ArrayList<String> truckTypeKeys,truckTypesValues,goodsTypesKeys,goodsTypesValues;
	String selectedTruckType=null,selectedGoodsType=null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loadstatus);

		context           = this;
		detectConnection  = new ConnectionDetector(context);
		parser            = JSONParser.getInstance();
		pDialog           = new ProgressDialog(this);
		pDialog.setCancelable(false);
		sharedPreferences = getApplicationContext().getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);
		loadsLV  = (ListView)findViewById(R.id.statuslv);
		offset          = 0;

		repostDialog    = new Dialog(LoadsStatus.this, R.style.AppTheme);
		repostDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		repostDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
		repostDialog.setContentView(R.layout.repostload_dia);

		src_et        = (AutoCompleteTextView)repostDialog.findViewById(R.id.srcactv);
		des_et        = (AutoCompleteTextView)repostDialog.findViewById(R.id.desactv);
		truckType_spn = (Spinner)repostDialog.findViewById(R.id.trucktype_spn);
		goodsType_spn = (Spinner)repostDialog.findViewById(R.id.goodstype_spn);
		expectedPrice_et  = (EditText)repostDialog.findViewById(R.id.expprc_et);
		pickuppoint_actv    = (AutoCompleteTextView)repostDialog.findViewById(R.id.pickuppoint_actv);
		comment_et        = (EditText)repostDialog.findViewById(R.id.comment_et);
		loadingchrgs_et   = (EditText)repostDialog.findViewById(R.id.loadingcharges_et);
		unloadingchrgs_et = (EditText)repostDialog.findViewById(R.id.unloadingcharges_et);
		nooftrucks_et     = (EditText)repostDialog.findViewById(R.id.nooftrucks_et);
		date_btn          = (Button)repostDialog.findViewById(R.id.date_btn);
		submit_btn        = (Button)repostDialog.findViewById(R.id.submit_btn);
		close_iv         = (ImageView)repostDialog.findViewById(R.id.close_iv);
		
		pickuppoint_actv.setAdapter(new GooglePlaceAutoCompleteAdapter(context, android.R.layout.simple_list_item_1, "India", false));
		src_et.setAdapter(new GooglePlaceAutoCompleteAdapter(context, android.R.layout.simple_list_item_1, "India", false));
		des_et.setAdapter(new GooglePlaceAutoCompleteAdapter(context, android.R.layout.simple_list_item_1, "India", false));
		src_et.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				srcset = true;
				
			}
			
		});
		des_et.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				destset = true;
				
			}
			
		});
		pickuppoint_actv.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				pickuppointset = true;
				
			}
			
		});
		
		
		fetchPostedLoads(0);
		intializeGoogleAd();

		loadsLV.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, 
					int scrollState) {}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, 
					int visibleItemCount, int totalItemCount) {
				if (totalLoads > 10) {	
					System.out.println("loadMore_st"+loadMore_st);
					if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
					{
						if(loadMore_st == false)
						{
							loadMore_st = true;
							if (loadsArray.length() < totalLoads) {
								offset = offset+10;
								System.out.println("Value to be loaded:"+offset);
								fetchPostedLoads(offset);
							}else{
								System.out.println("size is  less than 5");
							}
						}
					}
				}
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



	@Override
	protected void onResume() {
		super.onResume();
		TruckApp.getInstance().trackScreenView("LoadsStatus Screen");
	}




	public void fetchPostedLoads(int offset){
		if(detectConnection.isConnectingToInternet()){
			if(0==offset){
				loadsArray = new JSONArray();
				adapter           = new LoadStatusAdapter(loadsArray, LoadsStatus.this);
				loadsLV.setAdapter(adapter);
			}
			try{
				new GetPostedLoads(sharedPreferences.getString("uid", ""),String.valueOf(offset)).execute();
			}catch(Exception e){
				System.out.println("Ex inn on resume:"+e.toString());
			}
		}else {
			Toast.makeText(context, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
		}
	}


	private class GetPostedLoads extends AsyncTask<String, String, JSONObject>{

		String uid,offset;
		public GetPostedLoads(String uid,String offset) {
			this.uid    = uid;
			this.offset = offset;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog.setMessage("Fetching loads...");
			pDialog.show();  
		}

		@Override
		protected JSONObject doInBackground(String... params) {

			JSONObject json = null;
			try {	
				StringBuilder builder= new StringBuilder();
				builder.append("uid=").append(URLEncoder.encode(uid, "UTF-8"));
				builder.append("&offset=").append(URLEncoder.encode(offset, "UTF-8"));				
				String res = parser.excutePost(TruckApp.getPostedLoadsURL,builder.toString());
				System.out.println("Params:"+builder.toString()+"get posted loads o/p"+res);
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
						Toast.makeText(context,"No records available",Toast.LENGTH_LONG).show();
					}else if (1 == result.getInt("status")){
						totalLoads = result.getInt("count");
						if(totalLoads==0){
							Toast.makeText(context,"No records available",Toast.LENGTH_LONG).show();
						}
						JSONArray truckArray1 = result.getJSONArray("data");
						for (int i = 0; i < truckArray1.length(); i++) {
							loadsArray.put(truckArray1.getJSONObject(i));
						}
						fetchTruckTypes();
						truckArray1=null;
						if(totalLoads > loadsArray.length()){
							loadMore_st=false;
						}else{
							loadMore_st=true;
						}
						adapter.notifyDataSetChanged();
					}
				}catch (Exception e){
					System.out.println("ex in get leads"+e.toString());
				}
			}else{
				Toast.makeText(context,getResources().getString(R.string.exceptionmsg),Toast.LENGTH_LONG).show();
			}
		}
	}


	public class LoadStatusAdapter extends BaseAdapter{
		Activity       activity;
		LayoutInflater inflater ;
		JSONArray      loads;

		public LoadStatusAdapter(JSONArray loads,Activity activity) {
			this.loads       = loads;
			this.activity    = activity;
			this.inflater    = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		public class ViewHolder{
			TextView reqid_tv,src_tv,des_tv,goodstype_tv,trucktype_tv,
			exppri_tv,date_tv,status_tv,comment_tv,pickup_tit,pickup_tv,commenttit,
			goodstypetit,trucktypetit,exppritit,datetit,statustit,datecreated_tit,datecreated_tv,
			nooftrucks_tit,nooftrucks_tv,loading_tit,loading_tv,unloading_tit,unloading_tv;
			Button   cancel_btn,quotes_btn,repost_btn;
		}


		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return loads.length();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			try {
				return loads.get(arg0);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewholder=null;
			if(convertView == null){
				viewholder              = new ViewHolder();
				convertView             = inflater.inflate(R.layout.loadstatusitem, parent, false);
				viewholder.reqid_tv     = (TextView)convertView.findViewById(R.id.reqid_tv);
				viewholder.src_tv       = (TextView)convertView.findViewById(R.id.src_tv);
				viewholder.des_tv       = (TextView)convertView.findViewById(R.id.des_tv);
				viewholder.goodstype_tv = (TextView)convertView.findViewById(R.id.goodstype_tv);
				viewholder.goodstypetit = (TextView)convertView.findViewById(R.id.goodstit);
				viewholder.trucktype_tv = (TextView)convertView.findViewById(R.id.trucktype_tv);
				viewholder.trucktypetit = (TextView)convertView.findViewById(R.id.trucktyptit);
				viewholder.exppri_tv    = (TextView)convertView.findViewById(R.id.exppri_tv);
				viewholder.exppritit    = (TextView)convertView.findViewById(R.id.exppritit);
				viewholder.date_tv      = (TextView)convertView.findViewById(R.id.date_tv);
				viewholder.datetit      = (TextView)convertView.findViewById(R.id.datetit);
				viewholder.status_tv    = (TextView)convertView.findViewById(R.id.status_tv);
				viewholder.statustit    = (TextView)convertView.findViewById(R.id.statustit);
				viewholder.comment_tv   = (TextView)convertView.findViewById(R.id.comment_tv);
				viewholder.commenttit   = (TextView)convertView.findViewById(R.id.comment_tit);
				viewholder.pickup_tit   = (TextView)convertView.findViewById(R.id.pickup_tit);
				viewholder.pickup_tv   = (TextView)convertView.findViewById(R.id.pickup_tv);
				viewholder.datecreated_tit  = (TextView)convertView.findViewById(R.id.datecreated_tit);
				viewholder.datecreated_tv   = (TextView)convertView.findViewById(R.id.datecreated_tv);
				viewholder.nooftrucks_tit   = (TextView)convertView.findViewById(R.id.nooftrucks_tit);
				viewholder.nooftrucks_tv    = (TextView)convertView.findViewById(R.id.nooftrucks_tv);
				viewholder.loading_tit      = (TextView)convertView.findViewById(R.id.loadingcharge_tit);
				viewholder.loading_tv       = (TextView)convertView.findViewById(R.id.loadingcharge_tv);
				viewholder.unloading_tit    = (TextView)convertView.findViewById(R.id.unloadingcharge_tit);
				viewholder.unloading_tv     = (TextView)convertView.findViewById(R.id.unloadingcharge_tv);


				viewholder.cancel_btn   = (Button)convertView.findViewById(R.id.cancel_btn);
				viewholder.quotes_btn   = (Button)convertView.findViewById(R.id.quotes_btn);
				viewholder.repost_btn   = (Button)convertView.findViewById(R.id.repost_btn);
				convertView.setTag(viewholder);
			}else{
				viewholder = (ViewHolder)convertView.getTag();
			}
			setData(viewholder, position);
			return convertView;
		}

		private void setData(ViewHolder viewholder, int position) {
			try {
				JSONObject obj = loads.getJSONObject(position);
				viewholder.reqid_tv.setText("Load Id:#"+obj.getString("id_load_truck_request"));
				viewholder.src_tv.setText(obj.getString("source_city"));
				viewholder.des_tv .setText(obj.getString("destination_city"));



				if(obj.getString("goods_type").trim().length()==0){
					viewholder.goodstypetit.setVisibility(View.GONE);
					viewholder.goodstype_tv.setVisibility(View.GONE);
				}else{
					viewholder.goodstypetit.setVisibility(View.VISIBLE);
					viewholder.goodstype_tv.setVisibility(View.VISIBLE);
					viewholder.goodstype_tv.setText(obj.getString("goods_type"));
				}

				if(obj.getString("truck_type").trim().length()==0){
					viewholder.trucktypetit.setVisibility(View.GONE);
					viewholder.trucktype_tv.setVisibility(View.GONE);
				}else{
					viewholder.trucktypetit.setVisibility(View.VISIBLE);
					viewholder.trucktype_tv.setVisibility(View.VISIBLE);
					viewholder.trucktype_tv.setText(obj.getString("truck_type"));
				}



				if(obj.getString("expected_price").trim().length()==0){
					viewholder.exppritit .setVisibility(View.GONE);
					viewholder.exppri_tv.setVisibility(View.GONE);
				}else{
					viewholder.exppritit.setVisibility(View.VISIBLE);
					viewholder.exppri_tv.setVisibility(View.VISIBLE);
					viewholder.exppri_tv .setText(obj.getString("expected_price"));
				}

				if(obj.getString("date_required").trim().length()==0){
					viewholder.datetit .setVisibility(View.GONE);
					viewholder.date_tv .setVisibility(View.GONE);
				}else{
					viewholder.datetit.setVisibility(View.VISIBLE);
					viewholder.date_tv.setVisibility(View.VISIBLE);
					viewholder.date_tv .setText(obj.getString("date_required"));
				}

				if(obj.getString("status").trim().length()==0){
					viewholder.statustit .setVisibility(View.GONE);
					viewholder.status_tv .setVisibility(View.GONE);
				}else{
					viewholder.statustit.setVisibility(View.VISIBLE);
					viewholder.status_tv.setVisibility(View.VISIBLE);
					viewholder.status_tv .setText(obj.getString("status"));
				}

				if(obj.getString("no_of_trucks").trim().equalsIgnoreCase("0")){
					viewholder.nooftrucks_tit .setVisibility(View.GONE);
					viewholder.nooftrucks_tv .setVisibility(View.GONE);
				}else{
					viewholder.nooftrucks_tit.setVisibility(View.VISIBLE);
					viewholder.nooftrucks_tv.setVisibility(View.VISIBLE);
					viewholder.nooftrucks_tv .setText(obj.getString("no_of_trucks"));
				}

				if(obj.getString("loading_charge").trim().equalsIgnoreCase("0")){
					viewholder.loading_tit .setVisibility(View.GONE);
					viewholder.loading_tv .setVisibility(View.GONE);
				}else{
					viewholder.loading_tit.setVisibility(View.VISIBLE);
					viewholder.loading_tv.setVisibility(View.VISIBLE);
					viewholder.loading_tv .setText(obj.getString("loading_charge"));
				}

				if(obj.getString("unloading_charge").trim().equalsIgnoreCase("0")){
					viewholder.unloading_tit .setVisibility(View.GONE);
					viewholder.unloading_tv .setVisibility(View.GONE);
				}else{
					viewholder.unloading_tit.setVisibility(View.VISIBLE);
					viewholder.unloading_tv.setVisibility(View.VISIBLE);
					viewholder.unloading_tv .setText(obj.getString("unloading_charge"));
				}


				if(obj.getString("comment").trim().length()==0){
					viewholder.commenttit .setVisibility(View.GONE);
					viewholder.comment_tv .setVisibility(View.GONE);
				}else{
					viewholder.commenttit.setVisibility(View.VISIBLE);
					viewholder.comment_tv.setVisibility(View.VISIBLE);
					viewholder.comment_tv .setText(obj.getString("comment"));
				}

				//
				if(obj.getString("date_created").trim().length()==0){
					viewholder.datecreated_tit .setVisibility(View.GONE);
					viewholder.datecreated_tv .setVisibility(View.GONE);
				}else{
					viewholder.datecreated_tit.setVisibility(View.VISIBLE);
					viewholder.datecreated_tv.setVisibility(View.VISIBLE);
					viewholder.datecreated_tv .setText(obj.getString("date_created"));
				}

				if(obj.getString("pickup_point").trim().length()==0){
					viewholder.pickup_tit .setVisibility(View.GONE);
					viewholder.pickup_tv .setVisibility(View.GONE);
				}else{
					viewholder.pickup_tit .setVisibility(View.VISIBLE);
					viewholder.pickup_tv.setVisibility(View.VISIBLE);
					viewholder.pickup_tv.setText(obj.getString("pickup_point"));
				}

				String val = (obj.getString("tracking").equalsIgnoreCase("1"))?"Avaliable":"Not Avaliable";
				viewholder.cancel_btn .setTag(obj.getString("id_load_truck_request"));
				viewholder.repost_btn.setTag(obj.getString("id_load_truck_request"));
				if(0 == Integer.parseInt(obj.getString("quotecount"))){
					viewholder.quotes_btn.setVisibility(View.GONE);
				}else{
					viewholder.quotes_btn.setVisibility(View.VISIBLE);
					viewholder.quotes_btn.setText(obj.getString("quotecount")+" Quotes");

				}
				viewholder.quotes_btn .setTag(obj.getString("id_load_truck_request"));

				viewholder.repost_btn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {
						new GetLoadInfo(view.getTag().toString()).execute();
					}
				});

				viewholder.quotes_btn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						System.out.println("loadid:"+v.getTag().toString());
						Intent next_in = new Intent(context, QuotesActivity.class);
						next_in.putExtra("loadid", v.getTag().toString());
						startActivity(next_in);
					}
				});
				viewholder.cancel_btn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						System.out.println("loadid:"+v.getTag().toString());
						cancelLoad(v.getTag().toString());
					}
				});
				submit_btn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						submit_act();
					}
				});
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}


	class GetLoadInfo extends AsyncTask<String, String, JSONObject>{


		String lid;
		public GetLoadInfo(String lid) {
			this.lid    = lid;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog.setMessage("Loading the load info...");
			pDialog.show();  
		}

		@Override
		protected JSONObject doInBackground(String... params) {

			JSONObject json = null;
			try {	
				StringBuilder builder= new StringBuilder();
				builder.append("id_load_truck_request=").append(URLEncoder.encode(lid, "UTF-8"));				
				String res = parser.excutePost(TruckApp.getLoadInfo,builder.toString());
				System.out.println("Params:"+builder.toString()+"get load info o/p"+res);
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
						Toast.makeText(context,"Failed to get load info",Toast.LENGTH_LONG).show();
					}else if (1 == result.getInt("status")){
						showRepostDialog((result.getJSONObject("data")).toString());
					}
				}catch (Exception e){
					System.out.println("ex in get leads"+e.toString());
				}
			}else{
				Toast.makeText(context,getResources().getString(R.string.exceptionmsg),Toast.LENGTH_LONG).show();
			}
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



	public void showRepostDialog(String data){
		repostDialog.show();
		try {
			resetDialog();
			JSONObject jObj = new JSONObject(data);
			src_et.setText(jObj.getString("source_address"));
			des_et.setText(jObj.getString("destination_address"));
			nooftrucks_et.setText(jObj.getString("no_of_trucks"));
			expectedPrice_et.setText(jObj.getString("expected_price"));
			date_btn.setText(jObj.getString("date_required"));
			pickuppoint_actv.setText(jObj.getString("pickup_point"));
			comment_et.setText(jObj.getString("comment"));
			loadingchrgs_et.setText(jObj.getString("loading_charge"));
			unloadingchrgs_et.setText(jObj.getString("unloading_charge"));
			if(truckTypeKeys!=null && truckTypeKeys.contains(jObj.getString("id_truck_type"))){
				int tpos = truckTypeKeys.indexOf(jObj.getString("id_truck_type"));
				selectedTruckType = truckTypesValues.get(tpos);
				truckType_spn.setSelection(tpos);;
			}
			if(goodsTypesKeys!=null && goodsTypesKeys.contains(jObj.getString("id_goods_type"))){
				int gpos = goodsTypesKeys.indexOf(jObj.getString("id_goods_type"));
				selectedGoodsType = goodsTypesValues.get(gpos);
				goodsType_spn.setSelection(gpos);;
			}
			close_iv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					TruckApp.checkDialog(repostDialog);				
				}
			});
			
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
					if(srcset){
						srcset = false;
					}
					
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
					/*if (detectConnection.isConnectingToInternet()) {
						names = new ArrayList<String>();
						updateList(s.toString(), des_et);
					} else {
						System.out.println("NO NET");
					}*/
					if(destset){
						destset = false;
					}
					
				}

				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
						int arg3) {
				}

				@Override
				public void afterTextChanged(Editable arg0) {
				}
			});
			
			pickuppoint_actv.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					if(pickuppointset){
						pickuppointset = false;
					}
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					
				}
			});

			date_btn.setOnClickListener(this);

			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	private void resetDialog() {
		src_et.setText("");
		des_et.setText("");
		nooftrucks_et.setText("");
		expectedPrice_et.setText("");
		date_btn.setText("");
		pickuppoint_actv.setText("");
		comment_et.setText("");
		loadingchrgs_et.setText("");
		unloadingchrgs_et.setText("");
		selectedGoodsType=null;
		selectedTruckType=null;		
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

	
	
	public void cancelLoad(String lid){
		if(detectConnection.isConnectingToInternet()){
			try{
				new CancelLoad(lid).execute();
			}catch(Exception e){
				System.out.println("Ex inn on resume:"+e.toString());
			}
		}else {
			Toast.makeText(context, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
		}
	}

	private class CancelLoad extends AsyncTask<String, String, JSONObject>{

		String lid;
		public CancelLoad(String lid) {
			this.lid    = lid;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog.setMessage("Cancelling a load...");
			pDialog.show();  
		}

		@Override
		protected JSONObject doInBackground(String... params) {

			JSONObject json = null;
			try {	
				StringBuilder builder= new StringBuilder();
				builder.append("lid=").append(URLEncoder.encode(lid, "UTF-8"));				
				String res = parser.excutePost(TruckApp.cancelLoadURL,builder.toString());
				System.out.println("Params:"+builder.toString()+"cancel load o/p"+res);
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
						Toast.makeText(context,"Failed to cancel load",Toast.LENGTH_LONG).show();
					}else if (1 == result.getInt("status")){
						Toast.makeText(context,"Successfully  cancelled load",Toast.LENGTH_LONG).show();
						fetchPostedLoads(0);
					}
				}catch (Exception e){
					System.out.println("ex in get leads"+e.toString());
				}
			}else{
				Toast.makeText(context,getResources().getString(R.string.exceptionmsg),Toast.LENGTH_LONG).show();
			}
		}
	}


	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.locate_truck_menu , menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_refresh:
			fetchPostedLoads(0);
		default:
			break;
		}
		return super.onOptionsItemSelected(item);

	}*/
	
	
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
				selectedGoodsType!=null && selectedTruckType!=null && srcset && destset && pickuppointset){

			new PostLoad(sharedPreferences.getString("uid", ""), source_str, destination_str, pickup_str, expectedpri_str, 
					comment_str, selectedTruckType, selectedGoodsType, date_str,lodcharges_str,unlodcharges_str,nooftrucks_str).execute();

		}else{
			TruckApp.editTextValidation(src_et, source_str, getResources().getString(R.string.source_error));
			TruckApp.editTextValidation(des_et, destination_str, getResources().getString(R.string.destination_error));
			TruckApp.editTextValidation(nooftrucks_et, nooftrucks_str, getResources().getString(R.string.noooftrucks_error));
			TruckApp.autocompleteValidation(src_et, srcset, "select source from dropdown");
			TruckApp.autocompleteValidation(des_et, destset, "select destination from dropdown");
			TruckApp.autocompleteValidation(pickuppoint_actv, pickuppointset, "select pickuppoint from dropdown");
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
						Toast.makeText(context,"Failed to repost the load",Toast.LENGTH_LONG).show();
					}else if (1 == result.getInt("status")){	
						Toast.makeText(context,"You have successfully reposted the load",Toast.LENGTH_LONG).show();
						repostDialog.dismiss();
						fetchPostedLoads(0);
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
				builder.append("&comment=").append(URLEncoder.encode(comment, "UTF-8"));				builder.append("&id_truck_type=").append(URLEncoder.encode(id_truck_type, "UTF-8"));
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





	@Override
	protected void onDestroy() {
		sharedPreferences = null;
		detectConnection= null;
		parser= null;
		pDialog= null;
		loadsArray= null;
		loadsLV= null;
		adapter= null;
		super.onDestroy();
	}


}
