package me.wildlinksdk.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rjawanda on 1/15/18.
 */

public class PaypalSenderData {

    @Expose private String receiver;
    @Expose @SerializedName("recipient_type") private String recipientType;

    @Expose private Boolean validated;

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(String recipientType) {
        this.recipientType = recipientType;
    }

    public Boolean getValidated() {
        return validated;
    }

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }
}
