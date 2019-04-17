package me.wildlinksdk.android.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ron on 11/26/17.
 */

public class Merchant implements Parcelable {

    public static final Creator CREATOR = new Creator() {
        public Merchant createFromParcel(Parcel in) {
            return new Merchant(in);
        }

        @Override
        public Merchant[] newArray(int size) {
            return new Merchant[size];
        }
    };
    //The MerchantID.
    @Expose @SerializedName("ID") private Long id;
    //The Merchant name.
    @Expose @SerializedName("Name") private String name;
    //TUsed for removing from public view.
    @Expose @SerializedName("Disabled") private Boolean disabled;
    //Should be Featured.
    @Expose @SerializedName("Featured") private Boolean featured;
    //Shortcode path
    @Expose @SerializedName("ShortURL") private String shortUrl;
    //Shortcode path
    @Expose @SerializedName("Images") private List<Image> images;

    public Merchant() {

    }

    public Merchant(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.name = in.readString();
        this.disabled = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.featured = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.shortUrl = in.readString();
        images = new ArrayList<Image>(0);
        in.readList(images, Image.class.getClassLoader());
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public Boolean getFeatured() {
        return featured;
    }

    public List<Image> getImages() {
        return images;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeString(name);
        dest.writeValue(disabled);
        dest.writeValue(featured);
        dest.writeString(shortUrl);
        dest.writeList(images);
    }
}

