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

package hochschuledarmstadt.photostream_tools.adapter;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Collection;

import hochschuledarmstadt.photostream_tools.model.Comment;

/**
 * Mit dieser Klasse können Kommentare in einer RecyclerView angezeigt werden
 * @param <H> ViewHolder Klasse
 */
public abstract class SimpleCommentAdapter<H extends RecyclerView.ViewHolder> extends BaseAdapter<H, Comment>{

    /**
     * Liefert den Kommentar ({@code Comment}) an der Position {@code position} zurück
     * @param position Position in der Liste
     * @return {@code Comment} der Kommentar
     */
    @Override
    public Comment getItemAtPosition(int position) {
        return super.getItemAtPosition(position);
    }

    /**
     * Hängt einen Kommentar {@code comment} an den <b>Anfang</b> der Liste an
     * @param comment Kommentar, der an den <b>Anfang</b> der Liste hinzugefügt werden soll
     */
    @Override
    public void addAtFront(Comment comment) {
        super.addAtFront(comment);
    }

    /**
     * Hängt einen Kommentar {@code comment} an das <b>Ende</b> der Liste an
     * @param comment Kommentar, der an das <b>Ende</b> der Liste hinzugefügt werden soll
     */
    @Override
    public void add(Comment comment) {
        super.add(comment);
    }

    /**
     * Fügt alle Elemente in der Liste {@code comments} an das <b>Ende</b> der Liste an
     * @param comments Liste von Kommentaren, die an das <b>Ende</b> Liste angefügt werden sollen
     */
    @Override
    public void addAll(Collection<? extends Comment> comments) {
        super.addAll(comments);
    }

    /**
     * Ersetzt die aktuelle Liste des Adapters durch eine neue Liste von Kommentaren {@code comments}
     * @param comments die neue Liste von Kommentaren
     */
    @Override
    public void set(Collection<? extends Comment> comments) {
        super.set(comments);
    }

    /**
     * Entfernt einen Kommentar aus der Liste mit der übergebenen {@code id}
     * @param id id des Kommentars
     */
    @Override
    public void remove(int id) {
        super.remove(id);
    }

    /**
     * Liefert die Anzahl der Kommentare in der Liste
     * @return Anzahl der Kommentare
     */
    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    /**
     * Speichert die aktuelle Liste von Kommentaren in ein Bundle
     * @return bundle
     */
    @Override
    public Bundle saveInstanceState() {
        return super.saveInstanceState();
    }

    /**
     * Stellt die Liste von Kommentaren aus einem Bundle wieder her
     * @param bundle das Bundle, welches die Liste von Kommentaren enthält
     */
    @Override
    public void restoreInstanceState(Bundle bundle) {
        super.restoreInstanceState(bundle);
    }

    public interface OnItemClickListener extends BaseAdapter.OnItemClickListener<Comment>{
        @Override
        void onItemClicked(View v, Comment comment);
    }
}
