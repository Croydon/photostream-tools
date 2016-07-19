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

/**
 * {@link OnCommentCountChangedListener#onCommentCountChanged(int, int)}<br>
 * Wird aufgerufen, wenn sich die Anzahl der Kommentare zu einem Photo geändert hat.
 */
public interface OnCommentCountChangedListener {
    /**
     * Wird aufgerufen, wenn sich die Anzahl der Kommentare zu dem Photo mit der id {@code photoId} geändert hat.
     * @param photoId id des Photos zu der die Kommentare zugeordnet sind
     * @param commentCount Aktuelle Anzahl der Kommentare zu dem Photo
     */
    void onCommentCountChanged(int photoId, int commentCount);
}
