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

import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import hochschuledarmstadt.photostream_tools.callback.OnCommentDeletedListener;
import hochschuledarmstadt.photostream_tools.callback.OnCommentUploadFailedListener;
import hochschuledarmstadt.photostream_tools.callback.OnCommentsReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnNewCommentReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnNewPhotoReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotoDeletedListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotoLikeListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotoUploadListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotosReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnSearchedPhotosReceivedListener;
import hochschuledarmstadt.photostream_tools.model.Comment;
import hochschuledarmstadt.photostream_tools.model.HttpError;
import hochschuledarmstadt.photostream_tools.model.Photo;
import hochschuledarmstadt.photostream_tools.model.PhotoQueryResult;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class PhotoStreamClientTest {

    public static final String PHOTO_STREAM_URL = "http://doesnt-matter-because-httpconnection-will-be-mocked.com";
    public static final int PHOTO_ID = 1;
    private Context context;
    private PhotoStreamClient photoStreamClient;
    private DbTestConnectionDelegate dbDelegate;
    private WebSocketClientStub webSocketClient;

    @Before
    public void setUp() {
        createPhotoStreamClient(new HttpPhotoExecutorFactoryStub());
    }

    private void createPhotoStreamClient(HttpExecutorFactory factory) {
        context = RuntimeEnvironment.application.getApplicationContext();
        webSocketClient = new WebSocketClientStub();
        dbDelegate = new DbTestConnectionDelegate(context);
        UrlBuilder urlBuilder = new UrlBuilder(PHOTO_STREAM_URL);
        HttpImageLoader imageLoader = new HttpImageLoaderStub();
        final ImageCacher imageCacher = new ImageCacherStub();
        photoStreamClient = new PhotoStreamClient(context, urlBuilder , imageLoader,  imageCacher, dbDelegate, webSocketClient, factory);
        photoStreamClient.bootstrap();
    }

    @After
    public void tearDown() {
        dbDelegate.recreateTables();
    }

    @Test
    public void loadPhotos(){
        OnPhotosReceivedListener callback = mock(OnPhotosReceivedListener.class);
        photoStreamClient.addOnPhotosReceivedListener(callback);
        photoStreamClient.loadPhotos();
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnPhotosReceivedListener(callback);
        verify(callback, times(1)).onRequestStarted();
        verify(callback, times(1)).onPhotosReceived(isNotNull(PhotoQueryResult.class));
        verify(callback, times(1)).onRequestFinished();
    }

    @Test
    public void loadPhotosNotModified(){
        createPhotoStreamClient(new HttpPhotoNotModifiedExecutorFactoryStub());
        OnPhotosReceivedListener callback = mock(OnPhotosReceivedListener.class);
        photoStreamClient.setShouldReloadFirstPageOfPhotosFromCache(false);
        photoStreamClient.addOnPhotosReceivedListener(callback);
        photoStreamClient.loadPhotos();
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnPhotosReceivedListener(callback);
        verify(callback, times(1)).onRequestStarted();
        verify(callback, times(1)).onNoNewPhotosAvailable();
        verify(callback, times(1)).onRequestFinished();
    }

    @Test
    public void loadPhotosError(){
        createPhotoStreamClient(new HttpErrorExecutorFactoryStub());
        OnPhotosReceivedListener callback = mock(OnPhotosReceivedListener.class);
        photoStreamClient.addOnPhotosReceivedListener(callback);
        photoStreamClient.loadPhotos();
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnPhotosReceivedListener(callback);
        verify(callback, times(1)).onRequestStarted();
        verify(callback, times(1)).onReceivePhotosFailed(isNotNull(HttpError.class));
        verify(callback, times(1)).onRequestFinished();
    }

    @Test
    public void loadMorePhotos(){
        OnPhotosReceivedListener callback = mock(OnPhotosReceivedListener.class);
        photoStreamClient.addOnPhotosReceivedListener(callback);
        photoStreamClient.loadMorePhotos();
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnPhotosReceivedListener(callback);
        verify(callback, times(1)).onRequestStarted();
        verify(callback, times(1)).onPhotosReceived(isNotNull(PhotoQueryResult.class));
        verify(callback, times(1)).onRequestFinished();
    }

    @Test
    public void loadMorePhotosError(){
        createPhotoStreamClient(new HttpErrorExecutorFactoryStub());
        OnPhotosReceivedListener callback = mock(OnPhotosReceivedListener.class);
        photoStreamClient.addOnPhotosReceivedListener(callback);
        photoStreamClient.loadMorePhotos();
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnPhotosReceivedListener(callback);
        verify(callback, times(1)).onRequestStarted();
        verify(callback, times(1)).onReceivePhotosFailed(isNotNull(HttpError.class));
        verify(callback, times(1)).onRequestFinished();
    }

    @Test
    public void searchPhotos(){
        OnSearchedPhotosReceivedListener callback = mock(OnSearchedPhotosReceivedListener.class);
        photoStreamClient.addOnSearchPhotosResultListener(callback);
        photoStreamClient.searchPhotos("query");
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnSearchPhotosResultListener(callback);
        verify(callback, times(1)).onRequestStarted();
        verify(callback, times(1)).onSearchedPhotosReceived(isNotNull(PhotoQueryResult.class));
        verify(callback, times(1)).onRequestFinished();
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
        verify(callback, times(1)).onRequestStarted();
        verify(callback, times(1)).onReceiveSearchedPhotosFailed(eq(theQuery), isNotNull(HttpError.class));
        verify(callback, times(1)).onRequestFinished();
    }

    @Test
    public void searchMorePhotos(){
        OnSearchedPhotosReceivedListener callback = mock(OnSearchedPhotosReceivedListener.class);
        photoStreamClient.addOnSearchPhotosResultListener(callback);
        photoStreamClient.searchMorePhotos();
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnSearchPhotosResultListener(callback);
        verify(callback, times(1)).onRequestStarted();
        verify(callback, times(1)).onSearchedPhotosReceived(isNotNull(PhotoQueryResult.class));
        verify(callback, times(1)).onRequestFinished();
    }

    @Test
    public void searchMorePhotosError(){
        createPhotoStreamClient(new HttpErrorExecutorFactoryStub());
        OnSearchedPhotosReceivedListener callback = mock(OnSearchedPhotosReceivedListener.class);
        photoStreamClient.addOnSearchPhotosResultListener(callback);
        photoStreamClient.searchMorePhotos();
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnSearchPhotosResultListener(callback);
        verify(callback, times(1)).onRequestStarted();
        verify(callback, times(1)).onReceiveSearchedPhotosFailed(any(String.class), isNotNull(HttpError.class));
        verify(callback, times(1)).onRequestFinished();
    }

    @Test
    public void likePhoto(){
        OnPhotoLikeListener callback = mock(OnPhotoLikeListener.class);
        photoStreamClient.addOnPhotoLikeListener(callback);
        photoStreamClient.likePhoto(1);
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnPhotoLikeListener(callback);
        verify(callback, times(1)).onRequestStarted();
        verify(callback, times(1)).onPhotoLiked(eq(1));
        verify(callback, times(1)).onRequestFinished();
    }

    @Test
    public void likePhotoError(){
        createPhotoStreamClient(new HttpErrorExecutorFactoryStub());
        OnPhotoLikeListener callback = mock(OnPhotoLikeListener.class);
        photoStreamClient.addOnPhotoLikeListener(callback);
        photoStreamClient.likePhoto(1);
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnPhotoLikeListener(callback);
        verify(callback, times(1)).onRequestStarted();
        verify(callback, times(1)).onPhotoLikeFailed(eq(1), isNotNull(HttpError.class));
        verify(callback, times(1)).onRequestFinished();
    }

    @Test
    public void resetLikeForPhoto(){
        OnPhotoLikeListener callback = mock(OnPhotoLikeListener.class);
        photoStreamClient.addOnPhotoLikeListener(callback);
        photoStreamClient.resetLikeForPhoto(1);
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnPhotoLikeListener(callback);
        verify(callback, times(1)).onRequestStarted();
        verify(callback, times(1)).onPhotoDisliked(eq(1));
        verify(callback, times(1)).onRequestFinished();
    }

    @Test
    public void resetLikeForPhotoError(){
        createPhotoStreamClient(new HttpErrorExecutorFactoryStub());
        OnPhotoLikeListener callback = mock(OnPhotoLikeListener.class);
        photoStreamClient.addOnPhotoLikeListener(callback);
        photoStreamClient.resetLikeForPhoto(1);
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnPhotoLikeListener(callback);
        verify(callback, times(1)).onRequestStarted();
        verify(callback, times(1)).onPhotoLikeFailed(eq(1), any(HttpError.class));
        verify(callback, times(1)).onRequestFinished();
    }

    @Test
    public void uploadPhoto() throws IOException, JSONException {
        OnPhotoUploadListener callback = mock(OnPhotoUploadListener.class);
        OnNewPhotoReceivedListener c = mock(OnNewPhotoReceivedListener.class);
        photoStreamClient.addOnNewPhotoReceivedListener(c);
        photoStreamClient.addOnPhotoUploadListener(callback);
        photoStreamClient.uploadPhoto(createFakeJPGBytes(), "description");
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnNewPhotoReceivedListener(c);
        photoStreamClient.removeOnPhotoUploadListener(callback);

        verify(callback, times(1)).onRequestStarted();
        verify(callback, times(1)).onPhotoUploaded(any(Photo.class));
        verify(callback, times(1)).onRequestFinished();

        verify(c, times(0)).onRequestStarted();
        verify(c, times(1)).onNewPhotoReceived(any(Photo.class));
        verify(c, times(0)).onRequestFinished();
    }

    private byte[] createFakeJPGBytes() {
        return ByteBuffer.allocate(4).putInt(0xffd8ffe0).array();
    }

    @Test
    public void uploadPhotoError() throws IOException, JSONException {
        createPhotoStreamClient(new HttpErrorExecutorFactoryStub());
        OnPhotoUploadListener callback = mock(OnPhotoUploadListener.class);
        photoStreamClient.addOnPhotoUploadListener(callback);
        photoStreamClient.uploadPhoto(createFakeJPGBytes(), "description");
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnPhotoUploadListener(callback);
        verify(callback, times(1)).onRequestStarted();
        verify(callback, times(1)).onPhotoUploadFailed(any(HttpError.class));
        verify(callback, times(1)).onRequestFinished();
    }

    @Test
    public void uploadComment() {

        createPhotoStreamClient(new HttpCommentExecutorFactoryStub());
        OnNewCommentReceivedListener callback = mock(OnNewCommentReceivedListener.class);
        OnCommentUploadFailedListener c = mock(OnCommentUploadFailedListener.class);

        photoStreamClient.addOnNewCommentReceivedListener(callback);
        photoStreamClient.addOnUploadCommentFailedListener(c);

        photoStreamClient.uploadComment(PHOTO_ID, "comment");

        Robolectric.flushBackgroundThreadScheduler();

        photoStreamClient.removeOnNewCommentReceivedListener(callback);
        photoStreamClient.removeOnUploadCommentFailedListener(c);

        verify(callback, times(0)).onRequestStarted();
        verify(callback, times(1)).onNewCommentReceived(any(Comment.class));
        verify(callback, times(0)).onRequestFinished();

        verify(c, times(1)).onRequestStarted();
        verify(c, times(0)).onCommentUploadFailed(any(HttpError.class));
        verify(c, times(1)).onRequestFinished();
    }

    @Test
    public void uploadCommentError() {

        createPhotoStreamClient(new HttpErrorExecutorFactoryStub());

        OnNewCommentReceivedListener callback = mock(OnNewCommentReceivedListener.class);
        OnCommentUploadFailedListener c = mock(OnCommentUploadFailedListener.class);

        photoStreamClient.addOnNewCommentReceivedListener(callback);
        photoStreamClient.addOnUploadCommentFailedListener(c);

        photoStreamClient.uploadComment(1, "comment");

        Robolectric.flushBackgroundThreadScheduler();

        photoStreamClient.removeOnNewCommentReceivedListener(callback);
        photoStreamClient.removeOnUploadCommentFailedListener(c);

        verify(callback, times(0)).onRequestStarted();
        verify(callback, times(0)).onNewCommentReceived(any(Comment.class));
        verify(callback, times(0)).onRequestFinished();

        verify(c, times(1)).onRequestStarted();
        verify(c, times(1)).onCommentUploadFailed(any(HttpError.class));
        verify(c, times(1)).onRequestFinished();
    }

    @Test
    public void deletePhoto(){
        OnPhotoDeletedListener callback = mock(OnPhotoDeletedListener.class);
        photoStreamClient.addOnPhotoDeletedListener(callback);
        photoStreamClient.deletePhoto(1);
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnPhotoDeletedListener(callback);
        verify(callback, times(1)).onRequestStarted();
        verify(callback, times(1)).onPhotoDeleted(eq(1));
        verify(callback, times(1)).onRequestFinished();
    }

    @Test
    public void deletePhotoError(){
        createPhotoStreamClient(new HttpErrorExecutorFactoryStub());
        OnPhotoDeletedListener callback = mock(OnPhotoDeletedListener.class);
        photoStreamClient.addOnPhotoDeletedListener(callback);
        photoStreamClient.deletePhoto(1);
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnPhotoDeletedListener(callback);
        verify(callback, times(1)).onRequestStarted();
        verify(callback, times(1)).onPhotoDeleteFailed(eq(1), any(HttpError.class));
        verify(callback, times(1)).onRequestFinished();
    }

    @Test
    public void loadComments(){
        createPhotoStreamClient(new HttpCommentExecutorFactoryStub());
        OnCommentsReceivedListener callback = mock(OnCommentsReceivedListener.class);
        photoStreamClient.addOnCommentsReceivedListener(callback);
        photoStreamClient.loadComments(1);
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnCommentsReceivedListener(callback);
        verify(callback, times(1)).onRequestStarted();
        verify(callback, times(1)).onCommentsReceived(eq(1), any(List.class));
        verify(callback, times(1)).onRequestFinished();
    }

    @Test
    public void loadCommentsError(){
        createPhotoStreamClient(new HttpErrorExecutorFactoryStub());
        OnCommentsReceivedListener callback = mock(OnCommentsReceivedListener.class);
        photoStreamClient.addOnCommentsReceivedListener(callback);
        photoStreamClient.loadComments(1);
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnCommentsReceivedListener(callback);
        verify(callback, times(1)).onRequestStarted();
        verify(callback, times(1)).onReceiveCommentsFailed(eq(1), any(HttpError.class));
        verify(callback, times(1)).onRequestFinished();
    }

    @Test
    public void deleteComment(){
        createPhotoStreamClient(new HttpCommentExecutorFactoryStub());
        OnCommentDeletedListener callback = mock(OnCommentDeletedListener.class);
        photoStreamClient.addOnCommentDeletedListener(callback);
        photoStreamClient.deleteComment(1);
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnCommentDeletedListener(callback);
        verify(callback, times(1)).onRequestStarted();
        verify(callback, times(1)).onCommentDeleted(eq(1));
        verify(callback, times(1)).onRequestFinished();
    }

    @Test
    public void deleteCommentError(){
        createPhotoStreamClient(new HttpErrorExecutorFactoryStub());
        OnCommentDeletedListener callback = mock(OnCommentDeletedListener.class);
        photoStreamClient.addOnCommentDeletedListener(callback);
        photoStreamClient.deleteComment(1);
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnCommentDeletedListener(callback);
        verify(callback, times(1)).onRequestStarted();
        verify(callback, times(1)).onCommentDeleteFailed(eq(1), any(HttpError.class));
        verify(callback, times(1)).onRequestFinished();
    }

    @Test
    public void triggerNewPhotoReceived(){
        OnNewPhotoReceivedListener callback = mock(OnNewPhotoReceivedListener.class);
        photoStreamClient.addOnNewPhotoReceivedListener(callback);
        Photo photo = mock(Photo.class);
        webSocketClient.getMessageListener().onNewPhoto(photo);
        photoStreamClient.removeOnNewPhotoReceivedListener(callback);
        verify(callback, times(0)).onRequestStarted();
        verify(callback, times(1)).onNewPhotoReceived(eq(photo));
        verify(callback, times(0)).onRequestFinished();
    }

    @Test
    public void triggerPhotoDeleted(){
        OnPhotoDeletedListener callback = mock(OnPhotoDeletedListener.class);
        photoStreamClient.addOnPhotoDeletedListener(callback);
        webSocketClient.getMessageListener().onPhotoDeleted(1);
        photoStreamClient.removeOnPhotoDeletedListener(callback);
        verify(callback, times(0)).onRequestStarted();
        verify(callback, times(1)).onPhotoDeleted(eq(1));
        verify(callback, times(0)).onRequestFinished();
    }

    @Test
    public void triggerCommentDeleted(){
        OnCommentDeletedListener callback = mock(OnCommentDeletedListener.class);
        photoStreamClient.addOnCommentDeletedListener(callback);
        webSocketClient.getMessageListener().onCommentDeleted(1);
        photoStreamClient.removeOnCommentDeletedListener(callback);
        verify(callback, times(0)).onRequestStarted();
        verify(callback, times(1)).onCommentDeleted(eq(1));
        verify(callback, times(0)).onRequestFinished();
    }

    @Test
    public void triggerNewCommentReceived(){
        OnNewCommentReceivedListener callback = mock(OnNewCommentReceivedListener.class);
        photoStreamClient.addOnNewCommentReceivedListener(callback);
        Comment comment = mock(Comment.class);
        webSocketClient.getMessageListener().onNewComment(comment);
        photoStreamClient.removeOnNewCommentReceivedListener(callback);
        verify(callback, times(0)).onRequestStarted();
        verify(callback, times(1)).onNewCommentReceived(eq(comment));
        verify(callback, times(0)).onRequestFinished();
    }

}
