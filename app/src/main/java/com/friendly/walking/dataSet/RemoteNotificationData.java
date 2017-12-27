package com.friendly.walking.dataSet;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

/**
 * Created by jungjiwon on 2017. 12. 27..
 */

public class RemoteNotificationData implements Parcelable {
    public String code = "";
    public String message = "";
    public String title = "";
    public String link = "";

    public RemoteNotificationData() {}

    public RemoteNotificationData(Parcel in) {
        readFromParcel(in);
    }

    public RemoteNotificationData(Map<String, String> data) {
        this.code = data.get("code");
        this.message = data.get("message");
        this.title = data.get("title");
        this.link = data.get("link");
    }

    public RemoteNotificationData(String code, String message, String title, String link) {
        this.code = code;
        this.message = message;
        this.title = title;
        this.link = link;
    }

    @Override
    public String toString() {
        return "code :"+code+", message :"+message+", title :"+title+", link :"+link;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(code);
        parcel.writeString(code);
        parcel.writeString(title);
        parcel.writeString(link);
    }

    private void readFromParcel(Parcel in){
        code = in.readString();
        message = in.readString();
        title = in.readString();
        link = in.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public RemoteNotificationData createFromParcel(Parcel in) {
            return new RemoteNotificationData(in);
        }

        public RemoteNotificationData[] newArray(int size) {
            return new RemoteNotificationData[size];
        }
    };
}
