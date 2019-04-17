package me.wildlinksdk.android.api;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import me.wildlinksdk.android.BuildConfig;
import me.wildlinksdk.android.api.models.HeaderInfo;
import me.wildlinksdk.android.api.models.RegisterDeviceRequest;
import me.wildlinksdk.android.api.models.RegisterDeviceResponse;
import me.wildlinksdk.android.api.models.Session;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;

/***
 *
 */

public class AuthInterceptor implements Interceptor {

    private final String TAG = AuthInterceptor.class.getSimpleName();
    private String bodyString;


    private Object objectLock;
    private ApiModule apiModule;



    public AuthInterceptor(ApiModule apiModule) {

        objectLock = new Object();
        this.apiModule = apiModule;


    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request originalRequest = chain.request();
        Request newRequest;
        final SharedPrefsUtil prefs = apiModule.getSharedPrefsUtil();
        final OkHttpClient noAuthOkHttpClient = apiModule.getNoAuthOkHttpClient();
        final OkHttpClient okHttpClient = apiModule.getOkHttpClient();
        final Gson gson = apiModule.getGson();

        Response newResponse;
        try {

            //Log.d("deviceKey", "testing device token");
            //if (prefs.getDeviceToken().length() == 0) {
            //    Log.d(TAG, "len was zero");
            //    createDevice(prefs, noAuthOkHttpClient, gson);
            //}


            originalRequest = makeRequest(originalRequest);

            newResponse = chain.proceed(originalRequest);

            final int httpStatus = newResponse.code();

            // between 200 and 300
            if (httpStatus >= HttpURLConnection.HTTP_OK
                && httpStatus < HttpURLConnection.HTTP_MULT_CHOICE) {
                updateResponseHeaders(prefs, newResponse);
            }
            if (newResponse.code() == HttpURLConnection.HTTP_CONFLICT) {
                new AuthenticationException(newResponse.code(), newResponse.message());
            } else if (newResponse.code() == HttpURLConnection.HTTP_FORBIDDEN
                || newResponse.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {

               prefs.removeDeviceToken();
                prefs.removeSenderToken();


                Request.Builder builder = originalRequest.newBuilder();

                HeaderInfo hi = buildAuthorizationHeader(apiModule);
                builder.removeHeader("Authorization");
                builder.removeHeader("X-WF-DateTime");
                builder.removeHeader("User-Agent");

                builder.addHeader("Authorization", hi.authHeader);
                builder.addHeader("X-WF-DateTime", hi.time);
                builder.addHeader("User-Agent", BuildConfig.user_agent
                    + ","
                    + BuildConfig.sdk_version
                    + ","
                    + BuildConfig.BUILD_TYPE);
                originalRequest = builder.build();

                return chain.proceed(originalRequest);
            }
        } catch (NoSuchAlgorithmException nsa) {
            Log.d(TAG, "AuthInterceptor " + NoSuchAlgorithmException.class.getSimpleName());
            newResponse = chain.proceed(originalRequest);
            // what is the worst that can happen. No authorization i guess
        } catch (InvalidKeyException invk) {
            Log.d(TAG, "AuthInterceptor " + InvalidKeyException.class.getSimpleName());
            newResponse = chain.proceed(originalRequest);
        } catch (Exception e) {
            throw new IOException(e);
        }

        return newResponse;
    }

    /***
     *
     * @param originalRequest
     * @return Request from the original request add the headers and make a new request
     * @throws Exception
     */
    private Request makeRequest(Request originalRequest) throws Exception {
        HeaderInfo info = buildAuthorizationHeader(apiModule);

        Request.Builder builder = originalRequest.newBuilder();

        builder.header("Cache-Control", "no-cache")
            .header("Authorization", info.authHeader)
            .header("X-WF-DateTime", info.time)
            .header("User-Agent", BuildConfig.user_agent
                + ","
                + BuildConfig.sdk_version
                + ","
                + BuildConfig.BUILD_TYPE);
        originalRequest = builder.build();

        return originalRequest;
    }




    public static String getErrorMessage(final JSONObject object) {
        try {
            String errorMessageV2 = object.getString("ErrorMessage");
            return errorMessageV2;
        } catch (JSONException je) {
            return "N/A";
        }
    }

    /***
     * parses response headers looking for a change in the device or sender token
     * stores them in
     * @param response The response Header from OkHttp

     */
    private void updateResponseHeaders(SharedPrefsUtil prefs, Response response) {

        if (response.header("x-wf-devicetoken") != null) {
            prefs.storeDeviceToken(response.header("x-wf-devicetoken"));
        }
        if (response.header("x-wf-sendertoken") != null) {
            prefs.storeSenderToken(response.header("x-wf-sendertoken"));
        }
    }

    /***
     * build the header required to make a request
     *
     * should look like this WFAV1 3:9d5c3858a5b3a43a425dfa913584149df0826353712e0a7722988a74493aee1c:d5b5d717e345cb273fabe4c743dd72a1500290:
     * @param apiModule
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static HeaderInfo buildAuthorizationHeader(ApiModule apiModule)
        throws NoSuchAlgorithmException, InvalidKeyException {

        DateTime now = DateTime.now();
        DateTime nowUTC = DateTime.now().withZone(DateTimeZone.UTC);
        // TODO 2018-11-14T02:59:17.097Z
        org.joda.time.format.DateTimeFormatter df =
            DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        String formattedDate = nowUTC.toString(df);


        final Session session = reloadTokens(apiModule.getSharedPrefsUtil());

        String stringToSign = formattedDate
            + "\n"
            + session.getDeviceToken()
            + "\n"
            + session.getSenderToken()
            + "\n";


        String hash = apiModule.getCryptographyApi()
            .createSha256Hmac(stringToSign, apiModule.getProvider().provideClientSecret());


        String authHeader = BuildConfig.authHeaderPrefix
            + " "
            + apiModule.getProvider().provideWildlinkAppId()
            + ":"
            + hash
            + ":"
            + session.getDeviceToken()
            + ":"
            + session.getSenderToken();


        HeaderInfo hi = new HeaderInfo();
        hi.time = formattedDate;
        hi.authHeader = authHeader;
        return hi;
    }

    private static Session reloadTokens(SharedPrefsUtil prefs) {
        String deviceToken = prefs.getDeviceToken();
        String senderToken = prefs.getSenderToken();

        Session session = new Session(deviceToken, senderToken);

        return session;
    }


}