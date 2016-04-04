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

import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import hochschuledarmstadt.photostream_tools.model.HttpResult;

abstract class LikeOrDislikePhotoAsyncTask extends BaseAsyncTask<Void, Void, Boolean> {

    private static final String TAG = LikeOrDislikePhotoAsyncTask.class.getName();
    private final OnVotePhotoResultListener callback;
    private final int photoId;
    private final LikeTable likeTable;
    private final HttpPutExecutor executor;

    public LikeOrDislikePhotoAsyncTask(HttpPutExecutor executor, LikeTable likeTable, int photoId, OnVotePhotoResultListener callback){
        super();
        this.executor = executor;
        this.likeTable = likeTable;
        this.photoId = photoId;
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            likeOrDislikePhoto();
            return true;
        } catch (IOException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
            postError(new HttpResult(-1, e.toString()));
        } catch (JSONException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
        } catch (HttpPhotoStreamException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
            postError(e.getHttpResult());
        }
        return false;
    }

    private void likeOrDislikePhoto() throws IOException, JSONException, HttpPhotoStreamException {
        executor.execute();
    }

    protected abstract void saveUserLikedOrDislikedPhoto(LikeTable likeTable, int photoId);
    protected abstract void sendResult(OnVotePhotoResultListener callback, int photoId);

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if(result){
            saveUserLikedOrDislikedPhoto(likeTable, photoId);
            sendResult(callback, photoId);
        }
    }

    @Override
    protected void sendError(HttpResult httpResult) {
        callback.onPhotoLikeFailed(photoId, httpResult);
    }

    interface OnVotePhotoResultListener {
        void onPhotoLiked(int photoId);
        void onPhotoDisliked(int photoId);
        void onPhotoLikeFailed(int photoId, HttpResult httpResult);
    }
}
