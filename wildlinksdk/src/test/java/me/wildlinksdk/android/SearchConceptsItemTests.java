package me.wildlinksdk.android;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import com.google.firebase.FirebaseApp;
import com.google.gson.reflect.TypeToken;
import edu.emory.mathcs.backport.java.util.concurrent.CountDownLatch;
import java.util.ArrayList;
import java.util.List;
import me.wildlinksdk.android.api.Cache;
import me.wildlinksdk.android.models.Message;
import me.wildlinksdk.android.models.Stats;
import me.wildlinksdk.android.models.WildlinkBufferResult;
import me.wildlinksdk.android.models.WildlinkResult;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(RobolectricTestRunner.class)
//@Config(manifest=Config.NONE)
//@Config(constants = BuildConfig.class)
@Config( constants = BuildConfig.class, packageName = "me.wildlink.sdk")

public class SearchConceptsItemTests {

    LevenshteinDistance levenshteinDistance = new LevenshteinDistance();

    private static final String TAG = SearchConceptsItemTests.class.getSimpleName();
    public Message message;
    public CountDownLatch latch;
    private List<Stats> statsList;

    @Rule public TestName name = new TestName();

    @Before
    public void setUp() throws Exception { ShadowLog.stream = System.out;  }

    private class ReiRealm implements WlRealm   {
        private Context context;
        public ReiRealm(Context context) {
            this.context = context;
        }

        @Override
        public List<WildlinkBufferResult> queryConceptsItem(final String query, final String kind,
            final Cache.SearchCase searchCase) {
            List<WildlinkBufferResult> list = new ArrayList<>();
            try {
                CommonRobo commonRobo = new CommonRobo();
                String json = commonRobo.createStringFromResources(RuntimeEnvironment.application.getApplicationContext(),
                    "testRei1.json");
                list = commonRobo.getGson().fromJson(json, new TypeToken<List<WildlinkResult>>() {
                }.getType());
            }catch(Exception e) {
            }
            return list;
        }

        @Override
        public MerchantsItem queryMerchantsItem(final String query) {
            return null;
        }
        @Override
        public boolean isConceptsEmpty() {
            return false;
        }
        @Override
        public boolean isFeaturedStoresEmpty() {
            return false;
        }
        @Override
        public boolean isMerchantsEmpty() {
            return false;
        }
    }

    private class BestBuyRealm implements WlRealm   {
        private Context context;
        public BestBuyRealm(Context context) {
            this.context = context;
        }

        @Override
        public List<WildlinkBufferResult> queryConceptsItem(final String query, final String kind,
            final Cache.SearchCase searchCase) {
            List<WildlinkBufferResult> list = new ArrayList<>();
            try {
                CommonRobo commonRobo = new CommonRobo();
                if( query.equals("Best Buy")) {
                    String json = commonRobo.createStringFromResources(RuntimeEnvironment.application.getApplicationContext(),
                        "testBestBuy1.json");
                    list = commonRobo.getGson().fromJson(json, new TypeToken<List<WildlinkResult>>() {
                    }.getType());
                }else if( query.equals("Buy")) {
                    String json = commonRobo.createStringFromResources(RuntimeEnvironment.application.getApplicationContext(),
                        "testBestBuy2.json");
                    list = commonRobo.getGson().fromJson(json, new TypeToken<List<WildlinkResult>>() {
                    }.getType());
                }
            }catch(Exception e) {
            }
            return list;
        }

        @Override
        public MerchantsItem queryMerchantsItem(final String query) {
            return null;
        }
        @Override
        public boolean isConceptsEmpty() {
            return false;
        }
        @Override
        public boolean isFeaturedStoresEmpty() {
            return false;
        }
        @Override
        public boolean isMerchantsEmpty() {
            return false;
        }
    }


    private class CosmopolitanVegasRealm implements WlRealm   {
        private Context context;
        public CosmopolitanVegasRealm(Context context) {
            this.context = context;
        }

