package com.example.appsmarthome.Objects;

import android.os.Parcel;
import android.os.Parcelable;

public class UserObj implements Parcelable {
    private String fullName;
    private String email;
    private String phone;

    public UserObj(Parcel in) {
        fullName = in.readString();
        email = in.readString();
        phone = in.readString();
    }

    public static final Creator<UserObj> CREATOR = new Creator<UserObj>() {
        @Override
        public UserObj createFromParcel(Parcel in) {
            return new UserObj(in);
        }

        @Override
        public UserObj[] newArray(int size) {
            return new UserObj[size];
        }
    };

    public UserObj() {

    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(fullName);
        parcel.writeString(email);
        parcel.writeString(phone);
    }
}
