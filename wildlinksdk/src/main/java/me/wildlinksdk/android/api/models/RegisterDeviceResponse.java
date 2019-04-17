package me.wildlinksdk.android.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegisterDeviceResponse {

    @Expose @SerializedName("DeviceID") private String deviceId;
    @Expose @SerializedName("DeviceKey") private String deviceKey;

    @Expose @SerializedName("DeviceToken") private String deviceToken;

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(final String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceKey() {
        return deviceKey;
    }

    public void setDeviceKey(final String deviceKey) {
        this.deviceKey = deviceKey;
    }
}
