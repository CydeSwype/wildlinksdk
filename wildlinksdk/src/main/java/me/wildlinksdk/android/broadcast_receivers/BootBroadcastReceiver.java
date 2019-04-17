package me.wildlinksdk.android.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import me.wildlinksdk.android.api.ApiModule;
import me.wildlinksdk.android.services.ServiceInstaller;

/**
 * Created by rjawanda on 12/15/17.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = BootBroadcastReceiver.class.getSimpleName();

    public void onReceive(Context context, Intent intent) {

        if (ApiModule.INSTANCE.getSharedPrefsUtil().isWlEnabled() == null) {
            Log.d(TAG, "boot service this app is  not enabled");
            return;
        }

        if (ApiModule.INSTANCE.getSharedPrefsUtil().getNotificationData() != null) {
            Log.d(TAG, "Starting service");
            ServiceInstaller.installServices(context);
        } else {
            Log.d(TAG,
                "No shared prefs for this app not booting service" + context.getApplicationContext()
                    .getPackageName());
        }
    }
}
