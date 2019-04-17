package me.wildlinksdk.android.api;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Created by ron on 10/10/17.
 */

public final class Validate {
    private static final String TAG = Validate.class.getName();
    private static final String NO_INTERNET_PERMISSION_REASON =
        "No Internet permissions granted for the app, "
            + "please add <uses-permission android:name=\"android.permission.INTERNET\"/>"
            + " to your manifest";

    public static void hasInternetPermissions(Context context) {
        Validate.hasInternetPermissions(context, true);
    }

    public static void hasInternetPermissions(Context context, boolean shouldThrow) {
        Validate.notNull(context, "context");
        if (context.checkCallingOrSelfPermission(Manifest.permission.INTERNET)
            == PackageManager.PERMISSION_DENIED) {
            if (shouldThrow) {
                throw new IllegalStateException(NO_INTERNET_PERMISSION_REASON);
            } else {
                Log.w(TAG, NO_INTERNET_PERMISSION_REASON);
            }
        }
    }

    public static void notNull(Object arg, String name) {
        if (arg == null) {
            throw new NullPointerException("Argument '" + name + "' cannot be empty");
        }
    }
}
