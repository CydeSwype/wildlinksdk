package me.wildlinksdk.android;

/**
 * Created by rjawanda on 2/8/18.
 */

public class WildlinkInternalUseException extends Exception {
    public WildlinkInternalUseException(String message) {
        super(message + " method unfortunately cannot be used by clients");
    }
}

