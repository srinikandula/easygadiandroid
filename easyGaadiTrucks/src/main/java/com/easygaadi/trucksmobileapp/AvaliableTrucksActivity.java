package com.easygaadi.trucksmobileapp;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.JSONParser;

public class AvaliableTrucksActivity extends AppCompatActivity {

	Context context;
	SharedPreferences sharedPreferences,avlTrkPreferences;
	Editor editor;
	private ConnectionDetector detectConnection;
	JSONParser parser;
	ProgressDialog pDialog;
	JSONArray trucksArray;
	ListView trucksLV;
	AvaTrucksAdapter adapter;
	boolean  loadMore_st;
	int      totalLoads,offset;
	Dialog   truckDialog;

	Button send_btn;
	ImageView close_btn;
	TextView truck_tv;
	EditText price_et;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_avaliabletrucks);

		context           = this;
		detectConnection  = new ConnectionDetector(context);
		parser            = JSONParser.getInstance();
		pDialog           = new ProgressDialog(this);
		pDialog.setCancelable(false);
		sharedPreferences = getApplicationContext().getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);
		avlTrkPreferences = getApplicationContext().getSharedPreferences(getResources().getString(R.string.avatrucks), MODE_PRIVATE);
		editor            = avlTrkPreferences.edit();

		trucksLV  = (ListView)findViewById(R.id.avatrucksLV);
		offset          = 0;

		truckDialog = new Dialog(AvaliableTrucksActivity.this, android.R.style.Theme_Dialog);
		truckDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		truckDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
		truckDialog.setContentView(R.layout.booktruckdialog);

		close_btn    = (ImageView) truckDialog.findViewById(R.id.close_iv);
		send_btn     = (Button) truckDialog.findViewById(R.id.update_btn);
		price_et     = (EditText) truckDialog.findViewById(R.id.price_et);
		truck_tv     = (TextView) truckDialog.findViewById(R.id.truck_tv);

		fetchAvaliableTrucks(0);

		trucksLV.setOnScrollListener(new OnScrollListener() {

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
							if (trucksArray.length() < totalLoads) {
								offset = offset+10;
								System.out.println("Value to be loaded:"+offset);
								fetchAvaliableTrucks(offset);
							}else{
								System.out.println("size is  less than 5");
							}
						}
					}
				}
			}
		});

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		TruckApp.getInstance().trackScreenView("AvaliableTrucks Screen");
	}

	private void fetchAvaliableTrucks(int offset) {

		if(detectConnection.isConnectingToInternet()){
			if(0==offset){
				trucksArray = new JSONArray();
				adapter     = new AvaTrucksAdapter(trucksArray, AvaliableTrucksActivity.this);
				trucksLV.setAdapter(adapter);
			}
			try{
				new GetAvaliableTrucks(sharedPreferences.getString("uid", ""),String.valueOf(offset)).execute();
			}catch(Exception e){
				System.out.println("Ex inn on resume:"+e.toString());
			}
		}else {
			Toast.makeText(context, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
		}

	}


	private class GetAvaliableTrucks extends AsyncTask<String, String, JSONObject>{

		String uid,offset;

		public GetAvaliableTrucks(String uid,String offset) {
			this.uid = uid;
			this.offset = offset;
			System.out.println("Offset:"+offset);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog.setMessage("Fetching avaliable trucks...");
			pDialog.show();  
		}

		@Override
		protected JSONObject doInBackground(String... params) {
			JSONObject json = null;
			try {	
				StringBuilder builder= new StringBuilder();
				builder.append("uid=").append(URLEncoder.encode(uid, "UTF-8"));
				builder.append("&offset=").append(URLEncoder.encode(offset, "UTF-8"));
				String res = parser.excutePost(TruckApp.getAvaliableTrucksURL, builder.toString());
				/*System.out.println("get AvaliableTruck o/p"+res);*/
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
						Toast.makeText(context,"No records found",Toast.LENGTH_LONG).show();
					}else if (1 == result.getInt("status")){
						totalLoads = result.getInt("count");
						if(totalLoads == 0)
						{	Toast.makeText(context,"No records found",Toast.LENGTH_LONG).show(); }
						else{
							JSONArray truckArray1 = result.getJSONArray("data");
							for (int i = 0; i < truckArray1.length(); i++) {
								trucksArray.put(truckArray1.getJSONObject(i));
							}
							truckArray1 = null;
							if(totalLoads > trucksArray.length()){
								loadMore_st=false;
							}else{
								loadMore_st=true;
							}
							adapter.notifyDataSetChanged();
						}
					}
				}catch (Exception e){
					System.out.println("ex in get leads"+e.toString());
				}
			}else{
				Toast.makeText(context,getResources().getString(R.string.exceptionmsg),Toast.LENGTH_LONG).show();
			}
		}
	}


	public class AvaTrucksAdapter extends BaseAdapter{
		Activity       activity;
		LayoutInflater inflater ;
		JSONArray      trucks;

		HashMap<String, String> retrivedMap;
		List<String>    avaTruIdsList;

		public AvaTrucksAdapter(JSONArray trucks,Activity activity) {
			this.trucks      = trucks;
			this.activity    = activity;
			this.inflater    = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			retrivedMap      = (HashMap<String,String>) avlTrkPreferences.getAll();
			avaTruIdsList    = new ArrayList<String>(retrivedMap.keySet());

		}
		public class ViewHolder{
			TextView trucktype_tit,nooftruck_tit,
			truckreg_tit,date_tit,comment_tit,price_tit,gpstracking_tit,commenttit;
			TextView truckid_tv,src_tv,des_tv,trucktype_tv,nooftruck_tv,
			truckreg_tv,date_tv,comment_tv,price_tv,gpstracking_tv;
			Button book_btn;
		}


		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return trucks.length();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			try {
				return trucks.get(arg0);
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
				convertView             = inflater.inflate(R.layout.avatruckitem, parent, false);
				viewholder.truckid_tv   = (TextView)convertView.findViewById(R.id.truckid_tv);
				viewholder.src_tv       = (TextView)convertView.findViewById(R.id.src_tv);
				viewholder.des_tv       = (TextView)convertView.findViewById(R.id.des_tv);
				viewholder.trucktype_tv = (TextView)convertView.findViewById(R.id.trucktype_tv);
				viewholder.truckreg_tv  = (TextView)convertView.findViewById(R.id.truckreg_tv);
				viewholder.date_tv      = (TextView)convertView.findViewById(R.id.date_tv);
				viewholder.comment_tv   = (TextView)convertView.findViewById(R.id.comment_tv);
				viewholder.price_tv         = (TextView)convertView.findViewById(R.id.price_tv);
				viewholder.gpstracking_tv   = (TextView)convertView.findViewById(R.id.gpstracking_tv);
				viewholder.nooftruck_tv     = (TextView)convertView.findViewById(R.id.nooftruck_tv);

				viewholder.trucktype_tit    = (TextView)convertView.findViewById(R.id.trucktyptit);
				viewholder.truckreg_tit     = (TextView)convertView.findViewById(R.id.truckregtit);
				viewholder.date_tit         = (TextView)convertView.findViewById(R.id.datetit);
				viewholder.commenttit      = (TextView)convertView.findViewById(R.id.comment_tit);
				viewholder.price_tit        = (TextView)convertView.findViewById(R.id.pricetit);
				viewholder.gpstracking_tit  = (TextView)convertView.findViewById(R.id.gpstracking_tit);
				viewholder.nooftruck_tit    = (TextView)convertView.findViewById(R.id.nooftrucktit);


				viewholder.book_btn         = (Button)convertView.findViewById(R.id.book_btn);
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
				JSONObject obj = trucks.getJSONObject(position);

				retrivedMap      = (HashMap<String,String>) avlTrkPreferences.getAll();
				avaTruIdsList    = null;
				avaTruIdsList    = new ArrayList<String>(retrivedMap.keySet());
				String truckId   = obj.getString("tid");

				if(avaTruIdsList.contains(truckId)){
					viewholder.book_btn.setBackgroundResource(R.drawable.ic_booked);
				}else{
					viewholder.book_btn.setBackgroundResource(R.drawable.ic_book);
				}
				viewholder.truckid_tv.setText("Truck Id:#"+obj.getString("tid"));
				viewholder.src_tv.setText(obj.getString("source"));
				viewholder.des_tv .setText(obj.getString("destination"));
				if(obj.getString("truck_type").trim().length()==0){
					viewholder.trucktype_tit .setVisibility(View.GONE);
					viewholder.trucktype_tv .setVisibility(View.GONE);
				}else{
					viewholder.trucktype_tit .setVisibility(View.VISIBLE);
					viewholder.trucktype_tv .setVisibility(View.VISIBLE);
					viewholder.trucktype_tv.setText(obj.getString("truck_type"));
				}

				if(obj.getString("tuck_reg_no").trim().length()==0){
					viewholder.truckreg_tit .setVisibility(View.GONE);
					viewholder.truckreg_tv .setVisibility(View.GONE);
				}else{
					viewholder.truckreg_tit .setVisibility(View.VISIBLE);
					viewholder.truckreg_tv .setVisibility(View.VISIBLE);
					viewholder.truckreg_tv .setText(obj.getString("tuck_reg_no"));
				}

				if(obj.getString("date_available").trim().length()==0){
					viewholder.date_tit .setVisibility(View.GONE);
					viewholder.date_tv .setVisibility(View.GONE);
				}else{
					viewholder.date_tit .setVisibility(View.VISIBLE);
					viewholder.date_tv .setVisibility(View.VISIBLE);
					viewholder.date_tv .setText(obj.getString("date_available"));
				}

				if(obj.getString("comment").trim().length()==0){
					viewholder.commenttit .setVisibility(View.GONE);
					viewholder.comment_tv .setVisibility(View.GONE);
				}else{
					viewholder.commenttit .setVisibility(View.VISIBLE);
					viewholder.comment_tv .setVisibility(View.VISIBLE);
					viewholder.comment_tv.setText(obj.getString("comment"));
				}

				if(obj.getString("no_of_trucks").trim().length()==0){
					viewholder.nooftruck_tit .setVisibility(View.GONE);
					viewholder.nooftruck_tv .setVisibility(View.GONE);
				}else{
					viewholder.nooftruck_tit .setVisibility(View.VISIBLE);
					viewholder.nooftruck_tv .setVisibility(View.VISIBLE);
					viewholder.nooftruck_tv.setText(obj.getString("no_of_trucks"));
				}

				if(obj.getString("price").trim().length()==0){
					viewholder.price_tit .setVisibility(View.GONE);
					viewholder.price_tv .setVisibility(View.GONE);
				}else{
					viewholder.price_tit .setVisibility(View.VISIBLE);
					viewholder.price_tv .setVisibility(View.VISIBLE);
					viewholder.price_tv.setText(obj.getString("price"));
				}

				if(obj.getString("tracking").trim().length()==0){
					viewholder.gpstracking_tit .setVisibility(View.GONE);
					viewholder.gpstracking_tv .setVisibility(View.GONE);
				}else{
					viewholder.gpstracking_tit .setVisibility(View.VISIBLE);
					viewholder.gpstracking_tv .setVisibility(View.VISIBLE);
					String val = (obj.getString("tracking") == "1")?"Avaliable":"Not Avaliable";
					viewholder.gpstracking_tv.setText(val);
				}

				viewholder.book_btn.setTag(obj.getString("tid")+"-"+position);
				if(0 == obj.getInt("select")){		
					viewholder.book_btn.setEnabled(true);

				/*	viewholder.book_btn.setText("Book");
					viewholder.book_btn.setBackgroundColor(Color.parseColor("#56c9f5"));*/				
				}else{
					/*viewholder.book_btn.setBackgroundColor(Color.parseColor("#837E7C"));
					viewholder.book_btn.setText("Booked");*/
					viewholder.book_btn.setEnabled(false);
				}
				viewholder.book_btn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						String[] array =(v.getTag().toString()).split("-");
						//bookTruck(array[0],Integer.parseInt(array[1])); 
						try {
							new BookTruck(sharedPreferences.getString("uid", ""),array[0],Integer.parseInt(array[1]),"").execute();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void bookTruck(final String tid,final int position){
		price_et.setText("");
		truck_tv.setText("TruckId:#"+tid);
		close_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				TruckApp.checkDialog(truckDialog);
			}
		});

		send_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String price_str = price_et.getText().toString().trim();					
				try {
					new BookTruck(sharedPreferences.getString("uid", ""),tid,position,price_str).execute();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		truckDialog.show();
	}

	private class BookTruck extends AsyncTask<String, String, JSONObject>{

		String uid,tid,price;
		int    position;
		HashMap<String, String> retrivedMap;
		List<String>    avaTruIdsList;

		@SuppressWarnings("unchecked")
		public BookTruck(String uid,String tid,int position,String price) {
			this.uid      = uid;
			this.tid      = tid;
			this.position = position;
			this.price    = price;
			retrivedMap   = (HashMap<String,String>) avlTrkPreferences.getAll();
			avaTruIdsList = new ArrayList<String>(retrivedMap.keySet());			
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog.setMessage("Booking a truck...");
			pDialog.show();  
		}

		@Override
		protected JSONObject doInBackground(String... params) {

			JSONObject json = null;
			try {	
				StringBuilder builder= new StringBuilder();
				builder.append("uid=").append(URLEncoder.encode(uid, "UTF-8")).append("&price=").
				append(URLEncoder.encode(price, "UTF-8")).append("&tid=").append(URLEncoder.encode(tid, "UTF-8"));				
				String res = parser.excutePost(TruckApp.bookTruckURL,builder.toString());
				System.out.println("Params:"+builder.toString()+"book truck o/p"+res);
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
						Toast.makeText(context,"Failed to book truck",Toast.LENGTH_LONG).show();
					}else if (1 == result.getInt("status")){
						TruckApp.checkDialog(truckDialog);
						Toast.makeText(context,"Thanks for booking.Will contact you soon.",Toast.LENGTH_LONG).show();
						JSONObject obj = trucksArray.getJSONObject(position);
						obj.put("select", 1);
						trucksArray.put(position, obj);
						if(!avaTruIdsList.contains(tid)){
							editor.putString(tid, tid);
							editor.commit();				
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


	@Override
	protected void onDestroy() {
		sharedPreferences = null;
		detectConnection= null;
		parser= null;
		pDialog= null;
		trucksArray= null;
		trucksLV= null;
		adapter= null;
		super.onDestroy();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.locate_truck_menu , menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_refresh:
			fetchAvaliableTrucks(0);
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
