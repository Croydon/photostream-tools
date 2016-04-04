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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

class HttpPutExecutor extends HttpExecutor{

    public HttpPutExecutor(String url, String installationId) {
        super(url, installationId);
    }

    public HttpResponse execute() throws IOException, BaseAsyncTask.HttpPhotoStreamException {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(getUrl()).openConnection();
        urlConnection.setRequestMethod("PUT");
        urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
        urlConnection.addRequestProperty("installation_id", getInstallationId());
        if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK){
            throw new BaseAsyncTask.HttpPhotoStreamException(getHttpErrorResult(urlConnection.getErrorStream()));
        }else{
            return new HttpResponse(urlConnection.getResponseCode(), null);
        }
    }

}
