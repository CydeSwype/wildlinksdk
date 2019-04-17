package me.wildlinksdk.android.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ron on 10/4/17.
 * returned device token from the initial api registration
 */

public class DeviceToken {
    @Expose @SerializedName("DeviceToken") private String deviceToken;

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
