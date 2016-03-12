package hochschuledarmstadt.photostream_tools.callback;

/**
 * Created by Andreas Schattney on 12.03.2016.
 */
public interface OnPhotoUploadListener extends OnRequestListener {
    void onPhotoUploaded();
    void onPhotoUploadFailed();
}
