package hochschuledarmstadt.photostream_tools;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import hochschuledarmstadt.photostream_tools.model.HttpResult;

/**
 * Created by Andreas Schattney on 23.02.2016.
 */
class DeleteCommentAsyncTask extends BaseAsyncTask<Void,Void, Boolean> {

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
            deleteComment();
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

    private void deleteComment() throws IOException, HttpPhotoStreamException {
        final String url = String.format("%s/photostream/comment/%s", uri, commentId);
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
            callback.onCommentDeleted(commentId);
    }

    @Override
    protected void sendError(HttpResult httpResult) {
        callback.onCommentDeleteFailed(commentId, httpResult);
    }

    public interface OnDeleteCommentResultListener {
        void onCommentDeleted(int commentId);
        void onCommentDeleteFailed(int commentId, HttpResult httpResult);
    }

}
