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

package hochschuledarmstadt.photostream_tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import hochschuledarmstadt.photostream_tools.adapter.BasePhotoAdapter;
import hochschuledarmstadt.photostream_tools.model.BaseItem;
import hochschuledarmstadt.photostream_tools.model.Photo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class PhotoAdapterTest {

    private static class TestViewHolder extends RecyclerView.ViewHolder{
        public TestViewHolder(View itemView) {
            super(itemView);
        }
    }

    private PhotoAdapter simplePhotoAdapter;

    private static class PhotoAdapter extends BasePhotoAdapter<TestViewHolder> {

        private Context context = RuntimeEnvironment.application.getApplicationContext();

        PhotoAdapter() {
            super();
        }

        @Override
        public TestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LinearLayout itemView = new LinearLayout(context);
            itemView.setId(R.id.adapter_test_view_id);
            return new TestViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(TestViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
        }

        @Override
        protected void onBitmapLoadedIntoImageView(ImageView imageView) {

        }
    }

    @Before
    public void setUp() {
        simplePhotoAdapter = new PhotoAdapter();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testAddAtFront(){
        Photo fakePhoto = mock(Photo.class);
        simplePhotoAdapter.addAtFront(fakePhoto);
        assertTrue(simplePhotoAdapter.getItemCount() == 1);
    }

    @Test
    public void testAdd(){
        Photo fakePhoto = mock(Photo.class);
        simplePhotoAdapter.add(fakePhoto);
        assertTrue(simplePhotoAdapter.getItemCount() == 1);
    }

    @Test
    public void testAddMultipleAtEndAndFront() {
        Photo[] fakePhotos = new Photo[]{mock(Photo.class), mock(Photo.class), mock(Photo.class)};
        simplePhotoAdapter.add(fakePhotos[0]);
        simplePhotoAdapter.add(fakePhotos[1]);
        simplePhotoAdapter.addAtFront(fakePhotos[2]);
        assertEquals(fakePhotos[2], simplePhotoAdapter.getItemAtPosition(0));
    }

    @Test
    public void testAddMultiple() {
        Photo[] fakePhotos = new Photo[]{mock(Photo.class), mock(Photo.class), mock(Photo.class)};
        int lastItemPosition = fakePhotos.length - 1;
        simplePhotoAdapter.addAll(Arrays.asList(fakePhotos));
        assertEquals(fakePhotos[lastItemPosition], simplePhotoAdapter.getItemAtPosition(lastItemPosition));
    }

    @Test
    public void testRemove() {
        Photo fakePhoto = mock(Photo.class);
        when(fakePhoto.getId()).thenReturn(1);
        simplePhotoAdapter.add(fakePhoto);
        simplePhotoAdapter.remove(1);
        assertEquals(0, simplePhotoAdapter.getItemCount());
    }

    @Test
    public void testSaveInstanceState() {
        Bundle bundle = saveInstanceState();
        assertFalse(bundle.isEmpty());
    }

    private Bundle saveInstanceState() {
        Photo fakePhoto = mock(Photo.class);
        simplePhotoAdapter.add(fakePhoto);
        return simplePhotoAdapter.saveInstanceState();
    }

    @Test
    public void testRestoreInstanceState() {
        Bundle bundle = saveInstanceState();
        simplePhotoAdapter.restoreInstanceState(bundle);
        assertEquals(1, simplePhotoAdapter.getItemCount());
    }

    @Test
    public void testReplaceWithNewPhotos() {
        simplePhotoAdapter.add(mock(Photo.class));
        simplePhotoAdapter.set(Arrays.asList(mock(Photo.class), mock(Photo.class), mock(Photo.class), mock(Photo.class)));
        assertEquals(4, simplePhotoAdapter.getItemCount());
    }

    @Test
    public void testUpdateCommentCount() {
        final int NEW_COMMENT_COUNT = 2;
        final int photoId = 1;
        final boolean isPhotoLiked = true;
        final boolean deleteable = true;
        final int commentCount = 1;
        Photo photo = Fakes.buildFakePhoto(photoId, null, null, isPhotoLiked, deleteable, commentCount);
        simplePhotoAdapter.add(photo);
        simplePhotoAdapter.updateCommentCount(1, NEW_COMMENT_COUNT);
        assertEquals(NEW_COMMENT_COUNT, photo.getCommentCount());
    }

    @Test
    public void testSetLikeForPhoto() {
        final int photoId = 1;
        final boolean isPhotoLiked = false;
        final boolean deleteable = true;
        final int commentCount = 1;
        Photo photo = Fakes.buildFakePhoto(photoId, null, null, isPhotoLiked, deleteable, commentCount);
        assertFalse(photo.isLiked());
        simplePhotoAdapter.add(photo);
        if (simplePhotoAdapter.setLikeForPhoto(photoId)){
            assertTrue(photo.isLiked());
        }else{
            fail("photo should be liked after method call");
        }
    }

    @Test
    public void testResetLikeForPhoto() {
        final int photoId = 1;
        final boolean isPhotoLiked = true;
        final boolean deleteable = true;
        final int commentCount = 1;
        Photo photo = Fakes.buildFakePhoto(photoId, null, null, isPhotoLiked, deleteable, commentCount);
        assertTrue(photo.isLiked());
        simplePhotoAdapter.add(photo);
        if (simplePhotoAdapter.resetLikeForPhoto(photoId)){
            assertFalse(photo.isLiked());
        }else{
            fail("photo should be unliked after method call");
        }
    }

    @Test
    public void testOnClickIsWorking() {
        final CountDownLatch latch = new CountDownLatch(1);
        simplePhotoAdapter.setOnItemClickListener(R.id.adapter_test_view_id, new BasePhotoAdapter.OnItemClickListener<TestViewHolder>() {
            @Override
            public void onItemClicked(TestViewHolder viewHolder, View v, Photo photo) {
                latch.countDown();
            }

        });
        TestViewHolder viewHolder = simplePhotoAdapter.onCreateViewHolder(null, 0);
        simplePhotoAdapter.onBindViewHolder(viewHolder, 0);
        simplePhotoAdapter.add(mock(Photo.class));
        viewHolder.itemView.performClick();
        assertTrue(latch.getCount() == 0);
    }

    @Test
    public void testOnClickIsWorkingAfterReplacingListener() {
        final CountDownLatch latch = new CountDownLatch(1);
        simplePhotoAdapter.setOnItemClickListener(R.id.adapter_test_view_id, new BasePhotoAdapter.OnItemClickListener<TestViewHolder>() {
            @Override
            public void onItemClicked(TestViewHolder viewHolder, View v, Photo photo) {

            }
        });
        simplePhotoAdapter.setOnItemClickListener(R.id.adapter_test_view_id, new BasePhotoAdapter.OnItemClickListener<TestViewHolder>() {
            @Override
            public void onItemClicked(TestViewHolder viewHolder, View v, Photo photo) {
                latch.countDown();
            }
        });
        final TestViewHolder viewHolder = simplePhotoAdapter.onCreateViewHolder(null, 0);
        simplePhotoAdapter.add(mock(Photo.class));
        simplePhotoAdapter.onBindViewHolder(viewHolder, 0);
        viewHolder.itemView.performClick();
        assertTrue(latch.getCount() == 0);
    }

    @Test
    public void testOnLongClickIsWorking() {
        final CountDownLatch latch = new CountDownLatch(1);
        simplePhotoAdapter.setOnItemLongClickListener(R.id.adapter_test_view_id, new BasePhotoAdapter.OnItemLongClickListener<TestViewHolder>() {
            @Override
            public boolean onItemLongClicked(TestViewHolder viewHolder, View v, Photo photo) {
                latch.countDown();
                return true;
            }
        });
        TestViewHolder viewHolder = simplePhotoAdapter.onCreateViewHolder(null, 0);
        simplePhotoAdapter.onBindViewHolder(viewHolder, 0);
        simplePhotoAdapter.add(mock(Photo.class));
        viewHolder.itemView.performLongClick();
        assertTrue(latch.getCount() == 0);
    }

    @Test
    public void testOnLongClickIsWorkingAfterReplacingListener() {
        final CountDownLatch latch = new CountDownLatch(1);
        simplePhotoAdapter.setOnItemLongClickListener(R.id.adapter_test_view_id, new BasePhotoAdapter.OnItemLongClickListener<TestViewHolder>() {
            @Override
            public boolean onItemLongClicked(TestViewHolder viewHolder, View v, Photo photo) {
                return true;
            }
        });
        simplePhotoAdapter.setOnItemLongClickListener(R.id.adapter_test_view_id, new BasePhotoAdapter.OnItemLongClickListener<TestViewHolder>() {

            @Override
            public boolean onItemLongClicked(TestViewHolder viewHolder, View v, Photo photo) {
                latch.countDown();
                return true;
            }

        });
        TestViewHolder viewHolder = simplePhotoAdapter.onCreateViewHolder(null, 0);
        simplePhotoAdapter.onBindViewHolder(viewHolder, 0);
        simplePhotoAdapter.add(mock(Photo.class));
        viewHolder.itemView.performLongClick();
        assertTrue(latch.getCount() == 0);
    }

    @Test
    public void testOnTouchIsWorking() {
        final CountDownLatch latch = new CountDownLatch(2);
        simplePhotoAdapter.setOnItemTouchListener(R.id.adapter_test_view_id, new BasePhotoAdapter.OnItemTouchListener<TestViewHolder>() {

            @Override
            public boolean onItemTouched(TestViewHolder viewHolder, View v, MotionEvent motionEvent, Photo photo) {
                if (MotionEvent.ACTION_DOWN == motionEvent.getAction()){
                    latch.countDown();
                    return true;
                }else if(MotionEvent.ACTION_UP == motionEvent.getAction()){
                    latch.countDown();
                    return true;
                }
                return false;
            }
        });
        TestViewHolder viewHolder = simplePhotoAdapter.onCreateViewHolder(null, 0);
        simplePhotoAdapter.onBindViewHolder(viewHolder, 0);
        simplePhotoAdapter.add(mock(Photo.class));
        boolean result = viewHolder.itemView.dispatchTouchEvent(MotionEvent.obtain(2,2,MotionEvent.ACTION_DOWN,0,null,null,0,0,0,0,0,0,0,0));
        if (result)
            viewHolder.itemView.dispatchTouchEvent(MotionEvent.obtain(2,2,MotionEvent.ACTION_UP,0,null,null,0,0,0,0,0,0,0,0));
        assertTrue(latch.getCount() == 0);
    }

    @Test
    public void testOnTouchIsWorkingAfterReplacingListener() {
        final CountDownLatch latch = new CountDownLatch(2);
        simplePhotoAdapter.setOnItemTouchListener(R.id.adapter_test_view_id, new BasePhotoAdapter.OnItemTouchListener<TestViewHolder>() {
            @Override
            public boolean onItemTouched(TestViewHolder viewHolder, View v, MotionEvent motionEvent, Photo photo) {
                return false;
            }
        });
        simplePhotoAdapter.setOnItemTouchListener(R.id.adapter_test_view_id, new BasePhotoAdapter.OnItemTouchListener<TestViewHolder>() {
            @Override
            public boolean onItemTouched(TestViewHolder viewHolder, View v, MotionEvent motionEvent, Photo photo) {
                if (MotionEvent.ACTION_DOWN == motionEvent.getAction()){
                    latch.countDown();
                    return true;
                }else if(MotionEvent.ACTION_UP == motionEvent.getAction()){
                    latch.countDown();
                    return true;
                }
                return false;
            }
        });
        TestViewHolder viewHolder = simplePhotoAdapter.onCreateViewHolder(null, 0);
        simplePhotoAdapter.onBindViewHolder(viewHolder, 0);
        simplePhotoAdapter.add(mock(Photo.class));
        boolean result = viewHolder.itemView.dispatchTouchEvent(MotionEvent.obtain(2,2,MotionEvent.ACTION_DOWN,0,null,null,0,0,0,0,0,0,0,0));
        if (result)
            viewHolder.itemView.dispatchTouchEvent(MotionEvent.obtain(2,2,MotionEvent.ACTION_UP,0,null,null,0,0,0,0,0,0,0,0));
        assertTrue(latch.getCount() == 0);
    }

}
