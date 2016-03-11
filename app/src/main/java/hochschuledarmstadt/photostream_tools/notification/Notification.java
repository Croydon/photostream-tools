package hochschuledarmstadt.photostream_tools.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;
import hochschuledarmstadt.photostream_tools.R;

/**
 * Created by Andreas Schattney on 25.02.2016.
 */
public class Notification {

    public static void send(Context context,String message,int notificationId, Class<?> activityClass ){
        // Use NotificationCompat.Builder to set up our notification.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        // Set the intent that will fire when the user taps the notification.
        Intent resultIntent = new Intent(context,activityClass);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        // Large icon appears on the left of the notification
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
        builder.setSmallIcon(R.drawable.ic_launcher);
        String title = context.getString(R.string.app_name);
        builder.setTicker(title);
        // Content title, which appears in large type at the top of the notification
        builder.setContentTitle(title);
        builder.setAutoCancel(false);
        builder.setWhen(0);
        builder.setContentText(message);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        builder.setDefaults(NotificationCompat.DEFAULT_LIGHTS | NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Will display the notification in the notification bar
        notificationManager.notify(notificationId, builder.build());
    }

}
