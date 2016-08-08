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
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import hochschuledarmstadt.photostream_tools.R;
import hochschuledarmstadt.photostream_tools.model.BaseItem;

public abstract class Plugin<H extends RecyclerView.ViewHolder, T extends BaseItem & Parcelable> {

    @IdRes
    protected int viewId = View.NO_ID;

    protected BaseAdapter<H, T> adapter;

    void setViewId(@IdRes int viewId) {
        this.viewId = viewId;
    }

     @IdRes int getViewId(){
        return viewId;
    }

    void onItemClicked(H viewHolder, View v, T item) {
        trigger(viewHolder, v, item);
    }

    boolean onItemLongClicked(H viewHolder, View v, T item) {
        if (shouldExecute(viewHolder, v, item)) {
            trigger(viewHolder, v, item);
            return true;
        }else{
            return false;
        }
    }

    protected void dontAnimate(int itemId){
        adapter.dontAnimate(itemId);
    }

    /**
     *  Diese Methode soll als Ergebnis liefern, ob das Event für das übergebene {@code item} verarbeiten soll
     * @param viewHolder Enthält das View Element, welches das Plugin ausgelöst hat
     * @param v die View, welche das Plugin ausgelöst hat
     * @param item
     * @return Es muss {@code true} zurückgegeben werden, wenn das Plugin das Event verarbeiten soll, ansonsten {@code false}
     */
    protected abstract boolean shouldExecute(H viewHolder, View v, T item);
    abstract void trigger(H ViewHolder, View v, T item);
    abstract boolean onBindViewHolder(H viewHolder, int position);
    abstract void saveInstanceState(Bundle bundle);
    abstract void restoreInstanceState(Bundle bundle);
    void setAdapter(BaseAdapter<H, T> adapter){
        this.adapter = adapter;
    }
}
