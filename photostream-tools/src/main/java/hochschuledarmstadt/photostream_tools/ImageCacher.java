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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.os.EnvironmentCompat;
import android.util.Base64;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import hochschuledarmstadt.photostream_tools.model.Photo;

class ImageCacher {

    private final Context context;

    public ImageCacher(Context context){
        this.context = context;
    }

    private static final String FILENAME_FORMAT = "%s.jpg";

    private String getImageFileName(int id){
        return String.format(FILENAME_FORMAT, id);
    }

    private File concatImageFilePath(Context context, String imageFileName){
        File file = new File(context.getFilesDir(), imageFileName);
        if (!file.exists() && Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), imageFileName);
        }
        return file;
    }

    boolean cacheImage(Photo photo)throws IOException {
        int photoId = photo.getId();
        if (isCached(photoId)) {
            File filePath = getImageFilePathForPhotoId(photoId);
            String imageFilePath = filePath.getAbsolutePath();
            injectImageFilePath(photo, imageFilePath);
            return true;
        }
        else {
            String imageFilePath = photo.getImageFilePath();
            return cacheImage(photo, Base64.decode(imageFilePath, Base64.DEFAULT));
        }
    }

    boolean cacheImage(Photo photo, byte[] data) throws IOException {
        int photoId = photo.getId();
        boolean inCache = false;
        boolean error = false;
        String filename = getImageFileName(photoId);
        File imageFilePath = concatImageFilePath(context, filename);
        if (!imageExistsOnFileSystem(filename)) {
            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(imageFilePath, false);
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                bitmap.recycle();
                inCache = true;
            } catch (Exception e) {
                error = true;
            } finally {
                if (outputStream != null)
                    outputStream.close();
            }
            if (error){
                File file = getImageFilePathForPhotoId(photoId);
                if (file.exists())
                    file.delete();
            }
        }else{
            inCache = true;
        }

        injectImageFilePath(photo, imageFilePath.getAbsolutePath());

        return inCache;

    }

    private void injectImageFilePath(Photo photo, String imageFilePath) {
        try {
            Field f = photo.getClass().getDeclaredField("imageFilePath");
            f.setAccessible(true);
            f.set(photo, imageFilePath);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private boolean imageExistsOnFileSystem(String filename) {
        File file = concatImageFilePath(context, filename);
        return file.exists();
    }

    File getImageFilePathForPhotoId(int photoId) {
        return concatImageFilePath(context, getImageFileName(photoId));
    }

    boolean isCached(int photoId) {
        return imageExistsOnFileSystem(getImageFileName(photoId));
    }

}
