package me.wildlinksdk.android.api;

import android.util.Log;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ron on 11/18/17.
 */

public final class HttpApi {
    public static final MediaType JSON = MediaType.parse("application/json;;charsest=utf-8");
    private static final String TAG = HttpApi.class.getSimpleName();
    private ApiModule apiModule;

    public HttpApi(ApiModule apiModule) {
        this.apiModule = apiModule;
    }

    public void postRequest(String url, String json, okhttp3.Callback callback) {
        final OkHttpClient client = apiModule.getOkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url)

            .post(body).build();
        client.newCall(request).enqueue(callback);
    }

    public void postRequest(String url, RequestBody body, okhttp3.Callback callback) {
        final OkHttpClient client = apiModule.getOkHttpClient();

        Request request = new Request.Builder().url(url)

            .post(body).build();
        client.newCall(request).enqueue(callback);
    }

    public void patchRequest(String url, String json, okhttp3.Callback callback) {
        final OkHttpClient client = apiModule.getOkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).patch(body).build();
        client.newCall(request).enqueue(callback);
    }

    public void postRequestQuickTimeout(String url, String json, okhttp3.Callback callback) {
        Log.d(TAG, "Using quick timeout ok http client");

        final OkHttpClient client = apiModule.getQuickTimeoutHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(body).build();

        client.newCall(request).enqueue(callback);
    }

    public int postRequest(String url, String json) throws IOException {
        final OkHttpClient client = apiModule.getOkHttpClient();

        RequestBody body = RequestBody.create(JSON, json);

        Request request = new Request.Builder().url(url)

            .post(body).build();
        Response response = client.newCall(request).execute();

        return response.code();
    }

    public Response postRequestAsync(String url, String json) throws IOException {
        final OkHttpClient client = apiModule.getOkHttpClient();

        RequestBody body = RequestBody.create(JSON, json);

        Request request = new Request.Builder().url(url)

            .post(body).build();
        Response response = client.newCall(request).execute();

        return response;
    }

    /***
     * get with callback
     * @param url
     * @param callback
     */
    public void getRequest(String url, okhttp3.Callback callback) {
        final OkHttpClient client = apiModule.getOkHttpClient();

        Request request = new Request.Builder().url(url)
            .header("Content-Type", "application/json; charset=utf-8")
            .build();
        client.newCall(request).enqueue(callback);
    }

    public void getRequestTest(String url, okhttp3.Callback callback) {
        final OkHttpClient client = apiModule.getNoAuthOkHttpClient();

        Request request = new Request.Builder().url(url)

            .build();
        client.newCall(request).enqueue(callback);
    }

    /***
     * get with callback
     * @param url

     */
    public Response getRequestSync(String url) throws Exception {
        final OkHttpClient client = apiModule.getOkHttpClient();

        Request request = new Request.Builder().url(url)

            .header("Content-Type", "application/json; charset=utf-8").build();
        return client.newCall(request).execute();
    }
}
