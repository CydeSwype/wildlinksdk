package me.wildlinksdk.android.api;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.wildlinksdk.android.BuildConfig;
import me.wildlinksdk.android.ClickstatsListener;
import me.wildlinksdk.android.CommissionStatDetailsListener;
import me.wildlinksdk.android.Constants;
import me.wildlinksdk.android.EarningsListenter;
import me.wildlinksdk.android.GetSenderListener;
import me.wildlinksdk.android.HistoryListener;
import me.wildlinksdk.android.MerchantListener;
import me.wildlinksdk.android.PaypalPaymentListener;
import me.wildlinksdk.android.PaypalSenderDataListener;
import me.wildlinksdk.android.SimpleListener;
import me.wildlinksdk.android.ValidationListener;
import me.wildlinksdk.android.VanityListener;
import me.wildlinksdk.android.WebListener;
import me.wildlinksdk.android.WildlinkSdk;
import me.wildlinksdk.android.api.models.AttributionWrapper;
import me.wildlinksdk.android.api.models.HeaderInfo;
import me.wildlinksdk.android.api.models.History;
import me.wildlinksdk.android.api.models.LoggerListener;
import me.wildlinksdk.android.api.models.PaypalCredentials;
import me.wildlinksdk.android.api.models.PhraseLoggerRequest;
import me.wildlinksdk.android.api.models.RegisterDeviceRequest;
import me.wildlinksdk.android.api.models.RegisterDeviceResponse;
import me.wildlinksdk.android.api.models.SenderListener;
import me.wildlinksdk.android.api.models.SenderRequest;
import me.wildlinksdk.android.api.models.XidRequest;
import me.wildlinksdk.android.models.Commission;
import me.wildlinksdk.android.models.Earnings;
import me.wildlinksdk.android.models.Merchant;
import me.wildlinksdk.android.models.MerchantItemDomain;
import me.wildlinksdk.android.models.Message;
import me.wildlinksdk.android.models.PaypalSenderData;
import me.wildlinksdk.android.models.Stats;
import me.wildlinksdk.android.models.Validation;
import me.wildlinksdk.android.models.Vanity;
import me.wildlinksdk.android.services.TokenService;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;

import static me.wildlinksdk.android.api.HttpApi.JSON;

/**
 * Created by ron on 10/4/17.
 */

public class CloudServiceApi {

    public static String os;

    private final String TAG = CloudServiceApi.class.getSimpleName();

    private ApiModule apiModule;

    public CloudServiceApi(ApiModule apiModule) {

        this.apiModule = apiModule;
    }


