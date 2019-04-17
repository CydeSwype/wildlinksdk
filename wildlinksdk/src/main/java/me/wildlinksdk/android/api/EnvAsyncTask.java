package me.wildlinksdk.android.api;

import android.os.AsyncTask;
import android.util.Log;
import java.util.UUID;
import me.wildlinksdk.android.BuildConfig;
import me.wildlinksdk.android.Constants;
import me.wildlinksdk.android.SimpleListener;
import me.wildlinksdk.android.db.WlSqLiteOpenHelper;

public class EnvAsyncTask extends AsyncTask<String, Void, ApiError> {
    private static final String TAG = EnvAsyncTask.class.getSimpleName();
    private SimpleListener simpleListener;
    private ApiModule apiModule;
    private String env;
    private int clientTable;

    public EnvAsyncTask(String env, int clientTable, ApiModule apiModule,
        SimpleListener simpleListener) {
        this.simpleListener = simpleListener;
        this.apiModule = apiModule;
        this.env = env;
        this.clientTable = clientTable;
    }

    @Override
    protected ApiError doInBackground(String... params) {

        try {

            SharedPrefsUtil prefs = apiModule.getSharedPrefsUtil();

            String cid = UUID.randomUUID().toString();
            Log.d(TAG, "CID=" + cid);
            prefs.storeGoogleCid(cid);

            String uuid = UUID.randomUUID().toString();
            Log.d(TAG, "uuid=" + uuid);
            prefs.storeUuid(uuid);
            ApiModule.INSTANCE.closeDatabase();
            WlSqLiteOpenHelper helper = null;
            if (env.equalsIgnoreCase("prod")) {

                helper = new WlSqLiteOpenHelper(apiModule.getProvider().provideApplicationContext(),
                    "wildlink_sqlite_prod.db", Integer.valueOf(BuildConfig.SQLITE_DB_VERSION));
            } else {
                helper = new WlSqLiteOpenHelper(apiModule.getProvider().provideApplicationContext(),
                    "wildlink_sqlite_dev.db", Integer.valueOf(BuildConfig.SQLITE_DB_VERSION));
            }
            helper.onUpgrade(ApiModule.INSTANCE.getDatabase(),
                Integer.valueOf(BuildConfig.SQLITE_DB_VERSION),
                Integer.valueOf(BuildConfig.SQLITE_DB_VERSION));

            Log.d(TAG, "downloading the db");

            if (clientTable == Constants.TABLE_CONCEPTS) {
                ApiModule.INSTANCE.getCache().downloadConceptsDatabase(false, new SimpleListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "force downloadMerchantsDatabase");
                        simpleListener.onSuccess();
                    }

                    @Override
                    public void onFailure(final ApiError error) {
                        Log.d(TAG, "force downloadMerchantsDatabase failure code="
                            + error.getCode()
                            + ",message="
                            + error.getMessage());
                        simpleListener.onFailure(error);
                    }
                });
            } else {
                ApiModule.INSTANCE.getCache()
                    .downloadMerchantsDatabase(false, new SimpleListener() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "force downloadMerchantsDatabase");
                            simpleListener.onSuccess();
                        }

                        @Override
                        public void onFailure(final ApiError error) {
                            Log.d(TAG, "force downloadMerchantsDatabase failure code="
                                + error.getCode()
                                + ",message="
                                + error.getMessage());
                            simpleListener.onFailure(error);
                        }
                    });
            }
            return null;
        } catch (Exception e) {
            Log.d(TAG, "EnvAsyncTask failed with Exception=" + e.getMessage());
            ApiError error = new ApiError(ApiError.UNKNOWN_ERROR, e.getMessage());
            simpleListener.onFailure(error);
            return error;
        } finally {

        }
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(ApiError result) {

    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
}

