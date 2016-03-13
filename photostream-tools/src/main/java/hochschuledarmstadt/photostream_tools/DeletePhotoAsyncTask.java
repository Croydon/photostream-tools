package hochschuledarmstadt.photostream_tools;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import hochschuledarmstadt.photostream_tools.model.HttpResult;

/**
 * Created by Andreas Schattney on 23.02.2016.
 */
class DeletePhotoAsyncTask extends BaseAsyncTask<Void, Void, Boolean> {

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
            deletePhoto();
            return true;
        } catch (IOException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
            postError(new HttpResult(-1, e.toString()));
        } catch (HttpPhotoStreamException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
            postError(e.getHttpResult());
        }
        return false;
    }

    private void deletePhoto() throws IOException, HttpPhotoStreamException {
        final String url = String.format("%s/photostream/image/%s", uri, photoId);
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        urlConnection.setRequestMethod("DELETE");
        urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
        urlConnection.addRequestProperty("installation_id", installationId);
        final int responseCode = urlConnection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK){
            throw new HttpPhotoStreamException(getHttpErrorResult(urlConnection.getErrorStream()));
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (result)
            callback.onPhotoDeleted(photoId);
    }

    @Override
    protected void sendError(HttpResult httpResult) {
        callback.onPhotoDeleteFailed(photoId, httpResult);
    }

    public interface OnDeletePhotoResultListener {
        void onPhotoDeleted(int photoId);
        void onPhotoDeleteFailed(int photoId, HttpResult httpResult);
    }

}
