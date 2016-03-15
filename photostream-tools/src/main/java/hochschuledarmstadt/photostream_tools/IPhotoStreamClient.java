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

import hochschuledarmstadt.photostream_tools.callback.OnCommentsResultListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotoUploadListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotoLikeListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotosResultListener;
import hochschuledarmstadt.photostream_tools.callback.OnSearchPhotosResultListener;
import hochschuledarmstadt.photostream_tools.model.Comment;
import hochschuledarmstadt.photostream_tools.model.Photo;

public interface IPhotoStreamClient {
    void addOnPhotoLikeListener(OnPhotoLikeListener onPhotoLikeListener);
    void removeOnPhotoLikeListener(OnPhotoLikeListener onPhotoLikeListener);
    void addOnGetCommentsResultListener(OnCommentsResultListener onCommentsResultListener);
    void removeOnGetCommentsResultListener(OnCommentsResultListener onCommentsResultListener);
    void addOnPhotosResultListener(OnPhotosResultListener onPhotosResultListener);
    void removeOnPhotosResultListener(OnPhotosResultListener onPhotosResultListener);
    void addOnSearchPhotosResultListener(OnSearchPhotosResultListener onSearchPhotosResultListener);
    void removeOnSearchPhotosResultListener(OnSearchPhotosResultListener onSearchPhotosResultListener);
    void addOnPhotoUploadListener(OnPhotoUploadListener onPhotoUploadListener);
    void removeOnPhotoUploadListener(OnPhotoUploadListener onPhotoUploadListener);
    boolean uploadPhoto(byte[] imageBytes, String comment) throws IOException, JSONException;
    void getPhotos();
    void getMorePhotos();
    void searchPhotos(String query);
    void searchMorePhotos();
    void deletePhoto(Photo photo);
    void likePhoto(int photoId);
    void dislikePhoto(int photoId);
    boolean hasUserAlreadyLikedPhoto(int photoId);
    void getComments(int photoId);
    void sendComment(int photoId, String comment);
    void deleteComment(Comment comment);
    boolean hasOpenRequestsOfType(RequestType requestType);
}
