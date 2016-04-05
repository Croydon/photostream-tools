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
import android.support.annotation.NonNull;

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
import hochschuledarmstadt.photostream_tools.callback.OnCommentUploadListener;
import hochschuledarmstadt.photostream_tools.callback.OnCommentsReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnNewCommentReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnNewPhotoReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotoDeletedListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotoLikeListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotoUploadListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotosReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnSearchedPhotosReceivedListener;
import hochschuledarmstadt.photostream_tools.model.Comment;
import hochschuledarmstadt.photostream_tools.model.HttpResult;
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
        createPhotoStreamClient(new HttpPhotoNotModifiedExecutorFactoryStub());
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
    public void loadPhotosError(){
        createPhotoStreamClient(new HttpErrorExecutorFactoryStub());
        OnPhotosReceivedListener callback = mock(OnPhotosReceivedListener.class);
        photoStreamClient.addOnPhotosReceivedListener(callback);
        photoStreamClient.loadPhotos();
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnPhotosReceivedListener(callback);
        verify(callback, times(1)).onShowProgressDialog();
        verify(callback, times(1)).onReceivePhotosFailed(isNotNull(HttpResult.class));
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
    public void loadMorePhotosError(){
        createPhotoStreamClient(new HttpErrorExecutorFactoryStub());
        OnPhotosReceivedListener callback = mock(OnPhotosReceivedListener.class);
        photoStreamClient.addOnPhotosReceivedListener(callback);
        photoStreamClient.loadMorePhotos();
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnPhotosReceivedListener(callback);
        verify(callback, times(1)).onShowProgressDialog();
        verify(callback, times(1)).onReceivePhotosFailed(isNotNull(HttpResult.class));
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
    public void searchPhotosError(){
        createPhotoStreamClient(new HttpErrorExecutorFactoryStub());
        OnSearchedPhotosReceivedListener callback = mock(OnSearchedPhotosReceivedListener.class);
        String theQuery = "query";
        photoStreamClient.addOnSearchPhotosResultListener(callback);
        photoStreamClient.searchPhotos(theQuery);
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnSearchPhotosResultListener(callback);
        verify(callback, times(1)).onShowProgressDialog();
        verify(callback, times(1)).onReceiveSearchedPhotosFailed(eq(theQuery), isNotNull(HttpResult.class));
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
    public void searchMorePhotosError(){
        createPhotoStreamClient(new HttpErrorExecutorFactoryStub());
        OnSearchedPhotosReceivedListener callback = mock(OnSearchedPhotosReceivedListener.class);
        photoStreamClient.addOnSearchPhotosResultListener(callback);
        photoStreamClient.searchMorePhotos();
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnSearchPhotosResultListener(callback);
        verify(callback, times(1)).onShowProgressDialog();
        verify(callback, times(1)).onReceiveSearchedPhotosFailed(any(String.class), isNotNull(HttpResult.class));
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
    public void likePhotoError(){
        createPhotoStreamClient(new HttpErrorExecutorFactoryStub());
        OnPhotoLikeListener callback = mock(OnPhotoLikeListener.class);
        photoStreamClient.addOnPhotoLikeListener(callback);
        photoStreamClient.likePhoto(1);
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnPhotoLikeListener(callback);
        verify(callback, times(1)).onShowProgressDialog();
        verify(callback, times(1)).onPhotoLikeFailed(eq(1), isNotNull(HttpResult.class));
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
    public void resetLikeForPhotoError(){
        createPhotoStreamClient(new HttpErrorExecutorFactoryStub());
        OnPhotoLikeListener callback = mock(OnPhotoLikeListener.class);
        photoStreamClient.addOnPhotoLikeListener(callback);
        photoStreamClient.resetLikeForPhoto(1);
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnPhotoLikeListener(callback);
        verify(callback, times(1)).onShowProgressDialog();
        verify(callback, times(1)).onPhotoLikeFailed(eq(1), any(HttpResult.class));
        verify(callback, times(1)).onDismissProgressDialog();
    }

    @Test
    public void uploadPhoto() throws IOException, JSONException {
        OnPhotoUploadListener callback = mock(OnPhotoUploadListener.class);
        OnNewPhotoReceivedListener c = mock(OnNewPhotoReceivedListener.class);
        photoStreamClient.addOnNewPhotoReceivedListener(c);
        photoStreamClient.addOnPhotoUploadListener(callback);
        photoStreamClient.uploadPhoto(new byte[1024], "description");
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnNewPhotoReceivedListener(c);
        photoStreamClient.removeOnPhotoUploadListener(callback);

        verify(callback, times(1)).onShowProgressDialog();
        verify(callback, times(1)).onPhotoUploaded(any(Photo.class));
        verify(callback, times(1)).onDismissProgressDialog();

        verify(c, times(0)).onShowProgressDialog();
        verify(c, times(1)).onNewPhotoReceived(any(Photo.class));
        verify(c, times(0)).onDismissProgressDialog();
    }

    @Test
    public void uploadPhotoError() throws IOException, JSONException {
        createPhotoStreamClient(new HttpErrorExecutorFactoryStub());
        OnPhotoUploadListener callback = mock(OnPhotoUploadListener.class);
        photoStreamClient.addOnPhotoUploadListener(callback);
        photoStreamClient.uploadPhoto(new byte[1024], "description");
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnPhotoUploadListener(callback);
        verify(callback, times(1)).onShowProgressDialog();
        verify(callback, times(1)).onPhotoUploadFailed(any(HttpResult.class));
        verify(callback, times(1)).onDismissProgressDialog();
    }

    @Test
    public void uploadComment() {

        createPhotoStreamClient(new HttpCommentExecutorFactoryStub());
        OnNewCommentReceivedListener callback = mock(OnNewCommentReceivedListener.class);
        OnCommentUploadListener c = mock(OnCommentUploadListener.class);

        photoStreamClient.addOnNewCommentReceivedListener(callback);
        photoStreamClient.addOnUploadCommentListener(c);

        photoStreamClient.uploadComment(1, "comment");

        Robolectric.flushBackgroundThreadScheduler();

        photoStreamClient.removeOnNewCommentReceivedListener(callback);
        photoStreamClient.removeOnUploadCommentListener(c);

        verify(callback, times(0)).onShowProgressDialog();
        verify(callback, times(1)).onNewCommentReceived(any(Comment.class));
        verify(callback, times(0)).onDismissProgressDialog();

        verify(c, times(1)).onShowProgressDialog();
        verify(c, times(0)).onCommentUploadFailed(any(HttpResult.class));
        verify(c, times(1)).onDismissProgressDialog();
    }

    @Test
    public void uploadCommentError() {

        createPhotoStreamClient(new HttpErrorExecutorFactoryStub());

        OnNewCommentReceivedListener callback = mock(OnNewCommentReceivedListener.class);
        OnCommentUploadListener c = mock(OnCommentUploadListener.class);

        photoStreamClient.addOnNewCommentReceivedListener(callback);
        photoStreamClient.addOnUploadCommentListener(c);

        photoStreamClient.uploadComment(1, "comment");

        Robolectric.flushBackgroundThreadScheduler();

        photoStreamClient.removeOnNewCommentReceivedListener(callback);
        photoStreamClient.removeOnUploadCommentListener(c);

        verify(callback, times(0)).onShowProgressDialog();
        verify(callback, times(0)).onNewCommentReceived(any(Comment.class));
        verify(callback, times(0)).onDismissProgressDialog();

        verify(c, times(1)).onShowProgressDialog();
        verify(c, times(1)).onCommentUploadFailed(any(HttpResult.class));
        verify(c, times(1)).onDismissProgressDialog();
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
    public void deletePhotoError(){
        createPhotoStreamClient(new HttpErrorExecutorFactoryStub());
        OnPhotoDeletedListener callback = mock(OnPhotoDeletedListener.class);
        photoStreamClient.addOnPhotoDeletedListener(callback);
        photoStreamClient.deletePhoto(1);
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnPhotoDeletedListener(callback);
        verify(callback, times(1)).onShowProgressDialog();
        verify(callback, times(1)).onPhotoDeleteFailed(eq(1), any(HttpResult.class));
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
    public void loadCommentsError(){
        createPhotoStreamClient(new HttpErrorExecutorFactoryStub());
        OnCommentsReceivedListener callback = mock(OnCommentsReceivedListener.class);
        photoStreamClient.addOnCommentsReceivedListener(callback);
        photoStreamClient.loadComments(1);
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnCommentsReceivedListener(callback);
        verify(callback, times(1)).onShowProgressDialog();
        verify(callback, times(1)).onReceiveCommentsFailed(eq(1), any(HttpResult.class));
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
    public void deleteCommentError(){
        createPhotoStreamClient(new HttpErrorExecutorFactoryStub());
        OnCommentDeletedListener callback = mock(OnCommentDeletedListener.class);
        photoStreamClient.addOnCommentDeletedListener(callback);
        photoStreamClient.deleteComment(1);
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnCommentDeletedListener(callback);
        verify(callback, times(1)).onShowProgressDialog();
        verify(callback, times(1)).onCommentDeleteFailed(eq(1), any(HttpResult.class));
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
            try {
                JSONObject json = new JSONObject("{\"comment_id\":1,\"message\":\"Cooles Auto!\",\"deleteable\":false}");
                HttpPostExecutor executor = mock(HttpPostExecutor.class);
                when(executor.execute(any(String.class))).thenReturn(new HttpResponse(200, json.toString()));
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
            try {
                HttpPostExecutor executor = mock(HttpPostExecutor.class);
                when(executor.execute(any(String.class))).thenReturn(new HttpResponse(200, "{\"photo_id\":24,\"image\":\"/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAA0JCgsKCA0LCgsODg0PEyAVExISEyccHhcgLikxMC4p\\nLSwzOko+MzZGNywtQFdBRkxOUlNSMj5aYVpQYEpRUk//2wBDAQ4ODhMREyYVFSZPNS01T09PT09P\\nT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT0//wAARCAIBAXYDASIA\\nAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQA\\nAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3\\nODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWm\\np6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEA\\nAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSEx\\nBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElK\\nU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3\\nuLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwDuLH/j\\n0T8f5mp6hs/+PVPx/nU1bLYkKKKKYBRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFF\\nABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFAFXUXZLdSjFT\\nu6g47Gimar/x7L/vj+RorKW40WLT/j2T8f51LUdt/qF/H+dSVothBRRRTAKKKKACiiigAqG8uUs7\\nOa6lBKQoXIHUgDOBU1Y/iaxu9TsI7C1wqTyr58hxhEHPTOScgdP0pAP0nWkv9Onu7mB7NrZmWaOQ\\n5KYGc9Aent61I+u6WlnLdteJ5EUvks+Cfn9Bxz17Z/SsX+yNYt7rVA8v25dQsmVpQiR/vQpCgjPT\\n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\\n\",\"comment\":\"test\",\"deleteable\":false,\"comment_count\":0,\"favorite\":0}"));
                return executor;
            } catch (BaseAsyncTask.HttpPhotoStreamException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static class HttpPhotoNotModifiedExecutorFactoryStub implements HttpExecutorFactory {

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

    private static class HttpErrorExecutorFactoryStub implements HttpExecutorFactory {

        @Override
        public HttpPostExecutor createHttpPostExecutor(String url) {
            try {
                HttpPostExecutor executor = mock(HttpPostExecutor.class);
                HttpResult errorResult = new HttpResult(500, null);
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
                HttpResult errorResult = new HttpResult(500, null);
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
                HttpResult errorResult = new HttpResult(500, null);
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
                HttpResult errorResult = new HttpResult(500, null);
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
}
