package me.wildlinksdk.android;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.test.RenamingDelegatingContext;
import io.requery.android.database.sqlite.SQLiteDatabase;
import java.util.List;
import junit.framework.Assert;
import me.wildlinksdk.android.api.ApiModule;
import me.wildlinksdk.android.db.ConceptsDataSource;
import me.wildlinksdk.android.models.Item;
import me.wildlinksdk.android.ui.config.WlConfigActivity;

public class SearchItemsTests extends
    android.test.ActivityUnitTestCase<WlConfigActivity> {

    private static final String TAG = SearchItemsTests.class.getSimpleName();

    private static String PREFIX = "test";

    private RenamingDelegatingContext mRenamingDeligateContext;

    public SearchItemsTests() {
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

            ConceptsDataSource ds = new ConceptsDataSource(database);

            List<String> tokens =Common.readTokens(appContext,"test-tokens.txt");

            int i = 1;
            for(String token: tokens) {
                ds.insert("" + i,token);
                i++;
            }




            int count = ds.count();
            assertEquals(822,count);



            WildlinkSdk sdk = WildlinkSdk.getIntance();

            List<Item> items = sdk.searchItems("http://jackets.nordstroms.com/sddfsd Belton Cinema");
            Assert.assertEquals("Belton Cinema 8", items.get(0).getToken());

            items = sdk.searchItems("this is Belton Cinema");
            Assert.assertEquals("Belton Cinema 8", items.get(0).getToken());

            items = sdk.searchItems("this https://www.yahoo.com is Belton Cinema");
            Assert.assertEquals("Belton Cinema 8", items.get(0).getToken());



        } catch(Exception e){

            org.junit.Assert.fail("testSqlCount" + ",failed e=" + e.getMessage());

        }


    }

}