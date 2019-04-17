package me.wildlinksdk.android.utilities;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import me.wildlinksdk.android.BuildConfig;
import me.wildlinksdk.android.api.WlPendingIntent;
import me.wildlinksdk.android.models.Item;
import me.wildlinksdk.android.models.WildlinkBufferResult;
import me.wildlinksdk.android.models.WildlinkResult;
import me.wildlinksdk.android.ui.WlEnableActivity;
import okhttp3.OkHttpClient;

/**
 * Created by ron on 10/3/17.
 */

public class Utilities {
    public static final Comparator<WildlinkResult> DESCENDING_COMPARATOR =
        new Comparator<WildlinkResult>() {
            // Overriding the compare method to sort the age
            public int compare(WildlinkResult d, WildlinkResult d1) {
                return d.getRank().compareTo(d1.getRank());
            }
        };
    public static final Comparator<WildlinkResult> DECENDING_COMPARATOR2 =
        new Comparator<WildlinkResult>() {
            public int compare(WildlinkResult o1, WildlinkResult o2) {
                return Double.compare(o1.getRank(), o2.getRank());
            }
        };
    public static final Comparator<WildlinkBufferResult> DECENDING_COMPARATOR_WILDLINKBUFFER =
        new Comparator<WildlinkBufferResult>() {
            public int compare(WildlinkBufferResult o1, WildlinkBufferResult o2) {
                return Double.compare(o1.getRank(), o2.getRank());
            }
        };
    public static final Comparator<Item> DECENDING_ITEM_SCORE_COMPARATOR = new Comparator<Item>() {
        public int compare(Item o1, Item o2) {
            return Double.compare(o1.getScore(), o2.getScore());
        }
    };
    private static final String TAG = Utilities.class.getSimpleName();

    public Utilities() {

    }

    /***
     * only used in debug mode.
     * @param builder
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public static void trustEverythingInDebug(OkHttpClient.Builder builder)
        throws NoSuchAlgorithmException, KeyManagementException {
        // extra check so the client user doesn't use this without knowing what is going on
        if (BuildConfig.DEBUG) {
            final TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
                        String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
                        String authType) throws CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[] {};
                    }
                }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            builder.sslSocketFactory(sslSocketFactory);
        }
    }

    public String generateId() {
        return UUID.randomUUID().toString();
    }

    /***
     * this is how we send the buffer
     * @param inputBuffer
     * @return
     */
    public List<String> getQueryPhrases(String inputBuffer) {

        Log.d(TAG, "inputBuffer=");

        List<String> list = new ArrayList<String>();
        if (inputBuffer == null || inputBuffer.trim().length() == 0) {
            return list;
        }

        String buffer1 = inputBuffer.trim().replaceAll("\\(.*\\)", "");
        String buffer = buffer1.replace('\n', ' ');
        String[] split = buffer.split(" ");

        //String [] newArray = new String[split.length];
        List<String> newArray = new ArrayList<>();
        boolean found = false;
        int count = 0;
        for (int j = split.length - 1; j >= 0; j--) {
            String word = split[j];
            if (word.startsWith("http://") || word.startsWith("https://") || word.startsWith(
                "rcp://")) {
                found = true;
                break;
            }
            newArray.add(word);
            count++;
        }
        StringBuffer newBuffer = new StringBuffer();

        if (found) {
            List<String> returnList = new ArrayList<>();
            ListIterator li = newArray.listIterator(newArray.size());

            // Iterate in reverse.
            while (li.hasPrevious()) {

                String previous = (String) li.previous();
                if (previous.trim().length() > 0) {
                    newBuffer.append(previous);
                    newBuffer.append(" ");
                    //eturnList.add(previous);
                }
            }
        }
        if (newBuffer.toString().trim().length() > 0) {
            buffer = newBuffer.toString().trim();
        }

        count = 0;
        int index[] = new int[4];
        char space = 'z';
        boolean lastcharSpace = false;
        for (int i = buffer.trim().length() - 1; i >= 0 && count < 4; i--) {
            Log.d(TAG, "buffer=" + buffer.charAt(i) + ", space=[" + String.valueOf(space) + "]");
            if (buffer.charAt(i) == ' ') {
                lastcharSpace = true;
                index[count] = i + 1;
                space = ' ';
                count++;
                while (buffer.charAt(--i) == ' ' && i >= 0) {
                    i--;
                }
            }
        }

        if (count == 0) {
            list.add(buffer);
            return list;
        }
        for (int j = count - 1; j >= 0; j--) {
            list.add(buffer.substring(index[j]));
        }

        if (count < 4) {
            list.add(0, buffer);
            return list;
        }

        for (String s : list) {
            Log.d(TAG, "list i=" + s);
        }

        return list;
    }

