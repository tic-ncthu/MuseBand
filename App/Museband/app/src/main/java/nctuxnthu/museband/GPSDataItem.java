package nctuxnthu.museband;

import java.util.Objects;

/**
 * Created by ¨äÝÂ on 2015/6/17.
 */
public class GPSDataItem {

    @com.google.gson.annotations.SerializedName("id")
    public String gps_Id;
    @com.google.gson.annotations.SerializedName("status")
    public String gps_status;
    @com.google.gson.annotations.SerializedName("latitude")
    public Number gps_latitude;
    @com.google.gson.annotations.SerializedName("longtitude")
    public Number gps_longtitude;

}
