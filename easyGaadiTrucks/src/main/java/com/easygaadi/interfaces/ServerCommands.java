package com.easygaadi.interfaces;

import com.easygaadi.models.GroupItemResponse;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by ibraincpu6 on 05-12-2016.
 */

public interface ServerCommands {
    @GET("/operations/index.php/GPSapiV3/getGroupList")
    GroupItemResponse getListOfGroups(@Query("account_id") String accountId);
}
