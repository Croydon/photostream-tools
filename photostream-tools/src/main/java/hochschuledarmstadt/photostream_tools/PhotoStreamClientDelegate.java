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

import org.json.JSONException;

import java.io.IOException;

import hochschuledarmstadt.photostream_tools.callback.OnCommentCountChangedListener;
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


class PhotoStreamClientDelegate implements IPhotoStreamClient {

    private final String activityId;
    private PhotoStreamClientImpl photoStreamClientImpl;

    private boolean photosReceivedListenerRegistered = false;

    public PhotoStreamClientDelegate(String activityId, PhotoStreamClientImpl photoStreamClientImpl) {
        this.activityId = activityId;
        this.photoStreamClientImpl = photoStreamClientImpl;
    }

    @Override
    public void addOnRequestListener(OnRequestListener onRequestListener, RequestType... requestTypes) {
        photoStreamClientImpl.addOnRequestListener(onRequestListener, requestTypes);
    }

    @Override
    public void removeOnRequestListener(OnRequestListener onRequestListener) {
        photoStreamClientImpl.removeOnRequestListener(onRequestListener);
    }

    @Override
    public void addOnPhotoFavoriteListener(OnPhotoFavoredListener onPhotoFavoredListener) {
        photoStreamClientImpl.addOnPhotoLikeListener(onPhotoFavoredListener);
    }

    @Override
    public void removeOnPhotoFavoriteListener(OnPhotoFavoredListener onPhotoFavoredListener) {
        photoStreamClientImpl.removeOnPhotoLikeListener(onPhotoFavoredListener);
    }

    @Override
    public void addOnCommentsReceivedListener(OnCommentsReceivedListener onCommentsReceivedListener) {
        photoStreamClientImpl.addOnCommentsReceivedListener(onCommentsReceivedListener);
    }

    @Override
    public void removeOnCommentsReceivedListener(OnCommentsReceivedListener onCommentsReceivedListener) {
        photoStreamClientImpl.removeOnCommentsReceivedListener(onCommentsReceivedListener);
    }

    @Override
    public void addOnPhotosReceivedListener(OnPhotosReceivedListener onPhotosReceivedListener) {
        photosReceivedListenerRegistered = true;
        photoStreamClientImpl.addOnPhotosReceivedListener(onPhotosReceivedListener);
    }

    public boolean hasOnPhotosReceivedListenerRegistered() {
        return photosReceivedListenerRegistered;
    }

    @Override
    public void removeOnPhotosReceivedListener(OnPhotosReceivedListener onPhotosReceivedListener) {
        photosReceivedListenerRegistered = false;
        photoStreamClientImpl.removeOnPhotosReceivedListener(onPhotosReceivedListener);
    }

    @Override
    public void addOnSearchPhotosResultListener(OnSearchedPhotosReceivedListener onSearchedPhotosReceivedListener) {
        photoStreamClientImpl.addOnSearchPhotosResultListener(onSearchedPhotosReceivedListener);
    }

    @Override
    public void removeOnSearchPhotosResultListener(OnSearchedPhotosReceivedListener onSearchedPhotosReceivedListener) {
        photoStreamClientImpl.removeOnSearchPhotosResultListener(onSearchedPhotosReceivedListener);
    }

    @Override
    public void addOnPhotoUploadListener(OnPhotoUploadListener onPhotoUploadListener) {
        photoStreamClientImpl.addOnPhotoUploadListener(onPhotoUploadListener);
    }

    @Override
    public void removeOnPhotoUploadListener(OnPhotoUploadListener onPhotoUploadListener) {
        photoStreamClientImpl.removeOnPhotoUploadListener(onPhotoUploadListener);
    }

    @Override
    public void addOnPhotoDeletedListener(OnPhotoDeletedListener onPhotoDeletedListener) {
        photoStreamClientImpl.addOnPhotoDeletedListener(onPhotoDeletedListener);
    }

    @Override
    public void removeOnPhotoDeletedListener(OnPhotoDeletedListener onPhotoDeletedListener) {
        photoStreamClientImpl.removeOnPhotoDeletedListener(onPhotoDeletedListener);
    }

    @Override
    public void addOnNewPhotoReceivedListener(OnNewPhotoReceivedListener onNewPhotoReceivedListener) {
        photoStreamClientImpl.addOnNewPhotoReceivedListener(onNewPhotoReceivedListener);
    }

    @Override
    public void removeOnNewPhotoReceivedListener(OnNewPhotoReceivedListener onNewPhotoReceivedListener) {
        photoStreamClientImpl.removeOnNewPhotoReceivedListener(onNewPhotoReceivedListener);
    }

