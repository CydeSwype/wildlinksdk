package me.wildlinksdk.android;

import me.wildlinksdk.android.api.ApiError;
import me.wildlinksdk.android.api.Sender;

/**
 * Created by rjawanda on 12/13/17.
 */

public interface GetSenderListener {
    public void onSuccess(Sender sender);

    public void onFailure(ApiError error);
}
