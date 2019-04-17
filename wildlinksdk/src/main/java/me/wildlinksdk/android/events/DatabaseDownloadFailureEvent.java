package me.wildlinksdk.android.events;

public class DatabaseDownloadFailureEvent {
    private int code;
    private String message;

    public DatabaseDownloadFailureEvent(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    /***
     * http status code or -1 for unknown
     * @return
     */
    public int getCode() {
        return code;
    }

    /***
     * a message indicating what went wrong
     * @return
     */
    public String getMessage() {
        return message;
    }
}