    public WlPendingIntent getWlEnablePendingIntent(Context appContext) {
        Intent mainIntent = getApplicationIntent(appContext);

        Intent backIntent = new Intent(Intent.ACTION_MAIN);
        backIntent.setComponent(mainIntent.getComponent());
        backIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        Intent wlIntent = new Intent(appContext, WlEnableActivity.class);
        wlIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        return WlPendingIntent.getActivities(8787878, new Intent[] { backIntent, wlIntent },
            PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private Intent getApplicationIntent(Context appContext) {
        PackageManager packageManager = appContext.getPackageManager();
        Intent launchIntent = packageManager.getLaunchIntentForPackage(appContext.getPackageName());
        return launchIntent;
        //ComponentName componentName = launchIntent.getComponent();

        // return IntentCompat.makeRestartActivityTask(componentName);
    }

    @NonNull
    public WlPendingIntent getPendingIntentWhatsNew(Context appContext) {
        Intent mainIntent = getApplicationIntent(appContext);
        Log.d(TAG, "mainIntent=" + mainIntent.getAction());
        Log.d(TAG, "mainIntent=" + mainIntent.getComponent().toString());

        Intent backIntent = new Intent(Intent.ACTION_MAIN);
        backIntent.setComponent(mainIntent.getComponent());
        backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent wlIntent = new Intent(appContext, WlEnableActivity.class);
        return WlPendingIntent.getActivities(8787878, new Intent[] { backIntent, wlIntent },
            PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @NonNull
    public PendingIntent getPendingIntentPush(Context appContext, String url) {
        Intent mainIntent = getApplicationIntent(appContext);
        mainIntent.putExtra("url", url);
        Log.d(TAG, "url=" + url);
        Log.d(TAG, "mainIntent=" + mainIntent.getAction());

        Log.d(TAG, "mainIntent=" + mainIntent.getComponent().toString());
        mainIntent.setAction("push");

        return PendingIntent.getActivity(appContext, 0, mainIntent, 0);
    }

    public PendingIntent getUrlPendingIntent(Context context, String url) {

        Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
        notificationIntent.setData(Uri.parse(url));
        PendingIntent pi = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        return pi;
    }

    public WlPendingIntent getAppPendingIntent(Context appContext) {
        Log.d(TAG, "utilities getAppPendingIntent");
        Intent mainIntent = getApplicationIntent(appContext);

        Intent backIntent = new Intent(Intent.ACTION_MAIN);
        backIntent.setComponent(mainIntent.getComponent());
        backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.d(TAG, "returning pending intent");

        return WlPendingIntent.getActivities(8787878, new Intent[] { backIntent },
            PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public String formatNumber(String unformatted) {
        return unformatted.substring(0, 3)
            + "-"
            + unformatted.substring(4, 7)
            + "-"
            + unformatted.substring(8, 11);
    }

    public List<String> getQueryPhraseList(String buffer, int max) {

        Log.d(TAG, "inputBuffer=");

        List<String> list = new ArrayList<String>();
        if (buffer == null || buffer.trim().length() == 0) {
            return list;
        }

        int count = 0;
        int index[] = new int[max];
        char space = 'z';
        boolean lastcharSpace = false;
        for (int i = buffer.trim().length() - 1; i >= 0 && count < max; i--) {
            Log.d(TAG, "buffer=" + buffer.charAt(i) + ", space=[" + String.valueOf(space) + "]");
            if (buffer.charAt(i) == ' ') {
                lastcharSpace = true;
                index[count] = i + 1;
                space = ' ';
                count++;
                while (buffer.charAt(--i) == ' ' && i >= 0) {
                    i--;
                }
            }
        }

        if (count == 0) {
            list.add(buffer);
            return list;
        }
        for (int j = count - 1; j >= 0; j--) {
            list.add(buffer.substring(index[j]));
        }

        if (count < max) {
            list.add(0, buffer);
            return list;
        }

        for (String s : list) {
            Log.d(TAG, "list i=" + s);
        }

        return list;
    }
}
