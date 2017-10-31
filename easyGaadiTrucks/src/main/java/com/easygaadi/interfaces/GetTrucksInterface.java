package com.easygaadi.interfaces;

import org.json.JSONArray;

/**
 * Created by admin on 04-03-2017.
 */
public interface GetTrucksInterface {
     void callStarted();
     void callSuccess(JSONArray trucksArray);
     void callFailure(String message);
}
