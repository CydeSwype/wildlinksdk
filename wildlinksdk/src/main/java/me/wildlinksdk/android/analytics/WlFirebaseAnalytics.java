package me.wildlinksdk.android.analytics;

import android.os.Bundle;
import android.util.Log;
import com.google.firebase.analytics.FirebaseAnalytics;
import me.wildlinksdk.android.api.ApiModule;

/**
 * Created by rjawanda on 2/12/18.
 */

public class WlFirebaseAnalytics {

    private static final String TAG = WlFirebaseAnalytics.class.getSimpleName();
    private ApiModule apiModule;

    public WlFirebaseAnalytics(ApiModule apiModule) {
        this.apiModule = apiModule;
    }

    public void sendUserData(String phone, String email) {
        Bundle bundle = new Bundle();
        if (phone != null) {
            FirebaseAnalytics.getInstance(apiModule.getProvider().provideApplicationContext())
                .setUserProperty("phone", phone);
            bundle.putString("phone", phone);
        }
        if (email != null) {
            FirebaseAnalytics.getInstance(apiModule.getProvider().provideApplicationContext())
                .setUserProperty("email", email);
            bundle.putString("email", email);
        }

        FirebaseAnalytics.getInstance(apiModule.getProvider().provideApplicationContext()).
            logEvent("setUserData", bundle);
        Log.d(TAG, "firebaseSendUserData()" + bundle.toString());
    }

    public void startEvent(String method) {

        if (method != null) {
            Bundle bundle = new Bundle();
            bundle.putString("method", method);
            FirebaseAnalytics.getInstance(apiModule.getProvider().provideApplicationContext()).
                logEvent("start_event", bundle);
            Log.d(TAG, "startEvent()" + bundle.toString());
        }
    }
}
