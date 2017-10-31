package com.easygaadi.trucksmobileapp;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

public class CityAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
	
	//private static Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.3.100.207", 8080));
	private static final String LOG_TAG = "Google Places Autocomplete";
	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";
	private static final String API_KEY = "AIzaSyCd0ifbJjaQoMN66oY3Vyjh43r2gH9kPxA";

	
	private ArrayList<String> resList;

	public CityAutoCompleteAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		System.out.println("GooglePlaceAutocompleteAdapter created");
	}

	@Override
	public int getCount() {
		return resList.size();
	}

	@Override
	public String getItem(int index) {
		return resList.get(index);
	}

	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();
				if (constraint != null) {
					// Retrieve the autocomplete results.
					resList = autocomplete(constraint.toString());

					// Assign the data to the FilterResults
					filterResults.values = resList;
					filterResults.count = resList.size();
					System.out.println("filtered results\n"+resList.toString());
				}
				return filterResults;
			}

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				if (results != null && results.count > 0) {
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}
			}
		};
		return filter;
	}
	
	public static ArrayList<String> autocomplete(String input) {
		ArrayList<String> resultList = null;

		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
			sb.append("?key=" + API_KEY);
			sb.append("&components=country:in&types=(cities)");
			sb.append("&input=" + URLEncoder.encode(input, "utf8"));
			System.out.println(sb.toString());
			URL url = new URL(sb.toString());
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
			System.out.println(" jsonResults: "+jsonResults.toString());
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, "Error processing Places API URL", e);
			return resultList;
		} catch (IOException e) {
			Log.e(LOG_TAG, "Error connecting to Places API", e);
			return resultList;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		try {			
			// Create a JSON object hierarchy from the results
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
			String orig;
			String trimmed;
			// Extract the Place descriptions from the results
			resultList = new ArrayList<String>(predsJsonArray.length());
			System.out.println("resultList.length(): "+predsJsonArray.length());
			for (int i = 0; i < predsJsonArray.length(); i++) {
				System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
				//JSONArray termsJsonArray = predsJsonArray.getJSONObject(i).getJSONArray("terms");
				//JSONArray typesJsonArray = predsJsonArray.getJSONObject(i).getJSONArray
				System.out.println("============================================================");
				orig =predsJsonArray.getJSONObject(i).getString("description");
				
				if(orig.indexOf(", India")==(orig.length()-7)) trimmed = orig.substring(0,(orig.length()-7));
				else trimmed = orig;
				
				resultList.add(trimmed);
			}
		} catch (JSONException e) {
			Log.e(LOG_TAG, "Cannot process JSON results", e);
		}

		return resultList;
	}

}
