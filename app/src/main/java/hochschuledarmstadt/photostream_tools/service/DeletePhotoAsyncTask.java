package hochschuledarmstadt.photostream_tools.service;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import hochschuledarmstadt.photostream_tools.log.LogLevel;
import hochschuledarmstadt.photostream_tools.log.Logger;

/**
 * Created by Andreas Schattney on 23.02.2016.
 */
public class DeletePhotoAsyncTask extends AsyncTask<Void,Void,Boolean> {

    private static final String TAG = DeletePhotoAsyncTask.class.getName();
    private final OnDeletePhotoResultListener callback;
    private final String installationId;
    private final String uri;
    private final int photoId;

    public DeletePhotoAsyncTask(String installationId, String uri, int photoId, OnDeletePhotoResultListener callback){
        this.installationId = installationId;
        this.uri = uri;
        this.photoId = photoId;
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            return deleteComment();
        } catch (IOException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
        }
        return false;
    }

    private Boolean deleteComment() throws IOException {
        final String url = String.format("%s/photostream/image/%s", uri, photoId);
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        urlConnection.setRequestMethod("DELETE");
        urlConnection.setConnectTimeout(6000);
        urlConnection.addRequestProperty("installation_id", installationId);
        return urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (result)
            callback.onPhotoDeleted(photoId);
        else
            callback.onPhotoDeleteFailed(photoId);
    }

    public interface OnDeletePhotoResultListener {
        void onPhotoDeleted(int commentId);
        void onPhotoDeleteFailed(int commentId);
    }

}
