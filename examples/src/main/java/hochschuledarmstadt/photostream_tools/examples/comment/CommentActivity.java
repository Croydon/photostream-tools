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

import hochschuledarmstadt.photostream_tools.PhotoStreamActivity;
import hochschuledarmstadt.photostream_tools.IPhotoStreamClient;
import hochschuledarmstadt.photostream_tools.adapter.DividerItemDecoration;
import hochschuledarmstadt.photostream_tools.callback.OnCommentsResultListener;
import hochschuledarmstadt.photostream_tools.examples.R;
import hochschuledarmstadt.photostream_tools.examples.Utils;
import hochschuledarmstadt.photostream_tools.model.Comment;
import hochschuledarmstadt.photostream_tools.model.HttpResult;

public class CommentActivity extends PhotoStreamActivity implements OnCommentsResultListener {

    private static final String KEY_ADAPTER = "KEY_ADAPTER";
    private static final int PHOTO_ID = 3;

    private RecyclerView recyclerView;
    private CommentAdapter adapter;

    @Override
    protected void onPhotoStreamServiceConnected(IPhotoStreamClient photoStreamClient, Bundle savedInstanceState) {
        photoStreamClient.addOnGetCommentsResultListener(this);
        if (savedInstanceState == null)
            photoStreamClient.getComments(PHOTO_ID);
    }

    @Override
    protected void onPhotoStreamServiceDisconnected(IPhotoStreamClient photoStreamClient) {
        photoStreamClient.removeOnGetCommentsResultListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new CommentAdapter();
        if (savedInstanceState != null) {
            Bundle bundle = savedInstanceState.getBundle(KEY_ADAPTER);
            adapter.restoreInstanceState(bundle);
        }
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(KEY_ADAPTER, adapter.saveInstanceState());
    }

    @Override
    public void onGetComments(int photoId, List<Comment> comments) {
        if (PHOTO_ID == photoId)
            adapter.set(comments);
    }

    @Override
    public void onGetCommentsFailed(int photoId, HttpResult httpResult) {
        String title = "Could not load comments";
        String message = String.format("Response Code: %s\nMessage:%s", httpResult.getResponseCode(), httpResult.getMessage());
        Utils.showSimpleAlertDialog(this, title, message);
    }

    @Override
    public void onCommentDeleted(int commentId) {

    }

    @Override
    public void onCommentDeleteFailed(int commentId, HttpResult httpResult) {

    }

    @Override
    public void onNewComment(Comment comment) {

    }

    @Override
    public void onSendCommentFailed(HttpResult httpResult) {

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
