package me.wildlinksdk.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ron on 11/26/17.
 */

public class Earnings {
    @Expose @SerializedName("PaidAmount") private Double paidAmount;
    @Expose @SerializedName("PendingAmount") private Double pendingAmount;
    @Expose @SerializedName("ReadyAmount") private Double readyAmount;

    public Double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Double getPendingAmount() {
        return pendingAmount;
    }

    public void setPendingAmount(Double pendingAmount) {
        this.pendingAmount = pendingAmount;
    }

    public Double getReadyAmount() {
        return readyAmount;
    }

    public void setReadyAmount(Double readyAmount) {
        this.readyAmount = readyAmount;
    }
}
