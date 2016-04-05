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

import org.apache.tools.ant.taskdefs.condition.Http;
import org.bouncycastle.util.Store;
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
import java.util.concurrent.ExecutionException;

import hochschuledarmstadt.photostream_tools.model.HttpResult;
import hochschuledarmstadt.photostream_tools.model.PhotoQueryResult;

import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ApiRequestsFailTest {

    public static final int INVALID_PHOTO_ID = -1;
    private static final int INVALID_COMMENT_ID = -1;

    private Context context;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.application.getApplicationContext();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void loadPhotos() throws IOException, BaseAsyncTask.HttpPhotoStreamException {
        HttpGetExecutor executor = createMockHttpGetExecutor();
        LoadPhotosAsyncTask.GetPhotosCallback callback = mock(LoadPhotosAsyncTask.GetPhotosCallback.class);
        LoadPhotosAsyncTask streamAsyncTask = new LoadPhotosAsyncTask(executor, context, callback);
        streamAsyncTask.execute();
        Robolectric.flushBackgroundThreadScheduler();
        try {
            streamAsyncTask.get();
            verify(callback, times(1)).onPhotosError(any(HttpResult.class));
        } catch (InterruptedException e) {
            assertFalse(e.toString(), true);
        } catch (ExecutionException e) {
            assertFalse(e.toString(), true);
        }
    }

    @Test
    public void loadMorePhotos() throws IOException, BaseAsyncTask.HttpPhotoStreamException {
        HttpGetExecutor executor = createMockHttpGetExecutor();
        LoadMorePhotosAsyncTask.GetPhotosCallback callback = mock(LoadMorePhotosAsyncTask.GetPhotosCallback.class);
        LoadMorePhotosAsyncTask streamAsyncTask = new LoadMorePhotosAsyncTask(executor, context, callback);
        streamAsyncTask.execute();
        Robolectric.flushBackgroundThreadScheduler();
        try {
            streamAsyncTask.get();
            verify(callback, times(1)).onPhotosError(any(HttpResult.class));
        } catch (InterruptedException e) {
            assertFalse(e.toString(), true);
        } catch (ExecutionException e) {
            assertFalse(e.toString(), true);
        }
    }

    @Test
    public void searchPhotos() throws IOException, BaseAsyncTask.HttpPhotoStreamException {
        HttpGetExecutor executor = createMockHttpGetExecutor();
        String query = "query";
        SearchPhotosAsyncTask.OnSearchPhotosResultCallback callback = mock(SearchPhotosAsyncTask.OnSearchPhotosResultCallback.class);
        SearchPhotosAsyncTask searchPhotosAsyncTask = new SearchPhotosAsyncTask(executor, context, query, callback);
        searchPhotosAsyncTask.execute();
        Robolectric.flushBackgroundThreadScheduler();
        try {
            searchPhotosAsyncTask.get();
            verify(callback, times(1)).onSearchPhotosError(any(HttpResult.class));
        } catch (InterruptedException e) {
            assertFalse(e.toString(), true);
        } catch (ExecutionException e) {
            assertFalse(e.toString(), true);
        }
    }

    @Test
    public void searchMorePhotos() throws IOException, BaseAsyncTask.HttpPhotoStreamException {
        HttpGetExecutor executor = createMockHttpGetExecutor();
        SearchMorePhotosAsyncTask.OnSearchPhotosResultCallback callback = mock(SearchMorePhotosAsyncTask.OnSearchPhotosResultCallback.class);
        SearchMorePhotosAsyncTask searchPhotosAsyncTask = new SearchMorePhotosAsyncTask(executor, context, callback);
        searchPhotosAsyncTask.execute();
        Robolectric.flushBackgroundThreadScheduler();
        try {
            searchPhotosAsyncTask.get();
            verify(callback, times(1)).onSearchPhotosError(any(HttpResult.class));
        } catch (InterruptedException e) {
            assertFalse(e.toString(), true);
        } catch (ExecutionException e) {
            assertFalse(e.toString(), true);
        }
    }

    @Test
    public void uploadComment() throws IOException, BaseAsyncTask.HttpPhotoStreamException {
        HttpPostExecutor executor = createMockHttpPostExecutor();
        final int photoId = INVALID_PHOTO_ID;
        StoreCommentAsyncTask.OnCommentSentListener listener = mock(StoreCommentAsyncTask.OnCommentSentListener.class);
        StoreCommentAsyncTask storeCommentAsyncTask = new StoreCommentAsyncTask(executor, photoId, "this will fail", listener);
        storeCommentAsyncTask.execute();
        Robolectric.flushBackgroundThreadScheduler();
        try {
            storeCommentAsyncTask.get();
            verify(listener, times(1)).onSendCommentFailed(any(HttpResult.class));
        } catch (InterruptedException e) {
            assertFalse(e.toString(), true);
        } catch (ExecutionException e) {
            assertFalse(e.toString(), true);
        }
    }

    @Test
    public void uploadPhoto() throws IOException, BaseAsyncTask.HttpPhotoStreamException {
        HttpPostExecutor executor = createMockHttpPostExecutor();
        StorePhotoAsyncTask.OnPhotoStoredCallback listener = mock(StorePhotoAsyncTask.OnPhotoStoredCallback.class);
        StorePhotoAsyncTask storePhotoAsyncTask = new StorePhotoAsyncTask(executor, listener);
        storePhotoAsyncTask.execute(new JSONObject());
        Robolectric.flushBackgroundThreadScheduler();
        try {
            storePhotoAsyncTask.get();
            verify(listener, times(1)).onPhotoStoreError(any(HttpResult.class));
        } catch (InterruptedException e) {
            assertFalse(e.toString(), true);
        } catch (ExecutionException e) {
            assertFalse(e.toString(), true);
        }
    }

    @Test
    public void loadComments() throws IOException, BaseAsyncTask.HttpPhotoStreamException {
        HttpGetExecutor executor = createMockHttpGetExecutor();
        final int photoId = INVALID_PHOTO_ID;
        LoadCommentsAsyncTask.OnCommentsResultListener listener = mock(LoadCommentsAsyncTask.OnCommentsResultListener.class);
        LoadCommentsAsyncTask loadCommentsAsyncTask = new LoadCommentsAsyncTask(executor, photoId, listener);
        loadCommentsAsyncTask.execute();
        Robolectric.flushBackgroundThreadScheduler();
        try {
            loadCommentsAsyncTask.get();
            verify(listener, times(1)).onGetCommentsFailed(eq(photoId), any(HttpResult.class));
        } catch (InterruptedException e) {
            assertFalse(e.toString(), true);
        } catch (ExecutionException e) {
            assertFalse(e.toString(), true);
        }
    }

    @Test
    public void likePhoto() throws IOException, BaseAsyncTask.HttpPhotoStreamException {
        HttpPutExecutor executor = createMockHttpPutExecutor();
        final int photoId = INVALID_PHOTO_ID;
        LikeTable voteTable = new LikeTable(DbConnection.getInstance(context));
        LikePhotoAsyncTask.OnVotePhotoResultListener callback = mock(LikePhotoAsyncTask.OnVotePhotoResultListener.class);
        LikePhotoAsyncTask votePhotoAsyncTask = new LikePhotoAsyncTask(executor, voteTable, photoId, callback);
        votePhotoAsyncTask.execute();
        Robolectric.flushBackgroundThreadScheduler();
        try {
            votePhotoAsyncTask.get();
            verify(callback, times(1)).onPhotoLikeFailed(eq(photoId), any(HttpResult.class));
        } catch (InterruptedException e) {
            assertFalse(e.toString(), true);
        } catch (ExecutionException e) {
            assertFalse(e.toString(), true);
        }
    }

    @Test
    public void resetLikeForPhoto() throws IOException, BaseAsyncTask.HttpPhotoStreamException {
        HttpPutExecutor executor = createMockHttpPutExecutor();
        final int photoId = INVALID_PHOTO_ID;
        LikeTable voteTable = new LikeTable(DbConnection.getInstance(context));
        DislikePhotoAsyncTask.OnVotePhotoResultListener callback = mock(DislikePhotoAsyncTask.OnVotePhotoResultListener.class);
        DislikePhotoAsyncTask votePhotoAsyncTask = new DislikePhotoAsyncTask(executor, voteTable, photoId, callback);
        votePhotoAsyncTask.execute();
        Robolectric.flushBackgroundThreadScheduler();
        try {
            votePhotoAsyncTask.get();
            verify(callback, times(1)).onPhotoLikeFailed(eq(photoId), any(HttpResult.class));
        } catch (InterruptedException e) {
            assertFalse(e.toString(), true);
        } catch (ExecutionException e) {
            assertFalse(e.toString(), true);
        }
    }

    @Test
    public void deleteComment() throws IOException, BaseAsyncTask.HttpPhotoStreamException {
        HttpDeleteExecutor executor = createMockHttpDeleteExecutor();
        final int commentId = INVALID_COMMENT_ID;
        DeleteCommentAsyncTask.OnDeleteCommentResultListener callback = mock(DeleteCommentAsyncTask.OnDeleteCommentResultListener.class);
        DeleteCommentAsyncTask deleteCommentAsyncTask = new DeleteCommentAsyncTask(executor, commentId, callback);
        deleteCommentAsyncTask.execute();
        Robolectric.flushBackgroundThreadScheduler();
        try {
            deleteCommentAsyncTask.get();
            verify(callback, times(1)).onCommentDeleteFailed(eq(commentId), any(HttpResult.class));
        } catch (InterruptedException e) {
            assertFalse(e.toString(), true);
        } catch (ExecutionException e) {
            assertFalse(e.toString(), true);
        }
    }

    @Test
    public void deletePhoto() throws IOException, BaseAsyncTask.HttpPhotoStreamException {
        HttpDeleteExecutor executor = createMockHttpDeleteExecutor();
        final int photoId = INVALID_PHOTO_ID;
        DeletePhotoAsyncTask.OnDeletePhotoResultListener callback = mock(DeletePhotoAsyncTask.OnDeletePhotoResultListener.class);
        DeletePhotoAsyncTask deletePhotoAsyncTask = new DeletePhotoAsyncTask(executor, photoId, callback);
        deletePhotoAsyncTask.execute();
        Robolectric.flushBackgroundThreadScheduler();
        try {
            deletePhotoAsyncTask.get();
            verify(callback, times(1)).onPhotoDeleteFailed(eq(photoId), any(HttpResult.class));
        } catch (InterruptedException e) {
            assertFalse(e.toString(), true);
        } catch (ExecutionException e) {
            assertFalse(e.toString(), true);
        }
    }

    @NonNull
    private HttpPostExecutor createMockHttpPostExecutor() throws IOException, BaseAsyncTask.HttpPhotoStreamException {
        HttpPostExecutor executor = mock(HttpPostExecutor.class);
        HttpResult errorResult = new HttpResult(500, null);
        when(executor.execute(any(String.class))).thenThrow(new BaseAsyncTask.HttpPhotoStreamException(errorResult));
        return executor;
    }


    @NonNull
    private HttpDeleteExecutor createMockHttpDeleteExecutor() throws IOException, BaseAsyncTask.HttpPhotoStreamException {
        HttpDeleteExecutor executor = mock(HttpDeleteExecutor.class);
        HttpResult errorResult = new HttpResult(500, null);
        when(executor.execute()).thenThrow(new BaseAsyncTask.HttpPhotoStreamException(errorResult));
        return executor;
    }

    @NonNull
    private HttpPutExecutor createMockHttpPutExecutor() throws IOException, BaseAsyncTask.HttpPhotoStreamException {
        HttpPutExecutor executor = mock(HttpPutExecutor.class);
        HttpResult errorResult = new HttpResult(500, null);
        when(executor.execute()).thenThrow(new BaseAsyncTask.HttpPhotoStreamException(errorResult));
        return executor;
    }

    @NonNull
    private HttpGetExecutor createMockHttpGetExecutor() throws IOException, BaseAsyncTask.HttpPhotoStreamException {
        HttpGetExecutor executor = mock(HttpGetExecutor.class);
        HttpResult errorResult = new HttpResult(500, null);
        when(executor.execute()).thenThrow(new BaseAsyncTask.HttpPhotoStreamException(errorResult));
        return executor;
    }

}