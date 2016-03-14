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

import android.os.AsyncTask;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import hochschuledarmstadt.photostream_tools.model.Comment;
import hochschuledarmstadt.photostream_tools.model.HttpResult;

class StoreCommentAsyncTask extends BaseAsyncTask<Void, Void, Comment> {

    private static final String UTF_8 = "UTF-8";
    private static final String TAG = StoreCommentAsyncTask.class.getName();
    private final String installationId;
    private final OnCommentSentListener callback;
    private final int photoId;
    private final String comment;

    public StoreCommentAsyncTask(String installationId, String uri, int photoId, String comment, OnCommentSentListener callback){
        super(uri);
        this.installationId = installationId;
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
            postError(new HttpResult(-1, e.toString()));
        } catch (HttpPhotoStreamException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
            postError(e.getHttpResult());
        }
        return null;
    }

    private Comment sendComment() throws IOException, HttpPhotoStreamException {
        final String url = buildUri();
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
        urlConnection.addRequestProperty("installation_id", installationId);
        urlConnection.addRequestProperty("Content-Type", "application/json");
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), Charset.forName(UTF_8)));

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", comment);
            String s = jsonObject.toString();
            writer.write(s, 0, s.length());
            writer.flush();
        } catch (JSONException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
        }finally{
            writer.close();
        }

        int status = urlConnection.getResponseCode();
        boolean success = (status == HttpURLConnection.HTTP_OK);
        if (success){
            String result = convertStreamToString(urlConnection.getInputStream());
            Comment comment = new Gson().fromJson(result.toString(), Comment.class);
            return comment;
        }else{
            throw new HttpPhotoStreamException(getHttpErrorResult(urlConnection.getErrorStream()));
        }
    }

    private String buildUri() {
        return String.format("%s/photostream/image/%s/comment", uri, photoId);
    }

    @Override
    protected void onPostExecute(Comment comment) {
        super.onPostExecute(comment);
        if (comment != null)
            callback.onCommentSent(comment);
    }

    @Override
    protected void sendError(HttpResult httpResult) {
        callback.onSendCommentFailed(httpResult);
    }

    public interface OnCommentSentListener {
        void onCommentSent(Comment comment);
        void onSendCommentFailed(HttpResult httpResult);
    }
}
