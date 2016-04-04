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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import hochschuledarmstadt.photostream_tools.model.HttpResult;

class DeletePhotoAsyncTask extends BaseAsyncTask<Void, Void, Boolean> {

    private static final String TAG = DeletePhotoAsyncTask.class.getName();
    private final OnDeletePhotoResultListener callback;
    private final int photoId;
    private final HttpDeleteExecutor executor;

    public DeletePhotoAsyncTask(HttpDeleteExecutor executor, int photoId, OnDeletePhotoResultListener callback){
        super();
        this.executor = executor;
        this.photoId = photoId;
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            deletePhoto();
            return true;
        } catch (IOException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
            postError(new HttpResult(-1, e.toString()));
        } catch (HttpPhotoStreamException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
            postError(e.getHttpResult());
        }
        return false;
    }

    private void deletePhoto() throws IOException, HttpPhotoStreamException {
        executor.execute();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (result)
            callback.onPhotoDeleted(photoId);
    }

    @Override
    protected void sendError(HttpResult httpResult) {
        callback.onPhotoDeleteFailed(photoId, httpResult);
    }

    interface OnDeletePhotoResultListener {
        void onPhotoDeleted(int photoId);
        void onPhotoDeleteFailed(int photoId, HttpResult httpResult);
    }

}