        @Override
        public List<WildlinkBufferResult> queryConceptsItem(final String query, final String kind,
            final Cache.SearchCase searchCase) {
            List<WildlinkBufferResult> list = new ArrayList<>();
            try {
                CommonRobo commonRobo = new CommonRobo();
                if( query.equals("Cosmopolitan Vegas")) {
                    String json = commonRobo.createStringFromResources(RuntimeEnvironment.application.getApplicationContext(),
                        "testCosmopolitanVegas1.json");
                    list = commonRobo.getGson().fromJson(json, new TypeToken<List<WildlinkResult>>() {
                    }.getType());
                }else if( query.equals("Vegas")) {
                    String json = commonRobo.createStringFromResources(RuntimeEnvironment.application.getApplicationContext(),
                        "testCosmopolitanVegas2.json");
                    list = commonRobo.getGson().fromJson(json, new TypeToken<List<WildlinkResult>>() {
                    }.getType());
                }
            }catch(Exception e) {
            }
            return list;
        }

        @Override
        public MerchantsItem queryMerchantsItem(final String query) {
            return null;
        }
        @Override
        public boolean isConceptsEmpty() {
            return false;
        }
        @Override
        public boolean isFeaturedStoresEmpty() {
            return false;
        }
        @Override
        public boolean isMerchantsEmpty() {
            return false;
        }
    }


    private class ExpediaRealm implements WlRealm   {
        private Context context;
        public ExpediaRealm(Context context) {
            this.context = context;
        }

        @Override
        public List<WildlinkBufferResult> queryConceptsItem(final String query, final String kind,
            final Cache.SearchCase searchCase) {
            List<WildlinkBufferResult> list = new ArrayList<>();
            try {
                CommonRobo commonRobo = new CommonRobo();
                String json = commonRobo.createStringFromResources(RuntimeEnvironment.application.getApplicationContext(),
                    "testExpedia.json");
                list = commonRobo.getGson().fromJson(json, new TypeToken<List<WildlinkResult>>() {
                }.getType());
            }catch(Exception e) {
            }
            return list;
        }

        @Override
        public MerchantsItem queryMerchantsItem(final String query) {
            return null;
        }
        @Override
        public boolean isConceptsEmpty() {
            return false;
        }
        @Override
        public boolean isFeaturedStoresEmpty() {
            return false;
        }
        @Override
        public boolean isMerchantsEmpty() {
            return false;
        }
    }

    private class MacysRealm implements WlRealm   {
        private Context context;
        public MacysRealm(Context context) {
            this.context = context;
        }

        @Override
        public List<WildlinkBufferResult> queryConceptsItem(final String query, final String kind,
            final Cache.SearchCase searchCase) {
            List<WildlinkBufferResult> list = new ArrayList<>();
            try {
                CommonRobo commonRobo = new CommonRobo();
                String json = commonRobo.createStringFromResources(RuntimeEnvironment.application.getApplicationContext(),
                    "testMacys.json");
                list = commonRobo.getGson().fromJson(json, new TypeToken<List<WildlinkResult>>() {
                }.getType());
            }catch(Exception e) {
            }
            return list;
        }

        @Override
        public MerchantsItem queryMerchantsItem(final String query) {
            return null;
        }
        @Override
        public boolean isConceptsEmpty() {
            return false;
        }
        @Override
        public boolean isFeaturedStoresEmpty() {
            return false;
        }
        @Override
        public boolean isMerchantsEmpty() {
            return false;
        }
    }

   // @Test
    public void testMacys() throws Exception {

        try {

            Application application = RuntimeEnvironment.application;

            FirebaseApp.initializeApp(application);
            WildlinkSdk.init(new TestProviderImpl(application));

            WildlinkSdk sdk = WildlinkSdk.getIntance();

            sdk.overrideRealm(new MacysRealm(application.getApplicationContext()));

            List<WildlinkBufferResult> finalResults =
                sdk.searchConceptsItem("Macy's", Cache.SearchCase.INSENSITIVE, 1000);

            int count = 0;
            assertEquals("Macy's should be [0.0]", 0.0, finalResults.get(count).getRank());
            assertEquals("Macy's", "Macy's", finalResults.get(count).getPhrase());

            count++;
            assertTrue("Macy\'s Canada should be [1.16]", finalResults.get(count).getRank() > 1.15d
                && finalResults.get(count).getRank() < 1.17d);
            assertEquals("should be Macy\'s Canada", "Macy\'s Canada", finalResults.get(count).getPhrase());

            count++;
            assertTrue("Macy\'s Wine Cellar should be [2.0]", finalResults.get(count).getRank() > 1.99d
                && finalResults.get(count).getRank() < 2.01d);
            assertEquals("should be Macy\'s Wine Cellar", "Macy\'s Wine Cellar", finalResults.get(count).getPhrase());


        }catch(Exception e) {
            Log.d(TAG, "failed e=" + e.getMessage());
            fail(name.getMethodName() + ", e=" + e.getMessage());
        }

    }

