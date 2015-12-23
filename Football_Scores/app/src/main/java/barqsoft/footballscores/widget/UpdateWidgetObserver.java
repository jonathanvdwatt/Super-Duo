package barqsoft.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import barqsoft.footballscores.DatabaseContract;

import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilities;

/**
 * Created by f4720431 on 2015/12/21.
 */
public class UpdateWidgetObserver extends ContentObserver {
    private static final String TAG = UpdateWidgetObserver.class.getSimpleName();
    private AppWidgetManager mAppWidgetManager;
    private Context mContext;
    private RemoteViews mRemoteViews;

    public static final int COLUMN_DATE = 1;
    public static final int COLUMN_GAMETIME = 2;
    public static final int COLUMN_HOME = 3;
    public static final int COLUMN_AWAY = 4;

    public UpdateWidgetObserver(AppWidgetManager appWidgetManager, Context context, Handler handler) {
        super(handler);
        Log.d(TAG, "######## UpdateWidgetObserver default constructor");
        mAppWidgetManager = appWidgetManager;
        mContext = context;
    }

    @Override
    public void onChange(boolean selfChange) {
        Log.d(TAG, "onChange()");

        try {
            Cursor c = mContext.getContentResolver().query(
                    DatabaseContract.BASE_CONTENT_URI,
                    null,
                    null,
                    null,
                    null);

            // Find the next event and update the widget accordingly
            this.findTheNextEvent(c);
        }
        catch (Exception e) {
            Log.e(TAG, "Failed to find an event." , e);
        }
    }

    private boolean findTheNextEvent(Cursor c) throws Exception {
        Log.d(TAG, "######## findTheNextEvent()");

        Calendar calendar = Calendar.getInstance();

        while (c.moveToNext()) {

            int homeGoals = c.getInt(6);
            int awayGoals = c.getInt(7);
            Calendar event = Calendar.getInstance();

            /* Checks to see if this is the next event and if we do not
               already have scores.
            */
            if(homeGoals == -1 && awayGoals == -1 && calendar.before(event)) {
                String eventDate = c.getString(COLUMN_DATE);

                if(calendar.get(Calendar.DAY_OF_YEAR) == event.get(Calendar.DAY_OF_YEAR)) {
                    eventDate = "Today";
                }
                else if((calendar.get(Calendar.DAY_OF_YEAR) + 1) == event.get(Calendar.DAY_OF_YEAR)) {
                    eventDate = "Tomorrow";
                }

                this.updateWidget(
                        c.getString(COLUMN_HOME),
                        Utilities.getTeamCrestByTeamName(c.getString(COLUMN_HOME)),
                        c.getString(COLUMN_AWAY),
                        Utilities.getTeamCrestByTeamName(c.getString(COLUMN_AWAY)),
                        eventDate,
                        c.getString(COLUMN_GAMETIME));

                return true;
            }
        }
        return false;
    }

    private void updateWidget(String homeName, int homeIcon, String awayName, int awayIcon, String eventDate, String eventTime) {
        Log.d(TAG, "######## updateWidget()");

        mRemoteViews = new RemoteViews(this.mContext.getPackageName(), R.layout.widget_layout);
        ComponentName thisHereWidget = new ComponentName(mContext, MyWidgetProvider.class);
        int[] allWidgetIds = mAppWidgetManager.getAppWidgetIds(thisHereWidget);

        // Home team's details
        mRemoteViews.setTextViewText(R.id.widgetHomeName, homeName);
        mRemoteViews.setImageViewResource(R.id.widgetHomeCrest, homeIcon);
        mRemoteViews.setContentDescription(R.id.widgetHomeName, mContext.getString(R.string.home_team_name_text) + homeName);

        // Away team's details
        mRemoteViews.setTextViewText(R.id.widgetAwayName, awayName);
        mRemoteViews.setImageViewResource(R.id.widgetAwayCrest, awayIcon);
        mRemoteViews.setContentDescription(R.id.widgetAwayName, mContext.getString(R.string.away_team_name_text) + awayName);

        // Match date and time
        mRemoteViews.setTextViewText(R.id.widgetNextMatchDate, eventDate);
        mRemoteViews.setContentDescription(R.id.widgetNextMatchDate, mContext.getString(R.string.next_match_date_text) + eventDate);
        mRemoteViews.setTextViewText(R.id.widgetNextMatchTime, eventTime);
        mRemoteViews.setContentDescription(R.id.widgetNextMatchTime, mContext.getString(R.string.next_match_time_text) + eventTime);

        // Tell the widget manager to update the widgets
        for(int widgetId : allWidgetIds) {
            mAppWidgetManager.updateAppWidget(widgetId, mRemoteViews);
        }
    }
}
