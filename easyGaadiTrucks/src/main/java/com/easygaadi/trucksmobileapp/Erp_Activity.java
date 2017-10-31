package com.easygaadi.trucksmobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.easygaadi.erp.DriverList;
import com.easygaadi.erp.MaintenanceList;
import com.easygaadi.erp.PartList;
import com.easygaadi.erp.TripList;
import com.easygaadi.erp.TrunkList;
import com.easygaadi.trucksmobileapp.insurance.RequestQuote;
import com.easygaadi.trucksmobileapp.insurance.YourQuotes;
import com.easygaadi.utils.Constants;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.List;

public class Erp_Activity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Menu menu;
    Toolbar toolbar;
    TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.erp_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(final TabLayout.Tab tab) {

                Log.v("tab nam,e -->",""+tab.getText());
                switch (tab.getPosition()) {
                    case 0:
                        menu.getItem(0).setVisible(false);
                        menu.getItem(1).setVisible(true);
                        mTitle.setText(tab.getText()+" List");
                        break;
                    case 1:
                        mTitle.setText(tab.getText()+" List");
                        menu.getItem(0).setVisible(false);
                        menu.getItem(1).setVisible(true);
                        break;
                    case 2:
                        mTitle.setText(tab.getText()+" List");
                        menu.getItem(0).setVisible(false);
                        menu.getItem(1).setVisible(true);
                        break;
                    case 3:
                        mTitle.setText(tab.getText()+" List");
                        menu.getItem(0).setVisible(true);
                        menu.getItem(1).setVisible(true);
                        break;
                    case 4:
                        mTitle.setText(tab.getText()+" List");
                        menu.getItem(0).setVisible(true);
                        menu.getItem(1).setVisible(true);
                        break;

                    default:
                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TrunkList(), getString(R.string.erp_trunks));
        adapter.addFragment(new DriverList(), getString(R.string.erp_drivers));
        adapter.addFragment(new PartList(), getString(R.string.erp_party));
        adapter.addFragment(new TripList(), getString(R.string.erp_trips));
        adapter.addFragment(new MaintenanceList(), getString(R.string.erp_maintenance));
        viewPager.setAdapter(adapter);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.erp_menu, menu);
        menu.getItem(0).setVisible(false);
        menu.getItem(1).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_rewards:

                break;
            case R.id.action_home:
                finish();
                break;
            case R.id.action_map:

                break;
        /*case R.id.action_report:
            reportproblem();
			break;*/
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {

            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return mFragmentTitleList.get(position);
        }
    }
}
