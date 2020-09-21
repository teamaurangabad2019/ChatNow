package com.teammandroid.chatnow.models;

import android.os.Parcel;
import android.os.Parcelable;

public class UserModel implements Parcelable {

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
    int RowCount;
    String firebaseuserid;


    private boolean isSelected=false;

    public UserModel() {
    }

    public UserModel(int userid, String fullname, int roleid, String address, String mobile, String token, String email, String profilepic, double latitute, double longitude, String device, int isactive, String created, int createdby, String modified, int modifiedby, int rowCount, String firebaseuserid, boolean isSelected) {
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
        RowCount = rowCount;
        this.firebaseuserid = firebaseuserid;
        this.isSelected = isSelected;
    }

    public UserModel(int userid, String fullname, int roleid, String address, String mobile, String token, String email, String profilepic, double latitute, double longitude, String device, int isactive, String created, int createdby, String modified, int modifiedby, int rowCount, String firebaseuserid) {
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
        RowCount = rowCount;
        this.firebaseuserid=firebaseuserid;

    }

    protected UserModel(Parcel in) {
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
        RowCount = in.readInt();
        firebaseuserid=in.readString();

        isSelected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
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
        dest.writeInt(RowCount);
        dest.writeString(firebaseuserid);

        dest.writeByte((byte) (isSelected ? 1 : 0));


    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

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

    public int getRowCount() {
        return RowCount;
    }

    public void setRowCount(int rowCount) {
        RowCount = rowCount;
    }

    public String getFirebaseuserid() {
        return firebaseuserid;
    }

    public void setFirebaseuserid(String firebaseuserid) {
        this.firebaseuserid = firebaseuserid;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "userid=" + userid +
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
                ", RowCount=" + RowCount +
                ", isSelected=" + isSelected +
                ", firebasePartnerId=" + firebaseuserid +
                '}';
    }
}
