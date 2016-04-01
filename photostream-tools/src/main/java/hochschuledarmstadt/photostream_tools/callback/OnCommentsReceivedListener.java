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

import java.util.List;

import hochschuledarmstadt.photostream_tools.model.Comment;
import hochschuledarmstadt.photostream_tools.model.HttpResult;

/**
 * {@link OnCommentsReceivedListener#onCommentsReceived(int, List)}<br>
 * Wird aufgerufen wenn die Kommentare zu einem Photo abefragt werden konnten <br> <br>
 * {@link OnCommentsReceivedListener#onReceiveCommentsFailed(int, HttpResult)} <br>
 * Wird aufgerufen wenn beim Abrufen der Kommentare ein Fehler aufgetreten ist <br> <br>
 */
public interface OnCommentsReceivedListener extends OnRequestListener {

    /**
     * Wird aufgerufen wenn die Kommentare zu einem Photo abefragt werden konnten
     * @param photoId id des Photos zu der die Kommentare zugeordnet sind
     * @param comments die Kommentare
     */
    void onCommentsReceived(int photoId, List<Comment> comments);

    /**
     * Wird aufgerufen wenn beim Abrufen der Kommentare ein Fehler aufgetreten ist
     * @param photoId id des Photos zu der die Kommentare zugeordnet sind
     * @param httpResult enthält den HTTP Status Code, sowie die Fehlernachricht
     */
    void onReceiveCommentsFailed(int photoId, HttpResult httpResult);
}
