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
import org.robolectric.RobolectricTestRunner;
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
import hochschuledarmstadt.photostream_tools.callback.OnPhotoFavoredListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotoUploadListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotosReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnRequestListener;
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

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class PhotoStreamClientTest {

    static final String PHOTO_STREAM_URL = "http://doesnt-matter-because-httpconnection-will-be-mocked.com";
    static final int PHOTO_ID = 1;
    private Context context;
    private PhotoStreamClientDelegate photoStreamClient;
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
        UrlBuilder urlBuilder = new UrlBuilder(PHOTO_STREAM_URL, 5);
        HttpImageLoaderFactoryStub imageLoaderFactoryStub = new HttpImageLoaderFactoryStub();
        ImageCacherFactoryStub imageCacherFactoryStub = new ImageCacherFactoryStub();
        PhotoStreamClientImpl client = new PhotoStreamClientImpl(
                context,
                urlBuilder,
                imageLoaderFactoryStub,
                imageCacherFactoryStub ,
                dbDelegate,
                webSocketClient,
                factory
        );
        this.photoStreamClient = new PhotoStreamClientDelegate("someId", client);
        client.bootstrap();
    }

    @After
    public void tearDown() {
        dbDelegate.recreateTables();
    }

    @Test
    public void loadPhotos(){
        OnPhotosReceivedListener callback = mock(OnPhotosReceivedListener.class);
        OnRequestListener requestCallback = mock(OnRequestListener.class);
        photoStreamClient.addOnPhotosReceivedListener(callback);
        photoStreamClient.addOnRequestListener(requestCallback, RequestType.LOAD_PHOTOS);
        photoStreamClient.loadPhotos();
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnPhotosReceivedListener(callback);
        verify(requestCallback, times(1)).onRequestStarted();
        verify(callback, times(1)).onPhotosReceived(isNotNull(PhotoQueryResult.class));
        verify(requestCallback, times(1)).onRequestFinished();
    }

    @Test
    public void loadPhotosNotModified(){
        createPhotoStreamClient(new HttpPhotoNotModifiedExecutorFactoryStub());
        OnPhotosReceivedListener callback = mock(OnPhotosReceivedListener.class);
        OnRequestListener requestCallback = mock(OnRequestListener.class);
        photoStreamClient.setShouldReloadFirstPageOfPhotosFromCache(Boolean.FALSE);
        photoStreamClient.addOnPhotosReceivedListener(callback);
        photoStreamClient.addOnRequestListener(requestCallback, RequestType.LOAD_PHOTOS);
        photoStreamClient.loadPhotos();
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnPhotosReceivedListener(callback);
        verify(requestCallback, times(1)).onRequestStarted();
        verify(callback, times(1)).onNoNewPhotosAvailable();
        verify(requestCallback, times(1)).onRequestFinished();
    }

    @Test
    public void loadPhotosError(){
        createPhotoStreamClient(new HttpErrorExecutorFactoryStub());
        OnPhotosReceivedListener callback = mock(OnPhotosReceivedListener.class);
        OnRequestListener requestCallback = mock(OnRequestListener.class);
        photoStreamClient.addOnPhotosReceivedListener(callback);
        photoStreamClient.addOnRequestListener(requestCallback, RequestType.LOAD_PHOTOS);
        photoStreamClient.loadPhotos();
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnPhotosReceivedListener(callback);
        verify(requestCallback, times(1)).onRequestStarted();
        verify(callback, times(1)).onReceivePhotosFailed(isNotNull(HttpError.class));
        verify(requestCallback, times(1)).onRequestFinished();
    }

    @Test
    public void loadMorePhotos(){
        OnPhotosReceivedListener callback = mock(OnPhotosReceivedListener.class);
        OnRequestListener requestCallback = mock(OnRequestListener.class);
        photoStreamClient.addOnPhotosReceivedListener(callback);
        photoStreamClient.addOnRequestListener(requestCallback, RequestType.LOAD_PHOTOS);
        photoStreamClient.loadMorePhotos();
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnPhotosReceivedListener(callback);
        verify(requestCallback, times(1)).onRequestStarted();
        verify(callback, times(1)).onPhotosReceived(isNotNull(PhotoQueryResult.class));
        verify(requestCallback, times(1)).onRequestFinished();
    }

    @Test
    public void loadMorePhotosError(){
        createPhotoStreamClient(new HttpErrorExecutorFactoryStub());
        OnRequestListener requestCallback = mock(OnRequestListener.class);
        OnPhotosReceivedListener callback = mock(OnPhotosReceivedListener.class);
        photoStreamClient.addOnPhotosReceivedListener(callback);
        photoStreamClient.addOnRequestListener(requestCallback, RequestType.LOAD_PHOTOS);
        photoStreamClient.loadMorePhotos();
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnPhotosReceivedListener(callback);
        verify(requestCallback, times(1)).onRequestStarted();
        verify(callback, times(1)).onReceivePhotosFailed(isNotNull(HttpError.class));
        verify(requestCallback, times(1)).onRequestFinished();
    }

    @Test
    public void searchPhotos(){
        OnRequestListener requestCallback = mock(OnRequestListener.class);
        OnSearchedPhotosReceivedListener callback = mock(OnSearchedPhotosReceivedListener.class);
        photoStreamClient.addOnSearchPhotosResultListener(callback);
        photoStreamClient.addOnRequestListener(requestCallback, RequestType.SEARCH_PHOTOS);
        photoStreamClient.searchPhotos("query");
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnSearchPhotosResultListener(callback);
        verify(requestCallback, times(1)).onRequestStarted();
        verify(callback, times(1)).onSearchedPhotosReceived(isNotNull(PhotoQueryResult.class));
        verify(requestCallback, times(1)).onRequestFinished();
    }

    @Test
    public void searchPhotosError(){
        createPhotoStreamClient(new HttpErrorExecutorFactoryStub());
        OnRequestListener requestCallback = mock(OnRequestListener.class);
        OnSearchedPhotosReceivedListener callback = mock(OnSearchedPhotosReceivedListener.class);
        String theQuery = "query";
        photoStreamClient.addOnRequestListener(requestCallback, RequestType.SEARCH_PHOTOS);
        photoStreamClient.addOnSearchPhotosResultListener(callback);
        photoStreamClient.searchPhotos(theQuery);
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnSearchPhotosResultListener(callback);
        verify(requestCallback, times(1)).onRequestStarted();
        verify(callback, times(1)).onReceiveSearchedPhotosFailed(eq(theQuery), isNotNull(HttpError.class));
        verify(requestCallback, times(1)).onRequestFinished();
    }

    @Test
    public void searchMorePhotos(){
        OnRequestListener requestCallback = mock(OnRequestListener.class);
        OnSearchedPhotosReceivedListener callback = mock(OnSearchedPhotosReceivedListener.class);
        photoStreamClient.addOnSearchPhotosResultListener(callback);
        photoStreamClient.addOnRequestListener(requestCallback, RequestType.SEARCH_PHOTOS);
        photoStreamClient.searchMorePhotos();
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnSearchPhotosResultListener(callback);
        verify(requestCallback, times(1)).onRequestStarted();
        verify(callback, times(1)).onSearchedPhotosReceived(isNotNull(PhotoQueryResult.class));
        verify(requestCallback, times(1)).onRequestFinished();
    }

    @Test
    public void searchMorePhotosError(){
        createPhotoStreamClient(new HttpErrorExecutorFactoryStub());
        OnRequestListener requestCallback = mock(OnRequestListener.class);
        OnSearchedPhotosReceivedListener callback = mock(OnSearchedPhotosReceivedListener.class);
        photoStreamClient.addOnSearchPhotosResultListener(callback);
        photoStreamClient.addOnRequestListener(requestCallback, RequestType.SEARCH_PHOTOS);
        photoStreamClient.searchMorePhotos();
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnSearchPhotosResultListener(callback);
        verify(requestCallback, times(1)).onRequestStarted();
        verify(callback, times(1)).onReceiveSearchedPhotosFailed(any(String.class), isNotNull(HttpError.class));
        verify(requestCallback, times(1)).onRequestFinished();
    }

    @Test
    public void likePhoto(){
        OnRequestListener requestCallback = mock(OnRequestListener.class);
        OnPhotoFavoredListener callback = mock(OnPhotoFavoredListener.class);
        photoStreamClient.addOnPhotoFavoriteListener(callback);
        photoStreamClient.addOnRequestListener(requestCallback, RequestType.FAVORITE_PHOTO);
        photoStreamClient.favoritePhoto(1);
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnPhotoFavoriteListener(callback);
        verify(requestCallback, times(1)).onRequestStarted();
        verify(callback, times(1)).onPhotoFavored(eq(1));
        verify(requestCallback, times(1)).onRequestFinished();
    }

    @Test
    public void likePhotoError(){
        createPhotoStreamClient(new HttpErrorExecutorFactoryStub());
        OnRequestListener requestCallback = mock(OnRequestListener.class);
        OnPhotoFavoredListener callback = mock(OnPhotoFavoredListener.class);
        photoStreamClient.addOnPhotoFavoriteListener(callback);
        photoStreamClient.addOnRequestListener(requestCallback, RequestType.FAVORITE_PHOTO);
        photoStreamClient.favoritePhoto(1);
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnPhotoFavoriteListener(callback);
        verify(requestCallback, times(1)).onRequestStarted();
        verify(callback, times(1)).onFavoringPhotoFailed(eq(1), isNotNull(HttpError.class));
        verify(requestCallback, times(1)).onRequestFinished();
    }

    @Test
    public void resetLikeForPhoto(){
        OnPhotoFavoredListener callback = mock(OnPhotoFavoredListener.class);
        OnRequestListener requestCallback = mock(OnRequestListener.class);
        photoStreamClient.addOnPhotoFavoriteListener(callback);
        photoStreamClient.addOnRequestListener(requestCallback, RequestType.FAVORITE_PHOTO);
        photoStreamClient.unfavoritePhoto(1);
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnPhotoFavoriteListener(callback);
        verify(requestCallback, times(1)).onRequestStarted();
        verify(callback, times(1)).onPhotoUnfavored(eq(1));
        verify(requestCallback, times(1)).onRequestFinished();
    }

    @Test
    public void resetLikeForPhotoError(){
        createPhotoStreamClient(new HttpErrorExecutorFactoryStub());
        OnRequestListener requestCallback = mock(OnRequestListener.class);
        OnPhotoFavoredListener callback = mock(OnPhotoFavoredListener.class);
        photoStreamClient.addOnPhotoFavoriteListener(callback);
        photoStreamClient.addOnRequestListener(requestCallback, RequestType.FAVORITE_PHOTO);
        photoStreamClient.unfavoritePhoto(1);
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnPhotoFavoriteListener(callback);
        verify(requestCallback, times(1)).onRequestStarted();
        verify(callback, times(1)).onFavoringPhotoFailed(eq(1), any(HttpError.class));
        verify(requestCallback, times(1)).onRequestFinished();
    }

    @Test
    public void uploadPhoto() throws IOException, JSONException {
        OnPhotoUploadListener callback = mock(OnPhotoUploadListener.class);
        OnNewPhotoReceivedListener c = mock(OnNewPhotoReceivedListener.class);
        OnRequestListener requestCallback = mock(OnRequestListener.class);
        photoStreamClient.addOnNewPhotoReceivedListener(c);
        photoStreamClient.addOnRequestListener(requestCallback, RequestType.UPLOAD_PHOTO);
        photoStreamClient.addOnPhotoUploadListener(callback);
        photoStreamClient.uploadPhoto(createFakeJPGBytes(), "description");
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnNewPhotoReceivedListener(c);
        photoStreamClient.removeOnPhotoUploadListener(callback);

        verify(requestCallback, times(1)).onRequestStarted();
        verify(callback, times(1)).onPhotoUploaded(any(Photo.class));
        verify(requestCallback, times(1)).onRequestFinished();
        verify(c, times(1)).onNewPhotoReceived(any(Photo.class));
    }

    private byte[] createFakeJPGBytes() {
        return ByteBuffer.allocate(4).putInt(0xffd8ffe0).array();
    }

    @Test
    public void uploadPhotoError() throws IOException, JSONException {
        createPhotoStreamClient(new HttpErrorExecutorFactoryStub());
        OnPhotoUploadListener callback = mock(OnPhotoUploadListener.class);
        OnRequestListener requestCallback = mock(OnRequestListener.class);
        photoStreamClient.addOnPhotoUploadListener(callback);
        photoStreamClient.addOnRequestListener(requestCallback, RequestType.UPLOAD_PHOTO);
        photoStreamClient.uploadPhoto(createFakeJPGBytes(), "description");
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnPhotoUploadListener(callback);
        verify(requestCallback, times(1)).onRequestStarted();
        verify(callback, times(1)).onPhotoUploadFailed(any(HttpError.class));
        verify(requestCallback, times(1)).onRequestFinished();
    }

    @Test
    public void uploadComment() {

        createPhotoStreamClient(new HttpCommentExecutorFactoryStub());
        OnNewCommentReceivedListener callback = mock(OnNewCommentReceivedListener.class);
        OnCommentUploadFailedListener c = mock(OnCommentUploadFailedListener.class);
        OnRequestListener requestCallback = mock(OnRequestListener.class);

        photoStreamClient.addOnNewCommentReceivedListener(callback);
        photoStreamClient.addOnRequestListener(requestCallback, RequestType.UPLOAD_COMMENT);
        photoStreamClient.addOnUploadCommentFailedListener(c);

        photoStreamClient.uploadComment(PHOTO_ID, "comment");

        Robolectric.flushBackgroundThreadScheduler();

        photoStreamClient.removeOnNewCommentReceivedListener(callback);
        photoStreamClient.removeOnUploadCommentFailedListener(c);

        verify(requestCallback, times(1)).onRequestStarted();
        verify(callback, times(1)).onNewCommentReceived(any(Comment.class));
        verify(requestCallback, times(1)).onRequestFinished();
        verify(c, times(0)).onCommentUploadFailed(any(HttpError.class));
    }

    @Test
    public void uploadCommentError() {

        createPhotoStreamClient(new HttpErrorExecutorFactoryStub());

        OnNewCommentReceivedListener callback = mock(OnNewCommentReceivedListener.class);
        OnCommentUploadFailedListener c = mock(OnCommentUploadFailedListener.class);
        OnRequestListener requestCallback = mock(OnRequestListener.class);

        photoStreamClient.addOnNewCommentReceivedListener(callback);
        photoStreamClient.addOnUploadCommentFailedListener(c);
        photoStreamClient.addOnRequestListener(requestCallback, RequestType.UPLOAD_COMMENT);
        photoStreamClient.uploadComment(1, "comment");

        Robolectric.flushBackgroundThreadScheduler();

        photoStreamClient.removeOnNewCommentReceivedListener(callback);
        photoStreamClient.removeOnUploadCommentFailedListener(c);

        verify(requestCallback, times(1)).onRequestStarted();
        verify(callback, times(0)).onNewCommentReceived(any(Comment.class));
        verify(requestCallback, times(1)).onRequestFinished();
        verify(c, times(1)).onCommentUploadFailed(any(HttpError.class));
    }

    @Test
    public void deletePhoto(){
        OnPhotoDeletedListener callback = mock(OnPhotoDeletedListener.class);
        OnRequestListener requestCallback = mock(OnRequestListener.class);
        photoStreamClient.addOnPhotoDeletedListener(callback);
        photoStreamClient.addOnRequestListener(requestCallback, RequestType.DELETE_PHOTO);
        photoStreamClient.deletePhoto(1);
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnPhotoDeletedListener(callback);
        verify(requestCallback, times(1)).onRequestStarted();
        verify(callback, times(1)).onPhotoDeleted(eq(1));
        verify(requestCallback, times(1)).onRequestFinished();
    }

    @Test
    public void deletePhotoError(){
        createPhotoStreamClient(new HttpErrorExecutorFactoryStub());
        OnPhotoDeletedListener callback = mock(OnPhotoDeletedListener.class);
        OnRequestListener requestCallback = mock(OnRequestListener.class);
        photoStreamClient.addOnPhotoDeletedListener(callback);
        photoStreamClient.addOnRequestListener(requestCallback, RequestType.DELETE_PHOTO);
        photoStreamClient.deletePhoto(1);
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnPhotoDeletedListener(callback);
        verify(requestCallback, times(1)).onRequestStarted();
        verify(callback, times(1)).onPhotoDeleteFailed(eq(1), any(HttpError.class));
        verify(requestCallback, times(1)).onRequestFinished();
    }

    @Test
    public void loadComments(){
        createPhotoStreamClient(new HttpCommentExecutorFactoryStub());
        OnCommentsReceivedListener callback = mock(OnCommentsReceivedListener.class);
        OnRequestListener requestCallback = mock(OnRequestListener.class);
        photoStreamClient.addOnCommentsReceivedListener(callback);
        photoStreamClient.addOnRequestListener(requestCallback, RequestType.LOAD_COMMENTS);
        photoStreamClient.loadComments(1);
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnCommentsReceivedListener(callback);
        verify(requestCallback, times(1)).onRequestStarted();
        verify(callback, times(1)).onCommentsReceived(eq(1), any(List.class));
        verify(requestCallback, times(1)).onRequestFinished();
    }

    @Test
    public void loadCommentsError(){
        createPhotoStreamClient(new HttpErrorExecutorFactoryStub());
        OnCommentsReceivedListener callback = mock(OnCommentsReceivedListener.class);
        OnRequestListener requestCallback = mock(OnRequestListener.class);
        photoStreamClient.addOnCommentsReceivedListener(callback);
        photoStreamClient.addOnRequestListener(requestCallback, RequestType.LOAD_COMMENTS);
        photoStreamClient.loadComments(1);
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnCommentsReceivedListener(callback);
        verify(requestCallback, times(1)).onRequestStarted();
        verify(callback, times(1)).onReceiveCommentsFailed(eq(1), any(HttpError.class));
        verify(requestCallback, times(1)).onRequestFinished();
    }

    @Test
    public void deleteComment(){
        createPhotoStreamClient(new HttpCommentExecutorFactoryStub());
        OnCommentDeletedListener callback = mock(OnCommentDeletedListener.class);
        OnRequestListener requestCallback = mock(OnRequestListener.class);
        photoStreamClient.addOnCommentDeletedListener(callback);
        photoStreamClient.addOnRequestListener(requestCallback, RequestType.DELETE_COMMENT);
        photoStreamClient.deleteComment(1);
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnCommentDeletedListener(callback);
        verify(requestCallback, times(1)).onRequestStarted();
        verify(callback, times(1)).onCommentDeleted(eq(1));
        verify(requestCallback, times(1)).onRequestFinished();
    }

    @Test
    public void deleteCommentError(){
        createPhotoStreamClient(new HttpErrorExecutorFactoryStub());
        OnCommentDeletedListener callback = mock(OnCommentDeletedListener.class);
        OnRequestListener requestCallback = mock(OnRequestListener.class);
        photoStreamClient.addOnCommentDeletedListener(callback);
        photoStreamClient.addOnRequestListener(requestCallback, RequestType.DELETE_COMMENT);
        photoStreamClient.deleteComment(1);
        Robolectric.flushBackgroundThreadScheduler();
        photoStreamClient.removeOnCommentDeletedListener(callback);
        verify(requestCallback, times(1)).onRequestStarted();
        verify(callback, times(1)).onCommentDeleteFailed(eq(1), any(HttpError.class));
        verify(requestCallback, times(1)).onRequestFinished();
    }

    @Test
    public void triggerNewPhotoReceived(){
        OnNewPhotoReceivedListener callback = mock(OnNewPhotoReceivedListener.class);
        OnRequestListener requestCallback = mock(OnRequestListener.class);
        photoStreamClient.addOnNewPhotoReceivedListener(callback);
        photoStreamClient.addOnRequestListener(requestCallback, RequestType.UPLOAD_PHOTO);
        Photo photo = mock(Photo.class);
        webSocketClient.getMessageListener().onNewPhoto(photo);
        photoStreamClient.removeOnNewPhotoReceivedListener(callback);
        verify(requestCallback, times(0)).onRequestStarted();
        verify(callback, times(1)).onNewPhotoReceived(eq(photo));
        verify(requestCallback, times(0)).onRequestFinished();
    }

    @Test
    public void triggerPhotoDeleted(){
        OnPhotoDeletedListener callback = mock(OnPhotoDeletedListener.class);
        OnRequestListener requestCallback = mock(OnRequestListener.class);
        photoStreamClient.addOnPhotoDeletedListener(callback);
        photoStreamClient.addOnRequestListener(requestCallback, RequestType.DELETE_PHOTO);
        webSocketClient.getMessageListener().onPhotoDeleted(1);
        photoStreamClient.removeOnPhotoDeletedListener(callback);
        verify(requestCallback, times(0)).onRequestStarted();
        verify(callback, times(1)).onPhotoDeleted(eq(1));
        verify(requestCallback, times(0)).onRequestFinished();
    }

    @Test
    public void triggerCommentDeleted(){
        OnCommentDeletedListener callback = mock(OnCommentDeletedListener.class);
        OnRequestListener requestCallback = mock(OnRequestListener.class);
        photoStreamClient.addOnCommentDeletedListener(callback);
        webSocketClient.getMessageListener().onCommentDeleted(1);
        photoStreamClient.removeOnCommentDeletedListener(callback);
        verify(requestCallback, times(0)).onRequestStarted();
        verify(callback, times(1)).onCommentDeleted(eq(1));
        verify(requestCallback, times(0)).onRequestFinished();
    }

    @Test
    public void triggerNewCommentReceived(){
        OnNewCommentReceivedListener callback = mock(OnNewCommentReceivedListener.class);
        OnRequestListener requestCallback = mock(OnRequestListener.class);
        photoStreamClient.addOnNewCommentReceivedListener(callback);
        photoStreamClient.addOnRequestListener(requestCallback, RequestType.UPLOAD_COMMENT);
        Comment comment = mock(Comment.class);
        webSocketClient.getMessageListener().onNewComment(comment);
        photoStreamClient.removeOnNewCommentReceivedListener(callback);
        verify(requestCallback, times(0)).onRequestStarted();
        verify(callback, times(1)).onNewCommentReceived(eq(comment));
        verify(requestCallback, times(0)).onRequestFinished();
    }

}
