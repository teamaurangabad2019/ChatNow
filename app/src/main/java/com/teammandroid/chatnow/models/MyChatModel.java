package com.teammandroid.chatnow.models;

import android.os.Parcel;
import android.os.Parcelable;


public class MyChatModel implements Parcelable {

    String message;
    String profilepic;
    String partnerid;
    String partnerName;
    String senderid;
    String msgTime;
    String firebasePartnerId;
    String clerchatdate;

    int mediaType;
    int isdeleted;


    public MyChatModel() {
    }

    public MyChatModel(String message, String profilepic, String partnerid, String partnerName, String senderid, String msgTime, String firebasePartnerId, String clerchatdate, int mediaType, int isdeleted) {
        this.message = message;
        this.profilepic = profilepic;
        this.partnerid = partnerid;
        this.partnerName = partnerName;
        this.senderid = senderid;
        this.msgTime = msgTime;
        this.firebasePartnerId = firebasePartnerId;
        this.clerchatdate = clerchatdate;
        this.mediaType = mediaType;
        this.isdeleted = isdeleted;
    }

    public MyChatModel(String message, String profilepic, String partnerid, String partnerName, String senderid, String msgTime, String firebasePartnerId, String clerchatdate, int mediaType) {
        this.message = message;
        this.profilepic = profilepic;
        this.partnerid = partnerid;
        this.partnerName = partnerName;
        this.senderid = senderid;
        this.msgTime = msgTime;
        this.firebasePartnerId = firebasePartnerId;
        this.clerchatdate = clerchatdate;
        this.mediaType = mediaType;
    }

    public MyChatModel(String message, String profilepic, String partnerid, String partnerName, String senderid, String msgTime, String firebasePartnerId, String clerchatdate) {
        this.message = message;
        this.profilepic = profilepic;
        this.partnerid = partnerid;
        this.partnerName = partnerName;
        this.senderid = senderid;
        this.msgTime = msgTime;
        this.firebasePartnerId = firebasePartnerId;
        this.clerchatdate = clerchatdate;
    }

    protected MyChatModel(Parcel in) {
        message = in.readString();
        profilepic = in.readString();
        partnerid = in.readString();
        partnerName = in.readString();
        senderid = in.readString();
        msgTime = in.readString();
        firebasePartnerId = in.readString();
        clerchatdate = in.readString();

        mediaType=in.readInt();
        isdeleted=in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeString(profilepic);
        dest.writeString(partnerid);
        dest.writeString(partnerName);
        dest.writeString(senderid);
        dest.writeString(msgTime);
        dest.writeString(firebasePartnerId);
        dest.writeString(clerchatdate);

        dest.writeInt(mediaType);
        dest.writeInt(isdeleted);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MyChatModel> CREATOR = new Creator<MyChatModel>() {
        @Override
        public MyChatModel createFromParcel(Parcel in) {
            return new MyChatModel(in);
        }

        @Override
        public MyChatModel[] newArray(int size) {
            return new MyChatModel[size];
        }
    };

    public String getClerchatdate() {
        return clerchatdate;
    }

    public void setClerchatdate(String clerchatdate) {
        this.clerchatdate = clerchatdate;
    }

    public static Creator<MyChatModel> getCREATOR() {
        return CREATOR;
    }

    public String getFirebasePartnerId() {
        return firebasePartnerId;
    }

    public void setFirebasePartnerId(String firebasePartnerId) {
        this.firebasePartnerId = firebasePartnerId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public int getIsdeleted() {
        return isdeleted;
    }

    public void setIsdeleted(int isdeleted) {
        this.isdeleted = isdeleted;
    }

    @Override
    public String toString() {
        return "MyChatModel{" +
                "message='" + message + '\'' +
                ", profilepic='" + profilepic + '\'' +
                ", partnerid='" + partnerid + '\'' +
                ", partnerName='" + partnerName + '\'' +
                ", senderid='" + senderid + '\'' +
                ", msgTime='" + msgTime + '\'' +
                ", firebasePartnerId='" + firebasePartnerId + '\'' +
                ", clerchatdate='" + clerchatdate + '\'' +
                ", isdeleted='" + isdeleted + '\'' +
                ", mediaType=" + mediaType +
                '}';
    }
}
