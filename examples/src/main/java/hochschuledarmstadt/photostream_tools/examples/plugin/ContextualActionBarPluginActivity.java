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

package hochschuledarmstadt.photostream_tools.examples.plugin;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import hochschuledarmstadt.photostream_tools.IPhotoStreamClient;
import hochschuledarmstadt.photostream_tools.PhotoStreamActivity;
import hochschuledarmstadt.photostream_tools.adapter.PluginContextualActionBar;
import hochschuledarmstadt.photostream_tools.callback.OnPhotosReceivedListener;
import hochschuledarmstadt.photostream_tools.examples.R;
import hochschuledarmstadt.photostream_tools.examples.photo.PhotoAdapter;
import hochschuledarmstadt.photostream_tools.model.HttpError;
import hochschuledarmstadt.photostream_tools.model.Photo;
import hochschuledarmstadt.photostream_tools.model.PhotoQueryResult;

public class ContextualActionBarPluginActivity extends PhotoStreamActivity implements OnPhotosReceivedListener {

    private static final String KEY_ADAPTER = "KEY_ADAPTER";

    private PhotoAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plugin_example);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PhotoAdapter();
        adapter.addOnLongClickPlugin(R.id.itemRoot, new PluginContextualActionBar<Photo, PhotoAdapter.PhotoViewHolder>(this, R.menu.menu_cab_example) {
            @Override
            protected boolean onActionItemClicked(ActionMode mode, MenuItem menuItem, List<Integer> selectedItemIds) {
                if (menuItem.getItemId() == R.id.action_delete){
                    Toast.makeText(ContextualActionBarPluginActivity.this, getString(R.string.delete), Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (menuItem.getItemId() == R.id.action_share){
                    Toast.makeText(ContextualActionBarPluginActivity.this, getString(R.string.share), Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }

            @Override
            protected void onUpdateContextualActionBar(ActionMode actionMode, Context context, int selectedItemsCount) {
                // Titel soll anzeigen, wieviele Photos aktuell selektiert sind
                actionMode.setTitle(String.format(getString(R.string.cab_title_format), selectedItemsCount));
            }

            @Override
            protected boolean shouldExecute(PhotoAdapter.PhotoViewHolder viewHolder, View v, Photo item) {
                return true; // <-- alle Photos können markiert werden
                // return item.isDeleteable() <-- nur Photos, die löschbar sind, können markiert werden
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onPhotoStreamServiceConnected(IPhotoStreamClient photoStreamClient, Bundle savedInstanceState) {
        photoStreamClient.addOnPhotosReceivedListener(this);
        if (savedInstanceState == null)
            photoStreamClient.loadPhotos();
    }

    @Override
    protected void onPhotoStreamServiceDisconnected(IPhotoStreamClient photoStreamClient) {
        photoStreamClient.removeOnPhotosReceivedListener(this);
    }

    @Override
    public void onPhotosReceived(PhotoQueryResult result) {
        if (result.isFirstPage())
            adapter.set(result.getPhotos());
        else
            adapter.addAll(result.getPhotos());

        if (result.hasNextPage())
            getPhotoStreamClient().loadMorePhotos();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(KEY_ADAPTER, adapter.saveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        adapter.restoreInstanceState(savedInstanceState.getBundle(KEY_ADAPTER));
    }

    @Override
    public void onReceivePhotosFailed(HttpError httpError) {

    }

    @Override
    public void onNoNewPhotosAvailable() {

    }

    @Override
    public void onRequestStarted() {

    }

    @Override
    public void onRequestFinished() {

    }

}
