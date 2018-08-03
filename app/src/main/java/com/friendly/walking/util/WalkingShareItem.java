package com.friendly.walking.util;

import android.location.Location;
import android.os.Parcel;
import android.support.annotation.NonNull;

import com.felipecsl.asymmetricgridview.library.model.AsymmetricItem;


public class WalkingShareItem implements AsymmetricItem {
  private int columnSpan;
  private int rowSpan;
  private int position;
  private String nickName;
  private String email;
  private String lat;
  private String lot;
  private String imageUrl;

  public WalkingShareItem() {
    this(1, 1, 0, "", "");
  }

  public WalkingShareItem(int columnSpan, int rowSpan, int position, String nickName, String email) {
    this.columnSpan = columnSpan;
    this.rowSpan = rowSpan;

    this.position = position;
    this.nickName = nickName;
    this.email = email;
  }

  public WalkingShareItem(Parcel in) {
    readFromParcel(in);
  }

  public void setLocation(String lat, String lot) {
    this.lat = lat;
    this.lot = lot;
  }

  public void setImageUrl(String url) {
      imageUrl = url;
  }

  @Override
  public int getColumnSpan() {
    return columnSpan;
  }

  @Override
  public int getRowSpan() {
    return rowSpan;
  }

  public int getPosition() {
    return position;
  }

  public String getNickName() {
      return nickName;
  }

  public String getEmail() {
    return email;
  }

  public Location getLocation() {
    Location location = new Location("");
    location.setLatitude(Double.parseDouble(lat));
    location.setLongitude(Double.parseDouble(lot));

    return location;
  }

    public String getImageUrl() {
        return imageUrl;
    }

  @Override
  public String toString() {
    return String.format("position %s: %s x %s, lat : %s, lot : %s", position, rowSpan, columnSpan, lat, lot);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  private void readFromParcel(Parcel in) {
    columnSpan = in.readInt();
    rowSpan = in.readInt();
    position = in.readInt();
  }

  @Override
  public void writeToParcel(@NonNull Parcel dest, int flags) {
    dest.writeInt(columnSpan);
    dest.writeInt(rowSpan);
    dest.writeInt(position);
  }

  /* Parcelable interface implementation */
  public static final Creator<WalkingShareItem> CREATOR = new Creator<WalkingShareItem>() {
    @Override
    public WalkingShareItem createFromParcel(@NonNull Parcel in) {
      return new WalkingShareItem(in);
    }

    @Override
    @NonNull
    public WalkingShareItem[] newArray(int size) {
      return new WalkingShareItem[size];
    }
  };
}
