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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Kommunikationsschnittstelle zu dem PhotoStream Server
 */
public final class PhotoStreamService extends Service {

    private static final String TAG = PhotoStreamService.class.getName();
    private static final String PHOTOSTREAM_URL_MANIFEST_KEY = "PHOTOSTREAM_URL";

    private IBinder photoStreamServiceBinder = new PhotoStreamServiceBinder();
    private PhotoStreamClient photoStreamClient;

    public class PhotoStreamServiceBinder extends Binder {
        public PhotoStreamClient getClient() {
            return photoStreamClient;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        final String photoStreamUrl = loadPhotoStreamUrlFromManifest();
        final Context context = getApplicationContext();
        final String uniqueAndroidId = getUniqueAndroidId();

        UrlBuilder urlBuilder = new UrlBuilder(photoStreamUrl);
        DbConnection dbConnection = DbConnection.getInstance(context);
        WebSocketClient webSocketClient = new WebSocketClientImpl(photoStreamUrl, uniqueAndroidId);
        PhotoStreamCallbackContainer container = new PhotoStreamCallbackContainer();
        HttpExecutorFactory httpExecutorFactory = new HttpExecutorFactoryImpl(uniqueAndroidId);

        photoStreamClient = new PhotoStreamClient(context, urlBuilder, dbConnection, webSocketClient, container, httpExecutorFactory);
        photoStreamClient.bootstrap();

    }

    @NonNull
    private String loadPhotoStreamUrlFromManifest() {
        final String photoStreamUrl = readUrlFromManifest();
        if (photoStreamUrl == null){
            throw new IllegalStateException(getString(R.string.no_url_provided));
        }
        return photoStreamUrl;
    }

    private String readUrlFromManifest() {
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            String url = bundle.getString(PHOTOSTREAM_URL_MANIFEST_KEY);
            return url;
        } catch (PackageManager.NameNotFoundException e) {
            Logger.log(TAG, LogLevel.ERROR, "Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            Logger.log(TAG, LogLevel.ERROR, "Failed to load meta-data, NullPointer: " + e.getMessage());
        }
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private String getUniqueAndroidId(){
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @Override
    public void onDestroy() {
        photoStreamClient.destroy();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return photoStreamServiceBinder;
    }

}
