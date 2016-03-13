package hochschuledarmstadt.photostream_tools.examples.photo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import hochschuledarmstadt.photostream_tools.adapter.PhotoAdapter;
import hochschuledarmstadt.photostream_tools.examples.R;
import hochschuledarmstadt.photostream_tools.model.Photo;

/**
 * Created by Andreas Schattney on 12.03.2016.
 */
public class PhotoStreamAdapter extends PhotoAdapter<PhotoStreamAdapter.PhotoViewHolder, Photo> {

    private final Context context;

    public PhotoStreamAdapter(Context context){
        super();
        this.context = context;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhotoViewHolder(LayoutInflater.from(context).inflate(R.layout.photo_item, parent, false));
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        recycleBitmapIfNecessary(holder.imageView);
        Photo photo = getItemAtPosition(position);
        Bitmap bitmap = BitmapFactory.decodeFile(photo.getImageFilePath());
        holder.imageView.setImageBitmap(bitmap);
        holder.textView.setText(photo.getComment());
    }

    @Override
    public void onViewRecycled(PhotoViewHolder holder) {
        recycleBitmapIfNecessary(holder.imageView);
        super.onViewRecycled(holder);
    }

    private void recycleBitmapIfNecessary(ImageView imageView) {
        if (imageView.getDrawable() != null && imageView.getDrawable() instanceof BitmapDrawable){
            final Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            if (bitmap != null && !bitmap.isRecycled())
                bitmap.recycle();
        }
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder {

        public final TextView textView;
        public final ImageView imageView;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            textView = (TextView) itemView.findViewById(R.id.textView);
        }
    }

}
