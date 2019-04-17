package me.wildlinksdk.android.api.models;

import com.google.gson.annotations.Expose;

/**
 * Created by rjawanda on 12/12/17.
 */

public class PaypalCredentials {
    @Expose private String receiver;
    @Expose private String recipient_type;
    @Expose private Boolean validated;

    public PaypalCredentials(String recipient_type, String receiver) {
        this.receiver = receiver;
        this.recipient_type = recipient_type;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getRecipient_type() {
        return recipient_type;
    }

    public void setRecipient_type(String recipient_type) {
        this.recipient_type = recipient_type;
    }

    public Boolean getValidated() {
        return validated;
    }

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }
}
