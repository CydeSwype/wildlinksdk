package me.wildlinksdk.android.api;

import android.content.Intent;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import me.wildlinksdk.android.Constants;
import me.wildlinksdk.android.RefreshRate;
import me.wildlinksdk.android.SimpleListener;
import me.wildlinksdk.android.db.ConceptsDataSource;
import me.wildlinksdk.android.db.MerchantsDataSource;
import me.wildlinksdk.android.events.DatabaseDownloadCompleteEvent;
import me.wildlinksdk.android.models.Item;
import me.wildlinksdk.android.models.MerchantItemDomain;
import me.wildlinksdk.android.services.TokenService;
import me.wildlinksdk.android.utilities.Utilities;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;

/**
 * Created by ron on 10/9/17.
 */

public final class Cache {

    private final static String TAG = Cache.class.getSimpleName();
    private LevenshteinDistance levenshteinDistance;
    private Utilities utilities;
    private ApiModule apiModule;
    private boolean cachedData;
    private Object refreshingDomainDatabaseObject;
    private volatile boolean isRefreshingDomainDatabase;
    private Object refreshingByKindObjectLock;
    private volatile boolean isRefeshingConceptsDb;

    private volatile boolean isRefeshingFeaturedStoresDb;
    private Object refreshingFeaturedStoresObjectLock;

    private ConceptsDataSource mConceptsDataSource;
    private MerchantsDataSource merchantsDataSource;

    public Cache(ApiModule apiModule) {

        this.apiModule = apiModule;

        levenshteinDistance = new LevenshteinDistance();
        utilities = new Utilities();
        refreshingDomainDatabaseObject = new Object();
        refreshingByKindObjectLock = new Object();

        refreshingFeaturedStoresObjectLock = new Object();

        mConceptsDataSource = new ConceptsDataSource(apiModule.getDatabase());
        merchantsDataSource = new MerchantsDataSource(apiModule.getDatabase());
    }

    /***
     * @param word thing to search for (contains)
     * @return MerchantsItem url or null
     */
    public MerchantItemDomain searchMerchantItem(String word) {

        return merchantsDataSource.search(word);
    }

    public List<Item> searchItems(String buffer) {

        List<Item> finalList = null;
        try {
            finalList = new ArrayList<Item>();

            if (buffer == null || buffer.length() == 0) {
                return finalList;
            }
            // we need to remove the urls if there are any in the last 4 items.

            List<String> bufferNoHttpLinks = utilities.getQueryPhrases(buffer);

            finalList = mConceptsDataSource.searchBuffer(bufferNoHttpLinks.get(0));
        } catch (Exception e) {
            return finalList;
        }

        return finalList;
    }

    public void downloadMerchantsDatabase(boolean cache, final SimpleListener listener) {

        Log.d(TAG, "downloadMerchantsDatabase from APi manager");

        final HttpApi httpApi = apiModule.getHttpApi();
        final Gson gson = apiModule.getGson();

        if (!isMerchantsCacheStale() && cache) {
            Log.v(TAG,
                "no refresh required cache interval still valid, NO database was downloaded, you should be using cached value");
            isRefreshingDomainDatabase = false;
            EventBus.getDefault().post(new DatabaseDownloadCompleteEvent());
            listener.onSuccess();
            return;
        }

        synchronized (refreshingDomainDatabaseObject) {
            isRefreshingDomainDatabase = true;
        }

        new DownloadMerchantsDatabase(new CacheListener() {
            @Override
            public void onSuccess() {
                synchronized (refreshingDomainDatabaseObject) {
                    isRefreshingDomainDatabase = false;
                }

                String token = FirebaseInstanceId.getInstance().getToken();
                Log.d(TAG, "refreshToken=" + token);

                if (token != null && !token.equals(
                    apiModule.getSharedPrefsUtil().getFirebaseRefreshedToken())) {

                    Log.d(TAG, "refreshToken=" + token);
                    Intent intentService =
                        new Intent(apiModule.getProvider().provideApplicationContext(),
                            TokenService.class);
                    intentService.putExtra(Constants.KEY_WL_FIREBASE_TOKEN, token);
                    Log.d(TAG, "starting service to update firebase token");
                    apiModule.getProvider().provideApplicationContext().startService(intentService);
                    apiModule.getSharedPrefsUtil().storeFirebaseRefreshedToken(token);
                }
            }

            @Override
            public void onFailure(ApiError apiError) {
                synchronized (refreshingDomainDatabaseObject) {
                    isRefreshingDomainDatabase = false;

                    listener.onFailure(apiError);
                }
            }
        }, apiModule, listener).execute();
    }

