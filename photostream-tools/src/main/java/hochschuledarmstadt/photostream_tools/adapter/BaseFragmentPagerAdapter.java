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
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import hochschuledarmstadt.photostream_tools.PhotoStreamFragment;
import hochschuledarmstadt.photostream_tools.model.Photo;

public abstract class BaseFragmentPagerAdapter<T extends PhotoStreamFragment> extends FragmentStatePagerAdapter{

    private List<Photo> photos = new ArrayList<>();

    public BaseFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
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

    @Override
    public int getCount() {
        return photos.size();
    }

    protected abstract T createNewFragment();

    @Override
    public Fragment getItem(int position) {
        PhotoStreamFragment fragment = createNewFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("photo", photos.get(position));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return photos.get(position).getDescription();
    }

    public Parcelable saveInstanceState() {
        return new SavedState(photos);
    }

    public void restoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        this.photos.addAll(savedState.photos);
        notifyDataSetChanged();
    }

    @Override
    public Parcelable saveState() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        throw new UnsupportedOperationException();
    }

    public static class SavedState implements Parcelable{

        private List<Photo> photos;

        public SavedState(List<Photo> photos){
            this.photos = photos;
        }

        protected SavedState(Parcel in) {
            photos = new ArrayList<>();
            in.readTypedList(photos, Photo.CREATOR);
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
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedList(photos);
        }
    }

}
