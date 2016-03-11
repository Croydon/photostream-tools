package hochschuledarmstadt.photostream_tools.activity;

import android.os.Bundle;

import java.util.ArrayList;

import hochschuledarmstadt.photostream_tools.service.PhotoStreamClient;

/**
 * Created by Andreas Schattney on 11.03.2016.
 */
public abstract class BaseFragmentActivity extends BaseActivity implements ServiceStateChangedNotifier {

    private ArrayList<OnServiceStateChangedListener> serviceStateChangedListeners = new ArrayList<>();

    @Override
    public void removeOnServiceStateChangedListener(OnServiceStateChangedListener onServiceStateChangedListener) {
        if (serviceStateChangedListeners.contains(onServiceStateChangedListener))
            serviceStateChangedListeners.remove(onServiceStateChangedListener);
    }

    @Override
    public void addOnServiceStateChangedListener(OnServiceStateChangedListener onServiceStateChangedListener) {
        if (!serviceStateChangedListeners.contains(onServiceStateChangedListener))
            serviceStateChangedListeners.add(onServiceStateChangedListener);
    }

    @Override
    protected void onPhotoStreamServiceConnected(PhotoStreamClient photoStreamClient, Bundle savedInstanceState) {
        notifyOnServiceConnected(photoStreamClient);
    }

    @Override
    protected void onPhotoStreamServiceDisconnected(PhotoStreamClient photoStreamClient) {
        notifyOnServiceDisconnected(photoStreamClient);
    }

    private void notifyOnServiceConnected(PhotoStreamClient photoStreamClient){
        for (OnServiceStateChangedListener onServiceStateChangedListener : serviceStateChangedListeners)
            onServiceStateChangedListener.onServiceConnected(photoStreamClient);
    }

    private void notifyOnServiceDisconnected(PhotoStreamClient photoStreamClient){
        for (OnServiceStateChangedListener onServiceStateChangedListener : serviceStateChangedListeners)
            onServiceStateChangedListener.onServiceDisconnected(photoStreamClient);
    }
}
