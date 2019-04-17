package me.wildlinksdk.android.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Image implements Parcelable {
    public static final Creator CREATOR = new Creator() {
        public Merchant createFromParcel(Parcel in) {
            return new Merchant(in);
        }

        @Override
        public Merchant[] newArray(int size) {
            return new Merchant[size];
        }
    };
    @Expose @SerializedName("ID") private Long id;
    @Expose @SerializedName("Width") private Integer width;
    @Expose @SerializedName("Height") private Integer height;
    @Expose @SerializedName("ImageID") private Long imageId;
    @Expose @SerializedName("Kind") private String kind;
    @Expose @SerializedName("Ordinal") private Integer ordinal;
    @Expose @SerializedName("URL") private String url;

    public Image(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.width = (Integer) in.readValue(Integer.class.getClassLoader());
        this.height = (Integer) in.readValue(Integer.class.getClassLoader());
        this.imageId = (Long) in.readValue(Long.class.getClassLoader());
        this.kind = in.readString();
        this.ordinal = (Integer) in.readValue(Integer.class.getClassLoader());
        this.url = in.readString();
    }

    public Long getId() {
        return id;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }

    public Long getImageId() {
        return imageId;
    }

    public String getKind() {
        return kind;
    }

    public Integer getOrdinal() {
        return ordinal;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(width);
        dest.writeValue(height);
        dest.writeValue(imageId);
        dest.writeString(kind);
        dest.writeValue(ordinal);
        dest.writeString(url);
    }
}
