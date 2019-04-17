package me.wildlinksdk.android.analytics;

import android.os.AsyncTask;
import android.util.Log;
import com.google.gson.Gson;
import java.net.HttpURLConnection;
import me.wildlinksdk.android.api.ApiError;
import me.wildlinksdk.android.api.ApiModule;
import me.wildlinksdk.android.api.HttpApi;
import me.wildlinksdk.android.api.SharedPrefsUtil;
import okhttp3.Response;

/**
 * Created by rjawanda on 2/12/18.
 */

public class WlGoogleAnalytics {

    private static final String TAG = WlGoogleAnalytics.class.getSimpleName();
    private ApiModule apiModule;

    public WlGoogleAnalytics(ApiModule apiModule) {
        this.apiModule = apiModule;
    }

    public void sendEvent(String category, String action, String label) {

        if (!apiModule.getNetworkApi().isOnline()) {
            Log.d(TAG, "analytics offline");
            return;
        }
        Log.d(TAG, "sendEvent " + category);
        StringBuilder url = new StringBuilder("https://www.google-analytics.com/collect");

        url.append("?v=1");
        String defaultServerFlavor = apiModule.getSharedPrefsUtil().getDefaultServerFlavor();
        Log.d(TAG, "defaultServerFlavor=" + defaultServerFlavor);
        if (defaultServerFlavor.equalsIgnoreCase("dev")) {
            url.append("&tid=UA-109711780-6");
        } else if (defaultServerFlavor.equalsIgnoreCase("prod")) {
            url.append("&tid=UA-109711780-5");
        } else {
            Log.d(TAG, "unsupported google analytics build flavor");
            return;
        }
        String cid = this.apiModule.getSharedPrefsUtil().getGoogleClientCid();
        Log.d(TAG, "cid=" + cid);
        url.append("&cid=" + this.apiModule.getSharedPrefsUtil().getGoogleClientCid());
        url.append("&t=event");
        url.append("&ec=" + category);
        url.append("&ea=" + action);
        if (label != null) {

            url.append("&el=" + label);
            url.append("&ev=0");
        }

        new WlGoogleAnalyticsAsyncTask(url.toString(), this.apiModule.getHttpApi()).execute();
    }

    private static class WlGoogleAnalyticsAsyncTask extends AsyncTask<String, Void, ApiError> {

        private Gson gson;
        private HttpApi httpApi;
        private SharedPrefsUtil prefs;
        private String url;

        public WlGoogleAnalyticsAsyncTask(String url, HttpApi httpApi) {
            this.url = url;
            this.httpApi = httpApi;
        }

        @Override
        protected ApiError doInBackground(String... params) {
            Response response = null;
            try {
                ApiError apiError = null;

                response = httpApi.getRequestSync(url);
                Log.d(TAG, "response=" + response.code());
                if (!response.isSuccessful()) {

                    if (response.code() == HttpURLConnection.HTTP_FORBIDDEN
                        || response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        return new ApiError(response.code(), "Authentication error");
                    } else {
                        return new ApiError(response.code(),
                            "Networking error please retry or contact support");
                    }
                }

                return null;
            } catch (Exception e) {
                Log.d(TAG, "WlGoogleAnalyticsAsyncTask failed with Exception=" + e.getMessage());
                return new ApiError(ApiError.UNKNOWN_ERROR, e.getMessage());
            } finally {
                if (response != null) {
                    try {
                        response.close();
                    } catch (Exception e) {
                    }
                    ;
                }
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(ApiError result) {
            Log.d(TAG, "onPostExecute");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}



