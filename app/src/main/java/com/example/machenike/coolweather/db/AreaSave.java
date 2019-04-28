package com.example.machenike.coolweather.db;

import org.litepal.crud.DataSupport;

public class AreaSave extends DataSupport {
    String weatherID;
    String cityName;
    String weatherData;

    public String getWeatherData() {
        return weatherData;
    }

    public void setWeatherData(String weatherData) {
        this.weatherData = weatherData;
    }

    public String getWeatherID() {
        return weatherID;
    }

    public void setWeatherID(String weatherID) {
        this.weatherID = weatherID;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
