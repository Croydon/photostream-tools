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

package hochschuledarmstadt.photostream_tools.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Repräsentiert das Ergebnis zu der Abfrage einer Seite von Photos aus dem Stream
 */
public class PhotoQueryResult {

    @SerializedName("has_next_page")
    @Expose
    private boolean nextPage;

    @SerializedName("page")
    @Expose
    private Integer page;

    @SerializedName("photos")
    @Expose
    private List<Photo> photos = new ArrayList<>();

    /**
     * Liefert die Seite
     * @return int
     */
    public int getPage() {
        return page;
    }

    /**
     * Liefert {@code true} zurück, wenn die erste Seite aus dem Stream geladen wurde, ansonsten @code{false}
     * @return {@code true}, wenn erste Seite, ansonsten {@code false}
     */
    public boolean isFirstPage(){
        return page == 1;
    }

    /**
     * Liefert die Photos aus der aktuellen Seite des Streams
     * @return Liste von Photos
     */
    public List<Photo> getPhotos() {
        return photos;
    }

    /**
     * Liefert zurück, ob noch weitere Photos im Stream vorhanden sind
     * @return {@code true}, wenn noch weitere Photos abgerufen werden können, ansonsten {@code false}
     */
    public boolean hasNextPage() {
        return nextPage;
    }
}