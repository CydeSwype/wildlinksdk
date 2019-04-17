package me.wildlinksdk.android.services;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import me.wildlinksdk.android.BuildConfig;
import me.wildlinksdk.android.Constants;
import me.wildlinksdk.android.PublicConstants;
import me.wildlinksdk.android.R;
import me.wildlinksdk.android.RefreshRate;
import me.wildlinksdk.android.SimpleListener;
import me.wildlinksdk.android.WildlinkInternalUseException;
import me.wildlinksdk.android.WildlinkSdk;
import me.wildlinksdk.android.WildlinkServiceError;
import me.wildlinksdk.android.WildlinkServiceEvent;
import me.wildlinksdk.android.api.ApiError;
import me.wildlinksdk.android.api.ApiModule;
import me.wildlinksdk.android.api.SharedPrefsUtil;
import me.wildlinksdk.android.api.models.ServiceStoppedEvent;
import me.wildlinksdk.android.api.models.events.WlErrorEvent;
import me.wildlinksdk.android.db.MerchantsDataSource;
import me.wildlinksdk.android.models.Vanity;
import me.wildlinksdk.android.ui.config.WlConfigActivity;
import me.wildlinksdk.android.utilities.Utilities;
import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;

public class WildlinkForegroundService extends Service {

    public static final StringBuilder log =
        new StringBuilder("--- STUFF THAT I SENT TO MY SERVER ---\n\n");
    private static final String RUNNING_BROADCAST_RECEIVER_INTENT_NAME = "wl_fg_service_broadcast";
    private static final String PING_BROADCAST_RECEIVER_INTENT_NAME =
        "wl_fg_service_broadcast_ping";
    private static final String APP_UID = "app_uid";
    private static final String TAG = WildlinkForegroundService.class.getSimpleName();
    private static int counter = 0;
    private final IBinder mBinder = new LocalBinder();
    long oldTime = 0;
    private NotificationManager notificationManager;
    private WildlinkSdk sdk;
    private boolean isMonitoring;

    private ApiModule apiModule;
    private String clientSecret;
    private String appId;
    private int refreshRateKey;
    private int refreshRateValue;
    private String vanityUrl;
    private CacheTimerTask cacheTimerTask;
    private Utilities utilities;
    private SharedPrefsUtil prefs;
    private WildlinkServiceHelper presenter;
    private String oldClipboardData = "";

