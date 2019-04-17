package me.wildlinksdk.android.api.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;

/**
 * Created by ron on 10/6/17.
 */

public class Session implements Parcelable {
    public static final Creator<Session> CREATOR = new Creator<Session>() {
        @Override
        public Session createFromParcel(final Parcel in) {
            return new Session(in);
        }

        @Override
        public Session[] newArray(int size) {
            return new Session[size];
        }
    };
    @Expose private String deviceToken = "";
    @Expose private String senderToken = "";

    public Session() {
    }

    public Session(String deviceToken, String senderToken) {
        this.deviceToken = deviceToken;
        this.senderToken = senderToken;
    }

    public Session(Parcel in) {
        readFromParcel(in);
    }

    protected void readFromParcel(Parcel in) {
        deviceToken = in.readString();
        senderToken = in.readString();
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getSenderToken() {
        return senderToken;
    }

    public void setSenderToken(String senderToken) {
        this.senderToken = senderToken;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(deviceToken);
        parcel.writeString(senderToken);
    }
}
