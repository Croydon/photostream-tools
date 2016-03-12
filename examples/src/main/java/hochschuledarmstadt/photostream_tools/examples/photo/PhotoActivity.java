package hochschuledarmstadt.photostream_tools.examples.photo;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import hochschuledarmstadt.photostream_tools.BaseActivity;
import hochschuledarmstadt.photostream_tools.IPhotoStreamClient;
import hochschuledarmstadt.photostream_tools.adapter.DividerItemDecoration;
import hochschuledarmstadt.photostream_tools.callback.OnPhotosResultListener;
import hochschuledarmstadt.photostream_tools.examples.R;
import hochschuledarmstadt.photostream_tools.examples.photo.PhotoStreamAdapter;
import hochschuledarmstadt.photostream_tools.model.Photo;
import hochschuledarmstadt.photostream_tools.model.PhotoQueryResult;

public class PhotoActivity extends BaseActivity implements OnPhotosResultListener {

    private static final int COLUMNS_PER_ROW = 2;
    private RecyclerView recyclerView;
    private PhotoStreamAdapter adapter;

    @Override
    protected void onPhotoStreamServiceConnected(IPhotoStreamClient photoStreamClient, Bundle savedInstanceState) {
        photoStreamClient.addOnPhotosResultListener(this);
        if (savedInstanceState == null)
            photoStreamClient.getPhotos(1);
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
        recyclerView.addItemDecoration(new DividerItemDecoration(this, null));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new PhotoStreamAdapter(getApplicationContext());
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onPhotosReceived(PhotoQueryResult result) {
        adapter.append(result.getPhotos());
    }

    @Override
    public void onReceivePhotosFailed() {
        Toast.makeText(this, "Could not get photos", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNewPhoto(Photo photo) {

    }

    @Override
    public void onPhotoDeleted(int photoId) {

    }

    @Override
    public void onPhotoDeleteFailed(int photoId) {

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
