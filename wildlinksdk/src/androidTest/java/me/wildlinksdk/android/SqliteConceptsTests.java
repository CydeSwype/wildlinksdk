package me.wildlinksdk.android;


import android.support.test.InstrumentationRegistry;
import io.requery.android.database.sqlite.SQLiteDatabase;

import java.util.List;
import junit.framework.Assert;

import me.wildlinksdk.android.api.ApiModule;
import me.wildlinksdk.android.db.ConceptsDataSource;
import me.wildlinksdk.android.models.Item;
import me.wildlinksdk.android.ui.config.WlConfigActivity;

import android.content.Context;

import android.test.RenamingDelegatingContext;

public class SqliteConceptsTests extends
    android.test.ActivityUnitTestCase<WlConfigActivity> {

    private static final String TAG = SqliteConceptsTests.class.getSimpleName();

    private static String PREFIX = "test";

    private RenamingDelegatingContext mRenamingDeligateContext;

    public SqliteConceptsTests() {
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
            assertEquals(823,count);

            List<Item> items = ds.searchBuffer("Testing Lord");
            Assert.assertEquals("Lord & Taylor", items.get(0).getToken());
            Assert.assertEquals("Lord", items.get(0).getMatchingPhase());





           items = ds.searchBuffer("Belton Cinema");
            Assert.assertEquals("Belton Cinema 8", items.get(0).getToken());

            items = ds.searchBuffer("bumble and bumble");
            Assert.assertEquals("bumble and bumble", items.get(0).getToken());

            items = ds.searchBuffer("bumble and b");
            Assert.assertEquals("bumble and bumble", items.get(0).getToken());


            items = ds.searchBuffer("bumble and");
            Assert.assertEquals("bumble and bumble", items.get(0).getToken());

            items = ds.searchBuffer("bumble and bum");
            Assert.assertEquals("bumble and bumble", items.get(0).getToken());
            items = ds.searchBuffer("bebe");
            Assert.assertEquals("Bebe", items.get(0).getToken());
            items = ds.searchBuffer("i want to be at bebe");
            Assert.assertEquals("Bebe", items.get(0).getToken());
            Assert.assertEquals("should be bebe", "bebe", items.get(0).getMatchingPhase());


            items = ds.searchBuffer("lets go to big lots");
            Assert.assertEquals("Big Lots!", items.get(0).getToken());

            items = ds.searchBuffer("Bitsbox");
            Assert.assertEquals("Bitsbox", items.get(0).getToken());
            items = ds.searchBuffer("what do you think about Burton bumble and bumble");
            Assert.assertEquals("bumble and bumble", items.get(0).getToken());




        } catch(Exception e){

            org.junit.Assert.fail("testSqlCount" + ",failed e=" + e.getMessage());

        }


    }

}