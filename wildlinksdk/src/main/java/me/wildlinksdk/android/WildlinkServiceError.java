package me.wildlinksdk.android;

/**
 * Created by rjawanda on 1/29/18.
 */

public class WildlinkServiceError {
    private int wildlinkErrorCode;
    private int httpStatusCode;
    private String message;

    public WildlinkServiceError(int wildlinkErrorCode, int httpStatusCode, String message) {
        this.wildlinkErrorCode = wildlinkErrorCode;
        this.httpStatusCode = httpStatusCode;
        this.message = message;
    }

    public int getWildlinkErrorCode() {
        return wildlinkErrorCode;
    }

    public void setWildlinkErrorCode(int wildlinkErrorCode) {
        this.wildlinkErrorCode = wildlinkErrorCode;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
