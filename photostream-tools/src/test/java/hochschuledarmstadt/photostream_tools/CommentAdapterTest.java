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
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;

import hochschuledarmstadt.photostream_tools.adapter.SimpleCommentAdapter;
import hochschuledarmstadt.photostream_tools.model.Comment;
import hochschuledarmstadt.photostream_tools.model.Photo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class CommentAdapterTest {

    private static class TestViewHolder extends RecyclerView.ViewHolder{
        public TestViewHolder(View itemView) {
            super(itemView);
        }
    }

    private TestCommentAdapter simpleCommentAdapter;

    private static class TestCommentAdapter extends SimpleCommentAdapter<TestViewHolder>{

        TestCommentAdapter(){
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
        simpleCommentAdapter = new TestCommentAdapter();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testAddAtFront(){
        Comment fakeComment = mock(Comment.class);
        simpleCommentAdapter.addAtFront(fakeComment);
        assertTrue(simpleCommentAdapter.getItemCount() == 1);
    }

    @Test
    public void testAdd(){
        Comment fakeComment = mock(Comment.class);
        simpleCommentAdapter.add(fakeComment);
        assertTrue(simpleCommentAdapter.getItemCount() == 1);
    }

    @Test
    public void testAddMultipleAtEndAndFront() {
        Comment fakeComment1 = mock(Comment.class);
        Comment fakeComment2 = mock(Comment.class);
        Comment fakeComment3 = mock(Comment.class);
        simpleCommentAdapter.add(fakeComment1);
        simpleCommentAdapter.add(fakeComment2);
        simpleCommentAdapter.addAtFront(fakeComment3);
        assertEquals(fakeComment3, simpleCommentAdapter.getItemAtPosition(0));
    }

    @Test
    public void testAddMultiple() {
        Comment fakeComment1 = mock(Comment.class);
        Comment fakeComment2 = mock(Comment.class);
        Comment fakeComment3 = mock(Comment.class);
        simpleCommentAdapter.addAll(Arrays.asList(fakeComment1, fakeComment2, fakeComment3));
        assertEquals(fakeComment3, simpleCommentAdapter.getItemAtPosition(2));
    }

    @Test
    public void testRemove() {
        Comment fakeComment1 = mock(Comment.class);
        when(fakeComment1.getId()).thenReturn(1);
        simpleCommentAdapter.add(fakeComment1);
        simpleCommentAdapter.remove(1);
        assertEquals(0, simpleCommentAdapter.getItemCount());
    }

    @Test
    public void testSaveInstanceState() {
        Bundle bundle = saveInstanceState();
        assertFalse(bundle.isEmpty());
    }

    private Bundle saveInstanceState() {
        Comment fakeComment1 = mock(Comment.class);
        simpleCommentAdapter.add(fakeComment1);
        return simpleCommentAdapter.saveInstanceState();
    }

    @Test
    public void testRestoreInstanceState() {
        Bundle bundle = saveInstanceState();
        simpleCommentAdapter.restoreInstanceState(bundle);
        assertEquals(1, simpleCommentAdapter.getItemCount());
    }

    @Test
    public void testSetNewItems() {
        simpleCommentAdapter.add(mock(Comment.class));
        simpleCommentAdapter.set(Arrays.asList(mock(Comment.class), mock(Comment.class), mock(Comment.class), mock(Comment.class)));
        assertEquals(4, simpleCommentAdapter.getItemCount());
    }

}
