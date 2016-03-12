package hochschuledarmstadt.photostream_tools;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Created by Andreas Schattney on 18.02.2016.
 */
public abstract class BaseFragment extends Fragment implements OnServiceStateChangedListener {

    private boolean serviceDisconnectCalled;
    private Bundle refSavedInstanceState;

    protected abstract void onPhotoStreamServiceConnected(IPhotoStreamClient service, Bundle savedInstanceState);
    protected abstract void onPhotoStreamServiceDisconnected(IPhotoStreamClient service);

    private IPhotoStreamClient photoStreamClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.refSavedInstanceState = savedInstanceState;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((ServiceStateChangedNotifier)getActivity()).addOnServiceStateChangedListener(this);
    }

    @Override
    public void onDestroy() {
        ((ServiceStateChangedNotifier)getActivity()).removeOnServiceStateChangedListener(this);
        super.onDestroy();
    }

    protected boolean connectedToService(){
        return photoStreamClient != null;
    }

    @Override
    public void onServiceConnected(IPhotoStreamClient client) {
        photoStreamClient = client;
        onPhotoStreamServiceConnected(client, refSavedInstanceState);
    }

    @Override
    public void onServiceDisconnected(IPhotoStreamClient client) {
        if (!serviceDisconnectCalled) {
            serviceDisconnectCalled = true;
            onPhotoStreamServiceDisconnected(photoStreamClient);
            photoStreamClient = null;
        }
    }

    @Override
    public void onDestroyView() {
        if (!serviceDisconnectCalled){
            onServiceDisconnected(photoStreamClient);
            serviceDisconnectCalled = true;
        }
        refSavedInstanceState = null;
        super.onDestroyView();
    }

    public IPhotoStreamClient getPhotoStreamClient() {
        return photoStreamClient;
    }
}
