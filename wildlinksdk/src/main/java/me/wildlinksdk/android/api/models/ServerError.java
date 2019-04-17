package me.wildlinksdk.android.api.models;

import com.google.gson.annotations.Expose;
import java.util.List;

public class ServerError {

    @Expose private List<Text> text;

    private class Text {
        @Expose public String error_message;
    }
}
