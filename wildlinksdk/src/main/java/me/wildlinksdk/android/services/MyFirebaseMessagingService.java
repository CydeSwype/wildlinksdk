package me.wildlinksdk.android.services;

import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Map;
import me.wildlinksdk.android.SimpleListener;
import me.wildlinksdk.android.api.ApiError;
import me.wildlinksdk.android.api.ApiModule;

//MessageData {        "CommissionAmount": "10.0",        "MerchantName":"Name"  }
// MessageType COMMISSION_EARNED_1

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private NonPersistentNotification notificationHelper;
    private NotificationManager notificationManager;
    private ApiModule apiModule;

    public MyFirebaseMessagingService() {

        apiModule = ApiModule.INSTANCE;
        if (apiModule == null) {
            Log.d(TAG, "api module is null");
        }
        notificationManager = ((NotificationManager) apiModule.getProvider()
            .provideApplicationContext()
            .getSystemService(NOTIFICATION_SERVICE));
        Resources resources = apiModule.getProvider().provideApplicationContext().getResources();

        notificationHelper = new NonPersistentNotification(notificationManager, apiModule);
    }

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (isKindleFire()) {

            return;
        }

        if (apiModule.INSTANCE.getSharedPrefsUtil().isWlEnabled() == null) {
            Log.d(TAG, "not enabled in onMessageReceived");
            return;
        }
        Log.d(TAG, "sending remoteMessage");
        if (notificationHelper == null) {
            Log.d(TAG, "notificationHelper is null");
        }

        Map<String, String> data = remoteMessage.getData();
        if (data != null) {
            try {
                Gson gson = apiModule.INSTANCE.getGson();
                final Context context =
                    apiModule.INSTANCE.getProvider().provideApplicationContext();

                if (data.containsKey("MessageType")) {
                    String messageType = data.get("MessageType");
                    String json = data.get("MessageData");

                    //if (messageType.equals("COMMISSION_EARNED_1")) {
                    //    CommissionEarned commissionEarned =
                    //        (CommissionEarned) (gson).fromJson(json,
                    //            CommissionEarned.class);
                    //    handleCommissionEarned(commissionEarned, context,
                    //        BuildConfig.baseWebUrl + "/earnings");
                    //if (messageType.equals("COMMISSION_EARNED_BATCH_1")) {
                    //    CommissionEarnedBatch commissionEarnedBatch =
                    //        (CommissionEarnedBatch) (gson).fromJson(json,
                    //            CommissionEarnedBatch.class);
                    //    handleCommissionEarnedBatch(commissionEarnedBatch, context,
                    //        BuildConfig.baseWebUrl + "/earnings");
                    //} else if (messageType.equals("MIN_PAYMENT_REACHED_1")) {
                    //    handleMinPaymentReached(context, "https://www.wildlink.me/user/earnings");
                    //} else if (messageType.equals("MIN_PAYMENT_REACHED_NO_PAY_METHOD_1")) {
                    //    handleMinPaymentReachedNoPayMethod(context,
                    //        BuildConfig.baseWebUrl + "/payment-preferences");
                    //} else if (messageType.equals("DEPOSIT_SUCCESS_1")) {
                    //    Deposit deposit =
                    //        (Deposit) (gson).fromJson(json.toString(), Deposit.class);
                    //    handleDeposit(deposit, context,
                    //        BuildConfig.baseWebUrl + "/payment-preferences");
                    //} else if (messageType.equals("DEPOSIT_ERROR_1")) {
                    //
                    //    handleDepositError(context,
                    //        BuildConfig.baseWebUrl + "/payment-preferences");
                    if (messageType.equals("CLEAR_CACHE_1")) {
                        handleClearCache();
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "Exception e=" + e.getMessage());
            }
        }
    }

    public static boolean isKindleFire() {
        return android.os.Build.MANUFACTURER.equals("Amazon") && (android.os.Build.MODEL.equals(
            "Kindle Fire") || android.os.Build.MODEL.startsWith("KF"));
    }

    // first click notification.
    private void handleClearCache() {

        apiModule.getCache().downloadMerchantsDatabase(false, new SimpleListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "force downloadMerchantsDatabase");
            }

            @Override
            public void onFailure(final ApiError error) {
                Log.d(TAG, "force downloadMerchantsDatabase failure code="
                    + error.getCode()
                    + ",message="
                    + error.getMessage());
            }
        });
    }

    private class CommissionEarned {

        @Expose @SerializedName("CommissionAmount") public String commissionAmount;
        @Expose @SerializedName("MerchantName") public String merchantName;

        public CommissionEarned() {

        }
    }

    private class CommissionEarnedBatch {

        @Expose @SerializedName("CommissionAmount") public String commissionAmount;
        @Expose @SerializedName("CommissionAmount") public String merchantName;
        @Expose @SerializedName("MerchantCount") public String merchantCount;
    }

    //private void handleDepositError(  Context context,String url) {
    //    String title = context.getString(R.string.wl_push_deposit_error_title);
    //    String content = String.format(context.getString(R.string.wl_push_deposit_error_body));
    //
    //    notificationHelper.showPushNotificationData(title,content,url);
    //}
    //
    //private void handleDeposit( Deposit deposit,Context context,String url) {
    //    String title = context.getString(R.string.wl_push_deposit_success_title);
    //    String content = String.format(context.getString(R.string.wl_push_deposit_success_body),
    //        deposit.amount);
    //
    //    notificationHelper.showPushNotificationData(title,content,url);
    //}
    //
    //private void handleMinPaymentReached(Context context,String url) {
    //    String title = context.getString(R.string.wl_push_min_payment_reached_title);
    //    String content =context.getString(R.string.wl_push_min_payment_reached_body);
    //
    //    notificationHelper.showPushNotificationData(title,content,url);
    //}
    //private void handleMinPaymentReachedNoPayMethod(Context context,String url) {
    //    String title = context.getString(R.string.wl_push_min_payment_reached_no_pay_method_title);
    //    String content =context.getString(R.string.wl_push_min_payment_reached_no_pay_method_body);
    //
    //    notificationHelper.showPushNotificationData(title,content,url);
    //}

    //private void handleCommissionEarned(CommissionEarned commissionEarned,Context context,String url) {
    //    String title = context.getString(R.string.wl_push_commission_earned_title);
    //    String content = String.format(context.getString(R.string.wl_push_commission_earned_body),
    //        commissionEarned.commissionAmount,
    //        commissionEarned.merchantName);
    //
    //    notificationHelper.showPushNotificationData(title,content,url);
    //}
    //private void handleCommissionEarnedBatch(CommissionEarnedBatch commissionEarnedBatch,Context context,String url) {
    //    String title = context.getString(R.string.wl_push_commission_earned_title);
    //    String content = String.format(context.getString(R.string.wl_push_commission_earned_batch_body),
    //        commissionEarnedBatch.commissionAmount,
    //        commissionEarnedBatch.merchantName,
    //        commissionEarnedBatch.merchantCount);
    //
    //
    //    notificationHelper.showPushNotificationData(title,content,url);
    //}
    //private void handleCommissionEarnedBatch(CommissionEarned commissionEarned,Context context,String url) {
    //    String title = context.getString(R.string.wl_push_commission_earned_title);
    //    String content = String.format(context.getString(R.string.wl_push_commission_earned_body),
    //        commissionEarned.commissionAmount,
    //        commissionEarned.merchantName);
    //
    //    notificationHelper.showPushNotificationData(title,content,url);
    //}

    private class Deposit {

        @Expose @SerializedName("Amount") public String amount;
    }
}
