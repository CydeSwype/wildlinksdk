package me.wildlinksdk.android.ui.logs;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import me.wildlinksdk.android.R;
import me.wildlinksdk.android.api.ApiModule;
import me.wildlinksdk.android.api.SharedPrefsUtil;
import me.wildlinksdk.android.ui.config.WlConfigDialog;
import me.wildlinksdk.android.ui.widgets.ToastExt;
import me.wildlinksdk.android.utilities.Utilities;

/**
 * Created by rjawanda on 12/19/17.
 */

public class WlLogActivity extends AppCompatActivity {

    private static final String TAG = WlLogActivity.class.getSimpleName();
    private WlConfigDialog configDialog;
    private ToastExt toastExt;
    private SharedPrefsUtil prefs;
    private Utilities utilities;
    private ViewGroup loadingView;
    private EditText emailEt;
    private ViewGroup uploadContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wllog);

        loadingView = (ViewGroup) findViewById(R.id.wl_loading_overlay);
        emailEt = (EditText) findViewById(R.id.wl_log_email_et);
        uploadContainer = (ViewGroup) findViewById(R.id.wl_log_upload_container);
        uploadContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (emailEt.getText().toString().trim().length() > 0) {
                    loadingView.setVisibility(View.VISIBLE);
                    new LogAsyncTask(new LoadingListener() {

                        @Override
                        public void onComplete(String logs) {

                            loadingView.setVisibility(View.GONE);
                            Intent i = new Intent(Intent.ACTION_SEND);
                            i.setType("message/rfc822");
                            i.putExtra(Intent.EXTRA_EMAIL,
                                new String[] { emailEt.getText().toString().trim() });
                            i.putExtra(Intent.EXTRA_SUBJECT,
                                getResources().getString(R.string.wl_log_email_subject));
                            i.putExtra(Intent.EXTRA_TEXT, logs);
                            try {
                                startActivity(Intent.createChooser(i, "Wildlink Email"));
                            } catch (android.content.ActivityNotFoundException ex) {

                            }
                        }
                    }).execute();
                }
            }
        });

        toastExt = new ToastExt(this);
        utilities = new Utilities();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.wl_log_title_text));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        prefs = ApiModule.INSTANCE.getSharedPrefsUtil();
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

    private interface LoadingListener {
        public void onComplete(String logs);
    }

    private static class LogAsyncTask extends AsyncTask<String, Void, String> {

        private LoadingListener listener;

        public LogAsyncTask(LoadingListener listener) {
            this.listener = listener;
        }

        @Override
        protected String doInBackground(String... params) {

            StringBuffer buffer = new StringBuffer();
            try {
                Process process = Runtime.getRuntime().exec("logcat -d");
                BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    buffer.append(line);
                }
                bufferedReader.close();
                ;
            } catch (Exception e) {
                buffer.append(e.getMessage());
            }
            return buffer.toString();
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(String logs) {
            listener.onComplete(logs);
            return;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