    @Override
    public void addOnNewCommentReceivedListener(OnNewCommentReceivedListener onNewCommentReceivedListener) {
        photoStreamClientImpl.addOnNewCommentReceivedListener(onNewCommentReceivedListener);
    }

    @Override
    public void removeOnNewCommentReceivedListener(OnNewCommentReceivedListener onNewCommentReceivedListener) {
        photoStreamClientImpl.removeOnNewCommentReceivedListener(onNewCommentReceivedListener);
    }

    @Override
    public void addOnUploadCommentFailedListener(OnCommentUploadFailedListener onCommentUploadFailedListener) {
        photoStreamClientImpl.addOnUploadCommentFailedListener(onCommentUploadFailedListener);
    }

    @Override
    public void removeOnUploadCommentFailedListener(OnCommentUploadFailedListener onCommentUploadFailedListener) {
        photoStreamClientImpl.removeOnUploadCommentFailedListener(onCommentUploadFailedListener);
    }

    @Override
    public void addOnCommentDeletedListener(OnCommentDeletedListener onCommentDeletedListener) {
        photoStreamClientImpl.addOnCommentDeletedListener(onCommentDeletedListener);
    }

    @Override
    public void removeOnCommentDeletedListener(OnCommentDeletedListener onCommentDeletedListener) {
        photoStreamClientImpl.removeOnCommentDeletedListener(onCommentDeletedListener);
    }

    @Override
    public void addOnCommentCountChangedListener(OnCommentCountChangedListener onCommentCountChangedListener) {
        photoStreamClientImpl.addOnCommentCountChangedListener(onCommentCountChangedListener);
    }

    @Override
    public void removeOnCommentCountChangedListener(OnCommentCountChangedListener onCommentCountChangedListener) {
        photoStreamClientImpl.removeOnCommentCountChangedListener(onCommentCountChangedListener);
    }

    @Override
    public void uploadPhoto(byte[] imageBytes, String description) throws IOException, JSONException {
        photoStreamClientImpl.uploadPhoto(imageBytes, description);
    }

    @Override
    public void loadPhotos() {
        photoStreamClientImpl.loadPhotos(activityId);
    }

    @Override
    public void loadMorePhotos() {
        photoStreamClientImpl.loadMorePhotos();
    }

    @Override
    public void searchPhotos(String queryPhotoDescription) {
        photoStreamClientImpl.searchPhotos(queryPhotoDescription);
    }

    @Override
    public void searchMorePhotos() {
        photoStreamClientImpl.searchMorePhotos();
    }

    @Override
    public void deletePhoto(int photoId) {
        photoStreamClientImpl.deletePhoto(photoId);
    }

    @Override
    public void favoritePhoto(int photoId) {
        photoStreamClientImpl.likePhoto(photoId);
    }

    @Override
    public void unfavoritePhoto(int photoId) {
        photoStreamClientImpl.resetLikeForPhoto(photoId);
    }

    @Override
    public void loadComments(int photoId) {
        photoStreamClientImpl.loadComments(photoId);
    }

    @Override
    public void uploadComment(int photoId, String comment) {
        photoStreamClientImpl.uploadComment(photoId, comment);
    }

    @Override
    public void deleteComment(int commentId) {
        photoStreamClientImpl.deleteComment(commentId);
    }

    @Override
    public boolean hasOpenRequestOfType(RequestType requestType) {
        return photoStreamClientImpl.hasOpenRequestOfType(requestType);
    }

    public void addActivityMovedToBackground(PhotoStreamActivity activity) {
        photoStreamClientImpl.addActivityMovedToBackground(activity);
    }

    public void removeActivityMovedToBackground(PhotoStreamActivity activity) {
        photoStreamClientImpl.removeActivityMovedToBackground(activity);
    }

    public void addActivityVisible(PhotoStreamActivity activity) {
        photoStreamClientImpl.addActivityVisible(activity);
    }

    public void removeActivityVisible(PhotoStreamActivity activity) {
        photoStreamClientImpl.removeActivityVisible(activity);
    }

    public void clear() {
        photoStreamClientImpl = null;
    }

    public void setShouldReloadFirstPageOfPhotosFromCache(Boolean value) {
        photoStreamClientImpl.setShouldReloadFirstPageOfPhotosFromCache(activityId, value);
    }

    public void clearShouldReloadFirstPageOfPhotosFromCache() {
        photoStreamClientImpl.clearShouldReloadFirstPageOfPhotosFromCache(activityId);
    }
}
