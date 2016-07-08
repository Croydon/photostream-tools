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

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import hochschuledarmstadt.photostream_tools.model.HttpResult;
import hochschuledarmstadt.photostream_tools.model.Photo;
import hochschuledarmstadt.photostream_tools.model.PhotoQueryResult;

class SearchPhotosAsyncTask extends BaseAsyncTask<Void, Void, PhotoQueryResult> {

    private static final String TAG = SearchPhotosAsyncTask.class.getName();
    private final OnSearchPhotosResultCallback callback;
    private final HttpGetExecutor executor;
    private final Context context;
    private final String query;

    public SearchPhotosAsyncTask(HttpGetExecutor executor, Context context, String query, OnSearchPhotosResultCallback callback){
        super();
        this.executor = executor;
        this.context = context;
        this.query = query;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected PhotoQueryResult doInBackground(Void... params) {
        try {
            return searchPhotos();
        } catch (IOException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
            postError(new HttpResult(-1, e.toString()));
        } catch (HttpPhotoStreamException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
            postError(e.getHttpResult());
        }
        return null;
    }

    private PhotoQueryResult searchPhotos() throws IOException, HttpPhotoStreamException {
        HttpResponse httpResponse = executor.execute();
        PhotoQueryResult photoQueryResult = new Gson().fromJson(httpResponse.getResult(), PhotoQueryResult.class);
        final List<Photo> photos = photoQueryResult.getPhotos();
        for (Photo photo : photos){
            photo.cacheImage(context);
        }
        return photoQueryResult;
    }

    @Override
    protected void onPostExecute(PhotoQueryResult result) {
        super.onPostExecute(result);
        if (result != null) {
            callback.onSearchPhotosResult(result);
        }
    }

    @Override
    protected void sendError(HttpResult httpResult) {
        callback.onSearchPhotosError(httpResult);
    }

    interface OnSearchPhotosResultCallback {
        void onSearchPhotosResult(PhotoQueryResult photoQueryResult);
        void onSearchPhotosError(HttpResult httpResult);
    }

}
