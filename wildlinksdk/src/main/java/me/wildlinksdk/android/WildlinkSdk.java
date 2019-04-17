package me.wildlinksdk.android;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.api.ApiException;
import java.lang.ref.WeakReference;
import java.util.AbstractMap;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import me.wildlinksdk.android.analytics.WlFirebaseAnalytics;
import me.wildlinksdk.android.api.ApiError;
import me.wildlinksdk.android.api.ApiModule;
import me.wildlinksdk.android.api.CloudServiceApi;
import me.wildlinksdk.android.api.Sender;
import me.wildlinksdk.android.api.SharedPrefsUtil;
import me.wildlinksdk.android.api.models.LoggerListener;
import me.wildlinksdk.android.api.models.SenderListener;
import me.wildlinksdk.android.api.models.XidRequest;
import me.wildlinksdk.android.api.models.events.WlDisableEvent;
import me.wildlinksdk.android.api.models.events.WlEnabledEvent;
import me.wildlinksdk.android.api.models.events.WlErrorEvent;
import me.wildlinksdk.android.models.Item;
import me.wildlinksdk.android.models.Vanity;
import me.wildlinksdk.android.models.WildlinkResult;
import me.wildlinksdk.android.services.WildlinkForegroundService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by ron on 10/3/17.
 * Threadsafe singleton for Wildlink
 */

public final class WildlinkSdk implements Api, InternalApi {
    private static final String TAG = WildlinkSdk.class.getSimpleName();

    private static WildlinkSdk api;


    private WeakReference<Activity> activityWeakReference;
    private WlFirebaseAnalytics wlAanalytics;
    private boolean bound;
    private WildlinkForegroundService mService;

