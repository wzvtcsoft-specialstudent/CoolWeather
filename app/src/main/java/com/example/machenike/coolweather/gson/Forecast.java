package com.example.machenike.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Forecast {
    public String date;
    @SerializedName("tmp_max")
    public String max;
    @SerializedName("tmp_min")
    public String min;
    @SerializedName("cond_txt_d")
    public String info;
    @SerializedName("cond_txt_n")
    public String info_n;
    @SerializedName("cond_code_d")
    public String coded;
    @SerializedName("cond_code_n")
    public String coden;
//    public String date;
//    @SerializedName("tmp")
//    public Temperature temperature;
//    @SerializedName("cond")
//    public More more;
//    public class Temperature{
//        public String max;
//        public String min;
//    }
//    public class More{
//        @SerializedName("txt_d")
//        public String info;
//    }
}