    @Test
    public void testRei() throws Exception {

        try {

            Application application = RuntimeEnvironment.application;

            FirebaseApp.initializeApp(application);
            WildlinkSdk.init(new TestProviderImpl(application));

            WildlinkSdk sdk = WildlinkSdk.getIntance();

            sdk.overrideRealm(new ReiRealm(application.getApplicationContext()));

            List<WildlinkBufferResult> finalResults =
                sdk.searchConceptsItem("REI", Cache.SearchCase.INSENSITIVE, 1000);

            int count = 0;
            assertEquals("REI should be [0.0]", 0.0, finalResults.get(count).getRank());
            assertEquals("REI", "REI", finalResults.get(count).getPhrase());

            count++;
            assertTrue("Reimage should be [1.33]", finalResults.get(count).getRank() > 1.32
                && finalResults.get(count).getRank() < 1.34d);
            assertEquals("should be Reimage", "Reimage", finalResults.get(count).getPhrase());

            count++;
            assertTrue("REI.com should be [1.33]", finalResults.get(count).getRank() > 1.32
                && finalResults.get(count).getRank() < 1.34d);
            assertEquals("should be REI.com", "REI.com", finalResults.get(count).getPhrase());

            count++;
            assertTrue("REI Co-op should be [2.0]", finalResults.get(count).getRank() > 1.99
                && finalResults.get(count).getRank() < 2.01);
            assertEquals("should be REI Co-op", "REI Co-op", finalResults.get(count).getPhrase());

            count++;
            assertTrue("Reiss (AU) should be [2.33]", finalResults.get(count).getRank() > 2.32
                && finalResults.get(count).getRank() < 2.34);
            assertEquals("should be Reiss (AU)", "Reiss (AU)", finalResults.get(count).getPhrase());

            count++;
            assertTrue("Reisefux AT should be [2.66]", finalResults.get(count).getRank() > 2.65
                && finalResults.get(count).getRank() < 2.67);
            assertEquals("should be Reisefux AT", "Reisefux AT", finalResults.get(count).getPhrase());

            count++;
            assertTrue("Reitmans.com should be [3.0]", finalResults.get(count).getRank() > 2.99
                && finalResults.get(count).getRank() < 3.01);
            assertEquals("should be Reitmans.com", "Reitmans.com", finalResults.get(count).getPhrase());


            count++;
            assertTrue("Reid Cycles AU should be [3.66]", finalResults.get(count).getRank() > 3.65
                && finalResults.get(count).getRank() < 3.67);
            assertEquals("should be Reid Cycles AU", "Reid Cycles AU", finalResults.get(count).getPhrase());


        }catch(Exception e) {
            Log.d(TAG, "failed e=" + e.getMessage());
            fail(name.getMethodName() + ", e=" + e.getMessage());
        }

    }


