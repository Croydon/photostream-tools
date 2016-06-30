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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

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

class PhotoStreamClient implements AndroidSocket.OnMessageListener, IPhotoStreamClient {

    private static final String TAG = PhotoStreamService.class.getName();
    public static final String INTENT_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";

    private final HttpExecutorFactory httpExecutorFactory;
    private final Context context;
    private DbConnection dbConnection;
    private String lastSearchQuery;
    private String loadPhotosETag;
    private BroadcastReceiver internetAvailableBroadcastReceiver;
    private final UrlBuilder urlBuilder;
    private WebSocketClient webSocketClient;
    private PhotoStreamCallbackContainer callbackContainer;

    public PhotoStreamClient(Context context, UrlBuilder urlBuilder, DbConnection dbConnection, WebSocketClient webSocketClient, PhotoStreamCallbackContainer callbackContainer, HttpExecutorFactory httpExecutorFactory){
        this.context = context;
        this.urlBuilder = urlBuilder;
        this.webSocketClient = webSocketClient;
        this.callbackContainer = callbackContainer;
        this.dbConnection = dbConnection;
        this.httpExecutorFactory = httpExecutorFactory;
    }

    void destroy() {
        callbackContainer.clear();
        unregisterInternetAvailableBroadcastReceiver();
        if (webSocketClient != null) {
            webSocketClient.setMessageListener(null);
            webSocketClient.disconnect();
        }

    }

    @Override
    public void addOnPhotoLikeListener(OnPhotoLikeListener onPhotoLikeListener) {
        callbackContainer.addOnPhotoLikeListener(onPhotoLikeListener);
    }

    @Override
    public void removeOnPhotoLikeListener(OnPhotoLikeListener onPhotoLikeListener) {
        callbackContainer.removeOnPhotoLikeListener(onPhotoLikeListener);
    }

    @Override
    public void addOnCommentsReceivedListener(OnCommentsReceivedListener onCommentsReceivedListener){
        callbackContainer.addOnCommentsReceivedListener(onCommentsReceivedListener);
    }

    @Override
    public void removeOnCommentsReceivedListener(OnCommentsReceivedListener onCommentsReceivedListener){
        callbackContainer.removeOnCommentsReceivedListener(onCommentsReceivedListener);
    }

    @Override
    public void addOnPhotosReceivedListener(OnPhotosReceivedListener onPhotosReceivedListener) {
        callbackContainer.addOnPhotosReceivedListener(onPhotosReceivedListener);
    }

    @Override
    public void removeOnPhotosReceivedListener(OnPhotosReceivedListener onPhotosReceivedListener){
        callbackContainer.removeOnPhotosReceivedListener(onPhotosReceivedListener);
    }

    @Override
    public void addOnSearchPhotosResultListener(OnSearchedPhotosReceivedListener onSearchedPhotosReceivedListener){
        callbackContainer.addOnSearchPhotosResultListener(onSearchedPhotosReceivedListener);
    }

    @Override
    public void removeOnSearchPhotosResultListener(OnSearchedPhotosReceivedListener onSearchedPhotosReceivedListener){
        callbackContainer.removeOnSearchPhotosResultListener(onSearchedPhotosReceivedListener);
    }

    void bootstrap(){
        internetAvailableBroadcastReceiver = new BroadcastReceiver(){

            @Override
            public void onReceive(Context context, Intent intent) {
                if (webSocketClient != null) {

                    if (isOnline() && !webSocketClient.isConnected())
                        webSocketClient.connect();
                    else if (!isOnline()) {
                        webSocketClient.disconnect();
                    }
                }
            }
        };

        if (webSocketClient != null) {
            webSocketClient.setMessageListener(this);
            webSocketClient.connect();
        }
    }

