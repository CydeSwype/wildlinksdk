package me.wildlinksdk.android.api.models.events;

import me.wildlinksdk.android.api.ApiError;

public class WlErrorEvent {
    private ApiError apiError;

    public WlErrorEvent(ApiError apiError) {
        this.apiError = apiError;
    }

    public ApiError getApiError() {
        return apiError;
    }

    public void setApiError(final ApiError apiError) {
        this.apiError = apiError;
    }
}
