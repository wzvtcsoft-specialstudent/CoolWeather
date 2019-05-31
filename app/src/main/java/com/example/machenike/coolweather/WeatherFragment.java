package com.example.machenike.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.machenike.coolweather.db.AreaSave;
import com.example.machenike.coolweather.db.History;
import com.example.machenike.coolweather.gson.Forecast;
import com.example.machenike.coolweather.gson.Weather;
import com.example.machenike.coolweather.service.AutoUpdateService;
import com.example.machenike.coolweather.util.HttpUtils;
import com.example.machenike.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.Context.MODE_MULTI_PROCESS;

public class WeatherFragment extends Fragment{
    private View view;
    private ImageView bing_pic_img;
    private Button weather_back_bt;
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
    private LinearLayout history_list_layout;
    private ScrollView scroll_view;
    private SwipeRefreshLayout swipe_refresh;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String weatherID;
    private List<History> historyList;
    private Weather weather;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_weather, container, false);
        initView(view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initSwipeRefresh();
        setHomeButtom();
        initBingPic();
        setTitle();
        initWeatherInfo();
        showHistory();
    }
    public void initSwipeRefresh(){
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherID);
                DataSupport.deleteAll(History.class);
                requestHistory();
                loadBingPic();
            }
        });
    }

    private void setHomeButtom(){
        weather_back_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WeatherFragmentActivity activity = (WeatherFragmentActivity)getActivity();
                activity.OpenTheChooseAreaFragment();
            }
        });
    }

    private void requestHistory(){
        String address="http://www.ipip5.com/today/api.php?type=json";
        HttpUtils.sendOkhttpRequest(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();
                Log.d("-------",data);
                if(Utility.handleHistoryResponse(data)){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showHistory();
                        }
                    });
                };
            }
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d("History Eorror!","网络请求失败");
            }


        });
    }
    private void showHistory(){
        history_list_layout.removeAllViews();
        historyList= DataSupport.findAll(History.class);
        if(historyList.size()>0){
            for(History history:historyList){
                View view=LayoutInflater.from(getActivity())
                        .inflate(R.layout.history_item,history_list_layout,false);
                TextView year = (TextView)view.findViewById(R.id.year_history_item_tv);
                TextView info = (TextView)view.findViewById(R.id.how_history_item_tv);
                year.setText(history.getYear());
                info.setText(history.getInfo());
                history_list_layout.addView(view);
            }
        }else{
            requestHistory();
        }
    }

    private void initWeatherInfo(){
//        Intent intent=getActivity().getIntent();
//        weatherID=intent.getBooleanExtra("")

//        weatherID=preferences.getString("weather_id",null);
        Bundle bundle= this.getArguments();
        weatherID=bundle.getString("weather_id");
        scroll_view.setVisibility(View.INVISIBLE);
        swipe_refresh.setRefreshing(true);
        if(weatherID!=null)
        requestWeather(weatherID);
    }
    private void requestWeather(final String weatherId){

        new Thread(new Runnable() {
            @Override
            public void run() {
                String weatherUrl = "https://free-api.heweather.net/s6/weather?location="
                        +weatherId+"&key=1c5a7044e65241f1b624bf6880435606";
                HttpUtils.sendOkhttpRequest(weatherUrl, new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string();
                        Log.d("-----","Weather"+data);
                        weather = Utility.handleWeatherResponse(data);
                        List<AreaSave> areaSaveList = DataSupport.where
                                ("cityName = ?",weather.basic.cityName).find(AreaSave.class);
                        if(areaSaveList.size()==0)
                        Utility.saveArea(weatherID,weather.basic.cityName,data);
                        if(weather!=null&&"ok".equals(weather.status)){
                            editor.putString("weather",data);
                            editor.apply();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showWeatherInfo(weather);
                                    swipe_refresh.setRefreshing(false);
                                }
                            });
                        }

                    }
                    @Override
                    public void onFailure(Call call, IOException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                swipe_refresh.setRefreshing(false);
                                Toast.makeText(getActivity(),"获取数据失败",Toast.LENGTH_SHORT);
                            }
                        });
                    }
                });
            }
        }).start();

    }
    private void showWeatherInfo(Weather weather){
        String cityName = weather.basic.cityName;
        String updateTime = weather.update.updateTime.split(" ")[1];
        String nowTemp=weather.now.temperature +"℃";
        String weatherInfo = weather.now.info;
        title_city_tv.setText(cityName);
        title_update_time_tv.setText(updateTime);
        now_tmp_tv.setText(nowTemp);
        now_weather_info_tv.setText(weatherInfo);
        forecast_list_layout.removeAllViews();

        for(Forecast forecast :weather.forecastList){
//            View view=View.inflate(getActivity(),R.layout.forcast_item,null);
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.forcast_item,forecast_list_layout,false);
            TextView datetv=(TextView) view.findViewById(R.id.forcast_item_date_tv);
            TextView infotv=(TextView) view.findViewById(R.id.forcast_item_weather_tv);
            TextView maxtv=(TextView)view.findViewById(R.id.forcast_item_max_tv);
            TextView mintv=(TextView)view.findViewById(R.id.forcast_item_min_tv);
            ImageView imageView=(ImageView)view.findViewById(R.id.forcast_weather_img);

            datetv.setText(forecast.date);
            infotv.setText(forecast.info);
            maxtv.setText(forecast.max);
            mintv.setText(forecast.min);
            Glide.with(getActivity())
                    .load("https://cdn.heweather.com/cond_icon/"+forecast.coded+".png")
                    .into(imageView);
            forecast_list_layout.addView(view);
        }
