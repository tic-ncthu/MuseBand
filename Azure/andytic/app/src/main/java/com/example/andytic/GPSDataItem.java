package com.example.andytic;

/**
 * Created by andy on 15/6/11.
 */
public class GPSDataItem {
    @com.google.gson.annotations.SerializedName("id")
    public String Id;
    @com.google.gson.annotations.SerializedName("latitude")
    public Number gps_latitude;
    @com.google.gson.annotations.SerializedName("longtitude")
    public Number gps_longtitude;
}
