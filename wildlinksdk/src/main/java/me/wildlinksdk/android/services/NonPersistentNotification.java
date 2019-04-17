package me.wildlinksdk.android.services;

import android.app.NotificationManager;
import android.content.res.Resources;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import me.wildlinksdk.android.R;
import me.wildlinksdk.android.api.ApiModule;

//import me.wildlink.sdk.api.WlPendingIntent;

/**
 * Created by rjawanda on 1/19/18.
 */

public class NonPersistentNotification {
    private static final String TAG = NonPersistentNotification.class.getSimpleName();
    private static final String NOTIFICATION_ID = "676767672";
    public final String CHANNEL_POSTFIX_CREATE = "10";
    public final String CHANNEL_POSTFIX_PUSH_NOTIFICATION = "11";
    private NotificationManager notificationManager;
    private ApiModule apiModule;
    private Resources resources;

    public NonPersistentNotification(NotificationManager notificationManager, ApiModule apiModule) {
        this.apiModule = apiModule;
        this.notificationManager = notificationManager;
        this.resources = apiModule.getProvider().provideApplicationContext().getResources();
    }

    //  public void show( ) {
    //          createNotification();
    //   }

    //protected void createNotification() {
    //
    //    final Context context = apiModule.getProvider().provideApplicationContext();
    //    final Utilities utilities = new Utilities();
    //    final NotificationData  notificationData = ((WildlinkSdk.ClipboardMonitorProvider)apiModule.getProvider()).provideNotificationData();
    //
    //    IntentBuilder intentBuilder = new IntentBuilder(resources);
    //
    //    final SharedPrefsUtil prefs = apiModule.getSharedPrefsUtil();
    //
    //    float buttonFontSize = resources.getDimension(R.dimen.wl_notification_button_text_size);
    //
    //
    //    NotificationCompat.Builder builder = null;
    //    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    //
    //
    //        NotificationChannel channel = new NotificationChannel(notificationData.getChannelId() + CHANNEL_POSTFIX_CREATE,"Create Notification",
    //                NotificationManager.IMPORTANCE_HIGH);
    //        channel.setDescription("Wildlink");
    //        channel.enableVibration(false);
    //        channel.setShowBadge(false);
    //
    //        channel.setSound(null, null);
    //        channel.enableLights(true);
    //        channel.setDescription("Create notificaition channel");
    //        channel.setName("Create Notification");
    //
    //        builder = new NotificationCompat.Builder(context, notificationData.getChannelId() + CHANNEL_POSTFIX_CREATE);
    //
    //        notificationManager.createNotificationChannel(channel);
    //
    //    } else {
    //        builder = new NotificationCompat.Builder(context);
    //
    //    }
    //
    //
    //    Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    //
    //
    //    String title = context.getString(R.string.wl_non_persistent_notification_title_text);
    //    Log.d(TAG, "title=" + title);
    //
    //    String content = String.format(resources.getString(R.string.wl_notification_large_view_default_state_line1_text),
    //            context.getString(R.string.wl_app_name));
    //    Log.d(TAG, "content=" + content);
    //
    //    Drawable drawable = resources.getDrawable(R.drawable.wl_notification_icon_large);
    //
    //    builder.setPriority(Notification.PRIORITY_MAX)
    //
    //            .setContentTitle(title)
    //
    //            .setContentText(content)
    //            .setOngoing(false)
    //
    //
    //
    //            .setSmallIcon(R.drawable.wl_notification_icon_small)
    //
    //            .addAction(R.drawable.wl_notification_transparent,
    //                    setColor(R.string.wl_notification_buttons_enable_text),
    //                    intentBuilder.createEnablePendingIntent(((WildlinkSdk.ClipboardMonitorProvider) apiModule.getProvider())
    //                            .provideNotificationData(),context, 10))
    //            .addAction(R.drawable.wl_notification_transparent,
    //                    setColor(R.string.wl_notification_buttons_learn_more_text), intentBuilder.createLearnMorePendingIntent(context, 20))
    //
    //
    //            .setLargeIcon(((BitmapDrawable) drawable).getBitmap())
    //            .setSound(null)
    //            .setVibrate(new long[]{0, 0, 0, 0})
    //            .setContentIntent(utilities.getAppPendingIntent(context).getPendingIntent(context))
    //            .setWhen(0);
    //
    //    notificationManager.notify(Integer.parseInt(notificationData.getNotificationId()), builder.build());
    //
    //    apiModule.getGoogleAnalytics()
    //        .sendEvent("sdk_activated", "app_id", apiModule.getProvider().provideWildlinkAppId());
    //
    //
    //}

    protected SpannableString setColor(int data) {
        String string = resources.getString(data);
        SpannableString contentSpan = new SpannableString(string);
        contentSpan.setSpan(new ForegroundColorSpan(resources.getColor(R.color.wl_orange)), 0,
            string.length(), 0);

        return contentSpan;
    }

    //protected void showPushNotificationData(String title, String content,String actionUrl) {
    //
    //    Log.d(TAG, "actionUrl=" + actionUrl);
    //    final Context context = apiModule.getProvider().provideApplicationContext();
    //    final Utilities utilities = new Utilities();
    //
    //    final NotificationData  notificationData = ((WildlinkSdk.ClipboardMonitorProvider) apiModule.getProvider()).provideNotificationData();
    //
    //    IntentBuilder intentBuilder = new IntentBuilder(resources);
    //
    //    final SharedPrefsUtil prefs = apiModule.getSharedPrefsUtil();
    //
    //
    //    NotificationCompat.Builder builder = null;
    //    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    //
    //
    //        NotificationChannel channel = new NotificationChannel("3467161231" + CHANNEL_POSTFIX_PUSH_NOTIFICATION, "Push Notification",
    //                NotificationManager.IMPORTANCE_HIGH);
    //        channel.setDescription("Push Notification Channel");
    //
    //        channel.enableVibration(false);
    //        channel.setSound(null, null);
    //        channel.enableLights(true);
    //        channel.setShowBadge(false);
    //
    //
    //        channel.setName("Push Notification");
    //        builder = new NotificationCompat.Builder(context,  "3467161231" + CHANNEL_POSTFIX_PUSH_NOTIFICATION);
    //
    //        notificationManager.createNotificationChannel(channel);
    //
    //    } else {
    //        builder = new NotificationCompat.Builder(context);
    //
    //    }
    //
    //    Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    //
    //    Drawable drawable = resources.getDrawable(R.drawable.wl_notification_icon_large);
    //
    //    Log.d(TAG, "actionUrl=" + actionUrl);
    //    builder.setPriority(Notification.PRIORITY_MAX)
    //
    //            .setContentTitle(title)
    //
    //            .setContentText(content)
    //            .setOngoing(false)
    //            .setSmallIcon(R.drawable.wl_notification_icon_small)
    //            .setLargeIcon(((BitmapDrawable) drawable).getBitmap())
    //
    //            .setStyle(new NotificationCompat.BigTextStyle()
    //                    .bigText(content))
    //            .setSound(null)
    //
    //            .setVibrate(new long[]{0,0,0,0})
    //            .setContentIntent(utilities.getPendingIntentPush(context,actionUrl))
    //            .setWhen(0);
    //
    //    notificationManager.notify(676767672, builder.build());
    //
    //
    //
    //}
}
