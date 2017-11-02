package com.easygaadi.erp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int numTabs;

    public PagerAdapter(FragmentManager fm, int numTabs) {
        super(fm);
        this.numTabs = numTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                TruckList tab1 = new TruckList();
                return tab1;
            case 1:

                DriverList tab2 = new DriverList().newInstance("load","load");

                return tab2;
            case 2:
                PartList tab3 = new PartList();
                return tab3;
            case 3:
                TripList tab4 = new TripList();
                return tab4;
            case 4:
                MaintenanceList tab5 = new MaintenanceList();
                return tab5;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numTabs;
    }

}
