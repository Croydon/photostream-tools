package hochschuledarmstadt.photostream_tools.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import hochschuledarmstadt.photostream_tools.log.LogLevel;
import hochschuledarmstadt.photostream_tools.log.Logger;
import hochschuledarmstadt.photostream_tools.service.PhotoStreamClient;
import hochschuledarmstadt.photostream_tools.service.PhotoStreamService;

/**
 * Created by Andreas Schattney on 09.03.2016.
 */
public abstract class BaseActivity extends AppCompatActivity implements ServiceConnection {

    private static final String TAG = BaseActivity.class.getName();
    private PhotoStreamClient photoStreamClient;
    private boolean bound;
    private Bundle refSavedInstanceState;

    protected abstract void onPhotoStreamServiceConnected(PhotoStreamClient photoStreamClient, Bundle savedInstanceState);
    protected abstract void onPhotoStreamServiceDisconnected(PhotoStreamClient photoStreamClient);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.refSavedInstanceState = savedInstanceState;
        if (savedInstanceState == null){
            startService(new Intent(this, PhotoStreamService.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!bound)
            bindService(new Intent(this, PhotoStreamService.class), this, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        if (photoStreamClient != null)
            onPhotoStreamServiceDisconnected(photoStreamClient);
        try {
            unbindService(this);
        }catch(Exception e){
            Logger.log(TAG, LogLevel.ERROR, e.toString());
        }
        refSavedInstanceState = null;
        super.onDestroy();
    }

    protected boolean connectedToService() {
        return photoStreamClient != null && bound;
    }

    protected PhotoStreamClient getPhotoStreamClient() {
        return photoStreamClient;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        photoStreamClient = ((PhotoStreamService.PhotoStreamServiceBinder)service).getClient();
        bound = true;
        onPhotoStreamServiceConnected(photoStreamClient, refSavedInstanceState);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        bound = false;
        photoStreamClient = null;
    }
}
