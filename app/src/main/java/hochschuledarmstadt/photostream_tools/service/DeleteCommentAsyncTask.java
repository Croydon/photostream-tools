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
public class DeleteCommentAsyncTask extends AsyncTask<Void,Void,Boolean> {

    private static final String TAG = DeleteCommentAsyncTask.class.getName();
    private final OnDeleteCommentResultListener callback;
    private final String installationId;

    private final int commentId;
    private final String uri;

    public DeleteCommentAsyncTask(String installationId, String uri, int commentId, OnDeleteCommentResultListener callback){
        this.installationId = installationId;
        this.uri = uri;
        this.commentId = commentId;
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
        final String url = String.format("%s/photostream/comment/%s", uri, commentId);
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
            callback.onCommentDeleted(commentId);
        else
            callback.onCommentDeleteFailed(commentId);
    }

    public interface OnDeleteCommentResultListener {
        void onCommentDeleted(int commentId);
        void onCommentDeleteFailed(int commentId);
    }

}
