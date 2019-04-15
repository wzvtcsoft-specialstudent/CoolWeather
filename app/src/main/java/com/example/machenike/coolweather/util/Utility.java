package com.example.machenike.coolweather.util;

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
}
