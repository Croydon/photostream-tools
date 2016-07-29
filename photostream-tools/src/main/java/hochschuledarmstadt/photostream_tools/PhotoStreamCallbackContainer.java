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
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hochschuledarmstadt.photostream_tools.callback.OnCommentCountChangedListener;
import hochschuledarmstadt.photostream_tools.callback.OnCommentDeletedListener;
import hochschuledarmstadt.photostream_tools.callback.OnCommentUploadFailedListener;
import hochschuledarmstadt.photostream_tools.callback.OnCommentsReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnNewCommentReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnNewPhotoReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotoDeletedListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotoLikeListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotoUploadListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotosReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnRequestListener;
import hochschuledarmstadt.photostream_tools.callback.OnSearchedPhotosReceivedListener;
import hochschuledarmstadt.photostream_tools.model.Comment;
import hochschuledarmstadt.photostream_tools.model.HttpError;
import hochschuledarmstadt.photostream_tools.model.Photo;
import hochschuledarmstadt.photostream_tools.model.PhotoQueryResult;

class PhotoStreamCallbackContainer {

    private static final Handler handler = new Handler(Looper.getMainLooper());

    private final HashMap<RequestType, Integer> openRequests = new HashMap<>();

    private List<OnPhotoUploadListener> onPhotoUploadListeners = new ArrayList<>();
    private List<OnPhotosReceivedListener> onPhotosReceivedListeners = new ArrayList<>();
    private List<OnNewPhotoReceivedListener> onNewPhotoReceivedListeners = new ArrayList<>();
    private List<OnPhotoDeletedListener> onPhotoDeletedListeners = new ArrayList<>();
    private List<OnPhotoLikeListener> onPhotoLikeListeners = new ArrayList<>();
    private List<OnSearchedPhotosReceivedListener> onSearchPhotosListeners = new ArrayList<>();
    private List<OnCommentsReceivedListener> onCommentsReceivedListeners = new ArrayList<>();
    private List<OnCommentDeletedListener> onCommentDeletedListeners = new ArrayList<>();
    private List<OnCommentUploadFailedListener> onCommentUploadFailedListeners = new ArrayList<>();
    private List<OnNewCommentReceivedListener> onNewCommentReceivedListeners = new ArrayList<>();
    private List<OnCommentCountChangedListener> onCommentCountChangedListeners = new ArrayList<>();

    private final HashMap<RequestType, List<? extends OnRequestListener>> requestListenerMap = new HashMap<>();
    private List<PhotoStreamActivity> activities = new ArrayList<>();

    public PhotoStreamCallbackContainer(){
        addListenerToMap();
    }

    private void addListenerToMap() {
        requestListenerMap.put(RequestType.UPLOAD_PHOTO, onPhotoUploadListeners);
        requestListenerMap.put(RequestType.LOAD_PHOTOS, onPhotosReceivedListeners);
        requestListenerMap.put(RequestType.DELETE_PHOTO, onPhotoDeletedListeners);
        requestListenerMap.put(RequestType.LIKE_PHOTO, onPhotoLikeListeners);
        requestListenerMap.put(RequestType.SEARCH_PHOTOS, onSearchPhotosListeners);
        requestListenerMap.put(RequestType.LOAD_COMMENTS, onCommentsReceivedListeners);
        requestListenerMap.put(RequestType.UPLOAD_COMMENT, onCommentUploadFailedListeners);
        requestListenerMap.put(RequestType.DELETE_COMMENT, onCommentDeletedListeners);
    }

    public void clear(){
        openRequests.clear();
        requestListenerMap.clear();
        activities.clear();
    }

    public boolean hasOpenRequestsOfType(RequestType requestType) {
        return openRequests.containsKey(requestType) && openRequests.get(requestType) > 0;
    }

    private <T> void addListener(List<T> listeners, T listener, RequestType requestType){
        if (!listeners.contains(listener))
            listeners.add(listener);
        if (listener instanceof OnRequestListener && hasOpenRequestsOfType(requestType))
            ((OnRequestListener)listener).onRequestStarted();
    }

