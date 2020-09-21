package com.teammandroid.chatnow.models;

import android.os.Parcel;
import android.os.Parcelable;

public class GrpNotificationModel implements Parcelable {
    int messageid;
    String firebaseid;
    int senderid;
    String message;
    String msgtime;
    int isread;
    int mediatype;  //1 for image   //2 for gif  //3 for audio  //4 for video  //5 for documents  //6 for url  //7 for text
    int type;
    String gruoupid;

    private boolean isSelected = false;

    public GrpNotificationModel(int messageid, String firebaseid, int senderid, String message, String msgtime, int isread, int mediatype, int type, String gruoupid, boolean isSelected) {
        this.messageid = messageid;
        this.firebaseid = firebaseid;
        this.senderid = senderid;
        this.message = message;
        this.msgtime = msgtime;
        this.isread = isread;
        this.mediatype = mediatype;
        this.type = type;
        this.gruoupid = gruoupid;
        this.isSelected = isSelected;
    }

    protected GrpNotificationModel(Parcel in) {
        messageid = in.readInt();
        firebaseid = in.readString();
        senderid = in.readInt();
        message = in.readString();
        msgtime = in.readString();
        isread = in.readInt();
        mediatype = in.readInt();
        type = in.readInt();
        gruoupid = in.readString();
        isSelected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(messageid);
        dest.writeString(firebaseid);
        dest.writeInt(senderid);
        dest.writeString(message);
        dest.writeString(msgtime);
        dest.writeInt(isread);
        dest.writeInt(mediatype);
        dest.writeInt(type);
        dest.writeString(gruoupid);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GrpNotificationModel> CREATOR = new Creator<GrpNotificationModel>() {
        @Override
        public GrpNotificationModel createFromParcel(Parcel in) {
            return new GrpNotificationModel(in);
        }

        @Override
        public GrpNotificationModel[] newArray(int size) {
            return new GrpNotificationModel[size];
        }
    };

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public boolean isSelected() {
        return isSelected;
    }


    public GrpNotificationModel() {

    }

    public String getGruoupid() {
        return gruoupid;
    }

    public void setGruoupid(String gruoupid) {
        this.gruoupid = gruoupid;
    }

    public int getMessageid() {
        return messageid;
    }

    public void setMessageid(int messageid) {
        this.messageid = messageid;
    }

    public String getFirebaseid() {
        return firebaseid;
    }

    public void setFirebaseid(String firebaseid) {
        this.firebaseid = firebaseid;
    }

    public int getSenderid() {
        return senderid;
    }

    public void setSenderid(int senderid) {
        this.senderid = senderid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMsgtime() {
        return msgtime;
    }

    public void setMsgtime(String msgtime) {
        this.msgtime = msgtime;
    }

    public int getIsread() {
        return isread;
    }

    public void setIsread(int isread) {
        this.isread = isread;
    }

    public int getMediatype() {
        return mediatype;
    }

    public void setMediatype(int mediatype) {
        this.mediatype = mediatype;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
