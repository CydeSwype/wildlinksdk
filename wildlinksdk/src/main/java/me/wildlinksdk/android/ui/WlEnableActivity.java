package me.wildlinksdk.android.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import me.wildlinksdk.android.R;
import me.wildlinksdk.android.api.ApiModule;
import me.wildlinksdk.android.api.SharedPrefsUtil;
import me.wildlinksdk.android.api.models.events.WlEnabledEvent;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by rjawanda on 12/19/17.
 */

public class WlEnableActivity extends AppCompatActivity {

    private static final String TAG = WlEnableActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_enable_wl_button);

        final SharedPrefsUtil prefs = ApiModule.INSTANCE.getSharedPrefsUtil();

        prefs.setHasBeenAskedToEnableAlready();

        EnableDialog dialog = new EnableDialog();

        dialog.showDialog(getFragmentManager(), TAG, new EnableDialog.ClickListener() {
            @Override
            public void onPositiveClicked() {
                Log.d(TAG, "enabled");

                EventBus.getDefault().post(new WlEnabledEvent());
                finish();
            }

            @Override
            public void onNegativeClicked() {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        EnableDialog fragment = (EnableDialog) getFragmentManager().findFragmentByTag(TAG);
        if (fragment != null) {
            fragment.dismiss();
        }
        super.onBackPressed();
    }
}
