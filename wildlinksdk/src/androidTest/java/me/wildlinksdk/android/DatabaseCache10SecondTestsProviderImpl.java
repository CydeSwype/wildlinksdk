package me.wildlinksdk.android;

import android.content.Context;

import me.wildlinksdk.android.models.NotificationData;

import java.util.Calendar;

/**
 * Created by ron on 10/10/17.
 */

public class DatabaseCache10SecondTestsProviderImpl implements WildlinkSdk.ClipboardMonitorProvider {

    private Context appContext;
    private String databaseName;
    private RefreshRate refreshRate;

    public DatabaseCache10SecondTestsProviderImpl(Context appcontext) {
        this.appContext = appcontext;
    }

    @Override
    public Context provideApplicationContext() {
        return this.appContext;
    }



    @Override
    public String provideClientSecret() {
        return "y81qJSPVmVCAjsOz3i6l";
    }

    @Override
    public String provideWildlinkAppId() {
        return "3";
    }

    @Override
    public NotificationData provideNotificationData() {
        return null;
    }


    @Override
    public RefreshRate provideDatabaseCacheRefreshRate() {
        return new RefreshRate(Calendar.MILLISECOND, 10000);
    }

}