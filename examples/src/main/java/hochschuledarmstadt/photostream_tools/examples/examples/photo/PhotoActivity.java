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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import hochschuledarmstadt.photostream_tools.IPhotoStreamClient;
import hochschuledarmstadt.photostream_tools.PhotoStreamActivity;
import hochschuledarmstadt.photostream_tools.RequestType;
import hochschuledarmstadt.photostream_tools.adapter.BasePhotoAdapter;
import hochschuledarmstadt.photostream_tools.callback.OnCommentCountChangedListener;
import hochschuledarmstadt.photostream_tools.callback.OnNewPhotoReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotosReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnRequestListener;
import hochschuledarmstadt.photostream_tools.examples.R;
import hochschuledarmstadt.photostream_tools.examples.Utils;
import hochschuledarmstadt.photostream_tools.examples.advanced_examples.fullscreen.FullscreenActivityViewPager;
import hochschuledarmstadt.photostream_tools.model.HttpError;
import hochschuledarmstadt.photostream_tools.model.Photo;
import hochschuledarmstadt.photostream_tools.model.PhotoQueryResult;

public class PhotoActivity extends PhotoStreamActivity implements OnRequestListener, OnPhotosReceivedListener, OnNewPhotoReceivedListener, OnCommentCountChangedListener {

    /**
     * Anzahl von Spalten in der RecyclerView
     */
    private static final int COLUMNS_PER_ROW = 2;

    /**
     * Key für das Zwischenspeichern der Photos in der Methode {@link PhotoActivity#onSaveInstanceState(Bundle)}"
     */
    private static final String KEY_ADAPTER = "KEY_ADAPTER";

    /**
     * Key für das Zwischenspeichern des Sichtbarkeitsstatus des Buttons {@code loadMoreButton}
     */
    private static final String KEY_BUTTON_VISIBILITY = "KEY_BUTTON_VISIBILITY";

    private static final String KEY_BUTTON_ENABLED = "KEY_BUTTON_ENABLED";
    private static final String TAG = PhotoActivity.class.getName();

    /**
     * Für die Darstellung der Photos in einer Liste
     */
    private RecyclerView recyclerView;

    /**
     * Datenquelle für {@code recyclerView}
     */
    private PhotoAdapter adapter;

    /**
     * Zum Laden für weitere Seiten aus dem Stream
     */
    private Button loadMoreButton;


    @Override
    protected void onPhotoStreamServiceConnected(IPhotoStreamClient photoStreamClient, Bundle savedInstanceState) {
        Log.d(TAG, "onPhotoStreamServiceConnected()");
        /**
         * Listener registrieren
         */
        photoStreamClient.addOnRequestListener(this, RequestType.LOAD_PHOTOS);
        photoStreamClient.addOnPhotosReceivedListener(this);
        photoStreamClient.addOnCommentCountChangedListener(this);
        /**
         * Beim ersten Start der Activity soll die erste Seite aus dem Stream geladen werden
         */
        if (savedInstanceState == null){
            photoStreamClient.loadPhotos();
        }
    }

    @Override
    protected void onPhotoStreamServiceDisconnected(IPhotoStreamClient photoStreamClient) {
        Log.d(TAG, "onPhotoStreamServiceDisconnected()");
        /**
         * Alle registrierten Listener wieder entfernen
         */
        photoStreamClient.removeOnRequestListener(this);
        photoStreamClient.removeOnPhotosReceivedListener(this);
        photoStreamClient.removeOnCommentCountChangedListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_photo);

        // Referenz setzen auf RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // GridLayoutManager setzen. Anzahl der Spalten definiert durch COLUMNS_PER_ROW
        recyclerView.setLayoutManager(new GridLayoutManager(this, COLUMNS_PER_ROW));

        // Optional: Eine Element in der Liste animieren,
        // wenn es hinzugefügt, gelöscht, geändert oder verschoben wurde
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Referenz auf Button setzen
        loadMoreButton = (Button) findViewById(R.id.button);

