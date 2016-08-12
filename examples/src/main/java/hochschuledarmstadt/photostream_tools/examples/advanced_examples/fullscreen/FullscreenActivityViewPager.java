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

package hochschuledarmstadt.photostream_tools.examples.advanced_examples.fullscreen;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import hochschuledarmstadt.photostream_tools.BitmapUtils;
import hochschuledarmstadt.photostream_tools.FullscreenPhotoActivity;
import hochschuledarmstadt.photostream_tools.IPhotoStreamClient;
import hochschuledarmstadt.photostream_tools.RequestType;
import hochschuledarmstadt.photostream_tools.adapter.BasePhotoPagerAdapter;
import hochschuledarmstadt.photostream_tools.callback.OnPhotosReceivedListener;
import hochschuledarmstadt.photostream_tools.examples.R;
import hochschuledarmstadt.photostream_tools.examples.Utils;
import hochschuledarmstadt.photostream_tools.model.HttpError;
import hochschuledarmstadt.photostream_tools.model.Photo;
import hochschuledarmstadt.photostream_tools.model.PhotoQueryResult;

public class FullscreenActivityViewPager extends FullscreenPhotoActivity implements OnPhotosReceivedListener {

    // Key für das Speichern der Instanzdaten des Adapters in der onSaveInstanceState Methode
    private static final String KEY_ADAPTER = "KEY_ADAPTER";

