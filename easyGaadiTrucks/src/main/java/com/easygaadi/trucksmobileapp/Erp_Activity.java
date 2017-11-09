package com.easygaadi.trucksmobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.easygaadi.erp.DriverList;
import com.easygaadi.erp.MaintenanceList;
import com.easygaadi.erp.PagerAdapter;
import com.easygaadi.erp.PartList;
import com.easygaadi.erp.TripList;
import com.easygaadi.erp.TruckList;

import java.util.ArrayList;
import java.util.List;

public class Erp_Activity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Menu menu;
    Toolbar toolbar;
    TextView mTitle;
    ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.erp_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
       // viewPager = (ViewPager) findViewById(R.id.viewpager);
        //viewPager.setOffscreenPageLimit(1);
        //setupViewPager(viewPager);



        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.erp_trunks)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.erp_drivers)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.erp_party)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.erp_trips)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.erp_maintenance)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(0);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(final TabLayout.Tab tab) {

                Log.v("tab nam,e -->",""+tab.getText());
                viewPager.setCurrentItem(tab.getPosition());
                LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(Erp_Activity.this);
                Intent i;
                switch (tab.getPosition()) {
                    case 0:

                        mTitle.setText(tab.getText()+" List");
                        //viewPager.setCurrentItem(tab.getPosition());

                         i = new Intent("TAG_REFRESH");
                        lbm.sendBroadcast(i);
                        break;
                    case 1:
                        mTitle.setText(tab.getText()+" List");
                       // viewPager.setCurrentItem(tab.getPosition());
                         i = new Intent("TAG_REFRESH");
                        lbm.sendBroadcast(i);
                        break;
                    case 2:
                        mTitle.setText(tab.getText()+" List");
                        //viewPager.setCurrentItem(tab.getPosition());
                        i = new Intent("TAG_REFRESH");
                        lbm.sendBroadcast(i);
                        break;
                    case 3:
                        mTitle.setText(tab.getText()+" List");
                        //viewPager.setCurrentItem(tab.getPosition());
                        i = new Intent("TAG_REFRESH");
                        lbm.sendBroadcast(i);
                        break;
                    case 4:
                        mTitle.setText(tab.getText()+" List");
                       // viewPager.setCurrentItem(tab.getPosition());
                        i = new Intent("TAG_REFRESH");
                        lbm.sendBroadcast(i);
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
         adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TruckList(), getString(R.string.erp_trunks));
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
        menu.getItem(0).setVisible(true);
        menu.getItem(1).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_reports:
                startActivity(new Intent(Erp_Activity.this,ERP_Report.class));
                break;
            case R.id.action_home:
                finish();
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
