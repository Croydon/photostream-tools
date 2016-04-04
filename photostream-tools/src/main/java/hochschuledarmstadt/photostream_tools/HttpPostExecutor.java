/*
 * The MIT License
 *
 * Copyright (c) 2016 Andreas Schattney
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package hochschuledarmstadt.photostream_tools;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class HttpPostExecutor extends HttpExecutor {

    public HttpPostExecutor(String url, String installationId){
        super(url, installationId);
    }

    private String eTag = null;

    public String geteTag() {
        return eTag;
    }

    public HttpResponse execute(String message) throws IOException, BaseAsyncTask.HttpPhotoStreamException {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(getUrl()).openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setDoOutput(true);
        urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
        urlConnection.addRequestProperty("installation_id", getInstallationId());
        urlConnection.addRequestProperty("Content-Type", "application/json");
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), Charset.forName(UTF_8)));
        writer.write(message, 0, message.length());
        writer.flush();
        writer.close();
        final int status = urlConnection.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            String result = convertStreamToString(urlConnection.getInputStream());
            return new HttpResponse(status, result);
        }else{
            throw new BaseAsyncTask.HttpPhotoStreamException(getHttpErrorResult(urlConnection.getErrorStream()));
        }
    }

}
