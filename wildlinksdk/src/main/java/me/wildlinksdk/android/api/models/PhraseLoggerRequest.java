package me.wildlinksdk.android.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ron on 11/1/17.
 */

public class PhraseLoggerRequest {
    @Expose @SerializedName("context") private String wordContext;
    @Expose @SerializedName("context_with_link") private String contextWithLink;
    @Expose @SerializedName("sent_time") private String sentTime;
    @Expose @SerializedName("destination_app") private String destinationApp;

    public PhraseLoggerRequest(String wordContext, String contextWithLink, String sentTime,
        String destinationApp) {
        this.wordContext = wordContext;
        this.contextWithLink = contextWithLink;
        this.sentTime = sentTime;
        this.destinationApp = destinationApp;
    }

    public String getWordContext() {
        return wordContext;
    }

    public void setWordContext(String wordContext) {
        this.wordContext = wordContext;
    }

    public String getContextWithLink() {
        return contextWithLink;
    }

    public void setContextWithLink(String contextWithLink) {
        this.contextWithLink = contextWithLink;
    }

    public String getSentTime() {
        return sentTime;
    }

    public void setSentTime(String sentTime) {
        this.sentTime = sentTime;
    }

    public String getDestinationApp() {
        return destinationApp;
    }

    public void setDestinationApp(String destinationApp) {
        this.destinationApp = destinationApp;
    }
}
