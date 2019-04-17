package me.wildlinksdk.android.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ron on 11/26/17.
 */

class SenderValidate {
    @Expose @SerializedName("phone_number") public String phoneNumber;
    @Expose @SerializedName("validation_code") public String validationCode;

    public SenderValidate() {

    }

    public SenderValidate(String phoneNumber, String validationCode) {
        this.phoneNumber = phoneNumber;
        this.validationCode = validationCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getValidationCode() {
        return validationCode;
    }
}