    @Override
    public void loadPhotos() {

        String url = urlBuilder.getLoadPhotosApiUrl();
        HttpGetExecutor executor = httpExecutorFactory.createHttpGetExecutor(url);

        if (loadPhotosETag != null)
            executor.addHeaderField("if-modified-since", loadPhotosETag);

        final RequestType requestType = RequestType.LOAD_PHOTOS;

        LoadPhotosAsyncTask task = new LoadPhotosAsyncTask(executor, context, new LoadPhotosAsyncTask.GetPhotosCallback() {
            @Override
            public void onPhotosResult(PhotoQueryResult queryResult) {
                removeOpenRequest(requestType);
                callbackContainer.notifyOnPhotos(queryResult);
            }

            @Override
            public void onPhotosError(HttpResult httpResult) {
                removeOpenRequest(requestType);
                callbackContainer.notifyOnPhotosFailed(httpResult);
            }

            @Override
            public void onNewETag(String eTag) {
                loadPhotosETag = eTag;
            }

            @Override
            public void onNoNewPhotosAvailable() {

                removeOpenRequest(requestType);
                callbackContainer.notifyOnNoNewPhotosAvailable();
            }

        });
        addOpenRequest(requestType);
        task.execute();
    }

    private void removeOpenRequest(RequestType requestType) {
        callbackContainer.removeOpenRequest(requestType);
        callbackContainer.determineShouldDismissProgressDialog(requestType);
    }

    @Override
    public void loadMorePhotos() {
        String url = urlBuilder.getLoadMorePhotosApiUrl();
        HttpGetExecutor executor = httpExecutorFactory.createHttpGetExecutor(url);
        final RequestType requestType = RequestType.LOAD_PHOTOS;
        LoadMorePhotosAsyncTask task = new LoadMorePhotosAsyncTask(executor, context, new LoadPhotosAsyncTask.GetPhotosCallback() {
            @Override
            public void onPhotosResult(PhotoQueryResult queryResult) {
                removeOpenRequest(requestType);
                callbackContainer.notifyOnPhotos(queryResult);
            }

            @Override
            public void onPhotosError(HttpResult httpResult) {
                removeOpenRequest(requestType);
                callbackContainer.notifyOnPhotosFailed(httpResult);
            }

            @Override
            public void onNewETag(String eTag) {

            }

            @Override
            public void onNoNewPhotosAvailable() {

            }

        });
        addOpenRequest(requestType);
        task.execute();
    }

    @Override
    public void likePhoto(int photoId) {
        String url = urlBuilder.getLikePhotoApiUrl(photoId);
        HttpPutExecutor executor = httpExecutorFactory.createHttpPutExecutor(url);
        final RequestType requestType = RequestType.LIKE_PHOTO;
        LikeTable likeTable = new LikeTable(DbConnection.getInstance(context));
        LikeOrDislikePhotoAsyncTask task = new LikePhotoAsyncTask(executor, likeTable, photoId, new LikeOrDislikePhotoAsyncTask.OnVotePhotoResultListener() {

            @Override
            public void onPhotoLiked(int photoId) {
                removeOpenRequest(requestType);
                callbackContainer.notifyOnPhotoLiked(photoId);
            }

            @Override
            public void onPhotoDisliked(int photoId) {
                removeOpenRequest(requestType);
                callbackContainer.notifyOnPhotoDisliked(photoId);
            }

            @Override
            public void onPhotoLikeFailed(int photoId, HttpResult httpResult) {
                removeOpenRequest(requestType);
                callbackContainer.notifyOnPhotoLikeFailed(photoId, httpResult);
            }

        });
        addOpenRequest(requestType);
        task.execute();
    }

    @Override
    public boolean hasUserLikedPhoto(int photoId){
        LikeTable likeTable = new LikeTable(dbConnection);
        likeTable.openDatabase();
        boolean hasLiked = likeTable.hasUserLikedPhoto(photoId);
        likeTable.closeDatabase();
        return hasLiked;
    }

    @Override
    public void loadComments(int photoId){
        String url = urlBuilder.getLoadCommentsApiUrl(photoId);
        HttpGetExecutor executor = httpExecutorFactory.createHttpGetExecutor(url);
        final RequestType requestType = RequestType.LOAD_COMMENTS;
        LoadCommentsAsyncTask task = new LoadCommentsAsyncTask(executor, photoId, new LoadCommentsAsyncTask.OnCommentsResultListener() {

            @Override
            public void onGetComments(int photoId, List<Comment> comments) {
                removeOpenRequest(requestType);
                callbackContainer.notifyOnComments(photoId, comments);
            }

            @Override
            public void onGetCommentsFailed(int photoId, HttpResult httpResult) {
                removeOpenRequest(requestType);
                callbackContainer.notifyOnCommentsFailed(photoId, httpResult);
            }

        });
        addOpenRequest(requestType);
        task.execute();
    }

