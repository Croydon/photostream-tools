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

package hochschuledarmstadt.photostream_tools.examples.photo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import hochschuledarmstadt.photostream_tools.adapter.PhotoAdapter;
import hochschuledarmstadt.photostream_tools.examples.R;
import hochschuledarmstadt.photostream_tools.model.Photo;


public class SimplePhotoAdapter extends PhotoAdapter<SimplePhotoAdapter.PhotoViewHolder, Photo> {

    private final Context context;

    public SimplePhotoAdapter(Context context){
        super();
        this.context = context;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhotoViewHolder(LayoutInflater.from(context).inflate(R.layout.photo_item, parent, false));
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        recycleBitmapIfNecessary(holder.imageView);
        Photo photo = getItemAtPosition(position);
        Bitmap bitmap = BitmapFactory.decodeFile(photo.getImageFilePath());
        holder.imageView.setImageBitmap(bitmap);
        holder.textView.setText(photo.getComment());
    }

    @Override
    public void onViewRecycled(PhotoViewHolder holder) {
        recycleBitmapIfNecessary(holder.imageView);
        super.onViewRecycled(holder);
    }

    private void recycleBitmapIfNecessary(ImageView imageView) {
        if (imageView.getDrawable() != null && imageView.getDrawable() instanceof BitmapDrawable){
            final Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            if (bitmap != null && !bitmap.isRecycled())
                bitmap.recycle();
        }
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder {

        public final TextView textView;
        public final ImageView imageView;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            textView = (TextView) itemView.findViewById(R.id.textView);
        }
    }

}
