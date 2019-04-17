package me.wildlinksdk.android.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import me.wildlinksdk.android.BuildConfig;
import me.wildlinksdk.android.VanityListener;
import me.wildlinksdk.android.WildlinkSdk;
import me.wildlinksdk.android.api.ApiError;
import me.wildlinksdk.android.api.ApiModule;
import me.wildlinksdk.android.api.SharedPrefsUtil;
import me.wildlinksdk.android.api.models.History;
import me.wildlinksdk.android.api.models.events.WlErrorEvent;
import me.wildlinksdk.android.models.MerchantItemDomain;
import me.wildlinksdk.android.models.Vanity;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by rjawanda on 1/8/18.
 */

final class WildlinkServiceHelper {

    private static final String TAG = WildlinkServiceHelper.class.getSimpleName();
    private ApiModule apiModule;
    private WildlinkSdk sdk;
    private Listener listener;

    public WildlinkServiceHelper(ApiModule apiModule) {
        try {
            this.apiModule = apiModule;
            this.listener = listener;
            if (sdk == null) {
                sdk = WildlinkSdk.getIntance();
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception e=" + e.getMessage());
        }
    }

    public void processClipboard(final Context context, final String clipboard,
        final Listener listener) {
        final SharedPrefsUtil prefs = apiModule.getSharedPrefsUtil();

        if (listener == null) {

            listener.doNotCreateVanityUrl(clipboard);
            return;
        }

        // search merchant db.
        final MerchantItemDomain merchantsItemDomain =
            apiModule.getCache().searchMerchantItem(clipboard);

        if (merchantsItemDomain == null) {

            listener.nothingInDb();
            return;
        }

        if (!apiModule.getNetworkApi().isOnline()) {

            listener.doNotCreateVanityUrl(
                BuildConfig.vanityBaseUrl + "/" + merchantsItemDomain.merchantItem.getId());
            return;
        }

        if (prefs.isShortenLinkEnabled()) {


            sdk.createVanityUrl("wildlink_clipboard", clipboard, new VanityListener() {
                @Override
                public void onFailure(ApiError error) {

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            listener.doNotCreateVanityUrl(BuildConfig.vanityBaseUrl
                                + "/"
                                + merchantsItemDomain.merchantItem.getId());
                        }
                    });

                    EventBus.getDefault().post(new WlErrorEvent(error));
                }

                @Override
                public void onSuccess(final Vanity convertedVanity) {

                    if (listener == null) {

                        return;
                    }

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            convertedVanity.setDomain(merchantsItemDomain.domain);
                            listener.onSuccess(convertedVanity);
                        }
                    });

                }
            });
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            updateHistory(clipboard, clipboard);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //TODO test this (shortened link disabled)
                    listener.doNotCreateVanityUrl(
                        BuildConfig.vanityBaseUrl + "/" + merchantsItemDomain.merchantItem.getId());
                }
            });
        }
    }

    private void updateHistory(final String clipboard, final String wildlinkUrl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                History history = new History(wildlinkUrl, clipboard, "");
                SharedPrefsUtil prefs = apiModule.getSharedPrefsUtil();

                String historyJson = prefs.getHistory();
                List<History> historyList = null;
                if (historyJson == null) {
                    historyList = new ArrayList<History>();
                    historyList.add(history);
                } else {
                    historyList =
                        apiModule.getGson().fromJson(historyJson, new TypeToken<List<History>>() {
                        }.getType());
                    historyList.add(0, history);
                    if (historyList.size() > 50) {
                        historyList.remove(50);
                    }
                }

                String historyJsonUpdated =
                    apiModule.getGson().toJson(historyList, new TypeToken<List<History>>() {
                    }.getType());
                prefs.saveHistory(historyJsonUpdated);
            }
        }).start();
    }

    public interface Listener {
        public void onSuccess(Vanity vanity);

        public void doNotCreateVanityUrl(String databaseSearhResult);

        public void nothingInDb();
    }
}
