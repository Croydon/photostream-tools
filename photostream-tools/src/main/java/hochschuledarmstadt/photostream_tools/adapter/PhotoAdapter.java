package hochschuledarmstadt.photostream_tools.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import hochschuledarmstadt.photostream_tools.model.Photo;

/**
 * Created by Andreas Schattney on 11.03.2016.
 */
public abstract class PhotoAdapter<T extends RecyclerView.ViewHolder, H extends Photo> extends BaseAdapter<T, H> {

    public PhotoAdapter(ArrayList<H> photos){
        super(photos);
    }

    public PhotoAdapter(){
        super(new ArrayList<H>());
    }

    public void updateVoteCountForPhoto(int photoId, int newVoteCount) {
        final int photoCount = getItemCount();
        for (int position = 0; position < photoCount; position++){
            H photo = getItemAtPosition(position);
            if (photo.getId() == photoId) {
                photo.updateVotecount(newVoteCount);
                break;
            }
        }
    }

}
