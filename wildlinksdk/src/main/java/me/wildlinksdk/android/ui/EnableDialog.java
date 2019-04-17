package me.wildlinksdk.android.ui;

/**
 * Created by rjawanda on 12/21/17.
 */

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import me.wildlinksdk.android.BuildConfig;
import me.wildlinksdk.android.R;
import me.wildlinksdk.android.api.ApiModule;
import me.wildlinksdk.android.api.SharedPrefsUtil;

public class EnableDialog extends DialogFragment {

    private static final String TAG = EnableDialog.class.getSimpleName();
    private ClickListener clickListener;

    private TextView learnMoreTv;

    private TextView noThanksTv;

    private TextView termsTv;

    private TextView contentTv;

    public EnableDialog() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        clickListener.onNegativeClicked();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.enable_dialog_layout, container);

        learnMoreTv = (TextView) view.findViewById(R.id.wl_whats_new_learn_more_tv);
        noThanksTv = (TextView) view.findViewById(R.id.wl_whats_new_no_thanks_tv);
        termsTv = (TextView) view.findViewById(R.id.wl_whats_new_terms_tv);
        contentTv = (TextView) view.findViewById(R.id.wl_whats_new_content_tv);

        setupClickHandlers(view);
        SharedPrefsUtil prefs = ApiModule.INSTANCE.getSharedPrefsUtil();
        prefs.setWlEnableActivityHasBeenShownToUserAlready();

        learnMoreTv.setText(getString(R.string.wl_whats_new_dialog_learn_more_text));
        noThanksTv.setText(getString(R.string.wl_whats_new_dialog_new_no_thanks_text));

        String content = getString(R.string.wl_whats_new_dialog_text);
        String contentReplaced = String.format(content, getString(R.string.wl_app_name));

        contentTv.setText(contentReplaced);

        String terms1 = getString(R.string.wl_whats_new_dialog_terms1_text);
        String terms2 = getString(R.string.wl_whats_new_dialog_terms2_text);
        String terms3 = getString(R.string.wl_whats_new_dialog_terms3_text);
        String terms4 = getString(R.string.wl_whats_new_dialog_terms4_text);

        String yourString = terms1 + " " + terms2 + " " + terms3 + " " + terms4;
        int index2 = yourString.indexOf(terms2);
        int index4 = yourString.indexOf(terms4);

        ClickableSpan termsClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(BuildConfig.terms));
                startActivity(i);
            }

            @Override
            public void updateDrawState(final TextPaint textPaint) {
                textPaint.setColor(getResources().getColor(R.color.wl_whats_new_link_text_color));
                textPaint.setUnderlineText(true);
            }
        };

        ClickableSpan privacyClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(BuildConfig.privacy_policy));
                startActivity(i);
            }

            @Override
            public void updateDrawState(final TextPaint textPaint) {
                textPaint.setColor(getResources().getColor(R.color.wl_whats_new_link_text_color));
                textPaint.setUnderlineText(true);
            }
        };
        ClickableSpan learnMoreClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(BuildConfig.baseWebUrl));
                startActivity(i);
            }

            @Override
            public void updateDrawState(final TextPaint textPaint) {
                textPaint.setColor(getResources().getColor(R.color.wl_black));
                textPaint.setUnderlineText(true);
            }
        };
        ClickableSpan noThanksClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                getActivity().finish();
            }

            @Override
            public void updateDrawState(final TextPaint textPaint) {
                textPaint.setColor(getResources().getColor(R.color.wl_black));
                textPaint.setUnderlineText(true);
            }
        };

        SpannableString contentSpan = new SpannableString(yourString);
        contentSpan.setSpan(new UnderlineSpan(), index2, index2 + terms2.length(), 0);
        contentSpan.setSpan(termsClickableSpan, index2, index2 + terms2.length(),
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        contentSpan.setSpan(new UnderlineSpan(), index4, index4 + terms4.length(), 0);

        contentSpan.setSpan(privacyClickableSpan, index4, index4 + terms4.length(),
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        termsTv.setText(contentSpan);
        termsTv.setMovementMethod(LinkMovementMethod.getInstance());
        termsTv.setHighlightColor(
            getResources().getColor(R.color.wl_whats_new_link_hightlight_color));

        String learnMore = getString(R.string.wl_whats_new_dialog_learn_more_text);
        SpannableString lmContentSpan = new SpannableString(learnMore);
        lmContentSpan.setSpan(new UnderlineSpan(), 0, learnMore.length(), 0);
        lmContentSpan.setSpan(learnMoreClickableSpan, 0, learnMore.length(),
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        learnMoreTv.setMovementMethod(LinkMovementMethod.getInstance());
        learnMoreTv.setHighlightColor(
            getResources().getColor(R.color.wl_whats_new_link_hightlight_color));
        learnMoreTv.setText(lmContentSpan);

        String noThanks = getString(R.string.wl_whats_new_dialog_new_no_thanks_text);
        SpannableString ntContentSpan = new SpannableString(noThanks);
        ntContentSpan.setSpan(new UnderlineSpan(), 0, noThanks.length(), 0);
        ntContentSpan.setSpan(noThanksClickableSpan, 0, noThanks.length(),
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        noThanksTv.setMovementMethod(LinkMovementMethod.getInstance());
        noThanksTv.setHighlightColor(
            getResources().getColor(R.color.wl_whats_new_link_hightlight_color));
        noThanksTv.setText(ntContentSpan);

        return view;
    }

    public void setupClickHandlers(final View v) {

        ViewGroup enableContainer = (ViewGroup) v.findViewById(R.id.wl_whats_new_ebable_container);
        enableContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                SharedPrefsUtil prefs = ApiModule.INSTANCE.getSharedPrefsUtil();
                prefs.setWlEnabled();
                clickListener.onPositiveClicked();
            }
        });
        TextView learnMoreTv = (TextView) v.findViewById(R.id.wl_whats_new_learn_more_tv);
        learnMoreTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(BuildConfig.baseWebUrl));
                startActivity(i);
            }
        });
    }

    public void showDialog(FragmentManager manager, String tag, ClickListener listener) {
        this.clickListener = listener;
        show(manager, tag);

        return;
    }

    public String getAppName(Context context) {

        PackageManager packageManager = context.getPackageManager();

        ApplicationInfo ai = null;
        try {
            ai = packageManager.getApplicationInfo(context.getPackageName(), 0);
            Log.d(TAG, "UID=" + ai.uid);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        final String applicationName =
            (String) (ai != null ? packageManager.getApplicationLabel(ai) : "(unknown)");

        return applicationName;
    }

    public interface ClickListener {
        public void onPositiveClicked();

        public void onNegativeClicked();
    }
}