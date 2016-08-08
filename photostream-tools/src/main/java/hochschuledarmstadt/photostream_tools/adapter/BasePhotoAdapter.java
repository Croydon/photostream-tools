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

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import hochschuledarmstadt.photostream_tools.R;
import hochschuledarmstadt.photostream_tools.model.Photo;

/**
 * Mit dieser Klasse können Photos in einer RecyclerView angezeigt werden
 * @param <H> ViewHolder Klasse
 */
public abstract class BasePhotoAdapter<H extends RecyclerView.ViewHolder> extends BaseAdapter<H, Photo> {

    private static final int LIKE = -10;
    private static final int DISLIKE = -11;
    private static final int DEFAULT_CACHE_SIZE_IN_MB = 10;

    private Activity activity;
    private List<BitmapLoaderTask> tasks = new ArrayList<>();
    private LruBitmapCache lruBitmapCache;
    private OnImageLoadedListener listener = new InternalBitmapLoaderListener();

    private BasePhotoAdapter(ArrayList<Photo> photos, int cacheSizeInMegaByte){
        super(photos);
        lruBitmapCache = new LruBitmapCache(1000 * 1000 * cacheSizeInMegaByte);
    }

    public BasePhotoAdapter(int cacheSizeInMegaByte){
        this(new ArrayList<Photo>(), cacheSizeInMegaByte);
    }

    public BasePhotoAdapter(){
        this(new ArrayList<Photo>(), DEFAULT_CACHE_SIZE_IN_MB);
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

    @Override
    public void addOnLongClickPlugin(@IdRes int viewId, Plugin<H, Photo> plugin) {
        super.addOnLongClickPlugin(viewId, plugin);
    }

    @Override
    public void addOnClickPlugin(@IdRes int viewId, Plugin<H, Photo> plugin) {
        super.addOnClickPlugin(viewId, plugin);
    }

    /**
     * Speichert die aktuelle Liste von Photos in ein Bundle
     * @return bundle
     */
    @Override
    public Bundle saveInstanceState() {
        if (activity != null) {
            if (activity.isFinishing() || activity.isChangingConfigurations()) {
                destroyReferences();
            }
        }else{
            destroyReferences();
        }
        return super.saveInstanceState();
    }

    private void destroyReferences() {
        for (BitmapLoaderTask task : tasks) {
            Log.d(BasePhotoAdapter.class.getSimpleName(), "cancelled task");
            task.cancel(true);
        }
        tasks.clear();
        lruBitmapCache.destroy();
        listener = null;
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

    @Override
    public void onBindViewHolder(H holder, int position) {
        super.onBindViewHolder(holder, position);
        if (activity == null) {
            try {
                activity = (Activity) holder.itemView.getContext();
            } catch (ClassCastException e) {
            }
        }
    }

    protected void loadBitmapIntoImageViewAsync(H viewHolder, final ImageView imageView, final Photo photo){
        imageView.setImageBitmap(null);
        Integer prevKey = -1;
        try{
            prevKey = Integer.valueOf(imageView.getTag().toString());
        }catch(Exception e){}

        if (cancelPotentialWork(photo.getId(), imageView)) {
            if (prevKey != -1){
                lruBitmapCache.referenceDecrease(prevKey);
            }

            Object tag = viewHolder.itemView.getTag(R.id.should_animate);
            boolean shouldAnimate = tag == null || !tag.equals(Boolean.FALSE);
            if (shouldAnimate)
                viewHolder.itemView.setTag(R.id.should_animate, Boolean.TRUE);

            BitmapLoaderTask task = new BitmapLoaderTask(lruBitmapCache, imageView, photo.getId(), photo.getImageFile(), listener);
            task.setShouldAnimate(shouldAnimate);
            Bitmap placeHolderBitmap = lruBitmapCache.get(photo.getId());
            AsyncDrawable asyncDrawable = new AsyncDrawable(imageView.getContext().getResources(), placeHolderBitmap, task);
            imageView.setImageDrawable(asyncDrawable);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private boolean cancelPotentialWork(int photoId, ImageView imageView) {
        final BitmapLoaderTask bitmapLoaderTask = BitmapLoaderTask.getBitmapLoaderTaskRefFrom(imageView);
        if (bitmapLoaderTask != null) {
            final int workerTaskPhotoId = bitmapLoaderTask.getPhotoId();
            // If photoId is not yet set or it differs from the new data
            if (photoId != workerTaskPhotoId) {
                // Cancel previous task
                return bitmapLoaderTask.cancel(false);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    protected abstract void onBitmapLoadedIntoImageView(ImageView imageView);

    public interface OnItemClickListener<H extends RecyclerView.ViewHolder> extends BaseAdapter.OnItemClickListener<H, Photo>{
        @Override
        void onItemClicked(H viewHolder, View v, Photo photo);
    }

    public interface OnItemLongClickListener<H extends RecyclerView.ViewHolder> extends BaseAdapter.OnItemLongClickListener<H, Photo>{
        @Override
        boolean onItemLongClicked(H viewHolder, View v, Photo photo);
    }

    public interface OnItemTouchListener<H extends RecyclerView.ViewHolder> extends BaseAdapter.OnItemTouchListener<H, Photo>{
        @Override
        boolean onItemTouched(H viewHolder, View v, MotionEvent motionEvent, Photo photo);
    }

    private class InternalBitmapLoaderListener implements OnImageLoadedListener {

        @Override
        public void onTaskStarted(BitmapLoaderTask bitmapLoaderTask) {
            if (!tasks.contains(bitmapLoaderTask))
                tasks.add(bitmapLoaderTask);
        }

        @Override
        public void onTaskFinishedOrCanceled(BitmapLoaderTask bitmapLoaderTask, ImageView imageView) {
            tasks.remove(bitmapLoaderTask);
            if (imageView != null && bitmapLoaderTask.getShouldAnimate())
                onBitmapLoadedIntoImageView(imageView);
        }
    }

}
