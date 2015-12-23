package it.jaschke.alexandria.Util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by f4720431 on 2015/11/10.
 * http://stackoverflow.com/questions/5346980/intentservice-wont-show-toast
 */
public class DisplayToast implements Runnable {
    private static final String TAG = DisplayToast.class.getSimpleName();
    private final Context mContext;
    private String mText;

    public DisplayToast(Context context, String text) {
        this.mContext = context;
        mText = text;
    }

    @Override
    public void run() {
        Toast.makeText(mContext, mText, Toast.LENGTH_LONG).show();
    }
}