    private boolean blocked;
    private MerchantsDataSource ds;
    private RemoteViews extraButtonRemoteViews;
    private ClipboardManager.OnPrimaryClipChangedListener listener =
        new ClipboardManager.OnPrimaryClipChangedListener() {

            public void onPrimaryClipChanged() {
                if (blocked) {
                    Log.d(TAG, "blocked");
                    return;
                }

                Log.d(TAG, "not blocked");
                performClipboardCheck();
            }
        };
    private Timer timer;
    private TimerTask timerTask;
    private BroadcastReceiver pingBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "pingBroadcastReceiver()");
            String appUid = intent.getStringExtra(APP_UID);
            String ourAppUid = getApplicationUid();
            Log.d(TAG, "Ping received appUid from intent=" + appUid);
            Log.d(TAG, "our app uid=" + ourAppUid);

            if (appUid.equalsIgnoreCase(ourAppUid)) {
                Log.d(TAG, "pingBroadcastReceiver ignoring broadcast this is me");
                return;
            }
            Log.d(TAG, "sending broadcast to other app");

            Intent nameIntent = new Intent(RUNNING_BROADCAST_RECEIVER_INTENT_NAME);
            nameIntent.putExtra(APP_UID, ourAppUid);
            sendBroadcast(nameIntent);
        }
    };


    private BroadcastReceiver isRunningBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "------in BroadcastReceiver()");
            String action = intent.getAction();
            Log.d(TAG, "Action=" + action);

            if (action.equalsIgnoreCase(RUNNING_BROADCAST_RECEIVER_INTENT_NAME)) {

                String intentAppUid = intent.getStringExtra(APP_UID);
                String ourAppUid = getApplicationUid();
                Log.d(TAG, "received appUid in intent=" + intentAppUid);
                Log.d(TAG, "our appUid=" + ourAppUid);

                if (intentAppUid.equalsIgnoreCase(ourAppUid)) {
                    Log.d(TAG, "in BroadcastReceiver ignoring broadcast this is me");
                    return;
                }
                try {
                    getApplicationContext().unregisterReceiver(pingBroadcastReceiver);
                    getApplicationContext().unregisterReceiver(isRunningBroadcastReceiver);
                } catch (Exception e) {
                    Log.d(TAG, "failure removing broadcast receiver");
                }
                prefs.setWlDisabled();
                Log.d(TAG, "Houston shutting down. This service is already running");

                handleStop();
                stopSelf();

                return;
            }
        }
    };

    public WildlinkForegroundService() {
        Log.d(TAG, "WildlinkForegroundService construct()");
    }

    private void performClipboardCheck() {

            blocked = true;
            if (!prefs.isClipboardMonitoringEnabled()) {
                Log.i(TAG, "monitoring disabled");
                blocked = false;
                return;
            }

            final ClipboardManager cb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            if (cb.hasPrimaryClip()) {
                ClipData cd = cb.getPrimaryClip();
                if (cd == null) {
                    Log.i(TAG, "ClipData is null");
                    blocked = false;
                    return;
                }
                if (cd.getDescription() == null) {
                    Log.i(TAG, "ClipData.getDescription() is null");
                    blocked = false;
                    return;
                }

                if (cd.getDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {

                    ClipData.Item item = cd.getItemAt(0);
                    if (item == null) {
                        Log.i(TAG, "ClipData.getItemAt(0) is null");
                        blocked = false;
                        return;
                    }
                    if (item.getText() == null) {
                        Log.i(TAG, "ClipData.getText(0) is null");
                        blocked = false;
                        return;
                    }

                    final String clipboard = cd.getItemAt(0).getText().toString();
                    if (clipboard.length() < 5) {
                        Log.i(TAG, "clipboard data < 5");
                        // if it isn't a url just abandon here
                        blocked = false;
                        return;
                    }

                    final ClipboardData clipboardData = findHttpLink(clipboard);

                    if (clipboard.equals(BuildConfig.configUrl) || clipboard.equals(BuildConfig.httpsConfigUrl) || clipboard.equals(
                        BuildConfig.httpConfigUrl)) {
                        Intent intent = new Intent(getApplicationContext(), WlConfigActivity.class);
                        getApplicationContext().startActivity(intent);
                        Log.i(TAG, "configUrl found");
                        blocked = false;
                        return;
                    }

                    if (clipboard.indexOf(BuildConfig.linkUrlPostfix) != -1
                        && clipboard.indexOf(BuildConfig.vanityBaseUrl) == 0) {
                        // this is what avoids recursion
                        Log.d(TAG, "Aborting");
                        blocked = false;
                        return;
                    }
                    if (clipboard.equalsIgnoreCase(vanityUrl)) {
                        // this is what avoids recursion
                        Log.d(TAG, "Aborting clipboard is vanity url");
                        blocked = false;
                        return;
                    }

                    log.append(clipboard);
                    log.append("\n\n");

                    oldClipboardData = clipboard;


                    presenter.processClipboard(WildlinkForegroundService.this, clipboardData.httpLink,
                        new WildlinkServiceHelper.Listener() {

                            @Override
                            public void onSuccess(Vanity vanity) {
                                // now update the original data

                                //String clipboardWithVanityReplaced =
                                //    clipboard.replace(clipboardData.httpLink, vanity.getVanityUrl());

                                vanityUrl = vanity.getVanityUrl();
                                //ClipData clip = ClipData.newPlainText("simple text", clipboardWithVanityReplaced);
                                //cb.setPrimaryClip(clip);

                                //showPeekNotification(clipboardWithVanityReplaced);
                                //
                                //final Intent intent = new Intent(PublicConstants.ACTION_WL_CLIPBOARD_COPY_RESULT);
                                //intent.putExtra(PublicConstants.WAS_VANITY_CREATED,true);
                                //intent.putExtra(PublicConstants.VANITY_ORIGINAL_LINK,vanity.getOriginalUrl());
                                //intent.putExtra(PublicConstants.VANITY_SHORT_LINK,vanity.getShortUrl());
                                //intent.putExtra(PublicConstants.VANITY_DOMAIN,vanity.getDomain());
                                //
                                //sendBroadcast(intent);
                                blocked = false;
                            }

                            @Override
                            public void doNotCreateVanityUrl(String databaseSearchResult) {

                                Log.i(TAG, "doNotCreateVanityUrl()");

                                String previewData = databaseSearchResult + "?url=" + clipboardData.original;

                                //ClipData clip = ClipData.newPlainText("simple text", databaseSearchResult + "?url=" + clipboardData.original);
                                //
                                //cb.setPrimaryClip(clip);
                                // showPeekNotification(previewData);
                                blocked = false;
                            }

                            @Override
                            public void nothingInDb() {
                                Log.i(TAG, "nothing Found in Db");

                                blocked = false;

                                final Intent intent = new Intent(PublicConstants.ACTION_WL_CLIPBOARD_COPY_RESULT);
                                intent.putExtra(PublicConstants.WAS_VANITY_CREATED, false);
                                sendBroadcast(intent);
                                return;
                            }
                        });
                }
            }

    }

    /***
     *
     * @param clipboard
     * @return
     */
    private ClipboardData findHttpLink(final String clipboard) {
        if (clipboard.indexOf('\n') > 0) {
            // there is a carriage return
            return getHttp(clipboard, "\n");
        } else {
            // check if there is a space only
            if (clipboard.indexOf(' ') > 0) {
                return getHttp(clipboard, " ");
            }
        }

        return new ClipboardData(clipboard, clipboard, 0, clipboard.length());
    }

    private ClipboardData getHttp(String input, String splitString) {
        String[] splitData = input.split(splitString);
        boolean httpFound = false;
        for (String data : splitData) {
            int index = data.indexOf("http");

            if (data.indexOf("http") > -1) {
                // check for spaces
                if (data.indexOf(' ') > 0) {
                    String[] splitBySpaces = data.split(" ");
                    ClipboardData clipboardData = new ClipboardData(input, splitBySpaces[0], index,
                        splitBySpaces[0].length());
                    return clipboardData;
                }
                ClipboardData clipboardData = new ClipboardData(input, data, index, data.length());
                return clipboardData;
            }
        }

        ClipboardData clipboardData = new ClipboardData(input, input, 0, input.length());
        return clipboardData;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate() started");

        Log.d(TAG, "registering receiver");
        IntentFilter intentFilter = new IntentFilter(RUNNING_BROADCAST_RECEIVER_INTENT_NAME);

        registerReceiver(isRunningBroadcastReceiver, intentFilter);

        Log.d(TAG, "registering receiver");
        IntentFilter pingIntentFilter = new IntentFilter(PING_BROADCAST_RECEIVER_INTENT_NAME);

        registerReceiver(pingBroadcastReceiver, pingIntentFilter);

        isMonitoring = false;
        apiModule = ApiModule.INSTANCE;

        prefs = apiModule.getSharedPrefsUtil();

        presenter = new WildlinkServiceHelper(apiModule);
        Log.d(TAG, "onCreate complete");

        if( apiModule.getSharedPrefsUtil().wasDestroyed()) {
            apiModule.getSharedPrefsUtil().clearDestroyed();
            Log.d(TAG, "this was destroyed i am restarting with new intent");
            Intent intent = new Intent(getApplicationContext(),WildlinkForegroundService.class);
            intent.putExtra(Constants.FIRING_UP_BY_ME,true);
            startService(intent);

        }


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Log.d(TAG, "onStartCommand() startId=" + startId);




        if( intent != null) {
            if( intent.getBooleanExtra(Constants.FIRING_UP_BY_ME,false)) {
                Log.d(TAG, "found it was started by me nulling intent");
                intent = null;
            }
        }
        if (intent == null) {
            Log.i(TAG, "intent is null in service starting like it is booting");
            intent = new Intent(getApplicationContext(), WildlinkForegroundService.class);
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.KEY_WL_SERVICE_STARTUP_TYPE,
                Constants.WL_INT_STARTUP_TYPE_BOOT);
            intent.putExtra(Constants.WILDLINK_SERVICE_BUNDLE, bundle);
            intent.putExtra(Constants.KEY_WL_SERVICE_STARTUP_TYPE,
                Constants.WL_INT_STARTUP_TYPE_BOOT);
        }

        prefs = apiModule.INSTANCE.getSharedPrefsUtil();

        if (intent.getBooleanExtra(Constants.WILDLINK_SERVICE_STOP, false)) {

            Log.d(TAG, "received a stop command" + Thread.currentThread().getName());
            isMonitoring = false;
            handleStop();
            stopSelf();

            return START_STICKY;
        }

        Log.d(TAG, "prefs" + prefs.isWlEnabled());

        Log.d(TAG, "sending ping");


        prefs.setWlEnabled();
        Log.d(TAG, "prefs" + prefs.isWlEnabled());


        apiModule.getCache().downloadMerchantsDatabase(true, new SimpleListener() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "downloadMerchantsDatabase() onSuccess()");
                startTimer();
            }

            @Override
            public void onFailure(ApiError error) {
                WildlinkServiceError wse =
                    new WildlinkServiceError(PublicConstants.WL_SERVICE_DATABASE_DOWNLOAD_ERROR,
                        error.getCode(), error.getMessage());
                Log.d(TAG, "downloadMerchantsDatabase() onFailure() error="
                    + error.getCode()
                    + ",message="
                    + error.getMessage());
                EventBus.getDefault().post(new WlErrorEvent(error));
                startTimer();
            }
        });

        Log.d(TAG, "now checking if monitoring");

        if (isMonitoring) {

            // this will only happen if the user has pressed one of the notification buttons
            boolean isClipboardMonitoringEnabled =
                intent.getBooleanExtra(Constants.KEY_WL_ENABLE_CLIPBOARD_MONITORING, false);

            startTimer();
        }

        Log.d(TAG, "isMonitoring=" + isMonitoring);


        int startupType = intent.getIntExtra(Constants.KEY_WL_SERVICE_STARTUP_TYPE, -1);

        Log.d(TAG, "startupType=" + startupType);

        Log.d(TAG, "is monitoring=" + isMonitoring);
        if (!isMonitoring) {


            clientSecret = intent.getStringExtra(Constants.KEY_CLIENT_SECRET);
            appId = intent.getStringExtra(Constants.KEY_APP_ID);
            refreshRateKey = intent.getIntExtra(Constants.KEY_REFRESH_RATE_KEY, Calendar.HOUR);
            refreshRateValue = intent.getIntExtra(Constants.KEY_REFRESH_RATE_VALUE, 24);

            String clientCurrentVersion =
                intent.getStringExtra(Constants.KEY_CLIENT_CURRENT_VERSION);
            String initialIntegrationVersion =
                intent.getStringExtra(Constants.KEY_CLIENT_INITIAL_INTEGRATION_VERSION);

            Log.d(TAG, "starting service with intent");

            super.startService(intent);

            if (clientSecret == null) {
                clientSecret = prefs.getClientSecret();
                appId = prefs.getAppId();
                refreshRateKey = prefs.getRefreshRateKey();
                refreshRateValue = prefs.getRefreshRateValue();
            } else {
                prefs.storeClientSecret(clientSecret);
                prefs.storeAppId(appId);
                prefs.storeRefreshRateKey(refreshRateKey);
                prefs.storeRefreshRateValue(refreshRateValue);
            }

            Log.d(TAG, "initializing sdk");

            initializeSdk();

            Log.d(TAG, "stopping broadcast");
        }

        ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).addPrimaryClipChangedListener(
            listener);

        isMonitoring = true;

        apiModule.getCloudServiceApi().logger(DateTime.now(),null);


        return START_STICKY;
    }

    private void handleStop() {
        // stopping
        Log.d(TAG, "stopping self");
        stopSelf();
    }

    private void startTimer() {
        if (cacheTimerTask != null) {
            cacheTimerTask.cancel();
            Log.d(TAG, "cacheTimer cancelling");
        }
        cacheTimerTask = new CacheTimerTask();
        Timer t = new Timer();
        Log.d(TAG, "scheduling timer");

        RefreshRate refreshRate =
            apiModule.INSTANCE.getProvider().provideDatabaseCacheRefreshRate();

        long period = 1 * 60 * 60 * 1000;

        Log.d(TAG, "scheduling timer=" + period + ", milliseconds");
        t.scheduleAtFixedRate(cacheTimerTask, period, period);
    }

    private void initializeSdk() {

        if (sdk == null) {
            sdk = WildlinkSdk.getIntance();
        }

        final StringBuffer updatedCursor = new StringBuffer();

        if (apiModule.INSTANCE.getProvider() instanceof WildlinkInternalUseException) {

            apiModule.getCache().downloadMerchantsDatabase(true, new SimpleListener() {

                @Override
                public void onSuccess() {
                    Log.d(TAG, "downloadMerchantsDatabase() onSuccess()");
                    startTimer();
                }

                @Override
                public void onFailure(ApiError error) {
                    WildlinkServiceError wse =
                        new WildlinkServiceError(PublicConstants.WL_SERVICE_DATABASE_DOWNLOAD_ERROR,
                            error.getCode(), error.getMessage());
                    Log.d(TAG, "downloadMerchantsDatabase() onFailure() error="
                        + error.getCode()
                        + ",message="
                        + error.getMessage());
                    EventBus.getDefault().post(new WlErrorEvent(error));
                    startTimer();
                }
            });
        }
    }

    @Override
    public void onDestroy() {

        blocked = false;
        Log.d(TAG, "onDestroy");
        apiModule.getCloudServiceApi().logger(null,DateTime.now());


        try {
            getApplicationContext().unregisterReceiver(pingBroadcastReceiver);
        } catch (Exception e) {
            Log.d(TAG, "failure removing broadcast receiver");
        }
        try {

            getApplicationContext().unregisterReceiver(isRunningBroadcastReceiver);
        } catch (Exception e) {
            Log.d(TAG, "failure removing broadcast receiver");
        }

        isMonitoring = false;


        apiModule.getSharedPrefsUtil().onDestroy();



        AlarmManager mgr=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent i=new Intent(this, WildlinkForegroundService.class);
        PendingIntent pi=PendingIntent.getBroadcast(this, 0,
            i, 0);

        mgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime()+15000,
            pi);

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "unbind");
        return super.onUnbind(intent);
    }

    public void showShareDialog(String url) {
        Log.d(TAG, "showShareDialog url=" + url);

        try {

            Intent intentShare = new Intent(Intent.ACTION_SEND);
            intentShare.setType("text/plain");
            intentShare.putExtra(Intent.EXTRA_TEXT, url);

            intentShare.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Intent i = Intent.createChooser(intentShare, "Share Wildlink");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            this.startActivity(i);
        } catch (android.content.ActivityNotFoundException ex) {
            // (handle error)
            Log.d(TAG, "xactivity not found!" + ex.getMessage());
        } catch (Exception e) {
            Log.d(TAG, "yactivity not found!" + e.getMessage());
        }
    }

    public void startRestartTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i(TAG, "in timer ++++  " + (counter++));
            }
        };
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * Returns true if this is a foreground service.
     *
     * @param context The {@link Context}.
     */
    public boolean serviceIsRunningInForeground(Context context) {

        ActivityManager manager =
            (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        int count = 0;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
            Integer.MAX_VALUE)) {
            String thisClass = WildlinkForegroundService.class.getName();
            String serviceClass = service.service.getShortClassName();

            if (thisClass.equals(serviceClass)) {
                Log.d(TAG, "service is running, this should process clipboard results");
                return true;
            }
        }

        return false;
    }

    public String getApplicationUid() {

        return "" + getApplicationContext().getApplicationInfo().uid;
    }

    private class ClipboardData {
        public String httpLink;
        public int startIndex;
        public int endIndex;
        public String original;

        public ClipboardData(String original, String httpLink, int startIndex, int endIndex) {
            this.httpLink = httpLink;
            this.original = original;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }
    }

    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public WildlinkForegroundService getService() {
            return WildlinkForegroundService.this;
        }
    }

    public class CacheTimerTask extends TimerTask {

        @Override
        public void run() {
            apiModule.getCache().downloadMerchantsDatabase(true, new SimpleListener() {

                @Override
                public void onSuccess() {
                    Log.d(TAG, "downloadMerchantsDatabase() onSuccess()");
                    startTimer();
                }

                @Override
                public void onFailure(ApiError error) {
                    WildlinkServiceError wse =
                        new WildlinkServiceError(PublicConstants.WL_SERVICE_DATABASE_DOWNLOAD_ERROR,
                            error.getCode(), error.getMessage());
                    Log.d(TAG, "downloadMerchantsDatabase() onFailure() error="
                        + error.getCode()
                        + ",message="
                        + error.getMessage());
                    EventBus.getDefault().post(new WildlinkServiceEvent(wse));
                    startTimer();
                }
            });
        }
    }

    ;
}

