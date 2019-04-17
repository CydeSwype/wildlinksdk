package me.wildlinksdk.android.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegisterDeviceRequest {
    @Expose @SerializedName("DeviceKey") private String deviceKey;

    @Expose @SerializedName("OS") private String os;

    public RegisterDeviceRequest(String deviceKey, String os) {
        this.deviceKey = deviceKey;
        this.os = os;
    }

    public String getDeviceKey() {
        return deviceKey;
    }

    public void setDeviceKey(final String deviceKey) {
        this.deviceKey = deviceKey;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }
}
