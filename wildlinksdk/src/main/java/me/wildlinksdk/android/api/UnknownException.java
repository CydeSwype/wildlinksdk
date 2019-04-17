package me.wildlinksdk.android.api;

import java.io.IOException;

public class UnknownException extends IOException {
    private int code;

    public UnknownException(int code, String message) {

        super(message);
        this.code = code;
    }

    public UnknownException(String message) {
        super(message);
    }

    public int getCode() {
        return code;
    }

    public void setCode(final int code) {
        this.code = code;
    }
}
