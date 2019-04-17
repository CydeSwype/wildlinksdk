package me.wildlinksdk.android.models;

import java.util.Comparator;

/**
 * Created by ron on 10/28/17.
 */

public class WildlinkResultRanked implements Comparable<WildlinkResultRanked> {

    public static final Comparator<WildlinkResultRanked> DESCENDING_COMPARATOR =
        new Comparator<WildlinkResultRanked>() {
            // Overriding the compare method to sort the age
            public int compare(WildlinkResultRanked d, WildlinkResultRanked d1) {
                return d.rank - d1.rank;
            }
        };
    private WildlinkResult wildlinkResult;
    private int rank;

    public WildlinkResultRanked() {

    }

    public WildlinkResultRanked(WildlinkResult wildlinkResult, int rank) {
        this.wildlinkResult = wildlinkResult;
        this.rank = rank;
    }

    // Overriding the compareTo method
    public int compareTo(WildlinkResultRanked d) {
        return (this.wildlinkResult.getId()).compareTo(d.wildlinkResult.getId());
    }

    public WildlinkResult getWildlinkResult() {
        return wildlinkResult;
    }

    public void setWildlinkResult(WildlinkResult wildlinkResult) {
        this.wildlinkResult = wildlinkResult;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
