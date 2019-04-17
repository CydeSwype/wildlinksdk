package me.wildlinksdk.android;

import android.app.Application;

import me.wildlinksdk.android.api.ApiError;
import me.wildlinksdk.android.api.models.SenderListener;
import me.wildlinksdk.android.models.Message;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import edu.emory.mathcs.backport.java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
//@Config(constants = BuildConfig.class)

public class SenderTests1 {


    public Message message;
    public CountDownLatch latch;
    @Test
    public void testCreateSender() throws Exception {
        Application application = RuntimeEnvironment.application;


        WildlinkSdk.init(new TestProviderImpl(application));

        WildlinkSdk sdk = WildlinkSdk.getIntance();


        latch = new CountDownLatch(1);
        sdk.createSender("17606445617", new SenderListener() {
            @Override
            public void onSuccess(Message theMessage) {
                message = theMessage;

                latch.countDown();
            }

            @Override
            public void onFailure(ApiError error) {
                latch.countDown();
            }
        });
        latch.await();

        assertNotNull(message);
        assertNotNull(message.getMessage());


    }


}