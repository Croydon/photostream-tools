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

import hochschuledarmstadt.photostream_tools.model.HttpError;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpErrorExecutorFactoryStub implements HttpExecutorFactory {

    @Override
    public HttpPostExecutor createHttpPostExecutor(String url) {
        try {
            HttpPostExecutor executor = mock(HttpPostExecutor.class);
            HttpError errorResult = new HttpError(HttpResponse.STATUS_INTERNAL_SERVER_ERROR, null);
            when(executor.execute(any(String.class))).thenThrow(new BaseAsyncTask.HttpPhotoStreamException(errorResult));
            return executor;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BaseAsyncTask.HttpPhotoStreamException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public HttpDeleteExecutor createHttpDeleteExecutor(String url) {
        try {
            HttpDeleteExecutor executor = mock(HttpDeleteExecutor.class);
            HttpError errorResult = new HttpError(HttpResponse.STATUS_INTERNAL_SERVER_ERROR, null);
            when(executor.execute()).thenThrow(new BaseAsyncTask.HttpPhotoStreamException(errorResult));
            return executor;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BaseAsyncTask.HttpPhotoStreamException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public HttpPutExecutor createHttpPutExecutor(String url) {
        try {
            HttpPutExecutor executor = mock(HttpPutExecutor.class);
            HttpError errorResult = new HttpError(HttpResponse.STATUS_INTERNAL_SERVER_ERROR, null);
            when(executor.execute()).thenThrow(new BaseAsyncTask.HttpPhotoStreamException(errorResult));
            return executor;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BaseAsyncTask.HttpPhotoStreamException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public HttpGetExecutor createHttpGetExecutor(String url) {
        try {
            HttpGetExecutor executor = mock(HttpGetExecutor.class);
            HttpError errorResult = new HttpError(HttpResponse.STATUS_INTERNAL_SERVER_ERROR, null);
            when(executor.execute()).thenThrow(new BaseAsyncTask.HttpPhotoStreamException(errorResult));
            return executor;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BaseAsyncTask.HttpPhotoStreamException e) {
            e.printStackTrace();
        }
        return null;
    }
}