    private <T> void removeListener(List<T> listeners, T listener){
        if (listeners.contains(listener))
            listeners.remove(listener);
    }

    public void addOnCommentCountChangedListener(OnCommentCountChangedListener onCommentCountChangedListener){
        addListener(onCommentCountChangedListeners, onCommentCountChangedListener, null);
    }

    public void removeOnCommentCountChangedListener(OnCommentCountChangedListener onCommentCountChangedListener){
        removeListener(onCommentCountChangedListeners, onCommentCountChangedListener);
    }

    public void addOnPhotoUploadListener(OnPhotoUploadListener onPhotoUploadListener) {
        addListener(onPhotoUploadListeners, onPhotoUploadListener, RequestType.UPLOAD_PHOTO);
    }

    public void removeOnPhotoUploadListener(OnPhotoUploadListener onPhotoUploadListener) {
        removeListener(onPhotoUploadListeners, onPhotoUploadListener);
    }

    public void addOnPhotoDeletedListener(OnPhotoDeletedListener onPhotoDeletedListener) {
        addListener(onPhotoDeletedListeners, onPhotoDeletedListener, RequestType.DELETE_PHOTO);
    }

    public void removeOnPhotoDeletedListener(OnPhotoDeletedListener onPhotoDeletedListener){
        removeListener(onPhotoDeletedListeners, onPhotoDeletedListener);
    }

    public void addOnNewPhotoReceivedListener(OnNewPhotoReceivedListener onNewPhotoReceivedListener) {
        addListener(onNewPhotoReceivedListeners, onNewPhotoReceivedListener, null);
    }

    public void removeOnNewPhotoReceivedListener(OnNewPhotoReceivedListener onNewPhotoReceivedListener){
        removeListener(onNewPhotoReceivedListeners, onNewPhotoReceivedListener);
    }

    public void addOnNewCommentReceivedListener(OnNewCommentReceivedListener onNewCommentReceivedListener) {
        addListener(onNewCommentReceivedListeners, onNewCommentReceivedListener, null);
    }

    public void removeOnNewCommentReceivedListener(OnNewCommentReceivedListener onNewCommentReceivedListener){
        removeListener(onNewCommentReceivedListeners, onNewCommentReceivedListener);
    }

    public void addOnUploadCommentListener(OnCommentUploadFailedListener onCommentUploadFailedListener) {
        addListener(onCommentUploadFailedListeners, onCommentUploadFailedListener, RequestType.UPLOAD_COMMENT);
    }

    public void removeOnUploadCommentListener(OnCommentUploadFailedListener onCommentUploadFailedListener){
        removeListener(onCommentUploadFailedListeners, onCommentUploadFailedListener);
    }

    public void addOnCommentDeletedListener(OnCommentDeletedListener onCommentDeletedListener) {
        addListener(onCommentDeletedListeners, onCommentDeletedListener, RequestType.DELETE_COMMENT);
    }

    public void removeOnCommentDeletedListener(OnCommentDeletedListener onCommentDeletedListener){
        removeListener(onCommentDeletedListeners, onCommentDeletedListener);
    }

    public void addOnPhotoLikeListener(OnPhotoLikeListener onPhotoLikeListener) {
        addListener(onPhotoLikeListeners, onPhotoLikeListener, RequestType.LIKE_PHOTO);
    }

    public void removeOnPhotoLikeListener(OnPhotoLikeListener onPhotoLikeListener) {
        removeListener(onPhotoLikeListeners, onPhotoLikeListener);
    }

    public void addOnCommentsReceivedListener(OnCommentsReceivedListener onCommentsReceivedListener) {
        addListener(onCommentsReceivedListeners, onCommentsReceivedListener, RequestType.LOAD_COMMENTS);
    }

    public void removeOnCommentsReceivedListener(OnCommentsReceivedListener onCommentsReceivedListener) {
        removeListener(onCommentsReceivedListeners, onCommentsReceivedListener);
    }

    public void addOnPhotosReceivedListener(OnPhotosReceivedListener onPhotosReceivedListener) {
        addListener(onPhotosReceivedListeners, onPhotosReceivedListener, RequestType.LOAD_PHOTOS);
    }

