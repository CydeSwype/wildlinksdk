package me.wildlinksdk.android.ui.widgets;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import me.wildlinksdk.android.R;

public class ToastExt {
    private static final String TAG = ToastExt.class.getSimpleName();
    private Toast toast;
    private Context context;

    public ToastExt() {
    }

    public ToastExt(Context context) {
        this.context = context;
    }

    public void show(String message, int duration) {
        closeToast(duration);
        toast.setText(message);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        TextView view1 = (TextView) toast.getView().findViewById(android.R.id.message);
        view1.setTextColor(Color.WHITE);
        toast.getView().setBackgroundResource(R.color.wl_orange);
        toast.show();
    }

    private void closeToast(int duration) {
        if (toast == null) {
            toast = Toast.makeText(context, "", duration);
        } else {
            if (toast.getView().getVisibility() == View.VISIBLE) {
                toast.cancel();
                toast = Toast.makeText(context, "", toast.getDuration());
            }
        }
    }

    public void show(int stringId, int duration) {
        closeToast(duration);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.setText(stringId);
        TextView view1 = (TextView) toast.getView().findViewById(android.R.id.message);
        view1.setTextColor(Color.WHITE);
        toast.getView().setBackgroundResource(R.color.wl_orange);
        toast.show();
    }
}