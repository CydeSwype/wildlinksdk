package me.wildlinksdk.android;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.RenamingDelegatingContext;
import android.util.Pair;
import io.requery.android.database.sqlite.SQLiteDatabase;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import junit.framework.Assert;
import me.wildlinksdk.android.api.ApiError;
import me.wildlinksdk.android.api.ApiModule;
import me.wildlinksdk.android.db.MerchantsDataSource;
import me.wildlinksdk.android.models.MerchantItemDomain;
import me.wildlinksdk.android.models.Vanity;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.fail;
@RunWith(AndroidJUnit4.class)
public class VanityTests {

    private static final String TAG = SqliteMerchantsTests.class.getSimpleName();

    private static String PREFIX = "test";

    private RenamingDelegatingContext mRenamingDeligateContext;

    private Vanity vanity;
    private CountDownLatch latch;


    @Test
    public void testEncodedUrlAsync() {
        try {

            Context appContext = InstrumentationRegistry.getContext();

            WildlinkSdk.init(new DatabaseCache10SecondTestsProviderImpl(InstrumentationRegistry.getTargetContext()));

            SQLiteDatabase database = ApiModule.INSTANCE.getDatabase();

            MerchantsDataSource ds = new MerchantsDataSource(database);

            List<Pair<String, String>> rows =
                Common.readMerchants(appContext, "merchant-domains.txt");

            for (Pair<String, String> pair : rows) {
                ds.insert(pair.first, pair.second);
            }

            int count = ds.count();
            assertTrue(count > 0);

            String testUrl = "https://www.macys.com/shop/jewelry-watches/le-vian/Special_offers/Limited-Time%20Special?id=65939&cm_re=2018.08.09-_-HOMEPAGE_INCLUDE_1_row_04-_-CATEGORY%20--%205125%20--%20:60%20percent%20Off,%20Le%20Vian%20Chocolate%20Diamonds";
            MerchantItemDomain itemDomain = ds.search(testUrl);
            Assert.assertNotNull("itemDomain should not be null");
            Assert.assertEquals("macys.com", itemDomain.domain);
            Assert.assertEquals("12322", itemDomain.merchantItem.getId());

            vanity = null;
            latch = new CountDownLatch(1);
            ApiModule.INSTANCE.getCloudServiceApi()
                .createVanityUrl("test",
                    testUrl,
                    new VanityListener() {
                        @Override
                        public void onFailure(final ApiError error) {
                            latch.countDown();
                        }

                        @Override
                        public void onSuccess(final Vanity theVanity) {
                            vanity = theVanity;
                            latch.countDown();

                        }
                    });


            latch.await();
            Assert.assertNotNull("vanity must not be null", vanity);

            Assert.assertNotNull("vanity short url must not be null", vanity.getShortUrl());
            Assert.assertTrue("vanity short url length must be > 0 ", vanity.getShortUrl().length() > 0);
            Assert.assertTrue("vanity short url must contain http ", vanity.getShortUrl().indexOf("http") == 0);

            Assert.assertEquals("macys.com", vanity.getDomain());
            Assert.assertEquals(vanity.getOriginalUrl(),testUrl);



        } catch (Exception e) {

            org.junit.Assert.fail("testSqlCount" + ",failed e=" + e.getMessage());
        }
    }



    @Test
    public void testEncodedUrl() {
        try {

            Context appContext = InstrumentationRegistry.getContext();

            WildlinkSdk.init(new DatabaseCache10SecondTestsProviderImpl(InstrumentationRegistry.getTargetContext()));

            SQLiteDatabase database = ApiModule.INSTANCE.getDatabase();

            MerchantsDataSource ds = new MerchantsDataSource(database);

            List<Pair<String, String>> rows =
                Common.readMerchants(appContext, "merchant-domains.txt");

            for (Pair<String, String> pair : rows) {
                ds.insert(pair.first, pair.second);
            }

            int count = ds.count();
            assertTrue(count > 0);

            String testUrl = "https://www.macys.com/shop/jewelry-watches/le-vian/Special_offers/Limited-Time%20Special?id=65939&cm_re=2018.08.09-_-HOMEPAGE_INCLUDE_1_row_04-_-CATEGORY%20--%205125%20--%20:60%20percent%20Off,%20Le%20Vian%20Chocolate%20Diamonds";

            MerchantItemDomain itemDomain = ds.search(testUrl);
            Assert.assertNotNull("itemDomain should not be null");
            Assert.assertEquals("macys.com", itemDomain.domain);
            Assert.assertEquals("12322", itemDomain.merchantItem.getId());

            Vanity vanity = ApiModule.INSTANCE.getCloudServiceApi()
                .createVanityUrl("test", testUrl);


            Assert.assertEquals("macys.com", vanity.getDomain());
            Assert.assertNotNull("vanity short url must not be null", vanity.getShortUrl());
            Assert.assertTrue("vanity short url length must be > 0 ", vanity.getShortUrl().length() > 0);
            Assert.assertTrue("vanity short url must contain http ", vanity.getShortUrl().indexOf("http") == 0);

            Assert.assertEquals(vanity.getOriginalUrl(),testUrl);


        } catch (Exception e) {

            org.junit.Assert.fail("testSqlCount" + ",failed e=" + e.getMessage());
        }
    }