    @Override
    public void resetLikeForPhoto(int photoId) {
        String url = urlBuilder.getResetLikeForPhotoApiUrl(photoId);
        HttpPutExecutor executor = httpExecutorFactory.createHttpPutExecutor(url);
        final RequestType requestType = RequestType.LIKE_PHOTO;
        LikeTable likeTable = new LikeTable(DbConnection.getInstance(context));
        LikeOrDislikePhotoAsyncTask task = new DislikePhotoAsyncTask(executor, likeTable, photoId, new LikeOrDislikePhotoAsyncTask.OnVotePhotoResultListener() {
            @Override
            public void onPhotoLiked(int photoId) {
                removeOpenRequest(requestType);
                callbackContainer.notifyOnPhotoLiked(photoId);
            }

            @Override
            public void onPhotoDisliked(int photoId) {
                removeOpenRequest(requestType);
                callbackContainer.notifyOnPhotoDisliked(photoId);
            }

            @Override
            public void onPhotoLikeFailed(int photoId, HttpResult httpResult) {
                removeOpenRequest(requestType);
                callbackContainer.notifyOnPhotoLikeFailed(photoId, httpResult);
            }
        });
        addOpenRequest(requestType);
        task.execute();
    }

    @Override
    public void deleteComment(int commentId) {
        String url = urlBuilder.getDeleteCommentApiUrl(commentId);
        HttpDeleteExecutor executor = httpExecutorFactory.createHttpDeleteExecutor(url);
        final RequestType requestType = RequestType.DELETE_COMMENT;
        DeleteCommentAsyncTask task = new DeleteCommentAsyncTask(executor, commentId, new DeleteCommentAsyncTask.OnDeleteCommentResultListener() {
            @Override
            public void onCommentDeleted(int commentId) {
                removeOpenRequest(requestType);
                callbackContainer.notifyOnCommentDeleted(commentId);
            }

            @Override
            public void onCommentDeleteFailed(int commentId, HttpResult httpResult) {
                removeOpenRequest(requestType);
                callbackContainer.notifyOnCommentDeleteFailed(commentId, httpResult);
            }

        });
        addOpenRequest(requestType);
        task.execute();
    }

    @Override
    public void deletePhoto(int photoId){
        String url = urlBuilder.getDeletePhotoApiUrl(photoId);
        HttpDeleteExecutor executor = httpExecutorFactory.createHttpDeleteExecutor(url);
        final RequestType requestType = RequestType.DELETE_PHOTO;
        DeletePhotoAsyncTask task = new DeletePhotoAsyncTask(executor, photoId, new DeletePhotoAsyncTask.OnDeletePhotoResultListener() {

            @Override
            public void onPhotoDeleted(int photoId) {
                removeOpenRequest(requestType);
                callbackContainer.notifyOnPhotoDeleted(Photo.getImageFilePathForPhotoId(context, photoId), photoId);
            }

            @Override
            public void onPhotoDeleteFailed(int photoId, HttpResult httpResult) {
                removeOpenRequest(requestType);
                callbackContainer.notifyOnDeletePhotoFailed(photoId, httpResult);
            }

        });
        addOpenRequest(requestType);
        task.execute();
    }

    @Override
    public void uploadComment(int photoId, String comment) {
        String url = urlBuilder.getUploadCommentApiUrl(photoId);
        HttpPostExecutor httpPostExecutor = httpExecutorFactory.createHttpPostExecutor(url);
        final RequestType requestType = RequestType.UPLOAD_COMMENT;
        StoreCommentAsyncTask task = new StoreCommentAsyncTask(httpPostExecutor, photoId, comment, new StoreCommentAsyncTask.OnCommentSentListener() {
            @Override
            public void onCommentSent(Comment comment) {
                removeOpenRequest(requestType);
                callbackContainer.notifyOnNewComment(comment);
            }

            @Override
            public void onSendCommentFailed(HttpResult httpResult) {
                removeOpenRequest(requestType);
                callbackContainer.notifyOnCommentSentFailed(httpResult);
            }
        });
        addOpenRequest(requestType);
        task.execute();
    }

