package com.example.machenike.coolweather.db;

import org.litepal.crud.DataSupport;

public class History extends DataSupport {
    String year;
    String info;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
