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

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Über die Methoden dieser Klasse können Bitmaps geladen werden, die direkt für den Photo Upload skaliert werden.
 * Zusätzlich sind Methoden für das Recyclen von Bitmaps und konvertieren von Bitmaps in Byte Arrays vorhanden.
 */
public final class BitmapUtils {

    private static final String TAG = BitmapUtils.class.getName();
    private static final int JPEG_QUALITY = 80;
    private static final int TYPE_ASSET = -1;
    private static final int TYPE_FILE = -2;
    private static final int TYPE_OTHER = -3;

    private BitmapUtils() { }

    /**
     * Entfernt ein zur ImageView zugeordnetes Bitmap aus dem Speicher
     * @param imageView ImageView welches das Bitmap anzeigt
     */
    public static void recycleBitmapFromImageView(ImageView imageView) {
        if (imageView != null && imageView.getDrawable() != null && imageView.getDrawable() instanceof BitmapDrawable){
            final Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            recycleBitmap(bitmap);
        }
    }

    /**
     * Entfernt ein Bitmap aus dem Speicher
     * @param bitmap das Bitmap
     */
    public static void recycleBitmap(Bitmap bitmap){
        if (bitmap != null && !bitmap.isRecycled())
            bitmap.recycle();
    }

    /**
     * Lädt ein Bitmap anhand eines Dateinamens aus dem Asset Ordner
     * @param context android context
     * @param assetFileName Dateiname
     * @return Bitmap
     * @throws FileNotFoundException wird geworfen, wenn die Resource nicht vorhanden ist
     */
    public static Bitmap decodeBitmapFromAssetFile(Context context, String assetFileName) throws FileNotFoundException {
        return internalDecodeBitmap(context, Uri.parse(String.format("assets://%s", assetFileName)), TYPE_ASSET);
    }

    /**
     * Lädt ein Bitmap anhand eines Dateipfads
     * @param file dateipfad
     * @return Bitmap
     * @throws FileNotFoundException wird geworfen, wenn die Resource nicht vorhanden ist
     */
    public static Bitmap decodeBitmapFromFile(File file) throws FileNotFoundException {
        return internalDecodeBitmap(null, Uri.fromFile(file), TYPE_FILE);
    }

    /**
     * Lädt ein Bitmap anhand einer Uri
     * @param context Android Context
     * @param uri bitmap source
     * @return Bitmap
     * @throws FileNotFoundException wird geworfen, wenn die Resource nicht vorhanden ist
     */
    public static Bitmap decodeBitmapFromUri(Context context, Uri uri) throws FileNotFoundException {
        if (uri == null) throw new NullPointerException("uri is null");
        return internalDecodeBitmap(context, uri, TYPE_OTHER);
    }


    private static InputStream createInputStream(Context context, Uri uri, int type) throws IOException {
        switch(type){
            case TYPE_ASSET:
                return context.getAssets().open(uri.toString().replace("assets://", ""));
            case TYPE_FILE:
                try{
                    return new FileInputStream(uri.toString());
                }catch(Exception e){
                    return new FileInputStream(new File(Uri.decode(uri.toString()).replace("file://","")));
                }
            case TYPE_OTHER:
                return context.getContentResolver().openInputStream(uri);
        }
        return null;
    }

    private static Bitmap internalDecodeBitmap(Context context, Uri uri, int type) throws FileNotFoundException {
        Bitmap bm = null;
        try {
            BitmapFactory.Options options = lessResolution(createInputStream(context, uri, type), 600, 600);
            bm = BitmapFactory.decodeStream(createInputStream(context, uri, type), null, options);
            if (context != null) {
                ExifInterface exif = new ExifInterface(getRealPathFromURI(context, uri));
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
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
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

    /**
     * Konvertiert ein Bitmap {@code bitmap} in ein Byte Array
     * @param bitmap
     * @return byte[]
     */
    public static byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, bos);
        bitmap.recycle();
        return bos.toByteArray();
    }

    public static boolean isJPEG(byte[] data) throws IOException {
        return internalIsJPEG(new DataInputStream(new ByteArrayInputStream(data)));
    }

    public static Boolean isJPEG(File file) throws IOException {
        return internalIsJPEG(new DataInputStream(new BufferedInputStream(new FileInputStream(file))));
    }

    private static boolean internalIsJPEG(DataInputStream in) throws IOException {
        boolean result = false;
        try {
            result = in.readInt() == 0xffd8ffe0;
        } finally {
            in.close();
        }
        return result;
    }

}
