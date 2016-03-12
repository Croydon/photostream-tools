package hochschuledarmstadt.photostream_tools.callback;

import hochschuledarmstadt.photostream_tools.model.Photo;

/**
 * Created by Andreas Schattney on 12.03.2016.
 */
public interface OnPhotoListener extends OnRequestListener {
    void onNewPhoto(Photo photo);

    void onPhotoDeleted(int photoId);

    void onPhotoDeleteFailed(int photoId);
}
