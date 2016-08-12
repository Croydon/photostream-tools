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

package hochschuledarmstadt.photostream_tools.examples.examples.photo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import hochschuledarmstadt.photostream_tools.BitmapUtils;
import hochschuledarmstadt.photostream_tools.FullscreenPhotoActivity;
import hochschuledarmstadt.photostream_tools.IPhotoStreamClient;
import hochschuledarmstadt.photostream_tools.examples.R;
import hochschuledarmstadt.photostream_tools.examples.Utils;
import hochschuledarmstadt.photostream_tools.model.Photo;

public class FullscreenActivity extends FullscreenPhotoActivity {

    // Diese Activity erhält ein Photo über den empfangenen Intent.
    // Diese Variable dient zum referenzieren des Photos aus dem Intent.
    public static final String KEY_PHOTO = "KEY_PHOTO";

    // Für die Anzeige des Photos
    private ImageView imageView;
    // Ein Beispielbutton, der über das Photo gelegt wird
    private Button button;

    // Diese Variable gibt an, ob das Photo aktuell gezoomt dargestellt wird, oder nicht
    private boolean isZoomedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Als Erstes das Layout erzeugen
        setContentView(R.layout.activity_fullscreen_layout);

        // ImageView referenzieren
        imageView = (ImageView) findViewById(R.id.imageView);

        // Button referenzieren
        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Soll eine kurze Meldung anzeigen wenn auf den Button geklickt wurde
                Toast.makeText(FullscreenActivity.this, "button clicked", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Das Photo aus dem Intent referenzieren
        Photo photo = getIntent().getParcelableExtra(KEY_PHOTO);
        // Pfad zum Photo
        final File imageFile = photo.getImageFile();

        // Das Photo aus der Datei asynchron laden. Das Ergebnis wird im
        // OnBitmapLoadedListener zurückgegeben
        loadBitmapAsync(imageFile, new OnBitmapLoadedListener() {

            @Override
            public void onBitmapLoaded(Bitmap bitmap) {
                // Das geladene Bild setzen
                imageView.setImageBitmap(bitmap);

                // Logik für Zoom initialisieren
                setImageViewZoomable(imageView, new OnImageViewZoomChangedListener() {
                    @Override
                    public void onImageViewZoomReset() {
                        // Zoomfaktor auf 1.0, also wird das Bild nicht gezoomt dargestellt
                        isZoomedIn = false;
                        // Wenn Navigationbar und Statusbar sichtbar sind
                        if (isSystemUiVisible())
                            // dann auch den Button sichtbar machen
                            button.setVisibility(Button.VISIBLE);
                    }

                    @Override
                    public void onImageViewZoomedIn() {
                        // Zoomfaktor > 1.0, also wird das Bild gezoomt dargestellt
                        isZoomedIn = true;
                        // Wenn das Bild also gezoomt ist, dann den Button unsichtbar machen
                        button.setVisibility(Button.GONE);
                    }
                });

                // Wenn das Bild geladen wurde, dann eine kurze Animation durchführen
                animateImageView();

            }

            @Override
            public void onLoadBitmapError(IOException e) {
                Log.e(FullscreenActivity.class.getName(), "Fehler beim Dekodieren des Bilds", e);
            }
        });
    }

    private void animateImageView() {
        // Das angezeigte Bild zunächst auf die Hälfte der Größe zunächst skalieren
        imageView.setScaleY(0.5f);
        imageView.setScaleX(0.5f);
        // und fast unsichtbar darstellen
        imageView.setAlpha(0.1f);
        // Animation auf vollständige Größe und volle Sichtbarkeit durchführen
        imageView.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(800).start();
    }

    @Override
    protected void onStop() {
        // Wenn die Activity nicht mehr angezeigt wird, dann Speicher freigeben,
        // indem das Bild von der ImageView entfernt wird und das Bitmap Objekt
        // freigegeben wird.
        // Wird die Activity wieder angezeigt, dann wird unter anderem die onStart() Methode
        // ausgeführt, und das Bild wird erneut geladen
        BitmapUtils.recycleBitmapFromImageView(imageView);
        super.onStop();
    }

    @Override
    protected void onSystemUiVisible() {
        // Wenn Navigationbar und Statusbar angezeigt werden
        // und das Bild nicht gezoomt dargestellt wird,
        if (!isZoomedIn)
            // dann den Button anzeigen
            button.setVisibility(Button.VISIBLE);
    }

    @Override
    protected void onSystemUiHidden() {
        // Navigationbar und Statusbar werden nicht mehr angezeigt
        // Button unsichtbar machen
        button.setVisibility(Button.GONE);
    }

    @Override
    protected void onPhotoStreamServiceConnected(IPhotoStreamClient photoStreamClient, Bundle savedInstanceState) {
        // Wird in diesem Beispiel nicht verwendet
    }

    @Override
    protected void onPhotoStreamServiceDisconnected(IPhotoStreamClient photoStreamClient) {
        // Wird in diesem Beispiel nicht verwendet
    }

}
