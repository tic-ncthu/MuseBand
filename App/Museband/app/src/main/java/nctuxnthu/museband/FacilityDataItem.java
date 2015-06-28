package nctuxnthu.museband;

import java.util.Objects;
/**
 * Created by ¨äÝÂ on 2015/6/28.
 */

public class FacilityDataItem {

    @com.google.gson.annotations.SerializedName("id")
    public String Id;
    @com.google.gson.annotations.SerializedName("status")
    public String status;
    @com.google.gson.annotations.SerializedName("latitude")
    public Number latitude;
    @com.google.gson.annotations.SerializedName("longtitude")
    public Number longtitude;
    @com.google.gson.annotations.SerializedName("people")
    public Number people;
    @com.google.gson.annotations.SerializedName("time")
    public Number time;

    public FacilityDataItem() {
        status = "0";
        latitude = 0;
        longtitude = 0;
        people = 0;
        time = 0;
    }
}

