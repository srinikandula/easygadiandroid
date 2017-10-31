package com.easygaadi.trucksmobileapp;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.JSONParser;
import com.squareup.picasso.Picasso;

public class LoyaltyPointsActivity extends AppCompatActivity {


	Context context;
	SharedPreferences sharedPreferences;
	Editor      editor;
	private ConnectionDetector detectConnection;
	JSONParser parser;
	ProgressDialog pDialog;
	TextView points_tv,terms_tv;
	JSONObject jsonResult;
	ListView   terms_lv;
	GridView   gifts_gv;
	ImageView  closeInfo_iv;
	RelativeLayout infoLayout;

	Dialog redeemDialog;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loyaltypoints);

		context           = this;
		detectConnection  = new ConnectionDetector(context);
		parser            = JSONParser.getInstance();
		pDialog           = new ProgressDialog(this);
		points_tv         = (TextView)findViewById(R.id.title_tv);
		terms_tv         = (TextView)findViewById(R.id.terms_tv);
		terms_lv          = (ListView)findViewById(R.id.terms_lv);
		gifts_gv          = (GridView)findViewById(R.id.gifts_gv);
		closeInfo_iv      = (ImageView)findViewById(R.id.closeinfo_iv);
		infoLayout         = (RelativeLayout)findViewById(R.id.contentLayout);

		sharedPreferences = getApplicationContext().getSharedPreferences(
				getResources().getString(R.string.app_name), MODE_PRIVATE);
		editor           = sharedPreferences.edit();
		pDialog.setCancelable(false);
		fetchLoyaltyPoints();

		closeInfo_iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				infoLayout.setVisibility(View.GONE);
			}
		});
	}


	public void fetchLoyaltyPoints() {
		if (detectConnection.isConnectingToInternet()) {
			try {
				new GetLoyaltyPoints(sharedPreferences.getString("uid",
						"")).execute();
			} catch (Exception e) {
				System.out.println("Ex inn on resume:" + e.toString());
			}
		} else {
			Toast.makeText(context,
					getResources().getString(R.string.internet_str),
					Toast.LENGTH_LONG).show();
		}
	}


	class GetLoyaltyPoints extends AsyncTask<String, String, JSONObject>{

		String uid;

		public GetLoyaltyPoints(String uid) {
			super();
			this.uid = uid;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog.setMessage("Fetching loyalty points data...");
			pDialog.show();
		}

		@Override
		protected JSONObject doInBackground(String... params) {
			JSONObject json = null;
			try {
				StringBuilder builder = new StringBuilder();
				builder.append("uid=").append(
						URLEncoder.encode(uid, "UTF-8"));
				String res = parser.executeGet(TruckApp.loyaltypointsURL+"?uid="+uid);
				json = new JSONObject(res);
			} catch (Exception e) {
				Log.e("Login DoIN EX", e.toString());
			}
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != null) {
				try {
					if (0 == result.getInt("status")) {
						Toast.makeText(context, "Failed to get loyalty points",
								Toast.LENGTH_LONG).show();
					} else if (1 == result.getInt("status")) {
						jsonResult = result.getJSONObject("data");

						editor.putInt("rewards",jsonResult.getInt("your_points"));
						editor.commit();


						points_tv.setText(String.valueOf(jsonResult.getInt("your_points")));
						terms_tv.setText(String.valueOf(jsonResult.getString("terms")));

						terms_lv.setAdapter(new ArrayAdapter<String>(context,R.layout.terms_conditions_item,R.id.term_tv,
								convertArray2List(jsonResult.getJSONArray("content"))));
						gifts_gv.setAdapter(new GiftsAdapter(jsonResult.getJSONArray("gifts"), LoyaltyPointsActivity.this));	
					}
				} catch (Exception e) {
					System.out.println("ex in get leads" + e.toString());
				}
				TruckApp.checkPDialog(pDialog);
			} else {
				TruckApp.checkPDialog(pDialog);
				Toast.makeText(context,
						getResources().getString(R.string.exceptionmsg),
						Toast.LENGTH_LONG).show();
			}

		}

	}

	public ArrayList<String> convertArray2List(JSONArray array){
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < array.length(); i++) {
			try {
				list.add(array.getString(i));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		return list;
	}

	public class GiftsAdapter extends BaseAdapter{

		JSONArray giftsArray;
		LayoutInflater inflater;
		Activity activity;

		public GiftsAdapter(JSONArray giftsArray, Activity activity) {
			super();
			this.giftsArray = giftsArray;
			this.activity = activity;
			inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		class ViewHolder {
			public TextView rewards_tv;
			public ImageView gift_iv;
			public Button    redeem_btn;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return giftsArray.length();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			try {
				return giftsArray.get(arg0);
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

			ViewHolder viewHolder = null;
			if(convertView == null){
				convertView = inflater.inflate(R.layout.reward_grid_item, parent, false);
				viewHolder  = new ViewHolder();
				viewHolder.rewards_tv = (TextView)convertView.findViewById(R.id.rewards_tv);
				viewHolder.gift_iv    = (ImageView)convertView.findViewById(R.id.gift_img);
				viewHolder.redeem_btn = (Button)convertView.findViewById(R.id.redeem_btn);
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder)convertView.getTag();
			}
			setdata(position,viewHolder);
			return convertView;
		}

		private void setdata(int position, ViewHolder viewHolder) {
			try {
				JSONObject jObj = giftsArray.getJSONObject(position);
				viewHolder.rewards_tv.setText(Html.fromHtml("<s>"+String.valueOf(jObj.getInt("points"))+"</s> pts"));
				viewHolder.redeem_btn.setTag(jObj.toString());
				if(jObj.getInt("points") <= sharedPreferences.getInt("rewards", 0)){
					viewHolder.redeem_btn.setBackgroundResource(R.drawable.alertbtnbg);
					viewHolder.redeem_btn.setEnabled(true);
				}else{
					viewHolder.redeem_btn.setBackgroundResource(R.drawable.ic_redeem_disabled);
					viewHolder.redeem_btn.setEnabled(false);
				}
				if (jObj.getString("image").equals("")) {
					Picasso.with(context).load(R.drawable.noimageicon).into(viewHolder.gift_iv);
				} else {
					Picasso.with(context).load(jObj.getString("image")).into(viewHolder.gift_iv);
				}

				viewHolder.redeem_btn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {
						redeemGift(view.getTag().toString());
					}
				});

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}

	} 



	public void redeemGift(String tid){
		if(detectConnection.isConnectingToInternet()){

			redeemDialog = null;
			try{
				redeemDialog = new Dialog(LoyaltyPointsActivity.this, android.R.style.Theme_Dialog);
				redeemDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				redeemDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
				redeemDialog.setContentView(R.layout.deletetruckdialog);
				((TextView) redeemDialog.findViewById(R.id.deletetv)).
				setText("Are you sure do you want to redeem this gift?");
				((TextView) redeemDialog.findViewById(R.id.forgotpwd_title)).
				setText("Redeem Gift");
				Button send_btn      = (Button) redeemDialog.findViewById(R.id.send_btn);
				Button cancel_btn    = (Button) redeemDialog.findViewById(R.id.cancel_btn);
				send_btn.setTag(tid);
				send_btn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {
						new RedeemPoints(view.getTag().toString()).execute();			
					}
				});
				cancel_btn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						redeemDialog.dismiss();
					}
				});
				redeemDialog.show();

			}catch(Exception e){
				System.out.println("Ex inn on resume:"+e.toString());
			}
		}else {
			Toast.makeText(context, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
		}
	}


	public class RedeemPoints extends AsyncTask<String, String, JSONObject>{

		JSONObject jsonObj;  

		public RedeemPoints(String jsonStr) {
			super();
			try {
				this.jsonObj = new JSONObject(jsonStr);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog.setMessage("Redeeming your loyalty points ...");
			pDialog.show();
		}

		@Override
		protected JSONObject doInBackground(String... params) {
			JSONObject json = null;
			try {
				String url =TruckApp.sendGiftRequestURL+"?uid="+sharedPreferences.getString("uid",
						"")+"&lid="+jsonObj.getInt("lid")+"&points="+jsonObj.getInt("points");
				System.out.println("RURL:"+url);
				String res = parser.executeGet(url);
				System.out.println("RURL:"+url+" RRES:"+res);

				json = new JSONObject(res);
			} catch (Exception e) {
				Log.e("Login DoIN EX", e.toString());
			}
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject result) {

			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != null) {
				TruckApp.checkDialog(redeemDialog);
				try {
					if (0 == result.getInt("status")) {
						Toast.makeText(context, "Failed to apply for gift",
								Toast.LENGTH_LONG).show();
					} else if (1 == result.getInt("status")) {
						if(jsonResult!=null){
							Toast.makeText(context, "You have successfully Redeemed Gift.Will contact you soon.",
									Toast.LENGTH_LONG).show();
							int points = sharedPreferences.getInt("rewards", 0) - jsonObj.getInt("points");

							if(points>=0){
								editor.putInt("rewards",points);
								editor.commit();
								gifts_gv.setAdapter(new GiftsAdapter(jsonResult.getJSONArray("gifts"), LoyaltyPointsActivity.this));	
								points_tv.setText(String.valueOf(sharedPreferences.getInt("rewards", 0)));
							}

						}
					}
				} catch (Exception e) {
					System.out.println("ex in get leads" + e.toString());
				}
				TruckApp.checkPDialog(pDialog);
			} else {
				TruckApp.checkPDialog(pDialog);
				Toast.makeText(context,
						getResources().getString(R.string.exceptionmsg),
						Toast.LENGTH_LONG).show();
			}


		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.loyalty_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.action_info:
			if(jsonResult!=null){
				infoLayout.setVisibility(View.VISIBLE);
			}
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}



	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		TruckApp.getInstance().trackScreenView("LoyaltyPoints Screen");
	}

	@Override
	protected void onDestroy() {

		sharedPreferences = null;
		detectConnection = null;
		parser = null;
		pDialog = null;
		jsonResult = null;
		super.onDestroy();
	}

}
