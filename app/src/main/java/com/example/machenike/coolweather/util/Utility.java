package com.example.machenike.coolweather.util;

import com.example.machenike.coolweather.db.City;
import com.example.machenike.coolweather.db.County;
import com.example.machenike.coolweather.db.History;
import com.example.machenike.coolweather.db.Province;
import com.example.machenike.coolweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {
    public static boolean saveProvince(String data){
        try {
            JSONArray array = new JSONArray(data);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                Province province = new Province();
                province.setName(object.getString("name"));
                province.setCode(object.getInt("id"));
                province.save();
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
            return false;
    }
    public static boolean saveCity(String data,int provinceid){
        try {
            JSONArray array = new JSONArray(data);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                City city = new City();
                city.setName(object.getString("name"));
                city.setCode(object.getInt("id"));
                city.setProvinceCode(provinceid);
                city.save();
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean saveCounty(String data,int cityid){
        try {
            JSONArray array = new JSONArray(data);
            for (int i = 0; i <array.length() ; i++) {
                JSONObject object = array.getJSONObject(i);
                County county = new County();
                county.setName(object.getString("name"));
                county.setCityCode(cityid);
                county.setWeatherId(object.getString("weather_id"));
                county.save();
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }
    /**
     * 将返回的数据解析成Weather实体类
     */
    public static Weather handleWeatherResponse(String response){
        try {
            JSONObject object = new JSONObject(response);
            JSONArray array = object.getJSONArray("HeWeather6");
            String weatherdata = array.getJSONObject(0).toString();
            return new Gson().fromJson(weatherdata,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  null;
    }
    public static boolean handleHistoryResponse(String response){
        try {
            JSONObject object  = new JSONObject(response);
            JSONArray array = object.getJSONArray("result");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object1 = array.getJSONObject(i);
                History history = new History();
                String year=object1.getString("year");
                char first = year.charAt(0);

                if(first=='-') {
                    year="公元前 "+year.replace("-"," ");
                }else{
                    year="公元 "+year;
                }
                history.setYear(year);
                history.setInfo(object1.getString("title"));
                history.save();
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }
}
