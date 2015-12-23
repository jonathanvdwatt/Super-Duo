package it.jaschke.alexandria;

import android.util.Log;

import com.journeyapps.barcodescanner.CaptureActivity;

import java.awt.font.TextAttribute;

/**
 * Created by f4720431 on 2015/11/10.
 */
public class CaptureActivityAnyOrientation extends CaptureActivity {
    private static final String TAG = CaptureActivityAnyOrientation.class.getSimpleName();

    public CaptureActivityAnyOrientation() {
        Log.d(TAG, "CaptureActivityAnyOrientation()");
    }
}