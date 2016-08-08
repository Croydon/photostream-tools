/*
 * The MIT License
 *
 * Copyright (c) 2016 Andreas Schattney
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package hochschuledarmstadt.photostream_tools;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import hochschuledarmstadt.photostream_tools.callback.OnPhotosReceivedListener;

/**
 * Activities erhalten durch Erben von dieser Klasse Zugriff auf das Interface {@link IPhotoStreamClient}
 */
public abstract class PhotoStreamActivity extends AppCompatActivity implements ServiceConnection {

    private static final String TAG = PhotoStreamActivity.class.getName();

    private PhotoStreamClient photoStreamClient;
    private boolean bound;
    private Bundle refSavedInstanceState;
    private List<AsyncBitmapLoader<?>> asyncBitmapLoaders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.refSavedInstanceState = savedInstanceState;
        // Service starten, wenn die aktuelle Instanz der Activity neu erzeugt wurde
        if (savedInstanceState == null){
            startService(new Intent(this, PhotoStreamService.class));
        }
    }

    /**
     * Wird aufgerufen, wenn die Activity die Verbindung zu dem Service hergestellt hat
     * @param photoStreamClient Das Client Objekt
     * @param savedInstanceState Wenn die Activity reinitialisiert wird (z.B. Wechsel von Portrait in den Landscape Modus), dann enthält diese Variable die gespeicherten Variablen
     */
    protected abstract void onPhotoStreamServiceConnected(IPhotoStreamClient photoStreamClient, Bundle savedInstanceState);

    @Override
    protected void onPause() {
        super.onPause();
        if (!isFinishing() && isConnectedToService()) {
            photoStreamClient.registerActivity(this);
        }
    }

    /**
     * Wird aufgerufen, <b>kurz bevor</b> die Activity die Verbindung zu dem Service trennt <br>
     * Nach dieser Methode ist die Verbindung zum Service getrennt.
     * @param photoStreamClient
     */
    protected abstract void onPhotoStreamServiceDisconnected(IPhotoStreamClient photoStreamClient);

    @Override
    protected void onResume() {
        super.onResume();
        if (!bound)
            bindService(new Intent(this, PhotoStreamService.class), this, Context.BIND_AUTO_CREATE);
        if (isConnectedToService())
            photoStreamClient.unregisterActivity(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isConnectedToService()){
            if (isFinishing() && this instanceof OnPhotosReceivedListener) {
                photoStreamClient.resetEtag();
            }
        }
    }

    @Override
    protected void onDestroy() {
        cancelTasks();
        if (photoStreamClient != null) {
            photoStreamClient.unregisterActivity(this);
            onPhotoStreamServiceDisconnected(photoStreamClient);
        }
        try {
            unbindService(this);
        }catch(Exception e){
            Logger.log(TAG, LogLevel.ERROR, e.toString());
        }
        bound = false;
        refSavedInstanceState = null;
        super.onDestroy();
    }

    private void cancelTasks() {
        for (AsyncBitmapLoader<?> task : asyncBitmapLoaders){
            task.cancel(false);
        }
        asyncBitmapLoaders.clear();
    }

    public void loadBitmapAsync(File file, final OnBitmapLoadedListener listener){
        StreamDecoderFileStrategy strategy = new StreamDecoderFileStrategy();
        AsyncBitmapLoader<File> task = new AsyncBitmapLoader<>(strategy);
        task.setListener(new OnBitmapLoadedWrapper<File>(listener));
        task.execute(file);
    }

    public void loadBitmapAsync(Uri uri, final OnBitmapLoadedListener listener){
        if (uri.toString().startsWith("assets://")){
            String assetFileName = uri.toString().replace("assets://", "");
            loadBitmapAsync(assetFileName, listener);
        }else {
            UriDecoderStrategy strategy = new UriDecoderStrategy(getApplicationContext().getContentResolver());
            AsyncBitmapLoader<Uri> task = new AsyncBitmapLoader<>(strategy);
            task.setListener(new OnBitmapLoadedWrapper<Uri>(listener));
            task.execute(uri);
        }
    }

    void loadBitmapAsync(String assetFileName, final OnBitmapLoadedListener listener){
        AssetDecoderStrategy strategy = new AssetDecoderStrategy(getApplicationContext());
        AsyncBitmapLoader<String> task = new AsyncBitmapLoader<>(strategy);
        task.setListener(new OnBitmapLoadedWrapper<String>(listener));
        task.execute(assetFileName);
    }

    private class OnBitmapLoadedWrapper<T> implements AsyncBitmapLoader.OnMessageListener<T> {

        private final OnBitmapLoadedListener listener;

        public OnBitmapLoadedWrapper(OnBitmapLoadedListener listener){
            this.listener = listener;
        }

        @Override
        public void onTaskStarted(AsyncBitmapLoader<T> task) {
            if (!asyncBitmapLoaders.contains(task))
                asyncBitmapLoaders.add(task);
        }

        @Override
        public void onTaskFinished(AsyncBitmapLoader<T> task, Bitmap bitmap) {
            if (asyncBitmapLoaders.contains(task))
                asyncBitmapLoaders.remove(task);
            if (bitmap != null)
                listener.onBitmapLoaded(bitmap);
            task.setListener(null);
        }

        @Override
        public void onError(final IOException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listener.onLoadBitmapError(e);
                }
            });
        }
    }

    public interface OnBitmapLoadedListener {
        void onBitmapLoaded(Bitmap bitmap);
        void onLoadBitmapError(IOException e);
    }

    /**
     * Liefert zurück ob die Activity mit dem Service verbunden ist
     * @return {@code true}, wenn die Activity mit dem Service verbunden ist, ansonsten {@code false}
     */
    protected boolean isConnectedToService() {
        return photoStreamClient != null && bound;
    }

    /**
     * Wenn die Activity mit dem Service verbunden ist, dann wird die Instanz des Clients zurück geliefert, ansonsten {@code null}
     * @return Instanz des Clients oder {@code null}
     */
    protected IPhotoStreamClient getPhotoStreamClient() {
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
        photoStreamClient.unregisterActivity(this);
        bound = false;
        photoStreamClient = null;
    }

    protected static class AsyncBitmapLoader<T> extends AsyncTask<T, Void, Bitmap>{

        private final IDecoderStrategy<T> decoderStrategy;
        private OnMessageListener<T> messageListener;
        private T data;

        public AsyncBitmapLoader(IDecoderStrategy<T> decoderStrategy){
            this.decoderStrategy = decoderStrategy;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (messageListener != null)
                messageListener.onTaskStarted(this);
        }

        @Override
        protected Bitmap doInBackground(T... data) {
            this.data = data[0];
            Bitmap bitmap = null;
            try {
                bitmap = internalDecodeBitmap();
            } catch (IOException e) {
                messageListener.onError(e);
            }
            return bitmap;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (messageListener != null)
                messageListener.onTaskFinished(this, null);
        }

        @Override
        protected void onCancelled(Bitmap bitmap) {
            super.onCancelled(bitmap);
            if (bitmap != null)
                bitmap.recycle();
            if (messageListener != null)
                messageListener.onTaskFinished(this, null);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (!isCancelled())
                if (messageListener != null)
                    messageListener.onTaskFinished(this, bitmap);
            else {
                if (bitmap != null)
                    bitmap.recycle();
                if (messageListener != null)
                    messageListener.onTaskFinished(this, null);
            }
        }

        private Bitmap internalDecodeBitmap() throws FileNotFoundException {
            Bitmap bm = null;
            try {
                BitmapFactory.Options options = lessResolution(decoderStrategy.decode(data), 400, 350);
                bm = BitmapFactory.decodeStream(decoderStrategy.decode(data), null, options);
                ExifInterface exif = new ExifInterface(decoderStrategy.getAbsolutePath(data));
                String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
                int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;

                int rotationAngle = 0;
                if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
                else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
                else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

                if (rotationAngle != 0) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(rotationAngle);
                    Bitmap bmCopy = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
                    bm.recycle();
                    bm = bmCopy;
                }
            } catch (IOException e) {
                Logger.log(TAG, LogLevel.ERROR, e.toString());
            }
            return bm;
        }

        private static BitmapFactory.Options lessResolution (InputStream is, int reqWidth, int reqHeight) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            // First decode with inJustDecodeBounds=true to check dimensions
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, options);
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return options;
        }

        private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {
                // Calculate ratios of height and width to requested height and width
                final int heightRatio = Math.round(((float) height / (float) reqHeight));
                final int widthRatio = Math.round(((float) width / (float) reqWidth));

                // Choose the smallest ratio as inSampleSize value, this will guarantee
                // a final image with both dimensions larger than or equal to the
                // requested height and width.
                inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            }
            return inSampleSize;
        }

        public void setListener(OnMessageListener<T> messageListener) {
            this.messageListener = messageListener;
        }

        interface OnMessageListener<T> {
            void onTaskStarted(AsyncBitmapLoader<T> task);
            void onTaskFinished(AsyncBitmapLoader<T> task, Bitmap bitmap);
            void onError(IOException e);
        }

    }

    interface IDecoderStrategy<T> {
        InputStream decode(T data) throws IOException;
        String getAbsolutePath(T data);
    }

    private static class StreamDecoderFileStrategy implements IDecoderStrategy<File>{

        @Override
        public InputStream decode(File data) throws IOException {
            return new FileInputStream(data);
        }

        @Override
        public String getAbsolutePath(File data) {
            return data.getAbsolutePath();
        }
    }

    private static class AssetDecoderStrategy implements IDecoderStrategy<String> {

        private final Context context;

        public AssetDecoderStrategy(Context context){
            this.context = context;
        }

        @Override
        public InputStream decode(String assetFileName) throws IOException {
            return context.getAssets().open(assetFileName);
        }

        @Override
        public String getAbsolutePath(String data) {
            return getRealPathFromURI(context, Uri.parse(String.format("assets://%s", data)));
        }

        private static String getRealPathFromURI(Context context, Uri contentURI) {
            String result;
            Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
            if (cursor == null) {
                result = contentURI.getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
                cursor.close();
            }
            return result;
        }

    }

    private static class UriDecoderStrategy implements IDecoderStrategy<Uri>{

        private final ContentResolver contentResolver;

        public UriDecoderStrategy(ContentResolver contentResolver){
            this.contentResolver = contentResolver;
        }

        @Override
        public InputStream decode(Uri data) throws IOException {
            return contentResolver.openInputStream(data);
        }

        @Override
        public String getAbsolutePath(Uri data) {
            return getRealPathFromURI(contentResolver, data);
        }

        private static String getRealPathFromURI(ContentResolver contentResolver, Uri contentURI) {
            String result;
            Cursor cursor = contentResolver.query(contentURI, null, null, null, null);
            if (cursor == null) {
                result = contentURI.getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
                cursor.close();
            }
            return result;
        }

    }

}