    @Test
    public void testExpedia() throws Exception {

        try {

            Application application = RuntimeEnvironment.application;

            FirebaseApp.initializeApp(application);
            WildlinkSdk.init(new TestProviderImpl(application));

            WildlinkSdk sdk = WildlinkSdk.getIntance();

            sdk.overrideRealm(new ExpediaRealm(application.getApplicationContext()));

            List<WildlinkBufferResult> finalResults =
                sdk.searchConceptsItem("Expedia", Cache.SearchCase.INSENSITIVE, 1000);

            int count = 0;
            assertEquals("Expedia should be [0.0]", 0.0, finalResults.get(count).getRank());
            assertEquals("Expedia", "Expedia", finalResults.get(count).getPhrase());

            count++;
            assertEquals("Expedia should be [0.0]", 0.0, finalResults.get(count).getRank());
            assertEquals("Expedia", "Expedia", finalResults.get(count).getPhrase());



            count++;
            assertTrue("Expedia AT should be [0.438]", finalResults.get(count).getRank() > 0.427
                && finalResults.get(count).getRank() < 0.429d);
            assertEquals("should be Expedia AT", "Expedia AT", finalResults.get(count).getPhrase());

            count++;
            assertTrue("Expedia ES should be [0.438]", finalResults.get(count).getRank() > 0.427
                && finalResults.get(count).getRank() < 0.429d);
            assertEquals("should be Expedia ES", "Expedia ES", finalResults.get(count).getPhrase());

            count++;
            assertTrue("Expedia IT should be [0.438]", finalResults.get(count).getRank() > 0.427
                && finalResults.get(count).getRank() < 0.429d);
            assertEquals("should be Expedia IT", "Expedia IT", finalResults.get(count).getPhrase());

            count++;
            assertTrue("Expedia NO should be [0.438]", finalResults.get(count).getRank() > 0.427
                && finalResults.get(count).getRank() < 0.429d);
            assertEquals("should be Expedia NO", "Expedia NO", finalResults.get(count).getPhrase());

            count++;
            assertTrue("Expedia.br should be [0.438]", finalResults.get(count).getRank() > 0.427
                && finalResults.get(count).getRank() < 0.429d);
            assertEquals("should be Expedia.br", "Expedia.br", finalResults.get(count).getPhrase());


            count++;
            assertTrue("Expedia, Inc should be [0.]", finalResults.get(count).getRank() > 0.713
                && finalResults.get(count).getRank() < 0.715d);
            assertEquals("should be Expedia, Inc", "Expedia, Inc", finalResults.get(count).getPhrase());


        }catch(Exception e) {
            Log.d(TAG, "failed e=" + e.getMessage());
            fail(name.getMethodName() + ", e=" + e.getMessage());
        }

    }


    @Test
    public void testBestBuy() throws Exception {

        try {

            Application application = RuntimeEnvironment.application;

            FirebaseApp.initializeApp(application);
            WildlinkSdk.init(new TestProviderImpl(application));

            WildlinkSdk sdk = WildlinkSdk.getIntance();

            sdk.overrideRealm(new BestBuyRealm(application.getApplicationContext()));

            List<WildlinkBufferResult> finalResults =
                sdk.searchConceptsItem("Best Buy", Cache.SearchCase.INSENSITIVE, 1000);

            int count = 0;
            assertEquals("Best Buy should be [0.0]", 0.0, finalResults.get(count).getRank());
            assertEquals("Best Buy", "Best Buy", finalResults.get(count).getPhrase());

            count++;
            assertEquals("Best Buy should be [0.0]", 0.0, finalResults.get(count).getRank());
            assertEquals("Best Buy", "Best Buy", finalResults.get(count).getPhrase());

            count++;
            assertEquals("Best Buy should be [0.0]", 0.0, finalResults.get(count).getRank());
            assertEquals("Best Buy", "Best Buy", finalResults.get(count).getPhrase());


            count++;
            assertTrue("Buy Mac should be [1.33]", finalResults.get(count).getRank() > 1.32
                && finalResults.get(count).getRank() < 1.34d);
            assertEquals("should be Buy Mac", "Buy Mac", finalResults.get(count).getPhrase());

            count++;
            assertTrue("BuyFun, Inc should be [2.66]", finalResults.get(count).getRank() > 2.65
                && finalResults.get(count).getRank() < 2.67d);
            assertEquals("should be BuyFun, Inc", "BuyFun, Inc", finalResults.get(count).getPhrase());

            count++;
            assertTrue("Buytopia.ca,should be [2.66]", finalResults.get(count).getRank() > 2.65
                && finalResults.get(count).getRank() < 2.67d);
            assertEquals("should be Buytopia.ca", "Buytopia.ca", finalResults.get(count).getPhrase());



            count++;
            assertTrue("Buyincoins.com should be [3.66]", finalResults.get(count).getRank() > 3.65
                && finalResults.get(count).getRank() < 3.67d);
            assertEquals("should be ByIncoins.com", "Buyincoins.com", finalResults.get(count).getPhrase());



            count++;
            assertTrue("Buyincoins.com should be [3.66]", finalResults.get(count).getRank() > 3.65
                && finalResults.get(count).getRank() < 3.67d);
            assertEquals("should be BuyIncoins.com", "Buyincoins.com", finalResults.get(count).getPhrase());


            count++;
            assertTrue("Buy Extreme Beam should be [4.33]", finalResults.get(count).getRank() > 4.32
                && finalResults.get(count).getRank() < 4.34d);
            assertEquals("should be Buy Extreme Beam", "Buy Extreme Beam", finalResults.get(count).getPhrase());


            count++;
            assertTrue("Buy Extreme Beam should be [4.33]", finalResults.get(count).getRank() > 4.32
                && finalResults.get(count).getRank() < 4.34d);
            assertEquals("should be Buy Extreme Beam", "Buy Extreme Beam", finalResults.get(count).getPhrase());

            count++;
            assertTrue("BuyAutoParts.com should be [4.33]", finalResults.get(count).getRank() > 4.32
                && finalResults.get(count).getRank() < 4.34d);
            assertEquals("should be BuyAutoParts.com", "BuyAutoParts.com", finalResults.get(count).getPhrase());

            count++;
            assertTrue("BuyAutoParts.com should be [4.33]", finalResults.get(count).getRank() > 4.32
                && finalResults.get(count).getRank() < 4.34d);
            assertEquals("should be BuyAutoParts.com", "BuyAutoParts.com", finalResults.get(count).getPhrase());

            count++;
            assertTrue("Buy Emergency Foods should be [5.33]", finalResults.get(count).getRank() > 5.32
                && finalResults.get(count).getRank() < 5.34d);
            assertEquals("should be Buy Emergency Foods", "Buy Emergency Foods", finalResults.get(count).getPhrase());

            count++;
            assertTrue("BUYSEND.COM LIMITED should be [5.33]", finalResults.get(count).getRank() > 5.32
                && finalResults.get(count).getRank() < 5.34d);
            assertEquals("should be BUYSEND.COM LIMITED", "BUYSEND.COM LIMITED", finalResults.get(count).getPhrase());

            count++;
            assertTrue("Buysubscriptions.com - Immediate & BBC magazines should be [15.00]", finalResults.get(count).getRank() > 14.99
                && finalResults.get(count).getRank() < 15.01d);
            assertEquals("should be Buysubscriptions.com - Immediate & BBC magazines", "Buysubscriptions.com - Immediate & BBC magazines", finalResults.get(count).getPhrase());

        }catch(Exception e) {
            Log.d(TAG, "failed e=" + e.getMessage());
            fail(name.getMethodName() + ", e=" + e.getMessage());
        }

    }


