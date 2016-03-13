package hochschuledarmstadt.photostream_tools;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import hochschuledarmstadt.photostream_tools.model.HttpResult;

/**
 * Created by Andreas Schattney on 13.03.2016.
 */
abstract class BaseAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    public static final int CONNECT_TIMEOUT = 6000;
    protected final String uri;

    public BaseAsyncTask(String uri){
        this.uri = uri;
    }

    @NonNull
    protected String convertStreamToString(InputStream is) throws IOException {
        InputStreamReader reader = new InputStreamReader(is, Charset.forName("UTF-8"));
        char[] buffer = new char[4096];
        StringBuilder stringBuilder = new StringBuilder();
        int read;
        while ((read = reader.read(buffer, 0, buffer.length)) != -1) {
            stringBuilder.append(buffer, 0, read);
        }
        return stringBuilder.toString();
    }

    protected HttpResult getHttpErrorResult(InputStream errorStream) throws IOException {
        final String result = convertStreamToString(errorStream);
        return new Gson().fromJson(result, HttpResult.class);
    }

    protected void postError(final HttpResult httpResult) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                sendError(httpResult);
            }
        });
    }

    protected abstract void sendError(HttpResult httpResult);

    protected static class HttpPhotoStreamException extends Throwable {

        private final HttpResult httpResult;

        public HttpPhotoStreamException(HttpResult httpResult) {
            this.httpResult = httpResult;
        }
        public HttpResult getHttpResult() {
            return httpResult;
        }
    }

}
