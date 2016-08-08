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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hochschuledarmstadt.photostream_tools.R;
import hochschuledarmstadt.photostream_tools.model.BaseItem;

abstract class BaseAdapter<H extends RecyclerView.ViewHolder, T extends BaseItem & Parcelable> extends RecyclerView.Adapter<H> {

    public static final int EXTENSION_EVENT_TYPE_CLICK = 0x2389472;
    public static final int EXTENSION_EVENT_TYPE_LONG_CLICK = 0x8389472;
    private static final String KEY_COUNT_EXTENSIONS = "KEY_COUNT_EXTENSIONS";

    private DelegateOnLongClickListener delegateOnLongClickListener = new DelegateOnLongClickListener();
    private DelegateOnClickListener clickDelegate = new DelegateOnClickListener(null);

    private static final class PluginInfo<H extends RecyclerView.ViewHolder, T extends BaseItem & Parcelable> {
        public PluginInfo(Plugin<H, T> plugin, int eventType, @IdRes int viewId) {
            this.plugin = plugin;
            this.eventType = eventType;
            this.viewId = viewId;
        }

        public Plugin<H, T> plugin;
        public int eventType;
        @IdRes
        public int viewId;

    }

    protected static final String KEY_ITEMS = "KEY_ITEMS";
    protected ArrayList<T> items = new ArrayList<>();
    private List<PluginInfo<H, T>> plugins = new ArrayList<>();
    private Map<Integer, OnItemClickListener<H, T>> itemClickListenersMap = new HashMap<>();
    private Map<Integer, OnItemLongClickListener<H, T>> itemLongClickListenersMap = new HashMap<>();
    private Map<Integer, OnItemTouchListener<H, T>> itemTouchListenersMap = new HashMap<>();

    public BaseAdapter() {
        this(new ArrayList<T>());
    }

    public BaseAdapter(ArrayList<T> items) {
        this.items = items;
        setHasStableIds(true);
    }

    public void addOnLongClickPlugin(@IdRes int viewId, Plugin<H, T> plugin) {
        internalAddPlugin(viewId, plugin, EXTENSION_EVENT_TYPE_LONG_CLICK);
    }

    public void addOnClickPlugin(@IdRes int viewId, Plugin<H, T> plugin) {
        internalAddPlugin(viewId, plugin, EXTENSION_EVENT_TYPE_CLICK);
    }

    private void internalAddPlugin(@IdRes int viewId, Plugin<H, T> plugin, int eventType){
        PluginInfo<H, T> pluginInfo = new PluginInfo<>(plugin, eventType, viewId);
        if (!plugins.contains(pluginInfo)) {
            plugin.setAdapter(this);
            plugin.setViewId(viewId);
            plugins.add(pluginInfo);
        }
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getId();
    }

    /**
     * Liefert das Item {@code item} an der Position {@code position} zurück
     *
     * @param position Position in der Liste
     * @return Item
     */
    public T getItemAtPosition(int position) {
        return items.get(position);
    }

    /**
     * Hängt ein Item {@code item} an das <b>Ende</b> der Liste an
     *
     * @param item Item das an das <b>Ende</b> der Liste hinzugefügt werden soll
     */
    public void addAtFront(T item) {
        this.items.add(0, item);
        notifyItemInserted(0);
    }

