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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import hochschuledarmstadt.photostream_tools.BaseActivity;
import hochschuledarmstadt.photostream_tools.IPhotoStreamClient;
import hochschuledarmstadt.photostream_tools.RequestType;
import hochschuledarmstadt.photostream_tools.adapter.DividerItemDecoration;
import hochschuledarmstadt.photostream_tools.callback.OnPhotosResultListener;
import hochschuledarmstadt.photostream_tools.examples.R;
import hochschuledarmstadt.photostream_tools.examples.Utils;
import hochschuledarmstadt.photostream_tools.model.HttpResult;
import hochschuledarmstadt.photostream_tools.model.Photo;
import hochschuledarmstadt.photostream_tools.model.PhotoQueryResult;

public class PhotoActivity extends BaseActivity implements OnPhotosResultListener {

    private static final int COLUMNS_PER_ROW = 2;
    private static final String KEY_CURRENT_PAGE = "KEY_CURRENT_PAGE";
    private static final String KEY_ADAPTER = "KEY_ADAPTER";

    private RecyclerView recyclerView;
    private SimplePhotoAdapter adapter;
    private Button button;
    private int currentPage = 1;

    @Override
    protected void onPhotoStreamServiceConnected(IPhotoStreamClient photoStreamClient, Bundle savedInstanceState) {
        photoStreamClient.addOnPhotosResultListener(this);
        if (savedInstanceState == null)
            photoStreamClient.getPhotos(currentPage);
    }

    @Override
    protected void onPhotoStreamServiceDisconnected(IPhotoStreamClient photoStreamClient) {
        photoStreamClient.removeOnPhotosResultListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, COLUMNS_PER_ROW));
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getPhotoStreamClient().hasOpenRequestsOfType(RequestType.PHOTOS)){
                    getPhotoStreamClient().getPhotos(currentPage);
                }
            }
        });

        adapter = new SimplePhotoAdapter(getApplicationContext());
        if (savedInstanceState != null) {
            currentPage = savedInstanceState.getInt(KEY_CURRENT_PAGE);
            adapter.restoreInstanceState(savedInstanceState.getBundle(KEY_ADAPTER));
        }
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_PAGE, currentPage);
        outState.putBundle(KEY_ADAPTER, adapter.saveInstanceState());
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
            if (!client.hasOpenRequestsOfType(RequestType.PHOTOS)){
                getPhotoStreamClient().getPhotos(1);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPhotosReceived(PhotoQueryResult result) {
        final int photosCount = result.getPhotos().size();
        if (photosCount > 0) {
            button.setVisibility(View.VISIBLE);
            currentPage = result.getPage() + 1;
            if (result.getPage() == 1){
                adapter.set(result.getPhotos());
            }else{
                adapter.append(result.getPhotos());
            }
        }else{
            button.setVisibility(View.GONE);
        }
    }

    @Override
    public void onReceivePhotosFailed(HttpResult httpResult) {
        int responseCode = httpResult.getResponseCode();
        String title = "Could not load photos";
        String message = String.format("Response Code: %s\nMessage:%s", responseCode, httpResult.getMessage());
        Utils.showSimpleAlertDialog(this, title, message);
    }


    @Override
    public void onNewPhoto(Photo photo) {

    }

    @Override
    public void onPhotoDeleted(int photoId) {

    }

    @Override
    public void onPhotoDeleteFailed(int photoId, HttpResult httpResult) {

    }

    @Override
    public void onShowProgressDialog() {
        findViewById(R.id.progressCircle).setVisibility(View.VISIBLE);
    }

    @Override
    public void onDismissProgressDialog() {
        findViewById(R.id.progressCircle).setVisibility(View.GONE);
    }
}
