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

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import hochschuledarmstadt.photostream_tools.model.Comment;
import hochschuledarmstadt.photostream_tools.model.HttpResult;
import hochschuledarmstadt.photostream_tools.model.LoadCommentsQueryResult;

class LoadCommentsAsyncTask extends BaseAsyncTask<Void, Void, LoadCommentsQueryResult> {

    private final OnCommentsResultListener callback;
    private final HttpGetExecutor executor;
    private final int photoId;

    public LoadCommentsAsyncTask(HttpGetExecutor executor, int photoId, OnCommentsResultListener callback){
        super();
        this.executor = executor;
        this.photoId = photoId;
        this.callback = callback;
    }

    private static final String TAG = LoadCommentsAsyncTask.class.getName();

    @Override
    protected LoadCommentsQueryResult doInBackground(Void... params) {
        try {
            return getComments();
        } catch (IOException e) {
            postError(new HttpResult(-1, e.toString()));
            Logger.log(TAG, LogLevel.ERROR, e.toString());
        } catch (HttpPhotoStreamException e) {
            postError(e.getHttpResult());
            Logger.log(TAG, LogLevel.ERROR, e.toString());
        }
        return null;
    }

    private LoadCommentsQueryResult getComments() throws IOException, HttpPhotoStreamException {
        HttpResponse httpResponse = executor.execute();
        LoadCommentsQueryResult loadCommentsQueryResult = new Gson().fromJson(httpResponse.getResult(), LoadCommentsQueryResult.class);
        final List<Comment> comments = loadCommentsQueryResult.getComments();
        final Integer photoId = loadCommentsQueryResult.getPhotoId();
        for (Comment comment : comments) {
            try {
                Field field = comment.getClass().getDeclaredField("photoId");
                field.setAccessible(true);
                field.set(comment, photoId);
            } catch (IllegalAccessException e) {
                Logger.log(TAG, LogLevel.ERROR, e.toString());
            } catch (NoSuchFieldException e) {
                Logger.log(TAG, LogLevel.ERROR, e.toString());
            }
        }
        return loadCommentsQueryResult;
    }

    @Override
    protected void onPostExecute(LoadCommentsQueryResult loadCommentsQueryResult) {
        super.onPostExecute(loadCommentsQueryResult);
        if (loadCommentsQueryResult != null) {
            final int photoId = loadCommentsQueryResult.getPhotoId();
            callback.onGetComments(photoId, loadCommentsQueryResult.getComments());
        }
    }

    @Override
    protected void sendError(HttpResult httpResult) {
        callback.onGetCommentsFailed(photoId, httpResult);
    }

    interface OnCommentsResultListener {
        void onGetComments(int photoId, List<Comment> comments);
        void onGetCommentsFailed(int photoId, HttpResult httpResult);
    }

}