    // Eine Art Slider im User Interface
    private ViewPager viewPager;
    // Adapter (Datenquelle) für ViewPager
    private MyPagerAdapter adapter;
    // Listener für Callbacks beim Wechseln einer Seite
    private ViewPager.OnPageChangeListener listener;
    // Repräsentiert ob das aktuelle angezeigte Photo gezoomt dargestellt wird
    private boolean isZoomedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Layout erzeugen
        setContentView(R.layout.activity_fullscreen_viewpager);
        // Referenz auf ViewPager setzen
        viewPager = (ViewPager) findViewById(R.id.pager);
        // Anonyme Instanz des Interfaces erzeugen
        listener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Wird hier nicht benötigt
            }

            @Override
            public void onPageSelected(int position) {
                adapter.updateImageView(position);
                isZoomedIn = false;
                if (isConnectedToService())
                    loadMorePhotosIfPossible();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Wird hier ebenfalls nicht benötigt
            }
        };

        // Listener dem ViewPager hinzufügen
        viewPager.addOnPageChangeListener(listener);

        // Adapter erzeugen. Parameter entspricht dem Layout, dass für eine Seite angezeigt werden soll
        adapter = new MyPagerAdapter(R.layout.activity_fullscreen_layout);

        // Wenn die Activity wieder hergestellt wird
        if (savedInstanceState != null){
            // dann Status des vorherigen Adapters wieder herstellen.
            adapter.restoreInstanceState(savedInstanceState.getParcelable(KEY_ADAPTER));
        }

        // Adapter dem ViewPager als Datenquelle zuweisen
        viewPager.setAdapter(adapter);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Speichern des aktuellen Status des Adapters
        outState.putParcelable(KEY_ADAPTER, adapter.saveInstanceState());
    }

    @Override
    protected void onDestroy() {
        // Vor dem Zerstören der Activity den Listener wieder entfernen
        viewPager.removeOnPageChangeListener(listener);
        super.onDestroy();
    }

    @Override
    protected void onSystemUiVisible() {
        // Weiterleitung des Events an den Adapter
        if (adapter != null)
            adapter.onSystemUiVisible(viewPager.getCurrentItem());
    }

    @Override
    protected void onSystemUiHidden() {
        // Weiterleitung des Events an den Adapter
        if (adapter != null)
            adapter.onSystemUiHidden(viewPager.getCurrentItem());
    }

    @Override
    protected void onPhotoStreamServiceConnected(IPhotoStreamClient photoStreamClient, Bundle savedInstanceState) {
        photoStreamClient.addOnPhotosReceivedListener(this);
        // Erste Seite von Photos, beim ersten Start der Activity, aus dem Stream laden.
        if (savedInstanceState == null) {
            photoStreamClient.loadPhotos();
        }
    }

    private void loadMorePhotosIfPossible() {
        // Ist man beim vorletzten Element in der Liste angelangt und es ist kein Request dieser Art bereits am Laufen
        if (viewPager.getCurrentItem() + 2 > adapter.getCount() && !getPhotoStreamClient().hasOpenRequestOfType(RequestType.LOAD_PHOTOS))
            // Dann die nächste Seite aus dem Stream laden
            getPhotoStreamClient().loadMorePhotos();
    }

    @Override
    protected void onPhotoStreamServiceDisconnected(IPhotoStreamClient photoStreamClient) {
        // Listener wieder entfernen
        photoStreamClient.removeOnPhotosReceivedListener(this);
    }

    @Override
    public void onPhotosReceived(PhotoQueryResult result) {
        if (result.isFirstPage())
            adapter.set(result.getPhotos());
        else
            adapter.addAll(result.getPhotos());

    }

    @Override
    public void onReceivePhotosFailed(HttpError httpError) {
        Utils.showErrorInAlertDialog(this, "Fehler beim Laden von Photos", httpError);
    }

    @Override
    public void onNoNewPhotosAvailable() {

    }

    private class MyPagerAdapter extends BasePhotoPagerAdapter {

        public MyPagerAdapter(@LayoutRes int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void onBindView(final ViewGroup layout, final int position, Photo photo) {
            File imageFile = photo.getImageFile();
            initViews(layout, position, imageFile);
            updateButtonVisibility(layout, isSystemUiVisible());
        }

        private void initViews(ViewGroup layout, final int position, File imageFile) {

            final Button button = (Button) layout.findViewById(R.id.button);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(FullscreenActivityViewPager.this, "button clicked", Toast.LENGTH_SHORT).show();
                }
            });

            final ImageView imageView = (ImageView) layout.findViewById(R.id.imageView);

            loadBitmapAsync(imageFile, new OnBitmapLoadedListener() {

                @Override
                public void onBitmapLoaded(Bitmap bitmap) {
                    imageView.setImageBitmap(bitmap);

                    if (position == viewPager.getCurrentItem()) {
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
                    }
                }

                @Override
                public void onLoadBitmapError(IOException e) {
                    Log.e(FullscreenActivityViewPager.class.getName(), e.toString(), e);
                }

            });
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object view) {
            super.destroyItem(container, position, view);
            // Adapter will das Layout löschen. Das Bitmap Objekt muss hier recycled werden.
            View v = (View) view;
            ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
            BitmapUtils.recycleBitmapFromImageView(imageView);
        }

        public void updateImageView(int position) {
            // Position im ViewPager hat sich verändert.
            // Das entsprechende Layout an der aktuellen Position muss aktualisiert werden.
            ViewGroup v = (ViewGroup) getViewAtPosition(position);
            if (v != null) {
                Photo photo = getPhotoAtPosition(position);
                initViews(v, position, photo.getImageFile());
            }
        }

        public void onSystemUiVisible(int position) {
            // Bisschen tricky. Im ViewPager sind standardmäßig drei Layouts aktiv.
            // Das aktuelle Layout, welches angezeigt wird, und die Layout links und rechts davon.
            // Wenn die System UI sichtbar wird, müssen alle drei berücksichtigt werden. Allerdings muss man
            // hier aufpassen, weil die erste Seite zum Beispiel kein "linkes Layout" besitzt. Deswegen die Variablen
            // begin und end
            int begin = position == 0 ? 0 : -1;
            int end = position >= getCount() - 1 ? 0 : 1;
            for (int i = begin; i <= end; i++) {
                try {
                    Photo photo = getPhotoAtPosition(position + i);
                    ViewGroup v = (ViewGroup) getViewWithId(photo.getId());
                    onSystemUiVisible(v);
                } catch (Exception e) {
                }
            }
        }

        public void onSystemUiHidden(int position) {
            // siehe onSystemUiVisible(int position)
            int begin = position == 0 ? 0 : -1;
            int end = position == getCount() - 1 ? 0 : 1;
            for (int i = begin; i <= end; i++) {
                try {
                    Photo photo = getPhotoAtPosition(position + i);
                    ViewGroup v = (ViewGroup) getViewWithId(photo.getId());
                    onSystemUiHidden(v);
                } catch (Exception e) {
                }
            }
        }

        private void onSystemUiVisible(ViewGroup v) {
            try {
                v.findViewById(R.id.button).setVisibility(!isZoomedIn ? View.VISIBLE : View.GONE);
            } catch (Exception e) {}
        }

        private void onSystemUiHidden(ViewGroup v) {
            try {
                v.findViewById(R.id.button).setVisibility(View.GONE);
            } catch (Exception e) {}
        }

        public void updateButtonVisibility(ViewGroup layout, boolean systemUiVisible) {
            if (systemUiVisible)
                onSystemUiVisible(layout);
            else
                onSystemUiHidden(layout);
        }
    }

}

