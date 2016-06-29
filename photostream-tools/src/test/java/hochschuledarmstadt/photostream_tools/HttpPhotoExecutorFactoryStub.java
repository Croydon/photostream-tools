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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpPhotoExecutorFactoryStub implements HttpExecutorFactory {

    @Override
    public HttpPutExecutor createHttpPutExecutor(String url) {
        try {
            HttpPutExecutor executor = mock(HttpPutExecutor.class);
            when(executor.execute()).thenReturn(new HttpResponse(HttpResponse.STATUS_OK, null));
            return executor;
        } catch (BaseAsyncTask.HttpPhotoStreamException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public HttpGetExecutor createHttpGetExecutor(String url) {
        try {
            JSONObject json = new JSONObject();
            json.put("page", 1);
            json.put("photos", new JSONArray());
            json.put("has_next_page", false);
            HttpGetExecutor executor = mock(HttpGetExecutor.class);
            when(executor.execute()).thenReturn(new HttpResponse(HttpResponse.STATUS_OK, json.toString()));
            return executor;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (BaseAsyncTask.HttpPhotoStreamException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public HttpDeleteExecutor createHttpDeleteExecutor(String url) {
        try {
            HttpDeleteExecutor executor = mock(HttpDeleteExecutor.class);
            when(executor.execute()).thenReturn(new HttpResponse(HttpResponse.STATUS_OK, null));
            return executor;
        } catch (BaseAsyncTask.HttpPhotoStreamException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public HttpPostExecutor createHttpPostExecutor(String url) {
        try {
            HttpPostExecutor executor = mock(HttpPostExecutor.class);
            when(executor.execute(any(String.class))).thenReturn(new HttpResponse(HttpResponse.STATUS_OK, Fakes.PHOTO_RESULT));
            return executor;
        } catch (BaseAsyncTask.HttpPhotoStreamException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
