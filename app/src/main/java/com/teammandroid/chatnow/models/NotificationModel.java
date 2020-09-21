package com.teammandroid.chatnow.models;

import android.os.Parcel;
import android.os.Parcelable;

public class NotificationModel implements Parcelable {
    int messageid;
    String firebaseid;
    int receiverid;
    int senderid;
    String message;
    String msgtime;
    int isread;
    int isdownloaded;
    int mediatype;  //1 for image   //2 for gif  //3 for audio  //4 for video  //5 for documents  //6 for url  //7 for text
    int type;
    int replyToMsgId;
    int replyToMsgSenderId;
    String replyToMsgText;
    int replyToMsgTextMediaType;

    private boolean isSelected = false;

    public NotificationModel(int messageid, String firebaseid, int receiverid, int senderid, String message, String msgtime, int isread, int isdownloaded, int mediatype, int type, int replyToMsgId, int replyToMsgSenderId, String replyToMsgText, int replyToMsgTextMediaType, boolean isSelected) {
        this.messageid = messageid;
        this.firebaseid = firebaseid;
        this.receiverid = receiverid;
        this.senderid = senderid;
        this.message = message;
        this.msgtime = msgtime;
        this.isread = isread;
        this.isdownloaded = isdownloaded;
        this.mediatype = mediatype;
        this.type = type;
        this.replyToMsgId = replyToMsgId;
        this.replyToMsgSenderId = replyToMsgSenderId;
        this.replyToMsgText = replyToMsgText;
        this.replyToMsgTextMediaType = replyToMsgTextMediaType;
        this.isSelected = isSelected;
    }


    protected NotificationModel(Parcel in) {
        messageid = in.readInt();
        firebaseid = in.readString();
        receiverid = in.readInt();
        senderid = in.readInt();
        message = in.readString();
        msgtime = in.readString();
        isread = in.readInt();
        isdownloaded = in.readInt();
        mediatype = in.readInt();
        type = in.readInt();
        replyToMsgId = in.readInt();
        replyToMsgSenderId = in.readInt();
        replyToMsgText = in.readString();
        replyToMsgTextMediaType = in.readInt();
        isSelected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(messageid);
        dest.writeString(firebaseid);
        dest.writeInt(receiverid);
        dest.writeInt(senderid);
        dest.writeString(message);
        dest.writeString(msgtime);
        dest.writeInt(isread);
        dest.writeInt(isdownloaded);
        dest.writeInt(mediatype);
        dest.writeInt(type);
        dest.writeInt(replyToMsgId);
        dest.writeInt(replyToMsgSenderId);
        dest.writeString(replyToMsgText);
        dest.writeInt(replyToMsgTextMediaType);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NotificationModel> CREATOR = new Creator<NotificationModel>() {
        @Override
        public NotificationModel createFromParcel(Parcel in) {
            return new NotificationModel(in);
        }

        @Override
        public NotificationModel[] newArray(int size) {
            return new NotificationModel[size];
        }
    };

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public String toString() {
        return "NotificationModel{" +
                "messageid=" + messageid +
                ", firebaseid=" + firebaseid +
                ", receiverid=" + receiverid +
                ", senderid=" + senderid +
                ", message='" + message + '\'' +
                ", msgtime='" + msgtime + '\'' +
                ", isread=" + isread +
                ", isdownloaded=" + isdownloaded +
                ", mediatype=" + mediatype +
                ", type=" + type +
                ", replyToMsgId=" + replyToMsgId +
                ", replyToMsgSenderId=" + replyToMsgSenderId +
                ", replyToMsgText='" + replyToMsgText + '\'' +
                ", replyToMsgTextMediaType=" + replyToMsgTextMediaType +
                ", isSelected=" + isSelected +
                '}';
    }

    public NotificationModel() {

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

    public int getReceiverid() {
        return receiverid;
    }

    public void setReceiverid(int receiverid) {
        this.receiverid = receiverid;
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

    public int getIsdownloaded() {
        return isdownloaded;
    }

    public void setIsdownloaded(int isdownloaded) {
        this.isdownloaded = isdownloaded;
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

    public int getReplyToMsgId() {
        return replyToMsgId;
    }

    public void setReplyToMsgId(int replyToMsgId) {
        this.replyToMsgId = replyToMsgId;
    }

    public int getReplyToMsgSenderId() {
        return replyToMsgSenderId;
    }

    public void setReplyToMsgSenderId(int replyToMsgSenderId) {
        this.replyToMsgSenderId = replyToMsgSenderId;
    }

    public String getReplyToMsgText() {
        return replyToMsgText;
    }

    public void setReplyToMsgText(String replyToMsgText) {
        this.replyToMsgText = replyToMsgText;
    }

    public int getReplyToMsgTextMediaType() {
        return replyToMsgTextMediaType;
    }

    public void setReplyToMsgTextMediaType(int replyToMsgTextMediaType) {
        this.replyToMsgTextMediaType = replyToMsgTextMediaType;
    }
}
