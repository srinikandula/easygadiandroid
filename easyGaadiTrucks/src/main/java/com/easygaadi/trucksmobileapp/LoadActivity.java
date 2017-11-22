package com.easygaadi.trucksmobileapp;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.JSONParser;
import com.easygaadi.utils.Constants;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class LoadActivity extends AppCompatActivity {

	Context context;

	SharedPreferences sharedPreferences,loadsPreferences;
	Editor editor;
	private ConnectionDetector detectCnnection;
	JSONParser parser;
	ProgressDialog pDialog;
	ListView infoLV;
	Dialog pricedialog;
	EditText  price_et;
	Button send_btn;
	ImageView close_btn;
	TextView load_tv;

	private InterstitialAd mInterstitialAd;
	private AdView adView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);

		context           = this;
		detectCnnection   = new ConnectionDetector(context);
		parser            = JSONParser.getInstance();
		pDialog           = new ProgressDialog(this);
		pDialog.setCancelable(false);
		sharedPreferences = getApplicationContext().getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);
		loadsPreferences  = getApplicationContext().getSharedPreferences(getResources().getString(R.string.loads_tit), MODE_PRIVATE);
		editor            = loadsPreferences.edit();


		infoLV            = (ListView)findViewById(R.id.info_lv);
		pricedialog = new Dialog(LoadActivity.this, android.R.style.Theme_Dialog);
		pricedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		pricedialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
		pricedialog.setContentView(R.layout.pricedialog);

		close_btn    = (ImageView) pricedialog.findViewById(R.id.close_iv);
		send_btn     = (Button) pricedialog.findViewById(R.id.update_btn);
		price_et     = (EditText) pricedialog.findViewById(R.id.price_et);
		load_tv      = (TextView) pricedialog.findViewById(R.id.load_tv);
		adView = (AdView)findViewById(R.id.adView);
		//intializeBannerAd();
		fetchMessages();
		intializeGoogleAd();
	}

	public void fetchMessages(){
		if(detectCnnection.isConnectingToInternet()){
			try{
				System.out.println("AccId:"+sharedPreferences.getString("accountID", "no accountid "));
				new GetInfo(sharedPreferences.getString("accountID", "no accountid "),sharedPreferences.getString("uid", ""),
						sharedPreferences.getString("type", "")).execute();
			}catch(Exception e){
				System.out.println("Ex inn on resume:"+e.toString());
			}
		}else {
			Toast.makeText(context, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
		}
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


	private void intializeBannerAd() {
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
	}

	private void showInterstitial() {
		if (mInterstitialAd.isLoaded()) {
			mInterstitialAd.show();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		TruckApp.getInstance().trackScreenView("Loads Screen");
		/*if (adView != null) {
			adView.resume();
		}*/

	}



	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	private class GetInfo extends AsyncTask<String, String, JSONObject>{

		String id,uid,type;
		public GetInfo(String accountId,String uid,String type) {
			this.id   = accountId;
			this.uid  = uid;
			this.type = type;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog.setMessage("Fetching loads data...");
			pDialog.show();
		}

		@Override
		protected JSONObject doInBackground(String... params) {

			JSONObject json = null;
			try {
				String urlParameters =
						"accountid=" + URLEncoder.encode(id, "UTF-8")+
								"&uid=" + URLEncoder.encode(uid, "UTF-8")+
								"&type=" + URLEncoder.encode(type, "UTF-8")+
								"&access_token=" + URLEncoder.encode(sharedPreferences.getString("access_token",""), "UTF-8");
				String res = parser.excutePost(TruckApp.getLoadsURL, urlParameters);
				/*System.out.println("info:"+res);*/
				json = new JSONObject(res);
			}catch(Exception e){
				Log.e("info DoIN EX",e.toString());
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
						Toast.makeText(context,"No records found",Toast.LENGTH_LONG).show();
					}else if (1 == result.getInt("status")){
						JSONArray infoArray = result.getJSONArray("data");
						if(infoArray.length()>0){
							infoLV.setAdapter(new LoadsAdapter(infoArray, LoadActivity.this));
						}else{
							Toast.makeText(context,"No records found",Toast.LENGTH_LONG).show();
						}
					}else if(2 == result.getInt("status")){
						//TruckApp.logoutAction(LoadActivity.this);
						SharedPreferences sharedPreferences = getSharedPreferences(
								getResources().getString(R.string.app_name), MODE_PRIVATE);
						SharedPreferences.Editor
								editor = sharedPreferences.edit();
						editor.putInt("login", 0).commit();
						editor.putString("accountID", "").commit();
						editor.putString(Constants.FUEL_USERNAME_KEY,"").commit();
						editor.putString(Constants.FUEL_PASSWORD_KEY,"").commit();
						editor.putString(Constants.FUEL_CUSTOMERID_KEY,"").commit();
						editor.putString(Constants.TOLL_USERNAME_KEY,"").commit();
						editor.putString(Constants.TOLL_PASSWORD_KEY,"").commit();
						editor.putString(Constants.TOLL_CUSTOMERID_KEY,"").commit();
						startActivity(new Intent(context,
								LoginActivity.class));
						finish();
					}
				}catch (Exception e){
					System.out.println("ex in get info"+e.toString());
				}
			}else{
				Toast.makeText(context,getResources().getString(R.string.exceptionmsg),Toast.LENGTH_LONG).show();
			}
		}
	}

	public void price_dialog(String str){
		try {
			final JSONObject json = new JSONObject(str);
			price_et.setText("");
			if(json.getString("message").trim().length()!=0){
				load_tv.setVisibility(View.VISIBLE);
				load_tv.setText(json.getString("message"));
			}else{
				load_tv.setVisibility(View.GONE);
			}
			pricedialog.show();
			close_btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					TruckApp.checkDialog(pricedialog);
				}
			});

			send_btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					String price_str = price_et.getText().toString().trim();
					try {
						new SetPrice(sharedPreferences.getString("accountID", "no accountid "), sharedPreferences.getString("uid", ""),
								json.getString("id_gps_alerts"), price_str).execute();
					} catch (JSONException e) {
						e.printStackTrace();
					}
					/*}else{
						TruckApp.editTextValidation(price_et, price_str, getResources().getString(R.string.price_error));
					}*/
				}
			});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.trips_menu , menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_trips_refresh:
				fetchMessages();
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	protected void onPause() {
		/*if (adView != null) {
			adView.pause();
		}*/
		super.onPause();
	}


	@Override
	protected void onDestroy() {
		sharedPreferences=null;
		detectCnnection=null;
		parser=null;
		pDialog=null;
		infoLV=null;
		/*if (adView != null)
		{ adView.destroy(); }*/
		super.onDestroy();
	}

	private class SetPrice extends AsyncTask<String,String,JSONObject>{
		String id,id_load,price,uid;
		HashMap<String, String> retrivedMap;
		List<String>    loadIdsList;


		@SuppressWarnings("unchecked")
		public SetPrice(String id,String uid,String id_load,String price){
			this.id       = id;
			this.uid      = uid;
			this.id_load  = id_load;
			this.price    = price;
			retrivedMap     = (HashMap<String,String>) loadsPreferences.getAll();
			loadIdsList     = new ArrayList<String>(retrivedMap.keySet());
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog.setMessage("Applying for the load...");
			pDialog.show();
		}

		@Override
		protected JSONObject doInBackground(String... strings) {
			JSONObject res = null;
			try {

				String urlParameters =
						"accountid=" + URLEncoder.encode(id, "UTF-8")+
								"&uid=" + URLEncoder.encode(uid, "UTF-8")+
								"&id_gps_alert=" + URLEncoder.encode(id_load, "UTF-8")+
								"&price=" + URLEncoder.encode(price, "UTF-8");
				String result = parser.excutePost(TruckApp.setLoadURL,urlParameters);
				System.out.println(""+urlParameters+"o/p for setload"+result);

				res = new JSONObject(result);

			}catch(Exception e){
				Log.e("Login DoIN EX",e.toString());
				res =null;
			}
			return res;
		}

		@Override
		protected void onPostExecute(JSONObject s) {
			super.onPostExecute(s);
			TruckApp.checkPDialog(pDialog);
			TruckApp.checkDialog(pricedialog);
			if (s!=null){
				try{
					if (0 == s.getInt("status"))
					{ Toast.makeText(context,"Failed to apply for load.Please try again",Toast.LENGTH_LONG).show(); }
					else if (1 == s.getInt("status")) {
						if(!loadIdsList.contains(id_load)){
							editor.putString(id_load, id_load);
							editor.commit();
							((LoadsAdapter)infoLV.getAdapter()).notifyDataSetChanged();
						}
						Toast.makeText(context,"Successfully applied for load.",Toast.LENGTH_LONG).show();
					}

				}catch(JSONException e){
					System.out.println("Exception while extracting the response:"+e.toString());
				}
			}else{
				Toast.makeText(context,getResources().getString(R.string.exceptionmsg),Toast.LENGTH_LONG).show();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public class LoadsAdapter extends BaseAdapter {

		JSONArray      loadsArray;
		Activity       activity;
		LayoutInflater inflater;

		HashMap<String, String> retrivedMap;
		List<String>    loadIdsList;

		public class ViewHolder{
			/*TextView trucktype_tv,date_tv,des_tv,src_tv,price_tv,pricetit,message_tv,
			messagetit,goods_tv,goods_tit,trucktype_tit,date_tit;*/
			TextView src_city,src_state,des_city,des_state,truck_type_tv,date_tv,price_tv,message_tv;
			ImageView   apply_btn;
		}


		public LoadsAdapter(JSONArray array,Activity activity) {
			this.loadsArray = array;
			this.activity   = activity;
			this.inflater   = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			retrivedMap     = (HashMap<String,String>) loadsPreferences.getAll();
			loadIdsList     = new ArrayList<String>(retrivedMap.keySet());
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return loadsArray.length();
		}

		@Override
		public Object getItem(int pos) {
			// TODO Auto-generated method stub

			try {
				return loadsArray.get(pos);
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
				convertView               = inflater.inflate(R.layout.load_item, parent, false);
				viewholder.src_city       = (TextView)convertView.findViewById(R.id.src_city_tv);
				viewholder.src_state      = (TextView)convertView.findViewById(R.id.src_state_tv);
				viewholder.des_city       = (TextView)convertView.findViewById(R.id.des_city_tv);
				viewholder.des_state      = (TextView)convertView.findViewById(R.id.des_state_tv);
				viewholder.truck_type_tv  = (TextView)convertView.findViewById(R.id.trucktype_tv);
				viewholder.date_tv        = (TextView)convertView.findViewById(R.id.date_tv);
				viewholder.price_tv       = (TextView)convertView.findViewById(R.id.price_tv);
				viewholder.message_tv     = (TextView)convertView.findViewById(R.id.message_tv);
				viewholder.apply_btn      = (ImageView) convertView.findViewById(R.id.btn_apply);
				convertView.setTag(viewholder);
			}else{
				viewholder = (ViewHolder)convertView.getTag();
			}
			setData(viewholder, position);
			return convertView;
		}

		private void setData(final ViewHolder viewholder, int position) {
			try {
				JSONObject obj = loadsArray.getJSONObject(position);
				viewholder.src_city.setText(obj.getString("source"));
				viewholder.src_state.setText(obj.getString("source_state"));
				viewholder.des_city .setText(obj.getString("destination"));
				viewholder.des_state .setText(obj.getString("destination_state"));
				retrivedMap     = (HashMap<String,String>) loadsPreferences.getAll();
				loadIdsList     = null;
				loadIdsList     = new ArrayList<String>(retrivedMap.keySet());

				System.out.println("load json:"+obj.toString());
				String loadId = obj.getString("id_gps_alerts");

				if(loadIdsList.contains(loadId)){
					viewholder.apply_btn.setImageResource(R.drawable.icon_phone);
					viewholder.apply_btn.setEnabled(false);
				}else{
					viewholder.apply_btn.setImageResource(R.drawable.icon_phone_booken);
					viewholder.apply_btn.setEnabled(true);
				}

				viewholder.truck_type_tv.setText(obj.getString("id_truck_type_title"));;
				viewholder.date_tv .setText(obj.getString("date_required"));

				if(obj.getString("price").equalsIgnoreCase("0.00")){
					/*viewholder.price_tv .setVisibility(View.GONE);
					viewholder.pricetit .setVisibility(View.GONE);*/
					viewholder.price_tv .setText("***");
				}else{
					/*viewholder.price_tv .setVisibility(View.VISIBLE);
					viewholder.pricetit .setVisibility(View.VISIBLE);*/
					viewholder.price_tv .setText(obj.getString("price"));
				}
				viewholder.message_tv .setText(obj.getString("message"));
				viewholder.message_tv.setVisibility(View.GONE);

				if(obj.getString("message").trim().length()==0){
					viewholder.message_tv.setVisibility(View.GONE);
				}else{
					viewholder.message_tv.setVisibility(View.VISIBLE);
				}

				viewholder.apply_btn.setTag(obj);
				viewholder.apply_btn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						try {
							final JSONObject json = new JSONObject(v.getTag().toString());
							new SetPrice(sharedPreferences.getString("accountID", "no accountid "), sharedPreferences.getString("uid", ""),
									json.getString("id_gps_alerts"), "").execute();
						} catch (JSONException e) {
							e.printStackTrace();
						}
						/*price_dialog(v.getTag().toString());	*/
					}
				});

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
