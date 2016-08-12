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
import android.util.Log;

/**
 * Kommunikationsschnittstelle zu dem PhotoStream Server
 */
public final class PhotoStreamService extends Service implements PhotoStreamCallbackContainer.OnNoActivitesRemainingListener {

    private static final String TAG = PhotoStreamService.class.getName();
    private static final String PHOTOSTREAM_URL_MANIFEST_KEY = "PHOTOSTREAM_URL";
    private static final String PAGE_SIZE_MANIFEST_KEY = "PAGE_SIZE";
    private static final int DEFAULT_PAGE_SIZE = 5;
    private IBinder photoStreamServiceBinder = new PhotoStreamServiceBinder();
    private PhotoStreamClientImpl photoStreamClientImpl;

    @Override
    public void onNoActivitesRegistered() {
        stopSelf();
    }

    public class PhotoStreamServiceBinder extends Binder {
        public PhotoStreamClientImpl getClient() {
            return photoStreamClientImpl;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        final String photoStreamUrl = loadPhotoStreamUrlFromManifest();
        final int photoPageSize = loadPhotoPageSizeFromManifest();
        final Context context = getApplicationContext();
        final String uniqueAndroidId = getUniqueAndroidId();

        UrlBuilder urlBuilder = new UrlBuilder(photoStreamUrl, photoPageSize);
        String formatPhotoContentApiUrl = urlBuilder.getFormatPhotoContentApiUrl();
        HttpImageLoaderFactory imageLoaderFactory = new HttpImageLoaderFactory(formatPhotoContentApiUrl);
        ImageCacherFactory imageCacherFactory = new ImageCacherFactory(context);
        DbConnection db = DbConnection.getInstance(context);
        WebSocketClient wsClient = new WebSocketClientImpl(photoStreamUrl, uniqueAndroidId, new ImageCacher(getApplicationContext()), new HttpImageLoader(formatPhotoContentApiUrl));
        HttpExecutorFactory httpFactory = new HttpExecutorFactoryImpl(uniqueAndroidId);

        photoStreamClientImpl = new PhotoStreamClientImpl(context, urlBuilder, imageLoaderFactory, imageCacherFactory, db, wsClient, httpFactory);
        photoStreamClientImpl.setOnNoActivitiesRemainingListener(this);
        photoStreamClientImpl.bootstrap();

        Log.d(PhotoStreamService.class.getName(), "service created");

    }

    private int loadPhotoPageSizeFromManifest() {
        try {
            Bundle bundle = loadMetaDataFromManifest();
            int pageSize = bundle.getInt(PAGE_SIZE_MANIFEST_KEY, DEFAULT_PAGE_SIZE);
            if (pageSize <= 1)
                pageSize = DEFAULT_PAGE_SIZE;
            return pageSize;
        } catch (PackageManager.NameNotFoundException e) {
            Logger.log(TAG, LogLevel.ERROR, "Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            Logger.log(TAG, LogLevel.ERROR, "Failed to load meta-data, NullPointer: " + e.getMessage());
        }
        return DEFAULT_PAGE_SIZE;
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
            Bundle bundle = loadMetaDataFromManifest();
            String url = bundle.getString(PHOTOSTREAM_URL_MANIFEST_KEY);
            return url;
        } catch (PackageManager.NameNotFoundException e) {
            Logger.log(TAG, LogLevel.ERROR, "Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            Logger.log(TAG, LogLevel.ERROR, "Failed to load meta-data, NullPointer: " + e.getMessage());
        }
        return null;
    }

    private Bundle loadMetaDataFromManifest() throws PackageManager.NameNotFoundException {
        ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
        return ai.metaData;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    private String getUniqueAndroidId(){
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @Override
    public void onDestroy() {
        Log.d(PhotoStreamService.class.getName(), "service destroyed");
        photoStreamClientImpl.destroy();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return photoStreamServiceBinder;
    }

}
