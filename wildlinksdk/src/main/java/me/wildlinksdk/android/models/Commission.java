package me.wildlinksdk.android.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ron on 11/26/17.
 */

public class Commission implements Parcelable {

    public static final Creator CREATOR = new Creator() {
        public Commission createFromParcel(Parcel in) {
            return new Commission(in);
        }

        @Override
        public Commission[] newArray(int size) {
            return new Commission[size];
        }
    };
    //The Unique ID of the commission.
    @Expose @SerializedName("ID") private Integer id;
    //The transaction date in ISO 8601 format.
    @Expose @SerializedName("Date") private String date;
    //The commission amount earned from the transaction.
    @Expose @SerializedName("Amount") private Double amount;
    //The current status of the commission.
    @Expose @SerializedName("Status") private String status;
    //The merchant name for the transaction.
    @Expose @SerializedName("Merchant") private String merchant;

    public Commission() {

    }

    public Commission(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.date = in.readString();
        this.amount = (Double) in.readValue(Double.class.getClassLoader());
        this.status = in.readString();
        this.merchant = in.readString();
    }

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(final String date) {
        this.date = date;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(final Double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(final String merchant) {
        this.merchant = merchant;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeString(date);
        dest.writeDouble(amount);
        dest.writeString(status);
        dest.writeString(merchant);
    }
}