    @Test
    public void testJacketsNordstromComCount() {
        try {

            Context appContext = InstrumentationRegistry.getContext();

            WildlinkSdk.init(new DatabaseCache10SecondTestsProviderImpl(InstrumentationRegistry.getTargetContext()));

            SQLiteDatabase database = ApiModule.INSTANCE.getDatabase();

            MerchantsDataSource ds = new MerchantsDataSource(database);

            List<Pair<String, String>> rows =
                Common.readMerchants(appContext, "merchant-domains.txt");

            for (Pair<String, String> pair : rows) {
                ds.insert(pair.first, pair.second);
            }

            int count = ds.count();
            assertTrue(count > 0);

            MerchantItemDomain itemDomain = ds.search("http://jackets.nordstrom.com/sdkfsdfs/1111");
            Assert.assertEquals("nordstrom.com", itemDomain.domain);
            Assert.assertEquals("sdsjdjd", itemDomain.merchantItem.getId());

            Vanity vanity = ApiModule.INSTANCE.getCloudServiceApi()
                .createVanityUrl("test", "http://jackets.nordstrom.com/sdkfsdfs/1111");

            Assert.assertEquals(vanity.getDomain(), "nordstrom.com");


        } catch (Exception e) {

            org.junit.Assert.fail("testSqlCount" + ",failed e=" + e.getMessage());
        }
    }

    @Test
    public void testnotFoundCount() {
        try {

            Context appContext = InstrumentationRegistry.getContext();

            WildlinkSdk.init(new DatabaseCache10SecondTestsProviderImpl(InstrumentationRegistry.getTargetContext()));

            SQLiteDatabase database = ApiModule.INSTANCE.getDatabase();

            MerchantsDataSource ds = new MerchantsDataSource(database);

            List<Pair<String, String>> rows =
                Common.readMerchants(appContext, "merchant-domains.txt");

            for (Pair<String, String> pair : rows) {
                ds.insert(pair.first, pair.second);
            }

            int count = ds.count();
            assertTrue(count > 0);

            MerchantItemDomain itemDomain = ds.search("http://jackets.shopnordstrom.com/sdkfsdfs/1111");
            Assert.assertNull(itemDomain);

            Vanity vanity = ApiModule.INSTANCE.getCloudServiceApi()
                .createVanityUrl("test", "http://jackets.shopnordstrom.com/sdkfsdfs/1111");

            Assert.assertNull(vanity.getDomain());
        } catch (Exception e) {

            org.junit.Assert.fail("testSqlCount" + ",failed e=" + e.getMessage());
        }
    }

    @Test
    public void testWwwNordstromCom() {
        try {

            Context appContext = InstrumentationRegistry.getContext();

            WildlinkSdk.init(new DatabaseCache10SecondTestsProviderImpl(InstrumentationRegistry.getTargetContext()));

            SQLiteDatabase database = ApiModule.INSTANCE.getDatabase();

            MerchantsDataSource ds = new MerchantsDataSource(database);

            List<Pair<String, String>> rows =
                Common.readMerchants(appContext, "merchant-domains.txt");

            for (Pair<String, String> pair : rows) {
                ds.insert(pair.first, pair.second);
            }

            int count = ds.count();
            assertTrue(count > 0);

            MerchantItemDomain itemDomain = ds.search("http://nordstrom.com/sdkfsdfs/1111");
            Assert.assertEquals("nordstrom.com", itemDomain.domain);
            Assert.assertEquals("sdsjdjd", itemDomain.merchantItem.getId());

            Vanity vanity = ApiModule.INSTANCE.getCloudServiceApi()
                .createVanityUrl("test", "http://www.nordstrom.com/sdkfsdfs/1111");

            Assert.assertEquals(vanity.getDomain(), "nordstrom.com");
        } catch (Exception e) {

            org.junit.Assert.fail("testSqlCount" + ",failed e=" + e.getMessage());
        }
    }

