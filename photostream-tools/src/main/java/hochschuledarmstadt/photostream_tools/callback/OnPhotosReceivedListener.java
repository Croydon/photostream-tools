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
import hochschuledarmstadt.photostream_tools.model.PhotoQueryResult;

/**
 * {@code void onPhotosReceived(PhotoQueryResult result)}<br>
 * Wird aufgerufen, wenn eine Seite von Photos aus dem Stream geladen werden konnte <br> <br>
 * {@code void onReceivePhotosFailed(HttpResult httpResult)}<br>
 * Wird aufgerufen, wenn eine Seite von Photos aus dem Stream aufgrund eines Fehlers nicht geladen werden konnte. <br> <br>
 * {@code void onNoNewPhotosAvailable()} <br>
 * Wird aufgerufen, wenn keine neuen Photos im Stream vorhanden sind.
 *
 */
public interface OnPhotosReceivedListener extends OnRequestListener {
    /**
     * Wird aufgerufen, wenn eine Seite von Photos aus dem Stream geladen werden konnte
     * @param result enthält die angeforderte Seitenzahl aus dem Stream, sowie die Photos aus dem Stream der Seite
     */
    void onPhotosReceived(PhotoQueryResult result);

    /**
     * Wird aufgerufen, wenn eine Seite von Photos aus dem Stream aufgrund eines Fehlers nicht geladen werden konnte.
     * @param httpResult enthält den HTTP Status Code sowie die Fehlernachricht
     */
    void onReceivePhotosFailed(HttpResult httpResult);

    /**
     * Wird aufgerufen, wenn keine neuen Photos im Stream vorhanden sind.
     */
    void onNoNewPhotosAvailable();
}
