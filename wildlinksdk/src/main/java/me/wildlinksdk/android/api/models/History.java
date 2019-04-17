package me.wildlinksdk.android.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rjawanda on 1/17/18.
 */

public class History {
    @Expose @SerializedName("wildlink_url") private String wildlinkUrl;

    @Expose @SerializedName("origin_url") private String originalUrl;

    @Expose private String title;

    public History(String wildlinkUrl, String originalUrl, String title) {
        this.wildlinkUrl = wildlinkUrl;
        this.originalUrl = originalUrl;
        this.title = title;
    }

    public String getWildlinkUrl() {
        return wildlinkUrl;
    }

    public void setWildlinkUrl(String wildlinkUrl) {
        this.wildlinkUrl = wildlinkUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