    private static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }


    public void createDevice(final SimpleListener simpleListener)
    {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        int version = Build.VERSION.SDK_INT;
        String versionRelease = Build.VERSION.RELEASE;

        String phoneData =  "man " + manufacturer
            + " model " + model
            + " version " + version
            + " versionRelease " + versionRelease;

        os = "Android " + getDeviceName() + " " + phoneData;



        Response response = null;
        try {
            String deviceKey = apiModule.getSharedPrefsUtil().getDeviceKey();
            if( deviceKey != null) {

                simpleListener.onSuccess();
                return;
            }

            RegisterDeviceRequest request = new RegisterDeviceRequest(deviceKey, os);

            String baseUrl = apiModule.getSharedPrefsUtil().getBaseApiUrl();

            RequestBody body =
                RequestBody.create(HttpApi.JSON, apiModule.getGson().toJson(request, RegisterDeviceRequest.class));

            HeaderInfo info = AuthInterceptor.buildAuthorizationHeader(apiModule);

            Request okrequest = new Request.Builder().url(baseUrl + "/v2/device")
                .addHeader("Authorization", info.authHeader)
                .addHeader("X-WF-DateTime", info.time)
                .addHeader("User-Agent", BuildConfig.user_agent
                    + ","
                    + BuildConfig.sdk_version
                    + ","
                    + BuildConfig.BUILD_TYPE)
                .post(body)
                .build();





            apiModule.getNoAuthOkHttpClient().newCall(okrequest).enqueue(new Callback() {
                @Override
                public void onFailure(final Call call, final IOException e) {
                    simpleListener.onFailure(
                        new ApiError(ApiError.UNKNOWN_HOST, "Offline or network issue"));
                }

                @Override
                public void onResponse(final Call call, final Response response) throws IOException {

                    if (response.isSuccessful()) {

                        RegisterDeviceResponse registerDeviceResponse =
                            apiModule.getGson().fromJson(response.body().string(), RegisterDeviceResponse.class);


                        apiModule.getSharedPrefsUtil().storeDeviceKey(registerDeviceResponse.getDeviceKey());
                        apiModule.getSharedPrefsUtil().storeDeviceToken(registerDeviceResponse.getDeviceToken());


                        simpleListener.onSuccess();
                        return;
                    } else {
                        ResponseBody responseBody = response.body();


                        int code = response.code();
                        final StringBuffer errorMessage = new StringBuffer();
                        if (code == HttpURLConnection.HTTP_BAD_GATEWAY) {
                            errorMessage.append(" ");
                            response.close();
                            simpleListener.onFailure(
                                new ApiError(ApiError.UNKNOWN_HOST, "Offline or network issue"));
                            return;
                        }

                        errorMessage.append("");
                        if (responseBody != null) {
                            String responsBodyStr = responseBody.string();
                            if (responsBodyStr != null) {
                                try {
                                    JSONObject object = new JSONObject(responsBodyStr);
                                    if (object != null) {
                                        simpleListener.onFailure(new ApiError(code,
                                            AuthInterceptor.getErrorMessage(object)));
                                        return;
                                    }
                                }catch(Exception e) {
                                    simpleListener.onFailure(new ApiError(ApiError.UNKNOWN_HOST,
                                        "Could not get error object from body " + responsBodyStr ));
                                    return;
                                }
                            }
                        }

                        simpleListener.onFailure(new ApiError(code, errorMessage.toString()));
                        return;


                    }
                }
            });

        }catch(Exception e) {
            simpleListener.onFailure(new ApiError(ApiError.UNKNOWN_ERROR, e.getMessage()));

        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (Exception c) {
                }
                ;
            }
        }
    }


    public ApiError createDevice()
    {

        try {
            String deviceKey = apiModule.getSharedPrefsUtil().getDeviceKey();
            if (deviceKey != null) {

                return null;
            }

            Log.d("deviceKey", "inCreateDevice of AuthInterceptor() deviceKey=" + deviceKey);
            RegisterDeviceRequest request = new RegisterDeviceRequest(deviceKey,os);

            String baseUrl = apiModule.getSharedPrefsUtil().getBaseApiUrl();

            RequestBody body = RequestBody.create(HttpApi.JSON,
                apiModule.getGson().toJson(request, RegisterDeviceRequest.class));

            HeaderInfo info = AuthInterceptor.buildAuthorizationHeader(apiModule);

            Request okrequest = new Request.Builder().url(baseUrl + "/v2/device")
                .addHeader("Authorization", info.authHeader)
                .addHeader("X-WF-DateTime", info.time)
                .addHeader("User-Agent", BuildConfig.user_agent
                    + ","
                    + BuildConfig.sdk_version
                    + ","
                    + BuildConfig.BUILD_TYPE)
                .post(body)
                .build();

            Response response = apiModule.getNoAuthOkHttpClient().newCall(okrequest).execute();

            if (response.isSuccessful()) {

                RegisterDeviceResponse registerDeviceResponse =
                    apiModule.getGson().fromJson(response.body().string(), RegisterDeviceResponse.class);

                Log.d("deviceKey", "storing registerDeviceResponse.getDeviceToken()=" + registerDeviceResponse.getDeviceToken());
                apiModule.getSharedPrefsUtil().storeDeviceToken(registerDeviceResponse.getDeviceToken());
                return null;
            } else {
                ResponseBody responseBody = response.body();

                int code = response.code();
                final StringBuffer errorMessage = new StringBuffer();
                if (code == HttpURLConnection.HTTP_BAD_GATEWAY) {
                    errorMessage.append(" ");
                    response.close();
                    return new ApiError(ApiError.UNKNOWN_HOST, "Offline or network issue");
                }

                errorMessage.append("");
                if (responseBody != null) {
                    String responsBodyStr = responseBody.string();
                    if (responsBodyStr != null) {
                        try {
                            JSONObject object = new JSONObject(responsBodyStr);
                            if (object != null) {
                                return new ApiError(code, AuthInterceptor.getErrorMessage(object));
                            }
                        } catch (Exception e) {
                            return new ApiError(ApiError.UNKNOWN_HOST,
                                "Could not get error object from body " + responsBodyStr);
                        }
                    }
                }

                return new ApiError(code, errorMessage.toString());
            }
        }catch(Exception e) {
            return new ApiError(ApiError.UNKNOWN_HOST,e.getMessage());
        }


    }


    public void logger(final DateTime startDate,final DateTime stopDate) {

        DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        // final String theUrl = Uri.decode(urlToConvert);
        final HttpApi httpApi = apiModule.getHttpApi();
        final Gson gson = apiModule.getGson();
        final SharedPrefsUtil prefs = apiModule.getSharedPrefsUtil();
        String baseUrl = prefs.getBaseApiUrl();


        final StringBuffer url = new StringBuffer();
            url.append(baseUrl + "/v2/device/status");

        if( startDate != null) {
            url.append("?start=" + df.print(startDate));
            if( stopDate != null) {
                url.append("&stop=" + df.print(stopDate));
            }
        }else {
            url.append("?stop=" + df.print(stopDate));
        }


        httpApi.getRequest(url.toString(), new okhttp3.Callback() {

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d(TAG, "failure");
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response)
                throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        if (response.body() != null) {
                            String body = response.body().string();
                            int code = response.code();
                            Log.d(TAG, "cloud api exit" + code);
                            try {

                            } catch (Exception e) {

                            }
                        } else {

                        }

                        return;
                    }
                } finally {
                    if (response != null) {
                        try {
                            if (response.body() != null) {
                                response.body().close();
                            }
                            if (response != null) {
                                response.close();
                            }
                        } catch (Exception e) {
                        }
                        ;
                    }
                }
            }
        });
    }

    public void createVanityUrl(final String urlToConvert, final VanityListener listener) {

        // final String theUrl = Uri.decode(urlToConvert);
        final HttpApi httpApi = apiModule.getHttpApi();
        final Gson gson = apiModule.getGson();
        final SharedPrefsUtil prefs = apiModule.getSharedPrefsUtil();
        String baseUrl = prefs.getBaseApiUrl();
        final String url = baseUrl + "/v2/vanity";

        String json = "{\"URL\": \"" + urlToConvert + "\"}";

        httpApi.postRequestQuickTimeout(url, json, new okhttp3.Callback() {

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d(TAG, "failure");
                listener.onFailure(new ApiError(ApiError.UNKNOWN_ERROR, e.getMessage()));
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response)
                throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        if (response.body() != null) {
                            String body = response.body().string();
                            int code = response.code();
                            try {
                                ApiError error = gson.fromJson(body, ApiError.class);
                                error.setCode(code);
                                listener.onFailure(error);
                            } catch (Exception e) {

                                ApiError error = new ApiError(response.code(), body);
                                listener.onFailure(error);
                            }
                        } else {
                            listener.onFailure(new ApiError(response.code(), response.message()));
                        }

                        return;
                    }
                    try {
                        String body = response.body().string();
                        Vanity vanity = gson.fromJson(body, Vanity.class);

                        vanity.setOriginalUrl(urlToConvert);

                        final MerchantItemDomain merchantsItemDomain =
                            apiModule.getCache().searchMerchantItem(urlToConvert);
                        vanity.setDomain(merchantsItemDomain.domain);

                        listener.onSuccess(vanity);
                    } catch (Exception e) {
                        listener.onFailure(new ApiError(ApiError.UNKNOWN_ERROR, e.getMessage()));
                    }
                } finally {
                    if (response != null) {
                        try {
                            if (response.body() != null) {
                                response.body().close();
                            }
                            if (response != null) {
                                response.close();
                            }
                        } catch (Exception e) {
                        }
                        ;
                    }
                }
            }
        });
    }

    /***
     *
     * @param listener SimpleListener
     */
    public void downloadConceptsDatabase(boolean cacheFlag, SimpleListener listener) {

        Log.d(TAG, "downloadConceptsDatabase()");
        final Cache cache = apiModule.INSTANCE.getCache();
        Log.d(TAG, "downloadConceptsDatabase()");

        cache.downloadConceptsDatabase(cacheFlag, listener);
    }

    /***
     *
     * @param listener SimpleListener
     */
    public void downloadMerchantsDatabaseSqlite(boolean cacheFlag, SimpleListener listener) {

        Log.d(TAG, "downloadMerchantsDatabase()");
        final Cache cache = apiModule.INSTANCE.getCache();
        Log.d(TAG, "downloadMerchantsDatabase()");

        cache.downloadMerchantsDatabase(cacheFlag, listener);
    }

    public void createSender(String phoneNumber, final SenderListener listener) {

        final HttpApi httpApi = apiModule.getHttpApi();
        final Gson gson = apiModule.getGson();
        final SharedPrefsUtil prefs = apiModule.getSharedPrefsUtil();

        String baseUrl = prefs.getBaseApiUrl();

        SenderRequest senderRequest = new SenderRequest(phoneNumber);
        try {

            httpApi.postRequest(baseUrl + "/v1" + Constants.ENDPOINT_SENDER + "/sendcode",
                gson.toJson(senderRequest, SenderRequest.class), new okhttp3.Callback() {

                    @Override
                    public void onFailure(okhttp3.Call call, IOException e) {
                        Log.d(TAG, "failure");
                        listener.onFailure(new ApiError(ApiError.UNKNOWN_ERROR, e.getMessage()));
                    }

                    @Override
                    public void onResponse(okhttp3.Call call, okhttp3.Response response)
                        throws IOException {

                        try {
                            Log.d(TAG, "failure");

                            if (!response.isSuccessful()) {
                                if (response.body() != null) {
                                    String body = response.body().string();
                                    int code = response.code();

                                    try {
                                        ApiError error = gson.fromJson(body, ApiError.class);
                                        error.setCode(response.code());
                                        listener.onFailure(error);
                                    } catch (Exception e) {

                                        ApiError error = new ApiError(response.code(), body);
                                        listener.onFailure(error);
                                    }
                                } else {
                                    listener.onFailure(new ApiError(response.code()));
                                }

                                return;
                            }
                            try {
                                Message message =
                                    gson.fromJson(response.body().string(), Message.class);
                                listener.onSuccess(message);
                                return;
                            } catch (Exception e) {
                                listener.onFailure(
                                    new ApiError(ApiError.UNKNOWN_ERROR, e.getMessage()));
                                return;
                            }
                        } finally {
                            if (response != null) {
                                try {
                                    if (response.body() != null) {
                                        response.body().close();
                                    }
                                    if (response != null) {
                                        response.close();
                                    }
                                } catch (Exception e) {
                                }
                                ;
                            }
                        }
                    }
                });
        } catch (Exception e) {
            listener.onFailure(new ApiError(ApiError.UNKNOWN_ERROR, e.getMessage()));
        }
    }



    private void updateServer(String refreshedToken, Context context) {
        Intent intentService = new Intent(context, TokenService.class);
        intentService.putExtra(Constants.KEY_WL_FIREBASE_TOKEN, refreshedToken);
        Log.d(TAG, "starting service to update firebase token");
        context.startService(intentService);
    }

    public void phraseLogger(final String thePhraseContext, final String contextWithLink,
        long timestamp, final String destinationApp, final LoggerListener listener) {

        final HttpApi httpApi = apiModule.getHttpApi();
        final Gson gson = apiModule.getGson();
        DateTime nowUTC = new DateTime(timestamp).withZone(DateTimeZone.UTC);
        DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        final String formattedDate = nowUTC.toString(df);

        final SharedPrefsUtil prefs = apiModule.getSharedPrefsUtil();

        Log.d(TAG, "formattedDate=" + formattedDate);

        String baseUrl = prefs.getBaseApiUrl();

        PhraseLoggerRequest plr =
            new PhraseLoggerRequest(thePhraseContext, contextWithLink, formattedDate,
                destinationApp);

        httpApi.postRequest(baseUrl + "/" + Constants.ENDPOINT_PHRASE,
            gson.toJson(plr, PhraseLoggerRequest.class), new okhttp3.Callback() {

                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    Log.d(TAG, "failure");
                    listener.onFailure(new ApiError(ApiError.UNKNOWN_ERROR, e.getMessage()));
                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response)
                    throws IOException {

                    try {
                        listener.onSuccess();
                        Log.d(TAG, "onResponse call=" + response.code());
                    } finally {
                        if (response != null) {
                            try {
                                if (response.body() != null) {
                                    response.body().close();
                                }
                                if (response != null) {
                                    response.close();
                                }
                            } catch (Exception e) {
                            }
                            ;
                        }
                    }
                }
            });
    }

    public void getEarnings(final EarningsListenter listener) {

        final HttpApi httpApi = apiModule.getHttpApi();
        final Gson gson = apiModule.getGson();
        final SharedPrefsUtil prefs = apiModule.getSharedPrefsUtil();

        String baseUrl = prefs.getBaseApiUrl();

        final String url = baseUrl
            + "/"
            + BuildConfig.apiVersion
            + "/device/"
            + prefs.getUuid()
            + Constants.COMMISSION_SUMMARY;


        httpApi.getRequest(url, new okhttp3.Callback() {

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d(TAG, "failure");
                listener.onFailure(new ApiError(ApiError.UNKNOWN_ERROR, e.getMessage()));
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response)
                throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        if (response.body() != null) {
                            String body = response.body().string();
                            int code = response.code();
                            try {
                                ApiError error = gson.fromJson(body, ApiError.class);
                                error.setCode(code);
                                listener.onFailure(error);
                            } catch (Exception e) {

                                ApiError error = new ApiError(code, body);
                                listener.onFailure(error);
                            }
                        } else {
                            listener.onFailure(new ApiError(response.code()));
                        }

                        return;
                    }
                    try {
                        String body = response.body().string();
                        Earnings result = gson.fromJson(body, Earnings.class);
                        listener.onSuccess(result);
                    } catch (Exception e) {
                        listener.onFailure(new ApiError(ApiError.UNKNOWN_ERROR, e.getMessage()));
                    }
                } finally {
                    if (response != null) {
                        try {
                            if (response.body() != null) {
                                response.body().close();
                            }
                            if (response != null) {
                                response.close();
                            }
                        } catch (Exception e) {
                        }
                        ;
                    }
                }
            }
        });
    }

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
    public void getCommissionStatDetails(final CommissionStatDetailsListener listener) {

        final HttpApi httpApi = apiModule.getHttpApi();
        final Gson gson = apiModule.getGson();
        final SharedPrefsUtil prefs = apiModule.getSharedPrefsUtil();

        String baseUrl = prefs.getBaseApiUrl();

        final String url = baseUrl + "/v2/device/fixme/stats/commission-detail";


        httpApi.getRequest(url, new okhttp3.Callback() {

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d(TAG, "failure");
                listener.onFailure(new ApiError(ApiError.UNKNOWN_ERROR, e.getMessage()));
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response)
                throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        if (response.body() != null) {
                            String body = response.body().string();
                            int code = response.code();
                            try {
                                ApiError error = gson.fromJson(body, ApiError.class);
                                error.setCode(code);
                                listener.onFailure(error);
                            } catch (Exception e) {

                                ApiError error = new ApiError(code, body);
                                listener.onFailure(error);
                            }
                        } else {
                            listener.onFailure(new ApiError(response.code()));
                        }

                        return;
                    }
                    try {
                        String body = response.body().string();
                        List<Commission> commisions =
                            gson.fromJson(body, new TypeToken<List<Commission>>() {
                            }.getType());

                        listener.onSuccess(commisions);
                    } catch (Exception e) {
                        listener.onFailure(new ApiError(ApiError.UNKNOWN_ERROR, e.getMessage()));
                    }
                } finally {
                    if (response != null) {
                        try {
                            if (response.body() != null) {
                                response.body().close();
                            }
                            if (response != null) {
                                response.close();
                            }
                        } catch (Exception e) {
                        }
                        ;
                    }
                }
            }
        });
    }

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
    public void getMerchants(List<Long> merchantIds, final MerchantListener listener) {

        final HttpApi httpApi = apiModule.getHttpApi();
        final Gson gson = apiModule.getGson();
        final SharedPrefsUtil prefs = apiModule.getSharedPrefsUtil();

        String baseUrl = prefs.getBaseApiUrl();
        StringBuffer sb = new StringBuffer();

        // do some checking
        if (merchantIds != null && merchantIds.size() > 0) {
            int i = 0;
            for (Long id : merchantIds) {
                if (i == 0) {
                    sb.append("?id=" + merchantIds.get(i));
                } else {
                    sb.append("&id=" + merchantIds.get(i));
                }
                i++;
            }
        }
        final String url = baseUrl + "/v2/merchant" + sb.toString();


        httpApi.getRequest(url, new okhttp3.Callback() {

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d(TAG, "failure");
                listener.onFailure(new ApiError(ApiError.UNKNOWN_ERROR, e.getMessage()));
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response)
                throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        if (response.body() != null) {
                            String body = response.body().string();
                            int code = response.code();
                            try {
                                ApiError error = gson.fromJson(body, ApiError.class);
                                error.setCode(code);
                                listener.onFailure(error);
                            } catch (Exception e) {

                                ApiError error = new ApiError(code, body);
                                listener.onFailure(error);
                            }
                        } else {
                            listener.onFailure(new ApiError(response.code()));
                        }

                        return;
                    }
                    try {
                        String body = response.body().string();
                        List<Merchant> merchants =
                            gson.fromJson(body, new TypeToken<List<Merchant>>() {
                            }.getType());

                        listener.onSuccess(merchants);
                    } catch (Exception e) {
                        listener.onFailure(new ApiError(ApiError.UNKNOWN_ERROR, e.getMessage()));
                    }
                } finally {
                    if (response != null) {
                        try {
                            if (response.body() != null) {
                                response.body().close();
                            }
                            if (response != null) {
                                response.close();
                            }
                        } catch (Exception e) {
                        }
                        ;
                    }
                }
            }
        });
    }

    public void setPaypalCredentials(WildlinkSdk.RecipientType type, String data,
        final SimpleListener listener) {
        final HttpApi httpApi = apiModule.getHttpApi();
        final Gson gson = apiModule.getGson();
        final SharedPrefsUtil prefs = apiModule.getSharedPrefsUtil();
        String baseUrl = prefs.getBaseApiUrl();

        final String url = baseUrl + "/v1/sender/payment/paypal";

        PaypalCredentials senderRequest = new PaypalCredentials(type.toString(), data);
        Log.d(TAG, "url=" + url);
        httpApi.postRequest(url, gson.toJson(senderRequest, PaypalCredentials.class),
            new okhttp3.Callback() {

                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    Log.d(TAG, "failure");
                    listener.onFailure(new ApiError(ApiError.UNKNOWN_ERROR, e.getMessage()));
                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response)
                    throws IOException {
                    try {
                        if (!response.isSuccessful()) {
                            int code = response.code();
                            if (response.body() != null) {
                                String body = response.body().string();

                                try {
                                    ApiError error = gson.fromJson(body, ApiError.class);
                                    error.setCode(code);
                                    listener.onFailure(error);
                                } catch (Exception e) {

                                    ApiError error = new ApiError(code, body);
                                    listener.onFailure(error);
                                }
                            } else {
                                listener.onFailure(new ApiError(code));
                            }

                            return;
                        }
                        try {
                            listener.onSuccess();
                        } catch (Exception e) {
                            listener.onFailure(
                                new ApiError(ApiError.UNKNOWN_ERROR, e.getMessage()));
                        }
                    } finally {
                        if (response != null) {
                            try {
                                if (response.body() != null) {
                                    response.body().close();
                                }
                                if (response != null) {
                                    response.close();
                                }
                            } catch (Exception e) {
                            }
                            ;
                        }
                    }
                }
            });
    }

    public void getSender(final GetSenderListener listener) {
        final HttpApi httpApi = apiModule.getHttpApi();
        final Gson gson = apiModule.getGson();
        final SharedPrefsUtil prefs = apiModule.getSharedPrefsUtil();

        String baseUrl = prefs.getBaseApiUrl();

        final String url = baseUrl + "/v1/sender";

        Log.d(TAG, "url=" + url);
        httpApi.getRequest(url, new okhttp3.Callback() {

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d(TAG, "failure");
                listener.onFailure(new ApiError(ApiError.UNKNOWN_ERROR, e.getMessage()));
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response)
                throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        if (response.body() != null) {

                            String body = response.body().string();
                            int code = response.code();
                            try {
                                ApiError error = gson.fromJson(body, ApiError.class);
                                error.setCode(code);
                                listener.onFailure(error);
                            } catch (Exception e) {

                                ApiError error = new ApiError(response.code(), body);
                                listener.onFailure(error);
                            }
                        } else {
                            listener.onFailure(new ApiError(response.code()));
                        }

                        return;
                    }
                    try {
                        String body = response.body().string();
                        Sender result = gson.fromJson(body, Sender.class);
                        listener.onSuccess(result);
                    } catch (Exception e) {
                        listener.onFailure(new ApiError(ApiError.UNKNOWN_ERROR, e.getMessage()));
                    }
                } finally {
                    if (response != null) {
                        try {
                            if (response.body() != null) {
                                response.body().close();
                            }
                            if (response != null) {
                                response.close();
                            }
                        } catch (Exception e) {
                        }
                        ;
                    }
                }
            }
        });
    }

    public void getWildlinkHistory(final HistoryListener listener) {
        final HttpApi httpApi = apiModule.getHttpApi();
        final Gson gson = apiModule.getGson();
        final SharedPrefsUtil prefs = apiModule.getSharedPrefsUtil();
        new HistoryAsyncTask(gson, prefs, listener).execute();
    }

    public void getWebData(final String url, final WebListener listener) {

        final HttpApi httpApi = apiModule.getHttpApi();


        httpApi.getRequest(url, new okhttp3.Callback() {

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d(TAG, "failure");
                if (e instanceof UnknownHostException) {
                    listener.onFailure(new ApiError(ApiError.UNKNOWN_HOST, e.getMessage()));
                } else {
                    listener.onFailure(new ApiError(ApiError.UNKNOWN_ERROR, e.getMessage()));
                }
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response)
                throws IOException {
                try {
                    if (!response.isSuccessful()) {

                        listener.onFailure(new ApiError(response.code(), ""));

                        return;
                    }
                    try {
                        String body = response.body().string();

                        listener.onSuccess(body);
                    } catch (Exception e) {
                        listener.onFailure(new ApiError(ApiError.UNKNOWN_ERROR, e.getMessage()));
                    }
                } finally {
                    if (response != null) {
                        try {
                            if (response.body() != null) {
                                response.body().close();
                            }
                            if (response != null) {
                                response.close();
                            }
                        } catch (Exception e) {
                        }
                        ;
                    }
                }
            }
        });
    }

    public void updateAttribution(final String attribution) {
        Log.i(TAG, "updateAtribution in sdk " + attribution);
        if (attribution == null || attribution.trim().length() == 0) {
            Log.d(TAG, "updateAttribution attribution parameter is empty");
            return;
        }
        final HttpApi httpApi = apiModule.getHttpApi();
        final Gson gson = apiModule.getGson();

        final SharedPrefsUtil prefs = apiModule.getSharedPrefsUtil();
        String baseUrl = prefs.getBaseApiUrl();

        Log.d(TAG, "attribution=" + attribution);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(attribution);

            boolean attribute = jsonObject.getBoolean("attribution");
            if (attribute == false) {
                return;
            }
        } catch (Exception e) {
            JSONObject object = null;
            try {
                object = jsonObject.getJSONObject("attribution");

                final String url = baseUrl + "/v2/device";

                AttributionWrapper wrapper = gson.fromJson(attribution, AttributionWrapper.class);

                String networkId = wrapper.getAttribution().getNetwork_id();

                if (networkId == null) {
                    Log.i(TAG, "networkId was not found in Attribution set");
                    return;
                }
                Log.d(TAG, "url=" + url);

                RegisterDeviceRequest request = new RegisterDeviceRequest(null, null);
                //TOTO RON  request.setInstallChannel(networkId);
                httpApi.patchRequest(url, gson.toJson(request, RegisterDeviceRequest.class),
                    new okhttp3.Callback() {

                        @Override
                        public void onFailure(okhttp3.Call call, IOException e) {
                            Log.d(TAG, "failure updateAttribution e=" + e.getMessage());
                        }

                        @Override
                        public void onResponse(okhttp3.Call call, okhttp3.Response response)
                            throws IOException {

                            if (response != null) {
                                try {
                                    if (response.body() != null) {
                                        response.body().close();
                                    }
                                    if (response != null) {
                                        response.close();
                                    }
                                } catch (Exception e) {
                                }
                                ;
                            }

                            Log.d(TAG, "success adding networkId for kochava");
                        }
                    });
            } catch (Exception e1) {
                Log.d(TAG, "updateAttribution e1=" + e1.getMessage());
                if (object == null) {
                    return;
                }
            }
        }
    }

    /***
     * update device with xid ids from non wildlink source
     * @param xidRequest
     */
    public void updateXid(final XidRequest xidRequest, final SimpleListener simpleListener) {
        Log.i(TAG, "updateXid in sdk ");
        if (xidRequest == null) {
            Log.d(TAG, "XidRequest parameter is null");
            return;
        }
        final HttpApi httpApi = apiModule.getHttpApi();
        final Gson gson = apiModule.getGson();
        final SharedPrefsUtil prefs = apiModule.getSharedPrefsUtil();
        String baseUrl = prefs.getBaseApiUrl();

        final String url = baseUrl + "/v2/device/xid";



        httpApi.postRequest(url, gson.toJson(xidRequest, XidRequest.class), new okhttp3.Callback() {

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d(TAG, "failure");
                simpleListener.onFailure(new ApiError(ApiError.UNKNOWN_ERROR, e.getMessage()));
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response)
                throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        if (response.body() != null) {
                            String body = response.body().string();
                            int code = response.code();
                            try {
                                ApiError error = gson.fromJson(body, ApiError.class);
                                error.setCode(code);
                                simpleListener.onFailure(error);
                            } catch (Exception e) {

                                ApiError error = new ApiError(code, body);
                                simpleListener.onFailure(error);
                            }
                        } else {
                            simpleListener.onFailure(new ApiError(response.code()));
                        }

                        return;
                    }
                    simpleListener.onSuccess();
                } finally {
                    if (response != null) {
                        try {
                            if (response.body() != null) {
                                response.body().close();
                            }
                            if (response != null) {
                                response.close();
                            }
                        } catch (Exception e) {
                        }
                        ;
                    }
                }
            }
        });
    }

    /*
     * updates the install channel after the user first installs the application.
     *
     * @param installChannel When UTM source and medium are set to "google" and "cpc" respectively for
     * the first launch of the app, the newly created device should have an install_channel value of "google/cpc"
     * otherwise don't call this method
     */
    public void updateInstallChannel(final String installChannel) {
        Log.i(TAG, "updateInstallChannel " + installChannel);
        if (installChannel == null || installChannel.trim().length() == 0) {
            Log.d(TAG, "updateInstallChannel attribution parameter is empty");
            return;
        }
        final HttpApi httpApi = apiModule.getHttpApi();
        final Gson gson = apiModule.getGson();

        final SharedPrefsUtil prefs = apiModule.getSharedPrefsUtil();
        String baseUrl = prefs.getBaseApiUrl();

        Log.d(TAG, "installChannel=" + installChannel);

        final String url = baseUrl + "/v2/device";

        RegisterDeviceRequest request = new RegisterDeviceRequest(null, null);
        //TOTO RON TO ASK    request.setInstallChannel(installChannel);
        httpApi.patchRequest(url, gson.toJson(request, RegisterDeviceRequest.class),
            new okhttp3.Callback() {

                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    Log.d(TAG, "failure on updateInstallChannel e=" + e.getMessage());
                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response)
                    throws IOException {

                    if (response != null) {
                        try {
                            if (response.body() != null) {
                                response.body().close();
                            }
                            if (response != null) {
                                response.close();
                            }
                        } catch (Exception e) {
                        }
                        ;
                    }

                    Log.d(TAG, "success updateInstallChannel");
                }
            });
    }

    public void acquisition(final String rawInstallReferrer) {
        Log.i(TAG, "rawInstallReferrer " + rawInstallReferrer);
        if (rawInstallReferrer == null || rawInstallReferrer.trim().length() == 0) {
            Log.d(TAG, "rawInstallReferrer attribution parameter is empty");
            return;
        }
        final HttpApi httpApi = apiModule.getHttpApi();
        final Gson gson = apiModule.getGson();

        final SharedPrefsUtil prefs = apiModule.getSharedPrefsUtil();
        String baseUrl = prefs.getBaseApiUrl();

        Log.d(TAG, "installChannel=" + rawInstallReferrer);

        final String url = baseUrl + "/v2/device/acquisition";

        String json = "{\"acquisition\":\"" + rawInstallReferrer + "\"}";
        RequestBody request = RequestBody.create(JSON, json);

        httpApi.postRequest(url, request, new okhttp3.Callback() {

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d(TAG, "failure on acquisition e=" + e.getMessage());
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response)
                throws IOException {

                if (response != null) {
                    try {
                        if (response.body() != null) {
                            response.body().close();
                        }
                        if (response != null) {
                            response.close();
                        }
                    } catch (Exception e) {
                    }
                    ;
                }

                Log.d(TAG, "success acquisition");
            }
        });
    }

    public void setUserData(final String phone, final String email, final String paymentType,
        final SimpleListener listener) {
        final HttpApi httpApi = apiModule.getHttpApi();
        final Gson gson = apiModule.getGson();
        final SharedPrefsUtil prefs = apiModule.getSharedPrefsUtil();
        String baseUrl = prefs.getBaseApiUrl();

        final String url = baseUrl + "/v1/sender";



        Sender sender = new Sender(phone, email, paymentType);

        httpApi.postRequest(url, gson.toJson(sender, Sender.class), new okhttp3.Callback() {

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d(TAG, "failure");
                listener.onFailure(new ApiError(ApiError.UNKNOWN_ERROR, e.getMessage()));
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response)
                throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        if (response.body() != null) {
                            String body = response.body().string();
                            int code = response.code();
                            try {
                                ApiError error = gson.fromJson(body, ApiError.class);
                                error.setCode(code);
                                listener.onFailure(error);
                            } catch (Exception e) {

                                ApiError error = new ApiError(response.code(), body);
                                listener.onFailure(error);
                            }
                        } else {
                            listener.onFailure(new ApiError(response.code()));
                        }

                        return;
                    }
                    try {

                        listener.onSuccess();
                    } catch (Exception e) {
                        listener.onFailure(new ApiError(ApiError.UNKNOWN_ERROR, e.getMessage()));
                    }
                } finally {
                    if (response != null) {
                        try {
                            if (response.body() != null) {
                                response.body().close();
                            }
                            if (response != null) {
                                response.close();
                            }
                        } catch (Exception e) {
                        }
                        ;
                    }
                }
            }
        });
    }

    public void setUserData(final Sender sender, final SimpleListener listener) {
        final HttpApi httpApi = apiModule.getHttpApi();
        final Gson gson = apiModule.getGson();
        final SharedPrefsUtil prefs = apiModule.getSharedPrefsUtil();
        String baseUrl = prefs.getBaseApiUrl();

        final String url = baseUrl + "/v1/sender";

      ;

        httpApi.postRequest(url, gson.toJson(sender, Sender.class), new okhttp3.Callback() {

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d(TAG, "failure");
                listener.onFailure(new ApiError(ApiError.UNKNOWN_ERROR, e.getMessage()));
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response)
                throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        if (response.body() != null) {
                            String body = response.body().string();
                            int code = response.code();
                            try {
                                ApiError error = gson.fromJson(body, ApiError.class);
                                error.setCode(code);
                                listener.onFailure(error);
                            } catch (Exception e) {

                                ApiError error = new ApiError(code, body);
                                listener.onFailure(error);
                            }
                        } else {
                            listener.onFailure(new ApiError(response.code()));
                        }

                        return;
                    }
                    try {
                        listener.onSuccess();
                    } catch (Exception e) {
                        listener.onFailure(new ApiError(ApiError.UNKNOWN_ERROR, e.getMessage()));
                    }
                } finally {
                    if (response != null) {
                        try {
                            if (response.body() != null) {
                                response.body().close();
                            }
                            if (response != null) {
                                response.close();
                            }
                        } catch (Exception e) {
                        }
                        ;
                    }
                }
            }
        });
    }

    public void getPaypalPayment(final PaypalPaymentListener listener) {
        final HttpApi httpApi = apiModule.getHttpApi();
        final Gson gson = apiModule.getGson();
        final SharedPrefsUtil prefs = apiModule.getSharedPrefsUtil();

        String baseUrl = prefs.getBaseApiUrl();

        final String url = baseUrl + "/v1/sender";


        httpApi.getRequest(url, new okhttp3.Callback() {

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d(TAG, "failure");
                listener.onFailure(new ApiError(ApiError.UNKNOWN_ERROR, e.getMessage()));
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response)
                throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        if (response.body() != null) {
                            String body = response.body().string();
                            int code = response.code();

                            try {
                                ApiError error = gson.fromJson(body, ApiError.class);
                                error.setCode(code);
                                listener.onFailure(error);
                            } catch (Exception e) {

                                ApiError error = new ApiError(code, body);
                                listener.onFailure(error);
                            }
                        } else {
                            listener.onFailure(new ApiError(response.code()));
                        }

                        return;
                    }
                    try {
                        String body = response.body().string();
                        int code = response.code();
                        PaypalCredentials result = gson.fromJson(body, PaypalCredentials.class);
                        listener.onSuccess(result);
                    } catch (Exception e) {
                        listener.onFailure(new ApiError(ApiError.UNKNOWN_ERROR, e.getMessage()));
                    }
                } finally {
                    if (response != null) {
                        try {
                            if (response.body() != null) {
                                response.body().close();
                            }
                            if (response != null) {
                                response.close();
                            }
                        } catch (Exception e) {
                        }
                        ;
                    }
                }
            }
        });
    }

    public void createVanityUrl(String placement, final String urlToConvert,
        final VanityListener listener) {

        final String theUrl = Uri.decode(urlToConvert);

        final HttpApi httpApi = apiModule.getHttpApi();
        final Gson gson = apiModule.getGson();
        final SharedPrefsUtil prefs = apiModule.getSharedPrefsUtil();
        String baseUrl = prefs.getBaseApiUrl();

        final String url = baseUrl + "/v2/vanity";


        final VanityRequest request = new VanityRequest();
        request.URL = theUrl;
        request.placement = placement;
        httpApi.postRequestQuickTimeout(url, gson.toJson(request, VanityRequest.class),
            new okhttp3.Callback() {

                @Override
                public void onFailure(okhttp3.Call call, IOException e) {

                    listener.onFailure(new ApiError(ApiError.UNKNOWN_ERROR, e.getMessage()));
                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response)
                    throws IOException {
                    try {
                        if (!response.isSuccessful()) {
                            if (response.body() != null) {
                                String body = response.body().string();
                                int code = response.code();
                                try {
                                    ApiError error = gson.fromJson(body, ApiError.class);
                                    error.setCode(code);
                                    listener.onFailure(error);
                                } catch (Exception e) {

                                    ApiError error = new ApiError(response.code(), body);
                                    listener.onFailure(error);
                                }
                            } else {
                                listener.onFailure(new ApiError(response.code()));
                            }

                            return;
                        }
                        try {
                            String body = response.body().string();

                            Vanity vanity = gson.fromJson(body, Vanity.class);

                            try {

                                final MerchantItemDomain merchantsItemDomain =
                                    apiModule.getCache().searchMerchantItem(theUrl);
                                vanity.setDomain(merchantsItemDomain.domain);
                            } catch (Exception e) {

                            }

                            apiModule.getGoogleAnalytics()
                                .sendEvent("link_analyzed", vanity.getDomain(),
                                    apiModule.getProvider().provideWildlinkAppId());

                            vanity.setOriginalUrl(urlToConvert);
                            listener.onSuccess(vanity);
                        } catch (Exception e) {
                            listener.onFailure(
                                new ApiError(ApiError.UNKNOWN_ERROR, e.getMessage()));
                        }
                    } finally {
                        if (response != null) {
                            try {
                                if (response.body() != null) {
                                    response.body().close();
                                }
                                if (response != null) {
                                    response.close();
                                }
                            } catch (Exception e) {
                            }
                            ;
                        }
                    }
                }
            });
    }

    public Vanity createVanityUrl(String placement, String urlToConvert) {

        final String decodedUrl = Uri.decode(urlToConvert);

        final HttpApi httpApi = apiModule.getHttpApi();
        final Gson gson = apiModule.getGson();
        final SharedPrefsUtil prefs = apiModule.getSharedPrefsUtil();
        String baseUrl = prefs.getBaseApiUrl();

        Response response = null;
        try {

            final String url = baseUrl + "/v2/vanity";


            VanityRequest request = new VanityRequest();
            request.placement = placement;
            request.URL = decodedUrl;

            response = httpApi.postRequestAsync(url, gson.toJson(request, VanityRequest.class));
            if (!response.isSuccessful()) {
                return null;
            }

            String body = response.body().string();

            Vanity vanity = gson.fromJson(body, Vanity.class);

            try {

                final MerchantItemDomain merchantsItemDomain =
                    apiModule.getCache().searchMerchantItem(decodedUrl);
                vanity.setDomain(merchantsItemDomain.domain);
            } catch (Exception e) {

            }

            vanity.setOriginalUrl(urlToConvert);

            return vanity;
        } catch (Exception e) {
            return null;
        } finally {
            if (response != null) {
                try {
                    if (response.body() != null) {
                        response.body().close();
                    }
                    if (response != null) {
                        response.close();
                    }
                } catch (Exception e) {
                }
                ;
            }
        }
    }

    public void clickStats(CloudServiceApi.byEnum by, Long start, Long end,
        final ClickstatsListener listener) {

        final HttpApi httpApi = apiModule.getHttpApi();
        final Gson gson = apiModule.getGson();
        final SharedPrefsUtil prefs = apiModule.getSharedPrefsUtil();

        final DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

        String baseUrl = prefs.getBaseApiUrl();

        final StringBuilder url = new StringBuilder();

        url.append(baseUrl);
        url.append("/" + BuildConfig.apiVersion);
        url.append("/device/");
        url.append(prefs.getUuid());
        url.append(Constants.DEVICE_CLICK_STATS);
        url.append("?by=");
        url.append(by.toString());

        url.append("&start=");

        DateTime startDt = new DateTime(start).withZone(DateTimeZone.UTC);
        String formattedDate = startDt.toString(df);
        url.append(formattedDate.toString());

        if (end != null) {
            DateTime endDt = new DateTime(end).withZone(DateTimeZone.UTC);
            String formattedstopDate = endDt.toString(df);
            url.append("&end=");
            url.append(formattedstopDate.toString());
        }


        httpApi.getRequest(url.toString(), new okhttp3.Callback() {

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d(TAG, "failure");
                listener.onFailure(new ApiError(ApiError.UNKNOWN_ERROR, e.getMessage()));
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response)
                throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        if (response.body() != null) {
                            String body = response.body().string();
                            int code = response.code();
                            try {

                                ApiError error = gson.fromJson(body, ApiError.class);
                                error.setCode(code);
                                listener.onFailure(error);
                            } catch (Exception e) {

                                ApiError error = new ApiError(code, body);
                                listener.onFailure(error);
                            }
                        } else {
                            listener.onFailure(new ApiError(response.code()));
                        }

                        return;
                    }
                    try {
                        DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd");

                        String body = response.body().string();
                        Map<String, Integer> result =
                            gson.fromJson(body, new TypeToken<Map<String, Integer>>() {
                            }.getType());

                        List<Stats> statsList = new ArrayList<Stats>();
                        Map<DateTime, Integer> map = new HashMap<DateTime, Integer>();
                        Iterator it = result.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry) it.next();
                            String key = (String) pair.getKey();
                            Integer value = (Integer) pair.getValue();
                            Log.d(TAG, "server data utc date=" + key.toString());
                            DateTime date = DateTime.parse(key).withZone(DateTimeZone.UTC);
                            Log.d(TAG, "date parsed utc time=" + date.toString());
                            DateTime thisDate = date.toDateTime(DateTimeZone.getDefault());
                            Log.d(TAG, "converted local time=" + thisDate.toString());
                            DateTime startOfDayDateTime = thisDate.withTimeAtStartOfDay();
                            if (map.containsKey(startOfDayDateTime)) {
                                Integer count = map.get(startOfDayDateTime);
                                count += value;
                                map.put(startOfDayDateTime, count);
                            } else {
                                map.put(startOfDayDateTime, value);
                            }
                        }

                        Log.d(TAG,
                            "taking data and grouping it by date, also setting time to 00:00:00");
                        Set<Map.Entry<DateTime, Integer>> entrySet = map.entrySet();
                        Iterator<Map.Entry<DateTime, Integer>> iterator = entrySet.iterator();
                        while (iterator.hasNext()) {

                            Map.Entry<DateTime, Integer> entry = iterator.next();
                            DateTime theDate = entry.getKey();

                            String formattedDate = theDate.toString(df) + " 00:00:00";

                            Log.d(TAG, "grouped date localtm="
                                + formattedDate.toString()
                                + ", click count="
                                + entry.getValue());

                            Stats stats = new Stats(formattedDate, entry.getValue());
                            statsList.add(stats);
                        }
                        Log.d(TAG, "---------sending back to client");

                        listener.onSuccess(statsList);
                    } catch (Exception e) {
                        listener.onFailure(new ApiError(ApiError.UNKNOWN_ERROR, e.getMessage()));
                    }
                } finally {
                    if (response != null) {
                        try {
                            if (response.body() != null) {
                                response.body().close();
                            }
                            if (response != null) {
                                response.close();
                            }
                        } catch (Exception e) {
                        }
                        ;
                    }
                }
            }
        });
    }

    public void getPaypalSenderData(final PaypalSenderDataListener listener) {

        final HttpApi httpApi = apiModule.getHttpApi();
        final Gson gson = apiModule.getGson();
        final SharedPrefsUtil prefs = apiModule.getSharedPrefsUtil();

        final StringBuilder url = new StringBuilder();

        String baseUrl = prefs.getBaseApiUrl();

        url.append(baseUrl);
        url.append("/v1/sender/payment/paypal");


        httpApi.getRequest(url.toString(), new okhttp3.Callback() {

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d(TAG, "failure");
                listener.onFailure(new ApiError(ApiError.UNKNOWN_ERROR, e.getMessage()));
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response)
                throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        if (response.body() != null) {
                            String body = response.body().string();
                            int code = response.code();
                            try {
                                ApiError error = gson.fromJson(body, ApiError.class);
                                error.setCode(code);
                                listener.onFailure(error);
                            } catch (Exception e) {

                                ApiError error = new ApiError(code, body);
                                listener.onFailure(error);
                            }
                        } else {
                            listener.onFailure(new ApiError(response.code()));
                        }

                        return;
                    }
                    try {
                        String body = response.body().string();

                        PaypalSenderData data = gson.fromJson(body, PaypalSenderData.class);

                        listener.onSuccess(data);
                    } catch (Exception e) {
                        listener.onFailure(new ApiError(ApiError.UNKNOWN_ERROR, e.getMessage()));
                    }
                } finally {
                    if (response != null) {
                        try {
                            if (response.body() != null) {
                                response.body().close();
                            }
                            if (response != null) {
                                response.close();
                            }
                        } catch (Exception e) {
                        }
                        ;
                    }
                }
            }
        });
    }

    public enum byEnum {
        hour,
        day,
        month,
        year
    }

    private static class HistoryAsyncTask extends AsyncTask<String, Void, List<History>> {

        private SharedPrefsUtil prefs;
        private HistoryListener listener;
        private Gson gson;

        public HistoryAsyncTask(Gson gson, SharedPrefsUtil prefs, HistoryListener listener) {
            this.prefs = prefs;
            this.listener = listener;
            this.gson = gson;
        }

        @Override
        protected List<History> doInBackground(String... params) {

            String history = prefs.getHistory();

            if (history == null) {
                return new ArrayList<History>();
            }
            List<History> historyList = gson.fromJson(history, new TypeToken<List<History>>() {
            }.getType());
            return historyList;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(List<History> result) {
            listener.onSuccess(result);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private class VanityRequest {
        @Expose public String URL;
        @Expose @SerializedName("Placement") public String placement;
    }
}
