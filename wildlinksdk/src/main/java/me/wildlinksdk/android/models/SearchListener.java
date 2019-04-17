package me.wildlinksdk.android.models;

import java.util.List;
import me.wildlinksdk.android.api.models.BaseListener;

/**
 * Created by ron on 10/16/17.
 */

public interface SearchListener extends BaseListener {
    public void onSuccess(List<WildlinkResult> results);
}
