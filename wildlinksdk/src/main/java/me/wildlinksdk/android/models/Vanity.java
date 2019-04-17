package me.wildlinksdk.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rjawanda on 1/5/18.
 */

public class Vanity {
    @Expose @SerializedName("OriginalURL") private String originalUrl;
    private String domain;

    @Expose @SerializedName("VanityURL") private String vanityUrl;

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getVanityUrl() {
        return vanityUrl;
    }

    public void setVanityUrl(final String vanityUrl) {
        this.vanityUrl = vanityUrl;
    }

    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
