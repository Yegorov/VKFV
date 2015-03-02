package com.yegorov.vkfv.vk;

import android.os.Parcel;
import android.os.Parcelable;

public class AuthUser implements Parcelable{
    public final static String TAG = "AUTH_USER";

    private String userId;
    private String accessToken;
    private String expiresIn;

    public AuthUser() {
        this.userId      = "";
        this.accessToken = "";
        this.expiresIn   = "";
    }

    public AuthUser(String userId, String accessToken, String expiresIn) {
        this.userId      = userId;
        this.accessToken = accessToken;
        this.expiresIn   = expiresIn;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(accessToken);
        dest.writeString(expiresIn);
    }

    public static final Creator<AuthUser> CREATOR = new Creator<AuthUser>() {
        @Override
        public AuthUser createFromParcel(Parcel source) {
            return new AuthUser(source);
        }

        @Override
        public AuthUser[] newArray(int size) {
            return new AuthUser[size];
        }
    };

    private AuthUser(Parcel source) {
        userId = source.readString();
        accessToken = source.readString();
        expiresIn = source.readString();
    }
}
