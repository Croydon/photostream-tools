package hochschuledarmstadt.photostream_tools.callback;

import hochschuledarmstadt.photostream_tools.model.HttpResult;
import hochschuledarmstadt.photostream_tools.model.Photo;

/**
 * Created by Andreas Schattney on 12.03.2016.
 */
public interface OnPhotoUploadListener extends OnRequestListener {
    void onPhotoUploaded(Photo photo);
    void onPhotoUploadFailed(HttpResult httpResult);
}
