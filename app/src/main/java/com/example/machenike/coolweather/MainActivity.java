package com.example.machenike.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.machenike.coolweather.db.AreaSave;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<AreaSave> areaSaveList =new ArrayList<>();
        areaSaveList= DataSupport.findAll(AreaSave.class);
        if(areaSaveList.size()>0){
            Intent intent = new Intent(MainActivity.this,WeatherFragmentActivity.class);
            intent.putExtra("first",false);
            startActivity(intent);
            finish();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_main,new ChooseAreaFragment()).commit();

//        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
//        String weatherid=preferences.getString("weather_id",null);
//        if(weatherid!=null){
//            Intent intent = new Intent(MainActivity.this,WeatherActivity.class);
//            intent.putExtra("weather_id",weatherid);
//            startActivity(intent);
//            finish();
//        }



    }
}

