# wildlink-android SDK

Documentation

## Wildlink API 1.0.3.0 Apr 11, 2019

### Notes before proceeding
* Min Android SDK version is 19 (KitKat)

## Documentation

## Setup for Android Studio 3.3.2 Latest stable version

In the Projects `Build.gradle` file in `allprojects` add maven repository for jitPack

```Maven

allprojects {
    repositories {
        maven {
            url "https://jitpack.io"  //add this line to your repositories
        }
    }
}

```

In the app module Add this line to your dependencies

```Deps

dependencies {
    implementation 'com.github.wildlink:wildlinksdk:1.0.3.0'
}

```

### Usage in your client application

Extend your application class (sample below)

```Java

  // for app id and secret please contact Wildfire
  public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        WildlinkSdk.getIntance().init(this, null,"APPID","SECRET");


    }
  }


```

In your main Launcher activity in onCreate() initialize the monitor


```Java

    WildlinkSdk.getIntance().startClipboardMonitoringService(new SimpleListener() {
              @Override
              public void onSuccess() {
                  vanityBtn.setClickable(true);
                  messageEt.setText(getString(R.string.sdk_initialized));
              }

              @Override
              public void onFailure(final ApiError apiError) {
                  Log.d(TAG, "on failure = " + apiError.getMessage());
              }
          });


```

### SDK API

```Java


   /***
     * this will start a foreground service with a Notification showing in the android notification
     * tray. This works with channels on Android 8.1 and lower versions.
     * Downloads the domain database automatically in the background
     * NOTE: you must implement ClipboardMonitorProvider
     * @param appContext
     * @throws  Exception
     */
    public void startClipboardMonitoringService(Context appContext ) throws Exception;





```

### License

Copyright 2019 Wildfire Inc.
