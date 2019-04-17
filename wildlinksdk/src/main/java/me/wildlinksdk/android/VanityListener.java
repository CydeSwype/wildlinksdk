package me.wildlinksdk.android;

import me.wildlinksdk.android.api.ApiError;
import me.wildlinksdk.android.models.Vanity;

/**
 * Created by rjawanda on 1/5/18.
 */

public interface VanityListener {
    public void onFailure(ApiError error);

    public void onSuccess(Vanity vanity);
}
