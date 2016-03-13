package hochschuledarmstadt.photostream_tools.callback;

import hochschuledarmstadt.photostream_tools.model.HttpResult;
import hochschuledarmstadt.photostream_tools.model.PhotoQueryResult;

/**
 * Created by Andreas Schattney on 12.03.2016.
 */
public interface OnPhotosResultListener extends OnPhotoListener {
    void onPhotosReceived(PhotoQueryResult result);
    void onReceivePhotosFailed(HttpResult httpResult);
}
