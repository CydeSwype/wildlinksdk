package me.wildlinksdk.android.api;

/**
 * Created by ron on 10/14/17.
 */

public interface InitListener {
    public void initialized();

    public void failed(ApiError error);
}
