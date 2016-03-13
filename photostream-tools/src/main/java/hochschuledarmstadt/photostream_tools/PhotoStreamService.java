package hochschuledarmstadt.photostream_tools;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import java.util.UUID;

public class PhotoStreamService extends Service {

    private static final String TAG = PhotoStreamService.class.getName();

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
        final String photoStreamUrl = readUrlFromManifest();
        final Context context = getApplicationContext();
        final DbConnection dbConnection = DbConnection.getInstance(context);
        photoStreamClient = new PhotoStreamClient(context, photoStreamUrl, dbConnection, getInstallationId(context));
        photoStreamClient.bootstrap();
    }

    private String readUrlFromManifest() {
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            String url = bundle.getString("PHOTOSTREAM_URL");
            return url;
        } catch (PackageManager.NameNotFoundException e) {
            Logger.log(TAG, LogLevel.ERROR, "Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            Logger.log(TAG, LogLevel.ERROR, "Failed to load meta-data, NullPointer: " + e.getMessage());
        }
        return null;
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
