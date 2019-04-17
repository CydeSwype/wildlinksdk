package me.wildlinksdk.android;

import me.wildlinksdk.android.api.ApiError;

/**
 * Created by rjawanda on 12/12/17.
 */

public interface PaypalListener {
    public void onFailure(ApiError error);

    public void onSuccess();
}
