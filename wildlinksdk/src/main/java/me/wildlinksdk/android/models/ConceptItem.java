package me.wildlinksdk.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ron on 10/5/17.
 */

public class ConceptItem {
    @Expose @SerializedName("ID") private String id;
    @Expose @SerializedName("Value") private String value;

    public ConceptItem(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }
}
