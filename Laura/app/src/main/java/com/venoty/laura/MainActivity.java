package com.venoty.laura;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.venoty.laura.adapters.TabAdapter;
import com.venoty.laura.databases.GlucoseDBHelper;
import com.venoty.laura.fragments.ChartFragment;
import com.venoty.laura.fragments.HomeFragment;
import com.venoty.laura.models.Glucose;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Remove border bettwen tabs and toolbar
        getSupportActionBar().setElevation(0);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        adapter = new TabAdapter(getSupportFragmentManager());

        ChartFragment chartFragment = new ChartFragment();
        chartFragment.setTabLayout(tabLayout);

        HomeFragment homeFragment   = new HomeFragment();
        homeFragment.setTabLayout(tabLayout);

        adapter.addFragment(homeFragment);
        adapter.addFragment(chartFragment);

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_graph);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.export:
                Intent myIntent = new Intent(this, ExportData.class);
                startActivity(myIntent);
                return true;
            case R.id.insert:
                insertRandomData();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void insertRandomData(){
        GlucoseDBHelper db = new GlucoseDBHelper(getApplicationContext());
        Random rand  = new Random();
        int  measure = rand.nextInt(300) + 50;
        db.insertMeasure(new Glucose(measure));
        db.close();
    }
}
