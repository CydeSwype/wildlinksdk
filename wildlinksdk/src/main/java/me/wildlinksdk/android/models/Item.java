package me.wildlinksdk.android.models;

public class Item {
    private String token;
    private String ngram;
    private String id;
    private Double score;
    private Double rank;
    private String input;
    private String matchingPhase;

    public Item(String id, String token) {
        this.id = id;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(final Double score) {
        this.score = score;
    }

    public String getMatchingPhase() {
        return matchingPhase;
    }

    public void setMatchingPhrase(final String matchingPhase) {
        this.matchingPhase = matchingPhase;
    }
}
