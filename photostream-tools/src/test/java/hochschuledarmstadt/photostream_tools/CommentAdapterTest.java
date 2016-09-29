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
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;

import hochschuledarmstadt.photostream_tools.adapter.BaseCommentAdapter;
import hochschuledarmstadt.photostream_tools.model.Comment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class CommentAdapterTest {

    private static class TestViewHolder extends RecyclerView.ViewHolder{
        public TestViewHolder(View itemView) {
            super(itemView);
        }
    }

    private TestCommentAdapter simpleCommentAdapter;

    private static class TestCommentAdapter extends BaseCommentAdapter<TestViewHolder> {

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
        Comment[] fakeComments = new Comment[]{mock(Comment.class), mock(Comment.class), mock(Comment.class), mock(Comment.class)};
        simpleCommentAdapter.add(fakeComments[0]);
        simpleCommentAdapter.add(fakeComments[1]);
        simpleCommentAdapter.addAtFront(fakeComments[2]);
        assertEquals(fakeComments[2], simpleCommentAdapter.getItemAtPosition(0));
    }

    @Test
    public void testAddMultiple() {
        Comment[] fakeComments = new Comment[]{mock(Comment.class), mock(Comment.class), mock(Comment.class)};
        simpleCommentAdapter.addAll(Arrays.asList(fakeComments));
        int lastItemPosition = fakeComments.length - 1;
        assertEquals(fakeComments[lastItemPosition], simpleCommentAdapter.getItemAtPosition(lastItemPosition));
    }

    @Test
    public void testRemove() {
        Comment fakeComment = mock(Comment.class);
        when(fakeComment.getId()).thenReturn(1);
        simpleCommentAdapter.add(fakeComment);
        simpleCommentAdapter.remove(1);
        assertEquals(0, simpleCommentAdapter.getItemCount());
    }

    @Test
    public void testSaveInstanceState() {
        Bundle bundle = saveInstanceState();
        assertFalse(bundle.isEmpty());
    }

    private Bundle saveInstanceState() {
        Comment fakeComment = mock(Comment.class);
        simpleCommentAdapter.add(fakeComment);
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
        Comment[] fakeComments = new Comment[]{mock(Comment.class), mock(Comment.class), mock(Comment.class), mock(Comment.class)};
        simpleCommentAdapter.set(Arrays.asList(fakeComments));
        assertEquals(fakeComments.length, simpleCommentAdapter.getItemCount());
    }

}