    @Test
    public void testCosmopolitanVegas() throws Exception {

        try {

            Application application = RuntimeEnvironment.application;

            FirebaseApp.initializeApp(application);
            WildlinkSdk.init(new TestProviderImpl(application));

            WildlinkSdk sdk = WildlinkSdk.getIntance();

            sdk.overrideRealm(new CosmopolitanVegasRealm(application.getApplicationContext()));

            List<WildlinkBufferResult> finalResults =
                sdk.searchConceptsItem("Cosmopolitan Vegas", Cache.SearchCase.INSENSITIVE, 1000);

            int count = 0;
            assertEquals("Cosmopolitan Vegas should be [0.0]", 0.0, finalResults.get(count).getRank());
            assertEquals("Cosmopolitan Vegas", "Cosmopolitan Vegas", finalResults.get(count).getPhrase());


            count++;
            assertTrue("VEGAS.com should be [0.8]", finalResults.get(count).getRank() > 0.79d
                && finalResults.get(count).getRank() < 0.81d);
            assertEquals("should be VEGAS.com", "VEGAS.com", finalResults.get(count).getPhrase());

            count++;
            assertTrue("Vegas Tickets should be [1.6]", finalResults.get(count).getRank() > 1.59d
                && finalResults.get(count).getRank() < 1.61d);
            assertEquals("should be Vegas Tickets", "Vegas Tickets", finalResults.get(count).getPhrase());


        }catch(Exception e) {
            Log.d(TAG, "failed e=" + e.getMessage());
            fail(name.getMethodName() + ", e=" + e.getMessage());
        }

    }


    private WildlinkResult convertRealmObjectToResult(ConceptsItem item,Double rank) {
        WildlinkResult result = new WildlinkResult();
        result.setId(item.getId());
        if( item.getKind() != null) {
            result.setType(item.getKind());
        }
        result.setPhrase(item.getValue());
        result.setRank(rank);
        return result;
    }
}