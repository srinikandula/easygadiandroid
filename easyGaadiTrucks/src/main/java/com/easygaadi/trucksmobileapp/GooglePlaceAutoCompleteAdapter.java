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

public class GooglePlaceAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
	
	//private static Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.3.100.207", 8080));
	private static final String LOG_TAG = "Google Places Autocomplete";
	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";
	private static final String API_KEY = "AIzaSyCd0ifbJjaQoMN66oY3Vyjh43r2gH9kPxA";

	
	private ArrayList<String> resList;
	private static String trimCountryName;
	private static boolean trim = false;
	private static boolean filterCities= false;
	public GooglePlaceAutoCompleteAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		System.out.println("GooglePlaceAutocompleteAdapter created");
	}
	
	public GooglePlaceAutoCompleteAdapter(Context context, int textViewResourceId, String trimCountryName, boolean filterCities) {
		this(context, textViewResourceId);
		this.trimCountryName =", "+ trimCountryName;
		this.trim = true;
		this.filterCities = filterCities;
		System.out.println("GooglePlaceAutocompleteAdapter created: trimming country name ");
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
	
	private static ArrayList<String> autocomplete(String input) {
		ArrayList<String> resultList = null;

		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		String filters;
		if(filterCities) filters = "&components=country:in&types=(cities)";
		else filters = "&components=country:in";
		try {
			StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
			sb.append("?key=" + API_KEY);
			sb.append(filters);
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

			// Extract the Place descriptions from the results
			resultList = new ArrayList<String>(predsJsonArray.length());
			System.out.println("resultList.length(): "+predsJsonArray.length());
			String temp, description;
			for (int i = 0; i < predsJsonArray.length(); i++) {
				 temp = predsJsonArray.getJSONObject(i).getString("description");
				System.out.println(temp);
				System.out.println("============================================================");
				if(trim){
					if(temp.lastIndexOf(trimCountryName)==(temp.length()-trimCountryName.length())){
						description = temp.substring(0, (temp.lastIndexOf(trimCountryName)));
					} else description = temp;
				} else description = temp;
				
				
				resultList.add(description);
			}
		} catch (JSONException e) {
			Log.e(LOG_TAG, "Cannot process JSON results", e);
		}

		return resultList;
	}

}
