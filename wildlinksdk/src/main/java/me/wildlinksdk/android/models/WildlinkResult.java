package me.wildlinksdk.android.models;

import com.google.gson.annotations.Expose;

/**
 * Created by ron on 10/10/17.
 * not stored in realm
 */

public class WildlinkResult {
    @Expose private String id;
    @Expose private String phrase;
    @Expose private String type;
    @Expose private Double rank;

    public WildlinkResult() {

    }

    public WildlinkResult(String word, Double rank, String id) {
        this.phrase = word;
        this.rank = rank;
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public Double getRank() {
        return rank;
    }

    public void setRank(Double rank) {
        this.rank = rank;
    }
}
