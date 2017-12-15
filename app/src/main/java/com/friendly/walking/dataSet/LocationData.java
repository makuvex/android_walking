package com.friendly.walking.dataSet;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017-10-22.
 */

public class LocationData {

    public double latitude = 0;
    public double longtitude = 0;
    public long date = 0;

    public LocationData() {}

    public LocationData(long date, double latitude, double longtitude) {
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.date = date;
    }

    @Override
    public String toString() {
        Date now = new Date(date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");
        String getTime = sdf.format(now);

        return "getTime :"+getTime+", lat :"+latitude+", lot :"+longtitude;
    }
}
