package hochschuledarmstadt.photostream_tools;

/**
 * Created by Andreas Schattney on 18.02.2016.
 */
public interface OnServiceStateChangedListener {
    void onServiceConnected(IPhotoStreamClient client);
    void onServiceDisconnected(IPhotoStreamClient client);
}
