package me.wildlinksdk.android;

import me.wildlinksdk.android.api.ApiError;

public interface WebListener {
    public void onSuccess(String s);

    public void onFailure(ApiError apiError);
}
