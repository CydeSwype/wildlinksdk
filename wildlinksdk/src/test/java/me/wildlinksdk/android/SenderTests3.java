package me.wildlinksdk.android;

import android.app.Application;

import me.wildlinksdk.android.api.ApiError;
import me.wildlinksdk.android.api.models.PaypalCredentials;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import edu.emory.mathcs.backport.java.util.concurrent.CountDownLatch;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class SenderTests3 {


    public boolean completed;
    private PaypalCredentials object;
    public CountDownLatch latch;

    @Test
    public void testPaymentCredentials() throws Exception {
        Application application = RuntimeEnvironment.application;


        WildlinkSdk.init(new TestProviderImpl(application));

        WildlinkSdk sdk = WildlinkSdk.getIntance();


        latch = new CountDownLatch(1);
        sdk.setPaypalCredentials(WildlinkSdk.RecipientType.EMAIL,"ronjawanda@gmail.com",new SimpleListener() {

            @Override
            public void onFailure(ApiError error) {
                completed = false;
                latch.countDown();
            }

            @Override
            public void onSuccess( ) {
               completed = true;
               latch.countDown();
            }
        });
        latch.await();

        assertTrue(completed);

        latch = new CountDownLatch(1);
        sdk.getPaypalPayment(new PaypalPaymentListener() {
            @Override
            public void onSuccess(PaypalCredentials paypalCredentials) {
                object = paypalCredentials;
               latch.countDown();
            }

            @Override
            public void onFailure(ApiError error) {

               latch.countDown();
            }
        });


        latch.await();

        assertNotNull(object);
        assertEquals("EMAIL",object.getRecipient_type());


    }


}