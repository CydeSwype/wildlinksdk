package me.wildlinksdk.android.api.models;

import me.wildlinksdk.android.models.Message;

/**
 * Created by ron on 11/1/17.
 */

public interface SenderListener extends BaseListener {
    public void onSuccess(Message message);
}
