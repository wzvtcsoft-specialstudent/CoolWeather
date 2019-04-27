package com.example.machenike.coolweather;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.GridLayout;

import java.util.ArrayList;
import java.util.List;

public class WeatherFragmentActivity extends AppCompatActivity {

    private ViewPager fragment_main_viewpager;
    private DrawerLayout fragment_main_drawer_layout;
    private List<WeatherFragment> weatherFragmentList;
    private FragmentPagerAdapter fragmentPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_fragment);
        initView();
        initViewPager();
    }
    public void OpenTheChooseAreaFragment(){
        fragment_main_drawer_layout.openDrawer(GravityCompat.START);
    }
    public void addWeather(){
        WeatherFragment weatherFragment = new WeatherFragment();
        weatherFragmentList.add(weatherFragment);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fragmentPagerAdapter.notifyDataSetChanged();
                fragment_main_drawer_layout.closeDrawer(GravityCompat.START);
                fragment_main_viewpager.setCurrentItem(weatherFragmentList.size()-1);
            }
        });

    }
    private void initViewPager(){
        fragment_main_viewpager.setOffscreenPageLimit(6);
        WeatherFragment  fragment = new WeatherFragment();
        weatherFragmentList.add(fragment);
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
    }
}
