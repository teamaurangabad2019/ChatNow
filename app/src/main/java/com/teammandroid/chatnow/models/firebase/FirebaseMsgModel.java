package com.teammandroid.chatnow.models.firebase;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class FirebaseMsgModel implements Parcelable {

    int isdownloaded;
    int isread;
    int mediatype;   //1 for image   //2 for gif  //3 for audio  //4 for video  //5 for documents  //6 for url  //7 for text //8 contacts
    String  message;
    String messageid;
    String msgtime;
    int receiverid;
    String replyToMsgId;
    int replyToMsgSenderId;
    String  replyToMsgText;
    int replyToMsgTextMediaType;
    int senderid;
    int type;
    int isdeleted;
    int isForward;
    String msgStatus;

    ArrayList<FirebaseContactResult> contacts;

    private boolean isSelected = false;

    public FirebaseMsgModel(int isdownloaded, int isread, int mediatype, String message, String messageid, String msgtime, int receiverid, String replyToMsgId, int replyToMsgSenderId, String replyToMsgText, int replyToMsgTextMediaType, int senderid, int type, int isdeleted, int isForward, String msgStatus, ArrayList<FirebaseContactResult> contacts, boolean isSelected) {
        this.isdownloaded = isdownloaded;
        this.isread = isread;
        this.mediatype = mediatype;
        this.message = message;
        this.messageid = messageid;
        this.msgtime = msgtime;
        this.receiverid = receiverid;
        this.replyToMsgId = replyToMsgId;
        this.replyToMsgSenderId = replyToMsgSenderId;
        this.replyToMsgText = replyToMsgText;
        this.replyToMsgTextMediaType = replyToMsgTextMediaType;
        this.senderid = senderid;
        this.type = type;
        this.isdeleted = isdeleted;
        this.isForward = isForward;
        this.msgStatus = msgStatus;
        this.contacts = contacts;
        this.isSelected = isSelected;
    }

    public FirebaseMsgModel() {

    }

    protected FirebaseMsgModel(Parcel in) {
        isdownloaded = in.readInt();
        isread = in.readInt();
        mediatype = in.readInt();
        message = in.readString();
        messageid = in.readString();
        msgtime = in.readString();
        receiverid = in.readInt();
        replyToMsgId = in.readString();
        replyToMsgSenderId = in.readInt();
        replyToMsgText = in.readString();
        replyToMsgTextMediaType = in.readInt();
        senderid = in.readInt();
        type = in.readInt();
        isdeleted = in.readInt();
        isForward = in.readInt();
        msgStatus = in.readString();
        contacts = in.createTypedArrayList(FirebaseContactResult.CREATOR);
        isSelected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(isdownloaded);
        dest.writeInt(isread);
        dest.writeInt(mediatype);
        dest.writeString(message);
        dest.writeString(messageid);
        dest.writeString(msgtime);
        dest.writeInt(receiverid);
        dest.writeString(replyToMsgId);
        dest.writeInt(replyToMsgSenderId);
        dest.writeString(replyToMsgText);
        dest.writeInt(replyToMsgTextMediaType);
        dest.writeInt(senderid);
        dest.writeInt(type);
        dest.writeInt(isdeleted);
        dest.writeInt(isForward);
        dest.writeString(msgStatus);
        dest.writeTypedList(contacts);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FirebaseMsgModel> CREATOR = new Creator<FirebaseMsgModel>() {
        @Override
        public FirebaseMsgModel createFromParcel(Parcel in) {
            return new FirebaseMsgModel(in);
        }

        @Override
        public FirebaseMsgModel[] newArray(int size) {
            return new FirebaseMsgModel[size];
        }
    };

    public String getMsgStatus() {
        return msgStatus;
    }

    public void setMsgStatus(String msgStatus) {
        this.msgStatus = msgStatus;
    }

    public ArrayList<FirebaseContactResult> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<FirebaseContactResult> contacts) {
        this.contacts = contacts;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getIsdeleted() {
        return isdeleted;
    }

    public void setIsdeleted(int isdeleted) {
        this.isdeleted = isdeleted;
    }

    public static Creator<FirebaseMsgModel> getCREATOR() {
        return CREATOR;
    }

    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public String toString() {
        return "FirebaseMsgModel{" +
                "isdownloaded=" + isdownloaded +
                ", isread=" + isread +
                ", mediatype=" + mediatype +
                ", message='" + message + '\'' +
                ", messageid='" + messageid + '\'' +
                ", msgtime='" + msgtime + '\'' +
                ", receiverid=" + receiverid +
                ", replyToMsgId='" + replyToMsgId + '\'' +
                ", replyToMsgSenderId=" + replyToMsgSenderId +
                ", replyToMsgText='" + replyToMsgText + '\'' +
                ", replyToMsgTextMediaType=" + replyToMsgTextMediaType +
                ", senderid=" + senderid +
                ", type=" + type +
                ", isdeleted=" + isdeleted +
                ", isSelected=" + isSelected +
                ", isSelected=" + isForward +
                '}';
    }

    public int getIsForward() {
        return isForward;
    }

    public void setIsForward(int isForward) {
        this.isForward = isForward;
    }

    public int getIsdownloaded() {
        return isdownloaded;
    }

    public void setIsdownloaded(int isdownloaded) {
        this.isdownloaded = isdownloaded;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageid() {
        return messageid;
    }

    public void setMessageid(String messageid) {
        this.messageid = messageid;
    }

    public String getMsgtime() {
        return msgtime;
    }

    public void setMsgtime(String msgtime) {
        this.msgtime = msgtime;
    }

    public int getReceiverid() {
        return receiverid;
    }

    public void setReceiverid(int receiverid) {
        this.receiverid = receiverid;
    }

    public String getReplyToMsgId() {
        return replyToMsgId;
    }

    public void setReplyToMsgId(String replyToMsgId) {
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

    public int getSenderid() {
        return senderid;
    }

    public void setSenderid(int senderid) {
        this.senderid = senderid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
