package com.example.machenike.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.GridLayout;

import com.example.machenike.coolweather.db.AreaSave;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class WeatherFragmentActivity extends AppCompatActivity {

    private ViewPager fragment_main_viewpager;
    private DrawerLayout fragment_main_drawer_layout;
    private List<WeatherFragment> weatherFragmentList;
    private FragmentPagerAdapter fragmentPagerAdapter;
    private List<AreaSave> areaSaveList;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_fragment);
        initView();
        initViewPager();
        judgeFirst();
    }
    public void judgeFirst(){
        Intent intent = getIntent();
        boolean first = intent.getBooleanExtra("first",true);
        if(first){
            addWeather(intent.getStringExtra("weather_id"));
        }else{
            areaSaveList = DataSupport.findAll(AreaSave.class);
            for(AreaSave areaSave:areaSaveList){
//                editor.putString("weather_id",areaSave.getWeatherID());
//                editor.commit();
                addWeather(areaSave.getWeatherID());
            }
        }
    }
    public void OpenTheChooseAreaFragment(){
        fragment_main_drawer_layout.openDrawer(GravityCompat.START);
    }
    public void addWeather(String weatherID){
        WeatherFragment weatherFragment = new WeatherFragment();
        Bundle bundle = new Bundle();
        bundle.putString("weather_id",weatherID);
        weatherFragment.setArguments(bundle);
        weatherFragmentList.add(weatherFragment);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fragmentPagerAdapter.notifyDataSetChanged();
                if(fragment_main_drawer_layout.isDrawerOpen(GravityCompat.START))
                fragment_main_drawer_layout.closeDrawer(GravityCompat.START);
                fragment_main_viewpager.setCurrentItem(weatherFragmentList.size()-1);
            }
        });

    }
    private void initViewPager(){
        fragment_main_viewpager.setOffscreenPageLimit(6);
//        WeatherFragment fragment = new WeatherFragment();
//        weatherFragmentList.add(fragment);
        fragmentPagerAdapter=new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return weatherFragmentList.get(i);
            }

            @Override
            public int getCount() {
                return weatherFragmentList.size();
            }
        };
       fragment_main_viewpager.setAdapter(fragmentPagerAdapter);
        fragment_main_viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                //TODO
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }
    private void initView() {
        fragment_main_viewpager = (ViewPager) findViewById(R.id.fragment_main_viewpager);
        fragment_main_drawer_layout = (DrawerLayout) findViewById(R.id.fragment_main_drawer_layout);
        weatherFragmentList=new ArrayList<>();
        areaSaveList=new ArrayList<>();
        preferences= getSharedPreferences("AreaSave",MODE_MULTI_PROCESS);
        editor=preferences.edit();
    }
}
