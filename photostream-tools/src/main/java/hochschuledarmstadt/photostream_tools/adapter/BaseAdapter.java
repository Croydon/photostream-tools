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

import hochschuledarmstadt.photostream_tools.model.BaseItem;

abstract class BaseAdapter<T extends RecyclerView.ViewHolder, H extends BaseItem & Parcelable> extends RecyclerView.Adapter<T>{

    protected static final String KEY_ITEMS = "KEY_ITEMS";
    protected ArrayList<H> items = new ArrayList<>();

    public BaseAdapter(){
        this(new ArrayList<H>());
    }

    public BaseAdapter(ArrayList<H> items){
        this.items = items;
        setHasStableIds(true);
    }

    /**
     * Liefert das Item {@code item} an der Position {@code position} zurück
     * @param position Position in der Liste
     * @return Item
     */
    public H getItemAtPosition(int position){
        return items.get(position);
    }

    /**
     * Hängt ein Item {@code item} an das <b>Ende</b> der Liste an
     * @param item Item das an das <b>Ende</b> der Liste hinzugefügt werden soll
     */
    public void addAtFront(H item){
        this.items.add(0, item);
        notifyItemInserted(0);
    }

    /**
     * Hängt ein Item {@code item} an den <b>Anfang</b> der Liste an
     * @param item Item das an den <b>Anfang</b> der Liste hinzugefügt werden soll
     */
    public void add(H item){
        this.items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    /**
     * Fügt alle Elemente in der Liste {@code items} an das Ende der Liste an
     * @param items Liste von Items
     */
    public void addAll(Collection<? extends H> items){
        final int itemCountBefore = getItemCount();
        this.items.addAll(items);
        final int lastItemIndex = getItemCount()-1;
        notifyItemRangeInserted(itemCountBefore, lastItemIndex);
    }

    /**
     * Ersetzt die aktuelle Liste des Adapters durch eine neue Liste von Items {@code items}
     * @param items die neue Liste von Items
     */
    public void set(Collection<? extends H> items){
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    /**
     * Entfernt ein Item aus der Liste mit der übergebenen {@code id}
     * @param id id des Items
     */
    public void remove(int id) {
        for (int position = 0; position < items.size(); position++){
            H photo = getItemAtPosition(position);
            if (itemHasEqualId(id, photo)) {
                items.remove(position);
                notifyItemRemoved(position);
                break;
            }
        }
    }

    /**
     * Liefert die Anzahl der Items in der Liste
     * @return Anzahl der Items
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    protected boolean itemHasEqualId(int id, H item){
        return item.getId() == id;
    }

    /**
     * Speichert die aktuelle Liste von Items in ein Bundle
     * @return bundle
     */
    public Bundle saveInstanceState(){
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(KEY_ITEMS, items);
        return bundle;
    }

    /**
     * Stellt die Liste von Items aus einem Bundle wieder her
     * @param bundle das Bundle, welches die Liste von Items enthält
     */
    public void restoreInstanceState(Bundle bundle){
        items = bundle.getParcelableArrayList(KEY_ITEMS);
    }

}
