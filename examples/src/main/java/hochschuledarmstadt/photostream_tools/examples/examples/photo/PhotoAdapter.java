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

package hochschuledarmstadt.photostream_tools.examples.examples.photo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import hochschuledarmstadt.photostream_tools.adapter.BasePhotoAdapter;
import hochschuledarmstadt.photostream_tools.examples.R;
import hochschuledarmstadt.photostream_tools.model.Photo;


public class PhotoAdapter extends BasePhotoAdapter<PhotoAdapter.PhotoViewHolder> {

    // Konstanten für die Animation des angezeigten Bilds
    private static final float BEGIN_SCALE = 0.5f, BEGIN_ALPHA = 0.1f, MAX = 1.0f;
    private static final int DURATION_IN_MILLIS = 500;

    public PhotoAdapter(){
        super();
    }

    public PhotoAdapter(int cacheSizeInMegaByte) {
        super(cacheSizeInMegaByte);
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Objekt referenzieren für das Erzeugen des Layouts für ein Element in der angezeigten Liste
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        // Ein neues Layout für ein Element erzeugen
        View itemView = layoutInflater.inflate(R.layout.photo_item, parent, false);
        // ViewHolder erzeugen und dabei das erzeugte Layout übergeben
        return new PhotoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        // Photo an der angegebenen Position referenzieren
        final Photo photo = getItemAtPosition(position);
        // Die Beschreibung zu dem Photo setzen
        holder.textView.setText(photo.getDescription());
        // Bitmap anhand des Photo Objekts laden und in der ImageView setzen
        loadBitmapIntoImageViewAsync(holder, holder.imageView, photo);
    }

    @Override
    protected void onBitmapLoadedIntoImageView(ImageView imageView) {
        // Bitmap ist hiermit in der ImageView gesetzt.
        // Jetzt kann man zum Beispiel noch eine Animation durchführen
        imageView.setScaleX(BEGIN_SCALE);
        imageView.setScaleY(BEGIN_SCALE);
        imageView.setAlpha(BEGIN_ALPHA);
        imageView.animate().scaleX(MAX).scaleY(MAX).alpha(MAX).setDuration(DURATION_IN_MILLIS).start();
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {

        public final TextView textView;
        public final ImageView imageView;

        // itemView entspricht dem erzeugten Layout aus der OnCreateViewHolder() Methode
        public PhotoViewHolder(View itemView) {
            super(itemView);
            // Views referenzieren, auf die man in der onBindViewHolder
            // Methode zugreifen möchte
            textView = (TextView) itemView.findViewById(R.id.textView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }

}
