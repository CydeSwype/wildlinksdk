package me.wildlinksdk.android.api;

import java.io.IOException;

public class AuthenticationException extends IOException {
    private int code;

    public AuthenticationException(int code, String message) {

        super(message);
        this.code = code;
    }

    public AuthenticationException(String message) {
        super(message);
    }

    public int getCode() {
        return code;
    }

    public void setCode(final int code) {
        this.code = code;
    }
}
