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

package hochschuledarmstadt.photostream_tools.examples.advanced_examples.viewpager;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import hochschuledarmstadt.photostream_tools.IPhotoStreamClient;
import hochschuledarmstadt.photostream_tools.PhotoStreamFragmentActivity;
import hochschuledarmstadt.photostream_tools.RequestType;
import hochschuledarmstadt.photostream_tools.adapter.BaseFragmentPagerAdapter;
import hochschuledarmstadt.photostream_tools.callback.OnCommentCountChangedListener;
import hochschuledarmstadt.photostream_tools.callback.OnNewPhotoReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotoDeletedListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotosReceivedListener;
import hochschuledarmstadt.photostream_tools.examples.R;
import hochschuledarmstadt.photostream_tools.examples.Utils;
import hochschuledarmstadt.photostream_tools.model.HttpError;
import hochschuledarmstadt.photostream_tools.model.Photo;
import hochschuledarmstadt.photostream_tools.model.PhotoQueryResult;

public class ViewPagerFragmentActivity extends PhotoStreamFragmentActivity implements OnPhotosReceivedListener, OnNewPhotoReceivedListener, OnPhotoDeletedListener, OnCommentCountChangedListener {

    private static final String KEY_ADAPTER = "KEY_ADAPTER";

    /**
     * Beschreibt wie früh weitere Photos nachgeladen werden sollen.
     * Wenn beispielsweise 5 Photos angezeigt werden und auf das dritte Photo gewechselt wird
     * mit {@code PAGE_POSITION_OFFSET} = 2, dann werden weitere Photos geladen.
     */
    private static final int PAGE_POSITION_OFFSET = 2;
    private static final String KEY_MORE_PHOTOS_AVAILABLE = "KEY_MORE_PHOTOS_AVAILABLE";

    private ViewPager viewPager;
    private PhotoFragmentPagerAdapter adapter;
    private ViewPager.OnPageChangeListener pageChangeListener;
    private Toolbar toolbar;
    private TextView textViewSwipeHint;
    private boolean morePhotosAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_view_pager);

        if (savedInstanceState != null)
            morePhotosAvailable = savedInstanceState.getBoolean(KEY_MORE_PHOTOS_AVAILABLE);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setPageTransformer(true, new FancyPageTransformer());
        textViewSwipeHint = (TextView) findViewById(R.id.textViewHintSwipe);
        pageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                textViewSwipeHint.setVisibility(position == 0 ? TextView.VISIBLE : TextView.GONE);
                if (shouldLoadMorePhotos(position)) {
                    IPhotoStreamClient photoStreamClient = getPhotoStreamClient();
                    if (photoStreamClient != null)
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
        }

        viewPager.setAdapter(adapter);

    }

    private boolean shouldLoadMorePhotos(int currentPosition) {
        if (!morePhotosAvailable)
            return false;
        int n = (currentPosition + PAGE_POSITION_OFFSET);
        boolean shouldFetchMorePhotos = isConnectedToService() && n >= adapter.getCount();
        return shouldFetchMorePhotos && isNotAlreadyFetchingMorePhotos();
    }

    private boolean isNotAlreadyFetchingMorePhotos() {
        return !getPhotoStreamClient().hasOpenRequestOfType(RequestType.LOAD_PHOTOS);
    }

    private void updateActionBarSubtitle() {
        int position = viewPager.getCurrentItem();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setSubtitle(adapter.getPageTitle(position));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_ADAPTER, adapter.saveInstanceState());
        outState.putBoolean(KEY_MORE_PHOTOS_AVAILABLE, morePhotosAvailable);
    }

    @Override
    protected void onPhotoStreamServiceConnected(IPhotoStreamClient photoStreamClient, Bundle savedInstanceState) {
        super.onPhotoStreamServiceConnected(photoStreamClient, savedInstanceState);
        photoStreamClient.addOnPhotosReceivedListener(this);
        photoStreamClient.addOnPhotoDeletedListener(this);
        photoStreamClient.addOnNewPhotoReceivedListener(this);
        photoStreamClient.addOnCommentCountChangedListener(this);
        if (isFirstStartOfActivity(savedInstanceState) || shouldLoadPhotos(photoStreamClient)) {
            photoStreamClient.loadPhotos();
        }
    }

    private boolean isFirstStartOfActivity(Bundle savedInstanceState) {
        return savedInstanceState == null;
    }

    private boolean shouldLoadPhotos(IPhotoStreamClient photoStreamClient) {
        return adapter.getCount() == 0 && !photoStreamClient.hasOpenRequestOfType(RequestType.LOAD_PHOTOS);
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
        if (result.isFirstPage()) {
            adapter.set(result.getPhotos());
        } else
            adapter.addAll(result.getPhotos());
        updateActionBarSubtitle();
        morePhotosAvailable = result.hasNextPage();
    }

    @Override
    public void onReceivePhotosFailed(HttpError httpError) {
        Utils.showErrorInAlertDialog(this, "Could not receive Photos", httpError);
    }

    @Override
    public void onNoNewPhotosAvailable() {

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

    @Override
    public void onPhotoDeleted(int photoId) {
        adapter.remove(photoId);
        updateActionBarSubtitle();
        if (shouldLoadMorePhotos(viewPager.getCurrentItem()))
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

    public static class PhotoFragmentPagerAdapter extends BaseFragmentPagerAdapter<PageFragment> {

        public PhotoFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        protected PageFragment createNewFragment() {
            return new PageFragment();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return String.format("Photo %d von %d", position + 1, getCount());
        }
    }

}
