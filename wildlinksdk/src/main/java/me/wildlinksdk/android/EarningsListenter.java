package me.wildlinksdk.android;

import me.wildlinksdk.android.api.ApiError;
import me.wildlinksdk.android.models.Earnings;

/**
 * Created by ron on 11/26/17.
 */

public interface EarningsListenter {
    public void onFailure(ApiError error);

    public void onSuccess(Earnings earnings);
}
