package hochschuledarmstadt.photostream_tools.callback;

import hochschuledarmstadt.photostream_tools.model.HttpResult;
import hochschuledarmstadt.photostream_tools.model.PhotoQueryResult;

/**
 * Created by Andreas Schattney on 12.03.2016.
 */
public interface OnPopularPhotosResultListener extends OnPhotoListener {
    void onPopularPhotosReceived(PhotoQueryResult result);
    void onReceivePopularPhotosFailed(HttpResult httpResult);
}
