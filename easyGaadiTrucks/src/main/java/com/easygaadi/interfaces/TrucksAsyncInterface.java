package com.easygaadi.interfaces;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by admin on 04-03-2017.
 */
public interface TrucksAsyncInterface {
     void callStarted();
     void saveSettingsCompleted(JSONObject jsonObject, String cardType,String card_username,String card_password,String customerID);
}
