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

import android.os.Bundle;

import java.util.ArrayList;

/**
 * Fragmente, die von {@link PhotoStreamFragment} erben, erhalten über diese Klasse Zugriff auf das Interface {@link IPhotoStreamClient}
 */
public abstract class PhotoStreamFragmentActivity extends PhotoStreamActivity implements ServiceStateChangedNotifier {

    private ArrayList<OnServiceStateChangedListener> serviceStateChangedListeners = new ArrayList<>();

    boolean alreadyNotified = false;

    @Override
    public void removeOnServiceStateChangedListener(OnServiceStateChangedListener onServiceStateChangedListener) {
        if (serviceStateChangedListeners.contains(onServiceStateChangedListener))
            serviceStateChangedListeners.remove(onServiceStateChangedListener);
    }

    @Override
    public void addOnServiceStateChangedListener(OnServiceStateChangedListener onServiceStateChangedListener) {
        if (!serviceStateChangedListeners.contains(onServiceStateChangedListener)) {
            serviceStateChangedListeners.add(onServiceStateChangedListener);
            if (isConnectedToService())
                onServiceStateChangedListener.onServiceConnected(getPhotoStreamClient());
        }
    }

    /**
     * Called when PhotoStreamService is available
     * @param photoStreamClient class for communication to the server
     * @param savedInstanceState from onCreate() method
     */
    @Override
    protected void onPhotoStreamServiceConnected(IPhotoStreamClient photoStreamClient, Bundle savedInstanceState) {
        notifyOnServiceConnected(photoStreamClient);
    }

    @Override
    protected void onPhotoStreamServiceDisconnected(IPhotoStreamClient photoStreamClient) {
        notifyOnServiceDisconnected(photoStreamClient);
    }

    private void notifyOnServiceConnected(IPhotoStreamClient photoStreamClient){
        if (!serviceStateChangedListeners.isEmpty()) {
            if (!alreadyNotified) {
                alreadyNotified = true;
                for (OnServiceStateChangedListener onServiceStateChangedListener : serviceStateChangedListeners)
                    onServiceStateChangedListener.onServiceConnected(photoStreamClient);
            }
        }
    }

    private void notifyOnServiceDisconnected(IPhotoStreamClient photoStreamClient){
        for (OnServiceStateChangedListener onServiceStateChangedListener : serviceStateChangedListeners)
            onServiceStateChangedListener.onServiceDisconnected(photoStreamClient);
        alreadyNotified = false;
    }
}
