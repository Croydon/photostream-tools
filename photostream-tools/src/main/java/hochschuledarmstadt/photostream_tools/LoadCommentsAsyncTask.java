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
import hochschuledarmstadt.photostream_tools.model.CommentQueryResult;
import hochschuledarmstadt.photostream_tools.model.HttpResult;

class LoadCommentsAsyncTask extends BaseAsyncTask<Void, Void, CommentQueryResult> {

    private final OnCommentsResultListener callback;
    private final String installationId;
    private final int photoId;

    public LoadCommentsAsyncTask(String installationId, String uri, int photoId, OnCommentsResultListener callback){
        super(uri);
        this.installationId = installationId;
        this.photoId = photoId;
        this.callback = callback;
    }

    private static final String TAG = LoadCommentsAsyncTask.class.getName();

    @Override
    protected CommentQueryResult doInBackground(Void... params) {
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

    private CommentQueryResult getComments() throws IOException, HttpPhotoStreamException {
        final String url = String.format("%s/photostream/api/image/%s/comments", uri, photoId);
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        urlConnection.setDoInput(true);
        urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
        urlConnection.addRequestProperty("installation_id", installationId);
        final int responseCode = urlConnection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            final String result = convertStreamToString(urlConnection.getInputStream());
            CommentQueryResult commentQueryResult = new Gson().fromJson(result, CommentQueryResult.class);
            final List<Comment> comments = commentQueryResult.getComments();
            final Integer photoId = commentQueryResult.getPhotoId();
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
            return commentQueryResult;
        }else{
            throw new HttpPhotoStreamException(getHttpErrorResult(urlConnection.getErrorStream()));
        }
    }

    @Override
    protected void onPostExecute(CommentQueryResult commentQueryResult) {
        super.onPostExecute(commentQueryResult);
        if (commentQueryResult != null) {
            final int photoId = commentQueryResult.getPhotoId();
            callback.onGetComments(photoId, commentQueryResult.getComments());
        }
    }

    @Override
    protected void sendError(HttpResult httpResult) {
        callback.onGetCommentsFailed(photoId, httpResult);
    }

    public interface OnCommentsResultListener {
        void onGetComments(int photoId, List<Comment> comments);
        void onGetCommentsFailed(int photoId, HttpResult httpResult);
    }

}
