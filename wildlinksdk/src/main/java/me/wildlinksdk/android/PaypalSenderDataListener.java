package me.wildlinksdk.android;

import me.wildlinksdk.android.api.ApiError;
import me.wildlinksdk.android.models.PaypalSenderData;

/**
 * Created by rjawanda on 1/15/18.
 */

public interface PaypalSenderDataListener {
    public void onFailure(ApiError error);

    public void onSuccess(PaypalSenderData response);
}