//        if(weather.lifestyleList!=null){
//            aqi_tv.setText(weather.lifestyleList);
//            pm25_tv.setText(weather.aqi.city.pm25);
//        }
        if(weather.lifestyleList!=null){
            comfort_tv.setText("舒适度:"+weather.lifestyleList.get(0).info);
            car_tv.setText("洗车指数:"+weather.lifestyleList.get(6).info);
            sport_tv.setText("运动建议:"+weather.lifestyleList.get(3).info);
            pm25_tv.setText(weather.lifestyleList.get(7).level);
            aqi_tv.setText(weather.lifestyleList.get(7).level);
        }
        scroll_view.setVisibility(View.VISIBLE);
        Intent intent = new Intent(getActivity(), AutoUpdateService.class);
        getActivity().startService(intent);
    }

    private void initBingPic(){
        String bingpic=preferences.getString("bing_pic",null);
        if(bingpic!=null)
            Glide.with(getActivity()).load(bingpic).into(bing_pic_img);
        else
            loadBingPic();
    }
    private void loadBingPic(){
            String bingpicUrl="http://guolin.tech/api/bing_pic";
            HttpUtils.sendOkhttpRequest(bingpicUrl, new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String data = response.body().string();
                    Log.d("-----","BingPic"+data);
                    if(data!=null){
                        editor.putString("bing_pic",data);
                        editor.commit();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Glide.with(getActivity()).load(data).into(bing_pic_img);
                            }
                        });

                    }
                }
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }


            });
    }
    private void setTitle(){
        if(Build.VERSION.SDK_INT>=21){
            View decorView =getActivity().getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void initView(View view) {
        bing_pic_img = (ImageView) view.findViewById(R.id.bing_pic_img);
        weather_back_bt = (Button) view.findViewById(R.id.weather_back_bt);
        title_city_tv = (TextView) view.findViewById(R.id.title_city_tv);
        title_update_time_tv = (TextView) view.findViewById(R.id.title_update_time_tv);
        now_tmp_tv = (TextView) view.findViewById(R.id.now_tmp_tv);
        now_weather_info_tv = (TextView) view.findViewById(R.id.now_weather_info_tv);
        forecast_list_layout = (LinearLayout) view.findViewById(R.id.forecast_list_layout);
        aqi_tv = (TextView) view.findViewById(R.id.aqi_tv);
        pm25_tv = (TextView) view.findViewById(R.id.pm25_tv);
        comfort_tv = (TextView) view.findViewById(R.id.comfort_tv);
        car_tv = (TextView) view.findViewById(R.id.car_tv);
        sport_tv = (TextView) view.findViewById(R.id.sport_tv);
        history_list_layout = (LinearLayout) view.findViewById(R.id.history_list_layout);
        scroll_view = (ScrollView) view.findViewById(R.id.scroll_view);
        swipe_refresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        preferences=getActivity().getSharedPreferences("AreaSave",MODE_MULTI_PROCESS);;
        editor=preferences.edit();
        historyList=new ArrayList<>();
    }


}
