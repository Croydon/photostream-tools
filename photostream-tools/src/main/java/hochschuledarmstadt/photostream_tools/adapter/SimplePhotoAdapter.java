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
import android.view.MotionEvent;
import android.view.View;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import hochschuledarmstadt.photostream_tools.model.Photo;

/**
 * Mit dieser Klasse können Photos in einer RecyclerView angezeigt werden
 * @param <H> ViewHolder Klasse
 */
public abstract class SimplePhotoAdapter<H extends RecyclerView.ViewHolder> extends BaseAdapter<H, Photo> {

    private static final int LIKE = -10;
    private static final int DISLIKE = -11;

    public SimplePhotoAdapter(ArrayList<Photo> photos){
        super(photos);
    }

    public SimplePhotoAdapter(){
        super(new ArrayList<Photo>());
    }

    /**
     * Liefert das Photo ({@code Photo}) an der Position {@code position} zurück
     * @param position Position in der Liste
     * @return {@code Photo} der Kommentar
     */
    @Override
    public Photo getItemAtPosition(int position) {
        return super.getItemAtPosition(position);
    }

    /**
     * Hängt ein Photo {@code photo} an den <b>Anfang</b> der Liste an
     * @param photo Photo das an den <b>Anfang</b> der Liste hinzugefügt werden soll
     */
    @Override
    public void addAtFront(Photo photo) {
        super.addAtFront(photo);
    }

    /**
     * Hängt ein Photo {@code photo} an das <b>Ende</b> der Liste an
     * @param photo Photo, das an das <b>Ende</b> der Liste hinzugefügt werden soll
     */
    @Override
    public void add(Photo photo) {
        super.add(photo);
    }

    /**
     * Fügt alle Elemente in der Liste {@code photos} an das <b>Ende</b> der Liste an
     * @param photos Liste von Photos, die an das <b>Ende</b> Liste angefügt werden sollen
     */
    @Override
    public void addAll(Collection<? extends Photo> photos) {
        super.addAll(photos);
    }

    /**
     * Ersetzt die aktuelle Liste des Adapters durch eine neue Liste von Photos {@code photos}
     * @param photos die neue Liste von Photos
     */
    @Override
    public void set(Collection<? extends Photo> photos) {
        super.set(photos);
    }

    /**
     * Entfernt ein Photo aus der Liste mit der übergebenen {@code id}
     * @param id id des Photos
     */
    @Override
    public void remove(int id) {
        super.remove(id);
    }

    /**
     * Liefert die Anzahl der Photos in der Liste
     * @return Anzahl der Photos
     */
    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    /**
     * Speichert die aktuelle Liste von Photos in ein Bundle
     * @return bundle
     */
    @Override
    public Bundle saveInstanceState() {
        return super.saveInstanceState();
    }

    /**
     * Stellt die Liste von Photos aus einem Bundle wieder her
     * @param bundle das Bundle, welches die Liste von Photos enthält
     */
    @Override
    public void restoreInstanceState(Bundle bundle) {
        super.restoreInstanceState(bundle);
    }
    
    /**
     * Aktualisiert ein Photo mit der id {@code photoId} auf den Status <b>geliked</b>
     * @param photoId id des Photos
     * @return {@code true}, wenn das Photo innerhalb die Liste vorhanden ist, ansonsten {@code false}
     */
    public boolean setLikeForPhoto(int photoId) {
        return internalSetOrResetLike(photoId, LIKE);
    }

    /**
     * Aktualisiert ein Photo mit der id {@code photoId} auf den Status <b>nicht geliked</b>
     * @param photoId id des Photos
     * @return {@code true}, wenn das Photo innerhalb die Liste vorhanden ist, ansonsten {@code false}
     */
    public boolean resetLikeForPhoto(int photoId) {
        return internalSetOrResetLike(photoId, DISLIKE);
    }

    private boolean internalSetOrResetLike(int photoId, int likeConstant) {
        int itemCount = getItemCount();
        for (int position = 0; position < itemCount; position++) {
            Photo photo = getItemAtPosition(position);
            if (itemHasEqualId(photoId, photo)) {
                photo.setLiked(likeConstant == LIKE);
                notifyItemChanged(position);
                return true;
            }
        }
        return false;
    }

    public void updateCommentCount(int photoId, int comment_count) {
        int itemCount = getItemCount();
        for (int position = 0; position < itemCount; position++) {
            Photo photo = getItemAtPosition(position);
            if (itemHasEqualId(photoId, photo)) {
                try {
                    Field f = photo.getClass().getDeclaredField("commentCount");
                    f.setAccessible(true);
                    f.set(photo, comment_count);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                notifyItemChanged(position);
            }
        }
    }

    public interface OnItemClickListener extends BaseAdapter.OnItemClickListener<Photo>{
        @Override
        void onItemClicked(View v, Photo photo);
    }

    public interface OnItemLongClickListener extends BaseAdapter.OnItemLongClickListener<Photo>{
        @Override
        boolean onItemLongClicked(View v, Photo photo);
    }

    public interface OnItemTouchListener extends BaseAdapter.OnItemTouchListener<Photo>{
        @Override
        boolean onItemTouched(View v, MotionEvent motionEvent, Photo photo);
    }

}
