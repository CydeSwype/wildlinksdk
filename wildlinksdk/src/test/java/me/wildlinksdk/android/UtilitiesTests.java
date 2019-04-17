package me.wildlinksdk.android;

import android.app.Application;
import android.util.Log;
import edu.emory.mathcs.backport.java.util.concurrent.CountDownLatch;
import java.util.List;
import me.wildlinksdk.android.utilities.Utilities;
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

public class UtilitiesTests {


    private static final String TAG = UtilitiesTests.class.getSimpleName();

      @Rule public TestName name = new TestName();

    @Before
    public void setUp() throws Exception { ShadowLog.stream = System.out;  }



    @Test
    public void testgetQueryPhrases() throws Exception {

        try {

            Application application = RuntimeEnvironment.application;

            Utilities utilities = new Utilities();

            List<String> strings = utilities.getQueryPhrases("This\n\nis a test\nExperian");

            assertEquals("strings are incorrect", strings.size() , 4);
            assertEquals("first string should contain 4 strings", strings.get(0).split(" ").length , 4);


        }catch(Exception e) {
            Log.d(TAG, "failed e=" + e.getMessage());
            fail(name.getMethodName() + ", e=" + e.getMessage());
        }

    }


   @Test
    public void testgetQueryPhrases2() throws Exception {

        try {

            Application application = RuntimeEnvironment.application;

            Utilities utilities = new Utilities();

            List<String> strings = utilities.getQueryPhrases("I love bumble and bumble http://wild.link/sdsdkfhsd\nbumble and bu");

            assertEquals("strings are incorrect", strings.size() , 3);
            assertEquals("first string should contain 3 strings", strings.get(0).split(" ").length , 3);


        }catch(Exception e) {
            Log.d(TAG, "failed e=" + e.getMessage());
            fail(name.getMethodName() + ", e=" + e.getMessage());
        }

    }




   @Test
    public void testgetQueryPhrases3() throws Exception {

        try {

            Application application = RuntimeEnvironment.application;

            Utilities utilities = new Utilities();

            List<String> strings = utilities.getQueryPhrases("I love bumble and bumble http://wild.link/sdsdkfhsd\nbumble and bu I love bumble and bumble http://wild.link/sdsdkfhsd\nbumble and bumb");

            assertEquals("strings are incorrect", strings.size() , 3);
            assertEquals("first string should contain 3 strings", strings.get(0).split(" ").length , 3);
            assertEquals("should be bumble and bumb", strings.get(0) , "bumble and bumb");



        }catch(Exception e) {
            Log.d(TAG, "failed e=" + e.getMessage());
            fail(name.getMethodName() + ", e=" + e.getMessage());
        }

    }

    @Test
    public void testgetQueryPhrases4() throws Exception {

        try {

            Application application = RuntimeEnvironment.application;

            Utilities utilities = new Utilities();

            List<String> strings = utilities.getQueryPhrases("R");

            assertEquals("strings are incorrect", strings.size() , 1);



        }catch(Exception e) {
            Log.d(TAG, "failed e=" + e.getMessage());
            fail(name.getMethodName() + ", e=" + e.getMessage());
        }

    }

}