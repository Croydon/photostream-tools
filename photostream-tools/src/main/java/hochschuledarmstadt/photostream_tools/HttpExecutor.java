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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import hochschuledarmstadt.photostream_tools.model.HttpResult;

abstract class HttpExecutor {

    protected static final int CONNECT_TIMEOUT = 6000;
    private final String installationId;
    private final String url;
    private Charset encoding = Charset.forName("UTF-8");

    protected String getInstallationId() {
        return installationId;
    }

    protected String getUrl() {
        return url;
    }

    public HttpExecutor(String url, String installationId) {
        this.url = url;
        this.installationId = installationId;
    }

    @NonNull
    protected String convertStreamToString(InputStream is) throws IOException {
        InputStreamReader reader = new InputStreamReader(is, encoding);
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
        try{
            return new Gson().fromJson(result, HttpResult.class);
        }catch(Exception e){
            return new HttpResult(500, "Internal Server Error");
        }
    }

    public void setEncoding(Charset encoding) {
        this.encoding = encoding;
    }

    public Charset getEncoding() {
        return encoding;
    }
}
