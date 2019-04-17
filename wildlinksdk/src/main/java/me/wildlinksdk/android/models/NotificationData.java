package me.wildlinksdk.android.models;

/**
 * Created by rjawanda on 12/15/17.
 */

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import me.wildlinksdk.android.api.WlPendingIntent;

/**
 * Created by rjawanda on 12/10/17.
 */

public class NotificationData implements Parcelable {

    public static final Creator<NotificationData> CREATOR = new Creator<NotificationData>() {
        @Override
        public NotificationData createFromParcel(final Parcel in) {
            return new NotificationData(in);
        }

        @Override
        public NotificationData[] newArray(final int bytes) {
            return new NotificationData[bytes];
        }
    };
    @Expose private String notificationId;
    @Expose private String channelId;
    @Expose private String channelName;
    @Expose private Boolean isEnabledByDefault;
    @Expose private WlPendingIntent settingsPendingIntent;

    public NotificationData() {

    }

    /***
     parcellable stuff
     */

    public NotificationData(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        notificationId = in.readString();
        channelId = in.readString();
        channelName = in.readString();
        isEnabledByDefault = (Boolean) in.readValue(Boolean.class.getClassLoader());
        settingsPendingIntent =
            (WlPendingIntent) in.readValue(WlPendingIntent.class.getClassLoader());
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public Boolean isEnabledByDefault() {
        if (isEnabledByDefault == null) {
            return false;
        }
        return isEnabledByDefault;
    }

    public void setEnabledByDefault(Boolean enabledByDefault) {
        isEnabledByDefault = enabledByDefault;
    }

    public WlPendingIntent getSettingsPendingIntent() {
        return settingsPendingIntent;
    }

    public void setSettingsPendingIntent(WlPendingIntent settingsPendingIntent) {
        this.settingsPendingIntent = settingsPendingIntent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(notificationId);
        dest.writeString(channelId);
        dest.writeString(channelName);
        dest.writeValue(isEnabledByDefault);
        dest.writeValue(settingsPendingIntent);
    }
}
