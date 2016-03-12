package hochschuledarmstadt.photostream_tools;

import android.os.AsyncTask;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import hochschuledarmstadt.photostream_tools.model.Comment;

/**
 * Created by Andreas Schattney on 24.02.2016.
 */
class StoreCommentAsyncTask extends AsyncTask<StoreCommentQuery, Void, Comment> {

    private static final int CONNECT_TIMEOUT_IN_MILLIS = 6000;
    private static final String UTF_8 = "UTF-8";
    private static final String TAG = StoreCommentAsyncTask.class.getName();
    private final String installationId;
    private final OnCommentSentListener callback;

    public StoreCommentAsyncTask(String installationId, OnCommentSentListener callback){
        this.installationId = installationId;
        this.callback = callback;
    }

    @Override
    protected Comment doInBackground(StoreCommentQuery... params) {
        try {
            return sendComment(params[0]);
        } catch (IOException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
        }
        return null;
    }

    private Comment sendComment(StoreCommentQuery commentObj) throws IOException {
        final String url = String.format("http://5.45.97.155:8081/photostream/image/%s/comment", commentObj.getPhotoId());
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        urlConnection.setConnectTimeout(CONNECT_TIMEOUT_IN_MILLIS);
        urlConnection.addRequestProperty("installation_id", installationId);
        urlConnection.addRequestProperty("Content-Type", "application/json");
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), Charset.forName(UTF_8)));

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", commentObj.getComment());
            String s = jsonObject.toString();
            writer.write(s, 0, s.length());
            writer.flush();
        } catch (JSONException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
        }finally{
            writer.close();
        }

        int status = urlConnection.getResponseCode();
        boolean success = (status == HttpURLConnection.HTTP_OK);
        Comment comment = null;

        if (success){
            InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream(), Charset.forName("UTF-8"));
            char[] buffer = new char[4096];
            StringBuilder stringBuilder = new StringBuilder();
            int read;
            while((read = reader.read(buffer, 0, buffer.length)) != -1){
                stringBuilder.append(buffer,0,read);
            }
            Gson gson = new Gson();
            comment = gson.fromJson(stringBuilder.toString(), Comment.class);
        }
        return comment;
    }

    @Override
    protected void onPostExecute(Comment comment) {
        super.onPostExecute(comment);
        if (comment != null)
            callback.onCommentSent(comment);
        else
            callback.onSendCommentFailed();
    }

    public interface OnCommentSentListener {
        void onCommentSent(Comment comment);
        void onSendCommentFailed();
    }
}
