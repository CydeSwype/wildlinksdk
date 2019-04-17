package me.wildlinksdk.android;

import android.app.Application;

import me.wildlinksdk.android.api.ApiError;
import me.wildlinksdk.android.models.Validation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import edu.emory.mathcs.backport.java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
@Config(maxSdk = 27)

public class SenderTests2 {


    public Validation object;
    public CountDownLatch latch;
    @Test
    public void testCreateSender() throws Exception {
        Application application = RuntimeEnvironment.application;


        WildlinkSdk.init(new TestProviderImpl(application));

        WildlinkSdk sdk = WildlinkSdk.getIntance();

        String validationCode = "841574";
        latch = new CountDownLatch(1);
        sdk.senderValidate("17606445617",validationCode,new ValidationListener() {

            @Override
            public void onFailure(ApiError error) {
                latch.countDown();
            }

            @Override
            public void onSuccess(Validation validation) {
                object = validation;
               latch.countDown();
            }
        });
        latch.await();

        assertNotNull(object);
        assertNotNull(object.getSenderToken());



    }


}