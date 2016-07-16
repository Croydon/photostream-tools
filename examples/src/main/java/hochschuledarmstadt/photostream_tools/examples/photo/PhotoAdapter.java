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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;

import hochschuledarmstadt.photostream_tools.BitmapUtils;
import hochschuledarmstadt.photostream_tools.adapter.SimplePhotoAdapter;
import hochschuledarmstadt.photostream_tools.examples.R;
import hochschuledarmstadt.photostream_tools.model.Photo;


public class PhotoAdapter extends SimplePhotoAdapter<PhotoAdapter.PhotoViewHolder> {

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new PhotoViewHolder(layoutInflater.inflate(R.layout.photo_item, parent, false));
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        BitmapUtils.recycleBitmapFromImageView(holder.imageView);
        Photo photo = getItemAtPosition(position);
        File imageFile = photo.getImageFile();
        Bitmap bitmap = null;
        try {
            bitmap = BitmapUtils.decodeBitmapFromFile(imageFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        holder.imageView.setImageBitmap(bitmap);
        holder.textView.setText(photo.getDescription());
    }

    @Override
    public void onViewRecycled(PhotoViewHolder holder) {
        BitmapUtils.recycleBitmapFromImageView(holder.imageView);
        super.onViewRecycled(holder);
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {

        public final TextView textView;
        public final ImageView imageView;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }

}
