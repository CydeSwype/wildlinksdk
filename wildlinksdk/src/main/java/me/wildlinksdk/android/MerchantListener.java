package me.wildlinksdk.android;

import java.util.List;
import me.wildlinksdk.android.api.ApiError;
import me.wildlinksdk.android.models.Merchant;

/**
 * Created by ron on 11/26/17.
 */

public interface MerchantListener {
    public void onFailure(ApiError error);

    public void onSuccess(List<Merchant> commissions);
}