    public boolean isMerchantsCacheStale() {

        final SharedPrefsUtil prefs = apiModule.getSharedPrefsUtil();
        final RefreshRate refreshRate = apiModule.getProvider().provideDatabaseCacheRefreshRate();

        final MerchantsDataSource ds = new MerchantsDataSource(ApiModule.INSTANCE.getDatabase());

        Log.d(TAG, "isMerchantsCacheStale()");
        Log.d(TAG, "refreshRate By client units=" + refreshRate.timeUnits);
        Log.d(TAG, "refreshRate By client value=" + refreshRate.value);

        final int count = ds.count();

        if (count == 0) {
            Log.d(TAG, "Merchants db is empty");
            return true;
        }

        if (refreshRate == null || refreshRate.value == null) {
            Log.d(TAG, "stale db, refresh rate set to 1 day by default");
            refreshRate.timeUnits = Calendar.HOUR;
            refreshRate.value = 24;
        }
        DateTime lastRefreshDate = prefs.getMerchantsLastRefreshDate();
        if (lastRefreshDate == null) {
            Log.d(TAG, "stale db, refresh required");
            return true;
        }
        DateTime expiresOnDate = null;
        switch (refreshRate.timeUnits) {
            case Calendar.HOUR:
                expiresOnDate = lastRefreshDate.plusHours(refreshRate.value);
                break;
            case Calendar.MINUTE:
                expiresOnDate = lastRefreshDate.plusMinutes(refreshRate.value);
                break;
            case Calendar.MONTH:
                expiresOnDate = lastRefreshDate.plusMonths(refreshRate.value);
                break;
            case Calendar.MILLISECOND:
                expiresOnDate = lastRefreshDate.plusMillis(refreshRate.value);
                break;
            default:
                throw new InvalidParameterException(
                    "Must use Calendar.MINUTE, MONTH, or MILLISECOND only");
        }
        DateTime now = DateTime.now();

        if (expiresOnDate.isBefore(now)) {
            Log.d(TAG, "merchants cache is stale, last cache date plus value="
                + refreshRate.value
                + ", timeUnits="
                + refreshRate.timeUnits);
            return true;
        } else {
            Log.d(TAG, "merchants cache is not stale");
        }
        Log.d(TAG, "expires On=" + expiresOnDate.toString());
        Log.d(TAG, "now()=" + now.toString());

        return false;
    }

    public void downloadConceptsDatabase(boolean cache, final SimpleListener listener) {

        Log.d(TAG, "downloadConceptsDatabase from APi manager");

        final HttpApi httpApi = apiModule.getHttpApi();
        final Gson gson = apiModule.getGson();

        if (!isConceptsCacheStale() && cache) {
            Log.v(TAG,
                "no refresh required cache interval still valid, NO database was downloaded, you should be using cached value");
            isRefreshingDomainDatabase = false;
            listener.onSuccess();
            return;
        }

        synchronized (refreshingDomainDatabaseObject) {
            isRefreshingDomainDatabase = true;
        }

        new DownloadConceptsDatabase(new CacheListener() {
            @Override
            public void onSuccess() {
                String token = FirebaseInstanceId.getInstance().getToken();
                Log.d(TAG, "refreshToken=" + token);

                synchronized (refreshingDomainDatabaseObject) {
                    isRefreshingDomainDatabase = false;
                }

                if (token != null && !token.equals(
                    apiModule.getSharedPrefsUtil().getFirebaseRefreshedToken())) {

                    Log.d(TAG, "refreshToken=" + token);
                    Intent intentService =
                        new Intent(apiModule.getProvider().provideApplicationContext(),
                            TokenService.class);
                    intentService.putExtra(Constants.KEY_WL_FIREBASE_TOKEN, token);
                    Log.d(TAG, "starting service to update firebase token");
                    apiModule.getProvider().provideApplicationContext().startService(intentService);
                    apiModule.getSharedPrefsUtil().storeFirebaseRefreshedToken(token);
                }
            }

            @Override
            public void onFailure(ApiError apiError) {
                synchronized (refreshingDomainDatabaseObject) {
                    isRefreshingDomainDatabase = false;
                    listener.onFailure(apiError);
                }
            }
        }, apiModule, listener).execute();
    }

    public boolean isConceptsCacheStale() {

        final SharedPrefsUtil prefs = apiModule.getSharedPrefsUtil();
        final RefreshRate refreshRate = apiModule.getProvider().provideDatabaseCacheRefreshRate();

        final ConceptsDataSource ds = new ConceptsDataSource(apiModule.getDatabase());
        final int count = ds.count();
        if (count == 0) {
            Log.d(TAG, "Concepts db is empty");
            return true;
        }
        if (prefs.hasConceptsDatabaseFailure()) {
            return true;
        }

        Log.d(TAG, "isConceptsCacheStale()");
        Log.d(TAG, "refreshRate By client units=" + refreshRate.timeUnits);
        Log.d(TAG, "refreshRate By client value=" + refreshRate.value);

        if (refreshRate == null || refreshRate.value == null) {
            Log.d(TAG, "stale db, refresh rate set to 1 day by default");
            refreshRate.timeUnits = Calendar.HOUR;
            refreshRate.value = 24;
        }
        DateTime lastRefreshDate = prefs.getConceptsLastRefreshDate();
        if (lastRefreshDate == null) {
            Log.d(TAG, "stale db, refresh required");
            return true;
        }
        DateTime expiresOnDate = null;
        switch (refreshRate.timeUnits) {
            case Calendar.HOUR:
                expiresOnDate = lastRefreshDate.plusHours(refreshRate.value);
                break;
            case Calendar.MINUTE:
                expiresOnDate = lastRefreshDate.plusMinutes(refreshRate.value);
                break;
            case Calendar.MONTH:
                expiresOnDate = lastRefreshDate.plusMonths(refreshRate.value);
                break;
            case Calendar.MILLISECOND:
                expiresOnDate = lastRefreshDate.plusMillis(refreshRate.value);
                break;
            default:
                throw new InvalidParameterException(
                    "Must use Calendar.MINUTE, MONTH, or MILLISECOND only");
        }
        DateTime now = DateTime.now();

        if (expiresOnDate.isBefore(now)) {
            Log.d(TAG, "concepts cache is stale, last cache date plus value="
                + refreshRate.value
                + ", timeUnits="
                + refreshRate.timeUnits);
            return true;
        } else {
            Log.d(TAG, "cache is not stale");
        }
        Log.d(TAG, "expires On=" + expiresOnDate.toString());
        Log.d(TAG, "now()=" + now.toString());

        return false;
    }

    public static enum SearchCase {
        INSENSITIVE,
        SENSITIVE
    }

    protected interface CacheListener {
        public void onSuccess();

        void onFailure(ApiError apiError);
    }
}
