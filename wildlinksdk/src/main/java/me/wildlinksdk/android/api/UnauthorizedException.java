package me.wildlinksdk.android.api;

import java.io.IOException;

public class UnauthorizedException extends IOException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
