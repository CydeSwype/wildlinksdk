package me.wildlinksdk.android.ui.widgets;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import me.wildlinksdk.android.R;

public class OkDialog extends DialogFragment {

    public static final String BTN_TEXT_KEY = "ok_btn_text_key";

    public static final String TEXT_KEY = "text_key";

    public static final String TITLE_KEY = "title_key";

    public static final String NEGATIVE = "negative";

    private Typeface mTypeface = null;

    private ViewGroup mOkBtn;
    private Bundle mArgs;
    private TextView mTitle;
    private ViewGroup mNegBtn;

    private ClickListener clickListener;

    public OkDialog() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        mArgs = this.getArguments();

        View view = inflater.inflate(R.layout.wl_ok_dialog, container);
        mOkBtn = (ViewGroup) view.findViewById(R.id.ok_dialog_ok_container);
        mTitle = (TextView) view.findViewById(R.id.ok_dialog_title_tv);
        mTitle.setText(mArgs.getString(TITLE_KEY));
        // mOkBtn.setText(mArgs.getString(BTN_TEXT_KEY));
        mTitle.setTypeface(mTypeface);

        boolean includeNeg = mArgs.getBoolean(NEGATIVE);

        if (includeNeg) {
            mNegBtn = (ViewGroup) view.findViewById(R.id.ok_dialog_cancel_btn);
            mNegBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    clickListener.onNegativeClicked();
                    dismiss();
                }
            });
        } else {
            view.findViewById(R.id.ok_dialog_cancel_btn).setVisibility(View.GONE);
        }

        mOkBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clickListener.onPositiveClicked();
                dismiss();
            }
        });

        TextView mainTv = (TextView) view.findViewById(R.id.ok_dialog_main_tv);

        mainTv.setText(mArgs.getString(TEXT_KEY));

        return view;
    }

    public void showDialog(FragmentManager manager, String tag, ClickListener listener) {
        this.clickListener = listener;
        show(manager, tag);

        return;
    }

    public interface ClickListener {
        public void onPositiveClicked();

        public void onNegativeClicked();
    }
}