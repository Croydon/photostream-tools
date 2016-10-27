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

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.MenuRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import hochschuledarmstadt.photostream_tools.model.BaseItem;

public abstract class PluginContextualActionBar<T extends BaseItem & Parcelable, H extends RecyclerView.ViewHolder> extends Plugin<H, T> {

    private static final String KEY_SELECTED_IDS = "list";
    private static final String KEY_CAB_VISIBLE = "KEY_CAB_VISIBLE";
    private static final String KEY_CAB_TITLE = "cab_title";
    private static final String KEY_CAB_SUBTITLE = "cab_subtitle";

    private final int menuRes;
    private AppCompatActivity activity;
    private ArrayList<Integer> selectedIds = new ArrayList<>();
    private ActionMode actionMode;

    private Listener listener = new Listener();

    /**
     *
     * @param activity Die Activity
     * @param menuResource Die Menu Ressource, die in der Contextual ActionBar angezeigt werden soll
     */
    public PluginContextualActionBar(AppCompatActivity activity, @MenuRes int menuResource){
        this.activity = activity;
        this.menuRes = menuResource;
    }


    @Override
    void trigger(H viewHolder, View view, T item) {
        int amountBeforeChange = getAmountSelectedItems();

        int itemId = item.getId();
        Integer objPhotoId = Integer.valueOf(itemId);

        boolean selected = !(selectedIds.contains(objPhotoId));
        if (selected && !selectedIds.contains(objPhotoId))
            selectedIds.add(objPhotoId);
        else if (!selected && selectedIds.contains(objPhotoId))
            selectedIds.remove(objPhotoId);

        for (int i = 0; i < adapter.getItemCount(); i++) {
            if (adapter.getItemAtPosition(i).getId() == itemId) {
                dontAnimate(itemId);
                adapter.notifyItemChanged(i);
                break;
            }
        }

        List<T> allItems = getAllItems();
        if (amountBeforeChange == 0 && getAmountSelectedItems() > 0) {
            actionMode = activity.startSupportActionMode(listener);
            onUpdateContextualActionBar(actionMode, activity.getApplicationContext(), allItems, getAmountSelectedItems());
        } else if(amountBeforeChange > 0 && getAmountSelectedItems() == 0) {
            actionMode.finish();
        }else{
            onUpdateContextualActionBar(actionMode, activity.getApplicationContext(), allItems, getAmountSelectedItems());
        }
        allItems.clear();
    }

    @Override
    boolean onItemLongClicked(H viewHolder, View v, T item) {
        Integer itemId = Integer.valueOf(item.getId());
        if (selectedIds.contains(itemId)) {
            trigger(viewHolder, v, item);
            return true;
        }else
            return super.onItemLongClicked(viewHolder, v, item);
    }

    @Override
    void saveInstanceState(Bundle bundle) {
        bundle.putIntegerArrayList(KEY_SELECTED_IDS, selectedIds);
        bundle.putInt(KEY_CAB_VISIBLE, actionMode != null ? 1 : 0);
        CharSequence title = actionMode != null ? actionMode.getTitle() : null;
        CharSequence subtitle = actionMode != null ? actionMode.getSubtitle() : null;
        bundle.putString(KEY_CAB_TITLE, actionMode != null && title != null ? title.toString() : null);
        bundle.putString(KEY_CAB_SUBTITLE, actionMode != null && subtitle != null ? subtitle.toString() : null);
    }

    @Override
    void restoreInstanceState(Bundle bundle) {
        selectedIds = bundle.getIntegerArrayList(KEY_SELECTED_IDS);
        boolean shouldRestoreCab = bundle.getInt(KEY_CAB_VISIBLE) == 1;
        if (shouldRestoreCab) {
            actionMode = activity.startSupportActionMode(listener);
            actionMode.setTitle(bundle.getString(KEY_CAB_TITLE));
            actionMode.setSubtitle(bundle.getString(KEY_CAB_SUBTITLE));
        }
    }

    @Override
    void onBindViewHolder(H viewHolder, int position) {
        int photoId = adapter.getItemAtPosition(position).getId();
        View view = viewHolder.itemView.findViewById(getViewId());
        boolean selected = selectedIds.contains(Integer.valueOf(photoId));
        view.setSelected(selected);
    }

    @Override
    public void destroy() {
        super.destroy();
        adapter = null;
        if (actionMode != null) {
            actionMode = null;
        }
        activity = null;
        listener = null;
    }

    private List<T> getAllItems(){
        List<T> items = new ArrayList<>();
        int count = getAmountSelectedItems();
        int found = 0;
        for (int position = 0; position < adapter.getItemCount(); position++) {
            T item = adapter.getItemAtPosition(position);
            if (selectedIds.contains(Integer.valueOf(item.getId()))) {
                items.add(item);
                if (++found == count)
                    break;
            }
        }
        return items;
    }

    void reset() {
        if (adapter == null)
            return;
        int count = getAmountSelectedItems();
        int found = 0;
        for (int position = 0; position < adapter.getItemCount(); position++) {
            Integer itemId = Integer.valueOf(adapter.getItemAtPosition(position).getId());
            if (selectedIds.contains(itemId)) {
                dontAnimate(itemId);
                adapter.notifyItemChanged(position);
                if (++found == count)
                    break;
            }
        }
        selectedIds.clear();
    }

    /**
     * Liefert die Anzahl der markierten Views zurück
     * @return {@code int} Anzahl der markierten Views
     */
    public int getAmountSelectedItems() {
        return selectedIds.size();
    }

    /**
     * Diese Methode wird aufgerufen, wenn auf ein MenuItem in der Contextual Action Bar geklickt wurde
     * @param mode
     * @param menuItem das Menüitem, auf das geklickt wurde
     * @param selectedItemIds Enthält alle Ids der selektierten Elemente (z.B. photo ids)
     * @return {@code true}, wenn das Clickevent verarbeitet wurde, ansonsten {@code false}
     */
    protected abstract boolean onActionItemClicked(ActionMode mode, MenuItem menuItem, List<Integer> selectedItemIds);

    /**
     * Diese Methode wird aufgerufen, wenn die Contextual Action Bar aktualisiert werden soll
     * @param actionMode Hierüber kann der Titel der CAB gesetzt werden
     * @param context Context Objekt, falls benötigt
     * @param selectedItemsCount Die Anzahl der selektierten Elemente
     */
    protected abstract void onUpdateContextualActionBar(ActionMode actionMode, Context context, List<T> items, int selectedItemsCount);

    /**
     * Diese Methode wird aufgerufen, wenn die Contextual ActionBar zerstört wird
     */
    protected abstract void onActionModeFinished();

    private class Listener implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(menuRes, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            List<Integer> ids = new ArrayList<>(selectedIds);
            boolean result = PluginContextualActionBar.this.onActionItemClicked(mode, item, ids);
            ids.clear();
            if (result) {
                mode.finish();
            }
            return result;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            reset();
            actionMode = null;
            onActionModeFinished();
        }
    }

}
