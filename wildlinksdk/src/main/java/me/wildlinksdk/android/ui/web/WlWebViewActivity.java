package me.wildlinksdk.android.ui.web;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import me.wildlinksdk.android.Constants;
import me.wildlinksdk.android.R;

/**
 * Created by rjawanda on 12/19/17.
 */

public class WlWebViewActivity extends AppCompatActivity {

    private static final String TAG = WlWebViewActivity.class.getSimpleName();

    private WebView web;

    private ViewGroup loading;
    private String page;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wl_web_view);

        loading = (ViewGroup) findViewById(R.id.wl_loading_overlay);
        web = (WebView) findViewById(R.id.webview);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.d(TAG, "getIntent().getStringExtra(Constants.KEY_WL_WEB_PAGE_TITLE)="
            + getIntent().getStringExtra(Constants.KEY_WL_WEB_PAGE_TITLE));
        String title = getIntent().getStringExtra(Constants.KEY_WL_WEB_PAGE_TITLE);
        getSupportActionBar().setTitle(getIntent().getStringExtra(Constants.KEY_WL_WEB_PAGE_TITLE));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        loading.setVisibility(View.VISIBLE);
        web.setWebViewClient(new myWebClient());
        web.getSettings().setJavaScriptEnabled(true);
        page = getIntent().getStringExtra(Constants.KEY_WL_WEB_PAGE);
        web.loadUrl(getIntent().getStringExtra(Constants.KEY_WL_WEB_PAGE));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && web.canGoBack()) {
            web.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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

    public class myWebClient extends WebViewClient {

        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(page);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub

            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            loading.setVisibility(View.GONE);
        }
    }
}
