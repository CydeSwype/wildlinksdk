package me.wildlinksdk.android;

/**
 * Created by ron on 10/20/17.
 */

public class RefreshRate {
    public Integer value;
    // Calendar.HOURS, CALENDAR.MINUTES etc.
    public int timeUnits;

    public RefreshRate(int timeUnits, Integer value) {
        this.value = value;
        this.timeUnits = timeUnits;
    }
}
