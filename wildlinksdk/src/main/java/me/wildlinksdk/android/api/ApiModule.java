package me.wildlinksdk.android.api;

/**
 * Created by ron on 10/4/17.
 */

import android.net.TrafficStats;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.requery.android.database.sqlite.SQLiteDatabase;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.net.SocketFactory;
import me.wildlinksdk.android.BuildConfig;
import me.wildlinksdk.android.Constants;
import me.wildlinksdk.android.SimpleListener;
import me.wildlinksdk.android.WildlinkSdk;
import me.wildlinksdk.android.analytics.WlFirebaseAnalytics;
import me.wildlinksdk.android.analytics.WlGoogleAnalytics;
import me.wildlinksdk.android.api.models.ApiErrorSerializer;
import me.wildlinksdk.android.db.WlSqLiteOpenHelper;
import me.wildlinksdk.android.utilities.Utilities;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import static android.content.Context.MODE_PRIVATE;

public enum ApiModule {
    INSTANCE;
    private static final String TAG = ApiModule.INSTANCE.getClass().getSimpleName();
    private SoftReference<SharedPrefsUtil> sharedPrefsUtilReference;
    private SoftReference<OkHttpClient> noAuthOkHttpClientReference;
    private SoftReference<OkHttpClient> okHttpClientReference;
    private WildlinkSdk.Provider provider;
    private SoftReference<Gson> gsonReference;
    private SoftReference<HttpApi> httpApi2Reference;
    private SoftReference<RankingApi> rankingApiReference;
    private SoftReference<MathApi> mathApi2Reference;
    private SoftReference<Cache> cacheReference;
    private SoftReference<CryptographyApi> cryptoReference;
    private SoftReference<NetworkUtils> networkUtilsSoftReference;
    private SoftReference<CloudServiceApi> cloudServiceApiReference;
    private SoftReference<OkHttpClient> okHttpClientQuickTimeoutReference;
    private MathApi mathApi;
    private SoftReference<WlFirebaseAnalytics> wlFirebaseAnalyticsReference;
    private SoftReference<WlGoogleAnalytics> wlGoogleAnalyticsReference;
    private WlSqLiteOpenHelper wlSqLiteOpenHelper;
    private SQLiteDatabase database;

    public void init(WildlinkSdk.Provider provider) {
        this.provider = provider;


        wlSqLiteOpenHelper =
            new WlSqLiteOpenHelper(provider.provideApplicationContext(), BuildConfig.SQLITE_DB_NAME,
                Integer.valueOf(BuildConfig.SQLITE_DB_VERSION));
    }

    public SharedPrefsUtil getSharedPrefsUtil() {

        if (provider == null) {
            return null;
        }
        if (sharedPrefsUtilReference == null || sharedPrefsUtilReference.get() == null) {
            SharedPrefsUtil sharedPrefsUtil = new SharedPrefsUtil(
                provider.provideApplicationContext()
                    .getSharedPreferences(Constants.SHARE_PREFERENCES_PRIVATE, MODE_PRIVATE));
            sharedPrefsUtilReference = new SoftReference<SharedPrefsUtil>(sharedPrefsUtil);
        }
        return sharedPrefsUtilReference.get();
    }

    public SQLiteDatabase getDatabase() {
        if (database == null) {
            database = wlSqLiteOpenHelper.getWritableDatabase();
        } else {
            if (!database.isOpen()) {
                database = wlSqLiteOpenHelper.getWritableDatabase();
            }
        }
        return database;
    }

    public void closeDatabase() {

        if (database != null) {
            database.close();
        }

        database = null;
    }

