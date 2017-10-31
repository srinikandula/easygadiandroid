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

public class TrucksAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
	
	private static final String LOG_TAG = "Trucks Autocomplete";	
	private ArrayList<String> resList;
	private ArrayList<String> trkList;

	public TrucksAutoCompleteAdapter(Context context, int textViewResourceId, ArrayList<String> trks) {
		super(context, textViewResourceId);
		this.trkList = trks;
		System.out.println("TrucksAutocompleteAdapter created");
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
	
	public ArrayList<String> autocomplete(String input) {
		ArrayList<String> resultList = null;		
		String temp;
		try {			
			resultList = new ArrayList<String>();
			for (int i = 0; i < trkList.size(); i++) {	
				temp = trkList.get(i);
				if(temp.toLowerCase().indexOf(input.toLowerCase())!=-1)	resultList.add(temp);
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, "Cannot process results", e);
		}

		return resultList;
	}

}