    private Context context;
    private UUID uuid;
    private String appId;
    private String clientSecret;
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            try {
                WildlinkForegroundService.LocalBinder binder =
                    (WildlinkForegroundService.LocalBinder) service;
                Log.d(TAG, "bound to service");
                mService = binder.getService();
                bound = true;
            } catch (Exception e) {
                ApiError apiError = new ApiError(ApiError.SERVICE_BINDING,
                    "Unable to bind to service " + e.getMessage());
                EventBus.getDefault().post(new WlErrorEvent(apiError));
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
            Log.d(TAG, "disconnected to service");
        }
    };

    private WildlinkSdk() {

    }

    public static WildlinkSdk getIntance() {

        if (api == null) {
            api = new WildlinkSdk();
        }

        return api;
    }


    public void init(Context context,  String appId, String clientSecret) {




        this.context = context;

        this.appId = appId;
        this.clientSecret = clientSecret;

        EventBus.getDefault().register(this);

        ProviderImpl provider = new ProviderImpl(context, appId, clientSecret);

        ApiModule.INSTANCE.init(provider);

        SharedPrefsUtil prefs = ApiModule.INSTANCE.getSharedPrefsUtil();

       // prefs.storeUuid(provider.provideDeviceKey().toString());

        if (prefs.getGoogleClientCid() == null) {
            String cid = UUID.randomUUID().toString();

            prefs.storeGoogleCid(cid);
        }

    }



    public String getBaseApiUrl() {
        return ApiModule.INSTANCE.getSharedPrefsUtil().getBaseApiUrl();
    }

    public void forceDownloadConceptsDatabase(SimpleListener listener) {
        ApiModule.INSTANCE.getCloudServiceApi().downloadConceptsDatabase(false, listener);
    }

    public void downloadMerchantsDatabase(SimpleListener listener) {
        ApiModule.INSTANCE.getCloudServiceApi().downloadMerchantsDatabaseSqlite(true, listener);
    }

    /***
     * rely upon the event bus
     */
    public void downloadMerchantsDatabase() {


        ApiModule.INSTANCE.getCloudServiceApi()
            .downloadMerchantsDatabaseSqlite(true, new SimpleListener() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailure(final ApiError error) {

                }
            });
    }

    public void forceDownloadMerchantsDatabase(SimpleListener listener)
        throws WildlinkInternalUseException {

        if (!(listener instanceof WildlinkForegroundService)) {
            throw new WildlinkInternalUseException("downloadMerchantsDatabase()");
        }

        ApiModule.INSTANCE.getCloudServiceApi().downloadMerchantsDatabaseSqlite(false, listener);
    }

    public void downloadMerchantsDatabase(boolean useCache, SimpleListener listener)
        throws WildlinkInternalUseException {

        if (!(listener instanceof WildlinkForegroundService)) {
            throw new WildlinkInternalUseException("downloadMerchantsDatabase()");
        }

        ApiModule.INSTANCE.getCloudServiceApi().downloadMerchantsDatabaseSqlite(useCache, listener);
    }

    public void downloadMerchantsDatabaseSqlite(boolean useCache, SimpleListener listener)
        throws WildlinkInternalUseException {

        if (!(listener instanceof WildlinkForegroundService)) {
            throw new WildlinkInternalUseException("downloadMerchantsDatabase()");
        }

        ApiModule.INSTANCE.getCloudServiceApi().downloadMerchantsDatabaseSqlite(useCache, listener);
    }

    public List<Item> searchItems(String buffer) {
        List<Item> results = ApiModule.INSTANCE.getCache().searchItems(buffer);
        return results;
    }

    public void getPaypalSenderData(PaypalSenderDataListener listener) {

        ApiModule.INSTANCE.getCloudServiceApi().getPaypalSenderData(listener);
    }

    public void setUserData(Sender sender, SimpleListener listener) {
        ApiModule.INSTANCE.getCloudServiceApi().setUserData(sender, listener);
    }

    public void getWildlinkHistory(HistoryListener listener) {
        ApiModule.INSTANCE.getCloudServiceApi().getWildlinkHistory(listener);
    }

    public void getEarnings(EarningsListenter listener) {

        ApiModule.INSTANCE.getCloudServiceApi().getEarnings(listener);
    }

    public void clickStats(CloudServiceApi.byEnum by, Long start, Long end,
        ClickstatsListener listener) {
        ApiModule.INSTANCE.getCloudServiceApi().clickStats(by, start, end, listener);
    }

    public void getSender(GetSenderListener listener) {

        ApiModule.INSTANCE.getCloudServiceApi().getSender(listener);
    }

    public void setUserData(String phone, String email, String paymentType,
        SimpleListener listener) {

        ApiModule.INSTANCE.getCloudServiceApi().setUserData(phone, email, paymentType, listener);
    }

    public void createSender(String phoneNumber, SenderListener listener) {

        ApiModule.INSTANCE.getCloudServiceApi().createSender(phoneNumber, listener);
    }


    public void setPaypalCredentials(RecipientType type, String data, SimpleListener listener) {

        ApiModule.INSTANCE.getCloudServiceApi().setPaypalCredentials(type, data, listener);
    }

    public void getPaypalPayment(PaypalPaymentListener listener) {

        ApiModule.INSTANCE.getCloudServiceApi().getPaypalPayment(listener);
    }

    public List<WildlinkResult> rankCharacterSequenceList(String word, List<CharSequence> words) {
        return ApiModule.INSTANCE.getRankingApi().rankCharSequenceList(word, words);
    }

    public List<WildlinkResult> rankWildlinkResultList(String word, List<WildlinkResult> words) {
        return ApiModule.INSTANCE.getRankingApi().rankWildlinkResult(word, words);
    }

    public List<WildlinkResult> rankStringList(String buffer, List<String> words) {
        return ApiModule.INSTANCE.getRankingApi().rankStringList(buffer, words);
    }

    public void phraseLogger(final String thePhraseContext, final String contextWithLink,
        long timestamp, final String destinationApp, final LoggerListener listener) {

        ApiModule.INSTANCE.getCloudServiceApi()
            .phraseLogger(thePhraseContext, contextWithLink, timestamp, destinationApp, listener);
    }

    public void updateAttribution(String s) {
        ApiModule.INSTANCE.getCloudServiceApi().updateAttribution(s);
    }

    public void updateInstallChannel(String installChannel) {
        ApiModule.INSTANCE.getCloudServiceApi().updateInstallChannel(installChannel);
    }

    public void updateXid(XidRequest xidRequest, SimpleListener listner) {
        ApiModule.INSTANCE.getCloudServiceApi().updateXid(xidRequest, listner);
    }

    public void acquisition(String rawInstallReferrer) {
        ApiModule.INSTANCE.getCloudServiceApi().acquisition(rawInstallReferrer);
    }

    public void createVanityUrl(final String url, final VanityListener listener) {

        ApiModule.INSTANCE.getCloudServiceApi().createDevice(new SimpleListener() {
            @Override
            public void onSuccess() {
                ApiModule.INSTANCE.getCloudServiceApi().createVanityUrl(url, listener);
            }

            @Override
            public void onFailure(final ApiError error) {
                listener.onFailure(error);
            }
        });


    }

    public void setEnv(String env, int tableType, SimpleListener listener) {
        ApiModule.INSTANCE.setEnv(env, tableType, listener);
    }

    public void startInputMonitor() throws Exception {
        Log.d(TAG, "startInputMonitor()");

        ApiModule apiModuleInstance = ApiModule.INSTANCE;

        Context context = apiModuleInstance.getProvider().provideApplicationContext();

        if (!(apiModuleInstance.getProvider() instanceof WildlinkSdk.KeyboardMonitorProvider)) {
            throw new Exception(
                "In order to use the input monitor you must provide Wildlink.KeyboardMonitorProvider");
        }

        apiModuleInstance.getSharedPrefsUtil()
            .storeWhichTableTypeClientUses(Constants.KEY_CLIENT_TABLE_TYPE,
                Constants.TABLE_CONCEPTS);

        apiModuleInstance.getCloudServiceApi().downloadConceptsDatabase(true, new SimpleListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Success downloading  db");
            }

            @Override
            public void onFailure(ApiError error) {

                Log.d(TAG, "Failure downloading concept db="
                    + error.getCode()
                    + ", message="
                    + error.getMessage());
            }
        });
    }

    @Deprecated
    public void startClipboardMonitoringService() {


        ApiModule.INSTANCE.getCloudServiceApi().createDevice(new SimpleListener() {
            @Override
            public void onSuccess() {


            }

            @Override
            public void onFailure(final ApiError error) {

            }
        });



    }

    public void stopMonitoring() {

        Intent serviceIntent =
            new Intent(ApiModule.INSTANCE.getProvider().provideApplicationContext(),
                WildlinkForegroundService.class);
        serviceIntent.putExtra(Constants.WILDLINK_SERVICE_STOP, true);
    }

    public void disableClipboardMonitoring() {

        ApiModule.INSTANCE.disableClipboardMonitoring();
    }

    public void enableClipboardMonitoring() {

        ApiModule.INSTANCE.enableClipboardMonitoring();
    }

    public boolean isClipboardMonitoringEnabled() {
        return ApiModule.INSTANCE.isClipboardMonitoringEnabled();
    }


    public void createVanityUrl(final String placement, final String url,final  VanityListener listener) {

        ApiModule.INSTANCE.getCloudServiceApi().createDevice(new SimpleListener() {
            @Override
            public void onSuccess() {
                ApiModule.INSTANCE.getCloudServiceApi().createVanityUrl(placement, url, listener);
            }

            @Override
            public void onFailure(final ApiError error) {
                listener.onFailure(error);
            }
        });


    }

    /***
     * pass in url
     * @param url url to convert
     * @return null if not converted. Vanity object returned with getShortUrl being the converted link.
     */
    public Vanity createVanityUrl(String placement, String url) throws Exception{

        ApiError error = ApiModule.INSTANCE.getCloudServiceApi().createDevice();
        if( error != null) {
            throw new Exception(error.getMessage());
        }

        return ApiModule.INSTANCE.getCloudServiceApi().createVanityUrl(placement, url);
    }

    /***
     * Returns an array of zero or more commission items earned for the given device.
     * Each commission amount will be rounded to whole cents for display purposes.
     * The actual amount paid to the user when all eligible commissions are combined
     * for a payout may be vary slightly due to rounding differences.
     * @param listener CommissionStatDetailsListener contains
     *  public void onFailure(ApiError error);  ApiError contains code and message.
     *  if there was a response code from the server code will be the http status code
     *  and the message will be a reason for the error.
     *  if there is no response from the serer code will be ApiError.UNKNOWN_ERROR (-1)
     *  public void onSuccess(List<Commission> commissions);
     */
    public void getCommissionStatsDetails(final CommissionStatDetailsListener listener) {
        ApiModule apiModuleInstance = ApiModule.INSTANCE;
        apiModuleInstance.getCloudServiceApi().getCommissionStatDetails(listener);
    }

    /***
     *
     * @param merchantIds list of integers for merchant Id's.
     * @param listener  MerchantListener contains
     *  public void onFailure(ApiError error);  ApiError contains code and message.
     *  if there was a response code from the server code will be the http status code
     *  and the message will be a reason for the error.
     *  if there is no response from the serer code will be ApiError.UNKNOWN_ERROR (-1)
     *  public void onSuccess(List<Merchant> merchants);
     */
    public void getMerchants(List<Long> merchantIds, MerchantListener listener) {
        ApiModule apiModuleInstance = ApiModule.INSTANCE;
        apiModuleInstance.getCloudServiceApi().getMerchants(merchantIds, listener);
    }

    public String getFcmToken() {
        return ApiModule.INSTANCE.getSharedPrefsUtil().getFirebaseRefreshedToken();
    }

    public String getUuid() {
        return ApiModule.INSTANCE.getSharedPrefsUtil().getUuid();
    }

    /***
     *
     * @param listener resturns onSuccess or onFailure(ApiError error) so you know what is going on
     */
    public void startClipboardMonitoringService(final SimpleListener listener) {

        ApiModule.INSTANCE.getCloudServiceApi().createDevice(new SimpleListener() {
            @Override
            public void onSuccess() {
                handleStartClipboardMonitor(listener);
            }

            @Override
            public void onFailure(final ApiError error) {
              listener.onFailure(error);
            }
        });

    }

    private void startMonitor(final SimpleListener listener) {

    }
    private void handleStartClipboardMonitor(final SimpleListener listener) {
        Context context = ApiModule.INSTANCE.getProvider().provideApplicationContext();

        Log.d(TAG, "apiModuleInstance.getSharedPrefsUtil().isEnabled="
            + ApiModule.INSTANCE.getSharedPrefsUtil().isWlEnabled());

        if (serviceIsRunningInForeground(context)) {
            Log.d(TAG, "Service already running");

            Log.d(TAG,
                "service was already running but domain database had a failure. Attempting download");
            ApiModule.INSTANCE.getCache().downloadMerchantsDatabase(true, new SimpleListener() {
                @Override
                public void onSuccess() {

                    Log.d(TAG, "Success");
                }

                @Override
                public void onFailure(ApiError error) {
                    listener.onFailure(error);
                    Log.d(TAG, "Failure downloading domain db="
                        + error.getCode()
                        + ", message="
                        + error.getMessage());
                }
            });

            return;
        }

        Log.d(TAG, "checking boolean values");

        Intent intent = new Intent(context, WildlinkForegroundService.class);

        Bundle bundle = new Bundle();

        int startupType = intent.getIntExtra(Constants.KEY_WL_SERVICE_STARTUP_TYPE, -1);

        Log.d(TAG, "startupType=" + startupType);
        if (startupType == Constants.WL_INT_STARTUP_PACKAGE_UPDATED) {
            intent.putExtra(Constants.KEY_WL_SERVICE_STARTUP_TYPE,
                Constants.WL_INT_STARTUP_PACKAGE_UPDATED);
        }

        intent.putExtra(Constants.WILDLINK_SERVICE_STARTED_MANUALLY, true);

        ApiModule.INSTANCE.getCache().downloadMerchantsDatabase(true, listener);

        context.startService(intent);


        Log.d(TAG, "Intent(context,WildlinkForegroundService.class)");
        context.bindService(new Intent(context, WildlinkForegroundService.class), mConnection,
            Context.BIND_AUTO_CREATE | Context.BIND_WAIVE_PRIORITY);

        listener.onSuccess();

    }

    /**
     * Returns true if this is a foreground service.
     *
     * @param context The {@link Context}.
     */
    private boolean serviceIsRunningInForeground(Context context) {

        ActivityManager manager =
            (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        Log.d(TAG, "checking if our service is already running");

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
            Integer.MAX_VALUE)) {

            String thisClass = WildlinkForegroundService.class.getName();
            String serviceClass = service.service.getShortClassName();

            if (thisClass.equals(serviceClass)) {
                Log.d(TAG, "service is running");
                return true;
            }
        }

        Log.d(TAG, "Service is not running yet");

        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(WlEnabledEvent enableEvent) throws Exception {

        ApiModule.INSTANCE.getSharedPrefsUtil().setWlEnabled();
        startClipboardMonitoringService();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(WlDisableEvent event) {

    }

    public boolean hasWlEnableActivityBeenShownToUserAlready() {

        return ApiModule.INSTANCE.getSharedPrefsUtil()
            .hasWlEnableActivityHasBeenShownToUserAlready();
    }

    public void getWebData(String url, WebListener listener) {

        ApiModule apiModuleInstance = ApiModule.INSTANCE;

        apiModuleInstance.getCloudServiceApi().getWebData(url, listener);
    }

    public enum RecipientType {
        EMAIL,
        PHONE
    }

    public enum PaymentType {
        PAYPAL
    }

    public interface Provider {
        public Context provideApplicationContext();

        /***
         * the very first time the db is pulled from the server.
         * after that each time sdkInitialize is called it will check if
         * this many minutes has passed. If it has passed it will refresh the
         * db from the wildlink server. otherwise no network connectivity
         * will occur unless you call forceDbRefresh()
         *
         *@return RefreshRate value=value, timeUnits = Calendar.HOUR, Calendar.MINUTE,Calendar.MILLISECOND
         */
        public RefreshRate provideDatabaseCacheRefreshRate();

        public String provideClientSecret();

        public String provideWildlinkAppId();
    }

    public interface KeyboardMonitorProvider extends Provider {

    }

    private class ProviderImpl implements Provider {

        private Context appContext;
        private String appId;
        private String appSecret;


        public ProviderImpl(Context context,  String appId, String appSecret) {

            this.appContext = context;
            this.appId = appId;
            this.appSecret = appSecret;

        }

        @Override
        public Context provideApplicationContext() {
            return appContext;
        }



        @Override
        public RefreshRate provideDatabaseCacheRefreshRate() {
            return new RefreshRate(Calendar.HOUR, 24);
        }

        @Override
        public String provideClientSecret() {
            return appSecret;
        }

        @Override
        public String provideWildlinkAppId() {
            return appId;
        }
    }
}



