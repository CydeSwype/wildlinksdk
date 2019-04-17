package me.wildlinksdk.android.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.gson.Gson;
import java.io.IOException;
import me.wildlinksdk.android.Constants;
import me.wildlinksdk.android.api.ApiError;
import me.wildlinksdk.android.api.ApiModule;
import me.wildlinksdk.android.api.HttpApi;
import me.wildlinksdk.android.api.SharedPrefsUtil;
import me.wildlinksdk.android.api.models.Token;

/**
 * Created by yongsun on 12/4/15.
 */
public class TokenService extends IntentService {
    private static final String TAG = TokenService.class.getSimpleName();

    public TokenService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        try {

            String token = intent.getStringExtra(Constants.KEY_WL_FIREBASE_TOKEN);
            Log.i(TAG, "Firebase Registration Token: " + token);
            sendRegistrationToServer(sharedPreferences, token);
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.

        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        //        Intent registrationComplete = new Intent("registrationComplete");
        //        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     * <p/>
     * Modify this method to associate the user's GCM registration token with any server-side
     * account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(final SharedPreferences preferences, final String token) {
        ApiModule apiModule = ApiModule.INSTANCE;

        final HttpApi httpApi = apiModule.getHttpApi();
        final Gson gson = apiModule.getGson();
        final SharedPrefsUtil prefs = apiModule.getSharedPrefsUtil();
        String baseUrl = prefs.getBaseApiUrl();

        final String url = baseUrl + "/v2/device/xid";

        Log.d(TAG, "url=" + url);

        Token request = new Token(token);

        // TODO uncomment when service is available
        httpApi.postRequestQuickTimeout(url, gson.toJson(request, Token.class),
            new okhttp3.Callback() {

                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    Log.d(TAG, "failure");
                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response)
                    throws IOException {
                    if (!response.isSuccessful()) {
                        if (response.body() != null) {
                            String body = response.body().string();
                            try {
                                ApiError error = gson.fromJson(body, ApiError.class);
                                error.setCode(response.code());
                            } catch (Exception e) {

                                ApiError error = new ApiError(response.code(), body);
                            }

                            response.body().close();
                        } else {
                            Log.d(TAG, "server updated with new push token from firebase");
                        }

                        return;
                    }
                    try {

                        Log.d(TAG, "Server success");
                        prefs.storeFirebaseRefreshedToken(token);
                    } catch (Exception e) {

                    }
                }
            });
    }
}
