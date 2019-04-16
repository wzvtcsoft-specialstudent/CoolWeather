package com.example.machenike.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Suggestion {
    @SerializedName("comf")
    public Comfortable comfortable;
    @SerializedName("cw")
    public Car car;

    public Sport sport;

    public class Comfortable{
        @SerializedName("brf")
        public String level;
        @SerializedName("txt")
        public String info;
    }
    public class Sport{
        @SerializedName("brf")
        public String level;
        @SerializedName("txt")
        public String info;
    }
    public class Car{
        @SerializedName("brf")
        public String level;
        @SerializedName("txt")
        public String info;
    }
}