    /**
     * Hängt ein Item {@code item} an den <b>Anfang</b> der Liste an
     *
     * @param item Item das an den <b>Anfang</b> der Liste hinzugefügt werden soll
     */
    public void add(T item) {
        this.items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    /**
     * Fügt alle Elemente in der Liste {@code items} an das Ende der Liste an
     *
     * @param items Liste von Items
     */
    public void addAll(Collection<? extends T> items) {
        final int itemCountBefore = getItemCount();
        this.items.addAll(items);
        final int lastItemIndex = getItemCount() - 1;
        notifyItemRangeInserted(itemCountBefore, lastItemIndex);
    }

    /**
     * Ersetzt die aktuelle Liste des Adapters durch eine neue Liste von Items {@code items}
     *
     * @param items die neue Liste von Items
     */
    public void set(Collection<? extends T> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    /**
     * Entfernt ein Item aus der Liste mit der übergebenen {@code id}
     *
     * @param id id des Items
     */
    public void remove(int id) {
        for (int position = 0; position < items.size(); position++) {
            T photo = getItemAtPosition(position);
            if (itemHasEqualId(id, photo)) {
                items.remove(position);
                notifyItemRemoved(position);
                break;
            }
        }
    }

    public List<T> getItems(){
        return new ArrayList<>(items);
    }

    /**
     * Liefert die Anzahl der Items in der Liste
     *
     * @return Anzahl der Items
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    protected boolean itemHasEqualId(int id, T item) {
        return item.getId() == id;
    }

    /**
     * Speichert die aktuelle Liste von Items in ein Bundle
     *
     * @return bundle
     */
    public Bundle saveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_COUNT_EXTENSIONS, plugins.size());
        for (PluginInfo<H, T> e : plugins) {
            e.plugin.saveInstanceState(bundle);
        }
        bundle.putParcelableArrayList(KEY_ITEMS, items);
        return bundle;
    }

    /**
     * Stellt die Liste von Items aus einem Bundle wieder her
     *
     * @param bundle das Bundle, welches die Liste von Items enthält
     */
    public void restoreInstanceState(Bundle bundle) {
        int countExtensions = bundle.getInt(KEY_COUNT_EXTENSIONS);
        if (countExtensions != plugins.size()){
            throw new IllegalStateException("Extensions müssen vor dem restoreInstanceState() Aufruf gesetzt werden!");
        }
        items = bundle.getParcelableArrayList(KEY_ITEMS);
        for (PluginInfo<H, T> e : plugins) {
            e.plugin.restoreInstanceState(bundle);
        }
    }

    /**
     * Fügt einen {@code itemClickListener} für eine View mit der id {@code viewId} hinzu.
     * Wenn für diese View bereits ein {@link OnItemClickListener} existiert, wird dieser zuerst entfernt.
     *
     * @param viewId
     * @param itemClickListener
     */
    public void setOnItemClickListener(@IdRes int viewId, OnItemClickListener<H, T> itemClickListener) {
        boolean shouldRepopulateViews = false;
        if (itemClickListenersMap.containsKey(viewId)) {
            itemClickListenersMap.remove(viewId);
            shouldRepopulateViews = true;
        }
        itemClickListenersMap.put(viewId, itemClickListener);
        if (shouldRepopulateViews)
            notifyDataSetChanged();
    }

    public void setOnItemLongClickListener(@IdRes int viewId, OnItemLongClickListener<H, T> itemLongClickListener) {
        boolean shouldRepopulateViews = false;
        if (itemLongClickListenersMap.containsKey(viewId)) {
            itemLongClickListenersMap.remove(viewId);
            shouldRepopulateViews = true;
        }
        itemLongClickListenersMap.put(viewId, itemLongClickListener);
        if (shouldRepopulateViews)
            notifyDataSetChanged();
    }

    public void setOnItemTouchListener(@IdRes int viewId, OnItemTouchListener<H, T> itemTouchListener) {
        boolean shouldRepopulateViews = false;
        if (itemTouchListenersMap.containsKey(viewId)) {
            itemTouchListenersMap.remove(viewId);
            shouldRepopulateViews = true;
        }
        itemTouchListenersMap.put(viewId, itemTouchListener);
        if (shouldRepopulateViews)
            notifyDataSetChanged();
    }

    protected interface OnItemLongClickListener< H extends RecyclerView.ViewHolder, T extends BaseItem & Parcelable> {
        boolean onItemLongClicked(H viewHolder, View v, T item);
    }

    protected interface OnItemClickListener<H extends RecyclerView.ViewHolder, T extends BaseItem & Parcelable> {
        void onItemClicked(H viewHolder, View v, T item);
    }

