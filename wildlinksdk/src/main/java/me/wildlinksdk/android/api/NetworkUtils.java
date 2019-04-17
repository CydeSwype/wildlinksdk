package me.wildlinksdk.android.api;

/**
 * Created by ron on 10/4/17.
 * Network utils is used to check if the network is on or not
 * airplane mode, wifi etc.
 * isOnline is the thing you want to use.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

public class NetworkUtils {

    private final String TAG = NetworkUtils.class.getSimpleName();

    private Context context;
    private ConnectivityManager connectivityManager;

    public NetworkUtils(Context context) {
        this.context = context;
        connectivityManager =
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean isAirplaneModeOn(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.System.getInt(context.getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        } else {
            return Settings.Global.getInt(context.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        }
    }

    /***
     * use this first to determine if online or not.
     * if you want to get detailed use the isOnWifi etc. below
     * @return
     */
    public boolean isOnline() {

        if (connectivityManager == null) {
            connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }

        if (connectivityManager == null) {
            return false;
        }

        final boolean isAirplaneModeOn = isAirplaneModeOn();
        if (isAirplaneModeOn) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "using new network api");
            Network[] networks = connectivityManager.getAllNetworks();
            if (connectivityManager != null) {
                for (Network netinfo : networks) {

                    NetworkInfo ni = connectivityManager.getNetworkInfo(netinfo);
                    if (ni == null) {
                        return false;
                    }
                    if (ni.isConnected() && ni.isAvailable()) {
                        return true;
                    }
                }
            }

            return false;
        } else {
            if (connectivityManager.getActiveNetworkInfo() == null) {
                Log.d(TAG, "connectivityManager.getActiveNetworkInfo() is null");
                return false;
            } else {

                final boolean isConnected =
                    connectivityManager.getActiveNetworkInfo().isConnected();
                final boolean isConnectingOrConnected =
                    connectivityManager.getActiveNetworkInfo().isConnected();

                if (isConnected == false && isConnectingOrConnected == true) {
                    Log.d(TAG, "is connecting");
                    return true;
                }
                final boolean wifi = isOnWifi();

                final boolean cell = isOnCell();
                Log.d(TAG, "isConnnected=" + isConnected + ", wifi=" + wifi + ", cell=" + cell);

                if (isConnected || wifi || cell) {
                    Log.d(TAG, "CONNECTED");

                    return true;
                }

                return false;
            }
        }
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public boolean isAirplaneModeOn() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.System.getInt(context.getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        } else {
            return Settings.Global.getInt(context.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        }
    }

    public boolean isOnWifi() {
        if (connectivityManager.getActiveNetworkInfo() == null) {
            return false;
        }
        if (connectivityManager.getActiveNetworkInfo().isConnected()
            && (connectivityManager.getActiveNetworkInfo().getType()
            == ConnectivityManager.TYPE_WIFI
            || connectivityManager.getActiveNetworkInfo().getType()
            == ConnectivityManager.TYPE_WIMAX)) {
            return true;
        }
        return false;
    }

    public boolean isOnCell() {
        if (isAirplaneModeOn()) {
            return false;
        }

        if (connectivityManager.getActiveNetworkInfo() == null) {
            return false;
        }

        if (connectivityManager.getActiveNetworkInfo().isConnected()
            && (connectivityManager.getActiveNetworkInfo().getType()
            == ConnectivityManager.TYPE_WIFI
            || connectivityManager.getActiveNetworkInfo().getType()
            == ConnectivityManager.TYPE_WIMAX)) {
            return false;
        }
        if (connectivityManager.getActiveNetworkInfo() != null
            && connectivityManager.getActiveNetworkInfo().isConnected()) {
            return true;
        }
        return false;
    }
}

