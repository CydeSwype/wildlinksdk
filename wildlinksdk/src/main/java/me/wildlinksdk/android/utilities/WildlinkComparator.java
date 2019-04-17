package me.wildlinksdk.android.utilities;

import java.util.Comparator;
import me.wildlinksdk.android.models.WildlinkResult;

/**
 * Created by ron on 11/22/17.
 */

public class WildlinkComparator implements Comparator<WildlinkResult> {

    public int compare(WildlinkResult o1, WildlinkResult o2) {
        Double x1 = o1.getRank();
        Double x2 = o2.getRank();
        int sComp = x1.compareTo(x2);

        if (sComp != 0) {
            return sComp;
        } else {
            String s1 = o1.getPhrase();
            String s2 = o2.getPhrase();
            return s1.compareTo(s2);
        }
    }
}
