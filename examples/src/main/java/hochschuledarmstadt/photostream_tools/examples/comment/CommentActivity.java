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

package hochschuledarmstadt.photostream_tools.examples.comment;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;

import java.util.List;

import hochschuledarmstadt.photostream_tools.IPhotoStreamClient;
import hochschuledarmstadt.photostream_tools.PhotoStreamActivity;
import hochschuledarmstadt.photostream_tools.adapter.DividerItemDecoration;
import hochschuledarmstadt.photostream_tools.callback.OnCommentsReceivedListener;
import hochschuledarmstadt.photostream_tools.examples.R;
import hochschuledarmstadt.photostream_tools.examples.Utils;
import hochschuledarmstadt.photostream_tools.model.Comment;
import hochschuledarmstadt.photostream_tools.model.HttpResult;

public class CommentActivity extends PhotoStreamActivity implements OnCommentsReceivedListener {

    /**
     * Key für das Speichern der Kommentare in der Methode "onSaveInstanceState(...)"
     */
    private static final String KEY_ADAPTER = "KEY_ADAPTER";

    /**
     * Die Photo Id, für die Kommentare abgerufen werden
     */
    private static final int PHOTO_ID = 1;

    private RecyclerView recyclerView;
    private CommentAdapter adapter;

    // Methode wird von der Bibliothek "photostream-tools" automatisch aufgerufen,
    // wenn die Activity an den Service gebunden wurde.
    @Override
    protected void onPhotoStreamServiceConnected(IPhotoStreamClient photoStreamClient, Bundle savedInstanceState) {
        photoStreamClient.addOnCommentsReceivedListener(this);
        //Kommentare laden, wenn die Activity zum ersten Mal gestartet wird
        if (savedInstanceState == null)
            photoStreamClient.loadComments(PHOTO_ID);
    }

    /*
        Methode wird von der Bibliothek "photostream-tools" automatisch aufgerufen,
        wenn die Activity vom Service entbunden wurde.
     */
    @Override
    protected void onPhotoStreamServiceDisconnected(IPhotoStreamClient photoStreamClient) {
        // Listener wieder entfernen um Memory Leak zu vermeiden
        photoStreamClient.removeOnCommentsReceivedListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        // RecyclerView referenzieren
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // LinearLayoutManager setzen (Elemente in der angezeigten Liste werden sequentiell dargestellt)
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        // Visuellen Begrenzer setzen für Kommentare in der Liste
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        // Standard Animationen aktivieren
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new CommentAdapter();
        // Wenn die Activity neu aufgebaut wird
        if (savedInstanceState != null) {
            // dann die Kommentare aus der vorherigen Activity Instanz wiederherstellen
            Bundle bundle = savedInstanceState.getBundle(KEY_ADAPTER);
            adapter.restoreInstanceState(bundle);
        }
        // Der RecyclerView den Adapter zuweisen
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Kommentare aus dem Adapter im Bundle speichern
        outState.putBundle(KEY_ADAPTER, adapter.saveInstanceState());
    }

    @Override
    public void onCommentsReceived(int photoId, List<Comment> comments) {
        // Wenn die Kommentare zur aktuellen PHOTO_ID gehören
        if (PHOTO_ID == photoId)
            // dann die Kommentare setzen
            adapter.set(comments);
    }

    @Override
    public void onReceiveCommentsFailed(int photoId, HttpResult httpResult) {
        String title = "Could not load comments";
        Utils.showErrorInAlertDialog(this, title, httpResult);
    }

    @Override
    public void onShowProgressDialog() {
        // ProgressBar anzeigen
        findViewById(R.id.progressCircle).setVisibility(ProgressBar.VISIBLE);
    }

    @Override
    public void onDismissProgressDialog() {
        // ProgressBar verstecken
        findViewById(R.id.progressCircle).setVisibility(ProgressBar.GONE);
    }
}
