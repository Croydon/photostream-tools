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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    private PhotoAdapter photoAdapter;
    private RecyclerView recyclerView;
    private Button loadMoreButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        loadMoreButton = (Button) findViewById(R.id.button);
        loadMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPhotoStreamClient().searchMorePhotos();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, COLUMNS_PER_ROW));
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        photoAdapter = new PhotoAdapter();
        photoAdapter.setOnItemClickListener(R.id.imageView, new SimplePhotoAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View v, Photo photo) {
                Toast.makeText(SearchActivity.this, String.format("Photo Id: %s", photo.getId()), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(photoAdapter);
    }

    @Override
    protected void onPhotoStreamServiceConnected(IPhotoStreamClient photoStreamClient, Bundle savedInstanceState) {
        photoStreamClient.addOnSearchPhotosResultListener(this);
        if (savedInstanceState == null){
            getPhotoStreamClient().searchPhotos("Audi");
        }
    }

    @Override
    protected void onPhotoStreamServiceDisconnected(IPhotoStreamClient photoStreamClient) {
        photoStreamClient.removeOnSearchPhotosResultListener(this);
    }

    @Override
    public void onSearchedPhotosReceived(PhotoQueryResult result) {
        List<Photo> photos = result.getPhotos();
        loadMoreButton.setVisibility(result.hasNextPage() ? Button.VISIBLE : Button.GONE);
        if (result.isFirstPage())
            photoAdapter.set(photos);
        else
            photoAdapter.addAll(photos);
    }

    @Override
    public void onReceiveSearchedPhotosFailed(String query, HttpResult httpResult) {
        String title = "Could not load photos";
        Utils.showErrorInAlertDialog(this, title, httpResult);
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
