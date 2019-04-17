package me.wildlinksdk.android;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import me.wildlinksdk.android.api.ApiModule;
import me.wildlinksdk.android.api.CryptographyApi;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class CryptographyTests {
    @Rule
    public TestName testName = new TestName();

    @Test
    public void testEncryptDecryptUsingKeystoreSuccess() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();


        WildlinkSdk.init(new WildlinkProviderImpl(appContext));

        WildlinkSdk wildlinkSdk = WildlinkSdk.getIntance();


        ApiModule module2 = ApiModule.INSTANCE;

        CryptographyApi api = module2.getCryptographyApi();

        String hash = api.createSha256Hmac("123", "y81qJSPVmVCAjsOz3i6l");

        assertNotNull(testName.getMethodName() + ", results should not be null", hash);
        assertEquals(testName.getMethodName() + ", hash incorrect", "a49342294a76709cd2e0f85e3b5c4fccaf3fcb1c95d8279b677df30aedf884d4", hash);


    }


}
