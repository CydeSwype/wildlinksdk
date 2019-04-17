package me.wildlinksdk.android;

import me.wildlinksdk.android.api.ApiError;
import me.wildlinksdk.android.models.Validation;

/**
 * Created by ron on 11/26/17.
 */

public interface ValidationListener {
    public void onFailure(ApiError error);

    public void onSuccess(Validation validation);
}
