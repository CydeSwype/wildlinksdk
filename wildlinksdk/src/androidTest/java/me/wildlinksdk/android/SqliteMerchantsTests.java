package me.wildlinksdk.android;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.test.RenamingDelegatingContext;
import android.util.Pair;
import io.requery.android.database.sqlite.SQLiteDatabase;
import java.util.List;
import junit.framework.Assert;
import me.wildlinksdk.android.api.ApiModule;
import me.wildlinksdk.android.db.MerchantsDataSource;
import me.wildlinksdk.android.models.MerchantItemDomain;
import me.wildlinksdk.android.ui.config.WlConfigActivity;

public class SqliteMerchantsTests extends
    android.test.ActivityUnitTestCase<WlConfigActivity> {

    private static final String TAG = SqliteMerchantsTests.class.getSimpleName();

    private static String PREFIX = "test";

    private RenamingDelegatingContext mRenamingDeligateContext;

    public SqliteMerchantsTests() {
        super(WlConfigActivity.class);


    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Common.deleteMockDbFiles(PREFIX, BuildConfig.SQLITE_DB_NAME);


        mRenamingDeligateContext = null;

        mRenamingDeligateContext = Common.createRenamingDelegatingContext(
            getInstrumentation().getTargetContext(), PREFIX);


    }


    public void testSqlCount() {
        try {

            Context appContext = InstrumentationRegistry.getContext();

            WildlinkSdk.init(new DatabaseCache10SecondTestsProviderImpl(InstrumentationRegistry.getTargetContext()));

            SQLiteDatabase database = ApiModule.INSTANCE.getDatabase();

            MerchantsDataSource ds = new MerchantsDataSource(database);

            List<Pair<String,String>> rows =Common.readMerchants(appContext,"merchant-domains.txt");

            for(Pair<String,String> pair: rows) {
                ds.insert(pair.first,pair.second);
            }

            int count = ds.count();
            assertTrue(count > 0);

            MerchantItemDomain itemDomain = ds.search("http://nordstrom.com/sdkfsdfs/1111");
            Assert.assertEquals("nordstrom.com", itemDomain.domain);
            Assert.assertEquals("shjhsdf", itemDomain.merchantItem.getId());


            itemDomain = ds.search("http://www.nordstrom.com/sdkfsdfs/1111");
            Assert.assertEquals("www.nordstrom.com", itemDomain.domain);
            Assert.assertEquals("sdsjdjd", itemDomain.merchantItem.getId());


            itemDomain = ds.search("http://www.yahoo.ca/sdkfsdfs/1111");
            Assert.assertEquals("yahoo.ca", itemDomain.domain);
            Assert.assertEquals("276y727", itemDomain.merchantItem.getId());

            itemDomain = ds.search("https://m.shop.nordstrom.com/c/all-topman?campaign=0416topmanhp&jid=J009163-4981&cid=ju8gf&cm_sp=merch-_-corp_4981_J009163-_-hp_corp_P10_shop");
            Assert.assertEquals("m.shop.nordstrom.com", itemDomain.domain);
            Assert.assertEquals("12345", itemDomain.merchantItem.getId());




        } catch(Exception e){

            org.junit.Assert.fail("testSqlCount" + ",failed e=" + e.getMessage());

        }


    }


}