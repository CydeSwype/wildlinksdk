package me.wildlinksdk.android.ui.config;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import me.wildlinksdk.android.PublicConstants;
import me.wildlinksdk.android.R;
import me.wildlinksdk.android.SimpleListener;
import me.wildlinksdk.android.api.ApiError;
import me.wildlinksdk.android.api.ApiModule;
import me.wildlinksdk.android.api.EnvAsyncTask;
import me.wildlinksdk.android.api.SharedPrefsUtil;
import me.wildlinksdk.android.ui.widgets.ToastExt;
import me.wildlinksdk.android.utilities.Utilities;

/**
 * Created by rjawanda on 12/19/17.
 */

public class WlConfigActivity extends AppCompatActivity {

    private static final String TAG = WlConfigActivity.class.getSimpleName();
    private WlConfigDialog configDialog;
    private ToastExt toastExt;
    private SharedPrefsUtil prefs;
    private Utilities utilities;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wlconfig);

        toastExt = new ToastExt(this);
        utilities = new Utilities();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.wl_config_title_text));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        prefs = ApiModule.INSTANCE.getSharedPrefsUtil();

        showConfigDialog();
    }

    private void showConfigDialog() {

        configDialog = new WlConfigDialog();

        configDialog.showDialog(getFragmentManager(), TAG,
            new WlConfigDialog.WlConfigDialogListener() {
                @Override
                public void onPositiveClicked(String url, int position) {
                    // position 0 == prod, 1 == dev
                    Log.d(TAG, "url=" + url);

                    int clientTableType = prefs.getWhichTableTypeClientUses();

                    prefs.clear();

                    prefs.setOverrideBaseUrl(url);

                    final Intent intent = new Intent(PublicConstants.BROADCAST_RECEIVER_API_BASE);
                    intent.putExtra(PublicConstants.API_BASE, url);
                    String env = null;
                    if (position == 0) {
                        Log.d(TAG, "sending intent api_base=" + url);
                        Log.d(TAG, "system=prod");
                        intent.putExtra(PublicConstants.API_BASE, url);
                        env = "prod";
                        intent.putExtra(PublicConstants.SERVER_TYPE, env);
                        prefs.setOverrideServerFlavor(env);
                    } else {
                        Log.d(TAG, "sending intent api_base=" + url);
                        Log.d(TAG, "system=dev");
                        env = "dev";
                        intent.putExtra(PublicConstants.API_BASE, url);
                        intent.putExtra(PublicConstants.SERVER_TYPE, env);
                        prefs.setOverrideServerFlavor(env);
                    }

                    resetData(env, clientTableType, new SimpleListener() {
                        @Override
                        public void onSuccess() {
                            sendBroadcast(intent);

                            configDialog.dismiss();
                        }

                        @Override
                        public void onFailure(final ApiError error) {
                            toastExt.show("Failure changing env " + error.getMessage(),
                                Toast.LENGTH_LONG);
                            sendBroadcast(intent);
                            configDialog.dismiss();
                        }
                    });
                }

                @Override
                public void onNegativeClicked() {
                    configDialog.dismiss();
                    finish();
                }
            });
    }

    private void resetData(String env, int clientTableType, SimpleListener listener) {

        new EnvAsyncTask(env, clientTableType, ApiModule.INSTANCE, listener).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (configDialog.isVisible()) {
            configDialog.dismiss();
            return;
        }

        super.onBackPressed();
    }
}
