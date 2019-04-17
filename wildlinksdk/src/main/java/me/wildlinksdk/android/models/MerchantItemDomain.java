package me.wildlinksdk.android.models;

public class MerchantItemDomain {
    public MerchantItem merchantItem;
    public String domain;

    public MerchantItemDomain(MerchantItem item, String domain) {
        this.merchantItem = item;
        this.domain = domain;
    }
}
