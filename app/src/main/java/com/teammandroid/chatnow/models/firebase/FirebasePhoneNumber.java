package com.teammandroid.chatnow.models.firebase;

import android.os.Parcel;
import android.os.Parcelable;

import com.wafflecopter.multicontactpicker.RxContacts.PhoneNumber;

public class FirebasePhoneNumber implements Parcelable {

    private String typeLabel;
    private String number;

    public FirebasePhoneNumber() {
    }

    FirebasePhoneNumber(String typeLabel, String number) {
        this.typeLabel = typeLabel;
        this.number = number;
    }

    private FirebasePhoneNumber(Parcel in) {
        this.typeLabel = in.readString();
        this.number = in.readString();
    }

    public String getTypeLabel() {
        return typeLabel;
    }

    public void setTypeLabel(String typeLabel) {
        this.typeLabel = typeLabel;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.typeLabel);
        dest.writeString(this.number);
    }

    public static final Creator<FirebasePhoneNumber> CREATOR = new Creator<FirebasePhoneNumber>() {
        @Override
        public FirebasePhoneNumber createFromParcel(Parcel in) {
            return new FirebasePhoneNumber(in);
        }

        @Override
        public FirebasePhoneNumber[] newArray(int size) {
            return new FirebasePhoneNumber[size];
        }
    };
}
