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

package hochschuledarmstadt.photostream_tools.examples.examples.plugin;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import hochschuledarmstadt.photostream_tools.IPhotoStreamClient;
import hochschuledarmstadt.photostream_tools.PhotoStreamActivity;
import hochschuledarmstadt.photostream_tools.adapter.PluginAlertDialog;
import hochschuledarmstadt.photostream_tools.callback.OnPhotosReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnRequestListener;
import hochschuledarmstadt.photostream_tools.examples.R;
import hochschuledarmstadt.photostream_tools.examples.examples.photo.PhotoAdapter;
import hochschuledarmstadt.photostream_tools.model.HttpError;
import hochschuledarmstadt.photostream_tools.model.Photo;
import hochschuledarmstadt.photostream_tools.model.PhotoQueryResult;

public class AlertDialogPluginActivity extends PhotoStreamActivity implements OnRequestListener, OnPhotosReceivedListener{

    private static final String KEY_ADAPTER = "KEY_ADAPTER";
    private static final String KEY_BUTTON_VISIBILITY = "KEY_BUTTON_VISIBILITY";

    private PhotoAdapter adapter;
    private RecyclerView recyclerView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plugin_example);

        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setEnabled(false);
                getPhotoStreamClient().loadMorePhotos();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PhotoAdapter();

        adapter.addOnLongClickPlugin(R.id.itemRoot, new PluginAlertDialog<Photo, PhotoAdapter.PhotoViewHolder>(this, R.style.AppCompatDialogStyle) {
            @Override
            protected void onCreateAlertDialog(AlertDialog.Builder builder, Photo item) {
                // Die Klasse ist am Ende dieser Datei innere statische Klasse definiert
                final DialogOnItemClickListener listener = new DialogOnItemClickListener();

                builder.setTitle(item.getDescription())
                        .setSingleChoiceItems(R.array.dialog_items, -1, listener)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (listener.userHasMadeASelection()) {
                                    String[] items = getResources().getStringArray(R.array.dialog_items);
                                    String item = items[listener.getSelectedIndex()];
                                    Toast.makeText(AlertDialogPluginActivity.this, item, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                builder.show();
            }

            @Override
            protected boolean shouldExecute(PhotoAdapter.PhotoViewHolder viewHolder, View v, Photo item) {
                return true;
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
        button.setEnabled(true);
        if (result.isFirstPage())
            adapter.set(result.getPhotos());
        else
            adapter.addAll(result.getPhotos());

        button.setVisibility(result.hasNextPage() ? Button.VISIBLE : Button.GONE);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(KEY_ADAPTER, adapter.saveInstanceState());
        outState.putBoolean(KEY_BUTTON_VISIBILITY, button.getVisibility() == Button.VISIBLE);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        adapter.restoreInstanceState(savedInstanceState.getBundle(KEY_ADAPTER));
        button.setVisibility(savedInstanceState.getBoolean(KEY_BUTTON_VISIBILITY) ? Button.VISIBLE : Button.GONE);
    }

    @Override
    public void onReceivePhotosFailed(HttpError httpError) {
        button.setEnabled(true);
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

    private static class DialogOnItemClickListener implements AlertDialog.OnClickListener {

        private static final int INVALID_SELECTION = -1;
        private int selectedIndex = INVALID_SELECTION;

        @Override
        public void onClick(DialogInterface dialogInterface, int index) {
            selectedIndex = index;
        }

        public int getSelectedIndex() {
            return selectedIndex;
        }

        public boolean userHasMadeASelection() {
            return selectedIndex != INVALID_SELECTION;
        }
    }

}
