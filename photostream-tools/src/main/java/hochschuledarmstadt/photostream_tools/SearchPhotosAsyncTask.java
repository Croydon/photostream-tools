package hochschuledarmstadt.photostream_tools;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import hochschuledarmstadt.photostream_tools.model.HttpResult;
import hochschuledarmstadt.photostream_tools.model.Photo;
import hochschuledarmstadt.photostream_tools.model.PhotoQueryResult;

/**
 * Created by Andreas Schattney on 10.03.2016.
 */
class SearchPhotosAsyncTask extends BaseAsyncTask<Void, Void, PhotoQueryResult> {

    private static final String TAG = GetPhotosAsyncTask.class.getName();
    private final OnSearchPhotosResultCallback callback;
    private final Context context;
    private final String installationId;
    private final String uri;
    private final int page;
    private final String query;

    private static final PhotoQueryResult EMPTY = new PhotoQueryResult();

    public SearchPhotosAsyncTask(Context context, String installationId, String uri, String query, int page, OnSearchPhotosResultCallback callback){
        this.context = context;
        this.installationId = installationId;
        this.query = query;
        this.uri = uri;
        this.page = page;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected PhotoQueryResult doInBackground(Void... params) {
        try {
            return searchPhotos();
        } catch (IOException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
            postError(new HttpResult(-1, e.toString()));
        } catch (HttpPhotoStreamException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
            postError(e.getHttpResult());
        }
        return EMPTY;
    }

    protected String buildUrl(String uri, int page){
        return String.format("%s/photostream/search/?q=%s&page=%s", uri, query, page);
    }

    private PhotoQueryResult searchPhotos() throws IOException, HttpPhotoStreamException {
        final String url = buildUrl(uri, page);
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        urlConnection.setDoInput(true);
        urlConnection.setConnectTimeout(6000);
        urlConnection.addRequestProperty("installation_id", installationId);
        final int responseCode = urlConnection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK){
            String result = convertStreamToString(urlConnection.getInputStream());
            PhotoQueryResult photoQueryResult = new Gson().fromJson(result, PhotoQueryResult.class);
            final List<Photo> photos = photoQueryResult.getPhotos();
            for (Photo photo : photos){
                photo.saveToImageToCache(context);
            }
            return photoQueryResult;
        }else{
            throw new HttpPhotoStreamException(getHttpErrorResult(urlConnection.getErrorStream()));
        }
    }

    @Override
    protected void onPostExecute(PhotoQueryResult result) {
        super.onPostExecute(result);
        if (result != EMPTY) {
            callback.onSearchPhotosResult(result);
        }
    }

    @Override
    protected void sendError(HttpResult httpResult) {
        callback.onSearchPhotosError(httpResult);
    }

    public interface OnSearchPhotosResultCallback {
        void onSearchPhotosResult(PhotoQueryResult photoQueryResult);
        void onSearchPhotosError(HttpResult httpResult);
    }

}
