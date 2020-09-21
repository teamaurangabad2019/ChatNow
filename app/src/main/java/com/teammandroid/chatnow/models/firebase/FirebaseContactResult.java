package com.teammandroid.chatnow.models.firebase;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.RxContacts.Contact;
import com.wafflecopter.multicontactpicker.RxContacts.PhoneNumber;

import java.util.ArrayList;
import java.util.List;

public class FirebaseContactResult implements Parcelable {

        private String contactID;
        private String displayName;
        private boolean starred;
        private Uri photo;
        private Uri thumbnail;
        private List<String> emails = new ArrayList<>();
        private List<FirebasePhoneNumber> phoneNumbers = new ArrayList<>();

    public FirebaseContactResult() {
    }

    protected FirebaseContactResult(Parcel in) {
        contactID = in.readString();
        displayName = in.readString();
        starred = in.readByte() != 0;
        photo = in.readParcelable(Uri.class.getClassLoader());
        thumbnail = in.readParcelable(Uri.class.getClassLoader());
        emails = in.createStringArrayList();
        phoneNumbers = in.createTypedArrayList(FirebasePhoneNumber.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(contactID);
        dest.writeString(displayName);
        dest.writeByte((byte) (starred ? 1 : 0));
        dest.writeParcelable(photo, flags);
        dest.writeParcelable(thumbnail, flags);
        dest.writeStringList(emails);
        dest.writeTypedList(phoneNumbers);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FirebaseContactResult> CREATOR = new Creator<FirebaseContactResult>() {
        @Override
        public FirebaseContactResult createFromParcel(Parcel in) {
            return new FirebaseContactResult(in);
        }

        @Override
        public FirebaseContactResult[] newArray(int size) {
            return new FirebaseContactResult[size];
        }
    };

    public String getContactID() {
            return contactID;
        }

        public void setContactID(String mContactID) {
            this.contactID = mContactID;
        }

        public String getDisplayName() {
            return displayName;
        }

        public boolean isStarred() {
            return starred;
        }

        public Uri getPhoto() {
            return photo;
        }

        public Uri getThumbnail() {
            return thumbnail;
        }

        public List<String> getEmails() {
            return emails;
        }

        public List<FirebasePhoneNumber> getPhoneNumbers() {
            return phoneNumbers;
        }

    @Override
    public String toString() {
        return "FirebaseContactResult{" +
                "contactID='" + contactID + '\'' +
                ", displayName='" + displayName + '\'' +
                ", starred=" + starred +
                ", photo=" + photo +
                ", thumbnail=" + thumbnail +
                ", emails=" + emails +
                ", phoneNumbers=" + phoneNumbers +
                '}';
    }

        /* public FirebaseContactResult(Contact contact){
            this.contactID = String.valueOf(contact.getId());
            this.displayName = contact.getDisplayName();
            this.starred = contact.isStarred();
            this.photo = contact.getPhoto();
            this.thumbnail = contact.getThumbnail();
            this.emails.clear(); this.emails.addAll(contact.getEmails());
            this.phoneNumbers.clear(); this.phoneNumbers.addAll(contact.getPhoneNumbers());
        }
*/
}
