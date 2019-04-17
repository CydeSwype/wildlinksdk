package me.wildlinksdk.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ron on 11/26/17.
 */

public class Validation {
    @Expose @SerializedName("SenderToken") public String senderToken;

    public String getSenderToken() {
        return senderToken;
    }
}
