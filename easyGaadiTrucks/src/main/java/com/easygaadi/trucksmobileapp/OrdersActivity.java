package com.easygaadi.trucksmobileapp;

import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.JSONParser;

public class OrdersActivity extends AppCompatActivity {


	Context context;
	SharedPreferences sharedPreferences;
	private ConnectionDetector detectConnection;
	JSONParser parser;
	ProgressDialog pDialog;
	ListView ordersLV;
	JSONArray ordersArray;
	OrdersAdapter adapter;

	boolean  loadMore_st;
	int      totalOrders,offset;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_orders);
		
		
		context           = this;
		detectConnection  = new ConnectionDetector(context);
		parser            = JSONParser.getInstance();
		pDialog           = new ProgressDialog(this);
		pDialog.setCancelable(false);
		sharedPreferences = getApplicationContext().getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);
		ordersLV  = (ListView)findViewById(R.id.orders_lv);
		fetchOrders(0);
		
		ordersLV.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, 
					int scrollState) {}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, 
					int visibleItemCount, int totalItemCount) {
				if (totalOrders > 10) {	
					System.out.println("loadMore_st"+loadMore_st);
					if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
					{
						if(loadMore_st == false)
						{
							loadMore_st = true;
							if (ordersArray.length() < totalOrders) {
								offset = offset+10;
								System.out.println("Value to be loaded:"+offset);
								fetchOrders(offset);
							}else{
								System.out.println("size is  less than 10");
							}
						}
					}
				}
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		TruckApp.getInstance().trackScreenView("Orders Screen");
	}

	public void fetchOrders(int offset){
		if(detectConnection.isConnectingToInternet()){
			if(0==offset){
				ordersArray = new JSONArray();
				adapter           = new OrdersAdapter(ordersArray, OrdersActivity.this);
				ordersLV.setAdapter(adapter);
			}
			try{
				new GetOrders(sharedPreferences.getString("uid", ""),sharedPreferences.getString("type", ""),offset).execute();
			}catch(Exception e){
				System.out.println("Ex inn on resume:"+e.toString());
			}
		}else {
			Toast.makeText(context, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
		}
	}

	private class GetOrders extends AsyncTask<String, String, JSONObject>{

		String uid,type;
		String offset;
		public GetOrders(String uid,String type,int offset) {
			this.uid    = uid;
			this.type   = type;
			this.offset = String.valueOf(offset);
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog.setMessage("Fetching orders...");
			pDialog.show();  
		}

		@Override
		protected JSONObject doInBackground(String... params) {

			JSONObject json = null;
			try {	
				StringBuilder builder= new StringBuilder();

				builder.append("uid=").append(URLEncoder.encode(uid, "UTF-8"));
				builder.append("&offset=").append(URLEncoder.encode(offset, "UTF-8"));
				builder.append("&type=").append(URLEncoder.encode(type, "UTF-8"));				
				
				String res = parser.excutePost(TruckApp.getOrdersURL,builder.toString());
				//System.out.println("Params:"+builder.toString()+"get orders o/p"+res);
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

						totalOrders = result.getInt("count");
						if(totalOrders == 0)
						{	Toast.makeText(context,"No records found",Toast.LENGTH_LONG).show(); }
						else{

							JSONArray truckArray1 = result.getJSONArray("data");
							for (int i = 0; i < truckArray1.length(); i++) {
								ordersArray.put(truckArray1.getJSONObject(i));
							}
							truckArray1 = null;
							if(totalOrders > ordersArray.length()){
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


	public class OrdersAdapter extends BaseAdapter{
		Activity       activity;
		LayoutInflater inflater ;
		JSONArray      loads;

		public OrdersAdapter(JSONArray loads,Activity activity) {
			this.loads       = loads;
			this.activity    = activity;
			this.inflater    = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		public class ViewHolder{
			TextView orderid_tv,src_tv,des_tv,goodstype_tv,trucktype_tv,
			truckreg_tv,date_tv,status_tv,currentloc_tit,currentloc_tv,pendingamt_tit,
			pendingamt_tv,orderamt_tit,orderamt_tv,goods_tit,trucktype_tit,
			truckreg_tit,date_tit;
			Button btn_tracking;
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
				convertView             = inflater.inflate(R.layout.orderitem, parent, false);
				viewholder.orderid_tv   = (TextView)convertView.findViewById(R.id.orderid_tv);
				viewholder.src_tv       = (TextView)convertView.findViewById(R.id.src_tv);
				viewholder.des_tv       = (TextView)convertView.findViewById(R.id.des_tv);
				viewholder.goodstype_tv = (TextView)convertView.findViewById(R.id.goodstype_tv);
				viewholder.trucktype_tv = (TextView)convertView.findViewById(R.id.trucktype_tv);
				viewholder.truckreg_tv  = (TextView)convertView.findViewById(R.id.truckreg_tv);
				viewholder.date_tv      = (TextView)convertView.findViewById(R.id.date_tv);
				viewholder.status_tv    = (TextView)convertView.findViewById(R.id.status_tv);
				viewholder.currentloc_tit  = (TextView)convertView.findViewById(R.id.curloctit);
				viewholder.currentloc_tv   = (TextView)convertView.findViewById(R.id.curloc_tv);
				viewholder.pendingamt_tit  = (TextView)convertView.findViewById(R.id.pendingamounttit);
				viewholder.pendingamt_tv   = (TextView)convertView.findViewById(R.id.pendingamount_tv);
				viewholder.orderamt_tit    = (TextView)convertView.findViewById(R.id.orderamttit);
				viewholder.orderamt_tv     = (TextView)convertView.findViewById(R.id.orderamt_tv);
				viewholder.goods_tit      = (TextView)convertView.findViewById(R.id.goodstit);
				viewholder.trucktype_tit  = (TextView)convertView.findViewById(R.id.trucktyptit);
				viewholder.date_tit       = (TextView)convertView.findViewById(R.id.datetit);
				viewholder.truckreg_tit     = (TextView)convertView.findViewById(R.id.truckregtit);

				viewholder.btn_tracking    = (Button)convertView.findViewById(R.id.btn_tracking);
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
				viewholder.orderid_tv.setText("Order Id:#"+obj.getString("id_order"));
				viewholder.src_tv.setText(obj.getString("source_city"));
				viewholder.des_tv .setText(obj.getString("destination_city"));

				if(obj.getString("goods_type").trim().length()==0){
					viewholder.goods_tit .setVisibility(View.GONE);
					viewholder.goodstype_tv .setVisibility(View.GONE);
				}else{
					viewholder.goods_tit .setVisibility(View.VISIBLE);
					viewholder.goodstype_tv .setVisibility(View.VISIBLE);
					viewholder.goodstype_tv.setText(obj.getString("goods_type"));
				}

				if(obj.getString("truck_type").trim().length()==0){
					viewholder.trucktype_tit .setVisibility(View.GONE);
					viewholder.trucktype_tv .setVisibility(View.GONE);
				}else{
					viewholder.trucktype_tit .setVisibility(View.VISIBLE);
					viewholder.trucktype_tv .setVisibility(View.VISIBLE);
					viewholder.trucktype_tv.setText(obj.getString("truck_type"));
				}

				if(obj.getString("truck_reg_no").trim().length()==0){
					viewholder.truckreg_tit .setVisibility(View.GONE);
					viewholder.truckreg_tv .setVisibility(View.GONE);
				}else{
					viewholder.truckreg_tit .setVisibility(View.VISIBLE);
					viewholder.truckreg_tv .setVisibility(View.VISIBLE);
					viewholder.truckreg_tv .setText(obj.getString("truck_reg_no"));
				}

				if(obj.getString("date_ordered").trim().length()==0){
					viewholder.date_tit .setVisibility(View.GONE);
					viewholder.date_tv .setVisibility(View.GONE);
				}else{
					viewholder.date_tit .setVisibility(View.VISIBLE);
					viewholder.date_tv .setVisibility(View.VISIBLE);
					viewholder.date_tv .setText(obj.getString("date_ordered"));
				}


				viewholder.status_tv .setText(obj.getString("order_status_name"));
				viewholder.btn_tracking.setTag(obj.getString("id_order")+"-"+obj.getString("truck_reg_no"));

				if(obj.getString("current_location").trim().length()==0){
					viewholder.currentloc_tit .setVisibility(View.GONE);
					viewholder.currentloc_tv .setVisibility(View.GONE);
				}else{
					viewholder.currentloc_tit .setVisibility(View.VISIBLE);
					viewholder.currentloc_tv .setVisibility(View.VISIBLE);
					viewholder.currentloc_tv.setText(obj.getString("current_location"));
				}
				if(obj.getString("pending_amount").trim().length()==0){
					viewholder.pendingamt_tit .setVisibility(View.GONE);
					viewholder.pendingamt_tv .setVisibility(View.GONE);
				}else{
					viewholder.pendingamt_tit .setVisibility(View.VISIBLE);
					viewholder.pendingamt_tv .setVisibility(View.VISIBLE);
					viewholder.pendingamt_tv.setText(obj.getString("pending_amount"));
				}
				if(obj.getString("order_amount").trim().length()==0){
					viewholder.orderamt_tit .setVisibility(View.GONE);
					viewholder.orderamt_tv .setVisibility(View.GONE);
				}else{
					viewholder.orderamt_tit .setVisibility(View.VISIBLE);
					viewholder.orderamt_tv .setVisibility(View.VISIBLE);
					viewholder.orderamt_tv.setText(obj.getString("order_amount"));
				}

				if(1 == obj.getInt("tracking"))
				{ viewholder.btn_tracking.setVisibility(View.VISIBLE); }
				else
				{ viewholder.btn_tracking.setVisibility(View.GONE); }


				viewholder.btn_tracking.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						String[] values=arg0.getTag().toString().trim().split("-");
						Intent next_in= new Intent(context, TrackOrderActivity.class);
						next_in.putExtra("oid", values[0]);
						next_in.putExtra("regno", values[1]);
						context.startActivity(next_in);
					}
				});


			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

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
			fetchOrders(0);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}





}
