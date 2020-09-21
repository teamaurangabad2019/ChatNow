package com.teammandroid.chatnow.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ChatModel implements Parcelable {

    int chattingid;
    int senderid;
    int receiverid;
    String messagess;
    String isactive;
    String created;
    String createdby;
    String modified;
    String modifiedby;

    private boolean isSelected = false;


    public ChatModel(int chattingid, int senderid, int receiverid, String messagess, String isactive, String created, String createdby, String modified, String modifiedby) {
        this.chattingid = chattingid;
        this.senderid = senderid;
        this.receiverid = receiverid;
        this.messagess = messagess;
        this.isactive = isactive;
        this.created = created;
        this.createdby = createdby;
        this.modified = modified;
        this.modifiedby = modifiedby;
    }



    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public boolean isSelected() {
        return isSelected;
    }

    protected ChatModel(Parcel in) {
        chattingid = in.readInt();
        senderid = in.readInt();
        receiverid = in.readInt();
        messagess = in.readString();
        isactive = in.readString();
        created = in.readString();
        createdby = in.readString();
        modified = in.readString();
        modifiedby = in.readString();
    }

    public static final Creator<ChatModel> CREATOR = new Creator<ChatModel>() {
        @Override
        public ChatModel createFromParcel(Parcel in) {
            return new ChatModel(in);
        }

        @Override
        public ChatModel[] newArray(int size) {
            return new ChatModel[size];
        }
    };

    public int getChattingid() {
        return chattingid;
    }

    public void setChattingid(int chattingid) {
        this.chattingid = chattingid;
    }

    public int getSenderid() {
        return senderid;
    }

    public void setSenderid(int senderid) {
        this.senderid = senderid;
    }

    public int getReceiverid() {
        return receiverid;
    }

    public void setReceiverid(int receiverid) {
        this.receiverid = receiverid;
    }

    public String getMessagess() {
        return messagess;
    }

    public void setMessagess(String messagess) {
        this.messagess = messagess;
    }

    public String getIsactive() {
        return isactive;
    }

    public void setIsactive(String isactive) {
        this.isactive = isactive;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getCreatedby() {
        return createdby;
    }

    public void setCreatedby(String createdby) {
        this.createdby = createdby;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getModifiedby() {
        return modifiedby;
    }

    public void setModifiedby(String modifiedby) {
        this.modifiedby = modifiedby;
    }

    @Override
    public String toString() {
        return "ChatModel{" +
                "chattingid=" + chattingid +
                ", senderid=" + senderid +
                ", receiverid=" + receiverid +
                ", message='" + messagess + '\'' +
                ", isactive='" + isactive + '\'' +
                ", created='" + created + '\'' +
                ", createdby='" + createdby + '\'' +
                ", modified='" + modified + '\'' +
                ", modifiedby='" + modifiedby + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(chattingid);
        dest.writeInt(senderid);
        dest.writeInt(receiverid);
        dest.writeString(messagess);
        dest.writeString(isactive);
        dest.writeString(created);
        dest.writeString(createdby);
        dest.writeString(modified);
        dest.writeString(modifiedby);
    }
}
