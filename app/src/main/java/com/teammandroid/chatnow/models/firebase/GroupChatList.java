package com.teammandroid.chatnow.models.firebase;

import android.os.Parcel;
import android.os.Parcelable;

public class GroupChatList implements Parcelable {

    String groupId,groupTitle,groupDescription,groupIcon,timeStamp,createdBy;

    public GroupChatList() {
    }

    public GroupChatList(String groupId, String groupTitle, String groupDescription, String groupIcon, String timeStamp, String createdBy) {
        this.groupId = groupId;
        this.groupTitle = groupTitle;
        this.groupDescription = groupDescription;
        this.groupIcon = groupIcon;
        this.timeStamp = timeStamp;
        this.createdBy = createdBy;
    }

    protected GroupChatList(Parcel in) {
        groupId = in.readString();
        groupTitle = in.readString();
        groupDescription = in.readString();
        groupIcon = in.readString();
        timeStamp = in.readString();
        createdBy = in.readString();
    }

    public static final Creator<GroupChatList> CREATOR = new Creator<GroupChatList>() {
        @Override
        public GroupChatList createFromParcel(Parcel in) {
            return new GroupChatList(in);
        }

        @Override
        public GroupChatList[] newArray(int size) {
            return new GroupChatList[size];
        }
    };

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public String getGroupIcon() {
        return groupIcon;
    }

    public void setGroupIcon(String groupIcon) {
        this.groupIcon = groupIcon;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(groupId);
        dest.writeString(groupTitle);
        dest.writeString(groupDescription);
        dest.writeString(groupIcon);
        dest.writeString(timeStamp);
        dest.writeString(createdBy);
    }
}
