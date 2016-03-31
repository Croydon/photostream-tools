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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import hochschuledarmstadt.photostream_tools.callback.OnPhotosReceivedListener;

public abstract class PhotoStreamActivity extends AppCompatActivity implements ServiceConnection {

    private static final String TAG = PhotoStreamActivity.class.getName();
    private PhotoStreamClient photoStreamClient;
    private boolean bound;
    private Bundle refSavedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.refSavedInstanceState = savedInstanceState;
        if (savedInstanceState == null){
            startService(new Intent(this, PhotoStreamService.class));
        }
    }

    protected abstract void onPhotoStreamServiceConnected(IPhotoStreamClient photoStreamClient, Bundle savedInstanceState);
    protected abstract void onPhotoStreamServiceDisconnected(IPhotoStreamClient photoStreamClient);

    @Override
    protected void onResume() {
        super.onResume();
        if (!bound)
            bindService(new Intent(this, PhotoStreamService.class), this, Context.BIND_AUTO_CREATE);
        if (isConnectedToService())
            photoStreamClient.registerActivity(this);
    }

    @Override
    protected void onStop() {

        if (isConnectedToService()) {
            photoStreamClient.unregisterActivity(this);
        }

        super.onStop();

        if (isConnectedToService()){
            if (isFinishing() && this instanceof OnPhotosReceivedListener) {
                photoStreamClient.resetEtag();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (photoStreamClient != null) {
            photoStreamClient.unregisterActivity(this);
            onPhotoStreamServiceDisconnected(photoStreamClient);
        }
        try {
            unbindService(this);
        }catch(Exception e){
            Logger.log(TAG, LogLevel.ERROR, e.toString());
        }
        bound = false;
        refSavedInstanceState = null;
        super.onDestroy();
    }

    protected boolean isConnectedToService() {
        return photoStreamClient != null && bound;
    }

    protected IPhotoStreamClient getPhotoStreamClient() {
        return photoStreamClient;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        photoStreamClient = ((PhotoStreamService.PhotoStreamServiceBinder)service).getClient();
        photoStreamClient.registerActivity(this);
        bound = true;
        onPhotoStreamServiceConnected(photoStreamClient, refSavedInstanceState);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        photoStreamClient.unregisterActivity(this);
        bound = false;
        photoStreamClient = null;
    }
}
