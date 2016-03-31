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

package hochschuledarmstadt.photostream_tools.callback;

import hochschuledarmstadt.photostream_tools.model.HttpResult;

/**
 * {@code void onPhotoLiked(int photoId)}<br>
 * Wird aufgerufen wenn ein Photo geliked wurde <br><br>
 * {@code void onPhotoDisliked(int photoId)}<br>
 * Wird aufgerufen wenn ein Like von einem Photo entfernt wurde. <br><br>
 * {@code void onPhotoLikeFailed(int photoId, HttpResult httpResult)} <br>
 * Wird aufgerufen wenn beim Liken oder Disliken eines Photos ein Fehler aufgetreten ist
 *
 */
public interface OnPhotoLikeListener extends OnRequestListener {
    /**
     * Wird aufgerufen wenn ein Photo geliked wurde
     * @param photoId id des Photos
     */
    void onPhotoLiked(int photoId);
    /**
     * Wird aufgerufen wenn ein Like von einem Photo entfernt wurde
     * @param photoId id des Photos
     */
    void onPhotoDisliked(int photoId);

    /**
     * Wird aufgerufen wenn beim Liken oder Disliken eines Photos ein Fehler aufgetreten ist
     * @param photoId id des Photos
     * @param httpResult enthält HTTP Status Code sowie die Fehlernachricht
     */
    void onPhotoLikeFailed(int photoId, HttpResult httpResult);
}
