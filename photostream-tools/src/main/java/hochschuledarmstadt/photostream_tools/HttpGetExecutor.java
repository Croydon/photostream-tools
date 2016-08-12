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

import com.google.gson.Gson;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hochschuledarmstadt.photostream_tools.model.Photo;
import hochschuledarmstadt.photostream_tools.model.PhotoQueryResult;

class HttpGetExecutor extends HttpExecutor {

    private HashMap<String,String> headerFields = new HashMap<>();
    private String etag;
    private int page;

    public HttpGetExecutor(String url, String installationId) {
        super(url, installationId);
    }

    public HttpResponse execute() throws IOException, BaseAsyncTask.HttpPhotoStreamException {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(getUrl()).openConnection();
        urlConnection.setDoInput(true);
        urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
        urlConnection.setReadTimeout(CONNECT_TIMEOUT);
        urlConnection.addRequestProperty("installation_id", getInstallationId());
        for (Map.Entry<String, String> entry : headerFields.entrySet()){
            urlConnection.addRequestProperty(entry.getKey(), entry.getValue());
        }
        final int responseCode = urlConnection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NOT_MODIFIED){
            if (responseCode == HttpURLConnection.HTTP_OK) {
                etag = urlConnection.getHeaderField("ETag");
            }else{
                String pageFieldValue = urlConnection.getHeaderField("photo-page");
                if (pageFieldValue != null)
                    page = Integer.parseInt(pageFieldValue);
            }
            String result = convertStreamToString(urlConnection.getInputStream());
            return new HttpResponse(responseCode, result);
        }else{
            throw new BaseAsyncTask.HttpPhotoStreamException(getHttpErrorResult(urlConnection.getErrorStream()));
        }
    }

    public void addHeaderField(String field, String value) {
        if (!headerFields.containsKey(field))
            headerFields.put(field, value);
    }

    public String getEtag() {
        return etag;
    }

    public int getPage() {
        return page;
    }
}
