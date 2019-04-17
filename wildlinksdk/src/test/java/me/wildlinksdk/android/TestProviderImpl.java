package me.wildlinksdk.android;

import android.content.Context;

import java.util.Calendar;

/**
 * Created by ron on 10/10/17.
 */

public class TestProviderImpl implements WildlinkSdk.KeyboardMonitorProvider {

    private Context appContext;
    private String databaseName;
    private RefreshRate refreshRate;

    public TestProviderImpl(Context appcontext) {
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
    public RefreshRate provideDatabaseCacheRefreshRate() {
        return new RefreshRate(Calendar.MINUTE, 1);
    }

}