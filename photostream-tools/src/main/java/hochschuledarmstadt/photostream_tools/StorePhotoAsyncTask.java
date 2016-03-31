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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import hochschuledarmstadt.photostream_tools.model.HttpResult;
import hochschuledarmstadt.photostream_tools.model.Photo;

class StorePhotoAsyncTask extends BaseAsyncTask<JSONObject, Void, Photo> {

    private static final String TAG = StorePhotoAsyncTask.class.getName();
    public static final String UTF_8 = "UTF-8";
    private final OnPhotoStoredCallback callback;
    private final String installationId;

    public StorePhotoAsyncTask(String installationId, String uri, OnPhotoStoredCallback callback){
        super(uri);
        this.callback = callback;
        this.installationId = installationId;
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
            postError(new HttpResult(-1, e.toString()));
        } catch (HttpPhotoStreamException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
            postError(e.getHttpResult());
        }
        return null;
    }

    private Photo uploadPhoto(JSONObject jsonObject) throws IOException, HttpPhotoStreamException {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(buildUri()).openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setDoOutput(true);
        urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
        urlConnection.addRequestProperty("installation_id", installationId);
        urlConnection.addRequestProperty("Content-Type", "application/json");
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), Charset.forName(UTF_8)));
        String s = jsonObject.toString();
        writer.write(s, 0, s.length());
        writer.flush();
        writer.close();
        final int status = urlConnection.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK){
            String newEtag = urlConnection.getHeaderField("ETag");
            callback.onNewETag(newEtag);
            String result = convertStreamToString(urlConnection.getInputStream());
            Photo photo = new Gson().fromJson(result, Photo.class);
            return photo;
        }else{
            throw new HttpPhotoStreamException(getHttpErrorResult(urlConnection.getErrorStream()));
        }
    }

    private String buildUri() {
        return String.format("%s/photostream/api/image", uri);
    }

    @Override
    protected void onPostExecute(Photo photo) {
        super.onPostExecute(photo);
        if (photo != null)
            callback.onPhotoStoreSuccess(photo);
    }

    @Override
    protected void sendError(HttpResult httpResult) {
        callback.onPhotoStoreError(httpResult);
    }

    public interface OnPhotoStoredCallback {
        void onPhotoStoreSuccess(Photo photo);
        void onPhotoStoreError(HttpResult httpResult);
        void onNewETag(String eTag);
    }

}
