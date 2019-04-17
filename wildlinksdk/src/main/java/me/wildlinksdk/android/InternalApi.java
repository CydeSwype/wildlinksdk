package me.wildlinksdk.android;

import java.util.List;
import me.wildlinksdk.android.api.CloudServiceApi;
import me.wildlinksdk.android.api.models.LoggerListener;
import me.wildlinksdk.android.api.models.SenderListener;
import me.wildlinksdk.android.api.models.XidRequest;
import me.wildlinksdk.android.models.WildlinkResult;

/**
 * Created by rjawanda on 2/13/18.
 */

public interface InternalApi {

    /***
     * get list of converted links 50 max.
     * @param listener
     */
    public void getWildlinkHistory(HistoryListener listener);

    /***
     *
     * @param listener  public void doNotCreateVanityUrl(ApiError error);
     *                   public void onSuccess(Earnings earnings);
     */
    public void getEarnings(EarningsListenter listener);

    /***
     *
     * @param by Enum.[hour|day|month|year]
     * @param start unix timestamp starting date
     * @param end unix timestamp end date (if null no end date)
     * @param listener public void doNotCreateVanityUrl(ApiError error);
     *                 public void onSuccess(Clickstats clickstats);
     */
    public void clickStats(CloudServiceApi.byEnum by, Long start, Long end,
        ClickstatsListener listener);

    /***
     *
     * @param listener  public void onSuccess(Sender sender);
     *                  public void doNotCreateVanityUrl(ApiError error);
     */
    public void getSender(GetSenderListener listener);

    /***
     *
     * @param phone phone number starting with country code non formatted example 18005551212
     * @param email the email address
     * @param paymentType String one of PAYPAL, NONE
     * @param listener   public void doNotCreateVanityUrl(ApiError error);
     *                     public void onSuccess();
     */

    public void setUserData(String phone, String email, String paymentType,
        SimpleListener listener);

    /***
     *
     * @param phoneNumber phone number starting with country code non formatted example 18005551212
     * @param listener @param listener   public void doNotCreateVanityUrl(ApiError error);
     *                                   public void onSuccess(Message message);
     */
    public void createSender(String phoneNumber, SenderListener listener);


    /***
     *
     * @param type one of  enum EMAIL,PHONE
     * @param data if type== EMAIL , the users email, if type == PHONE
     *             the phone number starting with country code non formatted example 18005551212
     * @param listener public void doNotCreateVanityUrl(ApiError error);
     *                     public void onSuccess();
     */
    public void setPaypalCredentials(WildlinkSdk.RecipientType type, String data,
        SimpleListener listener);

    /***
     * get paypal payment data
     * @param listener PaypalPaymentListener
     *                 public void onSuccess(PaypalCredentials paypalCredentials);
     *                 public void doNotCreateVanityUrl(ApiError error);
     */
    public void getPaypalPayment(PaypalPaymentListener listener);

    ///***
    // * searches for ConceptsItem
    // * @param buffer
    // * @param searchCase
    // * @param limit
    // * @return
    // */
    //public List<WildlinkResult> searchByKind(String buffer, Cache.SearchCase searchCase, int limit);
    //
    //
    ///***
    // * searches the database that was downloaded using the downloadConceptsDatabase method.
    // * @param buffer searches a buffer (many lines of text) for the last 4 words. then takes those words and
    // *               designes a special search to locate combinations of reasults based on the last 4 words.
    // *
    // * @param searchCase enum one of  INSENSITIVE,SENSITIVE
    // * @param limit how many results you to return from the search
    // * @return WildlinkResult object that contains ranking and text.
    // */
    //public List<WildlinkResult> search(String buffer, Cache.SearchCase searchCase, int limit);
    //

    ///***
    // * searches the database downloaded using the downloadMerchantsDatabase.
    // *
    // * @param word the word to search for
    // * @param kind one of domain,keword
    // * @return Vanity
    // */
    //public Vanity search(String word, String kind);

    /***
     * utility  method to rank words
     * @param word ,the word to rank with (uses levenshtein algorithm)
     * @param words  , the list of words
     * @return the ranked list of WildlinkResult objects
     */
    public List<WildlinkResult> rankCharacterSequenceList(String word, List<CharSequence> words);

    /***
     * utility  method to rank words
     * @param word ,the word to rank with (uses levenshtein algorithm)
     * @param words  , the list of words
     * @return the ranked list of WildlinkResult objects
     */
    public List<WildlinkResult> rankWildlinkResultList(String word, List<WildlinkResult> words);

    /***
     * utility  method to rank words
     * @param word ,the word to rank with (uses levenshtein algorithm)
     * @param words  , the list of words
     * @return the ranked list of WildlinkResult objects
     */
    public List<WildlinkResult> rankStringList(String word, List<String> words);

    /***
     *
     * @param thePhraseContext
     * @param contextWithLink
     * @param timestamp
     * @param destinationApp
     * @param listener
     */
    public void phraseLogger(final String thePhraseContext, final String contextWithLink,
        long timestamp, final String destinationApp, final LoggerListener listener);

    /***
     *
     * @param s
     */
    public void updateAttribution(String s);

    /***
     *
     * @param installChannel
     */
    public void updateInstallChannel(String installChannel);

    /***
     *
     */
    public void updateXid(XidRequest xidRequest, SimpleListener listener);
}
