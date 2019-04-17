package me.wildlinksdk.android;

/**
 * Created by ron on 10/5/17.
 */

public class Constants {
    // anything with KEY_prefix is stored in the shared preferences
    public static final String KEY_DEVICE_KEY = "key_device_key";

    public static final String FIRING_UP_BY_ME = "key_firingup_by_me";
    public static final String KEY_UUID = "key_uuid";
    public static final String SHARE_PREFERENCES_PRIVATE = "wildfire_wildlink";
    public static final String KEY_DEVICE_TOKEN = "device_token";
    public static final String KEY_SENDER_TOKEN = "sender_token";
    public static final String FAIL_CODE_SDK_INIT_FAILED_STR = "Database download failure";
    public static final int FAIL_CODE_SDK_INIT_FAILED_INT = 1000;
    public static final String ENDPOINT_CONCEPT = "concept";
    public static final String ENDPOINT_SENDER = "/sender";
    public static final String COMMISSION_SUMMARY = "/stats/commission-summary";
    public static final String DEVICE_CLICK_STATS = "/stats/clicks";
    public static final String KEY_DATABASE_CONCEPTS_LAST_REFRESH_DATE =
        "key_concepts_last_refresh_date";
    public static final String KEY_DATABASE_FEATURED_STORE_LAST_REFRESH_DATE =
        "key_featured_store_last_refresh_date";
    public static final String KEY_DATABASE_DOMAIN_LAST_REFRESH_DATE =
        "key_domain_last_refresh_date";
    public static final String WILDLINK_KEY_NOTIFICATION_DATA = "wildlink_notification_data";
    public static final String WILDLINK_SERVICE_BUNDLE = "wildlink_service_bundle";
    public static final String WILDLINK_SERVICE_RECONFIG_BUNDLE =
        "wildlink_service_reconfig_bundle";
    public static final String WILDLINK_SERVICE_STOP = "wildlink_service_stop";
    public static final String KEY_CLIENT_SECRET = "wildlink_client_secret";
    public static final String KEY_APP_ID = "wildlink_app_id";
    public static final String KEY_REFRESH_RATE_KEY = "wildlink_refresh_rate_key";
    public static final String KEY_REFRESH_RATE_VALUE = "wildlink_refresh_rate_value";
    public static final String DB_KIND_DOMAIN = "domain";
    public static final String KEY_WL_SENDER_VERIFIED = "wl_sender_verified";
    public static final String KEY_WL_ENABLE_CLIPBOARD_MONITORING = "wl_enable_disable";
    public static final String KEY_WL_NOTIFCATION_PEEK = "wl_notification_peek";
    public static final String KEY_WL_ENABLE_ACTIVITY_SHOWN_ONCE = "wl_enable_activity_shown_once";
    public static final String KEY_WL_LOGGED_IN_PHONE_NUMBER = "wl_logged_in_phone_number";
    public static final String KEY_NOTIFCATION_PEEK_SHOWN_ONCE = "wl_notifcation_peek_shown_once";
    public static final String KEY_WL_NOTIFICATION_UNDO_PRESSED = "wl_notification_undo_pressed";
    public static final String KEY_WL_NOTIFICATION_VIEW_EARNINGS_PRESSED =
        "wl_notification_view_earnings_pressed";
    public static final String KEY_WL_NOTIFICATION_VIEW_TERMS_AND_PRIVACY_PRESSED =
        "wl_notification_view_terms_and_privacy_pressed";
    public static final String KEY_WL_NOTIFICATION_VIEW_LEARN_MORE_PRESSED =
        "wl_notification_view_learn_more_pressed";
    public static final String KEY_WL_FROM_BUTTON_PRESS = "wl_from_button_press";
    public static final String KEY_WL_WEB_PAGE = "wl_web_page";
    public static final String KEY_WL_WEB_PAGE_TITLE = "wl_web_page_title";
    public static final String KEY_WL_SETTTINGS_ENABLE_SHORTEN_LINK = "wl_enable_shorten_link";
    public static final String KEY_WL_NOTIFICATION_SHARE_PRESSED =
        "wl_notification_preview_pressed";
    public static final String KEY_WL_FIREBASE_TOKEN = "wl_firebase_token";
    public static final String WILDLINK_SERVICE_STARTED_MANUALLY = "wl_started_manually";
    public static final String KEY_WL_HISTORY = "wl_history";
    public static final String KEY_ENABLE_FROM_NON_PERSISTENT_NOTIFICATION =
        "key_enable_from_non_persistent_notification";
    public static final String KEY_WL_SERVICE_PARENT = "key_wl_service_parent";
    public static final String KEY_MERCHANTS_DATABASE_FAILURE = "key_merchants_database_failure";
    public static final String KEY_CONCEPTS_DATABASE_FAILURE = "key_concepts_database_failure";
    public static final String KEY_FEATURED_STORE_LIST_DATABASE_FAILURE =
        "key_featured_store_database_failure";
    public static final String KEY_WL_NOTIFICATION_FEEDBACK_PRESSED =
        "wl_notification_feedback_button";
    public static final String KEY_INSTABUG_DOMAIN_DOWNLOAD_FAILURE =
        "instabug_domain_download_failure";
    public static final String KEY_INSTABUG_APP_ERROR = "app_error";
    public static final String KEY_INSTABUG_HTTP_STATUS = "http_status";
    public static final String KEY_INSTABUG__APP_MESSAGE = "message";
    public static final String KEY_DATABASE_MERCHANTS_REFRESH_DATE = "domain_db_refresh_date";
    public static final String KEY_WL_BASE_URL_JSON = "key_wl_base_url_json";
    public static final String KEY_WL_OVERRIDE_BASE_URL = "key_wl_override_base_url";
    public static final String KEY_GOOGLE_CID = "key_google_cid";
    public static final String KEY_DATABSE_FEATURED_STORE_REFRESH_DATE =
        "featured_store_list_last_refresh_date";
    public static final String KEY_WL_OVERRIDE_BASE_API_SERVER_FLAVOR = "wl_base_server_type";
    public static final String KEY_CLIENT_TABLE_TYPE = "wl_db_type";
    public static final int TABLE_CONCEPTS = 1;
    public static final int TABLE_MERCHANTS = 2;
    private static final String version = "v1";
    public static final String ENDPOINT_DEVICE = version + "/device";
    public static final String ENDPOINT_PHRASE = version + "/phrase";
    //    public static String KEY_NOTIFICATION_DATA = "key_notification_data";
    public static String KEY_CLIPBOARD_MONITORING = "key_clipboard_monitoring";
    public static String KEY_NOTIFCATION_REMOTE_VIEWS = "key_notification_remote_views";
    public static String KEY_CLIENT_INITIAL_INTEGRATION_VERSION =
        "key_client_initial_integration_version";
    public static String KEY_CLIENT_CURRENT_VERSION = "key_client_current_version";
    public static String KEY_NOTIFICATION_WHATS_NEW_PENDING_INTENT =
        "key_notification_whats_new_pending_intent";
    public static String KEY_WL_NOTIFICATION_PENDING_INTENT = "key_notification_pending_intent";
    public static String KEY_WL_SERVICE_STARTUP_TYPE = "wl_service_startup_type";
    public static String KEY_WL_ENABLED = "wl_id_enabled";
    public static String KEY_WL_HAS_BEEN_ASKED_TO_ENABLE_ALREADY =
        "wl_has_been_asked_to_enable_already";
        //prod or dev
    public static int WL_INT_STARTUP_TYPE_USER = 1;
    public static int WL_INT_STARTUP_TYPE_BOOT = 2;
    public static int WL_INT_STARTUP_PACKAGE_UPDATED = 3;
}
