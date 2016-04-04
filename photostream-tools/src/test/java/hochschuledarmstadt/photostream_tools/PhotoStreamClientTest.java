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

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.List;

import hochschuledarmstadt.photostream_tools.callback.OnCommentDeletedListener;
import hochschuledarmstadt.photostream_tools.callback.OnCommentsReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnNewCommentReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnNewPhotoReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotoDeletedListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotoLikeListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotosReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnSearchedPhotosReceivedListener;
import hochschuledarmstadt.photostream_tools.model.Comment;
import hochschuledarmstadt.photostream_tools.model.Photo;
import hochschuledarmstadt.photostream_tools.model.PhotoQueryResult;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class PhotoStreamClientTest {

    public static final String PHOTO_STREAM_URL = "http://doesnt-matter-because-httpconnection-will-be-mocked.com";
    private Context context;
    private PhotoStreamClient photoStreamClient;
    private DbTestConnectionDelegate dbTestConnectionDelegate;
    private WebSocketClientStub webSocketClient;
    private PhotoStreamCallbackContainer container;

    @Before
    public void setUp() {
        createPhotoStreamClient(new HttpPhotoExecutorFactoryStub());
    }

    private void createPhotoStreamClient(HttpExecutorFactory factory) {
        context = RuntimeEnvironment.application.getApplicationContext();
        webSocketClient = new WebSocketClientStub();
        dbTestConnectionDelegate = new DbTestConnectionDelegate(context);
        container = new PhotoStreamCallbackContainer();
        UrlBuilder urlBuilder = new UrlBuilder(PHOTO_STREAM_URL);
        photoStreamClient = new PhotoStreamClient(context, urlBuilder , dbTestConnectionDelegate, webSocketClient, container, factory);
        photoStreamClient.bootstrap();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void loadPhotos(){
        OnPhotosReceivedListener callback = mock(OnPhotosReceivedListener.class);
        photoStreamClient.addOnPhotosReceivedListener(callback);
        photoStreamClient.loadPhotos();
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnPhotosReceivedListener(callback);
        verify(callback, times(1)).onShowProgressDialog();
        verify(callback, times(1)).onPhotosReceived(isNotNull(PhotoQueryResult.class));
        verify(callback, times(1)).onDismissProgressDialog();
    }

    @Test
    public void loadPhotosNotModified(){
        createPhotoStreamClient(new HttpGetNotModifiedExecutorFactoryStub());
        OnPhotosReceivedListener callback = mock(OnPhotosReceivedListener.class);
        photoStreamClient.addOnPhotosReceivedListener(callback);
        photoStreamClient.loadPhotos();
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnPhotosReceivedListener(callback);
        verify(callback, times(1)).onShowProgressDialog();
        verify(callback, times(1)).onNoNewPhotosAvailable();
        verify(callback, times(1)).onDismissProgressDialog();
    }

    @Test
    public void loadMorePhotos(){
        OnPhotosReceivedListener callback = mock(OnPhotosReceivedListener.class);
        photoStreamClient.addOnPhotosReceivedListener(callback);
        photoStreamClient.loadMorePhotos();
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnPhotosReceivedListener(callback);
        verify(callback, times(1)).onShowProgressDialog();
        verify(callback, times(1)).onPhotosReceived(isNotNull(PhotoQueryResult.class));
        verify(callback, times(1)).onDismissProgressDialog();
    }

    @Test
    public void searchPhotos(){
        OnSearchedPhotosReceivedListener callback = mock(OnSearchedPhotosReceivedListener.class);
        photoStreamClient.addOnSearchPhotosResultListener(callback);
        photoStreamClient.searchPhotos("query");
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnSearchPhotosResultListener(callback);
        verify(callback, times(1)).onShowProgressDialog();
        verify(callback, times(1)).onSearchedPhotosReceived(isNotNull(PhotoQueryResult.class));
        verify(callback, times(1)).onDismissProgressDialog();
    }

    @Test
    public void searchMorePhotos(){
        OnSearchedPhotosReceivedListener callback = mock(OnSearchedPhotosReceivedListener.class);
        photoStreamClient.addOnSearchPhotosResultListener(callback);
        photoStreamClient.searchMorePhotos();
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnSearchPhotosResultListener(callback);
        verify(callback, times(1)).onShowProgressDialog();
        verify(callback, times(1)).onSearchedPhotosReceived(isNotNull(PhotoQueryResult.class));
        verify(callback, times(1)).onDismissProgressDialog();
    }

    @Test
    public void likePhoto(){
        OnPhotoLikeListener callback = mock(OnPhotoLikeListener.class);
        photoStreamClient.addOnPhotoLikeListener(callback);
        photoStreamClient.likePhoto(1);
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnPhotoLikeListener(callback);
        verify(callback, times(1)).onShowProgressDialog();
        verify(callback, times(1)).onPhotoLiked(eq(1));
        verify(callback, times(1)).onDismissProgressDialog();
    }

    @Test
    public void resetLikeForPhoto(){
        OnPhotoLikeListener callback = mock(OnPhotoLikeListener.class);
        photoStreamClient.addOnPhotoLikeListener(callback);
        photoStreamClient.resetLikeForPhoto(1);
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnPhotoLikeListener(callback);
        verify(callback, times(1)).onShowProgressDialog();
        verify(callback, times(1)).onPhotoDisliked(eq(1));
        verify(callback, times(1)).onDismissProgressDialog();
    }


    @Test
    public void deletePhoto(){
        OnPhotoDeletedListener callback = mock(OnPhotoDeletedListener.class);
        photoStreamClient.addOnPhotoDeletedListener(callback);
        photoStreamClient.deletePhoto(1);
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnPhotoDeletedListener(callback);
        verify(callback, times(1)).onShowProgressDialog();
        verify(callback, times(1)).onPhotoDeleted(eq(1));
        verify(callback, times(1)).onDismissProgressDialog();
    }

    @Test
    public void loadComments(){
        createPhotoStreamClient(new HttpCommentExecutorFactoryStub());
        OnCommentsReceivedListener callback = mock(OnCommentsReceivedListener.class);
        photoStreamClient.addOnCommentsReceivedListener(callback);
        photoStreamClient.loadComments(1);
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnCommentsReceivedListener(callback);
        verify(callback, times(1)).onShowProgressDialog();
        verify(callback, times(1)).onCommentsReceived(eq(1), any(List.class));
        verify(callback, times(1)).onDismissProgressDialog();
    }

    @Test
    public void deleteComment(){
        createPhotoStreamClient(new HttpCommentExecutorFactoryStub());
        OnCommentDeletedListener callback = mock(OnCommentDeletedListener.class);
        photoStreamClient.addOnCommentDeletedListener(callback);
        photoStreamClient.deleteComment(1);
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnCommentDeletedListener(callback);
        verify(callback, times(1)).onShowProgressDialog();
        verify(callback, times(1)).onCommentDeleted(eq(1));
        verify(callback, times(1)).onDismissProgressDialog();
    }

    @Test
    public void triggerNewPhotoReceived(){
        OnNewPhotoReceivedListener callback = mock(OnNewPhotoReceivedListener.class);
        photoStreamClient.addOnNewPhotoReceivedListener(callback);
        Photo photo = mock(Photo.class);
        webSocketClient.getMessageListener().onNewPhoto(photo);
        photoStreamClient.removeOnNewPhotoReceivedListener(callback);
        verify(callback, times(0)).onShowProgressDialog();
        verify(callback, times(1)).onNewPhotoReceived(eq(photo));
        verify(callback, times(0)).onDismissProgressDialog();
    }

    @Test
    public void triggerPhotoDeleted(){
        OnPhotoDeletedListener callback = mock(OnPhotoDeletedListener.class);
        photoStreamClient.addOnPhotoDeletedListener(callback);
        webSocketClient.getMessageListener().onPhotoDeleted(1);
        photoStreamClient.removeOnPhotoDeletedListener(callback);
        verify(callback, times(0)).onShowProgressDialog();
        verify(callback, times(1)).onPhotoDeleted(eq(1));
        verify(callback, times(0)).onDismissProgressDialog();
    }

    @Test
    public void triggerCommentDeleted(){
        OnCommentDeletedListener callback = mock(OnCommentDeletedListener.class);
        photoStreamClient.addOnCommentDeletedListener(callback);
        webSocketClient.getMessageListener().onCommentDeleted(1);
        photoStreamClient.removeOnCommentDeletedListener(callback);
        verify(callback, times(0)).onShowProgressDialog();
        verify(callback, times(1)).onCommentDeleted(eq(1));
        verify(callback, times(0)).onDismissProgressDialog();
    }

    @Test
    public void triggerNewCommentReceived(){
        OnNewCommentReceivedListener callback = mock(OnNewCommentReceivedListener.class);
        photoStreamClient.addOnNewCommentReceivedListener(callback);
        Comment comment = mock(Comment.class);
        webSocketClient.getMessageListener().onNewComment(comment);
        photoStreamClient.removeOnNewCommentReceivedListener(callback);
        verify(callback, times(0)).onShowProgressDialog();
        verify(callback, times(1)).onNewCommentReceived(eq(comment));
        verify(callback, times(0)).onDismissProgressDialog();
    }

    private static class HttpCommentExecutorFactoryStub implements HttpExecutorFactory {

        @Override
        public HttpPutExecutor createHttpPutExecutor(String url) {
            try {
                HttpPutExecutor executor = mock(HttpPutExecutor.class);
                when(executor.execute()).thenReturn(new HttpResponse(200, null));
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
                JSONObject json = new JSONObject("{\"photo_id\":\"1\",\"comments\":[{\"comment_id\":1,\"message\":\"Cooles Auto!\",\"deleteable\":false}]}");
                HttpGetExecutor executor = mock(HttpGetExecutor.class);
                when(executor.execute()).thenReturn(new HttpResponse(200, json.toString()));
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
                when(executor.execute()).thenReturn(new HttpResponse(200, null));
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
            return null;
        }
    }

    private static class HttpPhotoExecutorFactoryStub implements HttpExecutorFactory {

        @Override
        public HttpPutExecutor createHttpPutExecutor(String url) {
            try {
                HttpPutExecutor executor = mock(HttpPutExecutor.class);
                when(executor.execute()).thenReturn(new HttpResponse(200, null));
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
                when(executor.execute()).thenReturn(new HttpResponse(200, json.toString()));
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
                when(executor.execute()).thenReturn(new HttpResponse(200, null));
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
            return null;
        }
    }

    private static class HttpGetNotModifiedExecutorFactoryStub implements HttpExecutorFactory {

        @Override
        public HttpPutExecutor createHttpPutExecutor(String url) {
            return null;
        }

        @Override
        public HttpGetExecutor createHttpGetExecutor(String url) {
            try {
                HttpGetExecutor executor = mock(HttpGetExecutor.class);
                when(executor.execute()).thenReturn(new HttpResponse(304, null));
                return executor;
            } catch (BaseAsyncTask.HttpPhotoStreamException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public HttpDeleteExecutor createHttpDeleteExecutor(String url) {
            return null;
        }

        @Override
        public HttpPostExecutor createHttpPostExecutor(String url) {
            return null;
        }
    }

}
