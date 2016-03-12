package hochschuledarmstadt.photostream_tools;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import hochschuledarmstadt.photostream_tools.model.Comment;
import hochschuledarmstadt.photostream_tools.model.CommentQueryResult;

/**
 * Created by Andreas Schattney on 23.02.2016.
 */
class GetCommentsAsyncTask extends AsyncTask<Void, Void, CommentQueryResult> {

    private final OnCommentsResultListener callback;
    private final String installationId;
    private final int photoId;
    private final String uri;

    public GetCommentsAsyncTask(String installationId, String uri, int photoId, OnCommentsResultListener callback){
        this.installationId = installationId;
        this.uri = uri;
        this.photoId = photoId;
        this.callback = callback;
    }

    private static final String TAG = GetCommentsAsyncTask.class.getName();

    @Override
    protected CommentQueryResult doInBackground(Void... params) {
        try {
            return getComments();
        } catch (IOException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
        }
        return null;
    }

    private CommentQueryResult getComments() throws IOException {
        final String url = String.format("%s/photostream/image/%s/comments", uri, photoId);
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
        CommentQueryResult commentQueryResult = gson.fromJson(stringBuilder.toString(), CommentQueryResult.class);
        final List<Comment> comments = commentQueryResult.getComments();
        final Integer photoId = commentQueryResult.getPhotoId();
        for (Comment comment : comments){
            try {
                Field field = comment.getClass().getDeclaredField("photoId");
                field.setAccessible(true);
                field.set(comment, photoId);
            } catch (IllegalAccessException e) {
                Logger.log(TAG, LogLevel.ERROR, e.toString());
            } catch (NoSuchFieldException e) {
                Logger.log(TAG, LogLevel.ERROR, e.toString());
            }
        }
        return commentQueryResult;
    }

    @Override
    protected void onPostExecute(CommentQueryResult commentQueryResult) {
        super.onPostExecute(commentQueryResult);
        if (commentQueryResult != null)
            callback.onGetComments(commentQueryResult.getPhotoId(), commentQueryResult.getComments());
        else
            callback.onGetCommentsFailed();
    }

    public interface OnCommentsResultListener {
        void onGetComments(int photoId, List<Comment> comments);
        void onGetCommentsFailed();
    }

}
