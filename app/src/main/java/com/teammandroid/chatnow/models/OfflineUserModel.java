package com.teammandroid.chatnow.models;

import android.os.Parcel;
import android.os.Parcelable;

public class OfflineUserModel implements Parcelable {

    int uid;
    int userid;
    String fullname;
    int roleid;
    String address;
    String mobile;
    String token;
    String email;
    String profilepic;
    double latitute;
    double longitude;
    String device;
    int isactive;
    String created;
    int createdby;
    String modified;
    int modifiedby;


    public OfflineUserModel() {
    }

    public OfflineUserModel(int uid, int userid, String fullname, int roleid, String address, String mobile, String token, String email,  String profilepic, double latitute, double longitude, String device, int isactive, String created, int createdby, String modified, int modifiedby) {
        this.uid = uid;
        this.userid = userid;
        this.fullname = fullname;
        this.roleid = roleid;
        this.address = address;
        this.mobile = mobile;
        this.token = token;
        this.email = email;
        this.profilepic = profilepic;
        this.latitute = latitute;
        this.longitude = longitude;
        this.device = device;
        this.isactive = isactive;
        this.created = created;
        this.createdby = createdby;
        this.modified = modified;
        this.modifiedby = modifiedby;
    }

    protected OfflineUserModel(Parcel in) {
        uid = in.readInt();
        userid = in.readInt();
        fullname = in.readString();
        roleid = in.readInt();
        address = in.readString();
        mobile = in.readString();
        token = in.readString();
        email = in.readString();
        profilepic = in.readString();
        latitute = in.readDouble();
        longitude = in.readDouble();
        device = in.readString();
        isactive = in.readInt();
        created = in.readString();
        createdby = in.readInt();
        modified = in.readString();
        modifiedby = in.readInt();
    }

    public static final Creator<OfflineUserModel> CREATOR = new Creator<OfflineUserModel>() {
        @Override
        public OfflineUserModel createFromParcel(Parcel in) {
            return new OfflineUserModel(in);
        }

        @Override
        public OfflineUserModel[] newArray(int size) {
            return new OfflineUserModel[size];
        }
    };

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public int getRoleid() {
        return roleid;
    }

    public void setRoleid(int roleid) {
        this.roleid = roleid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public double getLatitute() {
        return latitute;
    }

    public void setLatitute(double latitute) {
        this.latitute = latitute;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public int getIsactive() {
        return isactive;
    }

    public void setIsactive(int isactive) {
        this.isactive = isactive;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public int getCreatedby() {
        return createdby;
    }

    public void setCreatedby(int createdby) {
        this.createdby = createdby;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public int getModifiedby() {
        return modifiedby;
    }

    public void setModifiedby(int modifiedby) {
        this.modifiedby = modifiedby;
    }

    @Override
    public String toString() {
        return "OfflineUserModel{" +
                "uid=" + uid +
                ", userid=" + userid +
                ", fullname='" + fullname + '\'' +
                ", roleid=" + roleid +
                ", address='" + address + '\'' +
                ", mobile='" + mobile + '\'' +
                ", token='" + token + '\'' +
                ", email='" + email + '\'' +
                ", profilepic='" + profilepic + '\'' +
                ", latitute=" + latitute +
                ", longitude=" + longitude +
                ", device='" + device + '\'' +
                ", isactive=" + isactive +
                ", created='" + created + '\'' +
                ", createdby=" + createdby +
                ", modified='" + modified + '\'' +
                ", modifiedby=" + modifiedby +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(uid);
        dest.writeInt(userid);
        dest.writeString(fullname);
        dest.writeInt(roleid);
        dest.writeString(address);
        dest.writeString(mobile);
        dest.writeString(token);
        dest.writeString(email);
        dest.writeString(profilepic);
        dest.writeDouble(latitute);
        dest.writeDouble(longitude);
        dest.writeString(device);
        dest.writeInt(isactive);
        dest.writeString(created);
        dest.writeInt(createdby);
        dest.writeString(modified);
        dest.writeInt(modifiedby);
    }
}
