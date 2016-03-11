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

    public int prepend(H item){
        this.items.add(0, item);
        return 0;
    }

    public int append(H item){
        this.items.add(item);
        return items.indexOf(item);
    }

    public int[] append(Collection<? extends H> items){
        int[] range = new int[2];
        range[0] = getItemCount();
        this.items.addAll(items);
        range[1] = getItemCount()-1;
        return range;
    }

    public void set(Collection<? extends H> items){
        this.items.clear();
        this.items.addAll(items);
    }

    public int remove(int id) {
        for (int position = 0; position < items.size(); position++){
            H photo = getItemAtPosition(position);
            if (itemHasId(id, photo)) {
                items.remove(position);
                return position;
            }
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    protected abstract boolean itemHasId(int id, H item);

    public Bundle onSaveInstanceState(){
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(KEY_ITEMS, items);
        return bundle;
    }

    public void onRestoreInstanceState(Bundle bundle){
        items = bundle.getParcelableArrayList(KEY_ITEMS);
    }

}
