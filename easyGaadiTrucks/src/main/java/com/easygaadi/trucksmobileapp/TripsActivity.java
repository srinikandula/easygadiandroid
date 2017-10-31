package com.easygaadi.trucksmobileapp;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

import android.R.color;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.ExceptionHandler;
import com.easygaadi.gpsapp.utilities.JSONParser;
import com.easygaadi.trucksmobileapp.ListOfTrackingTrucks.TrackingTrucksAdapter;
import com.easygaadi.utils.Constants;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class TripsActivity extends AppCompatActivity {
	public static final int REQUEST_CODE = 100;
	public static final int RESULT_CODE = 200;
	Context context;
	SharedPreferences sharedPreferences;
	private ConnectionDetector detectConnection;
	Dialog confirmDialog;
	JSONParser parser;
	ProgressDialog pDialog;
	ListView tripsListView;
	JSONArray tripsArray;
	TripsListAdapter tripsListAdapter;
	int totalTrips, offset;
	boolean loadMore_st;
	EditText search_et;
	Button search_btn, confirm_btn, cancel_btn;
	TextView count_tv, confirm_tv, confirm_dlg_title_tv, confirm_dlg_src_tv, confirm_dlg_dest_tv, confirm_dlg_date_tv;
	private AdView adView;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_trips_list);
		context = this;
		sharedPreferences = getSharedPreferences(getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
		detectConnection = new ConnectionDetector(context);
		confirmDialog = new Dialog(context, android.R.style.Theme_Dialog);
		confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		confirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
		confirmDialog.setContentView(R.layout.dialog_confirm);

		parser = JSONParser.getInstance();
		pDialog = new ProgressDialog(context);
		offset = 0;
		tripsListView = (ListView) findViewById(R.id.trips_lv);
		search_et = (EditText) findViewById(R.id.trips_search_et);
		search_btn = (Button) findViewById(R.id.trips_search_btn);
		count_tv = (TextView) findViewById(R.id.trips_count_tv);
		adView = (AdView)findViewById(R.id.adView);
		intializeBannerAd();

		confirm_dlg_title_tv = (TextView) confirmDialog.findViewById(R.id.confirm_dlg_title_tv);
		confirm_btn = (Button) confirmDialog.findViewById(R.id.confirm_dlg_confirm_btn);
		cancel_btn = (Button) confirmDialog.findViewById(R.id.confirm_dlg_cancel_btn);
		confirm_tv = (TextView) confirmDialog.findViewById(R.id.confirm_dlg_main_tv);
		confirm_dlg_src_tv = (TextView) confirmDialog.findViewById(R.id.confirm_dlg_src_tv);
		confirm_dlg_dest_tv = (TextView) confirmDialog.findViewById(R.id.confirm_dlg_dest_tv);
		confirm_dlg_date_tv = (TextView) confirmDialog.findViewById(R.id.confirm_dlg_date_created_tv);
		fetchTrips(offset, false);

		search_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				fetchTrips(0, true);

			}
		});
		cancel_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (confirmDialog != null && confirmDialog.isShowing()) {
					confirmDialog.dismiss();
				}
			}
		});
		tripsListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (totalTrips > 10) {
					System.out.println("loadMore_st" + loadMore_st);
					if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
						if (loadMore_st == false) {
							loadMore_st = true;
							if (tripsArray.length() < totalTrips) {
								offset = offset + 10;
								System.out.println("Value to be loaded:" + offset);
								fetchTrips(offset, false);
							} else {
								System.out.println("size is  less than 10");
							}
						}
					}
				}
			}
		});

	}

	public void fetchTrips(int offset, boolean search) {
		if (detectConnection.isConnectingToInternet()) {
			if (0 == offset) {
				if (!search)
					search_et.setText("");
				tripsArray = new JSONArray();
				tripsListAdapter = new TripsListAdapter(tripsArray, TripsActivity.this);
				tripsListView.setAdapter(tripsListAdapter);
			}
			try {
				new GetTrips(sharedPreferences.getString("accountID", ""), offset, search).execute();
			} catch (Exception e) {
				System.out.println("Ex inn on resume:" + e.toString());
			}
		} else {
			Toast.makeText(context, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		TruckApp.getInstance().trackScreenView("Trips Screen");
		if (adView != null) {
			adView.resume();
		}

	}


	@Override
	protected void onPause() {
		if (adView != null) {
			adView.pause();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (adView != null)
		{ adView.destroy(); }
		super.onDestroy();
	}


	private void intializeBannerAd() {
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
	}


	private class GetTrips extends AsyncTask<String, String, JSONObject> {

		String uid, q;
		int offset;
		boolean srch;

		public GetTrips(String uid, int offset, boolean search) {
			this.uid = uid;
			this.offset = offset;
			this.srch = search;
			if (srch)
				q = search_et.getText().toString();
			else
				q = "";
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if (!srch)
				pDialog.setMessage("Fetching Trips...");
			else
				pDialog.setMessage("Searching Trips...");
			pDialog.show();
		}

		@Override
		protected JSONObject doInBackground(String... params) {

			JSONObject json = null;
			try {
				StringBuilder builder = new StringBuilder();
				builder.append("Trip[accountID]=").append(URLEncoder.encode(uid, "UTF-8"));
				builder.append("&Trip[offset]=").append(URLEncoder.encode(String.valueOf(offset), "UTF-8"));
				builder.append("&Trip[q]=").append(URLEncoder.encode(q, "UTF-8"));
				builder.append("&access_token=").append(sharedPreferences.getString("access_token",""));

				String res = parser.excutePost(TruckApp.getTripsURL, builder.toString());
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
						Toast.makeText(context, "Failed to fetch trips", Toast.LENGTH_LONG).show();
					}else if (2 == result.getInt("status")) {
						//TruckApp.logoutAction(TripsActivity.this);
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
					}  else if (1 == result.getInt("status")) {
						totalTrips = result.getInt("count");
						if (totalTrips >= 0) {
							if (result.has("data")) {
								JSONArray tripsArray1 = result.getJSONArray("data");
								for (int i = 0; i < tripsArray1.length(); i++) {
									tripsArray.put(tripsArray1.getJSONObject(i));
									// System.out.println("CUS
									// ID"+tripsArray1.getJSONObject(i).getString("id_customer"));
								}

								if (totalTrips > tripsArray.length()) {
									loadMore_st = false;
								} else {
									loadMore_st = true;
								}
								((TripsListAdapter) tripsListView.getAdapter()).notifyDataSetChanged();
								if (tripsArray.length() > 5 && tripsArray1 != null && tripsArray1.length() > 0) {
									tripsListView.setSelection(tripsArray.length() - tripsArray1.length());
								}

							}
							String holder;
							if (srch)
								holder = " Results ";
							else
								holder = " Trips ";
							count_tv.setText("Showing "+ tripsArray.length() + "/" + totalTrips+holder);
							count_tv.setVisibility(View.VISIBLE);
						}
					}

				} catch (Exception e) {
					System.out.println("ex in get leads" + e.toString());
				}
				TruckApp.checkPDialog(pDialog);
			} else {
				TruckApp.checkPDialog(pDialog);
				Toast.makeText(context, getResources().getString(R.string.exceptionmsg), Toast.LENGTH_LONG).show();
			}

		}
	}

	public class TripsListAdapter extends BaseAdapter {
		Activity activity;
		JSONArray mTripsArray;
		LayoutInflater inflater;

		public class ViewHolder {
			TextView tvTruckNo, tvSrc, tvDest, tvStartLoc, tvCurrLoc, tvStopLoc, tvOdo, tvDateCreated;
			ImageView ivStop, ivDelete;
			LinearLayout llTripItem;
			RelativeLayout rlCurrLoc, rlStopLoc, rlSrcDest;
		}

		public TripsListAdapter(JSONArray trips, Activity activity) {
			mTripsArray = trips;
			this.activity = activity;
			inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return mTripsArray.length();
		}

		@Override
		public Object getItem(int position) {
			try {
				return mTripsArray.get(position);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewholder = null;
			if (convertView == null) {
				viewholder = new ViewHolder();
				convertView = inflater.inflate(R.layout.trips_list_item, parent, false);
				viewholder.tvTruckNo = (TextView) convertView.findViewById(R.id.trips_truck_no);
				viewholder.tvSrc = (TextView) convertView.findViewById(R.id.trip_src_tv);
				viewholder.tvDest = (TextView) convertView.findViewById(R.id.trip_dest_tv);
				viewholder.tvStartLoc = (TextView) convertView.findViewById(R.id.trip_start_loc_tv);
				viewholder.tvCurrLoc = (TextView) convertView.findViewById(R.id.trip_curr_loc_tv);
				viewholder.tvStopLoc = (TextView) convertView.findViewById(R.id.trip_stop_loc_tv);
				viewholder.ivStop = (ImageView) convertView.findViewById(R.id.trip_stop_iv);
				viewholder.ivDelete = (ImageView) convertView.findViewById(R.id.trip_delete_iv);
				viewholder.rlCurrLoc = (RelativeLayout) convertView.findViewById(R.id.curr_loc_rl);
				viewholder.rlStopLoc = (RelativeLayout) convertView.findViewById(R.id.trip_stop_loc_rl);
				viewholder.tvOdo = (TextView) convertView.findViewById(R.id.trip_dist_tv);
				viewholder.tvDateCreated = (TextView) convertView.findViewById(R.id.trip_created_tv);
				viewholder.llTripItem = (LinearLayout) convertView.findViewById(R.id.trip_clickable_ll);
				viewholder.rlSrcDest = (RelativeLayout) convertView.findViewById(R.id.trip_src_dest_rl);
				convertView.setTag(viewholder);

			} else {
				viewholder = (ViewHolder) convertView.getTag();
			}

			try {
				JSONObject trip = mTripsArray.getJSONObject(position);
				viewholder.tvTruckNo.setText(trip.getString("deviceID"));
				viewholder.tvSrc.setText(trip.getString("source"));
				viewholder.tvDest.setText(trip.getString("destination"));
				viewholder.tvStartLoc.setText(trip.getString("startLoc"));
				viewholder.tvDateCreated.setText(
						DateFormat.getDateTimeInstance().format(new Date((long) trip.getInt("startPointTime") * 1000)));

				viewholder.ivStop.setTag(trip.toString());
				viewholder.ivDelete.setTag(trip.toString());
				viewholder.llTripItem.setTag(trip.toString());

				// convertView.setTag(R.id.tracktrip, trip.toString());
				System.out.println(trip.getString("id") + "-----" + trip.getInt("id"));
				if (trip.getString("endPointTime").equals("0")) {
					if (trip.getString("currentLoc").equals(""))
						viewholder.rlCurrLoc.setVisibility(View.GONE);
					else
						viewholder.tvCurrLoc.setText(trip.getString("currentLoc"));
					viewholder.tvOdo.setVisibility(View.GONE);
					viewholder.rlSrcDest.setBackgroundColor(getResources().getColor(R.color.active_green));

				} else {
					viewholder.tvOdo.setVisibility(View.VISIBLE);
					viewholder.rlStopLoc.setVisibility(View.VISIBLE);
					viewholder.tvOdo.setText(" " + trip.getString("odo") + " km");
					viewholder.tvStopLoc.setText(" " + trip.getString("endLoc"));
					viewholder.rlCurrLoc.setVisibility(View.GONE);
					viewholder.ivStop.setVisibility(View.GONE);
					viewholder.rlSrcDest.setBackgroundColor(getResources().getColor(R.color.notactive_red));
				}

				viewholder.llTripItem.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						try {
							JSONObject obj = new JSONObject(v.getTag().toString());
							String trip_id = (String) obj.getString("id");

							System.out.println(trip_id);
							Intent intent = new Intent(context, TrackTruckActivity.class);
							intent.putExtra("istrip", true);
							intent.putExtra("tripId", trip_id);
							intent.putExtra("truckNo", obj.getString("deviceID"));
							intent.putExtra("accId", obj.getString("accountID"));
							intent.putExtra("startTime", obj.getString("startPointTime"));
							intent.putExtra("endTime", obj.getString("endPointTime"));
							intent.putExtra("json", obj.toString());
							startActivityForResult(intent, REQUEST_CODE);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				});

				viewholder.ivStop.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						try {
							JSONObject obj = new JSONObject(v.getTag().toString());
							String trip_id = (String) obj.getString("id");
							String acc_id = (String) obj.getString("accountID");
							String dev_id = (String) obj.getString("deviceID");
							String start_time = (String) obj.getString("startPointTime");
							System.out.println(trip_id + "---" + acc_id + "---" + dev_id + "---" + start_time);
							// new StopTrip(trip_id, acc_id, dev_id,
							// start_time).execute();
							confirmDialog(obj.toString(), "stop");

						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				});

				viewholder.ivDelete.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						try {
							JSONObject obj = new JSONObject(v.getTag().toString());
							String trip_id = (String) obj.getString("id");
							String acc_id = (String) obj.getString("accountID");
							String dev_id = (String) obj.getString("deviceID");
							System.out.println(trip_id);
							// new DeleteTrip(trip_id, acc_id,
							// dev_id).execute();
							confirmDialog(obj.toString(), "delete");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				});

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return convertView;
		}

	}

	@Override
	protected void onActivityResult(int reqCode, int resCode, Intent data) {
		if (reqCode == REQUEST_CODE & resCode == RESULT_CODE) {
			if (data.hasExtra("refresh")) {
				if (data.getBooleanExtra("refresh", false))
					recreate();
			}
		}
	}

	private class StopTrip extends AsyncTask<String, String, JSONObject> {
		String tripID, accID, devID, startTime;

		public StopTrip(String id_trip, String acc_id, String dev_id, String start_time) {
			this.tripID = id_trip;
			this.accID = acc_id;
			this.devID = dev_id;
			this.startTime = start_time;
		}

		@Override
		protected JSONObject doInBackground(String... params) {
			JSONObject json = null;

			try {
				String url = TruckApp.stopTripURL;
				String urlParams = "Trip[id]=" + URLEncoder.encode(tripID, "UTF-8") + "&Trip[accountID]="
						+ URLEncoder.encode(accID, "UTF-8") + "&Trip[deviceID]=" + URLEncoder.encode(devID, "UTF-8")
						+ "&Trip[startPointTime]=" + URLEncoder.encode(startTime, "UTF-8");
				String res = parser.excutePost(url, urlParams);
				json = new JSONObject(res);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return json;
		}

		@Override
		public void onPostExecute(JSONObject result) {

			if (confirmDialog != null && confirmDialog.isShowing())
				confirmDialog.dismiss();
			if (result != null) {
				try {
					if (result.getInt("status") == 0) {
						Toast.makeText(context, "Error stopping trip", Toast.LENGTH_SHORT);
					} else if (result.getInt("status") == 1) {
						Toast.makeText(context, "Trip stopped", Toast.LENGTH_SHORT);
						/*
						 * final Handler handler = new Handler();
						 * handler.postDelayed(new Runnable() {
						 * 
						 * @Override public void run() { recreate(); } },
						 * Toast.LENGTH_SHORT);
						 */
						recreate();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				Toast.makeText(context, getResources().getString(R.string.exceptionmsg), Toast.LENGTH_LONG).show();
			}

		}

	}

	private class DeleteTrip extends AsyncTask<String, String, JSONObject> {
		String tripID, accID, devID;

		public DeleteTrip(String id_trip, String acc_id, String dev_id) {
			this.tripID = id_trip;
			this.accID = acc_id;
			this.devID = dev_id;
		}

		@Override
		protected JSONObject doInBackground(String... params) {
			JSONObject json = null;

			try {
				String url = TruckApp.deleteTripURL;
				String urlParams = "Trip[id]=" + URLEncoder.encode(tripID, "UTF-8") + "&Trip[accountID]="
						+ URLEncoder.encode(accID, "UTF-8") + "&Trip[deviceID]=" + URLEncoder.encode(devID, "UTF-8");
				String res = parser.excutePost(url, urlParams);
				json = new JSONObject(res);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return json;
		}

		@Override
		public void onPostExecute(JSONObject result) {

			if (confirmDialog != null && confirmDialog.isShowing())
				confirmDialog.dismiss();
			if (result != null) {
				try {
					if (result.getInt("status") == 0) {
						Toast.makeText(context, "Error deleting trip", Toast.LENGTH_SHORT);
					} else if (result.getInt("status") == 1) {
						Toast.makeText(context, "Trip deleted", Toast.LENGTH_SHORT);
						recreate();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				Toast.makeText(context, getResources().getString(R.string.exceptionmsg), Toast.LENGTH_LONG).show();
			}

		}

	}

	protected void confirmDialog(String string, String action) {
		final String act = action;
		try {
			final JSONObject jsonObj = new JSONObject(string);
			String dateCreated = DateFormat.getDateTimeInstance()
					.format(new Date((long) jsonObj.getInt("startPointTime") * 1000));
			if (action.equals("stop")) {
				//confirm_dlg_title_tv.setText("Stop Trip " + jsonObj.getString("deviceID"));
				TruckApp.spanTextview(confirm_dlg_title_tv, "Stop Trip ", jsonObj.getString("deviceID"));
				// confirm_tv.setText("Stop this trip created on
				// :"+dateCreated.substring(0,dateCreated.indexOf(' '))+"?");
				confirm_btn.setText("Stop");
			} else if (action.equals("delete")) {
				//confirm_dlg_title_tv.setText("Delete Trip " + jsonObj.getString("deviceID"));
				TruckApp.spanTextview(confirm_dlg_title_tv, "Delete Trip ", jsonObj.getString("deviceID"));
				confirm_btn.setText("Delete");
				// confirm_tv.setText("Delete this trip created on
				// :"+dateCreated.substring(0,dateCreated.indexOf(' '))+"?");
			}
			confirm_dlg_date_tv.setText("Date Created: " + dateCreated);
			confirm_dlg_src_tv.setText(jsonObj.getString("source"));
			confirm_dlg_dest_tv.setText(jsonObj.getString("destination"));
			confirmDialog.show();
			confirm_btn.setOnClickListener(new OnClickListener() {
				String id_trip = jsonObj.getString("id");
				String id_acc = jsonObj.getString("accountID");
				String id_dev = jsonObj.getString("deviceID");
				String time_start = jsonObj.getString("startPointTime");

				@Override
				public void onClick(View v) {
					if (detectConnection.isConnectingToInternet()) {
						if (act.equals("stop")) {
							System.out.println("-----------------STOP---------------");
							new StopTrip(id_trip, id_acc, id_dev, time_start).execute();
						} else if (act.equals("delete")) {
							System.out.println("-----------------DELETE-------------");
							new DeleteTrip(id_trip, id_acc, id_dev).execute();
						}
					} else
						Toast.makeText(context, R.string.internet_str, Toast.LENGTH_SHORT).show();

				}

			});

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.trips_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_trips_refresh)
			recreate();

		return super.onOptionsItemSelected(item);

	}
}
