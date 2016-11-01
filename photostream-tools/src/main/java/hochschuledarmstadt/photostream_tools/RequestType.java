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

/**
 * Diese Enumeration enthält alle Arten von Requests, die an den Server gesendet werden können
 */
public enum RequestType {
    /**
     * {@link IPhotoStreamClient#loadPhotos()} und {@link IPhotoStreamClient#loadMorePhotos()}
     */
    LOAD_PHOTOS,
    /**
     * {@link IPhotoStreamClient#uploadPhoto(byte[], String)}
     */
    UPLOAD_PHOTO,
    /**
     * {@link IPhotoStreamClient#deletePhoto(int)}
     */
    DELETE_PHOTO,
    /**
     * {@link IPhotoStreamClient#favoritePhoto(int)} und {@link IPhotoStreamClient#unfavoritePhoto(int)}
     */
    FAVORITE_PHOTO,
    /**
     * {@link IPhotoStreamClient#searchPhotos(String)} und {@link IPhotoStreamClient#searchMorePhotos()}
     */
    SEARCH_PHOTOS,
    /**
     * {@link IPhotoStreamClient#loadComments(int)}
     */
    LOAD_COMMENTS,
    /**
     * {@link IPhotoStreamClient#uploadComment(int, String)} (int)}
     */
    UPLOAD_COMMENT,
    /**
     * {@link IPhotoStreamClient#deleteComment(int)}
     */
    DELETE_COMMENT,
}
