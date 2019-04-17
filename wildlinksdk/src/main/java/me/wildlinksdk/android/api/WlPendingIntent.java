package me.wildlinksdk.android.api;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class WlPendingIntent implements Parcelable {
    public static final Creator<WlPendingIntent> CREATOR = new Creator<WlPendingIntent>() {
        @Override
        public WlPendingIntent createFromParcel(Parcel source) {
            return new WlPendingIntent(source);
        }

        @Override
        public WlPendingIntent[] newArray(int size) {
            return new WlPendingIntent[size];
        }
    };
    protected int requestCode;
    protected int flags;
    @NonNull protected Intent[] intents = null;
    private int length;

    private WlPendingIntent(int requestCode, @NonNull Intent[] intents, int flags) {

        this.flags = flags;
        this.intents = intents;
        this.requestCode = requestCode;
    }

    protected WlPendingIntent(Parcel in) {

        this.requestCode = in.readInt();
        this.flags = in.readInt();
        this.length = in.readInt();
        intents = new Intent[length];
        for (int i = 0; i < this.length; i++) {
            this.intents[i] = (Intent) in.readParcelable(Intent.class.getClassLoader());
        }
    }

    public static WlPendingIntent getActivities(int requestCode, @NonNull Intent[] intents,
        int flags) {
        return new WlPendingIntent(requestCode, intents, flags);
    }

    @Nullable
    public PendingIntent getPendingIntent(@NonNull Context context) {
        PendingIntent pendingIntent =
            PendingIntent.getActivities(context, requestCode, intents, flags);
        return pendingIntent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(this.requestCode);
        dest.writeInt(this.flags);
        dest.writeInt(intents.length);
        for (Intent intent : intents) {
            dest.writeParcelable(intent, Intent.PARCELABLE_WRITE_RETURN_VALUE);
        }
    }
}