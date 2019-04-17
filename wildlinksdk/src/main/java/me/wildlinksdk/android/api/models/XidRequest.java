package me.wildlinksdk.android.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class XidRequest {
    @Expose @SerializedName("Source") private String source;
    @Expose @SerializedName("Value") private String value;

    public XidRequest(String source, String value) {
        this.source = source;
        this.value = value;
    }
}
