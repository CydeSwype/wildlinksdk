package me.wildlinksdk.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ron on 11/27/17.
 */

public class Message {
    @Expose @SerializedName("message") private String message;

    public String getMessage() {
        return message;
    }
}
