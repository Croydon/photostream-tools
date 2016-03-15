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

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import hochschuledarmstadt.photostream_tools.model.Photo;

public abstract class SimplePhotoAdapter<T extends RecyclerView.ViewHolder> extends BaseAdapter<T, Photo> {

    private static final int LIKE = -10;
    private static final int DISLIKE = -10;

    public SimplePhotoAdapter(ArrayList<Photo> photos){
        super(photos);
    }

    public SimplePhotoAdapter(){
        super(new ArrayList<Photo>());
    }

    public boolean setLikeForPhoto(int photoId) {
        return internalSetLikeOrDislike(photoId, LIKE);
    }

    public boolean setDislikeForPhoto(int photoId) {
        return internalSetLikeOrDislike(photoId, DISLIKE);
    }

    private boolean internalSetLikeOrDislike(int photoId, int likeConstant) {
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

}
