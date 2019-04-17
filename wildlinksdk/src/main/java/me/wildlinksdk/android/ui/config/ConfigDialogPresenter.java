package me.wildlinksdk.android.ui.config;

import me.wildlinksdk.android.WildlinkSdk;

/**
 * Created by rjawanda on 1/2/18.
 */

public class ConfigDialogPresenter {

    private ConfigDialogContract.View view;
    private Object verificationCode;
    private WildlinkSdk sdk;

    public ConfigDialogPresenter(WildlinkSdk sdk, ConfigDialogContract.View view) {
        this.view = view;
        this.sdk = sdk;
    }
}
