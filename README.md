# wildlink-android SDK

Documentation

## Wildlink API 1.0.5.0 Apr 17, 2019

### Notes before proceeding
* Min Android SDK version is 19 (KitKat)

## Documentation

## Setup for Android Studio 3.4 (latest stable)

In your project's `Build.gradle` file in `allprojects` add the Maven repository for JitPack

```Maven

allprojects {
    repositories {
        maven { url "https://jitpack.io" } // add this line
    }
}

```

You will prompted to Sync.  Select the Sync Now option and allow Gradle to complete the sync.

Next, in the app module, add the Wildlink SDK to your dependencies:

```Deps

dependencies {
    implementation 'com.github.wildlink:wildlinksdk:1.0.5.0'
}

```

### Usage in your client application

Extend your application class

```Java

  // for app ID and secret please contact Wildfire (support@wildlink.me)
    public class MainActivity extends AppCompatActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate();
            WildlinkSdk.getIntance().init(this, null, "YOUR_APP_ID", "YOUR_APP_SECRET");
        }
    }

```

In your main Launcher activity in onCreate() initialize the monitor


```Java

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

```





```

### License

Copyright 2019 Wildfire Systems, Inc.
