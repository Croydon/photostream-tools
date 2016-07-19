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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import hochschuledarmstadt.photostream_tools.model.Comment;
import hochschuledarmstadt.photostream_tools.model.HttpError;

class StoreCommentAsyncTask extends BaseAsyncTask<Void, Void, Comment> {

    private static final String UTF_8 = "UTF-8";
    private static final String TAG = StoreCommentAsyncTask.class.getName();
    private final HttpPostExecutor executor;
    private final OnCommentSentListener callback;
    private final int photoId;
    private final String comment;

    public StoreCommentAsyncTask(HttpPostExecutor executor, int photoId, String comment, OnCommentSentListener callback){
        super();
        this.executor = executor;
        this.photoId = photoId;
        this.comment = comment;
        this.callback = callback;
    }

    @Override
    protected Comment doInBackground(Void... params) {
        try {
            return sendComment();
        } catch (IOException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
            postError(new HttpError(-1, e.toString()));
        } catch (HttpPhotoStreamException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
            postError(e.getHttpError());
        }
        return null;
    }

    private Comment sendComment() throws IOException, HttpPhotoStreamException {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", comment);
            String s = jsonObject.toString();
            HttpResponse httpResponse = executor.execute(s);
            Comment comment = new Gson().fromJson(httpResponse.getResult(), Comment.class);
            return comment;
        } catch (JSONException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
        }
        throw new IOException("JSONException");
    }

    @Override
    protected void onPostExecute(Comment comment) {
        super.onPostExecute(comment);
        if (comment != null)
            callback.onCommentSent(comment);
    }

    @Override
    protected void sendError(HttpError httpError) {
        callback.onSendCommentFailed(httpError);
    }

    interface OnCommentSentListener {
        void onCommentSent(Comment comment);
        void onSendCommentFailed(HttpError httpError);
    }
}
