package me.wildlinksdk.android.callbacks;

import java.util.List;
import me.wildlinksdk.android.api.ApiError;
import me.wildlinksdk.android.models.WildlinkResult;

/**
 * Created by ron on 10/10/17.
 */

public interface SearchCallback {
    public void onSearchResults(List<WildlinkResult> results);

    public void onSearchResultsError(ApiError error);
}
