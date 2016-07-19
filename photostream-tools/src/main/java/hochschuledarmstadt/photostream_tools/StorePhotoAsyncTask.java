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

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;

import hochschuledarmstadt.photostream_tools.model.HttpError;
import hochschuledarmstadt.photostream_tools.model.Photo;

class StorePhotoAsyncTask extends BaseAsyncTask<JSONObject, Void, Photo> {

    private static final String TAG = StorePhotoAsyncTask.class.getName();
    private final OnPhotoStoredCallback callback;
    private final HttpPostExecutor executor;

    public StorePhotoAsyncTask(HttpPostExecutor executor, OnPhotoStoredCallback callback){
        super();
        this.executor = executor;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Photo doInBackground(JSONObject... params) {
        try {
            return uploadPhoto(params[0]);
        } catch (IOException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
            postError(new HttpError(-1, e.toString()));
        } catch (HttpPhotoStreamException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
            postError(e.getHttpError());
        }
        return null;
    }

    private Photo uploadPhoto(JSONObject jsonObject) throws IOException, HttpPhotoStreamException {
        HttpResponse httpResponse = executor.execute(jsonObject.toString());
        String newEtag = executor.geteTag();
        if (newEtag != null)
            callback.onNewETag(newEtag);
        Photo photo = new Gson().fromJson(httpResponse.getResult(), Photo.class);
        return photo;
    }

    @Override
    protected void onPostExecute(Photo photo) {
        super.onPostExecute(photo);
        if (photo != null)
            callback.onPhotoStoreSuccess(photo);
    }

    @Override
    protected void sendError(HttpError httpError) {
        callback.onPhotoStoreError(httpError);
    }

    interface OnPhotoStoredCallback {
        void onPhotoStoreSuccess(Photo photo);
        void onPhotoStoreError(HttpError httpError);
        void onNewETag(String eTag);
    }

}
