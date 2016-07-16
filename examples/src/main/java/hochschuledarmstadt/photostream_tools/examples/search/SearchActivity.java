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

package hochschuledarmstadt.photostream_tools.examples.search;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import hochschuledarmstadt.photostream_tools.IPhotoStreamClient;
import hochschuledarmstadt.photostream_tools.PhotoStreamActivity;
import hochschuledarmstadt.photostream_tools.adapter.DividerItemDecoration;
import hochschuledarmstadt.photostream_tools.adapter.SimplePhotoAdapter;
import hochschuledarmstadt.photostream_tools.callback.OnSearchedPhotosReceivedListener;
import hochschuledarmstadt.photostream_tools.examples.R;
import hochschuledarmstadt.photostream_tools.examples.Utils;
import hochschuledarmstadt.photostream_tools.examples.photo.PhotoAdapter;
import hochschuledarmstadt.photostream_tools.model.HttpResult;
import hochschuledarmstadt.photostream_tools.model.Photo;
import hochschuledarmstadt.photostream_tools.model.PhotoQueryResult;

public class SearchActivity extends PhotoStreamActivity implements OnSearchedPhotosReceivedListener {

    private static final int COLUMNS_PER_ROW = 2;
    public static final String KEY_SEARCHVIEW_QUERY = "KEY_SEARCHVIEW_QUERY";
    public static final String KEY_SEARCHVIEW_EXPANDED = "KEY_SEARCHVIEW_EXPANDED";
    public static final String KEY_SEARCHVIEW_FOCUSED = "KEY_SEARCHVIEW_FOCUSED";
    private static final String KEY_PHOTOS = "KEY_PHOTOS";

    private PhotoAdapter photoAdapter;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private MenuItem searchMenuItem;
    private boolean searchViewFocused;
    private boolean searchViewExpanded;
    private String searchViewQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, COLUMNS_PER_ROW));
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        photoAdapter = new PhotoAdapter();

        if (savedInstanceState != null){
            Bundle bundle = savedInstanceState.getBundle(KEY_PHOTOS);
            photoAdapter.restoreInstanceState(bundle);
        }

        photoAdapter.setOnItemClickListener(R.id.imageView, new SimplePhotoAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View v, Photo photo) {
                Toast.makeText(SearchActivity.this, String.format("Photo Id: %s", photo.getId()), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(photoAdapter);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null){
            searchViewFocused = savedInstanceState.getBoolean(KEY_SEARCHVIEW_FOCUSED);
            searchViewExpanded = savedInstanceState.getBoolean(KEY_SEARCHVIEW_EXPANDED);
            searchViewQuery = savedInstanceState.getString(KEY_SEARCHVIEW_QUERY);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        setSearchViewListeners();
        updateSearchView();
        return super.onCreateOptionsMenu(menu);
    }

    private void setSearchViewListeners() {
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                searchViewFocused = hasFocus;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && query.trim().length() > 0) {
                    getPhotoStreamClient().searchPhotos(query);
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                searchViewExpanded = true;
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                searchViewExpanded = false;
                return true;
            }
        });
    }

    private void updateSearchView() {
        if (searchViewExpanded)
            MenuItemCompat.expandActionView(searchMenuItem);

        searchView.setQuery(searchViewQuery, false);

        if (searchViewFocused)
            searchView.requestFocus();
        else
            searchView.clearFocus();
    }

    @Override
    protected void onPhotoStreamServiceConnected(IPhotoStreamClient photoStreamClient, Bundle savedInstanceState) {
        photoStreamClient.addOnSearchPhotosResultListener(this);
    }

    @Override
    protected void onPhotoStreamServiceDisconnected(IPhotoStreamClient photoStreamClient) {
        photoStreamClient.removeOnSearchPhotosResultListener(this);
    }

    @Override
    public void onSearchedPhotosReceived(PhotoQueryResult result) {
        List<Photo> photos = result.getPhotos();
        photoAdapter.set(photos);
    }

    @Override
    public void onReceiveSearchedPhotosFailed(String query, HttpResult httpResult) {
        String title = "Could not load photos";
        Utils.showErrorInAlertDialog(this, title, httpResult);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(KEY_PHOTOS, photoAdapter.saveInstanceState());
        outState.putBoolean(KEY_SEARCHVIEW_EXPANDED, searchViewExpanded);
        outState.putBoolean(KEY_SEARCHVIEW_FOCUSED, searchViewFocused);
        outState.putString(KEY_SEARCHVIEW_QUERY, searchView.getQuery().toString());
    }

    @Override
    public void onShowProgressDialog() {
        findViewById(R.id.progressCircle).setVisibility(ProgressBar.VISIBLE);
    }

    @Override
    public void onDismissProgressDialog() {
        findViewById(R.id.progressCircle).setVisibility(ProgressBar.GONE);
    }
}
