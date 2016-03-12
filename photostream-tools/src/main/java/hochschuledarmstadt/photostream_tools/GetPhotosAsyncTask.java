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

import hochschuledarmstadt.photostream_tools.model.Photo;
import hochschuledarmstadt.photostream_tools.model.PhotoQueryResult;

/**
 * Created by Andreas Schattney on 19.02.2016.
 */
class GetPhotosAsyncTask extends AsyncTask<Void, Void, PhotoQueryResult> {

    private static final String TAG = GetPhotosAsyncTask.class.getName();
    private final GetPhotosCallback callback;
    private final Context context;
    private final String installationId;
    private final String uri;
    private final int page;

    public GetPhotosAsyncTask(Context context, String installationId, String uri, int page, GetPhotosCallback callback){
        this.context = context;
        this.installationId = installationId;
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
            return getPhotos();
        } catch (IOException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
        }
        return null;
    }

    protected String buildUrl(String uri, int page){
        final String url = String.format("%s/photostream/stream/?page=%s", uri, page);
        return url;
    }

    private PhotoQueryResult getPhotos() throws IOException {
        final String url = buildUrl(uri, page);
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        urlConnection.setDoInput(true);
        urlConnection.setConnectTimeout(6000);
        urlConnection.addRequestProperty("installation_id", installationId);
        InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream(), Charset.forName("UTF-8"));
        char[] buffer = new char[4096];
        StringBuilder stringBuilder = new StringBuilder();
        int read;
        while((read = reader.read(buffer, 0, buffer.length)) != -1){
            stringBuilder.append(buffer,0,read);
        }
        Gson gson = new Gson();
        PhotoQueryResult photoQueryResult = gson.fromJson(stringBuilder.toString(), PhotoQueryResult.class);
        final List<Photo> photos = photoQueryResult.getPhotos();
        for (Photo photo : photos){
            photo.saveToImageToCache(context);
        }
        return photoQueryResult;
    }

    @Override
    protected void onPostExecute(PhotoQueryResult result) {
        super.onPostExecute(result);
        if (result != null) {
            callback.OnPhotosResult(result);
        }else{
            callback.onError();
        }
    }

    public interface GetPhotosCallback {
        void OnPhotosResult(PhotoQueryResult photoQueryResult);
        void onError();
    }

}
