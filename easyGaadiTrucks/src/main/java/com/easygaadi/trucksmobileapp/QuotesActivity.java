package com.easygaadi.trucksmobileapp;

import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.JSONParser;

public class QuotesActivity extends AppCompatActivity {

	Context context;
	SharedPreferences sharedPreferences;
	private ConnectionDetector detectConnection;
	JSONParser parser;
	ProgressDialog pDialog;
	ListView quotesLV;
	JSONArray quotesArray;
	QuotesAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quotes);
		
		QuotesActivity.this.setTitle((new StringBuilder().append(getIntent().getStringExtra("loadid")).append("/Quotes")).toString());
		
		context           = this;
		detectConnection  = new ConnectionDetector(context);
		parser            = JSONParser.getInstance();
		pDialog           = new ProgressDialog(this);
		pDialog.setCancelable(false);
		sharedPreferences = getApplicationContext().getSharedPreferences(getResources().getString(R.string.app_name),MODE_PRIVATE);
		quotesLV  = (ListView)findViewById(R.id.quotesLV);
		fetchQuotes();
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		TruckApp.getInstance().trackScreenView("Quotes Screen");
	}



	public void fetchQuotes(){
		if(detectConnection.isConnectingToInternet()){
			quotesArray = new JSONArray();
			adapter     = new QuotesAdapter(quotesArray, QuotesActivity.this);
			quotesLV.setAdapter(adapter);
			try{
				new GetQuotes(getIntent().getStringExtra("loadid")).execute();
			}catch(Exception e){
				System.out.println("Ex inn on resume:"+e.toString());
			}
		}else {
			Toast.makeText(context, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
		}
	}



	private class GetQuotes extends AsyncTask<String, String, JSONObject>{

		String qid;
		public GetQuotes(String qid) {
			this.qid    = qid;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog.setMessage("Fetching quotes...");
			pDialog.show();  
		}

		@Override
		protected JSONObject doInBackground(String... params) {

			JSONObject json = null;
			try {	
				StringBuilder builder= new StringBuilder();
				builder.append("lid=").append(URLEncoder.encode(qid, "UTF-8"));				
				String res = parser.excutePost(TruckApp.getQuotesURL,builder.toString());
				//System.out.println("Params:"+builder.toString()+"get quotes o/p"+res);
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

						JSONArray truckArray1 = result.getJSONArray("data");
						for (int i = 0; i < truckArray1.length(); i++) {
							quotesArray.put(truckArray1.getJSONObject(i));
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


	public class QuotesAdapter extends BaseAdapter{
		Activity       activity;
		LayoutInflater inflater ;
		JSONArray      loads;

		public QuotesAdapter(JSONArray loads,Activity activity) {
			this.loads       = loads;
			this.activity    = activity;
			this.inflater    = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		public class ViewHolder{
			TextView reqid_tv,quote_tv,comment_tv;
			Button   confirm_btn;
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
				convertView             = inflater.inflate(R.layout.quoteitem, parent, false);
				viewholder.reqid_tv     = (TextView)convertView.findViewById(R.id.id_tv);
				viewholder.quote_tv     = (TextView)convertView.findViewById(R.id.quote_tv);
				viewholder.comment_tv   = (TextView)convertView.findViewById(R.id.comment_tv);
				viewholder.confirm_btn  = (Button)convertView.findViewById(R.id.confirm_btn);
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
		
				viewholder.reqid_tv.setText(obj.getString("qid"));
				viewholder.quote_tv.setText(obj.getString("quote"));		
				viewholder.comment_tv .setText(obj.getString("message"));
				viewholder.confirm_btn.setTextColor(Color.WHITE);
				
				if(obj.getString("booking_request").equalsIgnoreCase("1")){
					
					viewholder.confirm_btn.setText("Processing");
					viewholder.confirm_btn.setBackgroundColor(Color.WHITE);
					viewholder.confirm_btn.setEnabled(false);
					viewholder.confirm_btn.setTextColor(Color.parseColor("#56c9f5"));
		
				}else{
					viewholder.confirm_btn.setText("Book");
					viewholder.confirm_btn.setBackgroundColor(Color.parseColor("#ffbf00"));
					viewholder.confirm_btn.setEnabled(true);
				}
				
				viewholder.confirm_btn .setTag(obj.getString("qid"));
				
				viewholder.confirm_btn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						System.out.println("quoteid:"+v.getTag().toString());
						bookQuote(v.getTag().toString());
					}
				});
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void bookQuote(String qid){
		if(detectConnection.isConnectingToInternet()){
			try{
				new BookQuote(qid).execute();
			}catch(Exception e){
				System.out.println("Ex inn on resume:"+e.toString());
			}
		}else {
			Toast.makeText(context, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
		}
	}

	private class BookQuote extends AsyncTask<String, String, JSONObject>{

		String qid;
		public BookQuote(String qid) {
			this.qid    = qid;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog.setMessage("Booking a quote...");
			pDialog.show();  
		}

		@Override
		protected JSONObject doInBackground(String... params) {

			JSONObject json = null;
			try {	
				StringBuilder builder= new StringBuilder();

				builder.append("qid=").append(URLEncoder.encode(qid, "UTF-8"));				
				String res = parser.excutePost(TruckApp.bookQuoteURL,builder.toString());
				System.out.println("Params:"+builder.toString()+"book quote o/p"+res);
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
						Toast.makeText(context,"Failed to book quote",Toast.LENGTH_LONG).show();
					}else if (1 == result.getInt("status")){
						Toast.makeText(context,"Thanks for booking.Will get back to you soon!!",Toast.LENGTH_LONG).show();
						fetchQuotes();
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
		quotesArray= null;
		quotesLV= null;
		adapter= null;
		super.onDestroy();
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
			fetchQuotes();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
*/

}
