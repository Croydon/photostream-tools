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
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import hochschuledarmstadt.photostream_tools.BitmapUtils;
import hochschuledarmstadt.photostream_tools.FullscreenPhotoActivity;
import hochschuledarmstadt.photostream_tools.IPhotoStreamClient;
import hochschuledarmstadt.photostream_tools.PhotoStreamActivity;
import hochschuledarmstadt.photostream_tools.examples.R;
import hochschuledarmstadt.photostream_tools.model.Photo;

public class FullscreenActivity extends FullscreenPhotoActivity {

    public static final String KEY_PHOTO = "KEY_PHOTO";
    private ImageView imageView;
    private boolean isFirstStart = true;
    private Button button;
    private boolean isZoomedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_layout);

        imageView = (ImageView) findViewById(R.id.imageView);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(FullscreenActivity.this, "button clicked", Toast.LENGTH_SHORT).show();
            }
        });
        isFirstStart = (savedInstanceState == null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Photo photo = getIntent().getParcelableExtra(KEY_PHOTO);
        final File imageFile = photo.getImageFile();
        loadBitmapAsync(imageFile, new OnBitmapLoadedListener() {

            @Override
            public void onBitmapLoaded(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);

                // Logik für Zoom initialisieren
                setImageViewZoomable(imageView, new OnImageViewZoomChangedListener() {
                    @Override
                    public void onImageViewZoomReset() {
                        isZoomedIn = false;
                        if (isSystemUiVisible())
                            button.setVisibility(Button.VISIBLE);
                    }

                    @Override
                    public void onImageViewZoomedIn() {
                        isZoomedIn = true;
                        button.setVisibility(Button.GONE);
                    }
                });

                // Nur beim ersten Start der Activity die Animation durchführen
                if (isFirstStart) {
                    isFirstStart = false;
                    animateImageView();
                }
            }

            @Override
            public void onLoadBitmapError(IOException e) {
                Log.e(FullscreenActivity.class.getName(), e.toString(), e);
            }
        });
    }

    private void animateImageView() {
        imageView.setScaleY(0.5f);
        imageView.setScaleX(0.5f);
        imageView.setAlpha(0.1f);
        imageView.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(800).start();
    }

    @Override
    protected void onStop() {
        BitmapUtils.recycleBitmapFromImageView(imageView);
        super.onStop();
    }

    @Override
    protected void onSystemUiVisible() {
        if (!isZoomedIn)
            button.setVisibility(Button.VISIBLE);
    }

    @Override
    protected void onSystemUiHidden() {
        button.setVisibility(Button.GONE);
    }

    @Override
    protected void onPhotoStreamServiceConnected(IPhotoStreamClient photoStreamClient, Bundle savedInstanceState) {

    }

    @Override
    protected void onPhotoStreamServiceDisconnected(IPhotoStreamClient photoStreamClient) {

    }

}
