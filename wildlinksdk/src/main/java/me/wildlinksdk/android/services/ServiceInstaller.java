package me.wildlinksdk.android.services;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import me.wildlinksdk.android.Constants;
import me.wildlinksdk.android.api.ApiModule;
import me.wildlinksdk.android.api.SharedPrefsUtil;

public class ServiceInstaller {
    private static final String TAG = ServiceInstaller.class.getSimpleName();

    /**
     * Starts the Clipboard Listener Service and also schedule an alarm that makes it impossible
     * to actually kill the Clipboard Listener.
     */
    public static void installServices(Context context) {

        SharedPrefsUtil prefs = ApiModule.INSTANCE.getSharedPrefsUtil();

        Intent serviceIntent =
            new Intent(context.getApplicationContext(), WildlinkForegroundService.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KEY_WL_SERVICE_STARTUP_TYPE, Constants.WL_INT_STARTUP_TYPE_BOOT);
        serviceIntent.putExtra(Constants.WILDLINK_SERVICE_BUNDLE, bundle);
        serviceIntent.putExtra(Constants.KEY_WL_SERVICE_STARTUP_TYPE,
            Constants.WL_INT_STARTUP_TYPE_BOOT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }
}