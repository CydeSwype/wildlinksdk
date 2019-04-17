package me.wildlinksdk.android.api;

/**
 * Created by ron on 10/5/17.
 */

import android.content.SharedPreferences;
import java.util.UUID;
import me.wildlinksdk.android.BuildConfig;
import me.wildlinksdk.android.Constants;
import org.joda.time.DateTime;

public class SharedPrefsUtil {

    private static final String TAG = SharedPrefsUtil.class.getSimpleName();
    private SharedPreferences prefs;
    private String mConsumer;
    private String refreshedToken;
    private String[] baseUrlData;
    private String overrideBaseUrl;

    public SharedPrefsUtil(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    public void storeUuid(String uuid) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.KEY_UUID, uuid);
        editor.commit();
    }

    public String getUuid() {
        return prefs.getString(Constants.KEY_UUID, null);
    }

    public void storeDeviceToken(String deviceToken) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.KEY_DEVICE_TOKEN, deviceToken);
        editor.commit();
    }

    public String getDeviceToken() {
        return prefs.getString(Constants.KEY_DEVICE_TOKEN, "");
    }




    public String getDeviceKey() {
        return prefs.getString(Constants.KEY_DEVICE_KEY, null);
    }

    public void storeDeviceKey(String uuid) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.KEY_DEVICE_KEY, uuid);
        editor.commit();
    }

    public void removeDeviceKey() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(Constants.KEY_DEVICE_KEY);
        editor.commit();
    }

    public void storeSenderToken(String senderToken) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.KEY_SENDER_TOKEN, senderToken);
        editor.commit();
    }


    public void removeDeviceToken() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(Constants.KEY_DEVICE_TOKEN);
        editor.commit();
    }

    public void removeSenderToken() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(Constants.KEY_SENDER_TOKEN);
        editor.commit();
    }

    public DateTime getFeaturedStoreListLastRefreshDate() {
        long lastRefreshDate = prefs.getLong(Constants.KEY_DATABSE_FEATURED_STORE_REFRESH_DATE, -1);
        return lastRefreshDate == -1 ? null : new DateTime(lastRefreshDate);
    }

    public DateTime getConceptsLastRefreshDate() {
        long lastRefreshDate = prefs.getLong(Constants.KEY_DATABASE_CONCEPTS_LAST_REFRESH_DATE, -1);
        return lastRefreshDate == -1 ? null : new DateTime(lastRefreshDate);
    }

    public DateTime getMerchantsLastRefreshDate() {
        long lastRefreshDate = prefs.getLong(Constants.KEY_DATABASE_MERCHANTS_REFRESH_DATE, -1);
        return lastRefreshDate == -1 ? null : new DateTime(lastRefreshDate);
    }

    public String getSenderToken() {
        return prefs.getString(Constants.KEY_SENDER_TOKEN, "");
    }

    public void storeClientSecret(String secret) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.KEY_CLIENT_SECRET, secret);
        editor.commit();
    }

    public String getClientSecret() {
        return prefs.getString(Constants.KEY_CLIENT_SECRET, null);
    }

    public void storeAppId(String appId) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.KEY_APP_ID, appId);
        editor.commit();
    }

    public String getAppId() {
        return prefs.getString(Constants.KEY_APP_ID, null);
    }

    public void storeRefreshRateKey(int key) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(Constants.KEY_REFRESH_RATE_KEY, key);
        editor.commit();
    }

    public int getRefreshRateKey() {
        return prefs.getInt(Constants.KEY_REFRESH_RATE_KEY, -1);
    }

    public void storeRefreshRateValue(int value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(Constants.KEY_REFRESH_RATE_VALUE, value);
        editor.commit();
    }

    public int getRefreshRateValue() {
        return prefs.getInt(Constants.KEY_REFRESH_RATE_VALUE, -1);
    }

    //    public void storeNotificationData(String json) {
    //        SharedPreferences.Editor editor = prefs.edit();
    //        editor.putString(Constants.KEY_NOTIFICATION_DATA, json);
    //        editor.commit();
    //
    //    }
    public void storeRemoteViews(String json) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.KEY_NOTIFCATION_REMOTE_VIEWS, json);
        editor.commit();
    }

    public String getRemoteViews() {

        return prefs.getString(Constants.KEY_NOTIFCATION_REMOTE_VIEWS, null);
    }

    public void disableClipboardMonitoring() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Constants.KEY_CLIPBOARD_MONITORING, false);
        editor.commit();
    }

    public void enableClipboardMonitoring() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Constants.KEY_CLIPBOARD_MONITORING, true);
        editor.commit();
    }

    public boolean isClipboardMonitoringEnabled() {
        return prefs.getBoolean(Constants.KEY_CLIPBOARD_MONITORING, true);
    }

    public void storeInitialIntegrationVersion(String version) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.KEY_CLIENT_INITIAL_INTEGRATION_VERSION, version);
        editor.commit();
    }

    public void storeCurrentClientVersion(String version) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.KEY_CLIENT_CURRENT_VERSION, version);
        editor.commit();
    }

    public void storePendingIntent(String pendingIntent) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.KEY_NOTIFICATION_WHATS_NEW_PENDING_INTENT, pendingIntent);
        editor.commit();
    }

    public String getPendingIntent() {

        return prefs.getString(Constants.KEY_NOTIFICATION_WHATS_NEW_PENDING_INTENT, null);
    }

    public String isWlEnabled() {
        return prefs.getString(Constants.KEY_WL_ENABLED, null);
    }

    public boolean hasBeenAskedToEnableAlready() {
        return prefs.getBoolean(Constants.KEY_WL_HAS_BEEN_ASKED_TO_ENABLE_ALREADY, false);
    }

    public void setHasBeenAskedToEnableAlready() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Constants.KEY_WL_HAS_BEEN_ASKED_TO_ENABLE_ALREADY, true);
        editor.commit();
    }

    public void setWlEnabled() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.KEY_WL_ENABLED, "1");
        editor.commit();
    }

    public void setWlDisabled() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.KEY_WL_ENABLED, null);
        editor.commit();
    }

    public void setSenderVerified() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Constants.KEY_WL_SENDER_VERIFIED, true);
        editor.commit();
    }

    public boolean isSenderVerified() {
        return prefs.getBoolean(Constants.KEY_WL_SENDER_VERIFIED, false);
    }

    public void removeSenderVerified() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(Constants.KEY_WL_SENDER_VERIFIED);
        editor.commit();
    }

    public void enableNotificationPeek() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Constants.KEY_WL_NOTIFCATION_PEEK, true);
        editor.commit();
    }

    public void disableNotificationPeek() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Constants.KEY_WL_NOTIFCATION_PEEK, false);
        editor.commit();
    }

    public boolean isNotificationPeekEnabled() {
        return prefs.getBoolean(Constants.KEY_WL_NOTIFCATION_PEEK, true);
    }

    public void setWlEnableActivityHasBeenShownToUserAlready() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Constants.KEY_WL_ENABLE_ACTIVITY_SHOWN_ONCE, true);
        editor.commit();
    }

    public boolean hasWlEnableActivityHasBeenShownToUserAlready() {
        return prefs.getBoolean(Constants.KEY_WL_ENABLE_ACTIVITY_SHOWN_ONCE, false);
    }

    public void storeLoggedinNumber(String phoneNumber) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.KEY_WL_LOGGED_IN_PHONE_NUMBER, phoneNumber);
        editor.commit();
    }

    public String getLoggedinNumber() {
        return prefs.getString(Constants.KEY_WL_LOGGED_IN_PHONE_NUMBER, null);
    }

    public void setNotificationPeekShownOnce() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Constants.KEY_NOTIFCATION_PEEK_SHOWN_ONCE, true);
        editor.commit();
    }

    public boolean hasNotificationPeekBeenShownOnce() {
        return prefs.getBoolean(Constants.KEY_NOTIFCATION_PEEK_SHOWN_ONCE, false);
    }

    public void enableShortenLink() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Constants.KEY_WL_SETTTINGS_ENABLE_SHORTEN_LINK, true);
        editor.commit();
    }

    public void disableShortenLink() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Constants.KEY_WL_SETTTINGS_ENABLE_SHORTEN_LINK, false);
        editor.commit();
    }

    public boolean isShortenLinkEnabled() {
        return prefs.getBoolean(Constants.KEY_WL_SETTTINGS_ENABLE_SHORTEN_LINK, true);
    }

    public void storeFirebaseRefreshedToken(String token) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.KEY_WL_FIREBASE_TOKEN, token);
        editor.commit();
    }

    public String getFirebaseRefreshedToken() {
        return prefs.getString(Constants.KEY_WL_FIREBASE_TOKEN, "");
    }

    public void storeNotificationData(String notificationDataJson) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.WILDLINK_KEY_NOTIFICATION_DATA, notificationDataJson);
        editor.commit();
    }

    public String getNotificationData() {
        return prefs.getString(Constants.WILDLINK_KEY_NOTIFICATION_DATA, null);
    }

    public void removeNotificationData() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(Constants.WILDLINK_KEY_NOTIFICATION_DATA);
        editor.commit();
    }

    public void saveHistory(String jsonHistory) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.KEY_WL_HISTORY, jsonHistory);
        editor.commit();
    }

    public String getHistory() {
        return prefs.getString(Constants.KEY_WL_HISTORY, null);
    }

    public void storeApplicationName(String applicationName) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.KEY_WL_SERVICE_PARENT, applicationName);
        editor.commit();
    }

    public String getApplicationName() {
        return prefs.getString(Constants.KEY_WL_SERVICE_PARENT, null);
    }

    public void clear() {

        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

    public void setMerchantsDatabaseFailure() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Constants.KEY_MERCHANTS_DATABASE_FAILURE, true);
        editor.commit();
    }

    public void setFeaturedStoreDatabaseFailure() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Constants.KEY_FEATURED_STORE_LIST_DATABASE_FAILURE, true);
        editor.commit();
    }

    public void setConceptsDatabaseFailure() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Constants.KEY_CONCEPTS_DATABASE_FAILURE, true);
        editor.commit();
    }

    public void removeConceptsDatabaseDownloadFailure() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(Constants.KEY_CONCEPTS_DATABASE_FAILURE);
        editor.commit();
    }

    public void removeMerchantDatabaseDownloadFailure() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(Constants.KEY_MERCHANTS_DATABASE_FAILURE);
        editor.commit();
    }

    public void removeFeaturedStoreDatabaseFailure() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(Constants.KEY_FEATURED_STORE_LIST_DATABASE_FAILURE);
        editor.commit();
    }

    public boolean hasMerchantsDatabaseFailure() {
        return prefs.getBoolean(Constants.KEY_MERCHANTS_DATABASE_FAILURE, false);
    }

    public boolean hasConceptsDatabaseFailure() {
        return prefs.getBoolean(Constants.KEY_CONCEPTS_DATABASE_FAILURE, false);
    }

    public boolean hasFeaturedStoreListDatabaseFailure() {
        return prefs.getBoolean(Constants.KEY_FEATURED_STORE_LIST_DATABASE_FAILURE, false);
    }

    public void updateMechanteDatabaseRefreshDate() {
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong(Constants.KEY_DATABASE_MERCHANTS_REFRESH_DATE, DateTime.now().getMillis());

        editor.commit();
    }

    public void updateConceptsDatabaseRefreshDate() {
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong(Constants.KEY_DATABASE_CONCEPTS_LAST_REFRESH_DATE,
            DateTime.now().getMillis());

        editor.commit();
    }

    public void updateFeaturedStoreDatabaseRefreshDate() {
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong(Constants.KEY_DATABASE_FEATURED_STORE_LAST_REFRESH_DATE,
            DateTime.now().getMillis());

        editor.commit();
    }

    public String getBaseUrlData() {

        return prefs.getString(Constants.KEY_WL_BASE_URL_JSON, null);
    }

    public void setBaseUrlData(String json) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.KEY_WL_BASE_URL_JSON, json);
        editor.commit();
    }

    public void setOverrideBaseUrl(String overrideBaseUrl) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.KEY_WL_OVERRIDE_BASE_URL, overrideBaseUrl);
        editor.commit();
    }

    public String getBaseApiUrl() {
        return prefs.getString(Constants.KEY_WL_OVERRIDE_BASE_URL, BuildConfig.baseApiUrl);
    }

    public String getDefaultServerFlavor() {
        return prefs.getString(Constants.KEY_WL_OVERRIDE_BASE_API_SERVER_FLAVOR,
            BuildConfig.defaultServerFlavor);
    }

    public void setOverrideServerFlavor(String flavor) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.KEY_WL_OVERRIDE_BASE_API_SERVER_FLAVOR, flavor);
        editor.commit();
    }

    public String getGoogleClientCid() {
        return prefs.getString(Constants.KEY_GOOGLE_CID, null);
    }

    public void storeGoogleCid(String cid) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.KEY_GOOGLE_CID, cid);
        editor.commit();
    }

    public void clearPrefs() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(Constants.KEY_DATABASE_CONCEPTS_LAST_REFRESH_DATE);
        editor.remove(Constants.KEY_DATABASE_DOMAIN_LAST_REFRESH_DATE);
        editor.remove(Constants.KEY_DATABASE_FEATURED_STORE_LAST_REFRESH_DATE);
        editor.remove(Constants.KEY_DATABASE_MERCHANTS_REFRESH_DATE);
        editor.commit();
    }

    public void storeWhichTableTypeClientUses(final String keyDbType, final int type) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(Constants.KEY_CLIENT_TABLE_TYPE, type);
        editor.commit();
    }

    public int getWhichTableTypeClientUses() {
        return prefs.getInt(Constants.KEY_CLIENT_TABLE_TYPE, 0);
    }

    public void onDestroy() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("ondestroy", true);
        editor.commit();
    }
    public boolean wasDestroyed() {
        return prefs.getBoolean("ondestroy", false);

    }

    public void clearDestroyed() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("ondestroy");
        editor.commit();
    }
}
