package me.wildlinksdk.android.models;

import com.google.gson.annotations.Expose;

/**
 * Created by ron on 11/30/17.
 */

public class Stats {
    @Expose private String date;
    @Expose private Integer count;

    public Stats() {

    }

    public Stats(String date, Integer count) {
        this.date = date;
        this.count = count;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
