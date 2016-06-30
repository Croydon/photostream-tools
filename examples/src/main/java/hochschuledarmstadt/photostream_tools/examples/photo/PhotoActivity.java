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

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import hochschuledarmstadt.photostream_tools.adapter.DividerItemDecoration;
import hochschuledarmstadt.photostream_tools.adapter.SimplePhotoAdapter;
import hochschuledarmstadt.photostream_tools.callback.OnNewPhotoReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotoDeletedListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotosReceivedListener;
import hochschuledarmstadt.photostream_tools.examples.R;
import hochschuledarmstadt.photostream_tools.examples.Utils;
import hochschuledarmstadt.photostream_tools.model.HttpResult;
import hochschuledarmstadt.photostream_tools.model.Photo;
import hochschuledarmstadt.photostream_tools.model.PhotoQueryResult;

public class PhotoActivity extends PhotoStreamActivity implements OnPhotosReceivedListener, OnPhotoDeletedListener, OnNewPhotoReceivedListener {

    private static final int COLUMNS_PER_ROW = 2;
    private static final String KEY_ADAPTER = "KEY_ADAPTER";
    private static final String KEY_BUTTON_VISIBILITY = "KEY_BUTTON_VISIBILITY";
    private static final String TAG = PhotoActivity.class.getName();

    private RecyclerView recyclerView;
    private PhotoAdapter adapter;
    private Button loadMoreButton;

    @Override
    protected void onPhotoStreamServiceConnected(IPhotoStreamClient photoStreamClient, Bundle savedInstanceState) {
        Log.d(TAG, "onPhotoStreamServiceConnected()");
        photoStreamClient.addOnPhotosReceivedListener(this);
        if (savedInstanceState == null)
            photoStreamClient.loadPhotos();
    }

    @Override
    protected void onPhotoStreamServiceDisconnected(IPhotoStreamClient photoStreamClient) {
        Log.d(TAG, "onPhotoStreamServiceDisconnected()");
        photoStreamClient.removeOnPhotosReceivedListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        Log.d(TAG, "onCreate()");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, COLUMNS_PER_ROW));
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        loadMoreButton = (Button) findViewById(R.id.button);
        loadMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IPhotoStreamClient photoStreamClient = getPhotoStreamClient();
                if (!photoStreamClient.hasOpenRequestsOfType(RequestType.LOAD_PHOTOS)) {
                    photoStreamClient.loadMorePhotos();
                }
            }
        });

        adapter = new PhotoAdapter();
        adapter.setOnItemClickListener(R.id.imageView, new SimplePhotoAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View v, Photo photo) {
                Toast.makeText(PhotoActivity.this, String.format("photo id: %s", photo.getId()), Toast.LENGTH_SHORT).show();
            }
        });

        if (savedInstanceState == null){
            loadMoreButton.setVisibility(Button.GONE);
        }else{
            boolean buttonVisible = savedInstanceState.getInt(KEY_BUTTON_VISIBILITY) == 1;
            loadMoreButton.setVisibility(buttonVisible ? Button.VISIBLE : Button.GONE);
            adapter.restoreInstanceState(savedInstanceState.getBundle(KEY_ADAPTER));
        }
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState()");
        outState.putBundle(KEY_ADAPTER, adapter.saveInstanceState());
        outState.putInt(KEY_BUTTON_VISIBILITY, loadMoreButton.getVisibility() == Button.VISIBLE ? 1 : 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh){
            final IPhotoStreamClient client = getPhotoStreamClient();
            if (!client.hasOpenRequestsOfType(RequestType.LOAD_PHOTOS)){
                getPhotoStreamClient().loadPhotos();
            }
            return true;
        }
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
        // Den Button sichtbar machen, wenn weitere Seiten im Stream vorhanden sind, ansonsten ausblenden
        loadMoreButton.setVisibility(result.hasNextPage() ? Button.VISIBLE : Button.GONE);
    }

    @Override
    public void onReceivePhotosFailed(HttpResult httpResult) {
        String title = "Could not load photos";
        Utils.showErrorInAlertDialog(this, title, httpResult);
    }

    @Override
    public void onNoNewPhotosAvailable() {

    }

    @Override
    public void onShowProgressDialog() {
        findViewById(R.id.progressCircle).setVisibility(View.VISIBLE);
    }

    @Override
    public void onDismissProgressDialog() {
        findViewById(R.id.progressCircle).setVisibility(View.GONE);
    }

    @Override
    public void onNewPhotoReceived(Photo photo) {
        adapter.add(photo);
    }

    @Override
    public void onPhotoDeleted(int photoId) {
        adapter.remove(photoId);
    }

    @Override
    public void onPhotoDeleteFailed(int photoId, HttpResult httpResult) {

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
