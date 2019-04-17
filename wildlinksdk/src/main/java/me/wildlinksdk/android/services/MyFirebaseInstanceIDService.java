package me.wildlinksdk.android.services;

import android.content.Intent;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import me.wildlinksdk.android.Constants;
import me.wildlinksdk.android.api.ApiModule;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    public MyFirebaseInstanceIDService() {

    }

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        final ApiModule instance = ApiModule.INSTANCE;

        if (instance.getSharedPrefsUtil() == null) {
            Log.d(TAG, "probably not initialized yet, ignoring");
            return;
        }

        if (instance.getSharedPrefsUtil().getSenderToken() == null
            || instance.getSharedPrefsUtil().getSenderToken().length() == 0) {
            Log.d(TAG, "do not do anything here, sender Token no instance");
            return;
        }
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        Log.d(TAG, "updating firebase token");
        Intent intentService = new Intent(this, TokenService.class);
        intentService.putExtra(Constants.KEY_WL_FIREBASE_TOKEN, refreshedToken);
        startService(intentService);
    }
}