    @Test
    public void testWWWNordstromCom() {
        try {

            Context appContext = InstrumentationRegistry.getContext();

            WildlinkSdk.init(new DatabaseCache10SecondTestsProviderImpl(InstrumentationRegistry.getTargetContext()));

            SQLiteDatabase database = ApiModule.INSTANCE.getDatabase();

            MerchantsDataSource ds = new MerchantsDataSource(database);

            List<Pair<String, String>> rows =
                Common.readMerchants(appContext, "merchant-domains.txt");

            for (Pair<String, String> pair : rows) {
                ds.insert(pair.first, pair.second);
            }

            int count = ds.count();
            assertTrue(count > 0);

            MerchantItemDomain itemDomain = ds.search("http://www.nordstrom.com/sdsjdjd/1111");
            Assert.assertEquals("nordstrom.com", itemDomain.domain);
            Assert.assertEquals("sdsjdjd", itemDomain.merchantItem.getId());

            Vanity vanity = ApiModule.INSTANCE.getCloudServiceApi()
                .createVanityUrl("test", "http://www.nordstrom.com/sdsjdjd/sdkfsdfs");

            Assert.assertEquals(vanity.getDomain(), "nordstrom.com");
        } catch (Exception e) {

            org.junit.Assert.fail("testSqlCount" + ",failed e=" + e.getMessage());
        }
    }



    @Test
    public void testMShopNordstromCom() {
        try {

            Context appContext = InstrumentationRegistry.getContext();

            WildlinkSdk.init(new DatabaseCache10SecondTestsProviderImpl(InstrumentationRegistry.getTargetContext()));

            SQLiteDatabase database = ApiModule.INSTANCE.getDatabase();

            MerchantsDataSource ds = new MerchantsDataSource(database);

            List<Pair<String, String>> rows =
                Common.readMerchants(appContext, "merchant-domains.txt");

            for (Pair<String, String> pair : rows) {
                ds.insert(pair.first, pair.second);
            }

            int count = ds.count();
            assertTrue(count > 0);

            MerchantItemDomain itemDomain = ds.search("http://m.shop.nordstrom.com/sdkfsdfs/1111");
            Assert.assertEquals("m.shop.nordstrom.com", itemDomain.domain);
            Assert.assertEquals("12345", itemDomain.merchantItem.getId());

            Vanity vanity = ApiModule.INSTANCE.getCloudServiceApi()
                .createVanityUrl("test", "http://m.shop.nordstrom.com/sdkfsdfs/1111");

            Assert.assertEquals(vanity.getDomain(), "m.shop.nordstrom.com");

        } catch (Exception e) {

            org.junit.Assert.fail("testSqlCount" + ",failed e=" + e.getMessage());
        }
    }


    @Test
    public void testMShopNordstromComNoLongDomains() {
        try {

            Context appContext = InstrumentationRegistry.getContext();

            WildlinkSdk.init(new DatabaseCache10SecondTestsProviderImpl(InstrumentationRegistry.getTargetContext()));

            SQLiteDatabase database = ApiModule.INSTANCE.getDatabase();

            MerchantsDataSource ds = new MerchantsDataSource(database);

            List<Pair<String, String>> rows =
                Common.readMerchants(appContext, "merchant-domains-no-long-domains.txt");

            for (Pair<String, String> pair : rows) {
                ds.insert(pair.first, pair.second);
            }

            int count = ds.count();
            assertTrue(count > 0);

            MerchantItemDomain itemDomain = ds.search("http://m.shop.nordstrom.com/sdkfsdfs/1111");
            Assert.assertEquals("nordstrom.com", itemDomain.domain);
            Assert.assertEquals("sdsjdjd", itemDomain.merchantItem.getId());

            Vanity vanity = ApiModule.INSTANCE.getCloudServiceApi()
                .createVanityUrl("test", "http://m.shop.nordstrom.com/sdkfsdfs/1111");

            Assert.assertEquals(vanity.getDomain(), "nordstrom.com");

        } catch (Exception e) {

            org.junit.Assert.fail("testSqlCount" + ",failed e=" + e.getMessage());
        }
    }


}