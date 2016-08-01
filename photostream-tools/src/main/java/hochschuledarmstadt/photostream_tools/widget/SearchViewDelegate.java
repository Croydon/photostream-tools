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

package hochschuledarmstadt.photostream_tools.widget;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.AbsSavedState;
import android.view.MenuItem;
import android.view.View;

/**
 * Verwaltet den Status einer SearchView
 */
public class SearchViewDelegate {

    private MenuItem searchViewMenuItem;
    private SearchView searchView;
    private SavedState savedState;
    private SearchView.OnQueryTextListener onQueryTextListener;

    public SearchViewDelegate(){

    }

    public void setSearchViewMenuItem(MenuItem searchViewMenuItem) {
        this.searchViewMenuItem = searchViewMenuItem;
        this.searchView = (SearchView) MenuItemCompat.getActionView(searchViewMenuItem);
        if (onQueryTextListener != null)
            searchView.setOnQueryTextListener(onQueryTextListener);
        internalRestoreInstanceState();
    }

    private void internalRestoreInstanceState() {
        if (savedState != null) {
            if (savedState.isFocused())
                searchView.requestFocus();
            else
                searchView.clearFocus();
            if (savedState.isExpanded())
                MenuItemCompat.expandActionView(searchViewMenuItem);
            searchView.setQuery(savedState.getQuery(), false);
        }
    }

    /**
     * Speichert den aktuellen Status der SearchView in ein Parcelable Objekt
     * @return {@link Parcelable}
     */
    public Parcelable saveInstanceState(){
        SavedState savedState = new SavedState(AbsSavedState.EMPTY_STATE);
        savedState.setQuery(searchView.getQuery().toString());
        savedState.setFocused(searchView.isFocused());
        savedState.setExpanded(MenuItemCompat.isActionViewExpanded(searchViewMenuItem));
        return savedState;
    }

    /**
     * Stellt den Status einer SearchView aus einem Parcelable Objekt wieder her
     * @param parcelable
     */
    public void restoreInstanceState(Parcelable parcelable){
        this.savedState = (SavedState) parcelable;
    }

    public void setOnQueryTextListener(SearchView.OnQueryTextListener onQueryTextListener) {
        this.onQueryTextListener = onQueryTextListener;
        if (searchView != null)
            searchView.setOnQueryTextListener(onQueryTextListener);
    }

    public static class SavedState extends View.BaseSavedState {

        private boolean focused;
        private boolean expanded;
        private String query;

        public SavedState(Parcel source) {
            super(source);
            focused = source.readInt() == 1;
            expanded = source.readInt() == 1;
            query = source.readString();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(focused ? 1 : 0);
            dest.writeInt(expanded ? 1 : 0);
            dest.writeString(query);
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

        public void setFocused(boolean focused) {
            this.focused = focused;
        }

        public boolean isExpanded() {
            return expanded;
        }

        public void setExpanded(boolean expanded) {
            this.expanded = expanded;
        }

        public boolean isFocused() {
            return focused;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }
    }

}
