package hochschuledarmstadt.photostream_tools;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import java.util.UUID;

/**
 * Created by Andreas Schattney on 18.02.2016.
 */
public class PhotoStreamService extends Service {

    private IBinder photoStreamServiceBinder = new PhotoStreamServiceBinder();
    private PhotoStreamClient photoStreamClient;

    public class PhotoStreamServiceBinder extends Binder {

        public IPhotoStreamClient getClient() {
            return photoStreamClient;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        saveUniqueInstallationIdOnFirstStartIfNotPresent();
        final Context context = getApplicationContext();
        final DbConnection dbConnection = DbConnection.getInstance(context);
        photoStreamClient = new PhotoStreamClient(context, dbConnection, getInstallationId(context));
        photoStreamClient.bootstrap();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void saveUniqueInstallationIdOnFirstStartIfNotPresent() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String installationId = sharedPreferences.getString(getString(R.string.installation_id), null);
        if (installationId == null){
            sharedPreferences.edit().putString(getString(R.string.installation_id), UUID.randomUUID().toString()).commit();
        }
    }

    public static String getInstallationId(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String installationId = sharedPreferences.getString(context.getString(R.string.installation_id), null);
        return installationId;
    }

    @Override
    public void onDestroy() {
        photoStreamClient.destroy();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return photoStreamServiceBinder;
    }

}
