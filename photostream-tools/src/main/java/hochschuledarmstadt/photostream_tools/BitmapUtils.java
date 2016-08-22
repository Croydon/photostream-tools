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

/**
 * Über die Methoden dieser Klasse können Bitmaps geladen werden, die direkt für den Photo Upload skaliert werden.
 * Zusätzlich sind Methoden für das Recyclen von Bitmaps und konvertieren von Bitmaps in Byte Arrays vorhanden.
 */
public final class BitmapUtils {

    private static final String TAG = BitmapUtils.class.getName();

    private static final int JPEG_QUALITY = 80;

    private BitmapUtils() { }

    /**
     * Entfernt ein zur ImageView zugeordnetes Bitmap aus dem Speicher
     * @param imageView ImageView welches das Bitmap anzeigt
     */
    public static void recycleBitmapFromImageView(ImageView imageView) {
        if (imageView != null && imageView.getDrawable() != null && imageView.getDrawable() instanceof BitmapDrawable){
            final Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            recycleBitmap(bitmap);
            imageView.setImageBitmap(null);
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
     * Konvertiert ein Bitmap {@code bitmap} in ein Byte Array
     * @param bitmap
     * @return byte[]
     */
    public static byte[] bitmapToBytes(Bitmap bitmap) {
        if (bitmap == null) throw new NullPointerException("bitmap ist null!");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, bos);
        return bos.toByteArray();
    }

    public static boolean isJPEG(byte[] data) throws IOException {
        return internalIsJPEG(new DataInputStream(new ByteArrayInputStream(data)));
    }

    public static boolean isJPEG(File file) throws IOException {
        return internalIsJPEG(new DataInputStream(new BufferedInputStream(new FileInputStream(file))));
    }

    private static boolean internalIsJPEG(DataInputStream in) throws IOException {
        boolean result = false;
        try {
            int headerBytes = in.readInt();
            result = (headerBytes == 0xffd8ffe0);
        } finally {
            in.close();
        }
        return result;
    }

}
