package me.wildlinksdk.android.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rjawanda on 1/10/18.
 */

public class Token {
    @Expose @SerializedName("Source") private String source;
    @Expose @SerializedName("Value") private String token;

    public Token(String token) {
        source = "FCM_REGISTRATION_TOKEN";
        this.token = token;
    }
}
