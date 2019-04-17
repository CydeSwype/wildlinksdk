package me.wildlinksdk.android.api;

/**
 * Created by rjawanda on 2/7/18.
 */

public class WlHttpException extends Exception {
    private int httpStatusCode;

    public WlHttpException(int code, String message) {
        super(message);
        this.httpStatusCode = code;
    }
}
