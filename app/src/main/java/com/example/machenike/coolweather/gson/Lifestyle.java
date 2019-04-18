package com.example.machenike.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Lifestyle {
    public String type;
    @SerializedName("brf")
    public String level;
    @SerializedName("txt")
    public String info;
}
