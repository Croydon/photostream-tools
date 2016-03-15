/*
 * The MIT License
 *
 * Copyright (c) 2016 Andreas Schattney
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package hochschuledarmstadt.photostream_tools;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;

class Notification {

    public static void send(Context context,int notificationId, Class<?> activityClass, String title, String message, final int iconResource ){
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
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), iconResource));
        builder.setSmallIcon(iconResource);
        builder.setTicker(title);
        // Content title, which appears in large type at the top of the notification
        builder.setContentTitle(title);
        builder.setAutoCancel(true);
        builder.setWhen(0);
        builder.setContentText(message);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        builder.setDefaults(NotificationCompat.DEFAULT_LIGHTS | NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Will display the notification in the notification bar
        notificationManager.notify(notificationId, builder.build());
    }

}
