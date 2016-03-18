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
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;

import hochschuledarmstadt.photostream_tools.model.Id;

public abstract class BaseAdapter<T extends RecyclerView.ViewHolder, H extends Parcelable & Id> extends RecyclerView.Adapter<T>{

    protected static final String KEY_ITEMS = "KEY_ITEMS";

    protected ArrayList<H> items = new ArrayList<>();

    public BaseAdapter(ArrayList<H> items){
        this.items = items;
    }

    public BaseAdapter(){
        this(new ArrayList<H>());
    }

    public H getItemAtPosition(int position){
        return items.get(position);
    }

    public void addAtFront(H item){
        this.items.add(0, item);
        notifyItemInserted(0);
    }

    public void add(H item){
        this.items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public void addAll(Collection<? extends H> items){
        final int itemCountBefore = getItemCount();
        this.items.addAll(items);
        final int lastItemIndex = getItemCount()-1;
        notifyItemRangeInserted(itemCountBefore, lastItemIndex);
    }

    public void set(Collection<? extends H> items){
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void remove(int id) {
        int removedAt = -1;
        for (int position = 0; position < items.size(); position++){
            H photo = getItemAtPosition(position);
            if (itemHasEqualId(id, photo)) {
                items.remove(position);
                removedAt = position;
                break;
            }
        }
        if (removedAt != -1)
            notifyItemRemoved(removedAt);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    protected boolean itemHasEqualId(int id, H item){
        return item.getId() == id;
    }

    public Bundle saveInstanceState(){
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(KEY_ITEMS, items);
        return bundle;
    }

    public void restoreInstanceState(Bundle bundle){
        items = bundle.getParcelableArrayList(KEY_ITEMS);
    }

}
