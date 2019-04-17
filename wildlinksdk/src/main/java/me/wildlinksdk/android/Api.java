package me.wildlinksdk.android;

import java.util.List;
import me.wildlinksdk.android.models.Vanity;

/**
 * Created by rjawanda on 12/15/17.
 */

public interface Api {

    /***
     *  this will download a database to the sdk that you are using and will
     *  allow local searching using some of the search methods provided by the sdk
     */
    public void startInputMonitor() throws Exception;

    /***
     * this will start a hidden background service
     * you are responsible for keeping it running.
     * Place this in your main launcher app and listener for this event. You then can
     * restart the clipboard monitoring service
     *
     *   @Subscribe(threadMode = ThreadMode.MAIN)
     *    public void onEvent(ServiceStoppedEvent event) {
     *    }
     *
     *
     */
    /***
     *
     * @param listener response listener
     *
     */
    public void startClipboardMonitoringService(SimpleListener listener) ;

    /***
     *
     * @param url
     * @param listener
     */
    public void createVanityUrl(String url, VanityListener listener);


    /***
     * this is an asynchronous call
     * @param placement - can be anything you want that specifies how you are using it
     * @param url - the store url (example:  http://nordstrom.com/738972397w98e
     * @param listener - callback with vanity NOTE: this returns on background thread
     */
    public void createVanityUrl(String placement, String url, VanityListener listener);

    /***
     * this is a syncronous call
     * @param placement- can be anything you want that specifies how you are using it
     * @param url - the store url (example:  http://nordstrom.com/738972397w98e
     * @return Vanity this can be null
     */
    public Vanity createVanityUrl(String placement, String url) throws Exception;

    /***
     * Returns an array of zero or more commission items earned for the given device.
     * Each commission amount will be rounded to whole cents for display purposes.
     * The actual amount paid to the user when all eligible commissions are combined
     * for a payout may be vary slightly due to rounding differences.
     * @param listener CommissionStatDetailsListener contains
     *  public void onFailure(ApiError error);  ApiError contains code and message.
     *  if there was a response code from the server code will be the http status code
     *  and the message will be a reason for the error.
     *  if there is no response from the serer code will be ApiError.UNKNOWN_ERROR (-1)
     *  public void onSuccess(List<Commission> commissions);
     */
    public void getCommissionStatsDetails(final CommissionStatDetailsListener listener);

    /***
     *
     * @param merchantIds list of integers for merchant Id's.
     * @param listener  MerchantListener contains
     *  public void onFailure(ApiError error);  ApiError contains code and message.
     *  if there was a response code from the server code will be the http status code
     *  and the message will be a reason for the error.
     *  if there is no response from the serer code will be ApiError.UNKNOWN_ERROR (-1)
     *  public void onSuccess(List<Merchant> merchants);
     */

    public void getMerchants(List<Long> merchantIds, final MerchantListener listener);
}