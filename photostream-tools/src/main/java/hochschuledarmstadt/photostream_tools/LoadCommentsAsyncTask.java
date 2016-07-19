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
import java.util.List;

import hochschuledarmstadt.photostream_tools.model.Comment;
import hochschuledarmstadt.photostream_tools.model.HttpError;
import hochschuledarmstadt.photostream_tools.model.CommentsQueryResult;

class LoadCommentsAsyncTask extends BaseAsyncTask<Void, Void, CommentsQueryResult> {

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
    protected CommentsQueryResult doInBackground(Void... params) {
        try {
            return getComments();
        } catch (IOException e) {
            postError(new HttpError(-1, e.toString()));
            Logger.log(TAG, LogLevel.ERROR, e.toString());
        } catch (HttpPhotoStreamException e) {
            postError(e.getHttpError());
            Logger.log(TAG, LogLevel.ERROR, e.toString());
        }
        return null;
    }

    private CommentsQueryResult getComments() throws IOException, HttpPhotoStreamException {
        HttpResponse httpResponse = executor.execute();
        CommentsQueryResult commentsQueryResult = null;
        if (httpResponse.getStatusCode() == HttpResponse.STATUS_OK) {
            commentsQueryResult = new Gson().fromJson(httpResponse.getResult(), CommentsQueryResult.class);
            final List<Comment> comments = commentsQueryResult.getComments();
            final Integer photoId = commentsQueryResult.getPhotoId();
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
            String newEtag = executor.getEtag();
            if (newEtag != null)
                callback.onNewEtag(photoId, commentsQueryResult, newEtag);

        }else if(httpResponse.getStatusCode() == HttpResponse.STATUS_CONTENT_NOT_MODIFIED){
            commentsQueryResult = callback.onCommentsNotModified(photoId);
        }

        return commentsQueryResult;
    }

    @Override
    protected void onPostExecute(CommentsQueryResult commentsQueryResult) {
        super.onPostExecute(commentsQueryResult);
        if (commentsQueryResult != null) {
            final int photoId = commentsQueryResult.getPhotoId();
            callback.onGetComments(photoId, commentsQueryResult.getComments());
        }
    }

    @Override
    protected void sendError(HttpError httpError) {
        callback.onGetCommentsFailed(photoId, httpError);
    }

    interface OnCommentsResultListener {
        void onGetComments(int photoId, List<Comment> comments);
        void onNewEtag(int photoId, CommentsQueryResult result, String eTag);
        CommentsQueryResult onCommentsNotModified(int photoId);
        void onGetCommentsFailed(int photoId, HttpError httpError);
    }

}
