package me.wildlinksdk.android.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import me.wildlinksdk.android.Constants;

/**
 * Created by rjawanda on 12/19/17.
 */

public class WlMyPackageReplacedReceiver extends BroadcastReceiver {
    private static final String TAG = WlMyPackageReplacedReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive()");

        if (intent.getAction().equals(Intent.ACTION_MY_PACKAGE_REPLACED)) {
            Log.d(TAG, "ACTION_MY_PACKAGE_REPLACED");

            try {

                Intent serviceIntent =
                    new Intent(context.getApplicationContext(), WildlinkForegroundService.class);
                //Bundle bundle = new Bundle();
                //bundle.putInt(Constants.KEY_WL_SERVICE_STARTUP_TYPE, Constants.WL_INT_STARTUP_PACKAGE_UPDATED);
                //serviceIntent.putExtra(Constants.WILDLINK_SERVICE_BUNDLE,bundle);
                serviceIntent.putExtra(Constants.KEY_WL_SERVICE_STARTUP_TYPE,
                    Constants.WL_INT_STARTUP_PACKAGE_UPDATED);
                //Log.d(TAG, "adding start type of=" + Constants.WL_INT_STARTUP_PACKAGE_UPDATED);

                // Beware of OS 8.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.getApplicationContext().startForegroundService(serviceIntent);
                } else {
                    context.getApplicationContext().startService(serviceIntent);
                }
            } catch (Exception e) {
            }
        }
    }
}

