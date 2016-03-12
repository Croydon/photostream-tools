package hochschuledarmstadt.photostream_tools.adapter;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;

import hochschuledarmstadt.photostream_tools.model.Id;

/**
 * Created by Andreas Schattney on 11.03.2016.
 */
public abstract class BaseAdapter<T extends RecyclerView.ViewHolder, H extends Parcelable & Id> extends RecyclerView.Adapter<T>{

    protected static final String KEY_ITEMS = "KEY_ITEMS";

    protected ArrayList<H> items = new ArrayList<>();

    public BaseAdapter(ArrayList<H> items){
        this.items = items;
    }

    public H getItemAtPosition(int position){
        return items.get(position);
    }

    public void prepend(H item){
        this.items.add(0, item);
        notifyItemInserted(0);
    }

    public void append(H item){
        this.items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public void append(Collection<? extends H> items){
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

    public Bundle onSaveInstanceState(){
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(KEY_ITEMS, items);
        return bundle;
    }

    public void onRestoreInstanceState(Bundle bundle){
        items = bundle.getParcelableArrayList(KEY_ITEMS);
    }

}
