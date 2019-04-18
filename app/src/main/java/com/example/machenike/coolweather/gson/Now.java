package com.example.machenike.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Now {
    @SerializedName("tmp")
    public String temperature;
    @SerializedName("cond_txt")
    public String info;
    @SerializedName("cond_code")
    public String code;
    @SerializedName("hum")
    public String humidity;

//    @SerializedName("tmp")
//    public String temperature;
//    @SerializedName("cond_txt")
//    public String info;
//    @SerializedName("cond_code")
//    public String code;
//    @SerializedName("cond")
//    public More more;
//    public class More{
//        @SerializedName("txt")
//        public String info;
//    }

}
