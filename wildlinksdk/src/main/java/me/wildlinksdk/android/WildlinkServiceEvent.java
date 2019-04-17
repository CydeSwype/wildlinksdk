package me.wildlinksdk.android;

/**
 * Created by rjawanda on 1/29/18.
 */

public class WildlinkServiceEvent {
    private WildlinkServiceError wildlinkServiceError;

    public WildlinkServiceEvent(WildlinkServiceError wildlinkServiceError) {
        this.wildlinkServiceError = wildlinkServiceError;
    }

    public WildlinkServiceError getWildlinkServiceError() {
        return wildlinkServiceError;
    }

    public void setWildlinkServiceError(WildlinkServiceError wildlinkServiceError) {
        this.wildlinkServiceError = wildlinkServiceError;
    }
}
