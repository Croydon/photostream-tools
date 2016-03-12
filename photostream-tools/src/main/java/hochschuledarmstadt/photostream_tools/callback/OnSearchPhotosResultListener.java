package hochschuledarmstadt.photostream_tools.callback;

import hochschuledarmstadt.photostream_tools.model.PhotoQueryResult;

/**
 * Created by Andreas Schattney on 12.03.2016.
 */
public interface OnSearchPhotosResultListener extends OnPhotoListener {
    void onSearchedPhotosReceived(PhotoQueryResult result);

    void onReceiveSearchedPhotosFailed();
}
