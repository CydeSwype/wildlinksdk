package me.wildlinksdk.android.models;

import java.util.List;

/**
 * Created by ron on 10/10/17.
 */

public class WildlinkSearchResults {
    private List<WildlinkResult> match;
    private List<WildlinkResult> begin;
    private List<WildlinkResult> contain;

    public List<WildlinkResult> getMatch() {
        return match;
    }

    public void setMatches(List<WildlinkResult> match) {
        this.match = match;
    }

    public List<WildlinkResult> getBegin() {
        return begin;
    }

    public void setBeginsWith(List<WildlinkResult> begin) {
        this.begin = begin;
    }

    public List<WildlinkResult> getContain() {
        return contain;
    }

    public void setContains(List<WildlinkResult> contain) {
        this.contain = contain;
    }
}
