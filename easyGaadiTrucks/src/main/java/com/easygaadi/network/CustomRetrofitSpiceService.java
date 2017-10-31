package com.easygaadi.network;

import com.easygaadi.interfaces.ServerCommands;
import com.easygaadi.utils.CustomJsonConverter;
import com.google.gson.Gson;
import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

/**
 * Created by ibraincpu6 on 14-07-2016.
 */
public class CustomRetrofitSpiceService extends RetrofitGsonSpiceService {

    @Override
    public void onCreate() {
        super.onCreate();
            /*Add Interface files which contains retrofit methods of network calls*/
        addRetrofitInterface(ServerCommands.class);
    }

    @Override
    protected String getServerUrl() {
        return "http://egcrm.cloudapp.net";
    }

    @Override
    protected RestAdapter.Builder createRestAdapterBuilder() {
        RestAdapter.Builder builder;
        builder = super.createRestAdapterBuilder();
        builder.setConverter(new CustomJsonConverter(new Gson()))
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        //request.addHeader("Authorization", "Bearer " + accessToken);
                    }
                });
        builder.setLogLevel(RestAdapter.LogLevel.FULL);
        return builder;
    }
}
