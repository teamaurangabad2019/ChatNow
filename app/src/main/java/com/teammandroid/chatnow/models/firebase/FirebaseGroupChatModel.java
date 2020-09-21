package com.teammandroid.chatnow.models.firebase;

import android.os.Parcel;
import android.os.Parcelable;

import com.teammandroid.chatnow.models.NotificationModel;

import java.util.ArrayList;

public class FirebaseGroupChatModel implements Parcelable {

    private String sender,message,msgTime,msgUid,msgType,groupId,msgDate;

    String replyToMsgId;
    String replyToMsgSenderId;
    String replyToMsgText;
    int replyToMsgTextMediaType;

    int type;
    int isread;
    int isdownloaded;
    int mediatype;  //1 for image   //2 for gif  //3 for audio  //4 for video  //5 for documents  //6 for url  //7 for text

    String senderName;
    String msgStatus;
    int isForward;
    private boolean isSelected = false;


    int isDeleted;

    ArrayList<FirebaseContactResult> contacts;

    public FirebaseGroupChatModel() {
    }

    public FirebaseGroupChatModel(String sender, String message, String msgTime, String msgUid, String msgType, String groupId, String msgDate, String replyToMsgId, String replyToMsgSenderId, String replyToMsgText, int replyToMsgTextMediaType, int type, int isread, int isdownloaded, int mediatype, String senderName, String msgStatus, int isForward, boolean isSelected, int isDeleted, ArrayList<FirebaseContactResult> contacts) {
        this.sender = sender;
        this.message = message;
        this.msgTime = msgTime;
        this.msgUid = msgUid;
        this.msgType = msgType;
        this.groupId = groupId;
        this.msgDate = msgDate;
        this.replyToMsgId = replyToMsgId;
        this.replyToMsgSenderId = replyToMsgSenderId;
        this.replyToMsgText = replyToMsgText;
        this.replyToMsgTextMediaType = replyToMsgTextMediaType;
        this.type = type;
        this.isread = isread;
        this.isdownloaded = isdownloaded;
        this.mediatype = mediatype;
        this.senderName = senderName;
        this.msgStatus = msgStatus;
        this.isForward = isForward;
        this.isSelected = isSelected;
        this.isDeleted = isDeleted;
        this.contacts = contacts;
    }

    protected FirebaseGroupChatModel(Parcel in) {
        sender = in.readString();
        message = in.readString();
        msgTime = in.readString();
        msgUid = in.readString();
        msgType = in.readString();
        groupId = in.readString();
        msgDate = in.readString();
        replyToMsgId = in.readString();
        replyToMsgSenderId = in.readString();
        replyToMsgText = in.readString();
        replyToMsgTextMediaType = in.readInt();
        type = in.readInt();
        isread = in.readInt();
        isdownloaded = in.readInt();
        mediatype = in.readInt();
        senderName = in.readString();
        msgStatus = in.readString();
        isForward = in.readInt();
        isSelected = in.readByte() != 0;
        contacts = in.createTypedArrayList(FirebaseContactResult.CREATOR);

        isDeleted=in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sender);
        dest.writeString(message);
        dest.writeString(msgTime);
        dest.writeString(msgUid);
        dest.writeString(msgType);
        dest.writeString(groupId);
        dest.writeString(msgDate);
        dest.writeString(replyToMsgId);
        dest.writeString(replyToMsgSenderId);
        dest.writeString(replyToMsgText);
        dest.writeInt(replyToMsgTextMediaType);
        dest.writeInt(type);
        dest.writeInt(isread);
        dest.writeInt(isdownloaded);
        dest.writeInt(mediatype);
        dest.writeString(senderName);
        dest.writeString(msgStatus);
        dest.writeInt(isForward);
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeTypedList(contacts);

        dest.writeInt(isDeleted);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FirebaseGroupChatModel> CREATOR = new Creator<FirebaseGroupChatModel>() {
        @Override
        public FirebaseGroupChatModel createFromParcel(Parcel in) {
            return new FirebaseGroupChatModel(in);
        }

        @Override
        public FirebaseGroupChatModel[] newArray(int size) {
            return new FirebaseGroupChatModel[size];
        }
    };

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public ArrayList<FirebaseContactResult> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<FirebaseContactResult> contacts) {
        this.contacts = contacts;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }

    public String getMsgUid() {
        return msgUid;
    }

    public void setMsgUid(String msgUid) {
        this.msgUid = msgUid;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "FirebaseGroupChatModel{" +
                "sender='" + sender + '\'' +
                ", message='" + message + '\'' +
                ", msgTime='" + msgTime + '\'' +
                ", msgUid='" + msgUid + '\'' +
                ", msgType='" + msgType + '\'' +
                ", groupId='" + groupId + '\'' +
                ", msgDate='" + msgDate + '\'' +
                ", replyToMsgId='" + replyToMsgId + '\'' +
                ", replyToMsgSenderId='" + replyToMsgSenderId + '\'' +
                ", replyToMsgText='" + replyToMsgText + '\'' +
                ", replyToMsgTextMediaType=" + replyToMsgTextMediaType +
                ", type=" + type +
                ", isread=" + isread +
                ", isdownloaded=" + isdownloaded +
                ", mediatype=" + mediatype +
                ", senderName='" + senderName + '\'' +
                ", msgStatus='" + msgStatus + '\'' +
                ", isForward=" + isForward +
                ", isSelected=" + isSelected +
                ", isDeleted=" + isDeleted +
                ", contacts=" + contacts +
                '}';
    }

    public String getMsgStatus() {
        return msgStatus;
    }

    public void setMsgStatus(String msgStatus) {
        this.msgStatus = msgStatus;
    }

    public int getIsForward() {
        return isForward;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setIsForward(int isForward) {
        this.isForward = isForward;
    }

    public String getReplyToMsgId() {
        return replyToMsgId;
    }

    public void setReplyToMsgId(String replyToMsgId) {
        this.replyToMsgId = replyToMsgId;
    }

    public String getReplyToMsgSenderId() {
        return replyToMsgSenderId;
    }

    public void setReplyToMsgSenderId(String replyToMsgSenderId) {
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getMsgDate() {
        return msgDate;
    }

    public void setMsgDate(String msgDate) {
        this.msgDate = msgDate;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }
}

