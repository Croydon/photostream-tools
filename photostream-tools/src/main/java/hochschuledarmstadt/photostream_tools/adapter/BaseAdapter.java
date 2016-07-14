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
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hochschuledarmstadt.photostream_tools.model.BaseItem;

abstract class BaseAdapter<T extends RecyclerView.ViewHolder, H extends BaseItem & Parcelable> extends RecyclerView.Adapter<T>{

    protected static final String KEY_ITEMS = "KEY_ITEMS";
    protected ArrayList<H> items = new ArrayList<>();
    private Map<Integer, OnItemClickListener<H>> itemClickListenersMap = new HashMap<>();
    private Map<Integer, OnItemLongClickListener<H>> itemLongClickListenersMap = new HashMap<>();
    private Map<Integer, OnItemTouchListener<H>> itemTouchListenersMap = new HashMap<>();
    public BaseAdapter(){
        this(new ArrayList<H>());
    }

    public BaseAdapter(ArrayList<H> items){
        this.items = items;
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

    /**
     * Fügt einen {@code itemClickListener} für eine View mit der id {@code viewId} hinzu.
     * Wenn für diese View bereits ein {@link OnItemClickListener} existiert, wird dieser zuerst entfernt.
     * @param viewId
     * @param itemClickListener
     */
    public void setOnItemClickListener(int viewId, OnItemClickListener<H> itemClickListener){
        boolean shouldRepopulateViews = false;
        if (itemClickListenersMap.containsKey(viewId)) {
            itemClickListenersMap.remove(viewId);
            shouldRepopulateViews = true;
        }
        itemClickListenersMap.put(viewId, itemClickListener);
        if (shouldRepopulateViews)
            notifyDataSetChanged();
    }

    public void setOnItemLongClickListener(int viewId, OnItemLongClickListener<H> itemLongClickListener){
        boolean shouldRepopulateViews = false;
        if (itemLongClickListenersMap.containsKey(viewId)) {
            itemLongClickListenersMap.remove(viewId);
            shouldRepopulateViews = true;
        }
        itemLongClickListenersMap.put(viewId, itemLongClickListener);
        if (shouldRepopulateViews)
            notifyDataSetChanged();
    }

    public void setOnItemTouchListener(int viewId, OnItemTouchListener<H> itemTouchListener){
        boolean shouldRepopulateViews = false;
        if (itemTouchListenersMap.containsKey(viewId)) {
            itemTouchListenersMap.remove(viewId);
            shouldRepopulateViews = true;
        }
        itemTouchListenersMap.put(viewId, itemTouchListener);
        if (shouldRepopulateViews)
            notifyDataSetChanged();
    }

    protected interface OnItemLongClickListener<H> {
        boolean onItemLongClicked(View v, H item);
    }

    protected interface OnItemClickListener<H extends BaseItem & Parcelable> {
        void onItemClicked(View v, H item);
    }

    protected interface OnItemTouchListener<H extends BaseItem & Parcelable> {
        boolean onItemTouched(View v, MotionEvent motionEvent, H item);
    }

    @Override
    public void onBindViewHolder(T holder, int position) {
        applyOnItemClickListeners(holder);
        applyOnItemLongClickListeners(holder);
        applyOnItemTouchListeners(holder);
    }

    private void applyOnItemLongClickListeners(T holder) {
        for (Map.Entry<Integer, OnItemLongClickListener<H>> entry : itemLongClickListenersMap.entrySet()){
            int viewId = entry.getKey();
            View v = holder.itemView.findViewById(viewId);
            if (v != null) {
                v.setOnLongClickListener(new InternalOnLongClickListener(holder));
            }
        }
    }

    private void applyOnItemClickListeners(T holder) {
        for (Map.Entry<Integer, OnItemClickListener<H>> entry : itemClickListenersMap.entrySet()){
            int viewId = entry.getKey();
            View v = holder.itemView.findViewById(viewId);
            if (v != null && !ViewCompat.hasOnClickListeners(v)) {
                v.setOnClickListener(new InternalOnClickListener(holder));
            }
        }
    }

    private void applyOnItemTouchListeners(T holder) {
        for (Map.Entry<Integer, OnItemTouchListener<H>> entry : itemTouchListenersMap.entrySet()){
            int viewId = entry.getKey();
            View v = holder.itemView.findViewById(viewId);
            if (v != null) {
                v.setOnTouchListener(new InternalOnTouchListener(holder));
            }
        }
    }

    @Override
    public void onViewRecycled(T holder) {
        holder.itemView.setOnClickListener(null);
        holder.itemView.setOnLongClickListener(null);
        holder.itemView.setOnTouchListener(null);
        super.onViewRecycled(holder);
    }

    private class InternalOnClickListener implements View.OnClickListener {

        private final RecyclerView.ViewHolder viewHolder;

        public InternalOnClickListener(RecyclerView.ViewHolder viewHolder){
            this.viewHolder = viewHolder;
        }

        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            if (itemClickListenersMap.containsKey(viewId)) {
                OnItemClickListener<H> listener = itemClickListenersMap.get(viewId);
                int position = viewHolder.getAdapterPosition();
                H item = (position >= 0) ? getItemAtPosition(position) : null;
                listener.onItemClicked(v, item);
            }
        }
    }

    private class InternalOnTouchListener implements View.OnTouchListener{

        private final RecyclerView.ViewHolder viewHolder;

        public InternalOnTouchListener(RecyclerView.ViewHolder viewHolder){
            this.viewHolder = viewHolder;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int viewId = v.getId();
            if (itemTouchListenersMap.containsKey(viewId)) {
                OnItemTouchListener<H> listener = itemTouchListenersMap.get(viewId);
                int position = viewHolder.getAdapterPosition();
                H item = (position >= 0) ? getItemAtPosition(position) : null;
                return listener.onItemTouched(v, event, item);
            }
            return false;
        }
    }

    private class InternalOnLongClickListener implements View.OnLongClickListener {

        private final RecyclerView.ViewHolder viewHolder;

        public InternalOnLongClickListener(RecyclerView.ViewHolder viewHolder) {
            this.viewHolder = viewHolder;
        }

        @Override
        public boolean onLongClick(View v) {
            int viewId = v.getId();
            if (itemLongClickListenersMap.containsKey(viewId)) {
                OnItemLongClickListener<H> listener = itemLongClickListenersMap.get(viewId);
                int position = viewHolder.getAdapterPosition();
                H item = (position >= 0) ? getItemAtPosition(position) : null;
                return listener.onItemLongClicked(v, item);
            }
            return false;
        }
    }
}
