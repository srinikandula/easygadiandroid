package com.easygaadi.erp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.easygaadi.trucksmobileapp.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by ssv i3-210 on 11/6/2017.
 */

public class CommonERP {


    public Drawable getDays(Context context,String fdate){

        Date date;
        long diff = 0;
        Log.i("start date",fdate);
        DateFormat dateFormat,formatter;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String newDates = null;
        try {
            date = dateFormat.parse(fdate);
            formatter = new SimpleDateFormat("yyyy-MM-dd"); //If you need time just put specific format for time like 'HH:mm:ss'
            newDates = formatter.format(date);
            Log.i("custom date",newDates);
            Date today = new Date();
            diff =  date.getTime()-today.getTime()  ;
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("err--"+e.getMessage());
        }

        //int numOfDays = (int) (diff / (1000 * 60 * 60 * 24));
        long numOfDays = TimeUnit.DAYS.convert(diff,TimeUnit.MILLISECONDS);
        Log.i("numOfDays-->",""+numOfDays);
        System.out.println("numOfDays-->"+numOfDays);
        if(numOfDays >  30)
        {
            Drawable img = context.getResources().getDrawable( R.drawable.orange );
            img.setBounds( 0, 0, 60, 60 );
            return img;
        }else if(numOfDays < 30)
        {
            Drawable img = context.getResources().getDrawable( R.drawable.orange );
            img.setBounds( 0, 0, 60, 60 );
            return img;
        }else
        {
            Drawable img = context.getResources().getDrawable( R.drawable.red );
            img.setBounds( 0, 0, 60, 60 );
            return img;
        }
    }
}
