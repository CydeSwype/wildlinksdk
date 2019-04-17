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
import me.wildlinksdk.android.db.ConceptsDataSource;
import me.wildlinksdk.android.models.ConceptList;
import okhttp3.Response;

public class DownloadConceptsDatabase extends AsyncTask<String, Void, ApiError> {
    private static final String TAG = DownloadConceptsDatabase.class.getSimpleName();
    private SimpleListener simpleListener;
    private Gson gson;
    private HttpApi httpApi;
    private SharedPrefsUtil prefs;
    private Cache.CacheListener cacheListener;
    private WlGoogleAnalytics googleAnalytics;
    private String appId;

    public DownloadConceptsDatabase(Cache.CacheListener cacheListener, ApiModule apiModule,
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

        ConceptsDataSource ds = new ConceptsDataSource(ApiModule.INSTANCE.getDatabase());
        try {

            ApiError apiError = null;
            boolean isEnd = false;
            String cursor = null;
            String baseUrl = prefs.getBaseApiUrl();

            int idCount = 0;
            while (apiError == null && isEnd == false) {
                final StringBuilder url = new StringBuilder();

                url.append(baseUrl);
                url.append("/v1/concept");
                url.append("?limit=1000");
                if (cursor != null) {
                    url.append("&cursor=" + cursor);
                }

                Log.d(TAG, "url=" + url);

                Response response = null;
                try {
                    response = httpApi.getRequestSync(url.toString());
                    if (response.isSuccessful()) {

                        if (response.code() >= HttpURLConnection.HTTP_OK
                            && response.code() < HttpURLConnection.HTTP_MOVED_TEMP) {

                            InputStream in = response.body().byteStream();

                            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                            String result, line = reader.readLine();
                            result = line;
                            while ((line = reader.readLine()) != null) {
                                result += line;
                            }

                            response.body().close();

                            ConceptList conceptList = gson.fromJson(result, ConceptList.class);
                            if (conceptList.getConceptsList() == null
                                || conceptList.getConceptsList().size() == 0) {
                                isEnd = true;
                                Log.d(TAG, "database downloaded successfully");
                                return null;
                            }

                            Log.d(TAG,
                                "-------inserting data" + conceptList.getConceptsList().size());

                            try {
                                ds.beginTransaction();

                                ds.removeAll();

                                for (int i = 0; i < conceptList.getConceptsList().size(); i++) {

                                    ds.insert(conceptList.getConceptsList().get(i).getId(),
                                        conceptList.getConceptsList().get(i).getValue());
                                }
                                ds.endTransactionSuccessfull();
                            } finally {
                                ds.endTransaction();
                            }
                            cursor = conceptList.getNextCursor();
                        } else if (response.code() == HttpURLConnection.HTTP_NOT_FOUND) {

                            Log.d(TAG, "page not found" + response.code());
                            ApiError error = new ApiError(response.code(), "Page not found");
                            simpleListener.onFailure(error);

                            isEnd = true;
                            return new ApiError(response.code(),
                                "" + HttpURLConnection.HTTP_NOT_FOUND + ". Page not found");
                        } else if (response.code() == HttpURLConnection.HTTP_INTERNAL_ERROR) {

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
            Log.d(TAG, "DownloadConceptsDatabase failed with Exception=" + e.getMessage());
            return new ApiError(ApiError.UNKNOWN_ERROR, e.getMessage());
        } finally {

        }
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
            cacheListener.onSuccess();
            simpleListener.onSuccess();
            googleAnalytics.sendEvent("database", "domain", appId);
        } else {

            prefs.setMerchantsDatabaseFailure();

            simpleListener.onFailure(result);
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
}