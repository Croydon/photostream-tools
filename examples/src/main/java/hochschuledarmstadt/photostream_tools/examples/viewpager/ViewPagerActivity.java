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

package hochschuledarmstadt.photostream_tools.examples.viewpager;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.widget.ProgressBar;

import hochschuledarmstadt.photostream_tools.IPhotoStreamClient;
import hochschuledarmstadt.photostream_tools.PhotoStreamFragmentActivity;
import hochschuledarmstadt.photostream_tools.RequestType;
import hochschuledarmstadt.photostream_tools.callback.OnCommentCountChangedListener;
import hochschuledarmstadt.photostream_tools.callback.OnNewPhotoReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotoDeletedListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotosReceivedListener;
import hochschuledarmstadt.photostream_tools.examples.R;
import hochschuledarmstadt.photostream_tools.examples.Utils;
import hochschuledarmstadt.photostream_tools.model.HttpError;
import hochschuledarmstadt.photostream_tools.model.Photo;
import hochschuledarmstadt.photostream_tools.model.PhotoQueryResult;

public class ViewPagerActivity extends PhotoStreamFragmentActivity implements OnPhotosReceivedListener, OnNewPhotoReceivedListener, OnPhotoDeletedListener, OnCommentCountChangedListener {

    private static final String KEY_ADAPTER = "KEY_ADAPTER";

    /**
     * Beschreibt wie früh weitere Photos nachgeladen werden sollen.
     * Wenn beispielsweise 5 Photos angezeigt werden und auf das dritte Photo gewechselt wird
     * mit {@code PAGE_POSITION_OFFSET} = 2, dann werden weitere Photos geladen.
     */
    private static final int PAGE_POSITION_OFFSET = 2;
    private ViewPager viewPager;
    private PhotoFragmentPagerAdapter adapter;
    private ViewPager.OnPageChangeListener pageChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewPager = (ViewPager) findViewById(R.id.pager);

        pageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (shouldLoadMorePhotos(position)){
                    IPhotoStreamClient photoStreamClient = getPhotoStreamClient();
                    if(photoStreamClient != null)
                        photoStreamClient.loadMorePhotos();
                }
                updateActionBarSubtitle();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };

        viewPager.addOnPageChangeListener(pageChangeListener);

        adapter = new PhotoFragmentPagerAdapter(getSupportFragmentManager());

        if (savedInstanceState != null) {
            // Photos aus vorheriger Activity wiederherstellen
            adapter.restoreInstanceState(savedInstanceState.getParcelable(KEY_ADAPTER));
            updateActionBarSubtitle();
        }else{
            setDefaultActionBarSubtitle();
        }

        viewPager.setAdapter(adapter);

    }

    private boolean shouldLoadMorePhotos(int position) {
        int n = (position + PAGE_POSITION_OFFSET);
        boolean couldFetchMorePhotos = isConnectedToService() &&  n >= adapter.getCount();
        return couldFetchMorePhotos && isNotAlreadyFetchingMorePhotos();
    }

    private boolean isNotAlreadyFetchingMorePhotos() {
        return !getPhotoStreamClient().hasOpenRequestsOfType(RequestType.LOAD_PHOTOS);
    }

    private void setDefaultActionBarSubtitle() {
        getSupportActionBar().setSubtitle(String.format("%s/%s", 0, 0));
    }

    private void updateActionBarSubtitle() {
        getSupportActionBar().setSubtitle(String.format("%s/%s", viewPager.getCurrentItem() + 1, adapter.getCount()));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_ADAPTER, adapter.saveInstanceState());
    }

    @Override
    protected void onPhotoStreamServiceConnected(IPhotoStreamClient photoStreamClient, Bundle savedInstanceState) {
        super.onPhotoStreamServiceConnected(photoStreamClient, savedInstanceState);
        photoStreamClient.addOnPhotosReceivedListener(this);
        photoStreamClient.addOnPhotoDeletedListener(this);
        photoStreamClient.addOnNewPhotoReceivedListener(this);
        photoStreamClient.addOnCommentCountChangedListener(this);
        if (savedInstanceState == null || shouldLoadPhotos(photoStreamClient)){
            photoStreamClient.loadPhotos();
        }
    }

    private boolean shouldLoadPhotos(IPhotoStreamClient photoStreamClient) {
        return adapter.getCount() == 0 && !photoStreamClient.hasOpenRequestsOfType(RequestType.LOAD_PHOTOS);
    }

    @Override
    protected void onPhotoStreamServiceDisconnected(IPhotoStreamClient photoStreamClient) {
        super.onPhotoStreamServiceDisconnected(photoStreamClient);
        photoStreamClient.removeOnPhotosReceivedListener(this);
        photoStreamClient.removeOnPhotoDeletedListener(this);
        photoStreamClient.removeOnNewPhotoReceivedListener(this);
        photoStreamClient.removeOnCommentCountChangedListener(this);
    }

    @Override
    public void onPhotosReceived(PhotoQueryResult result) {
        if (result.isFirstPage())
            adapter.set(result.getPhotos());
        else
            adapter.addAll(result.getPhotos());
        updateActionBarSubtitle();
    }

    @Override
    public void onReceivePhotosFailed(HttpError httpError) {
        Utils.showErrorInAlertDialog(this, "Could not receive Photos", httpError);
    }

    @Override
    public void onNoNewPhotosAvailable() {

    }

    @Override
    public void onRequestStarted() {
        findViewById(R.id.progressCircle).setVisibility(ProgressBar.VISIBLE);
    }

    @Override
    public void onRequestFinished() {
        findViewById(R.id.progressCircle).setVisibility(ProgressBar.GONE);
    }

    @Override
    protected void onDestroy() {
        viewPager.removeOnPageChangeListener(pageChangeListener);
        super.onDestroy();
    }

    @Override
    public void onNewPhotoReceived(Photo photo) {
        adapter.addAtFront(photo);
        updateActionBarSubtitle();
    }

    public Photo getPhoto(int position){
        return adapter.getPhotoAtPosition(position);
    }

    @Override
    public void onPhotoDeleted(int photoId) {
        adapter.remove(photoId);
        updateActionBarSubtitle();
        if(shouldLoadMorePhotos(viewPager.getCurrentItem()))
            getPhotoStreamClient().loadMorePhotos();
    }

    @Override
    public void onPhotoDeleteFailed(int photoId, HttpError httpError) {
        Utils.showErrorInAlertDialog(this, String.format("Couldn't delete photo with id: %s", photoId), httpError);
    }

    @Override
    public void onCommentCountChanged(int photoId, int commentCount) {
        adapter.updateCommentCount(photoId, commentCount);
    }
}
