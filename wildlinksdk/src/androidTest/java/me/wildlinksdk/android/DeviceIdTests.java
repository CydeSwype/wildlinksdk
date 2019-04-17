package me.wildlinksdk.android;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.RenamingDelegatingContext;
import android.util.Pair;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import junit.framework.Assert;
import me.wildlinksdk.android.api.ApiError;
import me.wildlinksdk.android.api.ApiModule;
import me.wildlinksdk.android.api.models.XidRequest;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DeviceIdTests {

    private static final String TAG = SqliteMerchantsTests.class.getSimpleName();

    private static String PREFIX = "test";

    private RenamingDelegatingContext mRenamingDeligateContext;

    private CountDownLatch latch ;
    private boolean hasError = false;
    private ApiError apiError;
    @Test
    public void testXidUpdateInvalidInput() {
        try {

            Context appContext = InstrumentationRegistry.getContext();

            WildlinkSdk.init(new DatabaseCache10SecondTestsProviderImpl(InstrumentationRegistry.getTargetContext()));


            List<Pair<String, String>> rows =
                Common.readMerchants(appContext, "merchant-domains.txt");

            XidRequest request = new XidRequest("12345", "1123121");

            latch = new CountDownLatch(1);
            ApiModule.INSTANCE.getCloudServiceApi()
                .updateXid(request, new SimpleListener() {
                    @Override
                    public void onSuccess() {
                        hasError = false;
                        latch.countDown();
                    }

                    @Override
                    public void onFailure(final ApiError error) {
                        hasError = true;
                        apiError = error;
                        latch.countDown();
                    }
                });

            latch.await();
            Assert.assertNotNull("this should cause an api error", apiError);

            Assert.assertTrue("should say invalid source", apiError.getMessage().indexOf("UpdateDeviceXID Called with Invalid Source") == 0);


        } catch (Exception e) {


            org.junit.Assert.fail("DeviceIdTests testXidUpdateInvalidInput failed but should have captured an API Error" + ",failed e=" + e.getMessage());
        }
    }



    // you must force a failure for this to happen
    @Test
    public void testFailureDownlloadDb() {
        try {

            Context appContext = InstrumentationRegistry.getContext();

            WildlinkSdk.init(new DatabaseCache10SecondTestsProviderImpl(InstrumentationRegistry.getTargetContext()));


            latch = new CountDownLatch(1);
            ApiModule.INSTANCE.getCloudServiceApi()
                .downloadMerchantsDatabaseSqlite(false,new SimpleListener() {
                    @Override
                    public void onSuccess() {
                        hasError = false;
                        latch.countDown();
                    }

                    @Override
                    public void onFailure(final ApiError error) {
                        hasError = true;
                        apiError = error;
                        latch.countDown();
                    }
                });

            latch.await();
            Assert.assertTrue("this should cause an api error", hasError);

            Assert.assertTrue("should say invalid source", apiError.getMessage().indexOf("UpdateDeviceXID Called with Invalid Source") == 0);


        } catch (Exception e) {


            org.junit.Assert.fail("DeviceIdTests testXidUpdateInvalidInput failed but should have captured an API Error" + ",failed e=" + e.getMessage());
        }
    }

}