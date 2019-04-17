package me.wildlinksdk.android.api;

/**
 * Created by ron on 10/4/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ApiError {

    public static final int UNKNOWN_ERROR = -1;
    public static final int UNKNOWN_HOST = -2;
    public static final int SERVICE_BINDING = -3;
    private int code;
    @Expose @SerializedName("error_message") private String message;

    public ApiError() {

    }

    public ApiError(int code) {
        this.code = code;
    }
    public ApiError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message == null ? "" : message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

