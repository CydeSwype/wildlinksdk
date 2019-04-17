package me.wildlinksdk.android.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by rjawanda on 12/15/17.
 */

public class EnableReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Log.d("ronj", "enable receiver");
    }
}
