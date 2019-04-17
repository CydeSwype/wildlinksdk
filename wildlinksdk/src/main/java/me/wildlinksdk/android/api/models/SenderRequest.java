package me.wildlinksdk.android.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ron on 11/1/17.
 */

public class SenderRequest {
    @Expose @SerializedName("phone_number") private String phoneNumber;

    public SenderRequest(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