    @Override
    public void addOnPhotoUploadListener(OnPhotoUploadListener onPhotoUploadListener) {
        callbackContainer.addOnPhotoUploadListener(onPhotoUploadListener);
    }

    @Override
    public void removeOnPhotoUploadListener(OnPhotoUploadListener onPhotoUploadListener) {
        callbackContainer.removeOnPhotoUploadListener(onPhotoUploadListener);
    }

    @Override
    public void addOnPhotoDeletedListener(OnPhotoDeletedListener onPhotoDeletedListener) {
        callbackContainer.addOnPhotoDeletedListener(onPhotoDeletedListener);
    }

    @Override
    public void removeOnPhotoDeletedListener(OnPhotoDeletedListener onPhotoDeletedListener) {
        callbackContainer.removeOnPhotoDeletedListener(onPhotoDeletedListener);
    }

    @Override
    public void addOnNewPhotoReceivedListener(OnNewPhotoReceivedListener onNewPhotoReceivedListener) {
        callbackContainer.addOnNewPhotoReceivedListener(onNewPhotoReceivedListener);
    }

    @Override
    public void removeOnNewPhotoReceivedListener(OnNewPhotoReceivedListener onNewPhotoReceivedListener) {
        callbackContainer.removeOnNewPhotoReceivedListener(onNewPhotoReceivedListener);
    }

    @Override
    public void addOnNewCommentReceivedListener(OnNewCommentReceivedListener onNewCommentReceivedListener) {
        callbackContainer.addOnNewCommentReceivedListener(onNewCommentReceivedListener);
    }

    @Override
    public void removeOnNewCommentReceivedListener(OnNewCommentReceivedListener onNewCommentReceivedListener) {
        callbackContainer.removeOnNewCommentReceivedListener(onNewCommentReceivedListener);
    }

    @Override
    public void addOnUploadCommentListener(OnCommentUploadListener onCommentUploadListener) {
        callbackContainer.addOnUploadCommentListener(onCommentUploadListener);
    }

    @Override
    public void removeOnUploadCommentListener(OnCommentUploadListener onCommentUploadListener) {
        callbackContainer.removeOnUploadCommentListener(onCommentUploadListener);
    }

    @Override
    public void addOnCommentDeletedListener(OnCommentDeletedListener onCommentDeletedListener) {
        callbackContainer.addOnCommentDeletedListener(onCommentDeletedListener);
    }

    @Override
    public void removeOnCommentDeletedListener(OnCommentDeletedListener onCommentDeletedListener) {
        callbackContainer.removeOnCommentDeletedListener(onCommentDeletedListener);
    }

    @Override
    public boolean hasOpenRequestsOfType(RequestType requestType) {
        return callbackContainer.hasOpenRequestsOfType(requestType);
    }

    @Override
    public void searchMorePhotos(){
        String url = urlBuilder.getSearchMorePhotosApiUrl();
        HttpGetExecutor executor = httpExecutorFactory.createHttpGetExecutor(url);
        final RequestType requestType = RequestType.SEARCH_PHOTOS;
        SearchMorePhotosAsyncTask searchPhotosAsyncTask = new SearchMorePhotosAsyncTask(executor, context, new SearchPhotosAsyncTask.OnSearchPhotosResultCallback() {
            @Override
            public void onSearchPhotosResult(PhotoQueryResult photoQueryResult) {
                removeOpenRequest(requestType);
                callbackContainer.notifyOnSearchPhotosResult(photoQueryResult);
            }

            @Override
            public void onSearchPhotosError(HttpResult httpResult) {
                removeOpenRequest(requestType);
                callbackContainer.notifyOnSearchPhotosError(lastSearchQuery, httpResult);
            }
        });
        addOpenRequest(requestType);
        searchPhotosAsyncTask.execute();
    }

    @Override
    public void searchPhotos(final String query) {
        String url = urlBuilder.getSearchPhotosApiUrl(query);
        HttpGetExecutor executor = httpExecutorFactory.createHttpGetExecutor(url);
        final RequestType requestType = RequestType.SEARCH_PHOTOS;
        SearchPhotosAsyncTask searchPhotosAsyncTask = new SearchPhotosAsyncTask(executor, context, query, new SearchPhotosAsyncTask.OnSearchPhotosResultCallback() {
            @Override
            public void onSearchPhotosResult(PhotoQueryResult photoQueryResult) {
                lastSearchQuery = query;
                removeOpenRequest(requestType);
                callbackContainer.notifyOnSearchPhotosResult(photoQueryResult);
            }

            @Override
            public void onSearchPhotosError(HttpResult httpResult) {
                lastSearchQuery = query;
                removeOpenRequest(requestType);
                callbackContainer.notifyOnSearchPhotosError(query, httpResult);
            }
        });
        addOpenRequest(requestType);
        searchPhotosAsyncTask.execute();
    }

