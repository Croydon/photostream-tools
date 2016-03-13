package hochschuledarmstadt.photostream_tools.examples.comment;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;

import java.util.List;

import hochschuledarmstadt.photostream_tools.BaseActivity;
import hochschuledarmstadt.photostream_tools.IPhotoStreamClient;
import hochschuledarmstadt.photostream_tools.adapter.DividerItemDecoration;
import hochschuledarmstadt.photostream_tools.callback.OnCommentsResultListener;
import hochschuledarmstadt.photostream_tools.examples.R;
import hochschuledarmstadt.photostream_tools.model.Comment;
import hochschuledarmstadt.photostream_tools.model.HttpResult;

public class CommentActivity extends BaseActivity implements OnCommentsResultListener {

    private RecyclerView recyclerView;
    private CommentAdapter adapter;

    private final int photoId = 3;

    @Override
    protected void onPhotoStreamServiceConnected(IPhotoStreamClient photoStreamClient, Bundle savedInstanceState) {
        photoStreamClient.addOnGetCommentsResultListener(this);
        if (savedInstanceState == null)
            photoStreamClient.getComments(photoId);
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
        recyclerView.addItemDecoration(new DividerItemDecoration(this, null));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new CommentAdapter(getApplicationContext());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onGetComments(int photoId, List<Comment> comments) {
        if (this.photoId == photoId)
            adapter.set(comments);
    }

    @Override
    public void onGetCommentsFailed(int photoId, HttpResult httpResult) {

    }

    @Override
    public void onCommentDeleted(int commentId) {

    }

    @Override
    public void onDeleteCommentFailed(int commentId, HttpResult httpResult) {

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
