package hochschuledarmstadt.photostream_tools.callback;

import hochschuledarmstadt.photostream_tools.model.HttpResult;
import hochschuledarmstadt.photostream_tools.model.PhotoQueryResult;

public interface OnSearchPhotosResultListener extends OnPhotoListener {
    void onSearchedPhotosReceived(PhotoQueryResult result);
    void onReceiveSearchedPhotosFailed(String query, HttpResult httpResult);
}
