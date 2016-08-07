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

package hochschuledarmstadt.photostream_tools.examples.photo;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import hochschuledarmstadt.photostream_tools.BitmapUtils;
import hochschuledarmstadt.photostream_tools.FullscreenPhotoActivity;
import hochschuledarmstadt.photostream_tools.IPhotoStreamClient;
import hochschuledarmstadt.photostream_tools.examples.R;
import hochschuledarmstadt.photostream_tools.model.Photo;

public class FullscreenActivity extends FullscreenPhotoActivity {

    public static final String KEY_PHOTO = "KEY_PHOTO";
    private ImageView imageView;
    private boolean isFirstStart = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_layout);
        imageView = (ImageView) findViewById(R.id.imageView);
        isFirstStart = (savedInstanceState == null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Photo photo = getIntent().getParcelableExtra(KEY_PHOTO);
        File imageFile = photo.getImageFile();
        loadBitmapAsync(imageFile, new OnBitmapLoadedListener() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
                if (isFirstStart) {
                    isFirstStart = false;
                    imageView.setScaleY(0.5f);
                    imageView.setScaleX(0.5f);
                    imageView.setAlpha(0.1f);
                    imageView.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(800).start();
                }
            }

            @Override
            public void onError(IOException e) {

            }
        });
    }

    @Override
    protected void onStop() {
        BitmapUtils.recycleBitmapFromImageView(imageView);
        super.onStop();
    }

    @Override
    protected void onSystemUiVisible() {

    }

    @Override
    protected void onSystemUiHidden() {

    }

    @Override
    protected void onPhotoStreamServiceConnected(IPhotoStreamClient photoStreamClient, Bundle savedInstanceState) {

    }

    @Override
    protected void onPhotoStreamServiceDisconnected(IPhotoStreamClient photoStreamClient) {

    }
}
