package com.teammandroid.chatnow.models;

import android.os.Parcel;
import android.os.Parcelable;

public class MarkerData implements Parcelable {

    int id;
    String name;
    String address;
    double latitude;
    double longitute;
    String profile;


    public MarkerData(int id, String name, String address, double latitude, double longitute, String profile) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitute = longitute;
        this.profile = profile;
    }

    protected MarkerData(Parcel in) {
        id = in.readInt();
        name = in.readString();
        address = in.readString();
        latitude = in.readDouble();
        longitute = in.readDouble();
        profile = in.readString();
    }

    public static final Creator<MarkerData> CREATOR = new Creator<MarkerData>() {
        @Override
        public MarkerData createFromParcel(Parcel in) {
            return new MarkerData(in);
        }

        @Override
        public MarkerData[] newArray(int size) {
            return new MarkerData[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitute() {
        return longitute;
    }

    public void setLongitute(double longitute) {
        this.longitute = longitute;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }


    @Override
    public String toString() {
        return "MarkerData{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", latitude=" + latitude +
                ", longitute=" + longitute +
                ", profile='" + profile + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeDouble(latitude);
        dest.writeDouble(longitute);
        dest.writeString(profile);
    }
}
