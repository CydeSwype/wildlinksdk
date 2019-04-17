package me.wildlinksdk.android;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.RenamingDelegatingContext;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import junit.framework.Assert;
import me.wildlinksdk.android.api.ApiError;
import me.wildlinksdk.android.models.Merchant;
import me.wildlinksdk.android.models.Vanity;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class MerchantsTests {

    private static final String TAG = SqliteMerchantsTests.class.getSimpleName();

    private static String PREFIX = "test";

    private RenamingDelegatingContext mRenamingDeligateContext;

    private Vanity vanity;
    private CountDownLatch latch;
    private List<Merchant> list;


    @Test
    public void testGetMerchants() {
        try {
            Context appContext = InstrumentationRegistry.getContext();

            WildlinkSdk.init(new DatabaseCache10SecondTestsProviderImpl(InstrumentationRegistry.getTargetContext()));

            WildlinkSdk sdk = WildlinkSdk.getIntance();


            list = null;
            latch = new CountDownLatch(1);
            List<Long> ids = new ArrayList<>();
            ids.add(new Long(5481985));
            ids.add(new Long(5477615));

            sdk.getMerchants(ids, new MerchantListener() {
                @Override
                public void onSuccess(List<Merchant> merchants) {


                    list = merchants;
                    latch.countDown();
                }

                @Override
                public void onFailure(ApiError error) {
                    latch.countDown();
                }
            });
            latch.await();

            assertNotNull("list should not be null", list);
            assertNotNull(list.size() > 0);
            assertNotNull("shortUrl" + list.get(0).getShortUrl());
            assertNotNull("shortUrl" + list.get(1).getShortUrl());

            Assert.assertEquals(list.get(0).getId(), Long.valueOf(5508814));



        } catch(Exception e){

            org.junit.Assert.fail("testSqlCount" + ",failed e=" + e.getMessage());

        }


    }



}