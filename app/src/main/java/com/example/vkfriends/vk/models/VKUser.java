package com.example.vkfriends.vk.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class VKUser implements Parcelable {
    private String firstName;
    private String lastName;
    private String deactivated;
    private String photo;

    public VKUser(String firstName, String lastName, String deactivated, String photo) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.deactivated = deactivated;
        this.photo = photo;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDeactivated() {
        return deactivated;
    }

    public String getPhoto() {
        return photo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(deactivated);
        dest.writeString(photo);
    }

    private VKUser(Parcel source) {
        firstName = source.readString();
        lastName = source.readString();
        deactivated = source.readString();
        photo = source.readString();
    }

    public static final Creator<VKUser> CREATOR = new Creator<VKUser>() {
        @Override
        public VKUser createFromParcel(Parcel source) {
            return new VKUser(source);
        }

        @Override
        public VKUser[] newArray(int size) {
            return new VKUser[size];
        }
    };

    public static VKUser parse(JSONObject json) {
        return new VKUser(
                json.optString("first_name"),
                json.optString("last_name"),
                json.optString("deactivated"),
                json.optString("photo_max")
        );
    }
}
