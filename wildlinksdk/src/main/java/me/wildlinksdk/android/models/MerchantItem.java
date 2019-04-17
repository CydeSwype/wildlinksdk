package me.wildlinksdk.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ron on 10/5/17.
 */

public class MerchantItem {
    @Expose @SerializedName("ID") private String id;
    @Expose @SerializedName("Value") private String domain;

    public MerchantItem(String id, String domain) {
        this.id = id;
        this.domain = domain;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(final String domain) {
        this.domain = domain;
    }
}
