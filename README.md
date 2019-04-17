# wildlink-android SDK

## Wildlink API 1.0.5.0 Apr 17, 2019

Minimum requirements
* Android SDK version 19 (KitKat)

### Setup for Android Studio 3.4 (latest stable)

In your project's `build.gradle` file in `allprojects` add the Maven repository for JitPack

```Maven

allprojects {
    repositories {
        maven { url "https://jitpack.io" } // add this line
    }
}

```

If you are prompted to Sync, then select the Sync Now option and allow Gradle to complete the sync.

Next, in the app module, add the Wildlink SDK to your dependencies:

```Deps

dependencies {
    implementation 'com.github.wildlink:wildlinksdk:1.0.5.0'
}

```

Again, if you're prompted to sync, select Sync Now.


### Usage in your client application

The Wildlink SDK takes credentials specific to your account, automatically generates authentication tokens and collects device make, model and OS version information for analysis.  You may then start the clipboard monitor service which will automatically collect eligible URLs for later analysis (without changing the user's clipboard value).

```Java

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize the SDK with your credentials (for app ID and secret please contact Wildfire: support@wildlink.me)
        WildlinkSdk.getIntance().init(this, "YOUR_APP_ID", "YOUR_APP_SECRET");

        // begin monitoring the clipboard for eligible partner URLs
        WildlinkSdk.getIntance().startClipboardMonitoringService(new SimpleListener() {
            @Override
            public void onSuccess() {
                Log.d("Wildlink", "successfully started Wildlink clipboard monitor");
            }

            @Override
            public void onFailure(final ApiError apiError) {
                Log.d("Wildlink", "on failure = " + apiError.getMessage());
            }
        });
    }
}

```

### License

Copyright 2019 Wildfire Systems, Inc.
