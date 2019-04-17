package me.wildlinksdk.android;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;

import edu.emory.mathcs.backport.java.util.concurrent.CountDownLatch;
import me.wildlinksdk.android.api.ApiError;
import me.wildlinksdk.android.api.CloudServiceApi;
import me.wildlinksdk.android.models.Message;
import me.wildlinksdk.android.models.Stats;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class ClickStats {


    private static final String TAG = ClickStats.class.getSimpleName();
    public Message message;
    public CountDownLatch latch;
    private List<Stats> statsList;
    @Test
    public void testClickStats() throws Exception {
        Application application = RuntimeEnvironment.application;

        FirebaseApp.initializeApp(application);
        WildlinkSdk.init(new TestProviderImpl(application));

        WildlinkSdk sdk = WildlinkSdk.getIntance();


        latch = new CountDownLatch(1);


        DateTime nowLocal = DateTime.now();

        DateTime sevenDaysAgo = nowLocal.minusDays(7);

        Log.d(TAG, "sevenDaysAgo=" + sevenDaysAgo.toString());
        Log.d(TAG, "nowLocal=" + nowLocal.toString());

        Log.d(TAG, "sevenDaysAgo.UTC)" + sevenDaysAgo.toDateTime(DateTimeZone.UTC).toString());
        Log.d(TAG, "nowUtc=" +  DateTime.now().toDateTime(DateTimeZone.UTC).toString());


        sdk.clickStats(CloudServiceApi.byEnum.hour,sevenDaysAgo.toDateTime(DateTimeZone.UTC).getMillis(),
                DateTime.now().toDateTime(DateTimeZone.UTC).getMillis(),new ClickstatsListener() {
                    @Override
                    public void onFailure(ApiError error) {

                        latch.countDown();
                    }

                    @Override
                    public void onSuccess(List<Stats> theStatsList) {


                        statsList = theStatsList;
                        latch.countDown();
                    }
                });
        latch.await();

        Log.d(TAG, "statsList" + statsList.toString());

        //assertNotNull("statsList should not be null", statsList);



    }


}