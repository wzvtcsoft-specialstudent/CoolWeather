package com.example.machenike.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.machenike.coolweather.gson.Forecast;
import com.example.machenike.coolweather.gson.Weather;
import com.example.machenike.coolweather.util.HttpUtils;
import com.example.machenike.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ImageView bing_pic_img;
    private TextView title_city_tv;
    private TextView title_update_time_tv;
    private TextView now_tmp_tv;
    private TextView now_weather_info_tv;
    private LinearLayout forecast_list_layout;
    private TextView aqi_tv;
    private TextView pm25_tv;
    private TextView comfort_tv;
    private TextView car_tv;
    private TextView sport_tv;
    private ScrollView scroll_view;
    private DrawerLayout drawer_layout;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        initView();
        final String weatherid;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor=preferences.edit();
        String weatherstring = preferences.getString("weather",null);
//        if(weatherstring!=null){
//            Weather weather = Utility.handleWeatherResponse(weatherstring);
//            weatherid = weather.basic.weatherId;
//            showWeatherInfo(weather);
//        }else{
            weatherid = getIntent().getStringExtra("weather_id");
            scroll_view.setVisibility(View.INVISIBLE);
            requestWeather(weatherid);
//        }
    }
    public void requestWeather(final String weatherId){
        String weatherUrl = "http://guolin.tech/api/weather?cityid="
                +weatherId+"&key=1c5a7044e65241f1b624bf6880435606";
        HttpUtils.sendOkhttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(data);
                if(weather!=null&&"ok".equals(weather.status)){
                    editor.putString("weather",data);
                    editor.apply();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeatherInfo(weather);
                        }
                    });
                }

            }
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取数据失败",Toast.LENGTH_SHORT);
                    }
                });
            }


        });
    }
    private void showWeatherInfo(Weather weather){
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String nowTemp=weather.now.temperature +"℃";
        String weatherInfo = weather.now.info;
        title_city_tv.setText(cityName);
        title_update_time_tv.setText(updateTime);
        now_tmp_tv.setText(nowTemp);
        now_weather_info_tv.setText(weatherInfo);
        forecast_list_layout.removeAllViews();
        for(Forecast forecast :weather.forecastList){
            View view = LayoutInflater.from(this).inflate(R.layout.forcast_item,forecast_list_layout,false);
            TextView datetv=(TextView) view.findViewById(R.id.forcast_item_date_tv);
            TextView infotv=(TextView) view.findViewById(R.id.forcast_item_weather_tv);
            TextView maxtv=(TextView)view.findViewById(R.id.forcast_item_max_tv);
            TextView mintv=(TextView)view.findViewById(R.id.forcast_item_min_tv);

            datetv.setText(forecast.date);
            infotv.setText(forecast.more.info);
            maxtv.setText(forecast.temperature.max);
            mintv.setText(forecast.temperature.min);
            forecast_list_layout.addView(view);
        }
        if(weather.aqi!=null){
            aqi_tv.setText(weather.aqi.city.aqi);
            pm25_tv.setText(weather.aqi.city.pm25);
        }
        if(weather.suggestion!=null){
            comfort_tv.setText("舒适度:"+weather.suggestion.comfortable.info);
            car_tv.setText("洗车指数:"+weather.suggestion.car.info);
            sport_tv.setText("运动建议:"+weather.suggestion.sport.info);
        }
        scroll_view.setVisibility(View.VISIBLE);
    }
    private void initView() {
        bing_pic_img = (ImageView) findViewById(R.id.bing_pic_img);
        title_city_tv = (TextView) findViewById(R.id.title_city_tv);
        title_update_time_tv = (TextView) findViewById(R.id.title_update_time_tv);
        now_tmp_tv = (TextView) findViewById(R.id.now_tmp_tv);
        now_weather_info_tv = (TextView) findViewById(R.id.now_weather_info_tv);
        forecast_list_layout = (LinearLayout) findViewById(R.id.forecast_list_layout);
        aqi_tv = (TextView) findViewById(R.id.aqi_tv);
        pm25_tv = (TextView) findViewById(R.id.pm25_tv);
        comfort_tv = (TextView) findViewById(R.id.comfort_tv);
        car_tv = (TextView) findViewById(R.id.car_tv);
        sport_tv = (TextView) findViewById(R.id.sport_tv);
        scroll_view = (ScrollView) findViewById(R.id.scroll_view);
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
    }
}