    @Override
    public void uploadPhoto(byte[] imageBytes, String description) throws IOException, JSONException {

        if (imageBytes == null || description == null)
            throw new NullPointerException(imageBytes == null ? "imageBytes is null!" : "description is null!");

        String url = urlBuilder.getUploadPhotoApiUrl();
        HttpPostExecutor httpPostExecutor = httpExecutorFactory.createHttpPostExecutor(url);
        final RequestType requestType = RequestType.UPLOAD_PHOTO;
        final JSONObject jsonObject = createJsonObject(imageBytes, description);
        StorePhotoAsyncTask task = new StorePhotoAsyncTask(httpPostExecutor, new StorePhotoAsyncTask.OnPhotoStoredCallback() {
            @Override
            public void onPhotoStoreSuccess(Photo photo) {
                removeOpenRequest(requestType);
                Logger.log(TAG, LogLevel.INFO, "onPhotoStoreSuccess()");
                callbackContainer.notifyPhotoUploadSucceeded(photo);
                onNewPhoto(photo);
            }

            @Override
            public void onPhotoStoreError(HttpResult httpResult) {
                removeOpenRequest(requestType);
                Logger.log(TAG, LogLevel.INFO, "onPhotoStoreError()");
                callbackContainer.notifyPhotoUploadFailed(httpResult);
            }

            @Override
            public void onNewETag(String eTag) {

            }

        });
        addOpenRequest(requestType);
        task.execute(jsonObject);
    }

    private void addOpenRequest(RequestType requestType) {
        callbackContainer.addOpenRequest(requestType);
        callbackContainer.determineShouldShowProgressDialog(requestType);
    }

    private JSONObject createJsonObject(byte[] imageBytes, String comment) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("image", Base64.encodeToString(imageBytes, 0, imageBytes.length, Base64.DEFAULT));
        jsonObject.put("comment", comment);
        return jsonObject;
    }

    @Override
    public void onNewPhoto(Photo photo) {
        try {
            photo.saveToImageToCache(context);
            callbackContainer.notifyOnNewPhoto(context, photo);
        } catch (IOException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
        }
    }

    @Override
    public void onNewComment(Comment comment) {
        callbackContainer.notifyOnNewComment(comment);
    }

    @Override
    public void onCommentDeleted(int commentId) {
        callbackContainer.notifyOnCommentDeleted(commentId);
    }

    @Override
    public void onPhotoDeleted(int photoId) {
        callbackContainer.notifyOnPhotoDeleted(Photo.getImageFilePathForPhotoId(context, photoId), photoId);
    }

    @Override
    public void onConnect() {
        registerInternetAvailableBroadcastReceiver();
    }

    private void registerInternetAvailableBroadcastReceiver() {
        try{
            context.unregisterReceiver(internetAvailableBroadcastReceiver);
        }catch(Exception e){ }
    }

    @Override
    public void onDisconnect() {
        unregisterInternetAvailableBroadcastReceiver();
    }

    private void unregisterInternetAvailableBroadcastReceiver() {
        try{
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(INTENT_CONNECTIVITY_CHANGE);
            context.registerReceiver(internetAvailableBroadcastReceiver, intentFilter);
        }catch(Exception e){ }
    }

    private boolean isOnline(){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if(activeNetworkInfo == null){
            return false;
        } else {
            NetworkInfo.State state = activeNetworkInfo.getState();
            return state == NetworkInfo.State.CONNECTED;
        }
    }

    public void registerActivity(PhotoStreamActivity activity) {
        callbackContainer.registerActivity(activity);
    }

    public void unregisterActivity(PhotoStreamActivity activity) {
        callbackContainer.unregisterActivity(activity);
    }

    public void resetEtag() {
        loadPhotosETag = null;
    }
}
