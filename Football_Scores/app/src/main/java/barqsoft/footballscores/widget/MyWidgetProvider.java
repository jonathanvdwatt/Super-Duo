package barqsoft.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.service.MyFetchService;

/**
 * Created by f4720431 on 2015/12/21.
 */
public class MyWidgetProvider extends AppWidgetProvider {
    private static final String TAG = MyWidgetProvider.class.getSimpleName();

    private static HandlerThread sWorkerThread;
    private static Handler sWorkerQueue;
    private static UpdateWidgetObserver sUpdateObserver;

    private AppWidgetManager mAppWidgetManager;

    public MyWidgetProvider() {
        Log.d(TAG, "######## MyWidgetProvider()");

        sWorkerThread = new HandlerThread("ScoresProvider-worker");
        sWorkerThread.start();
        sWorkerQueue = new Handler(sWorkerThread.getLooper());
    }

    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "onEnabled()");

        final ContentResolver contentResolver = context.getContentResolver();

        if(sUpdateObserver == null) {
            Log.d(TAG, "######## sUpdateObserver is null.");

            try {
                mAppWidgetManager = AppWidgetManager.getInstance(context);
                sUpdateObserver = new UpdateWidgetObserver(mAppWidgetManager, context, sWorkerQueue);
                contentResolver.registerContentObserver(DatabaseContract.BASE_CONTENT_URI, true, sUpdateObserver);
            }
            catch (Exception e) {
                Log.e(TAG, "######## Unable to register ContentObserver", e);
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "######## onUpdate method called");

        // Get all ids
        ComponentName thisWidget = new ComponentName(context, MyWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        // Build the intent to call the service
        Intent intent = new Intent(context, MyFetchService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

        // Update the widgets via the service
        context.startService(intent);
    }
}
