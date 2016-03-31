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
import hochschuledarmstadt.photostream_tools.model.Photo;

/**
 * {@code void onPhotoUploaded(Photo photo)}<br>
 * Wird aufgerufen, wenn das Photo über den Server veröffentlicht wurde <br> <br>
 * {@code void onPhotoUploadFailed(HttpResult httpResult)}<br>
 * Wird aufgerufen, wenn ein Photo aufgrund eines Fehlers nicht über den Server veröffentlicht werden konnte.
 */
public interface OnPhotoUploadListener extends OnRequestListener {

    /**
     * Wird aufgerufen, wenn das Photo über den Server veröffentlicht wurde
     * @param photo das veröffentlichte Photo
     */
    void onPhotoUploaded(Photo photo);

    /**
     * Wird aufgerufen, wenn ein Photo aufgrund eines Fehlers nicht über den Server veröffentlicht werden konnte.
     * @param httpResult enthält den HTTP Status Code sowie die Fehlernachricht
     */
    void onPhotoUploadFailed(HttpResult httpResult);
}