        // OnClickListener registrieren
        loadMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Button deaktivieren
                loadMoreButton.setEnabled(false);
                IPhotoStreamClient photoStreamClient = getPhotoStreamClient();
                // Wenn nicht bereits ein Request für Photos ausgeführt wird,
                if (!photoStreamClient.hasOpenRequestOfType(RequestType.LOAD_PHOTOS)) {
                    // dann nächste Seite aus dem Stream laden
                    photoStreamClient.loadMorePhotos();
                }
            }
        });

        // Photoadapter erzeugen
        adapter = new PhotoAdapter();

        // OnItemClickListener für die ImageView mit der id "imageView" setzen.
        adapter.setOnItemClickListener(R.id.imageView, new BasePhotoAdapter.OnItemClickListener<PhotoAdapter.PhotoViewHolder>() {
            @Override
            public void onItemClicked(PhotoAdapter.PhotoViewHolder viewHolder, View v, Photo photo) {
                // Wenn auf die ImageView ein Klick ausgelöst wurde, dann die FullscreenActivity starten,
                // um das Photo im Vollbild anzuzeigen
                Intent intent = new Intent(PhotoActivity.this, FullscreenActivity.class);
                intent.putExtra(FullscreenActivity.KEY_PHOTO, photo);
                startActivity(intent);
            }
        });

        // Beispiel für einen weiteren OnItemClickListener
        adapter.setOnItemClickListener(R.id.textView, new BasePhotoAdapter.OnItemClickListener<PhotoAdapter.PhotoViewHolder>() {
            @Override
            public void onItemClicked(PhotoAdapter.PhotoViewHolder viewHolder, View v, Photo photo) {
                // Zeigt die ID des Photos in einem Toast an, wenn auf dessen Beschreibung geklickt wurde
                Toast.makeText(PhotoActivity.this, String.valueOf(photo.getId()), Toast.LENGTH_SHORT).show();
            }
        });

        // Als Letztes der RecyclerView den Adapter als Datenquelle zuweisen
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState()");
        // Aktuellen Zustand des Adapters zwischenspeichern
        outState.putBundle(KEY_ADAPTER, adapter.saveInstanceState());
        // Zwischenspeichern ob der Button sichtbar ist
        outState.putBoolean(KEY_BUTTON_VISIBILITY, loadMoreButton.getVisibility() == Button.VISIBLE);
        // Zwischenspeichern ob der Button aktiviert ist
        outState.putBoolean(KEY_BUTTON_ENABLED, loadMoreButton.isEnabled());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Zwischengespeicherte Zustände wiederherstellen
        boolean buttonVisible = savedInstanceState.getBoolean(KEY_BUTTON_VISIBILITY);
        boolean buttonEnabled = savedInstanceState.getBoolean(KEY_BUTTON_ENABLED);
        // Den zwischengespeicherten Sichtbarkeitsstatus für den Button setzen
        loadMoreButton.setVisibility(buttonVisible ? Button.VISIBLE : Button.GONE);
        // Button als aktiviert oder deaktiviert setzen
        loadMoreButton.setEnabled(buttonEnabled);
        // Photos wieder herstellen
        adapter.restoreInstanceState(savedInstanceState.getBundle(KEY_ADAPTER));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Menü erzeugen für diese Activity. Wird in der Toolbar/ActionBar angezeigt
        getMenuInflater().inflate(R.menu.menu_photo_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Wenn auf den Menüpunkt mit der id "action_refresh" geklickt wurde
        if (item.getItemId() == R.id.action_refresh){
            final IPhotoStreamClient client = getPhotoStreamClient();
            // Wenn nicht bereits ein Request für Photos ausgeführt wird,
            if (!client.hasOpenRequestOfType(RequestType.LOAD_PHOTOS)){
                // dann die erste Seite aus dem Stream laden
                getPhotoStreamClient().loadPhotos();
            }
            // true zurückgeben, um dem System mitzuteilen, dass das Event verarbeitet wurde
            return true;
        }
        // Wenn das Event nicht verarbeitet wurde, dann das Event weiterleiten
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPhotosReceived(PhotoQueryResult result) {
        List<Photo> photos = result.getPhotos();
        if (result.isFirstPage()){
            // Zum ersten Mal abgerufen oder Aktualisierung des Streams wurde explizit angefordet => Photos ersetzen
            adapter.set(photos);
        }else{
            // Photos an die Liste anhängen
            adapter.addAll(photos);
        }
        // Request ist beendet, also kann der Button wieder aktiviert werden
        loadMoreButton.setEnabled(true);
        // Den Button sichtbar machen, wenn weitere Seiten im Stream vorhanden sind, ansonsten ausblenden
        loadMoreButton.setVisibility(result.hasNextPage() ? Button.VISIBLE : Button.GONE);
    }

    @Override
    public void onReceivePhotosFailed(HttpError httpError) {
        String title = "Could not load photos";
        // Fehler anzeigen in einem AlertDialog
        Utils.showErrorInAlertDialog(this, title, httpError);
        // Request ist beendet, also kann der Button wieder aktiviert werden
        loadMoreButton.setEnabled(true);
    }

    @Override
    public void onNoNewPhotosAvailable() {
        // Keine neuen Photos verfügbar, Stream hat sich nicht verändert
    }

    @Override
    public void onCommentCountChanged(int photoId, int commentCount) {
        // Anzahl der Kommentare für das Photo mit der id "photoId" hat sich geändert.
        // Anzahl der Kommentare für dieses Photo im Adapter aktualisieren
        adapter.updateCommentCount(photoId, commentCount);
        Toast.makeText(this, String.format("Photo mit der Id \"%d\" hat jetzt %d Kommentare", photoId, commentCount), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestStarted() {
        // Anzeigen, dass ein HTTP Request gestartet wurde
        findViewById(R.id.progressCircle).setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestFinished() {
        // Wieder unsichtbar machen, nachdem keine Requests mehr laufen
        findViewById(R.id.progressCircle).setVisibility(View.GONE);
    }

    @Override
    public void onNewPhotoReceived(Photo photo) {
        // Ein neues Photo an erster Position in der Liste anfügen
        adapter.addAtFront(photo);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }
}
