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

package hochschuledarmstadt.photostream_tools.examples.examples.search;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import hochschuledarmstadt.photostream_tools.IPhotoStreamClient;
import hochschuledarmstadt.photostream_tools.PhotoStreamActivity;
import hochschuledarmstadt.photostream_tools.RequestType;
import hochschuledarmstadt.photostream_tools.adapter.BasePhotoAdapter;
import hochschuledarmstadt.photostream_tools.callback.OnRequestListener;
import hochschuledarmstadt.photostream_tools.callback.OnSearchedPhotosReceivedListener;
import hochschuledarmstadt.photostream_tools.examples.R;
import hochschuledarmstadt.photostream_tools.examples.Utils;
import hochschuledarmstadt.photostream_tools.examples.examples.photo.PhotoAdapter;
import hochschuledarmstadt.photostream_tools.model.HttpError;
import hochschuledarmstadt.photostream_tools.model.Photo;
import hochschuledarmstadt.photostream_tools.model.PhotoQueryResult;
import hochschuledarmstadt.photostream_tools.widget.SearchViewDelegate;

public class SearchActivity extends PhotoStreamActivity implements OnSearchedPhotosReceivedListener, OnRequestListener {

    private static final int COLUMNS_PER_ROW = 2;
    private static final String KEY_PHOTOS = "KEY_PHOTOS";
    private static final String KEY_SEARCHVIEW = "SEARCH_VIEW";

    // View für das Anzeigen einer Liste
    private RecyclerView recyclerView;

    // Datenquelle für die RecyclerView
    private PhotoAdapter photoAdapter;

    // Container für die SearchView um den Status der SearchView zwischenspeichern zu können
    private SearchViewDelegate searchViewDelegate = new SearchViewDelegate();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, COLUMNS_PER_ROW));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        photoAdapter = new PhotoAdapter();

        photoAdapter.setOnItemClickListener(R.id.imageView, new BasePhotoAdapter.OnItemClickListener<PhotoAdapter.PhotoViewHolder>() {
            @Override
            public void onItemClicked(PhotoAdapter.PhotoViewHolder viewHolder, View v, Photo photo) {
                Toast.makeText(SearchActivity.this, String.format("Photo Id: %s", photo.getId()), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(photoAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_example, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        searchViewDelegate.setSearchViewMenuItem(searchMenuItem);
        searchViewDelegate.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                IPhotoStreamClient photoStreamClient = getPhotoStreamClient();
                if (query != null && query.trim().length() > 0 && !photoStreamClient.hasOpenRequestOfType(RequestType.SEARCH_PHOTOS)) {
                    photoStreamClient.searchPhotos(query);
                    // Event wurde behandelt
                    return true;
                }else{
                    // Event wurde nicht behandelt
                    return false;
                }
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(KEY_PHOTOS, photoAdapter.saveInstanceState());
        outState.putParcelable(KEY_SEARCHVIEW, searchViewDelegate.saveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        photoAdapter.restoreInstanceState(savedInstanceState.getBundle(KEY_PHOTOS));
        searchViewDelegate.restoreInstanceState(savedInstanceState.getParcelable(KEY_SEARCHVIEW));
    }

    @Override
    protected void onPhotoStreamServiceConnected(IPhotoStreamClient photoStreamClient, Bundle savedInstanceState) {
        photoStreamClient.addOnRequestListener(this, RequestType.SEARCH_PHOTOS);
        photoStreamClient.addOnSearchPhotosResultListener(this);
    }

    @Override
    protected void onPhotoStreamServiceDisconnected(IPhotoStreamClient photoStreamClient) {
        photoStreamClient.removeOnRequestListener(this);
        photoStreamClient.removeOnSearchPhotosResultListener(this);
    }

    @Override
    public void onSearchedPhotosReceived(PhotoQueryResult result) {
        List<Photo> photos = result.getPhotos();
        photoAdapter.set(photos);
    }

    @Override
    public void onReceiveSearchedPhotosFailed(String query, HttpError httpError) {
        String title = "Could not load photos";
        Utils.showErrorInAlertDialog(this, title, httpError);
    }

    @Override
    public void onRequestStarted() {
        findViewById(R.id.progressCircle).setVisibility(ProgressBar.VISIBLE);
    }

    @Override
    public void onRequestFinished() {
        findViewById(R.id.progressCircle).setVisibility(ProgressBar.GONE);
    }
}
