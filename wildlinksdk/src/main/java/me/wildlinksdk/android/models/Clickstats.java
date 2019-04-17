package me.wildlinksdk.android.models;

import com.google.gson.annotations.Expose;
import java.util.List;

/**
 * Created by ron on 11/26/17.
 */

public class Clickstats {

    @Expose private List<Stats> stats;

    public List<Stats> getStats() {
        return stats;
    }

    public void setStats(List<Stats> stats) {
        this.stats = stats;
    }
}
