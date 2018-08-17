package com.friendly.walking.dataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Administrator on 2017-10-22.
 */

public class StrollTimeData {

    public String day;
    public String min;
    public ArrayList<LocationData> locationList;

    public StrollTimeData() {}

    public StrollTimeData(String day, String min) {
        this.day = day;
        this.min = min;
    }

    public StrollTimeData(String day, String min, ArrayList<LocationData> list) {
        this.day = day;
        this.min = min;
        this.locationList = list;
    }

    @Override
    public String toString() {
        return "day :"+day+", min :"+min+", locationList "+locationList;
    }
}
