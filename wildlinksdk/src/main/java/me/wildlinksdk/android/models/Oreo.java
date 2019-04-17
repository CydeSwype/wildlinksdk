package me.wildlinksdk.android.models;

/**
 * Created by rjawanda on 12/15/17.
 */

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;

/**
 * Created by rjawanda on 12/10/17.
 */

public class Oreo implements Parcelable {
    public static final Creator<Oreo> CREATOR = new Creator<Oreo>() {
        @Override
        public Oreo createFromParcel(final Parcel in) {
            return new Oreo(in);
        }

        @Override
        public Oreo[] newArray(final int bytes) {
            return new Oreo[bytes];
        }
    };
    @Expose private String channelDescription;
    @Expose private String channelId;
    @Expose private String channelName;
    @Expose private Boolean enableVibration;
    @Expose private Boolean enableLights;

    public Oreo() {

    }

    public Oreo(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        channelDescription = in.readString();
        channelId = in.readString();
        channelName = in.readString();

        enableVibration = (Boolean) in.readValue(Boolean.class.getClassLoader());
        enableLights = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public String getChannelDescription() {
        return channelDescription;
    }

    public void setChannelDescription(String channelDescription) {
        this.channelDescription = channelDescription;
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

    public Boolean getEnableVibration() {
        return enableVibration;
    }

    public void setEnableVibration(Boolean enableVibration) {
        this.enableVibration = enableVibration;
    }

    public Boolean getEnableLights() {
        return enableLights;
    }

    public void setEnableLights(Boolean enableLights) {
        this.enableLights = enableLights;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(channelDescription);
        dest.writeString(channelId);
        dest.writeString(channelName);
        dest.writeValue(enableVibration);
        dest.writeValue(enableLights);
    }
}
