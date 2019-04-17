package me.wildlinksdk.android;

/**
 * Created by rjawanda on 12/20/17.
 */

public class PublicConstants {
    public static final String INTENT_NAME_MY_PACKAGE_REPLACED = "my_package_updated";

    public static final int WL_SERVICE_DATABASE_DOWNLOAD_ERROR = 1001;

    public static final String BROADCAST_RECEIVER_API_BASE = "wl_api_base_broadcast";
    public static final String API_BASE = "wl_api_base";
    public static final String SERVER_TYPE = "wl_api_server_type"; // prod or dev

    public static final String ACTION_WL_CLIPBOARD_COPY_RESULT = "wl_link_processed";
    public static final String WAS_VANITY_CREATED = "wl_link_match_status";
    public static final String VANITY_ORIGINAL_LINK = "wl_vanity_original_link";
    public static final String VANITY_SHORT_LINK = "wl_vanity_short_link";
    public static final String VANITY_DOMAIN = "wl_vanity_domain";
}
