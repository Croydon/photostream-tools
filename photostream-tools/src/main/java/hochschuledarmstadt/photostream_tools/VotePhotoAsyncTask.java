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
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import hochschuledarmstadt.photostream_tools.model.HttpResult;

abstract class VotePhotoAsyncTask extends BaseAsyncTask<Void, Void, JSONObject> {

    private static final String TAG = VotePhotoAsyncTask.class.getName();
    private final OnVotePhotoResultListener callback;
    private final String installationId;
    private final int photoId;
    private final VoteTable voteTable;

    public VotePhotoAsyncTask(VoteTable voteTable, String installationId, String uri, int photoId, OnVotePhotoResultListener callback){
        super(uri);
        this.voteTable = voteTable;
        this.installationId = installationId;
        this.photoId = photoId;
        this.callback = callback;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        try {
            return votePhoto();
        } catch (IOException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
            postError(new HttpResult(-1, e.toString()));
        } catch (JSONException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
        } catch (HttpPhotoStreamException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
            postError(e.getHttpResult());
        }
        return null;
    }

    private JSONObject votePhoto() throws IOException, JSONException, HttpPhotoStreamException {
        final String url = buildUri(uri, photoId);
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        urlConnection.setRequestMethod("PUT");
        urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
        urlConnection.addRequestProperty("installation_id", installationId);
        if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
            String result = convertStreamToString(urlConnection.getInputStream());
            return new JSONObject(result);
        }else{
            throw new HttpPhotoStreamException(getHttpErrorResult(urlConnection.getErrorStream()));
        }
    }

    protected abstract String buildUri(String uri, int photoId);

    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        try {
            if (result != null) {
                saveUserVotedPhoto(photoId);
                int photo_id = result.getInt("photo_id");
                int votecount = result.getInt("votecount");
                saveUserVotedPhoto(photo_id);
                if (!result.has("already_upvoted")) {
                    callback.onPhotoVoted(photo_id, votecount);
                }else
                    callback.onPhotoAlreadyVoted(photo_id, votecount);
            }
        } catch (JSONException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
            callback.onPhotoVoteError(photoId, e);
        }
    }

    private void saveUserVotedPhoto(int photoId) {
        voteTable.openDatabase();
        voteTable.insertVote(photoId);
        voteTable.closeDatabase();
    }

    @Override
    protected void sendError(HttpResult httpResult) {
        callback.onPhotoVoteFailed(photoId, httpResult);
    }

    public interface OnVotePhotoResultListener {
        void onPhotoVoted(int photoId, int newVoteCount);
        void onPhotoVoteFailed(int photoId, HttpResult httpResult);
        void onPhotoVoteError(int photoId, Exception e);
        void onPhotoAlreadyVoted(int photoId, int votecount);
    }
}
