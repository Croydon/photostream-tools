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

import hochschuledarmstadt.photostream_tools.model.HttpError;

class DeleteCommentAsyncTask extends BaseAsyncTask<Void,Void, Boolean> {

    private static final String TAG = DeleteCommentAsyncTask.class.getName();
    private final OnDeleteCommentResultListener callback;
    private final HttpDeleteExecutor executor;
    private final int commentId;

    public DeleteCommentAsyncTask(HttpDeleteExecutor executor, int commentId, OnDeleteCommentResultListener callback){
        super();
        this.executor = executor;
        this.commentId = commentId;
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            deleteComment();
            return true;
        } catch (IOException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
            postError(new HttpError(-1, e.toString()));
        } catch (HttpPhotoStreamException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
            postError(e.getHttpError());
        }
        return false;
    }

    private void deleteComment() throws IOException, HttpPhotoStreamException {
        executor.execute();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (result)
            callback.onCommentDeleted(commentId);
    }

    @Override
    protected void sendError(HttpError httpError) {
        callback.onCommentDeleteFailed(commentId, httpError);
    }

    interface OnDeleteCommentResultListener {
        void onCommentDeleted(int commentId);
        void onCommentDeleteFailed(int commentId, HttpError httpError);
    }

}
