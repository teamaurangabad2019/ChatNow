package com.teammandroid.chatnow.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Response implements Parcelable {

    int Result;
    String Message;
    int HasError;

    public Response() {
    }

    public Response(int result, String message, int hasError) {
        Result = result;
        Message = message;
        HasError = hasError;
    }

    protected Response(Parcel in) {
        Result = in.readInt();
        Message = in.readString();
        HasError = in.readInt();
    }

    public static final Creator<Response> CREATOR = new Creator<Response>() {
        @Override
        public Response createFromParcel(Parcel in) {
            return new Response(in);
        }

        @Override
        public Response[] newArray(int size) {
            return new Response[size];
        }
    };

    public int getResult() {
        return Result;
    }

    public void setResult(int result) {
        Result = result;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public int getHasError() {
        return HasError;
    }

    public void setHasError(int hasError) {
        HasError = hasError;
    }

    @Override
    public String toString() {
        return "Response{" +
                "Result=" + Result +
                ", Message='" + Message + '\'' +
                ", HasError=" + HasError +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Result);
        dest.writeString(Message);
        dest.writeInt(HasError);
    }
}