    protected interface OnItemTouchListener<H extends RecyclerView.ViewHolder, T extends BaseItem & Parcelable> {
        boolean onItemTouched(H viewHolder, View v, MotionEvent motionEvenH, T item);
    }

    @Override
    public void onBindViewHolder(H holder, int position) {
        applyOnItemClickListeners(holder);
        applyOnItemLongClickListeners(holder);
        applyOnItemTouchListeners(holder);
        for (PluginInfo<H, T> e : plugins) {
            Integer itemId = Integer.valueOf((int) getItemId(position));
            if (ignoredAnimations.contains(itemId)){
                holder.itemView.setTag(R.id.should_animate, Boolean.FALSE);
                ignoredAnimations.remove(itemId);
            }
            e.plugin.onBindViewHolder(holder, position);
        }
    }

    private static ArrayList<View> getAllViewsWithValidId(View root) {
        ArrayList<View> views = new ArrayList<>();
        views.add(root);
        if (root instanceof ViewGroup) {
            ViewGroup rootViewGroup = (ViewGroup) root;
            final int childCount = rootViewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = rootViewGroup.getChildAt(i);
                if (child instanceof ViewGroup) {
                    views.addAll(getAllViewsWithValidId(child));
                } else {
                    views.add(child);
                }
            }
        }
        return views;
    }

    private void applyOnItemLongClickListeners(H holder) {

        List<View> viewsWithValidId = getAllViewsWithValidId(holder.itemView);

        for (View view : viewsWithValidId) {
            int viewId = view.getId();
            if (viewId != View.NO_ID) {
                OnItemLongClickListener<H, T> listener = itemLongClickListenersMap.get(viewId);
                List<PluginInfo<H, T>> injectees = getCompatibleExtensions(viewId, EXTENSION_EVENT_TYPE_LONG_CLICK);
                if (listener != null || !injectees.isEmpty()) {
                    view.setOnLongClickListener(new InternalOnLongClickListener(holder, injectees));
                } else {
                    view.setOnLongClickListener(delegateOnLongClickListener);
                }
            }else{
                view.setOnLongClickListener(delegateOnLongClickListener);
            }
        }
    }

    private List<Integer> ignoredAnimations = new ArrayList<>();

    void dontAnimate(int id){
        Integer itemId = Integer.valueOf(id);
        if (!ignoredAnimations.contains(itemId))
            ignoredAnimations.add(itemId);
    }

    private List<PluginInfo<H, T>> getCompatibleExtensions(int viewId, int eventType) {
        List<PluginInfo<H, T>> injectees = new ArrayList<>();
        for (PluginInfo<H, T> e : plugins) {
            if (e.eventType == eventType && viewId == e.viewId) {
                injectees.add(e);
            }
        }
        return injectees;
    }

    private void applyOnItemClickListeners(H holder) {

        List<View> viewsWithValidId = getAllViewsWithValidId(holder.itemView);

        for (View view : viewsWithValidId) {
            int viewId = view.getId();
            if (viewId != View.NO_ID) {
                OnItemClickListener<H, T> listener = itemClickListenersMap.get(viewId);
                List<PluginInfo<H, T>> injectees = getCompatibleExtensions(viewId, EXTENSION_EVENT_TYPE_CLICK);
                if (listener != null || !injectees.isEmpty()) {
                    view.setOnClickListener(new DelegateOnClickListener(new InternalOnClickListener(holder, injectees)));
                } else {
                    view.setOnClickListener(clickDelegate);
                }
            }else{
                view.setOnClickListener(clickDelegate);
            }
        }
    }

    private void applyOnItemTouchListeners(H holder) {
        for (Map.Entry<Integer, OnItemTouchListener<H, T>> entry : itemTouchListenersMap.entrySet()) {
            int viewId = entry.getKey();
            View v = holder.itemView.findViewById(viewId);
            if (v != null) {
                v.setOnTouchListener(new InternalOnTouchListener(holder));
            }
        }
    }

    @Override
    public void onViewRecycled(H holder) {
        holder.itemView.setOnClickListener(null);
        holder.itemView.setOnLongClickListener(null);
        holder.itemView.setOnTouchListener(null);
        super.onViewRecycled(holder);
    }

    private class DelegateOnClickListener implements View.OnClickListener {

        private final InternalOnClickListener listener;

        public DelegateOnClickListener(InternalOnClickListener listener){
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onClick(v);
            }else{
                try{
                    View parent = (View) v.getParent();
                    if (!RecyclerView.class.isAssignableFrom(parent.getClass())) {
                        parent.performClick();
                    }
                }catch(ClassCastException e){}
            }
        }
    }

    private class InternalOnClickListener implements View.OnClickListener {

        private final H viewHolder;
        private final List<PluginInfo<H, T>> injectees;

        public InternalOnClickListener(H viewHolder, List<PluginInfo<H, T>> injectees) {
            this.viewHolder = viewHolder;
            this.injectees = injectees;
        }

        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            if (itemClickListenersMap.containsKey(viewId)) {
                OnItemClickListener<H, T> listener = itemClickListenersMap.get(viewId);
                int position = viewHolder.getAdapterPosition();
                T item = (position >= 0) ? getItemAtPosition(position) : null;
                for (PluginInfo<H, T> e : injectees) {
                    e.plugin.onItemClicked(viewHolder, v, item);
                }
                if (listener != null)
                    listener.onItemClicked(viewHolder, v, item);
            }
        }
    }

    private class InternalOnTouchListener implements View.OnTouchListener {

        private final H viewHolder;

        public InternalOnTouchListener(H viewHolder) {
            this.viewHolder = viewHolder;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int viewId = v.getId();
            if (itemTouchListenersMap.containsKey(viewId)) {
                OnItemTouchListener<H, T> listener = itemTouchListenersMap.get(viewId);
                int position = viewHolder.getAdapterPosition();
                T item = (position >= 0) ? getItemAtPosition(position) : null;
                return listener.onItemTouched(viewHolder, v, event, item);
            }
            return false;
        }
    }

    private class DelegateOnLongClickListener implements View.OnLongClickListener {

        @Override
        public boolean onLongClick(View v) {
            boolean handled = false;
            View parent;
            View previousView = v;
            try{
                do{
                    parent = (View) previousView.getParent();
                    if (RecyclerView.class.isAssignableFrom(parent.getClass())) {
                        handled = false;
                        break;
                    }
                    handled = parent.performLongClick();
                    previousView = parent;
                }while(!handled);
            }catch(ClassCastException e){}
            return handled;
        }
    }

    private class InternalOnLongClickListener implements View.OnLongClickListener {

        private final H viewHolder;
        private final List<PluginInfo<H, T>> injectees;

        public InternalOnLongClickListener(H viewHolder, List<PluginInfo<H, T>> injectees) {
            this.viewHolder = viewHolder;
            this.injectees = injectees;
        }

        @Override
        public boolean onLongClick(View v) {
            int viewId = v.getId();
            OnItemLongClickListener<H, T> listener = itemLongClickListenersMap.get(viewId);
            int position = viewHolder.getAdapterPosition();
            T item = (position >= 0) ? getItemAtPosition(position) : null;
            boolean result = false;
            for (PluginInfo<H, T> e : injectees) {
                if (e.plugin.onItemLongClicked(viewHolder, v, item))
                    result = true;
            }
            if (listener == null || result)
                return result;
            else {
                if (!listener.onItemLongClicked(viewHolder, v, item)){
                    boolean handled = false;
                    View parentView;
                    View previousView = v;
                    try{
                        do{
                            parentView = (View) previousView.getParent();
                            if (parentView.getClass().getName().equals("android.support.v7.widget.RecyclerView")) {
                                handled = false;
                                break;
                            }
                            handled = parentView.performLongClick();
                            previousView = parentView;
                        }while(!handled);
                    }catch(ClassCastException e){}
                    return handled;
                }
                return true;
            }
        }
    }
}
