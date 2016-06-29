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

import android.os.Bundle;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Arrays;

import hochschuledarmstadt.photostream_tools.adapter.SimplePhotoAdapter;
import hochschuledarmstadt.photostream_tools.model.Photo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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

    private SimplePhotoAdapter<TestViewHolder> simplePhotoAdapter;

    private static class PhotoAdapter extends SimplePhotoAdapter<TestViewHolder> {

        PhotoAdapter() {
            super();
        }

        @Override
        public TestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(TestViewHolder holder, int position) {

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

}
