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

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.LayoutRes;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import hochschuledarmstadt.photostream_tools.model.Photo;

public abstract class BasePhotoPagerAdapter extends PagerAdapter{

    @LayoutRes
    private final int layoutResId;
    private ViewGroup container;

    public BasePhotoPagerAdapter(@LayoutRes int layoutResId){
        this.layoutResId = layoutResId;
    }

    private List<Photo> photos = new ArrayList<>();

    @Override
    public int getCount() {
        return photos.size();
    }

    public void set(List<Photo> photos){
        this.photos.clear();
        this.photos = photos;
        notifyDataSetChanged();
    }

    public void addAll(List<Photo> photos){
        this.photos.addAll(photos);
        notifyDataSetChanged();
    }

    public View getViewAtPosition(int position){
        if (container != null)
            return container.findViewWithTag(Integer.valueOf(position));
        else
            return null;
    }

    public View getViewWithId(int id){
        if (container != null)
            return container.findViewById(id);
        return null;
    }

    @Override
    public int getItemPosition(Object object) {
        View v = (View) object;
        int position = Integer.parseInt(v.getTag().toString());
        Photo photo = getPhotoAtPosition(position);
        if (photo != null) {
            for (int i = 0; i < photos.size(); i++) {
                Photo p = photos.get(i);
                if (p.getId() == photo.getId())
                    return (position == i) ? POSITION_UNCHANGED : POSITION_NONE;
            }
        }
        return POSITION_NONE;
    }

    public void updateCommentCount(int photoId, int commentCount){
        for (Photo photo : photos){
            if (photo.getId() == photoId){
                try {
                    Field f = photo.getClass().getDeclaredField("commentCount");
                    f.setAccessible(true);
                    f.set(photo, commentCount);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }finally {
                    notifyDataSetChanged();
                }
            }
        }
    }

    public void remove(int photoId) {
        for (int position = 0; position < photos.size(); position++) {
            Photo photo = photos.get(position);
            if (photo.getId() == photoId){
                photos.remove(position);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void addAtFront(Photo photo) {
        photos.add(0, photo);
        notifyDataSetChanged();
    }

    public Photo getPhotoAtPosition(int position) {
        if (position >= 0 && position < photos.size())
            return photos.get(position);
        else
            return null;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        this.container = container;
        Photo photo = photos.get(position);
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        ViewGroup layout = (ViewGroup) inflater.inflate(layoutResId, container, false);
        layout.setTag(Integer.valueOf(position));
        layout.setId(photo.getId());
        onBindView(layout, position, photo);
        container.addView(layout);
        return layout;
    }

    protected abstract void onBindView(ViewGroup layout, int position, Photo photo);

    @Override
    public void destroyItem(ViewGroup container, int position, Object view) {
        container.removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void restoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        this.photos = new ArrayList<>(savedState.items);
        savedState.items.clear();
        notifyDataSetChanged();
    }

    public Parcelable saveInstanceState() {
        return new SavedState(photos);
    }

    public ArrayList<Photo> getItems() {
        return new ArrayList<>(photos);
    }

    protected static class SavedState implements Parcelable {

        public List<Photo> items;

        protected SavedState(Parcel in) {
            in.readList(items, Photo.class.getClassLoader());
        }

        public SavedState(List<Photo> items){
            this.items = items;
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeList(items);
        }
    }
}
