package me.wildlinksdk.android.models;

/**
 * Created by ron on 11/9/17.
 */

public class WordRank {
    private int rank;
    private String word;
    private double percentage;

    public WordRank(String word, int rank) {
        this.word = word;
        this.rank = rank;
    }

    public WordRank(String word, int rank, double percentage) {
        this.word = word;
        this.rank = rank;
        this.percentage = percentage;
    }

    public WordRank(String word) {
        this.word = word;
        this.rank = 9999999;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}
