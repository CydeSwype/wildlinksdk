package me.wildlinksdk.android.api.models;

import me.wildlinksdk.android.api.ApiError;

/**
 * Created by ron on 10/16/17.
 */

public interface BaseListener {
    public void onFailure(ApiError error);
}
