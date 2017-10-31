package com.easygaadi.gpsapp.utilities;

import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by ibraincpu6 on 07-07-2016.
 */
public class Application extends android.app.Application {


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}