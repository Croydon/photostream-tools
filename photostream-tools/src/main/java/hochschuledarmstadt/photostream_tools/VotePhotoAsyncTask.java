package hochschuledarmstadt.photostream_tools;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by Andreas Schattney on 23.02.2016.
 */
abstract class VotePhotoAsyncTask extends AsyncTask<Void, Void, JSONObject> {

    private static final String TAG = VotePhotoAsyncTask.class.getName();
    private final OnVotePhotoResultListener callback;
    private final String installationId;
    private final int photoId;
    private final VoteTable voteTable;
    private final String uri;

    public VotePhotoAsyncTask(VoteTable voteTable, String installationId, String uri, int photoId, OnVotePhotoResultListener callback){
        this.voteTable = voteTable;
        this.installationId = installationId;
        this.uri = uri;
        this.photoId = photoId;
        this.callback = callback;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        try {
            return votePhoto();
        } catch (IOException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
        } catch (JSONException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
        }
        return null;
    }

    private JSONObject votePhoto() throws IOException, JSONException {
        final String url = buildUrl(uri, photoId);
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        urlConnection.setRequestMethod("PUT");
        urlConnection.setConnectTimeout(6000);
        urlConnection.addRequestProperty("installation_id", installationId);
        InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream(), Charset.forName("UTF-8"));
        char[] buffer = new char[1024];
        StringBuilder stringBuilder = new StringBuilder();
        int read;
        while((read = reader.read(buffer, 0, buffer.length)) != -1){
            stringBuilder.append(buffer,0,read);
        }
        return new JSONObject(stringBuilder.toString());
    }

    protected abstract String buildUrl(String uri, int photoId);

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
            }else{
                callback.onPhotoVoteFailed(photoId);
            }
        } catch (JSONException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
            callback.onError(photoId, e);
        }
    }

    private void saveUserVotedPhoto(int photoId) {
        voteTable.openDatabase();
        voteTable.insertVote(photoId);
        voteTable.closeDatabase();
    }

    public interface OnVotePhotoResultListener {
        void onPhotoVoted(int photoId, int newVoteCount);
        void onPhotoVoteFailed(int photoId);
        void onError(int photoId, Exception e);
        void onPhotoAlreadyVoted(int photoId, int votecount);
    }
}
