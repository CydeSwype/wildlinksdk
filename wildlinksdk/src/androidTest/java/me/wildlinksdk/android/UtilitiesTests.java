package me.wildlinksdk.android;

import android.support.test.runner.AndroidJUnit4;

import me.wildlinksdk.android.models.WildlinkSearchResults;
import me.wildlinksdk.android.utilities.Utilities;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class UtilitiesTests {
    private final String TAG = UtilitiesTests.class.getSimpleName();
    @Rule
    public TestName testName = new TestName();
    private boolean initialized;
    private WildlinkSearchResults results;
    private CountDownLatch latch;

    @Before
    public void setup() {


    }

    @Test
    public void testSearchClosestMatch_CaseInsensitive() {

        try {
            Utilities utilities = new Utilities();

            String buffer = "        They   then the gold     ";

            List<String> list = utilities.getQueryPhrases(buffer);

            assertNotNull("list should not be null", list);
            assertTrue("list size should be 4", list.size() == 4);

            assertEquals(testName.getMethodName() + ", incorrect 1st phrase", "They   then the gold", list.get(0));


        } catch (Exception e) {
            latch.countDown();
            fail(testName.getMethodName() + ",failed e=" + e.getMessage());
        }
    }
}
