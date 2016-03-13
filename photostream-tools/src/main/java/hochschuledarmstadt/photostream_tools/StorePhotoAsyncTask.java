package hochschuledarmstadt.photostream_tools;

import android.os.AsyncTask;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import hochschuledarmstadt.photostream_tools.model.HttpResult;
import hochschuledarmstadt.photostream_tools.model.Photo;

/**
 * Created by Andreas Schattney on 19.02.2016.
 */
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
            String result = convertStreamToString(urlConnection.getInputStream());
            Photo photo = new Gson().fromJson(result, Photo.class);
            return photo;
        }else{
            throw new HttpPhotoStreamException(getHttpErrorResult(urlConnection.getErrorStream()));
        }
    }

    private String buildUri() {
        return String.format("%s/photostream/image", uri);
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
    }

}
