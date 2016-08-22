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

package hochschuledarmstadt.photostream_tools.examples.examples.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import hochschuledarmstadt.photostream_tools.IPhotoStreamClient;
import hochschuledarmstadt.photostream_tools.model.Photo;

/**
 * Deklaration für Starten des BroadcastReceivers im Manifest deklariert (AndroidManifest.xml)
 */
public class NotificationBroadcastReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        if (isNewPhotoIntent(intent)){
            // Das empfangene Photo aus dem Intent referenzieren (ist in diesem Beispiel aber nicht vorhanden)
            Photo receivedPhoto = intent.getParcelableExtra(IPhotoStreamClient.INTENT_KEY_PHOTO);
            Toast.makeText(context, "Intent erhalten im BroadcastReceiver!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNewPhotoIntent(Intent intent) {
        return intent.getAction().equals(IPhotoStreamClient.INTENT_ACTION_NEW_PHOTO_AVAILABLE);
    }
}
