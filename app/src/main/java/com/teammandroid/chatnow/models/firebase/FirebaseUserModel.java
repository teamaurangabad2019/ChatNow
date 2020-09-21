package com.teammandroid.chatnow.models.firebase;


import android.os.Parcel;
import android.os.Parcelable;

public class FirebaseUserModel implements Parcelable {

    private String id;
    private String username;
    private String email;
    private String password;
    private String imageUrl;
    private String status;
    private String search;

    private String typingTo;
    private String onlineUserId;

    private String about;

    private String groupTypingTo;

    boolean isBlocked=false;

    String mobile;
    String token;
    String createdOn;
    String address;




    public FirebaseUserModel() {
    }

    public FirebaseUserModel(String id, String username, String email,String imageUrl, String status, String search, String typingTo, String onlineUserId, String about,String mobile, String token, String createdOn,String address) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.imageUrl = imageUrl;
        this.status = status;
        this.search = search;
        this.typingTo = typingTo;
        this.onlineUserId = onlineUserId;
        this.about = about;
        this.mobile = mobile;
        this.token = token;
        this.createdOn = createdOn;
        this.address=address;
    }



    public FirebaseUserModel(String id, String username, String email, String password, String imageUrl, String status, String search, String typingTo, String onlineUserId, String about, String groupTypingTo, boolean isBlocked) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.imageUrl = imageUrl;
        this.status = status;
        this.search = search;
        this.typingTo = typingTo;
        this.onlineUserId = onlineUserId;
        this.about = about;
        this.groupTypingTo = groupTypingTo;
        this.isBlocked = isBlocked;
    }

    public FirebaseUserModel(String id, String username, String email, String password, String imageUrl, String status, String search, String typingTo, String onlineUserId, String about, String groupTypingTo) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.imageUrl = imageUrl;
        this.status = status;
        this.search = search;
        this.typingTo = typingTo;
        this.onlineUserId = onlineUserId;
        this.about = about;
        this.groupTypingTo = groupTypingTo;
    }

    public FirebaseUserModel(String id, String username, String email, String password, String imageUrl, String status, String search, String typingTo, String onlineUserId, String about) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.imageUrl = imageUrl;
        this.status = status;
        this.search = search;
        this.typingTo = typingTo;
        this.onlineUserId = onlineUserId;
        this.about = about;
    }

    public FirebaseUserModel(String id, String username, String email, String password, String imageUrl, String status, String search) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.imageUrl = imageUrl;
        this.status = status;
        this.search = search;
    }

    public FirebaseUserModel(String id, String username, String email, String password, String imageUrl, String status, String search, String typingTo, String onlineUserId) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.imageUrl = imageUrl;
        this.status = status;
        this.search = search;
        this.typingTo = typingTo;
        this.onlineUserId = onlineUserId;
    }

    public FirebaseUserModel(String id, String username, String imageUrl, String status) {
        this.id = id;
        this.username = username;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    public FirebaseUserModel(String id, String username, String imageUrl, String status,String  typingTo) {
        this.id = id;
        this.username = username;
        this.imageUrl = imageUrl;
        this.status = status;
        this.typingTo=typingTo;
    }

    protected FirebaseUserModel(Parcel in) {
        id = in.readString();
        username = in.readString();
        email = in.readString();
        password = in.readString();
        imageUrl = in.readString();
        status = in.readString();
        search = in.readString();
        typingTo = in.readString();
        onlineUserId = in.readString();
        about = in.readString();
        groupTypingTo = in.readString();
        isBlocked = in.readByte() != 0;
        mobile = in.readString();
        token = in.readString();
        createdOn = in.readString();
        address = in.readString();
    }

    public static final Creator<FirebaseUserModel> CREATOR = new Creator<FirebaseUserModel>() {
        @Override
        public FirebaseUserModel createFromParcel(Parcel in) {
            return new FirebaseUserModel(in);
        }

        @Override
        public FirebaseUserModel[] newArray(int size) {
            return new FirebaseUserModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getTypingTo() {
        return typingTo;
    }

    public void setTypingTo(String typingTo) {
        this.typingTo = typingTo;
    }

    public String getOnlineUserId() {
        return onlineUserId;
    }

    public void setOnlineUserId(String onlineUserId) {
        this.onlineUserId = onlineUserId;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getGroupTypingTo() {
        return groupTypingTo;
    }

    public void setGroupTypingTo(String groupTypingTo) {
        this.groupTypingTo = groupTypingTo;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public String getMobileNumber() {
        return mobile;
    }

    public void setMobileNumber(String mobile) {
        this.mobile = mobile;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(imageUrl);
        dest.writeString(status);
        dest.writeString(search);
        dest.writeString(typingTo);
        dest.writeString(onlineUserId);
        dest.writeString(about);
        dest.writeString(groupTypingTo);
        dest.writeByte((byte) (isBlocked ? 1 : 0));
        dest.writeString(mobile);
        dest.writeString(token);
        dest.writeString(createdOn);
        dest.writeString(address);
    }

    @Override
    public String toString() {
        return "FirebaseUserModel{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", status='" + status + '\'' +
                ", search='" + search + '\'' +
                ", typingTo='" + typingTo + '\'' +
                ", onlineUserId='" + onlineUserId + '\'' +
                ", about='" + about + '\'' +
                ", groupTypingTo='" + groupTypingTo + '\'' +
                ", isBlocked=" + isBlocked +
                ", mobile='" + mobile + '\'' +
                ", token='" + token + '\'' +
                ", createdOn='" + createdOn + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
