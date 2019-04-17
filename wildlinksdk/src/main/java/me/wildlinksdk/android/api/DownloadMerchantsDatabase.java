package me.wildlinksdk.android.api;

import android.os.AsyncTask;
import android.util.Log;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import me.wildlinksdk.android.SimpleListener;
import me.wildlinksdk.android.analytics.WlGoogleAnalytics;
import me.wildlinksdk.android.db.MerchantsDataSource;
import me.wildlinksdk.android.events.DatabaseDownloadCompleteEvent;
import me.wildlinksdk.android.events.DatabaseDownloadFailureEvent;
import me.wildlinksdk.android.models.MerchantList;
import okhttp3.Response;
import org.greenrobot.eventbus.EventBus;

public class DownloadMerchantsDatabase extends AsyncTask<String, Void, ApiError> {
    private static final String TAG = DownloadMerchantsDatabase.class.getSimpleName();
    private SimpleListener simpleListener;
    private Gson gson;
    private HttpApi httpApi;
    private SharedPrefsUtil prefs;
    private Cache.CacheListener cacheListener;
    private WlGoogleAnalytics googleAnalytics;

    private String appId;

    public DownloadMerchantsDatabase(Cache.CacheListener cacheListener, ApiModule apiModule,
        SimpleListener simpleListener) {
        this.simpleListener = simpleListener;
        this.googleAnalytics = apiModule.getGoogleAnalytics();
        this.prefs = apiModule.getSharedPrefsUtil();
        this.gson = apiModule.getGson();
        this.httpApi = apiModule.getHttpApi();
        this.cacheListener = cacheListener;
        this.appId = apiModule.getProvider().provideWildlinkAppId();
    }

    @Override
    protected ApiError doInBackground(String... params) {

        Log.d(TAG, "doInBackground");
        boolean exit = false;

        for (int j = 0; j < 3 || exit; j++) {
            MerchantsDataSource ds = new MerchantsDataSource(ApiModule.INSTANCE.getDatabase());
            try {

                ApiError apiError = null;
                boolean isEnd = false;
                String cursor = null;
                String baseUrl = prefs.getBaseApiUrl();

                int idCount = 0;
                while (apiError == null && isEnd == false) {
                    final StringBuilder url = new StringBuilder();

                    url.append(baseUrl);
                    url.append("/v1/concept/domain");
                    url.append("?limit=1000");
                    if (cursor != null) {
                        url.append("&cursor=" + cursor);
                    }

                    Log.d(TAG, "url=" + url);

                    Response response = null;
                    try {
                        response = httpApi.getRequestSync(url.toString());
                        Log.d(TAG, "response.code=" + response.code());

                        if (response.isSuccessful()) {

                            if (response.code() >= HttpURLConnection.HTTP_OK
                                && response.code() < HttpURLConnection.HTTP_MOVED_TEMP) {

                                InputStream in = response.body().byteStream();

                                BufferedReader reader =
                                    new BufferedReader(new InputStreamReader(in));
                                String result, line = reader.readLine();
                                result = line;
                                while ((line = reader.readLine()) != null) {
                                    result += line;
                                }

                                response.body().close();

                                Log.d(TAG, "response.body()=" + result);
                                MerchantList merchantList =
                                    gson.fromJson(result, MerchantList.class);

                                if (merchantList.getConceptsList() == null
                                    || merchantList.getConceptsList().size() == 0) {
                                    isEnd = true;
                                    Log.d(TAG, "database downloaded but no items in list");
                                    cacheListener.onSuccess();
                                    return null;
                                }

                                Log.d(TAG, "-------inserting data" + merchantList.getConceptsList()
                                    .size());

                                boolean didAtLeastOneInsert = false;
                                try {
                                    ds.beginTransaction();
                                    ds.removeAll();
                                    for (int i = 0; i < merchantList.getConceptsList().size();
                                        i++) {

                                        if (merchantList.getConceptsList().get(i).getId() == null
                                            || merchantList.getConceptsList().get(i).getDomain()
                                            == null) {

                                            continue;
                                        }
                                        ds.insert(merchantList.getConceptsList().get(i).getId(),
                                            merchantList.getConceptsList().get(i).getDomain());
                                        didAtLeastOneInsert = true;
                                    }
                                    if (didAtLeastOneInsert) {
                                        ds.endTransactionSuccessfull();
                                    }
                                } finally {

                                    ds.endTransaction();
                                }
                                cursor = merchantList.getNextCursor();
                            } else if (response.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                                Log.d(TAG, "page not found" + response.code());
                                ApiError error = new ApiError(response.code(), "Page not found");
                                simpleListener.onFailure(error);

                                isEnd = true;
                                return new ApiError(response.code(),
                                    "" + HttpURLConnection.HTTP_NOT_FOUND + ". Page not found");
                            } else if (response.code() == HttpURLConnection.HTTP_INTERNAL_ERROR) {

                                Log.d(TAG, "internal error" + response.code());
                                ApiError error =
                                    gson.fromJson(response.body().string(), ApiError.class);
                                error.setCode(response.code());
                                simpleListener.onFailure(error);

                                isEnd = true;
                                return new ApiError(response.code(), error.getMessage());
                            }
                        } else {
                            isEnd = true;

                            if (response.code() == HttpURLConnection.HTTP_FORBIDDEN
                                || response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                                return new ApiError(response.code(), "Authentication error");
                            } else {
                                return new ApiError(response.code(),
                                    "Networking error please retry or contact support");
                            }
                        }
                    } finally {
                        exit = true;

                        if (response != null) {
                            try {
                                response.close();
                            } catch (Exception e) {
                            }
                            ;
                        }
                    }
                }

                return null;
            } catch (UnknownException ue) {
                Log.d(TAG, "DownloadMerchantsDatabase failed with UnknownException=");
                return new ApiError(ue.getCode(), ue.getMessage());
            } catch (AuthenticationException ae) {
                Log.d(TAG, "DownloadMerchantsDatabase failed with  AuthenticationException=");
                return new ApiError(ae.getCode(), ae.getMessage());
            } catch (Exception e) {
                if (j < 2) {
                    Log.d(TAG, "sleeping 500");
                    try {
                        Thread.sleep(500);
                    } catch (Exception e1) {
                    }
                    continue;
                }
                Log.d(TAG, "DownloadMerchantsDatabase failed with Exception=" + e.getMessage());
                return new ApiError(ApiError.UNKNOWN_ERROR, e.getMessage());
            } finally {

            }
        }
        return new ApiError(ApiError.UNKNOWN_ERROR, "failed to get any response after 3 tries");
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(ApiError result) {
        Log.d(TAG, "onPostExectute");
        if (result == null) {
            prefs.removeMerchantDatabaseDownloadFailure();
            prefs.updateMechanteDatabaseRefreshDate();
            EventBus.getDefault().post(new DatabaseDownloadCompleteEvent());
            cacheListener.onSuccess();
            simpleListener.onSuccess();
            googleAnalytics.sendEvent("database", "domain", appId);
        } else {

            prefs.setMerchantsDatabaseFailure();
            EventBus.getDefault()
                .post(new DatabaseDownloadFailureEvent(result.getCode(), result.getMessage()));

            cacheListener.onFailure(result);
            simpleListener.onFailure(result);
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
}