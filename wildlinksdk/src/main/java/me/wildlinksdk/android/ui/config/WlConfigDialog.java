package me.wildlinksdk.android.ui.config;

/**
 * Created by rjawanda on 12/21/17.
 */

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.List;
import me.wildlinksdk.android.BuildConfig;
import me.wildlinksdk.android.R;
import me.wildlinksdk.android.WildlinkSdk;
import me.wildlinksdk.android.api.ApiError;
import me.wildlinksdk.android.api.ApiModule;
import me.wildlinksdk.android.api.SharedPrefsUtil;
import me.wildlinksdk.android.ui.widgets.OkDialog;

public class WlConfigDialog extends DialogFragment implements ConfigDialogContract.View {

    private static final String TAG = WlConfigDialog.class.getSimpleName();
    private WlConfigDialogListener wlConfigDialogListener;
    private ConfigDialogPresenter presenter;
    private List<String> baseUrlArray;
    private String baseUrlJson;
    private SharedPrefsUtil prefs;

    private ViewGroup progressView;

    private Spinner baseUrlSpinner;

    private ViewGroup okContainer;

    private ViewGroup cancelContainer;

    public WlConfigDialog() {
        try {
            presenter = new ConfigDialogPresenter(WildlinkSdk.getIntance(), this);
        } catch (Exception e) {
            Log.d(TAG, "could not create dialog presenter");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        wlConfigDialogListener.onNegativeClicked();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.wl_config_dialog, container);

        progressView = (ViewGroup) view.findViewById(R.id.wl_progress_container);

        baseUrlSpinner = (Spinner) view.findViewById(R.id.base_url_spinner);

        okContainer = (ViewGroup) view.findViewById(R.id.wl_config_ok_container);

        cancelContainer = (ViewGroup) view.findViewById(R.id.wl_config_cancel_container);

        setupClickHandlers(view);

        prefs = ApiModule.INSTANCE.getSharedPrefsUtil();

        String baseApiUrl = prefs.getBaseApiUrl();
        String defaultServerFlavor = prefs.getDefaultServerFlavor();

        baseUrlArray = new ArrayList<String>();

        baseUrlArray.add("prod");
        baseUrlArray.add("dev");

        final ArrayAdapter<String> spinnerArrayAdapter =
            new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,
                baseUrlArray);

        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        baseUrlSpinner.setAdapter(spinnerArrayAdapter);

        // if the user stored a value previously make sure to set it as the selected one (highlighted)
        if (defaultServerFlavor != null) {
            if (defaultServerFlavor.contains("prod")) {

                baseUrlSpinner.setSelection(0);
            } else {
                baseUrlSpinner.setSelection(1);
            }
        }

        return view;
    }

    public void setupClickHandlers(final View v) {

        ViewGroup cancelContainer = (ViewGroup) v.findViewById(R.id.wl_config_cancel_container);
        cancelContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                wlConfigDialogListener.onNegativeClicked();
            }
        });

        ViewGroup okContainer = (ViewGroup) v.findViewById(R.id.wl_config_ok_container);
        okContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                int position = baseUrlSpinner.getSelectedItemPosition();

                if (position == 0) {
                    wlConfigDialogListener.onPositiveClicked(BuildConfig.prod, 0);
                } else {
                    wlConfigDialogListener.onPositiveClicked(BuildConfig.dev, 1);
                }
            }
        });
    }

    public void showDialog(FragmentManager manager, String tag, WlConfigDialogListener listener) {
        this.wlConfigDialogListener = listener;
        show(manager, tag);

        return;
    }

    @Override
    public void onFailure(final ApiError error) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressView.setVisibility(View.GONE);
                showFailedDialog("Verification Failed", error.getMessage());
            }
        });
    }

    private void showFailedDialog(String title, String string) {

        OkDialog okDialog = new OkDialog();
        okDialog.setStyle(R.style.WlOkDialog, 0);
        FragmentManager manager = getFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString(OkDialog.BTN_TEXT_KEY, "Ok");
        bundle.putString(OkDialog.TEXT_KEY, string);
        bundle.putString(OkDialog.TITLE_KEY, title);
        okDialog.setArguments(bundle);
        okDialog.showDialog(manager, "tag", new OkDialog.ClickListener() {

            @Override
            public void onPositiveClicked() {

            }

            @Override
            public void onNegativeClicked() {

            }
        });
    }

    public interface WlConfigDialogListener {
        public void onPositiveClicked(String url, int position);

        public void onNegativeClicked();
    }
}