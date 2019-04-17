package me.wildlinksdk.android;

import me.wildlinksdk.android.api.ApiError;
import me.wildlinksdk.android.api.models.PaypalCredentials;

/**
 * Created by rjawanda on 12/13/17.
 */

public interface PaypalPaymentListener {
    public void onSuccess(PaypalCredentials paypalCredentials);

    public void onFailure(ApiError error);
}