    public OkHttpClient getNoAuthOkHttpClient() {

        if (noAuthOkHttpClientReference == null || noAuthOkHttpClientReference.get() == null) {
            final OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(60 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(60 * 1000, TimeUnit.MILLISECONDS);

            if (BuildConfig.DEBUG) {
                try {
                    Utilities.trustEverythingInDebug(builder);
                } catch (Exception e) {

                }
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);

                builder.addInterceptor(logging);
            }

            builder.connectTimeout(60 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(60 * 1000, TimeUnit.MILLISECONDS);

            OkHttpClient client = builder.build();
            noAuthOkHttpClientReference = new SoftReference<OkHttpClient>(client);
        }
        return noAuthOkHttpClientReference.get();
    }

    public OkHttpClient getOkHttpClient() {

        if (okHttpClientReference == null || okHttpClientReference.get() == null) {
            final OkHttpClient.Builder builder = new OkHttpClient.Builder();

            // if( BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
            builder.interceptors().add(logging);

            HttpLoggingInterceptor logging2 = new HttpLoggingInterceptor();
            logging2.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.interceptors().add(logging2);
            // }

            AuthInterceptor authInterceptor = new AuthInterceptor(this);

            builder.retryOnConnectionFailure(true).interceptors().add(authInterceptor);

            builder.connectTimeout(60 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(60 * 1000, TimeUnit.MILLISECONDS);

            SocketFactory socketFactory = new DelegatingSocketFactory(SocketFactory.getDefault()) {
                @Override
                protected Socket configureSocket(Socket socket) throws IOException {
                    // set android
                    TrafficStats.tagSocket(socket);

                    return socket;
                }
            };
            OkHttpClient client = builder.socketFactory(socketFactory).build();

            // OkHttpClient client = builder.build();
            okHttpClientReference = new SoftReference<OkHttpClient>(client);
        }
        return okHttpClientReference.get();
    }

    public OkHttpClient getQuickTimeoutHttpClient() {

        if (okHttpClientQuickTimeoutReference == null
            || okHttpClientQuickTimeoutReference.get() == null) {
            final OkHttpClient.Builder builder = new OkHttpClient.Builder();

            AuthInterceptor authInterceptor = new AuthInterceptor(this);

            builder.interceptors().add(authInterceptor);

            builder.connectTimeout(2 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(2 * 1000, TimeUnit.MILLISECONDS);

            SocketFactory socketFactory = new DelegatingSocketFactory(SocketFactory.getDefault()) {
                @Override
                protected Socket configureSocket(Socket socket) throws IOException {
                    // set android
                    TrafficStats.tagSocket(socket);

                    System.out.println("configured");

                    return socket;
                }
            };

            OkHttpClient client = builder.socketFactory(socketFactory).build();

            okHttpClientQuickTimeoutReference = new SoftReference<OkHttpClient>(client);
        }
        return okHttpClientQuickTimeoutReference.get();
    }

    public Gson getGson() {

        if (gsonReference == null || gsonReference.get() == null) {
            GsonBuilder builderNoUser = new GsonBuilder().excludeFieldsWithoutExposeAnnotation();

            GsonBuilder builder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation();
            builder.registerTypeAdapter(ApiError.class, new ApiErrorSerializer());

            Gson gson = builder.create();

            gsonReference = new SoftReference<Gson>(gson);
        }
        return gsonReference.get();
    }

    public HttpApi getHttpApi() {
        if (httpApi2Reference == null || httpApi2Reference.get() == null) {

            HttpApi httpApi = new HttpApi(this);

            httpApi2Reference = new SoftReference<HttpApi>(httpApi);
        }
        return httpApi2Reference.get();
    }

    public Cache getCache() {

        if (cacheReference == null || cacheReference.get() == null) {

            Cache cacheApi = new Cache(this);

            cacheReference = new SoftReference<Cache>(cacheApi);
        }
        return cacheReference.get();
    }

    public CloudServiceApi getCloudServiceApi() {

        if (cloudServiceApiReference == null || cloudServiceApiReference.get() == null) {

            CloudServiceApi cloudServiceApi = new CloudServiceApi(this);

            cloudServiceApiReference = new SoftReference<CloudServiceApi>(cloudServiceApi);
        }
        return cloudServiceApiReference.get();
    }

    public CryptographyApi getCryptographyApi() {

        if (cryptoReference == null || cryptoReference.get() == null) {

            CryptographyApi cryptographyApi = new CryptographyApi(this);

            cryptoReference = new SoftReference<CryptographyApi>(cryptographyApi);
        }
        return cryptoReference.get();
    }

    public MathApi getMathApi() {
        if (mathApi2Reference == null || mathApi2Reference.get() == null) {

            MathApi mathApi = new MathApi(this);

            mathApi2Reference = new SoftReference<MathApi>(mathApi);
        }
        return mathApi2Reference.get();
    }

    public RankingApi getRankingApi() {
        if (rankingApiReference == null || rankingApiReference.get() == null) {

            RankingApi mathApi = new RankingApi(this);

            rankingApiReference = new SoftReference<RankingApi>(mathApi);
        }
        return rankingApiReference.get();
    }

    public NetworkUtils getNetworkApi() {
        if (networkUtilsSoftReference == null || networkUtilsSoftReference.get() == null) {

            NetworkUtils networkUtils = new NetworkUtils(getProvider().provideApplicationContext());

            networkUtilsSoftReference = new SoftReference<NetworkUtils>(networkUtils);
        }
        return networkUtilsSoftReference.get();
    }

    public WildlinkSdk.Provider getProvider() {
        return provider;
    }

    public void disableClipboardMonitoring() {
        getSharedPrefsUtil().disableClipboardMonitoring();
    }

    public void enableClipboardMonitoring() {
        getSharedPrefsUtil().enableClipboardMonitoring();
    }

    public boolean isClipboardMonitoringEnabled() {
        return getSharedPrefsUtil().isClipboardMonitoringEnabled();
    }

    public WlFirebaseAnalytics getFirebaseAnalytics() {
        if (wlFirebaseAnalyticsReference == null || wlFirebaseAnalyticsReference.get() == null) {

            WlFirebaseAnalytics analytics = new WlFirebaseAnalytics(this);

            wlFirebaseAnalyticsReference = new SoftReference<WlFirebaseAnalytics>(analytics);
        }
        return wlFirebaseAnalyticsReference.get();
    }

    public WlGoogleAnalytics getGoogleAnalytics() {
        if (wlGoogleAnalyticsReference == null || wlGoogleAnalyticsReference.get() == null) {

            WlGoogleAnalytics analytics = new WlGoogleAnalytics(this);

            wlGoogleAnalyticsReference = new SoftReference<WlGoogleAnalytics>(analytics);
        }
        return wlGoogleAnalyticsReference.get();
    }

    public void setEnv(final String env, int tableType, final SimpleListener listener) {
        new EnvAsyncTask(env, tableType, ApiModule.INSTANCE, listener).execute();
    }
}