    public void removeOnPhotosReceivedListener(OnPhotosReceivedListener onPhotosReceivedListener) {
        removeListener(onPhotosReceivedListeners, onPhotosReceivedListener);
    }

    public void addOnSearchPhotosResultListener(OnSearchedPhotosReceivedListener onSearchedPhotosReceivedListener) {
        addListener(onSearchPhotosListeners, onSearchedPhotosReceivedListener, RequestType.SEARCH_PHOTOS);
    }

    public void removeOnSearchPhotosResultListener(OnSearchedPhotosReceivedListener onSearchedPhotosReceivedListener) {
        removeListener(onSearchPhotosListeners, onSearchedPhotosReceivedListener);
    }

    public void notifyOnNoNewPhotosAvailable() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (OnPhotosReceivedListener onPhotosReceivedListener : onPhotosReceivedListeners) {
                    onPhotosReceivedListener.onNoNewPhotosAvailable();
                }
            }
        });
    }

    public void addOpenRequest(RequestType requestType) {
        synchronized (openRequests) {
            int n = 1;
            if (openRequests.containsKey(requestType)) {
                n = openRequests.get(requestType);
                n++;
                openRequests.remove(requestType);
            }
            openRequests.put(requestType, n);
        }
    }

    public int getOpenRequest(RequestType requestType){
        if (openRequests.containsKey(requestType)) {
            return openRequests.get(requestType);
        }
        return 0;
    }

    public void removeOpenRequest(RequestType requestType){
        synchronized (openRequests) {
            if (openRequests.containsKey(requestType)) {
                int amount = openRequests.get(requestType);
                amount--;
                openRequests.remove(requestType);
                if (amount > 0)
                    openRequests.put(requestType, amount);
            }
        }
    }

    public void determineShouldDismissProgressDialog(RequestType requestType) {
        synchronized (openRequests) {
            int amount = getOpenRequest(requestType);
            if (amount == 0)
                notifyDismissProgressDialog(requestType);
        }
    }

    public void determineShouldShowProgressDialog(RequestType requestType) {
        synchronized (openRequests) {
            int amount = getOpenRequest(requestType);
            if (amount == 1)
                notifyShowProgressDialog(requestType);
        }
    }

    public void notifyShowProgressDialog(final RequestType requestType) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                List<? extends OnRequestListener> onRequestListeners = requestListenerMap.get(requestType);
                for (OnRequestListener onRequestListener : onRequestListeners){
                    onRequestListener.onRequestStarted();
                }
            }
        });
    }

    public void notifyDismissProgressDialog(final RequestType requestType) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                List<? extends OnRequestListener> onRequestListeners = requestListenerMap.get(requestType);
                for (OnRequestListener onRequestListener : onRequestListeners) {
                    onRequestListener.onRequestFinished();
                }
            }
        });
    }

    public void notifyOnNewPhoto(Context context, Photo photo) {
        internalNotifyOnNewPhoto(photo);
        if (appIsInBackground() && photoIsNotFromThisUser(photo)) {
            Intent newPhotoIntent = new Intent(IPhotoStreamClient.INTENT_ACTION_NEW_PHOTO_AVAILABLE);
            newPhotoIntent.putExtra(IPhotoStreamClient.INTENT_KEY_PHOTO, photo);
            newPhotoIntent.setPackage(context.getPackageName());
            context.sendBroadcast(newPhotoIntent);
        }
    }

    private void internalNotifyOnNewPhoto(Photo photo) {
        for (OnNewPhotoReceivedListener listener : onNewPhotoReceivedListeners){
            listener.onNewPhotoReceived(photo);
        }
    }

    private boolean photoIsNotFromThisUser(Photo photo) {
        return !photo.isDeleteable();
    }

    public void notifyOnPhotoLikeFailed(int photoId, HttpError httpError) {
        for (OnPhotoLikeListener listener : onPhotoLikeListeners)
            listener.onPhotoLikeFailed(photoId, httpError);
    }

    public void notifyOnPhotoLiked(int photoId) {
        for (OnPhotoLikeListener onPhotoLikeListener : onPhotoLikeListeners){
            onPhotoLikeListener.onPhotoLiked(photoId);
        }
    }

    public void notifyOnPhotoDisliked(int photoId) {
        for (OnPhotoLikeListener onPhotoLikeListener : onPhotoLikeListeners){
            onPhotoLikeListener.onPhotoDisliked(photoId);
        }
    }

    public void notifyOnDeletePhotoFailed(int photoId, HttpError httpError) {
        for (OnPhotoDeletedListener listener : onPhotoDeletedListeners){
            listener.onPhotoDeleteFailed(photoId, httpError);
        }
    }

    public void notifyOnPhotoDeleted(int photoId) {
        for (OnPhotoDeletedListener listener : onPhotoDeletedListeners){
            listener.onPhotoDeleted(photoId);
        }
    }

    public void notifyOnCommentDeleted(int commentId) {
        for (OnCommentDeletedListener listener : onCommentDeletedListeners)
            listener.onCommentDeleted(commentId);
    }

    public void notifyOnCommentDeleteFailed(int commentId, HttpError httpError) {
        for (OnCommentDeletedListener listener : onCommentDeletedListeners)
            listener.onCommentDeleteFailed(commentId, httpError);
    }

    public void notifyOnCommentsFailed(int photoId, HttpError httpError) {
        for (OnCommentsReceivedListener listener : onCommentsReceivedListeners)
            listener.onReceiveCommentsFailed(photoId, httpError);
    }

    public void notifyOnComments(int photoId, List<Comment> comments) {
        for (OnCommentsReceivedListener listener : onCommentsReceivedListeners)
            listener.onCommentsReceived(photoId, comments);
    }

    public boolean appIsInBackground() {
        return !activities.isEmpty();
    }

    public void notifyOnPhotosFailed(HttpError httpError) {
        for (OnPhotosReceivedListener resultListener : onPhotosReceivedListeners)
            resultListener.onReceivePhotosFailed(httpError);
    }

    public void notifyOnPhotos(PhotoQueryResult photoQueryResult) {
        for (OnPhotosReceivedListener onPhotosReceivedListener : onPhotosReceivedListeners){
            onPhotosReceivedListener.onPhotosReceived(photoQueryResult);
        }
    }

    public void notifyOnSearchPhotosError(String query, HttpError httpError) {
        for (OnSearchedPhotosReceivedListener listener : onSearchPhotosListeners)
            listener.onReceiveSearchedPhotosFailed(query, httpError);
    }

    public void notifyOnSearchPhotosResult(PhotoQueryResult photoQueryResult) {
        for (OnSearchedPhotosReceivedListener listener : onSearchPhotosListeners)
            listener.onSearchedPhotosReceived(photoQueryResult);
    }

    public void notifyOnCommentSentFailed(HttpError httpError) {
        for (OnCommentUploadFailedListener listener : onCommentUploadFailedListeners)
            listener.onCommentUploadFailed(httpError);
    }

    public void notifyOnNewComment(Comment comment) {
        for (OnNewCommentReceivedListener listener : onNewCommentReceivedListeners)
            listener.onNewCommentReceived(comment);
    }

    public void notifyPhotoUploadFailed(HttpError httpError) {
        for (OnPhotoUploadListener onPhotoUploadListener : onPhotoUploadListeners)
            onPhotoUploadListener.onPhotoUploadFailed(httpError);
    }

    public void notifyPhotoUploadSucceeded(Photo photo) {
        for (OnPhotoUploadListener onPhotoUploadListener : onPhotoUploadListeners)
            onPhotoUploadListener.onPhotoUploaded(photo);
    }

    public void registerActivity(PhotoStreamActivity activity) {
        if (!activities.contains(activity))
            activities.add(activity);
    }

    public void unregisterActivity(PhotoStreamActivity activity) {
        if (activities.contains(activity))
            activities.remove(activity);
    }

    public void notifyOnCommentCountChanged(int photoId, int commentCount) {
        for (OnCommentCountChangedListener listener : onCommentCountChangedListeners){
            listener.onCommentCountChanged(photoId, commentCount);
        }
    }
}
