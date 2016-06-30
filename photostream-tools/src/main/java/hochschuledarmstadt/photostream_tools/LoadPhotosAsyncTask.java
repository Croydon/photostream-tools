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

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.squareup.okhttp.HttpUrl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import hochschuledarmstadt.photostream_tools.model.HttpResult;
import hochschuledarmstadt.photostream_tools.model.Photo;
import hochschuledarmstadt.photostream_tools.model.PhotoQueryResult;

class LoadPhotosAsyncTask extends BaseAsyncTask<Void, Void, PhotoQueryResult> {

    private static final String TAG = LoadPhotosAsyncTask.class.getName();
    private final GetPhotosCallback callback;
    private final HttpGetExecutor executor;
    private final Context context;

    public LoadPhotosAsyncTask(HttpGetExecutor executor, Context context, GetPhotosCallback callback){
        super();
        this.executor = executor;
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected PhotoQueryResult doInBackground(Void... params) {
        try {
            return getPhotos();
        }catch (HttpPhotoStreamException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
            final HttpResult httpResult = e.getHttpResult();
            postError(httpResult);
        } catch (IOException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
            final HttpResult httpResult = new HttpResult(-1, e.toString());
            postError(httpResult);
        }
        return null;
    }

    private PhotoQueryResult getPhotos() throws IOException, HttpPhotoStreamException {
        HttpResponse httpResponse = executor.execute();
        int statusCode = httpResponse.getStatusCode();
        if (statusCode == HttpURLConnection.HTTP_OK){
            callback.onNewETag(executor.getEtag());
            PhotoQueryResult photoQueryResult = new Gson().fromJson(httpResponse.getResult(), PhotoQueryResult.class);
            final List<Photo> photos = photoQueryResult.getPhotos();
            for (Photo photo : photos) {
                photo.saveToImageToCache(context);
            }
            return photoQueryResult;
        }else if(statusCode == HttpURLConnection.HTTP_NOT_MODIFIED){
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    callback.onNoNewPhotosAvailable();
                }
            });
        }
        return null;
    }

    @Override
    protected void onPostExecute(PhotoQueryResult result) {
        super.onPostExecute(result);
        if (result != null) {
            callback.onPhotosResult(result);
        }
    }

    @Override
    protected void sendError(HttpResult httpResult) {
        callback.onPhotosError(httpResult);
    }


    interface GetPhotosCallback {
        void onPhotosResult(PhotoQueryResult photoQueryResult);
        void onPhotosError(HttpResult httpResult);
        void onNewETag(String eTag);
        void onNoNewPhotosAvailable();
    }

}
