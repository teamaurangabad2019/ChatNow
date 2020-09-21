package com.teammandroid.chatnow.models;

import android.os.Parcel;
import android.os.Parcelable;


public class SettingsModel implements Parcelable {

    String settingsName;
    String settingsDescription;


    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(settingsName);
        dest.writeString(settingsDescription);

    }


    public SettingsModel(String settingsName, String settingsDescription) {
        this.settingsName = settingsName;
        this.settingsDescription = settingsDescription;
    }

    public String getSettingsDescription() {
        return settingsDescription;
    }

    public void setSettingsDescription(String settingsDescription) {
        this.settingsDescription = settingsDescription;
    }

    public SettingsModel(String settingsName) {
        this.settingsName=settingsName;
    }


    protected SettingsModel(Parcel in) {

        settingsName = in.readString();
        settingsDescription=in.readString();
    }

    public String getSettingsName() {
        return settingsName;
    }

    public void setSettingsName(String settingsName) {
        this.settingsName = settingsName;
    }

    public static final Creator<SettingsModel> CREATOR = new Creator<SettingsModel>() {
        @Override
        public SettingsModel createFromParcel(Parcel in) {
            return new SettingsModel(in);
        }

        @Override
        public SettingsModel[] newArray(int size) {
            return new SettingsModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

}
