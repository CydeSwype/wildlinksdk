package me.wildlinksdk.android.api;

import org.apache.commons.text.similarity.LevenshteinDistance;

/**
 * Created by ron on 11/22/17.
 */

public class MathApi {

    private ApiModule apiModule;
    private LevenshteinDistance levenshteinDistance;

    public MathApi(ApiModule apiModule) {
        this.apiModule = apiModule;
        this.levenshteinDistance = new LevenshteinDistance();
    }

    public double rank(String phrase, String word) {
        double rank = (double) levenshteinDistance.apply(phrase, word) / (double) word.length();
        return rank;
    }
}
