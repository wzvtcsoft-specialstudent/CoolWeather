package com.example.machenike.coolweather.util;

import com.example.machenike.coolweather.db.City;
import com.example.machenike.coolweather.db.County;
import com.example.machenike.coolweather.db.Province;

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
}
